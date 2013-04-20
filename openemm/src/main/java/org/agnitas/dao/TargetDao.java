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

import org.agnitas.dao.exception.target.TargetGroupPersistenceException;
import org.agnitas.target.Target;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author mhe
 */
public interface TargetDao {

    /**
     * Marks target group as "deleted" in the database.
     *
     * @param targetID
     *          The id of the target group to mark.
     * @param companyID
     *          The id of company.
     * @return  true on success.
     * @throws TargetGroupPersistenceException
     */
    boolean deleteTarget(int targetID, int companyID) throws TargetGroupPersistenceException;

    /**
     *  Loads target group identified by target id and company id.
     *
     * @param targetID
     *           The id of the target group that should be loaded.
     * @param companyID
     *          The companyID for the target group.
     * @return The Target or null on failure.
     */
    Target getTarget(int targetID, int companyID);
    
    /**
     *  Loads target group identified by target name and company id.
     *
     * @param targetName
     *           The name of the target group that should be loaded.
     * @param companyID
     *          The companyID for the target group.
     * @return The Target or null on failure.
     */
    Target getTargetByName(String targetName, int companyID);


    /**
     * Loads all target groups for company id. Target groups marked as "deleted" are ignored.
     * Uses HibernateTemplate.
     * @param companyID
     *          The companyID for the target groups.
     * @return List of Targets or empty list.
     */
    List<Target> getTargets(int companyID);

    /**
     * Loads all target groups for company id.
     * Uses HibernateTemplate.
     *
     * @param companyID
     *          The companyID for the target groups.
     * @param includeDeleted
     *          If true - target groups marked as "deleted" will be loaded as well.
     *
     * @return List of Targets or empty list.
     */
    List<Target> getTargets(int companyID, boolean includeDeleted);

    /**
     * Loads all target groups marked as "deleted" for company id.
     * Uses JdbcTemplate.
     *
     * @param companyID
     *          The companyID for the target groups.
     *
     * @return List of Targets or empty list.
     */
    List<Integer> getDeletedTargets(int companyID);

    /**
     * Saves or updates target group in database.
     *
     * @param target
     *          The target group to save.
     * @return Saved target group id.
     * @throws TargetGroupPersistenceException
     */
    int saveTarget(Target target) throws TargetGroupPersistenceException;

    /**
     * Loads all target groups allowed for given company.
     * Uses JdbcTemplate.
     *
     * @param companyID
     *      The companyID for the target groups.
     * @return List of Targets or empty list.
     */
	public Map<Integer, Target>	getAllowedTargets(int companyID);

	/**
	 * Load list of Target groups names by IDs.
     * Uses JdbcTemplate.
     *
	 * @param companyID company ID
	 * @param targetIds the IDs of target groups
	 * @return the list of names
	 */
	List<String> getTargetNamesByIds(int companyID, Set<Integer> targetIds);

	/**
	 * Load list of Target groups by IDs.
     * Uses HibernateTemplate.
     *
	 * @param companyID
     *          The company ID for target groups.
	 * @param targetIds
     *          The IDs of target groups to load.
	 * @return List of Targets or empty list.
	 */
    public List<Target> getTargetGroup(int companyID, Collection<Integer> targetIds);

	/**
	 * Load all target groups except listed in targetIds.
     * Uses HibernateTemplate.
     *
	 * @param companyID
     *          The company ID for target groups.
	 * @param targetIds
     *          The IDs of target groups to ignore.
	 * @return List of Targets or empty list.
	 */
    public List<Target> getUnchoosenTargets(int companyID, Collection<Integer> targetIds);
}
