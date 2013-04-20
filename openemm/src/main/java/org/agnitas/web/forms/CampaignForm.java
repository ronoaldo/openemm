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

package org.agnitas.web.forms;

import org.agnitas.util.SafeString;
import org.agnitas.web.CampaignAction;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import javax.servlet.http.HttpServletRequest;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Locale;

public class CampaignForm extends StrutsFormBase {

    private static final long serialVersionUID = -7580249894965605708L;
	protected String  description;
    protected String  shortname;
    private int     action;
    private int     campaignID;
    private int     opened;
    private int     optouts;
    private int     bounces;
    private int     subscribers;
    private int     targetID;
    private boolean netto;
    protected int previousAction;
    

    /**
     * Holds value of property statInProgress.
     */
    private boolean statInProgress;

    /**
     * Holds value of property statReady.
     */
    private boolean statReady;

    /**
     * Holds value of property mailingData.
     */
    private Hashtable mailingData;
    
    /**
     * Holds a Linked List with sorted Keys for the mailingData Hashmap.
     * The list is sorted in a way, that a request to the mailingData Hashmap
     * will return a Date-sorted result (eg. latest first).
     */
    private LinkedList<Number> sortedKeys;

    /**
     * Holds value of property clicks.
     */
    private int clicks;

    /**
     * Holds value of property maxOpened.
     */
    private int maxOpened;

    /**
     * Holds value of property maxOptouts.
     */
    private int maxOptouts;

    /**
     * Holds value of property maxBounces.
     */
    private int maxBounces;

    /**
     * Holds value of property maxSubscribers.
     */
    private int maxSubscribers;

    /**
     * Holds value of property maxClicks.
     */
    private int maxClicks;

    /**
     * Holds value of property csvfile.
     */
    private String csvfile;

    /**
     * Creates a new instance of MailinglistForm
     */
    public CampaignForm() {
    }

    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {

        super.reset(mapping, request);
        this.shortname= SafeString.getLocaleString("default.shortname", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY));
        this.description= SafeString.getLocaleString("default.description", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY));
           
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
     */
    public ActionErrors formSpecificValidate(ActionMapping mapping,
    HttpServletRequest request) {

        ActionErrors errors = new ActionErrors();

        if(action==CampaignAction.ACTION_SAVE) {
            if(this.shortname.length()<3)
                errors.add("shortname", new ActionMessage("error.nameToShort"));

        }

        if(action==CampaignAction.ACTION_STAT) {
            if (this.isStatInProgress()) {
                this.action = CampaignAction.ACTION_SPLASH;
            }

        }
        return errors;
    }

    @Override
    protected ActionErrors checkForHtmlTags(HttpServletRequest request) {
        if(action != CampaignAction.ACTION_VIEW_WITHOUT_LOAD){
            return super.checkForHtmlTags(request);
        }
        return new ActionErrors();
    }

    /**
     * Getter for property descritpion.
     *
     * @return Value of property description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Setter for property description.
     *
     * @param description New value of property description.
     */
    public void setDescription(String description) {
        this.description = description;
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
     * Getter for property shortname.
     *
     * @return Value of property shortname.
     */
    public String getShortname() {
        return this.shortname;
    }

    /**
     * Setter for property shortname.
     *
     * @param shortname New value of property shortname.
     */
    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    /**
     * Getter for property campaignID.
     *
     * @return Value of property campaignID.
     */
    public int getCampaignID() {
        return this.campaignID;
    }

    /**
     * Setter for property campaignID.
     *
     * @param campaignID New value of property campaignID.
     */
    public void setCampaignID(int campaignID) {
        this.campaignID = campaignID;
    }

    /**
     * Getter for property opened.
     *
     * @return Value of property opened.
     */
    public int getOpened() {
        return this.opened;
    }

    /**
     * Setter for property opened.
     *
     * @param opened New value of property opened.
     */
    public void setOpened(int opened) {
        this.opened = opened;
    }

    /**
     * Getter for property optouts.
     *
     * @return Value of property optouts.
     */
    public int getOptouts() {
        return this.optouts;
    }

    /**
     * Setter for property optouts.
     *
     * @param optouts New value of property optouts.
     */
    public void setOptouts(int optouts) {
        this.optouts = optouts;
    }

    /**
     * Getter for property bounces.
     *
     * @return Value of property bounces.
     */
    public int getBounces() {
        return this.bounces;
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
     * Getter for property subscribers.
     *
     * @return Value of property subscribers.
     */
    public int getSubscribers() {
        return this.subscribers;
    }

    /**
     * Setter for property subscribers.
     *
     * @param subscribers New value of property subscribers.
     */
    public void setSubscribers(int subscribers) {
        this.subscribers = subscribers;
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
     * Setter for property netto.
     *
     * @param netto New value of property netto.
     */
    public void setNetto(boolean netto) {
        this.netto = netto;
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
     * Getter for property statInProgress.
     *
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
     * Getter for property mailingData.
     *
     * @return Value of property mailingData.
     */
    public Hashtable getMailingData() {
        return this.mailingData;
    }

    /**
     * Setter for property mailingData.
     *
     * @param mailingData New value of property mailingData.
     */
    public void setMailingData(Hashtable mailingData) {
        this.mailingData = mailingData;
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
     * Setter for property clicks.
     *
     * @param clicks New value of property clicks.
     */
    public void setClicks(int clicks) {
        this.clicks = clicks;
    }

    /**
     * Getter for property maxOpened.
     *
     * @return Value of property maxOpened.
     */
    public int getMaxOpened() {
        return this.maxOpened;
    }

    /**
     * Setter for property maxOpened.
     *
     * @param maxOpened New value of property maxOpened.
     */
    public void setMaxOpened(int maxOpened) {
        this.maxOpened = maxOpened;
    }

    /**
     * Getter for property maxOptouts.
     *
     * @return Value of property maxOptouts.
     */
    public int getMaxOptouts() {
        return this.maxOptouts;
    }

    /**
     * Setter for property maxOptouts.
     *
     * @param maxOptouts New value of property maxOptouts.
     */
    public void setMaxOptouts(int maxOptouts) {
        this.maxOptouts = maxOptouts;
    }

    /**
     * Getter for property maxBounces.
     *
     * @return Value of property maxBounces.
     */
    public int getMaxBounces() {
        return this.maxBounces;
    }

    /**
     * Setter for property maxBounces.
     *
     * @param maxBounces New value of property maxBounces.
     */
    public void setMaxBounces(int maxBounces) {
        this.maxBounces = maxBounces;
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
     * Getter for property maxClicks.
     *
     * @return Value of property maxClicks.
     */
    public int getMaxClicks() {
        return this.maxClicks;
    }

    /**
     * Setter for property maxClicks.
     *
     * @param maxClicks New value of property maxClicks.
     */
    public void setMaxClicks(int maxClicks) {
        this.maxClicks = maxClicks;
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
     * Setter for property csvfile.
     *
     * @param csvfile New value of property csvfile.
     */
    public void setCsvfile(String csvfile) {
        this.csvfile = csvfile;
    }

	/**
	 * returns a list with sorted keys from the mailingData Hashmap!
	 * DANGER! the calculation of this is done in the CampaignAction!!!!
	 * NOWHERE ELSE! REMEMBER THAT! 
	 * 
	 * @return the sortedKeys
	 */
	public LinkedList<Number> getSortedKeys() {
		return sortedKeys;
	}

	/**
	 * set the sorted Keys. Use this only if you know what you are doing!
	 * Normaly this ist set ONLY from the Campaign Action! 
	 */
	public void setSortedKeys(LinkedList<Number> sortedKeys) {
		this.sortedKeys = sortedKeys;
	}

    public int getPreviousAction() {
        return previousAction;
    }

    public void setPreviousAction(int previousAction) {
        this.previousAction = previousAction;
    }
}
