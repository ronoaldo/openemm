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

import org.agnitas.beans.ProfileField;
import org.agnitas.util.CaseInsensitiveMap;

/**
 *
 * @author mhe
 */
public interface ProfileFieldDao {
    /**
     * Loads profile field by company id and column name.
     *
     * @param companyID
     *          The companyID for the profile field.
     * @param column
     *          The column name for profile field.
     * @return The ProfileField or null on failure or if companyID is 0.
     * @throws Exception 
     */
	public ProfileField getProfileField(int companyID, String column) throws Exception;

    /**
     * Loads all profile fields of certain company.
     *
     * @param companyID
     *          The companyID for the profile fields.
     * @return List of ProfileFields or empty list.
     */
	public List<ProfileField> getProfileFields(int companyID) throws Exception;
	
    /**
     * Loads all profile fields of certain company.
     *
     * @param companyID
     *          The companyID for the profile fields.
     * @return List of ProfileFields or empty list.
     */
	public List<ProfileField> getProfileFields(int companyID, int adminID) throws Exception;
	
    /**
     * Loads all profile fields of certain company.
     *
     * @param companyID
     *          The companyID for the profile fields.
     * @return List of ProfileFields or empty list.
     */
	public CaseInsensitiveMap<ProfileField> getProfileFieldsMap(int companyID) throws Exception;
	
    /**
     * Loads all profile fields of certain company.
     *
     * @param companyID
     *          The companyID for the profile fields.
     * @return List of ProfileFields or empty list.
     */
	public CaseInsensitiveMap<ProfileField> getProfileFieldsMap(int companyID, int adminID) throws Exception;

    /**
     * Saves or updates the profile field.
     *
     * @param field
     *          The profile field to save.
     * @throws Exception 
     */
	public boolean saveProfileField(ProfileField field) throws Exception;

    /**
     * Loads profile field by company id and short name.
     *
     * @param companyID
     *          The companyID for the profile field.
     * @param shortName
     *          The shortname for the profile field.
     * @return The ProfileField or null on failure or if companyID is 0.
     */
    public ProfileField getProfileFieldByShortname(int companyID, String shortName) throws Exception;

    /**
     * Creates a new custom column in customer_tbl for given company_id.
     *
     * @param companyID
     *          The company id for new column.
     * @param fieldname
     *          Column name in database.
     * @param fieldType
     *          Column type in database.
     * @param length
     *          Column size. 0 for numeric fields means default length of 32 bit.
     * @param fieldDefault
     *          Default column value.
     * @param notNull
     *          Column NOT NULL constraint.
     * @return true on success.
     * @throws Exception
     */
	public boolean addColumnToDbTable(int companyID, String fieldname, String fieldType, int length, String fieldDefault, boolean notNull) throws Exception;
	
	/**
     * Changes a custom column in customer_tbl for given company_id to a new type and/or default value.
     */
	public boolean alterColumnTypeInDbTable(int companyID, String fieldname, String fieldType, int length, String fieldDefault, boolean notNull) throws Exception;

    /**
     * Removes custom column in customer_tbl for given company_id.
     *
     * @param companyID
     *          Table of customers for this company will be altered.
     * @param fieldname
     *          Database column name to remove.
     * @throws Exception 
     */
	public void removeProfileField(int companyID, String fieldname) throws Exception;
}
