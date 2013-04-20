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
 * the code written by AGNITAS AG are Copyright (c) 2009 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

package org.agnitas.cms.web;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

/**
 * Servlet that provides pictures of CM templates and content modules.
 * It resend request to central content repository servlet
 * Parameters:
 * fid - id of media-file
 *
 * @author Vyacheslav Stepanov
 */
public class CmsShowImage extends HttpServlet {
	private static final String CRR_URL = getCrrUrl();

	public void service(HttpServletRequest req, HttpServletResponse response)
			throws IOException, ServletException {

		final ArrayList<String> paramValues = new ArrayList<String>();
		for(Object stringObject : req.getParameterMap().keySet()) {
			String paramName = (String) stringObject;
			final String value = req.getParameter(paramName);
			paramValues.add(paramName + "=" + value);
		}

		String redirectUrl = CRR_URL + "/ccr_image" + "?";
		for(String paramValue : paramValues) {
			redirectUrl += paramValue;
			redirectUrl += "&";
		}

		redirectUrl = redirectUrl.substring(0, redirectUrl.length() - 1);
		response.sendRedirect(redirectUrl);
	}

	private static String getCrrUrl() {
		final ResourceBundle resourceBundle = ResourceBundle.getBundle("cms");
		return resourceBundle.getString("cms.ccr.url");
	}
}