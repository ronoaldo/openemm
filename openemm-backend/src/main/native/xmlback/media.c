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
# include	<string.h>
# include	"xmlback.h"

pval_t *
pval_alloc (void) /*{{{*/
{
	pval_t	*p;
	
	if (p = (pval_t *) malloc (sizeof (pval_t))) {
		p -> v = NULL;
		p -> next = NULL;
	}
	return p;
}/*}}}*/
pval_t *
pval_free (pval_t *p) /*{{{*/
{
	if (p) {
		if (p -> v)
			xmlBufferFree (p -> v);
		free (p);
	}
	return NULL;
}/*}}}*/
pval_t *
pval_free_all (pval_t *p) /*{{{*/
{
	pval_t	*tmp;
	
	while (tmp = p) {
		p = p -> next;
		pval_free (tmp);
	}
	return NULL;
}/*}}}*/
parm_t *
parm_alloc (void) /*{{{*/
{
	parm_t	*p;
	
	if (p = (parm_t *) malloc (sizeof (parm_t))) {
		p -> name = NULL;
		p -> value = NULL;
		p -> next = NULL;
	}
	return p;
}/*}}}*/
parm_t *
parm_free (parm_t *p) /*{{{*/
{
	if (p) {
		if (p -> name)
			free (p -> name);
		if (p -> value)
			pval_free_all (p -> value);
		free (p);
	}
	return NULL;
}/*}}}*/
parm_t *
parm_free_all (parm_t *p) /*{{{*/
{
	parm_t	*tmp;
	
	while (tmp = p) {
		p = p -> next;
		parm_free (tmp);
	}
	return NULL;
}/*}}}*/
xmlBufferPtr
parm_valuecat (parm_t *p, const char *sep) /*{{{*/
{
	xmlBufferPtr	rc;
	
	rc = NULL;
	if (p && p -> value && (rc = xmlBufferCreate ())) {
		pval_t	*tmp;
		
		for (tmp = p -> value; tmp; tmp = tmp -> next) {
			if (sep && (tmp != p -> value))
				xmlBufferCCat (rc, sep);
			if (tmp -> v)
				xmlBufferAdd (rc, xmlBufferContent (tmp -> v), xmlBufferLength (tmp -> v));
		}
	}
	return rc;
}/*}}}*/
media_t *
media_alloc (void) /*{{{*/
{
	media_t	*m;
	
	if (m = (media_t *) malloc (sizeof (media_t))) {
		m -> type = MT_EMail;
		m -> prio = 0;
		m -> stat = MS_Active;
		m -> parm = NULL;
	}
	return m;
}/*}}}*/
media_t *
media_free (media_t *m) /*{{{*/
{
	if (m) {
		if (m -> parm)
			parm_free_all (m -> parm);
		free (m);
	}
	return NULL;
}/*}}}*/
bool_t
media_set_type (media_t *m, const char *type) /*{{{*/
{
	return media_parse_type (type, & m -> type);
}/*}}}*/
bool_t
media_set_priority (media_t *m, long prio) /*{{{*/
{
	m -> prio = prio;
	return true;
}/*}}}*/
bool_t
media_set_status (media_t *m, const char *status) /*{{{*/
{
	if (! strcmp (status, "unused"))
		m -> stat = MS_Unused;
	else if (! strcmp (status, "inactive"))
		m -> stat = MS_Inactive;
	else if (! strcmp (status, "active"))
		m -> stat = MS_Active;
	else
		return false;
	return true;
}/*}}}*/
parm_t *
media_find_parameter (media_t *m, const char *name) /*{{{*/
{
	parm_t	*run;
	
	for (run = m -> parm; run; run = run -> next)
		if (! strcasecmp (run -> name, name))
			break;
	return run;
}/*}}}*/
void
media_postparse (media_t *m, blockmail_t *blockmail) /*{{{*/
{
}/*}}}*/

bool_t
media_parse_type (const char *str, mtype_t *type) /*{{{*/
{
	if (! strcmp (str, "email"))
		*type = MT_EMail;
	else
		return false;
	return true;
}/*}}}*/
const char *
media_typeid (mtype_t type) /*{{{*/
{
	switch (type) {
	case MT_Unspec:
		return "";
	case MT_EMail:
		return "0";
	}
	return "";
}/*}}}*/
