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

import org.agnitas.beans.Mailing;
import org.agnitas.beans.TrackableLink;
import org.agnitas.dao.EmmActionDao;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.TrackableLinkDao;
import org.agnitas.util.AgnUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of <strong>Action</strong> that validates a user logon.
 * 
 * @author Martin Helff
 */

public class TrackableLinkAction extends StrutsActionBase {

	public static final int ACTION_SET_STANDARD_ACTION = ACTION_LAST + 1;

	public static final int ACTION_SET_STANDARD_DEEPTRACKING = ACTION_LAST + 2;

	public static final int ACTION_GLOBAL_USAGE = ACTION_LAST + 3;
	
	public static final int ACTION_ORG_LAST = ACTION_GLOBAL_USAGE;

	protected MailingDao mailingDao;
	protected EmmActionDao actionDao;
	protected TrackableLinkDao linkDao;


	// --------------------------------------------------------- Public Methods

	/**
	 * Process the specified HTTP request, and create the corresponding HTTP
	 * response (or forward to another web component that will create it).
	 * Return an <code>ActionForward</code> instance describing where and how
	 * control should be forwarded, or <code>null</code> if the response has
	 * already been completed.
	 *
     * ACTION_LIST: loads list of trackable links into form; loads name, description, click-action and open-action of
     *     current mailing into form. Forwards to trackable links list page
     * <br><br>
     * ACTION_VIEW: loads data of chosen trackable link into form, forwards to trackable link view page
     * <br><br>
     * ACTION_SAVE: saves trackable link into database, loads list of trackable links into form, resets click action id,
     *     forwards to trackable links list page.
     * <br><br>
     * ACTION_SET_STANDARD_ACTION: checks defaultActionType property of form. If it is "link" - owerwrites actions of
     *     current mailing links with the one chosen by user. If it is "click" - changes the default click-action of
     *     mailing with the one chosen by user. If it is "open" - changes the default open-action of mailing with the
     *     one selected by user.<br>
     *     Saves current mailing in DB, loads list of trackable links into form, resets click action id, forwards to
     *     trackable links list page.
     * <br><br>
     * ACTION_SET_STANDARD_DEEPTRACKING: saves deeptracking property for current mailing trackable links in database,
     *     loads list of trackable links into form forwards to trackable links list page.
     * <br><br>
     * ACTION_GLOBAL_USAGE: updates "usage" property for all links of mailing with a values selected by user, saves
     *     mailing in DB, loads list of trackable links into form, forwards to trackable links list page.
     * <br><br>
     * Any other ACTION_* would cause a forward to "list"
     * <br><br>
     * If destination is "list" - loads all actions to request, loads all none-form actions to request
     *
	 * @param form ActionForm object
     * @param req request
     * @param res response
	 * @param mapping
	 *            The ActionMapping used to select this instance
	 * @exception IOException
	 *                if an input/output error occurs
	 * @exception ServletException
	 *                if a servlet exception occurs
	 * @return destination
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {

		// Validate the request parameters specified by the user
		TrackableLinkForm aForm = null;
		ActionMessages errors = new ActionMessages();
    	ActionMessages messages = new ActionMessages();
		ActionForward destination = null;

		if (!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}

		aForm = (TrackableLinkForm) form;

		AgnUtils.logger().info("Action: " + aForm.getAction());

		if (!allowed("mailing.content.show", req)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"error.permissionDenied"));
			saveErrors(req, errors);
			return null;
		}

		try {
			switch (aForm.getAction()) {
			case ACTION_LIST:
				this.loadLinks(aForm, req);
				destination = mapping.findForward("list");
				break;

			case ACTION_VIEW:
				aForm.setAction(ACTION_SAVE);
				loadLink(aForm, req);
                destination = mapping.findForward("view");
				break;

			case ACTION_SAVE:
				destination = mapping.findForward("list");
				saveLink(aForm, req);
				this.loadLinks(aForm, req);
                aForm.setLinkAction(0);
				break;

			case ACTION_SET_STANDARD_ACTION:
				destination = mapping.findForward("list");
				if(aForm.getDefaultActionType() != null && !aForm.getDefaultActionType().equals("")) {
					setStandardActions(aForm, req);
				}
				this.loadLinks(aForm, req);
                aForm.setLinkAction(0);
                // Show "changes saved"
            	messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
				break;

			case ACTION_SET_STANDARD_DEEPTRACKING:
				destination = mapping.findForward("list");
				setStandardDeeptracking(aForm, req);
				this.loadLinks(aForm, req);
                messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                
				break;
				
			case ACTION_GLOBAL_USAGE:
				saveGlobalUsage(aForm, req);
				this.loadLinks(aForm, req);

				destination = mapping.findForward("list");
				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
				
				break;

			default:
				aForm.setAction(ACTION_LIST);
				this.loadLinks(aForm, req);
				destination = mapping.findForward("list");
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
			AgnUtils.logger().error("saving errors: " + destination);
		}

		if (destination != null && ("list".equals(destination.getName()) || "view".equals(destination.getName()))) {
			loadActions(aForm, req);
            loadNotFormActions(req);
		}
		
		// Report any message (non-errors) we have discovered
		if (!messages.isEmpty()) {
			saveMessages(req, messages);
		}
		
        // Report any message (non-errors) we have discovered
        if (!messages.isEmpty()) {
        	saveMessages(req, messages);
        }

		return destination;
	}

	/**
	 * Loads links.
	 */
	protected void loadLinks(TrackableLinkForm aForm, HttpServletRequest req) throws Exception {
		Mailing aMailing = mailingDao.getMailing(aForm.getMailingID(),
				getCompanyID(req));

		aForm.setLinks(aMailing.getTrackableLinks().values());
		aForm.setShortname(aMailing.getShortname());
        aForm.setDescription(aMailing.getDescription());
		aForm.setIsTemplate(aMailing.isIsTemplate());
        aForm.setOpenActionID(aMailing.getOpenActionID());
        aForm.setClickActionID(aMailing.getClickActionID());

		AgnUtils.logger().info("loadMailing: mailing loaded");
	}

    protected void loadNotFormActions(HttpServletRequest req) {
        List emmNotFormActions = actionDao.getEmmNotFormActions(AgnUtils.getCompanyID(req));
        req.setAttribute("notFormActions", emmNotFormActions);
    }

	protected void loadActions(TrackableLinkForm aForm, HttpServletRequest request) {
		List actions = actionDao.getEmmActions(getCompanyID(request));
		request.setAttribute("actions", actions);
	}

	/**
	 * Loads link.
	 */
	protected void loadLink(TrackableLinkForm aForm, HttpServletRequest req) {
		TrackableLink aLink;
		aLink = linkDao.getTrackableLink(aForm.getLinkID(), getCompanyID(req));
		if (aLink != null) {
			aForm.setLinkName(aLink.getShortname());
			aForm.setTrackable(aLink.getUsage());
			aForm.setLinkUrl(aLink.getFullUrl());
			aForm.setLinkAction(aLink.getActionID());
			aForm.setRelevance(aLink.getRelevance());
			aForm.setDeepTracking(aLink.getDeepTracking());
			aForm.setRelevance(aLink.getRelevance());
			if (req.getParameter("deepTracking") != null) { // only if parameter is provided in form
				aForm.setDeepTracking(aLink.getDeepTracking());
			}
		} else {
			AgnUtils.logger().error("could not load link: " + aForm.getLinkID());
		}
	}

	/**
	 * Saves link.
	 */
	protected void saveLink(TrackableLinkForm aForm, HttpServletRequest req) {
		TrackableLink aLink;
		aLink = linkDao.getTrackableLink(aForm.getLinkID(), getCompanyID(req));
		if (aLink != null) {
			aLink.setShortname(aForm.getLinkName());
			aLink.setUsage(aForm.getTrackable());
			aLink.setActionID(aForm.getLinkAction());
			aLink.setRelevance(aForm.getRelevance());
			if (req.getParameter("deepTracking") != null) { // only if parameter is provided in form
				aLink.setDeepTracking(aForm.getDeepTracking());
			}
			linkDao.saveTrackableLink(aLink);
		}
	}

	/**
	 * Gets the link action. Saves mailing.
	 */
	protected void setStandardActions(TrackableLinkForm aForm,
			HttpServletRequest req) {
		TrackableLink aLink;
		Mailing aMailing = mailingDao.getMailing(aForm.getMailingID(),
				getCompanyID(req));
        String type = aForm.getDefaultActionType();
		try {
            // set link actions
			Iterator<TrackableLink> it = aMailing.getTrackableLinks().values().iterator();
            if("link".equals(type)) {
                while (it.hasNext()) {
                    aLink = it.next();
                     aLink.setActionID(aForm.getLinkAction());
                }
            }
            // set mailing open and click actions
            if("open".equals(type)) aMailing.setOpenActionID(aForm.getOpenActionID());
            if("click".equals(type)) aMailing.setClickActionID(aForm.getClickActionID());
		} catch (Exception e) {
			AgnUtils.logger().error(e.getMessage());
			AgnUtils.logger().error(AgnUtils.getStackTrace(e));
		}
		mailingDao.saveMailing(aMailing);
	}

	protected void setStandardDeeptracking(TrackableLinkForm aForm,
			HttpServletRequest req) {
		// set Default Deeptracking;
		linkDao.setDeeptracking(aForm.getDeepTracking(), this.getCompanyID(req),
				aForm.getMailingID());
	}

	protected void saveGlobalUsage(TrackableLinkForm aForm, HttpServletRequest req) {
		TrackableLink aLink;
		Mailing aMailing = mailingDao.getMailing(aForm.getMailingID(), getCompanyID(req));
		try {
			Iterator<TrackableLink> it = aMailing.getTrackableLinks().values().iterator();
			while (it.hasNext()) {
				aLink = it.next();
				aLink.setUsage(aForm.getGlobalUsage());
			}
		} catch (Exception e) {
			AgnUtils.logger().error(e.getMessage());
			AgnUtils.logger().error(AgnUtils.getStackTrace(e));
		}
		mailingDao.saveMailing(aMailing);
	}

	public void setMailingDao(MailingDao mailingDao) {
		this.mailingDao = mailingDao;
	}

	public MailingDao getMailingDao() {
		return mailingDao;
	}

	public void setActionDao(EmmActionDao actionDao) {
		this.actionDao = actionDao;
	}

	public EmmActionDao getActionDao() {
		return actionDao;
	}

	public void setLinkDao(TrackableLinkDao linkDao) {
		this.linkDao = linkDao;
	}

	public TrackableLinkDao getLinkDao() {
		return linkDao;
	}
}
