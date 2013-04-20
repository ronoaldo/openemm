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

static sentry_t *
sentry_free (sentry_t *s) /*{{{*/
{
	if (s) {
		if (s -> name)
			free (s -> name);
		free (s);
	}
	return NULL;
}/*}}}*/
static sentry_t *
sentry_free_all (sentry_t *s) /*{{{*/
{
	sentry_t	*tmp;
	
	while (tmp = s) {
		s = s -> next;
		sentry_free (tmp);
	}
	return NULL;
}/*}}}*/
static sentry_t *
sentry_alloc (const char *name, int nlen, hash_t hash) /*{{{*/
{
	sentry_t	*s;
	
	if (s = (sentry_t *) malloc (sizeof (sentry_t)))
		if (s -> name = malloc (nlen + 1)) {
			if (nlen > 0)
				memcpy (s -> name, name, nlen);
			s -> name[nlen] = 0;
			s -> nlen = nlen;
			s -> hash = hash;
			s -> next = NULL;
		} else {
			free (s);
			s = NULL;
		}
	return s;
}/*}}}*/
static inline bool_t
finder (set_t *s, const char *name, int nlen, hash_t hash) /*{{{*/
{
	sentry_t	*run;
	
	for (run = s -> s[hash % s -> hsize]; run; run = run -> next)
		if (hash_smatch (run -> name, run -> nlen, run -> hash, name, nlen, hash, s -> icase))
			return true;
	return false;
}/*}}}*/
set_t *
set_alloc (bool_t icase, int aproxsize) /*{{{*/
{
	set_t	*s;
	
	if (s = (set_t *) malloc (sizeof (set_t))) {
		s -> icase = icase;
		s -> hsize = hash_size (aproxsize);
		s -> count = 0;
		if (s -> s = (sentry_t **) malloc (s -> hsize * sizeof (sentry_t))) {
			int	n;
			
			for (n = 0; n < s -> hsize; ++n)
				s -> s[n] = NULL;
		} else {
			free (s);
			s = NULL;
		}
	}
	return s;
}/*}}}*/
set_t *
set_free (set_t *s) /*{{{*/
{
	if (s) {
		if (s -> s) {
			int	n;
			
			for (n = 0; n < s -> hsize; ++n)
				sentry_free_all (s -> s[n]);
			free (s -> s);
		}
		free (s);
	}
	return NULL;
}/*}}}*/
bool_t
set_add (set_t *s, const char *name, int nlen) /*{{{*/
{
	bool_t	rc = true;
	hash_t	hash = hash_svalue (name, nlen, s -> icase);
	
	if (! finder (s, name, nlen, hash)) {
		sentry_t	*e = sentry_alloc (name, nlen, hash);
		
		if (e) {
			int	idx = hash % s -> hsize;
			
			e -> next = s -> s[idx];
			s -> s[idx] = e;
			s -> count++;
		} else
			rc = false;
	}
	return rc;
}/*}}}*/
void
set_remove (set_t *s, const char *name, int nlen) /*{{{*/
{
	hash_t		hash;
	sentry_t	*run, *prv;
	int		idx;
	
	hash = hash_svalue (name, nlen, s -> icase);
	idx = hash % s -> hsize;
	for (run = s -> s[idx], prv = NULL; run; run = run -> next)
		if (hash_smatch (run -> name, run -> nlen, run -> hash, name, nlen, hash, s -> icase))
			break;
	if (run) {
		if (prv)
			prv -> next = run -> next;
		else
			s -> s[idx] = run -> next;
		sentry_free (run);
		s -> count--;
	}
}/*}}}*/
bool_t
set_find (set_t *s, const char *name, int nlen) /*{{{*/
{
	return finder (s, name, nlen, hash_svalue (name, nlen, s -> icase));
}/*}}}*/
