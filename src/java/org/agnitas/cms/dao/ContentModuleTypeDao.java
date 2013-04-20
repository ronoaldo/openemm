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

package org.agnitas.cms.dao;

import org.agnitas.cms.webservices.generated.*;
import org.springframework.context.*;

import java.util.*;

/**
 * @author Vyacheslav Stepanov
 */
public interface ContentModuleTypeDao extends ApplicationContextAware {

	/**
	 * @param id id of needed CMT
	 * @return CMT for the given id
	 */
	ContentModuleType getContentModuleType(int id);

	/**
	 * @param companyId	 company id of needed CMTs
	 * @param includePublic if true - adds public CMTs to list
	 * @return list of CMTs for companyId
	 */
	List<ContentModuleType> getContentModuleTypes(int companyId,
												  boolean includePublic);

	/**
	 * Removes CMT with a given id
	 *
	 * @param id id of CMT
	 */
	void deleteContentModuleType(int id);

	/**
	 * Creates new CMT in database
	 *
	 * @param moduleType CMT to create
	 * @return CMT with set id
	 */
	int createContentModuleType(ContentModuleType moduleType);

	/**
	 * Updates CMT's name, description and content
	 *
	 * @param moduleType CMT to update
	 * @return true if update was successful, false if not
	 */
	boolean updateContentModuleType(ContentModuleType moduleType);

}