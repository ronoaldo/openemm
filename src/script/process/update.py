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
import	sys, os, getopt, time, signal, re
import	agn
agn.require ('2.9.8')
agn.loglevel = agn.LV_INFO
#
delay = 30
#
class Update: #{{{
	def __init__ (self, path, name):
		self.path = path
		self.name = name
		self.base = os.path.basename (self.path)
		if self.path.find (os.sep) != -1:
			d = os.path.dirname (self.path)
		else:
			d = None
		n = self.base.rfind ('.')
		if n != -1:
			b = self.base[:n] + '.fail'
		else:
			b = self.base + '.fail'
		if d is None:
			self.fail = b
		else:
			self.fail = d + os.sep + b
		self.cur = 1
		self.pid = os.getpid ()
		self.opts = {}
		self.lineno = None
	
	def options (self, nopts):
		for o in nopts:
			self.opts[o[0]] = o[1]
	
	def done (self):
		pass
	
	def shouldRun (self):
		return True

	def exists (self):
		return os.access (self.path, os.F_OK)

	def renameToTemp (self):
		tfname = self.path + '.%d.%d.%d' % (self.pid, time.time (), self.cur)
		self.cur += 1
		try:
			os.rename (self.path, tfname)
			agn.log (agn.LV_INFO, 'update', 'Renamed %s to %s' % (self.path, tfname))
			time.sleep (10)
		except OSError, e:
			agn.log (agn.LV_ERROR, 'update', 'Unable to rename %s to %s: %s' % (self.path, tfname, `e.args`))
			tfname = None
		return tfname
	
	def __save (self, fname, line):
		rc = False
		try:
			fd = open (fname, 'a')
			fd.write (line + '\n')
			fd.close ()
			rc = True
		except IOError, e:
			agn.log (agn.LV_ERROR, 'update', 'Failed to write to %s: %s' % (fname, `e.args`))
		return rc
	
	def saveToFail (self, line):
		return self.__save (self.fail, line)
	
	def __logfilename (self):
		now = time.localtime (time.time ())
		return agn.logpath + os.sep + '%04d%02d%02d-%s' % (now[0], now[1], now[2], self.base)
		
	def saveToLog (self, line):
		return self.__save (self.__logfilename (), line)

	def __removeFile (self, tfname):
		try:
			os.unlink (tfname)
		except OSError, e:
			agn.log (agn.LV_ERROR, 'update', 'Unable to remove tempfile %s: %s' % (tfname, `e.args`))
		
	def moveToLog (self, tfname):
		dest = self.__logfilename ()
		try:
			rc = False
			fdi = open (tfname, 'r')
			try:
				fdo = open (dest, 'a')
				while 1:
					buf = fdi.read (65536)
					if len (buf) > 0:
						fdo.write (buf)
					else:
						break
				fdo.close ()
			except IOError, e:
				agn.log (agn.LV_ERROR, 'update', 'Unable to open output file %s: %s' % (tfname, `e.args`))
			fdi.close ()
			if rc:
				self.__removeFile (tfname)
		except IOError, e:
			agn.log (agn.LV_ERROR, 'update', 'Unable to open input file %s: %s' % (tfname, `e.args`))

	def updateStart (self, inst):
		raise agn.error ('Need to overwrite updateStart in your subclass')
	def updateEnd (self, inst):
		raise agn.error ('Need to overwrite updateEnd in your subclass')
	def updateLine (self, inst, line):
		raise agn.error ('Need to overwrite updateLine in your subclass')

	def update (self, inst):
		tfname = self.renameToTemp ()
		if tfname is None:
			return False
		try:
			fd = open (tfname, 'r')
		except IOError, e:
			agn.log (agn.LV_ERROR, 'update', 'Unable to open %s: %s' % (tfname, `e.args`))
			fd = None
		if fd is None:
			return False
		self.lineno = 0
		removeTemp = True
		rc = self.updateStart (inst)

		for line in [agn.chop (l) for l in fd.readlines ()]:
			self.lineno += 1
			if self.lineno % 10000 == 0:
				agn.log (agn.LV_INFO, 'update', '%s: Now at line %d' % (self.name, self.lineno))
			if not self.updateLine (inst, line):
				if not self.saveToFail (line):
					removeTemp = False
				rc = False
			else:
				if not self.saveToLog (line):
					removeTemp = False
		if not self.updateEnd (inst):
			rc = False
		fd.close ()
		if removeTemp:
			self.__removeFile (tfname)
		inst.sync ()
		return rc
#}}}
class UpdateInfo: #{{{
	def __init__ (self, info):
		self.info = info
		self.map = {}
		for elem in info.split ('\t'):
			parts = elem.split ('=', 1)
			if len (parts) == 2:
				self.map[parts[0]] = parts[1]
			elif len (parts) == 1:
				self.map['stat'] = elem
	
	def __getitem__ (self, var):
		if self.map.has_key (var):
			return self.map[var]
		return None
	
	def get (self, var, dflt):
		if self.map.has_key (var):
			return self.map[var]
		return dflt
#}}}
class UpdateBounce (Update): #{{{

	bouncelog = agn.base + os.sep + 'var' + os.sep + 'spool' + os.sep + 'log' + os.sep + 'extbounce.log'

	def __init__ (self, path = bouncelog):
		Update.__init__ (self, path, 'bounce')
		self.ustatus = agn.UserStatus ()
		self.mailingMap = {}
		self.dsnparse = re.compile ('^([0-9])\\.([0-9])\\.([0-9])$')
		self.igcount = None
		self.sucount = None
		self.sbcount = None
		self.hbcount = None
	
	def __todetail (self, dsn, info):
		(ignore, detail, code, typ, remark) = (False, 0, 0, 2, 'bounce')
		match = self.dsnparse.match (dsn)
		if not match is None:
			grp = match.groups ()
			code = int (grp[0]) * 100 + int (grp[1]) * 10 + int (grp[2])
			infos = UpdateInfo (info)
			stat = infos.get ('stat', '').lower ()
			mailloop = infos['mailloop']
			
			#
			# special cases
			relay = infos.get ('relay', None)
			if relay is not None:
				if relay.find ('yahoo.com') != -1:
					if code / 100 == 5 and stat.find ('service unavailable') != -1:
						detail = 511
			admin = infos['admin']
			if not admin is None:
				typ = 4
				remark = admin
			status = infos['status']
			if not status is None:
				ttyp = self.ustatus.findStatus (status)
				if not ttyp is None:
					typ = ttyp

			if detail == 0:
				if (code in (511, 571) and (stat.find ('user unknown') != -1 or not mailloop is None)) or code == 513:
					detail = 511
				elif code == 512:
					detail = 512
				elif code in (420, 421, 422, 521, 522):
					detail = 420
				elif code in (430, 530, 535):
					detail = 430
				elif code / 100 == 4:
					if code / 10 in (42, 43, 47) and (stat.find ('gray') != -1 or stat.find ('grey') != -1):
						ignore = True
					detail = 400
				elif code in (500, 511, 550, 554):
					detail = 500
				elif code / 100 == 5:
					detail = 510
				elif code == 200:
					detail = 200
					typ = 1
				elif code / 100 == 1:
					detail = 100
					typ = 1
				else:
					agn.log (agn.LV_WARNING, 'updBounce', '%s resulting in %d does not match any rule' % (dsn, code))
		return (ignore, detail, code, typ, remark)

	def __mapMailingToCompany (self, inst, mailing):
		if not self.mailingMap.has_key (mailing):
			rec = inst.querys ('SELECT company_id FROM mailing_tbl WHERE mailing_id = :mailingID', {'mailingID': mailing})
			if rec is None:
				agn.log (agn.LV_ERROR, 'updBounce', 'No company_id for mailing %d found' % mailing)
				return 0
			else:
				self.mailingMap[mailing] = rec[0]
				return rec[0]
		else:
			return self.mailingMap[mailing]
	def updateStart (self, inst):
		self.igcount = 0
		self.sucount = 0
		self.sbcount = 0
		self.hbcount = 0
		return True
	
	def updateEnd (self, inst):

		agn.log (agn.LV_INFO, 'udpBounce', 'Found %d hardbounces, %d softbounces, %d successes, %d ignored in %d lines' % (self.hbcount, self.sbcount, self.sucount, self.igcount, self.lineno))
		return True
		
	def updateLine (self, inst, line):
		parts = line.split (';', 5)
		if len (parts) != 6:
			agn.log (agn.LV_WARNING, 'updBounce', 'Got invalid line: ' + line)
			return False
		try:
			(dsn, dummy, mailing, media, customer, info) = (parts[0], int (parts[1]), int (parts[2]), int (parts[3]), int (parts[4]), parts[5])
		except ValueError:
			agn.log (agn.LV_WARNING, 'updBounce', 'Unable to parse line: ' + line)
			return False
		if mailing <= 0 or customer <= 0:
			agn.log (agn.LV_WARNING, 'updBounce', 'Got line with invalid mailing or customer: ' + line)
			return False
		(ignore, detail, dsnnr, bouncetype, bounceremark) = self.__todetail (dsn, info)
		if ignore:
			agn.log (agn.LV_DEBUG, 'updBounce', 'Ignoring line: %s' % line)
			return True
		if detail <= 0:
			agn.log (agn.LV_WARNING, 'updBounce', 'Got line with invalid detail (%d): %s' % (detail, line))
			return False
		company = self.__mapMailingToCompany (inst, mailing)
		if company <= 0:
			agn.log (agn.LV_WARNING, 'updBounce', 'Cannot map mailing %d to company for line: %s' % (mailing, line))
			return False

		logging = True
		rc = True
		if logging:
			if detail == 200:
				self.sucount += 1
			else:
				data = { 'company': company,
					 'customer': customer,
					 'detail': detail,
					 'mailing': mailing,
					 'dsn': dsnnr
				}
				try:

					inst.update ('INSERT INTO bounce_tbl (company_id, customer_id, detail, mailing_id, dsn, change_date) VALUES (:company, :customer, :detail, :mailing, :dsn, now())', data)
				except agn.error, e:
					agn.log (agn.LV_ERROR, 'updBounce', 'Unable to add bounce %s to database: %s' % (`data`, e.msg))
					rc = False
				if detail in (510, 511, 512) or bouncetype in (3, 4, 6):
					self.hbcount += 1
					data = { 'status': bouncetype,
						 'remark': bounceremark,
						 'mailing': mailing,
						 'customer': customer,
						 'media': media
					}
					try:

						inst.update ('UPDATE customer_%d_binding_tbl SET user_status = :status, change_date = now(), user_remark = :remark, exit_mailing_id = :mailing WHERE customer_id = :customer AND user_status = 1 AND mediatype = :media' % company, data, commit = True)
					except agn.error, e:
						agn.log (agn.LV_ERROR, 'updBounce', 'Unable to unsubscribe %s for company %d from database: %s' % (`data`, company, e.msg))
						rc = False
				else:
					self.sbcount += 1
					if self.sbcount % 1000 == 0:
						inst.sync ()
		else:
			self.igcount += 1
		return rc
#}}}
class UpdateAccount (Update): #{{{

	accountlog = agn.base + os.sep + 'var' + os.sep + 'spool' + os.sep + 'log' + os.sep + 'account.log'

	def __init__ (self, path = accountlog):
		Update.__init__ (self, path, 'account')
		self.tscheck = re.compile ('^[0-9]{4}-[0-9]{2}-[0-9]{2}:[0-9]{2}:[0-9]{2}:[0-9]{2}$')
		self.ignored = None
		self.inserted = None
		self.failed = None

	def updateStart (self, inst):
		self.ignored = 0
		self.inserted = 0
		self.failed = 0
		return True
	
	def updateEnd (self, inst):
		agn.log (agn.LV_INFO, 'updAccount', 'Insert %d, failed %d, ignored %d records in %d lines' % (self.inserted, self.failed, self.ignored, self.lineno))
		return True
	
	def updateLine (self, inst, line):
		sql = 'INSERT INTO mailing_account_tbl ('
		values = 'VALUES ('
		sep = ''

		timestamp = 'now()'
		data = {}
		ignore = False
		for tok in line.split ():
			tup = tok.split ('=', 1)
			if len (tup) == 2:
				name = None
				(var, val) = tup

				if var == 'company':
					name = 'company_id'
				elif var == 'mailing':
					name = 'mailing_id'
				elif var == 'maildrop':
					name = 'maildrop_id'
				elif var == 'status_field':
					name = 'status_field'
				elif var in ('mailtype', 'subtype'):
					name = 'mailtype'
				elif var == 'count':
					name = 'no_of_mailings'
				elif var == 'bytes':
					name = 'no_of_bytes'
				elif var == 'block':
					name = 'blocknr'
				elif var == 'timestamp':
					if not self.tscheck.match (val) is None:

						timestamp = 'str_to_date(\'' + val + '\', \'%Y-%m-%d:%H:%i:%s\')'
				if not name is None:
					sql += '%s%s' % (sep, name)
					values += '%s:%s' % (sep, name)
					sep = ', '
					data[name] = val

		sql += '%schange_date) %s, %s)' % (sep, values, timestamp)
		rc = True
		if not ignore:
			try:
				inst.update (sql, data, commit = True)
				self.inserted += 1
			except agn.error, e:
				agn.log (agn.LV_ERROR, 'updAccount', 'Failed to insert %s into database: %s' % (line, e.msg))
				rc = False
				self.failed += 1
		else:
			self.ignored += 1
		return rc
#}}}
#
term = False
def handler (sig, stack):
	global	term
	
	term = True

def main ():
	global	term

	signal.signal (signal.SIGINT, handler)
	signal.signal (signal.SIGTERM, handler)

	if not agn.iswin:
		signal.signal (signal.SIGHUP, signal.SIG_IGN)
		signal.signal (signal.SIGPIPE, signal.SIG_IGN)
	#
	opts = getopt.getopt (sys.argv[1:], 'o:')
	updparm = {}
	use = []
	for opt in opts[0]:
		if opt[0] == '-o':
			parm = opt[1].split (':', 1)
			if len (parm) == 2:
				v = parm[1].split ('=', 1)
				if len (v) == 1:
					v.append ('true')
				if updparm.has_key (parm[0]):
					updparm[parm[0]].append (v)
				else:
					updparm[parm[0]] = [v]
				if not parm[0] in use:
					use.append (parm[0])
	for u in opts[1]:
		if not u in use:
			use.append (u)
	updates = []
	for u in use:
		if u == 'bounce':
			nu = UpdateBounce ()
		elif u == 'account':
			nu = UpdateAccount ()
		else:
			nu = None
			agn.log (agn.LV_ERROR, 'main', 'Invalid update: %s' % u)
		if not nu is None:
			if updparm.has_key (u):
				nu.options (updparm[u])
			updates.append (nu)
	if len (updates) == 0:
		agn.die (agn.LV_ERROR, 'main', 'No update procedure found')
	agn.lock ()
	agn.log (agn.LV_INFO, 'main', 'Starting up')
	while not term:
		agn.mark (agn.LV_INFO, 'loop', 180)
		db = None
		for upd in updates:
			if not term and upd.shouldRun () and upd.exists ():
				if db is None:
					db = agn.DBaseID ()
					if db is None:
						agn.log (agn.LV_ERROR, 'loop', 'Unable to connect to database')
				if not db is None:
					instance = db.cursor ()
					if not instance is None:
						if not upd.update (instance):
							agn.log (agn.LV_ERROR, 'loop', 'Update for %s failed' % upd.name)
						instance.close ()
					else:
						agn.log (agn.LV_ERROR, 'loop', 'Unable to get database cursor')
		if not db is None:
			db.close ()
		#
		# Zzzzz....
		countDelay = delay
		while countDelay > 0 and not term:

			if agn.iswin and agn.winstop ():
				term = True
				break
			time.sleep (1)
			countDelay -= 1
	for upd in updates:
		upd.done ()
	agn.log (agn.LV_INFO, 'main', 'Going down')
	agn.unlock ()
#
if __name__ == '__main__':
	main ()
