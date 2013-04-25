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

postfix_t *
postfix_alloc (void) /*{{{*/
{
	postfix_t	*p;
	
	if (p = (postfix_t *) malloc (sizeof (postfix_t)))
		if (p -> c = fix_alloc ()) {
			p -> pid = NULL;
			p -> after = -1;
			p -> ref = NULL;
			p -> stack = NULL;
		} else {
			free (p);
			p = NULL;
		}
	return p;
}/*}}}*/
postfix_t *
postfix_free (postfix_t *p) /*{{{*/
{
	if (p) {
		if (p -> c)
			fix_free (p -> c);
		if (p -> pid)
			free (p -> pid);
		free (p);
	}
	return NULL;
}/*}}}*/
