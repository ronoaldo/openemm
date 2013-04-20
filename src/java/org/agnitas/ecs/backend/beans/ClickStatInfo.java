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

package org.agnitas.ecs.backend.beans;

import java.io.Serializable;
import java.util.Map;

/**
 * Bean that stores mailing link-click statistics
 *
 * @author Vyacheslav Stepanov
 */
public interface ClickStatInfo extends Serializable {

	/**
	 * Add link statistics info
	 *
	 * @param urlId		id of link url in database
	 * @param clickNum	 number of clicks for link
	 * @param clickPercent percent value of link clicks
	 */
	public void addURLInfo(int urlId, int clickNum, double clickPercent);

	/**
	 * Get links click info
	 *
	 * @return map containing click-info (Map structure: urlId -> clicks number)
	 */
	public Map<Integer, Integer> getClicks();

	/**
	 * Set links click info
	 *
	 * @param clicks click map (Map structure: urlId -> clicks number)
	 */
	public void setClicks(Map<Integer, Integer> clicks);

	/**
	 * Get clicks percentage
	 *
	 * @return click percentage map (Map structure: urlId -> clicks percentage)
	 */
	public Map<Integer, Double> getPercentClicks();

	/**
	 * Get clicks percentage
	 *
	 * @param percentClicks click percentage map (Map structure: urlId -> clicks percentage)
	 */
	public void setPercentClicks(Map<Integer, Double> percentClicks);
}