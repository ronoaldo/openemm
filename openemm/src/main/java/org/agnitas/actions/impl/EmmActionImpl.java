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

package org.agnitas.actions.impl;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map;
import org.agnitas.actions.ActionOperation;
import org.agnitas.actions.EmmAction;
import org.springframework.context.ApplicationContext;

/** Main Container for Actions. Allows managing and executing Actions with an easy interface
 *
 * @author mhe
 * @version 2.0
 */
public class EmmActionImpl implements EmmAction {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -5500708140184014085L;

	/** Holds value of property companyID. */
    protected int companyID;
    
    /**
     * Holds value of property id.
     */
    protected int id;
    
    /** Holds value of property shortname. */
    protected String shortname;
    
    /** Holds value of property description. */
    protected String description = "";
    
    /** Holds value of property actions. */
    protected ArrayList<ActionOperation> actions;
    
    /**
     * Holds value of property type.
     */
    protected int type;
    
    /**
     * Number of forms , which use this Action
     */
    private int used;

    /**
     * Names of forms , which use this Action
     */
    protected String formNames;
    
    
    /** Creates new Action */
    public EmmActionImpl() {
    }
       
     /**
     * Executes all ActionOperations for this Action in ArrayList actions
     * 
     * @return true==sucess
     * false=error
     * @param con 
     * @param params Map containing all available informations
     */    
    public boolean executeActions(ApplicationContext con, Map params) {
        boolean returnValue=true;
        ActionOperation aOperation;
        
        if(actions==null) {
            return false;
        }
        
        ListIterator<ActionOperation> allActions=actions.listIterator();
        
        if(allActions==null) {
            return false;
        }
        
        while(allActions.hasNext()) {
            aOperation = allActions.next();
            returnValue=aOperation.executeOperation(con, this.companyID, params);
            if(returnValue==false) {
                break;
            }
        }
        
        return returnValue;
    }
    
    /** Adds a ActionOperation to the end of the list of ActionOperations. (ArrayList actions)
     *
     * @param aAction ActionOperation to be added to this Action
     */    
    public void addActionOperation(ActionOperation aAction) {
        this.actions.add(aAction);
    }
    
    /** Removes ActionOperation with the given Index from the list of ActionOperations (ArrayList actions)
     *
     * @return true==sucess
     * false==index does not exist
     * @param index Index to be removed from ArrayList actions 
     */    
    public boolean removeActionOperation(int index) {
        if(this.actions.remove(index)!=null) {
            return true;
        } else {
            return false;
        }
    }
    
    /** Getter for property companyID.
     *
     * @return Value of property companyID.
     */
    public int getCompanyID() {
        return companyID;
    }
    
    /** Setter for property companyID.
     *
     * @param companyID New value of property companyID.
     */
    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }
    
    /**
     * Getter for property id.
     * 
     * @return Value of property id.
     */
    public int getId() {
        return id;
    }
    
    /**
     * Setter for property id.
     * 
     * @param actionID 
     */
    public void setId(int actionID) {
        this.id = actionID;
    }
    
    /** Getter for property shortname.
     *
     * @return Value of property shortname.
     */
    public String getShortname() {
        return shortname;
    }
    
    /** Setter for property shortname.
     *
     * @param shortname New value of property shortname.
     */
    public void setShortname(String shortname) {
        this.shortname = shortname;
    }
    
    /** Getter for property description.
     *
     * @return Value of property description.
     */
    public String getDescription() {
        return description;
    }
    
    /** Setter for property description.
     *
     * @param description New value of property description.
     */
    public void setDescription(String description) {
        if(description == null || description.length() < 1) {
            description = " ";
        }
        this.description = description;
    }
    
    /** Getter for property actions.
     *
     * @return Value of property actions.
     */
    public ArrayList<ActionOperation> getActions() {
        return actions;
    }
    
    /** Setter for property actions.
     *
     * @param actions New value of property actions.
     */
    public void setActions(ArrayList<ActionOperation> actions) {
        this.actions = (ArrayList<ActionOperation>) actions;
    }
    
    /**
     * Getter for property type.
     *
     * @return Value of property type.
     */
    public int getType() {
        return this.type;
    }
    
    /**
     * Setter for property type.
     *
     * @param type New value of property type.
     */
    public void setType(int type) {
        this.type = type;
    }

	public int getUsed() {
		return used;
	}

	public void setUsed(int used) {
		this.used = used;
	}

    public String getFormNames() {
        return formNames;
    }

    public void setFormNames(String formNames) {
        this.formNames = formNames;
    }
    
}
