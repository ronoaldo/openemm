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

package org.agnitas.beans;

import java.io.Serializable;

/**
 * Bean contains information about mapping of csv-file column to database column
 * (while importing recipients from csv-file)
 *
 * @author Vyacheslav Stepanov
 */
public interface ColumnMapping extends Serializable {


    /**
     * Constant for fileColumn field. Indicates that csv-column of that mapping
     * should not be imported into database
     */
    public static final String DO_NOT_IMPORT = "do-not-import-column";

    /**
     * Getter for property id
     *
     * @return the property id for this ColumnMapping
     */
    int getId();

    /**
     * Setter for property id
     *
     * @param id new value for id property
     */
    void setId(int id);

    /**
     * Getter for property profileId (the owner-profile of this mapping)
     *
     * @return the property profileId for this ColumnMapping
     */
    int getProfileId();

    /**
     * Setter for property profileId
     *
     * @param profileId new value for profileId property
     */
    void setProfileId(int profileId);

    /**
     * Getter for property fileColumn (column in csv-file)
     *
     * @return the property fileColumn for this ColumnMapping
     */
    String getFileColumn();

    /**
     * Setter for property fileColumn
     *
     * @param fileColumn new value for fileColumn property
     */
    void setFileColumn(String fileColumn);

    /**
     * Getter for property databaseColumn (column in database)
     *
     * @return the property databaseColumn for this ColumnMapping
     */
    String getDatabaseColumn();

    /**
     * Setter for property databaseColumn
     *
     * @param databaseColumn new value for databaseColumn property
     */
    void setDatabaseColumn(String databaseColumn);

    /**
     * Getter for property mandatory (indicates if the column is required)
     *
     * @return the property mandatory for this ColumnMapping
     */
    boolean getMandatory();

    /**
     * Setter for property mandatory
     *
     * @param mandatory new value for mandatory property
     */
    void setMandatory(boolean mandatory);

    /**
     * Getter for property defaultValue
     *
     * @return defaultValue property for this ColumnMapping
     */
    String getDefaultValue();

    /**
     * Setter for property defaultValue
     *
     * @param defaultValue new value for defaultValue property
     */
    void setDefaultValue(String defaultValue);

}
