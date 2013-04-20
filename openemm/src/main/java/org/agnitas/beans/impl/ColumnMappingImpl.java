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

package org.agnitas.beans.impl;

import org.agnitas.beans.ColumnMapping;

/**
 * @author Vyacheslav Stepanov
 */
public class ColumnMappingImpl implements ColumnMapping {

    protected int id;

    protected int profileId;

    protected String fileColumn;

    protected String databaseColumn;

    protected boolean mandatory;

    protected String defaultValue = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public String getFileColumn() {
        return fileColumn;
    }

    public void setFileColumn(String fileColumn) {
        this.fileColumn = fileColumn;
    }

    public String getDatabaseColumn() {
        return databaseColumn;
    }

    public void setDatabaseColumn(String databaseColumn) {
        this.databaseColumn = databaseColumn;
    }

    public boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return new StringBuilder().
                append("csv = \"").append(fileColumn).
                append("\", database = \"").append(databaseColumn).
                append("\", mandatory = ").append(mandatory).toString();
    }
}