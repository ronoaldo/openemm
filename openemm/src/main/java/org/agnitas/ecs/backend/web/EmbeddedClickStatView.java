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

package org.agnitas.ecs.backend.web;

import org.agnitas.ecs.EcsGlobals;
import org.agnitas.ecs.backend.service.EmbeddedClickStatService;
import org.agnitas.util.AgnUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Servlet for generating Embedded click statistics mailing HTML.
 * Gets mailing HTML-content for certain recipient and adds
 * hidden fields at the end of HTML that will be used by ECS-page
 * javascript to generate click-stat-labels and place them near links
 *
 * @author Vyacheslav Stepanov
 */
public class EmbeddedClickStatView extends HttpServlet {

    /**
     * Method handles servlet work (see description of class). The input parameters are:
     * mailingId - id of mailing to get HTML content
     * recipientId - id of recipient to generate mailing content for
     * viewMode - view mode of Embedded click statistics
     * companyId - id of company
     *
     * @param req request
     * @param res response
     * @throws IOException
     * @throws ServletException
     */
    public void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        ServletOutputStream out;

    	EmbeddedClickStatService service = (EmbeddedClickStatService) WebApplicationContextUtils.getWebApplicationContext(
                this.getServletContext()).getBean("EmbeddedClickStatService");

    	String charsetPattern = "<meta http-equiv *= *\"content-type\".*charset *= *[A-Za-z0-9-.:_]*";

        res.setContentType("text/html");
        res.setCharacterEncoding("utf-8");
        out = res.getOutputStream();

        Locale locale = null;
        if( req.getParameter("language") != null)
        	locale = new Locale(req.getParameter("language"));
        else
        	locale = Locale.getDefault();
 		ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);

        if (req.getParameter("mailingId") == null || req.getParameter("recipientId") == null ||
                req.getParameter("viewMode") == null || req.getParameter("companyId") == null) {
            AgnUtils.logger().error("EmbeddedClickStatView: Parameters error (not enough parameters to show EmbeddedClickStat View)");
			String errorMsg = bundle.getString("ecs.Error.NoParams");
			out.write(errorMsg.getBytes("UTF-8"));
        } else {
            int mailingId = Integer.valueOf(req.getParameter("mailingId"));
            int recipientId = Integer.valueOf(req.getParameter("recipientId"));
            int viewMode = Integer.valueOf(req.getParameter("viewMode"));
            int companyId = Integer.valueOf(req.getParameter("companyId"));
            if (recipientId == 0) {
				String errorMsg = bundle.getString("ecs.Error.NoTestRecipients");
                out.write(errorMsg.getBytes("UTF-8"));
            } else {
                String mailingContent = service.getMailingContent(mailingId, recipientId);
                
                Pattern pattern = Pattern.compile(charsetPattern, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(mailingContent);
                mailingContent = matcher.replaceAll("<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8");
	
                if (viewMode != EcsGlobals.MODE_PURE_MAILING) {
                    mailingContent = service.addStatsInfo(mailingContent, viewMode, mailingId, companyId);
                }
                String contextPath = AgnUtils.getEMMProperty("system.url");
                mailingContent = "<script type=\"text/javascript\" src=\"" + contextPath + "/ecs/backend/js/jquery-1.6.2.min.js\"></script>" +
                        "<script type=\"text/javascript\" src=\"" + contextPath + "/ecs/backend/js/statLabelAdjuster.js\"></script>" +
                        mailingContent;
                out.write(mailingContent.getBytes("UTF-8"));
            }
        }

        out.flush();
        out.close();
    }

}