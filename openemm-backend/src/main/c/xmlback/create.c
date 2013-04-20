/*	-*- mode: c; mode: fold -*-	*/
/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/
# include	<ctype.h>
# include	<string.h>
# include	"xmlback.h"

static bool_t
use_block (block_t *block, links_t *links) /*{{{*/
{
	bool_t	rc;

	rc = false;
	if (block -> attachment) {
			rc = true;
	} else {
		if (links && block -> cid) {
			int	n;
		
			for (n = 0; n < links -> lcnt; ++n)
				if (((! links -> seen) || (! links -> seen[n])) &&
				    (! strcmp (block -> cid, links -> l[n]))) {
					if (links -> seen)
						links -> seen[n] = true;
					rc = true;
					break;
				}
		}
	}
	return rc;
}/*}}}*/

static int
is_end_of_line (blockmail_t *blockmail, int pos) /*{{{*/
{
	if ((pos + 2 < blockmail -> head -> length) &&
	    (blockmail -> head -> buffer[pos] == '\r') &&
	    (blockmail -> head -> buffer[pos + 1] == '\n'))
		return 2;
	if ((pos + 1 < blockmail -> head -> length) &&
	    ((blockmail -> head -> buffer[pos] == '\n') || (blockmail -> head -> buffer[pos] == '\r')))
		return 1;
	return 0;
}/*}}}*/
static void
cleanup_header (blockmail_t *blockmail) /*{{{*/
{
	int	olen, nlen;
	int	offset, save, n;
		
	for (olen = 0, nlen = 0; olen < blockmail -> head -> length; ) {
		if ((offset = is_end_of_line (blockmail, olen)) > 0) {
			save = olen;
			for (n = olen + offset; n < blockmail -> head -> length; ++n) {
				byte_t	ch = blockmail -> head -> buffer[n];
				
				if ((ch != ' ') && (ch != '\t')) {
					if (is_end_of_line (blockmail, n))
						olen = n;
					break;
				}
			}
			if (save < olen)
				continue;
		}
		if (olen != nlen)
			blockmail -> head -> buffer[nlen++] = blockmail -> head -> buffer[olen++];
		else
			++olen, ++nlen;
	}
	blockmail -> head -> length = nlen;
}/*}}}*/
static const xmlChar *
replace_head (const xmlChar *ch, int clen, int *rlen) /*{{{*/
{
	if ((clen == 1) && iscntrl (*ch))
		return NULL;
	*rlen = clen;
	return ch;
}/*}}}*/
static bool_t
create_mail (blockmail_t *blockmail, receiver_t *rec) /*{{{*/
{
	int		n, m;
	bool_t		st;
	int		attcount;
	links_t		*links;
	postfix_t	*postfixes;
	buffer_t	*dest;
	mailtype_t	*mtyp;
	blockspec_t	*bspec;
	block_t		*block;
	rblock_t	*rbprev, *rbhead;
	bool_t		changed;
	
	mtyp = NULL;
	for (n = 0; (! mtyp) && (n < blockmail -> mailtype_count); ++n) {
		int	match = 0;
		
		switch (n) {
		case 0:
			match = (rec -> mailtype == 0);
			break;
		case 1:
			match = (rec -> mailtype & MAILTYPE_HTML) && (! (rec -> mailtype & MAILTYPE_HTML_OFFLINE));
			break;
		case 2:
			match = ((rec -> mailtype & MAILTYPE_HTML) && (rec -> mailtype & MAILTYPE_HTML_OFFLINE)) ||
				(rec -> mailtype == MAILTYPE_HTML_OFFLINE);
			break;
		}
		if (match)
			mtyp = blockmail -> mailtype[n];
	}
	if (! mtyp) {
		log_out (blockmail -> lg, LV_WARNING, "No mailtype definition (%d) for receiver %d found, fallback to default", rec -> mailtype, rec -> customer_id);
		if (blockmail -> mailtype_count > 1)
			mtyp = blockmail -> mailtype[1];
		else {
			log_out (blockmail -> lg, LV_ERROR, "No default mailtype definition found, aborting");
			return false;
		}
	}
	st = true;
	attcount = 0;

	/*
	 * 1. Stage: check for usful blocks, count attachments and
	 *           create the content part */
	links = mtyp -> offline ? links_alloc () : NULL;
	for (n = 0; st && (n < mtyp -> blockspec_count); ++n) {
		bspec = mtyp -> blockspec[n];
		block = bspec -> block;
		changed = false;
		if (blockmail -> eval && (blockmail -> mailtype_index != -1)) {
			int	idx = -1;
			
			if (block -> tid == TID_EMail_Text) {
				idx = 0;
				changed = true;
			} else if (block -> tid == TID_EMail_HTML) {
				idx = 1;
				changed = true;
			}
			if (changed)
				eval_change_data (blockmail -> eval, blockmail -> mtbuf[idx], false, blockmail -> mailtype_index);
		}
			block -> inuse = block_match (block, blockmail -> eval);
		if (block -> inuse) {
			if ((block -> tid != TID_EMail_Head) &&
			    (block -> tid != TID_EMail_Text) &&
			    (block -> tid != TID_EMail_HTML)) {
				block -> inuse = use_block (block, links);
			} else {
				if (block -> tid == TID_EMail_HTML) {
					block -> inuse = rec -> mailtype & (MAILTYPE_HTML | MAILTYPE_HTML_OFFLINE) ? true : false;
				}
			}
		}
		if (block -> inuse) {
			if (block -> attachment)
				attcount++;
			if (! block -> binary) {
				if (st) {
					log_idpush (blockmail -> lg, "replace_tags", "->");
					st = replace_tags (blockmail, rec, block, (block -> tid != TID_EMail_Head ? NULL : replace_head), (block -> tid != TID_EMail_Text ? true : false));
					log_idpop (blockmail -> lg);
					if (! st)
						log_out (blockmail -> lg, LV_ERROR, "Unable to replace tags in block %d for %d", block -> nr, rec -> customer_id);
				}
				if (st) {
					log_idpush (blockmail -> lg, "modify_output", "->");
					st = modify_output (blockmail, rec, block, bspec, links);
					log_idpop (blockmail -> lg);
					if (! st)
						log_out (blockmail -> lg, LV_ERROR, "Unable to modify output in block %d for %d", block -> nr, rec -> customer_id);
				}
				if (st) {
					if (! blockmail -> raw) {
						log_idpush (blockmail -> lg, "convert_charset", "->");
						st = convert_charset (blockmail, block);
						log_idpop (blockmail -> lg);
						if (! st)
							log_out (blockmail -> lg, LV_ERROR, "Unable to convert chararcter set in block %d for %d", block -> nr, rec -> customer_id);
					} else {
						xmlBufferPtr	temp;
						
						temp = block -> out;
						block -> out = block -> in;
						block -> in = temp;
					}
				}
			}
		}
		if (changed)
			eval_change_data (blockmail -> eval, rec -> data[blockmail -> mailtype_index], rec -> dnull[blockmail -> mailtype_index], blockmail -> mailtype_index);
	}
	if (links)
		links_free (links);

	/*
	 * 2. Stage: determinate the required postfixes */
	postfixes = NULL;
	for (n = 0; st && (n < mtyp -> blockspec_count); ++n) {
		bspec = mtyp -> blockspec[n];
		block = bspec -> block;
		if (block -> inuse) {
			postfix_t	*cur, *tmp, *prv;
				
			for (m = bspec -> postfix_count - 1; m >= 0; --m) {
				cur = bspec -> postfix[m];
				if (cur -> pid) {
					for (tmp = postfixes, prv = NULL; tmp; tmp = tmp -> stack)
						if (tmp -> pid && (! strcmp (tmp -> pid, cur -> pid)))
							break;
						else
							prv = tmp;
					if (tmp) {
						cur -> stack = tmp -> stack;
						if (prv)
							prv -> stack = cur;
						else
							postfixes = cur;
						cur = NULL;
					}
				}
				if (cur) {
					cur -> stack = postfixes;
					postfixes = cur;
				}
			}
		}
	}

	/*
	 * 3. Stage: create the output */
	rbprev = NULL;
	rbhead = NULL;
	for (n = 0; st && (n <= mtyp -> blockspec_count); ++n) {
		if (n < mtyp -> blockspec_count) {
			bspec = mtyp -> blockspec[n];
			block = bspec -> block;
		} else {
			bspec = NULL;
			block = NULL;
		}
		if (blockmail -> raw) {
			
			if (st && block && block -> inuse) {
				const char	*name;
				rblock_t	*rbtmp;
				
				switch (block -> tid) {
				default:		name = block -> cid;	break;
				case TID_EMail_Head:	name = "__head__";	break;
				case TID_EMail_Text:	name = "__text__";	break;
				case TID_EMail_HTML:	name = "__html__";	break;
				}
				if (rbtmp = rblock_alloc (block -> tid, name, block -> out)) {
					if (rbprev)
						rbprev -> next = rbtmp;
					else
						blockmail -> rblocks = rbtmp;
					rbprev = rbtmp;
					if ((! rbhead) && (block -> tid == TID_EMail_Head)) {
						rbhead = rbtmp;
						append_cooked (blockmail -> head, rbhead -> content, block -> charset, EncNone /*block -> method*/);
					}
				}
			}
		} else {
			if (postfixes) {
				postfix_t	*run, *prv;
			
				for (run = postfixes, prv = NULL; st && run; run = run -> stack)
					if ((! block) || (run -> after < block -> nr)) {
						dest = (run -> ref -> block -> tid == TID_EMail_Head ? blockmail -> head : blockmail -> body);
						if (! append_cooked (dest, (attcount ? run -> c -> acont : run -> c -> cont), run -> ref -> block -> charset, Enc8bit)) {
							log_out (blockmail -> lg, LV_ERROR, "Unable to append postfix for block %d for %d", run -> ref -> block -> nr, rec -> customer_id);
							st = false;
						}
						if (prv)
							prv -> stack = run -> stack;
						else
							postfixes = run -> stack;
					} else
						prv = run;
			}
			if (st && block && block -> inuse) {
				dest = (block -> tid == TID_EMail_Head ? blockmail -> head : blockmail -> body);
				if (! append_cooked (dest, (attcount ? bspec -> prefix -> acont : bspec -> prefix -> cont), block -> charset, Enc8bit)) {
					log_out (blockmail -> lg, LV_ERROR, "Unable to append prefix for block %d for %d", block -> nr, rec -> customer_id);
					st = false;
				}
				if (st) {
					if (blockmail -> raw
					    ) {
						if (! append_cooked (dest, block -> out, block -> charset, EncNone))
							st = false;
					} else if (! block -> binary) {
						if (! append_cooked (dest, block -> out, block -> charset, block -> method))
							st = false;
					} else {
						if (! append_raw (dest, block -> bout))
							st = false;
					}
					if (! st)
						log_out (blockmail -> lg, LV_ERROR, "Unable to append content of block %d for %d", block -> nr, rec -> customer_id);
				}
			}
		}
	}
	/*
	 * 4. clear up empty lines in header */
	cleanup_header (blockmail);
	if (rbhead)
		rblock_retreive_content (rbhead, blockmail -> head);

	return st;
}/*}}}*/
bool_t
create_output (blockmail_t *blockmail, receiver_t *rec) /*{{{*/
{
	bool_t	st;
	bool_t	(*docreate) (blockmail_t *, receiver_t *);
	media_t	*m;
	
	st = true;
	m = NULL;
	blockmail -> active = true;
	blockmail -> head -> length = 0;
	blockmail -> body -> length = 0;
	if (blockmail -> raw && blockmail -> rblocks)
		blockmail -> rblocks = rblock_free_all (blockmail -> rblocks);
	if (rec -> mediatypes) {
		char	*copy, *cur, *ptr;
		mtype_t	type;
		
		docreate = NULL;
		if (copy = strdup (rec -> mediatypes)) {
			for (cur = copy; st && cur && (! m); ) {
				if (ptr = strchr (cur, ','))
					*ptr++ = '\0';
				if (media_parse_type (cur, & type)) {
					int	n;

					for (n = 0; n < blockmail -> media_count; ++n)
						if (blockmail -> media[n] -> type == type) {
							m = blockmail -> media[n];
							if (m -> stat == MS_Active) {
								switch (type) {
								case MT_EMail:
									docreate = create_mail;
									break;
								}
							} else
								blockmail -> active = false;
							break;
						}
				} else
					st = false;
				cur = ptr;
			}
			free (copy);
		} else
			st = false;
	} else
		docreate = create_mail;
	if (st) {
		rec -> media = m;
		strcpy (rec -> mid, media_typeid (m ? m -> type : MT_EMail));
		if (blockmail -> active && docreate)
			st = (*docreate) (blockmail, rec);
	}
	return st;
}/*}}}*/
