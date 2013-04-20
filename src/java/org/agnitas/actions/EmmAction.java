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

package org.agnitas.actions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author mhe
 */
public interface EmmAction extends Serializable {
    /**
     * Adds a ActionOperation to the end of the list of ActionOperations. (ArrayList actions)
     * 
     * @param aAction ActionOperation to be added to this Action
     */
    void addActionOperation(ActionOperation aAction);

    /**
     * Executes all ActionOperations for this Action in ArrayList actions
     * 
     * 
     * @return true==sucess
     * false=error
     * @param con 
     * @param params HashMap containing all available informations
     */
    boolean executeActions(ApplicationContext con, Map params);

    /**
     * Getter for property actionID.
     * 
     * @return Value of property actionID.
     */
    int getId();

    /**
     * Getter for property actions.
     * 
     * @return Value of property actions.
     */
    ArrayList<ActionOperation> getActions();

    /**
     * Getter for property companyID.
     * 
     * @return Value of property companyID.
     */
    int getCompanyID();

    /**
     * Getter for property description.
     * 
     * @return Value of property description.
     */
    String getDescription();

    /**
     * Getter for property shortname.
     * 
     * @return Value of property shortname.
     */
    String getShortname();

    /**
     * Getter for property type.
     * 
     * @return Value of property type.
     */
    int getType();

    /**
     * Removes ActionOperation with the given Index from the list of ActionOperations (ArrayList actions)
     * 
     * @param index Index to be removed from ArrayList actions
     * @return true==sucess
     * false==index does not exist
     */
    boolean removeActionOperation(int index);

    /**
     * Setter for property actionID.
     * 
     * @param actionID New value of property actionID.
     */
    void setId(int actionID);

    /**
     * Setter for property actions.
     * 
     * @param actions New value of property actions.
     */
    void setActions(ArrayList<ActionOperation> actions);

    /**
     * Setter for property companyID.
     * 
     * @param companyID New value of property companyID.
     */
    void setCompanyID(int companyID);

    /**
     * Setter for property description.
     * 
     * @param description New value of property description.
     */
    void setDescription(String description);

    /**
     * Setter for property shortname.
     * 
     * @param shortname New value of property shortname.
     */
    void setShortname(String shortname);

    /**
     * Setter for property type.
     * 
     * @param type New value of property type.
     */
    void setType(int type);

    public static final int TYPE_LINK = 0;

    public static final int TYPE_FORM = 1;

    public static final int TYPE_ALL = 9;
    
    /**
     * This will return 0 , if the action will be loaded by hibernate. 
     * @return number of forms, which use this action
     */
    public int getUsed();
    
    /**
     * Set the number of forms, which use this action. This will not be persisted in the database.
     * @param used
     */
    public void setUsed(int used);

    public String getFormNames();

    public void setFormNames(String formNames);
    
}
