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
# include	"agn.h"

/** Calculates hash value.
 * @param key the string to calculate the hash for
 * @param len the length of the key
 * @return the hash code
 */
hash_t
hash_value (const byte_t *key, int len) /*{{{*/
{
	hash_t	hash = 0;
	
	while (len-- > 0) {
		hash *= 119;
		hash |= *key++;
	}
	return hash;
}/*}}}*/
hash_t
hash_svalue (const char *key, int len, bool_t icase) /*{{{*/
{
	if (! icase)
		return hash_value ((const byte_t *) key, len);
	else {
		hash_t	hash = 0;
		
		while (len-- > 0) {
			hash *= 119;
			hash |= (unsigned char) tolower (*key++);
		}
		return hash;
	}
}/*}}}*/
bool_t
hash_match (const byte_t *key, int klen, hash_t khash, const byte_t *match, int mlen, hash_t mhash) /*{{{*/
{
	if ((klen == mlen) && (khash == mhash)) {
		if (mlen > 0)
			return memcmp (key, match, klen) == 0 ? true : false;
		return true;
	}
	return false;
}/*}}}*/
bool_t
hash_smatch (const char *key, int klen, hash_t khash, const char *match, int mlen, hash_t mhash, bool_t icase) /*{{{*/
{
	if (! icase)
		return hash_match ((const byte_t *) key, klen, khash, (const byte_t *) match, mlen, mhash);
	if ((klen == mlen) && (khash == mhash)) {
		if (mlen > 0)
			return strncasecmp (key, match, klen) == 0 ? true : false;
		return true;
	}
	return false;
}/*}}}*/
/** Find useful hashsize.
 * Taken the amount of nodes, a "good" value for the size
 * of the hash array is searched
 * @param size the number of nodes in the collection
 * @return the proposed size of the hash array
 */
int
hash_size (int size) /*{{{*/
{
	int	htab[] = {
		113,
		311,
		733,
		1601,
		3313,
		5113,
		8677,
		13121,
		25457,
		50021,
		99607
	};
	int	n;
	int	hsize;
	
	size >>= 2;
	hsize = htab[0];
	for (n = 0; n < sizeof (htab) / sizeof (htab[0]); ++n)
		if (htab[n] >= size) {
			hsize = htab[n];
			break;
		}
	return hsize;
}/*}}}*/
