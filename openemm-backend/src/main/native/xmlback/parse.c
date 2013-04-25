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
# include	<stdlib.h>
# include	<ctype.h>
# include	<string.h>
# include	"xmlback.h"

static char *
xml2str (blockmail_t *blockmail, const xmlChar *in) /*{{{*/
{
	char	*out;
	int	len;

	if (! blockmail -> translate) {
		len = xmlStrlen (in);
		if (out = malloc (len + 1)) {
			memcpy (out, in, len);
			out[len] = '\0';
		}
	} else {
		xmlBufferEmpty (blockmail -> in);
		xmlBufferCat (blockmail -> in, in);
		xmlBufferEmpty (blockmail -> out);
		switch (convert_block (blockmail -> translate, blockmail -> in, blockmail -> out, true)) {
		default:
		case -1:
			out = NULL;
			break;
		case 0:
			len = xmlBufferLength (blockmail -> in);
			if (out = malloc (len + 1)) {
				int	n, m, clen;
		
				for (n = 0, m = 0; n < len; )
					if (((clen = xmlCharLength (in[n])) == 1) && isascii (in[n]))
						out[m++] = in[n++];
					else
						n += clen;
				out[m] = '\0';
			}
			break;
		case 1:
			len = xmlBufferLength (blockmail -> out);
			if (out = malloc (len + 1)) {
				memcpy (out, xmlBufferContent (blockmail -> out), len);
				out[len] = '\0';
			}
			break;
		}
	}
	return out;
}/*}}}*/
static bool_t
str2long (const char *str, long *value) /*{{{*/
{
	bool_t	st;
	long	val;
	char	*eptr;
	
	st = false;
	val =  strtol (str, & eptr, 0);
	if (! *eptr) {
		*value = val;
		st = true;
	}
	return st;
}/*}}}*/
static bool_t
str2bool (const char *str, bool_t *value) /*{{{*/
{
	bool_t	st;
	
	st = true;
	if (! strcasecmp (str, "true"))
		*value = true;
	else if (! strcasecmp (str, "false"))
		*value = false;
	else
		st = false;
	return st;
}/*}}}*/

static xmlBufferPtr
extract_xml_property (xmlNodePtr node, const char *prop) /*{{{*/
{
	xmlBufferPtr	buf;
	xmlChar		*val;

	buf = NULL;
	if (val = xmlGetProp (node, char2xml (prop))) {
		if (buf = xmlBufferCreate ())
			xmlBufferCat (buf, val);
		xmlFree (val);
	}
	return buf;
}/*}}}*/
static char *
extract_property (blockmail_t *blockmail, xmlNodePtr node, const char *prop) /*{{{*/
{
	char	*value;
	xmlChar	*val;
	
	value = NULL;
	if (val = xmlGetProp (node, char2xml (prop))) {
		value = xml2str (blockmail, val);
		xmlFree (val);
	}
	return value;
}/*}}}*/
static bool_t
extract_numeric_property (blockmail_t *blockmail, long *value, xmlNodePtr node, const char *prop) /*{{{*/
{
	bool_t	st;
	char	*temp;
	
	st = false;
	if (temp = extract_property (blockmail, node, prop)) {
		st = str2long (temp, value);
		free (temp);
	}
	return st;
}/*}}}*/
static bool_t
extract_boolean_property (blockmail_t *blockmail, bool_t *value, xmlNodePtr node, const char *prop) /*{{{*/
{
	bool_t	st;
	char	*temp;
	
	st = false;
	if (temp = extract_property (blockmail, node, prop)) {
		st = str2bool (temp, value);
		free (temp);
	}
	return st;
}/*}}}*/

static bool_t
extract_content (xmlBufferPtr *buf, xmlDocPtr doc, xmlNodePtr base) /*{{{*/
{
	bool_t		st;
	xmlNodePtr	node;
	
	st = true;
	if (*buf)
		xmlBufferEmpty (*buf);
	else {
		*buf = xmlBufferCreate ();
		if (! buf)
			st = false;
	}
	for (node = base -> children; st && node; node = node -> next)
		if (node -> type == XML_TEXT_NODE) {
			xmlChar	*ptr;

			if (ptr = xmlNodeListGetString (doc, node, 1)) {
				xmlBufferAdd (*buf, ptr, xmlStrlen (ptr));
				xmlFree (ptr);
			} else
				st = false;
		}
	return st;
}/*}}}*/
static char *
extract_simple_content (blockmail_t *blockmail, xmlDocPtr doc, xmlNodePtr node) /*{{{*/
{
	char		*value;
	xmlBufferPtr	buf;
	
	value = NULL;
	buf = NULL;
	if (extract_content (& buf, doc, node) && buf) {
		value = xml2str (blockmail, xmlBufferContent (buf));
		xmlBufferFree (buf);
	}
	return value;
}/*}}}*/
static bool_t
extract_numeric_content (blockmail_t *blockmail, long *value, xmlDocPtr doc, xmlNodePtr node) /*{{{*/
{
	bool_t	st;
	char	*temp;
	
	st = false;
	if (temp = extract_simple_content (blockmail, doc, node)) {
		st = str2long (temp, value);
		free (temp);
	}
	return st;
}/*}}}*/
static void
unknown (blockmail_t *blockmail, xmlNodePtr node) /*{{{*/
{
	log_out (blockmail -> lg, LV_VERBOSE, "Ignore unknown element %s in %s", node -> name, blockmail -> fname);
}/*}}}*/
static void
invalid (blockmail_t *blockmail, xmlNodePtr node) /*{{{*/
{
	log_out (blockmail -> lg, LV_ERROR, "Unable to find valid element %s in %s", node -> name, blockmail -> fname);
}/*}}}*/

static bool_t
parse_description (blockmail_t *blockmail, xmlDocPtr doc, xmlNodePtr base) /*{{{*/
{
	bool_t		st;
	xmlNodePtr	node;
	long		val;
	char		*ptr;
	
	st = true;
	log_idpush (blockmail -> lg, "description", "->");
	for (node = base; node && st; node = node -> next)
		if (node -> type == XML_ELEMENT_NODE) {
			if (! xmlstrcmp (node -> name, "company")) {
				if (st = extract_numeric_property (blockmail, & val, node, "id")) {
					blockmail -> company_id = (int) val;
				}
			} else if (! xmlstrcmp (node -> name, "mailinglist")) {
				if (st = extract_numeric_property (blockmail, & val, node, "id"))
					blockmail -> mailinglist_id = (int) val;
			} else if (! xmlstrcmp (node -> name, "mailing")) {
				if (st = extract_numeric_property (blockmail, & val, node, "id"))
					blockmail -> mailing_id = (int) val;
				blockmail -> mailing_name = extract_xml_property (node, "name");
			} else if (! xmlstrcmp (node -> name, "maildrop")) {
				if (st = extract_numeric_property (blockmail, & val, node, "status_id"))
					blockmail -> maildrop_status_id = (int) val;
			} else if (! xmlstrcmp (node -> name, "status")) {
				if (ptr = extract_property (blockmail, node, "field")) {
					blockmail -> status_field = *ptr;
					free (ptr);
				} else
					st = false;
			} else
				unknown (blockmail, node);
			if (! st)
				invalid (blockmail, node);
		}
	log_idpop (blockmail -> lg);
	return st;
}/*}}}*/
static bool_t
parse_general (blockmail_t *blockmail, xmlDocPtr doc, xmlNodePtr base) /*{{{*/
{
	bool_t		st;
	xmlNodePtr	node;
	
	st = true;
	log_idpush (blockmail -> lg, "general", "->");
	for (node = base; node && st; node = node -> next)
		if (node -> type == XML_ELEMENT_NODE) {
			if (! xmlstrcmp (node -> name, "subject"))
				st = extract_content (& blockmail -> email.subject, doc, node);
			else if (! xmlstrcmp (node -> name, "from_email"))
				st = extract_content (& blockmail -> email.from, doc, node);
			else if (! xmlstrcmp (node -> name, "profile_url"))
				st = extract_content (& blockmail -> profile_url, doc, node);
			else if (! xmlstrcmp (node -> name, "unsubscribe_url"))
				st = extract_content (& blockmail -> unsubscribe_url, doc, node);
			else if (! xmlstrcmp (node -> name, "auto_url")) {
				st = extract_content (& blockmail -> auto_url, doc, node);
			} else if (! xmlstrcmp (node -> name, "onepixel_url"))
				st = extract_content (& blockmail -> onepixel_url, doc, node);
			else if (! xmlstrcmp (node -> name, "password"))
				st = extract_content (& blockmail -> password, doc, node);
			else if (! xmlstrcmp (node -> name, "total_subscribers"))
				st = extract_numeric_content (blockmail, & blockmail -> total_subscribers, doc, node);
			else
				unknown (blockmail, node);
			if (! st)
				invalid (blockmail, node);
		}
	log_idpop (blockmail -> lg);
	return st;
}/*}}}*/
static bool_t
parse_mailcreation (blockmail_t *blockmail, xmlDocPtr doc, xmlNodePtr base) /*{{{*/
{
	bool_t		st;
	xmlNodePtr	node;
	long		val;
	
	st = true;
	log_idpush (blockmail -> lg, "mailcreation", "->");
	for (node = base; node && st; node = node -> next)
		if (node -> type == XML_ELEMENT_NODE) {
			if (! xmlstrcmp (node -> name, "blocknr")) {
				if (st = extract_numeric_content (blockmail, & val, doc, node))
					blockmail -> blocknr = (int) val;
			} else if (! xmlstrcmp (node -> name, "innerboundary")) {
				if (! (blockmail -> innerboundary = extract_simple_content (blockmail, doc, node)))
					st = false;
			} else if (! xmlstrcmp (node -> name, "outerboundary")) {
				if (! (blockmail -> outerboundary = extract_simple_content (blockmail, doc, node)))
					st = false;
			} else if (! xmlstrcmp (node -> name, "attachboundary")) {
				if (! (blockmail -> attachboundary = extract_simple_content (blockmail, doc, node)))
					st = false;
			} else
				unknown (blockmail, node);
			if (! st)
				invalid (blockmail, node);
		}
	log_idpop (blockmail -> lg);
	return st;
}/*}}}*/
static bool_t
parse_media_parameter (blockmail_t *blockmail, xmlDocPtr doc, xmlNodePtr base, media_t *media) /*{{{*/
{
	bool_t		st;
	xmlNodePtr	node, child;
	char		*name;
	parm_t		*tmp, *prv;
	pval_t		*vtmp, *vprv;
	xmlBufferPtr	buf;
			
	st = true;
	for (tmp = media -> parm, prv = NULL; tmp; tmp = tmp -> next)
		prv = tmp;
	for (node = base; st && node; node = node -> next)
		if ((node -> type == XML_ELEMENT_NODE) && (! xmlstrcmp (node -> name, "variable")))
			if (name = extract_property (blockmail, node, "name")) {
				if (tmp = parm_alloc ()) {
					tmp -> name = name;
					if (prv)
						prv -> next = tmp;
					else
						media -> parm = tmp;
					prv = tmp;
					vprv = NULL;
					for (child = node -> children; st && child; child = child -> next)
						if ((child -> type == XML_ELEMENT_NODE) && (! xmlstrcmp (child -> name, "value"))) {
							buf = NULL;
							if (extract_content (& buf, doc, child)) {
								if (vtmp = pval_alloc ()) {
									vtmp -> v = buf;
									if (vprv)
										vprv -> next = vtmp;
									else
										tmp -> value = vtmp;
									vprv = vtmp;
								} else
									xmlBufferFree (buf);
							}
						}
				} else
					free (name);
			} else {
				log_out (blockmail -> lg, LV_ERROR, "Missing name in variable in %s", blockmail -> fname);
				st = false;
			}
	return st;
}/*}}}*/
static bool_t
parse_media (blockmail_t *blockmail, xmlDocPtr doc, xmlNodePtr node, media_t *media) /*{{{*/
{
	bool_t	st;
	char	*type, *stat;
	long	prio;
	
	type = extract_property (blockmail, node, "type");
	stat = extract_property (blockmail, node, "status");
	if (type && stat && extract_numeric_property (blockmail, & prio, node, "priority")) {
		st = true;
		
		if (media_set_type (media, type) &&
		    media_set_priority (media, prio) &&
		    media_set_status (media, stat)) {
			st = parse_media_parameter (blockmail, doc, node -> children, media);
			if (st)
				media_postparse (media, blockmail);
		} else {
			log_out (blockmail -> lg, LV_ERROR, "Invalid data for media: %s/%ld/%s in %s", type, prio, stat, blockmail -> fname);
			st = false;
		}
	} else {
		log_out (blockmail -> lg, LV_ERROR, "Missing name in media in %s", blockmail -> fname);
		st = false;
	}
	if (type)
		free (type);
	if (stat)
		free (stat);
	return st;
}/*}}}*/
static bool_t
parse_mediatypes (blockmail_t *blockmail, xmlDocPtr doc, xmlNodePtr base) /*{{{*/
{
	bool_t		st;
	xmlNodePtr	node;
	
	st = true;
	log_idpush (blockmail -> lg, "mediatypes", "->");
	for (node = base; node && st; node = node -> next)
		if (node -> type == XML_ELEMENT_NODE) {
			if (! xmlstrcmp (node -> name, "media")) {
				media_t	*media;

				DO_EXPAND (media, blockmail, media);
				if (media) {
					st = parse_media (blockmail, doc, node, media);
					if (! st)
						DO_SHRINK (blockmail, media);
				} else
					st = false;
			} else
				unknown (blockmail, node);
			if (! st)
				invalid (blockmail, node);
		}
	log_idpop (blockmail -> lg);
	return st;
}/*}}}*/

static bool_t	parse_block (blockmail_t *blockmail, xmlDocPtr doc, xmlNodePtr node, block_t *block);
static bool_t
parse_tagposition (blockmail_t *blockmail, xmlDocPtr doc, xmlNodePtr base, tagpos_t *tpos) /*{{{*/
{
	bool_t		st;
	xmlNodePtr	node;
	
	st = true;
	log_idpush (blockmail -> lg, "tagposition", "->");
	for (node = base; node && st; node = node -> next)
		if (node -> type == XML_ELEMENT_NODE) {
			if (! xmlstrcmp (node -> name, "block")) {
				if (! tpos -> content) {
					if (tpos -> content = block_alloc ())
						st = parse_block (blockmail, doc, node, tpos -> content);
					else
						st = false;
				} else {
					log_out (blockmail -> lg, LV_ERROR, "Duplicate block for tagpos in %s", blockmail -> fname);
					st = false;
				}
			} else
				unknown (blockmail, node);
			if (! st)
				invalid (blockmail, node);
		}
	log_idpop (blockmail -> lg);
	return st;
}/*}}}*/
static bool_t
parse_block (blockmail_t *blockmail, xmlDocPtr doc, xmlNodePtr node, block_t *block) /*{{{*/
{
	bool_t	st;
	long	bid;
	long	val;

	if (extract_numeric_property (blockmail, & bid, node, "id") &&
	    extract_numeric_property (blockmail, & val, node, "nr")) {
		xmlNodePtr	child;
		int		start, end;
		const xmlChar	*content;

		st = true;
		block -> bid = (int) bid;
		block -> nr = (int) val;
		block -> mime = extract_property (blockmail, node, "mimetype");
		block -> charset = extract_property (blockmail, node, "charset");
		block -> encode = extract_property (blockmail, node, "encode");
		block_find_method (block);
		block -> cid = extract_property (blockmail, node, "cid");
		block -> tid = TID_Unspec;
		if (block -> cid) {
			if (! strcmp (block -> cid, "agnHead"))
				block -> tid = TID_EMail_Head;
			else if (! strcmp (block -> cid, "agnText"))
				block -> tid = TID_EMail_Text;
			else if (! strcmp (block -> cid, "agnHtml"))
				block -> tid = TID_EMail_HTML;
		}
		extract_boolean_property (blockmail, & block -> binary, node, "is_binary");
		extract_boolean_property (blockmail, & block -> attachment, node, "is_attachment");
		block -> media = extract_property (blockmail, node, "media");
		if (block -> media)
			media_parse_type (block -> media, & block -> mediatype);
		block -> condition = extract_xml_property (node, "condition");
		start = 0;
		end = 0;
		content = NULL;
		for (child = node -> children; st && child; child = child -> next)
			if (child -> type == XML_ELEMENT_NODE) {
				if (! xmlstrcmp (child -> name, "content")) {
					if (st = extract_content (& block -> content, doc, child)) {
						if (st = block_setup_charset (block)) {
							if (block -> binary && (! (st = block_code_binary (block))))
								log_out (blockmail -> lg, LV_ERROR, "Unable to decode binary part in block %d in %s", block -> nr, blockmail -> fname);
							else {
								start = 0;
								end = xmlBufferLength (block -> content);
								content = xmlBufferContent (block -> content);
							}
						} else
							log_out (blockmail -> lg, LV_ERROR, "Unable to setup charset in block %d in %s", block -> nr, blockmail -> fname);
					} else
						log_out (blockmail -> lg, LV_ERROR, "Unable to extract content of block %d in %s", block -> nr, blockmail -> fname);
				} else if (! xmlstrcmp (child -> name, "tagposition")) {
					if (content) {
						xmlChar		*name;
						tagpos_t	*tpos;
						int		len;
						const xmlChar	*ptr;
						int		n;
				
						name = xmlGetProp (child, char2xml ("name"));
						if (name) {
							DO_EXPAND (tpos, block, tagpos);
							if (tpos) {
								xmlBufferCat (tpos -> name, name);
								if (extract_numeric_property (blockmail, & val, child, "hash"))
									tpos -> hash = val;
								else
									tpos -> hash = 0;
								if (extract_numeric_property (blockmail, & val, child, "type"))
									tpos -> type = val;
								tagpos_find_name (tpos);
								len = xmlBufferLength (tpos -> name);
								ptr = xmlBufferContent (tpos -> name);
								n = 0;
								st = false;
								while (start < end) {
									if (content[start] != ptr[n])
										n = 0;
									if (content[start] == ptr[n]) {
										++n;
										if (n == len) {
											st = true;
											break;
										}
									}
									++start;
								}
								if (st) {
									++start;
									tpos -> start = start - len;
									tpos -> end = start;
									if (child -> children)
										st = parse_tagposition (blockmail, doc, child -> children, tpos);
								} else
									log_out (blockmail -> lg, LV_ERROR, "tagposition %s not found in %s", ptr, blockmail -> fname);
								if (! st)
									DO_SHRINK (block, tagpos);
							} else
								st = false;
						} else {
							log_out (blockmail -> lg, LV_ERROR, "Missing properties in element %s in %s", child -> name, blockmail -> fname);
							st = false;
						}
						if (name)
							xmlFree (name);
					} else {
						log_out (blockmail -> lg, LV_ERROR, "Missing content for tagposition");
						st = false;
					}
				} else
					unknown (blockmail, child);
				if (! st)
					invalid (blockmail, child);
			}
		if (st && block -> condition)
			if (! eval_set_condition (blockmail -> eval, SP_BLOCK, block -> bid, block -> condition)) {
				log_out (blockmail -> lg, LV_ERROR, "Unable to setup block for BlockID %d in %s", block -> bid, blockmail -> fname);
				st = false;
			}
	} else {
		log_out (blockmail -> lg, LV_ERROR, "Missing number in block in %s", blockmail -> fname);
		st = false;
	}
	return st;
}/*}}}*/
static bool_t
parse_blocks (blockmail_t *blockmail, xmlDocPtr doc, xmlNodePtr base) /*{{{*/
{
	bool_t		st;
	xmlNodePtr	node;
	
	st = true;
	log_idpush (blockmail -> lg, "blocks", "->");
	for (node = base; node && st; node = node -> next)
		if (node -> type == XML_ELEMENT_NODE) {
			if (! xmlstrcmp (node -> name, "block")) {
				block_t	*block;

				DO_EXPAND (block, blockmail, block);
				if (block) {
					st = parse_block (blockmail, doc, node, block);
					if (! st) {
						DO_SHRINK (blockmail, block);
					}
				} else
					st = false;
			} else
				unknown (blockmail, node);
			if (! st)
				invalid (blockmail, node);
		}
	log_idpop (blockmail -> lg);
	return st;
}/*}}}*/
static bool_t
parse_fixdata (blockmail_t *blockmail, xmlDocPtr doc, xmlNodePtr base, fix_t *f) /*{{{*/
{
	bool_t		st;
	xmlNodePtr	node;
	
	st = true;
	log_idpush (blockmail -> lg, "fixdata", "->");
	for (node = base; node && st; node = node -> next)
		if (node -> type == XML_ELEMENT_NODE) {
			if (! xmlstrcmp (node -> name, "fixdata")) {
				char	*valid;
				int	mode;
				
				if (valid = extract_property (blockmail, node, "valid")) {
					if (! strcmp (valid, "simple"))
						mode = 1;
					else if (! strcmp (valid, "attach"))
						mode = 2;
					else if (! strcmp (valid, "all"))
						mode = 3;
					else
						mode = 0;
					if ((! (mode & 1 ? extract_content (& f -> cont, doc, node) : true)) ||
					    (! (mode & 2 ? extract_content (& f -> acont, doc, node) : true))) {
						log_out (blockmail -> lg, LV_ERROR, "Unable to get fixdata for %s (%d) in %s", valid, mode, blockmail -> fname);
						st = false;
					}
					free (valid);
				} else {
					log_out (blockmail -> lg, LV_ERROR, "Missing valid in fixdata in %s", blockmail -> fname);
					st = false;
				}
			} else
				unknown (blockmail, node);
			if (! st)
				invalid (blockmail, node);
		}
	log_idpop (blockmail -> lg);
	return st;
}/*}}}*/
static bool_t
parse_blockspec (blockmail_t *blockmail, blockspec_t *bspec, xmlDocPtr doc, xmlNodePtr base) /*{{{*/
{
	bool_t		st;
	xmlNodePtr	node;
	
	st = true;
	log_idpush (blockmail -> lg, "blockspec", "->");
	for (node = base; node && st; node = node -> next)
		if (node -> type == XML_ELEMENT_NODE) {
			if (! xmlstrcmp (node -> name, "prefix")) {
				if (! (st = parse_fixdata (blockmail, doc, node -> children, bspec -> prefix)))
					log_out (blockmail -> lg, LV_ERROR, "Unable to get prefix in blockspec %d in %s", bspec -> nr, blockmail -> fname);
			} else if (! xmlstrcmp (node -> name, "postfix")) {
				long		val;
				postfix_t	*postfix;

				if (extract_numeric_property (blockmail, & val, node, "output")) {
					DO_EXPAND (postfix, bspec, postfix);
					if (postfix) {
						postfix -> pid = extract_property (blockmail, node, "pid");
						postfix -> after = (int) val;
						postfix -> ref = bspec;
						if (! (st = parse_fixdata (blockmail, doc, node -> children, postfix -> c)))
							log_out (blockmail -> lg, LV_ERROR, "Unable to get postfix in blockspec %d in %s", bspec -> nr, blockmail -> fname);
					} else
						st = false;
				} else {
					log_out (blockmail -> lg, LV_ERROR, "Missing output in postfix in blockspec %d in %s", bspec -> nr, blockmail -> fname);
					st = false;
				}
			} else
				unknown (blockmail, node);
			if (! st)
				invalid (blockmail, node);
		}
	log_idpop (blockmail -> lg);
	return st;
}/*}}}*/
static bool_t
parse_type (blockmail_t *blockmail, mailtype_t *mtyp, xmlDocPtr doc, xmlNodePtr base) /*{{{*/
{
	bool_t		st;
	xmlNodePtr	node;
	long		val;
	char		*ptr;
	
	st = true;
	log_idpush (blockmail -> lg, "type", "->");
	for (node = base; node && st; node = node -> next)
		if (node -> type == XML_ELEMENT_NODE) {
			if (! xmlstrcmp (node -> name, "blockspec")) {
				if (st = extract_numeric_property (blockmail, & val, node, "nr")) {
					int		n;
					blockspec_t	*bspec;
					
					for (n = 0; n < blockmail -> block_count; ++n)
						if (blockmail -> block[n] -> nr == (int) val)
							break;
					if (n < blockmail -> block_count) {
						DO_EXPAND (bspec, mtyp, blockspec);
						if (bspec) {
							bspec -> nr = (int) val;
							bspec -> block = blockmail -> block[n];
							if (extract_numeric_property (blockmail, & val, node, "linelength"))
								bspec -> linelength = (int) val;
							if (ptr = extract_property (blockmail, node, "onepixlog")) {
								if (! strcmp (ptr, "top"))
									bspec -> opl = OPL_Top;
								else if (! strcmp (ptr, "bottom"))
									bspec -> opl = OPL_Bottom;
								else
									bspec -> opl = OPL_None;
								free (ptr);
							}
							if (! bspec -> block -> binary)
								blockspec_find_lineseparator (bspec);
							st = parse_blockspec (blockmail, bspec, doc, node -> children);
							if (! st)
								DO_SHRINK (mtyp, blockspec);
						} else
							st = false;
					} else {
						log_out (blockmail -> lg, LV_ERROR, "blockspec %d has no matching block in %s", (int) val, blockmail -> fname);
						st = false;
					}
				} else
					log_out (blockmail -> lg, LV_ERROR, "Missing number in blockspec in %s", blockmail -> fname);
			} else
				unknown (blockmail, node);
			if (! st)
				invalid (blockmail, node);
		}
	log_idpop (blockmail -> lg);
	return st;
}/*}}}*/
static bool_t
parse_types (blockmail_t *blockmail, xmlDocPtr doc, xmlNodePtr base) /*{{{*/
{
	bool_t		st;
	xmlNodePtr	node;
	
	st = true;
	log_idpush (blockmail -> lg, "types", "->");
	for (node = base; node && st; node = node -> next)
		if (node -> type == XML_ELEMENT_NODE) {
			if (! xmlstrcmp (node -> name, "type")) {
				char	*sval;

				if (sval = extract_property (blockmail, node, "id")) {
					mailtype_t	*mtyp;
					
					DO_EXPAND (mtyp, blockmail, mailtype);
					if (mtyp) {
						mtyp -> ident = sval;
						mtyp -> idnr = atoi (sval);
						mtyp -> offline = mtyp -> idnr == 2 ? true : false;
						st = parse_type (blockmail, mtyp, doc, node -> children);
						if (! st)
							DO_SHRINK (blockmail, mailtype);
					} else {
						free (sval);
						st = false;
					}
				} else
					log_out (blockmail -> lg, LV_ERROR, "Missing mailtype in type in %s", blockmail -> fname);
			} else
				unknown (blockmail, node);
			if (! st)
				invalid (blockmail, node);
		}
	log_idpop (blockmail -> lg);
	return st;
}/*}}}*/
static bool_t
parse_layout (blockmail_t *blockmail, xmlDocPtr doc, xmlNodePtr base) /*{{{*/
{
	bool_t		st;
	xmlNodePtr	node;
	char		*name;
	char		*type;
	field_t		*field;
	
	st = true;
	log_idpush (blockmail -> lg, "layout", "->");
	for (node = base; node && st; node = node -> next)
		if (node -> type == XML_ELEMENT_NODE) {
			if (! xmlstrcmp (node -> name, "element")) {
				name = extract_property (blockmail, node, "name");
				type = extract_property (blockmail, node, "type");
				if (name && type) {
					int	pos;
					
					pos = blockmail -> field_count;
					DO_EXPAND (field, blockmail, field);
					if (field) {
						field -> name = name;
						name = NULL;
						field_normalize_name (field);
						field -> type = *type;
						if ((blockmail -> mailtype_index == -1) &&
						    (! strcmp (field -> lname, "mailtype")))
							blockmail -> mailtype_index = pos;
					} else
						st = false;
				}
				if (name)
					free (name);
				if (type)
					free (type);
			} else
				unknown (blockmail, node);
			if (! st)
				invalid (blockmail, node);
		}
	if (st && blockmail -> field_count > 0) {
		if (blockmail -> eval) {
			int	failpos;
		
			if (! eval_set_variables (blockmail -> eval, blockmail -> field, blockmail -> field_count, & failpos)) {
				log_out (blockmail -> lg, LV_ERROR, "Unable to set variables for evaluator [%d: %s]", failpos, ((failpos >= 0) && (failpos < blockmail -> field_count) && blockmail -> field[failpos] ? blockmail -> field[failpos] -> name : "*unknown*"));
				st = false;
			}
		}
	}
	log_idpop (blockmail -> lg);
	return st;
}/*}}}*/
static bool_t
parse_tag (blockmail_t *blockmail, tag_t **tbase, xmlDocPtr doc, xmlNodePtr base) /*{{{*/
{
	bool_t		st;
	xmlNodePtr	node;
	tag_t		*prev, *temp;
	
	st = true;
	if (*tbase)
		for (prev = *tbase; temp = prev -> next; prev = temp)
			;
	else
		prev = NULL;
	log_idpush (blockmail -> lg, "tag", "->");
	for (node = base; node && st; node = node -> next)
		if (node -> type == XML_ELEMENT_NODE) {
			if (! xmlstrcmp (node -> name, "tag")) {
				xmlChar		*name;
				xmlBufferPtr	value;
				long		hash;
				
				name = xmlGetProp (node, char2xml ("name"));
				if (name) {
					if (temp = tag_alloc ()) {
						xmlBufferCat (temp -> name, name);
						if (temp -> cname = xml2string (temp -> name)) {
							temp -> type = extract_property (blockmail, node, "type");
							if (temp -> type && (temp -> topt = strchr (temp -> type, ':')))
								*(temp -> topt)++ = '\0';
							if (extract_numeric_property (blockmail, & hash, node, "hash"))
								temp -> hash = hash;
							else
								temp -> hash = 0;
							value = NULL;
							if (extract_content (& value, doc, node)) {
								xmlBufferAdd (temp -> value, xmlBufferContent (value), xmlBufferLength (value));
								xmlBufferFree (value);
							}
							tag_parse (temp, blockmail);
							if (prev)
								prev -> next = temp;
							else
								*tbase = temp;
							prev = temp;
						} else {
							temp = tag_free (temp);
							st = false;
						}
					} else
						st = false;
				} else {
					log_out (blockmail -> lg, LV_ERROR, "Missing properties in tag in %s", blockmail -> fname);
					st = false;
				}
				if (name)
					xmlFree (name);
			} else
				unknown (blockmail, node);
			if (! st)
				invalid (blockmail, node);
		}
	log_idpop (blockmail -> lg);
	return st;
}/*}}}*/
static bool_t
parse_dyncont (blockmail_t *blockmail, xmlDocPtr doc, xmlNodePtr base, dyn_t *dyn) /*{{{*/
{
	bool_t		st;
	xmlNodePtr	node;
	
	st = true;
	log_idpush (blockmail -> lg, "content", "->");
	for (node = base; node && st; node = node -> next)
		if (node -> type == XML_ELEMENT_NODE) {
			if (! xmlstrcmp (node -> name, "block")) {
				block_t	*block;

				DO_EXPAND (block, dyn, block);
				if (block) {
					st = parse_block (blockmail, doc, node, block);
					if (! st)
						DO_SHRINK (dyn, block);
				} else
					st = false;
			} else
				unknown (blockmail, node);
			if (! st)
				invalid (blockmail, node);
		}
	log_idpop (blockmail -> lg);
	return st;
}/*}}}*/
static bool_t
parse_dynamic (blockmail_t *blockmail, xmlDocPtr doc, xmlNodePtr base,
	       char *name, dyn_t **root) /*{{{*/
{
	bool_t		st;
	xmlNodePtr	node;
	dyn_t		*prv, *run, *tmp;
	
	st = true;
	*root = NULL;
	prv = NULL;
	log_idpush (blockmail -> lg, "dynamic", "->");
	for (node = base; node && st; node = node -> next)
		if (node -> type == XML_ELEMENT_NODE) {
			if (! xmlstrcmp (node -> name, "dyncont")) {
				long	did, order;

				if (extract_numeric_property (blockmail, & did, node, "id") &&
				    extract_numeric_property (blockmail, & order, node, "order")) {
					if (tmp = dyn_alloc (did, order)) {
						tmp -> condition = extract_xml_property (node, "condition");
						if (! *root) {
							if (! (tmp -> name = strdup (name)))
								st = false;
							*root = tmp;
						} else {
							for (run = *root, prv = NULL; run; run = run -> sibling)
								if (run -> order > tmp -> order)
									break;
								else
									prv = run;
							if (! prv) {
								tmp -> sibling = *root;
								tmp -> name = (*root) -> name;
								(*root) -> name = NULL;
								*root = tmp;
							} else {
								tmp -> sibling = prv -> sibling;
								prv -> sibling = tmp;
							}
						}
						st = parse_dyncont (blockmail, doc, node -> children, tmp);
						if (st && tmp -> condition) {
							if (! eval_set_condition (blockmail -> eval, SP_DYNAMIC, tmp -> did, tmp -> condition)) {
								log_out (blockmail -> lg, LV_ERROR, "Unable to setup condition for DynID %d in %s", tmp -> did, blockmail -> fname);
								st = false;
							}
						}
					} else
						st = false;
				} else {
					log_out (blockmail -> lg, LV_ERROR, "Missing properties for dynamic content");
					st = false;
				}
			} else
				unknown (blockmail, node);
			if (! st)
				invalid (blockmail, node);
		}
	log_idpop (blockmail -> lg);
	return st;
}/*}}}*/
static bool_t
parse_dynamics (blockmail_t *blockmail, xmlDocPtr doc, xmlNodePtr base) /*{{{*/
{
	bool_t		st;
	xmlNodePtr	node;
	dyn_t		*prv, *tmp;
	
	st = true;
	prv = NULL;
	log_idpush (blockmail -> lg, "dynamics", "->");
	for (node = base; node && st; node = node -> next)
		if (node -> type == XML_ELEMENT_NODE) {
			if (! xmlstrcmp (node -> name, "dynamic")) {
				long	did;
				char	*name;
				
				if (extract_numeric_property (blockmail, & did, node, "id") &&
				    (name = extract_property (blockmail, node, "name"))) {
					tmp = NULL;
					st = parse_dynamic (blockmail, doc, node -> children, name, & tmp);
					if (tmp)
						if (st) {
							if (prv)
								prv -> next = tmp;
							else
								blockmail -> dyn = tmp;
							prv = tmp;
							blockmail -> dynamic_count++;
						} else
							dyn_free (tmp);
					free (name);
				} else {
					log_out (blockmail -> lg, LV_ERROR, "Missing properties for dynamic part");
					st = false;
				}
			} else
				unknown (blockmail, node);
			if (! st)
				invalid (blockmail, node);
		}
	log_idpop (blockmail -> lg);
	return st;
}/*}}}*/
static bool_t
parse_urls (blockmail_t *blockmail, xmlDocPtr doc, xmlNodePtr base) /*{{{*/
{
	bool_t		st;
	xmlNodePtr	node;
	
	st = true;
	log_idpush (blockmail -> lg, "urls", "->");
	for (node = base; node && st; node = node -> next)
		if (node -> type == XML_ELEMENT_NODE) {
			if (! xmlstrcmp (node -> name, "url")) {
				long		uid, usage;
				xmlBufferPtr	dest;

				if (extract_numeric_property (blockmail, & uid, node, "id") &&
				    extract_numeric_property (blockmail, & usage, node, "usage") &&
				    (dest = extract_xml_property (node, "destination"))) {
					url_t	*url;

					DO_EXPAND (url, blockmail, url);
					if (url) {
						url -> uid = uid;
						url_set_destination (url, dest);
						url -> usage = usage;
						blockmail -> url_usage |= url -> usage;
					} else {
						xmlBufferFree (dest);
						st = false;
					}
				} else {
					log_out (blockmail -> lg, LV_ERROR, "Missing properties for url");
					st = false;
				}
			} else
				unknown (blockmail, node);
			if (! st)
				invalid (blockmail, node);
		}
	log_idpop (blockmail -> lg);
	return st;
}/*}}}*/

static bool_t
parse_details (blockmail_t *blockmail, xmlDocPtr doc, xmlNodePtr base,
	       receiver_t *rec) /*{{{*/
{
	bool_t		st;
	xmlNodePtr	node;
	
	st = true;
	log_idpush (blockmail -> lg, "details", "->");
	for (node = base; node && st; node = node -> next)
		if (node -> type == XML_ELEMENT_NODE) {
			if (! xmlstrcmp (node -> name, "tags")) {
				st = parse_tag (blockmail, & rec -> tag, doc, node -> children);
				if (! st)
					log_out (blockmail -> lg, LV_ERROR, "Unable to parse tag for receiver %d in %s", rec -> customer_id, blockmail -> fname);
			} else if (! xmlstrcmp (node -> name, "data")) {
				if (rec -> data) {
					if (rec -> dpos < rec -> dsize) {
						extract_boolean_property (blockmail, & rec -> dnull[rec -> dpos], node, "null");
						st = extract_content (& rec -> data[rec -> dpos], doc, node);
						if (st) {
							rec -> dpos++;
							
						}
					} else
						log_out (blockmail -> lg, LV_WARNING, "Got more data as expected (%d) for receiver %d in %s", rec -> dsize, rec -> customer_id, blockmail -> fname);
				} else
					log_out (blockmail -> lg, LV_WARNING, "Got data even no data is expected for receiver %d in %s", rec -> customer_id, blockmail -> fname);
			} else if (! xmlstrcmp (node -> name, "codedurl")) {
				long		uid;
				xmlBufferPtr	dest;

				if (extract_numeric_property (blockmail, & uid, node, "id") &&
				    (dest = extract_xml_property (node, "destination"))) {
					url_t	*url;

					DO_EXPAND (url, rec, url);
					if (url) {
						url -> uid = uid;
						url_set_destination (url, dest);
					} else {
						xmlBufferFree (dest);
						st = false;
					}
				} else {
					log_out (blockmail -> lg, LV_ERROR, "Missing properties for url");
					st = false;
				}
			} else
				unknown (blockmail, node);
		}
	log_idpop (blockmail -> lg);
	return st;
}/*}}}*/
static bool_t
parse_receivers (blockmail_t *blockmail, xmlDocPtr doc, xmlNodePtr base) /*{{{*/
{
	bool_t		st;
	receiver_t	*rec;
	xmlNodePtr	node;
	long		val;
	char		*ptr;
	
	st = false;
	log_idpush (blockmail -> lg, "receivers", "->");
	if (rec = receiver_alloc (blockmail -> field_count)) {
		st = true;
		for (node = base; node && st; node = node -> next) {
			if (node -> type == XML_ELEMENT_NODE) {
				if (! xmlstrcmp (node -> name, "receiver")) {
					xmlChar	*temp;

					st = false;
					if (extract_numeric_property (blockmail, & val, node, "customer_id")) {
						rec -> customer_id = (int) val;
						if (temp = xmlGetProp (node, char2xml ("to_email"))) {
							xmlBufferCat (rec -> to_email, temp);
							xmlFree (temp);
						}
						if (ptr = extract_property (blockmail, node, "user_type")) {
							rec -> user_type = *ptr;
							free (ptr);
						}
						if (temp = xmlGetProp (node, char2xml ("message_id"))) {
							xmlBufferCat (rec -> message_id, temp);
							xmlFree (temp);
						}
						if (extract_numeric_property (blockmail, & val, node, "mailtype")) {
							rec -> mailtype = (int) val;
							rec -> mediatypes = extract_property (blockmail, node, "mediatypes");
							if (parse_details (blockmail, doc, node -> children, rec)) {
								if (blockmail -> eval) {
									st = eval_set_data (blockmail -> eval, rec -> data, rec -> dnull, rec -> dpos);
									if (! st)
										log_out (blockmail -> lg, LV_ERROR, "Unable to update evaluator data");
								} else
									st = true;
								if (st) {
									log_idpush (blockmail -> lg, "create", "->");
									st = create_output (blockmail, rec);
									if (blockmail -> eval)
										eval_done_match (blockmail -> eval);
									log_idpop (blockmail -> lg);
									if (st) {
										log_idpush (blockmail -> lg, "write", "->");
										if (! blockmail_insync (blockmail, rec -> customer_id, rec -> mid, rec -> mailtype)) {
											if (blockmail -> active)
												st = (*blockmail -> output -> owrite) (blockmail -> outputdata, blockmail, rec);
											if (st)
												st = blockmail_tosync (blockmail, rec -> customer_id, rec -> mid, rec -> mailtype);
										}
										log_idpop (blockmail -> lg);
										if (! st)
											log_out (blockmail -> lg, LV_ERROR, "Unable to write output for receiver %d in %s", rec -> customer_id, blockmail -> fname);
										else
											blockmail -> receiver_count++;
									} else
										log_out (blockmail -> lg, LV_ERROR, "Unable to create output for receiver %d in %s", rec -> customer_id, blockmail -> fname);
								}
								if (blockmail -> eval)
									eval_done_data (blockmail -> eval);
							} else
								log_out (blockmail -> lg, LV_ERROR, "Unable to parse details for receiver %d in %s", rec -> customer_id, blockmail -> fname);
						} else
							log_out (blockmail -> lg, LV_ERROR, "Missing property mailtype in receiver %d in %s", rec -> customer_id, blockmail -> fname);
						receiver_clear (rec);
					} else
						log_out (blockmail -> lg, LV_ERROR, "Missing cutomer_id in receiver in %s", blockmail -> fname);
				} else
					unknown (blockmail, node);
			}
			if (! st)
				invalid (blockmail, node);
		}
		receiver_free (rec);
	}
	log_idpop (blockmail -> lg);
	return st;
}/*}}}*/

static bool_t
parse_blockmail (blockmail_t *blockmail, xmlDocPtr doc, xmlNodePtr base) /*{{{*/
{
	bool_t		st;
	xmlNodePtr	node;
	
	st = true;
	log_idpush (blockmail -> lg, "blockmail", "->");
	for (node = base; node && st; node = node -> next)
		if (node -> type == XML_ELEMENT_NODE) {
			if (! xmlstrcmp (node -> name, "description"))
				st = parse_description (blockmail, doc, node -> children);
			else if (! xmlstrcmp (node -> name, "general"))
				st = parse_general (blockmail, doc, node -> children);
			else if (! xmlstrcmp (node -> name, "mailcreation"))
				st = parse_mailcreation (blockmail, doc, node -> children);
			else if (! xmlstrcmp (node -> name, "mediatypes")) {
				st = parse_mediatypes (blockmail, doc, node -> children);
				if (st)
					st = blockmail_extract_mediatypes (blockmail);
			} else if (! xmlstrcmp (node -> name, "blocks")) {
				st = parse_blocks (blockmail, doc, node -> children);
			} else if (! xmlstrcmp (node -> name, "types"))
				st = parse_types (blockmail, doc, node -> children);
			else if (! xmlstrcmp (node -> name, "layout"))
				st = parse_layout (blockmail, doc, node -> children);
			else if (! xmlstrcmp (node -> name, "taglist")) {
				tag_t	*tmp;
				
				st = parse_tag (blockmail, & blockmail -> ltag, doc, node -> children);
				for (tmp = blockmail -> ltag, blockmail -> taglist_count = 0; tmp; tmp = tmp -> next)
					blockmail -> taglist_count++;
			} else if (! xmlstrcmp (node -> name, "global_tags")) {
				tag_t	*tmp;
				
				st = parse_tag (blockmail, & blockmail -> gtag, doc, node -> children);
				for (tmp = blockmail -> gtag, blockmail -> globaltag_count = 0; tmp; tmp = tmp -> next)
					blockmail -> globaltag_count++;
			} else if (! xmlstrcmp (node -> name, "dynamics"))
				st = parse_dynamics (blockmail, doc, node -> children);
			else if (! xmlstrcmp (node -> name, "urls"))
				st = parse_urls (blockmail, doc, node -> children);
			else if (! xmlstrcmp (node -> name, "receivers")) {
				st = eval_setup (blockmail -> eval);
				if (! st)
					log_out (blockmail -> lg, LV_ERROR, "Failed to setup evaluation");
				else
					st = parse_receivers (blockmail, doc, node -> children);
			} else
				unknown (blockmail, node);
			if (! st)
				invalid (blockmail, node);
		}
	log_idpop (blockmail -> lg);
	return st;
}/*}}}*/

bool_t
parse_file (blockmail_t *blockmail, xmlDocPtr doc, xmlNodePtr base) /*{{{*/
{
	bool_t		st;
	xmlNodePtr	node;

	st = false;
	for (node = base; node; node = node -> next)
		if ((node -> type == XML_ELEMENT_NODE) && (! xmlstrcmp (node -> name, "blockmail"))) {
			st = parse_blockmail (blockmail, doc, node -> children);
			if (blockmail -> eval) {
				eval_done_variables (blockmail -> eval);
				eval_done_condition (blockmail -> eval);
			}
		}
	return st;
}/*}}}*/
