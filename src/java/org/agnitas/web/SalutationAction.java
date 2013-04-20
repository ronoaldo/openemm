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
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.beans.Title;
import org.agnitas.dao.TitleDao;
import org.agnitas.service.SalutationListQueryWorker;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.forms.StrutsFormBase;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.springframework.context.ApplicationContext;

/**
 * Implementation of <strong>Action</strong> that handles Mailinglists
 * 
 * @author Martin Helff
 */

public final class SalutationAction extends StrutsActionBase {

    public static final String FUTURE_TASK = "GET_SALUTATIONIST_LIST";
	// --------------------------------------------------------- Public Methods

	/**
	 * Process the specified HTTP request, and create the corresponding HTTP
	 * response (or forward to another web component that will create it).
	 * Return an <code>ActionForward</code> instance describing where and how
	 * control should be forwarded, or <code>null</code> if the response has
	 * already been completed.
        * ACTION_LIST: calls a FutureHolder to get the list of entries.<br>
        * 		While FutureHolder is running destination is "loading".<br>
        * 		After FutureHolder is finished destination is "list".
        * <br><br>
        * ACTION_VIEW: loads data of chosen form of salutation into form,<br>
        *        forwards to form of salutation view page.
        * <br><br>
        * ACTION_SAVE:  save form of salutation in database.<br>
        *        calls a FutureHolder to get the list of entries.<br>
        * 	   While FutureHolder is running destination is "loading".<br>
        * 	   After FutureHolder is finished destination is "list".
        * <br><br>
        * ACTION_NEW: save new form of salutation in database, <br>
        *        calls a FutureHolder to get the list of entries.<br>
        * 	   While FutureHolder is running destination is "loading".<br>
        * 	   After FutureHolder is finished destination is "list".
        * <br><br>
        * ACTION_CONFIRM_DELETE: loads data of form of salutation into form, <br>
        *        forwards to jsp with question to confirm deletion.
        * <br><br>
        * ACTION_DELETE: delete the entry of form of salutation, <br>
        *        calls a FutureHolder to get the list of entries.<br>
        * 	   While FutureHolder is running destination is "loading".<br>
        * 	   After FutureHolder is finished destination is "list".
        * <br><br>
        * Any other ACTION_* calls a FutureHolder to get the list of entries.<br>
        * 	   While FutureHolder is running destination is "loading".<br>
        * 	   After FutureHolder is finished destination is "list".
        * <br><br>
	 * @param form
	 * @param req
	 * @param res
	 * @param mapping
	 *            The ActionMapping used to select this instance
	 * @exception IOException
	 *                if an input/output error occurs
	 * @exception ServletException
	 *                if a servlet exception occurs
	 * @return destination               vic
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {

		// Validate the request parameters specified by the user
		ApplicationContext aContext = this.getWebApplicationContext();
		SalutationForm aForm = null;
		ActionMessages errors = new ActionMessages();
		ActionMessages messages = new ActionMessages();
		ActionForward destination = null;

		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}

		if (form != null) {
			aForm = (SalutationForm) form;
		} else {
			aForm = new SalutationForm();
		}

		AgnUtils.logger().info("Action: " + aForm.getAction());

		if (req.getParameter("delete.x") != null) {
			aForm.setAction(ACTION_CONFIRM_DELETE);
		}

		try {
			switch (aForm.getAction()) {
			case SalutationAction.ACTION_LIST:
				if (allowed("settings.show", req)) {
                    if ( aForm.getColumnwidthsList() == null) {
                    		aForm.setColumnwidthsList(getInitializedColumnWidthList(4));
                    	}
					destination = prepareList(mapping,req,errors,destination,aForm);
				} else {
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                }
				break;

			case SalutationAction.ACTION_VIEW:
				if (aForm.getSalutationID() != 0) {
					aForm.setAction(SalutationAction.ACTION_SAVE);
					loadSalutation(aForm, aContext, req);
				} else {
					aForm.setAction(SalutationAction.ACTION_NEW);
				}
				destination = mapping.findForward("view");
				break;
			case SalutationAction.ACTION_SAVE:
				if ( AgnUtils.parameterNotEmpty(req, "save")) {
					saveSalutation(aForm, aContext, req);
					destination = prepareList(mapping,req,errors,destination,aForm);

					// Show "changes saved"
					messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
				}
				break;

			case SalutationAction.ACTION_NEW:
				if (allowed("settings.show", req)) {
					if ( AgnUtils.parameterNotEmpty(req, "save")) {
						aForm.setSalutationID(0);
						saveSalutation(aForm, aContext, req);
						aForm.setAction(SalutationAction.ACTION_SAVE);
						destination = prepareList(mapping,req,errors,destination,aForm);

						// Show "changes saved"
						messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
					}
				} else {
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                }
				break;

			case SalutationAction.ACTION_CONFIRM_DELETE:
				loadSalutation(aForm, aContext, req);
				aForm.setAction(SalutationAction.ACTION_DELETE);
				destination = mapping.findForward("delete");
				break;

			case SalutationAction.ACTION_DELETE:
				if (req.getParameter("kill") != null) {
					this.deleteSalutation(aForm, aContext, req);
					aForm.setAction(SalutationAction.ACTION_LIST);
					destination = prepareList(mapping,req,errors,destination,aForm);

					// Show "changes saved"
					messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
				}
				break;

			default:
				aForm.setAction(SalutationAction.ACTION_LIST);
				if (allowed("settings.show", req)) {
                    if ( aForm.getColumnwidthsList() == null) {
                    		aForm.setColumnwidthsList(getInitializedColumnWidthList(4));
                    	}
					destination = prepareList(mapping,req,errors,destination,aForm);
				} else {
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                }
			}

		} catch (Exception e) {
			AgnUtils.logger().error(
					"execute: " + e + "\n" + AgnUtils.getStackTrace(e));
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"error.exception"));
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
	 * Loads salutation.
	 */
	protected void loadSalutation(SalutationForm aForm,
			ApplicationContext aContext, HttpServletRequest req) {
		int compID = getCompanyID(req);
		int titID = aForm.getSalutationID();
		TitleDao titleDao = (TitleDao) getBean("TitleDao");
		Title title = titleDao.getTitle(titID, compID);

		Map map = title.getTitleGender();

		aForm.setSalMale((String) map.get(new Integer(Title.GENDER_MALE)));
		aForm.setSalFemale((String) map.get(new Integer(Title.GENDER_FEMALE)));
		aForm.setSalUnknown((String) map.get(new Integer(Title.GENDER_UNKNOWN)));
		aForm.setSalMiss((String) map.get(new Integer(Title.GENDER_MISS)));
		aForm.setSalPractice((String) map.get(new Integer(Title.GENDER_PRACTICE)));
		aForm.setSalCompany((String) map.get(new Integer(Title.GENDER_COMPANY)));
		aForm.setShortname(title.getDescription());
	}

	/**
	 * Saves salutation.
	 */
	protected void saveSalutation(SalutationForm aForm,
			ApplicationContext aContext, HttpServletRequest req) {
		int compID = getCompanyID(req);
		int titID = aForm.getSalutationID();
		TitleDao titleDao = (TitleDao) getBean("TitleDao");
		Title title = titleDao.getTitle(titID, compID);
		Map map = new HashMap();

		if (title == null) {
			title = (Title) getBean("Title");
			title.setId(titID);
			title.setCompanyID(compID);
		}
		title.setDescription(aForm.getShortname());
		map.put(new Integer(Title.GENDER_MALE), aForm.getSalMale());
		map.put(new Integer(Title.GENDER_FEMALE), aForm.getSalFemale());
		if (aForm.getSalUnknown() != null && aForm.getSalUnknown().length() > 0) {
			map.put(new Integer(Title.GENDER_UNKNOWN), aForm.getSalUnknown());
		}
		if (aForm.getSalMiss() != null && aForm.getSalMiss().length() > 0) {
			map.put(new Integer(Title.GENDER_MISS), aForm.getSalMiss());
		}
		if (aForm.getSalPractice() != null && aForm.getSalPractice().length() > 0) {
			map.put(new Integer(Title.GENDER_PRACTICE), aForm.getSalPractice());
		}
		if (aForm.getSalCompany() != null && aForm.getSalCompany().length() > 0) {
			map.put(new Integer(Title.GENDER_COMPANY), aForm.getSalCompany());
		}
		title.setTitleGender(map);
		getHibernateTemplate().saveOrUpdate("Title", title);
		getHibernateTemplate().flush();
		if (aForm.getSalutationID() == 0) {
			aForm.setSalutationID(title.getId());
		}
	}

	/**
	 * Removes salutation.
	 */
	protected void deleteSalutation(SalutationForm aForm,
			ApplicationContext aContext, HttpServletRequest req) {
		int compID = getCompanyID(req);
		int titID = aForm.getSalutationID();
		TitleDao titleDao = (TitleDao) getBean("TitleDao");

		titleDao.delete(titID, compID);
	}

    private ActionForward prepareList(ActionMapping mapping,
                                      HttpServletRequest req, ActionMessages errors,
                                      ActionForward destination, SalutationForm salutationForm) {
        TitleDao dao = (TitleDao) getBean("TitleDao");
        ActionMessages messages = null;

        try {
            setNumberOfRows(req, salutationForm);
            destination = mapping.findForward("loading");
            AbstractMap<String, Future> futureHolder = (AbstractMap<String, Future>) getBean("futureHolder");
            String key = FUTURE_TASK + "@" + req.getSession(false).getId();
            if (!futureHolder.containsKey(key)) {
                Future salutationFuture = getSalutationlistFuture(dao, req, getWebApplicationContext(), salutationForm);
                futureHolder.put(key, salutationFuture);
            }
            if (futureHolder.containsKey(key) && futureHolder.get(key).isDone()) {
                req.setAttribute("salutationEntries", futureHolder.get(key).get());
                destination = mapping.findForward("list");
                futureHolder.remove(key);
                salutationForm.setRefreshMillis(RecipientForm.DEFAULT_REFRESH_MILLIS);
                messages = salutationForm.getMessages();

                if (messages != null && !messages.isEmpty()) {
                    saveMessages(req, messages);
                    salutationForm.setMessages(null);
                }
            } else {
                if (salutationForm.getRefreshMillis() < 1000) { // raise the refresh time
                    salutationForm.setRefreshMillis(salutationForm.getRefreshMillis() + 50);
                }
                salutationForm.setError(false);
            }
        }
        catch (Exception e) {
            AgnUtils.logger().error("salutation: " + e + "\n" + AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
            salutationForm.setError(true); // do not refresh when an error has been occurred
        }
        return destination;
    }

    protected Future getSalutationlistFuture(TitleDao salutationDao, HttpServletRequest request, ApplicationContext aContext, StrutsFormBase aForm) throws NumberFormatException, IllegalAccessException, InstantiationException, InterruptedException, ExecutionException {

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

        ExecutorService service = (ExecutorService) aContext.getBean("workerExecutorService");
        Future future = service.submit(new SalutationListQueryWorker(salutationDao, companyID, sort, direction, Integer.parseInt(pageStr), rownums));

        return future;

    }

}
