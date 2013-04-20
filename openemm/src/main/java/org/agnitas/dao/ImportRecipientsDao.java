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

package org.agnitas.dao;

import org.agnitas.beans.ImportProfile;
import org.agnitas.beans.ProfileRecipientFields;
import org.agnitas.service.impl.CSVColumnState;
import org.agnitas.service.NewImportWizardService;
import org.apache.commons.validator.ValidatorResults;
import org.displaytag.pagination.PaginatedList;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.util.*;

/**
 * @author Viktor Gema
 */
public interface ImportRecipientsDao {

    public static final String TYPE = "type";

    /**
     * store recipient beans in to temporary table
     *
     * @param recipientBeans recipient beans
     * @param adminId        admin id
     * @param profile        selected import prfile
     * @param type           the types of recipients (see NewImportWizardService constants)
     * @param datasourceID   datasource id
     * @param columns        The description of all columns from csv file which are imported
     */
    void createRecipients(Map<ProfileRecipientFields, ValidatorResults> recipientBeans, Integer adminId, ImportProfile profile, Integer type, int datasourceID, CSVColumnState[] columns);

    /**
     * add new valid recipients in to customer table
     *
     * @param recipientBeans recipient beans
     * @param adminId        admin id
     * @param importProfile  selected import profile
     * @param columns        The description of all columns from csv file which are imported
     * @param datasourceID   datasource id
     */
    void addNewRecipients(Map<ProfileRecipientFields, ValidatorResults> recipientBeans, Integer adminId, ImportProfile importProfile, CSVColumnState[] columns, int datasourceID) throws Exception;


    /**
     * Get recipients from temporary table for the given types
     *
     * @param adminId      admin id
     * @param types        the types of recipients (see NewImportWizardService constants)
     * @param datasourceID datasource id
     * @return collection of recipients of given types and adminId
     */
    HashMap<ProfileRecipientFields, ValidatorResults> getRecipientsByType(int adminId, Integer[] types, int datasourceID);

    /**
     * Gets page of recipients from temporary table
     *
     * @param types        types of recipients
     * @param page         page
     * @param rownums      number of rows per page (maximum quantity to give)
     * @param adminID      admin id
     * @param datasourceId datasource Id
     * @return list of recipients
     */
    HashMap<ProfileRecipientFields, ValidatorResults> getRecipientsByTypePaginated(Integer[] types, int page, int rownums, Integer adminID, int datasourceId);

    /**
     * Gets number of recipients in temporary table for certain types
     *
     * @param types        types of recipients
     * @param adminID      admin id
     * @param datasourceId datasourceId
     * @return number of recipients for types
     */
    int getRecipientsCountByType(Integer[] types, Integer adminID, int datasourceId);

    /**
     * Select's only a certain page of invalid recipients, used for paging in list views
     *
     * @param columns              The description of all columns from csv file which are imported
     * @param sort                 column which is the sort criterion
     * @param direction            asc / desc
     * @param page                 the page
     * @param rownums              number of rows a page has
     * @param previousFullListSize
     * @param adminId              admin id
     * @param datasourceID         datasource id
     * @return a list of recipients
     * @throws Exception
     */
    PaginatedList getInvalidRecipientList(CSVColumnState[] columns, String sort, String direction, int page, int rownums, int previousFullListSize, Integer adminId, int datasourceID) throws Exception;

    /**
     * update recipients beans in temporary table after revalidation its or so one
     *
     * @param recipientBeans recipient beans
     * @param adminId        admin id
     * @param type           type the types of recipients (see NewImportWizardService constants)
     * @param importProfile  selected import profile
     * @param datasourceID   datasource id
     * @param columns        The description of all columns from csv file which are imported
     */
    void updateRecipients(Map<ProfileRecipientFields, ValidatorResults> recipientBeans, Integer adminId, int type, ImportProfile importProfile, int datasourceID, CSVColumnState[] columns);

    /**
     * update exist valid recipients in customer table
     *
     * @param recipientBeans recipient beans
     * @param importProfile  selected import profile
     * @param columns        The description of all columns from csv file which are imported
     * @param adminId        admin id
     */
    void updateExistRecipients(Collection<ProfileRecipientFields> recipientBeans, ImportProfile importProfile, CSVColumnState[] columns, Integer adminId);

    /**
     * get duplicate recipients by key column from customer table
     *
     * @param recipientBeans recipient beans
     * @param importProfile  selected import profile
     * @param columns        The description of all columns from csv file which are imported
     * @return
     */
    HashMap<ProfileRecipientFields, ValidatorResults> getDuplicateRecipientsFromExistData(Map<ProfileRecipientFields, ValidatorResults> recipientBeans, ImportProfile importProfile, CSVColumnState[] columns);

    /**
     * get duplicate recipients by key column from temporary table
     *
     * @param recipientBeans recipient beans
     * @param profile        selected import profile
     * @param columns        The description of all columns from csv file which are imported
     * @param adminId        admin id
     * @param datasourceID   datasource id
     * @return
     */
    HashMap<ProfileRecipientFields, ValidatorResults> getDuplicateRecipientsFromNewDataOnly(Map<ProfileRecipientFields, ValidatorResults> recipientBeans, ImportProfile profile, CSVColumnState[] columns, Integer adminId, int datasourceID);

    /**
     * Get information about database columns.
     * The information will be gathered from the DatabaseMetaData of the table
     * and the infoTable. The give infoTable should be the name of a table
     * which holds the following fields:
     * <dl>
     * <dt>col_name
     * <dd>Primary key to identify the column
     * <dt>shortname
     * <dd>Textfield for a short descriptive name of the column
     * <dt>default_value
     * <dd>the value which should be used for the column, when no other is given.
     * </dl>
     * The resulting list contains one row for each found column.
     * Each row is a Map consists of:
     * <dl>
     * <dt>column
     * <dd>the name of the column in Database
     * <dt>type
     * <dd>the typename as in java.sql.Types
     * (eg. VARCHAR for a java.sql.Types.VARCHAR
     * <dt>length
     * <dd>the size of the column as in DatabaseMetaData.getColumns()
     * <dt>nullable
     * <dd>inidcates whather the column can contain NULL values (1) or not (0).
     * <dt>shortname (optional)
     * <dd>a descriptive name for the column
     * <dt>default (optional)
     * <dd>value that should be used, when no value is given.
     * <dt>description (optional)
     * <dd>descriptive text for the column
     * </ul>
     *
     * @param companyId company id
     * @param column    The description of all columns from csv file which are imported
     * @return TreeMap containing column informations
     */
    LinkedHashMap<String, Map<String, Object>> getColumnInfoByColumnName(int companyId, String column);

    /**
     * import valid recipients in to black list
     *
     * @param recipientBeans recipient beans
     * @param companyId      company id
     */
    void importInToBlackList(Collection<ProfileRecipientFields> recipientBeans, int companyId);

    /**
     * load list of email which are containing in black list
     *
     * @param companyID company
     * @return return list of email which are containing in black list
     * @throws Exception
     */
    Set<String> loadBlackList(int companyID) throws Exception;

    /**
     * Create an empty temporary table for the given recipients.
     * The table can then be used for import operations.
     *
     * @param adminID       admin id
     * @param datasource_id datasource id
     * @param keyColumn     key column
     * @param companyId     company id
     * @param sessionId
     */
    void createTemporaryTable(int adminID, int datasource_id, String keyColumn, List<String> keyColumns, int companyId, String sessionId);

    void createTemporaryTable(int adminID, int datasource_id, String keyColumn, int companyId, String sessionId);

    /**
     * set single connection for work with temporary table
     *
     * @param singleConnection
     */
    public void setTemporaryConnection(SingleConnectionDataSource singleConnection);

    /**
     * get single connection for temporary table
     * @return
     */
    public SingleConnectionDataSource getTemporaryConnection();

    /**
     * Assigns imported recipients to mailing lists
     *
     * @param mailingLists mailing lists to assign
     * @param companyId    company id
     * @param datasourceId datasource id of imported recipients
     * @param mode         recipients import mode
     * @param adminId      admin id
     * @param importWizardHelper
     * @return assign statistics (mailinglist id -> recipients assigned)
     */
    Map<Integer, Integer> assiggnToMailingLists(List<Integer> mailingLists, int companyId, int datasourceId, int mode, int adminId, NewImportWizardService importWizardHelper);

	/**
	 * remove temporary table by table name
	 *
	 * @param tablename temporary table name
	 * @param sessionId
	 */
	void removeTemporaryTable(String tablename, String sessionId);

	/**
	 * get list of table names whuch are still doesn't remove
	 *
	 * @param sessionId
	 * @return list of tables name
	 */
	List<String> getTemporaryTableNamesBySessionId(String sessionId);
}
