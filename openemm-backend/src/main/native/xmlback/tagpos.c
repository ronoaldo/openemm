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
# include	"xmlback.h"

tagpos_t *
tagpos_alloc (void) /*{{{*/
{
	tagpos_t	*t;
	
	if (t = (tagpos_t *) malloc (sizeof (tagpos_t)))
		if (t -> name = xmlBufferCreate ()) {
			t -> hash = 0;
			t -> start = 0;
			t -> end = 0;
			t -> type = TP_NONE;
			t -> tname = NULL;
			t -> content = NULL;
		} else {
			free (t);
			t = NULL;
		}
	return t;
}/*}}}*/
tagpos_t *
tagpos_free (tagpos_t *t) /*{{{*/
{
	if (t) {
		if (t -> name)
			xmlBufferFree (t -> name);
		if (t -> tname)
			free (t -> tname);
		if (t -> content)
			block_free (t -> content);
		free (t);
	}
	return NULL;
}/*}}}*/
void
tagpos_find_name (tagpos_t *t) /*{{{*/
{
	int		len;
	const xmlChar	*ptr;
	const xmlChar	pattern[] = "name=";
	int		n;

	if (t -> tname) {
		free (t -> tname);
		t -> tname = NULL;
	}
	len = xmlBufferLength (t -> name);
	ptr = xmlBufferContent (t -> name);
	n = 0;
	while (len > 0) {
		if (*ptr == pattern[n])
			++n;
		else
			n = 0;
		++ptr;
		--len;
		if (! pattern[n])
			break;
	}
	if (len > 0) {
		bool_t		isquote;
		const xmlChar	*start, *end;
		
		if (*ptr == '"') {
			++ptr;
			--len;
			isquote = true;
		} else
			isquote = false;
		start = ptr;
		end = NULL;
		while ((len > 0) && (! end)) {
			if (isquote) {
				if (*ptr == '"')
					end = ptr;
			} else if (isspace (*ptr) || (*ptr == ']') || (*ptr == '/'))
				end = ptr;
			++ptr;
			--len;
		}
		if ((! end) && (! isquote))
			end = ptr;
		if (end) {
			len = end - start;
			if (t -> tname = malloc (len + 1)) {
				memcpy (t -> tname, start, len);
				t -> tname[len] = '\0';
			}
		}
	}
}/*}}}*/
