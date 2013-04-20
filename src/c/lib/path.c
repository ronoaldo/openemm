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
/** @file path.c
 * Path related support routines
 */
# include	<stdlib.h>
# include	<pwd.h>
# include	"agn.h"

static char	*home = NULL;
const char *
path_home (void) /*{{{*/
{
	if (! home) {
		const char	*env;
		
		if (env = getenv ("HOME")) {
			home = strdup (env);
		} else {
			struct passwd	*pw;
			
			setpwent ();
			pw = getpwuid (getuid ());
			if (pw -> pw_dir)
				home = strdup (pw -> pw_dir);
			endpwent ();
		}
	}
	return home ? home : ".";
}/*}}}*/
char *
mkpath (const char *start, ...) /*{{{*/
{
	va_list		par;
	char		path[PATH_MAX + 1];
	int		room;
	char		*ptr;
	const char	*elem;
	int		elen;
	
	va_start (par, start);
	room = sizeof (path) - 1;
	for (elem = start, ptr = path; elem; elem = va_arg (par, const char *)) {
		elen = strlen (elem);
		if (elen + 1 >= room)
			break;
		if (ptr != path) {
			*ptr++ = PATH_SEP;
			--room;
		}
		strncpy (ptr, elem, elen);
		ptr += elen;
		room -= elen;
	}
	va_end (par);
	*ptr = '\0';
	return elem ? NULL : strdup (path);
}/*}}}*/
