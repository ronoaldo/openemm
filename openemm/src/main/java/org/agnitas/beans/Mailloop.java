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

/**
 *
 * @author mhe, Nicole Serek
 */
public interface Mailloop {
    /**
     * Getter for property arHtml.
     *
     * @return Value of property arHtml.
     */
    String getArHtml();

    /**
     * Getter for property arSender.
     *
     * @return Value of property arSender.
     */
    String getArSender();

    /**
     * Getter for property arSubject.
     *
     * @return Value of property arSubject.
     */
    String getArSubject();

    /**
     * Getter for property arText.
     *
     * @return Value of property arText.
     */
    String getArText();

    /**
     * Getter for property companyID.
     *
     * @return Value of property companyID.
     */
    int getCompanyID();

    /**
     * Getter for property description.
     *
     * @return Value of property description.
     */
    String getDescription();

    /**
     * Getter for property forwardEmail.
     *
     * @return Value of property forwardEmail.
     */
    String getForwardEmail();

    /**
     * Getter for property mailloopID.
     *
     * @return Value of property mailloopID.
     */
    int getId();

    /**
     * Getter for property shortname.
     *
     * @return Value of property shortname.
     */
    String getShortname();

    /**
     * Getter for property doAutoresponder.
     *
     * @return Value of property doAutoresponder.
     */
    boolean isDoAutoresponder();

    /**
     * Getter for property doForward.
     *
     * @return Value of property doForward.
     */
    boolean isDoForward();

    /**
     * Setter for property arHtml.
     *
     * @param arHtml New value of property arHtml.
     */
    void setArHtml(String arHtml);

    /**
     * Setter for property arSender.
     *
     * @param arSender New value of property arSender.
     */
    void setArSender(String arSender);

    /**
     * Setter for property arSubject.
     *
     * @param arSubject New value of property arSubject.
     */
    void setArSubject(String arSubject);

    /**
     * Setter for property arText.
     *
     * @param arText New value of property arText.
     */
    void setArText(String arText);

    /**
     * Setter for property companyID.
     *
     * @param companyID New value of property companyID.
     */
    void setCompanyID(int companyID);

    /**
     * Setter for property description.
     *
     * @param description New value of property description.
     */
    void setDescription(String description);

    /**
     * Setter for property doAutoresponder.
     *
     * @param doAutoresponder New value of property doAutoresponder.
     */
    void setDoAutoresponder(boolean doAutoresponder);

    /**
     * Setter for property doForward.
     *
     * @param doForward New value of property doForward.
     */
    void setDoForward(boolean doForward);

    /**
     * Setter for property forwardEmail.
     *
     * @param forwardEmail New value of property forwardEmail.
     */
    void setForwardEmail(String forwardEmail);

    /**
     * Setter for property mailloopID.
     *
     * @param mailloopID New value of property mailloopID.
     */
    void setId(int mailloopID);

    /**
     * Setter for property shortname.
     *
     * @param shortname New value of property shortname.
     */
    void setShortname(String shortname);

    /**
     * Getter for property changedate.
     *
     * @return Value of property changedate.
     */
    java.sql.Timestamp getChangedate();

    /**
     * Setter for property changedate.
     *
     * @param changedate New value of property changedate.
     */
    void setChangedate(java.sql.Timestamp changedate);
    
    /**
     * Getter for property mailinglistID.
     *
     * @return Value of property mailinglistID.
     */
    int getMailinglistID();
    
    /**
     * Setter for property mailinglistID.
     *
     * @param mailinglistID New value of property mailinglistID.
     */
    void setMailinglistID(int mailinglistID);
    
    /**
     * Getter for property userformID.
     *
     * @return Value of property userformID.
     */
    int getUserformID();
    
    /**
     * Setter for property userformID.
     *
     * @param userformID New value of property userformID.
     */
    void setUserformID(int userformID);
    
    /**
     * Getter for property doSubscribe.
     *
     * @return Value of property doSubscribe.
     */
    boolean isDoSubscribe();
    
    /**
     * Setter for property doSubscribe.
     *
     * @param doSubscribe New value of property doSubscribe.
     */
    void setDoSubscribe(boolean doSubscribe);

}
