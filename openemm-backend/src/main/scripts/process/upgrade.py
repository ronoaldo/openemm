#!/usr/bin/env python
#	-*- mode: python; mode: fold -*-
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
import	sys, os, getopt, re, types, time, random
import	signal, stat, mimetypes
import	BaseHTTPServer, cgi, httplib
from	xml.dom.minidom import parseString
import	agn
agn.require ('2.0.0')
agn.loglevel = agn.LV_INFO
#
if agn.iswin:
	datafile = os.path.sep + 'openemm-upgrade.txt'
else:
	datafile = os.path.sep.join (['', 'var', 'tmp', 'openemm-upgrade-%d.txt' % os.getpid ()])
versionURL = 'http://www.openemm.org/upgrade/current_version-v2.xml'
#
class Property:
	comment = re.compile ('^[ \t]*(#.*)?$')
	def __readFile (self): #{{{
		try:
			fd = open (self.path)
			content = fd.read ()
			fd.close ()
		except IOError, e:
			agn.log (agn.LV_ERROR, 'prop', 'Failed to read property file "%s": %r' % (self.path, e.args))
			content = None
		return content
	#}}}
	def __init__ (self, path): #{{{
		self.path = path
		self.exists = os.path.isfile (self.path)
		self.content = None
		if self.exists:
			self.content = self.__readFile ()
			if self.content is None:
				self.exists = False
	#}}}
	def __parse (self, content): #{{{
		rc = {}
		order = []
		comment = ''
		for line in content.split ('\n'):
			if self.comment.match (line) is None:
				parts = line.split ('=', 1)
				if len (parts) == 2:
					key = parts[0].strip ()
					rc[key] = comment + line
					order.append (key)
				comment = ''
			else:
				comment += line + '\n'
		return (rc, order)
	#}}}
	def sync (self, version): #{{{
		if self.exists and os.path.isfile (self.path):
			ncontent = self.__readFile ()
			if not ncontent is None:
				orig = '%s-orig' % self.path
				if not os.path.isfile (orig):
					try:
						os.rename (self.path, orig)
					except OSError, e:
						agn.log (agn.LV_ERROR, 'prop', 'Failed to rename "%s" to "%s": %r' % (self.path, orig, e.args))
				(entries, order) = self.__parse (ncontent)
				parts = re.split ('# required:\r?\n', ncontent)
				if len (parts) > 1:
					(required, rorder) = self.__parse ('\n'.join (parts[1:]))
				else:
					required = None
				seen = set ()
				output = []
				for line in self.content.split ('\n'):
					if self.comment.match (line) is None:
						parts = line.split ('=', 1)
						if len (parts) == 2:
							if not parts[0] in entries:
								line = None
							elif required and parts[0] in required:
								line = required[parts[0]]
							seen.add (parts[0])
					if not line is None:
						output.append (line)
				found = 0
				for key in order:
					if not key in seen:
						found += 1
						if found == 1:
							output += [
								'#',
								'# New entries in %s' % version,
								'#'
							]
						output.append (entries[key])
				try:
					fd = open (self.path, 'w')
					fd.write ('\n'.join (output) + '\n')
					fd.close ()
				except IOError, e:
					agn.log (agn.LV_ERROR, 'prop', 'Failed to write back property file "%s": %r' % (self.path, e.args))
	#}}}
#
def parse (collect, node, prefix): #{{{
	try:
		name = unicode.encode (prefix)
	except:
		name = None
	for child in node.childNodes:
		if child.nodeType == child.TEXT_NODE and name:
			data = child.data.strip ()
			if data:
				try:
					collect[name] += ' ' + data
				except KeyError:
					collect[name] = data
		elif child.nodeType == child.ELEMENT_NODE:
			parse (collect, child, prefix + '.' + child.tagName)
#}}}
class Upgrade:
	def __readprop (self, fname): #{{{
		prop = None
		try:
			fd = open (fname)
			data = fd.read ()
			fd.close ()
			comment = re.compile ('^[ \t]*#')
			for line in [_l.strip () for _l in data.split ('\n') if comment.match (_l) is None]:
				parts = line.split ('=', 1)
				if len (parts) == 2:
					if prop is None:
						prop = {}
					prop[parts[0].strip ().lower ()] = parts[1].strip ()
		except IOError, e:
			agn.log (agn.LV_ERROR, 'init', 'Failed to read %s %r' % (fname, e.args))
		return prop
	#}}}
	def __init__ (self): #{{{
		self.active = True
		self.mark = -1
		self.properties = self.__readprop (os.path.sep.join ([agn.base, 'webapps', 'openemm', 'WEB-INF', 'classes', 'emm.properties']))
		self.messages = self.__readprop (os.path.sep.join ([agn.base, 'webapps', 'openemm', 'WEB-INF', 'classes', 'messages.properties']))
	#}}}
	def removeDatafile (self): #{{{
		try:
			os.unlink (datafile)
		except OSError, e:
			agn.log (agn.LV_VERBOSE, 'rm', 'Datafile %s cannot be removed %s' % (datafile, `e.args`))
	#}}}
	def markDatafile (self): #{{{
		try:
			st = os.stat (datafile)
			self.mark = st[stat.ST_SIZE]
		except OSError:
			self.mark = -1
	#}}}
	def resumeDatafile (self): #{{{
		if self.mark >= 0:
			try:
				fd = open (datafile, 'r+')
				fd.truncate (self.mark)
				fd.close ()
			except IOError:
				pass
	#}}}
	def addDatafile (self, token, s = ''): #{{{
		fd = open (datafile, 'a')
		fd.write (token + s + '\n')
		fd.close ()
	#}}}
	def message (self, s): #{{{
		self.addDatafile ('.', s)
	#}}}
	def output (self, s): #{{{
		self.addDatafile ('>', s)
	#}}}
	def error (self, s): #{{{
		self.addDatafile ('!', s)
	#}}}
	def final (self, s): #{{{
		self.addDatafile ('X', s)
	#}}}
	def stop (self): #{{{
		self.active = False
	#}}}
	def fail (self, s): #{{{
		self.error (s)
		self.stop ()
	#}}}
	def system (self, cmd): #{{{
		try:
			pp = os.popen (cmd + ' 2>&1', 'r', 0)
			while 1:
				line = pp.readline ()
				if line == '':
					break
				self.output (line.strip ())
			rc = pp.close ()
			if rc is None:
				rc = 0
		except OSError:
			rc = 1
		return rc
	#}}}
	def putenv (self, var, val): #{{{
		if val is None:
			try:
				del os.environ[var]
			except KeyError:
				pass
		else:
			os.environ[var] = str (val)
	#}}}
	def httpGet (self, uris, checksum = None, timeout = 60, msg = False): #{{{
		rc = None
		if type (uris) in types.StringTypes:
			uris = [uris]
		uparse = re.compile ('(https?)://([^:/]+)(:[0-9]+)?(/.*)?$')
		for uri in uris:
			if msg:
				self.message ('Trying to download file %s' % uri)
				self.markDatafile ()
			umatch = uparse.match (uri)
			if umatch is None:
				continue
			(proto, host, port, path) = umatch.groups ()
			if not proto in ('http', 'https'):
				continue
			if not port is None:
				port = int (port[1:])
			if path is None:
				path = '/'
			try:
				if msg:
					self.resumeDatafile ()
					self.message ('Download starting')
					count = 0
				if timeout:
					signal.alarm (timeout)
				if proto == 'http':
					http = httplib.HTTPConnection (host, port)
				else:
					http = httplib.HTTPSConnection (host, port)
				http.putrequest ('GET', path)
				http.endheaders ()
				resp = http.getresponse ()
				if msg:
					data = ''
					while self.active:
						chunk = resp.read (8192)
						if chunk == '':
							break
						data += chunk
						count += len (chunk)
						kbyte = (count + 1023) / 1024
						if kbyte < 1000:
							size = '%d' % kbyte
						else:
							size = '%d.%03d' % (kbyte / 1000, kbyte % 1000)
						self.resumeDatafile ()
						self.message ('Downloading %s kByte' % size)
				else:
					data = resp.read ()
				resp.close ()
				rc = data
				if not checksum is None:
					datacheck = agn.hash_md5 ()
					datacheck.update (rc)
					if datacheck.hexdigest () != checksum:
						rc = None
				if msg:
					if rc is None:
						self.message ('Failed to download %s' % uri)
					else:
						self.message ('Download of %s successful' % uri)
			finally:
				if timeout:
					signal.alarm (0)
			if not rc is None or not self.active:
				break
		return rc
	#}}}
	def parseXML (self, xml): #{{{
		try:
			dom = parseString (xml)
			p = {}
		except:
			p = None
		if not p is None:
			start = dom.documentElement
			parse (p, start, start.tagName)
		return p
	#}}}
	def helper (self, *args): #{{{
		base = '/home'
		helpfn = os.path.sep.join ([base, 'openemm', 'bin', 'updater'])
		if not os.access (helpfn, os.X_OK):
			helpfn = None
			last = None
			for oe in [_e for _e in os.listdir (base) if _e.lower ().startswith ('openemm')]:
				temp = os.path.sep.join ([base, oe, 'bin', 'updater'])
				if os.access (temp, os.X_OK):
					try:
						st = os.stat (temp)
						if last is None or last[stat.ST_CTIME] < st[stat.ST_CTIME]:
							helpfn = temp
							last = st
					except OSError:
						pass
		rc = False
		if not helpfn is None:
			cmd = helpfn
			for arg in args:
				cmd += ' "' + arg.replace ('"', '\\"') + '"'
			n = self.system (cmd)
			if not n:
				rc = True
		return rc
	#}}}
	def start (self): #{{{
		self.removeDatafile ()
		state = 0
		if agn.iswin:
			system = 'windows'
		else:
			system = 'linux'
		vxml = None
		oldVersion = None
		curVersion = None
		urls = None
		checksum = None
		distpath = None
		properties = None
		upgraded = False
		while self.active:
			if state == 0: # startup {{{
				self.message ('Update process starting up')
				if self.properties is None:
					self.fail ('Failed to read current active properties')
			#}}}
			elif state == 1: # current version {{{
				self.message ('Trying to determinate current running version')
				if self.messages is not None and 'logon.title' in self.messages:
					mversion = self.messages['logon.title']
					pattern = 'AGNITAS OpenEMM (.+)$'
				elif 'mailgun.ini.mailer' in self.properties:
					mversion = self.properties['mailgun.ini.mailer']
					pattern = 'OpenEMM V(.+)$'
				else:
					mversion = None
					pattern = None
				if mversion and pattern:
					vers = re.compile (pattern)
					match = vers.match (mversion)
					if not match is None:
						oldVersion = match.groups ()[0]
						self.message ('Found "%s" as current active version' % oldVersion)
						self.putenv ('OLD_VERSION', oldVersion)
					else:
						self.fail ('Invalid version string "%s" for "%s" found in properties' % (mversion, pattern))
				else:
					self.fail ('No version information in properties found')
			#}}}
			elif state == 2: # remote version {{{
				self.message ('Looking for new version on remote server (this may take some time)')
				vinfo = self.httpGet (versionURL)
				if not vinfo:
					self.fail ('Remote version information could not be retreived')
				vxml = self.parseXML (vinfo)
				if vxml is None:
					self.fail ('Invalid version information form remote server')
				else:
					try:
						curVersion = vxml['openemm.%s.base.version' % system]
						chksumname = 'openemm.%s.base.checksum' % system
						if vxml.has_key (chksumname):
							checksum = vxml[chksumname]
						self.message ('Found version %s on server' % curVersion)
						self.putenv ('NEW_VERSION', curVersion)
					except KeyError:
						self.fail ('No version information available for %s' % system)
			#}}}
			elif state == 3: # version check {{{
				if oldVersion == curVersion:
					self.message ('No new version available, keep system running')
					state = -9
				else:
					self.markDatafile ()
					n = 10
					while n > 0:
						self.resumeDatafile ()
						if n > 1:
							extra = 'in %d seconds' % n
						else:
							extra = 'now'
						self.message ('Current version is %s, new version %s, starting update %s' % (oldVersion, curVersion, extra))
						time.sleep (1)
						n -= 1
			#}}}
			elif state == 4: # determinate URLs {{{
				try:
					urls = vxml['openemm.%s.base.url' % system].split ()
					random.shuffle (urls)
				except KeyError:
					self.fail ('No URLs for distribution found, aborting')
			#}}}
			elif state == 5: # fetch distribution {{{
				if urls:
					self.message ('Trying to fetch the new version from one of these URLs:')
					for url in urls:
						self.message ('     %s' % url)
					self.message ('Be patient, this may take several minutes ...')
					dist = self.httpGet (urls, checksum = checksum, timeout = 30 * 60, msg = True)
				else:
					dist = None
				if dist:
					try:
						fname = vxml['openemm.%s.base.filename' % system]
					except KeyError:
						fname = (urls[0].split ('/'))[-1]
					if agn.iswin:
						try:
							drive = os.environ['HOMEDRIVE']
						except KeyError:
							drive = 'C:'
						distpath = drive + os.path.sep + fname
					else:
						distpath = '/tmp/%s' % fname
					self.message ('Trying to store the new version to %s' % distpath)
					try:
						fd = open (distpath, 'wb')
						fd.write (dist)
						fd.close ()
					except IOError, e:
						self.fail ('Failed to write distribution to %s: %s' % (distpath, `e.args`))
				else:
					self.fail ('Unable to fetch distribution from: %s' % ', '.join (urls))
			#}}}
			elif state == 6: # stop current instance {{{
				self.message ('Stopping running instance')
				if self.system ('/home/openemm/bin/openemm.sh stop'):
					self.fail ('Failed to stop running instance')
				properties = [Property ('/home/openemm/webapps/openemm/WEB-INF/classes/emm.properties'),
					      Property ('/home/openemm/webapps/openemm/WEB-INF/classes/cms.properties')]
			#}}}
			elif state == 7: # rename current installation and create new directory {{{
				self.message ('Renaming current version to keep as archive')
				if not self.helper ('archive', oldVersion):
					self.fail ('Unable to archive current installation')
				elif not self.helper ('create'):
					self.fail ('Unable to create new directory')
				else:
					try:
						os.chdir (agn.base)
					except OSError, e:
						self.fail ('Failed to change to newly created directory %s: %s' % (agn.base, `e.args`))
			#}}}
			elif state == 8: # unpack new version {{{
				self.message ('Unpacking new version')
				if not self.helper ('unpack', distpath):
					self.fail ('Unable to unpack distritbution to new path')
				elif not self.helper ('permissions'):
					self.fail ('Unable to set correct permissions')
				else:
					self.message ('New version unpacked')
			#}}}
			elif state == 9: # Scan for DB updates {{{
				self.message ('Searching for database updates from %s to %s' % (oldVersion, curVersion))
				emptyUpdates = {}
				try:
					fd = open (os.path.sep.join ([agn.base, 'USR_SHARE', 'empty-updates.txt']))
					for line in [_l.strip () for _l in fd.readlines () if not _l.startswith ('#')]:
						parts = line.split ('-')
						if len (parts) == 2:
							emptyUpdates[parts[0]] = parts[1]
					fd.close ()
				except IOError:
					pass
				updates = [_f for _f in os.listdir ('USR_SHARE') if _f.startswith ('update_openemm-') and _f.endswith ('.sql')]
				dbVersion = oldVersion
				while dbVersion != curVersion:
					nextUpdate = None
					nextVersion = None
					for upd in updates:
						uf = upd[15:-4].split ('-')
						if len (uf) == 2 and uf[0] == dbVersion:
							nextUpdate = upd
							nextVersion = uf[1]
							break
					if nextUpdate is None:
						try:
							nextUpdate = emptyUpdates[dbVersion]
						except KeyError:
							nextUpdate = None
						if nextUpdate is None:
							self.fail ('Unable to find SQL update from %s to %s' % (dbVersion, curVersion))
							break
						dbVersion = nextUpdate
						continue
					ok = False
					sqlfname = os.path.sep.join ([agn.base, 'USR_SHARE', nextUpdate])
					if os.path.isfile (sqlfname):
						cmd = 'mysql -u "%s" "--password=%s" -B -e "source %s" %s' % (agn.dbuser, agn.dbpass, sqlfname, agn.dbdatabase)
						if self.system (cmd) != 0:
							self.fail ('Update file %s failed' % sqlfname)
						else:
							ok = True
					else:
						self.fail ('Update file %s not found' % sqlfname)
					if ok:
						self.message ('Database updated from %s to %s' % (dbVersion, nextVersion))
						dbVersion = nextVersion
					else:
						self.fail ('Database update from %s to %s failed' % (dbVersion, nextVersion))
						break
			#}}}
			elif state == 10: # Start optional update script {{{
				if properties:
					self.message ('Syncing property files')
					for prop in properties:
						prop.sync (curVersion)
				pppath = os.path.sep.join ([agn.base, 'USR_SHARE', 'upgrade-postproc.sh'])
				if os.access (pppath, os.F_OK):
					self.message ('Starting post process script')
					if not self.helper ('script', pppath):
						self.fail ('Failed to postprocess')
			#}}}
			elif state == 11: # Restart Openemm {{{
				self.message ('Starting new instance')
				if self.system ('/home/openemm/bin/openemm.sh start'):
					self.fail ('Failed to start new instance')
				else:
					self.message ('New instance is running')
					upgraded = True
			#}}}
			elif state == 12: # Move USR_SHARE to destination #{{{
				self.message ('Install document files')
				if not self.helper ('share', curVersion):
					self.fail ('Failed to install document files')
				else:
					self.message ('Documents for %s installed' % curVersion)
			#}}}
			elif state == 13: # Waiting for OpenEMM to come up {{{
				self.markDatafile ()
				n = 10
				while n > 0:
					self.resumeDatafile ()
					if n > 1:
						extra = 'in %d seconds' % n
					else:
						extra = 'now'
					self.message ('New version should be ready %s' % extra)
					time.sleep (1)
					n -= 1
			#}}}
			else: # End {{{
				self.stop ()
			#}}}
			state += 1
		if None in (oldVersion, curVersion) or oldVersion != curVersion:
			try:
				updpath = os.path.sep.join ([agn.base, 'USR_SHARE', 'UPDATE.txt'])
				fd = open (updpath, 'r')
				for line in fd.readlines ():
					self.message (line.strip ())
				fd.close ()
			except IOError, e:
				agn.log (agn.LV_INFO, 'update', 'Update file %s not readable: %r' % (updpath, e.args))
		if distpath:
			try:
				os.unlink (distpath)
			except OSError:
				pass
		self.final (upgraded and 'new' or 'old')
	#}}}
term = False
class Request (BaseHTTPServer.BaseHTTPRequestHandler):
	template = None
	pages = {}

	def log_message(self, fmt, *args): #{{{
		agn.log (agn.LV_DEBUG, 'log', '[%s] %s' % (self.address_string (), fmt % args))
	#}}}
	def mergedVariables (self, variables): #{{{
		v = {}
		try:
			host = self.headers['host']
		except KeyError:
			host = 'localhost'
		p = host.split (':')
		v['host'] = p[0]
		if len (p) == 2:
			v['port'] = p[1]
		else:
			v['port'] = '80'
		if variables:
			for (key, value) in variables.items ():
				v[key] = value
		return v
	#}}}
	def answerStatic (self, mtype, content): #{{{
		self.send_response (200)
		self.send_header ('Connection', 'close')
		self.send_header ('Content-Type', mtype)
		self.send_header ('Content-Length', '%d' % len (content))
		self.end_headers ()
		self.wfile.write (content)
		self.wfile.flush ()
	#}}}
	def answer (self, code, variables = None): #{{{
		if code == 200:
			data = Request.template.fill (self.mergedVariables (variables))
		else:
			data = ''
		self.send_response (code)
		self.send_header ('Connection', 'close')
		self.send_header ('Content-Type', 'text/html')
		self.send_header ('Content-Length', '%d' % len (data))
		self.end_headers ()
		if data:
			self.wfile.write (data)
		self.wfile.flush ()
	#}}}
	def readData (self): #{{{
		fname = os.path.sep.join ([agn.base, 'conf', 'upgrade', 'upgrade.template'])
		try:
			fd = open (fname)
			content = fd.read ()
			fd.close ()
		except IOError:
			content = ''
		Request.template = agn.Template (content)
		try:
			path = os.path.sep.join ([agn.base, 'conf', 'upgrade'])
			for (fpath, fname) in [(os.path.sep.join ([path, _f]), _f) for _f in os.listdir (path) if not _f.startswith ('.') and _f != 'upgrade.template']:
				try:
					fd = open (fpath)
					content = fd.read ()
					fd.close ()
					page = agn.struct ()
					page.content = content
					page.mtype = mimetypes.guess_type (fname)[0]
					if not page.mtype:
						page.mtype = 'text/html'
					Request.pages[fname] = page
				except IOError:
					pass
		except OSError:
			pass
	#}}}
	def do_GET (self): #{{{
		global	datafile, term

		if Request.template is None:
			self.readData ()
		path = self.path
		n = path.find ('?')
		if n != -1:
			query = cgi.parse_qs (path[n + 1:], True)
			path = path[:n]
		else:
			query = None
		if path == '/':
			try:
				fd = open (datafile)
				data = fd.read ()
				fd.close ()
			except IOError:
				data = ''
			report = []
			done = False
			status = 'unset'
			for line in data.split ('\n'):
				if line:
					cmd = line[0]
					line = line[1:]
					if cmd in ('.', '!', '>'):
						r = agn.struct ()
						r.id = cmd
						if line:
							r.text = line
						else:
							r.text = '&nbsp;'
						report.append (r)
					elif cmd == 'X':
						done = True
						status = line
			vrs = {	'report': report,
				'done': int (done),
				'status': status
			       }
			self.answer (200, vrs)
			if done:
				signal.alarm (30)
		else:
			try:
				page = Request.pages[os.path.basename (path)]
				self.answerStatic (page.mtype, page.content)
			except KeyError:
				self.answer (404, None)
	#}}}
#class Server (BaseHTTPServer.SocketServer.ThreadingMixIn, BaseHTTPServer.HTTPServer):
class Server (BaseHTTPServer.HTTPServer):
	def __init__ (self, ipaddrs): #{{{
		BaseHTTPServer.HTTPServer.__init__ (self, ('', 8044), Request)
		self.acc = ipaddrs
	#}}}
	def verify_request(self, request, client_address): #{{{
		if self.acc and not client_address[0] in self.acc:
			agn.log (agn.LV_WARNING, 'request', 'Denied connection detected: ' + client_address[0])
			return False
		return True
	#}}}

pid = -1
ips = ['127.0.0.1']
opts = getopt.getopt (sys.argv[1:], 'i:')
for opt in opts[0]:
	if opt[0] == '-i':
		ips.append (opt[1])
agn.lockpath = '/var/tmp'
agn.lock ()
pid = os.fork ()
if pid == 0:
	upgrade = None
	
	def updhandler (sig, stack):
		global	upgrade

		if upgrade:
			if sig in (signal.SIGTERM, signal.SIGINT):
				upgrade.stop ()

	signal.signal (signal.SIGTERM, updhandler)
	signal.signal (signal.SIGINT, updhandler)
	signal.signal (signal.SIGALRM, updhandler)
	signal.signal (signal.SIGHUP, signal.SIG_IGN)
	upgrade = Upgrade ()
	upgrade.start ()
else:
	def webhandler (sig, stack):
		global	term
		
		term = True

	agn.log (agn.LV_INFO, 'upgrade', 'Starting up')
	signal.signal (signal.SIGTERM, webhandler)
	signal.signal (signal.SIGINT, webhandler)
	signal.signal (signal.SIGALRM, webhandler)
	signal.signal (signal.SIGHUP, signal.SIG_IGN)
	server = Server (None)
	while not term:
		server.handle_request ()
	time.sleep (2)
	if pid > 0:
		os.kill (pid, signal.SIGTERM)
	try:
		os.unlink (datafile)
	except OSError, e:
		agn.log (agn.LV_ERROR, 'upgrade', 'Failed to remove datafile %s: %s' % (datafile, `e.args`))
	agn.log (agn.LV_INFO, 'upgrade', 'Going down')
	agn.unlock ()
