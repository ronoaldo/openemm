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
# include	<stdarg.h>
# include	<ctype.h>
# include	<unistd.h>
# include	<fcntl.h>
# include	<errno.h>
# include	<string.h>
# include	<time.h>
# include	<limits.h>
# ifndef	WIN32
# include	<dirent.h>
# endif		/* WIN32 */
# include	<syslog.h>
# include	"xmlback.h"

typedef struct sendmail	sendmail_t;
typedef struct { /*{{{*/
	bool_t		istemp;		/* temp. filenames		*/
	bool_t		tosyslog;	/* output accounting info	*/
	char		*acclog;	/* optional accounting log	*/
	char		*bnclog;	/* optional bounce log		*/
	sendmail_t	*s;		/* output generating for mails	*/
	/*}}}*/
}	gen_t;

static bool_t
boolean (const char *str) /*{{{*/
{
	return ((! str) || atob (str)) ? true : false;
}/*}}}*/
static bool_t
write_file (const char *fname, const buffer_t *content, const char *nl, int nllen) /*{{{*/
{
	bool_t	st;
	int	fd;
	
	st = false;
	if ((fd = open (fname, O_WRONLY | O_CREAT | O_TRUNC, 0644)) != -1) {
		st = true;
		if (content -> length > 0) {
			byte_t	*ptr;
			long	len;
			int	n;
			
			ptr = content -> buffer;
			len = content -> length;
			if (nl) {
				int	nlen;
				
				while (len > 0) {
					for (nlen = 0; nlen < len; ++nlen)
						if ((ptr[nlen] == '\r') || (ptr[nlen] == '\n'))
							break;
					if (nlen > 0) {
						if (write (fd, ptr, nlen) == nlen) {
							ptr += nlen;
							len -= nlen;
						} else {
							st = false;
							break;
						}
					}
					if (len > 0) {
						if ((len > 1) && (ptr[0] == '\r') && (ptr[1] == '\n')) {
							ptr += 2;
							len -= 2;
						} else if ((ptr[0] == '\n') || (ptr[0] == '\r')) {
							ptr += 1;
							len -= 1;
						}
						if (write (fd, nl, nllen) != nllen) {
							st = false;
							break;
						}
					}
				}
			} else {
				while (len > 0)
					if ((n = write (fd, ptr, len)) > 0) {
						ptr += n;
						len -= n;
					} else {
						st = false;
						break;
					}
			}
		}
		if (close (fd) == -1)
			st = false;
	}
	return st;
}/*}}}*/

typedef struct { /*{{{*/
	char	*dir;		/* the spool directory			*/
	char	*buf;		/* buffer for creating files		*/
	char	*ptr;		/* pointer to start of filepart		*/
	char	*dptr;		/* pointer to start of vairant dir	*/
	char	*fptr;		/* pointer to start of variant part	*/
	char	*temp;		/* temp.file in directory		*/
	bool_t	devnull;	/* if we want to write to /dev/null	*/
	/*}}}*/
}	spool_t;
static spool_t *
spool_free (spool_t *s) /*{{{*/
{
	if (s) {
		if (s -> dir)
			free (s -> dir);
		if (s -> buf)
			free (s -> buf);
		if (s -> temp) {
			if (s -> temp[0])
				unlink (s -> temp);
			free (s -> temp);
		}
		free (s);
	}
	return NULL;
}/*}}}*/
static spool_t *
spool_alloc (const char *dir, bool_t subdirs) /*{{{*/
{
	spool_t	*s;
	
	if (s = (spool_t *) malloc (sizeof (spool_t))) {
		if (! strcmp (dir, "/dev/null")) {
			s -> dir = NULL;
			s -> buf = NULL;
			s -> ptr = NULL;
			s -> dptr = NULL;
			s -> fptr = NULL;
			s -> temp = NULL;
			s -> devnull = true;
		} else {
			int	dlen = strlen (dir);

			s -> dir = strdup (dir);
			s -> buf = malloc (dlen + 512);
			s -> temp = malloc (dlen + 348);
			if (s -> temp)
				s -> temp[0] = '\0';
			if (s -> dir && s -> buf && s -> temp) {
				strcpy (s -> buf, dir);
				s -> ptr = s -> buf + dlen;
				if (subdirs) {
# ifdef		WIN32
					*(s -> ptr)++ = '\\';
# else		/* WIN32 */
					*(s -> ptr)++ = '/';
# endif		/* WIN32 */
					s -> dptr = s -> ptr;
					*(s -> ptr)++ = 'q';
					*(s -> ptr)++ = 'f';
				} else
					s -> dptr = NULL;
# ifdef		WIN32
				*(s -> ptr)++ = '\\';
				sprintf (s -> temp, "%s\\.xmlgen.%06d", dir, (int) getpid ());
# else		/* WIN32 */
				*(s -> ptr)++ = '/';
				sprintf (s -> temp, "%s/.xmlgen.%06d", dir, (int) getpid ());
# endif		/* WIN32 */
				s -> fptr = s -> ptr;
				s -> devnull = false;
			} else
				s = spool_free (s);
		}
	}
	return s;
}/*}}}*/
static void
spool_setprefix (spool_t *s, const char *prefix) /*{{{*/
{
	if (! s -> devnull) {
		s -> fptr = s -> ptr;
		strcpy (s -> fptr, prefix);
		while (*(s -> fptr))
			s -> fptr++;
	}
}/*}}}*/
static void
spool_addprefix (spool_t *s, const char *prefix) /*{{{*/
{
	if (! s -> devnull) {
		strcpy (s -> fptr, prefix);
		while (*(s -> fptr))
			s -> fptr++;
	}
}/*}}}*/
static void
spool_tmpprefix (spool_t *s) /*{{{*/
{
	if (! s -> devnull) {
		char	prefix[64];
	
		sprintf (prefix, "%lxT%04lx", (long) getpid (), ((unsigned long) time (NULL) >> 6) & 0xffff);
		spool_addprefix (s, prefix);
	}
}/*}}}*/
static bool_t
spool_write (spool_t *s, buffer_t *content, const char *nl, int nllen) /*{{{*/
{
	return s -> devnull ? true : write_file (s -> buf, content, nl, nllen);
}/*}}}*/
static bool_t
spool_write_temp (spool_t *s, buffer_t *content, const char *nl, int nllen) /*{{{*/
{
	return s -> devnull ? true : write_file (s -> temp, content, nl, nllen);
}/*}}}*/
static bool_t
spool_validate (spool_t *s) /*{{{*/
{
	if (! s -> devnull)
		if (rename (s -> temp, s -> buf) == -1)
			return false;
	return true;
}/*}}}*/

# define	DEF_DESTDIR		"."

struct sendmail { /*{{{*/
	spool_t	*spool;			/* spool directory		*/
	long	nr;			/* an incremental counter	*/
	/*}}}*/
};

static sendmail_t *
sendmail_alloc (void) /*{{{*/
{
	sendmail_t	*s;
	
	if (s = (sendmail_t *) malloc (sizeof (sendmail_t))) {
		s -> spool = NULL;
		s -> nr = 0;
	}
	return s;
}/*}}}*/
static sendmail_t *
sendmail_free (sendmail_t *s) /*{{{*/
{
	if (s) {
		if (s -> spool)
			spool_free (s -> spool);
		free (s);
	}
	return NULL;
}/*}}}*/

static bool_t
sendmail_oinit (sendmail_t *s, blockmail_t *blockmail, var_t *opt) /*{{{*/
{
	bool_t	st;

	st = true;
	if (var_partial_imatch (opt, "path")) {
# ifndef	WIN32		
# define	SD_NONE		0
# define	SD_QF		(1 << 0)
# define	SD_DF		(1 << 1)
# define	SD_XF		(1 << 2)
# define	SD_ALL		(SD_QF | SD_DF | SD_XF)		
		int		subdirs;
		DIR		*dp;
		struct dirent	*ent;
		
		if (s -> spool)
			spool_free (s -> spool);
		subdirs = SD_NONE;
		if (dp = opendir (opt -> val)) {
			while ((ent = readdir (dp)) && (subdirs != SD_ALL))
				if ((! (subdirs & SD_QF)) && (! strcmp (ent -> d_name, "qf")))
					subdirs |= SD_QF;
				else if ((! (subdirs & SD_DF)) && (! strcmp (ent -> d_name, "df")))
					subdirs |= SD_DF;
				else if ((! (subdirs & SD_XF)) && (! strcmp (ent -> d_name, "xf")))
					subdirs |= SD_XF;
			closedir (dp);
		}
		if (! (s -> spool = spool_alloc (opt -> val, (subdirs == SD_ALL ? true : false))))
			st = false;
# undef		SD_NONE
# undef		SD_QF
# undef		SD_DF
# undef		SD_XF
# undef		SD_ALL
# else		/* WIN32 */
		if (! (s -> spool = spool_alloc (opt -> val, false)))
			st = false;
# endif		/* WIN32 */
	} else
		st = false;
	return st;
}/*}}}*/
static bool_t
sendmail_osanity (sendmail_t *s, blockmail_t *blockmail) /*{{{*/
{
	bool_t	st;
	
	st = true;
	if (! s -> spool)
		if (! (s -> spool = spool_alloc (DEF_DESTDIR, false)))
			st = false;
	if (st)
		spool_setprefix (s -> spool, "?f");
	return st;
}/*}}}*/
static bool_t
sendmail_odeinit (sendmail_t *s, gen_t *g, blockmail_t *blockmail, bool_t success) /*{{{*/
{
	return true;
}/*}}}*/
static bool_t
sendmail_owrite (sendmail_t *s, gen_t *g, blockmail_t *blockmail, receiver_t *rec) /*{{{*/
{
	bool_t	st;
	spool_t	*spool;
	
	if (s -> nr == 0) {
		if (g -> istemp) {
			spool_tmpprefix (s -> spool);
		} else {
			char	prefix[64];

			sprintf (prefix, "%06X000", blockmail -> mailing_id);
			spool_addprefix (s -> spool, prefix);
		}
	}
	s -> nr++;
	st = false;
	spool = s -> spool;
	if (! spool -> devnull) {
		const char	*nl;
		int		nllen;
		
		if (blockmail -> usecrlf) {
			nl = NULL;
			nllen = 0;
		} else {
			nl = "\n";
			nllen = 1;
		}
		if (g -> istemp)
			sprintf (spool -> fptr, "%08lx", (unsigned long) s -> nr);
		else if (rec -> customer_id == 0)
			sprintf (spool -> fptr, "F%07lx", (unsigned long) s -> nr);
		else
			sprintf (spool -> fptr, "%08X", rec -> customer_id);
		if (spool -> dptr)
			spool -> dptr[0] = 'd';
		spool -> ptr[0] = 'd';
		if (! spool_write (spool, blockmail -> body, nl, nllen))
			log_out (blockmail -> lg, LV_ERROR, "Unable to write data file %s (%m)", spool -> ptr);
		else if (! spool_write_temp (spool, blockmail -> head, nl, nllen))
			log_out (blockmail -> lg, LV_ERROR, "Unable to write control file %s (%m)", spool -> temp);
		else {
			if (spool -> dptr)
				spool -> dptr[0] = 'q';
			spool -> ptr[0] = 'q';
			if (! spool_validate (spool)) {
				log_out (blockmail -> lg, LV_WARNING, "Unable to rename temp.file %s to %s (%m), try old fashion link/unlink", spool -> temp, spool -> ptr);
				st = false;
			} else
				st = true;
		}
	} else
		st = true;
	return st;
}/*}}}*/


void *
generate_oinit (blockmail_t *blockmail, var_t *opts) /*{{{*/
{
	gen_t	*g;
	
	if (g = (gen_t *) malloc (sizeof (gen_t))) {
		g -> istemp = false;
		g -> tosyslog = false;
# ifdef		WIN32
		g -> acclog = strdup ("var\\spool\\log\\account.log");
		g -> bnclog = strdup ("var\\spool\\log\\extbounce.log");
# else		/* WIN32 */
		g -> acclog = NULL;
		g -> bnclog = NULL;
# endif		/* WIN32 */		
		g -> s = sendmail_alloc ();
		if (g -> s)
		{
			bool_t	st = true;
			char	media = '\0';
			var_t	*tmp;
			
			for (tmp = opts; st && tmp; tmp = tmp -> next)
				if ((! tmp -> var) || var_partial_imatch (tmp, "media")) {
					if (! strcasecmp (tmp -> val, "email"))
						media = 's';
					else {
						log_out (blockmail -> lg, LV_ERROR, "Unknown media %s", tmp -> val);
						st = false;
					}
				} else if (var_partial_imatch (tmp, "temporary")) {
					g -> istemp = boolean (tmp -> val);
				} else if (var_partial_imatch (tmp, "syslog")) {
					g -> tosyslog = boolean (tmp -> val);
				} else if (var_partial_imatch (tmp, "account-logfile")) {
					st = struse (& g -> acclog, tmp -> val);
				} else if (var_partial_imatch (tmp, "bounce-logfile")) {
					st = struse (& g -> bnclog, tmp -> val);
				} else {
					switch (media) {
					default:
						log_out (blockmail -> lg, LV_ERROR, "Unknown option %s and no media type enabled", tmp -> var);
						st = false;
						break;
					case 's':
						st = sendmail_oinit (g -> s, blockmail, tmp);
						break;
					}
				}
			if ((! st) ||
			    (! sendmail_osanity (g -> s, blockmail))
			    ) {
				generate_odeinit (g, blockmail, false);
				g = NULL;
			}
		} else {
			generate_odeinit (g, blockmail, false);
			g = NULL;
		}
	}
	return g;
}/*}}}*/
bool_t
generate_odeinit (void *data, blockmail_t *blockmail, bool_t success) /*{{{*/
{
	gen_t	*g = (gen_t *) data;
	bool_t	st = true;
	
	if (g) {
		if ((g -> s && (! sendmail_odeinit (g -> s, g, blockmail, success)))
		    )
			st = false;
		if (st && success && blockmail -> counter) {
			counter_t	*crun;
			int		fd;
			int		len;
			char		ts[64];
			char		scratch[4096];
			
			if (g -> acclog)  {
				time_t		now;
				struct tm	*tt;
					
				time (& now);
				if (tt = localtime (& now))
					snprintf (ts, sizeof (ts), "%04d-%02d-%02d:%02d:%02d:%02d",
						  tt -> tm_year + 1900, tt -> tm_mon + 1, tt -> tm_mday,
						  tt -> tm_hour, tt -> tm_min, tt -> tm_sec);
				else
					ts[0] = '\0';
			}
			if (g -> tosyslog)
				openlog ("xmlback", LOG_PID, LOG_MAIL);
			log_suspend_push (blockmail -> lg, ~LS_LOGFILE, false);
			for (crun = blockmail -> counter; crun; crun = crun -> next) {
				if (! crun -> unitcount)
					continue;

# define	FORMAT(s1,s2)	s1 "0;%d;%d;%d;%d;%c;%d;%s;%d;%ld;%lld" s2,			\
				blockmail -> company_id, blockmail -> mailinglist_id,		\
				blockmail -> mailing_id, blockmail -> maildrop_status_id,	\
				blockmail -> status_field, blockmail -> blocknr, 		\
				crun -> mediatype, crun -> subtype, 				\
				crun -> unitcount, crun -> bytecount
# define	WHAT		FORMAT ("mail creation: ", "")

				log_out (blockmail -> lg, LV_NOTICE, WHAT);
				if (g -> tosyslog)
					syslog (LOG_NOTICE, WHAT);
				if (g -> acclog) {
					len = snprintf (scratch, sizeof (scratch) - 1, "company=%d\tmailinglist=%d\tmailing=%d\tmaildrop=%d\tstatus_field=%c\tblock=%d\tmediatype=%s\tsubtype=%d\tcount=%ld\tbytes=%lld\tmailer=localhost\ttimestamp=%s\n",
							blockmail -> company_id, blockmail -> mailinglist_id,
							blockmail -> mailing_id, blockmail -> maildrop_status_id,
							blockmail -> status_field, blockmail -> blocknr,
							crun -> mediatype, crun -> subtype,
							crun -> unitcount, crun -> bytecount, ts);
					if ((fd = open (g -> acclog, O_WRONLY | O_APPEND | O_CREAT, 0644)) == -1) {
						log_out (blockmail -> lg, LV_ERROR, "Unable to open separate accounting logfile %s (%m)", g -> acclog);
						free (g -> acclog);
						g -> acclog = NULL;
					} else {
						if (write (fd, scratch, len) != len)
							log_out (blockmail -> lg, LV_ERROR, "Unable to write to separate accounting logfile %s (%m)", g -> acclog);
						if (close (fd) == -1)
							log_out (blockmail -> lg, LV_ERROR, "Failed to close separate accounting logfile %s (%m)", g -> acclog);
					}
				}
# undef		WHAT
# undef		FORMAT
			}
			log_suspend_pop (blockmail -> lg);
			if (g -> tosyslog)
				closelog ();
		}
		if (g -> acclog)
			free (g -> acclog);
		if (g -> bnclog)
			free (g -> bnclog);
		if (g -> s)
			sendmail_free (g -> s);
		free (g);
	}
	return st;
}/*}}}*/
bool_t
generate_owrite (void *data, blockmail_t *blockmail, receiver_t *rec) /*{{{*/
{
	gen_t	*g = (gen_t *) data;
	bool_t	st;

	if ((! rec -> media) || (rec -> media -> type == MT_EMail))
		st = sendmail_owrite (g -> s, g, blockmail, rec);
	else
		st = false;
	return st;
}/*}}}*/
