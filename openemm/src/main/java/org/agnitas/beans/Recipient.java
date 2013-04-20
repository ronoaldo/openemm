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

import java.util.Map;

import org.agnitas.util.CaseInsensitiveMap;

/**
 *
 * @author mhe
 */
public interface Recipient {
	public final static int MAILTYPE_TEXT = 0;
	public final static int MAILTYPE_HTML = 1;
	public final static int MAILTYPE_HTML_OFFLINE = 2;
    public final static int MAILTYPE_MHTML = 4;
	
	public final static int GENDER_FEMALE = 1; 
	public final static int GENDER_MALE = 0;
	public final static int GENDER_UNKNOWN = 2;
	
	public int getGender();
	public String getFirstname();
	public String getLastname();
	public String getEmail();
	
	/**
     * Checks if E-Mail-Adress given in customerData-Map is valid.
     *
     * @return true if E-Mail-Adress is valid
     */
	public boolean emailValid();

    /**
     * Checks if E-Mail-Adress given in customerData-Map is registered in blacklist(s)
     *
     * @return true if E-Mail-Adress is blacklisted
     */
	public boolean blacklistCheck();

    /**
     * Find Subscriber by providing a column-name and a value. Only exact machtes possible.
     *
     * @return customerID or 0 if no matching record found
     * @param col Column-Name
     * @param value Value to search for in col
     */
	public int findByKeyColumn(String col, String value);

    /**
     * Find Subscriber by providing a column-name and a value. Only exact machtes possible.
     *
     * @return customerID or 0 if no matching record found
     * @param col Column-Name
     * @param value Value to search for in col
     */
	public int findByColumn(String col, String value);

    /**
     * Find Subscriber by providing a username and password. Only exact machtes possible.
     *
     * @return customerID or 0 if no matching record found
     * @param userCol Column-Name for Username
     * @param userValue Value for Username
     * @param passCol Column-Name for Password
     * @param passValue Value for Password
     */
	public int findByUserPassword(String userCol, String userValue, String passCol, String passValue);

    /**
     * Getter for property companyID.
     *
     * @return Value of property companyID.
     */
	public int getCompanyID();

    /**
     * Getter for property custDBStructure.
     *
     * @return Value of property custDBStructure.
     */
	public Map<String, String> getCustDBStructure();

    /**
     * Getter for property custParameters.
     *
     * @return Value of property custParameters.
     */
	public Map<String, Object> getCustParameters();

    /**
     * Indexed getter for property custParameters.
     *
     * @return Value of the property at <CODE>key</CODE>.
     * @param key Name of Database-Field
     */
	public String getCustParameters(String key);

    /**
     * Load complete Subscriber-Data from DB. customerID must be set first for this method.
     *
     * @return Map with Key/Value-Pairs of customer data
     */
	public Map<String, Object> getCustomerDataFromDb();

    /**
     * Delete complete Subscriber-Data from DB. customerID must be set first for this method.
     */
	public void deleteCustomerDataFromDb();

    /**
     * Getter for property customerID.
     *
     * @return Value of property customerID.
     */
	public int getCustomerID();

    /**
     * Getter for property listBindings.
     *
     * @return Value of property listBindings.
     */
	public Map<Integer, Map<Integer, BindingEntry>> getListBindings();

    /**
     * Updates customer data by analyzing given HTTP-Request-Parameters.
     *
     * @return true on success
     * @param suffix Suffix appended to Database-Column-Names when searching for corresponding request parameters
     * @param req Map containing all HTTP-Request-Parameters as key-value-pair.
     */
	public boolean importRequestParameters(Map<String, Object> requestParameters, String suffix);

    /**
     * Inserts new customer record in Database with a fresh customer-id.
     *
     * @return true on success
     */
	public int insertNewCust();

    /**
     * Iterates through already loaded Mailinglist-Informations and checks if subscriber is active on at least one mailinglist.
     *
     * @return true if subscriber is active on a mailinglist
     */
	public boolean isActiveSubscriber();

    /**
     * Loads complete Mailinglist-Binding-Information for given customer-id from Database.
     *
     * @return Map with key/value-pairs as combinations of mailinglist-id and BindingEntry-Objects
     */
	public Map<Integer, Map<Integer, BindingEntry>> loadAllListBindings();

    /**
     * Load structure of Customer-Table for the given Company-ID in member variable "companyID".
     * Has to be done before working with customer-data in class instance.
     *
     * @return true on success
     */
	public boolean loadCustDBStructure();

    /**
     * resets internal customer-parameter hashmap.
     */
	public void resetCustParameters();

    /**
     * Setter for property companyID.
     *
     * @param companyID New value of property companyID.
     */
	public void setCompanyID(int companyID);

    /**
     * Setter for property custDBStructure.
     *
     * @param custDBStructure New value of property custDBStructure.
     */
	public void setCustDBStructure(Map<String, String> custDBStructure);

    /**
     * Setter for property custParameters.
     *
     * @param custParameters New value of property custParameters.
     */
	public void setCustParameters(Map<String, Object> custParameters);

    /**
     * Indexed setter for property custParameters.
     *
     * @param aKey identifies field in customer-record, must be the same like in Database
     * @param custParameters New value of the property at <CODE>aKey</CODE>.
     */
	public void setCustParameters(String aKey, String custParameters);

    /**
     * Setter for property customerID.
     *
     * @param customerID New value of property customerID.
     */
	public void setCustomerID(int customerID);

    /**
     * Setter for property listBindings.
     *
     * @param listBindings New value of property listBindings.
     */
	public void setListBindings(Map<Integer, Map<Integer, BindingEntry>> listBindings);

    /**
     * Updates internal Datastructure for Mailinglist-Bindings of this customer
     * by analyzing HTTP-Request-Parameters.
     *
     * @param params Map containing all HTTP-Request-Parameters as
     *               key-value-pair.
     * @param doubleOptIn true means use Double-Opt-In
     * @param tafWriteBack if true, eventually existent TAF-Information will be
     *                      written back to source-customer
     * @param remoteAddr IP-address of the client when subscribing.
     * @return true on success
     */
	public boolean updateBindingsFromRequest(Map<String, Object> params, boolean doubleOptIn, boolean tafWriteBack, String remoteAddr);

	public boolean updateBindingsFromRequest(Map<String, Object> params, boolean doubleOptIn, boolean tafWriteBack);

    /**
     * Updates Customer in DB. customerID must be set to a valid id, customer-data is taken from this.customerData.
     *
     * @return true on success
     */
	public boolean updateInDB();

	public Map<Integer, Map<Integer, BindingEntry>> getAllMailingLists();
    
	public CaseInsensitiveMap<ProfileField> getCustDBProfileStructure();
    
	public boolean isChangeFlag();
    
	public void setChangeFlag(boolean changeFlag);
}
