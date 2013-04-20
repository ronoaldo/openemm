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
# ifndef	__BAV_H
# define	__BAV_H		1
# include	"agn.h"

# define	ID_ACCEPT	"accept"
# define	ID_TEMPFAIL	"tempfail"
# define	ID_REJECT	"reject"
# define	ID_RELAY	ID_ACCEPT ":rid=relay"

typedef struct { /*{{{*/
	map_t	*amap;
	set_t	*hosts;
	/*}}}*/
}	cfg_t;

extern cfg_t	*cfg_alloc (const char *fname);
extern cfg_t	*cfg_free (cfg_t *c);
extern char	*cfg_valid_address (cfg_t *c, const char *addr);
# endif		/* __BAV_H */
