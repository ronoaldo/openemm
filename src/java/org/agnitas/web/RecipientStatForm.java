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

package org.agnitas.web;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;


public class RecipientStatForm extends ActionForm {
    
    private static final long serialVersionUID = 3117790653509228220L;
	private int companyID;    
    private int action;
    private int targetID;
    private int mailingListID;
    private int numBounce;
    private int numRecipients;
    private int numOptout;
    private int numActive;
    private int numHTML;
    private int numOffline;
    private int numText;
    private int numUnbound;
    private Hashtable hashOptIn;
    private Hashtable hashOptOut;
    private Hashtable hashBounce;
    private String month;
    private String cvsfile;
    
    /** 
     * Holds value of property blueOptout. 
     */
    private int blueOptout;    
    
    /**
     * Holds value of property blueBounce. 
     */
    private int blueBounce;    
    
    /**
     * Holds value of property blueActive. 
     */
    private int blueActive;
    
    /**
     * Holds value of property blueText. 
     */
    private int blueText;
    
    /**
     * Holds value of property blueHTML. 
     */
    private int blueHTML;
    
    /**
     * Holds value of property blueOffline. 
     */
    private int blueOffline;
    
    private int blueUnbound;
    
    /**
     * Holds value of property mediaType. 
     */
    private int mediaType;
    
    /**
     * Validate the properties that have been set from this HTTP request,
     * and return an <code>ActionErrors</code> object that encapsulates any
     * validation errors that have been found.  If no errors are found, return
     * <code>null</code> or an <code>ActionErrors</code> object with no
     * recorded error messages.
     * 
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     * @return errors
     */
    
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        
        ActionErrors errors = new ActionErrors();
        return errors;
    }

    /**
     * Getter for property targetID.
     *
     * @return Value of property targetID.
     */
    public int getTargetID() {
        return this.targetID;
    }
    
    /**
     * Setter for property targetID.
     *
     * @param targetID New value of property targetID.
     */
    public void setTargetID(int targetID) {
        this.targetID = targetID;
    }
    
    /**
     * Getter for property action.
     *
     * @return Value of property action.
     */
    public int getAction() {
        return this.action;
    }
    
    /**
     * Setter for property action.
     *
     * @param action New value of property action.
     */
    public void setAction(int action) {
        this.action = action;
    }
    
    /**
     * Getter for property csvfile.
     *
     * @return Value of property csvfile.
     */
    public String getCvsfile() {
        return this.cvsfile;
    }
    
    /**
     * Setter for property cvsfile.
     *
     * @param cvsfile New value of property csvfile.
     */
    public void setCvsfile(String cvsfile) {
        this.cvsfile = cvsfile;
    }
    
    /**
     * Getter for property numBounce.
     *
     * @return Value of property numBounce.
     */
    public int getNumBounce() {
        return this.numBounce;
    }
    
    /**
     * Setter for property numBounce.
     *
     * @param numBounce New value of property numBounce.
     */
    public void setNumBounce(int numBounce) {
        this.numBounce = numBounce;
    }
    
    /**
     * Getter for property numRecipients.
     *
     * @return Value of property numRecipients.
     */
    public int getNumRecipients() {
        return this.numRecipients;
    }
    
    /**
     * Setter for property numRecipients.
     *
     * @param numRecipients New value of property numRecipients.
     */
    public void setNumRecipients(int numRecipients) {
        this.numRecipients = numRecipients;
    }
    
    /**
     * Getter for property numOptout.
     *
     * @return Value of property numOptout.
     */
    public int getNumOptout() {
        return this.numOptout;
    }
    
    /**
     * Setter for property numOptout.
     *
     * @param numOptout New value of property numOptout.
     */
    public void setNumOptout(int numOptout) {
        this.numOptout = numOptout;
    }
    
    /**
     * Getter for property mailingListID.
     *
     * @return Value of property mailingListID.
     */
    public int getMailingListID() {
        return this.mailingListID;
    }
    
    /**
     * Setter for property mailingListID.
     *
     * @param mailingListID New value of property mailingListID.
     */
    public void setMailingListID(int mailingListID) {
        this.mailingListID = mailingListID;
    }
    
    /**
     * Getter for property numActive.
     *
     * @return Value of property numActive.
     */
    public int getNumActive() {
        return this.numActive;
    }
    
    /**
     * Setter for property numActive.
     *
     * @param numActive New value of property numActive.
     */
    public void setNumActive(int numActive) {
        this.numActive = numActive;
    }
    
    /**
     * Getter for property numText.
     *
     * @return Value of property numText.
     */
    public int getNumText() {
        return this.numText;
    }
    
    /**
     * Setter for property numText.
     *
     * @param numText New value of property numText.
     */
    public void setNumText(int numText) {
        this.numText = numText;
    }
    
    /**
     * Getter for property numHTML.
     *
     * @return Value of property numHTML.
     */
    public int getNumHTML() {
        return this.numHTML;
    }
    
    /**
     * Setter for property numHTML.
     *
     * @param numHTML New value of property numHTML.
     */
    public void setNumHTML(int numHTML) {
        this.numHTML = numHTML;
    }
    
    /**
     * Getter for property numOffline.
     *
     * @return Value of property numOffline.
     */
    public int getNumOffline() {
        return this.numOffline;
    }
    
    /**
     * Setter for property numOffline.
     *
     * @param numOffline New value of property numOffline.
     */
    public void setNumOffline(int numOffline) {
        this.numOffline = numOffline;
    }
    
    /**
     * Getter for property hashOptIn.
     *
     * @return Value of property hashoptIn.
     */
    public Hashtable getHashOptIn() {
        return this.hashOptIn;
    }
    
    /**
     * Setter for property hashOptIn.
     *
     * @param hashOptIn New value of property hashOptIn.
     */
    public void setHashOptIn(Hashtable hashOptIn) {
        this.hashOptIn = hashOptIn;
    }
    
    /**
     * Getter for property hashOptOut.
     *
     * @return Value of property hashOptOut.
     */
    public Hashtable getHashOptOut() {
        return this.hashOptOut;
    }
    
    /**
     * Setter for property hashOptOut.
     *
     * @param hashOptOut New value of property hashOptOut.
     */
    public void setHashOptOut(Hashtable hashOptOut) {
        this.hashOptOut = hashOptOut;
    }
    
    /**
     * Getter for property hashBounce.
     *
     * @return Value of property hashBounce.
     */
    public Hashtable getHashBounce() {
        return this.hashBounce;
    }
    
    /**
     * Setter for property hashBounce.
     *
     * @param hashBounce New value of property hashBounce.
     */
    public void setHashBounce(Hashtable hashBounce) {
        this.hashBounce = hashBounce;
    }
    
    /**
     * Getter for property month.
     *
     * @return Value of property month.
     */
    public String getMonth() {
        return this.month;
    }
    
    /**
     * Setter for property month.
     *
     * @param month New value of property month.
     */
    public void setMonth(String month) {
        this.month = month;
    }
    
    /**
     * Getter for property companyID.
     *
     * @return Value of property companyID.
     */
    public int getCompanyID() {
        return this.companyID;
    }
    
    /**
     * Setter for property companyID.
     *
     * @param companyID New value of property companyID.
     */
    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }
    
    /**
     * Getter for property blueOptOut.
     *
     * @return Value of property blueOptOut.
     */
    public int getBlueOptout() {
        return this.blueOptout;
    }
    
    /**
     * Setter for property blueOptout.
     *
     * @param blueOptout New value of property blueOptout.
     */
    public void setBlueOptout(int blueOptout) {
        this.blueOptout = blueOptout;
    }
    
    /**
     * Getter for property blueBounce.
     *
     * @return Value of property blueBounce.
     */
    public int getBlueBounce() {
        return this.blueBounce;
    }
    
    /**
     * Setter for property blueBounce.
     *
     * @param blueBounce New value of property blueBounce.
     */
    public void setBlueBounce(int blueBounce) {
        this.blueBounce = blueBounce;
    }
    
    /**
     * Getter for property blueActive.
     *
     * @return Value of property blueActive.
     */
    public int getBlueActive() {
        return this.blueActive;
    }
    
    /**
     * Setter for property blueActive.
     *
     * @param blueActive New value of property blueActive.
     */
    public void setBlueActive(int blueActive) {
        this.blueActive = blueActive;
    }
    
    /**
     * Getter for property blueText.
     *
     * @return Value of property blueText.
     */
    public int getBlueText() {
        return this.blueText;
    }
    
    /**
     * Setter for property blueText.
     *
     * @param blueText New value of property blueText.
     */
    public void setBlueText(int blueText) {
        this.blueText = blueText;
    }
    
    /**
     * Getter for property blueHTML.
     *
     * @return Value of property blueHTML.
     */
    public int getBlueHTML() {
        return this.blueHTML;
    }
    
    /**
     * Setter for property blueHTML.
     *
     * @param blueHTML New value of property blueHTML.
     */
    public void setBlueHTML(int blueHTML) {
        this.blueHTML = blueHTML;
    }
    
    /**
     * Getter for property blueOffline.
     *
     * @return Value of property blueOffline.
     */
    public int getBlueOffline() {
        return this.blueOffline;
    }
    
    /**
     * Setter for property blueOffline.
     *
     * @param blueOffline New value of property blueOffline.
     */
    public void setBlueOffline(int blueOffline) {
        this.blueOffline = blueOffline;
    }
    
    /**
     * Getter for property mediaType.
     *
     * @return Value of property mediaType.
     */
    public int getMediaType() {
        return this.mediaType;
    }
    
    /**
     * Setter for property mediaType.
     *
     * @param mediaType New value of property mediaType.
     */
    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

	public void setNumUnbound(int numUnbound) {
		this.numUnbound = numUnbound;		
	}  
	
	public int getNumUnbound() {
		return this.numUnbound;
	}
	
	public void setBlueUnbound(int blueUnbound) {
		this.blueUnbound = blueUnbound;
	}
	
	public int getBlueUnbound() {
		return this.blueUnbound;
	}
}
