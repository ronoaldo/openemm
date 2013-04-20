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

dyn_t *
dyn_alloc (int did, int order) /*{{{*/
{
	dyn_t	*d;
	
	if (d = (dyn_t *) malloc (sizeof (dyn_t))) {
		d -> did = did;
		d -> name = NULL;
		d -> order = order;
		d -> condition = NULL;
		DO_ZERO (d, block);
		d -> sibling = NULL;
		d -> next = NULL;
	}
	return d;
}/*}}}*/
dyn_t *
dyn_free (dyn_t *d) /*{{{*/
{
	if (d) {
		if (d -> name)
			free (d -> name);
		if (d -> condition)
			xmlBufferFree (d -> condition);
		DO_FREE (d, block);
		if (d -> sibling)
			dyn_free_all (d -> sibling);
		free (d);
	}
	return NULL;
}/*}}}*/
dyn_t *
dyn_free_all (dyn_t *d) /*{{{*/
{
	dyn_t	*tmp;
	
	while (tmp = d) {
		d = d -> next;
		dyn_free (tmp);
	}
	return NULL;
}/*}}}*/
bool_t
dyn_match (const dyn_t *d, eval_t *eval) /*{{{*/
{
	/* trivial case */
	if (! d -> condition)
		return true;
	return eval_match (eval, SP_DYNAMIC, d -> did);
}/*}}}*/
