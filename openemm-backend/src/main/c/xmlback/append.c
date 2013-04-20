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

static bool_t
final_eol (buffer_t *dest) /*{{{*/
{
	if ((dest -> length > 0) && (! buffer_iseol (dest, dest -> length - 1)))
		return buffer_appendcrlf (dest);
	return true;
}/*}}}*/
bool_t
append_mixed (buffer_t *dest, const char *desc, ...) /*{{{*/
{
	va_list		par;
	bool_t		rc;
	int		n;
	
	va_start (par, desc);
	rc = true;
	for (n = 0; rc && desc[n]; ++n)
		switch (desc[n]) {
		case 's':
			rc = buffer_stiffs (dest, va_arg (par, const char *));
			break;
		case 'b':
			{
				xmlBufferPtr	temp;
				
				temp = va_arg (par, xmlBufferPtr);
				rc = buffer_stiff (dest, xmlBufferContent (temp), xmlBufferLength (temp));
			}
			break;
		case 'i':
			{
				int		len;
				char		scratch[32];
				
				len = sprintf (scratch, "%d", va_arg (par, int));
				rc = buffer_stiffsn (dest, scratch, len);
			}
			break;
		default:
			rc = false;
			break;
		}
	return rc;
}/*}}}*/
bool_t
append_pure (buffer_t *dest, const xmlBufferPtr src) /*{{{*/
{
	return buffer_stiff (dest, xmlBufferContent (src), xmlBufferLength (src));
}/*}}}*/
bool_t
append_raw (buffer_t *dest, const buffer_t *src) /*{{{*/
{
	if (src -> length)
		return (buffer_stiff (dest, src -> buffer, src -> length) && buffer_stiffcrlf (dest)) ? true : false;
	return true;
}/*}}}*/
bool_t
append_cooked (buffer_t *dest, const xmlBufferPtr src,
	       const char *charset, encoding_t method) /*{{{*/
{
	bool_t	st;
	
	st = false;
	switch (method) {
	case EncNone:
		st = encode_none (src, dest);
		break;
	case EncHeader:
		st = encode_header (src, dest, charset);
		break;
	case Enc8bit:
		st = encode_8bit (src, dest);
		break;
	case EncQuotedPrintable:
		st = encode_quoted_printable (src, dest);
		break;
	case EncBase64:
		st = encode_base64 (src, dest);
		break;
	}
	if (st)
		st = final_eol (dest);
	return st;
}/*}}}*/
