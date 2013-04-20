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

package org.agnitas.cms.utils.dataaccess.mock;


import java.util.*;
import org.agnitas.cms.utils.dataaccess.mock.beans.*;

/**
 * Mock implementation of CMTemplateManager for appropriate generation Java2Wsdl
 *
 * @author Igor Nesterenko
 */
public class RemoteCMTemplateManager implements CMTemplateManager {

	public CMTemplate createCMTemplate(CMTemplate template) {
		return null;
	}

	public CMTemplate getCMTemplate(int id) {
		return null;
	}

	public List<CMTemplate> getCMTemplates(int companyId) {
		return null;
	}

	public List<CMTemplate> getCMTemplatesSortByName(int companyId,
													 String sortDirection) {
		return null;
	}

	public void deleteCMTemplate(int id) {
	}

	public boolean updateCMTemplate(int id, String name, String description) {
		return false;
	}

	public boolean updateContent(int id, byte[] content) {
		return false;
	}

	public void addMailingBindings(int cmTemplateId, List<Integer> mailingIds) {
	}

	public CMTemplate getCMTemplateForMailing(int mailingId) {
		return null;
	}

	public void removeMailingBindings(List<Integer> mailingIds) {
	}

	/**
	 * Can`t generate webservice port type with
	 * java.collection.Map implementation, workaround is
	 * replace this method with list wrapper,
	 * look to this.getMailingBindingWraper()
	 */
	public Map<Integer, Integer> getMailingBinding(int cmTemplateId) {
		return null;
	}

	/**
	 * Can`t generate webservice port type with
	 * java.collection.Map implementation, workaround is
	 * replace this method with list wrapper
	 * look to this.getMailingBindingWraper()
	 */
	public Map<Integer, Integer> getMailingBinding(List<Integer> mailingIds) {
		return null;
	}

	public List getMailingBindingWrapper(int cmTemplate) {
		return null;
	}

	public List getMailingBindingArrayWrapper(List<Integer> mailingIds) {
		return null;
	}

	public String getTextVersion(int adminId) {
		return null;
	}

	public void removeTextVersion(int adminId) {
	}

	public void saveTextVersion(int adminId, String text) {
	}

	public List<Integer> getMailingWithCmsContent(List<Integer> mailingIds,
												  int companyId) {
		return null;
	}
}