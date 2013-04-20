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
import	sys, getopt
import	agn
agn.require ('2.0.0')
#
class SMCtrl:
	configTable = 'config_tbl'
	configClause = 'class = \'system\' AND name = \'sendmail-enable\''
	
	def __init__ (self):
		self.db = agn.DBase ()
		self.cursor = None
		self.valid = False
		if not self.db is None:
			self.cursor = self.db.cursor ()
			if not self.cursor is None:
				configTableExists = False
				for r in self.cursor.query ('SHOW TABLES'):
					if r[0].lower () == self.configTable:
						configTableExists = True
				if not configTableExists:
					self.cursor.execute ("""
CREATE TABLE %s (
	class	VARCHAR(32) NOT NULL,
	classid	INT NOT NULL,
	name	VARCHAR(32) NOT NULL,
	value	TEXT
)""" % self.configTable)
				self.valid = True
	
	def done (self):
		if not self.db is None:
			if not self.cursor is None:
				self.cursor.close ()
			self.db.close ()
		self.db = None
		self.cursor = None
	
	def enabled (self):
		r = self.cursor.querys ('SELECT value FROM %s WHERE %s' % (self.configTable, self.configClause))
		if not r is None:
			enabled = agn.atob (r[0])
		else:
			enabled = True
		return enabled
	
	def __set (self, enable):
		data =  {'enable': enable}
		if self.cursor.update ('UPDATE %s SET value = :enable WHERE %s' % (self.configTable, self.configClause), data) == 0:
			self.cursor.execute ('INSERT INTO %s (class, classid, name, value) VALUES (\'system\', 0, \'sendmail-enable\', :enable)' % self.configTable, data)
	
	def enable (self):
		self.__set ('True')
	
	def disable (self):
		self.__set ('False')
				
def main (pgm, args):
	opts = getopt.getopt (args, '')
	parm = opts[1]
	if len (parm) == 0:
		agn.die (s = 'Usage: %s <command> [<command-parameter>]' % pgm)
	ctrl = SMCtrl ()
	if not ctrl.valid:
		agn.die (s = 'Failed to setup database interface')
	fail = None
	if parm[0] == 'status':
		if ctrl.enabled ():
			print '1'
		else:
			print '0'
	elif parm[0] == 'enable':
		ctrl.enable ()
	elif parm[0] == 'disable':
		ctrl.disable ()
	else:
		fail = 'Unknown command %s' % parm[0]
	ctrl.done ()
	if not fail is None:
		agn.die (s = fail)
#
if __name__ == '__main__':
	main (sys.argv[0], sys.argv[1:])
