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

package org.agnitas.cms.webservices;

import java.rmi.*;
import java.util.*;
import org.agnitas.cms.dao.*;
import org.agnitas.cms.webservices.generated.*;
import org.agnitas.util.*;
import org.springframework.remoting.jaxrpc.*;

/**
 * Gives service functionality to ContentModuleManager
 * from central content repository. Delegate operations
 * to ContentModuleDao.
 *
 * @author Igor Nesterenko
 * @see org.agnitas.cms.utils.dataaccess.ContentModuleManager
 * @see org.agnitas.cms.utils.dataaccess.RemoteContentModuleManager
 * @see org.agnitas.cms.dao.ContentModuleDao
 */
public class ContentModuleService extends ServletEndpointSupport
		implements RemoteContentModuleManager_PortType {

	private ContentModuleDao dao;

	private ContentModuleDao getDao() {
		if(dao == null) {
			dao = ((ContentModuleDao) getWebApplicationContext()
					.getBean("ContentModuleDao"));
		}
		return dao;
	}

	public ContentModule getContentModule(int id) throws RemoteException {
		return getDao().getContentModule(id);
	}

	public Object[] getContentModules(int companyId) throws RemoteException {
		final List<ContentModule> moduleList = getDao()
				.getContentModules(companyId);
		return moduleList.toArray();
	}

	public void deleteContentModule(int id) throws RemoteException {
		getDao().deleteContentModule(id);
	}

	public int createContentModule(ContentModule contentModule) throws
			RemoteException {
		return getDao().createContentModule(contentModule);
	}

	public boolean updateContentModule(int id, String newName,
									   String newDescription, int categoryId) throws
			RemoteException {
		return getDao().updateContentModule(id, newName, newDescription, categoryId);
	}

	public Object[] getContentModuleContents(int contentModuleId) throws
			RemoteException {
		return getDao().getContentModuleContents(contentModuleId).toArray();
	}

	public void saveContentModuleContentList(int contentModuleId,
											 Object[] tagList) throws
			RemoteException {
		final ArrayList<CmsTag> cmsTagList = new ArrayList<CmsTag>();
		for(Object object : tagList) {
			cmsTagList.add(((CmsTag) object));
		}
		getDao().saveContentModuleContentList(contentModuleId, cmsTagList);
	}

	public void saveContentModuleContent(int contentModuleId, CmsTag tag) throws
			RemoteException {
		getDao().saveContentModuleContent(contentModuleId, tag);
	}

	public void removeContentsForContentModule(int contentModuleId) throws
			RemoteException {
		getDao().removeContentsForContentModule(contentModuleId);
	}

	public Object[] getMailingBinding(Object[] mailingIds,
									  int contentModuleId) throws
			RemoteException {
		final ArrayList<Integer> mailingIdList = toIntegerList(mailingIds);
		return getDao().getMailingBinding(mailingIdList, contentModuleId)
				.toArray();
	}

	public Object[] getMailingsByContentModule(int contentModuleId) throws
			RemoteException {
		return getDao().getMailingsByContentModule(contentModuleId).toArray();
	}

	public void addMailingBindings(int contentModuleId,
								   Object[] mailingIds) throws RemoteException {
		getDao().addMailingBindings(contentModuleId, toIntegerList(mailingIds));
	}

	public void removeMailingBindings(int contentModuleId,
									  Object[] mailingIds) throws
			RemoteException {
		getDao().removeMailingBindings(contentModuleId,
				toIntegerList(mailingIds));
	}

	public Object[] getAssignedCMsForMailing(int mailingId) throws
			RemoteException {
		return getDao().getAssignedCMsForMailing(mailingId).toArray();
	}

	public Object[] getCMLocationsForMailingId(int mailingId) throws
			RemoteException {
		return getDao().getCMLocationsForMailingId(mailingId).toArray();
	}

	public Object[] getContentModulesForMailing(int mailingId) throws
			RemoteException {
		return getDao().getContentModulesForMailing(mailingId).toArray();
	}

	public void removeCMLocationsForMailing(int mailingId) throws
			RemoteException {
		getDao().removeCMLocationsForMailing(mailingId);
	}

	public void addCMLocations(Object[] locations) throws RemoteException {
		getDao().addCMLocations(toContentModuleLocationList(locations));
	}

	public void updateCMLocation(ContentModuleLocation location) throws
			RemoteException {
		AgnUtils.logger().error("Error unsupported operation");
	}

	private static ArrayList<Integer> toIntegerList(Object[] objects) {
		final ArrayList<Integer> integers = new ArrayList<Integer>();
		for(Object integerObject : objects) {
			integers.add(((Integer) integerObject));
		}
		return integers;
	}

	private static ArrayList<ContentModuleLocation> toContentModuleLocationList(
			Object[] objects) {
		final ArrayList<ContentModuleLocation> list = new ArrayList<ContentModuleLocation>();
		for(Object object : objects) {
			list.add(((ContentModuleLocation) object));
		}
		return list;
	}

	public void addMailingBindingToContentModules(Object[] contentModuleIds,
												  int mailingId) throws
			RemoteException {
		getDao().addMailingBindings(toIntegerList(contentModuleIds), mailingId);
	}

	public void removeMailingBindingFromContentModules(
			Object[] contentModuleIds, int mailingId) throws RemoteException {
		getDao().removeMailingBindings(toIntegerList(contentModuleIds),
				mailingId);
	}

	public void removeCMLocationForMailingsByContentModule(int contentModuleId,
														   Object[] mailingsToDeassign) throws
			RemoteException {
		getDao().removeCMLocationsForMailingsByContentModule(contentModuleId,
				toIntegerList(mailingsToDeassign));
	}

	public int createContentModuleCategory(ContentModuleCategory category) throws RemoteException {
		return getDao().createContentModuleCategory(category);
	}

	public void updateContentModuleCategory(ContentModuleCategory category) throws RemoteException {
		getDao().updateContentModuleCategory(category.getId(), category.getName(), category.getDescription());
	}

	public ContentModuleCategory getContentModuleCategory(int id) throws RemoteException {
		return getDao().getContentModuleCategory(id);
	}

	public void deleteContentModuleCategory(int categoryId) throws RemoteException {
		getDao().deleteContentModuleCategory(categoryId);
	}

	public Object[] getAllCMCategories(int companyId) throws RemoteException {
		return getDao().getAllCMCategories(companyId).toArray();
	}

	public Object[] getContentModulesForCategory(int companyId, int categoryId) throws RemoteException {
		return getDao().getContentModulesForCategory(companyId, categoryId).toArray();
	}
}
