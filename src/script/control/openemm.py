#	-*- python -*-
"""**********************************************************************************
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
**********************************************************************************
"""
#
import	sys, os, time, types
#
# Optional configuration area
#
########################################################################
#                                                                      #
#                                Tomcat                                #
#                                                                      #
########################################################################
#
# Path to installed tomcat 6 distribution, set this only, if the script
# is unable to determinate it by itself, e.g.:
# tomcathome = r'C:\Programs\Apache Software Foundation\Tomcat\6.0'
#
tomcathome = None
#
########################################################################
#                      End of configuration section                    #
########################################################################
def show (s):
	sys.stderr.write (s)
	sys.stderr.flush ()
def prompt (prmt):
	if prmt:
		show (prmt)
	return sys.stdin.readline ().strip ()
def error (msg):
	show (msg + '\n')
	prompt ('[press return]')
	sys.exit (1)
def addpath (path):
	path = str (path)
	parts = os.environ['PATH'].split (os.path.pathsep)
	if not path in parts:
		parts.insert (0, path)
		os.environ['PATH'] = os.path.pathsep.join (parts)
def checkprop (homedir):
	replaces = [
		'jdbc.url=jdbc:mysql://localhost/openemm?useUnicode=yes&characterEncoding=UTF-8&useOldAliasMetadataBehavior=true',
		'system.script_logdir=var\\log',
		'system.upload_archive=var\\tmp',
		'system.attachment_archive=var\\tmp',
		'system.logdir=var\\log',
		'system.upload=var\\tmp',
		'log4j.appender.LOGFILE.File=var\\log\\emm_axis.log',
		'log4j.appender.STRUTSLOG.File=var\\log\\emm_struts.log',
		'mailgun.ini.maildir=var\\spool\\ADMIN',
		'mailgun.ini.metadir=var\\spool\\META',
		'mailgun.ini.xmlback=bin\\xmlback.exe',
		'mailgun.ini.account_logfile=var\\spool\\log\\account.log',
		'mailgun.ini.bounce_logfile=var\\spool\\log\\extbounce.log',
		'plugins.home=plugins'
	]
	ignores = [
		'system.url',
		'system.updateserver',
		'ecs.server.url'
	]
	rplc = {}
	for replace in replaces:
		parts = replace.split ('=', 1)
		rplc[parts[0].strip ()] = replace.replace ('\\', '\\\\') + '\n'
	for webapp in os.path.sep.join ([homedir, 'webapps', 'openemm']), os.path.sep.join ([homedir, 'webapps', 'openemm-ws']):
		prop = os.path.sep.join ([webapp, 'WEB-INF', 'classes', 'emm.properties'])
		save = prop + '.orig'
		fd = open (prop)
		content = fd.readlines ()
		fd.close ()
		ncontent = []
		changed = False
		for line in content:
			if line[0] != '#':
				parts = line.split ('=', 1)
				if len (parts) == 2:
					if rplc.has_key (parts[0]):
						nline = rplc[parts[0]]
						if nline != line:
							line = nline
							changed = True
					elif not parts[0] in ignores:
						if '/' in line:
							error ('Found possible invalid entry in %s: %s' % (prop, line))
			ncontent.append (line)
		if changed:
			try:
				os.rename (prop, save)
			except (WindowsError, OSError):
				pass
			fd = open (prop, 'w')
			fd.write (''.join (ncontent))
			fd.close ()
		log4j = os.path.sep.join ([webapp, 'WEB-INF', 'classes', 'log4j.properties'])
		log4jold = log4j + '.orig'
		fd = open (log4j)
		content = fd.readlines ()
		fd.close ()
		ncontent = []
		changed = False
		for line in content:
			if not line.startswith ('#'):
				for (old, new) in [('log/openemm', 'var/log/log4j-openemm.log')]:
					if old in line:
						line = line.replace (old, new)
						changed = True
			ncontent.append (line)
		if changed:
			try:
				os.rename (log4j, log4jold)
			except (WindowsError, OSError):
				pass
			fd = open (log4j, 'w')
			fd.write (''.join (ncontent))
			fd.close ()

def checkpaths (home):
	required = ['var', 'var\\tmp', 'temp']
	for path in required:
		fpath = os.path.sep.join ([home, path])
		if not os.path.isdir (fpath):
			try:
				os.mkdir (fpath)
			except (WindowsError, OSError), e:
				error (str (e))
def checksetenv (home):
	lpath = os.path.sep.join ([home, 'webapps', 'openemm', 'WEB-INF', 'lib'])
	if os.path.isdir (lpath):
		cp = []
		for fname in os.listdir (lpath):
			if fname.lower ().startswith ('mysql') and fname.lower.endswith ('.jar'):
				cp.append (os.path.sep.join ([lpath, cp]))
	sepath = os.path.sep.join ([home, 'bin', 'setenv.bat'])
	if cp:
		content = 'set "CLASSPATH=%s"\n' % (os.path.pathsep.join (cp))
		fd = open (content, 'wt')
		fd.write (content)
		fd.close ()
	else:
		try:
			os.unlink (sepath)
		except OSError:
			pass

def checkserverxml (home):
	path = os.path.sep.join ([home, 'conf', 'server.xml'])
	if os.path.isfile (path):
		fd = open (path, 'r')
		content = fd.read ()
		fd.close ()
		ncontent = content.replace ('/home/openemm', home.replace (os.path.sep, '/'))
		if ncontent != content:
			fd = open (path, 'w')
			fd.write (ncontent)
			fd.close ()
#
show ('Starting up .. ')
try:
	homedrive = os.environ['HOMEDRIVE']
except KeyError:
	homedrive = 'C:'
home = homedrive + os.path.sep + 'OpenEMM'
if not os.path.isdir (home):
	guess = None
	for disk in 'CDEFGHIJKLMNOPQRSTUVWXYZ':
		temp = disk + ':' + os.path.sep + 'OpenEMM'
		if os.path.isdir (temp):
			guess = temp
			break
	if guess is None:
		error ('Failed to find homedir "%s"' % home)
	home = guess
show ('home is %s .. ' % home)
checkprop (home)
checkpaths (home)
checkserverxml (home)
#
os.environ['HOME'] = home
binhome = home + os.path.sep + 'bin'
addpath (binhome)
schome = binhome + os.path.sep + 'scripts'
os.environ['PYTHONPATH'] = schome
if not schome in sys.path:
	sys.path.append (schome)
os.environ['LC_ALL'] = 'C'
os.environ['LANG'] = 'en_US.ISO8859_1'
os.environ['NLS_LANG'] = 'american_america.UTF8'

import	agn
agn.require ('2.0.0')
show ('found codebase .. ')
#
# Check for working database
if not 'DBase' in dir (agn):
	error ('No database module found')
#
# add python to path
addpath (agn.pythonpath)
#
# find jdk
jdkkey = r'SOFTWARE\JavaSoft\Java Development Kit'
version = agn.winregFind (jdkkey, 'CurrentVersion')
if version is None:
	error ('JDK not found')
javahome = agn.winregFind (jdkkey + '\\' + version, 'JavaHome')
addpath (javahome + os.path.sep + 'bin')
os.environ['JAVA_HOME'] = javahome
os.environ['JAVA_OPTIONS'] = '-Xms256m -Xmx512m -XX:MaxPermSize=256m -Xss256k'
#
# find tomcat
if tomcathome is None:
	for fname in sorted ([_f for _f in os.listdir (homedrive + os.path.sep) if _f.startswith ('apache-tomcat-6')]):
		tomcathome = os.path.sep.join ([homedrive, fname])
	if tomcathome is None:
		error ('Tomcat 6.x not found')
addpath (tomcathome + os.path.sep + 'bin')
os.environ['CATALINA_HOME'] = tomcathome
os.environ['CATALINA_BASE'] = home
#
# find mysql
mysqlhome = None
for version in ['5.0', '5.1']:
	mskey = r'SOFTWARE\MySQL AB\MySQL Server %s' % version
	mysqlhome = agn.winregFind (mskey, 'Location')
	if not mysqlhome is None:
		break
if mysqlhome is None:
	bkey = r'SOFTWARE\MySQL AB'
	try:
		for mskey in sorted ([_r for _r in agn.winregList (bkey) if type (_r) in types.StringTypes]):
			if 'server' in mskey.lower ():
				mysqlhome = agn.winregFind ('%s\\%s' % (bkey, mskey), 'Location')
	except TypeError:
		show ('warning: no MySQL found using reg.key %s, continue anyway' % bkey)
if not mysqlhome is None:
	addpath (mysqlhome + os.path.sep + 'bin')
#
# Optional commands
if len (sys.argv) > 1:
	os.chdir (home)
	versionTable = '__version'
	curversion = '2013'
	if sys.argv[1] == 'setup':
		def findSQL (prefix):
			for fname in ['%s.sql' % prefix, '%s-%s.sql' % (prefix, curversion)]:
				path = 'USR_SHARE\\%s' % fname
				if os.path.isfile (path):
					return path
			error ('Unable to find %s.sql (or %s-%s.sql)' % (prefix, prefix, curversion))
		show ('setup:\n')
		show ('Setup database, please enter the super user password defined during MySQL installation:\n')
		if os.system ('mysqladmin -u root -p create openemm'):
			error ('Failed to create database')
		show ('Database created, now setting up initial data, please enter again your databae super user password:\n')
		if os.system ('mysql -u root -p -e "source %s" openemm' % findSQL ('openemm')):
			error ('Failed to setup database')
		show ('Setup CMS database, please enter the super user password defined during MySQL installation:\n')
		if os.system ('mysqladmin -u root -p create openemm_cms'):
			error ('Failed to create CMS database')
		show ('CMS Database created, now setting up initial data, please enter again your databae super user password:\n')
		if os.system ('mysql -u root -p -e "source %s" openemm_cms' % findSQL ('openemm_cms')):
			error ('Failed to setup CMS database')
		show ('Database setup completed.\n')
		db = agn.DBase ()
		if not db is None:
			cursor = db.cursor ()
			if not cursor is None:
				cursor.execute ('CREATE TABLE %s (version varchar(50))' % versionTable)
				cursor.execute ('INSERT INTO %s VALUES (:version)' % versionTable, {'version': curversion})
				cursor.sync ()
				cursor.close ()
			db.close ()
	if sys.argv[1] in ('setup', 'config'):
		db = agn.DBase ()
		if not db:
			error ('Failed to setup database connection')
		i = db.cursor ()
		if not i:
			error ('Failed to connect to database')
		rdir = None
		mailloop = None
		for r in i.query ('SELECT rdir_domain, mailloop_domain FROM company_tbl WHERE company_id = 1'):
			rdir = r[0]
			mailloop = r[1]
		if sys.argv[1] == 'config':
			show ('config:\n')
		if rdir is None: rdir = ''
		nrdir = prompt ('Enter redirection domain [%s]: ' % rdir)
		if not nrdir: nrdir = rdir
		if mailloop is None: mailloop = ''
		nmailloop = prompt ('Enter mailloop domain [%s]: ' % mailloop)
		if not nmailloop: nmailloop = mailloop
		if nrdir != rdir or nmailloop != mailloop:
			i.update ('UPDATE company_tbl SET rdir_domain = :rdir, mailloop_domain = :mailloop WHERE company_id = 1',
				  { 'rdir': nrdir, 'mailloop': nmailloop })
			db.commit ()
		i.close ()
		db.close ()
		sfname = 'conf' + os.path.sep + 'smart-relay'
		try:
			fd = open (sfname)
			sr = fd.read ().strip ()
			fd.close ()
		except IOError:
			sr = ''
		show ('Smart mail relay - optional parameter. Specifiy this, if you want to send\n')
		show ('all your outgoing mail via one deticated server (e.g. your ISP mail server.)\n')
		show ('You may add login information in the form <username>:<password>@<relay> if\n')
		show ('the smart relay requires authentication.\n')
		nsr = prompt ('Enter smart relay (or just - to remove existing one) [%s]: ' % sr)
		if nsr:
			if nsr == '-':
				try:
					os.unlink (sfname)
				except (WindowsError, OSError):
					pass
			elif nsr != sr:
				fd = open (sfname, 'w')
				fd.write ('%s\n' % nsr)
				fd.close ()
		prompt ('Congratulations, %s completed! [return] ' % sys.argv[1])
	elif sys.argv[1] == 'update':
		show ('update:\n')
		db = agn.DBase ()
		if not db:
			error ('Failed to setup database connection')
		i = db.cursor ()
		if not i:
			error ('Failed to connect to database')
		found = False
		for r in i.query ('SHOW TABLES'):
			if r[0] == versionTable:
				found = True
				break
		if not found:
			version = '5.1.0'
			tempfile = 'version.sql'
			fd = open (tempfile, 'w')
			fd.write ('CREATE TABLE %s (version varchar(50));\n' % versionTable)
			fd.close ()
			show ('Database update, please enter your database super user password now\n')
			st = os.system ('mysql -u root -p -e "source %s" openemm' % tempfile)
			try:
				os.unlink (tempfile)
			except (WindowsError, OSError):
				pass
			if st:
				error ('Failed to setup database')
			i.update ('INSERT INTO %s VALUES (:version)' % versionTable, {'version': version })
			db.commit ()
		else:
			version = None
			for r in i.query ('SELECT version FROM %s' % versionTable):
				version = r[0]
			if version is None:
				error ('Found version table, but no content in table')
			elif version == '5.1.0':
				version = '5.1.1'
			elif version == '5.1.1':
				version = '5.3.0'
		ans = prompt ('It looks like your previous version is "%s", is this correct? [no] ' % version)
		if not ans or not ans[0] in 'Yy':
			error ('Version conflict!')
		if version[0] == '5':
			found = False
			for r in i.query ('SHOW DATABASES'):
				if r[0] and r[0] == 'openemm_cms':
					found = True
					break
			if not found:
				show ('Setup CMS database, please enter the super user password defined during MySQL installation:\n')
				if os.system ('mysqladmin -u root -p create openemm_cms'):
					error ('Failed to create CMS database')
				show ('CMS Database created, now setting up initial data, please enter again your databae super user password:\n')
				if os.system ('mysql -u root -p -e "source USR_SHARE\\openemm_cms.sql" openemm_cms'):
					error ('Failed to setup CMS database')
		updates = []
		for fname in os.listdir ('USR_SHARE'):
			if fname.endswith ('.usql'):
				base = fname[:-5]
				parts = base.split ('-')
				if len (parts) == 2:
					updates.append ([parts[0], parts[1], 'USR_SHARE\\%s' % fname])
		seen = []
		while version != curversion:
			found = False
			oldversion = version
			for upd in updates:
				if upd[0] == version and not upd[2] in seen:
					try:
						fd = open (upd[2])
						cont = fd.read ()
						fd.close ()
						isEmpty = (len (cont) == 0)
					except IOError:
						isEmpty = False
					if not isEmpty:
						show ('Database upgrade from %s to %s, please enter your super user password now\n' % (version, upd[1]))
						if os.system ('mysql -u root -p -e "source %s" openemm' % upd[2]):
							error ('Failed to update')
					else:
						show ('No database update from %s to %s required\n' % (version, upd[1]))
					version = upd[1]
					seen.append (upd[2])
					i.update ('UPDATE %s SET version = :version' % versionTable, {'version': version})
					db.commit ()
					found = True
					break
			if not found:
				error ('No update from %s to %s found' % (version, curversion))
			if oldversion == '5.3.2':
				i.close ()
				db.close ()
				dbfile = os.path.sep.join (['var', 'tmp', 'openemm.dump'])
				dbconv = os.path.sep.join (['var', 'tmp', 'openemm.conv'])
				show ('===========================================================================\n')
				show ('!! Please read and follow the next steps carefully to avoid loss of data !!\n')
				show ('Now forcing cleanup of the database, please enter your super user password now\n')
				if os.system ('mysqldump -aCceQ --lock-all-tables -u root -p -r %s openemm' % dbfile):
					error ('Failed to dump current database')
				try:
					fdi = open (dbfile, 'r')
					fdo = open (dbconv, 'w')
				except IOError, e:
					error ('Failed to open database convertion file %s %s' % (dbconv, `e.args`))
				fdo.write ('ALTER DATABASE openemm DEFAULT CHARACTER SET utf8;\n')
				for line in fdi.readlines ():
					line = line.replace (' character set utf8 collate utf8_unicode_ci', '')
					line = line.replace (' collate utf8_unicode_ci', '')
					line = line.replace ('DEFAULT CHARSET=latin1', 'DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci')
					fdo.write (line)
				fdo.close ()
				fdi.close ()
				show ('Now we remove and recreate the database and import the converted content.\n')
				show ('Please enter your super user database password each time, if asked for:\n')
				state = 0
				while state < 3:
					if state == 0:
						action = 'Drop database'
						command = 'mysqladmin -u root -p drop openemm'
					elif state == 1:
						action = 'Create database'
						command = 'mysqladmin -u root -p create openemm'
					elif state == 2:
						action = 'Import database'
						command = 'mysql -u root -p openemm < %s' % dbconv
					show ('--> %s:\n' % action)
					if os.system (command):
						show ('Command failed! If you have just mistyped your password, just try\n')
						show ('again, otherwise abort the update and fix the problem by hand.\n')
					else:
						state += 1
				try:
					os.unlink (dbconv)
				except (WindowsError, OSError):
					pass
				db = agn.DBase ()
				i = db.cursor ()
				show ('===========================================================================\n')
		i.close ()
		db.close ()
		show ('Update to version %s finished! You may start config.bat now to see\n' % version)
		prompt ('if there are some new things to setup [return] ')
	else:
		error ('Unknown option %s' % sys.argv[1])
	sys.exit (0)

db = agn.DBase ()
if not db:
	error ('Failed to setup database connection')
i = db.cursor ()
if not i:
	error ('Failed to connect to database')
i.close ()
db.close ()
show ('found database.\n')
#
# remove potential stale files
sessions = os.path.sep.join ([home, 'webapps', 'openemm', 'WEB-INF', 'sessions'])
fnames = [agn.winstopfile]
if os.path.isdir (sessions):
	for fname in os.listdir (sessions):
		fnames.append (sessions + os.path.sep + fname)
for fname in fnames:
	try:
		os.unlink (fname)
#		show ('Removed stale file %s.\n' % fname)
	except (WindowsError, OSError):
		pass
#
# change to home directory
os.chdir (home)
def pystart (cmd, param = None):
	args = [cmd]
	if param:
		args += param.split ()
	args.insert (0, agn.pythonbin)
	return os.spawnv (os.P_NOWAIT, args[0], args)

def tomcatexec (module, what):
	lpath = home + os.path.sep + 'var' + os.path.sep + 'log' + os.path.sep
	lout = lpath + module + '_stdout.log'
	lerr = lpath + module + '_stderr.log'
	cmd = os.path.sep.join ([tomcathome, 'bin', '%s.bat' % what])
	args = [cmd]
	env = os.environ.copy ()
	env['LANG'] = 'en_US.ISO8859_1'
	saveout = os.dup (1)
	saveerr = os.dup (2)
	os.close (1)
	os.close (2)
	os.open (lout, os.O_WRONLY | os.O_APPEND | os.O_CREAT, 0666)
	os.open (lerr, os.O_WRONLY | os.O_APPEND | os.O_CREAT, 0666)
	pid = os.spawnve (os.P_NOWAIT, args[0], args, env)
	os.close (1)
	os.close (2)
	os.dup (saveout)
	os.dup (saveerr)
	os.close (saveout)
	os.close (saveerr)
	return pid

def tomcatstart (module):
	return tomcatexec (module, 'startup')
def tomcatstop (module):
	return tomcatexec (module, 'shutdown')
p_upd = pystart (schome + os.path.sep + 'update.py', 'account bounce')
if p_upd == -1:
	error ('Failed to start update process')
p_dst = pystart (schome + os.path.sep + 'pickdist.py')
if p_dst == -1:
	error ('Failed to start pickdist process')
p_bav = pystart (schome + os.path.sep + 'bav-update.py')
if p_bav == -1:
	error ('Failed to start bav-update process')
p_sem = pystart (schome + os.path.sep + 'semu.py')
if p_sem == -1:
	error ('Failed to start semu process')
p_con = tomcatstart ('openemm')
if p_con == -1:
	error ('Failed to start openemm')
prompt ('Running, press return for termination: ')
tomcatstop ('openemm')
show ('Signal termination to enviroment\n')
open (agn.winstopfile, 'w').close ()
time.sleep (3)
prompt ('Finished, press [return] ')
show ('(window closes on final termination of all processes) ')
time.sleep (2)
