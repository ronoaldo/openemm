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


import java.util.List;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.agnitas.beans.Mailinglist;
import org.agnitas.beans.UserForm;
import org.agnitas.web.forms.StrutsFormBase;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;


public class MailloopForm extends StrutsFormBase {

    private static final long serialVersionUID = 4181470849973380414L;

	/**
     * Holds value of property shortname.
     */
    private String shortname;

    /**
     * Holds value of property description.
     */
    private String description;

    /**
     * Holds value of property action.
     */

    private int action;


    private ActionMessages messages;
    private List<Mailinglist> mailinglists;
    private List<UserForm> userforms;


    public MailloopForm() {
        super();
        if (this.columnwidthsList == null) {
            this.columnwidthsList = new ArrayList<String>();
            for (int i = 0; i < 4; i++) {
                columnwidthsList.add("-1");
            }
        }
        mailinglists = new ArrayList<Mailinglist>();
        userforms = new ArrayList<UserForm>();
    }

    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
//        this.shortname="";
//        this.description="";
    	clearData();
    }

    /**
     * Initializes shortname and description and sets the mailloopID to 0. 
     */
    public void clearData() {
        this.shortname="";
        this.description="";
        this.mailloopID = 0;
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
     */
    public ActionErrors formSpecificValidate(ActionMapping mapping, HttpServletRequest request) {

        ActionErrors errors = new ActionErrors();

        if(action==MailloopAction.ACTION_SAVE) {
            if(allowed("mailing.show", request)) {
                if(this.shortname!=null && this.shortname.length()<3) {
                    errors.add("shortname", new ActionMessage("error.nameToShort"));
                }
            }
        }
        return errors;
    }

    @Override
    protected ActionErrors checkForHtmlTags(HttpServletRequest request) {
        if(action != MailloopAction.ACTION_VIEW_WITHOUT_LOAD){
            return super.checkForHtmlTags(request);
        }
        return new ActionErrors();
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
     * Holds value of property mailloopID.
     */
    private int mailloopID;

    /**
     * Getter for property mailloopID.
     *
     * @return Value of property mailloopID.
     */
    public int getMailloopID() {

        return this.mailloopID;
    }

    /**
     * Setter for property mailloopID.
     *
     * @param mailloopID New value of property mailloopID.
     */
    public void setMailloopID(int mailloopID) {

        this.mailloopID = mailloopID;
    }

    /**
     * Holds value of property doForward.
     */
    private boolean doForward;

    /**
     * Getter for property doForward.
     *
     * @return Value of property doForward.
     */
    public boolean isDoForward() {

        return this.doForward;
    }

    /**
     * Setter for property doForward.
     *
     * @param doForward New value of property doForward.
     */
    public void setDoForward(boolean doForward) {

        this.doForward = doForward;
    }

    /**
     * Holds value of property doAutoresponder.
     */
    private boolean doAutoresponder;

    /**
     * Getter for property doAutoresponder.
     *
     * @return Value of property doAutoresponder.
     */
    public boolean isDoAutoresponder() {

        return this.doAutoresponder;
    }

    /**
     * Setter for property doAutoresponder.
     *
     * @param doAutoresponder New value of property doAutoresponder.
     */
    public void setDoAutoresponder(boolean doAutoresponder) {

        this.doAutoresponder = doAutoresponder;
    }

    /**
     * Holds value of property arSubject.
     */
    private String arSubject;

    /**
     * Getter for property arSubject.
     *
     * @return Value of property arSubject.
     */
    public String getArSubject() {

        return this.arSubject;
    }

    /**
     * Setter for property arSubject.
     *
     * @param arSubject New value of property arSubject.
     */
    public void setArSubject(String arSubject) {

        this.arSubject = arSubject;
    }

    /**
     * Holds value of property arSender.
     */
    private String arSender;

    /**
     * Getter for property arSender.
     *
     * @return Value of property arSender.
     */
    public String getArSender() {

        return this.arSender;
    }

    /**
     * Setter for property arSender.
     *
     * @param arSender New value of property arSender.
     */
    public void setArSender(String arSender) {

        this.arSender = arSender;
    }

    /**
     * Holds value of property forwardEmail.
     */
    private String forwardEmail;

    /**
     * Getter for property forwardEmail.
     *
     * @return Value of property forwardEmail.
     */
    public String getForwardEmail() {

        return this.forwardEmail;
    }

    /**
     * Setter for property forwardEmail.
     *
     * @param forwardEmail New value of property forwardEmail.
     */
    public void setForwardEmail(String forwardEmail) {

        this.forwardEmail = forwardEmail;
    }

    /**
     * Holds value of property arText.
     */
    private String arText;

    /**
     * Getter for property arText.
     *
     * @return Value of property arText.
     */
    public String getArText() {

        return this.arText;
    }

    /**
     * Setter for property arText.
     *
     * @param arText New value of property arText.
     */
    public void setArText(String arText) {

        this.arText = arText;
    }

    /**
     * Holds value of property arHtml.
     */
    private String arHtml;

    /**
     * Getter for property arHtml.
     *
     * @return Value of property arHtml.
     */
    public String getArHtml() {

        return this.arHtml;
    }

    /**
     * Setter for property arHtml.
     *
     * @param arHtml New value of property arHtml.
     */
    public void setArHtml(String arHtml) {

        this.arHtml = arHtml;
    }

    /**
     * Holds value of property mailloops.
     */
    private List mailloops;

    /**
     * Getter for property mailloops.
     *
     * @return Value of property mailloops.
     */
    public List getMailloops() {

        return this.mailloops;
    }

    /**
     * Setter for property mailloops.
     *
     * @param mailloops New value of property mailloops.
     */
    public void setMailloops(List mailloops) {

        this.mailloops = mailloops;
    }

    /**
     * Holds value of property changedate.
     */
    private java.sql.Timestamp changedate;

    /**
     * Getter for property changedate.
     * @return Value of property changedate.
     */
    public java.sql.Timestamp getChangedate() {

    	return this.changedate;
    }

    /**
     * Setter for property changedate.
     * @param change_date New value of property changedate.
     */
    public void setChangedate(java.sql.Timestamp changedate) {

    	this.changedate = changedate;
    }
    
    /**
     * Holds value of property doSubscribe.
     */
    private boolean doSubscribe;

	public boolean isDoSubscribe() {
		return doSubscribe;
	}

	public void setDoSubscribe(boolean doSubscribe) {
		this.doSubscribe = doSubscribe;
	}
	
	/**
     * Holds value of property mailinglistID.
     */
    private int mailinglistID;

	public int getMailinglistID() {
		return mailinglistID;
	}

	public void setMailinglistID(int mailinglistID) {
		this.mailinglistID = mailinglistID;
	}
	
	/**
     * Holds value of property userformID.
     */
    private int userformID;

	public int getUserformID() {
		return userformID;
	}

	public void setUserformID(int userformID) {
		this.userformID = userformID;
	}

    public ActionMessages getMessages() {
        return messages;
    }

    public void setMessages(ActionMessages actionMessages) {
        this.messages = actionMessages;
    }

    public List<Mailinglist> getMailinglists() {
        return mailinglists;
    }

    public void setMailinglists(List<Mailinglist> mailinglists) {
        this.mailinglists = mailinglists;
    }

    public List<UserForm> getUserforms() {
        return userforms;
    }

    public void setUserforms(List<UserForm> userforms) {
        this.userforms = userforms;
    }

    protected boolean fromListPage;

    public boolean getFromListPage() {
        return fromListPage;
    }

    public void setFromListPage(boolean fromListPage) {
        this.fromListPage = fromListPage;
    }

	@Override
	protected boolean isParameterExcludedForUnsafeHtmlTagCheck( String parameterName, HttpServletRequest request) {
		return parameterName.equals("arText") || parameterName.equals("arHtml");
	}
}
