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

package org.agnitas.dao;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.agnitas.beans.Campaign;
import org.agnitas.beans.CampaignStats;
import org.agnitas.beans.MailingBase;

/**
 *
 * @author Andreas Rehak, Nicole Serek, Markus Unger
 */
public interface CampaignDao {
    
    /**
     * Getter for property campaign by campaign id and company id.
     *
     * @return Value of property campaign.
     */
    Campaign getCampaign(int campaignID, int companyID);

    /**
     *  Loads statistic data for sent mailings from certain archive
     *
     * @param useMailtracking
     *                  true - statistics by customer should be loaded
     *                  false - total statistic values should be loaded
     * @param aLocale
     *                  defines a language of statistic report
     * @param mailingIDs
     *                  ids of mailings for which the statistic data should be loaded;
     *                  if null - the statistic data should be loaded for all mailings of the archive
     * @param campaign
     *                  the Campaign object; is changing inside the method by loading content of statistic report csv-file
     * @param targetDao
     *                  TargetDao object, is used for getting target group database entry by given id
     * @param mailingSelection
     *                  null; is used inside the method to get the given mailing ids in string with separation by comma
     * @param targetID
     *                  the id of target group of customers for which the statistics should be loaded;
     *                  if 0 value is received - the statistics for all subscribers is loaded
     * @return CampaignStats bean object
     */
    public CampaignStats getStats(boolean useMailtracking, Locale aLocale, LinkedList<Integer> mailingIDs, Campaign campaign, TargetDao targetDao, String mailingSelection, int targetID);
    
    /**
     *  Saves campaign data
     *
     * @param campaign
     * @return the ID of the saved campaign
     */
    public int save(Campaign campaign);
    
    /**
     * Deletes campaign from database
     *
     * @param campaign
     * @return true if the Campaign has been deleted
     */
	public boolean delete(Campaign campaign);

    /**
     * Loads list of sent mailings which are related to the certain archive
     *
     * @param campaignID
     *                The id of the campaign
     * @param companyID
     *                The id of the campaign company
     * @return  List of MailingBase bean objects or empty list
     */
    public List<MailingBase> getCampaignMailings(int campaignID, int companyID);

    /**
     * Loads list of campaigns for certain company; sort and order criteria are used for getting sorted selection from database
     *
     * @param companyID
     *              The id of the company of campaigns
     * @param sort
     *              The name of column for sorting
     * @param order
     *              The sort order , 1 (for ascending) or 2 (for descending)
     *
     * @return List of Campaign bean objects or empty list
     */
    public List<Campaign> getCampaignList(int companyID, String sort, int order);


}
