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

import org.agnitas.beans.Mailing;
import org.agnitas.beans.MailingBase;
import org.agnitas.target.Target;
import org.displaytag.pagination.PaginatedList;
import org.springframework.context.ApplicationContextAware;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author mhe
 */
public interface MailingDao extends ApplicationContextAware {
	/**
	 * Gets mailing with mediatypes
     *
     * @param mailingID
     *              Id of the mailing in database
     * @param companyID
     *              Id of the company that created a mailing
     * @return Mailing bean object or null
	 */
	public Mailing getMailing(int mailingID, int companyID);

	/**
	 * Saves mailing, its mediatypes and trackable links
     *
     * @param mailing
     *              Mailing bean object; can be changed inside the method by loading id for new mailing
     * @return id of saved mailing
	 */
	public int saveMailing(Mailing mailing);

	/**
	 * Marks mailing as deleted
     *
     * @param mailingID
     *              Id of the mailing in database
     * @param companyID
     *              Id of the company that created a mailing
     * @return true - success; false - if the mailing does not exist in database
	 */
	public boolean deleteMailing(int mailingID, int companyID);

	/**
	 * Loads non-deleted mailings from certain mailing list
     *
     * @param companyID
     *              Id of the company for mailings
	 * @param mailinglistID
     *              Id of mailing list
	 * @return List of Mailing
	 */
	public List<Mailing> getMailingsForMLID(int companyID, int mailinglistID);

    /**
     *  Loads mailing action names with their full urls
     *
     * @param mailingID
     *              Id of the mailing in database
     * @param companyID
     *              Id of the company that created a mailing
     * @return HashMap
     */
	public Map<String, String> loadAction(int mailingID, int companyID);

    /**
     * Gets id of the mailing from certain mailing list that have been last sent for the given customer by the given company
     *
     * @param customerID
     *              Id of the customer
     * @param companyID
     *              Id of the company
     * @param mailinglist
     *              Id of the mailing list
     * @return positive number or zero
     */
	public int findLastNewsletter(int customerID, int companyID, int mailinglist);

    /**
     * Gets values for agn tag with certain name for given company (including default value)
     *
     * @param name
     *          The name of agn tag
     * @param companyID
     *          The id of the company
     * @return string array or null
     */
	public String[] getTag(String name, int companyID);

    /**
     * Deletes certain dynamic content of given mailing
     *
     * @param mailing
     *              MailingBase bean object
     * @param contentID
     *              Id of the dynamic content to be deleted
     * @return true - success; false - nothing was deleted
     */
	public boolean deleteContentFromMailing(MailingBase mailing, int contentID);

    /**
     *  Could not be used in fact because the column auto_url does not exist in mailing_tbl
     *
     */
	public String getAutoURL(int mailingID);

    /**
     *  Could not be used in fact because the column rdir_domain does not exist in mailinglist_tbl
     *
     */
	public String getAutoURL(int mailingID, int companyID);

    /**
     * Selects all non-deleted mailings of certain company and creates paginated list according to given criteria of sorting and pagination
     *
     * @param companyID
     *              The id of the company
     * @param types
     *              Types of mailings for selection (0 - normal, 1 - date-based, 2 - action-based)
     * @param isTemplate
     *              true - for getting list of templates, false - for getting list of mailings
     * @param sort
     *              The name of the column for sorting
     * @param direction
     *              The sort order
     * @param page
     *              The number of the page
     * @param rownums
     *              The number of rows to be shown on page
     * @return PaginatedList of MailingBase
     */
	public PaginatedList getMailingList(int companyID, String types, boolean isTemplate, String sort, String direction, int page, int rownums);

    /**
     *  Gets date format by given type number
     * @param type
     *          The type number
     * @return  non-empty string
     */
	public String getFormat(int type);

	/**
	 * if a mailing has been as a world mailing a statusid has been generated
	 * 
	 * @param mailingID
     *              Id of the mailing in database
     * @param companyID
     *              Id of the company that created a mailing
	 * @return 0 if no worldmailing has been generated
	 */
	public int getStatusidForWorldMailing(int mailingID, int companyID);

	public int getGenstatusForWorldMailing(int mailingID) throws Exception;

	/**
	 * Checks, if a mailing has at least one recipient required for preview.
	 * 
	 * @param mailingId
     *              Id of the mailing in database
     * @param companyID
     *              Id of the company that created a mailing
	 * @return true, if at least one recipient is present, otherwise false
	 */
	public boolean hasPreviewRecipients(int mailingId, int companyID);

	public Map<Integer, Integer> getAllMailingsOnTheSystem();

	/**
	 * Is there any transmission for that mailing running ? - There is no entry
	 * in maildrop_status_tbl for that mailing_id -> ready - There are matching
	 * entries in both maildrop_status_tbl and mailing_account_tbl that means ->
	 * ready - There are only entries in maildrop_status_tbl -> not ready
	 * 
	 * @param mailingID
                   Id of the mailing in database
	 * @return true
	 */
	public boolean isTransmissionRunning(int mailingID);

    /**
     *  Checks if any action related to given mailing exists
     *
     * @param mailingId
     *              Id of the mailing in database
     * @param companyID
     *              Id of the company that created a mailing
     * @return true - has at list one action, otherwise - false
     */
	public boolean hasActions(int mailingId, int companyID);

	/**
	 * Returns the mailing IDs referencing the given template.
	 * 
	 * @param mailTemplate
	 *            referenced template
	 * 
	 * @return list of mailing IDs referencing given template
	 */
	public List<Integer> getTemplateReferencingMailingIds(Mailing mailTemplate);

    /**
     * Check if it's mailing or template
     *
     * @param templateID
     *              Id of the mailing/template in database
     * @param companyID
     *              Id of the company that created a mailing/template
     * @return  true - it's template, false - it's mailing
     */
	public boolean checkMailingReferencesTemplate(int templateID, int companyID);

    /**
     * Does nothing
     * @return false
     */
	public boolean cleanupContentForDynName(int mailingID, String dynName, int companyID);

    /**
     * Checks the existence of mailing in the database
     *
     * @param mailingID
     *              Id of the mailing in database
     * @param companyID
     *              Id of the company that created a mailing
     * @return true - if the mailing exists, false - if does not
     */
	public boolean exist(int mailingID, int companyID);

    /**
     * Checks the existence of template in the database
     *
     * @param mailingID
     * @param companyID
     * @param isTemplate
     * @return
     */
	public boolean exist(int mailingID, int companyID, boolean isTemplate);

    /**
     * Gets names and descriptions of mailings listed by ids
     *
     * @param mailingIDList
     *                  String contains mailing ids separated with comma
     * @param allNames
     *                  HashTable for mailing names, is changing inside the method
     * @param allDesc
     *                  HashTable for mailing descriptions, is changing inside the method
     * @param companyID
     *                  Id of the company that created the mailings
     * @return  String object
     */
	public String compareMailingsNameAndDesc(String mailingIDList, Hashtable<Integer, String> allNames, Hashtable<Integer, String> allDesc, int companyID);

    /**
     * Loads number of recipients of each mailing from the given list and chose the biggest number
     *
     * @param mailingIDList
     *                  List of mailing ids
     * @param allSent
     *                  Mailing id with number of mailing recipients, is updating inside the method
     * @param biggestRecipients
     *                  Max number of recipients, is updating inside the method
     * @param companyID
     *                  Id of the company that sent the mailings
     * @param aTarget
     *                  Target bean object with recipients target group data
     * @return max number of recipients
     */
	public int compareMailingsSendMailings(String mailingIDList, Hashtable<Integer, Integer> allSent, int biggestRecipients, int companyID, Target aTarget);

    /**
     *  For each mailing from the given list loads number of openers and chose the biggest number
     *
     * @param mailingIDList
     *                  List of mailing ids
     * @param companyID
     *                  Id of the company that sent the mailings
     * @param allOpen
     *                  Mailing id with number of openers, is updating inside the method
     * @param biggestOpened
     *                  Max number of openers, is updating inside the method
     * @param aTarget
     *                  Target bean object with recipients target group data
     * @return max number of openers
     */
	public int compareMailingsOpened(String mailingIDList, int companyID, Hashtable<Integer, Integer> allOpen, int biggestOpened, Target aTarget);

    /**
     * For each mailing from the given list loads number of clickers and chose the biggest number
     *
     * @param mailingIDList
     *                  List of mailing ids
     * @param allClicks
     *                  Mailing id with number of clickers, is updating inside the method
     * @param biggestClicks
     *                  Max number of clickers, is updating inside the method
     * @param companyID
     *                  Id of the company that sent the mailings
     * @param aTarget
     *                  Target bean object with recipients target group data
     * @return max number of clickers
     */
	public int compareMailingsTotalClicks(String mailingIDList, Hashtable<Integer, Integer> allClicks, int biggestClicks, int companyID, Target aTarget);

    /**
     * For each mailing from the given list loads numbers of customer which did not open (opt-out) or skipped (bounce) the mailing, and chose the biggest numbers
     *
     * @param mailingIDList
     *                  List of mailing ids
     * @param allOptout
     *                  Mailing id with number of non-openers, is updating inside the method
     * @param allBounce
     *                  Mailing id with number of bounces, is updating inside the method
     * @param biggestOptout
     *                  Max number of non-openers, is updating inside the method
     * @param biggestBounce
     *                   Max number of bounces, is updating inside the method
     * @param companyID
     *                  Id of the company that sent the mailings
     * @param aTarget
     *                  Target bean object with recipients target group data
     * @return Map with biggest bounce and biggest optout values
     */
	public Map<String, Integer> compareMailingsOptoutAndBounce(String mailingIDList, Hashtable<Integer, Integer> allOptout, Hashtable<Integer, Integer> allBounce, int biggestOptout, int biggestBounce, int companyID, Target aTarget);

    /**
     * Loads list of non-deleted mailing have been sent by certain company
     *
     * @param companyID
     *                  Id of the company that sent the mailings
     * @return  List of MailingBase bean objects
     */
	public List<MailingBase> getMailingsForComparation(int companyID);

    /**
     * Loads list of templates of certain company
     *
     * @param companyID
     *               Id of the company
     * @return List of Mailing bean objects
     */
	public List<Mailing> getTemplates(int companyID);

    /**
     * Loads list of non-deleted templates of certain company
     *
     * @param companyID
     *               Id of the company
     * @return List of MailingBase bean objects
     */
	public List<MailingBase> getTemplateMailingsByCompanyID(int companyID);

    /**
     * Gets mailing by given id
     *
     * @param templateID
     *              Id of the mailing in database
     * @param companyID
     *              Id of the company that created a mailing
     * @return  MailingBase bean object or null
     */
	public MailingBase getMailingForTemplateID(int templateID, int companyID);

    /**
     * Loads list of action-based mailings have been sent by certain company
     *
     * @param companyID
     *              Id of the company that sent the mailings
     * @return  List of MailingBase bean objects
     */
	public List<MailingBase> getMailingsByStatusE(int companyID);

    /**
     * Loads list of dynamic tags of certain company, also includes default dynamic tags
     *
     * @param companyID
     *             Id of the company
     * @return
     */
	public List<Map<String, String>> getTags(int companyID);

    /**
     * Loads list of non-deleted mailings/templates of certain company
     * @param companyId
     *             Id of the company
     * @param isTemplate
     *              true - load templates, false - load mailings
     * @return List of Mailing objects
     */
	public List<Mailing> getMailings(int companyId, boolean isTemplate);

    /**
     * Gets id of open action for the mailing
     *
     * @param mailingID
     *              Id of the mailing
     * @param companyID
     *              Id  of the company
     * @return positive integer or zero
     */
	public int getMailingOpenAction(int mailingID, int companyID);

    /**
     * Gets id of click action for the mailing
     *
     * @param mailingID
     *              Id of the mailing
     * @param companyID
     *              Id  of the company
     * @return positive integer or zero
     */
	public int getMailingClickAction(int mailingID, int companyID);

	public boolean isWorldMailingSent(int mailingId, int companyId);

    /**
     * Gets parameter string for mailing of email type
     * @param mailingID
     *              Id of the mailing
     * @return String object or null
     */
	public String getEmailParameter(int mailingID);
	
	public String getSQLExpression(String targetExpression);
}
