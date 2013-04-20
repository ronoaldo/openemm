#!/bin/sh
#	-*- sh -*-
##################################################################################
# The contents of this file are subject to the Common Public Attribution
# License Version 1.0 (the "License"); you may not use this file except in
# compliance with the License. You may obtain a copy of the License at
# http://www.openemm.org/cpal1.html. The License is based on the Mozilla
# Public License Version 1.1 but Sections 14 and 15 have been added to cover
# use of software over a computer network and provide for limited attribution
# for the Original Developer. In addition, Exhibit A has been modified to be
# consistent with Exhibit B.
# Software distributed under the License is distributed on an "AS IS" basis,
# WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
# the specific language governing rights and limitations under the License.
# 
# The Original Code is OpenEMM.
# The Original Developer is the Initial Developer.
# The Initial Developer of the Original Code is AGNITAS AG. All portions of
# the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
# Reserved.
# 
# Contributor(s): AGNITAS AG. 
##################################################################################
#
. $HOME/bin/scripts/config.sh
#
tmpfn=/var/tmp/upg-pp-$$
rc=0
#
# 1.) Add missing CMS database
cmssql=$HOME/USR_SHARE/openemm_cms.sql
if [ -f $cmssql ]; then
	prop=""
	for path in `ls -t1d /home/openemm?*`; do
		for part in "openemm" "core" "openemm/htdocs"; do
			temp="$path/webapps/$part/WEB-INF/classes/emm.properties"
			if [ -f "$temp" ]; then
				prop="$temp"
				break
			fi
		done
		if [ "$prop" ]; then
			break
		fi
	done
	user="agnitas"
	pass="openemm"
	if [ "$prop" ]; then
		user="`echo \`egrep 'jdbc.username *=' $prop | cut -d= -f2-\``"
		pass="`echo \`egrep 'jdbc.password *=' $prop | cut -d= -f2-\``"
	fi
	mysql -u "$user" "--password=$pass" -B -e 'show databases' > $tmpfn
	if [ $? -ne 0 ]; then
		log "ERROR: Failed to read databases (maybe root access is password protected?)"
		rc=1
	else
		grep -q openemm_cms $tmpfn
		if [ $? -ne 0 ]; then
			mysqladmin -u root create openemm_cms
			if [ $? -ne 0 ]; then
				log "ERROR: Failed to create openemm_cms database"
				rc=1
			else
				mysql -u root openemm_cms < $cmssql
				if [ $? -ne 0 ]; then
					log "ERROR: Failed to fill $cmssql into new database openemm_cms"
					rc=1
				fi
			fi
		fi
	fi
fi
#
# 2.) Set proper path for FCEditor
props=$HOME/webapps/openemm/WEB-INF/classes/emm.properties
if [ -f $props ]; then
	nentry="fckpath=fckeditor-2.6.6"
	egrep -q "^${nentry}\$" $props
	if [ $? -ne 0 ]; then
		sed "s/fckpath=.*/$nentry/" < $props > $tmpfn
		if [ $? -eq 0 ]; then
			mv $tmpfn $props
			if [ $? -ne 0 ]; then
				log "ERROR: Failed to replace $props using $tmpfn"
			fi
		else
			log "ERROR: Failed to patch $props"
		fi
	fi
fi
#
# X.) Cleanup
rm -f $tmpfn
exit $rc
