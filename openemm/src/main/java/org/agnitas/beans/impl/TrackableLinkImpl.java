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

import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.agnitas.actions.EmmAction;
import org.agnitas.beans.Company;
import org.agnitas.beans.Recipient;
import org.agnitas.beans.TrackableLink;
import org.agnitas.dao.CompanyDao;
import org.agnitas.dao.EmmActionDao;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.RecipientDao;
import org.agnitas.emm.core.commons.uid.ExtensibleUID;
import org.agnitas.emm.core.commons.uid.ExtensibleUIDConstants;
import org.agnitas.emm.core.commons.uid.ExtensibleUIDService;
import org.agnitas.emm.core.commons.uid.builder.impl.exception.RequiredInformationMissingException;
import org.agnitas.emm.core.commons.uid.builder.impl.exception.UIDStringBuilderException;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.CaseInsensitiveMap;
import org.agnitas.util.SafeString;
import org.agnitas.util.TimeoutLRUMap;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

/**
 * 
 * @author Martin Helff
 */
public class TrackableLinkImpl implements TrackableLink {
	private static final long serialVersionUID = 7315623241160185674L;

	private static final transient Logger logger = Logger.getLogger(TrackableLinkImpl.class);

	protected int companyID;
	protected int customerID;
	protected int id;
	protected int mailingID;
	protected int actionID;
	protected String fullUrl = null;

	/** Holds value of property shortname. */
	protected String shortname;

	/** Holds value of property usage. */
	protected int usage;

	private TimeoutLRUMap<String, String> baseUrlCache = new TimeoutLRUMap<String, String>(AgnUtils.getDefaultIntValue("rdir.keys.maxCache"), AgnUtils.getDefaultIntValue("rdir.keys.maxCacheTimeMillis"));

	/** Creates new TrackableLink */
	public TrackableLinkImpl() {
	}

	@Override
	public void setCompanyID(int aid) {
		companyID = aid;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public void setMailingID(int aid) {
		mailingID = aid;
	}

	@Override
	public void setActionID(int aid) {
		actionID = aid;
	}

	@Override
	public void setFullUrl(String url) {
		if (url == null)
			url = "";

		fullUrl = url;
	}

	@Override
	public String getFullUrl() {
		if (fullUrl == null) {
			return "";
		}

		return fullUrl;
	}

	@Override
	public String personalizeLink(int customerID, String orgUID, ApplicationContext con) {
		boolean exitValue = true;
		Matcher aMatch = null;
		Pattern aRegExp = null;
		String newUrl = this.fullUrl;
		int start = 0;
		int end = 0;
		LinkedList<String> allColumnNames = new LinkedList<String>();
		int colNum = -1;

		String tmpString = null;
		boolean includeUID = false;
		boolean includeMailingID = false;
		boolean includeUrlID = false;
		Iterator<String> aIt = null;
		String tmpColname = null;

		this.customerID = customerID;
		try {
			aRegExp = Pattern.compile("##[^#]+##");
			aMatch = aRegExp.matcher(newUrl);
			while (true) {
				if (!aMatch.find(end)) {
					break;
				}
				start = aMatch.start();
				end = aMatch.end();

				if (newUrl.substring(start, end).equalsIgnoreCase("##AGNUID##")) {
					includeUID = true;
					continue;
				}
				if (newUrl.substring(start, end).equalsIgnoreCase("##MAILING_ID##")) {
					includeMailingID = true;
					continue;
				}
				if (newUrl.substring(start, end).equalsIgnoreCase("##URL_ID##")) {
					includeUrlID = true;
					continue;
				}
				colNum++;
				allColumnNames.add(newUrl.substring(start + 2, end - 2).toLowerCase());
			}

		} catch (Exception e) {
			logger.error("personalizeLink", e);
			exitValue = false;
		}

		if (exitValue && colNum >= 0) {
			Recipient cust = (Recipient)con.getBean("Recipient");
			RecipientDao dao = (RecipientDao)con.getBean("RecipientDao");
			cust.setCompanyID(this.companyID);
			cust.setCustomerID(customerID);
			cust.loadCustDBStructure();
			cust.setCustParameters(dao.getCustomerDataFromDb(cust.getCompanyID(), cust.getCustomerID()));

			aIt = allColumnNames.iterator();
			while (aIt.hasNext()) {
				try {
					tmpColname = aIt.next();
					tmpString = cust.getCustParameters(tmpColname);
					if (tmpString == null) {
						tmpString = "";
					}
					newUrl = SafeString.replaceIgnoreCase(newUrl, "##" + tmpColname + "##", URLEncoder.encode(tmpString, "UTF-8"));
					// newUrl=SafeString.replace(newUrl, "##"+tmpColname+"##", tmpString);
				} catch (Exception e) {
					logger.error("personalizeLink", e);
				}
			}
		}

		if (includeUID) {

			try {
				newUrl = SafeString.replaceIgnoreCase(newUrl, "##AGNUID##", URLEncoder.encode(orgUID, "UTF-8"));
				// newUrl=SafeString.replaceIgnoreCase(newUrl, "##AGNUID##", URLEncoder.encode(deepTrackingUID, "UTF-8"));
			} catch (Exception e) {
				logger.error("personalizeLink", e);
			}
		}

		if (includeMailingID) {
			try {
				newUrl = SafeString.replaceIgnoreCase(newUrl, "##MAILING_ID##", URLEncoder.encode(Integer.toString(this.mailingID), "UTF-8"));
			} catch (Exception e) {
				logger.error("personalizeLink", e);
			}
		}

		if (includeUrlID) {
			try {
				newUrl = SafeString.replaceIgnoreCase(newUrl, "##URL_ID##", URLEncoder.encode(Integer.toString(this.id), "UTF-8"));
			} catch (Exception e) {
				logger.error("personalizeLink", e);
			}
		}

		return newUrl;
	}

	@Override
	public boolean performLinkAction(Map<String, Object> params, int customerID, ApplicationContext con) {
		boolean exitValue = true;
		EmmAction aAction = null;
		EmmActionDao actionDao = (EmmActionDao)con.getBean("EmmActionDao");

		if (actionID == 0) {
			return exitValue;
		}

		aAction = actionDao.getEmmAction(this.actionID, this.companyID);

		if (params == null) {
			params = new CaseInsensitiveMap<Object>();
		}
		params.put("customerID", new Integer(customerID));
		params.put("mailingID", new Integer(this.mailingID));

		exitValue = aAction.executeActions(con, params);
		return exitValue;
	}

	@Override
	public String encodeTagStringLinkTracking(ApplicationContext con, int custID) {
		String tag = "";
		String baseUrl = null;
		CompanyDao cDao = (CompanyDao)con.getBean("CompanyDao");
		Company company = cDao.getCompany(this.companyID);

		if (baseUrlCache != null) {
			baseUrl = baseUrlCache.get(Long.toString(this.mailingID));
		}

		if (baseUrl == null) {
			try {

				// 1. ? select ml.RDIR_DOMAIN FROM MAILINGLIST_TBL ml JOIN MAILING_TBL m ON ( ml.MAILINGLIST_ID = m.MAILINGLIST_ID) WHERE m.MAILING_ID=36501;
				// 2. ? select RDIR_DOMAIN FROM COMPANY_TBL where company_id=30;
				if (AgnUtils.isOracleDB()) {
					MailingDao dao = (MailingDao)con.getBean("MailingDao");
					baseUrl = dao.getAutoURL(this.mailingID, this.companyID) + "/r.html?";
				}
				if (baseUrl == null) {
					// TODO: extract to emm.properties
					baseUrl = company.getRdirDomain() + "/r.html?";
				}
				if (baseUrlCache != null) {
					baseUrlCache.put(Long.toString(mailingID), baseUrl);
				}
			} catch (Exception e) {
				logger.error("encodeTagStringLinkTracking", e);
				tag = null;
			}
		}

		if (tag != null) {
			try {
				tag = "uid=" + makeUIDString(customerID, con);
			} catch (Exception e) {
				logger.error("Exception in UID", e);
			}
		}
		return baseUrl + tag;
	}

	private String makeUIDString(int customerID, ApplicationContext con) {
		ExtensibleUIDService service = (ExtensibleUIDService)con.getBean(ExtensibleUIDConstants.SERVICE_BEAN_NAME);

		ExtensibleUID uid = service.newUID();
		uid.setCompanyID(this.companyID);
		uid.setCustomerID(customerID);
		uid.setMailingID(this.mailingID);
		uid.setUrlID(this.id);

		try {
			return service.buildUIDString(uid);
		} catch (UIDStringBuilderException e) {
			logger.error("makeUIDString", e);
			return "";
		} catch (RequiredInformationMissingException e) {
			logger.error("makeUIDString", e);
			return "";
		}
	}

	/**
	 * Getter for property shortname.
	 * 
	 * @return Value of property shortname.
	 */
	@Override
	public String getShortname() {
		return this.shortname;
	}

	/**
	 * Setter for property shortname.
	 * 
	 * @param shortname
	 *            New value of property shortname.
	 */
	@Override
	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	/**
	 * Getter for property usage.
	 * 
	 * @return Value of property usage.
	 */
	@Override
	public int getUsage() {
		return this.usage;
	}

	/**
	 * Setter for property usage.
	 * 
	 * @param usage
	 *            New value of property usage.
	 */
	@Override
	public void setUsage(int usage) {
		this.usage = usage;
	}

	/**
	 * Getter for property urlID.
	 * 
	 * @return Value of property urlID.
	 */
	@Override
	public int getId() {
		return this.id;
	}

	/**
	 * Getter for property actionID.
	 * 
	 * @return Value of property actionID.
	 */
	@Override
	public int getActionID() {
		return this.actionID;
	}

	/**
	 * Getter for property companyID.
	 * 
	 * @return Value of property companyID.
	 */
	@Override
	public int getCompanyID() {
		return this.companyID;
	}

	/**
	 * Getter for property mailingID.
	 * 
	 * @return Value of property mailingID.
	 */
	@Override
	public int getMailingID() {
		return this.mailingID;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) // According to Object.equals(Object), equals(null) returns false
			return false;

		return ((TrackableLink)obj).hashCode() == this.hashCode();
	}

	@Override
	public int hashCode() {
		return getFullUrl().hashCode();
	}

	@Override
	public boolean addDeepTrackingParameters(ApplicationContext con) {
		// not implemented
		return false;
	}

	@Override
	public String encodeTagStringDeepTracking(ApplicationContext con) {
		// not implemented
		return null;
	}

	@Override
	public int getDeepTracking() {
		// not implemented
		return 0;
	}

	@Override
	public String getDeepTrackingSession() {
		// not implemented
		return null;
	}

	@Override
	public String getDeepTrackingUID() {
		// not implemented
		return null;
	}

	@Override
	public String getDeepTrackingUrl() {
		// not implemented
		return null;
	}

	@Override
	public int getRelevance() {
		// not implemented
		return 0;
	}

	@Override
	public void setDeepTracking(int deepTracking) {
		// not implemented

	}

	@Override
	public void setRelevance(int relevance) {
		// not implemented

	}

	@Override
	public void setAdminLink(boolean adminLink) {
		// not implemented
	}

	@Override
	public boolean isAdminLink() {
		// not implemented
		return false;
	}

}
