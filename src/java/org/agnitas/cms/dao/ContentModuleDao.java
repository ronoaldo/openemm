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

package org.agnitas.cms.dao;

import org.agnitas.cms.webservices.generated.CmsTag;
import org.agnitas.cms.webservices.generated.ContentModule;
import org.agnitas.cms.webservices.generated.ContentModuleLocation;
import org.agnitas.cms.webservices.generated.ContentModuleCategory;
import org.springframework.context.ApplicationContextAware;

import java.util.List;


/**
 * @author Vyacheslav Stepanov
 */
public interface ContentModuleDao extends ApplicationContextAware {

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
	boolean updateContentModule(int id, String newName, String newDescription, int categoryId);

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
	List<Integer> getMailingBinding(List<Integer> mailingIds,
									int contentModuleId);

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

	void addMailingBindings(List<Integer> contentModuleIds, int mailingId);

	void removeMailingBindings(List<Integer> contentModuleIds, int mailingId);

	/**
	 * @param mailingId id of mailing
	 * @return list of CM ids that assigned for the mailing
	 */
	List<Integer> getAssignedCMsForMailing(int mailingId);

	List<ContentModuleLocation> getCMLocationsForMailingId(int mailingId);

	List<ContentModule> getContentModulesForMailing(int mailingId);

	void removeCMLocationsForMailing(int mailingId);

	void addCMLocations(List<ContentModuleLocation> locations);

	void saveContentModuleContentList(int contentModuleId,
									  List<CmsTag> tagList);

	void removeCMLocationsForMailingsByContentModule(int contentModuleId,
													 List<Integer> mailingIds);
    List<ContentModuleCategory> getAllCMCategories(int companyId);

	public int createContentModuleCategory(ContentModuleCategory category);

    public void deleteContentModuleCategory(int categoryId);

    public ContentModuleCategory getContentModuleCategory(int id);

    public boolean updateContentModuleCategory(int id, String newName, String newDescription);

    public List<ContentModule> getContentModulesForCategory(int companyId, int categoryId);
}