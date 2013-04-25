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
# include	"xmlback.h"

blockspec_t *
blockspec_alloc (void) /*{{{*/
{
	blockspec_t	*b;
	
	if (b = (blockspec_t *) malloc (sizeof (blockspec_t))) {
		b -> nr = -1;
		b -> block = NULL;
		b -> prefix = fix_alloc ();
		DO_ZERO (b, postfix);
		b -> linelength = 0;
		b -> linesep = NULL;
		b -> seplength = 0;
		b -> opl = OPL_None;
		if (! b -> prefix)
			b = blockspec_free (b);
	}
	return b;
}/*}}}*/
blockspec_t *
blockspec_free (blockspec_t *b) /*{{{*/
{
	if (b) {
		if (b -> prefix)
			fix_free (b -> prefix);
		DO_FREE (b, postfix);
		if (b -> linesep)
			free (b -> linesep);
		free (b);
	}
	return NULL;
}/*}}}*/
bool_t
blockspec_set_lineseparator (blockspec_t *b, const xmlChar *sep, int slen) /*{{{*/
{
	bool_t	rc;
	
	rc = true;
	if (! sep) {
		if (b -> linesep) {
			free (b -> linesep);
			b -> linesep = NULL;
		}
		b -> seplength = 0;
	} else if (b -> linesep = (b -> linesep ? realloc (b -> linesep, slen) : malloc (slen))) {
		memcpy (b -> linesep, sep, slen);
		b -> seplength = slen;
	} else
		rc = false;
	return rc;
}/*}}}*/
bool_t
blockspec_find_lineseparator (blockspec_t *b) /*{{{*/
{
	const xmlChar	*sep;
	int		slen;
	int		len;
	const xmlChar	*cont;
	int		n;

	sep = NULL;
	slen = 0;
	if (b -> block && b -> block -> content) {
		len = xmlBufferLength (b -> block -> content);
		cont = xmlBufferContent (b -> block -> content);
		for (n = 0; n < len; )
			if ((cont[n] == '\r') || (cont[n] == '\n')) {
				sep = cont + n;
				slen = 1;
				++n;
				if (n < len)
					if (sep[0] == '\r') {
						if (cont[n] == '\n')
							slen++;
					} else if (sep[0] == '\n') {
						if (cont[n] == '\r')
							slen++;
					}
				break;
			} else
				n += xmlCharLength (cont[n]);
		if (! sep) {
			sep = char2xml ("\r\n");
			slen = 2;
		}
	}
	return blockspec_set_lineseparator (b, sep, slen);
}/*}}}*/
