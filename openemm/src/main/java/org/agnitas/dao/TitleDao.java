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

import org.agnitas.beans.Title;
import org.springframework.context.ApplicationContextAware;
import org.displaytag.pagination.PaginatedList;

import java.util.List;

/**
 *
 * @author mhe
 */
public interface TitleDao extends ApplicationContextAware {
	/**
	 * Load a title from the database.
	 *
	 * @param titleID the unique id of the title to load.
	 * @param companyID the id of the company for the given title.
	 * @return the loaded title.
	 */
	Title	getTitle(int titleID, int companyID);

	/**
	 * Delete a title in the database.
	 *
	 * @param titleID the unique id of the title to delete.
	 * @param companyID the id of the company for the given title.
	 * @return true on success.
	 */
	boolean	delete(int titleID, int companyID);

    /**
     * Loads all titles of certain company and creates paginated list according to given criteria for sorting and pagination
     *
     * @param companyID
     *                The id of the company for titles
     * @param sort
     *                The name of the column to be sorted
     * @param direction
     *                The sort direction , 0 (for ascending) or 1 (for descending)
     * @param page
     *                The number of the page
     * @param rownums
     *                The number of rows to be shown on page
     * @return  PaginatedList of Title or empty list.
     */
    PaginatedList getSalutationList(int companyID, String sort, String direction, int page, int rownums);

    /**
     * Load all titles for company id.
     *
     * @param companyID
     *          The id of the company for titles.
     * @return List of Titles or empty list.
     */
    public List<Title> getTitles(int companyID);
}
