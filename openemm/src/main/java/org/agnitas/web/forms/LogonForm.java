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

package org.agnitas.web.forms;


import javax.servlet.http.HttpServletRequest;

import org.agnitas.util.AgnUtils;
import org.agnitas.web.LogonAction;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * Form bean for the user profile page.  This form has the following fields,
 * with default values in square brackets:
 * <ul>
 * <li><b>password</b> - Entered password value
 * <li><b>username</b> - Entered username value
 * </ul>
 *
 * @author mhe
 * @version $Revision: 1.1 $
 */

public final class LogonForm extends StrutsFormBase {
    
    private static final long serialVersionUID = 6413306440712343148L;

	/**
     * The password.
     */
    private String password = null;
    
    /**
     * The username.
     */
    private String username = null;
    
    /** 
     * Holds value of property action. 
     */
    private int action;
    
    /**
     * Holds value of property layout. 
     */
    private int layout=0;

    /**
     * Holds value of property pwd_expire.
     */
    private java.util.Date pwd_expire;

    /**
     * Holds value of property pwd_valid.
     */
    private int pwd_valid;

    /**
     * Holds value of property password_new1.
     */
    private String password_new1;

    /**
     * Holds value of property password_new2.
     */
    private String password_new2;
    
    private String design="";
    
    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        
        this.password = null;
        this.username = null;
        this.pwd_valid=0;
        
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
        
        AgnUtils.logger().info("validate: action " + action);
        ActionErrors errors = new ActionErrors();
        
        if(action==LogonAction.ACTION_LOGON) {
            if ((username == null) || (username.length() < 1))
                errors.add("username", new ActionMessage("error.username.required"));                 
            if ((password == null) || (password.length() < 1))
                errors.add("password", new ActionMessage("error.password.required"));              
        }
        return errors;   
    }

    /**
     * Getter for property password.
     *
     * @return Value of password.
     */
    public String getPassword() {
        
        return (this.password);
        
    }
   
    /**
     * Setter for property password.
     *
     * @param password New value of property password.
     */
    public void setPassword(String password) {
        
        this.password = password;
        
    }
   
    /**
     * Getter for property username.
     *
     * @return Value of username.
     */
    public String getUsername() {
        
        return (this.username);
        
    }
 
    /**
     * Setter for property username.
     *
     * @param username New value of property username.
     */
    public void setUsername(String username) {
        
        this.username = username;
        
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
     * Getter for property layout.
     *
     * @return Value of property layout.
     */
    public int getLayout() {
        return this.layout;
    }
    
    /**
     * Setter for property layout.
     *
     * @param layout New value of property layout.
     */
    public void setLayout(int layout) {
        this.layout = layout;
    }

    /**
     * Getter for property pwd_expire.
     *
     * @return Value of property pwd_expire.
     */
    public java.util.Date getPwd_expire() {

        return this.pwd_expire;
    }

    /**
     * Setter for property pwd_expire.
     *
     * @param pwd_expire New value of property pwd_expire.
     */
    public void setPwd_expire(java.util.Date pwd_expire) {

        this.pwd_expire = pwd_expire;
    }

    /**
     * Getter for property pwd_valid.
     *
     * @return Value of property pwd_valid.
     */
    public int getPwd_valid() {

        return this.pwd_valid;
    }

    /**
     * Setter for property pwd_valid.
     *
     * @param pwd_valid New value of property pwd_valid.
     */
    public void setPwd_valid(int pwd_valid) {

        this.pwd_valid = pwd_valid;
    }

    /**
     * Getter for property password_new1.
     *
     * @return Value of property password_new1.
     */
    public String getPassword_new1() {

        return this.password_new1;
    }

    /**
     * Setter for property password_new1.
     *
     * @param password_new1 New value of property password_new1.
     */
    public void setPassword_new1(String password_new1) {

        this.password_new1 = password_new1;
    }

    /**
     * Getter for property password_new2.
     *
     * @return Value of property password_new2.
     */
    public String getPassword_new2() {

        return this.password_new2;
    }

    /**
     * Setter for property password_new2.
     *
     * @param password_new2 New value of property password_new2.
     */
    public void setPassword_new2(String password_new2) {

        this.password_new2 = password_new2;
    }

	public String getDesign() {
		return design;
	}

	public void setDesign(String design) {
		this.design = design;
	}

	@Override
	protected boolean isParameterExcludedForUnsafeHtmlTagCheck( String parameterName, HttpServletRequest request) {
		return parameterName.equals( "textTemplate") || parameterName.equals( "htmlTemplate");
	}

}
