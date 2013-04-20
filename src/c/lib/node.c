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
/** @file node.c
 * Handle nodes in hash collection.
 */
# include	<stdlib.h>
# include	<string.h>
# include	"agn.h"

/** Allocate a generic node.
 * @param key the key
 * @param klen the length of the key
 * @param hash the hash code
 * @param data the data
 * @param dlen the length of the data
 * @return the new node on success, NULL otherwise
 */
gnode_t *
gnode_alloc (const byte_t *key, int klen, hash_t hash,
	     const byte_t *data, int dlen) /*{{{*/
{
	gnode_t	*g;
	
	if (g = (gnode_t *) malloc (sizeof (gnode_t))) {
		g -> key = (byte_t *) malloc (klen + 1);
		g -> klen = klen;
		g -> hash = hash;
		g -> data = (byte_t *) malloc (dlen + 1);
		g -> dlen = dlen;
		g -> next = NULL;
		if (g -> key && g -> data) {
			if (klen > 0)
				memcpy (g -> key, key, klen);
			if (dlen > 0)
				memcpy (g -> data, data, dlen);
		} else
			g = gnode_free (g);
	}
	return g;
}/*}}}*/
/** Frees generic node.
 * @param g the node to free
 * @return NULL
 */
gnode_t *
gnode_free (gnode_t *g) /*{{{*/
{
	if (g) {
		if (g -> key)
			free (g -> key);
		if (g -> data)
			free (g -> data);
		free (g);
	}
	return NULL;
}/*}}}*/
/** Frees generic nodes.
 * @param g the node to start
 * @return NULL
 */
gnode_t *
gnode_free_all (gnode_t *g) /*{{{*/
{
	gnode_t	*tmp;
	
	while (tmp = g) {
		g = g -> next;
		gnode_free (tmp);
	}
	return NULL;
}/*}}}*/
/** Set a generics node data.
 * @param g the node to update
 * @param data the data to use
 * @param dlen the length of the data
 */
bool_t
gnode_setdata (gnode_t *g, const byte_t *data, int dlen) /*{{{*/
{
	bool_t	rc;
	byte_t	*tmp;
	
	if ((g -> dlen >= dlen) && g -> data) {
		rc = true;
	} else if (tmp = realloc (g -> data, dlen + 1)) {
		g -> data = tmp;
		rc = true;
	} else
		rc = false;
	if (rc) {
		memcpy (g -> data, data, dlen);
		g -> dlen = dlen;
	}
	return rc;
}/*}}}*/

/** Allocate node.
 * Allocate memory for a new node
 * @param mkey the key for the hashing
 * @param hash the hash code
 * @param okey the original key
 * @param data the value for this node
 * @return the new node on success, NULL otherwise
 */
node_t *
node_alloc (const char *mkey, hash_t hash,
	    const char *okey, const char *data) /*{{{*/
{
	node_t	*n;
	
	if (n = (node_t *) malloc (sizeof (node_t))) {
		n -> mkey = NULL;
		n -> hash = hash;
		n -> okey = NULL;
		n -> data = NULL;
		n -> next = NULL;
		if ((mkey && (! (n -> mkey = strdup (mkey)))) ||
		    (okey && (! (n -> okey = strdup (okey)))) ||
		    (data && (! (n -> data = strdup (data)))))
			n = node_free (n);
	}
	return n;
}/*}}}*/
/** Frees node.
 * Return the memory allocated to the system
 * @param n the node to free
 * @return NULL
 */
node_t *
node_free (node_t *n) /*{{{*/
{
	if (n) {
		if (n -> mkey)
			free (n -> mkey);
		if (n -> okey)
			free (n -> okey);
		if (n -> data)
			free (n -> data);
		free (n);
	}
	return NULL;
}/*}}}*/
/** Free nodes.
 * Return the resources of the node and all siblings to the system
 * @param n the node to start
 * @return NULL
 */
node_t *
node_free_all (node_t *n) /*{{{*/
{
	node_t	*tmp;
	
	while (tmp = n) {
		n = n -> next;
		node_free (tmp);
	}
	return NULL;
}/*}}}*/
/** Set node data.
 * Set/change the value for this node
 * @param n the node to change
 * @param data the new content
 * @return true on success, false otherwise
 */
bool_t
node_setdata (node_t *n, const char *data) /*{{{*/
{
	if (n -> data)
		free (n -> data);
	n -> data = data ? strdup (data) : NULL;
	return (data && (! n -> data)) ? false : true;
}/*}}}*/
