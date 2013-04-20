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
import	time
import	agn
agn.require ('2.3.0')
agn.loglevel = agn.LV_INFO
#
agn.lock ()
agn.log (agn.LV_INFO, 'main', 'Starting up')
db = agn.DBaseID ()
if db is None:
	agn.die (s = 'Failed to setup database interface')
db.log = lambda a: agn.log (agn.LV_DEBUG, 'db', a)
curs = db.cursor ()
if curs is None:
	agn.die (s = 'Failed to get database cursor')
mailtrackTable = 'bounce_collect_tbl'
#
#
# 1.) Kill old softbounces

old = time.localtime (time.time () - 179 * 24 * 60 * 60)
query = 'DELETE FROM softbounce_email_tbl WHERE creation_date < \'%04d-%02d-%02d\'' % (old[0], old[1], old[2])
try:
	agn.log (agn.LV_INFO, 'kill', 'Remove old addresses from softbounce_email_tbl')
	rows = curs.update (query, commit = True)
	agn.log (agn.LV_INFO, 'kill', 'Removed %d address(es)' % rows)
except agn.error, e:
	agn.log (agn.LV_ERROR, 'kill', 'Failed to remove old entries using %s: %s (%s)' % (query, e.msg, db.lastError ()))
#
#
# 2.) Collect bounces to mailtrack_tbl
query = '*unset collect*'
try:
	query = 'UPDATE timestamp_tbl SET prev = cur, temp = current_timestamp WHERE timestamp_id = 2'
	rows = curs.update (query)
	if rows == 0:
		agn.log (agn.LV_INFO, 'collect', 'Missing entry in timestamp_tbl, try to create one')

		query = 'INSERT INTO timestamp_tbl (timestamp_id, description, cur, prev, temp) VALUES (2, \'Softbounce collection marker\', now(), \'1980-01-01\', now())'
		rows = curs.update (query)
	if rows != 1:
		raise agn.error ('Failed to set timestamp using %s' % query)
	db.commit ()
	agn.log (agn.LV_INFO, 'collect', 'Updated timestamps')
	time.sleep (1)

	iquery = 'INSERT INTO %s (customer_id, company_id, mailing_id, status_id, change_date) ' % mailtrackTable
	iquery += 'VALUES (:customer, :company, :mailing, 90, now())'
	insert = db.cursor ()
	if insert is None:
		raise agn.error ('Failed to get new cursor for insertion')
	bquery = db.cursor ()
	if bquery is None:
		raise agn.error ('Failed to get new cursor for bounce query')
	query =  'SELECT customer_id, company_id, mailing_id, detail '
	query += 'FROM bounce_tbl '

	query += 'WHERE change_date > (SELECT cur FROM timestamp_tbl WHERE timestamp_id = 2) AND change_date <= (SELECT temp FROM timestamp_tbl WHERE timestamp_id = 2) '
	query += 'ORDER BY company_id, customer_id'
	cur = [0, 0, 0, 0]
	(records, uniques, inserts) = (0, 0, 0)
	if bquery.query (query) is None:
		raise agn.error ('Failed to query bounce_tbl using: %s' % query)
	while not cur is None:
		try:
			record = bquery.next ()
			records += 1
		except StopIteration:
			record = None
		if record is None or cur[0] != record[0] or cur[1] != record[1]:
			if not record is None:
				uniques += 1
			if cur[0] > 0 and cur[3] >= 400 and cur[3] < 510:
				parm = {
					'customer': cur[0],
					'company': cur[1],
					'mailing': cur[2]
					}
				insert.update (iquery, parm)
				inserts += 1
			cur = record
		elif record[3] > cur[3]:
			cur = [record[0], record[1], record[2], record[3]]
	db.commit ()
	bquery.close ()
	insert.close ()
	agn.log (agn.LV_INFO, 'collect', 'Read %d records (%d uniques) and inserted %d' % (records, uniques, inserts))
	query = 'UPDATE timestamp_tbl SET cur = temp WHERE timestamp_id = 2'
	curs.update (query)
	db.commit ()
	agn.log (agn.LV_INFO, 'collect', 'Timestamp updated')
except agn.error, e:
	agn.log (agn.LV_ERROR, 'collect', 'Failed: %s (last query %s) %s' % (e.msg, query, db.lastError ()))
#
#
# 3.) Merge them!

query = '*unset merge*'
try:
	query = 'SELECT MAX(mailtrack_id) FROM %s' % mailtrackTable
	data = curs.querys (query)
	if data is None:
		raise agn.error ('Unable to fetch max mailtrack_id')
	if data[0] is None:
		max_mailtrack_id = 0
	else:
		max_mailtrack_id = data[0]

	iquery = 'INSERT INTO softbounce_email_tbl (company_id, email, bnccnt, mailing_id, creation_date, change_date) VALUES (:company, :email, 1, :mailing, now(), now())'
	icurs = db.cursor ()

	uquery = 'UPDATE softbounce_email_tbl SET mailing_id = :mailing, change_date = now(), bnccnt=bnccnt+1 WHERE company_id = :company AND email = :email'
	ucurs = db.cursor ()
	squery = 'SELECT count(*) FROM softbounce_email_tbl WHERE company_id = :company AND email = :email'
	scurs = db.cursor ()
	dquery = 'DELETE FROM %s WHERE mailtrack_id < %d AND status_id = 90 AND company_id = :company' % (mailtrackTable, max_mailtrack_id)
	dcurs = db.cursor ()
	if None in [ icurs, ucurs, scurs, dcurs ]:
		raise agn.error ('Unable to setup curses for merging')
	
	query =  'SELECT distinct company_id FROM %s ' % mailtrackTable
	query += 'WHERE status_id = 90 '
	query += 'AND mailtrack_id < %d ' % max_mailtrack_id
	query += 'AND company_id IN (SELECT company_id FROM company_tbl WHERE status = \'active\')'
		
	for company in [c[0] for c in curs.queryc (query)]:
		agn.log (agn.LV_INFO, 'merge', 'Working on %d' % company)
		query =  'SELECT mt.customer_id, mt.mailing_id, cust.email '
		query += 'FROM %s mt, customer_%d_tbl cust ' % (mailtrackTable, company)
		query += 'WHERE cust.customer_id = mt.customer_id '
		query += 'AND mt.company_id = %d ' % company
		query += 'AND mt.status_id = 90 '
		query += 'ORDER BY cust.email, mt.mailing_id'

		for record in curs.query (query):
			(cuid, mid, eml) = record
			parm = {
				'company': company,
				'customer': cuid,
				'mailing': mid,
				'email': eml
			}
			data = scurs.querys (squery, parm, cleanup = True)
			if not data is None:
				if data[0] == 0:
					icurs.update (iquery, parm, cleanup = True)
				else:
					ucurs.update (uquery, parm, cleanup = True)
		parm = {
			'company': company
		}
		dcurs.update (dquery, parm, cleanup = True)
	db.commit ()
	icurs.close ()
	ucurs.close ()
	scurs.close ()
	dcurs.close ()
except agn.error, e:
	agn.log (agn.LV_ERROR, 'merge', 'Failed: %s (last query %s) %s' % (e.msg, query, db.lastError ()))
#
#
# 4.) Make softbounce to hardbounce
query = '*unset unsub*'
try:
	query =  'SELECT distinct company_id FROM softbounce_email_tbl'
	query += ' WHERE company_id IN (SELECT company_id FROM company_tbl WHERE status = \'active\')'
	query += ' ORDER BY company_id'
	stats = []
	for company in [c[0] for c in curs.queryc (query)]:
		cstat = [company, 0, 0]
		stats.append (cstat)
		agn.log (agn.LV_INFO, 'unsub', 'Working on %d' % company)
		dquery = 'DELETE FROM softbounce_email_tbl WHERE company_id = %d AND email = :email' % company
		dcurs = db.cursor ()

		uquery =  'UPDATE customer_%d_binding_tbl SET user_status = 2, user_remark = \'Softbounce\', exit_mailing_id = :mailing, change_date = now() ' % company
		uquery += 'WHERE customer_id = :customer AND user_status = 1'
		ucurs = db.cursor ()

		squery =  'SELECT email, mailing_id, bnccnt, creation_date, change_date '
		squery += 'FROM softbounce_email_tbl '

		squery += 'WHERE company_id = %d AND bnccnt > 7 AND DATEDIFF(change_date,creation_date) > 30' % company
		scurs = db.cursor ()
		if None in [dcurs, ucurs, scurs]:
			raise agn.error ('Failed to setup curses')
		ccount = 0
		for record in scurs.query (squery):
			parm = {
				'email': record[0],
				'mailing': record[1],
				'bouncecount': record[2],
				'creationdate': record[3],
				'timestamp': record[4],
				'customer': None
			}
			query =  'SELECT customer_id FROM customer_%d_tbl WHERE email = :email ' % company
			data = curs.querys (query, parm, cleanup = True)
			if data is None:
				continue
			custs = [agn.struct (id = _d, click = 0, open = 0) for _d in data if _d]
			if not custs:
				continue
			if len (custs) == 1:
				cclause = 'customer_id = %d' % custs[0].id
			else:
				cclause = 'customer_id IN (%s)' % ', '.join ([str (_c.id) for _c in custs])
			old = time.localtime (time.time () - 30 * 24 * 60 * 60)

			query =  'SELECT customer_id, count(*) FROM rdir_log_tbl WHERE %s AND company_id = %d' % (cclause, company)
			query += ' AND change_date > \'%04d-%02d-%02d\'' % (old[0], old[1], old[2])
			query += ' GROUP BY customer_id'
			for r in curs.queryc (query, parm, cleanup = True):
				for c in custs:
					if c.id == r[0]:
						c.click += r[1]
			query =  'SELECT customer_id, count(*) FROM onepixel_log_tbl WHERE %s AND company_id = %d' % (cclause, company)
			query += ' AND change_date > \'%04d-%02d-%02d\'' % (old[0], old[1], old[2])
			query += ' GROUP BY customer_id'
			for r in curs.queryc (query, parm, cleanup = True):
				for c in custs:
					if c.id == r[0]:
						c.open += r[1]
			for c in custs:
				if c.click > 0 or c.open > 0:
					cstat[1] += 1
					agn.log (agn.LV_INFO, 'unsub', 'Email %s [%d] has %d klick(s) and %d onepix(es) -> active' % (parm['email'], c.id, c.click, c.open))
				else:
					cstat[2] += 1
					agn.log (agn.LV_INFO, 'unsub', 'Email %s [%d] has no klicks and no onepixes -> hardbounce' % (parm['email'], c.id))
					parm['customer'] = c.id
					ucurs.update (uquery, parm, cleanup = True)
			dcurs.update (dquery, parm, cleanup = True)
			ccount += 1
			if ccount == 1000:
				agn.log (agn.LV_INFO, 'unsub', 'Commiting')
				db.commit ()
				ccount = 0
		db.commit ()
		scurs.close ()
		ucurs.close ()
		dcurs.close ()
	for cstat in stats:
		agn.log (agn.LV_INFO, 'unsub', 'Company %d has %d active and %d marked as hardbounced users' % tuple (cstat))
except agn.error, e:
	agn.log (agn.LV_ERROR, 'unsub', 'Failed: %s (last query %s) %s' % (e.msg, query, db.lastError ()))
#
#
# X.) Cleanup
db.commit ()
curs.close ()
db.close ()
agn.log (agn.LV_INFO, 'main', 'Going down')
agn.unlock ()
