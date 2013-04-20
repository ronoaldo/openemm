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
 * Provide remote functionality to ContentModuleTypeManager.
 * Invokes service from central content repository.
 *
 * @author Igor Nesterenko
 * @see org.agnitas.cms.webservices.ContentModuleTypeService
 */

public class RemoteContentModuleTypeManager implements ContentModuleTypeManager {
	private RemoteContentModuleTypeManager_PortType moduleTypeService;

	public void setPortUrl(String portUrlString) {
		final RemoteContentModuleTypeManagerServiceLocator locator = new RemoteContentModuleTypeManagerServiceLocator();
		try {
			moduleTypeService = locator
					.getRemoteContentModuleTypeManager(new URL(portUrlString));
		} catch(ServiceException e) {
			AgnUtils.logger().error("Error while acces to service " + e + "\n" +
					AgnUtils.getStackTrace(e));
		} catch(MalformedURLException e) {
			AgnUtils.logger().error("Error while parsing port address " + e + "\n" +
					AgnUtils.getStackTrace(e));
		}
	}


	public ContentModuleType getContentModuleType(int id) {
		try {
			return moduleTypeService.getContentModuleType(id);
		} catch(RemoteException e) {
			AgnUtils.logger()
					.error("Error in service " + e + "\n" + AgnUtils.getStackTrace(e));
		}
		return null;
	}

	public List<ContentModuleType> getContentModuleTypes(int companyId,
														 boolean includePublic) {
		final ArrayList<ContentModuleType> moduleTypeList = new ArrayList<ContentModuleType>();
		try {
			final Object[] moduleTypes = moduleTypeService
					.getContentModuleTypes(companyId, includePublic);
			for(Object moduleType : moduleTypes) {
				moduleTypeList.add(((ContentModuleType) moduleType));
			}
		} catch(RemoteException e) {
			AgnUtils.logger().error("Error while acces to service " + e + "\n" +
					AgnUtils.getStackTrace(e));
		}
		return moduleTypeList;
	}

	public void deleteContentModuleType(int id) {
		try {
			moduleTypeService.deleteContentModuleType(id);
		} catch(RemoteException e) {
			AgnUtils.logger().error("Error while delete " +
					ContentModuleType.class.getSimpleName() + " " + e + "\n" +
					AgnUtils.getStackTrace(e));
		}
	}

	public int createContentModuleType(ContentModuleType moduleType) {
		try {
			return moduleTypeService.createContentModuleType(moduleType);
		} catch(RemoteException e) {
			AgnUtils.logger().error("Error while creation " +
					ContentModuleType.class.getSimpleName() + " " + e + "\n" +
					AgnUtils.getStackTrace(e));
		}
		return 0;
	}

	public boolean updateContentModuleType(ContentModuleType moduleType) {
		try {
			return moduleTypeService.updateContentModuleType(moduleType);
		} catch(RemoteException e) {
			AgnUtils.logger().error("Error while update " +
					ContentModuleType.class.getSimpleName() + e + "\n" +
					AgnUtils.getStackTrace(e));
		}
		return false;
	}
}
