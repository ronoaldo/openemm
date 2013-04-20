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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.agnitas.beans.DynamicTag;
import org.agnitas.beans.impl.DynamicTagImpl;
import org.agnitas.dao.DynamicTagDao;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * @author Andreas Rehak
 */
public class DynamicTagDaoImpl implements DynamicTagDao {
	
	private static final transient Logger logger = Logger.getLogger( DynamicTagDaoImpl.class);
	
    @Override
	public int	getIdForName(int mailingID, String name) {
		JdbcTemplate jdbc=new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));

		try {
			return jdbc.queryForInt("select dyn_name_id from dyn_name_tbl where mailing_id=? and dyn_name=?", new Object[] { new Integer(mailingID), name});
		} catch(Exception e) {
			logger.error( "Error getting ID for tag: " + name, e);
			
			return 0;
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

	@Override
	@SuppressWarnings("unchecked")
	public List<DynamicTag> getNameList(int companyId, int mailingId) {
		JdbcTemplate jdbc=new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		return jdbc.query("select dyn_name_id, dyn_name from dyn_name_tbl where mailing_id = ? and company_id = ? and deleted = 0", new Object[] { mailingId, companyId }, dynNameRowMapper);
	}
	
	private final RowMapper dynNameRowMapper = new RowMapper() {

		@Override
		public Object mapRow(ResultSet resultSet, int rowNum) throws SQLException {
			DynamicTag undoContent = new DynamicTagImpl();
			
			undoContent.setId(resultSet.getInt("dyn_name_id"));
			undoContent.setDynName(resultSet.getString("dyn_name"));
			
			return undoContent;
		}
		
	};
}
