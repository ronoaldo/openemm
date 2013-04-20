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

import org.agnitas.ecs.backend.beans.ClickStatColor;

/**
 * Implementation of {@link ClickStatColor} bean
 *
 * @author Vyacheslav Stepanov
 */
public class ClickStatColorImpl implements ClickStatColor {

	private int id;

	private int companyId;

	private String color;

	private double rangeStart;

	private double rangeEnd;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public double getRangeStart() {
		return rangeStart;
	}

	public void setRangeStart(double rangeStart) {
		this.rangeStart = rangeStart;
	}

	public double getRangeEnd() {
		return rangeEnd;
	}

	public void setRangeEnd(double rangeEnd) {
		this.rangeEnd = rangeEnd;
	}
}
