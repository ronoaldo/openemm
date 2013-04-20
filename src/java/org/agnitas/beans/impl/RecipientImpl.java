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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.agnitas.beans.BindingEntry;
import org.agnitas.beans.ProfileField;
import org.agnitas.beans.Recipient;
import org.agnitas.beans.factory.BindingEntryFactory;
import org.agnitas.beans.factory.RecipientFactory;
import org.agnitas.dao.RecipientDao;
import org.agnitas.service.ColumnInfoService;
import org.agnitas.util.CaseInsensitiveMap;
import org.apache.commons.lang.StringUtils;

/**
 * Handles all kind of operations to be done with subscriber-data. Requires that
 * a valid companyID is set after creating a new instance.
 * 
 * @author mhe
 */
public class RecipientImpl implements Recipient {
	protected ColumnInfoService columnInfoService;
	protected RecipientDao recipientDao;
	protected BindingEntryFactory bindingEntryFactory;
	protected RecipientFactory recipientFactory;

	protected int companyID;
	protected int customerID;
	protected Map<Integer, Map<Integer, BindingEntry>> listBindings;
	protected Map<String, String> custDBStructure;
	protected CaseInsensitiveMap<ProfileField> custDBProfileStructure;
	protected CaseInsensitiveMap<Object> custParameters = new CaseInsensitiveMap<Object>();
	protected boolean changeFlag = false;
	
	// ----------------------------------------------------------------------------------------------------------------
	// Dependency Injection
	
	public void setRecipientDao(RecipientDao recipientDao) {
		this.recipientDao = recipientDao;
	}

	public void setColumnInfoService(ColumnInfoService columnInfoService) {
		this.columnInfoService = columnInfoService;
	}

	public void setBindingEntryFactory(BindingEntryFactory bindingEntryFactory) {
		this.bindingEntryFactory = bindingEntryFactory;
	}

	public void setRecipientFactory(RecipientFactory recipientFactory) {
		this.recipientFactory = recipientFactory;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Business Logic
	
	@Override
	public boolean blacklistCheck() {
		return recipientDao.blacklistCheck(((String) getCustParameters().get("email")).toLowerCase().trim(),getCompanyID());
	}

	@Override
	public boolean updateInDB() {
		return recipientDao.updateInDB(this);
	}

	@Override
	public int findByColumn(String col, String value) {
		return recipientDao.findByColumn(companyID, col, value);
	}

	@Override
	public int findByKeyColumn(String col, String value) {
		return recipientDao.findByKeyColumn(this, col, value);
	}

	@Override
	public void deleteCustomerDataFromDb() {
		recipientDao.deleteCustomerDataFromDb(companyID, customerID);
	}

	@Override
	public int findByUserPassword(String userCol, String userValue, String passCol, String passValue) {
		return recipientDao.findByUserPassword(companyID, userCol, userValue, passCol, passValue);
	}

	@Override
	public Map<String, Object> getCustomerDataFromDb() {
		custParameters = recipientDao.getCustomerDataFromDb(companyID, customerID);
		return custParameters;
	}

	@Override
	public Map<Integer, Map<Integer, BindingEntry>> loadAllListBindings() {
		listBindings = recipientDao.loadAllListBindings(companyID, customerID);
		return listBindings;
	}

	@Override
	public int insertNewCust() {
		return recipientDao.insertNewCust(this);
	}

	@Override
	public int getCustomerID() {
		return customerID;
	}

	@Override
	public void setCustomerID(int customerID) {
		this.customerID = customerID;
	}

	@Override
	public int getCompanyID() {
		return companyID;
	}

	@Override
	public void setCompanyID(int companyID) {
		this.companyID = companyID;
	}

	@Override
	public Map<Integer, Map<Integer, BindingEntry>> getListBindings() {
		return listBindings;
	}

	@Override
	public void setListBindings(Map<Integer, Map<Integer, BindingEntry>> listBindings) {
		this.listBindings = listBindings;
	}

	@Override
	public Map<String, String> getCustDBStructure() {
		return custDBStructure;
	}

	@Override
	public void setCustDBStructure(Map<String, String> custDBStructure) {
		this.custDBStructure = custDBStructure;
	}

	@Override
	public String getCustParameters(String key) {
		return (String) custParameters.get(key);
	}

	@Override
	public boolean isChangeFlag() {
		return changeFlag;
	}

	@Override
	public void setChangeFlag(boolean changeFlag) {
		this.changeFlag = changeFlag;
	}

	@Override
	public Map<String, Object> getCustParameters() {
		return custParameters;
	}

	@Override
	public void resetCustParameters() {
		custParameters.clear();
	}

	@Override
	public Map<Integer, Map<Integer, BindingEntry>> getAllMailingLists() {
		return recipientDao.getAllMailingLists(customerID, companyID);
	}

	@Override
	public CaseInsensitiveMap<ProfileField> getCustDBProfileStructure() {
		return custDBProfileStructure;
	}

	@Override
	public String getEmail() {
		return (String) custParameters.get("email");
	}

	@Override
	public String getFirstname() {
		return (String) custParameters.get("firstname");
	}

	@Override
	public int getGender() {
		return ((Number) custParameters.get("gender")).intValue();
	}

	@Override
	public String getLastname() {
		return (String) custParameters.get("lastname");
	}

	/**
	 * Load structure of Customer-Table for the given Company-ID in member
	 * variable "companyID". Load profile data into map. Has to be done before
	 * working with customer-data in class instance
	 * 
	 * @return true on success
	 */
	@Override
	public boolean loadCustDBStructure() {
		custDBStructure = new CaseInsensitiveMap<String>();
		custDBProfileStructure = new CaseInsensitiveMap<ProfileField>();

		try {
			for (ProfileField fieldDescription : columnInfoService.getColumnInfos(companyID)) {
				custDBStructure.put(fieldDescription.getColumn(), fieldDescription.getDataType());
				custDBProfileStructure.put(fieldDescription.getColumn(), fieldDescription);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Indexed setter for property custParameters.
	 * 
	 * @param aKey
	 *            identifies field in customer-record, must be the same like in
	 *            Database
	 * @param custParameter
	 *            New value of the property at <CODE>aKey</CODE>.
	 */
	@Override
	public void setCustParameters(String aKey, String custParameter) {
		String key = aKey;
		String aValue = null;

		if (key.toUpperCase().endsWith("_DAY_DATE")) {
			key = key.substring(0, key.length() - "_DAY_DATE".length());
		}
		if (key.toUpperCase().endsWith("_MONTH_DATE")) {
			key = key.substring(0, key.length() - "_MONTH_DATE".length());
		}
		if (key.toUpperCase().endsWith("_YEAR_DATE")) {
			key = key.substring(0, key.length() - "_YEAR_DATE".length());
		}
		if (key.toUpperCase().endsWith("_HOUR_DATE")) {
			key = key.substring(0, key.length() - "_HOUR_DATE".length());
		}
		if (key.toUpperCase().endsWith("_MINUTE_DATE")) {
			key = key.substring(0, key.length() - "_MINUTE_DATE".length());
		}
		if (key.toUpperCase().endsWith("_SECOND_DATE")) {
			key = key.substring(0, key.length() - "_SECOND_DATE".length());
		}

		if (custDBStructure.containsKey(key)) {
			aValue = null;
			if (custParameters.get(aKey) != null) {
				aValue = (String) custParameters.get(aKey);
			}
			if (!StringUtils.equals(custParameter, aValue)) {
				changeFlag = true;
				custParameters.put(aKey, custParameter);
			}
		}
	}

	/**
	 * Setter for property custParameters.
	 * 
	 * @param custParameters
	 *            New value of property custParameters.
	 */
	@Override
	public void setCustParameters(Map<String, Object> custParameters) {
		if (custParameters instanceof CaseInsensitiveMap) {
			this.custParameters = (CaseInsensitiveMap<Object>) custParameters;
		} else {
			this.custParameters = new CaseInsensitiveMap<Object>(custParameters);
		}
		changeFlag = true;
	}

	/**
	 * Check security of a request parameter. Checks the given string for
	 * certain patterns that could be used for exploits.
	 */
	private boolean isSecure(String value) {
		return !value.contains("<");
	}

	/**
	 * Copy a date from reqest to database values.
	 * 
	 * @param req
	 *            a Map of request parameters (name/value pairs).
	 * @param name
	 *            the name of the field to copy.
	 * @param suffix
	 *            a suffix for the parameters in the map.
	 * @return true when the copying was successfull.
	 */
	private boolean copyDate(Map<String, Object> req, String name, String suffix) {
		String[] field = { "_DAY_DATE", "_MONTH_DATE", "_YEAR_DATE", "_HOUR_DATE", "_MINUTE_DATE", "_SECOND_DATE" };
		String s = null;

		name = name.toUpperCase();
		for (int c = 0; c < field.length; c++) {
			if (req.get(name + field[c] + suffix) != null) {
				String fieldname = name + field[c] + suffix;
				Object o = req.get(fieldname);
				s = o.toString();
				setCustParameters(fieldname, s);
			}
		}
		return true;
	}

	/**
	 * Check if the given name is allowed for requests. This is used to ensure
	 * that system columns are not changed by form requests.
	 * 
	 * @param name
	 *            the name to check for allowance.
	 * @return true when field may be writen.
	 */
	private boolean isAllowedName(String name) {
		name = name.toLowerCase();
		if (name.startsWith("agn")) {
			return false;
		}
		if (name.equals("customer_id") || name.equals("change_date")) {
			return false;
		}
		if (name.equals("timestamp") || name.equals("creation_date")) {
			return false;
		}
		return true;
	}

	/**
	 * Updates customer data by analyzing given HTTP-Request-Parameters
	 * 
	 * @return true on success
	 * @param suffix
	 *            Suffix appended to Database-Column-Names when searching for
	 *            corresponding request parameters
	 * @param req
	 *            Map containing all HTTP-Request-Parameters as key-value-pair.
	 */
	@Override
	public boolean importRequestParameters(Map<String, Object> requestParameters, String suffix) {
		CaseInsensitiveMap<Object> caseInsensitiveParameters = new CaseInsensitiveMap<Object>(requestParameters);
		
		if (suffix == null) {
			suffix = "";
		}
		Iterator<String> e = custDBStructure.keySet().iterator();

		while (e.hasNext()) {
			String aName = e.next();
			String name = aName.toUpperCase();

			if (!isAllowedName(aName)) {
				continue;
			}
			String colType = (String) custDBStructure.get(aName);
			if (colType.equalsIgnoreCase("DATE")) {
				copyDate(caseInsensitiveParameters, aName, suffix);
			} else if (caseInsensitiveParameters.get(name + suffix) != null) {
				String aValue = (String) caseInsensitiveParameters.get(name + suffix);
				if (name.equalsIgnoreCase("EMAIL")) {
					if (aValue.length() == 0) {
						aValue = " ";
					}
					aValue = aValue.toLowerCase();
					aValue = aValue.trim();
				} else if (name.length() > 4) {
					if (name.substring(0, 4).equals("SEC_")
							|| name.equals("FIRSTNAME")
							|| name.equals("LASTNAME")) {
						if (!isSecure(aValue)) {
							return false;
						}
					}
				}
				if (name.equalsIgnoreCase("DATASOURCE_ID")) {
					if (getCustParameters(aName) == null) {
						setCustParameters(aName, aValue);
					}
				} else {
					setCustParameters(aName, aValue);
				}
			}
		}
		return true;
	}

	/**
	 * Updates internal Datastructure for Mailinglist-Bindings of this customer
	 * by analyzing HTTP-Request-Parameters
	 * 
	 * @return true on success
	 * @param tafWriteBack
	 *            if true, eventually existent TAF-Information will be written
	 *            back to source-customer
	 * @param params
	 *            Map containing all HTTP-Request-Parameters as key-value-pair.
	 * @param doubleOptIn
	 *            true means use Double-Opt-In
	 */
	@Override
	public boolean updateBindingsFromRequest(Map<String, Object> params, boolean doubleOptIn, boolean tafWriteBack, String remoteAddr) {
		HttpServletRequest request = (HttpServletRequest) params.get("_request");
		@SuppressWarnings("unchecked")
		Map<String, Object> requestParameters = (Map<String, Object>) params.get("requestParameters");
		String postfix = null;
		int mailinglistID = 0;
		int mediatype = 0;
		int subscribeStatus = 0;
		String tmpName = null;
		boolean changeit = false;
		Map<Integer, BindingEntry> mList = null;
		BindingEntry aEntry = null;
		int mailingID = 0;

		try {
			Integer tmpNum = (Integer) params.get("mailingID");
			mailingID = tmpNum.intValue();
		} catch (Exception e) {
			mailingID = 0;
		}

		for (String aName : requestParameters.keySet()) {
			postfix = "";
			if (aName.startsWith("agnSUBSCRIBE")) {
				mediatype = 0;
				mailinglistID = 0;
				subscribeStatus = 0;
				aEntry = null;
				if (aName.length() > "agnSUBSCRIBE".length()) {
					postfix = aName.substring("agnSUBSCRIBE".length());
				}
				try {
					subscribeStatus = Integer.parseInt((String) requestParameters.get(aName));
				} catch (Exception e1) {
					subscribeStatus = 0;
				}

				tmpName = "agnMAILINGLIST" + postfix;
				try {
					mailinglistID = Integer.parseInt((String) requestParameters.get(tmpName));
				} catch (Exception e1) {
					mailinglistID = 0;
				}

				tmpName = "agnMEDIATYPE" + postfix;
				try {
					mediatype = Integer.parseInt((String) requestParameters.get(tmpName));
				} catch (Exception e1) {
					mediatype = 0;
				}

				// find BindingEntry or create new one
				mList = listBindings.get(mailinglistID);
				if (mList != null) {
					aEntry = mList.get(mediatype);
				}

				if (aEntry != null) {
					changeit = false;
					// put changes in db
					int oldStatus = aEntry.getUserStatus();
					switch (oldStatus) {
					case BindingEntry.USER_STATUS_ADMINOUT:
					case BindingEntry.USER_STATUS_BOUNCED:
					case BindingEntry.USER_STATUS_OPTOUT:
						if (subscribeStatus == 1) {
							changeit = true;
						}
						break;

					case BindingEntry.USER_STATUS_WAITING_FOR_CONFIRM:
					case BindingEntry.USER_STATUS_ACTIVE:
						if (subscribeStatus == 0) {
							changeit = true;
						}
						break;
					}
					if (changeit) {
						switch (subscribeStatus) {
						case 0:
							aEntry.setUserStatus(BindingEntry.USER_STATUS_OPTOUT);
							if (mailingID != 0) {
								aEntry.setUserRemark("Opt-Out-Mailing: "
										+ mailingID);
								aEntry.setExitMailingID(mailingID);
							} else {
								aEntry.setUserRemark("User-Opt-Out: "
										+ request.getRemoteAddr());
								aEntry.setExitMailingID(0);
							}
							break;

						case 1:
							if (!doubleOptIn) {
								aEntry.setUserStatus(BindingEntry.USER_STATUS_ACTIVE);
								aEntry.setUserRemark("Opt-In-IP: "
										+ request.getRemoteAddr());
							} else {
								aEntry.setUserStatus(BindingEntry.USER_STATUS_WAITING_FOR_CONFIRM);
								aEntry.setUserRemark("Opt-In-IP: "
										+ request.getRemoteAddr());
							}
							break;
						}
						aEntry.updateStatusInDB(companyID);
					}
				} else {
					if (subscribeStatus == 1) {
						aEntry = bindingEntryFactory.newBindingEntry();
						aEntry.setCustomerID(customerID);
						aEntry.setMediaType(mediatype);
						aEntry.setMailinglistID(mailinglistID);
						aEntry.setUserType(BindingEntry.USER_TYPE_WORLD);
						aEntry.setRemoteAddr(remoteAddr);

						if (!doubleOptIn) {
							aEntry.setUserStatus(BindingEntry.USER_STATUS_ACTIVE);
							aEntry.setUserRemark("Opt-In-IP: "
									+ request.getRemoteAddr());
							if (tafWriteBack) { // only if there was never a
												// binding for adress...
								tellFriendWriteback(); // make
															// taf-writeback to
															// originating
															// customer
							}
						} else {
							aEntry.setUserStatus(BindingEntry.USER_STATUS_WAITING_FOR_CONFIRM);
							aEntry.setUserRemark("Opt-In-IP: "
									+ request.getRemoteAddr());
						}

						aEntry.insertNewBindingInDB(companyID);
						if (mList == null) {
							mList = new HashMap<Integer, BindingEntry>();
							listBindings.put(mailinglistID, mList);
						}
						mList.put(mediatype, aEntry);
					}
				}
			}
		}

		return true;
	}

	@Override
	public boolean updateBindingsFromRequest(Map<String, Object> params, boolean doubleOptIn, boolean tafWriteBack) {
		return updateBindingsFromRequest(params, doubleOptIn, tafWriteBack, null);
	}

	/**
	 * Iterates through already loaded Mailinglist-Informations and checks if
	 * subscriber is active on at least one mailinglist
	 * 
	 * @return true if subscriber is active on a mailinglist
	 */
	@Override
	public boolean isActiveSubscriber() {
		if (listBindings != null) {
			for (Map<Integer, BindingEntry> listBindingItem : listBindings.values()) {
				for (BindingEntry bindingEntry : listBindingItem.values()) {
					if (bindingEntry.getUserStatus() == BindingEntry.USER_STATUS_ACTIVE) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Checks if E-Mail-Adress given in customerData-HashMap is valid
	 * 
	 * @return true if E-Mail-Adress is valid
	 */
	@Override
	public boolean emailValid() {
		String email = (String) custParameters.get("email");

		if (email == null) {
			return false;
		}
		email = email.trim();
		if (email == null) {
			return false;
		}

		if (!Pattern.matches("[^<@]+@[^<@.]+[.][^<@]+", email)) {
			return false;
		}
		return true;
	}

	/**
	 * Writes TAF-Info back to source-customer record for tracking purposes
	 * 
	 * @return always true
	 */
	private boolean tellFriendWriteback() {
		boolean result = true;
		Recipient sourceCust = null;
		int custID = 0;
		int tafNum = 0;
		String tmp = null;
		String custStr = null;

		// add check if fields exist in db-structure!
		if (!custDBStructure.containsKey("AGN_TAF_SOURCE")
				|| !custDBStructure.containsKey("AGN_TAF_NUMBER")
				|| !custDBStructure.containsKey("AGN_TAF_CUSTOMER_IDS")) {
			return true;
		}

		if (getCustParameters("AGN_TAF_SOURCE") != null) {
			try {
				custID = Integer.parseInt((String) getCustParameters("AGN_TAF_SOURCE"));
			} catch (Exception e) {
				custID = 0;
			}
		}

		if (custID != 0) {
			sourceCust = recipientFactory.newRecipient();
			sourceCust.setCompanyID(companyID);
			sourceCust.setCustomerID(custID);
			sourceCust.loadCustDBStructure();
			sourceCust.setCustParameters(recipientDao.getCustomerDataFromDb(companyID, customerID));
			if (sourceCust.getCustParameters("AGN_TAF_CUSTOMER_IDS") != null) {
				tmp = sourceCust.getCustParameters("AGN_TAF_CUSTOMER_IDS");
			} else {
				tmp = "";
			}
			custStr = " " + customerID + ";";
			if (tmp.indexOf(custStr) == -1) {
				tmp = tmp + custStr;
				sourceCust.setCustParameters("AGN_TAF_CUSTOMER_IDS", tmp);

				try {
					tafNum = Integer.parseInt((String) sourceCust
							.getCustParameters("AGN_TAF_NUMBER"));
				} catch (Exception e) {
					tafNum = 0;
				}
				tafNum++;
				sourceCust.setCustParameters("AGN_TAF_NUMBER",
						Integer.toString(tafNum));
				recipientDao.updateInDB(sourceCust);
			}
		}

		return result;
	}
	
	/**
	 * String representation for easier debugging
	 */
	@Override
	public String toString() {
		return 
			"(" + companyID + "/" + customerID + ")" +
			" Lastname: " + (custParameters == null ? "" : custParameters.get("lastname")) +
			" Firstname: " + (custParameters == null ? "" : custParameters.get("firstname")) +
			" Gender: " + (custParameters == null ? "" : custParameters.get("gender")) +
			" Email: " + (custParameters == null ? "" : custParameters.get("email")) +
			" Mailtype: " + (custParameters == null ? "" : custParameters.get("mailtype")) +
			" Bindings: " + (listBindings == null ? 0 : listBindings.size()) + 
			" ChangeFlag: " + changeFlag;
	}
}
