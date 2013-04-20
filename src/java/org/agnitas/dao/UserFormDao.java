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

package org.agnitas.dao;

import java.util.List;

import org.agnitas.beans.UserForm;

/**
 *
 * @author mhe
 */
public interface UserFormDao {
	/**
	 * Loads user form identified by form id and company id.
	 *
	 * @param formID
	 *            The id of the user form that should be loaded.
	 * @param companyID
	 *            The companyID for the user form.
	 * @return The UserForm or null on failure.
	 * @throws Exception 
	 */
    public UserForm getUserForm(int formID, int companyID) throws Exception;
    
	/**
	 * Loads user form identified by form name and company id.
	 *
	 * @param name
	 *            The name of the user form that should be loaded.
	 * @param companyID
	 *            The companyID for the user form.
	 * @return The UserForm or null on failure.
	 * @throws Exception 
	 */
    public UserForm getUserFormByName(String name, int companyID) throws Exception;

    /**
     * Saves or updates userForm.
     *
     * @param form
     *          The userForm that should be saved.
     * @return Saved userForm id.
     * @throws Exception 
     */
    public int storeUserForm(UserForm form) throws Exception;
    
    /**
     * Deletes user form identified by form name and company id.
     *
	 * @param formID
	 *            The id of the user form that should be deleted.
	 * @param companyID
	 *            The companyID for the user form that should be deleted.
     * @return true on success.
     */
    public boolean deleteUserForm(int formID, int companyID);

    /**
     * Load all user forms for company id.
     *
     * @param companyID
     *          The id of the company for user forms.
     * @return List of UserForm or empty list.
     */
    public List<UserForm> getUserForms(int companyID);
}
