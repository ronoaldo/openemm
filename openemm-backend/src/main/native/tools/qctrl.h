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
# ifndef	__QCTRL_H
# define	__QCTRL_H		1
# include	"agn.h"

typedef struct entry { /*{{{*/
	char		*fname;		/* filename of entry		*/
	int		match;		/* the value return by filter	*/
	struct entry	*next;
	/*}}}*/
}	entry_t;
typedef struct { /*{{{*/
	char	**content;	/* the content of the qf file		*/
	int	count;		/* # of lines in the qf file		*/
	int	idx;		/* current index for searches		*/
	/*}}}*/
}	qf_t;
typedef struct { /*{{{*/
	char		path[PATH_MAX + 1];	/* path to the queue	*/
	char		fbuf[PATH_MAX + 1];	/* scratch buffer	*/
	char		*fptr;		/* filepointer into fbuf	*/
	buffer_t	*qf;		/* buffer to read q-files	*/
	entry_t		*ent;		/* all matching entries		*/
	/*}}}*/
}	queue_t;

extern entry_t		*entry_alloc (const char *fname, int match);
extern entry_t		*entry_free (entry_t *e);
extern entry_t		*entry_free_all (entry_t *e);

extern qf_t		*qf_alloc (const buffer_t *src);
extern qf_t		*qf_free (qf_t *q);
extern const char	*qf_first (qf_t *q, char ch);
extern const char	*qf_next (qf_t *q, char ch);
extern int		qf_count (qf_t *q, char ch);

extern queue_t		*queue_scan (const char *path, int (*filter) (void *, queue_t *, const char *), void *data);
extern queue_t		*queue_free (queue_t *q);
extern int		queue_lock (queue_t *q, const char *fname, pid_t pid);
extern void		queue_unlock (queue_t *q, int fd, pid_t pid);
extern bool_t		queue_read (queue_t *q, const char *fname);
extern bool_t		queue_readfd (queue_t *q, int fd);

/*
 * command related routines 
 */
extern void		*move_init (log_t *lg, bool_t force, char **args, int alen);
extern bool_t		move_deinit (void *data);
extern bool_t		move_exec (void *data);
extern void		*stat_init (log_t *lg, bool_t force, char **args, int alen);
extern bool_t		stat_deinit (void *data);
extern bool_t		stat_exec (void *data);
# endif		/* __QCTRL_H */
