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

import org.agnitas.dao.CleanDBDao;
import org.agnitas.util.AgnUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author Nicole Serek
 */
public class CleanDBDaoImpl implements CleanDBDao {
	
	private static final transient Logger logger = Logger.getLogger( CleanDBDaoImpl.class);
	
	@Override
	public void cleanup() {
		JdbcTemplate jdbc = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		String sql = null;
		
		if(AgnUtils.isMySQLDB()) {
			sql = "DELETE FROM bounce_tbl WHERE " + AgnUtils.changeDateName() + " < date_sub(curdate(), interval " + AgnUtils.getDefaultIntValue("bounces.maxRemain.days") + " day)";
		} else {
			// OracleDB handles bounces differently
		}
		
		try {
			jdbc.execute(sql);
		} catch (Exception e) {
			logger.error( "Error deleting old bounces: " + e.getMessage(), e);
			AgnUtils.sendExceptionMail("sql:" + sql, e);
		}
		
		sql = null;
		if(AgnUtils.isMySQLDB()) {
			sql = "DELETE cust, bind FROM customer_1_tbl cust, customer_1_binding_tbl bind WHERE cust.customer_id=bind.customer_id " +
				  "AND bind.user_status=5 AND bind." + AgnUtils.changeDateName() + " < date_sub(curdate(), interval " + AgnUtils.getDefaultIntValue("pending.maxRemain.days") + " day)";
		} else {
			// OracleDB handles pending subscribers differently
		}
		
		try {
			jdbc.execute(sql);
		} catch (Exception e) {
			logger.error( "error deleting pending subscribers: " + e.getMessage(), e);
			AgnUtils.sendExceptionMail("sql:" + sql, e);
		}
	}
	
	/**
     * Holds value of property applicationContext.
     */
    protected ApplicationContext applicationContext;
	
	/**
     * Setter for property applicationContext.
     * @param applicationContext New value of property applicationContext.
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
