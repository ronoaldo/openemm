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
# ifndef	__BAVWRAP_H
# define	__BAVWRAP_H		1
# include	"agn.h"

typedef struct { /*{{{*/
	int		msize;	/* max size in memory			*/
	buffer_t	*buf;	/* input buffer				*/
	int		fd;	/* optional file to write temp. buffer	*/
	int		size;	/* commulated size of this store	*/
	int		pos;	/* current position			*/
	/*}}}*/
}	store_t;

extern int	store_add (store_t *st, byte_t *buf, int len);
extern int	store_get (store_t *st, byte_t *buf, int room);
extern void	store_rewind (store_t *st);
extern store_t	*store_alloc (int inmemsize);
extern store_t	*store_free (store_t *st);
# endif		/* __BAVWRAP_H */
