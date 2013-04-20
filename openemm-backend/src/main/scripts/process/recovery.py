#!/usr/bin/env python
#	-*- mode: python; mode: fold -*-
#
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
import	sys, os, getopt, time, gzip, re, codecs
import	agn
agn.loglevel = agn.LV_DEBUG
agn.require ('2.2.3')
#
class Mailing (agn.struct): #{{{
	meta = agn.mkpath (agn.base, 'var', 'spool', 'META')
	archive = agn.mkpath (agn.base, 'var', 'spool', 'ARCHIVE')
	def __init__ (self, statusID, statusField, mailingID, companyID, check): #{{{
		self.statusID = statusID
		self.statusField = statusField
		self.mailingID = mailingID
		self.companyID = companyID
		self.check = check
		self.seen = None
		self.pattern = None
		self.tempFile = agn.mkpath (self.meta, '.recover-%d.temp' % self.statusID)
		self.recoverFile = agn.mkpath (self.meta, 'recover-%d.list' % self.statusID)
		self.count = 0
		self.active = True
		self.current = 0
		self.last = 0
	#}}}
	def done (self): #{{{
		for path in [self.tempFile, self.recoverFile]:
			if os.path.isfile (path):
				os.unlink (path)
	#}}}
	def __cmp__ (self, other): #{{{
		return cmp (self.statusID, other.statusID)
	#}}}
	def __parseXML (self, path): #{{{
		pattern = re.compile ('<receiver customer_id="([0-9]+)"')
		fd = gzip.open (path, 'r')
		try:
			current = set ()
			mode = 0
			for line in fd.readlines ():
				if mode == 0:
					if '<receivers>' in line:
						mode = 1
				elif mode == 1:
					mtch = pattern.search (line)
					if not mtch is None:
						current.add (int (mtch.groups ()[0]))
			self.seen.update (current)
		except IOError, e:
			agn.log (agn.LV_WARNING, 'parse', 'Failed to parse "%s": %r' % (path, e.args))
		fd.close ()
	#}}}
	def __collect (self, path, remove): #{{{
		files = os.listdir (path)
		for fname in [_f for _f in files if not self.pattern.match (_f) is None]:
			fpath = agn.mkpath (path, fname)
			if remove:
				try:
					os.unlink (fpath)
					agn.log (agn.LV_DEBUG, 'collect', 'File "%s" removed' % fpath)
				except OSError, e:
					agn.log (agn.LV_ERROR, 'collect', 'Failed to remove file "%s": %r' % (fpath, e.args))
			elif fname.endswith ('.xml.gz'):
				self.__parseXML (fpath)
	#}}}
	def collectSeen (self): #{{{
		self.seen = set ()
		self.pattern = re.compile ('^AgnMail(-[0-9]+)?=D[0-9]{14}=%d=%d=[^=]+=liaMngA\\.(stamp|final|xml\\.gz)$' % (self.companyID, self.mailingID))
		self.__collect (self.meta, True)
		for sdir in self.check:
			spath = agn.mkpath (self.archive, sdir)
			if os.path.isdir (spath):
				self.__collect (spath, False)
	#}}}
	def createFilelist (self): #{{{
		if self.seen:
			fd = open (self.tempFile, 'w')
			fd.write ('\n'.join ([str (_s) for _s in self.seen]) + '\n')
			fd.close ()
			os.rename (self.tempFile, self.recoverFile)
	#}}}
	def setGeneratedCount (self, count): #{{{
		self.count = count
		agn.log (agn.LV_INFO, 'mailing', 'Seen %d customers, logged %d' % (len (self.seen), self.count))
	#}}}
#}}}
class Recovery: #{{{
	def __init__ (self, maxAge, startUpDelay, restrictToMailings, doit): #{{{
		self.maxAge = maxAge
		self.startUpDelay = startUpDelay
		self.restrictToMailings = restrictToMailings
		self.doit = doit
		self.db = None
		self.cursor = None
		self.mailings = []
		self.mailingInfo = {}
	#}}}
	def done (self): #{{{
		for m in self.mailings:
			m.done ()
		self.dbClose ()
	#}}}
	def setup (self): #{{{
		return self.dbOpen ()
	#}}}
	def dbOpen (self, force = False): #{{{
		if force or self.cursor is None:
			self.dbClose ()
		if self.db is None:
			self.db = agn.DBaseID ()
			if not self.db is None:
				self.cursor = self.db.cursor ()
			else:
				self.cursor = None
		return not self.cursor is None
	#}}}
	def dbClose (self, commit = True): #{{{
		if not self.db is None:
			if not self.cursor is None:
				self.cursor.sync (commit)
				self.cursor.close ()
			self.db.close ()
		self.db = None
		self.cursor = None
	#}}}
	def __makeRange (self, start, end): #{{{
		rc = []
		s = time.mktime ((start[0], start[1], start[2], 12, 0, 0, -1, -1, -1))
		e = time.mktime ((end[0], end[1], end[2], 12, 0, 0, -1, -1, -1))
		while s <= e:
			ts = time.gmtime (s)
			rc.append ('%04d%02d%02d' % (ts.tm_year, ts.tm_mon, ts.tm_mday))
			s += 24 * 60 * 60
		return rc
	#}}}
	def __toiso (self, s): #{{{
		try:
			if s is None:
				return s
			return codecs.encode (unicode (s,'UTF8'), 'ISO-8859-1')
		except UnicodeEncodeError:
			return s
	#}}}
	def __mail (self, mailingID): #{{{
		try:
			rc = self.mailingInfo[mailingID]
		except KeyError:
			r = self.cursor.querys ('SELECT company_id, shortname, deleted FROM mailing_tbl WHERE mailing_id = :mid', {'mid': mailingID})
			if r is not None:
				rc = agn.struct (companyID = r[0], name = self.__toiso (r[1]), deleted = (r[2] != 0))
			else:
				rc = agn.struct (companyID = 0, name = '#%d not found' % mailingID, deleted = False)
			self.mailingInfo[mailingID] = rc
		return rc
	#}}}
	def __mailingName (self, mailingID): #{{{
		return self.__mail (mailingID).name
	#}}}
	def __mailingDeleted (self, mailingID): #{{{
		return self.__mail (mailingID).deleted
	#}}}
	def collectMailings (self): #{{{
		now = time.localtime ()
		expire = time.localtime (time.time () - self.maxAge * 24 * 60 * 60)
		yesterday = time.localtime (time.time () - 24 * 60 * 60)

		query = 'SELECT status_id, mailing_id FROM maildrop_status_tbl WHERE genstatus = 2 AND status_field = \'R\' AND genchange > \'%04d-%02d-%02d\'' % (expire.tm_year, expire.tm_mon, expire.tm_mday)
		check = 'SELECT count(*) FROM rulebased_sent_tbl WHERE mailing_id = :mid AND date_to_str (lastsent, \'%%Y-%%m-%%d\') = \'%04d-%02d-%02d\'' %  (yesterday.tm_year, yesterday.tm_mon, yesterday.tm_mday)
		update = 'UPDATE maildrop_status_tbl SET genstatus = 1, genchange = current_timestamp WHERE status_id = :sid'
		for (statusID, mailingID) in self.cursor.queryc (query):
			if self.restrictToMailings is None or mailingID in self.restrictToMailings:
				count = self.cursor.querys (check, {'mid': mailingID})
				if count is not None and count[0] == 1:
					agn.log (agn.LV_INFO, 'collect', 'Reactivate rule based mailing %d: %s' % (mailingID, self.__mailingName (mailingID)))
					if self.doit:
						self.cursor.update (update, {'sid': statusID})
				else:
					agn.log (agn.LV_WARNING, 'collect', 'Rule based mailing %d (%s) not reactivated as it had not been sent out yesterday' % (mailingID, self.__mailingName (mailingID)))
		if self.doit:
			self.cursor.sync ()

		query = 'SELECT status_id, mailing_id, company_id, status_field, senddate FROM maildrop_status_tbl WHERE '
		query += 'genstatus = 2 AND genchange > \'%04d-%02d-%02d\' AND status_field = \'W\'' % (expire.tm_year, expire.tm_mon, expire.tm_mday)
		for (statusID, mailingID, companyID, statusField, sendDate) in self.cursor.queryc (query):
			if self.restrictToMailings is None or mailingID in self.restrictToMailings:
				check = self.__makeRange ([sendDate.year, sendDate.month, sendDate.day], [now.tm_year, now.tm_mon, now.tm_mday])
				agn.log (agn.LV_INFO, 'collect', 'Mark mailing %d (%s) for recovery' % (mailingID, self.__mailingName (mailingID)))
				if not self.__mailingDeleted (mailingID) and self.__mailingName (mailingID):
					self.mailings.append (Mailing (statusID, statusField, mailingID, companyID, check))
		self.mailings.sort ()
		agn.log (agn.LV_INFO, 'collect', 'Found %d mailing(s) to recover' % len (self.mailings))
	#}}}
	def recoverMailings (self): #{{{
		if self.doit and self.mailings and self.startUpDelay > 0:
			agn.log (agn.LV_INFO, 'recover', 'Wait for backend to start up')
			time.sleep (self.startUpDelay)
		for m in self.mailings:
			m.collectSeen ()
			if self.doit:
				m.createFilelist ()
				count = 0
				for (totalMails, ) in self.cursor.query ('SELECT total_mails FROM mailing_backend_log_tbl WHERE status_id = :sid', {'sid': m.statusID}):
					if not totalMails is None and totalMails > count:
						count = totalMails
				m.setGeneratedCount (count)
				self.cursor.execute ('DELETE FROM mailing_backend_log_tbl WHERE status_id = :sid', {'sid': m.statusID})

				self.cursor.execute ('UPDATE maildrop_status_tbl SET genstatus = 0 WHERE status_id = :sid', {'sid': m.statusID})
				self.cursor.sync ()
			else:
				print ('%s: %d recipients already seen' % (self.__mailingName (m.mailingID), len (m.seen)))

		if self.doit:
			start = int (time.time ())
			active = True
			while active:
				active = False
				statusIDs = [_m.statusID for _m in self.mailings if _m.active]
				if not statusIDs: continue
				agn.log (agn.LV_DEBUG, 'recover', 'Still %d mailings active' % len (statusIDs))
				genStati = {}
				query = 'SELECT status_id, genstatus FROM maildrop_status_tbl WHERE status_id IN (%s)' % ', '.join ([str (_s) for _s in statusIDs])
				for (statusID, genStatus) in self.cursor.query (query):
					genStati[statusID] = genStatus
				now = int (time.time ())
				for m in self.mailings:
					try:
						genStatus = genStati[m.statusID]
					except KeyError:
						genStatus = -1
					if genStatus == 3:
						agn.log (agn.LV_INFO, 'recover', 'Mailing %d terminated as expected' % m.mailingID)
						m.active = False
					elif genStatus == 2:
						if m.last:
							current = 0
							for (currentMails, ) in self.cursor.query ('SELECT current_mails FROM mailing_backend_log_tbl WHERE status_id = :sid', {'sid': m.statusID}):
								if not currentMails is None:
									current = currentMails
							if current != m.current:
								m.current = current
								m.last = now
							else:
								if (current > 0 and m.last + 1200 < now) or \
								   (current == 0 and m.last + 3600 < now):
								   	agn.log (agn.LV_INFO, 'recover', 'Mailing %d terminated due to inactivity after %d mails' % (m.mailingID, current))
									m.active = False
						else:
							m.last = now
					elif genStatus == 1:
						if start + 1800 + 7200 < now:
							agn.log (agn.LV_INFO, 'recover', 'Mailing %d terminated while not starting up' % m.mailingID)
							m.active = False
					elif genStatus == 0:
						if start + 7200 < now:
							agn.log (agn.LV_INFO, 'recover', 'Mailing %d terminated while not getting triggered' % m.mailingID)
							m.active = False
					elif genStatus > 3:
						agn.log (agn.LV_INFO, 'recover',  'Mailing %d terminated with status %d' % (m.mailingID, genStatus))
						m.active = False
					if m.active:
						active = True
					else:
						count = 0
						for (totalMails, ) in self.cursor.query ('SELECT total_mails FROM mailing_backend_log_tbl WHERE status_id = :sid', {'sid': m.statusID}):
							if not totalMails is None:
								count = totalMails
						count += len (m.seen)
						self.cursor.execute ('UPDATE mailing_backend_log_tbl SET total_mails = :cnt, current_mails = :cnt WHERE status_id = :sid', {'sid': m.statusID, 'cnt': count})
				if active:
					time.sleep (30)
	#}}}
#}}}
if __name__ == '__main__': #{{{
	def usage (msg = None):
		sys.stderr.write ("""Syntax: %(pgm)s [-n] [-a <days>]
Function: recovers previously aborted mailings
Options:
\t-n           do not execute recovery, just print what will be done
\t-a <days>    maximum age in days to restart mailings [1]
""" % {'pgm': sys.argv[0]})
		if not msg is None:
			sys.stderr.write ('%s\n' % msg)
			sys.exit (1)
		sys.exit (0)

	def main ():
		doit = True
		maxAge = 1
		startUpDelay = 60
		mailings = None
		try:
			(opts, parm) = getopt.getopt (sys.argv[1:], '?nva:d:m:')
			for opt in opts:
				if opt[0] == '-?':
					usage ()
				elif opt[0] == '-n':
					doit = False
				elif opt[0] == '-v':
					agn.outlevel = agn.LV_DEBUG
					agn.outstream = sys.stdout
				elif opt[0] == '-a':
					agn.validate (opt[1], '[0-9]+', (lambda a: int (a) > 0, 'age must be > 0'), reason = 'Numeric value expected for age')
					maxAge = int (opt[1])
				elif opt[0] == '-d':
					startUpDelay = int (opt[1])
				elif opt[0] == '-m':
					if mailings is None:
						mailings = []
					mailings.append (int (opt[1]))
		except (getopt.GetoptError, agn.error), e:
			usage (e.msg)
		agn.lock ()
		agn.log (agn.LV_INFO, 'main', 'Starting up')
		rc = False
		rec = Recovery (maxAge, startUpDelay, mailings, doit)
		if rec.setup ():
			try:
				rec.collectMailings ()
				rec.recoverMailings ()
				rc = True
			except agn.error, e:
				agn.log (agn.LV_ERROR, 'main', 'Failed recovery: %s' % e.msg)
		else:
			agn.log (agn.LV_ERROR, 'main', 'Failed to setup recovery process')
		rec.done ()
		agn.log (agn.LV_INFO, 'main', 'Going down')
		agn.unlock ()
		if not rc:
			sys.exit (1)
	#
	main ()
#}}}
