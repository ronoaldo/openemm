#!/bin/sh
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
cat << __EOF__
OpenEMM Conversion Script
=========================

Please make sure that the OpenEMM application itself is stopped 
and no other process accesses the database!

Temporary files are written to /tmp, so please make sure you have
enough space on your partition where /tmp is on.

If you would like to continue, press ENTER, to abort press Control-C.

__EOF__
echo -n '--> '
read dummy
if [ ! "$dummy" = "" ]; then
	echo "Aborted."
	exit 1
fi
#
running="`ps -u openemm -f 2>/dev/null | grep [j]ava | wc -l`"
whoami="`which whoami`"
if [ "$whoami" ] && [ -x "$whoami" ] && [ "`$whoami`" = "openemm" ]; then
	needsu='false'
else
	needsu='true'
fi
if [ $running -gt 0 ]; then
	cat << __EOF__
OpenEMM seems to be running, it will be now stopped.
__EOF__
	if [ "$needsu" = "true" ]; then
		su -c "./bin/openemm.sh stop" - openemm
	else
		(
			cd
			./bin/openemm.sh stop
		)
	fi
fi
#
umask 077
#
dbfile=/tmp/openemm.dump
dbconv=/tmp/openemm.conv
if [ -f $dbfile ]; then
	cat << __EOF__
Dumpfile $dbfile already exists, if you do not need it any more,
please remove the file and restart the script afterwards. 
__EOF__
	exit 1
fi
#
cat << __EOF__
Now dumping the current content of the database to $dbfile
This may take some time, depending on the size of the database,
so be patient. You have to enter the root password of the
MySQL instance to start the dump:
__EOF__
mysqldump -aCceQ --lock-all-tables -u root -p -r $dbfile openemm
if [ $? -ne 0 ]; then
	cat << __EOF__
Sorry, something went wrong, please consult the error message
and try to fix the problem, then restart the script.
__EOF__
	rm -f $dbfile
	exit 1
fi
#
cat << __EOF__
Now we convert the dumped to UTF-8, please stand by.
__EOF__
echo "ALTER DATABASE openemm DEFAULT CHARACTER SET utf8;" > $dbconv
sed -e 's/ \(character set utf8 \)\?collate utf8_unicode_ci//' \
    -e 's/DEFAULT CHARSET=latin1/DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci/' \
	< $dbfile >> $dbconv
if [ $? -ne 0 ]; then
	cat << __EOF__
The conversion failed, please try to fix the problem and restart
the script.
__EOF__
	rm -f $dbfile
	exit 1
fi
#
cat << __EOF__
Conversion has finished, now we are going to drop the database and
reimport the modified data.

WARNING: Be sure to keep a backup just for the case! We DO NOT GIVE
ANY WARRANTY TO ANY DATA LOSS DUE TO THE CONVERSION! DO IT AT YOUR
OWN RISK!

Please type in YES if you really want to continue.
__EOF__
echo -n "Are you sure to continue? "
read ans
case "$ans" in
[Yy][Ee][Ss])
	;;
*)
	echo "Aborted, better luck next time."
	rm -f $dbfile $dbconv
	exit 0
	;;
esac
#
cat << __EOF__
Now you are asked three times for the root password for the MySQL server,
first time drops the database, second time recreates it and third time
imports the saved data.
__EOF__
mode=0
while [ $mode -lt 3 ] ; do
	case "$mode" in
	0)
		echo "========== Drop database =========="
		err="Drop of database failed"
		mysqladmin -u root -p drop openemm
		;;
	1)
		echo "========== Create database =========="
		err="Recreation of database failed"
		mysqladmin -u root -p create openemm
		;;
	2)
		echo "========== Import into database =========="
		err="Import of database failed"
		mysql -u root -p openemm < $dbconv
		;;
	esac
	if [ $? -ne 0 ]; then
		cat << __EOF__

=====================================================
Error "$err" occurs.
=====================================================

If this is a simple error (like mistyped password) just press RETURN and
retry again. Otherwise, if you can fix the problem on the fly, fix it now
and press ENTER to continue. Otherwise (again) press Control-C or Break
to abort conversion. Be aware that your current database may now be lost,
so keep the original backup file $dbfile and the converted file $dbconv
to recover your database.
__EOF__
		echo -n '--> '
		read dummy
	else
		mode=`expr $mode + 1`
	fi
done
#
cat << __EOF__
Congratulations, the conversion is completed sucessfully. Please log in and
verify that everything is working. WE KEEP THE ORIGINAL DUMP FILE IN $dbfile
SO YOU HAVE A BACKUP AT HAND. If everything is working, you can just remove
the file.
__EOF__
rm -f $dbconv
#
if [ $running -gt 0 ]; then
	cat << __EOF__
Restart OpenEMM.
__EOF__
	if [ "$needsu" = "true" ]; then
		su -c "./bin/openemm.sh start" - openemm
	else
		(
			cd
			./bin/openemm.sh start
		)
	fi
fi
