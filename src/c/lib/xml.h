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
# ifndef	__LIB_XML_H
# define	__LIB_XML_H		1
# include	"agn.h"

/* Thin wrapper around buffer_t for handling buffers with
 * XML data
 */
typedef byte_t		xchar_t;
typedef buffer_t	xmlbuf_t;
typedef struct { /*{{{*/
	int	csize;
	cache_t	*lower,
		*upper,
		*title;
	/*}}}*/
}	xconv_t;

extern int		xchar_length (xchar_t ch);
extern int		xchar_strict_length (xchar_t ch);
extern int		xchar_valid_position (const xchar_t *s, int length);
extern bool_t		xchar_valid (const xchar_t *s, int length);
extern const char	*xchar_to_char (const xchar_t *s);
extern const xchar_t	*char_2_xchar (const char *s);
extern const char	*byte_to_char (const byte_t *b);
extern int		xstrlen (const xchar_t *s);
extern int		xstrcmp (const xchar_t *s1, const char *s2);
extern int		xstrncmp (const xchar_t *s1, const char *s2, size_t n);
extern bool_t		xmlbuf_equal (xmlbuf_t *b1, xmlbuf_t *b2);
extern char		*xmlbuf_to_string (xmlbuf_t *b);
extern long		xmlbuf_to_long (xmlbuf_t *b);

static inline xmlbuf_t *
xmlbuf_alloc (int nsize) /*{{{*/
{
	return (xmlbuf_t *) buffer_alloc (nsize);
}/*}}}*/
static inline xmlbuf_t *
xmlbuf_free (xmlbuf_t *b) /*{{{*/
{
	return (xmlbuf_t *) buffer_free ((buffer_t *) b);
}/*}}}*/
static inline void
xmlbuf_clear (xmlbuf_t *b) /*{{{*/
{
	buffer_clear ((buffer_t *) b);
}/*}}}*/
static inline int
xmlbuf_length (xmlbuf_t *b) /*{{{*/
{
	return buffer_length ((buffer_t *) b);
}/*}}}*/
static inline const xchar_t *
xmlbuf_content (xmlbuf_t *b) /*{{{*/
{
	return (const xchar_t *) buffer_content ((buffer_t *) b);
}/*}}}*/
static inline const char *
xmlbuf_string (xmlbuf_t *b) /*{{{*/
{
	return buffer_string ((buffer_t *) b);
}/*}}}*/
static inline char *
xmlbuf_copystring (xmlbuf_t *b) /*{{{*/
{
	return buffer_copystring ((buffer_t *) b);
}/*}}}*/
static inline xmlbuf_t *
pool_xrequest (pool_t *p, int nsize) /*{{{*/
{
	return (xmlbuf_t *) pool_request (p, nsize);
}/*}}}*/
static inline xmlbuf_t *
pool_xrelease (pool_t *p, xmlbuf_t *b) /*{{{*/
{
	return (xmlbuf_t *) pool_release (p, (buffer_t *) b);
}/*}}}*/

extern const xchar_t	*xtolower (const xchar_t *s, int *slen, int *olen);
extern const xchar_t	*xtoupper (const xchar_t *s, int *slen, int *olen);
extern const xchar_t	*xtotitle (const xchar_t *s, int *slen, int *olen);
extern xchar_t		*xlowern (const xchar_t *s, int len, int *olen);
extern xchar_t		*xlower (const xchar_t *s, int *olen);
extern xchar_t		*xuppern (const xchar_t *s, int len, int *olen);
extern xchar_t		*xupper (const xchar_t *s, int *olen);
extern xchar_t		*xtitlen (const xchar_t *s, int len, int *olen);
extern xchar_t		*xtitle (const xchar_t *s, int *olen);

extern xconv_t		*xconv_free (xconv_t *xc);
extern xconv_t		*xconv_alloc (int cache_size);
extern const xchar_t	*xconv_lower (xconv_t *xc, const xchar_t *s, int slen, int *olen);
extern const xchar_t	*xconv_upper (xconv_t *xc, const xchar_t *s, int slen, int *olen);
extern const xchar_t	*xconv_title (xconv_t *xc, const xchar_t *s, int slen, int *olen);
#endif		/* __LIB_XML_H */
