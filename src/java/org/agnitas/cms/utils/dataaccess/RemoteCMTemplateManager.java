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
 * Provide remote functionality to CMTemplateManager.
 * Invokes service from central content repository.
 *
 * @author Igor Nesterenko
 * @see org.agnitas.cms.webservices.CMTemplateService
 */
public class RemoteCMTemplateManager implements CMTemplateManager {

	RemoteCMTemplateManager_PortType cmTemplateService;


	public RemoteCMTemplateManager() {
		super();
	}

	public void setPortUrl(String portUrlString) {
		RemoteCMTemplateManagerServiceLocator serviceLocator = new RemoteCMTemplateManagerServiceLocator();
		try {
			cmTemplateService = serviceLocator
					.getRemoteCMTemplateManager(new URL(portUrlString));
		} catch(ServiceException e) {
			AgnUtils.logger().error("Error while creation remote connection " + e + "\n" +
					AgnUtils.getStackTrace(e));
		} catch(MalformedURLException e) {
			AgnUtils.logger().error("Error while parsing port address " + e + "\n" +
					AgnUtils.getStackTrace(e));
		}
	}

	public CMTemplate createCMTemplate(CMTemplate template) {
		CMTemplate newCmTemplate = null;
		try {
			newCmTemplate = cmTemplateService.createCMTemplate(template);
		} catch(Exception exception) {
			AgnUtils.logger()
					.error("Error while creation of CM Template: " + exception + "\n" +
							AgnUtils.getStackTrace(exception));
		}
		return newCmTemplate;
	}

	public CMTemplate getCMTemplate(int id) {
		CMTemplate cmTemplate = null;

		try {
			cmTemplate = cmTemplateService.getCMTemplate(id);
		} catch(RemoteException e) {
			AgnUtils.logger().error("Error while getting CM Template: " + e + "\n" +
					AgnUtils.getStackTrace(e));
		}

		return cmTemplate;
	}

	public List<CMTemplate> getCMTemplates(int companyId) {
		List<CMTemplate> cmTemplateList = new ArrayList<CMTemplate>();
		try {
			Object[] cmTemplates = cmTemplateService.getCMTemplates(companyId);
			for(int index = 0; index < cmTemplates.length; index++) {
				CMTemplate cmTemplate = (CMTemplate) cmTemplates[index];
				cmTemplateList.add(cmTemplate);
			}
		} catch(RemoteException e) {
			AgnUtils.logger()
					.error("Error while getting CM Template`s list: " + e + "\n" +
							AgnUtils.getStackTrace(e));
		}
		return cmTemplateList;
	}

	public void deleteCMTemplate(int id) {
		try {
			cmTemplateService.deleteCMTemplate(id);
		} catch(RemoteException e) {
			AgnUtils.logger().error("Error while deleting CM Template: " + e + "\n" +
					AgnUtils.getStackTrace(e));
		}
	}

	public boolean updateCMTemplate(int id, String name, String description) {
		try {
			return cmTemplateService.updateCMTemplate(id, name, description);
		} catch(RemoteException e) {
			AgnUtils.logger().error("Error while update CM Template: " + e + "\n" +
					AgnUtils.getStackTrace(e));
		}
		return false;
	}

	public boolean updateContent(int id, byte[] content) {
		try {
			return cmTemplateService.updateContent(id, content);
		} catch(RemoteException e) {
			AgnUtils.logger()
					.error("Error while update content of CM Template: " + e + "\n" +
							AgnUtils.getStackTrace(e));
		}
		return false;
	}

	public Map<Integer, Integer> getMailingBinding(int cmTemplateId) {
		final HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		try {
			final Object[] objects = cmTemplateService
					.getMailingBindingWrapper(cmTemplateId);
			for(int index = 0; index < objects.length; index++) {
				map.put((Integer) objects[index], cmTemplateId);
			}
			return map;
		} catch(RemoteException e) {
			AgnUtils.logger()
					.error("Error while get mail binding for CM Template: " + e + "\n" +
							AgnUtils.getStackTrace(e));
		}
		return map;
	}

	public Map<Integer, Integer> getMailingBinding(List<Integer> mailingIds) {
		final HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		try {
			final Object[] mailingTemplateArrays = cmTemplateService
					.getMailingBindingArrayWrapper(mailingIds.toArray());
			Object[] mailings = (Object[]) mailingTemplateArrays[0];
			Object[] templates = (Object[]) mailingTemplateArrays[1];
			for(int index = 0; index < mailings.length; index++) {
				final Integer mailingId = (Integer) mailings[index];
				final Integer templateId = (Integer) templates[index];
				map.put(mailingId, templateId);
			}
		} catch(RemoteException e) {
			AgnUtils.logger()
					.error("Error while update content of CM Template: " + e + "\n" +
							AgnUtils.getStackTrace(e));
		}
		return map;
	}

	public void addMailingBindings(int cmTemplateId, List<Integer> mailingIds) {
		try {
			cmTemplateService.addMailingBindings(cmTemplateId, mailingIds.toArray());
		} catch(RemoteException e) {
			AgnUtils.logger()
					.error("Error while add mail binding to CM Template: " + e + "\n" +
							AgnUtils.getStackTrace(e));
		}
	}

	public void removeMailingBindings(List<Integer> mailingIds) {
		try {
			cmTemplateService.removeMailingBindings(mailingIds.toArray());
		} catch(RemoteException e) {
			AgnUtils.logger()
					.error("Error while remove mail binding to CM Template: " + e + "\n" +
							AgnUtils.getStackTrace(e));
		}
	}

	public CMTemplate getCMTemplateForMailing(int mailingId) {
		try {
			return cmTemplateService.getCMTemplateForMailing(mailingId);
		} catch(RemoteException e) {
			AgnUtils.logger().error("Error while getting CM Template: " + e + "\n" +
					AgnUtils.getStackTrace(e));
		}
		return null;
	}

	public String getTextVersion(int adminId) {
		try {
			return cmTemplateService.getTextVersion(adminId);
		} catch(RemoteException e) {
			AgnUtils.logger().error("Error while getting text version" + e + "\n" +
					AgnUtils.getStackTrace(e));
		}
		return null;
	}

	public void removeTextVersion(int adminId) {
		try {
			cmTemplateService.removeTextVersion(adminId);
		} catch(RemoteException e) {
			AgnUtils.logger().error("Error while remove text version" + e + "\n" +
					AgnUtils.getStackTrace(e));
		}
	}

	public void saveTextVersion(int adminId, String text) {
		try {
			cmTemplateService.saveTextVersion(adminId, text);
		} catch(RemoteException e) {
			AgnUtils.logger().error("Error while save text version" + e + "\n" +
					AgnUtils.getStackTrace(e));
		}
	}

	public List<Integer> getMailingWithCmsContent(List<Integer> mailingIds,
												  int companyId) {
		try {
			final Object[] cmsMailingIds = cmTemplateService
					.getMailingWithCmsContent(mailingIds.toArray(), companyId);
			final ArrayList<Integer> cmsMailingIdList = new ArrayList<Integer>();
			for(Object cmsMailingIdObject : cmsMailingIds) {
				cmsMailingIdList.add(((Integer) cmsMailingIdObject));
			}
			return cmsMailingIdList;
		} catch(RemoteException e) {
			AgnUtils.logger().error("Error while save text version" + e + "\n" +
					AgnUtils.getStackTrace(e));
		}
		return null;
	}
}
