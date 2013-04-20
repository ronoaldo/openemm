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

#define incr(xxx,lll)	do {								\
				if ((n = xmlValidPosition ((xxx), (lll))) == -1)	\
					return false;					\
				else {							\
					(xxx) += n;					\
					(lll) -= n;					\
				}							\
			} while (0)

bool_t
xmlSQLlike (const xmlChar *pattern, int plen,
	    const xmlChar *string, int slen) /*{{{*/
{
	const xmlChar	*cur;
	int		n, sn;
	
	while ((plen > 0) && (slen > 0)) {
		cur = pattern;
		incr (pattern, plen);
		if (*cur == '_')
			incr (string, slen);
		else if (*cur == '%') {
			while ((plen > 0) && (*pattern == '%')) {
				cur = pattern;
				incr (pattern, plen);
			}
			if (! plen)
				return true;
			while (slen > 0) {
				if (xmlSQLlike (pattern, plen, string, slen))
					return true;
				incr (string, slen);
			}
		} else {
			if ((*cur == '\\') && (plen > 0)) {
				cur = pattern;
				incr (pattern, plen);
			}
			if (((sn = xmlStrictCharLength (*string)) > slen) || (sn != n) || (n == 1 ? (*cur != *string) : memcmp (cur, string, n)))
				return false;
			incr (string, slen);
		}
	}
	if ((slen == 0) && (plen > 0))
		while ((plen > 0) && (*pattern == '%'))
			incr (pattern, plen);
	return plen == slen ? true : false;
}/*}}}*/
