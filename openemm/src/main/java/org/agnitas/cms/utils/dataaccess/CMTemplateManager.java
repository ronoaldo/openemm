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
import org.agnitas.cms.webservices.generated.*;

/**
 * Provides core operations with cms`s templates
 *
 * @author Vyacheslav Stepanov
 */
public interface CMTemplateManager {
	/**
	 * Method stores CM Template
	 *
	 * @param template template to be created
	 * @return stored template with set id
	 */
	CMTemplate createCMTemplate(CMTemplate template);

	/**
	 * @param id id of needed CM Template
	 * @return CM Template for the given id
	 */
	CMTemplate getCMTemplate(int id);

	/**
	 * @param companyId company id of needed CM Templates
	 * @return list of CM Templates for companyId
	 */
	List<CMTemplate> getCMTemplates(int companyId);

	/**
	 * Method removes CM Templates with a given id
	 *
	 * @param id id of CM Template that will be removed
	 */
	void deleteCMTemplate(int id);

	/**
	 * Method updates name and description of CM Template with given id
	 *
	 * @param id		  the id of CM Template that needs to be updated
	 * @param name		new name of CM Template
	 * @param description new description of CM Template
	 * @return true if CM Template was successfuly updated, false otherwise
	 */
	boolean updateCMTemplate(int id, String name, String description);

	/**
	 * Method updates content on CM template
	 *
	 * @param id	  id of CM template to be updated
	 * @param content the new content
	 * @return true if update was successfull
	 */
	boolean updateContent(int id, byte[] content);

	/**
	 * Gets mailing bindings for cm_template_id
	 *
	 * @param cmTemplateId id of CM template
	 * @return map(mailingId->cmTemplateId) of mail bindings
	 */
	Map<Integer, Integer> getMailingBinding(int cmTemplateId);

	/**
	 * Gets mailing bindings for mailings id
	 *
	 * @param mailingIds id of mailings
	 * @return map(mailingId->cmTemplateId) of mail bindings
	 */
	Map<Integer, Integer> getMailingBinding(List<Integer> mailingIds);

	/**
	 * Adds set of mail bindings
	 *
	 * @param cmTemplateId id of CM template
	 * @param mailingIds   ids of mailings
	 */
	void addMailingBindings(int cmTemplateId, List<Integer> mailingIds);

	/**
	 * Removes mailing bindings for mailings ids
	 *
	 * @param mailingIds mailings ids
	 */
	void removeMailingBindings(List<Integer> mailingIds);

	/**
	 * Gets cmTemplate got mailings id
	 *
	 * @param mailingId id of mailing
	 * @return cmTemplate for mailing`s id
	 */
	CMTemplate getCMTemplateForMailing(int mailingId);


	/**
	 * Gets text version for admin
	 *
	 * @param adminId
	 * @return test version
	 */
	String getTextVersion(int adminId);

	/**
	 * Removes text version by admin
	 *
	 * @param adminId
	 */
	void removeTextVersion(int adminId);

	/**
	 * Save text version content
	 *
	 * @param adminId admin`s id
	 * @param text	text version`s content
	 */
	void saveTextVersion(int adminId, String text);

	List<Integer> getMailingWithCmsContent(List<Integer> mailingIds, int companyId);
}
