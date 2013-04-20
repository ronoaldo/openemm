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
import java.util.Set;

import org.agnitas.beans.Admin;
import org.agnitas.beans.AdminEntry;
import org.agnitas.beans.impl.PaginatedListImpl;

/**
 * Dao for Admin Objects Loads and saves Adminobjects to/from database.
 * 
 * @author ar
 */
public interface AdminDao {

	/**
	 * Loads an admin identified by admin id and company id.
	 * 
	 * @param adminID
	 *            The id of the admin that should be loaded.
	 * @param companyID
	 *            The companyID for the admin.
	 * @return The Admin or null on failure.
	 */
	public Admin getAdmin(int adminID, int companyID);

	/**
	 * Loads an admin identified by login data.
	 * 
	 * @param name
	 *            The username of the admin.
	 * @param password
	 *            The password for the admin.
	 * @return The Admin or null on failure.
	 */
	public Admin getAdminByLogin(String name, String password);

	/**
	 * Saves an admin.
	 * 
	 * @param admin
	 *            The admin that should be saved.
	 */
	public void save(Admin admin);

    /**
     * Deletes an admin and his permissions.
     *
     * @param admin
     *            The admin to be deleted.
     * @return true
     */
	public boolean delete(Admin admin);

    /**
     * Loads all admins of certain company
     *
     * @param companyID
     *                The id of the company for admins
     * @return  List of AdminEntry or empty list
     */
	public List<AdminEntry> getAllAdminsByCompanyId(int companyID);

    /**
     * Selects all admins of certain company and creates paginated list according to given criteria for sorting and pagination
     *
     * @param companyID
     *                The id of the company for admins
     * @param sort
     *                The name of the column to be sorted
     * @param direction
     *                The sort direction , 0 (for ascending) or 1 (for descending)
     * @param page
     *                The number of the page
     * @param rownums
     *                The number of rows to be shown on page
     * @return  PaginatedList of AdminEntry or empty list
     */
	public PaginatedListImpl<AdminEntry> getAdminListByCompanyId(int companyID, String sort, String direction, int page, int rownums);

    /**
     * Loads list of all admins are stored in database
     *
     * @return  List of AdminEntry or empty list
     */
	public List<AdminEntry> getAllAdmins();

    /**
     * Checks the existance of any admin with given username for certain company
     *
     * @param companyId
     *                The id of the company of the admin
     * @param username
     *                User name for the admin
     * @return true if the admin exists, and false otherwise
     */
	public boolean adminExists(int companyId, String username);

    /**
     * Saves permission set for given admin
     *
     * @param adminID
     *              The id of the admin whose right are to be stored
     * @param userRights
     *               Set of permissions
     */
    public void saveAdminRights(int adminID, Set<String> userRights);
}
