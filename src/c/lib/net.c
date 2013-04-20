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
/** @file net.c
 * Network related routines.
 */
# include	<string.h>
# include	<netdb.h>
# include	<sys/utsname.h>
# include	"agn.h"

/** Get full qualified domain name.
 * Retreive the full qualified domain name for a given hostname
 * @param name the hostname
 * @return the fqdn on success, NULL otherwise
 */
char *
get_fqdn (const char *name) /*{{{*/
{
	char		*fqdn;
	struct hostent	*hent;
	
	fqdn = NULL;
	sethostent (0);
	if ((hent = gethostbyname (name)) && hent -> h_name)
		fqdn = strdup (hent -> h_name);
	endhostent ();
	return fqdn;
}/*}}}*/
/** Get full qualified domain name for current system.
 * Retreive the fqdn for the local machine
 * @return the fqdn on success, NULL otherwise
 */
char *
get_local_fqdn (void) /*{{{*/
{
	char		*fqdn;
	struct utsname	un;
	
	fqdn = NULL;
	if (uname (& un) != -1)
		fqdn = get_fqdn (un.nodename);
	return fqdn;
}/*}}}*/
