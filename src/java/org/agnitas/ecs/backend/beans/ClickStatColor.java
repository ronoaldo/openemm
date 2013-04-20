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

/**
 * Bean that stores color value for percent range (used for ECS)
 *
 * @author Vyacheslav Stepanov
 */
public interface ClickStatColor extends Serializable {

	/**
	 * Getter for id property
	 *
	 * @return id
	 */
	public int getId();

	/**
	 * Setter for id property
	 *
	 * @param id new id of object
	 */
	public void setId(int id);

	/**
	 * Getter for companyId property
	 *
	 * @return id of company
	 */
	public int getCompanyId();

	/**
	 * Setter for companyId property
	 *
	 * @param companyId new id of company
	 */
	public void setCompanyId(int companyId);

	/**
	 * Getter for color property (should be in HEX i.e. "FF00FF")
	 *
	 * @return color property
	 */
	public String getColor();

	/**
	 * Setter for color property (should be in HEX i.e. "FF00FF")
	 *
	 * @param color new color property for this object
	 */
	public void setColor(String color);

	/**
	 * Getter for rangeStart property - lower limit of percent range
	 *
	 * @return rangeStart property
	 */
	public double getRangeStart();

	/**
	 * Setter for rangeStart property - lower limit of percent range
	 *
	 * @param rangeStart new rangeStart for this object
	 */
	public void setRangeStart(double rangeStart);

	/**
	 * Getter for rangeEnd property - upper limit of percent range
	 *
	 * @return rangeEnd property
	 */
	public double getRangeEnd();

	/**
	 * Setter for rangeEnd property - upper limit of percent range
	 *
	 * @param rangeEnd new rangeEnd for this object
	 */
	public void setRangeEnd(double rangeEnd);

}
