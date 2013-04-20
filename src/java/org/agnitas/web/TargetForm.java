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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.agnitas.target.TargetOperator;
import org.agnitas.web.forms.StrutsFormBase;
import org.agnitas.web.forms.helper.EmptyStringFactory;
import org.agnitas.web.forms.helper.ZeroIntegerFactory;
import org.apache.commons.collections.Factory;
import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.GrowthList;
import org.apache.commons.collections.list.LazyList;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.util.MessageResources;

public class TargetForm extends StrutsFormBase {
    
	private static final Factory emptyStringFactory;
	private static final Factory zeroIntegerFactory;
	private static final Factory nullFactory;
	
	public static final int COLUMN_TYPE_STRING = 0;
	public static final int COLUMN_TYPE_NUMERIC = 1;
	public static final int COLUMN_TYPE_DATE = 2;
	
    private static final long serialVersionUID = 45877020863407141L;
	private String shortname;
    private String description;
    private int targetID;
    private int action;
    private int numOfRecipients;
    
    // defined rules
    private List<String> columnAndTypeList;
    private List<Integer> chainOperatorList;
    private List<Integer> parenthesisOpenedList;
    private List<Integer> primaryOperatorList;
    private List<String> primaryValueList;
    private List<Integer> parenthesisClosedList;
    private List<String> dateFormatList;
    private List<Integer> secondaryOperatorList;
    private List<String> secondaryValueList;
    private List<TargetOperator[]> validTargetOperatorsList;
    private List<String> columnNameList;
    private List<Integer> columnTypeList;

    // new rules
    private String columnAndTypeNew;
    private int chainOperatorNew;
    private int parenthesisOpenedNew;
    private int primaryOperatorNew;
    private String primaryValueNew;
    private int parenthesisClosedNew;
    private String dateFormatNew;
    private int secondaryOperatorNew;
    private String secondaryValueNew;
    
    private boolean addTargetNode;
    private int targetNodeToRemove;
    
    static {
    	
    	emptyStringFactory = new EmptyStringFactory();
    	
    	zeroIntegerFactory = new ZeroIntegerFactory();
    	    	
    	nullFactory = FactoryUtils.nullFactory();
    
    }
    
    /**
     * Last action we came from.
     */
    private int previousAction;
    
    /**
     * The list size a user prefers while viewing a table 
     */
    private int preferredListSize = 20; 
    
    /**
     * The list size has been loaded from the admin's properties 
     */
    private boolean preferredListSizeLoaded = true;
    
    public TargetForm() {
        columnAndTypeList = (List<String>) GrowthList.decorate(LazyList.decorate(new ArrayList<String>(), emptyStringFactory));
        chainOperatorList = (List<Integer>) GrowthList.decorate(LazyList.decorate(new ArrayList<Integer>(), zeroIntegerFactory));
        parenthesisOpenedList = (List<Integer>) GrowthList.decorate(LazyList.decorate(new ArrayList<Integer>(), zeroIntegerFactory));
        primaryOperatorList = (List<Integer>) GrowthList.decorate(LazyList.decorate(new ArrayList<Integer>(), zeroIntegerFactory));
        primaryValueList = (List<String>) GrowthList.decorate(LazyList.decorate(new ArrayList<String>(), emptyStringFactory));
        parenthesisClosedList = (List<Integer>) GrowthList.decorate(LazyList.decorate(new ArrayList<Integer>(), zeroIntegerFactory));
        dateFormatList = (List<String>) GrowthList.decorate(LazyList.decorate(new ArrayList<String>(), emptyStringFactory));
        secondaryOperatorList = (List<Integer>) GrowthList.decorate(LazyList.decorate(new ArrayList<Integer>(), zeroIntegerFactory));
        secondaryValueList = (List<String>) GrowthList.decorate(LazyList.decorate(new ArrayList<String>(), emptyStringFactory));
        validTargetOperatorsList = (List<TargetOperator[]>) GrowthList.decorate(LazyList.decorate(new ArrayList<TargetOperator[]>(), nullFactory));
        columnNameList = (List<String>) GrowthList.decorate(LazyList.decorate(new ArrayList<String>(), emptyStringFactory));
        columnTypeList = (List<Integer>) GrowthList.decorate(LazyList.decorate(new ArrayList<Integer>(), zeroIntegerFactory));
    }
    
    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        
        this.targetID = 0;
        Locale aLoc=(Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
        
        MessageResources text=(MessageResources)this.getServlet().getServletContext().getAttribute(org.apache.struts.Globals.MESSAGES_KEY);
        //MessageResources text=this.getServlet().getResources();
        
        this.shortname = text.getMessage(aLoc, "default.shortname");
        this.description = text.getMessage(aLoc, "default.description");
        
        // Reset form fields for new rule
        columnAndTypeNew = null;
        chainOperatorNew = 0;
        parenthesisOpenedNew = 0;
        primaryOperatorNew = 0;
        primaryValueNew = null;
        parenthesisClosedNew = 0;
        dateFormatNew = null;
        secondaryOperatorNew = 0;
        secondaryValueNew = null;
    }
    
    public void clearRules() {
        columnAndTypeList.clear();
        chainOperatorList.clear();
        parenthesisOpenedList.clear();
        primaryOperatorList.clear();
        primaryValueList.clear();
        parenthesisClosedList.clear();
        dateFormatList.clear();
        secondaryOperatorList.clear();
        secondaryValueList.clear();
        columnNameList.clear();
        columnTypeList.clear();
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
    @Override
    public ActionErrors formSpecificValidate(ActionMapping mapping,
            HttpServletRequest request) {
        
        ActionErrors errors = new ActionErrors();

        if(getAction() == TargetAction.ACTION_SAVE) {

            if(!this.checkParenthesisBalance()) {
                errors.add("brackets", new ActionMessage("error.target.bracketbalance"));
            }
            if(this.shortname!=null && this.shortname.length()<1) {
                errors.add("shortname", new ActionMessage("error.nameToShort"));
            }
            if(this.getNumTargetNodes() == 0 && !this.getAddTargetNode()) { 
                errors.add("norule", new ActionMessage("error.target.norule"));
            }
        }
        
        return errors;
    }
    
    protected boolean checkParenthesisBalance() {
    	int opened = 0;
    	int closed = 0;
    	
    	int lastIndex = this.getNumTargetNodes();  
    	
    	for(int index = 0; index < lastIndex; index++) {
    		opened += this.getParenthesisOpened(index);
    		closed += this.getParenthesisClosed(index);
    	}
    	
    	return opened == closed;
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
     * Getter for property description.
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
     * Getter for property numOfRecipients.
     *
     * @return Value of property numOfRecipients.
     */
	public int getNumOfRecipients() {
		return numOfRecipients;
	}

	/**
     * Setter for property numOfRecipients.
     *
     * @param numOfRecipients New value of property numOfRecipients.
     */
	public void setNumOfRecipients(int numOfRecipients) {
		this.numOfRecipients = numOfRecipients;
	}

	public int getPreferredListSize() {
		return preferredListSize;
	}

	public void setPreferredListSize(int preferredListSize) {
		this.preferredListSize = preferredListSize;
	}

	public boolean isPreferredListSizeLoaded() {
		return preferredListSizeLoaded;
	}

	public void setPreferredListSizeLoaded(boolean preferredListSizeLoaded) {
		this.preferredListSizeLoaded = preferredListSizeLoaded;
	}

	public int getPreviousAction() {
		return previousAction;
	}

	public void setPreviousAction(int previousAction) {
		this.previousAction = previousAction;
	}
	
	public void removeRule(int index) {
        safeRemove(columnAndTypeList, index);
        safeRemove(chainOperatorList, index);
        safeRemove(parenthesisOpenedList, index);
        safeRemove(primaryOperatorList, index);
        safeRemove(primaryValueList, index);
        safeRemove(parenthesisClosedList, index);
        safeRemove(dateFormatList, index);
        safeRemove(secondaryOperatorList, index);
        safeRemove(secondaryValueList, index);
        safeRemove(columnNameList, index);
        safeRemove(columnTypeList, index);
	}
	
	/**
	 * Removes and index safely from list. If index does not exists, nothing happens.
	 * 
	 * @param list list to remove index from
	 * @param index index to be removed
	 */
	private void safeRemove(List<?> list, int index) {
		if( list.size() > index && index >= 0)
			list.remove(index);
	}
	
	public int getNumTargetNodes() {
		return this.columnAndTypeList.size();
	}
	
	public String getColumnAndType(int index) {
		return this.columnAndTypeList.get(index);
	}
	
	public void setColumnAndType(int index, String value) {
		this.columnAndTypeList.set(index, value);
	}
	
	public int getChainOperator(int index) {
		return this.chainOperatorList.get(index);
	}
	
	public void setChainOperator(int index, int value) {
		this.chainOperatorList.set(index, value);
	}
	
	public int getParenthesisOpened(int index) {
		return this.parenthesisOpenedList.get(index);
	}
	
	public void setParenthesisOpened(int index, int value) {
		this.parenthesisOpenedList.set(index, value);
	}
	
	public int getPrimaryOperator(int index) {
		return this.primaryOperatorList.get(index);
	}
	
	public void setPrimaryOperator(int index, int value) {
		this.primaryOperatorList.set(index, value);
	}
	
	public String getPrimaryValue(int index) {
		return this.primaryValueList.get(index);
	}
	
	public void setPrimaryValue(int index, String value) {
		this.primaryValueList.set(index, value);
	}
	
	public int getParenthesisClosed(int index) {
		return this.parenthesisClosedList.get(index);
	}
	
	public void setParenthesisClosed(int index, int value) {
		this.parenthesisClosedList.set(index, value);
	}
	
	public String getDateFormat(int index) {
		return this.dateFormatList.get(index);
	}
	
	public void setDateFormat(int index, String value) {
		this.dateFormatList.set(index, value);
	}
	
	public int getSecondaryOperator(int index) {
		return this.secondaryOperatorList.get(index);
	}
	
	public void setSecondaryOperator(int index, int value) {
		this.secondaryOperatorList.set(index, value);
	}
	
	public String getSecondaryValue(int index) {
		return this.secondaryValueList.get(index);
	}
	
	public void setSecondaryValue(int index, String value) {
		this.secondaryValueList.set(index, value);
	}
	
	public List<String> getAllColumnsAndTypes() {
		return this.columnAndTypeList;
	}
	
	public void setValidTargetOperators(int index, TargetOperator[] operators) {
		this.validTargetOperatorsList.set(index, operators);
	}
	
	public TargetOperator[] getValidTargetOperators(int index) {
		return this.validTargetOperatorsList.get(index);
	}
	
	public void setColumnName(int index, String value) {
		this.columnNameList.set(index, value);
	}
	
	public String getColumnName(int index) {
		return this.columnNameList.get(index);
	}
	
	public void setColumnType(int index, int type) {
		this.columnTypeList.set(index, type);
	}
	
	public int getColumnType(int index) {
		return this.columnTypeList.get(index);
	}

	public String getColumnAndTypeNew() {
		return columnAndTypeNew;
	}

	public void setColumnAndTypeNew(String columnAndTypeNew) {
		this.columnAndTypeNew = columnAndTypeNew;
	}

	public int getChainOperatorNew() {
		return chainOperatorNew;
	}

	public void setChainOperatorNew(int chainOperatorNew) {
		this.chainOperatorNew = chainOperatorNew;
	}

	public int getParenthesisOpenedNew() {
		return parenthesisOpenedNew;
	}

	public void setParenthesisOpenedNew(int parenthesisOpenedNew) {
		this.parenthesisOpenedNew = parenthesisOpenedNew;
	}

	public int getPrimaryOperatorNew() {
		return primaryOperatorNew;
	}

	public void setPrimaryOperatorNew(int primaryOperatorNew) {
		this.primaryOperatorNew = primaryOperatorNew;
	}

	public String getPrimaryValueNew() {
		return primaryValueNew;
	}

	public void setPrimaryValueNew(String primaryValueNew) {
		this.primaryValueNew = primaryValueNew;
	}

	public int getParenthesisClosedNew() {
		return parenthesisClosedNew;
	}

	public void setParenthesisClosedNew(int parenthesisClosedNew) {
		this.parenthesisClosedNew = parenthesisClosedNew;
	}

	public String getDateFormatNew() {
		return dateFormatNew;
	}

	public void setDateFormatNew(String dateFormatNew) {
		this.dateFormatNew = dateFormatNew;
	}

	public int getSecondaryOperatorNew() {
		return secondaryOperatorNew;
	}

	public void setSecondaryOperatorNew(int secondaryOperatorNew) {
		this.secondaryOperatorNew = secondaryOperatorNew;
	}

	public String getSecondaryValueNew() {
		return secondaryValueNew;
	}

	public void setSecondaryValueNew(String secondaryValueNew) {
		this.secondaryValueNew = secondaryValueNew;
	}
	
	public void setAddTargetNode( boolean addTargetNode) {
		this.addTargetNode = addTargetNode;
	}
	
	public boolean getAddTargetNode() {
		return this.addTargetNode;
	}
	
	public void setTargetNodeToRemove( int targetNodeToRemove) {
		this.targetNodeToRemove = targetNodeToRemove;
	}
	
	public int getTargetNodeToRemove() {
		return this.targetNodeToRemove;
	}
}
