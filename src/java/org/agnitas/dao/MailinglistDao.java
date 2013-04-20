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

import org.agnitas.beans.Mailinglist;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author mhe
 */
public interface MailinglistDao extends ApplicationContextAware {

    /**
     * Deletes mailinglist from database.
     *
     * @param listID
     *          The id of the mailing list to delete.
     * @param companyID
     *          The id of mailing list company.
     * @return  true on success.
     */

    boolean deleteMailinglist(int listID, int companyID);

    /**
     * Getter for property mailinglist by list id and company id.
     *
     * @return Value of mailinglist.
     */

    /**
     *  Loads mailing list identified by list id and company id.
     *
     * @param listID
     *           The id of the mailing list that should be loaded.
     * @param companyID
     *          The companyID for the mailing list.
     * @return The Mailinglist or null on failure or if companyID is 0.
     */

    Mailinglist getMailinglist(int listID, int companyID);

    /**
     * Loads all mailing lists for company id.
     *
     * @param companyID
     *          The companyID for the mailing lists.
     * @return List of Mailinglists or empty list.
     */
    List<Mailinglist> getMailinglists(int companyID);

    /**
     * Saves or updates mailinglist.
     *
     * @param list
     *          The mailing list to save.
     * @return Saved mailinglist id.
     */
    int saveMailinglist(Mailinglist list);

    /**
     * Deletes all bindings for mailing list.
     *
     * @param id
     *          The id of mailing list.
     * @param companyID
     *          The company id for bindings.
     * @return true.
     */
    boolean deleteBindings(int id, int companyID);

    /**
     * Get numbers of recipients related to given mailing list.
     *
     * @param admin
     *          Include admin recipients.
     * @param test
     *          Include test recipients.
     * @param world
     *          Include normal recipients.
     * @param targetID
     *          Id of target group.
     * @param companyID
     *          The company id for recipients.
     * @param id
     * @return number of active recipients for mailing list.
     */
    int getNumberOfActiveSubscribers(boolean admin, boolean test, boolean world, int targetID, int companyID, int id);

    /**
     * Checks if mailing list with given name exists.
     *
     * @param mailinglistName
     *          The name of mailing list for check.
     * @param companyID
     *          The company id for mailing list.
     * @return true if the mailing list exists, and false otherwise
     */
    boolean mailinglistExists(String mailinglistName, int companyID);

    /**
     * Checks if mailing list with given id exists.
     *
     * @param mailinglistID
     *          The mailing list id for check.
     * @param companyID
     *          The company id for mailing list.
     * @return true if the mailing list exists, and false otherwise
     */
    boolean	exist(int mailinglistID, int companyID);
    
}
