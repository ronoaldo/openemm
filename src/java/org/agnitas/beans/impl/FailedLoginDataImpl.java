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

package org.agnitas.beans.impl;

import org.agnitas.beans.FailedLoginData;

/**
 * Class for storing informations about failed logins.
 * Stored are number of failed logins since the last 
 * successful logins and the elapsed time since the last failed login.
 * 
 * Successful logins during a lock period are not noted.
 * 
 * @author Markus Dörschmidt
 *
 */
public class FailedLoginDataImpl implements FailedLoginData {
	/**
	 * Number of failed logins 
	 */
	protected int numFailedLogins;
	
	/**
	 * Elapsed time since last failed login in seconds
	 */
	protected int lastFailedLoginTimeDifference;
	
	@Override
	public int getLastFailedLoginTimeDifference() {
		return this.lastFailedLoginTimeDifference;
	}

	@Override
	public int getNumFailedLogins() {
		return this.numFailedLogins;
	}

	@Override
	public void setLastFailedLoginTimeDifference(int timeDifference) {
		this.lastFailedLoginTimeDifference = timeDifference;	
	}

	@Override
	public void setNumFailedLogins(int numFailedLogins) {
		this.numFailedLogins = numFailedLogins;
	}
}
