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

package org.agnitas.stat;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author mhe
 */
public interface MailingStat extends Serializable {
   /**
    * Deletes property adminClicks from database.
    */
    boolean cleanAdminClicks();

    /**
     * Getter for property aktURL.
     * 
     * @return Value of property aktURL.
     */
    String getAktURL();

    /**
     * Getter for property bounceStat from database.
     * 
     * @return Value of property bounceStat from database.
     */
    boolean getBounceStatFromDB(HttpServletRequest request);

    /**
     * Getter for property bounces.
     * 
     * @return Value of property bounces.
     */
    int getBounces();

    /**
     * Getter for property clickSubsribers.
     * 
     * @return Value of property clickSubscribers.
     */
    int getClickSubscribers();

    /**
     * Getter for property clickedUrls.
     * 
     * @return Value of property clickedUrls.
     */
    LinkedList getClickedUrls();

    /**
     * Getter for property clicks.
     * 
     * @return Value of property clicks.
     */
    int getClicks();

    /**
     * Getter for property csvfile.
     * 
     * @return Value of property csvfile.
     */
    String getCsvfile();

    /**
     * Getter for property dayStat from database.
     *
     * @return Value of property dayStat.
     */
    boolean getDayStatFromDB(HttpServletRequest request);

    /**
     * Getter for property firstdate.
     * 
     * @return Value of property firstdate.
     */
    String getFirstdate();

    /**
     * Getter for property mailingID.
     * 
     * @return Value of property mailingID.
     */
    int getMailingID();

    /**
     * Getter for property mailingShortname.
     * 
     * @return Value of property mailingShortname.
     */
    String getMailingShortname();

    /**
     * Getter for property mailingStat from database.
     *
     * @return Value of property mailingStat.
     */
    boolean getMailingStatFromDB(Locale aLocale);

    /**
     * Getter for property naxNRblue.
     * 
     * @return Value of property naxNRblue.
     */
    int getMaxNRblue();

    /**
     * Getter for property maxSubscribers.
     * 
     * @return Value of property maxSubscribers.
     */
    int getMaxSubscribers();

    /**
     * Getter for property mayblue.
     * 
     * @return Value of property maxblue.
     */
    int getMaxblue();

    /**
     * Getter for property notRelevantUrls.
     * 
     * @return Value of property notRelevantUrls.
     */
    LinkedList getNotRelevantUrls();

    /**
     * Getter for property openedMails.
     * 
     * @return Value of property openedMails.
     */
    int getOpenedMails();

    /**
     * Getter for property openedStat from database.
     *
     * @return Value of property openedStat.
     */
    boolean getOpenedStatFromDB(HttpServletRequest request);

    /**
     * Getter for property optOuts.
     * 
     * @return Value of property optOuts.
     */
    int getOptOuts();

    /**
     * Getter for property startdate.
     * 
     * @return Value of property startdate.
     */
    String getStartdate();

    /**
     * Getter for property statValues.
     * 
     * @return Value of property statvalues.
     */
    Hashtable getStatValues();

    /**
     * Getter for property targetID.
     * 
     * @return Value of property targetID.
     */
    int getTargetID();

    /**
     * Getter for property targetIDs.
     * 
     * @return Value of property targetIDs.
     */
    LinkedList getTargetIDs();

    /**
     * Getter for property targetName.
     * 
     * @return Value of property targetName.
     */
    String getTargetName();

    /**
     * Getter for property totalSubscribers.
     * 
     * @return Value of property totalSubscribers.
     */
    int getTotalSubscribers();

    /**
     * Getter for property urlID.
     * 
     * @return Value of property urlID.
     */
    int getUrlID();

    /**
     * Getter for property urlShortnames.
     * 
     * @return Value of property urlShortnames.
     */
    Hashtable getUrlShortnames();

    /**
     * Getter for property urlNames.
     * 
     * @return Value of property urlNames.
     */
    Hashtable getUrls();

    /**
     * Getter for property values.
     * 
     * @return Value of property values.
     */
    Hashtable getValues();

    /**
     * Getter for property weekStat from database.
     *
     * @return Value of property weekStat.
     */
    boolean getWeekStatFromDB(HttpServletRequest request);

    /**
     * Getter for property clicked.
     * 
     * @return Value of property clicked.
     */
    boolean isClicked();

    /**
     * Getter for property netto.
     * 
     * @return Value of property netto.
     */
    boolean isNetto();

    /**
     * Getter for property sent.
     * 
     * @return Value of property sent.
     */
    boolean isSent();

    /**
     * Setter for property aktURL.
     * 
     * @param aktURL New value of property aktURL.
     */
    void setAktURL(String aktURL);

    /**
     * Setter for property bounces.
     * 
     * @param bounces New value of property bounces.
     */
    void setBounces(int bounces);

    /**
     * Setter for property clickSubscribers.
     * 
     * @param clickSubscribers New value of property clickSubscribers.
     */
    void setClickSubscribers(int clickSubscribers);

    /**
     * Setter for property clicked.
     * 
     * @param clicked New value of property clicked.
     */
    void setClicked(boolean clicked);

    /**
     * Setter for property clickedUrls.
     * 
     * @param clickedUrls New value of property clickedUrls.
     */
    void setClickedUrls(LinkedList clickedUrls);

    /**
     * Setter for property clicks.
     * 
     * @param clicks New value of property clicks.
     */
    void setClicks(int clicks);

    /**
     * Setter for property companyID.
     * 
     * @param id New value of property companyID.
     */
    void setCompanyID(int id);

    /**
     * Setter for property csvfile.
     * 
     * @param file New value of property csvfile.
     */
    void setCsvfile(String file);

    /**
     * Setter for property firstdate.
     * 
     * @param firstdate New value of property firstdate.
     */
    void setFirstdate(String firstdate);

    /**
     * Setter for property mailingID.
     * 
     * @param mailingID New value of property mailingID.
     */
    void setMailingID(int mailingID);

    /**
     * Setter for property mailingShortname.
     * 
     * @param mailingShortname New value of property mailingShortname.
     */
    void setMailingShortname(String mailingShortname);

    /**
     * Setter for property naxNRblue.
     * 
     * @param maxNRblue New value of property maxNRblue.
     */
    void setMaxNRblue(int maxNRblue);

    /**
     * Setter for property maxSubscribers.
     * 
     * @param maxSubscribers New value of property maxSubscribers.
     */
    void setMaxSubscribers(int maxSubscribers);

    /**
     * Setter for property maxblue.
     * 
     * @param maxblue New value of property maxblue.
     */
    void setMaxblue(int maxblue);

    /**
     * Setter for property netto.
     * 
     * @param netto New value of property netto.
     */
    void setNetto(boolean netto);

    /**
     * Setter for property notRelevantUrls.
     * 
     * @param notRelevantUrls New value of property notRelevantUrls.
     */
    void setNotRelevantUrls(LinkedList notRelevantUrls);

    /**
     * Setter for property openedMails.
     * 
     * @param openedMails New value of property openedMails.
     */
    void setOpenedMails(int openedMails);

    /**
     * Setter for property optOuts.
     * 
     * @param optOuts New value of property optOuts.
     */
    void setOptOuts(int optOuts);

    /**
     * Setter for property sent.
     * 
     * @param sent New value of property sent.
     */
    void setSent(boolean sent);

    /**
     * Setter for property startdate.
     * 
     * @param startdate New value of property startdate.
     */
    void setStartdate(String startdate);

    /**
     * Setter for property statValues.
     * 
     * @param statValues New value of property statValues.
     */
    void setStatValues(Hashtable statValues);

    /**
     * Setter for property targetID.
     * 
     * @param targetID New value of property targetID.
     */
    void setTargetID(int targetID);

    /**
     * Setter for property targetIDs.
     * 
     * @param targetIDs New value of property targetIDs.
     */
    void setTargetIDs(LinkedList targetIDs);

    /**
     * Setter for property targetName.
     * 
     * @param targetName New value of property targetName.
     */
    void setTargetName(String targetName);

    /**
     * Setter for property totalSubscribers.
     * 
     * @param totalSubscribers New value of property totalSubscribers.
     */
    void setTotalSubscribers(int totalSubscribers);

    /**
     * Setter for property urlID.
     * 
     * @param urlID New value of property urlID.
     */
    void setUrlID(int urlID);

    /**
     * Setter for property urlShortnames.
     * 
     * @param urlShortnames New value of property urlShortnames.
     */
    void setUrlShortnames(Hashtable urlShortnames);

    /**
     * Setter for property urlNames.
     * 
     * @param urls New value of property urls.
     */
    void setUrls(Hashtable urls);
    
    public boolean getOpenTimeStatFromDB(javax.servlet.http.HttpServletRequest request);
	
	public boolean getOpenTimeDayStat(javax.servlet.http.HttpServletRequest request);
    
}
