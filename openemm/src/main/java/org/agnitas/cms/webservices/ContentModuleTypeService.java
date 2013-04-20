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
import org.springframework.remoting.jaxrpc.*;

/**
 * Gives service functionality to ContentModuleTypeManager
 * from central content repository. Delegate operations
 * to ContentModuleTypeDao.
 *
 * @author Igor Nesterenko
 * @see org.agnitas.cms.utils.dataaccess.ContentModuleTypeManager
 * @see org.agnitas.cms.utils.dataaccess.RemoteContentModuleTypeManager
 * @see org.agnitas.cms.dao.ContentModuleTypeDao
 */
public class ContentModuleTypeService extends ServletEndpointSupport
		implements RemoteContentModuleTypeManager_PortType {

	private ContentModuleTypeDao contentModuleTypeDao;

	private ContentModuleTypeDao getDao() {
		if(contentModuleTypeDao == null) {
			contentModuleTypeDao = (ContentModuleTypeDao) getWebApplicationContext()
					.getBean("ContentModuleTypeDao");
		}
		return contentModuleTypeDao;
	}

	public ContentModuleType getContentModuleType(int id) throws
			RemoteException {
		return getDao().getContentModuleType(id);
	}

	public Object[] getContentModuleTypes(int companyId,
										  boolean includePublic) throws
			RemoteException {
		final List<ContentModuleType> moduleTypes = getDao()
				.getContentModuleTypes(companyId, includePublic);
		return moduleTypes.toArray();
	}

	public void deleteContentModuleType(int id) throws RemoteException {
		getDao().deleteContentModuleType(id);
	}

	public int createContentModuleType(ContentModuleType moduleType) throws
			RemoteException {
		return getDao().createContentModuleType(moduleType);
	}

	public boolean updateContentModuleType(ContentModuleType moduleType) throws
			RemoteException {
		return getDao().updateContentModuleType(moduleType);
	}
}
