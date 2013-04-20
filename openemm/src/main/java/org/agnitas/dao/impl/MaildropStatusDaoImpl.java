/*********************************************************************************
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
 ********************************************************************************/

package org.agnitas.dao.impl;

import javax.sql.DataSource;

import org.agnitas.dao.MaildropStatusDao;
import org.agnitas.util.AgnUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Andreas Rehak
 */
public class MaildropStatusDaoImpl implements MaildropStatusDao {

	private static final transient Logger logger = Logger.getLogger( MaildropStatusDaoImpl.class);
	
	private DataSource dataSource;

	@Override
	public boolean	delete(int id) {
		JdbcTemplate jdbc=new JdbcTemplate(dataSource);
		String sql = "delete from maildrop_status_tbl where status_id=?";

		try {
			if(jdbc.update(sql, new Object[] {new Integer(id)}) < 1) {
				return false;
			}
		} catch(Exception e) {
			logger.error( "Error: " + e.getMessage(), e);
			AgnUtils.sendExceptionMail("sql:" + sql, e);

			return false;
		}
		return true;
	}
	
	@Override
	public int deleteUnsentWorldMailingEntries(int mailingID) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		String sql = "DELETE FROM MAILDROP_STATUS_TBL WHERE genstatus = 0 AND status_field = 'W' AND mailing_id = ? ";
		int affectedRows = template.update(sql, new Object[] { mailingID} );
		
		return affectedRows;
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
