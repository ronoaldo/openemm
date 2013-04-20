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


/*
 * EMMWebservice.java
 *
 */

package org.agnitas.webservice;

import org.agnitas.beans.BindingEntry;
import org.agnitas.beans.DynamicTag;
import org.agnitas.beans.DynamicTagContent;
import org.agnitas.beans.MaildropEntry;
import org.agnitas.beans.Mailing;
import org.agnitas.beans.MailingComponent;
import org.agnitas.beans.Mailinglist;
import org.agnitas.beans.Mediatype;
import org.agnitas.beans.MediatypeEmail;
import org.agnitas.beans.Recipient;
import org.agnitas.beans.impl.MailinglistImpl;
import org.agnitas.dao.BindingEntryDao;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.MailinglistDao;
import org.agnitas.dao.RecipientDao;
import org.agnitas.util.AgnUtils;
import org.apache.axis.MessageContext;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.springframework.context.ApplicationContext;

import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

/**
 *
 * @author Martin Helff, Nicole Serek
 * @version 5.0
 */
public class EmmWebservice extends WebServiceBase implements EmmWebService_Port {
    
    /** Creates a new instance of OpenEMMWebservice */
    public EmmWebservice() {
    }
    
    /**
     * Method for creating a new email-mailing.
     * This only creates the mailing, use "insertContent" to fill in the Text and "sendMailing" to send out the email
     * @param username Username from ws_admin_tbl
     * @param password Password from ws_admin_tbl
     * @param shortname Name for the mailing, visible in OpenEMM
     * Not visible to the recipients of the mailing
     * @param description Description (max. 1000 Chars) for the mailing, visible in OpenEMM
     * Not visible to the recipients of the mailing
     * @param mailinglistID Mailinglist-Id, must exist in OpenEMM
     * @param targetID Target group id.
     * Possible values:
     * ==0: All subscribers from given mailinglist
     * >0: An existing target group id
     * @param mailingType Type of mailing.
     * 
     * Possible values:
     * 0 == normal mailing
     * 1 == Event-based mailing
     * 2 == Rule-based mailing (like automated birthday-mailings)
     * 
     * If unsure, say: "0"
     * @param templateID Template to be used for creation of the mailing.
     * Can be every existing template- or mailing-id from OpenEMM or zero for no template.
     * 
     * If no template is used, to content blocks are available for insertContent are "emailText" and "emailHtml"
     * @param emailSubject Subject-line for the email
     * @param emailSender Sender-address for email.
     * Can be a simple email-address like <CODE>info@agnitas.de</CODE> or with a given realname on the form:
     * <CODE>"AGNITAS AG" <info@agnitas.de></CODE>
     * @param emailCharset Charater-set to be used for encoding the email.
     * Any character-set JDK 1.4 knows is allowed.
     * Common value is "iso-8859-1" 
     * @param emailLinefeed Automated linefeed in text version of the email.
     * @param emailFormat Format of email.
     * 
     * Possible values:
     * 0 == only text
     * 1 == Online-HTML (Multipart)
     * 2 == Offline-HTML (Multipart+embedded graphics)
     * 
     * If unsure, say "2"
     * @return Id of created mailing or 0
     * @throws java.rmi.RemoteException necessary for Apache Axis
     */	   
    public int newEmailMailing(java.lang.String username, java.lang.String password, java.lang.String shortname, java.lang.String description, int mailinglistID, StringArrayType targetID, int mailingType, int templateID, java.lang.String emailSubject, java.lang.String emailSender, java.lang.String emailCharset, int emailLinefeed, int emailFormat) throws java.rmi.RemoteException {
        ApplicationContext con = getWebApplicationContext();
        int result = 0;
        MailingDao mDao = (MailingDao) con.getBean("MailingDao");
        Mailing aMailing = (Mailing) con.getBean("Mailing");
        MediatypeEmail paramEmail = null;
        MessageContext msct = MessageContext.getCurrentContext();
        
        if(!authenticateUser(msct, username, password, 1)) {
            return result;
        }
        
        aMailing.setCompanyID(1);
        aMailing.setId(0);
        aMailing.setMailTemplateID(templateID);
        if(aMailing.getMailTemplateID() != 0) {
        	//load Template
        	aMailing = loadTemplate(aMailing, mDao, con);
        }
        aMailing.setDescription(description);
        aMailing.setShortname(shortname);
        aMailing.setCompanyID(1);
        
        if(aMailing.getMailTemplateID() == 0 || mailinglistID != 0) {
        	aMailing.setMailinglistID(mailinglistID);
        }
        
        if(aMailing.getMailTemplateID() == 0 || Integer.valueOf(targetID.getX(0)).intValue() != 0) {
        	Collection targetGroup = new ArrayList();
            for(int i=0; i<targetID.getX().length; i++) {
            	int target = Integer.valueOf(targetID.getX(i)).intValue();
            	aMailing.setTargetID(target);
                targetGroup.add(target);
            }     
            aMailing.setTargetGroups(targetGroup);	
        }
        
        if(aMailing.getMailTemplateID() == 0) {
        	aMailing.setMailingType(mailingType);
        }
        paramEmail = aMailing.getEmailParam();
        if(paramEmail == null) {
            paramEmail = (MediatypeEmail) con.getBean("MediatypeEmail");
            paramEmail.setCompanyID(1);
            paramEmail.setMailingID(aMailing.getId());
        }
        paramEmail.setStatus(Mediatype.STATUS_ACTIVE);
        paramEmail.setSubject(emailSubject);
        try {
        	if(aMailing.getMailTemplateID() == 0 && !emailSender.trim().equalsIgnoreCase("")) {
        		InternetAddress adr = new InternetAddress(emailSender);

        		paramEmail.setFromEmail(adr.getAddress());
        		paramEmail.setFromFullname(adr.getPersonal());
        	}
        } catch(Exception e) {
            AgnUtils.logger().error("Error in sender address");
        }
      
        if(aMailing.getMailTemplateID() == 0) {
        	paramEmail.setCharset(emailCharset);
        	paramEmail.setMailFormat(emailFormat);
        	paramEmail.setLinefeed(emailLinefeed);
        }
        paramEmail.setPriority(1);
        paramEmail.setOnepixel(MediatypeEmail.ONEPIXEL_BOTTOM);
        Map mediatypes = aMailing.getMediatypes();

        mediatypes.put(new Integer(0), paramEmail);
        aMailing.setMediatypes(mediatypes);
      
        try {
        	if(aMailing.getMailTemplateID() == 0) {
				MailingComponent comp = null;

				comp = (MailingComponent) con.getBean("MailingComponent");
				comp.setCompanyID(1);
				comp.setComponentName("agnText");
				comp.setType(MailingComponent.TYPE_TEMPLATE);
				comp.setEmmBlock("[agnDYN name=\"emailText\"/]");
				comp.setMimeType("text/plain");
				aMailing.addComponent(comp);

				comp = (MailingComponent) con.getBean("MailingComponent");
				comp.setCompanyID(1);
				comp.setComponentName("agnHtml");
				comp.setType(MailingComponent.TYPE_TEMPLATE);
				comp.setEmmBlock("[agnDYN name=\"emailHtml\"/]");
				comp.setMimeType("text/html");
				aMailing.addComponent(comp);
        	}
			aMailing.buildDependencies(true, con);
			
            mDao.saveMailing(aMailing);
            result = aMailing.getId();
        } catch (Exception e) {
            AgnUtils.logger().info("Error in create mailing id: " + aMailing.getId() + " msg: " + e);
            result = 0;
        }
        return result;
    }
    
    /**
     * Method for adding content to an existing mailing
     * @param username Username from ws_admin_tbl
     * @param password Password from ws_admin_tbl
     * @param mailingID Id of mailing, normally returned by newEmailMailing(WithReply)
     * @param blockName Name of content-block. Depends on the template used in newEmailMailing. If no template is used, valid block-names are "emailText" and "emailHtml"
     * @param blockContent content for this blockName. Multiple contents can be provided for one blockName, but only one is shown to a single subscriber.
     * @param targetID id of target-group for this content. use "0" for "All subscribers"
     * @param priority Priority of this blockContent. Smaller numbers mean higher priority.
     * @return length of provided blockContent or 0
     * @throws java.rmi.RemoteException necessary for Apache Axis
     */
    public int insertContent(java.lang.String username, java.lang.String password, int mailingID, java.lang.String blockName, java.lang.String blockContent, int targetID, int priority) throws java.rmi.RemoteException {
        ApplicationContext con = getWebApplicationContext();
        MailingDao dao=(MailingDao) con.getBean("MailingDao");
        int result = 0;
        DynamicTagContent aContent = null;
        DynamicTagContent aContentTmp = null;
        MessageContext msct = MessageContext.getCurrentContext();
       
        if(!authenticateUser(msct, username, password, 1)) {
            return result;
        }
        
        result = blockContent.length();
	//	SessionFactory sessionFactory = (SessionFactory) con.getBean("sessionFactory");
	//	Session session = SessionFactoryUtils.getSession(sessionFactory, true);
	//	TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));        
        try {
            Mailing aMailing = dao.getMailing(mailingID, 1);
            
            Map dTags = aMailing.getDynTags();
            Iterator i = dTags.keySet().iterator();
            DynamicTag aTag = null;
            while(i.hasNext()) {
                aTag = (DynamicTag) dTags.get(i.next());
                if(aTag.getDynName().equals(blockName)) {
                    Map dContent = aTag.getDynContent();

                    if(dContent != null) {
                        Iterator c_iter = dContent.keySet().iterator();

                        aContent = null;
                        aContentTmp = null;
                        while(c_iter.hasNext()) {
                            aContentTmp = (DynamicTagContent) dContent.get(c_iter.next());
                            if(aContentTmp.getTargetID() == targetID) {
                                aContent = aContentTmp;
                            }
                        }
                    }
                    if(aContent == null) {
                        aContent = (DynamicTagContent) con.getBean("DynamicTagContent");
                        aContent.setDynNameID(aTag.getId());
                        aContent.setId(0);
                        aContent.setDynName(aTag.getDynName());
                        aContent.setDynOrder(priority);
                        aContent.setTargetID(targetID);
                        aContent.setDynContent(blockContent);
                        aContent.setMailingID(mailingID);
                        aContent.setCompanyID(1);
                        aTag.addContent(aContent);
                    }
                    aContent.setDynOrder(priority);
                    aContent.setTargetID(targetID);
                    aContent.setDynContent(blockContent);
                    break;
                }
            }
            
            try {
                aMailing.buildDependencies(false, con);
            } catch (Exception e) {
                AgnUtils.logger().error(e.getMessage());
            }

            dao.saveMailing(aMailing);
        } catch (Exception e) {
            result = 0;
            AgnUtils.logger().info("soap problem content: "+e);
        }
//		TransactionSynchronizationManager.unbindResource(sessionFactory);
//		SessionFactoryUtils.releaseSession(session, sessionFactory);
        
        return result;
    }
    
    /**
     * Method for deleting content from an existing mailing
     * @param username Username from ws_admin_tbl
     * @param password Password from ws_admin_tbl
     * @param contentID id of content, which has to be deleted
     * @return 1 == sucess
     * 0 == failure
     */
    public int deleteContent(java.lang.String username, java.lang.String password, int contentID) {
        ApplicationContext con = getWebApplicationContext();
        MailingDao dao = (MailingDao) con.getBean("MailingDao");
        int result = 0;
        MessageContext msct = MessageContext.getCurrentContext();
       
        if(!authenticateUser(msct, username, password, 1)) {
            return result;
        }
        
        try {
            dao.deleteContentFromMailing(null, contentID);
            result = 1;
        } catch (Exception e) {
            result = 0;
            AgnUtils.logger().info("soap problem could not delete content: "+e);
        }
        
        return result;
    }
    
    /**
     * Method for testing and sending a mailing
     * @param username Username from ws_admin_tbl
     * @param password Password from ws_admin_tbl
     * @param mailingID ID of mailing to be sent, normally returned by newEmailMailing(WithReply)
     * @param sendGroup Possible values:
     * 'A': Only admin-subscribers (for testing)
     * 'T': Only test- and admin-subscribers (for testing)
     * 'W': All subscribers (only once for security reasons)
     * @param sendTime scheduled send-time in <B>seconds</B> since January 1, 1970, 00:00:00 GMT
     * @param stepping for artificially slowing down the send-process, seconds between deliviery of to mailing-blocks. Set to 0 unless you know what you are doing.
     * @param blocksize for artificially slowing down the send-process, number of mails in one mailing-block. Set to 0 unless you know what you are doing.
     * @return 1 == sucess
     * 0 == failure
     * @throws java.rmi.RemoteException needed by Apache Axis
     */
    public int sendMailing(java.lang.String username, java.lang.String password, int mailingID, java.lang.String sendGroup, int sendTime, int stepping, int blocksize) throws java.rmi.RemoteException {
        ApplicationContext con = getWebApplicationContext();
        MailingDao dao = (MailingDao) con.getBean("MailingDao");
        int returnValue = 0;
        char mailingType = '\0';
        MessageContext msct = MessageContext.getCurrentContext();
        
        if(!authenticateUser(msct, username, password, 1)) {
            return returnValue;
        }
       
        try {
            Mailing aMailing = dao.getMailing(mailingID, 1);
            if(aMailing == null) {
                return returnValue;
            }
            
            if(sendGroup.equals("A")) {
                mailingType = MaildropEntry.STATUS_ADMIN;
            }

            if(sendGroup.equals("T")) {
                mailingType = MaildropEntry.STATUS_TEST;
            }

            if(sendGroup.equals("W")) {
            	mailingType = MaildropEntry.STATUS_WORLD;
            }

            if(sendGroup.equals("R")) {
                mailingType = MaildropEntry.STATUS_DATEBASED;
            }

            if(sendGroup.equals("C")) {
                mailingType = MaildropEntry.STATUS_ACTIONBASED;
            }
            
            
            MaildropEntry drop = (MaildropEntry) con.getBean("MaildropEntry");
            GregorianCalendar aCal = new GregorianCalendar(TimeZone.getDefault());

            drop.setStatus(mailingType);
            drop.setGenDate(aCal.getTime());
            drop.setMailingID(aMailing.getId());
            drop.setCompanyID(aMailing.getCompanyID());
            if(sendTime!=0 && mailingType==MaildropEntry.STATUS_WORLD) {
            	//set genstatus = 0, when senddate is in future
                drop.setGenStatus(0);
                //gendate is 3 hours before sendtime
                aCal.setTime(new java.util.Date(((long) sendTime -10800)*1000L));
                drop.setGenDate(aCal.getTime());
                aCal.setTime(new java.util.Date(((long) sendTime)*1000L));
            } else {
            	drop.setGenStatus(1);
            }
            drop.setSendDate(aCal.getTime());
              
            aMailing.getMaildropStatus().add(drop);
            dao.saveMailing(aMailing);
            if(drop.getGenStatus() == 1
                   && drop.getStatus() != MaildropEntry.STATUS_ACTIONBASED
                   && drop.getStatus() != MaildropEntry.STATUS_DATEBASED) {
                aMailing.triggerMailing(drop.getId(), new Hashtable(), con);
            }
            returnValue = 1; 
        } catch (Exception e) {
            AgnUtils.logger().info("soap prob send mail: "+e);
        }
        
        return returnValue;
    }
    
    /**
     * Method for inserting a recipient to the customer-database
     * @param username Username from ws_admin_tbl
     * @param password Password from ws_admin_tbl
     * @param doubleCheck If true checks, if email is already in database and does not import it.
     * @param keyColumn Column on which double check is used
     * @param overwrite If true, overwrites existing data of the recipient
     * @param paramNames Names of the columns
     * @param paramValues Values of the columns
     * @return customerID
     */
    public int addSubscriber(java.lang.String username, java.lang.String password, boolean doubleCheck, java.lang.String keyColumn, boolean overwrite, StringArrayType paramNames, StringArrayType paramValues) {
        ApplicationContext con = getWebApplicationContext();
        CaseInsensitiveMap allParams=new CaseInsensitiveMap();
        int returnValue = 0;
        int tmpCustID = 0;
        MessageContext msct = MessageContext.getCurrentContext();
        
        if(!authenticateUser(msct, username, password, 1)) {
            return returnValue;
        }
        
        try {
            for(int i = 0; i<paramNames.getX().length; i++) {
                if(paramNames.getX(i).toLowerCase().equals("email")) {
                    paramValues.setX(i, paramValues.getX(i).toLowerCase());
                }
                allParams.put(paramNames.getX(i), paramValues.getX(i));
            }
            
            Recipient aCust = (Recipient) con.getBean("Recipient");
            RecipientDao dao = (RecipientDao) con.getBean("RecipientDao");
            aCust.setCompanyID(1);
            aCust.setCustParameters(allParams);
            if(doubleCheck) {
                tmpCustID = dao.findByColumn(aCust.getCompanyID(), keyColumn.toLowerCase(), (String)allParams.get(keyColumn.toLowerCase()));
                if(tmpCustID == 0) {
                    returnValue = dao.insertNewCust(aCust);
                } else {
                    returnValue = tmpCustID;
                    if(overwrite) {
                        aCust.setCustomerID(tmpCustID);
                        dao.updateInDB(aCust);
                    }
                }
            } else {
                returnValue = dao.insertNewCust(aCust);
            }
        } catch (Exception e) {
            AgnUtils.logger().info("soap prob new subscriber: "+e);
            returnValue=0;
        }
        
        return returnValue;
    }
    
    /**
     * Method for loading customer data from the customer-database
     * @param username Username from ws_admin_tbl
     * @param password Password from ws_admin_tbl
     * @param customerID CustomerID of the recipient which data should be loaded
     * @throws java.rmi.RemoteException
     * @return SubscriberData
     */
    public SubscriberData getSubscriber(java.lang.String username, java.lang.String password, int customerID) throws java.rmi.RemoteException {
        ApplicationContext con = getWebApplicationContext();
        SubscriberData returnValue = new SubscriberData();
        MessageContext msct = MessageContext.getCurrentContext();
        
        if(!authenticateUser(msct, username, password, 1)) {
            return returnValue;
        }
        
        try {
            Recipient aCust = (Recipient) con.getBean("Recipient");
            RecipientDao dao = (RecipientDao) con.getBean("RecipientDao");
            aCust.setCompanyID(1);
            aCust.setCustomerID(customerID);
            Map allParams = dao.getCustomerDataFromDb(aCust.getCompanyID(), aCust.getCustomerID());
            if(allParams != null) {
                String[] tmpKeys = new String[allParams.size()];
                String[] tmpValues = new String[allParams.size()];
                String tmpKey = null;
                Iterator c_iter = allParams.keySet().iterator();
                int i = 0;
                while(c_iter.hasNext()) {
                    tmpKey = (String) c_iter.next();
                    tmpKeys[i] = new String(tmpKey);
                    tmpValues[i] = new String((String)allParams.get(tmpKey));
                    i++;
                }
                StringArrayType keyArray = new StringArrayType();
                keyArray.setX(tmpKeys);
                returnValue.setParamNames(keyArray);
                StringArrayType valueArray = new StringArrayType();
                valueArray.setX(tmpValues);
                returnValue.setParamValues(valueArray);
                if(i>0) {
                    returnValue.setCustomerID(customerID);
                }
            }
        } catch (Exception e) {
            AgnUtils.logger().info("soap prob get subscriber: "+e);
            returnValue.setCustomerID(0);
        }
        
        return returnValue;
    }
    
    /**
     * Method to find a customer in the customer-database
     * @param username Username from ws_admin_tbl
     * @param password Password from ws_admin_tbl
     * @param keyColumn Database-column to be searched.
     * @param value Value to be searched for.
     * @return CustomerID of the first matching record
     */
    public int findSubscriber(java.lang.String username, java.lang.String password, java.lang.String keyColumn, java.lang.String value) {
    	AgnUtils.logger().info("soap prob find subscriber:...");
    	ApplicationContext con = getWebApplicationContext();
        int returnValue = 0;
        MessageContext msct = MessageContext.getCurrentContext();
        
        if(!authenticateUser(msct, username, password, 1)) {
        	AgnUtils.logger().info("soap prob find subscriber: wrong credentials " +username + " " +password);
        	return returnValue;
        }
        
        try {
            Recipient aCust = (Recipient) con.getBean("Recipient");
            RecipientDao dao = (RecipientDao) con.getBean("RecipientDao");
            aCust.setCompanyID(1);
            returnValue = dao.findByColumn(aCust.getCompanyID(), keyColumn.toLowerCase(), value);
        } catch (Exception e) {
            AgnUtils.logger().info("soap prob find subscriber: "+e);
            returnValue = 0;
        }
               
        return returnValue;
    }
    
    /**
     * Method to delete a customer from the customer-database
     * @param username Username from ws_admin_tbl
     * @param password Password from ws_admin_tbl
     * @param customerID Id of the recipient who has to be deleted
     * @return 1==sucess
     * 0==failure
     */
    public int deleteSubscriber(java.lang.String username, java.lang.String password, int customerID) {
        ApplicationContext con = getWebApplicationContext();
        int returnValue = 0;
        MessageContext msct = MessageContext.getCurrentContext();
         
        if(!authenticateUser(msct, username, password, 1)) {
            return returnValue;
        }
        
        try {
            Recipient aCust = (Recipient) con.getBean("Recipient");
            RecipientDao dao = (RecipientDao) con.getBean("RecipientDao");
            aCust.setCompanyID(1);
            aCust.setCustomerID(customerID);
            dao.deleteCustomerDataFromDb(aCust.getCompanyID(), aCust.getCustomerID());
            returnValue = 1;
        } catch (Exception e) {
            AgnUtils.logger().info("soap could not delete subscriber: "+e);
            returnValue = 0;
        }
        return returnValue;
    }
    
    /**
     * Sets binding for a recipient on a mailinglist
     * @param username Username from ws_admin_tbl
     * @param password Password from ws_admin_tbl
     * @param customerID Id of the recipient where the binding has to be updated
     * @param mailinglistID Id of the mailinglist that has to be updated
     * @param mediatype Mediatype that has to be updated (0 for email)
     * @param status New status for the recipient on the mailinglist
     * @param bindingType Type of binding for the recipient on the mailinglist
     * @param remark Comment to binding
     * Possible value: "Opt-out by admin"
     * @param exitMailingID Id of unsubscribe mailing, 0 if recipient is already on the mailinglist
     * @return 1 == success
     * 0 == failure
     */
    public int setSubscriberBinding(java.lang.String username, java.lang.String password, int customerID, int mailinglistID, int mediatype, int status, java.lang.String bindingType, java.lang.String remark, int exitMailingID) throws java.rmi.RemoteException {
        ApplicationContext con = getWebApplicationContext();
        BindingEntryDao dao = (BindingEntryDao) con.getBean("BindingEntryDao");
        int result = 0;
        MessageContext msct = MessageContext.getCurrentContext();
        BindingEntry aEntry = null;
        
        if(!authenticateUser(msct, username, password, 1)) {
            return result;
        }
        
        try {
            aEntry = dao.get(customerID, 1, mailinglistID, mediatype);
            if(aEntry == null) {
                aEntry=(BindingEntry) con.getBean("BindingEntry");
                aEntry.setCustomerID(customerID); 
                aEntry.setMailinglistID(mailinglistID); 
                aEntry.setMediaType(mediatype); 
            }
            aEntry.setUserStatus(status);
            if(bindingType != null) {
                aEntry.setUserType(bindingType);
            }
            if(exitMailingID != -1) {
                aEntry.setExitMailingID(exitMailingID);
            }
            if(remark != null) {
                aEntry.setUserRemark(remark);
            }
            dao.save(1, aEntry);
            result = customerID;
        } catch (Exception e) {
            AgnUtils.logger().info("soap prob set binding: "+e);
            result = 0;
        }
        return result;
    }
    
  /**
     * Method for creating a new email-mailing.
     * This only creates the Mailing, use "insertContent" to fill in the Text and "sendMailing" to send out the email
     * @param username Username from ws_admin_tbl
     * @param password Password from ws_admin_tbl
     * @param shortname Name for the mailing, visible in OpenEMM
     * Not visible to the recipients of the mailing
     * @param description Description (max. 1000 Chars) for the mailing, visible in OpenEMM
     * Not visible to the recipients of the mailing
     * @param mailinglistID Mailinglist-id, must exist in OpenEMM
     * @param targetID Target group id.
     * Possible values:
     * ==0: All subscribers from given mailinglist
     * >0: An existing target group id
     * @param mailingType Type of mailing.
     *
     * Possible values:
     * 0 == normal mailing
     * 1 == Event-based mailing
     * 2 == Rule-based mailing (like automated birthday-mailings)
     *
     * If unsure, say: "0"
     * @param templateID Template to be used for mailing creation.
     * Can be every existing template- or mailing-id from OpenEMM or zero for no template.
     *
     * If no template is used, to content blocks are available for insertContent are "emailText" and "emailHtml"
     * @param emailSubject Subject-line for the email
     * @param emailSender Sender-address for email.
     * Can be a simple email-address like <CODE>info@agnitas.de</CODE> or with a given realname on the form:
     * <CODE>"AGNITAS AG" <info@agnitas.de></CODE>
     * @param emailReply Reply-to-address for the email
     * @param emailCharset Charater-set to be used for encoding the email.
     * Any character-set JDK 1.4 knows is allowed.
     * Common value is "iso-8859-1"
     * @param emailLinefeed Automated linefeed in text version of the email.
     * @param emailFormat Format of email.
     *
     * Possible values:
     * 0 == only text
     * 1 == Online-HTML (Multipart)
     * 2 == Offline-HTML (Multipart+embedded graphics)
     *
     * If unsure, say "2"
     * @return ID of created mailing or 0
     * @throws java.rmi.RemoteException necessary for Apache Axis
     */
    public int newEmailMailingWithReply(java.lang.String username, java.lang.String password, java.lang.String shortname, java.lang.String description, int mailinglistID, StringArrayType targetID, int mailingType, int templateID, java.lang.String emailSubject, java.lang.String emailSender, java.lang.String emailReply, java.lang.String emailCharset, int emailLinefeed, int emailFormat) throws java.rmi.RemoteException {
        ApplicationContext con = getWebApplicationContext();
        int result = 0;
        MailingDao mDao = (MailingDao) con.getBean("MailingDao");
        Mailing aMailing = (Mailing) con.getBean("Mailing");
        MediatypeEmail paramEmail = null;
        MessageContext msct=MessageContext.getCurrentContext();

        if(!authenticateUser(msct, username, password, 1)) {
            return result;
        }

        aMailing.setCompanyID(1);
        aMailing.setId(0);
        aMailing.setMailTemplateID(templateID);
        if(aMailing.getMailTemplateID() != 0) {
        	//load Template
        	aMailing = loadTemplate(aMailing, mDao, con);
        }
        aMailing.setDescription(description);
        aMailing.setShortname(shortname);
        aMailing.setCompanyID(1);
        
        if(aMailing.getMailTemplateID() == 0 || mailinglistID != 0) {
        	aMailing.setMailinglistID(mailinglistID);
        }

		if (aMailing.getMailTemplateID() == 0
				|| Integer.valueOf(targetID.getX(0)).intValue() != 0) {
			Collection targetGroup = new ArrayList();
			for (int i = 0; i < targetID.getX().length; i++) {
				int target = Integer.valueOf(targetID.getX(i)).intValue();
				aMailing.setTargetID(target);
				targetGroup.add(target);
			}

			aMailing.setTargetGroups(targetGroup);
		}
        
        if(aMailing.getMailTemplateID() == 0) {
        	aMailing.setMailingType(mailingType);
        }
        paramEmail=aMailing.getEmailParam();
        if(paramEmail == null) {
            paramEmail = (MediatypeEmail) con.getBean("MediatypeEmail");
            paramEmail.setCompanyID(1);
            paramEmail.setMailingID(aMailing.getId());
        }
        paramEmail.setStatus(Mediatype.STATUS_ACTIVE);
        paramEmail.setSubject(emailSubject);
        try {
        	if(aMailing.getMailTemplateID() == 0 && !emailSender.trim().equalsIgnoreCase("")) {
        		InternetAddress adr = new InternetAddress(emailSender);

        		paramEmail.setFromEmail(adr.getAddress());
        		paramEmail.setFromFullname(adr.getPersonal());
        	}
        } catch(Exception e) {
            AgnUtils.logger().error("Error in sender address");
        }
        try {
        	if(aMailing.getMailTemplateID() == 0 && !emailReply.trim().equalsIgnoreCase("")) {
        		InternetAddress adr=new InternetAddress(emailReply);

        		paramEmail.setReplyEmail(adr.getAddress());
        		paramEmail.setReplyFullname(adr.getPersonal());
        	}
        } catch(Exception e) {
            AgnUtils.logger().error("Error in reply address");
        }
        if(aMailing.getMailTemplateID() == 0) {
        	paramEmail.setCharset(emailCharset);
        	paramEmail.setMailFormat(emailFormat);
        	paramEmail.setLinefeed(emailLinefeed);
        }
        paramEmail.setPriority(1);
        paramEmail.setOnepixel(MediatypeEmail.ONEPIXEL_BOTTOM);
        Map mediatypes = aMailing.getMediatypes();

        mediatypes.put(new Integer(0), paramEmail);
        aMailing.setMediatypes(mediatypes);

        try {
        	if(aMailing.getMailTemplateID() == 0) {
        		MailingComponent comp = null;

        		comp = (MailingComponent)con.getBean("MailingComponent");
        		comp.setCompanyID(1);
        		comp.setComponentName("agnText");
        		comp.setType(MailingComponent.TYPE_TEMPLATE);
        		comp.setEmmBlock("[agnDYN name=\"emailText\"/]");
        		comp.setMimeType("text/plain");
        		aMailing.addComponent(comp);

        		comp = (MailingComponent)con.getBean("MailingComponent");
        		comp.setCompanyID(1);
        		comp.setComponentName("agnHtml");
        		comp.setType(MailingComponent.TYPE_TEMPLATE);
        		comp.setEmmBlock("[agnDYN name=\"emailHtml\"/]");
        		comp.setMimeType("text/html");
        		aMailing.addComponent(comp);
        	}

            aMailing.buildDependencies(true, con);
            mDao.saveMailing(aMailing);

            result = aMailing.getId();
        } catch (Exception e) {
            AgnUtils.logger().info("Error in create mailing id: " + aMailing.getId() + " msg: " + e);
            result = 0;
        }
        return result;
    }

    /**
     * Method for update an email-mailing.
     * This only creates the mailing, use "link insertContent" to fill in the text and "link sendMailing" to send out the email
     * @param username Username from ws_admin_tbl
     * @param password Password from ws_admin_tbl
     * @param mailingID Id of mailing which should be changed
     * @param shortname Name for the mailing, visible in OpenEMM
     * Not visible to the recipients of the mailing
     * @param description Description (max. 1000 Chars) for the mailing, visible in OpenEMM
     * Not visible to the recipients of the mailing
     * @param mailinglistID Mailinglist-id, must exist in OpenEMM
     * @param targetID Target group id.
     * Possible values:
     * ==0: All subscribers from given mailinglist
     * >0: An existing target group id
     * @param mailingType Type of mailing.
     *
     * Possible values:
     * 0 == normal mailing
     * 1 == Event-based mailing
     * 2 == Rule-based mailing (like automated birthday-mailings)
     *
     * If unsure, say: "0"
     * @param emailSubject Subject-line for the email
     * @param emailSender Sender-address for email.
     * Can be a simple email-address like <CODE>info@agnitas.de</CODE> or with a given realname on the form:
     * <CODE>"AGNITAS AG" <info@agnitas.de></CODE>
     * @param emailReply Reply-to-address for the email
     * @param emailCharset Charater-set to be used for encoding the email.
     * Any character-set JDK 1.4 knows is allowed.
     * Common value is "iso-8859-1"
     * @param emailLinefeed Automated linefeed in text-version of the email.
     * @param emailFormat Format of email.
     *
     * Possible values:
     * 0 == only text
     * 1 == Online-HTML (Multipart)
     * 2 == Offline-HTML (Multipart+embedded graphics)
     * If unsure, say "2"
     * @return true if success
     * @throws java.rmi.RemoteException necessary for Apache Axis
     */
    public boolean updateEmailMailing(java.lang.String username, java.lang.String password, int mailingID, java.lang.String shortname, java.lang.String description, int mailinglistID, StringArrayType targetID, int mailingType, java.lang.String emailSubject, java.lang.String emailSender, java.lang.String emailReply, java.lang.String emailCharset, int emailLinefeed, int emailFormat) throws java.rmi.RemoteException {
        ApplicationContext con = getWebApplicationContext();
        boolean result = false;
        MailingDao mDao = (MailingDao) con.getBean("MailingDao");
        Mailing aMailing = (Mailing) mDao.getMailing(mailingID, 1);
        MediatypeEmail paramEmail = null;
        MessageContext msct = MessageContext.getCurrentContext();

        if(!authenticateUser(msct, username, password, 1)) {
            return result;
        }

        aMailing.setDescription(description);
        aMailing.setShortname(shortname);
        aMailing.setMailinglistID(mailinglistID);
        
        Collection targetGroup = new ArrayList();
        for (int i=0; i<targetID.getX().length; i++) {
        	int target = Integer.valueOf(targetID.getX(i)).intValue();
            targetGroup.add(target);
        }         
        
        aMailing.setTargetGroups(targetGroup);
        aMailing.setMailingType(mailingType);
        paramEmail = aMailing.getEmailParam();
        if(paramEmail == null) {
            paramEmail = (MediatypeEmail) con.getBean("MediatypeEmail");
            paramEmail.setCompanyID(1);
            paramEmail.setMailingID(aMailing.getId());
        }
        paramEmail.setStatus(Mediatype.STATUS_ACTIVE);
        paramEmail.setSubject(emailSubject);
        try {
            InternetAddress adr = new InternetAddress(emailSender);

            paramEmail.setFromEmail(adr.getAddress());
            paramEmail.setFromFullname(adr.getPersonal());
        } catch(Exception e) {
            AgnUtils.logger().error("Error in sender address");
        }
        try {
        	if(aMailing.getMailTemplateID() == 0 && !emailReply.trim().equalsIgnoreCase("")) {
        		InternetAddress adr=new InternetAddress(emailReply);

        		paramEmail.setReplyEmail(adr.getAddress());
        		paramEmail.setReplyFullname(adr.getPersonal());
        	}
        } catch(Exception e) {
            AgnUtils.logger().error("Error in reply address");
        }
        paramEmail.setCharset(emailCharset);
        paramEmail.setMailFormat(emailFormat);
        paramEmail.setLinefeed(emailLinefeed);
        paramEmail.setPriority(1);
        Map mediatypes = aMailing.getMediatypes();

        mediatypes.put(new Integer(0), paramEmail);
        aMailing.setMediatypes(mediatypes);

        try {
            aMailing.buildDependencies(true, con);
            mDao.saveMailing(aMailing);
            result = true;
        } catch (Exception e) {
            AgnUtils.logger().info("Error in create mailing id: "+aMailing.getId()+" msg: "+e);
            result = false;
        }
        return result;
    }
    
    /**
     * Gets bindings of the recipient on a mailinglist
     * @param username Username from ws_admin_tbl
     * @param password Password from ws_admin_tbl
     * @param customerID Id of the recipient where the binding has to be shown
     * @param mailinglistID Id of the mailinglist that has to be searched
     * @param mediatype Mediatype that has to be shown (0 for email)
     * @return status New status for the recipient on the mailinglist
     *         bindingType Type of binding for the recipient on the mailinglist
     *         exitMailingID Id of unsubscribe mailing, 0 if recipient is already on the mailinglist
     *         remark Comment to binding
     */
    public String getSubscriberBinding(java.lang.String username, java.lang.String password, int customerID, int mailinglistID, int mediatype) throws java.rmi.RemoteException {
        ApplicationContext con = getWebApplicationContext();
        BindingEntryDao dao = (BindingEntryDao) con.getBean("BindingEntryDao");
        String result = null;
        MessageContext msct = MessageContext.getCurrentContext();
        BindingEntry aEntry = null;
        
        if(!authenticateUser(msct, username, password, 1)) {
            return result;
        }
        
        try {
            aEntry = dao.get(customerID, 1, mailinglistID, mediatype);
            if(aEntry != null) {
            	result = aEntry.getUserStatus() + ";" + aEntry.getUserType() + ";" + aEntry.getExitMailingID() + ";" + aEntry.getUserRemark();
            }
        } catch (Exception e) {
            AgnUtils.logger().info("soap prob set binding: "+e);
            result = null;
        }
        return result;
    }
    
    /**
     * loads template values into the new mailing
     */
    private Mailing loadTemplate(Mailing aMailing, MailingDao mDao, ApplicationContext con) {
		Mailing template = null;
    	MailingComponent tmpComp = null;
    	template = mDao.getMailing(aMailing.getMailTemplateID(), aMailing.getCompanyID());
    	
    	if(template != null) {
    		aMailing=(Mailing)template.clone(this.getWebApplicationContext());
            aMailing.setId(0);
            aMailing.setMailTemplateID(template.getId());
            aMailing.setIsTemplate(template.isIsTemplate());
            aMailing.setCampaignID(template.getCampaignID());
            aMailing.setDescription(template.getDescription());
            aMailing.setShortname(template.getShortname());
            aMailing.setMailinglistID(template.getMailinglistID());
            aMailing.setMailingType(template.getMailingType());
            aMailing.setArchived(template.getArchived());
            aMailing.setTargetMode(template.getTargetMode());
            aMailing.setTargetGroups(template.getTargetGroups());
            aMailing.setMediatypes(template.getMediatypes());
            aMailing.setIsTemplate(false);
    	}
		return aMailing;
	}
    
    /**
     * Method for updating a recipient in the customer-database
     * @param username Username from ws_admin_tbl
     * @param password Password from ws_admin_tbl
     * @param customerID customerID of the customer to be updated
     * @param paramNames Names of the columns
     * @param paramValues Values of the columns
     * @return true if update was successful
     */
    public boolean updateSubscriber(java.lang.String username, java.lang.String password, int customerID, StringArrayType paramNames, StringArrayType paramValues) {
        ApplicationContext con = getWebApplicationContext();
        CaseInsensitiveMap allParams = new CaseInsensitiveMap();
        MessageContext msct = MessageContext.getCurrentContext();
        
        if(!authenticateUser(msct, username, password, 1)) {
            return false;
        }
        
        try {
        	Recipient aCust = (Recipient) con.getBean("Recipient");
            RecipientDao dao = (RecipientDao) con.getBean("RecipientDao");
            aCust.setCompanyID(1);
            aCust.loadCustDBStructure();
            aCust.setCustParameters(dao.getCustomerDataFromDb(aCust.getCompanyID(), customerID));
            aCust.setCustomerID(customerID);
           	
            for(int i = 0; i<paramNames.getX().length; i++) {
                if(paramNames.getX(i).toLowerCase().equals("email")) {
                    paramValues.setX(i, paramValues.getX(i).toLowerCase());
                }
                allParams.put(paramNames.getX(i), paramValues.getX(i));
                String name = paramNames.getX(i);
                String value = paramValues.getX(i);
                aCust.setCustParameters(name, value);
            }
            
       		dao.updateInDB(aCust);
        } catch (Exception e) {
            AgnUtils.logger().info("soap prob updating subscriber: "+e);
            return false;
        }
        
        return true;
    }

    /**
     * Method for adding a mailinglist to the database 
     * @param username Username from ws_admin_tbl
     * @param password Password from ws_admin_tbl
     * @param shortname Name of the new mailing list, visible in OpenEMM
     * @param description Description (max. 1000 Chars) for the mailing list, visible in OpenEMM
     * @return ID of created mailinglist or 0
     * @throws java.rmi.RemoteException necessary for Apache Axis
     */
    public int addMailinglist(java.lang.String username, java.lang.String password, String shortname, String description) throws java.rmi.RemoteException {
        int result = 0;
        final ApplicationContext con = getWebApplicationContext();
        final MessageContext msct = MessageContext.getCurrentContext();

        if(!authenticateUser(msct, username, password, 1)) {
            return result;
        }

        try {

            MailinglistDao dao = (MailinglistDao) con.getBean("MailinglistDao");
            Mailinglist mailinglist = new MailinglistImpl();
            mailinglist.setCompanyID(1);
            mailinglist.setShortname(shortname);
            mailinglist.setDescription(description);

            result = dao.saveMailinglist(mailinglist);

        }  catch (final Exception e) {
            AgnUtils.logger().info("soap prob adding mailinglist: "+e);
            result = 0;
        }

        return result;

    }
    
    /**
     * Method for deleting a mailinglist from the database 
     * @param username Username from ws_admin_tbl
     * @param password Password from ws_admin_tbl
     * @param mailinglistID Id of the mailinglist that will be deleted
     * @return 1 == success
     * 0 == failure
     * @throws java.rmi.RemoteException necessary for Apache Axis
     */
    public int deleteMailinglist(java.lang.String username, java.lang.String password, int mailinglistID) throws java.rmi.RemoteException {
        int result = 0;
        ApplicationContext con = getWebApplicationContext();
        MessageContext msct = MessageContext.getCurrentContext();

        if(!authenticateUser(msct, username, password, 1)) {
            return result;
        }

        try {
            MailinglistDao dao = (MailinglistDao) con.getBean("MailinglistDao");
            boolean deleteMailinglist = dao.deleteMailinglist(mailinglistID, 1);
            boolean deleteBindings = false;
            if (deleteMailinglist) {
                deleteBindings = dao.deleteBindings(mailinglistID, 1);
            }
            if (deleteBindings && deleteMailinglist) {
            	result = 1;
            }

        }  catch (final Exception e) {
            AgnUtils.logger().info("soap prob deleting mailinglist: "+e);
            result = 0;
        }

        return result;

    }
}
