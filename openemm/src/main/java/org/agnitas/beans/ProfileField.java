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
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

package org.agnitas.beans;

public interface ProfileField {
	public static int MODE_EDIT_EDITABLE = 0;
	public static int MODE_EDIT_READONLY = 1;
	public static int MODE_EDIT_NOT_VISIBLE = 2;
	
	public int getCompanyID();

	public void setCompanyID(int company);

	public String getColumn();

	public void setColumn(String column);

	public int getAdminID();

	public void setAdminID(int adminID);

	public String getShortname();

	public void setShortname(String desc);

	public String getDescription();

	public void setDescription(String desc);

	public String getDataType();

	public void setDataType(String dataType);

	public int getDataTypeLength();

	public void setDataTypeLength(int dataTypeLength);

	public String getDefaultValue();

	public void setDefaultValue(String value);
	
	public boolean getNullable();
	
	public void setNullable(boolean nullable);

	/**
	 * ModeEdit
	 * Usage mode of this field in the GUI when creating or changing a customer
	 * 
	 * Allowed values:
	 * 	MODE_EDIT_NOT_VISIBLE:
	 * 		This field is not intended to be shown in the GUI
	 * 	MODE_EDIT_READONLY:
	 * 		This field is shown but can not be edited by the user
	 * 	MODE_EDIT_EDITABLE and all others:
	 * 		Show this field in GUI for edit
	 * @return
	 */
	public int getModeEdit();

	public void setModeEdit(int edit);

	public int getModeInsert();

	public void setModeInsert(int insert);
}
