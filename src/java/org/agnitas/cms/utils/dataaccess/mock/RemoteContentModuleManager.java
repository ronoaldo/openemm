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
import org.agnitas.util.*;

/**
 * @author Igor Nesterenko
 */
public class RemoteContentModuleManager implements ContentModuleManager {

	public ContentModule getContentModule(int id) {
		return null;
	}

	public List<ContentModule> getContentModules(int companyId) {
		return null;
	}

	public void deleteContentModule(int id) {
	}

	public int createContentModule(ContentModule contentModule) {
		return 0;
	}

	public boolean updateContentModule(int id, String newName, String newDescription) {
		return false;
	}

	public List<CmsTag> getContentModuleContents(int contentModuleId) {
		return null;
	}

	public void saveContentModuleContent(int contentModuleId, CmsTag tag) {
	}

	public void removeContentsForContentModule(int contentModuleId) {
	}

	public List<Integer> getMailingBinding(List<Integer> mailingIds,
										   int contentModuleId) {
		return null;
	}

	public List<Integer> getMailingsByContentModule(int contentModuleId) {
		return null;
	}

	public void addMailingBindings(int contentModuleId, List<Integer> mailingIds) {
	}

	public void removeMailingBindings(int contentModuleId, List<Integer> mailingIds) {
	}

	public void addMailingBindingToContentModules(List<Integer> contentModuleIds,
												  int mailingId) {
	}

	public void removeMailingBindingFromContentModules(List<Integer> contentModuleIds,
													   int mailingId) {
	}

	public List<Integer> getAssignedCMsForMailing(int mailingId) {
		return null;
	}

	public List<ContentModuleLocation> getCMLocationsForMailingId(
			int mailingId) {
		return null;
	}

	public List<ContentModule> getContentModulesForMailing(int mailingId) {
		return null;
	}

	public void removeCMLocationsForMailing(int mailingId) {
	}

	public void addCMLocations(List<ContentModuleLocation> locations) {
	}

	public void saveContentModuleContentList(int contentModuleId, List tagList) {
	}

	public void updateCMLocation(ContentModuleLocation location) {
		AgnUtils.logger().error("Error unsupported operation");
	}

	public void removeCMLocationForMailingsByContentModule(int contentModuleId,
														   List<Integer> mailingsToDeassign) {
	}

}