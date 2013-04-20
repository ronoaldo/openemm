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
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

import org.agnitas.web.forms.StrutsFormBase;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;


public class MailingStatForm extends StrutsFormBase {

    private static final long serialVersionUID = 8720023475879373134L;
	private int action;
    private int mailingID;    
    private int targetID;
    private int clickSubscribers;
    private int totalSubscribers;
    private int clicks;
    private int openedMails;
    private int optOuts;
    private int bounces;
    private int urlID;
    private int nextTargetID;
    private int maxblue;
    private int maxSubscribers;
    
    private boolean sent;    
    private boolean netto;
    private boolean statInProgress;
    private boolean statReady;
    
    private String aktURL;
    private String startdate;
    private String csvfile;
    private String mailingShortname;    
    private String firstdate;

    private Hashtable values;
    private Hashtable statValues;
    private Hashtable urlNames;
    private Hashtable urlShortnames;

    private LinkedList targetIDs;
    private LinkedList clickedUrls;
    private LinkedList notRelevantUrls;

    /**
     * Holds value of property maxNRblue.
     */
    private int maxNRblue;    
    
    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {

        this.targetID = 0;
        
        
        //Locale aLoc=(Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
        //MessageResources text=this.getServlet().getResources();
    }

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
    public ActionErrors formSpecificValidate(ActionMapping mapping,
                                 HttpServletRequest request) {

        ActionErrors errors = new ActionErrors();
        
        if(this.action == MailingStatAction.ACTION_MAILINGSTAT) {
            if (this.isStatInProgress()) {
                this.action = MailingStatAction.ACTION_SPLASH;
            }       
        }
        
        if(this.action == MailingStatAction.ACTION_OPENEDSTAT) {
            if (this.isStatInProgress()) {
                this.action = MailingStatAction.ACTION_OPENEDSTAT_SPLASH;
            }
        }
        

        return errors;
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
     * Setter for property action.
     *
     * @param action New value of property action.
     */
    public void setAction(int action) {
        this.action = action;
    }

    /**
     * Setter for property csvfile.
     *
     * @param csvfile New value of property csvfile.
     */
    public void setCsvfile(String csvfile) {
        this.csvfile = csvfile;
    }
    
    /**
     * Setter for property netto.
     *
     * @param netto New value of property netto.
     */
    public void setNetto(boolean netto) {
        this.netto = netto;
    }

    /**
     * Setter for property clickSubscribers.
     *
     * @param clickSubscribers New value of property clickSubscribers.
     */
    public void setClickSubscribers(int clickSubscribers) {
        this.clickSubscribers = clickSubscribers;
    }
    
    /**
     * Setter for property clicks.
     *
     * @param clicks New value of property clicks.
     */
    public void setClicks(int clicks) {
        this.clicks = clicks;
    }
    
    /**
     * Setter for property openedMails.
     *
     * @param openedMails New value of property openendMails.
     */
    public void setOpenedMails(int openedMails) {
        this.openedMails = openedMails;
    }

    /**
     * Setter for property optOuts.
     *
     * @param optOuts New value of property optOuts.
     */
    public void setOptOuts(int optOuts) {
        this.optOuts = optOuts;
    }
    
    /**
     * Setter for property bounces.
     *
     * @param bounces New value of property bounces.
     */
    public void setBounces(int bounces) {
        this.bounces = bounces;
    }
    
    /**
     * Setter for property totalSubscribers.
     *
     * @param totalSubscribers New value of property totalSubscribers.
     */
    public void setTotalSubscribers(int totalSubscribers) {
        this.totalSubscribers = totalSubscribers;
    }
    
    /**
     * Setter for property aktURL.
     *
     * @param aktURL New value of property aktURL.
     */
    public void setAktURL(String aktURL) {
        this.aktURL = aktURL;
    }
    
    /**
     * Setter for property startdate.
     *
     * @param startdate New value of property startdate.
     */
    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }
    
    /**
     * Setter for property values.
     *
     * @param values New value of property values.
     */
    public void setValues(Hashtable values) {
        this.values = values;
    }    
    
    /**
     * Setter for property mailingID.
     *
     * @param mailingID New value of property mailingID.
     */
    public void setMailingID(int mailingID) {
        this.mailingID = mailingID;
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
     * Getter for property action.
     *
     * @return Value of property action.
     */
    public int getAction() {
        return this.action;
    }
    
    /**
     * Getter for property csvfile.
     *
     * @return Value of property csvfile.
     */
    public String getCsvfile() {
        return this.csvfile;
    }
    
    /**
     * Getter for property netto.
     *
     * @return Value of property netto.
     */
    public boolean isNetto() {
        return this.netto;
    }
    
    /**
     * Getter for property clickSubscribers.
     *
     * @return Value of property clickSubscribers.
     */
    public int getClickSubscribers() {
        return this.clickSubscribers;
    }
    
    /**
     * Getter for property clicks.
     *
     * @return Value of property clicks.
     */
    public int getClicks() {
        return this.clicks;
    }
    
    /**
     * Getter for property openedMails.
     *
     * @return Value of property openendMails.
     */
    public int getOpenedMails() {
        return this.openedMails;
    }
    
    /**
     * Getter for property optOuts.
     *
     * @return Value of property optOuts.
     */
    public int getOptOuts() {
        return this.optOuts;
    }
    
    /**
     * Getter for property bonces.
     *
     * @return Value of property bounces.
     */
    public int getBounces() {
        return this.bounces;
    }
    
    /**
     * Getter for property totalSubscribers.
     *
     * @return Value of property totalSubscribers.
     */
    public int getTotalSubscribers() {
        return this.totalSubscribers;
    }
    
    /**
     * Getter for property aktURL.
     *
     * @return Value of property aktURL.
     */
    public String getAktURL() {
        return this.aktURL;
    }
    
    /**
     * Getter for property startdate.
     *
     * @return Value of property startdate.
     */
    public String getStartdate() {
        return this.startdate;
    }
    
    /**
     * Getter for property values.
     *
     * @return Value of property values.
     */
    public Hashtable getValues() {
        return this.values;
    }    
    
    /**
     * Getter for property mailingID.
     *
     * @return Value of property mailingID.
     */
    public int getMailingID() {
        return this.mailingID;
    }

    /**
     * Getter for property mailingShortname.
     *
     * @return Value of property mailingShortname.
     */
    public String getMailingShortname() {
        return this.mailingShortname;
    }
    
    /**
     * Setter for property mailingShortname.
     *
     * @param mailingShortname New value of property mailingShortname.
     */
    public void setMailingShortname(String mailingShortname) {
        this.mailingShortname = mailingShortname;
    }
    
    /**
     * Getter for property sent.
     *
     * @return Value of property sent.
     */
    public boolean isSent() {
        return this.sent;
    }
    
    /**
     * Setter for property sent.
     *
     * @param sent New value of property sent.
     */
    public void setSent(boolean sent) {
        this.sent = sent;
    }
    
    /**
     * Getter for property urlID.
     *
     * @return Value of property urlID.
     */
    public int getUrlID() {
        return this.urlID;
    }
    
    /**
     * Setter for property urlID.
     *
     * @param urlID New value of property urlID.
     */
    public void setUrlID(int urlID) {
        this.urlID = urlID;
    }
    
    /**
     * Getter for property maxblue.
     *
     * @return Value of property maxblue.
     */
    public int getMaxblue() {
        return this.maxblue;
    }
    
    /**
     * Setter for property maxblue.
     *
     * @param maxblue New value of property maxblue.
     */
    public void setMaxblue(int maxblue) {
        this.maxblue = maxblue;
    }
    
    /**
     * Getter for property firstdate.
     *
     * @return Value of property firstdate.
     */
    public String getFirstdate() {
        return this.firstdate;
    }
    
    /**
     * Setter for property firstdate.
     *
     * @param firstdate New value of property firstdate.
     */
    public void setFirstdate(String firstdate) {
        this.firstdate = firstdate;
    }
    
    /**
     * Getter for property targetGroups.
     *
     * @return Value of property targetGroups.
     */
    public LinkedList getTargetIDs() {
        return this.targetIDs;
    }
    
    /**
     * Setter for property targetGroups.
     *
     * @param targetIDs New value of property targetIDs.
     */
    public void setTargetIDs(LinkedList targetIDs) {
        this.targetIDs = targetIDs;
    }
    
    /**
     * Getter for property nextTargetID.
     *
     * @return Value of property nextTargetID.
     */
    public int getNextTargetID() {
        return this.nextTargetID;
    }
    
    /**
     * Setter for property nextTargetID.
     *
     * @param nextTargetID New value of property nextTargetID.
     */
    public void setNextTargetID(int nextTargetID) {
        this.nextTargetID = nextTargetID;
    }
    
    /**
     * Getter for property statValues.
     *
     * @return Value of property statValues.
     */
    public Hashtable getStatValues() {
        return this.statValues;
    }
    
    /**
     * Setter for property statValues.
     *
     * @param statValues New value of property statValues.
     */
    public void setStatValues(Hashtable statValues) {
        this.statValues = statValues;
    }
    
    /**
     * Getter for property urlNames.
     *
     * @return Value of property urlNames.
     */
    public Hashtable getUrlNames() {
        return this.urlNames;
    }
    
    /**
     * Setter for property urlNames.
     *
     * @param urlNames New value of property urlNames.
     */
    public void setUrlNames(Hashtable urlNames) {
        this.urlNames = urlNames;
    }
    
    /**
     * Getter for property urlShortnames.
     *
     * @return Value of property urlShortnames.
     */
    public Hashtable getUrlShortnames() {
        return this.urlShortnames;
    }
    
    /**
     * Setter for property urlShortnames.
     *
     * @param urlShortnames New value of property urlShortnames.
     */
    public void setUrlShortnames(Hashtable urlShortnames) {
        this.urlShortnames = urlShortnames;
    }
    
    /**
     * Getter for property maxSubscribers.
     *
     * @return Value of property maxSubscribers.
     */
    public int getMaxSubscribers() {
        return this.maxSubscribers;
    }
    
    /**
     * Setter for property maxSubscribers.
     *
     * @param maxSubscribers New value of property maxSubscribers.
     */
    public void setMaxSubscribers(int maxSubscribers) {
        this.maxSubscribers = maxSubscribers;
    }
    
    /**
     * Getter for property statInProgress.
     * This Method checks, if the statistic is generated at the moment.
     * If true, you will have to wait until its ready.     
     * @return Value of property statInProgress.
     */
    public boolean isStatInProgress() {
        return this.statInProgress;
    }
    
    /**
     * Setter for property statInProgress.
     *
     * @param statInProgress New value of property statInProgress.
     */
    public void setStatInProgress(boolean statInProgress) {
        this.statInProgress = statInProgress;
    }
    
    /**
     * Getter for property statReady.
     *
     * @return Value of property statReady.
     */
    public boolean isStatReady() {
        return this.statReady;
    }
    
    /**
     * Setter for property statReady.
     *
     * @param statReady New value of property statReady.
     */
    public void setStatReady(boolean statReady) {
        this.statReady = statReady;
    }
    
    /**
     * Getter for property clickedUrls.
     *
     * @return Value of property clickedUrls.
     */
    public LinkedList getClickedUrls() {
        return this.clickedUrls;
    }
    
    /**
     * Setter for property clickedUrls.
     *
     * @param clickedUrls New value of property clickedUrls.
     */
    public void setClickedUrls(LinkedList clickedUrls) {
        this.clickedUrls = clickedUrls;
    }
    
    /**
     * Getter for property notRelevantUrls.
     *
     * @return Value of property notRelevantUrls.
     */
    public LinkedList getNotRelevantUrls() {
        return this.notRelevantUrls;
    }
    
    /**
     * Setter for property notRelevantUrls.
     *
     * @param notRelevantUrls New value of property notRelevantUrls.
     */
    public void setNotRelevantUrls(LinkedList notRelevantUrls) {
        this.notRelevantUrls = notRelevantUrls;
    }
    
    /**
     * Getter for property maxNRblue.
     *
     * @return Value of property maxNRblue.
     */
    public int getMaxNRblue() {
        return this.maxNRblue;
    }
    
    /**
     * Setter for property maxNRblue.
     *
     * @param maxNRblue New value of property maxNRblue.
     */
    public void setMaxNRblue(int maxNRblue) {
        this.maxNRblue = maxNRblue;
    } 
}
