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

receiver_t *
receiver_alloc (int data_blocks) /*{{{*/
{
	receiver_t	*r;
	
	if (r = (receiver_t *) malloc (sizeof (receiver_t))) {
		r -> customer_id = -1;
		r -> user_type = '\0';
		r -> to_email = xmlBufferCreate ();
		r -> message_id = xmlBufferCreate ();
		r -> mailtype = 0;
		r -> mediatypes = NULL;
		r -> media = NULL;
		r -> mid[0] = '0';
		r -> mid[1] = '\0';
		r -> tag = NULL;
		DO_ZERO (r, url);
		r -> data = NULL;
		r -> dnull = NULL;
		r -> dsize = 0;
		r -> dpos = 0;
		r -> cache = NULL;
		if ((! r -> to_email) ||
		    (! r -> message_id))
			r = receiver_free (r);
		else
			if (data_blocks > 0)
				if ((r -> data = (xmlBufferPtr *) malloc (data_blocks * sizeof (xmlBufferPtr))) &&
				    (r -> dnull = (bool_t *) malloc (data_blocks * sizeof (bool_t)))) {
					int	n;
				
					for (n = 0; n < data_blocks; ++n) {
						r -> data[n] = NULL;
						r -> dnull[n] = false;
					}
					r -> dsize = data_blocks;
				} else
					r = receiver_free (r);
	}
	return r;
}/*}}}*/
receiver_t *
receiver_free (receiver_t *r) /*{{{*/
{
	if (r) {
		receiver_clear (r);
		if (r -> to_email)
			xmlBufferFree (r -> to_email);
		if (r -> message_id)
			xmlBufferFree (r -> message_id);
		if (r -> mediatypes)
			free (r -> mediatypes);
		if (r -> data) {
			int	n;
			
			for (n = 0; n < r -> dsize; ++n)
				if (r -> data[n])
					xmlBufferFree (r -> data[n]);
			free (r -> data);
		}
		if (r -> dnull)
			free (r -> dnull);
		free (r);
	}
	return NULL;
}/*}}}*/
void
receiver_clear (receiver_t *r) /*{{{*/
{
	if (r -> to_email)
		xmlBufferEmpty (r -> to_email);
	if (r -> message_id)
		xmlBufferEmpty (r -> message_id);
	r -> mailtype = 0;
	if (r -> mediatypes) {
		free (r -> mediatypes);
		r -> mediatypes = NULL;
	}
	r -> media = NULL;
	r -> mid[0] = '0';
	r -> mid[1] = '\0';
	r -> tag = tag_free_all (r -> tag);
	DO_FREE (r, url);
	if (r -> data) {
		int	n;
		
		for (n = 0; n < r -> dsize; ++n) {
			if (r -> data[n])
				xmlBufferEmpty (r -> data[n]);
			r -> dnull[n] = false;
		}
		r -> dpos = 0;
	}
	r -> cache = dcache_free_all (r -> cache);
}/*}}}*/
