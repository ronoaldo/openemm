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

package org.agnitas.ecs.backend.dao;

import org.agnitas.ecs.backend.beans.ClickStatColor;
import org.agnitas.ecs.backend.beans.ClickStatInfo;

import java.util.Collection;

/**
 * Dao for accessing <code>click_stat_colors_tbl</code> - the table that
 * stores color values for percent ranges for differnet companies
 *
 * @author Vyacheslav Stepanov
 */
public interface EmbeddedClickStatDao {

	/**
	 * Method gets collection of color values for company id
	 *
	 * @param companyId id of company
	 * @return collection of {@link ClickStatColor} beans for companyId
	 */
	public Collection<ClickStatColor> getClickStatColors(int companyId);

	/**
	 * Gets click statistics for the mailing for certain ecs mode
	 *
	 * @param companyId id of company
	 * @param mailingId id of mailing
	 * @param mode	  ecs-mode
	 * @return click statistics for links of the mailing
	 */
	public ClickStatInfo getClickStatInfo(int companyId, int mailingId, int mode);
}
