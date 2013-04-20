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

package org.agnitas.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.agnitas.beans.BindingEntry;
import org.agnitas.beans.CustomerImportStatus;
import org.agnitas.beans.Recipient;
import org.agnitas.util.CaseInsensitiveMap;
import org.agnitas.util.CsvColInfo;
import org.displaytag.pagination.PaginatedList;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Nicole Serek
 */
public interface RecipientDao extends ApplicationContextAware {

    /**
     * Check whether it is allowed to add the given number of recipients.
     * The maximum number of recipients/company is defined in
     * emm.properties with recipient.maxRows.
     *
     * @param companyID The id of the company to check.
     * @param count the number of recipients that should be added.
     * @return true if it is allowed to add the given number of recipients.
     */
    public boolean mayAdd(int companyID, int count);

    /**
     * Check whether the number of recipients is not critical after adding
     * the given number of recipients.
     *
     * @param companyID The id of the company to check.
     * @param count the number of recipients that should be added.
     * @return true if it is allowed to add the given number of recipients.
     */
    public boolean isNearLimit(int companyID, int count);

    /**
     * Inserts new customer record in Database with a fresh customer-id
     *
     * @return true on success
     */
	public int insertNewCust(Recipient cust);
	
	/**
     * Updates Customer in DB. customerID must be set to a valid id, customer-data is taken from this.customerData
     *
     * @return true on success
     */
	public boolean updateInDB(Recipient cust);
	
	/**
     * Find Subscriber by providing a column-name and a value. Only exact machtes possible.
     *
     * @param col Column name
     * @param value Value to search for in col
     * @return customerID or 0 if no matching record found
     */
    public int findByKeyColumn(Recipient cust, String col, String value);

    /**
     * Find Subscriber by providing the id of the company, a column-name and a value.
     *
      * @param companyID The id of the company
     * @param col Column name
     * @param value Value to search for in col
     * @return customerID or 0 if no matching record found
     */
    public int findByColumn(int companyID, String col, String value);

    /**
     * Find Subscriber by providing a username and password. Only exact machtes possible.
     *
     * @param companyID The id of the company
     * @param userCol Column name for Username
     * @param userValue Value for Username
     * @param passCol Column name for Password
     * @param passValue Value for Password
     * @return customerID or 0 if no matching record found
     */
    public int findByUserPassword(int companyID, String userCol, String userValue, String passCol, String passValue);
    
    /**
     * Load complete Subscriber data from DB. customerID must be set first for this method.
     *
     * @param companyID The id of the company
     * @param customerID The id of the customer
     * @return Map with Key/Value-Pairs of customer data
     */
    public CaseInsensitiveMap<Object> getCustomerDataFromDb(int companyID, int customerID);

    /**
     * Delete complete Subscriber-Data from DB. customerID must be set first for this method.
     *
     * @param companyID The id of the company
     * @param customerID The id of the customer
     */
    public void deleteCustomerDataFromDb(int companyID, int customerID);
    
    /**
     * Loads complete Mailinglist-Binding-Information for given customer-id from Database
     *
     * @param companyID The id of the company
     * @param customerID The id of the customer
     * @return Map with key/value-pairs as combinations of mailinglist-id and BindingEntry-Objects
     */
    public Map<Integer, Map<Integer, BindingEntry>> loadAllListBindings(int companyID, int customerID);
    
    /**
     * Checks if E-Mail-Adress given in customerData-HashMap is registered in blacklist(s)
     *
     * @param email The address
     * @param companyID The id of the company
     * @return true if E-Mail-Adress is blacklisted
     */
    public boolean blacklistCheck(String email, int companyID);

    /**
     * Loads value of given column for given recipient
     *
     * @param selectVal The name of column in database
     * @param recipientID The id of the recipient
     * @param companyID The id of the company
     * @return String value of column named selectVal or empty String if there is not value
     */
    public String getField(String selectVal, int recipientID, int companyID);

    /**
     * Loads all mailing lists for given customer
     *
     * @param customerID The id of the customer
     * @param companyID The id of the company
     * @return Map with key/value-pairs as combinations of mailinglist-id and map with key/value-pairs as combinations of mediatype and BindingEntry-Objects
     */
    public Map<Integer, Map<Integer, BindingEntry>>	getAllMailingLists(int customerID, int companyID);

    /**
     * Create an empty temporary table for the given customer.
     * The table can then be used for import operations.
     *
     * @param companyID the id of the company.
     * @param datasourceID the unique id for the import operation.
     * @param status the object of the column that should be use as unique key.
     * @return true on success.
     */
    public boolean createImportTables(int companyID, int datasourceID, CustomerImportStatus status);

	/**
     * Delete a temporary table that was created with createImportTables.
     *
     * @param companyID the id of the company.
     * @param datasourceID the unique id for the import operation.
     * @return true on success.
     */
    public boolean deleteImportTables(int companyID, int datasourceID);

    /**
     * Writes new Subscriber-Data through temporary tables to DB
     *
     * @param aForm InputForm for actual import process
     * @param jdbc valid JdbcTemplate to build temporary tables on
     * @param req The HttpServletRequest that caused this action
     */

    //void writeContent( int companyID,int datasourceID,int importMode, CustomerImportStatus status, String csvFileName, List<CsvColInfo> csvAllColumns, Map<String,CsvColInfo>  columnMapping, LinkedList parsedContent, Vector mailingLists, int linesOK );

    /**
     * Load number of recipients for given condition
     *
     * @param companyID the id of the company.
     * @param target the condition for select to database
     * @return number of recipients or 0 if error
     */
    public int sumOfRecipients(int companyID, String target);

    /**
     * Delete recipients from database for given condition
     *
     * @param companyID the id of the company
     * @param target the condition for delete to database
     * @return true if success or false if error
     */
    public boolean deleteRecipients(int companyID, String target);

    /**
     * Delete recipients from database for given list of customer id
     *
     * @param companyID the id of the company
     * @param list the list of customer id
     */
    public void deleteRecipients(int companyID, List<Integer> list);

    /**
     * Select's only a certain page of recipients with all available fields, used for dynamic paging in list views
     *
     * @param columns set of columns are to be selected
     * @param sqlStatementForCount the sql statement which delivers the number of possible rows
     * @param sqlStatementForRows  the page of the result set
     * @param sort column which is the sort criterion
     * @param direction  asc / desc
     * @param page the page
     * @param rownums number of rows a page has
     * @param previousFullListSize size of previous list
     * @return a list of recipients represented with PaginatedList
     */
	public PaginatedList getRecipientList(Set<String>columns,String sqlStatementForCount, String sqlStatementForRows, String sort, String direction , int page, int rownums, int previousFullListSize)  throws IllegalAccessException, InstantiationException;

    /**
     *  Select's only a certain page of recipients with all available fields, used for dynamic paging in list views
     *
     * @param columns set of columns are to be selected
     * @param sqlStatementForCount the sql statement which delivers the number of possible rows
     * @param sqlParametersForCount parameter for number of rows
     * @param sqlStatementForRows the page of the result set
     * @param sqlParametersForRows parameter for temp list of rows
     * @param sort column which is the sort criterion
     * @param direction asc / desc
     * @param page the page
     * @param rownums number of rows a page has
     * @param previousFullListSize size of previous list
     * @return a list of recipients represented with PaginatedList
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
	public PaginatedList getRecipientList(Set<String>columns, String sqlStatementForCount, Object[] sqlParametersForCount, String sqlStatementForRows, Object[] sqlParametersForRows, String sort, String direction , int page, int rownums, int previousFullListSize)  throws IllegalAccessException, InstantiationException;

    /**
     * Loads meta information for all columns from database for given customer
     *
     * @param companyID the id of the company
     * @return  Map with key/value-pairs as combinations of column name and CsvColInfo Objects
     */
	public CaseInsensitiveMap<CsvColInfo> readDBColumns(int companyID);

    /**
     * Load set of emails of customers from blacklist for given company
     *
     * @param companyID the id of the company
     * @return set of emails of customers from blacklist
     * @throws Exception
     */
	public Set<String> loadBlackList(int companyID) throws Exception;
	
	//public void writeParsedConImportWizardServiceImplerviceImpl importWizardHelper, int errorsOnInsert, String customer_body, ArrayList usedColumns, int numFields);
	
    /**
     * Method gets a list of test/admin recipients for preview drop-down list
     *
     * @param companyId the id of company
     * @param mailingId id of mailing
     * @return Map in a format "recipient id" -> "recipient description (name, lastname, email)"
     */
	public Map<Integer, String> getAdminAndTestRecipientsDescription(int companyId, int mailingId);

    /**
     * Loads the list of bounced recipients for given mailing
     *
     * @param companyId the id of company
     * @param mailingId the id of mailing
     * @return the list of  Recipient objects
     */
    public List<Recipient> getBouncedMailingRecipients(int companyId, int mailingId);

    /**
     * Loads the id of new customer
     *
     * @param companyID the id of company
     * @return id of new customer
     */
    public int getNewCustomerID(int companyID);

    /**
     * Check of existence of customer in database for given id
     *
     * @param companyId the id of company
     * @param customerId the id of customer
     * @return true if customer exist or false if not
     */
    public boolean exist(int customerId, int companyId);

    public void deleteAllNoBindings(int companyID, String toBeDeletedTable);

    public String createTmpTableByMailinglistID(int companyID, int mailinglistID);

    public void deleteRecipientsBindings(int mailinglistID, int companyID, boolean activeOnly, boolean noAdminsAndTests);
}
