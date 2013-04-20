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

package org.agnitas.ecs.backend.beans.impl;

import org.agnitas.ecs.backend.beans.ClickStatInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link org.agnitas.ecs.backend.beans.ClickStatInfo} bean
 *
 * @author Vyacheslav Stepanov
 */
public class ClickStatInfoImpl implements ClickStatInfo {

	private Map<Integer, Integer> clicks;

	private Map<Integer, Double> percentClicks;

	public ClickStatInfoImpl() {
		clicks = new HashMap<Integer, Integer>();
		percentClicks = new HashMap<Integer, Double>();
	}

	public void addURLInfo(int urlId, int clickNum, double clickPercent) {
		clicks.put(urlId, clickNum);
		percentClicks.put(urlId, clickPercent);
	}

	public Map<Integer, Integer> getClicks() {
		return clicks;
	}

	public void setClicks(Map<Integer, Integer> clicks) {
		this.clicks = clicks;
	}

	public Map<Integer, Double> getPercentClicks() {
		return percentClicks;
	}

	public void setPercentClicks(Map<Integer, Double> percentClicks) {
		this.percentClicks = percentClicks;
	}
}