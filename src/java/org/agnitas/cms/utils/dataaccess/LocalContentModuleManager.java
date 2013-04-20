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

import java.util.*;
import org.agnitas.cms.dao.*;
import org.agnitas.cms.dao.impl.*;
import org.agnitas.cms.webservices.generated.*;

/**
 * @author Vyacheslav Stepanov
 */
public class LocalContentModuleManager implements ContentModuleManager {

	ContentModuleDao contentModuleDao;

	public LocalContentModuleManager(ContentModuleDaoImpl contentModuleDao) {
		this.contentModuleDao = contentModuleDao;
	}

	public ContentModule getContentModule(int id) {
		return contentModuleDao.getContentModule(id);
	}

	public List<ContentModule> getContentModules(int companyId) {
		return contentModuleDao.getContentModules(companyId);
	}

	public void deleteContentModule(int id) {
		contentModuleDao.deleteContentModule(id);
	}

	public int createContentModule(ContentModule contentModule) {
		return contentModuleDao.createContentModule(contentModule);
	}

	public boolean updateContentModule(int id, String newName, String newDescription, int newCategoryId) {
		return contentModuleDao.updateContentModule(id, newName, newDescription, newCategoryId);
	}

	public List<CmsTag> getContentModuleContents(int contentModuleId) {
		return contentModuleDao.getContentModuleContents(contentModuleId);
	}

	public void saveContentModuleContent(int contentModuleId, CmsTag tag) {
		contentModuleDao.saveContentModuleContent(contentModuleId, tag);
	}

	public void removeContentsForContentModule(int contentModuleId) {
		contentModuleDao.removeContentsForContentModule(contentModuleId);
	}

	public List<Integer> getMailingBinding(List<Integer> mailingIds,
										   int contentModuleId) {
		return contentModuleDao.getMailingBinding(mailingIds, contentModuleId);
	}

	public List<Integer> getMailingsByContentModule(int contentModuleId) {
		return contentModuleDao.getMailingsByContentModule(contentModuleId);
	}

	public void addMailingBindings(int contentModuleId, List<Integer> mailingIds) {
		contentModuleDao.addMailingBindings(contentModuleId, mailingIds);
	}

	public void removeMailingBindings(int contentModuleId, List<Integer> mailingIds) {
		contentModuleDao.removeMailingBindings(contentModuleId, mailingIds);
	}

	public void addMailingBindingToContentModules(List<Integer> contentModuleIds,
												  int mailingId) {
		contentModuleDao.addMailingBindings(contentModuleIds, mailingId);
	}

	public void removeMailingBindingFromContentModules(List<Integer> contentModuleIds,
													   int mailingId) {
		contentModuleDao.removeMailingBindings(contentModuleIds, mailingId);
	}

	public List<Integer> getAssignedCMsForMailing(int mailingId) {
		return contentModuleDao.getAssignedCMsForMailing(mailingId);
	}

	public List<ContentModuleLocation> getCMLocationsForMailingId(int mailingId) {
		return contentModuleDao.getCMLocationsForMailingId(mailingId);
	}

	public List<ContentModule> getContentModulesForMailing(int mailingId) {
		return contentModuleDao.getContentModulesForMailing(mailingId);
	}

	public void removeCMLocationsForMailing(int mailingId) {
		contentModuleDao.removeCMLocationsForMailing(mailingId);
	}

	public void removeCMLocationForMailingsByContentModule(int contentModuleId,
														   List<Integer> mailingsToDeassign) {
		contentModuleDao.removeCMLocationsForMailingsByContentModule(contentModuleId,
				mailingsToDeassign);
	}

	public void addCMLocations(List<ContentModuleLocation> locations) {
		contentModuleDao.addCMLocations(locations);
	}

	public void saveContentModuleContentList(int contentModuleId, List<CmsTag> tagList) {
		contentModuleDao.saveContentModuleContentList(contentModuleId, tagList);
	}

	public int createContentModuleCategory(ContentModuleCategory category) {
		return contentModuleDao.createContentModuleCategory(category);
	}

	public void updateContentModuleCategory(ContentModuleCategory category) {
		contentModuleDao.updateContentModuleCategory(category.getId(), category.getName(), category.getDescription());
	}

	public ContentModuleCategory getContentModuleCategory(int id) {
		return contentModuleDao.getContentModuleCategory(id);
	}

	public void deleteContentModuleCategory(int categoryId) {
		contentModuleDao.deleteContentModuleCategory(categoryId);
	}

	public List<ContentModuleCategory> getAllCMCategories(int companyId) {
		return contentModuleDao.getAllCMCategories(companyId);
	}

	public List<ContentModule> getContentModulesForCategory(int companyId, int categoryId) {
		return contentModuleDao.getContentModulesForCategory(companyId, categoryId);
	}


}