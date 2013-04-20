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

import org.agnitas.cms.utils.dataaccess.mock.beans.*;

import java.util.*;

/**
 * This class used only for Java2WSDL generation.
 * Interface must be synchronized with
 * org.agnitas.cms.utils.dataaccess.ContentModuleManager
 *
 * @author Igor Nesterenko
 */
public interface ContentModuleManager {

	ContentModule getContentModule(int id);

	List<ContentModule> getContentModules(int companyId);

	void deleteContentModule(int id);

	int createContentModule(ContentModule contentModule);

	boolean updateContentModule(int id, String newName, String newDescription);

	List<CmsTag> getContentModuleContents(int contentModuleId);

	void saveContentModuleContent(int contentModuleId, CmsTag tag);

	void removeContentsForContentModule(int contentModuleId);

	List<Integer> getMailingBinding(List<Integer> mailingIds, int contentModuleId);

	List<Integer> getMailingsByContentModule(int contentModuleId);

	void addMailingBindings(int contentModuleId, List<Integer> mailingIds);

	void removeMailingBindings(int contentModuleId, List<Integer> mailingIds);

	void addMailingBindingToContentModules(List<Integer> contentModuleIds,
										   int mailingId);

	void removeMailingBindingFromContentModules(List<Integer> contentModuleIds,
												int mailingId);

	List<Integer> getAssignedCMsForMailing(int mailingId);

	List<ContentModuleLocation> getCMLocationsForMailingId(int mailingId);

	List<ContentModule> getContentModulesForMailing(int mailingId);

	void removeCMLocationsForMailing(int mailingId);

	void removeCMLocationForMailingsByContentModule(int contentModuleId,
													List<Integer> mailingsToDeassign);

	void addCMLocations(List<ContentModuleLocation> locations);

	void saveContentModuleContentList(int contentModuleId,
									  List<org.agnitas.cms.webservices.generated.CmsTag>
											  tagList);

}