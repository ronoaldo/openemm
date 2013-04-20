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

# ifndef	I
# define	I	/* */
# endif		/* I */

# ifndef	__MISC_C
# define	__MISC_C		1
I bool_t
xmlEqual (xmlBufferPtr p1, xmlBufferPtr p2) /*{{{*/
{
	int		len;
	const xmlChar	*c1, *c2;
	
	len = xmlBufferLength (p1);
	if ((len == xmlBufferLength (p2)) &&
	    (c1 = xmlBufferContent (p1)) &&
	    (c2 = xmlBufferContent (p2)) &&
	    ((! len) || (! memcmp (c1, c2, len * sizeof (xmlChar)))))
		return true;
	return false;
}/*}}}*/
I int
xmlCharLength (xmlChar ch) /*{{{*/
{
	extern int	xmlLengthtab[256];
	
	return xmlLengthtab[ch];
}/*}}}*/
I int
xmlStrictCharLength (xmlChar ch) /*{{{*/
{
	extern int	xmlStrictLengthtab[256];
	
	return xmlStrictLengthtab[ch];
}/*}}}*/
I int
xmlValidPosition (const xmlChar *str, int length) /*{{{*/
{
# define	VALID(ccc)	(((ccc) & 0xc0) == 0x80)
	int	len, n;
	
	if (((len = xmlStrictCharLength (*str)) > 0) && (length >= len))
		for (n = len; n > 1; ) {
			--n;
			if (! VALID (*(str + n))) {
				len = -1;
				break;
			}
		}
	else
		len = -1;
	return len;
# undef		VALID	
}/*}}}*/
I bool_t
xmlValid (const xmlChar *str, int length) /*{{{*/
{
	int	n;
	
	while (length > 0)
		if ((n = xmlValidPosition (str, length)) > 0) {
			str += n;
			length -= n;
		} else
			break;
	return length == 0 ? true : false;
}/*}}}*/
I char *
xml2string (xmlBufferPtr p) /*{{{*/
{
	int		len = xmlBufferLength (p);
	const xmlChar	*ptr = xmlBufferContent (p);
	char		*rc;
	
	if (rc = malloc (len + 1)) {
		if (len > 0)
			memcpy (rc, ptr, len);
		rc[len] = '\0';
	}
	return rc;
}/*}}}*/
I const char *
xml2char (const xmlChar *s) /*{{{*/
{
	return (const char *) s;
}/*}}}*/
I const xmlChar *
char2xml (const char *s) /*{{{*/
{
	return (const xmlChar *) s;
}/*}}}*/
I const char *
byte2char (const byte_t *b) /*{{{*/
{
	return (const char *) b;
}/*}}}*/
I int
xmlstrcmp (const xmlChar *s1, const char *s2) /*{{{*/
{
	return strcmp (xml2char (s1), s2);
}/*}}}*/
I int
xmlstrncmp (const xmlChar *s1, const char *s2, size_t n) /*{{{*/
{
	return strncmp (xml2char (s1), s2, n);
}/*}}}*/
I long
xml2long (xmlBufferPtr p) /*{{{*/
{
	int		len = xmlBufferLength (p);
	const xmlChar	*ptr = xmlBufferContent (p);
	char		scratch[128];
	
	if (len >= sizeof (scratch))
		len = sizeof (scratch) - 1;
	memcpy (scratch, ptr, len);
	scratch[len] = '\0';
	return strtol (scratch, NULL, 0);
}/*}}}*/
# endif		/* __MISC_C */
