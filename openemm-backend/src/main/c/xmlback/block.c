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

block_t *
block_alloc (void) /*{{{*/
{
	block_t	*b;
	
	if (b = (block_t *) malloc (sizeof (block_t))) {
		b -> bid = -1;
		b -> nr = -1;
		b -> mime = NULL;
		b -> charset = NULL;
		b -> encode = NULL;
		b -> method = EncNone;
		b -> cid = NULL;
		b -> tid = TID_Unspec;
		b -> binary = false;
		b -> attachment = false;
		b -> media = NULL;
		b -> mediatype = MT_Unspec;
		b -> condition = NULL;
		b -> content = NULL;
		b -> translate = NULL;
		b -> in = NULL;
		b -> out = NULL;
		b -> bcontent = NULL;
		b -> bout = NULL;
		DO_ZERO (b, tagpos);
		b -> inuse = false;
	}
	return b;
}/*}}}*/
block_t *
block_free (block_t *b) /*{{{*/
{
	if (b) {
		if (b -> mime)
			free (b -> mime);
		if (b -> charset)
			free (b -> charset);
		if (b -> encode)
			free (b -> encode);
		if (b -> cid)
			free (b -> cid);
		if (b -> media)
			free (b -> media);
		if (b -> condition)
			xmlBufferFree (b -> condition);
		if (b -> content)
			xmlBufferFree (b -> content);
		if (b -> translate)
			xmlCharEncCloseFunc (b -> translate);
		if (b -> in)
			xmlBufferFree (b -> in);
		if (b -> out)
			xmlBufferFree (b -> out);
		if (b -> bcontent)
			buffer_free (b -> bcontent);
		if (b -> bout)
			buffer_free (b -> bout);
		DO_FREE (b, tagpos);
		free (b);
	}
	return NULL;
}/*}}}*/
bool_t
block_setup_charset (block_t *b) /*{{{*/
{
	bool_t	st;
	
	st = false;
	if (b -> translate) {
		xmlCharEncCloseFunc (b -> translate);
		b -> translate = NULL;
	}
	if ((! b -> charset) || (b -> translate = xmlFindCharEncodingHandler (b -> charset))) {
		if (b -> translate)
			if (! (b -> translate -> input || b -> translate -> iconv_in ||
			       b -> translate -> output || b -> translate -> iconv_out)) {
				xmlCharEncCloseFunc (b -> translate);
				b -> translate = NULL;
			}
		if (! b -> in)
			b -> in = xmlBufferCreate ();
		if (! b -> out)
			b -> out = xmlBufferCreate ();
		if (b -> in && b -> out)
			st = true;
	}
	return st;
}/*}}}*/
void
block_find_method (block_t *b) /*{{{*/
{
	static struct {
		const char	*name;
		encoding_t	code;
	}	codetab[] = {
		{	"none",			EncNone			},
		{	"header",		EncHeader		},
		{	"8bit",			Enc8bit			},
		{	"quoted-printable",	EncQuotedPrintable	},
		{	"base64",		EncBase64		}
	};
	
	b -> method = EncNone;
	if (b -> encode) {
		int	n;
		
		for (n = 0; n < sizeof (codetab) / sizeof (codetab[0]); ++n)
			if (! strcmp (b -> encode, codetab[n].name)) {
				b -> method = codetab[n].code;
				break;
			}
	}
}/*}}}*/
bool_t
block_code_binary_out (block_t *b) /*{{{*/
{
	bool_t	st;
	int	current, assume;
	
	st = false;
	current = b -> bcontent -> length;
	assume = -1;
	switch (b -> method) {
	case EncNone:			/* 100% */
		assume = current;
		break;
	case EncHeader:			/* 200% */
		assume = current * 2;
		break;
	case Enc8bit:			/* 110% */
		assume = (current * 11) / 10;
		break;
	case EncQuotedPrintable:	/* 150% */
		assume = (current * 3) / 2;
		break;
	case EncBase64:			/* 150% */
		assume = (current * 3) / 2;
		break;
	}
	if (assume != -1) {
		if (! b -> bout)
			b -> bout = buffer_alloc (assume + 4);
		else
			b -> bout -> length = 0;
		if (b -> bout) {
			xmlBuffer	temp;	/* to avoid copying around */
			
			temp.content = b -> bcontent -> buffer;
			temp.use = b -> bcontent -> length;
			temp.size = b -> bcontent -> size;
			temp.alloc = -1;
			switch (b -> method) {
			case EncNone:
				st = encode_none (& temp, b -> bout);
				break;
			case EncHeader:
				st = encode_header (& temp, b -> bout, b -> charset);
				break;
			case Enc8bit:
				st = encode_8bit (& temp, b -> bout);
				break;
			case EncQuotedPrintable:
				st = encode_quoted_printable (& temp, b -> bout);
				break;
			case EncBase64:
				st = encode_base64 (& temp, b -> bout);
				break;
			}
		}
	}
	return st;
}/*}}}*/
bool_t
block_code_binary (block_t *b) /*{{{*/
{
	bool_t	st;
	
	st = false;
	if (b -> bcontent)
		b -> bcontent = buffer_free (b -> bcontent);
	if (b -> binary && b -> content && (b -> bcontent = buffer_alloc (xmlBufferLength (b -> content) + 1))) {
		st = decode_base64 (b -> content, b -> bcontent);
		if (! st)
			b -> bcontent = buffer_free (b -> bcontent);
	}
	if (st)
		st = block_code_binary_out (b);
	return st;
}/*}}}*/
bool_t
block_match (block_t *b, eval_t *eval) /*{{{*/
{
	if (! b -> condition)
		return true;
	return eval_match (eval, SP_BLOCK, b -> bid);
}/*}}}*/
