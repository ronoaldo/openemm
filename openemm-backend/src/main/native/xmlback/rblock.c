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

rblock_t *
rblock_alloc (tid_t tid, const char *bname, xmlBufferPtr content) /*{{{*/
{
	rblock_t	*r;
	
	if (r = (rblock_t *) malloc (sizeof (rblock_t))) {
		r -> tid = tid;
		r -> bname = NULL;
		r -> content = NULL;
		r -> next = NULL;
		if ((bname && (! rblock_set_name (r, bname))) ||
		    (content && (! rblock_set_content (r, content))))
			r = rblock_free (r);
	}
	return r;
}/*}}}*/
rblock_t *
rblock_free (rblock_t *r) /*{{{*/
{
	if (r) {
		if (r -> bname)
			free (r -> bname);
		if (r -> content)
			xmlBufferFree (r -> content);
		free (r);
	}
	return NULL;
}/*}}}*/
rblock_t *
rblock_free_all (rblock_t *r) /*{{{*/
{
	rblock_t	*tmp;
	
	while (tmp = r) {
		r = r -> next;
		rblock_free (tmp);
	}
	return NULL;
}/*}}}*/
bool_t
rblock_set_name (rblock_t *r, const char *bname) /*{{{*/
{
	if (r -> bname)
		free (r -> bname);
	r -> bname = bname ? strdup (bname) : NULL;
	return ((! bname) || r -> bname) ? true : false;
}/*}}}*/
static inline bool_t
copy (rblock_t *r, const byte_t *content, int length) /*{{{*/
{
	if (r -> content)
		xmlBufferEmpty (r -> content);
	else
		r -> content = xmlBufferCreate ();
	if (r -> content) {
		xmlBufferAdd (r -> content, content, length);
		if (xmlBufferLength (r -> content) != length) {
			xmlBufferFree (r -> content);
			r -> content = NULL;
		}
	}
	return r -> content ? true : false;
}/*}}}*/
bool_t
rblock_set_content (rblock_t *r, xmlBufferPtr content) /*{{{*/
{
	return copy (r, xmlBufferContent (content), xmlBufferLength (content));
}/*}}}*/
bool_t
rblock_retreive_content (rblock_t *r, buffer_t *content) /*{{{*/
{
	return copy (r, content -> buffer, content -> length);
}/*}}}*/
