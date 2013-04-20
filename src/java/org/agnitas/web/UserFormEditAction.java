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
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.beans.UserForm;
import org.agnitas.beans.factory.UserFormFactory;
import org.agnitas.dao.EmmActionDao;
import org.agnitas.dao.UserFormDao;
import org.agnitas.util.AgnUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;


/**
 * Implementation of <strong>Action</strong> that handle input of user forms.
 *
 * @author mhe
 */

public class UserFormEditAction extends StrutsActionBase {
	private static final transient Logger logger = Logger.getLogger(UserFormEditAction.class);

    public static final int ACTION_VIEW_WITHOUT_LOAD = ACTION_LAST + 1;    
    public static final int ACTION_SECOND_LAST = ACTION_LAST + 1;

    protected UserFormDao userFormDao;
    protected EmmActionDao emmActionDao;
    protected UserFormFactory userFormFactory;

    // --------------------------------------------------------- Public Methods


    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     * <br>
	 * ACTION_LIST: loads list of user forms into request;<br>
     *     forwards to user form list page.
	 * <br><br>
	 * ACTION_SAVE: saves user form data in database, sets user form id into form;<br>
     *     loads lists of user forms and actions that has ACTION_TYPE 0 or 9 in database into request;<br>
     *     sets destination = "success",<br>
     *     forwards to user form view page.
     * <br><br>
     * ACTION_VIEW: loads data of chosen user form data into form;<br>
     *    loads list of actions that has ACTION_TYPE 0 or 9 in database into request;<br>
     *      forwards to user form view page
     * <br><br>
     * ACTION_VIEW_WITHOUT_LOADING: is used after failing form validation<br>
     *      for loading essential data into request before returning to the view page;<br>
     *      does not reload form data.
     * <br><br>
     *ACTION_CONFIRM_DELETE:  loads data of chosen user form into form, <br>
     *      forwards to jsp with question to confirm deletion.
     * <br><br>
     * ACTION_DELETE: deletes the entry of certain user form;<br>
     * loads list of actions that has ACTION_TYPE 0 or 9 in database into request;<br>
     *          forwards to user form list page.
	 * <br><br>
	 * Any other ACTION_* would cause a forward to "list"
     * <br><br>
     * @param form
     * @param req
     * @param res
     * @param mapping The ActionMapping used to select this instance
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     * @return the action to forward to.
     */
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        // Validate the request parameters specified by the user
        UserFormEditForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();
        ActionForward destination=null;

        if(!AgnUtils.isUserLoggedIn(req)) {
            return mapping.findForward("logon");
        }

        aForm=(UserFormEditForm)form;
        if (logger.isInfoEnabled()) logger.info("Action: "+aForm.getAction());

        try {
            switch(aForm.getAction()) {
                case UserFormEditAction.ACTION_LIST:
                    if(allowed("forms.view", req)) {
                        destination=mapping.findForward("list");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case UserFormEditAction.ACTION_VIEW:
                    if(allowed("forms.view", req)) {
                        if(aForm.getFormID()!=0) {
                            loadUserForm(aForm, req);
                        }
                        loadEmmActions(req);
                        aForm.setAction(UserFormEditAction.ACTION_SAVE);
                        destination=mapping.findForward("view");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case UserFormEditAction.ACTION_SAVE:
                    if(allowed("forms.change", req)) {
                        destination=mapping.findForward("success");
                        saveUserForm(aForm, req);
                        loadEmmActions(req);
                        
                        // Show "changes saved"
                        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case UserFormEditAction.ACTION_CONFIRM_DELETE:
                    if(allowed("forms.delete", req)) {
                        loadUserForm(aForm, req);
                        aForm.setAction(UserFormEditAction.ACTION_DELETE);
                        destination=mapping.findForward("delete");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case UserFormEditAction.ACTION_DELETE:
                    if(allowed("forms.delete", req)) {
                        if(AgnUtils.parameterNotEmpty(req, "delete")) {
                            deleteUserForm(aForm, req);
                        }
                        aForm.setAction(UserFormEditAction.ACTION_LIST);
                        destination=mapping.findForward("list");

                        // Show "changes saved"
                        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;
                case UserFormEditAction.ACTION_VIEW_WITHOUT_LOAD:
                    if(allowed("forms.view", req)) {
                        loadEmmActions(req);
                        aForm.setAction(UserFormEditAction.ACTION_SAVE);
                        destination=mapping.findForward("view");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                default:
                    aForm.setAction(UserFormEditAction.ACTION_LIST);
                    if(allowed("forms.view", req)) {
                        destination=mapping.findForward("list");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
            }
        } catch (Exception e) {
            logger.error("execute: "+e+"\n"+AgnUtils.getStackTrace(e), e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }

        if(destination != null && ("list".equals(destination.getName()) || "success".equals(destination.getName()))) {
			if ( aForm.getColumnwidthsList() == null) {
        		aForm.setColumnwidthsList(getInitializedColumnWidthList(3));
        	}

			try {
				req.setAttribute("userformlist", userFormDao.getUserForms(AgnUtils.getCompanyID(req)));
				setNumberOfRows(req, aForm);
			} catch (Exception e) {
				logger.error("userformlist: "+e+"\n"+AgnUtils.getStackTrace(e), e);
	            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
			}
        }
        
        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
        }

        // Report any message (non-errors) we have discovered
        if (!messages.isEmpty()) {
        	saveMessages(req, messages);
        }

        return destination;
    }

    /**
     * Load a user form data into form.
     * Retrieves the data of a form from the database.
     * @param aForm on input contains the id of the form.
     *              On exit contains the data read from the database.
     * @param req request
     * @throws Exception 
     */
    protected void loadUserForm(UserFormEditForm aForm, HttpServletRequest req) throws Exception {
        UserForm aUserForm=userFormDao.getUserForm(aForm.getFormID(), AgnUtils.getCompanyID(req));

        if(aUserForm!=null && aUserForm.getId()!=0) {
            aForm.setFormName(aUserForm.getFormName());
            aForm.setDescription(aUserForm.getDescription());
            aForm.setStartActionID(aUserForm.getStartActionID());
            aForm.setEndActionID(aUserForm.getEndActionID());
            aForm.setSuccessTemplate(aUserForm.getSuccessTemplate());
            aForm.setErrorTemplate(aUserForm.getErrorTemplate());
            aForm.setSuccessUrl(aUserForm.getSuccessUrl());
            aForm.setErrorUrl(aUserForm.getErrorUrl());
            aForm.setSuccessUseUrl(aUserForm.isSuccessUseUrl());
            aForm.setErrorUseUrl(aUserForm.isErrorUseUrl());
            if (logger.isInfoEnabled()) logger.info("loadUserForm: form "+aForm.getFormID()+" loaded");
            AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": do load user form  " + aForm.getFormName());
        } else {
            AgnUtils.userlogger().warn("loadUserForm: could not load userform "+aForm.getFormID());
        }
    }

    /**
     * Save a user form in database; loads user form id into form.
     * Writes the data of a form to the database.             
     * @param aForm contains the data of the form.
     * @param req  request
     * @throws Exception 
     */
    protected void saveUserForm(UserFormEditForm aForm, HttpServletRequest req) throws Exception {
        UserForm aUserForm=userFormFactory.newUserForm();

        aUserForm.setCompanyID(AgnUtils.getAdmin(req).getCompany().getId());
        aUserForm.setId(aForm.getFormID());
        aUserForm.setFormName(aForm.getFormName());
        aUserForm.setDescription(aForm.getDescription());
        aUserForm.setStartActionID(aForm.getStartActionID());
        aUserForm.setEndActionID(aForm.getEndActionID());
        aUserForm.setSuccessTemplate(aForm.getSuccessTemplate());
        aUserForm.setErrorTemplate(aForm.getErrorTemplate());
        aUserForm.setSuccessUrl(aForm.getSuccessUrl());
        aUserForm.setErrorUrl(aForm.getErrorUrl());
        aUserForm.setSuccessUseUrl(aForm.isSuccessUseUrl());
        aUserForm.setErrorUseUrl(aForm.isErrorUseUrl());
        int newFormId = userFormDao.storeUserForm(aUserForm);
        if (aForm.getFormID() == 0) {
            AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": create user form  " + aForm.getFormName());
        } else {
            AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": edit user form  " + aForm.getFormName());
        }
        aForm.setFormID(newFormId);
    }

    /**
     * Delete a user form.
     * Removes the data of a form from the database.
     * @param aForm contains the id of the form.
     * @param  req request
     */
    protected void deleteUserForm(UserFormEditForm aForm, HttpServletRequest req) {
        userFormDao.deleteUserForm(aForm.getFormID(), AgnUtils.getAdmin(req).getCompany().getId());
        AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": delete user form  " + aForm.getFormName());
    }

    protected void loadEmmActions(HttpServletRequest req){
        List emmActions = emmActionDao.getEmmNotLinkActions(AgnUtils.getAdmin(req).getCompany().getId());
        req.setAttribute("emm_actions", emmActions);
    }

    public void setUserFormDao(UserFormDao userFormDao) {
        this.userFormDao = userFormDao;
    }

    public void setEmmActionDao(EmmActionDao emmActionDao) {
        this.emmActionDao = emmActionDao;
    }

    public void setUserFormFactory(UserFormFactory userFormFactory) {
        this.userFormFactory = userFormFactory;
    }
}
