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
# include	<unistd.h>
# include	<sys/types.h>
# include	<sys/stat.h>
# include	<dirent.h>
# include	<pwd.h>
# include	"agn.h"

# define	OPENEMM_HOME		"/home/openemm"
# define	OPENEMM_USER		"openemm"
# define	SHARE			"USR_SHARE"
# define	OPENEMM_SHARE		"/usr/share/doc"

static int
do_script (int ac, char **av) /*{{{*/
{
	struct stat	st;
	
	if (ac == 0) {
		fprintf (stderr, "Script: need script name to execute.\n");
		return 1;
	}
	if ((strlen (av[0]) < sizeof (OPENEMM_HOME)) ||
	    strncmp (av[0], OPENEMM_HOME, sizeof (OPENEMM_HOME) - 1) ||
	    (av[0][sizeof (OPENEMM_HOME) - 1] != '/')) {
		fprintf (stderr, "Script: execution script must be under %s.\n", OPENEMM_HOME);
		return 1;
	}
	if (lstat (av[0], & st) == -1) {
		fprintf (stderr, "Script: unable to access script %s: %m.\n", av[0]);
		return 1;
	}
	if (! S_ISREG (st.st_mode)) {
		fprintf (stderr, "Script: %s must be a regular file.\n", av[0]);
		return 1;
	}
	setuid (geteuid ());
	setgid (getegid ());
	execv (av[0], (char * const *) av);
	fprintf (stderr, "Script: failed to execute %s.\n", av[0]);
	return 1;
}/*}}}*/
static int
do_archive (int ac, char **av) /*{{{*/
{
	char	*version;
	char	path[sizeof (OPENEMM_HOME) + 256];
	int	n;
	
	if (ac != 1) {
		fprintf (stderr, "Archive: require one parameter, the version.\n");
		return 1;
	}
	version = av[0];
	n = 0;
	do {
		if (! n)
			snprintf (path, sizeof (path), "%s-%s", OPENEMM_HOME, version);
		else
			snprintf (path, sizeof (path), "%s-%s.%d", OPENEMM_HOME, version, n);
		++n;
	} while (access (path, F_OK) != -1);
	if (rename (OPENEMM_HOME, path) == -1)
		return 1;
	return 0;
}/*}}}*/
static int
do_create (int ac, char **av) /*{{{*/
{
	if (mkdir (OPENEMM_HOME, 0755) == -1)
		return 1;
	chown (OPENEMM_HOME, getuid (), getgid ());
	return 0;
}/*}}}*/
static int
do_unpack (int ac, char **av) /*{{{*/
{
	const char	*args[4];
	const char	*tars[] = {
		"/bin/tar",
		"/usr/bin/tar",
		"/usr/local/bin/tar",
		"/bin/gtar",
		"/usr/bin/gtar",
		"/usr/local/bin/gtar"
	};
	int		n;
	
	if (ac != 1) {
		fprintf (stderr, "Unpack: require the pathname to unpack.\n");
		return 1;
	}
	if (chdir (OPENEMM_HOME) == -1) {
		fprintf (stderr, "Unpack: failed to chdir to %s directory (%m).\n", OPENEMM_HOME);
		return 1;
	}
	args[0] = NULL;
	for (n = 0; n < sizeof (tars) / sizeof (tars[0]); ++n)
		if (access (tars[n], X_OK) != -1) {
			args[0] = tars[n];
			break;
		}
	if (args[0]) {
		args[1] = "xzpf";
		args[2] = av[0];
		args[3] = NULL;
		execv (args[0], (char * const *) args);
	}
	return 1;
}/*}}}*/
static void
permissions (const char *path, uid_t uid, gid_t gid) /*{{{*/
{
	static struct {
		const char	*path;
		uid_t		uid;
		gid_t		gid;
		mode_t		mode;
	}	pfiles[] = {
		{	OPENEMM_HOME "/bin/smctrl", 0, 0, 06755		},
		{	OPENEMM_HOME "/bin/updater", 0, 0, 06755	},
		{	OPENEMM_HOME "/conf/bav/bav.rc", 0, 0, 0600	}
	};
	struct stat	st;
	
	if (lstat (path, & st) != -1) {
		int	n;
	
		for (n = 0; n < sizeof (pfiles) / sizeof (pfiles[0]); ++n)
			if (! strcmp (pfiles[n].path, path))
				break;
		if (n < sizeof (pfiles) / sizeof (pfiles[0])) {
			if ((pfiles[n].uid != -1) && (pfiles[n].gid != -1))
				lchown (path, pfiles[n].uid, pfiles[n].gid);
			else
				lchown (path, uid, gid);
			if ((pfiles[n].mode != -1) && (! S_ISLNK (st.st_mode)))
				chmod (path, pfiles[n].mode);
		} else
			lchown (path, uid, gid);
		if (S_ISDIR (st.st_mode)) {
			DIR		*dp;
			struct dirent	*ent;
			char		*scratch, *ptr;
			int		plen;
			
			plen = strlen (path);
			if (scratch = malloc (plen + 1026)) {
				strcpy (scratch, path);
				ptr = scratch + plen;
				*ptr++ = '/';
				if (dp = opendir (path)) {
					while (ent = readdir (dp))
						if (((ent -> d_name[0] != '.') || (ent -> d_name[1] && (ent -> d_name[1] != '.'))) &&
						    strlen (ent -> d_name) < 1024) {
							strcpy (ptr, ent -> d_name);
							permissions (scratch, uid, gid);
						}
					closedir (dp);
				}
				free (scratch);
			}
		}
	}
}/*}}}*/
static int
do_permissions (int ac, char **av) /*{{{*/
{
	struct passwd	*pw;
	uid_t		uid;
	gid_t		gid;
	
	uid = getuid ();
	gid = getgid ();
	setpwent ();
	pw = getpwnam (OPENEMM_USER);
	if (pw) {
		uid = pw -> pw_uid;
		gid = pw -> pw_gid;
	}
	endpwent ();
	permissions (OPENEMM_HOME, uid, gid);
	return 0;
}/*}}}*/
static int
do_share (int ac, char **av) /*{{{*/
{
	int		n;
	char		dest[sizeof (OPENEMM_SHARE) + 256];
	char		temp[sizeof (OPENEMM_SHARE) + 256];
	const char	*args[6];
	const char	*mvs[] = {
		"/bin/mv",
		"/usr/bin/mv"
	};
	
	if (ac != 1) {
		fprintf (stderr, "Share: require the version.\n");
		return 1;
	}
	if (chdir (OPENEMM_HOME) == -1) {
		fprintf (stderr, "Share: failed to chdir to %s directory (%m).\n", OPENEMM_HOME);
		return 1;
	}
	if (access (SHARE, F_OK) == -1) {
		fprintf (stderr, "Share: source directory %s missing.\n", SHARE);
		return 1;
	}
	sprintf (dest, "%s/OpenEMM-%s", OPENEMM_SHARE, av[0]);
	if (access (dest, F_OK) != -1) {
		n = 0;
		do {
			++n;
			sprintf (temp, "%s-%d", dest, n);
		}	while ((n < 512) && (access (temp, F_OK) != -1));
		if (rename (dest, temp) == -1) {
			fprintf (stderr, "Share: failed to rename %s to %s (%m).\n", dest, temp);
			return 1;
		}
	}
	for (n = 0; n < sizeof (mvs) / sizeof (mvs[0]); ++n)
		if (access (mvs[n], X_OK) != -1)
			break;
	if (n == sizeof (mvs) / sizeof (mvs[0])) {
		fprintf (stderr, "Share: failed to find mv command.\n");
		return 1;
	}
	args[0] = mvs[n];
	args[1] = "-f";
	args[2] = SHARE;
	args[3] = dest;
	args[4] = NULL;
	execv (args[0], (char * const *) args);
	return 1;
}/*}}}*/

static struct { /*{{{*/
	const char	*cmd;
	int		(*func) (int, char **);
	/*}}}*/
}	cmdtab[] = { /*{{{*/
	{	"script",		do_script	},
	{	"archive",		do_archive	},
	{	"create",		do_create	},
	{	"unpack",		do_unpack	},
	{	"permissions",		do_permissions	},
	{	"share",		do_share	}
	/*}}}*/
};
static int
usage (const char *pgm) /*{{{*/
{
	int	n;
	
	fprintf (stderr, "Usage: %s <command> [<params>]\n", pgm);
	fprintf (stderr, "Available commands are:\n");
	for (n = 0; n < sizeof (cmdtab) / sizeof (cmdtab[0]); ++n)
		fprintf (stderr, "\t%s\n", cmdtab[n].cmd);
	return 1;
}/*}}}*/
int
main (int argc, char **argv) /*{{{*/
{
	int	n;
	char	*cmd;
	
	while ((n = getopt (argc, argv, "")) != -1)
		switch (n) {
		case '?':
		default:
			return usage (argv[0]);
		}
	if (optind == argc)
		return usage (argv[0]);
	cmd = argv[optind++];
	for (n = 0; n < sizeof (cmdtab) / sizeof (cmdtab[0]); ++n)
		if (! strcmp (cmdtab[n].cmd, cmd))
			break;
	if (n == sizeof (cmdtab) / sizeof (cmdtab[0]))
		return usage (argv[0]);
	umask (0);
	return (*cmdtab[n].func) (argc - optind, argv + optind);
}/*}}}*/
