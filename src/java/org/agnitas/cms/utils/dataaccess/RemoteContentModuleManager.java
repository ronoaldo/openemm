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

import javax.xml.rpc.*;
import java.net.*;
import java.rmi.*;
import java.util.*;
import org.agnitas.cms.webservices.generated.*;
import org.agnitas.util.*;

/**
 * Provide remote functionality to ContentModuleManager.
 * Invokes service from central content repository.
 *
 * @author Igor Nesterenko
 * @see org.agnitas.cms.webservices.ContentModuleService
 */
public class RemoteContentModuleManager implements ContentModuleManager {

	private RemoteContentModuleManager_PortType contentModuleService;

	public void setPortUrl(String portUrlString) {
		final RemoteContentModuleManagerServiceLocator serviceLocator = new RemoteContentModuleManagerServiceLocator();
		try {
			contentModuleService = serviceLocator
					.getRemoteContentModuleManager(new URL(portUrlString));
		} catch(ServiceException e) {
			AgnUtils.logger().error("Error while creation remote connection " + e + "\n" +
					AgnUtils.getStackTrace(e));
		} catch(MalformedURLException e) {
			AgnUtils.logger().error("Error while parsing port address " + e + "\n" +
					AgnUtils.getStackTrace(e));
		}
	}

	public ContentModule getContentModule(int id) {
		try {
			return contentModuleService.getContentModule(id);
		} catch(RemoteException e) {
			AgnUtils.logger()
					.error("Error while get " + ContentModule.class.getSimpleName() +
							" " + e + "\n" + AgnUtils.getStackTrace(e));
		}
		return null;
	}

	public List<Integer> getMailingsByContentModule(int contentModuleId) {
		try {
			final Object[] contentModules = contentModuleService
					.getMailingsByContentModule(contentModuleId);
			final ArrayList<Integer> moduleList = new ArrayList<Integer>();
			for(Object object : contentModules) {
				moduleList.add(((Integer) object));
			}
			return moduleList;
		} catch(RemoteException e) {
			AgnUtils.logger()
					.error("Error while get content module`s mailing binding " + e +
							"\n" + AgnUtils.getStackTrace(e));
		}
		return null;
	}

	public List<ContentModule> getContentModules(int companyId) {
		final ArrayList<ContentModule> moduleList = new ArrayList<ContentModule>();
		try {
			final Object[] contentModules = contentModuleService
					.getContentModules(companyId);
			for(Object contentModule : contentModules) {
				moduleList.add(((ContentModule) contentModule));
			}
		} catch(RemoteException e) {
			AgnUtils.logger().error("Error while get list of " +
					ContentModule.class.getSimpleName() + " " + e + "\n" +
					AgnUtils.getStackTrace(e));
		}
		return moduleList;
	}

	public void deleteContentModule(int id) {
		try {
			contentModuleService.deleteContentModule(id);
		} catch(RemoteException e) {
			AgnUtils.logger()
					.error("Error while delete  " + ContentModule.class.getSimpleName() +
							" " + e + "\n" + AgnUtils.getStackTrace(e));
		}
	}

	public int createContentModule(ContentModule contentModule) {
		try {
			return contentModuleService.createContentModule(contentModule);
		} catch(RemoteException e) {
			AgnUtils.logger()
					.error("Error while create " + ContentModule.class.getSimpleName() +
							" " + e + "\n" + AgnUtils.getStackTrace(e));
		}
		return 0;
	}

	public boolean updateContentModule(int id, String newName, String newDescription, int newCategoryId) {
		try {
			return contentModuleService.updateContentModule(id, newName, newDescription, newCategoryId);
		} catch(RemoteException e) {
			AgnUtils.logger()
					.error("Error while update " + ContentModule.class.getSimpleName() +
							"`s content " + e + "\n" + AgnUtils.getStackTrace(e));
		}
		return false;
	}

	public List<CmsTag> getContentModuleContents(int contentModuleId) {
		final ArrayList<CmsTag> cmsTagList = new ArrayList<CmsTag>();
		try {
			final Object[] tags = contentModuleService
					.getContentModuleContents(contentModuleId);
			for(Object tag : tags) {
				cmsTagList.add(((CmsTag) tag));
			}
		} catch(RemoteException e) {
			AgnUtils.logger()
					.error("Error while get " + ContentModule.class.getSimpleName() +
							"`s contents " + e + "\n" + AgnUtils.getStackTrace(e));
		}
		return cmsTagList;
	}

	public void saveContentModuleContent(int contentModuleId, CmsTag tag) {
		try {
			contentModuleService.saveContentModuleContent(contentModuleId, tag);
		} catch(RemoteException e) {
			AgnUtils.logger()
					.error("Error while save " + ContentModule.class.getSimpleName() +
							"`s content " + e + "\n" + AgnUtils.getStackTrace(e));
		}
	}

	public void removeContentsForContentModule(int contentModuleId) {
		try {
			contentModuleService.removeContentsForContentModule(contentModuleId);
		} catch(RemoteException e) {
			AgnUtils.logger()
					.error("Error while remove " + ContentModule.class.getSimpleName() +
							"`s content " + e + "\n" + AgnUtils.getStackTrace(e));
		}
	}

	public List<Integer> getMailingBinding(List<Integer> mailingIds,
										   int contentModuleId) {
		final ArrayList<Integer> mailingIdList = new ArrayList<Integer>();
		try {
			final Object[] mailingBindings = contentModuleService
					.getMailingBinding(mailingIds.toArray(), contentModuleId);
			for(Object mailingBinding : mailingBindings) {
				mailingIdList.add(((Integer) mailingBinding));
			}
		} catch(RemoteException e) {
			AgnUtils.logger().error("Error while get mailing by " +
					ContentModule.class.getSimpleName() + "`s id " + e + "\n" +
					AgnUtils.getStackTrace(e));
		}
		return mailingIdList;
	}

	public void addMailingBindings(int contentModuleId, List<Integer> mailingIds) {
		try {
			contentModuleService
					.addMailingBindings(contentModuleId, mailingIds.toArray());
		} catch(RemoteException e) {
			AgnUtils.logger().error("Error while add mailing binding for" +
					ContentModule.class.getSimpleName() + " " + e + "\n" +
					AgnUtils.getStackTrace(e));
		}
	}

	public void removeMailingBindings(int contentModuleId, List<Integer> mailingIds) {
		try {
			contentModuleService
					.removeMailingBindings(contentModuleId, mailingIds.toArray());
		} catch(RemoteException e) {
			AgnUtils.logger().error("Error while remove mailing binding from " +
					ContentModule.class.getSimpleName() + " " + e + "\n" +
					AgnUtils.getStackTrace(e));
		}
	}

	public void addMailingBindingToContentModules(List<Integer> contentModuleIds,
												  int mailingId) {
		try {
			contentModuleService
					.addMailingBindingToContentModules(contentModuleIds.toArray(),
							mailingId);
		} catch(RemoteException e) {
			AgnUtils.logger()
					.error("Error while adding mailing binding to content modules " + e +
							"\n" + AgnUtils.getStackTrace(e));
		}
	}

	public void removeMailingBindingFromContentModules(List<Integer> contentModuleIds,
													   int mailingId) {
		try {
			contentModuleService
					.removeMailingBindingFromContentModules(contentModuleIds.toArray(),
							mailingId);
		} catch(RemoteException e) {
			AgnUtils.logger()
					.error("Error while removing mailing binding from content modules " +
							e + "\n" + AgnUtils.getStackTrace(e));
		}
	}


	public List<Integer> getAssignedCMsForMailing(int mailingId) {
		final ArrayList<Integer> moduleIdList = new ArrayList<Integer>();
		try {
			final Object[] moduleIds = contentModuleService
					.getAssignedCMsForMailing(mailingId);
			for(Object moduleId : moduleIds) {
				moduleIdList.add(((Integer) moduleId));
			}
		} catch(RemoteException e) {
			AgnUtils.logger().error("Error while get mailing for " +
					ContentModule.class.getSimpleName() + " " + e + "\n" +
					AgnUtils.getStackTrace(e));
		}
		return moduleIdList;
	}

	public List<ContentModuleLocation> getCMLocationsForMailingId(int mailingId) {
		final ArrayList<ContentModuleLocation> moduleLocationList = new ArrayList<ContentModuleLocation>();
		try {
			final Object[] moduleLocations = contentModuleService
					.getCMLocationsForMailingId(mailingId);
			for(Object moduleLocation : moduleLocations) {
				moduleLocationList.add(((ContentModuleLocation) moduleLocation));
			}
		} catch(RemoteException e) {
			AgnUtils.logger().error("Error while get " +
					ContentModuleLocation.class.getSimpleName() + " " + e + "\n" +
					AgnUtils.getStackTrace(e));
		}
		return moduleLocationList;
	}

	public List<ContentModule> getContentModulesForMailing(int mailingId) {
		final ArrayList<ContentModule> moduleList = new ArrayList<ContentModule>();
		try {
			final Object[] modules = contentModuleService
					.getContentModulesForMailing(mailingId);
			for(Object module : modules) {
				moduleList.add(((ContentModule) module));
			}
		} catch(RemoteException e) {
			AgnUtils.logger()
					.error("Error while get " + ContentModule.class.getSimpleName() +
							" by mailing " + e + "\n" + AgnUtils.getStackTrace(e));
		}
		return moduleList;
	}

	public void removeCMLocationsForMailing(int mailingId) {
		try {
			contentModuleService.removeCMLocationsForMailing(mailingId);
		} catch(RemoteException e) {
			AgnUtils.logger().error("Error while remove mailing binding " + e + "\n" +
					AgnUtils.getStackTrace(e));
		}
	}

	public void addCMLocations(List<ContentModuleLocation> locations) {
		try {
			contentModuleService.addCMLocations(locations.toArray());
		} catch(RemoteException e) {
			AgnUtils.logger().error("Error while add CM location " + e + "\n" +
					AgnUtils.getStackTrace(e));
		}
	}

	public void saveContentModuleContentList(int contentModuleId, List<CmsTag> tagList) {
		try {
			contentModuleService
					.saveContentModuleContentList(contentModuleId, tagList.toArray());
		} catch(RemoteException e) {
			AgnUtils.logger().error("Error while get module content " + e + "\n" +
					AgnUtils.getStackTrace(e));
		}
	}

	public int createContentModuleCategory(ContentModuleCategory category) {
		try {
			return contentModuleService.createContentModuleCategory(category);
		} catch(RemoteException e) {
			AgnUtils.logger().error("Error while creating module category " + e + "\n" +
					AgnUtils.getStackTrace(e));
		}
		return 0;
	}

	public void updateContentModuleCategory(ContentModuleCategory category) {
		try {
			contentModuleService.updateContentModuleCategory(category);
		} catch(RemoteException e) {
			AgnUtils.logger().error("Error while updating module category " + e + "\n" + AgnUtils.getStackTrace(e));
		}
	}

	public ContentModuleCategory getContentModuleCategory(int id) {
		try {
			return contentModuleService.getContentModuleCategory(id);
		} catch(RemoteException e) {
			AgnUtils.logger().error("Error while getting module category " + e + "\n" + AgnUtils.getStackTrace(e));
		}
		return null;
	}

	public void deleteContentModuleCategory(int categoryId) {
		try {
			contentModuleService.deleteContentModuleCategory(categoryId);
		} catch(RemoteException e) {
			AgnUtils.logger().error("Error while deleting module category " + e + "\n" + AgnUtils.getStackTrace(e));
		}
	}

	public List<ContentModuleCategory> getAllCMCategories(int companyId) {
		try {
			Object[] categories = contentModuleService.getAllCMCategories(companyId);
			ArrayList<ContentModuleCategory> resultList = new ArrayList<ContentModuleCategory>();
			for(Object category : categories) {
				resultList.add(((ContentModuleCategory) category));
			}
			return resultList;
		} catch(RemoteException e) {
			AgnUtils.logger().error("Error while getting module categories " + e + "\n" + AgnUtils.getStackTrace(e));
		}
		return null;
	}

	public List<ContentModule> getContentModulesForCategory(int companyId, int categoryId) {
		try {
			Object[] modules = contentModuleService.getContentModulesForCategory(companyId, categoryId);
			ArrayList<ContentModule> resultList = new ArrayList<ContentModule>();
			for(Object module : modules) {
				resultList.add(((ContentModule) module));
			}
			return resultList;
		} catch(RemoteException e) {
			AgnUtils.logger().error("Error while getting modules for category " + e + "\n" + AgnUtils.getStackTrace(e));
		}
		return null;
	}

	public void removeCMLocationForMailingsByContentModule(int contentModuleId,
														   List<Integer> mailingsToDeassign) {
		try {
			contentModuleService
					.removeCMLocationForMailingsByContentModule(contentModuleId,
							mailingsToDeassign.toArray());
		} catch(RemoteException e) {
			AgnUtils.logger()
					.error("Error while remove module content location from mailing" + e +
							"\n" + AgnUtils.getStackTrace(e));
		}
	}
}
