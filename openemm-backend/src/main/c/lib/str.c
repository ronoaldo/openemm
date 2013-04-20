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
/** @file str.c
 * String utilities.
 */
# include	<stdlib.h>
# include	<ctype.h>
# include	<string.h>
# include	"agn.h"

/** Reuse a string variable.
 * Create a copy of input string, free up old memory, if neccessary
 * @param buf the destination to copy to, freeing used memory
 * @param str the string to copy
 * @return true on success, false otherwise
 */
bool_t
struse (char **buf, const char *str) /*{{{*/
{
	if (*buf)
		free (*buf);
	*buf = str ? strdup (str) : NULL;
	return (! str) || *buf ? true : false;
}/*}}}*/
char *
strldup (const char *s) /*{{{*/
{
	char	*rc;
	
	if (rc = malloc (strlen (s) + 1)) {
		char	*ptr;
		
		for (ptr = rc; *ptr++ = tolower (*s++);)
			;
	}
	return rc;
}/*}}}*/
char *
strudup (const char *s) /*{{{*/
{
	char	*rc;
	
	if (rc = malloc (strlen (s) + 1)) {
		char	*ptr;
		
		for (ptr = rc; *ptr++ = toupper (*s++);)
			;
	}
	return rc;
}/*}}}*/
