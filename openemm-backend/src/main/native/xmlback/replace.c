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
# include	"xmlback.h"

static bool_t
expand_tags (tag_t *base) /*{{{*/
{
	bool_t		st;
	tag_t		*cur, *tmp;
	const xmlChar	*ptr;
	int		len;
	int		n, pos;
	int		bstart;
	xmlBufferPtr	out;
	
	st = true;
	for (cur = base; cur; cur = cur -> next) {
		for (tmp = base; tmp; tmp = tmp -> next)
			tmp -> used = (tmp == cur ? true : false);
		ptr = xmlBufferContent (cur -> value);
		len = xmlBufferLength (cur -> value);
		pos = 0;
		bstart = 0;
		out = NULL;
		while (pos < len) {
			n = xmlCharLength (ptr[pos]);
			if ((n == 1) && (ptr[pos] == '[')) {
				int	start, end;
				start = pos++;
				end = -1;
				while (pos < len) {
					n = xmlCharLength (ptr[pos]);
					if ((n == 1) && (ptr[pos] == ']')) {
						++pos;
						end = pos;
						break;
					}
					pos += n;
				}
				if (end != -1) {
					for (tmp = base; tmp; tmp = tmp -> next)
						if ((! tmp -> used) && tag_match (tmp, ptr + start, end - start))
							break;
					if (tmp) {
						if (! out)
							if (! (out = xmlBufferCreate ())) {
								st = false;
								break;
							}
						if (bstart < start)
							xmlBufferAdd (out, ptr + bstart, start - bstart);
						xmlBufferAdd (out, xmlBufferContent (tmp -> value), xmlBufferLength (tmp -> value));
						tmp -> used = true;
						bstart = pos;
					}
				}
			} else
				pos += n;
		}
		if (out) {
			if (bstart < len)
				xmlBufferAdd (out, ptr + bstart, len - bstart);
			xmlBufferFree (cur -> value);
			cur -> value = out;
		}
	}
	return st;
}/*}}}*/
static void
individual_replace (const xmlChar *(*replace) (const xmlChar *, int, int *),
		    xmlBufferPtr dest, const xmlChar *tval, int tlen) /*{{{*/
{
	int		clen;
	const xmlChar	*rplc;
	int		rlen;
	
	while (tlen > 0) {
		clen = xmlCharLength (*tval);
		if ((rplc = (*replace) (tval, clen, & rlen)) && (rlen > 0))
			xmlBufferAdd (dest, rplc, rlen);
		tval += clen;
		tlen -= clen;
	}
}/*}}}*/
static const dyn_t *
find_dynamic (blockmail_t *blockmail, receiver_t *rec, const char *name) /*{{{*/
{
	dcache_t	*dc;
	const dyn_t	*dyn;
					
	for (dc = rec -> cache; dc; dc = dc -> next)
		if (! strcmp (dc -> name, name))
			break;
	if (! dc)
		for (dyn = blockmail -> dyn; dyn; dyn = dyn -> next)
			if (! strcmp (dyn -> name, name)) {
				if (dc = dcache_alloc (name, dyn)) {
					dc -> next = rec -> cache;
					rec -> cache = dc;
				}
				break;
			}
	return dc ? dc -> dyn : NULL;
}/*}}}*/
bool_t
replace_tags (blockmail_t *blockmail, receiver_t *rec, block_t *block,
	      const xmlChar *(*replace) (const xmlChar *, int, int *),
	      bool_t ishtml) /*{{{*/
{
	bool_t		st;
	long		start, cur, next, end, len;
	const xmlChar	*content, *cont;
	int		tidx;
	tagpos_t	*tp;
	int		n;
	tag_t		*tag;
	
	st = expand_tags (rec -> tag);
	start = 0;
	end = xmlBufferLength (block -> content);
	content = xmlBufferContent (block -> content);
	xmlBufferEmpty (block -> in);
	for (cur = start, tidx = 0; cur < end; ) {
		if (tidx < block -> tagpos_count) {
			tp = block -> tagpos[tidx++];
			next = tp -> start;
		} else {
			tp = NULL;
			next = end;
		}
		len = next - cur;
		if (len > 0)
			xmlBufferAdd (block -> in, content + cur, len);
		if (tp) {
			cur = tp -> end;
			tag = NULL;
			if (IS_DYNAMIC (tp -> type) && tp -> tname) {
				const dyn_t	*dyn;

				for (dyn = find_dynamic (blockmail, rec, tp -> tname); dyn; dyn = dyn -> sibling)
					if (dyn_match (dyn, blockmail -> eval))
						break;
				if (dyn) {
					block_t	*use;
						
					use = NULL;
					if (tp -> type & TP_DYNAMICVALUE) {
						for (n = 0; (! use) && (n < dyn -> block_count); ++n)
							switch (dyn -> block[n] -> nr) {
							case 0:
								if (! ishtml)
									use = dyn -> block[n];
								break;
							case 1:
								if (ishtml)
									use = dyn -> block[n];
								break;
							}
					} else if (tp -> type & TP_DYNAMIC)
						use = tp -> content;
					if (use) {
						if (replace_tags (blockmail, rec, use, NULL, ishtml)) {
							if (replace)
								individual_replace (replace, block -> in, xmlBufferContent (use -> in), xmlBufferLength (use -> in));
							else
								xmlBufferAdd (block -> in, xmlBufferContent (use -> in), xmlBufferLength (use -> in));
						} else
							st = false;
					}
				}
			} else {
				for (n = 0; (n < 2) && (! tag); ++n) {
					for (tag = n ? blockmail -> gtag : rec -> tag; tag; tag = tag -> next)
						if (((tag -> hash == 0) || (tp -> hash == 0) || (tag -> hash == tp -> hash)) &&
						    (xmlEqual (tag -> name, tp -> name)))
							break;
				}
			}
			if (tag && (cont = tag_content (tag, blockmail, rec, & n)) && (n > 0))
				if (replace)
					individual_replace (replace, block -> in, cont, n);
				else
					xmlBufferAdd (block -> in, cont, n);
		} else
			cur = next;
	}
	return st;
}/*}}}*/
