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

package org.agnitas.util;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.agnitas.beans.MaildropEntry;
import org.agnitas.beans.Mailing;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.RecipientDao;
import org.agnitas.emm.core.commons.uid.ExtensibleUID;
import org.agnitas.emm.core.commons.uid.ExtensibleUIDService;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * ScriptHelper for Velocity scripts
 * 
 * @author Martin Helff, Andreas Rehak
 */
public class ScriptHelper {
	private static final transient Logger logger = Logger.getLogger(ScriptHelper.class);
	
	protected ApplicationContext con = null;

	public ScriptHelper(ApplicationContext con) {
		this.con = con;
	}

	public ApplicationContext getApplicationContext() {
		return con;
	}

	private Map<String, String> buildRecipient(NodeList allMessageChilds) throws Exception {
		Map<String, String> result = new HashMap<String, String>();
		Node aNode = null;
		String nodeName = null;
		NodeList recipientNodes = null;
		Node recipientNode = null;
		String recipientNodeName = null;
		NamedNodeMap allAttr = null;

		for (int i = 0; i < allMessageChilds.getLength(); i++) {
			aNode = allMessageChilds.item(i);
			nodeName = aNode.getNodeName();

			if (nodeName.equals("recipient")) {
				recipientNodes = aNode.getChildNodes();
				for (int j = 0; j < recipientNodes.getLength(); j++) {
					recipientNode = recipientNodes.item(j);
					recipientNodeName = recipientNode.getNodeName();
					if (recipientNodeName.equals("gender")
							|| recipientNodeName.equals("firstname")
							|| recipientNodeName.equals("lastname")
							|| recipientNodeName.equals("mailtype")
							|| recipientNodeName.equals("email")) {
						try {
							result.put(recipientNodeName.toUpperCase(), recipientNode.getFirstChild().getNodeValue());
						} catch (Exception e) {
							// do nothing
						}
					}
					if (recipientNodeName.equals("extracol")) {
						allAttr = recipientNode.getAttributes();
						try {
							result.put(allAttr.getNamedItem("name").getNodeValue().toUpperCase(), recipientNode.getFirstChild().getNodeValue());
						} catch (Exception e) {
							// do nothing
						}
					}
				}
			}
			if (nodeName.equals("content")) {
				allAttr = aNode.getAttributes();
				try {
					result.put(allAttr.getNamedItem("name").getNodeValue().toUpperCase(), aNode.getFirstChild().getNodeValue());
				} catch (Exception e) {
					// do nothing
				}
			}
		}

		return result;
	}

	public List<Map<String, Object>> parseTransactionMailXml(String xmlInput) {
		List<Map<String, Object>> result = new LinkedList<Map<String, Object>>();
		boolean validation = false;
		boolean ignoreWhitespace = true;
		boolean ignoreComments = true;
		boolean putCDATAIntoText = true;
		boolean createEntityRefs = false;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		// set the configuration options
		dbf.setValidating(validation);
		dbf.setIgnoringComments(ignoreComments);
		dbf.setIgnoringElementContentWhitespace(ignoreWhitespace);
		dbf.setCoalescing(putCDATAIntoText);
		// The opposite of creating entity ref nodes is expanding them inline
		dbf.setExpandEntityReferences(!createEntityRefs);

		DocumentBuilder db = null;
		Document doc = null;
		try {
			db = dbf.newDocumentBuilder();
			doc = db.parse(new InputSource(new StringReader(xmlInput)));
			Element base = doc.getDocumentElement();
			NodeList allMessages = base.getChildNodes();
			NodeList allMessageChilds = null;
			Node aMessage = null;
			NamedNodeMap allAttr = null;
			String nodeName = null;
			int messageType = 0;
			Map<String, Object> messageEntry = null;

			for (int i = 0; i < allMessages.getLength(); i++) {
				aMessage = allMessages.item(i);
				nodeName = aMessage.getNodeName();

				if (nodeName.equals("message")) {
					messageEntry = new HashMap<String, Object>();
					allAttr = aMessage.getAttributes();
					messageType = Integer.parseInt(allAttr.getNamedItem("type").getNodeValue());
					messageEntry.put("messageType", new Integer(messageType));
					allMessageChilds = aMessage.getChildNodes();
					messageEntry.put("recipient", buildRecipient(allMessageChilds));
					result.add(messageEntry);
				}
			}

		} catch (Exception e) {
			logger.error(AgnUtils.getStackTrace(e));
			result = null;
		}

		return result;
	}

	public boolean sendEmail(String from_adr, String to_adr, String subject, String body_text, String body_html, int mailtype, String charset) {
		return AgnUtils.sendEmail(from_adr, to_adr, subject, body_text, body_html, mailtype, charset);
	}

	public Map<Object, Object> newHashMap() {
		return new HashMap<Object, Object>();
	}

	public void println(String output) {
		logger.error(output);
	}

	/**
	 * Finds the last newsletter that would have been sent to the given
	 * customer. The newsletter also gets a new entry maildrop_status_tbl to
	 * allow it to be sent as action mail.
	 * 
	 * @param customerID
	 *            Id of the recipient for the newsletter.
	 * @param companyID
	 *            the company to look in.
	 * @return The mailingID of the last newsletter that would have been sent to
	 *         this recipient.
	 */
	public int findLastNewsletter(int customerID, int companyID, int mailinglist) {
		MailingDao dao = (MailingDao) con.getBean("MailingDao");
		int mailingID = dao.findLastNewsletter(customerID, companyID, mailinglist);
		Mailing mailing = dao.getMailing(mailingID, companyID);

		if (mailing == null) {
			return 0;
		}
		
		for (MaildropEntry entry : mailing.getMaildropStatus()) {
			if (entry.getStatus() == MaildropEntry.STATUS_ACTIONBASED) {
				return mailingID;
			}
		}

		// Create new maildrop entry
		MaildropEntry drop = (MaildropEntry) con.getBean("MaildropEntry");

		drop.setStatus(MaildropEntry.STATUS_ACTIONBASED);
		drop.setSendDate(new java.util.Date());
		drop.setGenDate(new java.util.Date());
		drop.setGenStatus(1);
		drop.setGenChangeDate(new java.util.Date());
		drop.setMailingID(mailingID);
		drop.setCompanyID(companyID);
		
		mailing.getMaildropStatus().add(drop);
		dao.saveMailing(mailing);
		
		return mailingID;
	}

	public boolean validateEmail(String email) {
		return email.endsWith("agnitas.de");
	}

	/**
	 * Parse a given date string to a GregorianCalendar
	 * 
	 * @param dateValue
	 * @param datePattern
	 * @return
	 */
	public GregorianCalendar parseDate(String dateValue, String datePattern) {
		try {
			GregorianCalendar returnValue = new GregorianCalendar();
			Date parsedDate = new SimpleDateFormat(datePattern).parse(dateValue);
			returnValue.setTime(parsedDate);
			return returnValue;
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * Create a UID for a customer
	 * 
	 * @param companyId
	 * @param customerKeyColumnName
	 * @param customerKeyColumnValue
	 * @return
	 */
	public String createUidForCustomer(int companyId, String customerKeyColumnName, String customerKeyColumnValue) {
		try {
			// Search for customer
			RecipientDao recipientDao = (RecipientDao) con.getBean("RecipientDao");
			int customerID = recipientDao.findByColumn(companyId, customerKeyColumnName, customerKeyColumnValue);
			
			// Create UID
			if (customerID > 0) {
				ExtensibleUIDService extensibleUIDService = (ExtensibleUIDService) con.getBean("ExtensibleUIDService");
				ExtensibleUID extensibleUID = extensibleUIDService.newUID();
				extensibleUID.setCompanyID(companyId);
				extensibleUID.setCustomerID(customerID);
				return extensibleUIDService.buildUIDString(extensibleUID);
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}
}
