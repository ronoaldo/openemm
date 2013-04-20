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
import	sys, os, time, socket, re, types
try:
	import	subprocess
except ImportError:
	subprocess = None
	import	popen2
import	threading
import	smtplib
import	smtpd, asyncore
import	agn, aps, bavd
agn.require ('2.0.0')
#
agn.loglevel = agn.LV_DEBUG
#
ADMIN_SPOOL = agn.mkpath (agn.base, 'var', 'spool', 'ADMIN')
QUEUE_SPOOL = agn.mkpath (agn.base, 'var', 'spool', 'QUEUE')
MIDQUEUE_SPOOL = agn.mkpath (agn.base, 'var', 'spool', 'MIDQUEUE')
SLOWQUEUE_SPOOL = agn.mkpath (agn.base, 'var', 'spool', 'SLOWQUEUE')
BAV_CONF = agn.mkpath (agn.base, 'var', 'spool', 'bav', 'bav.conf')
BOUNCE_LOG = agn.mkpath (agn.base, 'var', 'spool', 'log', 'extbounce.log')
#
fqdn = socket.getfqdn ()
#
try:
	import	syslog
except ImportError:
	syslog = None
try:
	import	DNS

	DNS.DiscoverNameServers ()
	resolver = DNS
except ImportError:
	resolver = None
#
running = True
class Threadpool: #{{{
	def __init__ (self, maxthreads):
		self.maxthreads = maxthreads
		self.threads = [None] * self.maxthreads
	
	def join (self):
		for thr in self.threads:
			if thr is not None:
				thr.join ()
		self.threads = [None] * self.maxthreads
	
	def findFreeSlot (self):
		global	running
		
		rc = None
		while running and rc is None:
			n = 0
			while n < self.maxthreads:
				if self.threads[n] is None:
					if rc is None:
						rc = n
				else:
					self.threads[n].join (0)
					if not self.threads[n].isAlive ():
						self.threads[n].join ()
						self.threads[n] = None
						if rc is None:
							rc = n
				n += 1
			if rc is None:
				time.sleep (1)
		return rc
	
	def setThread (self, thrID, thr):
		self.threads[thrID] = thr
#}}}
class Mail: #{{{
	lock = threading.Lock ()
	count = 1
	
	def __init__ (self, sender, receiver):
		self.sender = sender
		self.receiver = receiver
		self.queue = None
		self.header = {}
		self.body = None
	
	def __setitem__ (self, var, val):
		self.header[var.lower ()] = [var, val]
	
	def setQueue (self, queue):
		self.queue = queue

	def setBody (self, body):
		self.body = body
	
	def createMail (self):
		self.lock.acquire ()
		nr = self.count
		self.count += 1
		self.lock.release ()
		now = time.time ()
		mid = '%.04f.%d' % (now, nr)
		qf = 'T%d\n' % now
		qf += 'K%d\n' % (now + 5 * 24 * 60 * 60)
		qf += 'N1\n'
		qf += 'Menqueued\n'
		qf += 'S<%s>\n' % self.sender
		qf += 'R<%s>\n' % self.receiver
		hids = self.header.keys ()
		for use in [['Return-Path', '<%s>' % self.sender],
			    ['Message-ID', '<%s@%s>' % (mid, fqdn)],
			    ['Date', time.strftime ('%a, %d %b %Y %H:%M:%S GMT', time.gmtime (now))],
			    ['From', self.sender],
			    ['To', self.receiver],
			   ]:
			try:
				hid = use[0].lower ()
				qf += 'H%s: %s\n' % (use[0], self.header[hid][1])
				hids.remove (hid)
			except KeyError:
				qf += 'H%s: %s\n' % (use[0], use[1])
		for hid in hids:
			qf += 'H%s: %s\n' % (self.header[hid][0], self.header[hid][1])
		qf += '.\n'
		tfname = self.queue + os.sep + 'tfb%s' % mid
		qfname = self.queue + os.sep + 'qfb%s' % mid
		dfname = self.queue + os.sep + 'dfb%s' % mid
		try:
			fd = open (tfname, 'w')
			fd.write (qf)
			fd.close ()
			fd = open (dfname, 'w')
			fd.write (self.body)
			fd.close ()
			os.rename (tfname, qfname)
			agn.log (agn.LV_DEBUG, 'mail', 'Mail from <%s> to <%s> created as %s/%s' % (self.sender, self.receiver, qfname, dfname))
		except (IOError, OSError), e:
			agn.log (agn.LV_ERROR, 'mail', 'Failed to create mail from <%s> to <%s>: %s' % (self.sender, self.receiver, str (e.args)))
			try:
				os.unlink (tfname)
			except OSError, e:
				agn.log (agn.LV_ERROR, 'mail', 'Failed to remove temp.file %s %s' % (tfname, str (e.args)))
			try:
				os.unlink (dfname)
			except OSError, e:
				agn.log (agn.LV_ERROR, 'mail', 'Failed to remove data file %s %s' % (dfname, str (e.args)))
#}}}
class Spool: #{{{
	class Relays: #{{{
		class Relay: #{{{
			def __init__ (self, expire, resolv):
				self.expire = expire
				self.resolv = resolv
		#}}}
		class Smartrelay: #{{{
			def __init__ (self, sexpr):
				self.smartrelay = None
				self.username = None
				self.password = None
				parts = sexpr.split ('@')
				if len (parts) >= 2:
					self.smartrelay = parts[-1]
					auth = '@'.join (parts[:-1]).split (':', 1)
					if len (auth) == 2:
						self.username = auth[0]
						self.password = auth[1]
				else:
					self.smartrelay = sexpr
		#}}}

		def __init__ (self):
			self.r = {}
			self.checkForSmartRelay = True
			self.smartRelay = None

		nsLock = threading.Lock ()
		def nsLookup (self, domain):
			self.nsLock.acquire ()
			try:
				data = ''
				error = ''
				for typ in ['mx', 'any']:
					if subprocess is None:
						pp = popen2.Popen3 ('nslookup -type=%s "%s"' % (typ, domain), True)
						tempdata = pp.fromchild.read ()
						temperror = pp.childerr.read ()
						pp.wait ()
					else:
						pp = subprocess.Popen (['nslookup', '-type=%s' % typ, domain], stdout = subprocess.PIPE, stderr = subprocess.PIPE, shell = False, universal_newlines = True)
						(tempdata, temperror) = pp.communicate ()
						pp.wait ()
					data += tempdata
					error += temperror
			except OSError, e:
				data = None
				error = None
				agn.log (agn.LV_ERROR, 'relay', 'Failed to start external program: %s' % str (e.args))
			self.nsLock.release ()
			rtype = None
			resolv = None
			if data:
				pat = re.compile ('^%s[ \t]+' % re.escape (domain))
				is_a = False
				mx = []
				cname = None
				seen = []
				for line in [l for l in data.split ('\n') if not l in seen and not pat.match (l) is None]:
					seen.append (line)
					parm = line.split (None, 1)
					if len (parm) == 2:
						opts = {}
						for opt in parm[1].split (','):
							popt = opt.split ('=', 1)
							if len (popt) == 2:
								opts[popt[0].strip ().lower ()] = popt[1].strip ()
						if opts.has_key ('internet address'):
							is_a = True
						elif opts.has_key ('mail exchanger'):
							cmx = opts['mail exchanger']
							try:
								pref = int (opts['mx preference'])
							except (KeyError, ValueError):
								pref = 0
								parts = cmx.split ()
								if len (parts) > 1:
									try:
										pref = int (parts[0])
									except ValueError:
										agn.log (agn.LV_WARNING, 'relay', 'Unparsable MX: %s' % cmx)
									cmx = parts[-1]
							if cmx.endswith ('.'):
								cmx = cmx[:-1]
							mx.append ('%04d:%s' % (pref, cmx))
						elif opts.has_key ('canonical name'):
							cname = opts['canonical name']
							if cname.endswith ('.'):
								cname = cname[:-1]
				if mx:
					mx.sort ()
					rtype = 'MX'
					resolv = ','.join ([x.split (':')[1] for x in mx])
					agn.log (agn.LV_DEBUG, 'relay', 'Found "%s" as relay for "%s"' % (resolv, domain))
				elif is_a:
					rtype = 'A'
					resolv = domain
					agn.log (agn.LV_DEBUG, 'relay', 'Use A record for "%s" as relay' % domain)
				elif cname:
					rtype = 'CNAME'
					resolv = cname
			if resolv is None and error:
				for line in error.split ('\n'):
					if line.startswith ('***'):
						parts = line.split (':')
						if len (parts) > 1:
							reason = parts[-1].strip ().lower ()
							if reason == 'non-existent domain':
								rtype = '*'
								resolv = ''
								agn.log (agn.LV_DEBUG, 'relay', 'Domain "%s" not found' % domain)
			return (rtype, resolv)

		def findDomain (self, domain):
			if resolver is None:
				return self.nsLookup (domain)
			q = resolver.Request ()
			answers = q.req (domain, qtype = 'MX')
			if len (answers.answers) > 0:
				rtype = 'MX'
				collect = []
				for answer in answers.answers:
					collect.append (answer['data'])
				collect.sort ()
				resolv = ','.join ([_m[1] for _m in collect])
			else:
				answers = q.req (domain, qtype = 'CNAME')
				if len (answers.answers) > 0:
					rtype = 'CNAME'
					resolv = answers.answers[0]['data']
				else:
					answers = q.req (domain, qtype = 'A')
					if len (answers.answers) > 0:
						rtype = 'A'
						resolv = domain
					else:
						rtype = '*'
						resolv = ''
			return (rtype, resolv)

		def findRelay (self, domain, now):
			loopcheck = [domain]
			(rtype, resolv) = self.findDomain (domain)
			while rtype == 'CNAME' and not resolv in loopcheck:
				loopcheck.append (resolv)
				(rtype, resolv) = self.findDomain (resolv)
			if not resolv is None and rtype in ('A', 'MX'):
				r = Spool.Relays.Relay (now + 3600, resolv)
				self.r[domain] = r
				return r
			return None

		def getRelay (self, domain):
			if self.checkForSmartRelay:
				self.checkForSmartRelay = False
				try:
					fd = open ('conf' + os.path.sep + 'smart-relay')
					data = fd.read ().strip ()
					fd.close ()
					if data:
						self.smartRelay = Spool.Relays.Smartrelay (data)
				except IOError:
					pass
			#
			if not self.smartRelay is None:
				return self.smartRelay
			#
			now = time.time ()
			try:
				r = self.r[domain]
				if r.expire < now:
					del self.r[domain]
					r = None
			except KeyError:
				r = None
			if r is None:
				r = self.findRelay (domain, now)
				if not r is None:
					self.r[domain] = r
			if not r is None:
				return r.resolv
			return None
	#}}}

	relays = Relays ()

	class Entry (threading.Thread): #{{{
		parseFID = re.compile ('^qf([0-9A-F]{6})[0-9A-Z]{3}([0-9A-F]{8})$', re.IGNORECASE)
		parseDSN = re.compile ('^(#?([0-9]\\.[0-9]\\.[0-9])|.*\\(#?([0-9]\\.[0-9]\\.[0-9])\\))')
		noSuchUser = re.compile ('(no such|invalid|unknown) (user|address|mailbox)', re.IGNORECASE)
		noSuchHost = re.compile ('unknown (host|domain)', re.IGNORECASE)

		def __init__ (self, **kws):
			threading.Thread.__init__ (self, **kws)
			self.qpath = None
			self.dpath = None
			self.plugin = None
			self.pctx = None
			self.mid = None
			self.qdata = None
			self.qlines = None
			self.qmap = None
			self.qfmod = None
			self.sendtime = None
			self.mailingID = None
			self.customerID = None
			self.mail = None

		def setup (self, path, plugin, qfname, dfname):
			self.qpath = path + os.sep + qfname
			self.dpath = path + os.sep + dfname
			self.plugin = plugin
			self.pctx = agn.struct ()
			if len (qfname) > 2:
				self.mid = qfname[2:]
			else:
				self.mid = qfname
			try:
				fd = open (self.qpath)
				self.qdata = fd.read ()
				fd.close ()
			except IOError, e:
				self.qdata = None
				agn.log (agn.LV_ERROR, self.mid, 'Failed to read control file %s: %s' % (self.qpath, str (e.args)))
			self.qmap = {}
			if self.qdata:
				self.qlines = self.qdata.split ('\n')
				for line in self.qlines:
					if len (line) and line[0] in 'TKNMSRX':
						self.qmap[line[0]] = line
			else:
				self.qlines = None
			self.qfmod = False
			self.sendtime = 0
			mtch = self.parseFID.match (qfname)
			if not mtch is None:
				(mid, cid) = mtch.groups ()
				self.mailingID = int (mid, 16)
				self.customerID = int (cid, 16)
				agn.log (agn.LV_DEBUG, self.mid, 'New entry %s for Mailing %d and Customer %d' % (self.qpath, self.mailingID, self.customerID))
			else:
				agn.log (agn.LV_DEBUG, self.mid, 'New entry %s from external source' % self.qpath)
		
		def updateQF (self):
			if self.qfmod:
				try:
					qnew = ''
					for key in self.qmap.keys ():
						found = False
						for line in self.qlines:
							if len (line) and line[0] == key:
								found = True
								break
						if not found:
							qnew += self.qmap[key] + '\n'
					fd = open (self.qpath, 'w')
					if qnew:
						fd.write (qnew)
					for line in [l for l in self.qlines if l]:
						if self.qmap.has_key (line[0]):
							line = self.qmap[line[0]]
						fd.write (line + '\n')
					fd.close ()
					agn.log (agn.LV_DEBUG, self.mid, 'Updated qfile %s' % self.qpath)
				except IOError, e:
					agn.log (agn.LV_ERROR, self.mid, 'Failed to update qfile "%s": %s' % (self.qpath, str (e.args)))
		
		def removeSpoolfiles (self):
			for path in [self.qpath, self.dpath]:
				try:
					os.unlink (path)
				except OSError, e:
					agn.log (agn.LV_ERROR, self.mid, 'Failed to remove spoolfile %s: %s' % (path, str (e.args)))
		
		def moveSpoolfiles (self, target):
			for path in [self.qpath, self.dpath]:
				dest = agn.mkpath (target, os.path.basename (path))
				try:
					os.rename (path, dest)
				except OSError, e:
					agn.log (agn.LV_ERROR, self.mid, 'Failed to move spoolfile %s to %s: %s' % (path, dest, str (e.args)))

		def __getset (self, qid, nval):
			try:
				val = int (self.qmap[qid][1:])
			except (KeyError, ValueError):
				val = nval
				self.qmap[qid] = '%s%d' % (qid, val)
				self.qfmod = True
			return val
		
		def __getaddr (self, s):
			start = s.find ('<')
			end = s.find ('>')
			if start != -1 and end != -1 and start < end:
				s = s[start + 1:end]
			return s
		
		def __mergeDSN (self, s, dflt):
			try:
				return int (s[0]) * 100 + int (s[2]) * 10 + int (s[4])
			except:
				return dflt

		def validate (self, now, increase, retries):
			valid = False
			expire = self.__getset ('K', now + 5* 60 * 60 * 24)
			if expire < now or not self.qmap.has_key ('R') or not self.qmap.has_key ('S'):
				if expire < now:
					agn.log (agn.LV_INFO, self.mid, 'Removed expired entry')
				else:
					agn.log (agn.LV_WARNING, self.mid, 'Removed incomplete entry')
				self.removeSpoolfiles ()
			else:
				start = self.__getset ('T', now)
				tries = self.__getset ('N', 1)
				if retries is not None and retries[0] < tries:
					agn.log (agn.LV_INFO, self.mid, 'Moving to %s' % retries[1])
					self.moveSpoolfiles (retries[1])
				else:
					self.sendtime = start + (tries - 1) * increase
					if self.sendtime < now:
						agn.log (agn.LV_DEBUG, self.mid, 'Entry is ready to send, current trycount is %d' % tries)
						self.qmap['N'] = 'N%d' % (tries + 1, )
						self.qfmod = True
						self.mail = ''
						for line in self.qlines:
							if len (line):
								if line[0] == 'H':
									line = line[1:]
									if line.startswith ('?'):
										line = line[1:]
										n = line.find ('?')
										if n != -1:
											line = line[n + 1:]
									self.mail += line + '\n'
								elif line[0] in ' \t':
									self.mail += line + '\n'
						self.mail += '\n'
						try:
							fd = open (self.dpath)
							self.mail += fd.read ()
							fd.close ()
							if self.mail[-1] != '\n':
								self.mail += '\n'
							valid = True
						except IOError, e:
							agn.log (agn.LV_ERROR, self.mid, 'Failed to read body from %s: %s' % (self.dpath, str (e.args)))
					else:
						agn.log (agn.LV_DEBUG, self.mid, 'Skip %s as it is not ready to send' % self.mid)
			return valid

		def writeBounce (self, dsn, message):
			if not self.mailingID is None and not self.customerID is None and not message is None:
				s = '%d.%d.%d;1;%d;0;%d;stat=%s\n' % (dsn / 100, (dsn / 10) % 10, dsn % 10, self.mailingID, self.customerID, message)
				try:
					fd = open (BOUNCE_LOG, 'a')
					fd.write (s)
					fd.close ()
				except IOError, e:
					agn.log (agn.LV_ERROR, self.mid, 'Failed to write bounce log to %s: %s' % (BOUNCE_LOG, str (e.args)))
			else:
				agn.log (agn.LV_DEBUG, self.mid, 'Skip incomplete bounce %r/%r/%r' % (self.mailingID, self.customerID, message))
		
		def report (self, dsn, message, reps):
			did = dsn / 100
			if did in (2, 5):
				if did == 5:
					self.writeBounce (dsn, message)
					agn.log (agn.LV_INFO, self.mid, 'Hardbounce %d: %s' % (dsn, message))
					stat = 'Failure'
				else:
					agn.log (agn.LV_INFO, self.mid, 'Successful %d: %s' % (dsn, message))
					stat = 'Sent'
				self.removeSpoolfiles ()
			elif did == 4:
				self.writeBounce (dsn, message)
				self.qmap['M'] = 'M[%d] %s' % (dsn, message)
				self.qfmod = True
				self.updateQF ()
				agn.log (agn.LV_INFO, self.mid, 'Softbounce %d: %s' % (dsn, message))
				stat = 'Deferred'
			else:
				agn.log (agn.LV_ERROR, self.mid, 'Strange report %d: %r' % (dsn, message))
				stat = 'Suspect'
			dod = (dsn / 10) % 10
			dud = dsn % 10
			smsg = '%s: dsn=%d.%d.%d, %s, stat=%s (%s)' % (self.mid, did, dod, dud, ', '.join (['%s=%s' % (_c, reps[_c]) for _c in sorted (reps)]), stat, message)
			agn.log (agn.LV_INFO, 'sent', smsg)
			if syslog is not None:
				syslog.syslog (syslog.LOG_NOTICE, smsg)

		def run (self):
			sender = self.__getaddr (self.qmap['S'][1:])
			receiver = self.__getaddr (self.qmap['R'][1:])
			mail = self.mail
			if self.plugin:
				try:
					(header, body) = mail.split ('\n\n', 1)
				except ValueError:
					header = ''
					body = mail
				head = []
				pos = -1
				for line in header.split ('\n'):
					if pos == -1 or line[0] not in (' ', '\t'):
						head.append (line)
						pos += 1
					else:
						head[pos] += '\n%s' % line
				env = agn.struct (sender = sender, receiver = receiver, head = head, body = body)
				self.plugin ().handleOutgoingMail (self.pctx, env)
				if env.sender != sender:
					sender = env.sender
				if env.receiver != receiver:
					receiver = env.receiver
				nheader = '\n'.join (env.head)
				if nheader != header or env.body != body:
					mail = '%s\n\n%s' % (nheader, env.body)
			reps = {'from': '<%s>' % sender, 'to': '<%s>' % receiver}
			parts = receiver.split ('@')
			if len (parts) != 2:
				self.report (511, 'Invalid receiver', reps)
				return
			domain = parts[1].lower ()
			auth = None
			try:
				relay = self.qmap['X'][1:]
			except KeyError:
				qrelay = Spool.relays.getRelay (domain)
				if type (qrelay) == types.StringType:
					relay = qrelay
					self.qmap['X'] = 'X%s' % relay
					self.qfmod = True
				elif qrelay is None:
					relay = None
				else:
					try:
						if qrelay.username and qrelay.password:
							auth = [qrelay.username, qrelay.password]
						relay = qrelay.smartrelay
					except AttributeError:
						relay = None
			if relay is None:
				self.report (412, 'Failed to resolve domain %s' % domain, reps)
				return
			if relay == '':
				self.report (512, 'Domain %s not existing' % domain, reps)
				return
			dsn = 0
			msg = ''
			for r in relay.split (','):
				retry = False
				reps['relay'] = r
				try:
					parts = r.split (':')
					if len (parts) == 2:
						host = parts[0]
						try:
							port = int (parts[1])
						except ValueError:
							port = None
					else:
						host = r
						port = None
					if port is None:
						smtp = smtplib.SMTP (host)
					else:
						smtp = smtplib.SMTP (host, port)
					smtp.ehlo ()
					if auth:
						smtp.starttls ()
						smtp.ehlo ()
						smtp.login (auth[0], auth[1])
					smtp.sendmail (sender, [receiver], mail)
					smtp.quit ()
					dsn = 250
					msg = 'Message send via %s' % r
				except smtplib.SMTPSenderRefused, e:
					dsn = e.smtp_code
					msg = e.sender + ': ' + e.smtp_error
					if dsn / 100 != 5:
						retry = True
				except smtplib.SMTPRecipientsRefused, e:
					(dsn, msg) = e.recipients.values ()[0]
					mtch = self.parseDSN.match (msg)
					if not mtch is None:
						grp = mtch.groups ()
						if grp[1]:
							dsn = self.__mergeDSN (grp[1], dsn)
						elif grp[2]:
							dsn = self.__mergeDSN (grp[2], dsn)
					elif not self.noSuchUser.search (msg) is None:
						dsn = 511
					elif not self.noSuchHost.search (msg) is None:
						dsn = 512
					if dsn in (511, 571):
						msg += ': user unknown'
				except smtplib.SMTPResponseException, e:
					(dsn, msg) = e.args
					if dsn / 100 != 5:
						retry = True
				except smtplib.SMTPException, e:
					dsn = 400
					msg = str (e.args)
					retry = True
				except socket.error, e:
					dsn = 400
					msg = e.args[1]
					retry = True
				except Exception, e:
					agn.log (agn.LV_ERROR, self.mid, 'Exception in send: %r' % e)
					dsn = 400
					msg = 'fault'
				if not retry:
					break
				agn.log (agn.LV_WARNING, self.mid, 'Retry as sent to %s failed %d: %r' % (r, dsn, msg))
			self.report (dsn, msg, reps)
	#}}}

	def __init__ (self, path, interval, retries, threads, plugin):
		self.path = path
		self.sid = os.path.basename (path)
		agn.createPath (self.path)
		self.interval = interval
		self.retries = retries
		self.plugin = plugin
		self.pool = Threadpool (threads)
		agn.log (agn.LV_INFO, self.sid, 'Initial setup for %d threads completed' % threads)
	
	def join (self):
		self.pool.join ()

	def delay (self):
		global	running

		n = self.interval
		while running and n > 0:
			time.sleep (1)
			n -= 1

	def execute (self):
		global	running

		flist = os.listdir (self.path)
		agn.log (agn.LV_DEBUG, self.sid, 'Found %d files in queue' % len (flist))
		now = time.time ()
		for qfname in [fn for fn in flist if fn.startswith ('qf')]:
			dfname = 'd' + qfname[1:]
			if dfname in flist:
				e = Spool.Entry ()
				e.setup (self.path, self.plugin, qfname, dfname)
				if e.validate (now, self.interval - 1, self.retries):
					thr = self.pool.findFreeSlot ()
					if not thr is None:
						e.start ()
						self.pool.setThread (thr, e)
			else:
				agn.log (agn.LV_WARNING, self.sid, 'Stale control file %s found' % qfname)
			if not running:
				break
#}}}
class Sender (threading.Thread): #{{{
	def __init__ (self, **kws):
		threading.Thread.__init__ (self, **kws)
		self.spool = None

	def setSpool (self, spool):
		self.spool = spool
	
	def run (self):
		global	running
		
		agn.log (agn.LV_INFO, 'sender', 'Starting for %s' % self.spool.sid)
		while running:
			self.spool.execute ()
			self.spool.delay ()
		self.spool.join ()
		agn.log (agn.LV_INFO, 'sender', 'Ending for %s' % self.spool.sid)
#}}}
class Server (threading.Thread): #{{{
	class Bavconf: #{{{
		def __init__ (self):
			self.aliases = {}
			self.rules = {}
			try:
				fd = open (BAV_CONF)
				for line in [lin for lin in fd.readlines () if len (lin) > 0 and not lin[0] in ('#', '\n')]:
					parts = line[:-1].split (None, 1)
					if len (parts) == 2:
						action = parts[1].split (':', 1)
						if len (action) == 2:
							if action[0] == 'alias':
								self.aliases[parts[0]] = action[1]
							else:
								self.rules[parts[0]] = action
				fd.close ()
			except IOError, e:
				agn.log (agn.LV_ERROR, 'bav', 'Failed to open %s: %s' % (BAV_CONF, str (e.args)))
		
		def findRule (self, rcpt):
			try:
				alias = self.aliases[rcpt]
			except KeyError:
				alias = rcpt
			try:
				rc = self.rules[alias]
			except KeyError:
				rc = None
			return rc
	#}}}
	
	class Bavd (bavd.BAV): #{{{
		def __init__ (self, message, mode):
			msg = bavd.email.message_from_string (message)
			bavd.BAV.__init__ (self, msg, mode)
		
		def sendmail (self, msg, to):
			rmsg = msg.as_string (False)
			mail = Mail ('mailloop@%s' % fqdn, to)
			mail.setQueue (QUEUE_SPOOL)
			parts = rmsg.split ('\n\n', 1)
			if len (parts) == 2:
				header = []
				cur = None
				for line in parts[0].split ('\n'):
					if len (line) and line[0] in '\t ':
						if not cur is None:
							cur[1] += ' ' + line.lstrip ()
					else:
						token = line.split (':', 1)
						if len (token) == 2:
							cur = [token[0], token[1].lstrip ()]
							header.append (cur)
				for head in header:
					mail[head[0]] = head[1]
				mail.setBody (parts[1])
				mail.createMail ()
	#}}}

	class Process (threading.Thread): #{{{
		X_AGN = 'X-AGNMailloop'
		daemonSender = re.compile ('^$|MAILER-DAEMON', re.IGNORECASE)
		daemonHeader = [re.compile ('^(Resent-)?(From|Sender|Return-Path):.*(<>|<MAILER[_-]?DAEMON[^>]*>)', re.IGNORECASE),
				re.compile ('^Precedence:.*(junk|bulk|list)', re.IGNORECASE)]

		def __init__ (self, bav, peer, sender, receiver, header, body, message):
			threading.Thread.__init__ (self)
			self.bav = bav
			self.peer = peer
			self.sender = sender
			self.receiver = receiver
			self.header = header
			self.body = body
			self.message = message
			self.mail = None
		
		def createBounce (self, code, msg):
			mail = Mail ('', self.sender)
			mail.setQueue (QUEUE_SPOOL)
			mail['From'] = 'MAILER-DAEMON <>'
			mail['Subject'] = 'Mail failed: %d %s' % (code, msg)
			mail.setBody ('Mail failed due to %d:\n%s\n\n\nThe original message follows:\n%s\n%s\n' % (code, msg, '\n'.join (['>' + h[0] + ': ' + h[1] for h in self.header]), self.body))
			mail.createMail ()
			
		def isSystemMail (self):
			if not self.daemonSender.search (self.sender) is None:
				return True
			for head in [h[0] + ': ' + h[1] for h in self.header]:
				for rhead in self.daemonHeader:
					if not rhead.search (head) is None:
						return True
			baver = Server.Bavd (self.mail, 0)
			return not baver.execute ()
				
		def run (self):
			action = self.bav.findRule (self.receiver)
			if action is None:
				self.createBounce (510, 'Unknown user ' + self.receiver)
				return
			if action[0] == 'reject':
				self.createBounce (500, 'User rejected ' + self.receiver)
				return
			if action[0] == 'tempfail':
				self.createBounce (400, 'Unable to handle mail, try again later')
				return
			if action[0] != 'accept':
				self.createBounce (400, 'Internal error, invalid action ' + action[0])
				return
			info = action[1]
			info += ',from=%s,to=%s' % (self.sender, self.receiver)
			self.header.append ([self.X_AGN, info])
			self.mail = '\n'.join ([h[0] + ': ' + h[1] for h in self.header]) + '\n' + self.body
			if self.isSystemMail ():
				baver = Server.Bavd (self.mail, 2)
			else:
				baver = Server.Bavd (self.mail, 1)
			baver.execute ()
	#}}}

	class ServerLoop (smtpd.SMTPServer): #{{{
		X_LOOP = 'X-AGNLoop'

		def __init__ (self, plugin):
			self.plugin = plugin
			if agn.iswin:
				port = 25
			else:
				port = 8025
			smtpd.SMTPServer.__init__ (self, ('0.0.0.0', port), None)
			self.pool = Threadpool (10)
	
		def process_message (self, peer, mailfrom, rcpttos, data):
			# 1.) Unifiy data
			data = data.replace ('\r\n', '\n')
			# 2.) Extract header
			header = [['Return-Path', '<%s>' % mailfrom]]
			missFrom = True
			n = data.find ('\n\n')
			if n != -1:
				heads = data[:n].split ('\n')
				body = data[n + 1:]
				cur = None
				for head in heads:
					if len (head) > 0 and head[0] in ' \t':
						if not cur is None:
							cur[1] += ' ' + head.lstrip ()
					else:
						parts = head.split (':', 1)
						if len (parts) == 2:
							cur = [parts[0], parts[1].lstrip ()]
							header.append (cur)
							if missFrom and cur[0].lower () == 'from':
								missFrom = False
			else:
				body = data
			if missFrom:
				header.append (['From', mailfrom])
			# 3.) Check for loops and silently ignore mail
			isLoop = False
			for head in header:
				if head[0] == self.X_LOOP:
					isLoop = True
					break
			if isLoop:
				return
			# 4.) Add loop marker to header
			header.append ([self.X_LOOP, 'set'])
			# 5.) Start off real processing
			bav = Server.Bavconf ()
			for rcpt in rcpttos:
				threadID = self.pool.findFreeSlot ()
				if not threadID is None:
					proc = Server.Process (bav, peer, mailfrom, rcpt, header, body, data)
					proc.start ()
					self.pool.setThread (threadID, proc)
	#}}}

	def __init__ (self, **kws):
		threading.Thread.__init__ (self, **kws)
		self.plugin = None
	
	def setPlugin (self, plugin):
		self.plugin = plugin

	def run (self):
		Server.ServerLoop (self.plugin)
		asyncore.loop (timeout = 1.0)
#}}}
#
def main ():
	if not agn.iswin:
		import	signal
	
		def __handler (sig, stack):
			global	running
			running = False
		signal.signal (signal.SIGINT, __handler)
		signal.signal (signal.SIGTERM, __handler)
		signal.signal (signal.SIGHUP, signal.SIG_IGN)
		signal.signal (signal.SIGPIPE, signal.SIG_IGN)
		signal.signal (signal.SIGCHLD, signal.SIG_DFL)

	agn.log (agn.LV_INFO, 'main', 'Starting up')
	if syslog is not None:
		syslog.openlog (os.path.basename (sys.argv[0]), syslog.LOG_PID, syslog.LOG_MAIL)
	plugin = aps.Manager (paths = agn.mkpath (agn.base, 'conf', 'semu'), apiVersion = '1.0.0', apiDescription = """
Be aware, all plugins must be thread safe as they are executed in
a threaded enviroment!

All methods called during a thread are passed a context "ctx" as
the first parameter where the plugin can store private data. To
avoid name clashes, the plugin should only add one attribute whith
the name of the plugin and assign a required data type to this
attribute, which can be a complex one as a dictionary or a class
instance.

NOTE: As there is only one method ATM, the usage of "ctx" is not
existing and should be considered as a tribute for future extensions.

def handleOutgoingMail (ctx, env):
	if an outgoing mail is ready to be deliviered this method is
	called. "ctx" is described above, "env" is of type agn.struct()
	which has these attributes prefilled:
	- sender: holds the envelope sender of this mail (string)
	- receiver: holds the envelope reveiver (string)
	- head: holds each header line (list)
	- body: holds the mail body (string)
	Each can be modified during processing.
	""")
	plugin.bootstrap ('semu.cfg')
	s1 = Sender (name = 'Spool ADMIN')
	s1.setSpool (Spool (ADMIN_SPOOL, 30, None, 5, plugin))
	s1.start ()
	s2 = Sender (name = 'Spool QUEUE')
	s2.setSpool (Spool (QUEUE_SPOOL, 120, (3, MIDQUEUE_SPOOL), 50, plugin))
	s2.start ()
	s3 = Sender (name = 'Spool MIDQUEUE')
	s3.setSpool (Spool (MIDQUEUE_SPOOL, 780, (10, SLOWQUEUE_SPOOL), 10, plugin))
	s3.start ()
	s4 = Sender (name = 'Spool SLOWQUEUE')
	s4.setSpool (Spool (SLOWQUEUE_SPOOL, 2440, None, 10, plugin))
	s4.start ()
	serv = Server (name = 'SMTP Server')
	serv.start ()

	global running
	while running:
		if agn.iswin:
			if agn.winstop ():
				running = False
				break
		time.sleep (1)
	agn.log (agn.LV_INFO, 'main', 'Shutting down')
	asyncore.close_all ()
	serv.join ()
	s3.join ()
	s2.join ()
	s1.join ()
	plugin.shutdown ()
	if syslog is not None:
		syslog.closelog ()
	agn.log (agn.LV_INFO, 'main', 'Going down')
#
if __name__ == '__main__':
	main ()
