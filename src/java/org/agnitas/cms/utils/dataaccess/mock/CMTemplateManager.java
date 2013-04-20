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
 * This class used only for Java2WSDL generation.
 * Interface must be synchronized with
 * org.agnitas.cms.utils.dataaccess.CMTemplateManager
 *
 * @author Igor Nesterenko
 */
public interface CMTemplateManager {

	CMTemplate createCMTemplate(CMTemplate template);

	CMTemplate getCMTemplate(int id);

	List<CMTemplate> getCMTemplates(int companyId);

	List<CMTemplate> getCMTemplatesSortByName(int companyId, String sortDirection);

	void deleteCMTemplate(int id);

	boolean updateCMTemplate(int id, String name, String description);

	boolean updateContent(int id, byte[] content);

	Map<Integer, Integer> getMailingBinding(int cmTemplateId);

	Map<Integer, Integer> getMailingBinding(List<Integer> mailingIds);

	void addMailingBindings(int cmTemplateId, List<Integer> mailingIds);

	void removeMailingBindings(List<Integer> mailingIds);

	CMTemplate getCMTemplateForMailing(int mailingId);

	String getTextVersion(int adminId);

	void removeTextVersion(int adminId);

	void saveTextVersion(int adminId, String text);

	List<Integer> getMailingWithCmsContent(List<Integer> mailingIds, int companyId);
}