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

import org.agnitas.beans.BindingEntry;
import org.agnitas.target.Target;
import org.springframework.context.ApplicationContextAware;


/**
 *
 * @author ar
 */
public interface BindingEntryDao extends ApplicationContextAware {

	/**
	 * Load a binding from database. Uses recipientID, mailinglistID and
	 * mediaType of the given binding.
	 *
	 * @param recipientID
     *          The id of the recipient for the binding.
	 * @param companyID
     *          The id of the company for the binding.
     * @param mailinglistID
     *          The id of the mailinglist for the binding.
     * @param mediaType
     *          The value of mediatype for the binding.
	 * @return The BindingEntry or null on failure.
	 */
	BindingEntry get(int recipientID, int companyID,
			int mailinglistID, int mediaType);

    /**
     * Updates existing Binding in Database or create new Binding.
     *
     * @param companyID
     *          The id of the company for the binding
     * @param entry
     *          The Binding to update or create
     */

	void save(int companyID, BindingEntry entry);

	/**
	 * Updates the Binding in the Database
	 *
     * @param entry
     *          Binding to update
	 * @param companyID
     *          The company ID of the Binding
	 * @return True: Sucess
	 * False: Failure
	 */
	boolean updateBinding(BindingEntry entry, int companyID);

	/**
	 * Inserts a new binding into the database.
	 *
	 * @param entry
     *          The entry to create.
	 * @param companyID
     *          The company we are working on.
	 * @return true on success.
	 */
	boolean insertNewBinding(BindingEntry entry, int companyID);

    /**
     * Update the status for the binding. Also updates exit_mailing_id and
     * user_remark to reflect the status change.
     *
     * @param entry
     *          The entry on which the status has changed.
     * @param companyID
     *          The company we are working on.
     * @return true on success.
     */
	boolean updateStatus(BindingEntry entry, int companyID);

    /**
     * Set given email to status optout. The given email can be an sql
     * like pattern.
     *
     * @param email
     *          The sql like pattern of the email-address.
     * @param CompanyID
     *          Only update addresses for this company.
     */
	boolean optOutEmailAdr(String email, int CompanyID);

    /**
     * Subscribes all customers in the given target group to the given mailinglist.
     *
     * @param companyID     The company to work in.
     * @param mailinglistID The id of the mailinglist to which the targets should be subscribed.
     * @param target        The target describing the recipients that shall be added.
     * @return true on success.
     */
    boolean addTargetsToMailinglist(int companyID, int mailinglistID, Target target);

    /* moved form BindingEntry */

	/**
	 * Load a binding from database. Uses recipientID, mailinglistID and
	 * mediaType of the given binding.
	 *
	 * @param entry
     *          Binding that holds parameters for loading.
	 * @param companyID
     *          The id of the company for the binding.
	 * @return True: Sucess
	 * False: Failure
	 */
    boolean getUserBindingFromDB(BindingEntry entry, int companyID);

    /**
     * Check if Binding entry exists.
     *
     * @param customerId
     *          The id of the customer for the binding
     * @param companyId
     *           The id of the company for the binding.
     * @param mailinglistId
     *          The id of the mailinglist for the binding.
     * @param mediatype
     *           The value of mediatype for the binding.
     * @return true if the Binding exists, and false otherwise
     */

    boolean exist(int customerId, int companyId, int mailinglistId, int mediatype);

    /**
     * Delete Binding. Uses customerId, companyId, mailinglistId and
	 * mediatype of the given binding.
     *
     * @param customerId
     *          The id of the customer for the binding.
     * @param companyId
     *           The id of the company for the binding.
     * @param mailinglistId
     *          The id of the mailinglist for the binding.
     * @param mediatype
     *           The value of mediatype for the binding.
     */
    void delete(int customerId, int companyId, int mailinglistId, int mediatype);

    /**
     * Load list of Bindings by companyId and recipientID.
     *
     * @param companyId
     *          The id of the company for the bindings.
     * @param recipientID
     *          The id of the recipient for the binding.
     * @return
     */
	List<BindingEntry> getBindings(int companyId, int recipientID);

}