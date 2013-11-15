IMPORTANT INFORMATION
=====================
Since version 5.5 the login on port 8081 is no longer supported, please use
port 8080 instead.

Since version 6.0 Java 5 is no longer supported because SUN stopped free
support in October 2009.

If you used the online update of OpenEMM to upgrade to this version, please
read file UPDATE.txt in this directory to finish the update process manually.

If you want to upgrade OpenEMM manually, please read the section below.

If you want to install OpenEMM from scratch we strongly recommend to download
and consult the extensive OpenEMM Install Guide (PDF format) to avoid problems.

To use the online update feature of OpenEMM (Linux only) you have to open
TCP/IP port 8044 in the same way you opened port 8080 as described in the
OpenEMM Install Guide.

If you use Windows, an online update is not possible. Please read the Windows
Install Guide for information on how to install and update OpenEMM.


OpenEMM QUICK UPDATE GUIDE for Red Hat and compatible Linux distributions
=========================================================================
1. Download binary tarball to directory /tmp from web address
   http://sourceforge.net/projects/openemm/files/

2. Stop the old OpenEMM and rename the old OpenEMM directory
$> su - openemm
$> openemm.sh stop
$> exit
$> cd /home
$> mv openemm openemm_backup

3. Create the new OpenEMM directory
$> mkdir openemm
$> chown -R openemm:openemm openemm
$> cd openemm

4. Untar OpenEMM tarball as root
$> tar -xvzpf /tmp/OpenEMM-2013-bin.tar.gz
   (please do not forget option "p"!)

5. Copy content for /usr/share/doc
$> mkdir -p /usr/share/doc/OpenEMM-2013
$> mv USR_SHARE/* /usr/share/doc/OpenEMM-2013
$> rm -rf USR_SHARE

6. Replace the generic string "http://localhost:8080" with the domain name of
   your server (like "http://www.domain.com:8080") in these files:
   - /usr/share/doc/OpenEMM-2013/openemm-2013.sql
   - /home/openemm/webapps/openemm/WEB-INF/classes/emm.properties
   - /home/openemm/webapps/openemm/WEB-INF/classes/cms.properties
   - /home/openemm/webapps/openemm-ws/WEB-INF/classes/emm.properties
   - /home/openemm/webapps/openemm-ws/WEB-INF/classes/emm-ws.properties

7. Copy the modifications you made to the "old" files emm.properties and
   cms.properties (found in /home/openemm_backup/webapps/openemm/WEB-INF/classes/)
   to the new files but leave parameter "fckpath" unchanged.

8. Start MySQL DBMS and update the OpenEMM DB and CMS DB
   (depending on the version which you update from you have to update the
    databases step by step through executing the corresponding SQL files
    in the right order - please see OpenEMM Install Guide for details)
$> /etc/init.d/mysqld start
$> mysql -u root -p openemm < /usr/share/doc/OpenEMM-2013/update_openemm-...
$> mysql -u root -p openemm < /usr/share/doc/OpenEMM-2013/update_openemm-...
   For the update from 6.2 to 2011 you only have to process file
   update_openemm-6.2-2011_RC1.sql.
   For the update from 2011 to 2013 you only have to process file
   update_openemm-2011-2013.sql.

9. Make sure that Oracle Java SDK 6 is installed at /opt/openemm/java and
   Apache Tomcat 6 is installed at /opt/openemm/tomcat. See OpenEMM Install
   Guide chapter 3 (Java) and chapter 4 (Tomcat) for details.

10. Launch OpenEMM
$> su - openemm
$> openemm.sh start
$> exit


DOWNGRADING OPENEMM
===================
If you want to downgrade OpenEMM to a version before 6.2 you have to synchro-
nize table 'customer_1_tbl_seq' with 'customer_1_tbl' before launching the old
version of OpenEMM, because 'customer_1_tbl_seq' is no longer used beginning
with OpenEMM 6.2. Start MySQL, enter the command to 'update customer_1_tbl_seq'
and leave MySQL:
$> mysql -u root -p openemm
mysql> INSERT INTO `customer_1_tbl_seq` (`customer_id`) SELECT max(`customer_id`) FROM `customer_1_tbl`;
mysql> quit


OPENEMM FILES
=============
MySQL OpenEMM CMS database dump:
/usr/share/doc/OpenEMM-2013/openemm_cms-2013.sql

MySQL OpenEMM CMS demo database dump:
/usr/share/doc/OpenEMM-2013/openemm_demo-cms-2013.sql

OpenEMM Change Log:
/usr/share/doc/OpenEMM-2013/CHANGELOG.txt

OpenEMM License:
/usr/share/doc/OpenEMM-2013/LICENSE.txt

ThirdPartyLicenses:
/usr/share/doc/OpenEMM-2013/ThirdPartyLicenses/

MySQL OpenEMM database dump:
/usr/share/doc/OpenEMM-2013/openemm-2013.sql

MySQL database update (from OpenEMM a.b.c to x.y.z)
/usr/share/doc/OpenEMM-2013/update_openemm-a.b.c-x.y.z.sql
(File update_openemm-2013_RC2-2013.sql is only needed by users who installed
 a release candidate of OpenEMM 2013.)

System info for OpenEMM online update feature:
/usr/share/doc/OpenEMM-2013/empty-updates.txt

Script which is executed by OpenEMM's online update
after the upgrade to the current version:
/usr/share/doc/OpenEMM-2013/upgrade-postproc.sh

MySQL Database Conversion Script to convert the whole database
to UTF-8 character set (if you want to use OpenEMM beyond the
5.4.0 release, you must once convert your database):
/home/openemm/bin/openemm-charset-convert.sh


MISCELLANEOUS
=============
Website:    http://www.openemm.org
Twitter:    http://www.twitter.com/openemm
Support:    http://www.openemm.org/support.html
Facebook:   http://www.facebook.com/openemm
Newsletter: http://www.openemm.org/newsletter.html
Maintainer: Martin Aschoff (maschoff AT adamatis DOT org)

OpenEMM uses the Open Source Initiative Approved License "Common Public
Attribution License 1.0 (CPAL)". Open Source Initiative Approved is a
trademark of the Open Source Initiative.

Copyright (c) 2006-2013 AGNITAS AG, Munich, Germany
