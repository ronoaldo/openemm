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

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.beans.Admin;
import org.agnitas.beans.AdminEntry;
import org.agnitas.dao.AdminDao;
import org.agnitas.service.UserActivityLogQueryWorker;
import org.agnitas.service.UserActivityLogService;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.forms.UserActivityLogForm;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.displaytag.pagination.PaginatedList;
import org.springframework.context.ApplicationContext;

/**
 * @author Viktor Gema
 */
public class UserActivityLogAction extends StrutsActionBase {

    public static final String FUTURE_TASK = "USER_ACTIVITY_LOG_LIST";

    private AdminDao adminDao;
    private ExecutorService workerExecutorService;
    private UserActivityLogService userActivityLogService;
    private AbstractMap<String, Future> futureHolder;

    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     * <br>
     * calls a FutureHolder to get the list of entries.<br>
	 * While FutureHolder is running, destination is "loading". <br>
	 * After FutureHolder is finished destination is "list".<br>
     * Also loads lists of all users and users by company into form.
     * <br><br>
     * @param mapping The ActionMapping used to select this instance
     * @param form ActionForm object contains data for the action filled by the jsp
     * @param req  HTTP request
     * @param res  HTTP response
     * @return destination specified in struts-config.xml to forward to next jsp
     * @throws java.io.IOException if an input/output error occurs
     * @throws javax.servlet.ServletException if a servlet exception occurs
     */
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest req,
                                 HttpServletResponse res)
            throws IOException, ServletException {


        String futureKey = FUTURE_TASK + "@" + req.getSession(false).getId();
        ApplicationContext aContext = this.getWebApplicationContext();
        UserActivityLogForm aForm = null;
        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();
        ActionForward destination = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Admin admin = AgnUtils.getAdmin(req);
        if (!AgnUtils.isUserLoggedIn(req)) {
            return mapping.findForward("logon");
        }
        SimpleDateFormat localeFormat = getLocaleFormat(admin);

        if (form != null) {
            aForm = (UserActivityLogForm) form;
            if (StringUtils.isEmpty(aForm.getFromDate())) {
                aForm.setFromDate(localeFormat.format(new Date()));
            }
            if (StringUtils.isEmpty(aForm.getToDate())) {
                aForm.setToDate(localeFormat.format(new Date()));
            }
            if (StringUtils.isEmpty(aForm.getUsername())) {
                if (!allowed("adminlog.show", req) && !allowed("masterlog.show", req)) {
                    aForm.setUsername(AgnUtils.getAdmin(req).getUsername());
                }
            }
        } else {
            aForm = new UserActivityLogForm();
            aForm.setFromDate(localeFormat.format(new Date()));
            aForm.setToDate(localeFormat.format(new Date()));
            if (!allowed("adminlog.show", req) && !allowed("masterlog.show", req)) {
                aForm.setUsername(AgnUtils.getAdmin(req).getUsername());
            }
        }

        AgnUtils.logger().info("Action: " + aForm.getAction());




//        switch (aForm.getAction()) {
//            case UserActivityLogAction.ACTION_LIST:
//                if ( aForm.getColumnwidthsList() == null) {
//                    	aForm.setColumnwidthsList(getInitializedColumnWidthList(5));
//                    }
//                if (allowed("userlog.show", req) || allowed("adminlog.show", req) || allowed("masterlog.show", req)) {
//                    destination = mapping.findForward("list");
//                } else {
//                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
//                }
//                break;
//            default:
//                aForm.setAction(UserActivityLogAction.ACTION_LIST);
//        	}
        	aForm.setAction(UserActivityLogAction.ACTION_LIST);
        	if ( aForm.getColumnwidthsList() == null) {
        		aForm.setColumnwidthsList(getInitializedColumnWidthList(5));
        	}
        	if (allowed("userlog.show", req) || allowed("adminlog.show", req) || allowed("masterlog.show", req)) {
        		destination = mapping.findForward("list");
        	} else {
        		errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
        	}
        if (destination != null && "list".equals(destination.getName())) {
            try {
                setNumberOfRows(req, aForm);
                destination = mapping.findForward("loading");

                if (!futureHolder.containsKey(futureKey)) {
                    // normalize dates by pattern "yyyy-MM-dd"
                    try {
                        Date fromDate = localeFormat.parse(aForm.getFromDate());
                        aForm.setFromDate(dateFormat.format(fromDate));
                    }
                    catch (java.text.ParseException e) {

                    }
                    try {
                        Date toDate = localeFormat.parse(aForm.getToDate());
                        aForm.setToDate(dateFormat.format(toDate));
                    }
                    catch (java.text.ParseException e) {

                    }
                    futureHolder.put(futureKey, getRecipientListFuture(req, aContext, aForm));
                }

                if (futureHolder.containsKey(futureKey) && futureHolder.get(futureKey).isDone()) {
                    req.setAttribute("userActivitylogList", futureHolder.get(futureKey).get());
                    //req.getSession().setAttribute("recipientsInCurrentTable", futureHolder.get(futureKeyList).get());
                    destination = mapping.findForward("list");
                    aForm.setAll(((PaginatedList) futureHolder.get(futureKey).get()).getFullListSize());
                    futureHolder.remove(futureKey);
                    aForm.setRefreshMillis(RecipientForm.DEFAULT_REFRESH_MILLIS);
                    Date fromDate = dateFormat.parse(aForm.getFromDate());
                    aForm.setFromDate(localeFormat.format(fromDate));
                    Date toDate = dateFormat.parse(aForm.getToDate());
                    aForm.setToDate(localeFormat.format(toDate));
                    SimpleDateFormat localeTableFormat = getLocaleTableFormat(admin);
                    req.setAttribute("localDatePattern", localeFormat.toPattern());
                    req.setAttribute("localeTablePattern", localeTableFormat.toPattern());
                } else {
                    if (aForm.getRefreshMillis() < 1000) { // raise the refresh time
                        aForm.setRefreshMillis(aForm.getRefreshMillis() + 50);
                    }
                    aForm.setError(false);
                }
                //retrieve list of users
                aForm.setAdminList(adminDao.getAllAdmins());
                aForm.setAdminByCompanyList(adminDao.getAllAdminsByCompanyId(aForm.getCompanyID(req)));

            } catch (Exception e) {
                AgnUtils.logger().error("useractivitylogList: " + e + "\n" + AgnUtils.getStackTrace(e));
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
                aForm.setError(true); // do not refresh when an error has been occurred
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
     *  Gets the list of user activity log entries according to given sorting parameters and filter conditions
     *  (activity action of certain type, date/time logging period, activity of certain admin).
     *
     * @param request  HTTP request
     * @param aContext  application context
     * @param aForm  UserActivityLogForm object
     * @return Future object contains the paginated list of user activity log entries
     * @throws NumberFormatException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws IntrospectionException
     * @throws InvocationTargetException
     */
    public Future getRecipientListFuture(HttpServletRequest request, ApplicationContext aContext, UserActivityLogForm aForm)
            throws NumberFormatException, IllegalAccessException, InstantiationException, InterruptedException, ExecutionException, IntrospectionException, InvocationTargetException {


        String sort = getSort(request, aForm);
        String direction = request.getParameter("dir");
        int rownums = aForm.getNumberofRows();


        List<AdminEntry> admins = null;
        if (AgnUtils.allowed("masterlog.show", request)) {
            admins = adminDao.getAllAdmins();
        } else {
            admins = adminDao.getAllAdminsByCompanyId(AgnUtils.getCompanyID(request));
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

        Future future = workerExecutorService.submit(new UserActivityLogQueryWorker(userActivityLogService, AgnUtils.getAdmin(request).getAdminID(), Integer.parseInt(pageStr), rownums, aForm, sort, direction, admins));
        return future;

    }

    /**
     * Gets date format according to current admin locale
     * @param admin current admin in session
     * @return SimpleDateFormat object
     */
    protected SimpleDateFormat getLocaleFormat(Admin admin) {
		Locale locale = admin.getLocale();
		return (SimpleDateFormat) SimpleDateFormat.getDateInstance( DateFormat.SHORT, locale);
	}

    /**
     * Gets format for date and time according to current admin locale
     * @param admin current admin in session
     * @return SimpleDateFormat object
     */
    protected SimpleDateFormat getLocaleTableFormat(Admin admin) {
		Locale locale = admin.getLocale();
		return (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale);
	}

    public AdminDao getAdminDao() {
        return adminDao;
    }

    public void setAdminDao(AdminDao adminDao) {
        this.adminDao = adminDao;
    }

    public ExecutorService getWorkerExecutorService() {
        return workerExecutorService;
    }

    public void setWorkerExecutorService(ExecutorService workerExecutorService) {
        this.workerExecutorService = workerExecutorService;
    }

    public UserActivityLogService getUserActivityLogService() {
        return userActivityLogService;
    }

    public void setUserActivityLogService(UserActivityLogService userActivityLogService) {
        this.userActivityLogService = userActivityLogService;
    }

    public AbstractMap<String, Future> getFutureHolder() {
        return futureHolder;
    }

    public void setFutureHolder(AbstractMap<String, Future> futureHolder) {
        this.futureHolder = futureHolder;
    }
}
