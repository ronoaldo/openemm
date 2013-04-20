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

package org.agnitas.beans.impl;

import java.security.MessageDigest;
import java.util.Locale;
import java.util.Set;

import org.agnitas.beans.Admin;
import org.apache.log4j.Logger;

public class AdminImpl implements Admin {

	private static final transient Logger logger = Logger.getLogger(AdminImpl.class);

	private static final long serialVersionUID = -6728189620613687946L;
	protected org.agnitas.beans.Company company = new org.agnitas.beans.impl.CompanyImpl();
	protected int adminID;
	protected int layoutID;
	protected int layoutBaseID;
	protected String adminCountry;
	protected String shortname;
	protected String username;
	protected String password;
	protected String fullname;
	protected String adminLang;
	protected String adminLangVariant = "";
	protected String adminTimezone;
	protected java.sql.Timestamp creationDate;
	protected java.util.Date lastPasswordChange = new java.util.Date();
	protected int mailtracking = 0;
	protected int preferredListSize;
	private int defaultImportProfileID;

	/**
	 * Holds value of property group.
	 */
	protected org.agnitas.beans.AdminGroup group = new org.agnitas.beans.impl.AdminGroupImpl();

	// * * * * *
	// SETTER:
	// * * * * *
	@Override
	public void setCompany(org.agnitas.beans.Company id) {
		company = id;
	}

	@Override
	public void setShortname(String name) {
		shortname = name;
	}

	@Override
	public void setAdminID(int adminID) {
		this.adminID = adminID;
	}

	@Override
	public void setCompanyID(int companyID) {
		company.setId(companyID);
	}

	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public void setCreationDate(java.sql.Timestamp creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public void setLastPasswordChange(java.util.Date lastPasswordChange) {
		this.lastPasswordChange = lastPasswordChange;
	}

	@Override
	public void setPassword(String password) {
		this.password = password != null ? password : "";
		try {
			this.passwordHash = MessageDigest.getInstance("MD5").digest(this.password.getBytes());
		} catch (Exception e) {
			logger.fatal("Error setting password hash", e);
		}
	}

	@Override
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	@Override
	public void setAdminLang(String adminLang) {
		this.adminLang = adminLang;
	}

	@Override
	public void setAdminLangVariant(String adminLangVariant) {
		this.adminLangVariant = adminLangVariant;
	}

	@Override
	public void setAdminTimezone(String adminTimezone) {
		this.adminTimezone = adminTimezone;
	}

	@Override
	public void setLayoutID(int layoutID) {
		this.layoutID = layoutID;
	}

	@Override
	public void setLayoutBaseID(int layoutBaseID) {
		this.layoutBaseID = layoutBaseID;
	}

	@Override
	public void setAdminCountry(String adminCountry) {
		this.adminCountry = adminCountry;
	}

	@Override
	public void setMailtracking(int mailtracking) {
		this.mailtracking = mailtracking;
	}

	// * * * * *
	// GETTER:
	// * * * * *
	@Override
	public org.agnitas.beans.Company getCompany() {

		return company;
	}

	@Override
	public String getShortname() {
		return shortname;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public int getAdminID() {
		return this.adminID;
	}

	@Override
	public int getCompanyID() {
		return company.getId();
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public java.sql.Timestamp getCreationDate() {
		return creationDate;
	}

	@Override
	public java.util.Date getLastPasswordChange() {
		return lastPasswordChange;
	}

	@Override
	public String getFullname() {
		return this.fullname;
	}

	@Override
	public String getAdminLang() {
		return this.adminLang;
	}

	@Override
	public String getAdminLangVariant() {
		return this.adminLangVariant;
	}

	@Override
	public String getAdminTimezone() {
		return this.adminTimezone;
	}

	@Override
	public int getLayoutID() {
		return this.layoutID;
	}

	@Override
	public int getLayoutBaseID() {
		return this.layoutBaseID;
	}

	@Override
	public String getAdminCountry() {
		return this.adminCountry;
	}

	@Override
	public int getMailtracking() {
		return mailtracking;
	}

	/**
	 * Getter for property groupID.
	 * 
	 * @return Value of property groupID.
	 */
	@Override
	public org.agnitas.beans.AdminGroup getGroup() {

		return this.group;
	}

	/**
	 * Setter for property groupID.
	 * 
	 * @param group
	 */
	@Override
	public void setGroup(org.agnitas.beans.AdminGroup group) {

		this.group = group;
	}

	/**
	 * Holds value of property adminPermissions.
	 */
	protected Set<String> adminPermissions;

	/**
	 * Getter for property adminPermissions.
	 * 
	 * @return Value of property adminPermissions.
	 */
	@Override
	public Set<String> getAdminPermissions() {

		return this.adminPermissions;
	}

	/**
	 * Setter for property adminPermissions.
	 * 
	 * @param adminPermissions
	 *            New value of property adminPermissions.
	 */
	@Override
	public void setAdminPermissions(Set<String> adminPermissions) {

		this.adminPermissions = adminPermissions;
	}

	public static String[] getTokens(String token) {
		String[] tokens = new String[0];
		if (token != null) {
			tokens = token.split("\\|");
		}
		return tokens;
	}

	@Override
	public boolean permissionAllowed(String token) {
		boolean result = false;

		final String[] tokens = getTokens(token);

		for (String subToken : tokens) {
			result |= this.adminPermissions.contains(subToken);
		}

		if (this.group != null) {
			if (this.group.permissionAllowed(token)) {
				result = true;
			}
		}

		return result;
	}

	@Override
	public java.util.Locale getLocale() {
		return new Locale(this.adminLang, this.adminCountry);
	}

	/**
	 * Holds value of property passwordHash.
	 */
	private byte[] passwordHash;

	/**
	 * Getter for property passwordHash.
	 * 
	 * @return Value of property passwordHash.
	 */
	@Override
	public byte[] getPasswordHash() {
		return this.passwordHash;
	}

	/**
	 * Setter for property passwordHash.
	 * 
	 * @param passwordHash
	 *            New value of property passwordHash.
	 */
	@Override
	public void setPasswordHash(byte[] passwordHash) {
		this.passwordHash = passwordHash;
	}

	@Override
	public int getPreferredListSize() {
		return this.preferredListSize;
	}

	@Override
	public void setPreferredListSize(int preferredlistsize) {
		this.preferredListSize = preferredlistsize;

	}

	@Override
	public int getDefaultImportProfileID() {
		return defaultImportProfileID;
	}

	@Override
	public void setDefaultImportProfileID(int defaultImportProfileID) {
		this.defaultImportProfileID = defaultImportProfileID;
	}
}
