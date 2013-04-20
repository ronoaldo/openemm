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

package org.agnitas.beans;

/**
 * Interface for retrieving informations about failed logins.
 * These informations are
 * - number of failed logins since last successful login (Consider this like a counter
 *   that get reset to 0 in successful logins.)
 * - elapsed time since last failed login
 * 
 * Successful logins during a lock period are not noted.
 * 
 * @author Markus Dörschmidt
 *
 */
public interface FailedLoginData {
	/**
	 * Returns the number of failed logins since last successful login.
	 * 
	 * @return number of failed logins
	 */
	public int getNumFailedLogins();
	
	/**
	 * Returns elapsed time since last failed login.
	 * 
	 * @return elapsed time since last failed login in seconds
	 */
	public int getLastFailedLoginTimeDifference();
	
	/**
	 * Set number of failed logins since last login.
	 * 
	 * @param numFailedLogins number of failed logins since last login 
	 */
	public void setNumFailedLogins(int numFailedLogins);
	
	/**
	 * Set elapsed time since last failed login
	 * @param timeDifference elapsed time since last failed login in seconds 
	 */
	public void setLastFailedLoginTimeDifference(int timeDifference);
}
