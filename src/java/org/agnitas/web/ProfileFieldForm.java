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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.agnitas.web.forms.StrutsFormBase;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

public class ProfileFieldForm extends StrutsFormBase {
	private static final transient Logger logger = Logger.getLogger(ProfileFieldForm.class);
    private static final long serialVersionUID = -7521320155092130264L;
    
	private int companyID;
    private int action;
    private int fieldLength;
    private String fieldname;
    private String description;
    private String fieldType;
    private String fieldDefault;
    private boolean fieldNull = false;    
    private String shortname;    
    private String targetsDependent;
    protected boolean fromListPage;
    
    /**
     * Holds value of property oldStyle. 
     */
    private boolean oldStyle;

    private ActionMessages messages;

    /**
     * Creates a new instance of MailinglistForm 
     */
    public ProfileFieldForm() {
        fieldNull=true;
    }
 
    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        
    	super.reset(mapping, request);
        this.companyID = 0;
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
	public ActionErrors formSpecificValidate(ActionMapping mapping,
					HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();

		if(action==ProfileFieldAction.ACTION_NEW) {
			try {
				if(!fieldname.equals(URLEncoder.encode(fieldname, "UTF-8"))) {
					errors.add("fieldname", new ActionMessage("error.profiledb.fieldname"));
					fieldname=null;
					request.setAttribute( "hasErrors", true );
					return errors;
				}
			} catch(UnsupportedEncodingException e) {
				logger.error(e.getMessage());
			}

			if(fieldname.length()<3) {
				errors.add("fieldname", new ActionMessage("error.profiledb.fieldname_too_short"));
				fieldname=null;
				request.setAttribute( "hasErrors", true );
				return errors;
			}
            
			if(shortname.length()<3) {
				errors.add("fieldname", new ActionMessage("error.profiledb.shortname_too_short"));
				request.setAttribute( "hasErrors", true );
				return errors;
			}
		}
        
		if(action==ProfileFieldAction.ACTION_SAVE) {
			if(this.shortname!=null && this.shortname.length()<3) {
				errors.add("fieldname", new ActionMessage("error.profiledb.shortname_too_short"));
				return errors;
			}
		}   
		return errors;
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
     * Getter for property fieldname.
     *
     * @return Value of property fieldname.
     */
    public String getFieldname() {
        return this.fieldname;
    }
    
    /**
     * Setter for property fieldname.
     *
     * @param fieldname New value of property fieldname.
     */
    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }
    
    /**
     * Getter for property companyID.
     *
     * @return Value of property companyID.
     */
    public int getCompanyID() {
        return this.companyID;
    }
    
    /**
     * Setter for property companyID.
     *
     * @param companyID New value of property companyID.
     */
    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }
    
    /**
     * Getter for property fieldType.
     *
     * @return Value of property fieldType.
     */
    public String getFieldType() {
        return this.fieldType;
    }
    
    /**
     * Setter for property fieldType.
     *
     * @param fieldType New value of property fieldType.
     */
    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }
    
    /**
     * Getter for property fieldLength.
     *
     * @return Value of property fieldLength.
     */
    public int getFieldLength() {
        return this.fieldLength;
    }
    
    /**
     * Setter for property fieldLength.
     *
     * @param fieldLength New value of property fieldLength.
     */
    public void setFieldLength(int fieldLength) {
        this.fieldLength = fieldLength;
    }
    
    /**
     * Getter for property fieldDefault.
     *
     * @return Value of property fieldDefault.
     */
    public String getFieldDefault() {
        return this.fieldDefault;
    }
    
    /**
     * Setter for property fieldDefault.
     *
     * @param fieldDefault New value of property fieldDefault.
     */
    public void setFieldDefault(String fieldDefault) {
        this.fieldDefault = fieldDefault;
    }
    
    /**
     * Getter for property fieldNull.
     *
     * @return Value of property fieldNull.
     */
    public boolean isFieldNull() {
        return this.fieldNull;
    }
    
    /**
     * Setter for property fieldNull.
     *
     * @param fieldNull New value of property fieldNull.
     */
    public void setFieldNull(boolean fieldNull) {
        this.fieldNull = fieldNull;
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
     * Getter for property targetsDependent.
     *
     * @return Value of property targetsDependent.
     */
    public String getTargetsDependent() {
        return this.targetsDependent;
    }
    
    /**
     * Setter for property targetsDependent.
     *
     * @param targetsDependent New value of property targetDependent.
     */
    public void setTargetsDependent(String targetsDependent) {
        this.targetsDependent = targetsDependent;
    }
    
    /**
     * Getter for property oldStyle.
     *
     * @return Value of property oldStyle.
     */
    public boolean isOldStyle() {
        return this.oldStyle;
    }
    
    /**
     * Setter for property oldStyle.
     *
     * @param oldStyle New value of property oldStyle.
     */
    public void setOldStyle(boolean oldStyle) {
        this.oldStyle = oldStyle;
    }

    public ActionMessages getMessages() {
        return messages;
    }

    public void setMessages(ActionMessages messages) {
        this.messages = messages;
    }

    public boolean getFromListPage() {
        return fromListPage;
    }

    public void setFromListPage(boolean fromListPage) {
        this.fromListPage = fromListPage;
    }
}
