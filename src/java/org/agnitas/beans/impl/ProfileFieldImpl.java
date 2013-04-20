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

package org.agnitas.beans.impl;

import java.io.Serializable;

import org.agnitas.beans.ProfileField;
import org.apache.commons.lang.StringUtils;

public class ProfileFieldImpl implements ProfileField, Serializable {
	private static final long serialVersionUID = -6125451198749198856L;

	protected int companyID = -1;
	protected String column;
	protected int adminID = 0;
	protected String shortname = "";
	protected String description = "";
	protected String dataType;
	protected int dataTypeLength;
	protected String defaultValue = "";
	protected boolean nullable = true;
	protected int modeEdit = 0;
	protected int modeInsert = 0;

	public int getCompanyID() {
		return companyID;
	}
	
	public void setCompanyID(int company) {
		this.companyID = company;
	}

	public String getColumn() {
		return column;
	}
	
	public void setColumn(String column) {
		if (column != null) {
			this.column = column.toUpperCase();
		} else {
			this.column = null;
		}
		
		// Fallback for special cases in which the shortname is not set
		if (StringUtils.isEmpty(shortname)) {
			shortname = column;
		}
	}

	public int getAdminID() {
		return adminID;
	}
	
	public void setAdminID(int adminID) {
		this.adminID = adminID;
	}

	public String getShortname() {
		return shortname;
	}
	
	public void setShortname(String shortname) {
		if (shortname == null) {
			this.shortname = "";
		} else {
			this.shortname = shortname;
		}
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String desc) {
		if (desc == null) {
			desc = "";
		}
		this.description = desc;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dbType2String(dataType);
	}

	public int getDataTypeLength() {
		if ("VARCHAR".equals(dataType)) {
			return dataTypeLength;
		} else {
			return 0;
		}
	}

	public void setDataTypeLength(int dataTypeLength) {
		this.dataTypeLength = dataTypeLength;
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}
	
	public void setDefaultValue(String value) {
		if (value == null) {
			value = "";
		}
		this.defaultValue = value;
	}

	public int getModeEdit() {
		return modeEdit;
	}
	
	public void setModeEdit(int modeEdit) {
		this.modeEdit = modeEdit;
	}

	public int getModeInsert() {
		return modeInsert;
	}
	
	public void setModeInsert(int modeInsert) {
		this.modeInsert = modeInsert;
	}

	public boolean equals(Object o) {
		if (!getClass().isInstance(o)) {
			return false;
		}

		ProfileField f = (ProfileField) o;

		if (f.getCompanyID() != companyID)
			return false;

		if (!f.getColumn().equalsIgnoreCase(column))
			return false;

		return true;
	}

	public int hashCode() {
		Integer i = new Integer(companyID);

		return i.hashCode() * column.hashCode();
	}
	
	public boolean getNullable() {
		return nullable;
	}
	
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}
	
	/**
	 * String representation for easier debugging
	 */
	@Override
	public String toString() {
		int length = getDataTypeLength() ;
		return "(" + companyID + ") " + column + " shortname:" + shortname + " " 
				+ dataType + (length > 0 ? "(" + length + ")" : "") 
				+ (StringUtils.isNotEmpty(defaultValue) ? " default:" + defaultValue : "")
				+ " nullable:" + nullable;
	}
	
	protected static String dbType2String(int type) {
		switch (type) {
			case java.sql.Types.BIGINT:
			case java.sql.Types.INTEGER:
			case java.sql.Types.SMALLINT:
				return "INTEGER";
	
			case java.sql.Types.DECIMAL:
			case java.sql.Types.DOUBLE:
			case java.sql.Types.FLOAT:
			case java.sql.Types.NUMERIC:
			case java.sql.Types.REAL:
				return "DOUBLE";
	
			case java.sql.Types.CHAR:
				return "CHAR";
	
			case java.sql.Types.VARCHAR:
			case java.sql.Types.LONGVARCHAR:
			case java.sql.Types.CLOB:
				return "VARCHAR";
	
			case java.sql.Types.DATE:
			case java.sql.Types.TIMESTAMP:
			case java.sql.Types.TIME:
				return "DATE";
				
			default:
				return "UNKNOWN(" + type + ")";
		}
	}
	
	protected static String dbType2String(String typeName) {
		if (StringUtils.isBlank(typeName)) {
			return null;
		} else if (typeName.equalsIgnoreCase("BIGINT")
				|| typeName.equalsIgnoreCase("INT")
				|| typeName.equalsIgnoreCase("INTEGER")
				|| typeName.equalsIgnoreCase("NUMBER")
				|| typeName.equalsIgnoreCase("SMALLINT")) {
			return "INTEGER";
		} else if (typeName.equalsIgnoreCase("DECIMAL")
				|| typeName.equalsIgnoreCase("DOUBLE")
				|| typeName.equalsIgnoreCase("FLOAT")
				|| typeName.equalsIgnoreCase("NUMERIC")
				|| typeName.equalsIgnoreCase("REAL")) {
			return "DOUBLE";
		} else if (typeName.equalsIgnoreCase("CHAR")) {
			return "CHAR";
		} else if (typeName.equalsIgnoreCase("VARCHAR")
				|| typeName.equalsIgnoreCase("VARCHAR2")
				|| typeName.equalsIgnoreCase("LONGVARCHAR")
				|| typeName.equalsIgnoreCase("CLOB")) {
			return "VARCHAR";
		} else if (typeName.equalsIgnoreCase("DATE")
				|| typeName.equalsIgnoreCase("TIMESTAMP")
				|| typeName.equalsIgnoreCase("TIME")) {
			return "DATE";
		} else {
			return "UNKNOWN(" + typeName + ")";
		}
	}
}
