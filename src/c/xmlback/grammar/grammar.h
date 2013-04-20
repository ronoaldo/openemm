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
# ifndef	__GRAMMAR_H
# define	__GRAMMAR_H		1
# include	"xmlback.h"

typedef struct { /*{{{*/
	int		tid;
	char		*token;
	/*}}}*/
}	token_t;

typedef struct { /*{{{*/
	buffer_t	*buf;
	unsigned long	errcnt;
	buffer_t	*parse_error;
	xconv_t		*xconv;
	/*}}}*/
}	private_t;

extern token_t		*token_alloc (int tid, const char *token);
extern token_t		*token_free (token_t *t);

extern bool_t		transform (buffer_t *buf, const xmlChar *input, int input_length, buffer_t *parse_error, xconv_t *xconv);
# ifndef	NDEBUG
extern bool_t		transformtable_check (buffer_t *out);
# endif		/* NDEBUG */

extern void		ParseTrace (FILE *, char *);
extern const char	*ParseTokenName (int);
extern void		*ParseAlloc (void *(*) (size_t));
extern void		ParseFree (void *, void (*) (void *));
extern void		Parse (void *, int, token_t *, private_t *);
#endif		/* __GRAMMAR_H */
