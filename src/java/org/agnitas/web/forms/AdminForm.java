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

import org.agnitas.util.AgnUtils;
import org.agnitas.web.AdminAction;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 *
 * @author  mhe
 */
public class AdminForm extends StrutsFormBase {
    
	private static final long serialVersionUID = -253714570721911412L;
	protected int    action;
	protected int	previousAction;
    protected int    adminID = 0;
    protected int    companyID = 1;
    protected int    customerID;
    protected int    layoutID = 0;
    protected String username;
    protected String password;
    protected String fullname;
    protected String adminTimezone;
    
    private String language;
    private Locale adminLocale;
    private String passwordConfirm;
   
    /** 
     * Holds value of property userRights. 
     */
    private Set<String> userRights;
    
    /**
     * Holds value of property groupRights.
     */
    private Set<String> groupRights;
    
    /**
     * Holds value of property groupID.
     */
    private int groupID = 0;
    private ActionMessages messages;

    // constructor:
    public AdminForm() {
        super();
        if (this.columnwidthsList == null) {
            this.columnwidthsList = new ArrayList<String>();
            for (int i = 0; i < 3; i++) {
                columnwidthsList.add("-1");
            }
        }
    }
    
    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        
       // this.adminID = 0;
       // this.layoutID=0;
        this.setAdminLocale(Locale.GERMANY);
        this.setAdminTimezone("Europe/Berlin");
        this.userRights=new HashSet<String>();
        this.groupRights=new HashSet<String>();
    }
    
    /**
     * Validate the properties that have been set from this HTTP request,
     * and return an <code>ActionMessages</code> object that encapsulates any
     * validation errors that have been found.  If no errors are found, return
     * <code>null</code> or an <code>ActionMessages</code> object with no
     * recorded error messages.
     * 
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     * @return errors
     */
    public ActionErrors formSpecificValidate(ActionMapping mapping,
                                 HttpServletRequest request) {
        
        ActionErrors errors = new ActionErrors();

        boolean doNotDelete = request.getParameter("delete") == null || request.getParameter("delete").isEmpty();
        if(doNotDelete && (action==AdminAction.ACTION_SAVE || action==AdminAction.ACTION_NEW)) {
            if(this.username.length()<3)
                errors.add("username", new ActionMessage("error.username.tooShort"));

            if(this.password.length()<5 && this.password.length() > 0)
                errors.add("password", new ActionMessage("error.password.tooShort"));
            
            if(!this.password.equals(this.passwordConfirm))
                errors.add("password", new ActionMessage("error.password.mismatch"));

            if(this.username.length() > 20)
				errors.add("username",  new ActionMessage("error.username.tooLong"));

			if(this.password.length() > 20)
				errors.add("password",  new ActionMessage("error.password.tooLong"));
        }
        
        if(action==AdminAction.ACTION_SAVE_RIGHTS) {
            Enumeration aEnum=request.getParameterNames();
            String paramName=null;
            String value=null;
            while(aEnum.hasMoreElements()) {
                paramName=(String)aEnum.nextElement();
                if(paramName.startsWith("user_right")) {
                    value=request.getParameter(paramName);
                    if(value!=null) {
                        if(value.startsWith("user__")) {
                            value=value.substring(6);
                            AgnUtils.logger().info("put: "+value);
                            this.userRights.add(value);
                        }
                    }
                }
            }
        }
        return errors;
    }

    @Override
    protected ActionErrors checkForHtmlTags(HttpServletRequest request) {
        if(action != AdminAction.ACTION_VIEW_WITHOUT_LOAD){
            return super.checkForHtmlTags(request);
        }
        return new ActionErrors();
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
     * Getter for property adminID.
     *
     * @return Value of property adminID.
     */
    public int getAdminID() {
        return this.adminID;
    }
    
    /** 
     * Getter for property username.
     *
     * @return Value of property username.
     */
    public String getUsername() {
        return this.username;
    }
    
    /** 
     * Getter for property password.
     *
     * @return Value of property password.
     */
    public String getPassword() {
        return this.password;
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
     * Getter for property fullname.
     *
     * @return Value of property fullname.
     */
    public String getFullname() {
        return this.fullname;
    }
    
    /** 
     * Getter for property customerID.
     *
     * @return Value of property customerID.
     */
    public int getCustomerID() {
        return this.customerID;
    }
    
    /** 
     * Getter for property adminTimezone.
     *
     * @return Value of property adminTimezone.
     */
    public String getAdminTimezone() {
        return this.adminTimezone;
    }
    
    /** 
     * Getter for property layoutID.
     *
     * @return Value of property layoutID.
     */
    public int getLayoutID() {
        return this.layoutID;
    }
    
    /** 
     * Getter for property language.
     *
     * @return Value of property language.
     */
    public String getLanguage() {
        return this.language;
    }
    
    /** 
     * Getter for property adminLocale.
     *
     * @return Value of property adminLocale.
     */
    public Locale getAdminLocale() {
        return this.adminLocale;
    }
    
    /** 
     * Getter for property passwordConfirm.
     *
     * @return Value of property passwordConfirm.
     */
    public String getPasswordConfirm() {
        return this.passwordConfirm;
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
     * Setter for property adminID.
     *
     * @param adminID New value of property adminID.
     */
    public void setAdminID(int adminID) {
        this.adminID = adminID;
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
     * Setter for property password.
     *
     * @param password New value of property password.
     */
    public void setPassword(String password) {
        this.password = password;
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
     * Setter for property fullname.
     *
     * @param fullname New value of property fullname.
     */
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
    
    /**
     * Setter for property customerID.
     *
     * @param customerID New value of property customerID.
     */
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }
    
    /**
     * Setter for property admineTimezone.
     *
     * @param timezone New value of property adminTimezone.
     */
    public void setAdminTimezone(String timezone) {
        this.adminTimezone = timezone;
    }
    
    /**
     * Setter for property layoutID.
     *
     * @param layoutID New value of property layoutID.
     */
    public void setLayoutID(int layoutID) {
        this.layoutID = layoutID;
    }
    
    /**
     * Setter for property language.
     *
     * @param language New value of property language.
     */
    public void setLanguage(String language) {
        this.language = language;
        if(this.language!=null) {
            int aPos=this.language.indexOf('_');
            String lang=this.language.substring(0,aPos);
            String country=this.language.substring(aPos+1);
            AgnUtils.logger().info("Got lang: "+lang+" Country: "+country);
            this.adminLocale=new Locale(lang, country);
        }
    }
    
    /**
     * Setter for property adminLocale.
     *
     * @param adminLocale New value of property adminLocale.
     */
    public void setAdminLocale(Locale adminLocale) {
        this.adminLocale = adminLocale;
        if(this.adminLocale!=null) {
            this.language=this.adminLocale.toString();
        }
    }
    
    /**
     * Setter for property passwordConfirm.
     *
     * @param passwordConfirm New value of property passwordConfirm.
     */
    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
    
    /** 
     * Getter for property userRights.
     *
     * @return Value of property userRights.
     */
    public Set<String> getUserRights() {
        return this.userRights;
    }
    
    /** 
     * Setter for property userRights.
     *
     * @param userRights New value of property userRights.
     */
    public void setUserRights(Set<String> userRights) {
        this.userRights = userRights;
    }
    
    /**
     * Getter for property groupRights.
     *
     * @return Value of property groupRights.
     */
    public Set<String> getGroupRights() {
        return this.groupRights;
    }
    
    /**
     * Setter for property groupRights.
     *
     * @param groupRights New value of property groupRights.
     */
    public void setGroupRights(Set<String> groupRights) {
        this.groupRights = groupRights;
    }
    
    /**
     * Getter for property groupID.
     *
     * @return Value of property groupID.
     */
    public int getGroupID() {
        return this.groupID;
    }
    
    /**
     * Setter for property groupID.
     *
     * @param groupID New value of property groupID.
     */
    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

	public int getPreviousAction() {
		return previousAction;
	}

	public void setPreviousAction(int previousAction) {
		this.previousAction = previousAction;
	}

    public ActionMessages getMessages() {
        return messages;
    }

    public void setMessages(ActionMessages messages) {
        this.messages = messages;
    }
}
