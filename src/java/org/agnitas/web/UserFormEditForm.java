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


import org.agnitas.web.forms.StrutsFormBase;
import org.apache.commons.collections.Factory;
import org.apache.commons.collections.list.LazyList;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * Implementation of <strong>Form</strong> that holds data for user forms.
 * @author ar, mhe
 */
public class UserFormEditForm extends StrutsFormBase{
    
    private static final long serialVersionUID = 5344970502954958422L;

	/** Holds value of property action. */
    private int action;
    
    /** Holds value of property formID. */
    private int formID;
    
    /** Holds value of property formName. */
    private String formName;
    
    /** Holds value of property description. */
    private String description;
    
    /** Holds value of property startActionID. */
    private int startActionID;
    
    /** Holds value of property endActionID. */
    private int endActionID;
    
    /** Holds value of property successTemplate. */
    private String successTemplate;
    
    /** Holds value of property errorTemplate. */
    private String errorTemplate;

    protected boolean fromListPage;

    private String successUrl;
    private String errorUrl;
    private boolean successUseUrl;
    private boolean errorUseUrl;
    
    /**
     * Validate the properties that have been set from this HTTP request,
     * and return an <code>ActionErrors</code> object that encapsulates any
     * validation errors that have been found.  If no errors are found, return
     * <code>null</code> or an <code>ActionErrors</code> object with no
     * recorded error messages.
     * 
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     * @return messages for errors, that occured. 
     */
    public ActionErrors formSpecificValidate(ActionMapping mapping,
                                             HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();

        if (action == AdminAction.ACTION_SAVE) {
            if(allowed("forms.change", request)) {
                if (getFormName() == null || getFormName().trim().equals("")) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.nameToShort"));
                } else {
                    if (getFormName().length() > 50) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.form.nameTooLong"));
                    }
                }
            }
        }

        return errors;
    }

    @Override
    protected ActionErrors checkForHtmlTags(HttpServletRequest request) {
        if (action != UserFormEditAction.ACTION_VIEW_WITHOUT_LOAD) {
            return super.checkForHtmlTags(request);
        }
        return new ActionErrors();
    }
    
    @Override
	protected boolean isParameterExcludedForUnsafeHtmlTagCheck( String parameterName, HttpServletRequest request) {
    	return parameterName.equals( "errorTemplate") || parameterName.equals( "successTemplate");
	}

	/** Getter for property action.
     * @return Value of property action.
     */
    public int getAction() {
        return this.action;
    }
    
    /** Setter for property action.
     * @param action New value of property action.
     */
    public void setAction(int action) {
        this.action = action;
    }
    
    /** Getter for property fontID.
     * @return Value of property fontID.
     *
     */
    public int getFormID() {
        return this.formID;
    }
    
    /**
     * Setter for property fontID.
     * 
     * @param formID 
     */
    public void setFormID(int formID) {
        this.formID = formID;
    }
    
    /** Getter for property fontName.
     * @return Value of property fontName.
     *
     */
    public String getFormName() {
        return this.formName;
    }
    
    /**
     * Setter for property fontName.
     * 
     * @param formName 
     */
    public void setFormName(String formName) {
        this.formName = formName;
    }
    
    /**
     * Getter for property description.
     * @return Value of property description.
     */
    public String getDescription() {
        return this.description;
    }
    
    /**
     * Setter for property description.
     * @param description New value of property description.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Getter for property startActionID.
     * @return Value of property startActionID.
     */
    public int getStartActionID() {
        return this.startActionID;
    }
    
    /**
     * Setter for property startActionID.
     * @param startActionID New value of property startActionID.
     */
    public void setStartActionID(int startActionID) {
        this.startActionID = startActionID;
    }
    
    /**
     * Getter for property endActionID.
     * @return Value of property endActionID.
     */
    public int getEndActionID() {
        return this.endActionID;
    }
    
    /**
     * Setter for property endActionID.
     * @param endActionID New value of property endActionID.
     */
    public void setEndActionID(int endActionID) {
        this.endActionID = endActionID;
    }
    
    /**
     * Getter for property successTemplate.
     * @return Value of property successTemplate.
     */
    public String getSuccessTemplate() {
        return this.successTemplate;
    }
    
    /**
     * Setter for property successTemplate.
     * @param successTemplate New value of property successTemplate.
     */
    public void setSuccessTemplate(String successTemplate) {
        this.successTemplate = successTemplate;
    }
    
    /**
     * Getter for property errorTemplate.
     * @return Value of property errorTemplate.
     */
    public String getErrorTemplate() {
        return this.errorTemplate;
    }
    
    /**
     * Setter for property errorTemplate.
     * @param errorTemplate New value of property errorTemplate.
     */
    public void setErrorTemplate(String errorTemplate) {
        this.errorTemplate = errorTemplate;
    }


    public String getSuccessUrl() {
        return successUrl;
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    public String getErrorUrl() {
        return errorUrl;
    }

    public void setErrorUrl(String errorUrl) {
        this.errorUrl = errorUrl;
    }

    public boolean isSuccessUseUrl() {
        return successUseUrl;
    }

    public void setSuccessUseUrl(boolean successUseUrl) {
        this.successUseUrl = successUseUrl;
    }

    public boolean isErrorUseUrl() {
        return errorUseUrl;
    }

    public void setErrorUseUrl(boolean errorUseUrl) {
        this.errorUseUrl = errorUseUrl;
    }

    @Override
    public void reset(ActionMapping map, HttpServletRequest request) {
    	 Factory factory = new Factory() {
    	     public Object create() {
    	         return new Integer(0);
    	     }
    	 } ;
    	 columnwidthsList = LazyList.decorate(new ArrayList(), factory);

    }

    public boolean getFromListPage() {
        return fromListPage;
    }

    public void setFromListPage(boolean fromListPage) {
        this.fromListPage = fromListPage;
    }
}
