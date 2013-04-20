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

import org.agnitas.beans.Mailloop;
import org.springframework.context.ApplicationContextAware;
import org.displaytag.pagination.PaginatedList;

/**
 *
 * @author Martin Helff
 */
public interface MailloopDao extends ApplicationContextAware {
    /**
     * Deletes mailloop.
     *
     * @param loopID
     *            Id of the mailloop
     * @param companyID
     *              Id of the company
     * @return true==success, false==error
     */
    boolean deleteMailloop(int loopID, int companyID);

    /**
     * Loads mailloop by mailloop id and company id.
     *
     * @param mailloopID
     *              Id of the mailloop
     * @param companyID
     *              Id of the company
     * @return Mailloop bean object or null
     */
    Mailloop getMailloop(int mailloopID, int companyID);

    /**
     * Saves mailloop.
     *
     * @param loop
     *          Mailloop bean object
     * @return Saved mailloop id.
     */
    int saveMailloop(Mailloop loop);
    
    /**
     * Loads list of mailloops by company id.
     *
     * @param companyID
     *               Id of the company
     * @return List of mailloops.
     */
    List getMailloops(int companyID);

    /**
     * Selects all mailloops of certain company and creates paginated list according to given criteria of sorting and pagination
     *
     * @param companyID
     *              The id of the company for admins
     * @param sort
     *              The name of the column for sorting
     * @param direction
     *              The sort order
     * @param page
     *              The number of the page
     * @param rownums
     *              The number of rows to be shown on page
     * @return PaginatedList of Mailloop bean objects
     */
    PaginatedList getMailloopList(int companyID, String sort, String direction, int page, int rownums);
}
