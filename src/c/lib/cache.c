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
# include	"agn.h"

static centry_t *
centry_free (centry_t *c) /*{{{*/
{
	if (c) {
		if (c -> key)
			free (c -> key);
		if (c -> data)
			free (c -> data);
		free (c);
	}
	return NULL;
}/*}}}*/
static centry_t *
centry_alloc (hash_t hash, const byte_t *key, int klen, const byte_t *data, int dlen) /*{{{*/
{
	centry_t	*c;
	
	if (c = (centry_t *) malloc (sizeof (centry_t))) {
		c -> hash = hash;
		c -> key = NULL;
		c -> klen = klen;
		c -> data = NULL;
		c -> dlen = dlen;
		c -> prev = c -> next = NULL;
		c -> back = c -> forw = NULL;
		if ((c -> key = malloc (klen)) && (c -> data = malloc (dlen))) {
			memcpy (c -> key, key, klen);
			memcpy (c -> data, data, dlen);
		} else
			c = centry_free (c);
	}
	return c;
}/*}}}*/
static bool_t
centry_update_key (centry_t *ce, hash_t hash, const byte_t *key, int klen) /*{{{*/
{
	if (ce -> key = realloc (ce -> key, klen)) {
		memcpy (ce -> key, key, klen);
		ce -> klen = klen;
		ce -> hash = hash;
	} else {
		ce -> klen = 0;
		ce -> hash = 0;
	}
	return ce -> klen == klen ? true : false;
}/*}}}*/
static bool_t
centry_update_data (centry_t *ce, const byte_t *data, int dlen) /*{{{*/
{
	if (ce -> data = realloc (ce -> data, dlen)) {
		memcpy (ce -> data, data, dlen);
		ce -> dlen = dlen;
	} else
		ce -> dlen = 0;
	return ce -> dlen == dlen ? true : false;
}/*}}}*/
static bool_t
centry_update (centry_t *ce, hash_t hash, const byte_t *key, int klen, const byte_t *data, int dlen) /*{{{*/
{
	return centry_update_key (ce, hash, key, klen) && centry_update_data (ce, data, dlen) ? true : false;
}/*}}}*/

cache_t *
cache_free (cache_t *c) /*{{{*/
{
	if (c) {
		if (c -> store) {
			int	n;
			
			for (n = 0; n < c -> hsize; ++n) {
				centry_t	*cur, *temp;
				
				for (cur = c -> store[n]; cur; ) {
					temp = cur;
					cur = cur -> next;
					centry_free (temp);
				}
			}
			free (c -> store);
		}
		free (c);
	}
	return NULL;
}/*}}}*/
cache_t *
cache_alloc (int size) /*{{{*/
{
	cache_t	*c;
	
	if (c = (cache_t *) malloc (sizeof (cache_t))) {
		c -> size = size;
		c -> hsize = hash_size (size);
		c -> count = 0;
		c -> store = NULL;
		c -> head = c -> tail = NULL;
		if (c -> store = (centry_t **) malloc (c -> hsize * sizeof (centry_t))) {
			int	n;
			
			for (n = 0; n < c -> hsize; ++n)
				c -> store[n] = NULL;
		} else
			c = cache_free (c);
	}
	return c;
}/*}}}*/

static centry_t *
cache_unlink (cache_t *c, centry_t *ce, bool_t full) /*{{{*/
{
	if (ce) {
		if (ce -> back)
			ce -> back -> forw = ce -> forw;
		else
			c -> head = ce -> forw;
		if (ce -> forw)
			ce -> forw -> back = ce -> back;
		else
			c -> tail = ce -> back;
		ce -> back = ce -> forw = NULL;
		
		if (full) {
			int	idx = ce -> hash % c -> hsize;
			
			if (ce -> prev)
				ce -> prev -> next = ce -> next;
			else
				c -> store[idx] = ce -> next;
			if (ce -> next)
				ce -> next -> prev = ce -> prev;
			ce -> prev = ce -> next = NULL;
			c -> count--;
		}
	}
	return ce;
}/*}}}*/
static centry_t *
cache_link (cache_t *c, centry_t *ce, bool_t full) /*{{{*/
{
	if (ce) {
		ce -> forw = c -> head;
		if (ce -> forw)
			ce -> forw -> back = ce;
		else
			c -> tail = ce;
		c -> head = ce;

		if (full) {
			int	idx = ce -> hash % c -> hsize;

			ce -> next = c -> store[idx];
			if (ce -> next)
				ce -> next -> prev = ce;
			c -> store[idx] = ce;
			c -> count++;
		}
	}
	return ce;
}/*}}}*/

centry_t *
cache_find (cache_t *c, const byte_t *key, int klen) /*{{{*/
{
	hash_t		hash = hash_value (key, klen);
	int		idx = hash % c -> hsize;
	centry_t	*ce;

	for (ce = c -> store[idx]; ce; ce = ce -> next)
		if (hash_match (key, klen, hash, ce -> key, ce -> klen, ce -> hash)) {
			cache_unlink (c, ce, false);
			cache_link (c, ce, false);
			return ce;
		}
	return NULL;
}/*}}}*/
centry_t *
cache_add (cache_t *c, const byte_t *key, int klen, const byte_t *data, int dlen) /*{{{*/
{
	hash_t		hash = hash_value (key, klen);
	int		idx = hash % c -> hsize;
	centry_t	*ce;

	for (ce = c -> store[idx]; ce; ce = ce -> next)
		if (hash_match (key, klen, hash, ce -> key, ce -> klen, ce -> hash))
			break;
	if (ce)
		centry_update_data (ce, data, dlen);
	else {
		if (c -> count < c -> size)
			ce = centry_alloc (hash, key, klen, data, dlen);
		else
			ce = cache_unlink (c, c -> tail, true);
		if (ce) {
			centry_update (ce, hash, key, klen, data, dlen);
			cache_link (c, ce, true);
		}
	}
	return ce;
}/*}}}*/
void
cache_remove (cache_t *c, centry_t *ce) /*{{{*/
{
	if (ce) {
		cache_unlink (c, ce, true);
		centry_free (ce);
	}
}/*}}}*/
void
cache_delete (cache_t *c, const byte_t *key, int klen) /*{{{*/
{
	cache_remove (c, cache_find (c, key, klen));
}/*}}}*/
