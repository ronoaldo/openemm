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
# include	<stdio.h>
# include	<stdlib.h>
# include	<ctype.h>
# include	<unistd.h>
# include	<string.h>
# include	<netinet/in.h>
# include	"libmilter/mfapi.h"
# include	"bav.h"

# define	SOCK_PATH		"var/run/bav.sock"
# define	LOCK_PATH		"var/lock/bav.lock"
# define	CFGFILE			"var/spool/bav/bav.conf"
# define	X_AGN			"X-AGNMailloop"
# define	X_LOOP			"X-AGNLoop"
# define	LOOP_SET		"set"

static const char	*program;
static const char	*loglevel;
static char		*cfgfile;

typedef struct charc { /*{{{*/
	char		*str;
	struct charc	*next;
	/*}}}*/
}	charc_t;
static charc_t *
charc_free (charc_t *c) /*{{{*/
{
	if (c) {
		if (c -> str)
			free (c -> str);
		free (c);
	}
	return NULL;
}/*}}}*/
static charc_t *
charc_free_all (charc_t *c) /*{{{*/
{
	charc_t	*tmp;
	
	while (tmp = c) {
		c = c -> next;
		charc_free (tmp);
	}
	return NULL;
}/*}}}*/
static charc_t *
charc_alloc (const char *str) /*{{{*/
{
	charc_t	*c;
	
	if (c = (charc_t *) malloc (sizeof (charc_t))) {
		c -> str = str ? strdup (str) : NULL;
		c -> next = NULL;
		if (str && (! c -> str))
			c = charc_free (c);
	}
	return c;
}/*}}}*/

typedef struct { /*{{{*/
	cfg_t		*cfg;
	bool_t		is_local;
	int		x_agn;
	char		*from;
	charc_t		*receiver, *prev;
	char		*info;
	log_t		*lg;
	/*}}}*/
}	priv_t;
static char *
xfree (char *s) /*{{{*/
{
	if (s)
		free (s);
	return NULL;
}/*}}}*/
static bool_t
xcopy (char **buf, const char *str) /*{{{*/
{
	if (*buf)
		free (*buf);
	*buf = str ? strdup (str) : NULL;
	return (! str) || *buf ? true : false;
}/*}}}*/
static void
priv_clear (priv_t *p) /*{{{*/
{
	if (p) {
		p -> x_agn = 0;
		p -> from = xfree (p -> from);
		p -> receiver = charc_free_all (p -> receiver);
		p -> prev = NULL;
		p -> info = xfree (p -> info);
	}
}/*}}}*/
static priv_t *
priv_free (priv_t *p) /*{{{*/
{
	if (p) {
		priv_clear (p);
		if (p -> lg)
			log_free (p -> lg);
		if (p -> cfg)
			cfg_free (p -> cfg);
		free (p);
	}
	return NULL;
}/*}}}*/
static priv_t *
priv_alloc (void) /*{{{*/
{
	priv_t	*p;
	
	if (p = (priv_t *) malloc (sizeof (priv_t)))
		if (p -> cfg = cfg_alloc (cfgfile)) {
			p -> is_local = false;
			p -> x_agn = 0;
			p -> from = NULL;
			p -> receiver = NULL;
			p -> prev = NULL;
			p -> info = NULL;
			if (! (p -> lg = log_alloc (NULL, program, loglevel)))
				p = priv_free (p);
		} else {
			free (p);
			p = NULL;
		}
	return p;
}/*}}}*/
static bool_t
priv_setfrom (priv_t *p, const char *from) /*{{{*/
{
	return xcopy (& p -> from, from);
}/*}}}*/
static bool_t
priv_setto (priv_t *p, const char *to) /*{{{*/
{
	charc_t	*r;
	
	if (r = charc_alloc (to)) {
		if (p -> prev)
			p -> prev -> next = r;
		else
			p -> receiver = r;
		p -> prev = r;
	}
	return r ? true : false;
}/*}}}*/
static bool_t
priv_addinfo (priv_t *p, const char *info) /*{{{*/
{
	char	*temp;

	if ((! p -> info) || (! p -> info[0]))
		return xcopy (& p -> info, info);
	if (temp = malloc (strlen (p -> info) + strlen (info) + 2)) {
		sprintf (temp, "%s,%s", p -> info, info);
		free (p -> info);
		p -> info = temp;
		return true;
	}
	return false;
}/*}}}*/
static bool_t
priv_addinfopair (priv_t *p, const char *var, const char *val) /*{{{*/
{
	bool_t	rc;
	char	*scratch, *ptr;
	
	if (scratch = malloc (strlen (var) + strlen (val) + 2)) {
		for (ptr = scratch; *var; *ptr++ = *var++)
			;
		*ptr++ = '=';
		for (;*val; ++val)
			*ptr++ = *val == ',' ? '_' : *val;
		*ptr = '\0';
		rc = priv_addinfo (p, scratch);
		free (scratch);
	} else
		rc = false;
	return rc;
}/*}}}*/

static sfsistat
handle_connect (SMFICTX *ctx, char  *hostname, _SOCK_ADDR *hostaddr) /*{{{*/
{
	priv_t	*p;

	if (! (p = priv_alloc ()))
		return SMFIS_TEMPFAIL;
	if (hostaddr -> sa_family == AF_INET) {
		struct sockaddr_in	*iaddr = (struct sockaddr_in *) hostaddr;

		if (ntohl (iaddr -> sin_addr.s_addr) == INADDR_LOOPBACK)
			p -> is_local = true;
	}
# ifdef		AF_INET6
	else if (hostaddr -> sa_family == AF_INET6) {
		struct sockaddr_in6	*i6addr = (struct sockaddr_in6 *) hostaddr;
		static struct in6_addr	loopback = IN6ADDR_LOOPBACK_INIT;
		
		if (memcmp (& i6addr -> sin6_addr, & loopback, sizeof (i6addr -> sin6_addr)) == 0)
			p -> is_local = true;
	}
# endif		/* AF_INET6 */
	smfi_setpriv (ctx, p);
	return SMFIS_CONTINUE;
}/*}}}*/
static sfsistat
handle_from (SMFICTX *ctx, char **argv) /*{{{*/
{
	priv_t	*p = (priv_t *) smfi_getpriv (ctx);
	
	if (! p)
		return SMFIS_TEMPFAIL;
	priv_clear (p);
	if (! priv_setfrom (p, argv[0]))
		return SMFIS_TEMPFAIL;
	priv_addinfopair (p, "from", argv[0]);
	return SMFIS_CONTINUE;
}/*}}}*/
static sfsistat
handle_to (SMFICTX *ctx, char **argv) /*{{{*/
{
	priv_t	*p = (priv_t *) smfi_getpriv (ctx);
	char	*chk, *opt;
	bool_t	reject, tempfail;
	
	if (! p)
		return SMFIS_TEMPFAIL;
	if (p -> is_local)
		return SMFIS_CONTINUE;
	if (! (chk = cfg_valid_address (p -> cfg, argv[0]))) {
		log_out (p -> lg, LV_ERROR, "Unable to setup initial data for `%s'", argv[0]);
		return SMFIS_TEMPFAIL;
	}
	if (opt = strchr (chk, ':'))
		*opt++ = '\0';
	reject = false;
	tempfail = false;
	if (! strcmp (chk, ID_REJECT))
		reject = true;
	else if (! strcmp (chk, ID_TEMPFAIL))
		tempfail = true;
	else if ((! strcmp (chk, ID_ACCEPT)) && opt)
		priv_addinfo (p, opt);
	priv_addinfopair (p, "to", argv[0]);
	free (chk);
	if (reject) {
		log_out (p -> lg, LV_INFO, "Receiver `%s' is rejected", argv[0]);
		smfi_setreply (ctx, (char *) "550", (char *) "5.1.1", (char *) "No such user");
		return SMFIS_REJECT;
	}
	if (tempfail) {
		log_out (p -> lg, LV_INFO, "Receiver `%s' is temp. disbaled", argv[0]);
		smfi_setreply (ctx, (char *) "400", (char *) "4.0.0", (char *) "Please try again later");
		return SMFIS_TEMPFAIL;
	}
	if (! priv_setto (p, argv[0]))
		return SMFIS_TEMPFAIL;
	return SMFIS_CONTINUE;
}/*}}}*/
static sfsistat
handle_header (SMFICTX *ctx, char *field, char *value) /*{{{*/
{
	priv_t	*p = (priv_t *) smfi_getpriv (ctx);

	if (! p)
		return SMFIS_TEMPFAIL;
	if (p -> is_local)
		return SMFIS_CONTINUE;
	if (! strcasecmp (field, X_LOOP)) {
		log_out (p -> lg, LV_WARNING, "Mail from `%s' has already loop marker set, rejected", p -> from);
		smfi_setreply (ctx, (char *) "500", (char *) "5.4.6", (char *) "Loop detected");
		return SMFIS_REJECT;
	}
	if (! strcasecmp (field, X_AGN))
		p -> x_agn++;
	return SMFIS_CONTINUE;
}/*}}}*/
static sfsistat
handle_eom (SMFICTX *ctx) /*{{{*/
{
	priv_t	*p = (priv_t *) smfi_getpriv (ctx);
	int	n;
	
	if (! p)
		return SMFIS_TEMPFAIL;
	for (n = 0; n < p -> x_agn; ++n)
		smfi_chgheader (ctx, (char *) X_AGN, 0, NULL);
	if (! p -> is_local) {
		if (p -> info)
			smfi_addheader (ctx, (char *) X_AGN, p -> info);
		smfi_addheader (ctx, (char *) X_LOOP, (char *) LOOP_SET);
	}
	return SMFIS_CONTINUE;
}/*}}}*/
static sfsistat
handle_close(SMFICTX *ctx) /*{{{*/
{
	priv_free (smfi_getpriv (ctx));
	smfi_setpriv (ctx, NULL);
	return SMFIS_CONTINUE;
}/*}}}*/
static struct smfiDesc	bav = { /*{{{*/
	(char *) "bounce address verification",
	SMFI_VERSION,
	SMFIF_ADDHDRS | SMFIF_CHGHDRS,
	handle_connect,
	NULL,
	handle_from,
	handle_to,
	handle_header,
	NULL,
	NULL,
	handle_eom,
	NULL,
	handle_close,
# if	SMFI_VERSION > 2	
	NULL,
	NULL,
	NULL
# endif	
	/*}}}*/
};

static int
usage (const char *pgm) /*{{{*/
{
	fprintf (stderr, "Usage: %s [-L <loglevel>] [-s <socket name>] [-s <reread in seconds>] [-c <config filename>]\n", pgm);
	return 1;
}/*}}}*/
int
main (int argc, char **argv) /*{{{*/
{
	int	rc;
	char	*ptr;
	char	*sock_name;
	int	reread;
	int	n;
	lock_t	*lock;

	if (ptr = strrchr (argv[0], '/'))
		program = ptr + 1;
	else
		program = argv[0];
	loglevel = "WARNING";
	sock_name = NULL;
	reread = 0;
	cfgfile = NULL;

	while ((n = getopt (argc, argv, "L:s:r:c:")) != -1)
		switch (n) {
		case 'L':
			loglevel = optarg;
			break;
		case 's':
			if (sock_name)
				free (sock_name);
			if (! (sock_name = strdup (optarg))) {
				fprintf (stderr, "Failed to allocate memory for socket name %s (%m).\n", optarg);
				return 1;
			}
			break;
		case 'r':
			if ((reread = atoi (optarg)) < 1) {
				fprintf (stderr, "Reread value must be at least 1.\n");
				return 1;
			}
			break;
		case 'c':
			if (cfgfile)
				free (cfgfile);
			if (! (cfgfile = strdup (optarg))) {
				fprintf (stderr, "Faile to allocate memory for config.filename %s (%m).\n", optarg);
				return 1;
			}
			break;
		default:
			return usage (argv[0]);
		}
	if (optind < argc)
		return usage (argv[0]);
	if (! (lock = lock_alloc (LOCK_PATH))) {
		fprintf (stderr, "Failed to allocate memory for locking (%m).\n");
		return 1;
	}
	if (! lock_lock (lock)) {
		fprintf (stderr, "Instance seems already to run, aborting.\n");
		return 1;
	}

	if ((! sock_name) || (! cfgfile)) {
		const char	*home;

		if (! (home = getenv ("HOME")))
			home = "";
		if (! sock_name) {
			if (! (sock_name = malloc (strlen (home) + sizeof (SOCK_PATH) + 16))) {
				fprintf (stderr, "Failed to allocate socket name %s/%s (%m).\n", home, SOCK_PATH);
				return 1;
			}
			sprintf (sock_name, "unix:%s/%s", home, SOCK_PATH);
		}
		if (! cfgfile) {
			if (! (cfgfile = malloc (strlen (home) + sizeof (CFGFILE) + 1))) {
				fprintf (stderr, "Failed to allocate config.filename %s/%s (%m).\n", home, CFGFILE);
				return 1;
			}
			sprintf (cfgfile, "%s/%s", home, CFGFILE);
		}
	}
	rc = 1;
	if (smfi_register (bav) == MI_FAILURE)
		fprintf (stderr, "Failed to register filter.\n");
	else if (smfi_setconn (sock_name) == MI_FAILURE)
		fprintf (stderr, "Failed to register socket name \"%s\".\n", sock_name);
	else if (smfi_opensocket (1) == MI_FAILURE)
		fprintf (stderr, "Failed to open socket socket \"%s\".\n", sock_name);
	else {
		if (smfi_main () == MI_FAILURE)
			fprintf (stderr, "Failed to hand over control to milter.\n");
		else
			rc = 0;
	}
	unlink (sock_name);
	lock_unlock (lock);
	lock_free (lock);
	free (sock_name);
	free (cfgfile);
	return rc;
}/*}}}*/
