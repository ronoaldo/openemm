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
import org.springframework.context.*;
import org.springframework.remoting.jaxrpc.*;

/**
 * Gives service functionality to CMTemplateManager
 * from central content repository. Delegate operations
 * to CMTemplateDao.
 *
 * @author Igor Nesterenko
 * @see org.agnitas.cms.utils.dataaccess.CMTemplateManager
 * @see org.agnitas.cms.utils.dataaccess.RemoteCMTemplateManager
 * @see org.agnitas.cms.dao.CMTemplateDao
 */
public class CMTemplateService extends ServletEndpointSupport
		implements RemoteCMTemplateManager_PortType {

	private CMTemplateDao cmTemplateDat;

	private CMTemplateDao getDao() {
		if(cmTemplateDat == null) {
			final ApplicationContext applicationContext = getApplicationContext();
			cmTemplateDat = ((CMTemplateDao) applicationContext
					.getBean("CMTemplateDao"));
		}
		return cmTemplateDat;
	}

	public CMTemplate createCMTemplate(CMTemplate template) throws
			RemoteException {
		return getDao().createCMTemplate(template);
	}

	public CMTemplate getCMTemplate(int id) throws RemoteException {
		return getDao().getCMTemplate(id);
	}

	public Object[] getCMTemplates(int companyId) throws RemoteException {
		final List<CMTemplate> cmTemplateList = getDao()
				.getCMTemplates(companyId);
		return cmTemplateList.toArray();
	}

	public Object[] getCMTemplatesSortByName(int companyId,
											 String sortDirection) throws
			RemoteException {
		final List<CMTemplate> cmTemplateList = getDao()
				.getCMTemplatesSortByName(companyId, sortDirection);
		return cmTemplateList.toArray();
	}

	public void deleteCMTemplate(int id) throws RemoteException {
		getDao().deleteCMTemplate(id);
	}

	public boolean updateCMTemplate(int id, String name,
									String description) throws RemoteException {
		return getDao().updateCMTemplate(id, name, description);
	}

	public boolean updateContent(int id, byte[] content) throws
			RemoteException {
		return getDao().updateContent(id, content);
	}

	public CMTemplate getCMTemplateForMailing(int mailingId) throws
			RemoteException {
		return getDao().getCMTemplateForMailing(mailingId);
	}

	public void addMailingBindings(int cmTemplateId, Object[] mailingIds) throws
			RemoteException {
		final ArrayList<Integer> mailingIdList = toIntegerList(mailingIds);
		getDao().addMailingBindings(cmTemplateId, mailingIdList);
	}

	public void removeMailingBindings(Object[] mailingIds) throws
			RemoteException {
		getDao().removeMailingBindings(toIntegerList(mailingIds));
	}

	public Object[] getMailingBindingWrapper(int cmTemplate) throws
			RemoteException {
		final Map<Integer, Integer> map = getDao()
				.getMailingBinding(cmTemplate);
		final List<Integer> mailingList = new ArrayList<Integer>(map.keySet());
		return mailingList.toArray();
	}

	public Object[] getMailingBindingArrayWrapper(Object[] mailingIds) throws
			RemoteException {
		final List<Integer> mailingIdList = toIntegerList(mailingIds);
		final Map<Integer, Integer> map = getDao()
				.getMailingBinding(mailingIdList);
		final List<Integer> existTemplatesIdList = new ArrayList<Integer>(
				map.values());
		final List<Integer> existMailingIdList = new ArrayList<Integer>(
				map.keySet());
		final ArrayList list = new ArrayList();
		list.add(existMailingIdList.toArray());
		list.add(existTemplatesIdList.toArray());
		return list.toArray();
	}

	private static ArrayList<Integer> toIntegerList(Object[] mailingIds) {
		final ArrayList<Integer> mailingIdList = new ArrayList<Integer>();
		for(Object object : mailingIds) {
			mailingIdList.add((Integer) object);
		}
		return mailingIdList;
	}

	public String getTextVersion(int adminId) throws RemoteException {
		return getDao().getTextVersion(adminId);
	}

	public void removeTextVersion(int adminId) throws RemoteException {
		getDao().removeTextVersion(adminId);
	}

	public void saveTextVersion(int adminId, String text) throws RemoteException {
		getDao().saveTextVersion(adminId, text);
	}

	public Object[] getMailingWithCmsContent(Object[] mailingIds, int companyId) throws
			RemoteException {
		final List<Integer> mailingsWithCmsContent = getDao()
				.getMailingWithCmsContent(toIntegerList(mailingIds), companyId);
		return mailingsWithCmsContent.toArray();
	}
}
