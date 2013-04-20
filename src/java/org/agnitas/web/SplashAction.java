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
 * the code written by AGNITAS AG are Copyright (c) 2010 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/

package org.agnitas.web;

import org.agnitas.util.AgnUtils;
import org.agnitas.web.forms.SplashForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author Vyacheslav Stepanov
 */
public class SplashAction extends StrutsActionBase {

	public static final int ACTION_CMS_SPLASH = ACTION_LAST + 1;
	public static final int ACTION_STATS_SPLASH = ACTION_LAST + 2;
    public static final int ACTION_SPLASH_RECIPIENTS = ACTION_LAST + 3;
    public static final int ACTION_SPLASH_SETTINGS = ACTION_LAST + 4;
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
								 HttpServletResponse servletResponse) throws Exception {

		ActionForward destination = null;
		ActionMessages errors = new ActionMessages();

		if(!AgnUtils.isUserLoggedIn(request)) {
			return mapping.findForward("logon");
		}

		SplashForm aForm = (SplashForm) actionForm;

		try {
			switch(aForm.getAction()) {
				case ACTION_CMS_SPLASH:
					destination = mapping.findForward("splash_cms");
					break;
				case ACTION_STATS_SPLASH:
					destination = mapping.findForward("splash_stats");
					break;
                case ACTION_SPLASH_RECIPIENTS:
                    destination = mapping.findForward("splash_recipients");
                    break;
                case ACTION_SPLASH_SETTINGS:
                    destination = mapping.findForward("splash_settings");
                    break;
			}
		}
		catch(Exception e) {
			AgnUtils.logger().error("execute: " + e + "\n" + AgnUtils.getStackTrace(e));
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
		}

		if(!errors.isEmpty()) {
			saveErrors(request, errors);
		}

		return destination;

	}
}
