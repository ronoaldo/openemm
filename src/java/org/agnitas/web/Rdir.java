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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.actions.EmmAction;
import org.agnitas.beans.Company;
import org.agnitas.beans.TrackableLink;
import org.agnitas.dao.EmmActionDao;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.TrackableLinkDao;
import org.agnitas.emm.core.commons.uid.ExtensibleUID;
import org.agnitas.emm.core.commons.uid.ExtensibleUIDConstants;
import org.agnitas.emm.core.commons.uid.ExtensibleUIDService;
import org.agnitas.emm.core.commons.uid.parser.exception.UIDParseException;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.TimeoutLRUMap;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Before sending the mailing all trackable links URLs are replaced with URL of this servlet. When recipient clicks the link in mailing - he gets to this servlet and the system tracks that recipient
 * made a click. After that recipient is forwarded to actual link.
 */
public class Rdir extends HttpServlet {
	private static final long serialVersionUID = -133097955106781586L;

	private static final transient Logger logger = Logger.getLogger(Rdir.class);

	protected TimeoutLRUMap<Integer, TrackableLink> urlCache = new TimeoutLRUMap<Integer, TrackableLink>(AgnUtils.getDefaultIntValue("rdir.keys.maxCache"), AgnUtils.getDefaultIntValue("rdir.keys.maxCacheTimeMillis"));
	private ExtensibleUIDService extensibleUIDService = null;
	private TrackableLinkDao trackableLinkDao = null;
	private MailingDao mailingDao = null;
	private EmmActionDao emmActionDao = null;

	/**
	 * Servlet service-method, is invoked when user calls the servlet. Logs the click to DB and redirects recipient to actual link. Also invokes link action (if there is one) and mailing default click
	 * action.
	 * 
	 * @param req
	 *            HTTP request. Should contain parameter "uid" - the encoded data of link/user (contains companyID, urlID, customerID, mailingID)
	 * @param res
	 *            HTTP response. If everything goes ok - the response sends redirect to actual link
	 * @throws IOException
	 *             if an input/output error occurs
	 * @throws ServletException
	 *             if a servlet exception occurs
	 */
	@Override
	public void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		ApplicationContext con = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());

		try {
			// validate uid
			ExtensibleUID uid = getExtensibleUIDService().newUID();

			String param = req.getParameter("uid");
			if (param != null) {
				try {
					uid = getExtensibleUIDService().parse(param);
				} catch (UIDParseException e) {
					logger.warn("Error parsing UID: " + param + " (" + e.getMessage() + ")");
					logger.debug(e);
					return;
				}
			} else {
				logger.error("service: uid missing");
			}
			if (uid == null || uid.getCompanyID() == 0) {
				return;
			}

			Company aCompany = AgnUtils.getCompanyCache(uid.getCompanyID(), con);
			if (aCompany == null) {
				return;
			}

			/* TODO: check validateUID -> didn't recognize valid UIDs (maybe unittest) */
			/*
			 * // TODO: Implement Helper class to validate the very old UIDs if(uid.validateUID(aCompany.getSecret()) == false) { logger.warn("uid invalid: "+param); return; }
			 */

			TrackableLink aLink = (TrackableLink)urlCache.get(uid.getUrlID());
			if (aLink == null || aLink.getCompanyID() != uid.getCompanyID()) {
				// get link and do actions
				aLink = getTrackableLinkDao().getTrackableLink(uid.getUrlID(), uid.getCompanyID());
				if (aLink != null) {
					urlCache.put(uid.getUrlID(), aLink);
				}
			}

			// link is beeing personalized, replaces AGNUID
			String fullUrl = aLink.personalizeLink(uid.getCustomerID(), param, con);
			if (fullUrl == null) {
				logger.error("service: could not personalize link");
				return;
			}

			if (AgnUtils.getDefaultValue("redirection.status") == null || AgnUtils.getDefaultIntValue("redirection.status") == 302) {
				res.sendRedirect(fullUrl);
				// res.sendRedirect(aLink.getFullUrl());
			} else {
				res.setStatus(AgnUtils.getDefaultIntValue("redirection.status"));
				res.setHeader("Location", fullUrl);
				res.flushBuffer();
			}

			// log click in db
			if (!getTrackableLinkDao().logClickInDB(aLink, uid.getCustomerID(), req.getRemoteAddr())) {
				return;
			}

			int companyID = uid.getCompanyID();
			int mailingID = uid.getMailingID();
			int customerID = uid.getCustomerID();
			int clickActionID = getMailingDao().getMailingClickAction(mailingID, companyID);
			if (clickActionID != 0) {
				EmmAction emmAction = getEmmActionDao().getEmmAction(clickActionID, companyID);
				if (emmAction != null) {
					// execute configured actions
					CaseInsensitiveMap params = new CaseInsensitiveMap();
					params.put("requestParameters", AgnUtils.getReqParameters(req));
					params.put("_request", req);
					params.put("customerID", customerID);
					params.put("mailingID", mailingID);
					emmAction.executeActions(con, params);
				}
			}

			// execute configured actions
			CaseInsensitiveMap params = new CaseInsensitiveMap();
			params.put("requestParameters", AgnUtils.getReqParameters(req));
			params.put("_request", req);

			aLink.performLinkAction(params, uid.getCustomerID(), con);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void setExtensibleUIDService(ExtensibleUIDService extensibleUIDService) {
		this.extensibleUIDService = extensibleUIDService;
	}

	private ExtensibleUIDService getExtensibleUIDService() {
		if (extensibleUIDService == null) {
			ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
			extensibleUIDService = (ExtensibleUIDService) applicationContext.getBean(ExtensibleUIDConstants.SERVICE_BEAN_NAME);
		}
		return extensibleUIDService;
	}

	public void setTrackableLinkDao(TrackableLinkDao trackableLinkDao) {
		this.trackableLinkDao = trackableLinkDao;
	}

	private TrackableLinkDao getTrackableLinkDao() {
		if (trackableLinkDao == null) {
			ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
			trackableLinkDao = (TrackableLinkDao) applicationContext.getBean("TrackableLinkDao");
		}
		return trackableLinkDao;
	}

	public void setMailingDao(MailingDao mailingDao) {
		this.mailingDao = mailingDao;
	}

	private MailingDao getMailingDao() {
		if (mailingDao == null) {
			ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
			mailingDao = (MailingDao) applicationContext.getBean("MailingDao");
		}
		return mailingDao;
	}

	public void setTrackableLinkDao(EmmActionDao emmActionDao) {
		this.emmActionDao = emmActionDao;
	}

	private EmmActionDao getEmmActionDao() {
		if (emmActionDao == null) {
			ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
			emmActionDao = (EmmActionDao) applicationContext.getBean("EmmActionDao");
		}
		return emmActionDao;
	}
}