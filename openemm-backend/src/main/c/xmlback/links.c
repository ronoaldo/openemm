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

links_t *
links_alloc (void) /*{{{*/
{
	links_t	*l;
	
	if (l = (links_t *) malloc (sizeof (links_t))) {
		l -> l = NULL;
		l -> seen = NULL;
		l -> lcnt = 0;
		l -> lsiz = 0;
	}
	return l;
}/*}}}*/
links_t *
links_free (links_t *l) /*{{{*/
{
	if (l) {
		if (l -> l) {
			int	n;
			
			for (n = 0; n < l -> lcnt; ++n)
				if (l -> l[n])
					free (l -> l[n]);
			free (l -> l);
		}
		free (l);
	}
	return NULL;
}/*}}}*/
bool_t
links_expand (links_t *l) /*{{{*/
{
	if (l -> lcnt >= l -> lsiz) {
		int	nsiz;
		char	**temp;
		
		nsiz = l -> lsiz ? l -> lsiz * 2 : 32;
		if (temp = (char **) realloc (l -> l, nsiz * sizeof (char *))) {
			l -> l = temp;
			l -> seen = (bool_t *) realloc (l -> seen, nsiz * sizeof (bool_t));
			l -> lsiz = nsiz;
		}
	}
	return l -> lcnt < l -> lsiz ? true : false;
}/*}}}*/
bool_t
links_nadd (links_t *l, const char *lnk, int llen) /*{{{*/
{
	bool_t	st;
	int	n, len;
	
	st = false;
	for (n = 0; n < l -> lcnt; ++n)
		if (((len = strlen (l -> l[n])) == llen) && (! memcmp (l -> l[n], lnk, len)))
			break;
	if (n < l -> lcnt)
		st = true;
	else if (links_expand (l) && (l -> l[l -> lcnt] = malloc (llen + 1))) {
		memcpy (l -> l[l -> lcnt], lnk, llen);
		l -> l[l -> lcnt][llen] = '\0';
		if (l -> seen)
			l -> seen[l -> lcnt] = false;
		l -> lcnt++;
		st = true;
	}
	return st;
}/*}}}*/

