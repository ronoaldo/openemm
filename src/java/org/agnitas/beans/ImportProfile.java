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
import java.util.List;
import java.util.Map;

/**
 * Bean containing data of import profile (import profile contains recipient
 * import information that can be reused during several imports)
 *
 * @author Vyacheslav Stepanov
 */
public interface ImportProfile extends Serializable {

    /**
     * Getter for property id (id of import profile in database)
     *
     * @return value of property id for this ImportProfile
     */
    int getId();

    /**
     * Setter for property id
     *
     * @param id the new value for the id
     */
    void setId(int id);

    /**
     * Getter for property adminId
     *
     * @return value of property for adminId this ImportProfile
     */
    int getAdminId();

    /**
     * Setter for property adminId
     *
     * @param adminId the new value for the adminId
     */
    void setAdminId(int adminId);

    /**
     * Getter for property companyId
     *
     * @return value of property companyId for this ImportProfile
     */
    int getCompanyId();

    /**
     * Setter for property companyId
     *
     * @param companyId the new value for the companyId
     */
    void setCompanyId(int companyId);

    /**
     * Getter for property name
     *
     * @return value of property name for this ImportProfile
     */
    String getName();

    /**
     * Setter for property name
     *
     * @param name the new value for the name
     */
    void setName(String name);

    /**
     * Getter for property separator (character that separates columns in
     * csv-file)
     * See possible values in {@link org.agnitas.util.importvalues.Separator}
     *
     * @return value of property separator for this ImportProfile
     */
    int getSeparator();

    /**
     * Setter for property separator
     * See possible values in {@link org.agnitas.util.importvalues.Separator}
     *
     * @param separator the new value for the separator
     */
    void setSeparator(int separator);

    /**
     * Getter for property textRecognitionChar (character that is used to wrap
     * text values in csv-file)
     * See possible values in {@link org.agnitas.util.importvalues.TextRecognitionChar}
     *
     * @return value of property textRecognitionChar for this ImportProfile
     */
    int getTextRecognitionChar();

    /**
     * Setter for property textRecognitionChar
     * See possible values in {@link org.agnitas.util.importvalues.TextRecognitionChar}
     *
     * @param textRecognitionChar the new value for the textRecognitionChar
     */
    void setTextRecognitionChar(int textRecognitionChar);

    /**
     * Getter for property charset (the character-set of csv-file)
     * See possible values in {@link org.agnitas.util.importvalues.Charset}
     *
     * @return value of property charset for this ImportProfile
     */
    int getCharset();

    /**
     * Setter for property charset
     * See possible values in {@link org.agnitas.util.importvalues.Charset}
     *
     * @param charset the new value for the charset
     */
    void setCharset(int charset);

    /**
     * Getter for property dateFormat (the format of date columns that will
     * be used when parsing csv-file)
     * See possible values in {@link org.agnitas.util.importvalues.DateFormat}
     *
     * @return value of property dateFormat for this ImportProfile
     */
    int getDateFormat();

    /**
     * Setter for property dateFormat
     * See possible values in {@link org.agnitas.util.importvalues.DateFormat}
     *
     * @param dateFormat the new value for the dateFormat
     */
    void setDateFormat(int dateFormat);

    /**
     * Getter for property importMode
     * See possible values in {@link org.agnitas.util.importvalues.ImportMode}
     *
     * @return value of property importMode for this ImportProfile
     */
    int getImportMode();

    /**
     * Setter for property importMode
     * See possible values in {@link org.agnitas.util.importvalues.ImportMode}
     *
     * @param importMode the new value for the importMode
     */
    void setImportMode(int importMode);

    /**
     * Getter for property checkForDuplicates
     * See possible values in {@link org.agnitas.util.importvalues.CheckForDuplicates}
     *
     * @return value of property checkForDuplicates for this ImportProfile
     */
    int getCheckForDuplicates();

    /**
     * Setter for property checkForDuplicates
     * See possible values in {@link org.agnitas.util.importvalues.CheckForDuplicates}
     *
     * @param checkForDuplicates the new value for the checkForDuplicates
     */
    void setCheckForDuplicates(int checkForDuplicates);

    /**
     * Getter for property nullValuesAction (action that will be performed to
     * null values)
     * See possible values in {@link org.agnitas.util.importvalues.NullValuesAction}
     *
     * @return value of property nullValuesAction for this ImportProfile
     */
    int getNullValuesAction();

    /**
     * Setter for property nullValuesAction
     * See possible values in {@link org.agnitas.util.importvalues.NullValuesAction}
     *
     * @param nullValuesAction the new value for the nullValuesAction
     */
    void setNullValuesAction(int nullValuesAction);

    /**
     * Getter for property keyColumn (a database-column that will be used when
     * looking for recipient duplicates)
     *
     * @return value of property keyColumn for this ImportProfile
     */
    String getKeyColumn();

    /**
     * Setter for property keyColumn
     *
     * @param keyColumn the new value for the keyColumn
     */
    void setKeyColumn(String keyColumn);

    /**
     * Getter for property extendedEmailCheck (this property indicates if
     * there's a need of extended email check during recipient import process)
     *
     * @return value of property extendedEmailCheck for this ImportProfile
     */
    boolean getExtendedEmailCheck();

    /**
     * Setter for property extendedEmailCheck
     *
     * @param extendedEmailCheck the new value for the extendedEmailCheck
     */
    void setExtendedEmailCheck(boolean extendedEmailCheck);

    /**
     * Gets gender mappings for current ImportProfile (the binding between
     * text-gender and integer-gender)
     *
     * @return gender mappings for current ImportProfile
     */
    Map<String, Integer> getGenderMapping();
    
    /**
     * Get Gendermappings for display purpose
     * 
     * @return
     */
    public Map<String, Integer> getGenderMappingJoined();

    /**
     * Sets gender mappings for current ImportProfile
     *
     * @param genderMapping gender mappings for the current ImportProfile
     */
    void setGenderMapping(Map<String, Integer> genderMapping);

    /**
     * Gets list of column mapping for the current ImportProfile
     *
     * @return column mapping for this ImportProfile
     */
    List<ColumnMapping> getColumnMapping();

    /**
     * Sets the list of column mapping
     *
     * @param columnMapping the new value for the column mapping list
     */
    void setColumnMapping(List<ColumnMapping> columnMapping);

    /**
     * Getter for property mailForReport (the report will be sent to this
     * e-mail when recipient import is finished)
     *
     * @return value of property mailForReport for this ImportProfile
     */
    String getMailForReport();

    /**
     * Setter for property mailForReport
     *
     * @param mailForReport the new value for the mailForReport
     */
    void setMailForReport(String mailForReport);

    /**
     * Getter for property defaultMailType (maill type that will be set to
     * recipients if there is no column mapped to mailtype db column)
     *
     * @return value of property defaultMailType for this ImportProfile
     */
    int getDefaultMailType();

    /**
     * Setter for property defaultMailType
     *
     * @param defaultMailType new defaultMailType value
     */
    void setDefaultMailType(int defaultMailType);

    /**
     * Get integer value of geder field in DB
     *
     * @param fieldValue string value from csv file
     * @return integer value of gender
     */
    public Integer getGenderValueByFieldValue(String fieldValue);

    /**
	 * Method that saves gender mappings converting that from sequence
	 * @param stringGenderSequence sequence of gender string values (e.g. "Mr, Sir, Herr")
	 * @param intGender gender number value
	 */
	public boolean addGenderMappingSequence(String stringGenderSequence, int intGender);

    /**
     *
     * @return
     */
    public boolean getUpdateAllDuplicates();

    /**
     * Setter for property updateAllDuplicates
     * @param updateAllDuplicates
     */
    public void setUpdateAllDuplicates(boolean updateAllDuplicates);

	public int getImportId();

	public void setImportId(int importId);

    public List<String> getKeyColumns();

    public void setKeyColumns(List<String> keyColumns);

    public boolean keyColumnsContainsCustomerId();

}