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

import org.agnitas.beans.Campaign;
import org.agnitas.beans.MailingBase;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.EmmActionAction;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.util.MessageResources;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EmmActionForm extends StrutsFormBase {

    private static final long serialVersionUID = -6049830951608775632L;
	private String shortname;
    private String description;
    private int actionID;
    private int action;
    private ArrayList actions;
    private Map used;
    private List<Campaign> campaigns;
    private List<MailingBase> mailings;
    protected boolean fromListPage;

	public EmmActionForm() {
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

        if(AgnUtils.parameterNotEmpty(request, "add")) {
            this.setAction(EmmActionAction.ACTION_ADD_MODULE);
        }

        if(this.getAction()==EmmActionAction.ACTION_NEW) {
            this.actionID=0;
            Locale aLoc=(Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
            MessageResources text=(MessageResources)this.getServlet().getServletContext().getAttribute(org.apache.struts.Globals.MESSAGES_KEY);
            //MessageResources text=this.getServlet().getResources();

            this.shortname=text.getMessage(aLoc, "default.shortname");
            this.actions=null;
            this.description=text.getMessage(aLoc, "default.description");
            this.deleteModule=0;
            this.type=0;
            this.action=EmmActionAction.ACTION_VIEW;
        }

        if (this.getAction() == EmmActionAction.ACTION_SAVE) {
            if(this.shortname!=null && this.shortname.length()<1) {
                errors.add("shortname", new ActionMessage("error.nameToShort"));
            }
            if (this.shortname!=null && this.shortname.length() > 50) {
                errors.add("shortname", new ActionMessage("error.action.nameTooLong"));
            }
        }

        if(request.getParameter("deleteModule")!=null) {
            if(this.actions!=null) {
                this.actions.remove(this.deleteModule);
            }
        }

        return errors;
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
     * Getter for property actionID.
     *
     * @return Value of property actionID.
     */
    public int getActionID() {

        return this.actionID;
    }

    /**
     * Setter for property actionID.
     *
     * @param actionID New value of property actionID.
     */
    public void setActionID(int actionID) {

        this.actionID = actionID;
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
     * Getter for property actions.
     *
     * @return Value of property actions.
     */
    public ArrayList getActions() {

        return this.actions;
    }

    /**
     * Setter for property actions.
     *
     * @param actions New value of property actions.
     */
    public void setActions(ArrayList actions) {

        this.actions = actions;
    }

    /**
     * Holds value of property type.
     */
    private int type;

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

    /**
     * Holds value of property deleteModule.
     */
    private int deleteModule;

    /**
     * Getter for property deleteModule.
     *
     * @return Value of property deleteModule.
     */
    public int getDeleteModule() {

        return this.deleteModule;
    }

    /**
     * Setter for property deleteModule.
     *
     * @param deleteModule New value of property deleteModule.
     */
    public void setDeleteModule(int deleteModule) {

        this.deleteModule = deleteModule;
    }

    /**
     * Holds value of property newModule.
     */
    private String newModule;

    /**
     * Getter for property newModule.
     *
     * @return Value of property newModule.
     */
    public String getNewModule() {

        return this.newModule;
    }

    /**
     * Setter for property newModule.
     *
     * @param newModule New value of property newModule.
     */
    public void setNewModule(String newModule) {

        this.newModule = newModule;
    }

	public Map getUsed() {
		return used;
	}

	public void setUsed(Map used) {
		this.used = used;
	}

    public List<Campaign> getCampaigns() {
        return campaigns;
    }

    public void setCampaigns(List<Campaign> campaigns) {
        this.campaigns = campaigns;
    }

    public List<MailingBase> getMailings() {
        return mailings;
    }

    public void setMailings(List<MailingBase> mailings) {
        this.mailings = mailings;
    }

    public boolean getFromListPage() {
        return fromListPage;
    }

    public void setFromListPage(boolean fromListPage) {
        this.fromListPage = fromListPage;
    }

	@Override
	protected boolean isParameterExcludedForUnsafeHtmlTagCheck( String parameterName, HttpServletRequest request) {
		return parameterName.endsWith( ".script") || parameterName.endsWith( ".textMail") || parameterName.endsWith( ".htmlMail");
	}
}
