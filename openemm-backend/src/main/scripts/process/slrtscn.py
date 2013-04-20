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
import	os, signal, time, sre
import	agn
agn.require ('2.0.0')
agn.loglevel = agn.LV_INFO
#
syslog = '/var/log/maillog'
savefile = agn.base + os.sep + 'var' + os.sep + 'run' + os.sep + 'slrtscan.save'

bouncelog = agn.base + os.sep + 'var' + os.sep + 'spool' + os.sep + 'log' + os.sep + 'extbounce.log'
accountlog = agn.base + os.sep + 'var' + os.sep + 'spool' + os.sep + 'log' + os.sep + 'account.log'
isstat = sre.compile ('sendmail\\[[0-9]+\\]: *([0-9A-F]{6}[0-9A-Z]{3}[0-9A-F]{7,8}):.*stat=(.*)$')
iscreat = sre.compile ('mail creation: *([0-9A-Z;-]+)')
#
parser = sre.compile ('^([a-z]{3} +[0-9]+ [0-9]{2}:[0-9]{2}:[0-9]{2}) +([^ ]+) +sendmail\\[[0-9]+\\]: *[0-9A-F]{6}([0-9A-Z]{3})[0-9A-F]{7,8}:(.*)$', sre.IGNORECASE)
def parseline (pline):
	rc = {
		'__line': pline
	}
	pmtch = parser.match (pline)
	if not pmtch is None:
		g = pmtch.groups ()
		rc['__timestamp'] = g[0]
		rc['__mailer'] = g[1]
		parms = g[3].split (',')
		for parm in parms:
			p = parm.split ('=', 1)
			if len (p) == 2:
				rc[p[0].strip ()] = p[1].strip ()
	return rc

def get (dmap, key):
	try:
		return dmap[key]
	except KeyError:
		return ''

def timestr ():
	now = time.localtime (time.time ())
	return '%04d-%02d-%02d:%02d:%02d:%02d' % (now[0], now[1], now[2], now[3], now[4], now[5])
#
term = False
def handler (sig, stack):
	global	term
	
	term = True
signal.signal (signal.SIGINT, handler)
signal.signal (signal.SIGTERM, handler)
signal.signal (signal.SIGHUP, signal.SIG_IGN)
signal.signal (signal.SIGPIPE, signal.SIG_IGN)
#
agn.lock ()
agn.log (agn.LV_INFO, 'main', 'Starting up')
while not term:
	time.sleep (1)
	agn.mark (agn.LV_INFO, 'loop', 180)
	try:
		fp = agn.Filepos (syslog, savefile)
	except agn.error, e:
		agn.log (agn.LV_ERROR, 'main', 'Unable to open %s: %s' % (syslog, e.msg))
		fp = None
		try:
			st = os.stat (savefile)
			if st.st_size == 0:
				agn.log (agn.LV_ERROR, 'main', 'Remove corrupt empty file %s' % savefile)
				os.unlink (savefile)
		except OSError:
			pass
	if fp is None:
		continue
	count = 0
	while not term:
		line = fp.readline ()
		if line is None:
			count += 1
			if count > 10:
				break
			time.sleep (1)
			continue
		count = 0
		mtch = isstat.search (line)
		if not mtch is None:
			(qid, detail) = mtch.groups ()
			n1 = detail.find (':')
			n2 = detail.find ('(')
			stat = None
			reason = None
			if n1 != -1 or n2 != -1:
				if n1 != -1 and (n2 == -1 or n2 > n1):
					stat = detail[:n1]
					reason = detail[n1 + 1:].strip ()
				else:
					stat = detail[:n2 - 1]
					reason = detail[n2:]
			else:
				stat = detail
			stat = stat.strip ()
			mailing = int (qid[:6], 16)
			if len (qid) == 17:
				customer = int (qid[9:], 16)
			else:
				customer = int (qid[10:], 16)
			details = parseline (line)
			if details.has_key ('dsn'):
				dsn = details['dsn']
				if len (dsn) > 0:
					try:
						fd = open (bouncelog, 'a')

						fd.write ('%s;0;%d;0;%d;stat=%s\trelay=%s\n' %	(dsn, mailing, customer, get (details, 'stat'), get (details, 'relay')))
						fd.close ()
					except IOError, e:
						agn.log (agn.LV_ERROR, 'loop', 'Unable to write %s: %s' % (bouncelog, `e.args`))
			continue
		mtch = iscreat.search (line)
		if not mtch is None:
			(data, ) = mtch.groups ()
			detail = data.split (';')
			dlen = len (detail)
			parts = line.split ()
			if len (parts) > 3:
				mailer = parts[3]
			else:
				mailer = 'unknown'

			if len (detail) == 11:
				acc = 'company=%s\tmailinglist=%s\tmailing=%s\tmaildrop=%s\tstatus_field=%s\tblock=%s\tmediatype=%s\tsubtype=%s\tcount=%s\tbytes=%s\tmailer=%s\ttimestamp=%s\n' % \
					(detail[1], detail[2], detail[3], detail[4], detail[5], detail[6], detail[7], detail[8], detail[9], detail[10], mailer, timestr ())
			else:
				acc = None
			if not acc is None:
				try:
					fd = open (accountlog, 'a')
					fd.write (acc)
					fd.close ()
				except IOError, e:
					agn.log (agn.LV_ERROR, 'loop', 'Failed to append to %s: %s' % (accountlog, `e.args`))
			continue
		agn.log (agn.LV_VERBOSE, 'loop', 'Unparseable line: %s' % line)
	fp.close ()

agn.log (agn.LV_INFO, 'main', 'Going down')
agn.unlock ()
