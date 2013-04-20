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

package org.agnitas.ecs.backend.service;

import org.agnitas.ecs.backend.dao.EmbeddedClickStatDao;

/**
 * Service class that handles creation of Embedded click statistics
 *
 * @author Vyacheslav Stepanov
 */
public interface EmbeddedClickStatService {

	/**
	 * Color that will we set to stat-label if color for some percentage
	 * value is not specified in DB
	 */
	public static final String DEFAULT_STAT_LABEL_COLOR = "FFFFFF";

	/**
	 * Method gets mailing HTML-content for the certain recipient
	 *
	 * @param mailingId   id of mailing
	 * @param recipientId id of recipient
	 * @return mailing HTML-content
	 */
	public String getMailingContent(int mailingId, int recipientId);

	/**
	 * Method adds click-stat info to mailing content in a form of hidden fields.
	 * These hidden fields will accumulate information about click statistics (clicks
	 * number, clicks percentage, color for each URL) and will be used by javascript to
	 * create click stat labels
	 *
	 * @param content   mailing HTML content
	 * @param mode	  ECS mode
	 * @param companyId id of company
	 * @return mailing HTML content + hidden fields that will be used by ECS-page javascript
	 */
	public String addStatsInfo(String content, int mode, int mailingId, int companyId);

	/**
	 * Setter for Dao that handles color values for different percentage values for ECS
	 *
	 * @param ecsDao dao object
	 */
	public void setEmbeddedClickStatDao(EmbeddedClickStatDao ecsDao);

}
