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

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.agnitas.actions.EmmAction;

/**
 *
 * @author mhe
 */
public interface EmmActionDao {
    /**
     * Loads emmAction
     *
     * @param actionID
     *              The id of emm action in database
     * @param companyID
     *              The id of the company that uses the action
     * @return  EMMAction bean object or null
     */
    public EmmAction getEmmAction(int actionID, int companyID);

    /**
     * Saves emmAction.
     *
     * @param action
     *              EMMAction bean object
     * @return Saved action id or 0
     */
    public int saveEmmAction(EmmAction action);
    
    /**
     * Deletes emmAction
     *
     * @param actionID
     *              The id of emm action in database
     * @param companyID
     *              The id of the company that uses the action
     * @return true==success
     *false==error
     */
    public boolean deleteEmmAction(int actionID, int companyID);
    
    /**
     * Loads all emm actions for certain company
     *
     * @param companyID
     *              The id of the company that uses the actions
     * @return List of emm actions or empty list
     */
    public List getEmmActions(int companyID);

    /**
     *  Loads all emm actions for certain company except actions of form type
     *
     * @param companyID
     *              The id of the company that uses the actions
     * @return List of emm actions or empty list
     */
    public List getEmmNotFormActions(int companyID);

     /**
     *  Loads all emm actions for certain company except actions of link type
     *
     * @param companyID
     *              The id of the company that uses the actions
     * @return List of emm actions or empty list
     */
    public List getEmmNotLinkActions(int companyID);

    /**
     * Loads numbers of usage in forms for emm actions of certain company
     *
     * @param companyID
     *              The id of the company that uses the actions
     * @return HashMap object
     */
    public Map loadUsed(int companyID);

    /**
     *  Gets names of forms for which the action is used
     *
     * @param actionId
     *              The id of emm action in database
     * @param companyId
     *              The id of the company that uses the action
     * @return String with form names are separated by semicolon, or empty string
     */
    public String getUserFormNames(int actionId, int companyId);

    /**
     *  Loads list of emm actions with sorting
     *
     * @param request
     *              Is used for getting name of column for sorting and sorting order
     * @return List of emm actions
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public List<EmmAction> getActionList(HttpServletRequest request);
}
