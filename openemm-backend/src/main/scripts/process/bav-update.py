#!/usr/bin/env python
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
import	os, signal, time, errno, socket
import	email.Message, email.Header, email.Charset
import	StringIO, codecs
import	agn
agn.require ('2.0.0')
agn.loglevel = agn.LV_INFO
#

if not agn.iswin:
	try:
		import	smenable
	except ImportError:
		smenable = None
else:
	smenable = None
#
delay = 180

configFilename = agn.base + os.sep + 'var' + os.sep + 'spool' + os.sep + 'bav' + os.sep + 'bav.conf'
localFilename = agn.base + os.sep + 'conf' + os.sep + 'bav' + os.sep + 'bav.conf-local'
arDirectory = agn.base + os.sep + 'var' + os.sep + 'spool' + os.sep + 'bav'
updateLog = agn.base + os.sep + 'var' + os.sep + 'run' + os.sep + 'bav-update.log'
mailBase = '/etc/mail'
#
charset = 'UTF-8'

_csetname = charset.lower ()
_cset = email.Charset.CHARSETS[_csetname]
email.Charset.CHARSETS[_csetname] = (email.Charset.QP, email.Charset.QP, _cset[2])
del _csetname, _cset

def fileReader (fname):
	fd = open (fname, 'r')
	rc = [agn.chop (line) for line in fd.readlines () if not line[0] in '\n#']
	fd.close ()
	return rc

class Autoresponder:
	def __init__ (self, rid, timestamp, sender, subject, text, html):
		self.rid = rid
		self.timestamp = timestamp
		self.sender = sender
		self.subject = subject
		self.text = self._encode (text)
		self.html = self._encode (html)
		self.fname = arDirectory + os.sep + 'ar_%s.mail' % rid
		self.limit = arDirectory + os.sep + 'ar_%s.limit' % rid

	def _encode (self, s):
		if s and charset != 'UTF-8':
			temp = StringIO.StringIO (s)
			convert = codecs.EncodedFile (temp, charset, 'UTF-8')
			try:
				s = convert.read ()
			except Exception, e:
				agn.log (agn.LV_ERROR, 'auto', 'Failed to convert autoresponder text for %s %s' % (self.rid, `e.args`))
		return s

	def _mkheader (self, s):
		rc = ''
		for w in s.split ():
			needEncode = False
			for c in w:
				if ord (c) > 127:
					needEncode = True
					break
			if rc:
				rc += ' '
			if needEncode:
				h = email.Header.Header (w, charset)
				rc += h.encode ()
			else:
				rc += w
		return rc
	
	def _prepmsg (self, m, isroot, mtype, pl):
		m.set_payload (pl)
		m.set_charset (charset)
		m.set_type (mtype)
		if not isroot:
			del m['mime-version']

	def writeFile (self):
		msg = email.Message.Message ()
		if self.sender:
			msg['From'] = self._mkheader (self.sender)
		if self.subject:
			msg['Subject'] = self._mkheader (self.subject)
		if not self.html:
			self._prepmsg (msg, True, 'text/plain', self.text)
		else:
			text = email.Message.Message ()
			html = email.Message.Message ()
			self._prepmsg (text, False, 'text/plain', self.text)
			self._prepmsg (html, False, 'text/html', self.html)
			msg.set_type ('multipart/alternative')
			msg.attach (text)
			msg.attach (html)
		try:
			fd = open (self.fname, 'w')
			fd.write (msg.as_string (False) + '\n')
			fd.close ()
		except IOError, e:
			agn.log (agn.LV_ERROR, 'auto', 'Unable to write message %s %s' % (self.fname, `e.args`))
	
	def removeFile (self):
		try:
			os.unlink (self.fname)
		except OSError, e:
			agn.log (agn.LV_ERROR, 'auto', 'Unable to remove file %s %s' % (self.fname, `e.args`))
		try:
			os.unlink (self.limit)
		except OSError, e:
			agn.log (agn.LV_ERROR, 'auto', 'Unable to remove file %s %s' % (self.limit, `e.args`))
#
class Data:
	def __init__ (self):
		self.fixdomain = 'localhost'
		self.domains = []
		self.prefix = 'ext_'
		self.last = None
		self.autoresponder = []
		self.mtdom = {}

		self.sendmailFree = False
		if agn.iswin:
			self.sendmailFree = True
		else:
			if not smenable is None:
				sm = smenable.SMCtrl ()
				if sm.valid and not sm.enabled ():
					self.sendmailFree = True
				sm.done ()
		self.readMailertable ()
		try:
			files = os.listdir (arDirectory)
			for fname in files:
				if len (fname) > 8 and fname[:3] == 'ar_' and fname[-5:] == '.mail':
					rid = fname[3:-5]
					self.autoresponder.append (Autoresponder (rid, 0, None, None, None, None))
		except OSError, e:
			agn.log (agn.LV_ERROR, 'data', 'Unable to read directory %s %s' % (arDirectory, `e.args`))
		self.updateCount = 0
	
	def readMailertable (self):
		self.domains = []
		self.mtdom = {}

		if self.sendmailFree:
			self.domains = [self.fixdomain]
			me = socket.getfqdn ()
			if me:
				self.domains.append (me)
			db = agn.DBaseID ()
			if not db is None:
				c = db.cursor ()
				if not c is None:
					for r in c.query ('SELECT mailloop_domain FROM company_tbl'):
						if r[0] and not r[0] in self.domains:
							self.domains.append (r[0])
					c.close ()
				db.close ()
			return
		try:
			for line in fileReader (mailBase + '/mailertable'):
				parts = line.split ()
				if len (parts) > 0 and parts[0][0] != '.':
					self.domains.append (parts[0])
					self.mtdom[parts[0]] = 0
		except IOError, e:
			agn.log (agn.LV_ERROR, 'data', 'Unable to read mailertable %s' % `e.args`)
		try:
			for line in fileReader (mailBase + '/relay-domains'):
				if self.mtdom.has_key (line):
					self.mtdom[line] += 1
				else:
					agn.log (agn.LV_ERROR, 'data', 'We relay domain "%s" without catching it in mailertable' % line)
			for key in self.mtdom.keys ():
				if self.mtdom[key] == 0:
					agn.log (agn.LV_ERROR, 'data', 'We define domain "%s" in mailertable, but do not relay it' % key)
		except IOError, e:
			agn.log (agn.LV_ERROR, 'data', 'Unable to read relay-domains %s' % `e.args`)
		if not self.domains:
			self.domains.append (self.fixdomain)
	
	def removeUpdateLog (self):
		try:
			os.unlink (updateLog)
		except OSError, e:
			if e.args[0] != errno.ENOENT:
				agn.log (agn.LV_ERROR, 'data', 'Failed to remove old update log %s %s' % (updateLog, `e.args`))
		
	def done (self):
		self.removeUpdateLog ()

	def readMailFiles (self):
		rc = ''

		if self.sendmailFree:
			return rc
		try:
			for line in fileReader (mailBase + '/local-host-names'):
				rc += '@%s\taccept:rid=local\n' % line
		except IOError, e:
			agn.log (agn.LV_ERROR, 'data', 'Unable to read local-host-names %s' % `e.args`)
		try:
			lhost = socket.getfqdn ()
			if lhost:
				rc += '@%s\taccept:rid=local\n' % lhost
		except Exception, e:
			agn.log (agn.LV_ERROR, 'data', 'Unable to find local FQDN %s' % `e.args`)
		try:
			for line in fileReader (mailBase + '/virtusertable'):
				parts = line.split ()
				if len (parts) == 2:
					rc += '%s\taccept:rid=virt,fwd=%s\n' % (parts[0], parts[1])
		except IOError, e:
			agn.log (agn.LV_ERROR, 'data', 'Unable to read virtusertable %s' % `e.args`)
		return rc

	def readDatabase (self, auto):
		rc = ''
		db = agn.DBase ()
		if not db:
			agn.log (agn.LV_ERROR, 'data', 'Unable to create database connection')
			raise agn.error ('readDatabase.open')
		try:
			i = db.cursor ()
			if not i:
				agn.log (agn.LV_ERROR, 'data', 'Unable to get database cursor')
				raise agn.error ('readDatabase.cursor')
			try:
				ctab = {}
				query = 'SELECT company_id, mailloop_domain FROM company_tbl WHERE status = \'active\''
				missing = []
				for record in i.query (query):
					if record[1]:
						ctab[record[0]] = record[1]
					else:
						missing.append (record[0])
				if missing:
					missing.sort ()
					agn.log (agn.LV_VERBOSE, 'data', 'Missing mailloop_domain for %s' % ', '.join ([str (m) for m in missing]))

				query = 'SELECT rid, shortname, company_id, forward_enable, forward, ar_enable, ar_sender, ar_subject, ar_text, ar_html, subscribe_enable, mailinglist_id, form_id, date_format(change_date,\'%Y%m%d%H%i%S\') FROM mailloop_tbl'
				for record in i.query (query):
					subscribe_enable = None
					mailinglist_id = None
					form_id = None

					(rid, shortname, company_id, forward_enable, forward, ar_enable, ar_sender, ar_subject, ar_text, ar_html, subscribe_enable, mailinglist_id, form_id, timestamp) = record
					if not rid is None:
						rid = str (rid)
						domains = None

						if timestamp is None:
							timestamp = time.time ()
						else:
							timestamp = int (timestamp)
						if ar_enable and not ar_text:
							ar_enable = False
						if ar_enable:
							def nvl (s):
								if s is not None:
									return str (s)
								return s
							auto.append (Autoresponder (rid, timestamp, ar_sender, ar_subject, nvl (ar_text), nvl (ar_html)))
						if domains is None:
							try:
								cdomain = ctab[company_id]
								if self.domains and cdomain != self.domains[0]:
									domains = [self.domains[0]]
								else:
									domains = []

								if agn.iswin and not cdomain in self.domains:
									self.domains.append (cdomain)
								if not self.domains or cdomain in self.domains:
									domains.append (cdomain)

								else:

									agn.log (agn.LV_WARNING, 'data', 'Domain "%s" not known' % cdomain)
							except KeyError:
								agn.log (agn.LV_DEBUG, 'data', 'No domain for company found, further processing')
						if domains is None:
							domains = self.domains
						elif not self.domains[0] in domains:
							domains.insert (0, self.domains[0])
						extra = 'rid=%s' % rid
						if company_id:
							extra += ',cid=%d' % company_id
						if forward_enable and forward:
							extra += ',fwd=%s' % forward
						if ar_enable:
							extra += ',ar=%s' % rid
						if subscribe_enable and mailinglist_id and form_id:
							extra += ',sub=%d:%d' % (mailinglist_id, form_id)
						for domain in domains:
							line = '%s%s@%s\taccept:%s' % (self.prefix, rid, domain, extra)
							agn.log (agn.LV_VERBOSE, 'data', 'Add line: ' + line)
							rc += line + '\n'
			finally:
				i.close ()
		finally:
			db.close ()
		return rc
	
	def readLocalFiles (self):
		rc = ''

		if agn.iswin:
			return rc
		try:
			for line in fileReader (localFilename):
				rc += line + '\n'
		except IOError, e:
			agn.log (agn.LV_VERBOSE, 'local', 'Unable to read local file %s %s' % (localFilename, `e.args`))
		return rc
	
	def updateAutoresponder (self, auto):
		newlist = []
		for new in auto:
			found = None
			for old in self.autoresponder:
				if new.rid == old.rid:
					found = old
					break
			if not found or new.timestamp > found.timestamp:
				new.writeFile ()
				newlist.append (new)
			else:
				newlist.append (found)
		for old in self.autoresponder:
			found = False
			for new in newlist:
				if old.rid == new.rid:
					found = True
					break
			if not found:
				old.removeFile ()
		self.autoresponder = newlist
	
	def renameFile (self, oldFile, newFile):

		if agn.iswin:
			try:
				os.unlink (newFile)
			except OSError:
				pass
		try:
			os.rename (oldFile, newFile)
		except OSError, e:
			agn.log (agn.LV_ERROR, 'data', 'Unable to rename %s to %s %s' % (oldFile, newFile, `e.args`))
			try:
				os.unlink (oldFile)
			except OSError, e:
				agn.log (agn.LV_WARNING, 'data', 'Failed to remove temp. file %s %s' % (oldFile, `e.args`))
			raise agn.error ('renameFile')

	def updateConfigfile (self, new):
		if new != self.last:
			temp = configFilename + '.%d' % os.getpid ()
			try:
				fd = open (temp, 'w')
				fd.write (new)
				fd.close ()
				self.renameFile (temp, configFilename)
				self.last = new
			except IOError, e:
				agn.log (agn.LV_ERROR, 'data', 'Unable to write %s %s' % (temp, `e.args`))
				raise agn.error ('updateConfigfile.open')

	def writeUpdateLog (self, text):
		try:
			fd = open (updateLog, 'a')
			fd.write ('%d %s\n' % (self.updateCount, text))
			fd.close ()
			self.updateCount += 1
		except IOError, e:
			agn.log (agn.LV_ERROR, 'data', 'Unable to write update log %s %s' % (updateLog, `e.args`))

	def update (self, forced):
		try:
			auto = []
			new = self.readMailFiles ()
			new += self.readDatabase (auto)
			new += self.readLocalFiles ()
			self.updateAutoresponder (auto)
			self.updateConfigfile (new)
			updateText = 'success'
		except agn.error, e:
			agn.log (agn.LV_ERROR, 'data', 'Update failed: ' + e.msg)
			updateText = 'failed: ' + e.msg
		if forced:
			self.writeUpdateLog (updateText)
#
running = True
reread = True


def handler (sig, stack):
	global	running, reread
	
	if not agn.iswin and sig == signal.SIGUSR1:
		reread = True
	else:
		running = False

signal.signal (signal.SIGINT, handler)
signal.signal (signal.SIGTERM, handler)
if not agn.iswin:
	signal.signal (signal.SIGUSR1, handler)
	signal.signal (signal.SIGHUP, signal.SIG_IGN)
	signal.signal (signal.SIGPIPE, signal.SIG_IGN)

agn.log (agn.LV_INFO, 'main', 'Starting up')
agn.lock ()
data = Data ()
while running:
	forcedUpdate = reread
	reread = False
	data.update (forcedUpdate)
	n = delay
	while n > 0 and running and not reread:

		if agn.iswin and agn.winstop ():
			running = False
			break
		time.sleep (1)
		n -= 1
data.done ()
agn.unlock ()
agn.log (agn.LV_INFO, 'main', 'Going down')
