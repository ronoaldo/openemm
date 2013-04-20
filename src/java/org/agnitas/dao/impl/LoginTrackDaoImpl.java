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
 * the code written by AGNITAS AG are Copyright (c) 2009 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/

package org.agnitas.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.agnitas.beans.FailedLoginData;
import org.agnitas.beans.impl.FailedLoginDataImpl;
import org.agnitas.dao.LoginTrackDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * DAO implementation for login tracking using a MySQL database.
 * 
 * @author Markus Dörschmidt
 */
public class LoginTrackDaoImpl implements LoginTrackDao {
	/**
	 * Status for a successful login
	 */
	public static final int LOGIN_TRACK_STATUS_SUCCESS = 10;
	
	/**
	 * Status for a failed login
	 */
	public static final int LOGIN_TRACK_STATUS_FAILED = 20;
	
	/**
	 * Status for a successful login during lock period
	 */
	public static final int LOGIN_TRACK_STATUS_DURING_BLOCK = 40;
	
	/**
	 * RowMapper for DB access
	 */
	protected final static RowMapper failedLoginDataRowMapper = new FailedLoginDataRowMapper();
	
	protected DataSource dataSource;
	
	public void setDataSource( DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * Implementation of the RowMapper
	 * 
	 * @author Markus Dörschmidt
	 *
	 */
	static class FailedLoginDataRowMapper implements RowMapper {
		@Override
		public Object mapRow(ResultSet rs, int row) throws SQLException {
			FailedLoginDataImpl result = new FailedLoginDataImpl();
			
			result.setNumFailedLogins(rs.getInt(1));
			result.setLastFailedLoginTimeDifference(rs.getInt(2));
			
			return result;
		}
	
	}
	
	@Override
	public FailedLoginData getFailedLoginData(String ipAddress) {
		JdbcTemplate template = new JdbcTemplate( dataSource);
		String sql = "SELECT count(ip_address), ifnull(timestampdiff(second, max(ifnull(creation_date, 0)), now()),0) " +
			"FROM login_track_tbl " +
			"WHERE ip_address = ? " +
			"AND login_status = " + LoginTrackDaoImpl.LOGIN_TRACK_STATUS_FAILED + " " +
			"AND creation_date > (" +
			"     SELECT ifnull(max(creation_date), 0) " +
			"     FROM login_track_tbl " +
			"     WHERE ip_address = ? " +
			"     AND login_status = " + LoginTrackDaoImpl.LOGIN_TRACK_STATUS_SUCCESS + ")";
		
		List<FailedLoginData> list = (List<FailedLoginData>) template.query(sql, new Object[] { ipAddress, ipAddress }, failedLoginDataRowMapper);
		
		if (list.size() == 1)
			return list.get(0);
		else
			return new FailedLoginDataImpl();  // No failed logins found
	}

	@Override
	public void trackFailedLogin(String ipAddress, String username) {
		trackLoginStatus(ipAddress, username, LoginTrackDaoImpl.LOGIN_TRACK_STATUS_FAILED);
	}

	@Override
	public void trackSuccessfulLogin(String ipAddress, String username) {
		trackLoginStatus(ipAddress, username, LoginTrackDaoImpl.LOGIN_TRACK_STATUS_SUCCESS);
	}
	
	@Override
	public void trackLoginDuringBlock(String ipAddress, String username) {
		trackLoginStatus(ipAddress, username, LoginTrackDaoImpl.LOGIN_TRACK_STATUS_DURING_BLOCK);
	}
	
	/**
	 * Generic method for recording logins.
	 * 
	 * @param ipAddress IP address of host
	 * @param username use username in login
	 * @param status login status
	 */
	protected void trackLoginStatus(String ipAddress, String username, int status) {
		JdbcTemplate template = new JdbcTemplate( dataSource);
		String sql = "INSERT INTO login_track_tbl (ip_address, login_status, username) VALUES (?,?,?)";
		
		template.update(sql, new Object[] { ipAddress, status, username });
	}
	
	@Override
	public int deleteOldRecords(int holdBackDays, int maxRecords) {
		JdbcTemplate template = new JdbcTemplate( dataSource);
		String sql = "DELETE FROM login_track_tbl WHERE timestampdiff(day, creation_date, now()) > ? LIMIT ?";
		
		return template.update(sql, new Object[] { new Integer(holdBackDays), new Integer(maxRecords) });
	}
}
