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

public interface CampaignStatEntry extends Serializable {
   
     /**
     * Getter for property bounces.
     * 
     * @return Value of property bounces.
     */
    int getBounces();

    /**
     * Getter for property clickRate.
     * 
     * @return Value of property clickRate.
     */
    double getClickRate();

    /**
     * Getter for property clicks.
     * 
     * @return Value of property clicks.
     */
    int getClicks();

    /**
     * Getter for property name.
     * 
     * @return Value of property mane.
     */
    String getName();

    /**
     * Getter for property openRate.
     * 
     * @return Value of property openRate.
     */
    double getOpenRate();

    /**
     * Getter for property opened.
     * 
     * @return Value of property opened.
     */
    int getOpened();

    /**
     * Getter for property optOuts.
     * 
     * @return Value of property optOuts.
     */
    int getOptouts();

    /**
     * Getter for property shortname.
     * 
     * @return Value of property shortname.
     */
    String getShortname();

    /**
     * Getter for property totalMails.
     * 
     * @return Value of property totalMails.
     */
    int getTotalMails();

    /**
     * Getter for property revenue.
     *
     * @return Value of property revenue.
     */
    double getRevenue();

    /**
     * Setter for property bounces.
     * 
     * @param bounces New value of property bounces.
     */
    void setBounces(int bounces);

    /**
     * Setter for property clickRate.
     * 
     * @param clickRate New value of property clickRate.
     */
    void setClickRate(double clickRate);

    /**
     * Setter for property clicks.
     * 
     * @param clicks New value of property clicks.
     */
    void setClicks(int clicks);

    /**
     * Setter for property name.
     * 
     * @param name New value of property name.
     */
    void setName(String name);

    /**
     * Setter for property openRate.
     * 
     * @param openRate New value of property openRate.
     */
    void setOpenRate(double openRate);

    /**
     * Setter for property opened.
     * 
     * @param opened New value of property opened.
     */
    void setOpened(int opened);

    /**
     * Setter for property optouts.
     * 
     * @param optouts New value of property optouts.
     */
    void setOptouts(int optouts);

    /**
     * Setter for property shortname.
     * 
     * @param shortname New value of property shortname.
     */
    void setShortname(String shortname);

    /**
     * Setter for property totalMails.
     * 
     * @param totalMails New value of property totalMails.
     */
    void setTotalMails(int totalMails);

    /**
     * Setter for property revenue.
     *
     * @param revenue New value of property revenue.
     */
    void setRevenue(double revenue);
    
}
