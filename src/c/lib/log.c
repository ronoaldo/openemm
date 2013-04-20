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
/** @file log.c
 * Logging handling.
 * This modules handles all logging using different loglevels and
 * output modules
 */
# include	<stdio.h>
# include	<stdlib.h>
# include	<stdarg.h>
# include	<ctype.h>
# include	<unistd.h>
# include	<string.h>
# include	<time.h>
# include	<sys/types.h>
# include	<sys/stat.h>
# include	<syslog.h>
# include	"agn.h"

/** Stack for IDs.
 * This stack is used to keep track of logging IDs.
 */
typedef struct idc { /*{{{*/
	char		*str;	/**< concated ID string			*/
	struct idc	*next;	/**< next element in stack		*/
	/*}}}*/
}	idc_t;
/** Alloced ID.
 * @param prefix the already stacked IDs
 * @param str the new ID
 * @param separator
 * @return the new head of the stack on success, NULL otherwise
 */
static idc_t *
idc_alloc (idc_t *prefix, const char *str, const char *separator) /*{{{*/
{
	idc_t	*i;
	
	if (i = (idc_t *) malloc (sizeof (idc_t))) {
		if (prefix) {
			if (i -> str = malloc (strlen (prefix -> str) + strlen (str) + (separator ? strlen (separator) : 0) + 1))
				sprintf (i -> str, "%s%s%s", prefix -> str, (separator ? separator : ""), str);
		} else
			i -> str = strdup (str);
		if (i -> str)
			i -> next = NULL;
		else {
			free (i);
			i = NULL;
		}
	}
	return i;
}/*}}}*/
/** Frees an ID.
 * @param i the ID to free
 * @return NULL
 */
static idc_t *
idc_free (idc_t *i) /*{{{*/
{
	if (i) {
		if (i -> str)
			free (i -> str);
		free (i);
	}
	return NULL;
}/*}}}*/
/** Frees stack.
 * @param i the ID to start from
 * @return NULL
 */
static idc_t *
idc_free_all (idc_t *i) /*{{{*/
{
	idc_t	*tmp;
	
	while (tmp = i) {
		i = i -> next;
		idc_free (tmp);
	}
	return NULL;
}/*}}}*/

/** Stack for suspends.
 * On suspend requests, theses are stored in a stack
 */
typedef struct suspend { /*{{{*/
	unsigned long	mask;	/**< the new mask to suspend		*/
	struct suspend	*next;	/**< next element in stack		*/
	/*}}}*/
}	suspend_t;
/** Alloc a suspend.
 * @param mask the mask for this suspend instance
 * @return new instance on success, NULL otherwise
 */
static suspend_t *
suspend_alloc (unsigned long mask) /*{{{*/
{
	suspend_t	*s;
	
	if (s = (suspend_t *) malloc (sizeof (suspend_t))) {
		s -> mask = mask;
		s -> next = NULL;
	}
	return s;
}/*}}}*/
/** Frees a suspend.
 * @param s the instance to be freed
 * @return NULL
 */
static suspend_t *
suspend_free (suspend_t *s) /*{{{*/
{
	if (s) {
		free (s);
	}
	return NULL;
}/*}}}*/
/** Frees whole stack.
 * @param s the instance to start freeing
 * @return NULL
 */
static suspend_t *
suspend_free_all (suspend_t *s) /*{{{*/
{
	suspend_t	*tmp;
	
	while (tmp = s) {
		s = s -> next;
		suspend_free (tmp);
	}
	return NULL;
}/*}}}*/
/** Loglevel table.
 * Table to match textual loglevels to their values
 */
static struct { /*{{{*/
	const char	*name;	/**< name of logtab entry		*/
	int		nlen;	/**< length of name			*/
	int		level;	/**< the loglevel for this name		*/
	/*}}}*/
}	logtab[] = { /*{{{*/
# define	MKTAB(sss,lll)		{ sss, sizeof (sss) - 1, lll }
	MKTAB ("NONE", LV_NONE),
	MKTAB ("FATAL", LV_FATAL),
	MKTAB ("ERROR", LV_ERROR),
	MKTAB ("WARNING", LV_WARNING),
	MKTAB ("NOTICE", LV_NOTICE),
	MKTAB ("INFO", LV_INFO),
	MKTAB ("VERBOSE", LV_VERBOSE),
	MKTAB ("DEBUG", LV_DEBUG)
# undef		MKTAB	
	/*}}}*/
};

/** Returns name for loglevel.
 * Finds and returns the string representation for a loglevel
 * @param lvl the numeric loglevel
 * @return the string, if lvl had been in valid range, NULL otherwise
 */
const char *
log_level_name (int lvl) /*{{{*/
{
	if ((lvl >= 0) && (lvl < sizeof (logtab) / sizeof (logtab[0])))
		return logtab[lvl].name;
	return NULL;
}/*}}}*/
int
log_level (const char *levelname) /*{{{*/
{
	int	level;
	int	llen;
	int	n;
	
	level = -1;
	if (levelname) {
		llen = strlen (levelname);
		for (n = 0; n < sizeof (logtab) / sizeof (logtab[0]); ++n)
			if ((llen <= logtab[n].nlen) && (! strncasecmp (levelname, logtab[n].name, llen))) {
				level = logtab[n].level;
				break;
			}
	}
	return level;
}/*}}}*/
/** Allocate a logger.
 * Create a new instance for a logging interface
 * @param logpath optional path to logfile
 * @param program name of the program, used to build logfile filename
 * @param levelname optional loglevel to use
 * @return new instance on success, NULL otherwise
 */
log_t *
log_alloc (const char *logpath, const char *program, const char *levelname) /*{{{*/
{
	log_t	*l;
	char	*ptr;
	bool_t	st;

	if (l = (log_t *) malloc (sizeof (log_t))) {
		l -> logfd = -1;
		l -> slprio = -1;
		l -> logpath = NULL;
		l -> program = NULL;
		gethostname (l -> hostname, sizeof (l -> hostname) - 1);
		l -> hostname[sizeof (l -> hostname) - 1] = '\0';
		if (ptr = strchr (l -> hostname, '.'))
			*ptr = '\0';
		l -> fname[0] = '\0';
		l -> level = LV_ERROR;
		l -> use = 0;
		l -> last = 0;
		l -> lastday = -1;
		l -> diff = 0;
		l -> lfp = NULL;
		l -> idc = NULL;
		l -> slactive = false;
		l -> obuf = NULL;
		l -> collect = NULL;
		l -> clevel = LV_DEBUG;
		l -> suspend = NULL;
		if (levelname)
			st = log_level_set (l, levelname);
		else
			st = true;
		if (st) {
# ifdef		WIN32
			st = (logpath && *logpath) ? log_path_set (l, logpath) : log_path_default (l);
# else		/* WIN32 */
			st = (logpath && (*logpath == '/')) ? log_path_set (l, logpath) : log_path_default (l);
# endif		/* WIN32 */
		}
		if (st && program) {
			const char	*cptr;
			
			if (cptr = strrchr (program, PATH_SEP))
				++cptr;
			else
				cptr = program;
			if (! (l -> program = strdup (cptr)))
				st = false;
		}
		if (! st)
			l = log_free (l);
	}
	return l;
}/*}}}*/
/** Frees logger.
 * Returns all allocated resources to the system
 * @param l the logger
 * @return NULL
 */
log_t *
log_free (log_t *l) /*{{{*/
{
	if (l) {
		if (l -> slactive)
			closelog ();
		if (l -> logpath)
			free (l -> logpath);
		if (l -> program)
			free (l -> program);
		if (l -> lfp)
			fclose (l -> lfp);
		if (l -> idc)
			idc_free_all ((idc_t *) l -> idc);
		if (l -> obuf)
			buffer_free (l -> obuf);
		if (l -> collect)
			buffer_free (l -> collect);
		if (l -> suspend)
			suspend_free_all ((suspend_t *) l -> suspend);
		free (l);
	}
	return NULL;
}/*}}}*/
/** Sets loglevel.
 * The loglevel is set using a textual levelname
 * @param l the logger
 * @param levelname the new loglevel to use
 * @return true if levelname had been valid, false otherwise
 */
bool_t
log_level_set (log_t *l, const char *levelname) /*{{{*/
{
	int	nlevel;
	bool_t	rc;
	
	if ((nlevel = log_level (levelname)) != -1) {
		l -> level = nlevel;
		rc = true;
	} else
		rc = false;
	return rc;
}/*}}}*/
/** Set logmask.
 * Sets the logging mask to new value
 * @param l the logger
 * @param mask the new mask
 */
void
log_set_mask (log_t *l, logmask_t mask) /*{{{*/
{
	l -> use = mask;
}/*}}}*/
/** Clears logmask.
 * @param l the logger
 */
void
log_clr_mask (log_t *l) /*{{{*/
{
	l -> use = LM_NONE;
}/*}}}*/
/** Add to logmask.
 * The logmask is modified by adding the given mask
 * @param l the logger
 * @param mask the mask to add
 */
void
log_add_mask (log_t *l, logmask_t mask) /*{{{*/
{
	l -> use |= mask;
}/*}}}*/
/** Delete from logmask.
 * The logmask is modified by delete the given mask
 * @param l the logger
 * @param mask the mask to delete
 */
void
log_del_mask (log_t *l, logmask_t mask) /*{{{*/
{
	l -> use &= ~mask;
}/*}}}*/
/** Set the logging path.
 * Sets the path for the directory where the logfiles should
 * be written to
 * @param l the logger
 * @param logpath the new path
 * @return true on success, false otherwise
 */
bool_t
log_path_set (log_t *l, const char *logpath) /*{{{*/
{
	l -> last = 0;
	l -> lastday = 0;
	if (l -> logpath)
		free (l -> logpath);
	l -> logpath = logpath ? strdup (logpath) : NULL;
	return (logpath && (! l -> logpath)) ? false : true;
}/*}}}*/
/** Sets default logging path.
 * Sets the default logging path creating from enviroment variable
 * @param l the logger
 * @return true on success, false otherwise
 */
bool_t
log_path_default (log_t *l) /*{{{*/
{
	const char	*path;
	char		scratch[PATH_MAX + 1];

	if (! (path = getenv ("LOG_HOME"))) {
		if (path = getenv ("HOME"))
			snprintf (scratch, sizeof (scratch) - 1, "%s%cvar%clog", path, PATH_SEP, PATH_SEP);
		else
			snprintf (scratch, sizeof (scratch) - 1, "var%clog", PATH_SEP);
		path = scratch;
	}
	return log_path_set (l, path);
}/*}}}*/
/** Sets filedesc to write output to.
 * @param l the logger
 * @param fd the file descriptor to write to
 */
void
log_tofd (log_t *l, int fd) /*{{{*/
{
	l -> logfd = fd;
}/*}}}*/
/** Resets output filedesc.
 * @param l the logger
 */
void
log_nofd (log_t *l) /*{{{*/
{
	l -> logfd = -1;
}/*}}}*/
/** Setup syslog.
 * Sets all required paramter to write output to syslog
 * @param l the logger
 * @param ident directly passed to <b>openlog(3)</b>
 * @param option directly passed to <b>openlog(3)</b>
 * @param facility directly passed to <b>openlog(3)</b>
 * @param priority will be passed to each call of <b>syslog(3)</b>
 */
void
log_tosyslog (log_t *l, const char *ident, int option, int facility, int priority) /*{{{*/
{
	if (l -> slactive)
		log_nosyslog (l);
	if (! ident)
		ident = l -> program;
	if (option == -1)
		option = LOG_PID;
	if (facility == -1)
		facility = LOG_LOCAL0;
	openlog (ident, option, facility);
	l -> slprio = priority;
	l -> slactive = true;
}/*}}}*/
/** Disable syslog.
 * @param l the logger
 */
void
log_nosyslog (log_t *l) /*{{{*/
{
	if (l -> slactive) {
		closelog ();
		l -> slactive = false;
	}
}/*}}}*/
/** Collect output.
 * Start collecting the output of logging in an in memory buffer
 * @param l the logger
 * @return true on success, false otherwise
 */
bool_t
log_collect (log_t *l, int level) /*{{{*/
{
	if (l -> collect)
		buffer_clear (l -> collect);
	else
		l -> collect = buffer_alloc (2048);
	if (level == -1)
		l -> clevel = LV_DEBUG;
	else
		l -> clevel = level;
	return l -> collect ? true : false;
}/*}}}*/
/** Disable collecting.
 * @param l the logger
 */
void
log_uncollect (log_t *l) /*{{{*/
{
	if (l -> collect)
		l -> collect = buffer_free (l -> collect);
}/*}}}*/
/** Set logging ID.
 * Sets the current logging ID to what, removing all pending IDs
 * @param l the logger
 * @param what the new ID
 * @return true on success, false otherwise
 */
bool_t
log_idset (log_t *l, const char *what) /*{{{*/
{
	log_idclr (l);
	l -> idc = idc_alloc (NULL, what, NULL);
	return l -> idc ? true : false;
}/*}}}*/
/** Clears logging IDs.
 * Removes all pending logging IDs.
 * @param l the logger
 */
void
log_idclr (log_t *l) /*{{{*/
{
	if (l -> idc)
		l -> idc = idc_free_all ((idc_t *) l -> idc);
}/*}}}*/
/** Push new logging ID.
 * The new logging id <i>what</i> is pushed on top of the ID stack.
 * If there is already one on the stack, the new ID is created using
 * the stack value, concaternated by separator and new ID
 * @param l the logger
 * @param what the new ID
 * @param separator the separator for concaternation
 * @return true on success, false otherwise
 */
bool_t
log_idpush (log_t *l, const char *what, const char *separator) /*{{{*/
{
	idc_t	*tmp;
	
	if (tmp = idc_alloc ((separator ? (idc_t *) l -> idc : NULL), what, separator)) {
		tmp -> next = (idc_t *) l -> idc;
		l -> idc = tmp;
	}
	return tmp ? true : false;
}/*}}}*/
/** Pops logging ID.
 * Removes the top element of the logging ID stack.
 * @param l the logger
 */
void
log_idpop (log_t *l) /*{{{*/
{
	idc_t	*tmp;
	
	if (tmp = (idc_t *) l -> idc) {
		l -> idc = tmp -> next;
		idc_free (tmp);
	}
}/*}}}*/
/** Pops suspend.
 * Removes the top elements of the suspend stack.
 * @param l the logger
 */
void
log_suspend_pop (log_t *l) /*{{{*/
{
	suspend_t	*s = (suspend_t *) l -> suspend;
	
	if (s) {
		l -> suspend = s -> next;
		suspend_free (s);
	}
}/*}}}*/
/** Push suspend.
 * Add a new suspend mask to the suspend stack. If <i>set</i> is true,
 * then the old suspend mask is not added
 * @param l the logger
 * @param mask the new mask
 * @param set if true, then the old mask is not added
 */
void
log_suspend_push (log_t *l, unsigned long mask, bool_t set) /*{{{*/
{
	suspend_t	*s = (suspend_t *) l -> suspend;
	suspend_t	*tmp;
	
	if (tmp = suspend_alloc (mask | (s && (! set) ? s -> mask : 0))) {
		tmp -> next = s;
		l -> suspend = tmp;
	}
}/*}}}*/
/** Checks suspend.
 * The current suspend stack is checked, if output for <i>what</i>
 * should be suspended at the moment.
 * @param l the logger
 * @param what the output method to check
 * @return true if output should be suspended, false otherwise
 */
bool_t
log_suspend (log_t *l, unsigned long what) /*{{{*/
{
	suspend_t	*s = (suspend_t *) l -> suspend;
	
	return (s && (s -> mask & what)) ? true : false;
}/*}}}*/
static bool_t
mkfname (log_t *l, time_t now) /*{{{*/
{
	long		day;
	struct tm	*tt;

	day = (now + l -> diff) / (24 * 60 * 60);
	if ((l -> fname[0] && (day != l -> lastday)) ||
	    (! l -> fname[0]) ||
	    (! l -> lfp)) {
		if (l -> lfp) {
			fclose (l -> lfp);
			l -> lfp = NULL;
		}
		l -> diff = tzdiff (now);
		l -> lastday = day;
		if (tt = localtime (& now)) {
			mode_t	omask;
			
			snprintf (l -> fname, sizeof (l -> fname) - 1,
				  "%s%c%04d%02d%02d-%s-%s.log",
				  (l -> logpath ? l -> logpath : "."), PATH_SEP,
				  tt -> tm_year + 1900,
				  tt -> tm_mon + 1,
				  tt -> tm_mday,
				  l -> hostname,
				  (l -> program ? l -> program : "unset"));
			omask = umask (0);
			umask (0133 | omask);
			l -> lfp = fopen (l -> fname, "a");
			umask (omask);
		}
	}
	l -> last = now;
	return l -> lfp ? true : false;
}/*}}}*/
/** Write to logfile.
 * Primary interface to write to logfile using printf like format
 * and varargs parameter passing
 * @param l the logger
 * @param level the loglevel of this message
 * @param mask loglevel mask
 * @param what identification for what the entry is
 * @param fmt printf like format
 * @param par format parameter
 * @return true if successful, false otherwise
 */
bool_t
log_vmout (log_t *l, int level, logmask_t mask, const char *what, const char *fmt, va_list par) /*{{{*/
{
	bool_t	st;
	
	if ((level <= l -> level) && LM_MATCH (mask, l -> use)) {
		time_t		now;
		struct tm	*tt;
		long		spos, epos;
		byte_t		*scratch;
		
		st = false;
		time (& now);
		if (((! log_suspend (l, LS_LOGFILE)) && mkfname (l, now)) ||
		    ((! log_suspend (l, LS_FILEDESC)) && (l -> logfd != -1)) ||
		    ((! log_suspend (l, LS_SYSLOG)) && l -> slactive && (l -> slprio != -1)) ||
		    ((! log_suspend (l, LS_COLLECT)) && l -> collect)) {
			if ((l -> obuf || (l -> obuf = buffer_alloc (512))) &&
			    (tt = localtime (& now))) {
				buffer_clear (l -> obuf);
				if (buffer_format (l -> obuf, "[%02d.%02d.%04d  %02d:%02d:%02d] %d ",
						   tt -> tm_mday, tt -> tm_mon + 1, tt -> tm_year + 1900,
						   tt -> tm_hour, tt -> tm_min, tt -> tm_sec, (int) getpid ()) &&
				    (((level >= 0) && (level < sizeof (logtab) / sizeof (logtab[0]))) ?
				     buffer_appends (l -> obuf, logtab[level].name) :
				     buffer_format (l -> obuf, "(%d)", level)) &&
				    (what ? buffer_format (l -> obuf, "/%s: ", what) : buffer_appends (l -> obuf, ": "))) {
					spos = l -> obuf -> length;
					if (buffer_vformat (l -> obuf, fmt, par)) {
						epos = l -> obuf -> length;
						if (buffer_appendch (l -> obuf, '\n')) {
							st = true;
							if ((! log_suspend (l, LS_LOGFILE)) && l -> lfp)
								if ((fwrite (l -> obuf -> buffer, sizeof (byte_t), l -> obuf -> length, l -> lfp) != l -> obuf -> length) ||
								    (fflush (l -> lfp) != EOF)) {
									fclose (l -> lfp);
									l -> lfp = NULL;
									st = false;
								}
							if ((! log_suspend (l, LS_FILEDESC)) && (l -> logfd != -1) && (spos < epos))
								if ((write (l -> logfd, l -> obuf -> buffer + spos, epos - spos) != epos - spos) ||
								    (write (l -> logfd, "\n", 1) != 1)) {
									/* no closing required, as passed from caller! */
									l -> logfd = -1;
									st = false;
								}
							if ((! log_suspend (l, LS_SYSLOG)) && l -> slactive && (l -> slprio != -1) && (spos < epos) &&
							    (scratch = buffer_cut (l -> obuf, spos, epos - spos, NULL))) {
								syslog (l -> slprio, "%s", scratch);
								free (scratch);
							}
							if ((! log_suspend (l, LS_COLLECT)) && (level <= l -> clevel) && l -> collect)
								buffer_appendbuf (l -> collect, l -> obuf);
						}
					}
				}
			}
		}
	} else
		st = true;
	return st;
}/*}}}*/
/** Write to logfile.
 * Same as <i>log_vmout</i> except that parameter are passed directly
 * @see log_vmout
 */
bool_t
log_mout (log_t *l, int level, logmask_t mask, const char *what, const char *fmt, ...) /*{{{*/
{
	va_list	par;
	bool_t	st;
	
	va_start (par, fmt);
	st = log_vmout (l, level, mask, what, fmt, par);
	va_end (par);
	return st;
}/*}}}*/
/** Write to logfile.
 * Same as <i>log_vmout</i> except ident is created from internal ID stack
 * @see log_vmout
 */
bool_t
log_vidout (log_t *l, int level, logmask_t mask, const char *fmt, va_list par) /*{{{*/
{
	return log_vmout (l, level, mask, (l -> idc ? ((idc_t *) l -> idc) -> str : NULL), fmt, par);
}/*}}}*/
/** Write to logfile.
 * Same as <i>log_vidout</i> except that parameter are passed directly
 * @see log_vidout
 */
bool_t
log_idout (log_t *l, int level, logmask_t mask, const char *fmt, ...) /*{{{*/
{
	va_list	par;
	bool_t	st;
	
	va_start (par, fmt);
	st = log_vidout (l, level, mask, fmt, par);
	va_end (par);
	return st;
}/*}}}*/
/** Write to logfile
 * Same as <i>log_vmout</i> except passing the priority for syslog is possible
 * @see log_vmout
 */
bool_t
log_slvout (log_t *l, int level, logmask_t mask, int priority, const char *what, const char *fmt, va_list par) /*{{{*/
{
	int	savprio;
	bool_t	savactive;
	bool_t	st;

	savprio = l -> slprio;
	savactive = l -> slactive;
	if (! savactive)
		log_tosyslog (l, NULL, -1, -1, priority);
	else
		l -> slprio = priority;
	st = log_vmout (l, level, mask, what, fmt, par);
	if (! savactive)
		log_nosyslog (l);
	l -> slprio = savprio;
	l -> slactive = savactive;
	return st;
}/*}}}*/
/** Write to logfile.
 * Same as <i>log_vslout</i> except that parameter are passed directly
 * @see log_vidout
 */
bool_t
log_slout (log_t *l, int level, logmask_t mask, int priority, const char *what, const char *fmt, ...) /*{{{*/
{
	va_list	par;
	bool_t	st;
	
	va_start (par, fmt);
	st = log_slvout (l, level, mask, priority, what, fmt, par);
	va_end (par);
	return st;
}/*}}}*/
/** Write to logfile.
 * Same as <i>log_vidout</i> except no logmask is required
 * @see log_vidout
 */
bool_t
log_vout (log_t *l, int level, const char *fmt, va_list par) /*{{{*/
{
	return log_vmout (l, level, 0, (l -> idc ? ((idc_t *) l -> idc) -> str : NULL), fmt, par);
}/*}}}*/
/** Write to logfile.
 * Same as <i>log_vout</i> except that parameter are passed directly
 * @see log_vout
 */
bool_t
log_out (log_t *l, int level, const char *fmt, ...) /*{{{*/
{
	va_list	par;
	bool_t	st;
	
	va_start (par, fmt);
	st = log_vout (l, level, fmt, par);
	va_end (par);
	return st;
}/*}}}*/
