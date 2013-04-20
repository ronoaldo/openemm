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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.agnitas.actions.ActionOperation;
import org.agnitas.actions.EmmAction;
import org.agnitas.beans.factory.ActionOperationFactory;
import org.agnitas.beans.factory.EmmActionFactory;
import org.agnitas.dao.CampaignDao;
import org.agnitas.dao.EmmActionDao;
import org.agnitas.dao.MailingDao;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.SafeString;
import org.agnitas.web.forms.EmmActionForm;
import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Implementation of <strong>Action</strong> that handles Targets
 *
 * @author Martin Helff
 */

public class EmmActionAction extends StrutsActionBase {
	private static final transient Logger logger = Logger.getLogger(EmmActionAction.class);
    
    public static final int ACTION_LIST = 1;
    public static final int ACTION_VIEW = 2;
    public static final int ACTION_SAVE = 4;
    public static final int ACTION_NEW = 6;
    public static final int ACTION_DELETE = 7;
    public static final int ACTION_CONFIRM_DELETE = 8;
    public static final int ACTION_ADD_MODULE = 9;

    private CampaignDao campaignDao;
    private EmmActionDao emmActionDao;
    private DataSource dataSource;
    private EmmActionFactory emmActionFactory;
    private ActionOperationFactory actionOperationFactory;
    private MailingDao mailingDao;
    
    // --------------------------------------------------------- Public Methods
    
    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     * <br>
	 * ACTION_LIST: initializes columns width list if necessary, forwards to emm action list page.
	 * <br><br>
	 * ACTION_SAVE: saves emm action data in database; sets new emm action id in form field; forwards
     *     to emm action view page.
	 * <br><br>
     * ACTION_VIEW: loads data of chosen emm action into form, forwards to emm action view page
     * <br><br>
     * ACTION_ADD_MODULE: adds new action operation module to the given emm action, forwards to emm action view page.
     * <br><br>
	 * ACTION_CONFIRM_DELETE: loads data of chosen emm action into form, forwards to jsp with question to confirm deletion
	 * <br><br>
	 * ACTION_DELETE: deletes the entry of certain emm action, forwards to emm action list page
	 * <br><br>
	 * Any other ACTION_* would cause a forward to "list"
     * <br><br>
     * If the forward is "list" - loads list of emm-actions to request; also loads list of campaigns and list of
     * sent actionbased-mailings (sets that to form)
     *
     * @param form ActionForm object, data for the action filled by the jsp
     * @param req  HTTP request
     * @param res HTTP response
     * @param mapping The ActionMapping used to select this instance
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     * @return destination specified in struts-config.xml to forward to next jsp
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
    	
        EmmActionForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();
        ActionForward destination=null;
        
        if(!AgnUtils.isUserLoggedIn(req)) {
            return mapping.findForward("logon");
        }
        
        aForm=(EmmActionForm)form;
        
        req.getSession().setAttribute("oplist", this.getActionOperations(req)); // TODO: Improvement required. Session scope is bad here and in view.jsp
        
		if (logger.isInfoEnabled()) logger.info("Action: " + aForm.getAction());
        try {
            switch(aForm.getAction()) {
                case EmmActionAction.ACTION_LIST:
                    if(allowed("actions.show", req)) {
                    	//loadActionUsed(aForm, req);
						if ( aForm.getColumnwidthsList() == null) {
                    		aForm.setColumnwidthsList(getInitializedColumnWidthList(4));
                    	}
                        destination=mapping.findForward("list");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                case EmmActionAction.ACTION_VIEW:
                    if(allowed("actions.show", req)) {
                        if(aForm.getActionID()!=0) {
                            aForm.setAction(EmmActionAction.ACTION_SAVE);
                            loadAction(aForm, req);
                        } else {
                            aForm.setAction(EmmActionAction.ACTION_SAVE);
                        }
                        destination=mapping.findForward("success");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                case EmmActionAction.ACTION_SAVE:
                    if(allowed("actions.change", req)) {
                        saveAction(aForm, req);

                    	// Show "changes saved", only if we didn't request a module to be removed
                        if(req.getParameter("deleteModule") == null) {
                        	messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                        }
                        destination=mapping.findForward("success");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                case EmmActionAction.ACTION_CONFIRM_DELETE:
                    loadAction(aForm, req);
                    destination=mapping.findForward("delete");
                    aForm.setAction(EmmActionAction.ACTION_DELETE);
                    break;
                case EmmActionAction.ACTION_DELETE:
                    if(allowed("actions.delete", req)) {
                        deleteAction(aForm, req);

                        // Show "changes saved"
                        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                        aForm.setAction(EmmActionAction.ACTION_LIST);
                        destination=mapping.findForward("list");
                    } else {	
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                case EmmActionAction.ACTION_ADD_MODULE:
                    ActionOperation aMod = actionOperationFactory.newActionOperation(aForm.getNewModule());
                    ArrayList actions=aForm.getActions();
                    if(actions==null) {
                        actions=new ArrayList();
                    }
                    actions.add(aMod);
                    aForm.setActions(actions);
                    aForm.setAction(EmmActionAction.ACTION_SAVE);
                    destination=mapping.findForward("success");
                    break;
                default:
                    if(allowed("actions.show", req)) {
						if ( aForm.getColumnwidthsList() == null) {
                    		aForm.setColumnwidthsList(getInitializedColumnWidthList(4));
                    	}
                        destination=mapping.findForward("list");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
            }
        } catch (Exception e) {
			logger.error("execute: " + e + "\n" + AgnUtils.getStackTrace(e), e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }
        
        if(destination != null && "list".equals(destination.getName())) {
        	try {
				req.setAttribute("emmactionList", getActionList(req));
				setNumberOfRows(req, aForm);
                aForm.setCampaigns(campaignDao.getCampaignList(AgnUtils.getAdmin(req).getCompany().getId(),"lower(shortname)",1));
                aForm.setMailings(mailingDao.getMailingsByStatusE(AgnUtils.getAdmin(req).getCompany().getId()));
			} catch (Exception e) {
				logger.error("getActionList: " + e + "\n" + AgnUtils.getStackTrace(e), e);
	            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
			}
        }
        
        
        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
            return (new ActionForward(mapping.getInput()));
        }

        // Report any message (non-errors) we have discovered
	     if (!messages.isEmpty()) {
	     	saveMessages(req, messages);
	     }

        return destination;
    }

    /**
     * Loads an emm action data from DB into the form.
     *
     * @param aForm EmmActionForm object
     * @param req  HTTP request
     * @throws Exception
     */
    protected void loadAction(EmmActionForm aForm, HttpServletRequest req) throws Exception {
        EmmAction aAction=emmActionDao.getEmmAction(aForm.getActionID(), AgnUtils.getAdmin(req).getCompany().getId());
        
        if(aAction!=null && aAction.getId()!=0) {
            aForm.setShortname(aAction.getShortname());
            aForm.setDescription(aAction.getDescription());
            aForm.setType(aAction.getType());
            aForm.setActions(aAction.getActions());
            if (logger.isInfoEnabled()) logger.info("loadAction: action "+aForm.getActionID()+" loaded");
            AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": do load action " + aForm.getShortname());
        } else {
			logger.warn("loadAction: could not load action " + aForm.getActionID());
        }
    }

    /**
     * Saves emm action data in database; resets emm action id in the form.
     *
     * @param aForm EmmActionForm object
     * @param req HTTP request
     * @throws Exception
     */
    protected void saveAction(EmmActionForm aForm, HttpServletRequest req) throws Exception {
        EmmAction aAction=emmActionFactory.newEmmAction();

        aAction.setCompanyID(AgnUtils.getAdmin(req).getCompany().getId());
        aAction.setId(aForm.getActionID());
        aAction.setType(aForm.getType());
        aAction.setShortname(aForm.getShortname());
        aAction.setDescription(aForm.getDescription());
        aAction.setActions(aForm.getActions());

        final int newEmmActionId = emmActionDao.saveEmmAction(aAction);
        if (aForm.getActionID() == 0) {
            AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": create action " + aForm.getShortname());
        } else {
            AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": edit action " + aForm.getShortname());
        }
        aForm.setActionID(newEmmActionId);
    }
    
    /**
     * Deletes an action.
     *
     * @param aForm EmmActionForm object
     * @param req HTTP request
     */
    protected void deleteAction(EmmActionForm aForm, HttpServletRequest req) {
       emmActionDao.deleteEmmAction(aForm.getActionID(), AgnUtils.getAdmin(req).getCompany().getId());
        AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": delete action " + aForm.getShortname());
    }

    /**
     * Gets action operations map.
     *
     * @param req  HTTP request
     * @return Map object contains emm action operations
     */
    protected Map getActionOperations(HttpServletRequest req) {
        String name=null;
        String key=null;
        TreeMap ops=new TreeMap();
        ApplicationContext con = WebApplicationContextUtils.getRequiredWebApplicationContext(req.getSession().getServletContext());
        String[] names=con.getBeanNamesForType(org.agnitas.actions.ActionOperation.class);
        for(int i=0; i<names.length; i++) {
            name=names[i];
            if(allowed("action.op."+name, req)) {
                key = SafeString.getLocaleString("action.op." + name, (Locale) req.getSession().getAttribute(Globals.LOCALE_KEY));
                ops.put(key, name);
            }
        }
        return ops;
    }
    
    /**
     * @deprecated   is not in use yet
     */
    
    protected void loadActionUsed(EmmActionForm aForm, HttpServletRequest req) throws Exception {
        Map used = emmActionDao.loadUsed(AgnUtils.getAdmin(req).getCompany().getId());
        aForm.setUsed(used);
    }

    public List<EmmAction> getActionList(HttpServletRequest request) throws IllegalAccessException, InstantiationException {
        return emmActionDao.getActionList(request);
    }

    public CampaignDao getCampaignDao() {
        return campaignDao;
    }

    public void setCampaignDao(CampaignDao campaignDao) {
        this.campaignDao = campaignDao;
    }

    public EmmActionDao getEmmActionDao() {
        return emmActionDao;
    }

    public void setEmmActionDao(EmmActionDao emmActionDao) {
        this.emmActionDao = emmActionDao;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public EmmActionFactory getEmmActionFactory() {
        return emmActionFactory;
    }

    public void setEmmActionFactory(EmmActionFactory emmActionFactory) {
        this.emmActionFactory = emmActionFactory;
    }

    public ActionOperationFactory getActionOperationFactory() {
        return actionOperationFactory;
    }

    public void setActionOperationFactory(ActionOperationFactory actionOperationFactory) {
        this.actionOperationFactory = actionOperationFactory;
    }

    public MailingDao getMailingDao() {
        return mailingDao;
    }

    public void setMailingDao(MailingDao mailingDao) {
        this.mailingDao = mailingDao;
    }
}