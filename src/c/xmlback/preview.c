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
# include	<string.h>
# include	"xmlback.h"

typedef struct { /*{{{*/
	char	*path;
	/*}}}*/
}	preview_t;
static preview_t *
preview_alloc (void) /*{{{*/
{
	preview_t	*p;
	
	if (p = (preview_t *) malloc (sizeof (preview_t))) {
		p -> path = NULL;
	}
	return p;
}/*}}}*/
static preview_t *
preview_free (preview_t *p) /*{{{*/
{
	if (p) {
		if (p -> path)
			free (p -> path);
		free (p);
	}
	return NULL;
}/*}}}*/
static bool_t
preview_set_output_path (preview_t *p, const char *path) /*{{{*/
{
	if (p -> path)
		free (p -> path);
	p -> path = path ? strdup (path) : NULL;
	return ((! path) || p -> path) ? true : false;
}/*}}}*/

void *
preview_oinit (blockmail_t *blockmail, var_t *opts) /*{{{*/
{
	preview_t	*pv;

	if (pv = preview_alloc ()) {
		var_t		*tmp;
		const char	*path;
		
		path = NULL;
		for (tmp = opts; tmp; tmp = tmp -> next)
			if ((! tmp -> var) || var_partial_imatch (tmp, "path"))
				path = tmp -> val;
		if ((! path) || (! preview_set_output_path (pv, path)))
			pv = preview_free (pv);
	}
	return pv;
}/*}}}*/
bool_t
preview_odeinit (void *data, blockmail_t *blockmail, bool_t success) /*{{{*/
{
	preview_t	*pv = (preview_t *) data;
	
	preview_free (pv);
	return true;
}/*}}}*/

static bool_t
make_pure_header (buffer_t *dest, const byte_t *content, long length, bool_t usecrlf) /*{{{*/
{
	bool_t	st = true;
	long	n = 0;
	long	start, end;
	
	while (st && (n < length)) {
		start = n;
		while ((n  < length) && (content[n] != '\r') && (content[n] != '\n'))
			++n;
		end = n;
		if (n < length && (content[n] == '\r'))
			++n;
		if (n < length && (content[n] == '\n'))
			++n;
		if ((start < end) && (content[start] == 'H')) {
			++start;
			if ((start + 2 < end) && (content[start] == '?')) {
				++start;
				while ((start < end) && (content[start] != '?'))
					++start;
				if ((start < end) && (content[start] == '?'))
					++start;
			}
			if (start < end)
				if ((! buffer_stiff (dest, content + start, end - start)) ||
				    (! (usecrlf ? buffer_stiffsn (dest, "\r\n", 2) : buffer_stiffsn (dest, "\n", 1))))
					st = false;
		}
	}
	return st;
}/*}}}*/
static bool_t
replace_crnl_by_nl (buffer_t *dest, const byte_t *content, long length) /*{{{*/
{
	bool_t	st = true;
	long	n = 0;
	long	start = 0;
					
	while (st && (n <= length))
		if ((n == length) || ((n + 1 < length) && (content[n] == '\r') && (content[n + 1] == '\n'))) {
			if (start < n)
				if (! buffer_stiff (dest, content + start, n - start))
					st = false;
			++n;
			start = n;
		} else
			++n;
	return st;
}/*}}}*/
bool_t
preview_owrite (void *data, blockmail_t *blockmail, receiver_t *rec) /*{{{*/
{
	preview_t	*pv = (preview_t *) data;
	bool_t		st = false;
	xmlDocPtr	doc;
	
	if (doc = xmlNewDoc (char2xml ("1.0"))) {
		xmlNodePtr	root;
		rblock_t	*run;
		buffer_t	*scratch;
		
		if (root = xmlNewNode (NULL, char2xml ("preview"))) {
			xmlDocSetRootElement (doc, root);
			st = true;
			scratch = buffer_alloc (65536);
			for (run = blockmail -> rblocks; st && run; run = run -> next)
				if (run -> bname && run -> content) {
					const char	*encode = NULL;
					const byte_t	*content = xmlBufferContent (run -> content);
					long		length = xmlBufferLength (run -> content);

					if (run -> tid == TID_Unspec) {
						if (scratch) {
							buffer_clear (scratch);
							st = encode_base64 (run -> content, scratch);
							content = scratch -> buffer;
							length = scratch -> length;
							encode = "base64";
						} else
							st = false;
					} else if ((run -> tid == TID_EMail_Head) || (! blockmail -> usecrlf)) {
						if (scratch) {
							buffer_clear (scratch);
							if (run -> tid == TID_EMail_Head)
								st = make_pure_header (scratch, content, length, blockmail -> usecrlf);
							else
								st = replace_crnl_by_nl (scratch, content, length);
							content = scratch -> buffer;
							length = scratch -> length;
						} else
							st = false;
					}
					if (st) {
						xmlNodePtr	node, text;
						
						if ((node = xmlNewNode (NULL, char2xml ("content"))) &&
						    xmlNewProp (node, char2xml ("name"), char2xml (run -> bname)) &&
						    ((! encode) || xmlNewProp (node, char2xml ("encode"), char2xml (encode)))) {
							xmlAddChild (root, node);
							if (text = xmlNewTextLen (content, length)) {
								xmlAddChild (node, text);
							} else
								st = false;
						} else
							st = false;
					}
				}
			if (scratch)
				buffer_free (scratch);
		}
		if (st && pv -> path) {
			if (xmlSaveFile (pv -> path, doc) == -1)
				st = false;
		} else
			st = false;
		xmlFreeDoc (doc);
	}
	return st;
}/*}}}*/
