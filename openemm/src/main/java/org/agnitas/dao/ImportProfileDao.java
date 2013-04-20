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

package org.agnitas.dao;

import java.util.List;

import org.agnitas.beans.ImportProfile;

/**
 * @author Andreas Soderer
 * @date 03.05.2012
 */
public interface ImportProfileDao {

    /**
     * Inserts new import profile to database.
     * Also creates corresponding column and gender mappings.
     *
     * @param item
     *          ImportProfile entry to insert.
     * @return ID of inserted import profile.
     */
	public int insertImportProfile(ImportProfile item);

    /**
     * Updates import profile and its column and gender mappings.
     *
     * @param item
     *          ImportProfile entry to update.
     */
	public void updateImportProfile(ImportProfile item);

    /**
     * Loads an import profile identified by ID.
     *
     * @param id
     *          The ID of import profile.
     * @return The ImportProfile or null on failure.
     */
	public ImportProfile getImportProfileById(int id);

    /**
     * Loads an import profile identified by shortname.
     *
     * @param shortname
     *          The shortname of import profile.
     * @return The ImportProfile or null on failure.
     */
	public ImportProfile getImportProfileByShortname(String shortname);

    /**
     * Loads list of import profiles identified by company id.
     *
     * @param companyId
     *          The company id for import profiles.
     * @return The list of ImportProfiles or empty list.
     */
	public List<ImportProfile> getImportProfilesByCompanyId(int companyId);

    /**
     * Deletes import profile with column and gender mappings.
     *
     * @param item
     *          The import profile to be deleted.
     */
	public void deleteImportProfile(ImportProfile item);

    /**
     * Deletes import profile by ID with column and gender mappings.
     *
     * @param id
     *          The ID of import profile to be deleted.
     */
	public void deleteImportProfileById(int id);


}
