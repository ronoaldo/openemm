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

package org.agnitas.service;

import org.agnitas.beans.CustomerImportStatus;
import org.agnitas.beans.ImportProfile;
import org.agnitas.beans.ProfileRecipientFields;
import org.agnitas.dao.ImportLoggerDao;
import org.agnitas.dao.ImportProfileDao;
import org.agnitas.dao.ImportRecipientsDao;
import org.agnitas.dao.RecipientDao;
import org.agnitas.service.impl.CSVColumnState;
import org.agnitas.service.impl.ImportWizardContentParseException;
import org.apache.struts.action.ActionMessages;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * @author Viktor Gema
 */
public interface NewImportWizardService extends Serializable {

    public static final int BLOCK_SIZE = 1000;

    public static final String VALIDATOR_RESULT_RESERVED = "VALIDATOR_RESULT_RESERVED";

    public static final String ERROR_EDIT_RECIPIENT_EDIT_RESERVED = "ERROR_EDIT_RECIPIENT_EDIT_RESERVED";

    public static final int RECIPIENT_TYPE_VALID = 1;

    public static final int RECIPIENT_TYPE_FIELD_INVALID = 2;

    public static final int RECIPIENT_TYPE_INVALID = 3;

    public static final int RECIPIENT_TYPE_FIXED_BY_HAND = 4;

    public static final int RECIPIENT_TYPE_DUPLICATE_RECIPIENT = 5;

    public static final int RECIPIENT_TYPE_DUPLICATE_IN_NEW_DATA_RECIPIENT = 6;

    public static final int RESULT_TYPE = 7;

    public static final String MAILTYPE_KEY = "mailtype";

    public static final String GENDER_KEY = "gender";

    public static final String PARSE_ERRORS = "parseErrors";

    public static final String DATE_ERROR = "date";

    public static final String EMAIL_ERROR = "email";

    public static final String EMAILDOUBLE_ERROR = "emailDouble";

    public static final String GENDER_ERROR = GENDER_KEY;

    public static final String MAILTYPE_ERROR = MAILTYPE_KEY;

    public static final String NUMERIC_ERROR = "numeric";

    public static final String STRUCTURE_ERROR = "structure";

    public static final String BLACKLIST_ERROR = "blacklist";

    public static final String DBINSERT_ERROR = "dbinsert";

    public static final int MAX_GENDER_VALUE_BASIC = 2;

    public static final int MAX_GENDER_VALUE_EXTENDED = 5;

    /**
     * Start parse CSV file
     *
     * @throws Exception
     */
    public void doParse() throws Exception;

    /**
     * validate beans after change in error edit page or validate beans after parse CSV file
     *
     * @param validateBeansChangedByHand trigger what exactly need validate
     * @throws Exception
     */
    public void doValidate(Boolean validateBeansChangedByHand) throws Exception;

    /**
     * validate import profile match given csv file
     *
     * @throws ImportWizardContentParseException
     *
     * @throws IOException
     */
    public void validateImportProfileMatchGivenCVSFile() throws ImportWizardContentParseException, IOException;

    /**
     * Setter for property csv file input stream
     *
     * @param fileData New value of property fileData.
     */
    public abstract void setInputFile(File inputFile);

    /**
     * Getter for property profile
     *
     * @return Value of property profile
     */
    public Integer getProfileId();

    /**
     * Setterfor property profile
     *
     * @param profile new vlue of property profile
     */
    public void setProfileId(Integer profile);

    /**
     * Getter for property status
     *
     * @return Value of property status
     */
    public CustomerImportStatus getStatus();

    /**
     * Setter for property Status
     *
     * @param status new vlue of property status
     */
    public void setStatus(CustomerImportStatus status);

    /**
     * Getter for property importRecipientsDao
     *
     * @return
     */
    public ImportRecipientsDao getImportRecipientsDao();

    /**
     * Setter for property importRecipientsDao
     *
     * @param importRecipientsDao
     */
    public void setImportRecipientsDao(ImportRecipientsDao importRecipientsDao);

    /**
     * Getter for property importProfilerDao
     *
     * @return
     */
    public ImportProfileDao getImportProfileDao();

    /**
     * Setter for property importProfilerDao
     *
     * @param importProfileDao
     */
    public void setImportProfileDao(ImportProfileDao importProfileDao);

    /**
     * @return
     * @throws Exception
     * @param errors
     */
    public LinkedList<LinkedList<String>> getPreviewParsedContent(ActionMessages errors) throws Exception;

    /**
     * Getter for Admin Id
     *
     * @return
     */
    public Integer getAdminId();

    /**
     * Setter for Admin Id
     *
     * @param adminId
     */
    public void setAdminId(Integer adminId);

    public ImportProfile getImportProfile();

    /**
     * Getter description of all columns from csv file which are imported
     *
     * @return return array of column description
     */
    public CSVColumnState[] getColumns();

    /**
     * Setter description of all columns from csv file which are imported
     *
     * @param columns
     */
    public void setColumns(CSVColumnState[] columns);

    /**
     * return true if present terrors for eror edit page
     *
     * @return
     */
    public boolean isPresentErrorForErrorEditPage();

    /**
     * Getter for  Beans chaged by hand on error Edit page
     *
     * @return
     */
    public List<ProfileRecipientFields> getBeansAfterEditOnErrorEditPage();

    /**
     * Seter for  Beans chaged by hand on error Edit page
     *
     * @param beansAfterEditOnErrorEditPage
     */
    public void setBeansAfterEditOnErrorEditPage(List<ProfileRecipientFields> beansAfterEditOnErrorEditPage);

    /**
     * Setter for property  ImportLoggerDao
     *
     * @param importLoggerDao
     */
    void setImportLoggerDao(ImportLoggerDao importLoggerDao);

    void log(int datasourceId, int lines, String statistics);

	/**
	 * Setter for recipient DAO
	 * @param recipientDao recipient DAO
	 */
	public void setRecipientDao(RecipientDao recipientDao);

	/**
	 * Getter for recipient DAO
	 * @return recipient DAO
	 */
	public RecipientDao getRecipientDao();

	/**
	 * @return true if recipient-limit will be overfilled after the importing of
	 * recipients from CSV file, false if not
	 */
	public boolean isRecipientLimitReached();

	/**
	 * @return true if there are too many recipients in CSV file (more than allowed by limit), false if not
	 */
	public boolean isImportLimitReached();

	/**
	 * @return true if recipient-limit will soon be reached (more than 90%
	 * of max allowed recipients will be in DB), false if not
	 */
    public boolean isNearLimit();

    /**
     * @return how many percent is finished during  importing recipients
     */
    public int getCompletedPercent();

    /**
     * Set how many percent is finished during  importing recipients
     *
     * @param completedPercent
     */
    public void setCompletedPercent(int completedPercent);

    public Integer getCompanyId();

    public void setCompanyId(Integer companyId);

    public void setMaxGenderValue(int maxGenderValue);

}
