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
import java.util.GregorianCalendar;
import java.util.AbstractMap;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.beans.Mailloop;
import org.agnitas.beans.factory.MailloopFactory;
import org.agnitas.dao.MailinglistDao;
import org.agnitas.dao.MailloopDao;
import org.agnitas.dao.UserFormDao;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.forms.StrutsFormBase;
import org.agnitas.service.MailloopListQueryWorker;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * Implementation of <strong>Action</strong> that handles Mailloop-Data
 *
 * @author Martin Helff
 */

public final class MailloopAction extends StrutsActionBase {

    public static final int ACTION_SEND_TEST = ACTION_LAST + 1;
    public static final int ACTION_VIEW_WITHOUT_LOAD = ACTION_LAST + 2;

    private static final String FUTURE_TASK = "GET_MAILLOOP_LIST";

    private MailloopDao mailloopDao;
    private MailinglistDao mailinglistDao;
    private UserFormDao userFormDao;
    private ExecutorService executorService;
    private AbstractMap<String, Future> futureHolder;
    private MailloopFactory mailloopFactory;

    // --------------------------------------------------------- Public Methods


    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     * All actions requires "mailing.show" permission. <br>
     * ACTION_LIST: calls a FutureHolder to get the list of entries.<br>
     * 		While FutureHolder is running destination is "loading". <br>
     * 		After FutureHolder is finished destination is "list".
     * <br><br>
     * ACTION_VIEW: loads data of chosen bounce-filter into form,<br>
     * 		forwards to view page.
     * <br><br>
     * ACTION_NEW: clears form data, <br>
     *                loads form data from database, <br>
     *                forwards to page of creation new bounce-filter entry
     * <br><br>
     * ACTION_SAVE: saves new bounce-filter or updates existing bounce-filter in database.  <br>
     *     Requires request parameters "saveMailloop" for saving. <br>
     *          Calls a FutureHolder to get the list of entries.<br>
     * 	        While FutureHolder is running destination is "loading". <br>
     * 	        After FutureHolder is finished destination is "list".
     * <br><br>
     * ACTION_CONFIRM_DELETE: loads form data, <br>
     *                forwards to jsp with question to confirm deletion.
     * <br><br>
     * ACTION_DELETE: deletes the entry of bounce-filter, <br>
     *                calls a FutureHolder to get the list of entries.<br>
     * 	        While FutureHolder is running destination is "loading". <br>
     * 	        After FutureHolder is finished destination is "list".
     * <br><br>
     * ACTION_VIEW_WITHOUT_LOAD: is used after failing form validation <br>
     *                for loading essential data into request before returning to the view page;<br>
     *                does not reload form data
     * <br><br>
     * Any other ACTION_* would cause a forward to "list"
     * <br><br>
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param req The HTTP request we are processing
     * @param res The HTTP response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest req,
            HttpServletResponse res)
            throws Exception {

        // Validate the request parameters specified by the user
        MailloopForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();
        ActionForward destination=null;

        if(!AgnUtils.isUserLoggedIn(req)) {
            return mapping.findForward("logon");
        }

        aForm=(MailloopForm)form;


        try {
            switch(aForm.getAction()) {
                case MailloopAction.ACTION_LIST:
                    if(allowed("mailing.show", req)) {
                        destination = prepareList(mapping, req, errors, destination, aForm);
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case MailloopAction.ACTION_VIEW:
                    if(allowed("mailing.show", req)) {
                        if(aForm.getMailloopID()!=0) {
                            loadMailloop(aForm, req);
                        } else {
                            aForm.clearData();
                        }
                        loadMailloopFormData(aForm, req);

                        aForm.setAction(MailloopAction.ACTION_SAVE);
                        destination=mapping.findForward("view");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case MailloopAction.ACTION_NEW:
                    if(allowed("mailing.show", req)) {
                        aForm.clearData();
                        aForm.setAction(MailloopAction.ACTION_SAVE);
                        destination=mapping.findForward("view");
                        loadMailloopFormData(aForm, req);
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case MailloopAction.ACTION_SAVE:
                    if(allowed("mailing.show", req)) {
                        if (AgnUtils.parameterNotEmpty(req, "saveMailloop")) {
                            saveMailloop(aForm, req);
                            destination = prepareList(mapping, req, errors, destination, aForm);

                            // Show "changes saved"
                            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                        }
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case MailloopAction.ACTION_CONFIRM_DELETE:
                    if(allowed("mailing.show", req)) {
                        loadMailloop(aForm, req);
                        destination=mapping.findForward("delete");
                        aForm.setAction(MailloopAction.ACTION_DELETE);
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case MailloopAction.ACTION_DELETE:
                    if(allowed("mailing.show", req)) {
                        deleteMailloop(aForm, req);
//                        aForm.setAction(MailloopAction.ACTION_LIST);
                        destination=prepareList(mapping, req, errors, destination, aForm);

                        // Show "changes saved"
                        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                case MailloopAction.ACTION_VIEW_WITHOUT_LOAD:
                    if(allowed("mailing.show", req)) {
                        loadMailloopFormData(aForm, req);

                        aForm.setAction(MailloopAction.ACTION_SAVE);
                        destination=mapping.findForward("view");
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                    }
                    break;

                default:
                    aForm.setAction(MailloopAction.ACTION_LIST);
                    destination=mapping.findForward("list");
                    break;
            }

        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }

		 if(destination != null &&  "list".equals(destination.getName())) {
        	setNumberOfRows(req, aForm);
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

    protected void loadMailloop(MailloopForm aForm, HttpServletRequest req) {
        Mailloop aLoop = mailloopDao.getMailloop(aForm.getMailloopID(), getCompanyID(req));
        if(aLoop!=null) {
            try {
                BeanUtils.copyProperties(aForm, aLoop);
                AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": do load bounce filter " + aLoop.getShortname());
            } catch (Exception e) {
                AgnUtils.logger().error("loadMailloop: "+e);
            }
        } else {
            AgnUtils.logger().error("loadMailloop: could not load Mailloop");
        }
    }

    protected void loadMailloopFormData(MailloopForm aForm, HttpServletRequest req) {
        int companyID = AgnUtils.getCompanyID(req);
        aForm.setMailinglists(mailinglistDao.getMailinglists(companyID));
        aForm.setUserforms(userFormDao.getUserForms(companyID));
    }

    /**
     * Saves mailloop.
     */
    protected void saveMailloop(MailloopForm aForm, HttpServletRequest req) {
    	java.util.Calendar cal=new GregorianCalendar();
        Mailloop aLoop=null;
        int loopID=aForm.getMailloopID();
        java.sql.Timestamp ts = new java.sql.Timestamp(cal.getTime().getTime());
        aForm.setChangedate(ts);

        if(loopID!=0) {
            aLoop=mailloopDao.getMailloop(aForm.getMailloopID(), getCompanyID(req));
        }

        if(aLoop==null) {
            aLoop=mailloopFactory.newMailloop();
            aLoop.setCompanyID(getCompanyID(req));
            loopID=0;
        }

        try {
            BeanUtils.copyProperties(aLoop, aForm);
            aLoop.setId(loopID);
        } catch (Exception e) {
            AgnUtils.logger().error("saveMailloop: "+e);
        }

        mailloopDao.saveMailloop(aLoop);
        if (loopID==0){
            AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": create bounce filter " + aLoop.getShortname());
        }else{
            AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": edit bounce filter " + aLoop.getShortname());
        }
    }

    /**
     * Removes mailloop.
     */
    protected void deleteMailloop(MailloopForm aForm, HttpServletRequest req) {
        if(aForm.getMailloopID()!=0) {
            mailloopDao.deleteMailloop(aForm.getMailloopID(), getCompanyID(req));
            AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": delete bounce filter " + aForm.getShortname());
        }
    }

    private ActionForward prepareList(ActionMapping mapping,
                                      HttpServletRequest req, ActionMessages errors,
                                      ActionForward destination, MailloopForm mailloopForm) {
        ActionMessages messages = null;

        try {
            setNumberOfRows(req, mailloopForm);
            destination = mapping.findForward("loading");
            String key = FUTURE_TASK + "@" + req.getSession(false).getId();
            if (!futureHolder.containsKey(key)) {
                Future mailloopFuture = getMaillooplistFuture(req, mailloopForm);
                futureHolder.put(key, mailloopFuture);
            }
            if (futureHolder.containsKey(key) && futureHolder.get(key).isDone()) {
                req.setAttribute("mailloopEntries", futureHolder.get(key).get());
                destination = mapping.findForward("list");
                futureHolder.remove(key);
                mailloopForm.setRefreshMillis(RecipientForm.DEFAULT_REFRESH_MILLIS);
                messages = mailloopForm.getMessages();

                if (messages != null && !messages.isEmpty()) {
                    saveMessages(req, messages);
                    mailloopForm.setMessages(null);
                }
            } else {
                if (mailloopForm.getRefreshMillis() < 1000) { // raise the refresh time
                    mailloopForm.setRefreshMillis(mailloopForm.getRefreshMillis() + 50);
                }
                mailloopForm.setError(false);
            }
        }
        catch (Exception e) {
            AgnUtils.logger().error("mailloop: " + e + "\n" + AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
            mailloopForm.setError(true); // do not refresh when an error has been occurred
        }
        return destination;
    }

    protected Future getMaillooplistFuture(HttpServletRequest request, StrutsFormBase aForm) throws NumberFormatException, IllegalAccessException, InstantiationException, InterruptedException, ExecutionException {

        String sort = getSort(request, aForm);
        String direction = request.getParameter("dir");

        int rownums = aForm.getNumberofRows();
        if (direction == null) {
            direction = aForm.getOrder();
        } else {
            aForm.setOrder(direction);
        }

        String pageStr = request.getParameter("page");
        if (pageStr == null || "".equals(pageStr.trim())) {
            if (aForm.getPage() == null || "".equals(aForm.getPage().trim())) {
                aForm.setPage("1");
            }
            pageStr = aForm.getPage();
        } else {
            aForm.setPage(pageStr);
        }

        if (aForm.isNumberOfRowsChanged()) {
            aForm.setPage("1");
            aForm.setNumberOfRowsChanged(false);
            pageStr = "1";
        }

        int companyID = AgnUtils.getCompanyID(request);
        Future future = executorService.submit(new MailloopListQueryWorker(mailloopDao, companyID, sort, direction, Integer.parseInt(pageStr), rownums));

        return future;

    }

    public void setMailloopDao(MailloopDao mailloopDao) {
        this.mailloopDao = mailloopDao;
    }

    public void setMailinglistDao(MailinglistDao mailinglistDao) {
        this.mailinglistDao = mailinglistDao;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void setFutureHolder(AbstractMap<String, Future> futureHolder) {
        this.futureHolder = futureHolder;
    }

    public void setMailloopFactory(MailloopFactory mailloopFactory) {
        this.mailloopFactory = mailloopFactory;
    }

    public void setUserFormDao(UserFormDao userFormDao) {
        this.userFormDao = userFormDao;
    }
}
