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
# include	<errno.h>
# include	"xmlback.h"

# if	0
static int
use_iconv (iconv_t ic, xmlBufferPtr out, xmlBufferPtr in) /*{{{*/
{
	int		rc;
	const char	*iptr;
	int		ilen;
	int		isize;
	char		*optr;
	int		olen;
	char		*obuf;
	int		osize;
	int		nsize;
	char		*temp;
	
	if (iconv (ic, NULL, NULL, NULL, NULL) == -1)
		return -1;
	rc = 0;
	iptr = xmlBufferContent (in);
	ilen = xmlBufferLength (in);
	osize = 0;
	obuf = NULL;
	nsize = -1;
	while ((rc != -1) && (ilen > 0)) {
		if (ilen < 8192)
			nsize = 8192;
		else
			nsize = ilen + 1;
		if (nsize > osize) {
			if (! (temp = realloc (obuf, nsize)))
				break;
			obuf = temp;
			osize = nsize;
		}
		optr = obuf;
		olen = osize;
		isize = ilen;
		if ((iconv (ic, & iptr, & ilen, & optr, & olen) != -1) || (errno == E2BIG)) {
			if (ilen < isize)
				if (osize - olen > 0)
					xmlBufferAdd (out, obuf, osize - olen);
		} else
			break;
	}
	if (obuf)
		free (obuf);
	if (ilen > 0)
		rc = -1;
	return rc;
}/*}}}*/
# endif
int
convert_block (xmlCharEncodingHandlerPtr translate, xmlBufferPtr in, xmlBufferPtr out, bool_t isoutput) /*{{{*/
{
	int	rc;
	
	rc = 0;
	if (translate)
		if (isoutput) {
# if	1
			if (translate -> output || translate -> iconv_out)
				if (xmlCharEncOutFunc (translate, out, in) < 0)
					rc = -1;
				else
					rc = 1;
# else			
			if (translate -> output) {
				if (xmlCharEncOutFunc (translate, out, in) < 0)
					rc = -1;
				else
					rc = 1;
			} else if (translate -> iconv_out) {
				if (use_iconv (translate -> iconv_out, out, in) < 0)
					rc = -1;
				else
					rc = 1;
			}
# endif			
		} else {
# if	1			
			if (translate -> input || translate -> iconv_in)
				if (xmlCharEncInFunc (translate, out, in) < 0)
					rc = -1;
				else
					rc = 1;
# else			
			if (translate -> input) {
				if (xmlCharEncInFunc (translate, out, in) < 0)
					rc = -1;
				else
					rc = 1;
			} else if (translate -> iconv_in) {
				if (use_iconv (translate -> iconv_in, out, in) < 0)
					rc = -1;
				else
					rc = 1;
			}
# endif			
		}
	return rc;
}/*}}}*/
bool_t
convert_charset (blockmail_t *blockmail, block_t *block) /*{{{*/
{
	bool_t	st;
	
	st = true;
	xmlBufferEmpty (block -> out);
	switch (convert_block (block -> translate, block -> in, block -> out, true)) {
	default:
	case -1:
		log_out (blockmail -> lg, LV_ERROR, "Unable to convert block %d to %s", block -> nr, block -> charset);
		st = false;
		break;
	case 0:
		xmlBufferAdd (block -> out, xmlBufferContent (block -> in), xmlBufferLength (block -> in));
		break;
	case 1:
		break;
	}
	return st;
}/*}}}*/
