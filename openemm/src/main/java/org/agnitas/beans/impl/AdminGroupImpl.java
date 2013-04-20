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

import java.util.Set;

import org.agnitas.beans.AdminGroup;

public class AdminGroupImpl implements AdminGroup {

	private static final long serialVersionUID = 4657656754098173278L;
	protected int companyID;
	protected String shortname;

	/**
	 * Holds value of property groupID.
	 */
	protected int groupID = 0;

	// * * * * *
	// SETTER:
	// * * * * *
	public void setCompanyID(int id) {
		companyID = id;
	}

	public void setShortname(String name) {
		shortname = name;
	}

	public int getCompanyID() {
		return companyID;
	}

	public String getShortname() {
		return shortname;
	}

	/**
	 * Getter for property groupID.
	 * 
	 * @return Value of property groupID.
	 */
	public int getGroupID() {
		return this.groupID;
	}

	/**
	 * Setter for property groupID.
	 * 
	 * @param groupID
	 *            New value of property groupID.
	 */
	public void setGroupID(int groupID) {
		this.groupID = groupID;
	}

	/**
	 * Holds value of property groupPermissions.
	 */
	protected Set<String> groupPermissions;

	/**
	 * Getter for property groupPermissions.
	 * 
	 * @return Value of property groupPermissions.
	 */
	public Set<String> getGroupPermissions() {

		return this.groupPermissions;
	}

	/**
	 * Setter for property groupPermissions.
	 * 
	 * @param groupPermissions
	 *            New value of property groupPermissions.
	 */
	public void setGroupPermissions(Set<String> groupPermissions) {

		this.groupPermissions = groupPermissions;
	}

	public boolean permissionAllowed(String token) {
		boolean result = false;

		String[] tokens = AdminImpl.getTokens(token);

		for (String subToken : tokens) {
			result |= this.groupPermissions.contains(subToken);
		}

		return result;
	}

	/**
	 * Holds value of property description.
	 */
	protected String description;

	/**
	 * Getter for property description.
	 * 
	 * @return Value of property description.
	 */
	public String getDescription() {

		return this.description;
	}

	/**
	 * Setter for property description.
	 * 
	 * @param description
	 *            New value of property description.
	 */
	public void setDescription(String description) {

		this.description = description;
	}
}
