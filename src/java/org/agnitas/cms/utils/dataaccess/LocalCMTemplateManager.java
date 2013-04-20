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
import org.agnitas.cms.webservices.generated.*;

/**
 * Temporary implementation of CMTemplateManager. Will be replaced with
 * implementation that uses SOAP for accessing Central content repository
 *
 * @author Vyacheslav Stepanov
 */
public class LocalCMTemplateManager implements CMTemplateManager {

	CMTemplateDao templateDao;

	public LocalCMTemplateManager(CMTemplateDao templateDao) {
		this.templateDao = templateDao;
	}

	public CMTemplate createCMTemplate(CMTemplate template) {
		return templateDao.createCMTemplate(template);
	}

	public CMTemplate getCMTemplate(int id) {
		return templateDao.getCMTemplate(id);
	}

	public List<CMTemplate> getCMTemplates(int companyId) {
		return templateDao.getCMTemplates(companyId);
	}

	public void deleteCMTemplate(int id) {
		templateDao.deleteCMTemplate(id);
	}

	public boolean updateCMTemplate(int id, String name, String description) {
		return templateDao.updateCMTemplate(id, name, description);
	}

	public boolean updateContent(int id, byte[] content) {
		return templateDao.updateContent(id, content);
	}

	public Map<Integer, Integer> getMailingBinding(int cmTemplateId) {
		return templateDao.getMailingBinding(cmTemplateId);
	}

	public void addMailingBindings(int cmTemplateId, List<Integer> mailingIds) {
		templateDao.addMailingBindings(cmTemplateId, mailingIds);
	}

	public void removeMailingBindings(List<Integer> mailingIds) {
		templateDao.removeMailingBindings(mailingIds);
	}

	public Map<Integer, Integer> getMailingBinding(List<Integer> mailingIds) {
		return templateDao.getMailingBinding(mailingIds);
	}

	public CMTemplate getCMTemplateForMailing(int mailingId) {
		return templateDao.getCMTemplateForMailing(mailingId);
	}

	public String getTextVersion(int adminId) {
		return templateDao.getTextVersion(adminId);
	}

	public void removeTextVersion(int adminId) {
		templateDao.removeTextVersion(adminId);
	}

	public void saveTextVersion(int adminId, String text) {
		templateDao.saveTextVersion(adminId, text);
	}

	public List<Integer> getMailingWithCmsContent(List<Integer> mailingIds,
												  int companyId) {
		return templateDao.getMailingWithCmsContent(mailingIds, companyId);
	}
}
