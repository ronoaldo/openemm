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

package org.agnitas.cms.utils.dataaccess;

import org.agnitas.cms.webservices.generated.*;

import java.util.*;

/**
 * Provides core operations with content modules
 *
 * @author Vyacheslav Stepanov
 */
public interface ContentModuleManager {

	/**
	 * @param id id of needed CM
	 * @return CM for the given id
	 */
	ContentModule getContentModule(int id);

	/**
	 * @param companyId company id of needed CMs
	 * @return list of CMs for companyId
	 */
	List<ContentModule> getContentModules(int companyId);

	/**
	 * Removes CM with a given id
	 *
	 * @param id id of CM
	 */
	void deleteContentModule(int id);

	/**
	 * Creates new CM in database
	 *
	 * @param contentModule CM to create
	 * @return id of newly created CM
	 */
	int createContentModule(ContentModule contentModule);

	/**
	 * Updates CM's name and description in DB
	 *
	 * @param id			 id of CM to update
	 * @param newName		new name of CM
	 * @param newDescription new description of CM
	 * @return true if update is successful
	 */
	boolean updateContentModule(int id, String newName, String newDescription, int newCategoryId);

	/**
	 * Method returns CM's placeholders content
	 *
	 * @param contentModuleId id of content module
	 * @return CM's placeholders content in a form of CmsTag list
	 */
	List<CmsTag> getContentModuleContents(int contentModuleId);

	/**
	 * @param contentModuleId id of content module
	 * @param tag			 CmsTag representing CM's placeholder and its content
	 */
	void saveContentModuleContent(int contentModuleId, CmsTag tag);

	/**
	 * Method removes all placeholders contents of content module
	 *
	 * @param contentModuleId id of content module
	 */
	void removeContentsForContentModule(int contentModuleId);

	/**
	 * @param mailingIds	  source list of mailing ids
	 * @param contentModuleId id of content module
	 * @return mailing ids that have given content module assigned
	 */
	List<Integer> getMailingBinding(List<Integer> mailingIds, int contentModuleId);

	/**
	 * @param contentModuleId id of content module
	 * @return mailing ids that have given content module assigned
	 */
	List<Integer> getMailingsByContentModule(int contentModuleId);

	/**
	 * Adds mailing bindings for all mailing ids for the given content module id
	 *
	 * @param contentModuleId id of content module
	 * @param mailingIds	  ids of mailings
	 */
	void addMailingBindings(int contentModuleId, List<Integer> mailingIds);

	/**
	 * removes mailing bindings
	 *
	 * @param contentModuleId id of content module
	 * @param mailingIds	  ids of mailins
	 */
	void removeMailingBindings(int contentModuleId, List<Integer> mailingIds);

	/**
	 * Adds mailings binding to many contents modules
	 *
	 * @param contentModuleIds list of content module`s ids
	 * @param mailingId		mailing id for binding with list of content modules
	 */
	void addMailingBindingToContentModules(List<Integer> contentModuleIds, int mailingId);

	/**
	 * Removes content modulles from mailing binding
	 *
	 * @param contentModuleIds list of content modules
	 * @param mailingId		mailing`s id for binding
	 */
	void removeMailingBindingFromContentModules(List<Integer> contentModuleIds,
												int mailingId);

	/**
	 * Gets assigned content modules`s id for mailind`s id
	 *
	 * @param mailingId id of mailing
	 * @return list of CM ids that assigned for the mailing
	 */
	List<Integer> getAssignedCMsForMailing(int mailingId);

	/**
	 * Gets content module locations for mailing`s id
	 *
	 * @param mailingId mailing`s id
	 * @return list of content module locations for mailing`s id
	 */
	List<ContentModuleLocation> getCMLocationsForMailingId(int mailingId);

	/**
	 * @param mailingId mailing`s id
	 * @return list of content modules for mailing`s id
	 */
	List<ContentModule> getContentModulesForMailing(int mailingId);

	/**
	 * Adds content modules location to db
	 *
	 * @param locations list of content module location
	 */
	void addCMLocations(List<ContentModuleLocation> locations);

	/**
	 * Removes all content modules locations from mailing
	 *
	 * @param mailingId id of mailing
	 */
	void removeCMLocationsForMailing(int mailingId);

	/**
	 * Removes specific conent module location from set of mailings
	 *
	 * @param contentModuleId id of specific content module
	 * @param mailingIds	  list of mailing`s ids
	 */
	void removeCMLocationForMailingsByContentModule(int contentModuleId,
													List<Integer> mailingIds);

	/**
	 * Saves content module`s tag`s content in specific content module
	 *
	 * @param contentModuleId id of specific content module
	 * @param tagList		 list of cms tags
	 */
	void saveContentModuleContentList(int contentModuleId, List<CmsTag> tagList);

    int createContentModuleCategory(ContentModuleCategory category);

    void updateContentModuleCategory(ContentModuleCategory category);

    public ContentModuleCategory getContentModuleCategory(int id);

    void deleteContentModuleCategory(int categoryId);

    List<ContentModuleCategory> getAllCMCategories(int companyId);

    List<ContentModule> getContentModulesForCategory(int companyId, int categoryId);
}