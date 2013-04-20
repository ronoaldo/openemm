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

import org.agnitas.beans.Mailloop;

/**
 *
 * @author mhe, Nicole Serek
 */
public class MailloopImpl implements Mailloop {

    /** Creates a new instance of Mailloop */
    public MailloopImpl() {
    }

    /**
     * Holds value of property id.
     */
    private int id;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Holds value of property shortname.
     */
    private String shortname;

    public String getShortname() {

        return this.shortname;
    }

    public void setShortname(String shortname) {

        this.shortname = shortname;
    }

    /**
     * Holds value of property description.
     */
    private String description;

    public String getDescription() {

        return this.description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    /**
     * Holds value of property companyID.
     */
    private int companyID;

    public int getCompanyID() {

        return this.companyID;
    }

    public void setCompanyID(int companyID) {

        this.companyID = companyID;
    }

    /**
     * Holds value of property forwardEmail.
     */
    private String forwardEmail;

    public String getForwardEmail() {

        return this.forwardEmail;
    }

    public void setForwardEmail(String forwardEmail) {

        this.forwardEmail = forwardEmail;
    }

    /**
     * Holds value of property arSender.
     */
    private String arSender;

    public String getArSender() {

        return this.arSender;
    }

    public void setArSender(String arSender) {

        this.arSender = arSender;
    }

    /**
     * Holds value of property arSubject.
     */
    private String arSubject;

    public String getArSubject() {

        return this.arSubject;
    }

    public void setArSubject(String arSubject) {

        this.arSubject = arSubject;
    }

    /**
     * Holds value of property arText.
     */
    private String arText;

    public String getArText() {

        return this.arText;
    }

    public void setArText(String arText) {

        this.arText = arText;
    }

    /**
     * Holds value of property arHtml.
     */
    private String arHtml;

    public String getArHtml() {

        return this.arHtml;
    }

    public void setArHtml(String arHtml) {

        this.arHtml = arHtml;
    }

    /**
     * Holds value of property doForward.
     */
    private boolean doForward;

    public boolean isDoForward() {

        return this.doForward;
    }

    public void setDoForward(boolean doForward) {

        this.doForward = doForward;
    }

    /**
     * Holds value of property doAutoresponder.
     */
    private boolean doAutoresponder;

    public boolean isDoAutoresponder() {

        return this.doAutoresponder;
    }

    public void setDoAutoresponder(boolean doAutoresponder) {

        this.doAutoresponder = doAutoresponder;
    }
    
    /**
     * Holds value of property changedate.
     */
    private java.sql.Timestamp changedate;

    public java.sql.Timestamp getChangedate() {

    	return this.changedate;
    }

    public void setChangedate(java.sql.Timestamp changedate) {

    	this.changedate = changedate;
    }
    
    /**
     * Holds value of property doSubscribe.
     */
    private boolean doSubscribe;

    public boolean isDoSubscribe() {

        return this.doSubscribe;
    }

    public void setDoSubscribe(boolean doSubscribe) {

        this.doSubscribe = doSubscribe;
    }
    
    /**
     * Holds value of property mailinglistID.
     */
    private int mailinglistID;

    public int getMailinglistID() {
        return this.mailinglistID;
    }

    public void setMailinglistID(int mailinglistID) {
        this.mailinglistID = mailinglistID;
    }
    
    /**
     * Holds value of property userformID.
     */
    private int userformID;

    public int getUserformID() {
        return this.userformID;
    }

    public void setUserformID(int userformID) {
        this.userformID = userformID;
    }

}
