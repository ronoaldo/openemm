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
# include	<unistd.h>
# include	<paths.h>
# include	"bavwrap.h"

int
store_add (store_t *st, byte_t *buf, int len) /*{{{*/
{
	int	rc;
	
	rc = -1;
	if (st -> fd != -1) {
		if ((lseek (st -> fd, 0, SEEK_END) != -1) && (write (st -> fd, buf, len) == len))
			rc = 0;
	} else
		if (st -> size + len > st -> msize) {
			char	temp[128] = _PATH_VARTMP "bavwrap.XXXXXX";
			
			if ((st -> fd = mkstemp (temp)) != -1) {
				unlink (temp);
				if (((st -> buf -> length == 0) || (write (st -> fd, st -> buf -> buffer, st -> buf -> length) == st -> buf -> length)) &&
				    (write (st -> fd, buf, len) == len))
					rc = 0;
			}
		} else if (buffer_append (st -> buf, buf, len))
			rc = 0;
	if (rc == 0)
		st -> size += len;
	return rc;
}/*}}}*/
int
store_get (store_t *st, byte_t *buf, int room) /*{{{*/
{
	int	rc;
	
	if (st -> pos >= st -> size)
		rc = 0;
	else {
		int	n;

		rc = -1;
		if (st -> pos + room > st -> size)
			room = st -> size - st -> pos;
		if (st -> fd != -1) {
			if ((lseek (st -> fd, st -> pos, SEEK_SET) != -1) && ((n = read (st -> fd, buf, room)) > 0))
				rc = n;
		} else {
			memcpy (buf, st -> buf -> buffer + st -> pos, room);
			rc = room;
		}
	}
	if (rc > 0)
		st -> pos += rc;
	return rc;
}/*}}}*/
void
store_rewind (store_t *st) /*{{{*/
{
	st -> pos = 0;
	if (st -> fd != -1)
		lseek (st -> fd, 0, SEEK_SET);
}/*}}}*/
store_t *
store_alloc (int inmemsize) /*{{{*/
{
	store_t	*st;
	
	if (st = malloc (sizeof (store_t))) {
		st -> msize = inmemsize;
		st -> buf = NULL;
		st -> fd = -1;
		st -> size = 0;
		st -> pos = 0;
		if (! (st -> buf = buffer_alloc (inmemsize + 4)))
			st = store_free (st);
	}
	return st;
}/*}}}*/
store_t *
store_free (store_t *st) /*{{{*/
{
	if (st) {
		if (st -> buf)
			buffer_free (st -> buf);
		if (st -> fd != -1)
			close (st -> fd);
		free (st);
	}
	return NULL;
}/*}}}*/
