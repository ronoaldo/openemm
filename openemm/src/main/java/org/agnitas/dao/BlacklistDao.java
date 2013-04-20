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

import org.agnitas.beans.BlackListEntry;
import org.agnitas.beans.Mailinglist;
import org.agnitas.beans.impl.PaginatedListImpl;

/**
 * Dao for Blacklist Objects
 *
 * @author ar
 */
public interface BlacklistDao {
    /**
     * Adds the given email to the blacklist.
     *
     * @param companyID the company to add it for.
     * @param email the address to add to the blacklist.
     * @return true on success.
     */
	public boolean insert(int companyID, String email);

    /**
     * Remove the given email from the blacklist.
     *
     * @param companyID the company to work on.
     * @param email the address to remove from to the blacklist.
     * @return true on success.
     */
	public boolean delete(int companyID, String email);

    /**
     * Get a list of blacklisted recipients
     *
     * @param companyID the company to work on.
     * @param sort the field for sorting of returned list
     * @param direction the direction of  sorting of returned list
     * @param page offset of the first item to return
     * @param rownums maximum number of items to return
     * @return list of blacklisted  recipients
     */
    public PaginatedListImpl<BlackListEntry> getBlacklistedRecipients(int companyID, String sort, String direction , int page, int rownums );

    /**.
     * Check the presence given company and email in the blacklist.
     *
     * @param companyID the company to check.
     * @param email the address to check.
     * @return true if  the company and the email presents in the blacklist.
     */
    public boolean exist(int companyID, String email);

    /**
     * Get a list of email addresses for given company from the  blacklist.
     *
     * @param companyID the company to work on.
     * @return list of email addresses for given company from the  blacklist.
     */
    public List<String> getBlacklist(int companyID);

    /**
     * Lists all mailinglists to these a recipient with given email address is bound with <i>blacklisted</i> state.
     * 
     * @param companyId company ID
     * @param email email to search
     * 
     * @return list of all mailinglists with "blacklisted" binding for given address
     */
	public List<Mailinglist> getMailinglistsWithBlacklistedBindings( int companyId, String email);

	/**
	 * Updates all blacklisted bindings for a list of mailinglists for a given email to a given user status.
	 *  
	 * @param companyId	company ID
	 * @param email email to update related bindings
	 * @param mailinglistIds list of mailinglist ID to update
	 * @param userStatus new user status
	 */
	public void updateBlacklistedBindings(int companyId, String email, List<Integer> mailinglistIds, int userStatus);
}
