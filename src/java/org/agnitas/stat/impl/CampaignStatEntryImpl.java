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

package org.agnitas.stat.impl;

import org.agnitas.stat.CampaignStatEntry;

public class CampaignStatEntryImpl implements CampaignStatEntry {
    private static final long serialVersionUID = -4863870455420608269L;
	protected String name;        
    protected String shortname;
    protected int clicks;
    protected int opened;
    protected int optouts;
    protected int bounces;
    protected int totalMails;
    protected double revenue;
        
    /**
     * Holds value of property openRate.
     */
    protected double openRate;
    
    /**
     * Holds value of property clickRate.
     */
    protected double clickRate;
    
    public CampaignStatEntryImpl() {
        name=" ";
        shortname=" ";
        clicks=0;
        opened=0;
        optouts=0;
        bounces=0;
        totalMails=0;
    }

    public String getShortname() {
        return this.shortname;
    }
    public int getClicks() {
        return this.clicks;
    }
    public void setShortname(String shortname) {
        this.shortname = shortname;
    }
    public void setClicks(int clicks) {
        this.clicks = clicks;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOpened() {
        return this.opened;
    }

    public void setOpened(int opened) {
        this.opened = opened;
    }

    public int getOptouts() {
        return this.optouts;
    }

    public void setOptouts(int optouts) {
        this.optouts = optouts;
    }

    public int getBounces() {
        return this.bounces;
    }

    public void setBounces(int bounces) {
        this.bounces = bounces;
    }

    public int getTotalMails() {
        return this.totalMails;
    }

    public void setTotalMails(int totalMails) {
        this.totalMails = totalMails;
    }

    /**
     * Getter for property openRate.
     * @return Value of property openRate.
     */
    public double getOpenRate() {
        return this.openRate;
    }

    /**
     * Setter for property openRate.
     * @param openRate New value of property openRate.
     */
    public void setOpenRate(double openRate) {
        this.openRate = openRate;
    }

    /**
     * Getter for property clickRate.
     * @return Value of property clickRate.
     */
    public double getClickRate() {
        return this.clickRate;
    }

    /**
     * Setter for property clickRate.
     * @param clickRate New value of property clickRate.
     */
    public void setClickRate(double clickRate) {
        this.clickRate = clickRate;
    }

    /**
     * Getter for property revenue.
     * @return Value of property revenue.
     */
    public double getRevenue() {
        return revenue;
    }

    /**
     * Setter for property revenue.
     * @param revenue New value of property revenue.
     */
    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }
}