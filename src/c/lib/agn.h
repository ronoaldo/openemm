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
/** @file agn.h
 * Header file for the Agnitas C Library
 * All usable datatypes and functions are defined or declared
 * here. The definition of the functions can be found in each
 * separate file
 */
# ifndef	__LIB_AGN_H
# define	__LIB_AGN_H		1
# include	<stdio.h>
# include	<stdarg.h>
# include	<string.h>
# include	<unistd.h>
# include	<sys/types.h>
# include	<signal.h>
# include	<time.h>
# include	<limits.h>
# ifdef		linux
# include	<paths.h>
# else		/* linux */
# define	_PATH_DEVNULL		"/dev/null"
# define	_PATH_VARRUN		"/var/run/"
# define	_PATH_VARTMP		"/var/tmp/"
# ifdef		WIN32
# undef		_PATH_DEVNULL
# define	_PATH_DEVNULL		"nul"
# undef		_PATH_VARRUN
# define	_PATH_VARRUN		"\\OpenEMM\\var\\run\\"
# undef		_PATH_VARTMP
# define	_PATH_VARTMP		"\\OpenEMM\\var\\tmp\\"
# define	PATH_MAX		1024
# define	PATH_SEP		'\\'
typedef int	mode_t;
typedef int	sigset_t;
# define	__attribute__(xxx)
# define	inline
static int
snprintf (char *buf, int blen, const char *fmt, ...) /*{{{*/
{
	va_list	par;
	int	rc;
	
	va_start (par, fmt);
	rc = vsprintf (buf, fmt, par);
	va_end (par);
	return rc;
}/*}}}*/
# endif		/* WIN32 */
# endif		/* linux */
# ifndef	PATH_SEP
# define	PATH_SEP		'/'
# endif		/* PATH_SEP */

/*{{{	some predefined loglevels */
/** @def LV_NONE
 * Loglevel for undefined behaviour */
/** @def LV_FATAL
 * Loglevel for fatal error messages */
/** @def LV_ERROR
 * Loglevel for error messages */
/** @def LV_WARNING
 * Loglevel for warnings */
/** @def LV_NOTICE
 * Loglevel for for important notices, which are no warning or error */
/** @def LV_INFO
 * Loglevel for informal messages */
/** @def LV_VERBOSE
 * Loglevel for more verbose informations */
/** @def LV_DEBUG
 * Loglevel for debug output */
# define	LV_NONE			0
# define	LV_FATAL		1
# define	LV_ERROR		2
# define	LV_WARNING		3
# define	LV_NOTICE		4
# define	LV_INFO			5
# define	LV_VERBOSE		6
# define	LV_DEBUG		7
/*}}}*/
/*{{{	logmask handling */
/** @def LM_NONE
 * The empty log mask */
/** @def LM_BIT(bbb)
 * Sets the bitmask for a given loglevel */
/** @def LM_MATCH(mmm,ppp)
 * Checks wether a given loglevel mask matches a used loglevel mask */
# define	LM_NONE			((logmask_t) 0)
# define	LM_BIT(bbb)		((logmask_t) (1 << (bbb)))
# define	LM_MATCH(mmm,ppp)	(((! (mmm)) || ((ppp) & (mmm))) ? true : false)
/*}}}*/
/*{{{	logsuspend masks */
/** @def LS_LOGFILE
 * Mask to suspend output to logfile */
/** @def LS_FILEDESC
 * Mask to suspend output to file */
/** @def LS_SYSLOG
 * Mask to suspend output to syslog */
/** @def LS_COLLECT
 * Mask to suspend output to collection */
# define	LS_LOGFILE		(1 << 0)
# define	LS_FILEDESC		(1 << 1)
# define	LS_SYSLOG		(1 << 2)
# define	LS_COLLECT		(1 << 3)
/*}}}*/

/**
 * Represents an unsigned 8 bit value
 */
typedef unsigned char	byte_t;
/**
 * Unsigned number to hold a 32 bit hash value
 */
typedef unsigned long	hash_t;
/**
 * Symbolic names for boolean values
 */
typedef enum { /*{{{*/
	false,
	true
	/*}}}*/
}	bool_t;

/**
 * Keeps track of a dynamic growing/shrinking buffer
 */
typedef struct buffer { /*{{{*/
	long	length;		/**< used length of buffer			*/
	long	size;		/**< allocated size of buffer			*/
	byte_t	*buffer;	/**< the buffer itself				*/
	long	spare;		/**< alloc this in bytes as spare memory	*/
	bool_t	valid;		/**< if no operation has failed			*/
	struct buffer
		*link;		/**< for pool management			*/
	/*}}}*/
}	buffer_t;
typedef struct pool	pool_t;

/**
 * A linked list of variable/value pairs
 */
typedef struct var { /*{{{*/
	char	*var;		/**< The name of the variable			*/
	char	*val;		/**< Its value					*/
	struct var
		*next;		/**< Next element in list or NULL		*/
	/*}}}*/
}	var_t;

/**
 * A node in a generic hashmap
 */
typedef struct gnode { /*{{{*/
	byte_t	*key;		/**< the key of the gnode			*/
	int	klen;		/**< length of the key				*/
	hash_t	hash;		/**< its hash value				*/
	byte_t	*data;		/**< the data of the gnode			*/
	int	dlen;		/**< the length of the data			*/
	struct gnode
		*next;		/**< sibling in same hash tree			*/
	/*}}}*/
}	gnode_t;

/**
 * A node in the hashmap
 */
typedef struct node { /*{{{*/
	char	*mkey;		/**< the key of this node			*/
	hash_t	hash;		/**< its hash value				*/
	char	*okey;		/**< original key of this node			*/
	char	*data;		/**< its value					*/
	struct node
		*next;		/**< sibling in same hash tree			*/
	/*}}}*/
}	node_t;

typedef enum { /*{{{*/
	MAP_Generic,		/**< generic hash map				*/
	MAP_CaseSensitive,	/**< case sensitive mode			*/
	MAP_CaseIgnore		/**< ignore case mode				*/
	/*}}}*/
}	mapmode_t;

/**
 * Mapping a ka hash collection
 */
typedef struct { /*{{{*/
	mapmode_t
		mode;		/**< which mapping mode				*/
	int	hsize;		/**< size of the hashing array			*/
	union {
		void	**u;
		gnode_t	**g;
		node_t	**n;
	}	cont;		/**< the hashing array of nodes			*/
	/*}}}*/
}	map_t;

/**
 * An entry in a (hash)set
 */
typedef struct sentry { /*{{{*/
	char	*name;		/**< name of set entry				*/
	int	nlen;		/**< length of name				*/
	hash_t	hash;		/**< hash value					*/
	struct sentry
		*next;		/**< sibling entry				*/
	/*}}}*/
}	sentry_t;
typedef struct set { /*{{{*/
	bool_t	icase;		/**< ignore case during matches			*/
	int	hsize;		/**< size of hashing array			*/
	int	count;		/**< current number of entries			*/
	sentry_t
		**s;		/**< the data itself				*/
	/*}}}*/
}	set_t;

/**
 * An entry in a LRU cache
 */
typedef struct centry { /*{{{*/
	hash_t		hash;			/* hash value over key	*/
	byte_t		*key;			/* key			*/
	int		klen;			/* key length		*/
	byte_t		*data;			/* data			*/
	int		dlen;			/* data length		*/
	struct centry	*prev, *next;		/* link in hash		*/
	struct centry	*back, *forw;		/* link in timeline	*/
	/*}}}*/
}	centry_t;
typedef struct { /*{{{*/
	int		size;			/* max size in cache	*/
	int		hsize;			/* hash size		*/
	int		count;			/* # of current entries	*/
	centry_t	**store;		/* hash store		*/
	centry_t	*head, *tail;		/* timeline		*/
	/*}}}*/
}	cache_t;

/**
 * Keeps track of signal handling
 */
typedef struct { /*{{{*/
	void		*base;		/**< all signals			*/
	sigset_t	mask;		/**< the mask for all signals		*/
	sigset_t	oldmask;	/**< for blocking			*/
	bool_t		isblocked;	/**< is currently blocked?		*/
	/*}}}*/
}	csig_t;

/**
 * Bitmask for handling logging masks
 */
typedef unsigned long	logmask_t;
/**
 * All informations required for logging
 */
typedef struct { /*{{{*/
	int		logfd;		/**< file desc. to copy output to	*/
	int		slprio;		/**< syslog priority			*/
	char		*logpath;	/**< path to logdirectory		*/
	char		hostname[64];	/**< the local hostname			*/
	char		*program;	/**< name of program			*/
	char		fname[PATH_MAX];	/**< filename to write to	*/
	int		level;		/**< current loglevel			*/
	logmask_t	use;		/**< the current used logmask		*/
	time_t		last;		/**< last time we wrote something	*/
	long		lastday;	/**< dito for the day			*/
	int		diff;		/**< TZ drift				*/
	FILE		*lfp;		/**< filepointer to output file		*/
	void		*idc;		/**< ID chain				*/
	bool_t		slactive;	/**< syslog is active			*/
	buffer_t	*obuf;		/**< output buffer			*/
	buffer_t	*collect;	/**< to collect all messages		*/
	int		clevel;		/**< limit level for collection		*/
	
	void		*suspend;	/**< suspend list for logging		*/
	/*}}}*/
}	log_t;
/**
 * Keep track of a lock using a file
 */
typedef struct { /*{{{*/
	char		*fname;		/**< filename of the lockfile		*/
	bool_t		islocked;	/**< if we had the lock			*/
	/*}}}*/
}	lock_t;
# ifndef	WIN32

typedef struct { /*{{{*/
	bool_t	background;	/* if we should run in background	*/
	bool_t	detach;		/* if we should detach from current tty	*/
	pid_t	pid;		/* process ID of daemon process		*/
	char	*pidfile;	/* file to write PID to			*/
	bool_t	pfvalid;	/* if pidfile is used by us		*/
	/*}}}*/
}	daemon_t;
# endif		/* WIN32 */

extern buffer_t		*buffer_alloc (int nsize);
extern buffer_t		*buffer_free (buffer_t *b);
extern bool_t		buffer_valid (buffer_t *b);
extern void		buffer_clear (buffer_t *b);
extern void		buffer_truncate (buffer_t *b, long length);
extern int		buffer_length (buffer_t *b);
extern const byte_t	*buffer_content (buffer_t *b);
extern bool_t		buffer_size (buffer_t *b, int nsize);
extern bool_t		buffer_set (buffer_t *b, const byte_t *data, int dlen);
extern bool_t		buffer_setbuf (buffer_t *b, buffer_t *data);
extern bool_t		buffer_setb (buffer_t *b, byte_t data);
extern bool_t		buffer_setsn (buffer_t *b, const char *str, int len);
extern bool_t		buffer_sets (buffer_t *b, const char *str);
extern bool_t		buffer_setch (buffer_t *b, char ch);
extern bool_t		buffer_append (buffer_t *b, const byte_t *data, int dlen);
extern bool_t		buffer_appendbuf (buffer_t *b, buffer_t *data);
extern bool_t		buffer_appendb (buffer_t *b, byte_t data);
extern bool_t		buffer_appendsn (buffer_t *b, const char *str, int len);
extern bool_t		buffer_appends (buffer_t *b, const char *str);
extern bool_t		buffer_appendch (buffer_t *b, char ch);
extern bool_t		buffer_appendnl (buffer_t *b);
extern bool_t		buffer_appendcrlf (buffer_t *b);
extern bool_t		buffer_insert (buffer_t *b, int pos, const byte_t *data, int dlen);
extern bool_t		buffer_insertbuf (buffer_t *b, int pos, buffer_t *data);
extern bool_t		buffer_insertsn (buffer_t *b, int pos, const char *str, int len);
extern bool_t		buffer_inserts (buffer_t *b, int pos, const char *str);
extern bool_t		buffer_stiff (buffer_t *b, const byte_t *data, int dlen);
extern bool_t		buffer_stiffbuf (buffer_t *b, buffer_t *data);
extern bool_t		buffer_stiffb (buffer_t *b, byte_t data);
extern bool_t		buffer_stiffsn (buffer_t *b, const char *str, int len);
extern bool_t		buffer_stiffs (buffer_t *b, const char *str);
extern bool_t		buffer_stiffch (buffer_t *b, char ch);
extern bool_t		buffer_stiffnl (buffer_t *b);
extern bool_t		buffer_stiffcrlf (buffer_t *b);
extern bool_t		buffer_vformat (buffer_t *b, const char *fmt, va_list par) __attribute__ ((format (printf, 2, 0)));
extern bool_t		buffer_format (buffer_t *b, const char *fmt, ...) __attribute__ ((format (printf, 2, 3)));
extern bool_t		buffer_strftime (buffer_t *b, const char *fmt, const struct tm *tt);
extern byte_t		*buffer_cut (buffer_t *b, long start, long length, long *rlength);
extern const char	*buffer_string (buffer_t *b);
extern char		*buffer_copystring (buffer_t *b);
extern int		buffer_iseol (const buffer_t *b, int pos);
extern int		buffer_index (const buffer_t *b, const byte_t *content, int clen);
extern int		buffer_indexsn (const buffer_t *b, const char *s, int slen);
extern int		buffer_indexs (const buffer_t *b, const char *s);

extern pool_t		*pool_alloc (void);
extern pool_t		*pool_free (pool_t *p);
extern void		pool_flush (pool_t *p);
extern buffer_t		*pool_request (pool_t *p, int nsize);
extern buffer_t		*pool_release (pool_t *p, buffer_t *b);
extern buffer_t		*buffer_request (int nsize);
extern buffer_t		*buffer_release (buffer_t *b);

extern var_t		*var_alloc (const char *var, const char *val);
extern var_t		*var_free (var_t *v);
extern var_t		*var_free_all (var_t *v);
extern bool_t		var_variable (var_t *v, const char *var);
extern bool_t		var_value (var_t *v, const char *val);
extern bool_t		var_match (var_t *v, const char *var);
extern bool_t		var_imatch (var_t *v, const char *var);
extern bool_t		var_partial_match (var_t *v, const char *var);
extern bool_t		var_partial_imatch (var_t *v, const char *var);
extern var_t		*var_find (var_t *v, const char *var);
extern var_t		*var_ifind (var_t *v, const char *var);
extern var_t		*var_partial_find (var_t *v, const char *var);
extern var_t		*var_partial_ifind (var_t *v, const char *var);

extern hash_t		hash_value (const byte_t *key, int len);
extern hash_t		hash_svalue (const char *key, int len, bool_t icase);
extern bool_t		hash_match (const byte_t *key, int klen, hash_t khash, const byte_t *match, int mlen, hash_t mhash);
extern bool_t		hash_smatch (const char *key, int klen, hash_t khash, const char *match, int mlen, hash_t mhash, bool_t icase);
extern int		hash_size (int size);

extern gnode_t		*gnode_alloc (const byte_t *key, int klen, hash_t hash,
				      const byte_t *data, int dlen);
extern gnode_t		*gnode_free (gnode_t *g);
extern gnode_t		*gnode_free_all (gnode_t *g);
extern bool_t		gnode_setdata (gnode_t *g, const byte_t *data, int dlen);
extern node_t		*node_alloc (const char *mkey, hash_t hash,
				     const char *okey, const char *data);
extern node_t		*node_free (node_t *n);
extern node_t		*node_free_all (node_t *n);
extern bool_t		node_setdata (node_t *n, const char *data);

extern map_t		*map_alloc (mapmode_t mode, int aproxsize);
extern map_t		*map_free (map_t *m);
extern gnode_t		*map_gadd (map_t *m, const byte_t *key, int klen, const byte_t *data, int dlen);
extern node_t		*map_add (map_t *m, const char *key, const char *data);
extern bool_t		map_delete_node (map_t *m, node_t *n);
extern bool_t		map_delete (map_t *m, const char *key);
extern gnode_t		*map_gfind (map_t *m, const byte_t *key, int klen);
extern node_t		*map_find (map_t *m, const char *key);

extern set_t		*set_alloc (bool_t icase, int aproxsize);
extern set_t		*set_free (set_t *s);
extern bool_t		set_add (set_t *s, const char *name, int nlen);
extern void		set_remove (set_t *s, const char *name, int nlen);
extern bool_t		set_find (set_t *s, const char *name, int nlen);

extern cache_t		*cache_free (cache_t *c);
extern cache_t		*cache_alloc (int size);
extern centry_t		*cache_find (cache_t *c, const byte_t *key, int klen);
extern centry_t		*cache_add (cache_t *c, const byte_t *key, int klen, const byte_t *data, int dlen);
extern void		cache_remove (cache_t *c, centry_t *ce);
extern void		cache_delete (cache_t *c, const byte_t *key, int klen);

extern csig_t		*csig_alloc (int signr, ...);
extern csig_t		*csig_free (csig_t *c);
extern void		csig_block (csig_t *c);
extern void		csig_unblock (csig_t *c);

extern const char	*log_level_name (int lvl);
extern int		log_level (const char *levelname);
extern log_t		*log_alloc (const char *logpath, const char *program, const char *levelname);
extern log_t		*log_free (log_t *l);
extern bool_t		log_level_set (log_t *l, const char *levelname);
extern void		log_set_mask (log_t *l, logmask_t mask);
extern void		log_clr_mask (log_t *l);
extern void		log_add_mask (log_t *l, logmask_t mask);
extern void		log_del_mask (log_t *l, logmask_t mask);
extern bool_t		log_path_set (log_t *l, const char *logpath);
extern bool_t		log_path_default (log_t *l);
extern void		log_tofd (log_t *l, int fd);
extern void		log_nofd (log_t *l);
extern void		log_tosyslog (log_t *l, const char *ident, int option, int facility, int priority);
extern void		log_nosyslog (log_t *l);
extern bool_t		log_collect (log_t *l, int level);
extern void		log_uncollect (log_t *l);
extern bool_t		log_idset (log_t *l, const char *what);
extern void		log_idclr (log_t *l);
extern bool_t		log_idpush (log_t *l, const char *what, const char *separator);
extern void		log_idpop (log_t *l);
extern void		log_suspend_pop (log_t *l);
extern void		log_suspend_push (log_t *l, unsigned long mask, bool_t set);
extern bool_t		log_suspend (log_t *l, unsigned long what);
extern bool_t		log_vmout (log_t *l, int level, logmask_t mask, const char *what, const char *fmt, va_list par) __attribute__ ((format (printf, 5, 0)));
extern bool_t		log_mout (log_t *l, int level, logmask_t mask, const char *what, const char *fmt, ...) __attribute__ ((format (printf, 5, 6)));
extern bool_t		log_vidout (log_t *l, int level, logmask_t mask, const char *fmt, va_list par) __attribute__ ((format (printf, 4, 0)));
extern bool_t		log_idout (log_t *l, int level, logmask_t mask, const char *fmt, ...) __attribute__ ((format (printf, 4, 5)));
extern bool_t		log_slvout (log_t *l, int level, logmask_t mask, int priority, const char *what, const char *fmt, va_list par) __attribute__ ((format (printf, 6, 0)));
extern bool_t		log_slout (log_t *l, int level, logmask_t mask, int priority, const char *what, const char *fmt, ...) __attribute__ ((format (printf, 6, 7)));
extern bool_t		log_vout (log_t *l, int level, const char *fmt, va_list par) __attribute__ ((format (printf, 3, 0)));
extern bool_t		log_out (log_t *l, int level, const char *fmt, ...) __attribute__ ((format (printf, 3, 4)));

extern lock_t		*lock_alloc (const char *fname);
extern lock_t		*lock_free (lock_t *l);
extern bool_t		lock_lock (lock_t *l);
extern void		lock_unlock (lock_t *l);
# ifndef	WIN32

extern daemon_t		*daemon_alloc (const char *prog, bool_t background, bool_t detach);
extern daemon_t		*daemon_free (daemon_t *d);
extern void		daemon_done (daemon_t *d);
extern bool_t		daemon_lstart (daemon_t *d, log_t *l, logmask_t lmask, const char *lwhat);
extern bool_t		daemon_start (daemon_t *d, log_t *l);
extern bool_t		daemon_sstart (daemon_t *d);
# endif		/* WIN32 */

extern int		tzdiff (time_t tim);
extern bool_t		atob (const char *str);
extern const char	*path_home (void);
extern char		*mkpath (const char *start, ...);
extern bool_t		struse (char **buf, const char *str);
extern char		*strldup (const char *s);
extern char		*strudup (const char *s);
extern char		*get_fqdn (const char *name);
extern char		*get_local_fqdn (void);
extern char		*skip (char *str);


# ifdef		__OPTIMIZE__
static inline bool_t
__buffer_valid (buffer_t *b) /*{{{*/
{
	return b && b -> valid;
}/*}}}*/
static inline void
__buffer_clear (buffer_t *b) /*{{{*/
{
	b -> length = 0;
	b -> valid = true;
}/*}}}*/
static inline int
__buffer_length (buffer_t *b) /*{{{*/
{
	return b -> length;
}/*}}}*/
static inline const byte_t *
__buffer_content (buffer_t *b) /*{{{*/
{
	return b -> buffer;
}/*}}}*/
static inline bool_t
__buffer_stiff (buffer_t *b, const byte_t *data, int dlen) /*{{{*/
{
	if (b -> length + dlen < b -> size) {
		switch (dlen) {
		default:
			memcpy (b -> buffer + b -> length, data, dlen);
			b -> length += dlen;
			break;
		case 32:
			b -> buffer[b -> length++] = *data++;
		case 31:
			b -> buffer[b -> length++] = *data++;
		case 30:
			b -> buffer[b -> length++] = *data++;
		case 29:
			b -> buffer[b -> length++] = *data++;
		case 28:
			b -> buffer[b -> length++] = *data++;
		case 27:
			b -> buffer[b -> length++] = *data++;
		case 26:
			b -> buffer[b -> length++] = *data++;
		case 25:
			b -> buffer[b -> length++] = *data++;
		case 24:
			b -> buffer[b -> length++] = *data++;
		case 23:
			b -> buffer[b -> length++] = *data++;
		case 22:
			b -> buffer[b -> length++] = *data++;
		case 21:
			b -> buffer[b -> length++] = *data++;
		case 20:
			b -> buffer[b -> length++] = *data++;
		case 19:
			b -> buffer[b -> length++] = *data++;
		case 18:
			b -> buffer[b -> length++] = *data++;
		case 17:
			b -> buffer[b -> length++] = *data++;
		case 16:
			b -> buffer[b -> length++] = *data++;
		case 15:
			b -> buffer[b -> length++] = *data++;
		case 14:
			b -> buffer[b -> length++] = *data++;
		case 13:
			b -> buffer[b -> length++] = *data++;
		case 12:
			b -> buffer[b -> length++] = *data++;
		case 11:
			b -> buffer[b -> length++] = *data++;
		case 10:
			b -> buffer[b -> length++] = *data++;
		case 9:
			b -> buffer[b -> length++] = *data++;
		case 8:
			b -> buffer[b -> length++] = *data++;
		case 7:
			b -> buffer[b -> length++] = *data++;
		case 6:
			b -> buffer[b -> length++] = *data++;
		case 5:
			b -> buffer[b -> length++] = *data++;
		case 4:
			b -> buffer[b -> length++] = *data++;
		case 3:
			b -> buffer[b -> length++] = *data++;
		case 2:
			b -> buffer[b -> length++] = *data++;
		case 1:
			b -> buffer[b -> length++] = *data++;
		case 0:
			break;
		}
		return true;
	} else
		return buffer_stiff (b, data, dlen);
}/*}}}*/
static inline bool_t
__buffer_stiffbuf (buffer_t *b, buffer_t *data) /*{{{*/
{
	return data  && data -> length > 0 ? __buffer_stiff (b, data -> buffer, data -> length) : true;
}/*}}}*/
static inline bool_t
__buffer_stiffb (buffer_t *b, byte_t data) /*{{{*/
{
	if (b -> length + 1 < b -> size) {
		b -> buffer[b -> length++] = data;
		return true;
	} else
		return buffer_stiffb (b, data);
}/*}}}*/
static inline bool_t
__buffer_stiffch (buffer_t *b, char ch) /*{{{*/
{
	if (b -> length + 1 < b -> size) {
		b -> buffer[b -> length++] = ch;
		return true;
	} else
		return buffer_stiffch (b, ch);
}/*}}}*/
static inline bool_t
__buffer_stiffnl (buffer_t *b) /*{{{*/
{
	if (b -> length + 1 < b -> size) {
		b -> buffer[b -> length++] = '\n';
		return true;
	} else
		return buffer_stiffch (b, '\n');
}/*}}}*/
static inline bool_t
__buffer_stiffcrlf (buffer_t *b) /*{{{*/
{
	if (b -> length + 2 < b -> size) {
		b -> buffer[b -> length++] = '\r';
		b -> buffer[b -> length++] = '\n';
		return true;
	} else
		return buffer_stiffcrlf (b);
}/*}}}*/
static inline int
__buffer_iseol (const buffer_t *b, int pos) /*{{{*/
{
	if (pos < b -> length) {
		if (b -> buffer[pos] == '\n')
			return 1;
		if ((b -> buffer[pos] == '\r') && (pos + 1 < b -> length) && (b -> buffer[pos + 1] == '\n'))
			return 2;
	}
	return 0;
}/*}}}*/
# define	buffer_valid		__buffer_valid
# define	buffer_clear		__buffer_clear
# define	buffer_length		__buffer_length
# define	buffer_content		__buffer_content
# define	buffer_stiff		__buffer_stiff
# define	buffer_stiffb		__buffer_stiffb
# define	buffer_stiffch		__buffer_stiffch
# define	buffer_stiffnl		__buffer_stiffnl
# define	buffer_stiffcrlf	__buffer_stiffcrlf
# define	buffer_iseol		__buffer_iseol
# endif		/* __OPTIMIZE__ */
# endif		/* __LIB_AGN_H */
