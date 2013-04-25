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
# include	<fcntl.h>
# include	<string.h>
# include	<errno.h>
# include	<sys/stat.h>
# include	"agn.h"

lock_t *
lock_alloc (const char *fname) /*{{{*/
{
	lock_t	*l;
	
	if (l = (lock_t *) malloc (sizeof (lock_t)))
		if (l ->fname = strdup (fname)) {
			l -> islocked = false;
		} else {
			free (l);
			l = NULL;
		}
	return l;
}/*}}}*/
lock_t *
lock_free (lock_t *l) /*{{{*/
{
	if (l) {
		if (l -> islocked)
			lock_unlock (l);
		free (l -> fname);
		free (l);
	}
	return NULL;
}/*}}}*/
bool_t
lock_lock (lock_t *l) /*{{{*/
{
	if (! l -> islocked) {
		int	omask;
		int	state;
		int	fd;
		char	scratch[32];
		int	slen;
				
		
		omask = umask (0133);
		for (state = 0; state < 2; ++state)
			if ((fd = open (l -> fname, O_CREAT | O_EXCL | O_WRONLY, 0644)) != -1) {
				slen = sprintf (scratch, "%10d\n", getpid ());
				if (write (fd, scratch, slen) == slen)
					l -> islocked = true;
				close (fd);
				break;
			} else if ((! state) && ((fd = open (l -> fname, O_RDONLY)) != -1)) {
				slen = read (fd, scratch, sizeof (scratch) - 1);
				close (fd);
				if (slen > 0) {
					int	pid;

					scratch[slen] = '\0';
					pid = atoi (scratch);
					if ((pid > 0) && (kill (pid, 0) == -1) && (errno == ESRCH))
						unlink (l -> fname);
				}
			}
		umask (omask);
	}
	return l -> islocked;
}/*}}}*/
void
lock_unlock (lock_t *l) /*{{{*/
{
	if (l -> islocked) {
		unlink (l -> fname);
		l -> islocked = false;
	}
}/*}}}*/
