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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.beans.BindingEntry;
import org.agnitas.beans.BlackListEntry;
import org.agnitas.beans.Mailinglist;
import org.agnitas.beans.Recipient;
import org.agnitas.beans.factory.RecipientFactory;
import org.agnitas.beans.impl.PaginatedListImpl;
import org.agnitas.dao.BlacklistDao;
import org.agnitas.dao.RecipientDao;
import org.agnitas.emm.core.blacklist.service.BlacklistModel;
import org.agnitas.emm.core.blacklist.service.BlacklistService;
import org.agnitas.service.BlacklistQueryWorker;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.forms.BlacklistForm;
import org.agnitas.web.forms.StrutsFormBase;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.springframework.context.ApplicationContext;

/**
 * Implementation of <strong>Action</strong> that handles Blacklists
 * 
 * @author Alexander Schmoeller
 */
public class BlacklistAction extends StrutsActionBase {
	/** The logger. */
	private static final transient Logger logger = Logger.getLogger(BlacklistAction.class);

	/** BlacklistDao. */
	private BlacklistDao blacklistDao = null;
	protected AbstractMap<String, Future<PaginatedListImpl<BlackListEntry>>> futureHolder = null;
	
	/** The service layer. */
	private BlacklistService blacklistService;
	
	/** RecipientDao. */
	protected RecipientDao recipientDao = null;
	protected ExecutorService workerExecutorService = null;
	protected RecipientFactory recipientFactory = null;

	public static final String FUTURE_TASK = "GET_BLACKLIST_LIST";
	public static final int ACTION_DOWNLOAD = ACTION_LAST + 1;

	// --------------------------------------------------------- Public Methods

	/**
	 * Process the specified HTTP request, and create the corresponding HTTP
	 * response (or forward to another web component that will create it).
	 * Return an <code>ActionForward</code> instance describing where and how
	 * control should be forwarded, or <code>null</code> if the response has
	 * already been completed.<br>
	 * Error and success messages are set during the action to give the user 
	 * a feedback in the forwarded web component.<br>
	 * <br>
	 * ACTION_LIST: calls a FutureHolder to get the list of entries.<br> 
	 * 		While FutureHolder is running destination is "loading". <br>
	 * 		After FutureHolder is finished destination is "list".
	 * <br><br>
	 * ACTION_SAVE: checks, if email is not empty, valid and not already in the database.<br>
	 * 		When email is saved into the database the status of an existing recipients is updated
	 * <br><br>
	 * ACTION_CONFIRM_DELETE: only forwards to jsp with question to confirm deletion
	 * <br><br>
	 * ACITON_DELETE: deletes the entry of the blacklist and loads the new list to display
	 * <br><br>
	 * Any other ACTION_* would cause a forward to "list"
	 * <br>
	 * @param form data for the action filled by the jsp
	 * @param req request from jsp
	 * @param res response
	 * @param mapping
	 *            The ActionMapping used to select this instance
	 * @exception IOException
	 *                if an input/output error occurs
	 * @exception ServletException
	 *                if a servlet exception occurs
	 * @return destination specified in struts-config.xml to forward to next jsp
	*/
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		ActionMessages errors = new ActionMessages();
		ActionForward destination = null;

		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}

		Integer action;
		try {
			action = Integer.parseInt(req.getParameter("action"));
		} catch (Exception e) {
			action = BlacklistAction.ACTION_LIST;
		}

		if (logger.isInfoEnabled()) {
			logger.info("Action: " + action);
		}

		try {
			destination = executeIntern(mapping, form, req, errors, destination, action);
		} catch (Exception e) {
			logger.error("execute: " + e + "\n" + AgnUtils.getStackTrace(e));
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
			destination = mapping.findForward("error");
		}

		// Report any errors we have discovered back to the original form
		if (!errors.isEmpty()) {
			saveErrors(req, errors);
		}
		
		return destination;
	}

	// What the hell is that ??? Using huge execute methods with switch/ case in
	// the other actions and now an executeIntern where you have again a
	// switch/case ?
	// A really cool improvement ...
	protected ActionForward executeIntern(ActionMapping mapping, ActionForm form, HttpServletRequest req, ActionMessages errors, ActionForward destination, Integer action) {
		BlacklistForm blacklistForm = (BlacklistForm) form;
		ActionMessages messages = new ActionMessages();

		if (blacklistForm.getColumnwidthsList() == null) {
			blacklistForm.setColumnwidthsList(getInitializedColumnWidthList(2));
		}

		switch (action) {
			case BlacklistAction.ACTION_LIST:
				if (allowed("blacklist", req)) {
					errors.add(blacklistForm.getErrors());
					blacklistForm.setErrors(null);
	
					destination = prepareList(mapping, req, errors, destination, blacklistDao, blacklistForm);
				} else {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "error.permissionDenied"));
				}
				break;
			case BlacklistAction.ACTION_SAVE:
				String email = blacklistForm.getNewemail();
	
				if (StringUtils.isBlank(email)) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "error.email.empty"));
					blacklistForm.setErrors(errors);
					destination = prepareList(mapping, req, errors, destination, blacklistDao, blacklistForm);
				} else {
					email = AgnUtils.normalizeEmail(email);
					if (blacklistDao.exist(AgnUtils.getAdmin(req).getCompany().getId(), email)) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.blacklist.recipient.isalreadyblacklisted", blacklistForm.getNewemail()));
						blacklistForm.setErrors(errors);
						destination = prepareList(mapping, req, errors, destination, blacklistDao, blacklistForm);
					} else {
						try {
							if (blacklistDao.insert(AgnUtils.getAdmin(req).getCompany().getId(), email)) {
								updateUserStatus(email.trim(), req);
								if (logger.isInfoEnabled()) {
									logger.info(AgnUtils.getAdmin(req).getUsername() + ": blacklist add email: " + email);
								}
								messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
								blacklistForm.setMessages(messages);
							}
							destination = prepareList(mapping, req, errors, destination, blacklistDao, blacklistForm);
						} catch (Exception e) {
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.backlist.recipient.unable", blacklistForm.getNewemail()));
							blacklistForm.setErrors(errors);
							destination = prepareList(mapping, req, errors, destination, blacklistDao, blacklistForm);
						}
					}
				}
	
				blacklistForm.setNewemail(null);
				break;
			case ACTION_CONFIRM_DELETE:
				email = req.getParameter("delete");
				BlacklistModel bm = new BlacklistModel();
				bm.setCompanyId( AgnUtils.getCompanyID( req));
				bm.setEmail( email);
				List<Mailinglist> list = blacklistService.getMailinglistsWithBlacklistedBindings( bm);
				blacklistForm.setBlacklistedMailinglists( list);
				
				destination = mapping.findForward("delete");
				break;
			case BlacklistAction.ACTION_DELETE:
				try {
					// TODO: Move logic to service layer!
					email = URLDecoder.decode( req.getParameter("delete"), "UTF-8");
					blacklistDao.delete(AgnUtils.getAdmin(req).getCompany().getId(), email);
					
					bm = new BlacklistModel();
					bm.setEmail( email);
					bm.setCompanyId( AgnUtils.getCompanyID( req));
					
					blacklistService.updateBlacklistedBindings( bm, checkedMailinglistsAsList( blacklistForm), BindingEntry.USER_STATUS_ADMINOUT);
					messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
					blacklistForm.setMessages(messages);
					destination = prepareList(mapping, req, errors, destination, blacklistDao, blacklistForm);
					if (logger.isInfoEnabled()) {
						logger.info(AgnUtils.getAdmin(req).getUsername() + ": blacklist delete email: " + email);
					}
				} catch( UnsupportedEncodingException e) {
					logger.fatal( "Error deleting blacklist item", e);
				}
				break;
			default:
				destination = mapping.findForward("list");
		}
		
		return destination;
	}
	
	private List<Integer> checkedMailinglistsAsList( BlacklistForm form) {
		List<Integer> list = new Vector<Integer>();
		
		if( form.getBlacklistedMailinglists() != null) {
			for( int i = 0; i < form.getBlacklistedMailinglists().size(); i++) {
				if( form.getCheckedBlacklistedMailinglists( i) > 0)
					list.add( form.getCheckedBlacklistedMailinglists( i));
			}
		}
		
		return list;
	}

	private ActionForward prepareList(ActionMapping mapping, HttpServletRequest req, ActionMessages errors, ActionForward destination, BlacklistDao dao, BlacklistForm blacklistForm) {
		ActionMessages messages = null;

		try {
			setNumberOfRows(req, blacklistForm);
			destination = mapping.findForward("loading");
			String key = FUTURE_TASK + "@" + req.getSession(false).getId();
			if (!futureHolder.containsKey(key)) {
				Future<PaginatedListImpl<BlackListEntry>> blacklistFuture = getBlacklistFuture(dao, req, getWebApplicationContext(), blacklistForm);
				futureHolder.put(key, blacklistFuture);
			}
			if (futureHolder.containsKey(key) && futureHolder.get(key).isDone()) {
				req.setAttribute("blackListEntries", futureHolder.get(key).get());
				destination = mapping.findForward("list");
				futureHolder.remove(key);
				blacklistForm.setRefreshMillis(RecipientForm.DEFAULT_REFRESH_MILLIS);
				messages = blacklistForm.getMessages();

				if (messages != null && !messages.isEmpty()) {
					saveMessages(req, messages);
					blacklistForm.setMessages(null);
				}
			} else {
				if (blacklistForm.getRefreshMillis() < 1000) {
					// raise the refresh time
					blacklistForm.setRefreshMillis(blacklistForm.getRefreshMillis() + 50);
				}
				blacklistForm.setError(false);
			}
		} catch (Exception e) {
			logger.error("blacklist: " + e + "\n" + AgnUtils.getStackTrace(e));
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
			// do not refresh when an error has been occurred
			blacklistForm.setError(true);
		}
		return destination;
	}

	/**
	 * Loads a customer by email. Set all bindings to status
	 * 
	 * @param newEmail
	 * @param req
	 */
	protected void updateUserStatus(String newEmail, HttpServletRequest req) {
		Recipient cust = recipientFactory.newRecipient();

		cust.setCompanyID(AgnUtils.getAdmin(req).getCompany().getId());
		int customerID = recipientDao.findByKeyColumn(cust, "email", newEmail);
		cust.setCustomerID(customerID);

		cust.setCustParameters(recipientDao.getCustomerDataFromDb(cust.getCompanyID(), cust.getCustomerID()));

		Map<Integer, Map<Integer, BindingEntry>> mailinglistBindings = recipientDao.loadAllListBindings(cust.getCompanyID(), cust.getCustomerID());

		for (Map<Integer, BindingEntry> mailinglistBinding : mailinglistBindings.values()) {
			for (BindingEntry mailinglistBindingEntry : mailinglistBinding.values()) {
				mailinglistBindingEntry.setUserStatus(BindingEntry.USER_STATUS_BLACKLIST);
				mailinglistBindingEntry.setUserRemark("Blacklisted by " + AgnUtils.getAdmin(req).getAdminID());
				mailinglistBindingEntry.updateStatusInDB(cust.getCompanyID());
			}
		}
	}

	protected Future<PaginatedListImpl<BlackListEntry>> getBlacklistFuture(BlacklistDao blacklistDao, HttpServletRequest request, ApplicationContext aContext, StrutsFormBase aForm)
			throws NumberFormatException, IllegalAccessException, InstantiationException, InterruptedException, ExecutionException {
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

		Future<PaginatedListImpl<BlackListEntry>> future = workerExecutorService.submit(new BlacklistQueryWorker(blacklistDao, companyID, sort, direction, Integer.parseInt(pageStr), rownums));

		return future;
	}

	public void setBlacklistDao(BlacklistDao blacklistDao) {
		this.blacklistDao = blacklistDao;
	}
	
	public void setBlacklistService( BlacklistService blacklistService) {
		this.blacklistService = blacklistService;
	}

	public void setFutureHolder(AbstractMap<String, Future<PaginatedListImpl<BlackListEntry>>> futureHolder) {
		this.futureHolder = futureHolder;
	}

	public void setRecipientDao(RecipientDao recipientDao) {
		this.recipientDao = recipientDao;
	}

	public void setWorkerExecutorService(ExecutorService workerExecutorService) {
		this.workerExecutorService = workerExecutorService;
	}

	public RecipientFactory getRecipientFactory() {
		return recipientFactory;
	}

	public void setRecipientFactory(RecipientFactory recipientFactory) {
		this.recipientFactory = recipientFactory;
	}
	
	/**
	 * Returns the BlacklistService injected into this Action.
	 * 
	 * @return BlacklistService
	 */
	protected BlacklistService getBlacklistService() {
		return this.blacklistService;
	}
}
