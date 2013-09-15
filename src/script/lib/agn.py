#	-*- mode: python; mode: fold -*-
#
"""

**********************************************************************************
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
Support routines for general and company specific purposes:
	class struct:     general empty class for temp. structured data
	class error:	  new version for general execption
	def chop:         removes trailing newlines
	def atoi:         converts a string to a numeric value (fault tolerant)
	def atob:         converts a string to a boolean value
	def numfmt:       converts a number to pretty printed version
	def validate:     validates an input string
	def filecount:    counts files matching a pattern in a directory
	def which:        finds program in path
	def mkpath:       creates a path from path components
	def fingerprint:  calculates a fingerprint from a file
	def toutf8:       converts input string to UTF-8 encoding
	def fromutf8:     converts UTF-8 encoded strings to unicode
	def msgn:         output a message on stdout, if verbose ist set
	def msgcnt:       output a number for progress
	def msgfcnt:      output final number
	def msg:          output a message with trailing newline on stdout,
	                  if verbose is set
	def err:          output a message on stderr
	def transformSQLwildcard: transform a SQL wildcard string to a regexp
	def compileSQLwildcard: transform and compile a SQL wildcard
	class UserStatus: describes available user stati

	class Backlog:    support class for enabling backlogging
	def loglevelName: returns a string representation of a log level
	def loglevelValue:returns a numeric value for a log level
	def logfilename:  creates the filename to write logfiles to
	def logappend:    copies directly to logfile
	def log:          writes an entry to the logfile
	def logexc:       writes details of exception to logfile
	def mark:         writes a mark to the logfile, if nothing had been
	                  written for a descent time
	def backlogEnable: switch backlogging on
	def backlogDisable: switch backlogging off
	def backlogRestart: flush all recorded entries and restart with
	                    a clean buffer
	def backlogSuspend: suspend storing entries to backlog
	def backlogResume: resume storing entries to backlog
	def backlogSave:  write current backlog to logfile

	def lock:         creates a lock for this running process
	def unlock:       removes the lock
	def signallock:   send signal to process owing a lockfile

	def createPath:   creates a full path with all missing subdirectories
	def mkArchiveDirectory:  creates a subdirectory for daily archives
	class Filepos:    line by line file reading with remembering th
	                  the file position
	
	def die:          terminate the program removing aquired lock, if
	                  neccessary
	rip = die         alias for die
	
	class Messenger:  wrapper for subprocess named queueing
	def mcomm:        return bound communicator
	def mcreate:      create a new global channel
	def msend:        send using global channels
	def manser:       answer to global channel request
	def mrecv:        receiver message through global channels
	
	class Parallel:   wrapper for subprocessing, can also be used
	                  externally
	def pfork:        starts a method as subprocess
	def palive:       returns tuple (alive, active) in numbers of
	                  processes
	def pwait:        joins all  subprocess(es)
	def pready:       joins max. one process, if one has terminated
	def pterm:        terminates subprocesses
	
	def mailsend:     send a mail using SMTP
	class UID:         handles parsing and validation of UIDs
	class METAFile:    generic class to parse XML meta file names

	class DBCore:      an abstract core database driver class
	class DBCursor:    a cursor instance for database access
	class DBMySQL:     MySQL driver and cursor
	class DBMySQLCursor:
	
	class Datasource:  easier handling for datasource IDs
	class MessageCatalog: message catalog for templating
	class Template:    simple templating system

"""
#
# Imports, Constants and global Variables
#{{{
import	sys, os, types, errno, stat, signal
import	time, re, socket, subprocess, collections
try:
	import	hashlib
	
	hash_md5 = hashlib.md5
	hash_sha1 = hashlib.sha1
except ImportError:
	import	md5, sha
	
	hash_md5 = md5.md5
	hash_sha1 = sha.sha
import	platform, traceback, codecs
import	smtplib
#
changelog = [
	('2.0.0', '2008-04-18', 'Initial version of redesigned code', 'ud@agnitas.de'),
	('2.0.1', '2008-07-01', 'Added autocommitment', 'ud@agnitas.de'),
	('2.0.3', '2008-07-31', 'Template with inclusing support', 'ud@agnitas.de'),
	('2.0.4', '2008-08-07', 'Added numfmt', 'ud@agnitas.de'),
	('2.0.5', '2008-08-11', 'Added validate', 'ud@agnitas.de'),
	('2.0.8', '2008-11-04', 'Added customLogfilename', 'ud@agnitas.de'),

	('2.0.9', '2008-11-13', 'Added some quirks for windows startup', 'ud@agnitas.de'),
	('2.1.3', '2009-01-13', 'Fixed bug in template including', 'ud@agnitas.de'),
	('2.1.4', '2009-01-15', 'Add MessageCatalog for templating', 'ud@agnitas.de'),

	('2.1.5', '2009-01-23', 'Extended parsing of UIDs', 'ud@agnitas.de'),
	('2.1.7', '2009-04-29', 'Added createPath', 'ud@agnitas.de'),
	('2.1.8', '2009-05-06', 'Added logexc', 'ud@agnitas.de'),

	('2.2.0', '2009-05-06', 'Moved configuration to emm.properties', 'ud@agnitas.de'),
	('2.2.1', '2009-05-18', 'Added @deprecated decorator', 'ud@agnitas.de'),
	('2.2.3', '2009-07-28', 'validate: Added support for keyword reason', 'ud@agnitas.de'),

	('2.2.4', '2009-10-26', 'Read database paramter for CMS', 'ud@agnitas.de'),
	('2.2.5', '2009-11-09', 'Extend class struct with __init__ method', 'ud@agnitas.de'),
	('2.2.6', '2009-11-17', 'Minor bugfix in createPath on relative pathes', 'ud@agnitas.de'),
	('2.3.0', '2010-03-09', 'Added logging for database interface', 'ud@agnitas.de'),
	('2.4.0', '2010-04-21', 'Added class METAFile', 'ud@agnitas.de'),
	('2.6.0', '2010-12-29', 'Removed obsolete customLogfilename', 'ud@agnitas.de'),

	('2.7.2', '2011-03-14', 'Changed property application name from core to openemm', 'ud@agnitas.de'),
	('2.7.3', '2011-03-15', 'Changed toutf8 for better handling of passed charset', 'ud@agnitas.de'),
	('2.7.4', '2011-03-29', 'Enhanced locking to treat empty lock files as invalid', 'ud@agnitas.de'),
	('2.8.2', '2011-08-16', 'Reworked database interface', 'ud@agnitas.de'),
	('2.8.3', '2011-08-18', 'Added simple parallel infrastructure using pfork, pwait and pterm', 'ud@agnitas.de'),
	('2.8.4', '2011-08-19', 'Added atoi, a fault tolerant variant of int(...)', 'ud@agnitas.de'),
	('2.8.5', '2011-08-22', 'Enhanced simple parallel infrastructure', 'ud@agnitas.de'),
	('2.8.6', '2011-10-18', 'Added simple multiprocessing based messenger system', 'ud@agnitas.de'),
	('2.9.0', '2012-04-05', 'Added keyword query results', 'ud@agnitas.de'),
	('2.9.8', '2012-07-09', 'Revised database interface', 'ud@agnitas.de'),
]
version = (changelog[-1][0], '2013-09-06 21:01:29 CEST', 'ma')
#
verbose = 1
system = platform.system ().lower ()
host = platform.node ()
if host.find ('.') != -1:
	host = host.split ('.')[0]

if system == 'windows':
	import	_winreg
	
	def winregFind (key, qkey):
		try:
			value = None
			rkey = _winreg.OpenKey (_winreg.HKEY_LOCAL_MACHINE, key)
			n = 0
			while value is None:
				temp = _winreg.EnumValue (rkey, n)
				if qkey is None or qkey == temp[0]:
					value = temp[1]
				n += 1
			rkey.Close ()
		except WindowsError:
			value = None
		return value
	
	def winregList (key):
		rc = []
		try:
			rkey = _winreg.OpenKey (_winreg.HKEY_LOCAL_MACHINE, key)
			mode = 0
			while mode < 2:
				n = 0
				try:
					while True:
						if mode == 0:
							rc.append (_winreg.EnumKey (rkey, n))
						else:
							rc.append (_winreg.EnumValue (rkey, n))
						n += 1
				except WindowsError:
					mode += 1
		except WindowsError:
			rc = None
		return rc

	for key in [r'SOFTWARE\Classes\Python.File\shell\open\command',
		    r'SOFTWARE\Classes\Applications\python.exe\shell\open\command',
		    r'SOFTWARE\Classes\py_auto_file\shell\open\command']:
		pythonbin = winregFind (key, None)
		if not pythonbin is None:
			break
	if pythonbin is None:
		pythonbin = r'C:\Python25\python.exe'
		if not os.path.isfile (pythonbin):
			for path in [_d for _d in os.listdir ('C:\\') if _d.lower ().startswith ('python2')]:
				pythonbin = 'C:\\%s\\python.exe' %  path
	else:
		pythonbin = pythonbin.split ()[0]
		if len (pythonbin) > 1 and pythonbin[0] == '"' and pythonbin[-1] == '"':
			pythonbin = pythonbin[1:-1]
	pythonpath = os.path.dirname (pythonbin)
	try:
		home = os.environ['HOMEDRIVE'] + '\\OpenEMM'
	except KeyError:
		home = 'C:\\OpenEMM'
	os.environ['HOME'] = home
	iswin = True
	
	winstopfile = os.path.sep.join ([home, 'var', 'run', 'openemm.stop'])
	def winstop ():
		return os.path.isfile (winstopfile)
else:
	iswin = False
#
try:
	base = os.environ['HOME']
except KeyError:
	base = '.'
scripts = os.path.sep.join ([base, 'bin', 'scripts'])
if not scripts in sys.path:
	sys.path.insert (0, scripts)
del scripts


class _Properties:
	fnames = [os.path.sep.join ([base, 'webapps', 'openemm', 'WEB-INF', 'classes', 'emm.properties']),
		  os.path.sep.join ([base, 'webapps', 'openemm', 'WEB-INF', 'classes', 'cms.properties'])]
	def __init__ (self):
		self.props = {}
		for fname in self.fnames:
			try:
				fd = open (fname, 'r')
				for line in fd:
					while line.endswith ('\n') or line.endswith ('\r'):
						line = line[:-1]
					line = line.lstrip ()
					if not line or line.startswith ('#'):
						continue
					elem = line.split ('=', 1)
					if len (elem) != 2:
						continue
					var = elem[0].strip ().lower ()
					val = elem[1].lstrip ()
					self.props[var] = val
				fd.close ()
			except IOError, e:
				pass
		for (eid, keyUrl, keyUsername, keyPassword, defaults) in [
			('emm', 'jdbc.url', 'jdbc.username', 'jdbc.password', ['localhost', 'agnitas', 'openemm', 'openemm']),
			('cms', 'cmsdb.url', 'cmsdb.username', 'cmsdb.password', ['localhost', 'agnitas', 'openemm', 'openemm_cms'])
			]:
			try:
				url = self[keyUrl]
				usr = self[keyUsername]
				pwd = self[keyPassword]
				m = re.search ('[^/]+://([^/]+)/([^?]+)', url)
				if not m is None:
					(hst, dbn) = m.groups ()
				else:
					hst = None
					dbn = None
			except KeyError:
				url = None
				usr = None
				pwd = None
				hst = None
				dbn = None
			if not 'python.%s.dbhost' % eid in self.props:
				self['python.%s.dbhost' % eid] = hst and hst or defaults[0]
			if not 'python.%s.dbuser' % eid in self.props:
				self['python.%s.dbuser' % eid] = usr and usr or defaults[1]
			if not 'python.%s.dbpass' % eid in self.props:
				self['python.%s.dbpass' % eid] = pwd and pwd or defaults[2]
			if not 'python.%s.database' % eid in self.props:
				self['python.%s.database' % eid] = dbn and dbn or defaults[3]
	
	def has_key (self, var):
		return self.props.has_key (var.lower ())
		
	def __getitem__ (self, var):
		rc = None
		seen = []
		var = var.lower ()
		while rc is None:
			val = self.props[var]
			seen.append (var)
			if val.startswith ('::'):
				var = val[2:].lower ()
				if var in seen:
					seen.append ('***%s***' % var)
					raise ValueError ('circular dependencies detected: %s' % ' -> '.join (seen))
			else:
				rc = val
		return rc
	
	def __setitem__ (self, var, val):
		self.props[var.lower ()] = val
	
	def __len__ (self):
		return len (self.props)
properties = _Properties ()
#}}}
#
# Support routines
#
#{{{
class struct:
	"""class struct:

General empty class as placeholder for temp. structured data"""
	def __init__ (self, **kws):
		for (var, val) in kws.items ():
			if not var.startswith ('_'):
				self.__dict__[var] = val

	def __str__ (self):
		return '[%s]' % ', '.join (['%s=%r' % (_n, self.__dict__[_n]) for _n in self.__dict__])

class error (Exception):
	"""class error (Exception):

This is a general exception thrown by this module."""
	def __init__ (self, message = None):
		Exception.__init__ (self, message)
		self.msg = message

def __require (checkversion, srcversion, modulename):
	for (c, v) in zip (checkversion.split ('.'), srcversion[0].split ('.')):
		cv = int (c)
		vv = int (v)
		if cv > vv:
			raise error ('%s: Version too low, require at least %s, found %s' % (modulename, checkversion, srcversion[0]))
		elif cv < vv:
			break
	if checkversion.split ('.')[0] != srcversion[0].split ('.')[0]:
		raise error ('%s: Major version mismatch, %s is required, %s is available' % (modulename, checkversion.split ('.')[0], srcversion[0].split ('.')[0]))

def require (checkversion ):
	__require (checkversion, version, 'agn')

def chop (s):
	"""def chop (s):

removes any trailing LFs and CRs."""
	while len (s) > 0 and s[-1] in '\r\n':
		s = s[:-1]
	return s

def atoi (s, base = 10, dflt = 0):
	"""def atoi (s, base = 10, dflt = 0):

parses input parameter as numeric value, use default if
it is not parsable."""
	if type (s) in (int, long):
		return s
	try:
		rc = int (s, base)
	except (ValueError, TypeError):
		rc = dflt
	return rc

def atob (s):
	"""def atob (s):

tries to interpret the incoming string as a boolean value."""
	if type (s) is bool:
		return s
	if s and len (s) > 0 and s[0] in [ '1', 'T', 't', 'Y', 'y', '+' ]:
		return True
	return False

def numfmt (n, separator = '.'):
	"""def numfmt (n, separator = '.'):

convert the number to a more readble form using separator."""
	if n == 0:
		return '0'
	if n < 0:
		prefix = '-'
		n = -n
	else:
		prefix = ''
	rc = ''
	while n > 0:
		if n >= 1000:
			rc = '%s%03d%s' % (separator, n % 1000, rc)
		else:
			rc = '%d%s' % (n, rc)
		n /= 1000
	return prefix + rc

def validate (s, pattern, *funcs, **kw):
	"""def validate (s, pattern, *funcs, **kw):

pattern is a regular expression where s is matched against.
Each group element is validated against a function found in funcs."""
	if not pattern.startswith ('^'):
		pattern = '^' + pattern
	if not pattern.endswith ('$') or pattern.endswith ('\\$'):
		pattern += '$'
	try:
		reflags = kw['flags']
	except KeyError:
		reflags = 0
	try:
		pat = re.compile (pattern, reflags)
	except Exception, e:
		raise error ('Failed to compile regular expression "%s": %s' % (pattern, e.args[0]))
	mtch = pat.match (s)
	if mtch is None:
		try:
			reason = kw['reason']
		except KeyError:
			reason = 'No match'
		raise error (reason)
	if len (funcs) > 0:
		flen = len (funcs)
		n = 0
		report = []
		grps = mtch.groups ()
		if not grps:
			grps = [mtch.group ()]
		for elem in grps:
			if n < flen:
				if type (funcs[n]) in (types.ListType, types.TupleType):
					(func, reason) = funcs[n]
				else:
					func = funcs[n]
					reason = '%r' % func
				if not func (elem):
					report.append ('Failed in group #%d: %s' % (n + 1, reason))
			n += 1
		if report:
			raise error ('Validation failed: %s' % ', '.join (report))

def filecount (directory, pattern):
	"""def filecount (directory, pattern):

counts the files in dir which are matching the regular expression
in pattern."""
	pat = re.compile (pattern)
	dirlist = os.listdir (directory)
	count = 0
	for fname in dirlist:
		if pat.search (fname):
			count += 1
	return count

def which (program):
	"""def which (program):

finds 'program' in the $PATH enviroment, returns None, if not available."""
	rc = None
	try:
		paths = os.environ['PATH'].split (':')
	except KeyError:
		paths = []
	for path in paths:
		if path:
			p = os.path.sep.join ([path, program])
		else:
			p = program
		if os.access (p, os.X_OK):
			rc = p
			break
	return rc

def mkpath (*parts, **opts):
	"""def mkpath (*parts, **opts):

create a valid pathname from the elements"""
	try:
		absolute = opts['absolute']
	except KeyError:
		absolute = False
	rc = os.path.sep.join (parts)
	if absolute and not rc.startswith (os.path.sep):
		rc = os.path.sep + rc

	if iswin:
		try:
			drive = opts['drive']
			if len (drive) == 1:
				rc += drive + ':' + rc
		except KeyError:
			pass
	return os.path.normpath (rc)

def fingerprint (fname):
	"""def fingerprint (fname):

calculates a MD5 hashvalue (a fingerprint) of a given file."""
	fp = hash_md5 ()
	fd = open (fname, 'rb')
	while 1:
		chunk = fd.read (65536)
		if chunk == '':
			break
		fp.update (chunk)
	fd.close ()
	return fp.hexdigest ()

__encoder = codecs.getencoder ('UTF-8')
def toutf8 (s, charset = 'ISO-8859-1'):
	"""def toutf8 (s, [charset]):

convert unicode (or string with charset information) inputstring
to UTF-8 string."""
	if type (s) == types.StringType:
		if charset is None:
			s = unicode (s)
		else:
			s = unicode (s, charset)
	return __encoder (s)[0]
def fromutf8 (s):
	"""def fromutf8 (s):

converts an UTF-8 coded string to a unicode string."""
	return unicode (s, 'UTF-8')
def msgn (s):
	"""def msgn (s):

prints s to stdout, if the module variable verbose is not equal to 0."""
	global	verbose

	if verbose:
		sys.stdout.write (s)
		sys.stdout.flush ()
def msgcnt (cnt):
	"""def msgcnt (cnt):

prints a counter to stdout. If the number has more than eight digits, this
function will fail. msgn() is used for the output itself."""
	msgn ('%8d\b\b\b\b\b\b\b\b' % cnt)
def msgfcnt (cnt):
	msgn ('%8d' % cnt)
def msg (s):
	"""def msg (s):

prints s with a newline appended to stdout. msgn() is used for the output
itself."""
	msgn (s + '\n')
def err (s):
	"""def err (s):

prints s with a newline appended to stderr."""
	sys.stderr.write (s + '\n')
	sys.stderr.flush ()

def transformSQLwildcard (s):
	r = ''
	needFinal = True
	for ch in s:
		needFinal = True
		if ch in '$^*?()+[{]}|\\.':
			r += '\\%s' % ch
		elif ch == '%':
			r += '.*'
			needFinal = False
		elif ch == '_':
			r += '.'
		else:
			r += ch
	if needFinal:
		r += '$'
	return r
def compileSQLwildcard (s, reFlags = 0):
	return re.compile (transformSQLwildcard (s), reFlags)

class UserStatus:
	UNSET = 0
	ACTIVE = 1
	BOUNCE = 2
	ADMOUT = 3
	OPTOUT = 4
	WAITCONFIRM = 5
	BLACKLIST = 6
	SUSPEND = 7
	stati = { 'unset': UNSET,
		  'active': ACTIVE,
		  'bounce': BOUNCE,
		  'admout': ADMOUT,
		  'optout': OPTOUT,
		  'waitconfirm': WAITCONFIRM,
		  'blacklist': BLACKLIST,
		  'suspend': SUSPEND
		}
	rstati = None
	
	def __init__ (self):
		if self.rstati is None:
			self.rstati = {}
			for (var, val) in self.stati.items ():
				self.rstati[val] = var
	
	def findStatus (self, st, dflt = None):
		rc = None
		if type (st) in types.StringTypes:
			try:
				rc = self.stati[st]
			except KeyError:
				rc = None
		if rc is None:
			try:
				rc = int (st)
			except ValueError:
				rc = None
		if rc is None:
			rc = dflt
		return rc
	
	def findStatusName (self, stid):
		try:
			rc = self.rstati[stid]
		except KeyError:
			rc = None
		return rc
#}}}
#
# 1.) Logging
#
#{{{
class Backlog:
	def __init__ (self, maxcount, level):
		self.maxcount = maxcount
		self.level = level
		self.backlog = []
		self.count = 0
		self.isSuspended = False
		self.asave = None
	
	def add (self, s):
		if not self.isSuspended and self.maxcount:
			if self.maxcount > 0 and self.count >= self.maxcount:
				self.backlog.pop (0)
			else:
				self.count += 1
			self.backlog.append (s)
	
	def suspend (self):
		self.isSuspended = True
	
	def resume (self):
		self.isSuspended = False
	
	def restart (self):
		self.backlog = []
		self.count = 0
		self.isSuspended = False
	
	def save (self):
		if self.count > 0:
			self.backlog.insert (0, '-------------------- BEGIN BACKLOG --------------------\n')
			self.backlog.append ('--------------------  END BACKLOG  --------------------\n')
			logappend (self.backlog)
			self.backlog = []
			self.count = 0
	
	def autosave (self, level):
		if not self.asave is None and level in self.asave:
			return True
		return False
	
	def addLevelForAutosave (self, level):
		if self.asave is None:
			self.asave = [level]
		elif not level in self.asave:
			self.asave.append (level)
	
	def removeLevelForAutosave (self, level):
		if not self.asave is None and level in self.asave:
			self.asave.remove (level)
			if not self.asave:
				self.asave = None
	
	def clearLevelForAutosave (self):
		self.asave = None
	
	def setLevelForAutosave (self, levels):
		if levels:
			self.asave = levels
		else:
			self.asave = None

LV_NONE = 0
LV_FATAL = 1
LV_REPORT = 2
LV_ERROR = 3
LV_WARNING = 4
LV_NOTICE = 5
LV_INFO = 6
LV_VERBOSE = 7
LV_DEBUG = 8
loglevel = LV_WARNING
logtable = {	'FATAL': LV_FATAL,
		'REPORT': LV_REPORT,
		'ERROR': LV_ERROR,
		'WARNING': LV_WARNING,
		'NOTICE': LV_NOTICE,
		'INFO': LV_INFO,
		'VERBOSE': LV_VERBOSE,
		'DEBUG': LV_DEBUG
}
for __tmp in logtable.keys ():
	logtable[logtable[__tmp]] = __tmp
loghost = host
logname = None
logpath = None
outlevel = LV_FATAL
outstream = None
backlog = None
try:
	logpath = os.environ['LOG_HOME']
except KeyError:
	logpath = mkpath (base, 'var', 'log')
if len (sys.argv) > 0:
	logname = os.path.basename (sys.argv[0])
	(basename, extension) = os.path.splitext (logname)
	if extension.lower ().startswith ('.py'):
		logname = basename
if not logname:
	logname = 'unset'
loglast = 0
#
def loglevelName (lvl):
	"""def loglevelName (lvl):

returns a name for a numeric loglevel."""
	try:
		return logtable[lvl]
	except KeyError:
		return str (lvl)

def loglevelValue (lvlname):
	"""def loglevelValue (lvlname):

return the numeric value for a loglevel."""
	name = lvlname.upper ().strip ()
	try:
		return logtable[name]
	except KeyError:
		for k in [_k for _k in logtable.keys () if type (_k) == types.StringType]:
			if k.startswith (name):
				return logtable[k]
	raise error ('Unknown log level name "%s"' % lvlname)

def logfilename (name = None, epoch = None):
	global	logname, logpath, loghost
	
	if name is None:
		name = logname
	if epoch is None:
		epoch = time.time ()
	now = time.localtime (epoch)
	return mkpath (logpath, '%04d%02d%02d-%s-%s.log' % (now[0], now[1], now[2], loghost, name))

def logappend (s):
	global	loglast

	fname = logfilename ()
	try:
		fd = open (fname, 'a')
		if type (s) in types.StringTypes:
			fd.write (s)
		elif type (s) in (types.ListType, types.TupleType):
			for l in s:
				fd.write (l)
		else:
			fd.write (str (s) + '\n')
		fd.close ()
		loglast = int (time.time ())
	except Exception, e:
		err ('LOGFILE write failed[%r, %r]: %r' % (type (e), e.args, s))

def log (lvl, ident, s):
	global	loglevel, logname, backlog

	if not backlog is None and backlog.autosave (lvl):
		backlog.save ()
		backlogIgnore = True
	else:
		backlogIgnore = False
	if lvl <= loglevel or \
	   (lvl <= outlevel and not outstream is None) or \
	   (not backlog is None and lvl <= backlog.level):
		if not ident:
			ident = logname
		now = time.localtime (time.time ())
		lstr = '[%02d.%02d.%04d  %02d:%02d:%02d] %d %s/%s: %s\n' % (now[2], now[1], now[0], now[3], now[4], now[5], os.getpid (), loglevelName (lvl), ident, s)
		if lvl <= loglevel:
			logappend (lstr)
		else:
			backlogIgnore = False
		if lvl <= outlevel and not outstream is None:
			outstream.write (lstr)
			outstream.flush ()
		if not backlogIgnore and not backlog is None and lvl <= backlog.level:
			backlog.add (lstr)

def logexc (lvl, ident, s = None):
	exc = sys.exc_info ()
	if not s is None:
		log (lvl, ident, s)
	if not None in exc:
		(typ, value, tb) = exc
		for l in [_l for _l in ('\n'.join (traceback.format_exception (typ, value, tb))).split ('\n') if _l]:
			log (lvl, ident, l)
		del tb

def mark (lvl, ident, dur = 60):
	global	loglast
	
	now = int (time.time ())
	if loglast + dur * 60 < now:
		log (lvl, ident, '-- MARK --')

def level_name (lvl):
	return loglevelName (lvl)

def backlogEnable (maxcount = 100, level = LV_DEBUG):
	global	backlog
	
	if maxcount == 0:
		backlog = None
	else:
		backlog = Backlog (maxcount, level)

def backlogDisable ():
	global	backlog
	
	backlog = None

def backlogRestart ():
	global	backlog
	
	if not backlog is None:
		backlog.restart ()

def backlogSave ():
	global	backlog
	
	if not backlog is None:
		backlog.save ()

def backlogSuspend ():
	global	backlog
	
	if not backlog is None:
		backlog.suspend ()

def backlogResume ():
	global	backlog
	
	if not backlog is None:
		backlog.resume ()

def logExcept (typ, value, tb):
	ep = traceback.format_exception (typ, value, tb)
	rc = 'CAUGHT EXCEPTION:\n'
	for p in ep:
		rc += p
	backlogSave ()
	log (LV_FATAL, 'except', rc)
	err (rc)
sys.excepthook = logExcept
#}}}
#
# 2.) Locking
#
#{{{

if iswin:
	lockfd = None
lockname = None
try:
	lockpath = os.environ['LOCK_HOME']
except KeyError:
	lockpath = mkpath (base, 'var', 'lock')

def _mklockpath (pgmname):
	global	lockpath
	
	return mkpath (lockpath, '%s.lock' % pgmname)

def lock (isFatal = True):
	global	lockname, logname

	if lockname:
		return lockname
	name = _mklockpath (logname)
	s = '%10d\n' % (os.getpid ())
	report = 'Try locking using file "' + name + '"\n'
	n = 0
	while n < 2:
		n += 1
		try:
			if not lockname:
				fd = os.open (name, os.O_WRONLY | os.O_CREAT | os.O_EXCL, 0444)
				os.write (fd, s)
				os.close (fd)
				lockname = name

				if iswin:
					global	lockfd

					lockfd = open (lockname)
					os.chmod (lockname, 0777)
				report += 'Lock aquired\n'
		except OSError, e:
			if e.errno == errno.EEXIST:
				report += 'File exists, try to read it\n'
				try:
					fd = os.open (name, os.O_RDONLY)
					inp = os.read (fd, 32)
					os.close (fd)
					idx = inp.find ('\n')
					if idx != -1:
						inp = inp[:idx]
					inp = chop (inp)
					try:
						pid = int (inp)
					except ValueError:
						pid = -1
					if pid > 0:
						report += 'Locked by process %d, look if it is still running\n' % (pid)
						try:

							if iswin:
								try:
									os.unlink (name)
									n -= 1
								except WindowsError:
									pass
							else:
								os.kill (pid, 0)
							report += 'Process is still running\n'
							n += 1
						except OSError, e:
							if e.errno == errno.ESRCH:
								report += 'Remove stale lockfile\n'
								try:
									os.unlink (name)
								except OSError, e:
									report += 'Unable to remove lockfile: ' + e.strerror + '\n'
							elif e.errno == errno.EPERM:
								report += 'Process is running and we cannot access it\n'
							else:
								report += 'Unable to check: ' + e.strerror + '\n'
					else:
						try:
							st = os.stat (name)
							if st.st_size == 0:
								report += 'Empty lock file, assuming due to crash or disk full\n'
								os.unlink (name)
						except OSError, e:
							report += 'Failed to check for or remove empty lock file: %s\n' % e.strerror
				except OSError, e:
					report += 'Unable to read file: ' + e.strerror + '\n'
			else:
				report += 'Unable to create file: ' + e.strerror + '\n'
	if not lockname and isFatal:
		raise error (report)
	return lockname

def unlock ():
	global	lockname

	if lockname:
		try:

			if iswin:
				global	lockfd

				if not lockfd is None:
					lockfd.close ()
					lockfd = None
				os.chmod (lockname, 0777)
			os.unlink (lockname)
			lockname = None
		except OSError, e:
			if e.errno != errno.ENOENT:
				raise error ('Unable to remove lock: ' + e.strerror + '\n')

def signallock (program, signr = signal.SIGTERM):
	rc = False
	report = ''
	fname = _mklockpath (program)
	try:
		fd = open (fname, 'r')
		pline = fd.readline ()
		fd.close ()
		try:
			pid = int (pline.strip ())
			if pid > 0:
				try:
					os.kill (pid, signr)
					rc = True
					report = None
				except OSError, e:
					if e.errno == errno.ESRCH:
						report += 'Process %d does not exist\n' % pid
						try:
							os.unlink (fname)
						except OSError, e:
							report += 'Unable to remove stale lockfile %s %r\n' % (fname, e.args)
					elif e.errno == errno.EPERM:
						report += 'No permission to signal process %d\n' % pid
					else:
						report += 'Failed to signal process %d %r' % (pid, e.args)
			else:
				report += 'PIDFile contains invalid PID: %d\n' % pid
		except ValueError:
			report += 'Content of PIDfile is not valid: "%s"\n' % chop (pline)
	except IOError, e:
		if e.args[0] == errno.ENOENT:
			report += 'Lockfile %s does not exist\n' % fname
		else:
			report += 'Lockfile %s cannot be opened: %r\n' % (fname, e.args)
	return (rc, report)
#}}}
#
# 3.) file I/O
#
#{{{
def createPath (path, mode = 0777):
	if not os.path.isdir (path):
		try:
			os.mkdir (path, mode)
		except OSError, e:
			if e.args[0] != errno.EEXIST:
				if e.args[0] != errno.ENOENT:
					raise error ('Failed to create %s: %s' % (path, e.args[1]))
				elem = path.split (os.path.sep)
				target = ''
				for e in elem:
					target += e
					if target and not os.path.isdir (target):
						try:
							os.mkdir (target, mode)
						except OSError, e:
							raise error ('Failed to create %s at %s: %s' % (path, target, e.args[1]))
					target += os.path.sep

archtab = {}
def mkArchiveDirectory (path, mode = 0777):
	global	archtab

	tt = time.localtime (time.time ())
	ts = '%04d%02d%02d' % (tt[0], tt[1], tt[2])
	arch = mkpath (path, ts)
	if not arch in archtab:
		try:
			st = os.stat (arch)
			if not stat.S_ISDIR (st[stat.ST_MODE]):
				raise error ('%s is not a directory' % arch)
		except OSError, e:
			if e.args[0] != errno.ENOENT:
				raise error ('Unable to stat %s: %s' % (arch, e.args[1]))
			try:
				os.mkdir (arch, mode)
			except OSError, e:
				raise error ('Unable to create %s: %s' % (arch, e.args[1]))
		archtab[arch] = True
	return arch
	
seektab = []
class Filepos:
	def __stat (self, stat_file):
		try:
			if stat_file:
				st = os.stat (self.fname)
			else:
				st = os.fstat (self.fd.fileno ())
			rc = (st[stat.ST_INO], st[stat.ST_CTIME], st[stat.ST_SIZE])
		except (OSError, IOError):
			rc = None
		return rc

	def __open (self):
		global	seektab

		errmsg = None
		if os.access (self.info, os.F_OK):
			try:
				fd = open (self.info, 'r')
				line = fd.readline ()
				fd.close ()
				parts = chop (line).split (':')
				if len (parts) == 3:
					self.inode = int (parts[0])
					self.ctime = int (parts[1])
					self.pos = int (parts[2])
				else:
					errmsg = 'Invalid input for %s: %s' % (self.fname, line)
			except (IOError, ValueError), e:
				errmsg = 'Unable to read info file %s: %r' % (self.info, e.args)
		if not errmsg:
			try:
				self.fd = open (self.fname, 'r')
			except IOError, e:
				errmsg = 'Unable to open %s: %r' % (self.fname, e.args)
			if self.fd:
				st = self.__stat (False)
				if st:
					ninode = st[0]
					nctime = st[1]
					if ninode == self.inode:
						if st[2] >= self.pos:
							self.fd.seek (self.pos)
						else:
							self.pos = 0
					self.inode = ninode
					self.ctime = nctime
				else:
					errmsg = 'Failed to stat %s' % self.fname
				if errmsg:
					self.fd.close ()
					self.fd = None
		if errmsg:
			raise error (errmsg)
		if not self in seektab:
			seektab.append (self)

	def __init__ (self, fname, info, checkpoint = 64):
		self.fname = fname
		self.info = info
		self.checkpoint = checkpoint
		self.fd = None
		self.inode = -1
		self.ctime = 0
		self.pos = 0
		self.count = 0
		self.__open ()
	
	def __save (self):
		fd = open (self.info, 'w')
		fd.write ('%d:%d:%d' % (self.inode, self.ctime, self.fd.tell ()))
		fd.close ()
		self.count = 0
	
	def close (self):
		if self.fd:
			self.__save ()
			self.fd.close ()
			self.fd = None
		if self in seektab:
			seektab.remove (self)

	def __check (self):
		rc = True
		st = self.__stat (True)
		if st:
			if st[0] == self.inode and st[1] == self.ctime and st[2] > self.fd.tell ():
				rc = False
		return rc

	def __readline (self):
		line = self.fd.readline ()
		if line != '':
			self.count += 1
			if self.count >= self.checkpoint:
				self.__save ()
			return chop (line)
		else:
			return None
	
	def readline (self):
		line = self.__readline ()
		if line is None and not self.__check ():
			self.close ()
			self.__open ()
			line = self.__readline ()
		return line
#
def die (lvl = LV_FATAL, ident = None, s = None):
	global	seektab

	if s:
		err (s)
		log (lvl, ident, s)
	for st in seektab[:]:
		st.close ()
	unlock ()
	sys.exit (1)
rip = die
#}}}
#
# 4.) Parallel process wrapper
#
#{{{
try:
	import	multiprocessing
	#
	class Messenger (object):
		class Communicator (object):
			def __init__ (self, msg, myself):
				self.msg = msg
				self.myself = myself
			
			def send (self, receiver, content):
				self.msg.send (self.myself, receiver, content)
			
			def answer (self, m, content):
				self.msg.send (self.myself, m.sender, content)

			def recv (self):
				self.msg.recv (self.myself)
				
		def __init__ (self):
			self.channels = {}
		
		def comm (self, myself):
			return self.Communicator (self, myself)
			
		def create (self, name, exclusive = True):
			if type (name) not in types.StringTypes:
				raise TypeError ('name expected to be a string')
			if name in self.channels:
				if exclusive:
					raise ValueError ('%s: already existing' % name)
			else:
				self.channels[name] = multiprocessing.Queue ()
			return self.comm (name)
		
		def send (self, sender, receiver, content):
			self.channels[receiver].put ((sender, receiver, content))
		
		def answer (self, m, content):
			self.send (self, m[1], m[0], content)
		
		def recv (self, receiver):
			return self.channels[receiver].get ()
	
	_m = Messenger ()
	mcomm = _m.comm
	mcreate = _m.create
	msend = _m.send
	manswer = _m.answer
	mrecv = _m.recv
	del _m
		
	class Parallel (object):
		class Process (multiprocessing.Process):
			def __init__ (self, method, args, name):
				multiprocessing.Process.__init__ (self, name = name)
				self.method = method
				self.args = args
				self.logname = name
				self.resultq = multiprocessing.Queue ()
				self.value = None

			def run (self):
				if self.logname is not None:
					global	logname

					logname = '%s-%s' % (logname, self.logname.lower ().replace ('/', '_'))
					self.logname = None

				if self.args is None:
					rc = self.method ()
				else:
					rc = self.method (*self.args)
				self.resultq.put (rc)
		
			def result (self):
				if self.value is None:
					if not self.resultq.empty ():
						self.value = self.resultq.get ()
				return self.value

		def __init__ (self):
			self.active = set ()
	
		def fork (self, method, args = None, name = None):
			p = self.Process (method, args, name)
			self.active.add (p)
			p.start ()
			return p
		
		def living (self):
			return (len ([_p for _p in self.active if _p.is_alive ()]), len (self.active))
	
		def wait (self, name = None, timeout = None, count = None):
			done = set ()
			rc = {}
			for p in self.active:
				if name is None or p.name == name:
					p.join (timeout)
					if timeout is None or not p.is_alive ():
						rc[p.name] = (p.exitcode, p.result ())
						done.add (p)
						if count is not None:
							count -= 1
							if count == 0:
								break
			self.active = self.active.difference (done)
			return rc
		
		def ready (self):
			for p in self.active:
				if not p.is_alive ():
					rc = self.wait (name = p.name, count = 1)
					if rc:
						return rc.values ()[0]
			return None
	
		def term (self, name = None):
			for p in self.active:
				if name is None or p.name == name:
					p.terminate ()
			return self.wait (name = name)

	_p = Parallel ()
	pfork = _p.fork
	palive = _p.living
	pwait = _p.wait
	pready = _p.ready
	pterm = _p.term
	del _p
except ImportError:
	pass
#}}}
#
# 5.) mailing/httpclient
#
#{{{
def mailsend (relay, sender, receivers, headers, body,
	      myself = host):
	codetype = lambda code: code / 100
	rc = False
	if not relay:
		return (rc, 'Missing relay\n')
	if not sender:
		return (rc, 'Missing sender\n')
	if type (receivers) in types.StringTypes:
		receivers = [receivers]
	if len (receivers) == 0:
		return (rc, 'Missing receivers\n')
	if not body:
		return (rc, 'Empty body\n')
	report = ''
	try:
		s = smtplib.SMTP (relay)
		(code, detail) = s.helo (myself)
		if codetype (code) != 2:
			raise smtplib.SMTPResponseException (code, 'HELO ' + myself + ': ' + detail)
		else:
			report += 'HELO %s sent\n%d %s recvd\n' % (myself, code, detail)
		(code, detail) = s.mail (sender)
		if codetype (code) != 2:
			raise smtplib.SMTPResponseException (code, 'MAIL FROM:<' + sender + '>: ' + detail)
		else:
			report += 'MAIL FROM:<%s> sent\n%d %s recvd\n' % (sender, code, detail)
		for r in receivers:
			(code, detail) = s.rcpt (r)
			if codetype (code) != 2:
				raise smtplib.SMTPResponseException (code, 'RCPT TO:<' + r + '>: ' + detail)
			else:
				report += 'RCPT TO:<%s> sent\n%d %s recvd\n' % (r, code, detail)
		mail = ''
		hsend = False
		hrecv = False
		if headers:
			for h in headers:
				if len (h) > 0 and h[-1] != '\n':
					h += '\n'
				if not hsend and len (h) > 5 and h[:5].lower () == 'from:':
					hsend = True
				elif not hrecv and len (h) > 3 and h[:3].lower () == 'to:':
					hrecv = True
				mail = mail + h
		if not hsend:
			mail += 'From: ' + sender + '\n'
		if not hrecv:
			recvs = ''
			for r in receivers:
				if recvs:
					recvs += ', '
				recvs += r
			mail += 'To: ' + recvs + '\n'
		mail += '\n' + body
		(code, detail) = s.data (mail)
		if codetype (code) != 2:
			raise smtplib.SMTPResponseException (code, 'DATA: ' + detail)
		else:
			report += 'DATA sent\n%d %s recvd\n' % (code, detail)
		s.quit ()
		report += 'QUIT sent\n'
		rc = True
	except smtplib.SMTPConnectError, e:
		report += 'Unable to connect to %s, got %d %s response\n' % (relay, e.smtp_code, e.smtp_error)
	except smtplib.SMTPServerDisconnected:
		report += 'Server connection lost\n'
	except smtplib.SMTPResponseException, e:
		report += 'Invalid response: %d %s\n' % (e.smtp_code, e.smtp_error)
	except socket.error, e:
		report += 'General socket error: %r\n' % (e.args, )
	except Exception, e:
		report += 'General problems during mail sending: %r, %r\n' % (type (e), e.args)
	return (rc, report)
#}}}
#
# 6.) system interaction
#
#{{{
#}}}
#
# 7.) Validate UIDs
#
#{{{
class UID:
	def __init__ (self):
		self.companyID = 0
		self.mailingID = 0
		self.customerID = 0
		self.URLID = 0
		self.signature = None
		self.prefix = None
		self.password = None
	
	def __decodeBase36 (self, s):
		return int (s, 36)
	
	def __codeBase36 (self, i):
		if i == 0:
			return '0'
		elif i < 0:
			i = -i
			sign = '-'
		else:
			sign = ''
		s = ''
		while i > 0:
			s = '0123456789abcdefghijklmnopqrstuvwxyz'[i % 36] + s
			i /= 36
		return sign + s
	
	def __makeSignature (self, s):
		hashval = hash_sha1 (s).digest ()
		sig = ''
		for ch in hashval[::2]:
			sig += self.__codeBase36 ((ord (ch) >> 2) % 36)
		return sig
	
	def __makeBaseUID (self):
		if self.prefix:
			s = self.prefix + '.'
		else:
			s = ''
		s += self.__codeBase36 (self.companyID) + '.' + \
		     self.__codeBase36 (self.mailingID) + '.' + \
		     self.__codeBase36 (self.customerID) + '.' + \
		     self.__codeBase36 (self.URLID)
		return s
	
	def createSignature (self):
		return self.__makeSignature (self.__makeBaseUID () + '.' + self.password)
	
	def createUID (self):
		baseUID = self.__makeBaseUID ()
		return baseUID + '.' + self.__makeSignature (baseUID + '.' + self.password)
	
	def parseUID (self, uid):
		parts = uid.split ('.')
		plen = len (parts)
		if not plen in (5, 6):
			raise error ('Invalid input format')
		start = plen - 5
		if start == 1:
			self.prefix = parts[0]
		else:
			self.prefix = None
		try:
			self.companyID = self.__decodeBase36 (parts[start])
			self.mailingID = self.__decodeBase36 (parts[start + 1])
			self.customerID = self.__decodeBase36 (parts[start + 2])
			self.URLID = self.__decodeBase36 (parts[start + 3])
			self.signature = parts[start + 4]
		except ValueError:
			raise error ('Invalid input in data')
	
	def validateUID (self):
		lsig = self.createSignature ()
		return lsig == self.signature

class METAFile (object):
	splitter = re.compile ('[^0-9]+')
	def __init__ (self, path):
		self.setPath (path)
	
	def __makeTimestamp (self, epoch):
		tt = time.localtime (epoch)
		return '%04d%02d%02d%02d%02d%02d' % (tt[0], tt[1], tt[2], tt[3], tt[4], tt[5])

	def __parseTimestamp (self, ts):
		if ts[0] == 'D' and len (ts) == 15:
			rc = ts[1:]
		else:
			try:
				rc = self.__makeTimestamp (int (ts))
			except ValueError:
				rc = None
		return rc
	
	def __error (self, s):
		if self.error is None:
			self.error = [s]
		else:
			self.error.append (s)
		self.valid = False
	
	def isReady (self, epoch = None):
		if epoch is None:
			ts = self.__makeTimestamp (time.time ())
		elif type (epoch) in types.StringTypes:
			ts = epoch
		elif type (epoch) in (types.IntType, types.LongType):
			ts = self.__makeTimestamp (epoch)
		else:
			raise TypeError ('Expecting either None, string or numeric, got %r' % type (epoch))
		return self.valid and cmp (self.timestamp, ts) <= 0

	def getError (self):
		if self.error is None:
			return 'no error'
		return ', '.join (self.error)

	def setPath (self, path):
		self.valid = False
		self.error = None
		self.path = path
		self.directory = None
		self.filename = None
		self.extension = None
		self.basename = None
		self.timestamp = None
		self.mailid = None
		self.mailing = None
		self.blocknr = None
		self.blockid = None
		self.single = None
		if path is not None:
			self.directory = os.path.dirname (self.path)
			self.filename = os.path.basename (self.path)
			n = self.filename.find ('.')
			if n != -1:
				self.extension = self.filename[n + 1:]
				self.basename = self.filename[:n]
			else:
				self.basename = self.filename
			parts = self.basename.split ('=')
			if len (parts) != 6:
				self.__error ('Invalid format of input file')
			else:
				self.valid = True
				self.timestamp = self.__parseTimestamp (parts[1])
				if self.timestamp is None:
					self.__error ('Unparseable timestamp in "%s" found' % parts[1])
				self.mailid = parts[3]
				mparts = [_m for _m in self.splitter.split (self.mailid) if _m]
				if len (mparts) == 0:
					self.__error ('Unparseable mailing ID in "%s" found' % parts[3])
				else:
					try:
						self.mailing = int (mparts[-1])
					except ValueError:
						self.__error ('Unparseable mailing ID in mailid "%s" found' % self.mailid)
				try:
					self.blocknr = int (parts[4])
					self.blockid = '%d' % self.blocknr
					self.single = False
				except ValueError:
					self.blocknr = 0
					self.blockid = parts[4]
					self.single = True
#}}}
#
# 8.) General database interface
#
#{{{
class DBResultType:
	Array = 0
	List = 1
	Struct = 2
	Hash = 3
	Result = 4

class DBResult (object):
	def __init__ (self, col, row):
		self._Col = col
		self._Row = row

	def __len__ (self):
		return len (self._Row)

	def __getitem__ (self, what):
		if type (what) in (int, long):
			return self._Row[what]
		return self.__dict__[what]

	def __contains__ (self, what):
		return what in self._Col

	def __call__ (self, what, dflt = None):
		try:
			return self[what]
		except (KeyError, IndexError):
			return dflt
	
	def __str__ (self):
		return '[%s]' % ', '.join (['%s=%r' % (_c, _r) for (_c, _r) in zip (self._Col, self._Row)])
	
	def getColumns (self):
		return self._Col
	
	def getValues (self):
		return self._Row
	
	def getItems (self):
		return zip (self._Col, self._Row)

	def NVL (self, what, onNull):
		rc = self[what]
		if rc is None:
			rc = onNull
		return rc

class DBCore (object):
	def __init__ (self, dbms, driver, cursorClass):
		self.dbms = dbms
		self.driver = driver
		self.cursorClass = cursorClass
		self.db = None
		self.lasterr = None
		self.log = None
		self.cursors = []
	
	def __enter__ (self):
		return self
	
	def __exit__ (self, exc_type, exc_value, traceback):
		self.close ()
	
	def error (self, errmsg):
		self.lasterr = errmsg
		self.close ()
	
	def reprLastError (self):
		return str (self.lasterr)
	
	def lastError (self):
		if self.lasterr is not None:
			return self.reprLastError ()
	
	def sync (self, commit = True):
		if self.db is not None:
			if commit:
				self.db.commit ()
			else:
				self.db.rollback ()
				
	def commit (self):
		self.sync (True)
	
	def rollback (self):
		self.sync (False)

	def close (self):
		if self.db is not None:
			for c in self.cursors:
				try:
					c.close ()
				except self.driver.Error:
					pass
			try:
				self.db.close ()
			except self.driver.Error, e:
				self.lasterr = e
			self.db = None
		self.cursors = []
	
	def open (self):
		self.close ()
		try:
			self.connect ()
		except self.driver.Error, e:
			self.error (e)
		return self.isOpen ()
	
	def isOpen (self):
		return self.db is not None
	
	def getCursor (self):
		if self.isOpen () or self.open ():
			try:
				curs = self.db.cursor ()
				if curs is not None:
					try:
						if curs.arraysize < 100:
							curs.arraysize = 100
					except AttributeError:
						pass
			except self.driver.Error, err:
				curs = None
				self.error (err)
		else:
			curs = None
		return curs
	
	def cursor (self, autocommit = False):
		if self.isOpen () or self.open ():
			c = self.cursorClass (self, autocommit)
			if c is not None:
				self.cursors.append (c)
		else:
			c = None
		return c
	
	def release (self, cursor):
		if cursor in self.cursors:
			self.cursors.remove (cursor)
			cursor.close ()
		
	def query (self, req):
		c = self.cursor ()
		if c is None:
			raise error ('Unable to get database cursor: ' + self.lastError ())
		rc = None
		try:
			rc = [r for r in c.query (req)]
		finally:
			c.close ()
		return rc
		
	def update (self, req):
		c = self.cursor ()
		if c is None:
			raise error ('Unable to get database cursor: ' + self.lastError ())
		rc = None
		try:
			rc = c.update (req)
		finally:
			c.close ()
		return rc
	execute = update

class DBCache (object):
	def __init__ (self, data):
		self.data = data
		self.count = len (data)
		self.pos = 0

	def __iter__ (self):
		return self

	def __next__ (self):
		if self.pos >= self.count:
			raise StopIteration ()
		record = self.data[self.pos]
		self.pos += 1
		return record
	next = __next__

class DBCursor (object):
	def __init__ (self, db, autocommit, needReformat):
		self.db = db
		self.autocommit = autocommit
		self.needReformat = needReformat
		self.curs = None
		self.desc = False
		self.defaultRType = None
		self.querypost = None
		self.rowspec = None
		self.cacheReformat = {}
		self.cacheCleanup = {}
		self.log = db.log
		self.qreplace = {}
	
	def __enter__ (self):
		return self
	
	def __exit__ (self, exc_type, exc_value, traceback):
		self.close ()
	
	def rselect (self, s, **kws):
		if kws:
			rplc = self.qreplace.copy ()
			for (var, val) in kws.items ():
				rplc[var] = val
			return s % rplc
		return s % self.qreplace

	qmapper = {
		'MySQLdb':	'mysql',
	}
	def qselect (self, **args):
		try:
			return args[self.db.driver.__name__]
		except KeyError:
			return args[self.qmapper[self.db.driver.__name__]]
		
	def lastError (self):
		if self.db is not None:
			return self.db.lastError ()
		return 'no database interface active'
		
	def error (self, errmsg):
		if self.db is not None:
			self.db.lasterr = errmsg
		self.close ()

	def close (self):
		if self.curs is not None:
			try:
				self.db.release (self.curs)
				if self.log: self.log ('Cursor closed')
			except self.db.driver.Error, e:
				self.db.lasterr = e
				if self.log: self.log ('Cursor closing failed: %s' % self.lastError ())
			self.curs = None
			self.desc = False
	
	def open (self):
		self.close ()
		if self.db is not None:
			try:
				self.curs = self.db.getCursor ()
				if self.curs is not None:
					if self.log: self.log ('Cursor opened')
				else:
					if self.log: self.log ('Cursor open failed')
			except self.db.driver.Error, e:
				self.error (e)
				if self.log: self.log ('Cursor opening failed: %s' % self.lastError ())
		else:
			if self.log: self.log ('Cursor opeing failed: no database available')
		return self.curs is not None
		
	def description (self):
		if self.desc:
			return self.curs.description
		return None

	rfparse = re.compile ('\'[^\']*\'|:[A-Za-z0-9_]+|%')
	def reformat (self, req, parm):
		try:
			(nreq, varlist) = self.cacheReformat[req]
		except KeyError:
			nreq = ''
			varlist = []
			while 1:
				mtch = self.rfparse.search (req)
				if mtch is None:
					nreq += req
					break
				else:
					span = mtch.span ()
					nreq += req[:span[0]]
					chunk = req[span[0]:span[1]]
					if chunk == '%':
						nreq += '%%'
					elif chunk.startswith ('\'') and chunk.endswith ('\''):
						nreq += chunk.replace ('%', '%%')
					else:
						varlist.append (chunk[1:])
						nreq += '%s'
					req = req[span[1]:]
			self.cacheReformat[req] = (nreq, varlist)
		nparm = []
		for key in varlist:
			nparm.append (parm[key])
		return (nreq, nparm)

	def cleanup (self, req, parm):
		try:
			varlist = self.cacheCleanup[req]
		except KeyError:
			varlist = []
			while 1:
				mtch = self.rfparse.search (req)
				if mtch is None:
					break
				span = mtch.span ()
				chunk = req[span[0]:span[1]]
				if chunk.startswith (':'):
					varlist.append (chunk[1:])
				req = req[span[1]:]
			self.cacheCleanup[req] = varlist
		nparm = {}
		for key in varlist:
			nparm[key] = parm[key]
		return nparm

	def __valid (self):
		if self.curs is None:
			if not self.open ():
				raise error ('Unable to setup cursor: ' + self.lastError ())

	def __rowsetup (self, data):
		if self.rowspec is None:
			d = self.description ()
			if d is None:
				self.rowspec = []
				nr = 1
				while nr <= len (data):
					self.rowspec.append ('_%d' % nr)
					nr += 1
			else:
				self.rowspec = [_d[0].lower () for _d in d]
	
	def __rowlist (self, data):
		self.__rowsetup (data)
		return zip (self.rowspec, data)

	def __rowfill (self, hash, data):
		for (var, val) in self.__rowlist (data):
			hash[var] = val
	
	def __rowstruct (self, data):
		rc = struct ()
		self.__rowfill (rc.__dict__, data)
		return rc
		
	def __rowhash (self, data):
		rc = {}
		self.__rowfill (rc, data)
		return rc
		
	def __rowresult (self, data):
		self.__rowsetup (data)
		rc = DBResult (self.rowspec, data)
		self.__rowfill (rc.__dict__, data)
		return rc
	
	def __iter__ (self):
		return self
		
	def __next__ (self):
		try:
			data = self.curs.fetchone ()
		except self.db.driver.Error, e:
			self.error (e)
			raise error ('query next failed: ' + self.lastError ())
		if data is None:
			raise StopIteration ()
		if self.querypost:
			return self.querypost (data)
		return data
	next = __next__

	def setOutputSize (self, *args):
		if self.db.dbms == 'oracle':
			self.__valid ()
			self.curs.setoutputsize (*args)

	def setInputSizes (self, **args):
		if self.db.dbms == 'oracle':
			self.__valid ()
			self.curs.setinputsizes (**args)

	def defaultResultType (self, rtype):
		old = self.defaultRType
		self.defaultRType = rtype
		return old

	def query (self, req, parm = None, cleanup = False, rtype = None):
		self.__valid ()
		if rtype is None:
			rtype = self.defaultRType
		self.rowspec = None
		if rtype == DBResultType.List:
			self.querypost =  self.__rowlist
		elif rtype == DBResultType.Struct:
			self.querypost = self.__rowstruct
		elif rtype == DBResultType.Hash:
			self.querypost = self.__rowhash
		elif rtype == DBResultType.Result:
			self.querypost = self.__rowresult
		else:
			self.querypost = None
		try:
			if parm is None:
				if self.log: self.log ('Query: %s' % req)
				self.curs.execute (req)
			else:
				if self.needReformat:
					(req, parm) = self.reformat (req, parm)
				elif cleanup:
					parm = self.cleanup (req, parm)
				if self.log: self.log ('Query: %s using %s' % (req, parm))
				self.curs.execute (req, parm)
			if self.log: self.log ('Query started')
		except self.db.driver.Error, e:
			self.error (e)
			if self.log:
				if parm is None:
					self.log ('Query %s failed: %s' % (req, self.lastError ()))
				else:
					self.log ('Query %s using %r failed: %s' % (req, parm, self.lastError ()))
			raise error ('query start failed: ' + self.lastError ())
		self.desc = True
		return self
		
	def queryc (self, req, parm = None, cleanup = False, rtype = None):
		if self.query (req, parm, cleanup, rtype) == self:
			try:
				data = self.curs.fetchall ()
				if self.querypost:
					data = [self.querypost (_d) for _d in data]
				return DBCache (data)
			except self.db.driver.Error, e:
				self.error (e)
				if self.log:
					if parm is None:
						self.log ('Queryc %s fetch failed: %s' % (req, self.lastError ()))
					else:
						self.log ('Queryc %s using %r fetch failed: %s' % (req, parm, self.lastError ()))
				raise error ('query all failed: ' + self.lastError ())
		if self.log:
			if parm is None:
				self.log ('Queryc %s failed: %s' % (req, self.lastError ()))
			else:
				self.log ('Queryc %s using %r failed: %s' % (req, parm, self.lastError ()))
		raise error ('unable to setup query: ' + self.lastError ())
		
	def querys (self, req, parm = None, cleanup = False, rtype = None):
		rc = None
		for rec in self.query (req, parm, cleanup, rtype):
			rc = rec
			break
		return rc
	
	def queryp (self, req, **kws):
		return self.query (req, kws)
	def querypc (self, req, **kws):
		return self.queryc (req, kws)
	def queryps (self, req, **kws):
		return self.querys (req, kws)
		
	def sync (self, commit = True):
		rc = False
		if self.db is not None:
			if self.db.db is not None:
				try:
					self.db.sync (commit)
					if self.log:
						if commit:
							self.log ('Sync done commiting')
						else:
							self.log ('Sync done rollbacking')
					rc = True
				except self.db.driver.Error, e:
					self.error (e)
					if self.log:
						if commit:
							self.log ('Sync failed commiting')
						else:
							self.log ('Sync failed rollbacking')
			else:
				if self.log: self.log ('Sync failed: database not open')
		else:
			if self.log: self.log ('Sync failed: database not available')
		return rc

	def update (self, req, parm = None, commit = False, cleanup = False):
		self.__valid ()
		try:
			if parm is None:
				if self.log: self.log ('Update: %s' % req)
				self.curs.execute (req)
			else:
				if self.needReformat:
					(req, parm) = self.reformat (req, parm)
				elif cleanup:
					parm = self.cleanup (req, parm)
				if self.log: self.log ('Update: %s using %r' % (req, parm))
				self.curs.execute (req, parm)
			if self.log: self.log ('Update affected %d rows' % self.curs.rowcount)
		except self.db.driver.Error, e:
			self.error (e)
			if self.log:
				if parm is None:
					self.log ('Update %s failed: %s' % (req, self.lastError ()))
				else:
					self.log ('Update %s using %r failed: %s' % (req, parm, self.lastError ()))
			raise error ('update failed: ' + self.lastError ())
		rows = self.curs.rowcount
		if rows > 0 and (commit or self.autocommit):
			if not self.sync ():
				if self.log:
					if parm is None:
						self.log ('Commit after update failed for %s: %s' % (req, self.lastError ()))
					else:
						self.log ('Commit after update failed for %s using %r: %s' % (req, parm, self.lastError ()))
				raise error ('commit failed: ' + self.lastError ())
		self.desc = False
		return rows
	execute = update

	def updatep (self, req, **kws):
		return self.update (req, kws)
	executep = updatep

try:
	import	MySQLdb

	class DBCursorMySQL (DBCursor):
		def __init__ (self, db, autocommit):
			DBCursor.__init__ (self, db, autocommit, True)
			self.qreplace['sysdate'] = 'current_timestamp'

	class DBMySQL (DBCore):

		dbhost = properties['python.emm.dbhost']
		dbuser = properties['python.emm.dbuser']
		dbpass = properties['python.emm.dbpass']
		dbname = properties['python.emm.database']
		def __init__ (self, host = dbhost, user = dbuser, passwd = dbpass, name = dbname):
			DBCore.__init__ (self, 'mysql', MySQLdb, DBCursorMySQL)
			self.host = host
			self.user = user
			self.passwd = passwd
			self.name = name
		
		def reprLastError (self):
			return 'MySQL-%d: %s' % (self.lasterr.args[0], self.lasterr.args[1].strip ())

		def connect (self):
			try:
				(host, port) = self.host.split (':', 1)
				self.db = self.driver.connect (host, self.user, self.passwd, self.name, int (port))
			except ValueError:
				self.db = self.driver.connect (self.host, self.user, self.passwd, self.name)

	class CMSDBMySQL (DBMySQL):

		dbhost = properties['python.cms.dbhost']
		dbuser = properties['python.cms.dbuser']
		dbpass = properties['python.cms.dbpass']
		dbname = properties['python.cms.database']
		def __init__ (self, host = dbhost, user = dbuser, passwd = dbpass, name = dbname):
			DBMySQL.__init__ (self, host, user, passwd, name)

	DBase = DBMySQL
	CMSDBase = CMSDBMySQL
	def DBaseID (a = None):
		return DBase ()
except ImportError:
	DBMySQL = None


class Datasource:
	def __init__ (self):
		self.cache = {}
		
	def getID (self, desc, companyID, sourceGroup, db = None):
		try:
			rc = self.cache[desc]
		except KeyError:
			rc = None
			if db is None:
				db = DBase ()
				dbOpened = True
			else:
				dbOpened = False
			if not db is None:
				curs = db.cursor ()
				if not curs is None:
					for state in [0, 1]:
						for rec in curs.query ('SELECT datasource_id FROM datasource_description_tbl WHERE company_id = %d AND description = :description' % companyID, {'description': desc}):
							rc = int (rec[0])
						if rc is None and state == 0:
							query = curs.qselect (oracle = \
								'INSERT INTO datasource_description_tbl (datasource_id, description, company_id, sourcegroup_id, timestamp) VALUES ' + \
								'(datasource_description_tbl_seq.nextval, :description, %d, %d, sysdate)' % (companyID, sourceGroup), \
								mysql = \
								'INSERT INTO datasource_description_tbl (description, company_id, sourcegroup_id, creation_date) VALUES ' + \
								'(:description, %d, %d, current_timestamp)' % (companyID, sourceGroup))
							curs.update (query, {'description': desc}, commit = True)
					curs.close ()
				if dbOpened:
					db.close ()
			if not rc is None:
				self.cache[desc] = rc
		return rc

#}}}
#
# 9.) Simple templating
#
#{{{
class MessageCatalog:
	"""class MessageCatalog:

This class is primary designed to be integrated in the templating system,
but can also be used stand alone. You instanciate the class with a file
name of the message file which contains of a default section (starting
from top or introduced by a section "[*]". For each supported language
you add a section with the language token, e.g. "[de]" for german and
a list of tokens with the translation. A message catalog file may look
like this:
#	comments start as usual with a hash sign
#	this is the default section
yes: Yes
no: No
#
#	this is the german version
[de]
yes: Ja
no: Nein

You may extend an entry over the current line with a trailing backslash.

If you pass a message catalog to the templating system, you can refer
to the catalog by either using ${_['token']} to just translate one token
or using ${_ ('In your mother language YES means %(yes)')}. There are
also shortcut versions for ${_['xxx']) can be written as _[xxx] and
${_ ('xxx')} can be written as _{xxx}.

In a stand alone variante, this looks like this:
>>> m = MessageCatalog ('/some/file/name')
>>> m.setLang ('de')
>>> print m['yes']
Ja
>>> print m ('yes')
yes
>>> print m ('yes in your language is %(yes)')
yes in your language is Ja
>>> print m['unset']
*unset*
>>> m.setFill (None)
>>> print m['unset']
unset

As you can see in the last example an unknown token is expanded to itself
surrounded by a fill string, if set (to easyly catch missing tokens). If
you unset the fill string, the token itself is used with no further
processing.
"""
	messageParse = re.compile ('%\\(([^)]+)\\)')
	commentParse = re.compile ('^[ \t]*#')
	def __init__ (self, fname, lang = None, fill = '*'):
		self.messages = {None: {}}
		self.lang = None
		self.fill = fill
		if not fname is None:
			cur = self.messages[None]
			fd = open (fname, 'r')
			for line in [_l.strip () for _l in fd.read ().replace ('\\\n', '').split ('\n') if _l and self.commentParse.match (_l) is None]:
				if len (line) > 2 and line.startswith ('[') and line.endswith (']'):
					lang = line[1:-1]
					if lang == '*':
						lang = None
					if not lang in self.messages:
						self.messages[lang] = {}
					cur = self.messages[lang]
				else:
					parts = line.split (':', 1)
					if len (parts) == 2:
						(token, msg) = [_p.strip () for _p in parts]
						if len (msg) >= 2 and msg[0] in '\'"' and msg[-1] == msg[0]:
							msg = msg[1:-1]
						cur[token] = msg
			fd.close ()
	
	def __setitem__ (self, token, s):
		try:
			self.messages[self.lang][token] = s
		except KeyError:
			self.messages[self.lang] = {token: s}
	
	def __getitem__ (self, token):
		try:
			msg = self.messages[self.lang][token]
		except KeyError:
			if not self.lang is None:
				try:
					msg = self.messages[None][token]
				except KeyError:
					msg = None
			else:
				msg = None
		if msg is None:
			if self.fill is None:
				msg = token
			else:
				msg = '%s%s%s' % (self.fill, token, self.fill)
		return msg

	def __call__ (self, s):
		return self.messageParse.subn (lambda m: self[m.groups ()[0]], s)[0]
	
	def setLang (self, lang):
		self.lang = lang
	
	def setFill (self, fill):
		self.fill = fill

class Template:
	"""class Template:

This class offers a simple templating system. One instance the class
using the template in string from. The syntax is inspirated by velocity,
but differs in serveral ways (and is even simpler). A template can start
with an optional code block surrounded by the tags '#code' and '#end'
followed by the content of the template. Access to variables and
expressions are realized by $... where ... is either a simple varibale
(e.g. $var) or something more complex, then the value must be
surrounded by curly brackets (e.g. ${var.strip ()}). To get a literal
'$'sign, just type it twice, so '$$' in the template leads into '$'
in the output. A trailing backslash removes the following newline to
join lines.

Handling of message catalog is either done by calling ${_['...']} and
${_('...')} or by using the shortcut _[this is the origin] or
_{%(message): %(error)}. As this is a simple parser the brackets
must not part of the string in the shortcut, in this case use the
full call.

Control constructs must start in a separate line, leading whitespaces
ignoring, with a hash '#' sign. These constructs are supported and
are mostly transformed directly into a python construct:
	
## ...                      this introduces a comment up to end of line
#property(expr)             this sets a property of the template
#pragma(expr)               alias for property
#include(expr)              inclusion of file, subclass must realize this
#if(pyexpr)             --> if pyexpr:
#elif(pyexpr)           --> elif pyexpr:
#else                   --> else
#do(pycmd)              --> pycmd
#pass                   --> pass [same as #do(pass)]
#break			--> break [..]
#continue		--> continue [..]
#for(pyexpr)            --> for pyexpr:
#while(pyexpr)          --> while pyexpr:
#try                    --> try:
#except(pyexpr)         --> except pyexpr:
#finally                --> finally
#with(pyexpr)           --> with pyexpr:
#end                        ends an indention level
#stop                       ends processing of input template

To fill the template you call the method fill(self, namespace, lang = None)
where 'namespace' is a dictonary with names accessable by the template.
Beside, 'lang' could be set to a two letter string to post select language
specific lines from the text. These lines must start with a two letter
language ID followed by a colon, e.g.:
	
en:This is an example.
de:Dies ist ein Beispiel.

Depending on 'lang' only one (or none of these lines) are outputed. If lang
is not set, these lines are put (including the lang ID) both in the output.
If 'lang' is set, it is also copied to the namespace, so you can write the
above lines using the template language:

#if(lang=='en')
This is an example.
#elif(lang=='de')
Dies ist ein Beispiel.
#end

And for failsafe case, if lang is not set:

#try
 #if(lang=='en')
This is an example.
 #elif(lang=='de')
Dies ist ein Beispiel.
 #end
#except(NameError)
 #pass
#end
"""
	codeStart = re.compile ('^[ \t]*#code[^\n]*\n', re.IGNORECASE)
	codeEnd = re.compile ('(^|\n)[ \t]*#end[^\n]*(\n|$)', re.IGNORECASE | re.MULTILINE)
	token = re.compile ('((^|\n)[ \t]*#(#|property|pragma|include|if|elif|else|do|pass|break|continue|for|while|try|except|finally|with|end|stop)|\\$(\\$|[0-9a-z_]+(\\.[0-9a-z_]+)*|\\{[^}]*\\})|_(\\[[^]]+\\]|{[^}]+}))', re.IGNORECASE | re.MULTILINE)
	rplc = re.compile ('\\\\|"|\'|\n|\r|\t|\f|\v', re.MULTILINE)
	rplcMap = {'\n': '\\n', '\r': '\\r', '\t': '\\t', '\f': '\\f', '\v': '\\v'}
	langID = re.compile ('^([ \t]*)([a-z][a-z]):', re.IGNORECASE)
	emptyCatalog = MessageCatalog (None, fill = None)
	def __init__ (self, content, precode = None, postcode = None):
		self.content = content
		self.precode = precode
		self.postcode = postcode
		self.compiled = None
		self.properties = {}
		self.namespace = None
		self.code = None
		self.indent = None
		self.empty = None
		self.compileErrors = None
	
	def __getitem__ (self, var):
		if not self.namespace is None:
			try:
				val = self.namespace[var]
			except KeyError:
				val = ''
		else:
			val = None
		return val
	
	def __setProperty (self, expr):
		try:
			(var, val) = [_e.strip () for _e in expr.split ('=', 1)]
			if len (val) >= 2 and val[0] in '"\'' and val[-1] == val[0]:
				quote = val[0]
				self.properties[var] = val[1:-1].replace ('\\%s' % quote, quote).replace ('\\\\', '\\')
			elif val.lower () in ('true', 'on', 'yes'):
				self.properties[var] = True
			elif val.lower () in ('false', 'off', 'no'):
				self.properties[var] = False
			else:
				try:
					self.properties[var] = int (val)
				except ValueError:
					self.properties[var] = val
		except ValueError:
			var = expr.strip ()
			if var:
				self.properties[var] = True
			
	def __indent (self):
		if self.indent:
			self.code += ' ' * self.indent
	
	def __code (self, code):
		self.__indent ()
		self.code += '%s\n' % code
		if code:
			if code[-1] == ':':
				self.empty = True
			else:
				self.empty = False
			
	def __deindent (self):
		if self.empty:
			self.__code ('pass')
		self.indent -= 1
	
	def __compileError (self, start, errtext):
		if not self.compileErrors:
			self.compileErrors = ''
		self.compileErrors += '** %s: %s ...\n\n\n' % (errtext, self.content[start:start + 60])

	def __replacer (self, mtch):
		rc = []
		for ch in mtch.group (0):
			try:
				rc.append (self.rplcMap[ch])
			except KeyError:
				rc.append ('\\x%02x' % ord (ch))
		return ''.join (rc)
	
	def __escaper (self, s):
		return s.replace ('\'', '\\\'')

	def __compileString (self, s):
		self.__code ('__result.append (\'%s\')' % re.sub (self.rplc, self.__replacer, s))
			
	def __compileExpr (self, s):
		self.__code ('__result.append (str (%s))' % s)

	def __compileCode (self, token, arg):
		if not token is None:
			if arg:
				self.__code ('%s %s:' % (token, arg))
			else:
				self.__code ('%s:' % token)
		elif arg:
			self.__code (arg)
					
	def __compileContent (self):
		self.code = ''
		if self.precode:
			self.code += self.precode
			if self.code[-1] != '\n':
				self.code += '\n'
		pos = 0
		clen = len (self.content)
		mtch = self.codeStart.search (self.content)
		if not mtch is None:
			start = mtch.end ()
			mtch = self.codeEnd.search (self.content, start)
			if not mtch is None:
				(end, pos) = mtch.span ()
				self.code += self.content[start:end] + '\n'
			else:
				self.__compileError (0, 'Unfinished code segment')
		self.indent = 0
		self.empty = False
		self.code += '__result = []\n'
		while pos < clen:
			mtch = self.token.search (self.content, pos)
			if mtch is None:
				start = clen
				end = clen
			else:
				(start, end) = mtch.span ()
				groups = mtch.groups ()
				if groups[1]:
					start += len (groups[1])
			if start > pos:
				self.__compileString (self.content[pos:start])
			pos = end
			if not mtch is None:
				tstart = start
				if not groups[2] is None:
					token = groups[2]
					arg = ''
					if token != '#':
						if pos < clen and self.content[pos] == '(':
							pos += 1
							level = 1
							quote = None
							escape = False
							start = pos
							end = -1
							while pos < clen and level > 0:
								ch = self.content[pos]
								if escape:
									escape = False
								elif ch == '\\':
									escape = True
								elif not quote is None:
									if ch == quote:
										quote = None
								elif ch in '\'"':
									quote = ch
								elif ch == '(':
									level += 1
								elif ch == ')':
									level -= 1
									if level == 0:
										end = pos
								pos += 1
							if start < end:
								arg = self.content[start:end]
							else:
								self.__compileError (tstart, 'Unfinished statement')
						if pos < clen and self.content[pos] == '\n':
							pos += 1
					if token == '#':
						while pos < clen and self.content[pos] != '\n':
							pos += 1
						if pos < clen:
							pos += 1
					elif token in ('property', 'pragma'):
						self.__setProperty (arg)
					elif token in ('include', ):
						try:
							included = self.include (arg)
							if included:
								self.content = self.content[:pos] + included + self.content[pos:]
								clen += len (included)
						except error, e:
							self.__compileError (tstart, 'Failed to include "%s": %s' % (arg, e.msg))
					elif token in ('if', 'else', 'elif', 'for', 'while', 'try', 'except', 'finally', 'with'):
						if token in ('else', 'elif', 'except', 'finally'):
							if self.indent > 0:
								self.__deindent ()
							else:
								self.__compileError (tstart, 'Too many closeing blocks')
						if (arg and token in ('if', 'elif', 'for', 'while', 'except', 'with')) or \
						   (not arg and token in ('else', 'try', 'finally')):
							self.__compileCode (token, arg)
						elif arg:
							self.__compileError (tstart, 'Extra arguments for #%s detected' % token)
						else:
							self.__compileError (tstart, 'Missing statement for #%s' % token)
						self.indent += 1
					elif token in ('pass', 'break', 'continue'):
						if arg:
							self.__compileError (tstart, 'Extra arguments for #%s detected' % token)
						else:
							self.__compileCode (None, token)
					elif token in ('do', ):
						if arg:
							self.__compileCode (None, arg)
						else:
							self.__compileError (tstart, 'Missing code for #%s' % token)
					elif token in ('end', ):
						if arg:
							self.__compileError (tstart, 'Extra arguments for #end detected')
						if self.indent > 0:
							self.__deindent ()
						else:
							self.__compileError (tstart, 'Too many closing blocks')
					elif token in ('stop', ):
						pos = clen
				elif not groups[3] is None:
					expr = groups[3]
					if expr == '$':
						self.__compileString ('$')
					else:
						if len (expr) >= 2 and expr[0] == '{' and expr[-1] == '}':
							expr = expr[1:-1]
						self.__compileExpr (expr)
				elif not groups[5] is None:
					expr = groups[5]
					if expr[0] == '[':
						self.__compileExpr ('_[\'%s\']' % self.__escaper (expr[1:-1]))
					elif expr[0] == '{':
						self.__compileExpr ('_ (\'%s\')' % self.__escaper (expr[1:-1]))
				elif not groups[0] is None:
					self.__compileString (groups[0])
		if self.indent > 0:
			self.__compileError (0, 'Missing %d closing #end statement(s)' % self.indent)
		if self.compileErrors is None:
			if self.postcode:
				if self.code and self.code[-1] != '\n':
					self.code += '\n'
				self.code += self.postcode
			self.compiled = compile (self.code, '<template>', 'exec')
	
	def include (self, arg):
		raise error ('Subclass responsible for implementing "include (%r)"' % arg)

	def property (self, var):
		try:
			return self.properties[var]
		except KeyError:
			return None

	def compile (self):
		if self.compiled is None:
			try:
				self.__compileContent ()
				if self.compiled is None:
					raise error ('Compilation failed: %s' % self.compileErrors)
			except Exception, e:
				raise error ('Failed to compile [%r] %r:\n%s\n' % (type (e), e.args, self.code))

	def fill (self, namespace, lang = None, mc = None):
		if self.compiled is None:
			self.compile ()
		if namespace is None:
			self.namespace = {}
		else:
			self.namespace = namespace.copy ()
		if not lang is None:
			self.namespace['lang'] = lang
		self.namespace['property'] = self.properties
		if mc is None:
			mc = self.emptyCatalog
		mc.setLang (lang)
		self.namespace['_'] = mc
		try:
			exec self.compiled in self.namespace
		except Exception, e:
			raise error ('Execution failed [%s]: %s' % (e.__class__.__name__, str (e)))
		result = ''.join (self.namespace['__result'])
		if not lang is None:
			nresult = []
			for line in result.split ('\n'):
				mtch = self.langID.search (line)
				if mtch is None:
					nresult.append (line)
				else:
					(pre, lid) = mtch.groups ()
					if lid.lower () == lang:
						nresult.append (pre + line[mtch.end ():])
			result = '\n'.join (nresult)
		result = result.replace ('\\\n', '')
		self.namespace['result'] = result
		return result
#}}}
