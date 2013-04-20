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

package org.agnitas.service.impl;

/**
 * @author Viktor Gema
 */
public class CSVColumnState {
    public static final int TYPE_CHAR = 1;

    public static final int TYPE_NUMERIC = 2;

    public static final int TYPE_DATE = 3;

    private String colName;

    private Boolean importedColumn;

    private int type;

	private boolean nullable;

    public CSVColumnState() {
    }

    public CSVColumnState(String colName, Boolean importedColumn, int type) {
        this.colName = colName;
        this.importedColumn = importedColumn;
        this.type = type;
    }

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public Boolean getImportedColumn() {
        return importedColumn;
    }

    public void setImportedColumn(Boolean importedColumn) {
        this.importedColumn = importedColumn;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}
}
