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
#

import	BaseHTTPServer
import	sys, os, signal, stat, errno, time
import	agn
agn.require ('2.0.0')
#
agn.loglevel = agn.LV_INFO
#
class Request (BaseHTTPServer.BaseHTTPRequestHandler):
	def out (self, str):
		self.wfile.write (str)
		self.wfile.flush ()

	def err (self, str):
		self.out ('-ERR: ' + str + '\r\n')
	
	def ok (self, str):
		self.out ('+OK: ' + str + '\r\n')
	
	def data (self, data):
		self.out ('*DATA %d\r\n%s' % (len (data), data))
	
	def do_GET (self):
		path = self.path
		agn.log (agn.LV_VERBOSE, 'get', 'Got path: ' + path)
		n = path.find ('?')
		if n != -1:
			query = path[n + 1:]
			path = path[:n]
		else:
			query = None
		self.out ('HTTP/1.0 200 OK\r\n')
		self.out ('Content-Type: text/plain\r\n')
		self.out ('\r\n')
		if path == '/ping':
			self.ok ('pong')
		elif path == '/reread':
			updateLog = agn.base + os.sep + 'var' + os.sep + 'run' + os.sep + 'bav-update.log'
			size = -1
			try:
				st = os.stat (updateLog)
				size = st[stat.ST_SIZE]
			except OSError, e:
				agn.log (agn.LV_INFO, 'get', 'Failed to stat %s %s' % (updateLog, `e.args`))
				if e.args[0] == errno.ENOENT:
					self.err ('Missing file %s, update daemon not running?' % updateLog)
				else:
					self.err ('Unable to stat %s %s' % (updateLog, `e.args`))
			if size != -1:
				(rc, msg) = agn.signallock ('bav-update', signal.SIGUSR1)
				if not rc:
					agn.log (agn.LV_INFO, 'get', 'Unable to signal bav-update: %s' % msg)
					self.err ('Failed to signal update daemon, perhaps its not running?')
				else:
					n = 10
					while n > 0:
						nsize = -1
						try:
							st = os.stat (updateLog)
							nsize = st[stat.ST_SIZE]
						except OSError, e:
							agn.log (agn.LV_INFO, 'get', 'Unable to restat %s %s' % (updateLog, `e.args`))
							self.err ('Unable to stat %s again %s' % (updateLog, `e.args`))
						if nsize > size or nsize == -1:
							break
						n -= 1
						if n > 0:
							time.sleep (1)
					if nsize == size:
						agn.log (agn.LV_INFO, 'get', 'Update process did not wrote %s' % updateLog)
						self.err ('Update process seems not to respond')
					else:
						try:
							fd = open (updateLog, 'r')
							fd.seek (size)
							inp = fd.readline ()
							fd.close ()
							if inp:
								inp = agn.chop (inp)
								parts = inp.split (None, 1)
								if len (parts) == 2:
									if parts[1] == 'success':
										agn.log (agn.LV_VERBOSE, 'get', 'Got %s' % inp)
										self.ok ('Update completed')
									else:
										agn.log (agn.LV_INFO, 'get', 'Update failed: %s' % inp)
										self.err ('Updated failed: ' + parts[1])
								else:
									agn.log (agn.LV_INFO, 'get', 'Update failed with invalid entry: %s' % inp)
									self.err ('Invalid entry in %s: %s' % (updateLog, inp))
							else:
								agn.log (agn.LV_INFO, 'get', 'Update failed with missing entry')
								self.err ('Missing entry in %s' % updateLog)
						except IOError, e:
							agn.log (agn.LV_INFO, 'get', 'Update failed due to unreadable file %s %s' % (updateLog, `e.args`))
							self.err ('Unable to read %s %s' % (updateLog, `e.args`))
		else:
			agn.log (agn.LV_WARNING, 'get', 'Invalid command/path ' + self.path)
			self.err ('Unknown path')

class Server (BaseHTTPServer.HTTPServer):
	def __init__ (self):
		BaseHTTPServer.HTTPServer.__init__ (self, ('localhost', 8900), Request)

def handler (sig, stack):
	agn.log (agn.LV_INFO, 'trigger', 'Going down')
	sys.exit (0)

signal.signal (signal.SIGTERM, handler)
signal.signal (signal.SIGINT, handler)
signal.signal (signal.SIGHUP, signal.SIG_IGN)
agn.log (agn.LV_INFO, 'trigger', 'Starting up')
server = Server ()
server.serve_forever ()

