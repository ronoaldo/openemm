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
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.beans.Admin;
import org.agnitas.beans.AdminEntry;
import org.agnitas.beans.AdminGroup;
import org.agnitas.beans.impl.AdminImpl;
import org.agnitas.beans.impl.PaginatedListImpl;
import org.agnitas.dao.AdminDao;
import org.agnitas.dao.AdminGroupDao;
import org.agnitas.dao.CompanyDao;
import org.agnitas.service.AdminListQueryWorker;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.forms.AdminForm;
import org.agnitas.web.forms.StrutsFormBase;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * Implementation of <strong>Action</strong> that handles Account Admins
 * 
 * @author Andreas Rehak, Martin Helff
 */

public class AdminAction extends StrutsActionBase {
	private static final transient Logger logger = Logger.getLogger(AdminAction.class);

	public static final int ACTION_VIEW_RIGHTS = ACTION_LAST + 1;
	public static final int ACTION_SAVE_RIGHTS = ACTION_LAST + 2;
	public static final int ACTION_VIEW_WITHOUT_LOAD = ACTION_LAST + 3;
	private static final String FUTURE_TASK = "GET_ADMIN_LIST";

	protected AdminDao adminDao;
	protected AdminGroupDao adminGroupDao;
	protected CompanyDao companyDao;
	protected ConcurrentHashMap<String, Future<PaginatedListImpl<AdminEntry>>> futureHolder;
	protected ScheduledThreadPoolExecutor executorService;

	// ---------------------------------------- Public Methods

	/**
	 * Process the specified HTTP request, and create the corresponding HTTP
	 * response (or forward to another web component that will create it).
	 * Return an <code>ActionForward</code> instance describing where and how
	 * control should be forwarded, or <code>null</code> if the response has
	 * already been completed.
	 * <br>
	 * ACTION_LIST: calls a FutureHolder to get the list of entries.<br>
	 * 		While FutureHolder is running, destination is "loading". <br>
	 * 		After FutureHolder is finished destination is "list".
	 * <br><br>
	 * ACTION_SAVE: checks, if admin username was changed to one that is used for some another admin of the same company<br>
 * 		    If the username is ok, saves admin data
	 * <br><br>
     * ACTION_VIEW: loads data of chosen admin into form and forwards to admin view page
     * <br><br>
     * ACTION_VIEW_RIGHTS: loads list of permissions for given admin and forwards to user right list page
     * <br><br>
     * ACTION_SAVE_RIGHTS: saves permissions for certain admin and forwards to user right list page
     * <br><br>
     * ACTION_NEW: creates new admin db entry if the password field is not empty (after trimming the password string)<br>
     *      and there is no another admin with the same username;<br>
     *      saves permissions for the new admin;<br>
     *      forwards to admin list page
     * <br><br>
     * ACTION_VIEW_WITHOUT_LOAD: is used after failing form validation<br>
     *      for loading essential data into request before returning to the view page;<br>
     *      does not reload form data
     * <br><br>
	 * ACTION_CONFIRM_DELETE: only forwards to jsp with question to confirm deletion.
	 * <br><br>
	 * ACITON_DELETE: deletes the entry of certain admin, deletes userrights of given admin, forwards to admin list page.
	 * <br><br>
	 * Any other ACTION_* would cause a forward to "list"
     * <br><br>
	 * @param mapping
	 *            The ActionMapping used to select this instance
	 * @param form  ActionForm object, data for the action filled by the jsp
	 * @param req   HTTP request from jsp
	 * @param res   HTTP response
	 * @exception IOException
	 *                if an input/output error occurs
	 * @exception ServletException
	 *                if a servlet exception occurs
	 * @return destination specified in struts-config.xml to forward to next jsp
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		AdminForm aForm = null;
		ActionMessages errors = new ActionMessages();
		ActionMessages messages = new ActionMessages();
		ActionForward destination = null;

		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}

		if (form != null) {
			aForm = (AdminForm) form;
		} else {
			aForm = new AdminForm();
		}

		if (logger.isInfoEnabled()) logger.info("Action: " + aForm.getAction());
		if (req.getParameter("delete") != null && req.getParameter("delete").equals("delete")) {
			aForm.setAction(ACTION_CONFIRM_DELETE);
		}

		try {
			switch (aForm.getAction()) {
			case AdminAction.ACTION_LIST:
				if (allowed("admin.show", req)) {
					destination = prepareList(mapping, req, errors, destination, aForm);
				} else {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
				}
				break;

			case AdminAction.ACTION_VIEW:
				if (allowed("admin.show", req)) {
					if (aForm.getAdminID() != 0) {
						aForm.setAction(AdminAction.ACTION_SAVE);
						loadAdmin(aForm, req);
					} else {
						aForm.setAction(AdminAction.ACTION_NEW);
					}
					destination = mapping.findForward("view");
				} else {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
				}
				break;

			case AdminAction.ACTION_SAVE:
				if (allowed("admin.change", req)) {
					if (AgnUtils.parameterNotEmpty(req, "save")) {
						if (!adminUsernameChangedToExisting(aForm)) {
							saveAdmin(aForm, req);

							// Show "changes saved"
							messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
						} else {
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.username.duplicate"));
						}
					}
				} else {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
				}
				destination = mapping.findForward("view");
				break;

			case AdminAction.ACTION_VIEW_RIGHTS:
				loadAdmin(aForm, req);
				aForm.setAction(AdminAction.ACTION_SAVE_RIGHTS);
				destination = mapping.findForward("rights");
				break;

			case AdminAction.ACTION_SAVE_RIGHTS:
				saveAdminRights(aForm, req);
				loadAdmin(aForm, req);
				aForm.setAction(AdminAction.ACTION_SAVE_RIGHTS);
				destination = mapping.findForward("rights");

				// Show "changes saved"
				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
				break;

			case AdminAction.ACTION_NEW:
				if (allowed("admin.new", req)) {
					if (AgnUtils.parameterNotEmpty(req, "save")) {
						aForm.setAdminID(0);

						if (aForm.getPassword().length() > 0) {
							if (!adminExists(aForm)) {
								try {
									saveAdmin(aForm, req);

									// Show "changes saved"
									messages.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage("default.changes_saved"));

									destination = prepareList(mapping, req, errors, destination, aForm);
									aForm.setAction(AdminAction.ACTION_LIST);
								} catch (Exception e) {
									errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.admin.save"));
									destination = mapping.findForward("view");
									aForm.setAction(AdminAction.ACTION_NEW);
								}
							} else {
								errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.username.duplicate"));
								destination = mapping.findForward("view");
								aForm.setAction(ACTION_NEW);
							}
						} else {
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.admin.no_password"));
							destination = mapping.findForward("view");
							aForm.setAction(AdminAction.ACTION_NEW);
						}
					}
				} else {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
				}
				break;

			case AdminAction.ACTION_CONFIRM_DELETE:
				loadAdmin(aForm, req);
				aForm.setAction(AdminAction.ACTION_DELETE);
				destination = mapping.findForward("delete");
				break;

			case AdminAction.ACTION_DELETE:
				if (req.getParameter("kill") != null) {
					if (allowed("admin.delete", req)) {
						deleteAdmin(aForm, req);

						// Show "changes saved"
						messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
					} else {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
					}
					aForm.setAction(AdminAction.ACTION_LIST);
					destination = prepareList(mapping, req, errors, destination, aForm);
				}
				break;
			case AdminAction.ACTION_VIEW_WITHOUT_LOAD:
				if (allowed("admin.show", req)) {
					req.setAttribute("adminGroups", adminGroupDao.getAdminGroupsByCompanyId(AgnUtils.getAdmin(req).getCompanyID()));
					if (aForm.getAdminID() != 0) {
						aForm.setAction(AdminAction.ACTION_SAVE);
					} else {
						aForm.setAction(AdminAction.ACTION_NEW);
					}
					destination = mapping.findForward("view");
				} else {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
				}
				break;

			default:
				aForm.setAction(AdminAction.ACTION_LIST);
				destination = prepareList(mapping, req, errors, destination,
						aForm);
			}
		} catch (Exception e) {
			logger.error("execute: " + e + "\n" + AgnUtils.getStackTrace(e));
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
			throw new ServletException(e);
		}

		if (destination != null && "view".equals(destination.getName())) {
			req.setAttribute("adminGroups", adminGroupDao.getAdminGroupsByCompanyId(AgnUtils.getAdmin(req).getCompanyID()));
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
	 * Load an admin account. Loads the data of the admin from the database and
	 * stores it in the form.
	 * 
	 * @param aForm
	 *            the formular passed from the jsp
	 * @param req
	 *            the Servlet Request (needed to get the company id)
	 */
	protected void loadAdmin(AdminForm aForm, HttpServletRequest req) {
		int adminID = aForm.getAdminID();
		int compID = AgnUtils.getAdmin(req).getCompany().getId();
		Admin admin = adminDao.getAdmin(adminID, compID);

		if (admin != null) {
			aForm.setUsername(admin.getUsername());
			aForm.setPassword("");
			aForm.setPasswordConfirm("");
			aForm.setCompanyID(admin.getCompany().getId());
			aForm.setFullname(admin.getFullname());
			aForm.setAdminLocale(new Locale(admin.getAdminLang(), admin.getAdminCountry()));
			aForm.setAdminTimezone(admin.getAdminTimezone());
			aForm.setLayoutID(admin.getLayoutID());
			aForm.setGroupID(admin.getGroup().getGroupID());
			aForm.setUserRights(admin.getAdminPermissions());
			aForm.setGroupRights(admin.getGroup().getGroupPermissions());
			aForm.setNumberofRows(admin.getPreferredListSize());
			if (logger.isInfoEnabled()) logger.info("loadAdmin: admin " + aForm.getAdminID()+ " loaded");
		} else {
			aForm.setAdminID(0);
			aForm.setCompanyID(AgnUtils.getAdmin(req).getCompany().getId());
			logger.warn("loadAdmin: admin " + aForm.getAdminID() + " could not be loaded");
		}
	}

	/**
	 * Save an admin account. Gets the admin data from a form and stores it in
	 * the database.
	 * 
	 * @param aForm
	 *            the formular passed from the jsp
	 * @param req
	 *            the Servlet Request (needed to get the company id)
	 */
	protected void saveAdmin(AdminForm aForm, HttpServletRequest req) {
		int adminID = aForm.getAdminID();
		int compID = aForm.getCompanyID();
		int groupID = aForm.getGroupID();
		Admin admin = adminDao.getAdmin(adminID, compID);
		boolean isNew = false;
		if (admin == null) {
			admin = new AdminImpl();
			admin.setCompanyID(compID);
			admin.setCompany(companyDao.getCompany(compID));
			admin.setLayoutID(0);
			isNew = true;
		}

		AdminGroup group = (AdminGroup) adminGroupDao.getAdminGroup(groupID);

		admin.setAdminID(aForm.getAdminID());

		if (!isNew && passwordChanged(admin.getUsername(), aForm.getPassword())) {
			admin.setLastPasswordChange(new Date());
		}

		admin.setUsername(aForm.getUsername());
		if (aForm.getPassword() != null
				&& aForm.getPassword().trim().length() != 0) {
			admin.setPassword(aForm.getPassword());
		}

		if (aForm.getPassword().length() > 0) {
			logger.error("Username: " + aForm.getUsername() + " PasswordLength: " + aForm.getPassword().length());
		} else {
			logger.error("Username: " + aForm.getUsername());
		}

		admin.setFullname(aForm.getFullname());
		admin.setAdminCountry(aForm.getAdminLocale().getCountry());
		admin.setAdminLang(aForm.getAdminLocale().getLanguage());
		admin.setAdminTimezone(aForm.getAdminTimezone());
		admin.setGroup(group);
		admin.setPreferredListSize(aForm.getNumberofRows());

		adminDao.save(admin);

		if (isNew) {
			AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": create user " + admin.getUsername());
		} else {
			AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": edit user " + aForm.getUsername());
		}
		if (logger.isInfoEnabled()) logger.info("saveAdmin: admin " + aForm.getAdminID());
	}

	protected boolean passwordChanged(String username, String password) {
		Admin admin = adminDao.getAdminByLogin(username, password);
		if (StringUtils.isEmpty(password) || (admin != null && admin.getAdminID() > 0)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Save the permission for an admin. Gets the permissions for the admin from
	 * the form and stores it in the database.
	 * 
	 * @param aForm
	 *            the formular passed from the jsp
	 * @param req
	 *            the Servlet Request (needed to get the company id)
	 */
	protected void saveAdminRights(AdminForm aForm, HttpServletRequest req) {
		adminDao.saveAdminRights(aForm.getAdminID(), aForm.getUserRights());
		if (logger.isInfoEnabled()) logger.info("saveAdminRights: permissions changed");
	}

	/**
	 * Deletes an admin from the database. Also deletes all the admin permissions.
	 * 
	 * @param aForm
	 *            the formular passed from the jsp
	 * @param req
	 *            the Servlet Request (needed to get the company id)
	 */
	protected void deleteAdmin(AdminForm aForm, HttpServletRequest req) {
		int adminID = aForm.getAdminID();
		int companyID = AgnUtils.getAdmin(req).getCompany().getId();
		Admin admin = adminDao.getAdmin(adminID, companyID);
		String username = admin != null ? admin.getUsername() : aForm.getUsername();
		adminDao.delete(admin);
		if (logger.isInfoEnabled()) logger.info("Admin " + adminID + " deleted");
		AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": delete user " + username);
	}

    /**
     * Creates Future object contains list of admins.
     * @param mapping the ActionMapping used to select this instance
     * @param req  HTTP request
     * @param errors ActionMessages object contains error messages, could be changed inside the method
     * @param destination specified in struts-config.xml to forward to next jsp
     * @param adminForm  AdminForm object
     * @return action forward for displaying page with admin list or loading admin page, if the list is not prepared yet
     */
	protected ActionForward prepareList(ActionMapping mapping, HttpServletRequest req, ActionMessages errors, ActionForward destination, AdminForm adminForm) {
		ActionMessages messages = null;

		try {
			setNumberOfRows(req, adminForm);
			destination = mapping.findForward("loading");
			String key = FUTURE_TASK + "@" + req.getSession(false).getId();
			if (!futureHolder.containsKey(key)) {
				Future<PaginatedListImpl<AdminEntry>> adminFuture = getAdminlistFuture(adminDao, req, adminForm);
				futureHolder.put(key, adminFuture);
			}

			if (futureHolder.containsKey(key) && futureHolder.get(key).isDone()) {
				req.setAttribute("adminEntries", futureHolder.get(key).get());
				destination = mapping.findForward("list");
				futureHolder.remove(key);
				adminForm.setRefreshMillis(RecipientForm.DEFAULT_REFRESH_MILLIS);
				messages = adminForm.getMessages();

				if (messages != null && !messages.isEmpty()) {
					saveMessages(req, messages);
					adminForm.setMessages(null);
				}
			} else {
				// raise the refresh time
				if (adminForm.getRefreshMillis() < 1000) { 
					adminForm.setRefreshMillis(adminForm.getRefreshMillis() + 50);
				}
				adminForm.setError(false);
			}
		} catch (Exception e) {
			logger.error("admin: " + e + "\n" + AgnUtils.getStackTrace(e));
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
			// do not refresh when an error has been occurred
			adminForm.setError(true);
		}
		return destination;
	}

    /**
     * Gets list of admins from database according to given sorting parameters.
     * @param adminDao AdminDao object
     * @param request  HTTP request
     * @param aForm  AdminForm object
     * @return Future object contains admin list
     * @throws NumberFormatException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InterruptedException
     * @throws ExecutionException
     */
	protected Future<PaginatedListImpl<AdminEntry>> getAdminlistFuture(AdminDao adminDao, HttpServletRequest request, StrutsFormBase aForm)
			throws NumberFormatException, IllegalAccessException, InstantiationException, InterruptedException, ExecutionException {
		int rownums = aForm.getNumberofRows();

		String direction = request.getParameter("dir");
		if (direction == null) {
			direction = request.getSession().getAttribute("admin_dir") == null ? "" : (String) request.getSession().getAttribute("admin_dir");
		} else {
			request.getSession().setAttribute("admin_dir", direction);
		}

		String sort = request.getParameter("sort");
		if (sort == null) {
			sort = request.getSession().getAttribute("admin_sort") == null ? "" : (String) request.getSession().getAttribute("admin_sort");
		} else {
			request.getSession().setAttribute("admin_sort", sort);
		}

		String pageStr = request.getParameter("page");
		if (pageStr == null || "".equals(pageStr.trim())) {
			pageStr = request.getSession().getAttribute("admin_page") == null ? "1" : (String) request.getSession().getAttribute("admin_page");
		} else {
			request.getSession().setAttribute("admin_page", pageStr);
		}

		if (aForm.isNumberOfRowsChanged()) {
			aForm.setPage("1");
			request.getSession().setAttribute("admin_page", "1");
			aForm.setNumberOfRowsChanged(false);
			pageStr = "1";
		}

		int companyID = AgnUtils.getCompanyID(request);

		Future<PaginatedListImpl<AdminEntry>> future = executorService.submit(new AdminListQueryWorker(adminDao, companyID, sort, direction, Integer.parseInt(pageStr), rownums));

		return future;
	}

	/**
	 * Method checks if admin with entered username already exists in system.
	 * 
	 * @param aForm
	 *            form
	 * @return true if admin already exists, false otherwise
	 */
	protected boolean adminExists(AdminForm aForm) {
		return adminDao.adminExists(aForm.getCompanyID(), aForm.getUsername());
	}

	/**
	 * Method checks if username was changed to existing one.
	 * 
	 * @param aForm
	 *            the form
	 * @return true if username was changed to existing one; false - if the
	 *         username was changed to none-existing or if the username was not
	 *         changed at all
	 */
	protected boolean adminUsernameChangedToExisting(AdminForm aForm) {
		Admin currentAdmin = adminDao.getAdmin(aForm.getAdminID(), aForm.getCompanyID());
		if (currentAdmin.getUsername().equals(aForm.getUsername())) {
			return false;
		} else {
			return adminDao.adminExists(aForm.getCompanyID(), aForm.getUsername());
		}
	}

	public void setAdminDao(AdminDao adminDao) {
		this.adminDao = adminDao;
	}

	public void setAdminGroupDao(AdminGroupDao adminGroupDao) {
		this.adminGroupDao = adminGroupDao;
	}

	public void setCompanyDao(CompanyDao companyDao) {
		this.companyDao = companyDao;
	}

	public void setFutureHolder(ConcurrentHashMap<String, Future<PaginatedListImpl<AdminEntry>>> futureHolder) {
		this.futureHolder = futureHolder;
	}

	public void setExecutorService(ScheduledThreadPoolExecutor executorService) {
		this.executorService = executorService;
	}
}
