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
# include	<fcntl.h>
# include	<string.h>
# include	<errno.h>
# include	<sys/types.h>
# include	<sys/stat.h>
# include	"agn.h"

daemon_t *
daemon_alloc (const char *prog, bool_t background, bool_t detach) /*{{{*/
{
	daemon_t	*d;
	
	if (d = (daemon_t *) malloc (sizeof (daemon_t))) {
		d -> background = background;
		d -> detach = detach;
		d -> pid = getpid ();
		d -> pidfile = NULL;
		d -> pfvalid = false;
		if (prog) {
			const char	*ptr;
			
			if (ptr = strrchr (prog, '/'))
				++ptr;
			else
				ptr = prog;
			if (d -> pidfile = malloc (sizeof (_PATH_VARRUN) + strlen (ptr)))
				sprintf (d -> pidfile, _PATH_VARRUN "%s", ptr);
			else
				d = daemon_free (d);
		}
	}
	return d;
}/*}}}*/
daemon_t *
daemon_free (daemon_t *d) /*{{{*/
{
	if (d) {
		daemon_done (d);
		if (d -> pidfile)
			free (d -> pidfile);
		free (d);
	}
	return NULL;
}/*}}}*/
void
daemon_done (daemon_t *d) /*{{{*/
{
	if (d -> pfvalid) {
		unlink (d -> pidfile);
		d -> pfvalid = false;
	}
}/*}}}*/
bool_t
daemon_lstart (daemon_t *d, log_t *l, logmask_t lmask, const char *lwhat) /*{{{*/
{
	if (d -> background) {
		pid_t	pid;
		mode_t	omask;
		int	n;
		int	fd;
		char	buf[32];
		int	blen;
		
		if ((pid = fork ()) == -1) {
			if (l)
				log_out (l, lmask, lwhat, "Unable to fork (%d, %m)", errno);
			return false;
		} else if (pid > 0)
			exit (0);
		d -> pid = getpid ();
		if (d -> pidfile) {
			omask = umask (0);
			for (n = 0; n < 2; ++n) {
				fd = open (d -> pidfile, O_WRONLY | O_CREAT | O_EXCL, 0644);
				if ((fd == -1) && (! n)) {
					if ((fd = open (d -> pidfile, O_RDONLY)) != -1) {
						blen = read (fd, buf, sizeof (buf) - 1);
						close (fd);
						if (blen > 0) {
							buf[blen] = '\0';
							pid = (pid_t) atol (buf);
							if ((pid > 0) && (kill (pid, 0) == -1) && (errno == ESRCH))
								unlink (d -> pidfile);
						}
						fd = -1;
					}
				}
			}
			umask (omask);
			if (fd == -1) {
				if (l)
					log_out (l, lmask, lwhat, "Unable to create pidfile %s, maybe an instance is already running?", d -> pidfile);
				return false;
			}
			d -> pfvalid = true;
			blen = sprintf (buf, "%10ld\n", (long) d -> pid);
			if (write (fd, buf, blen) != blen)
				d -> pfvalid = false;
			close (fd);
			if (! d -> pfvalid) {
				unlink (d -> pidfile);
				if (l)
					log_out (l, lmask, lwhat, "Failed to write pidfile %s, maybe disk is full?", d -> pidfile);
				return false;
			}
		}
	}
	if (d -> detach) {
		int	n;
		int	fd;

		umask (0);
		for (n = 0; n >= 0; ++n)
			if ((close (n) == -1) && (errno == EBADF))
				break;
		if (setsid () == -1) {
			if (l)
				log_out (l, lmask, lwhat, "Unable to set session (%d, %m)", errno);
			return false;
		}
		if (((fd = open (_PATH_DEVNULL, O_RDWR)) != 0) ||
		    (dup (fd) != 1) ||
		    (dup (fd) != 2)) {
			if (l)
				log_out (l, lmask, lwhat, "Unable to open %s propperly (%d, %m)", _PATH_DEVNULL, errno);
			return false;
		}
	}
	return true;
}/*}}}*/
bool_t
daemon_start (daemon_t *d, log_t *l) /*{{{*/
{
	char	what[64];
	
	sprintf (what, "DAEMON[%c/%c]",
		 (d -> background ? 'b' : 'f'),
		 (d -> detach ? 'd' : 'a'));
	return daemon_lstart (d, l, 0, what);
}/*}}}*/
bool_t
daemon_sstart (daemon_t *d) /*{{{*/
{
	return daemon_lstart (d, NULL, 0, NULL);
}/*}}}*/
