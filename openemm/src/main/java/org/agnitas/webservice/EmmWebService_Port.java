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

/**
 * EmmWebService_Port.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package org.agnitas.webservice;

public interface EmmWebService_Port extends java.rmi.Remote {
    public int newEmailMailing(java.lang.String username, java.lang.String password, java.lang.String shortname, java.lang.String description, int mailinglistID, StringArrayType targetID, int mailingType, int templateID, java.lang.String emailSubject, java.lang.String emailSender, java.lang.String emailCharset, int emailLinefeed, int emailFormat) throws java.rmi.RemoteException;
    public int newEmailMailingWithReply(java.lang.String username, java.lang.String password, java.lang.String shortname, java.lang.String description, int mailinglistID, StringArrayType targetID, int mailingType, int templateID, java.lang.String emailSubject, java.lang.String emailSender, java.lang.String emailReply, java.lang.String emailCharset, int emailLinefeed, int emailFormat) throws java.rmi.RemoteException;
    public boolean updateEmailMailing(java.lang.String username, java.lang.String password, int mailingID, java.lang.String shortname, java.lang.String description, int mailinglistID, StringArrayType targetID, int mailingType, java.lang.String emailSubject, java.lang.String emailSender, java.lang.String emailReply, java.lang.String emailCharset, int emailLinefeed, int emailFormat) throws java.rmi.RemoteException;
    public int insertContent(java.lang.String username, java.lang.String password, int mailingID, java.lang.String blockName, java.lang.String blockContent, int targetID, int priority) throws java.rmi.RemoteException;
    public int deleteContent(java.lang.String username, java.lang.String password, int contentID) throws java.rmi.RemoteException;
    public int sendMailing(java.lang.String username, java.lang.String password, int mailingID, java.lang.String sendGroup, int sendTime, int stepping, int blocksize) throws java.rmi.RemoteException;
    public int addMailinglist(java.lang.String username, java.lang.String password, String shortname, String description) throws java.rmi.RemoteException;
    public int deleteMailinglist(java.lang.String username, java.lang.String password, int mailinglistID) throws java.rmi.RemoteException;
    public int addSubscriber(java.lang.String username, java.lang.String password, boolean doubleCheck, java.lang.String keyColumn, boolean overwrite, StringArrayType paramNames, StringArrayType paramValues) throws java.rmi.RemoteException;
    public SubscriberData getSubscriber(java.lang.String username, java.lang.String password, int customerID) throws java.rmi.RemoteException;
    public int findSubscriber(java.lang.String username, java.lang.String password, java.lang.String keyColumn, java.lang.String value) throws java.rmi.RemoteException;
    public int setSubscriberBinding(java.lang.String username, java.lang.String password, int customerID, int mailinglistID, int mediatype, int status, java.lang.String bindingType, java.lang.String remark, int exitMailingID) throws java.rmi.RemoteException;
    public int deleteSubscriber(java.lang.String username, java.lang.String password, int customerID) throws java.rmi.RemoteException;
    public String getSubscriberBinding(java.lang.String username, java.lang.String password, int customerID, int mailinglistID, int mediatype) throws java.rmi.RemoteException;
    public boolean updateSubscriber(java.lang.String username, java.lang.String password, int customerID, StringArrayType paramNames, StringArrayType paramValues) throws java.rmi.RemoteException;
}
