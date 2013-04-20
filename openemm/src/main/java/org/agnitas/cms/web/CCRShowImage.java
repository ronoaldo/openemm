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
import org.agnitas.cms.dao.*;
import org.agnitas.cms.webservices.generated.*;
import org.agnitas.util.*;
import org.springframework.web.context.*;
import org.springframework.web.context.support.*;

/**
 * Servlet which dispatchs request from CmsShowImage,
 * to more quickly image loading without webservices, becauce CCRShowImage
 * placed in central content repository and woks with db.
 * Parameters:
 * fid - id of media-file
 *
 * @author Igor Nesterenko
 */
public class CCRShowImage extends HttpServlet {

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse response) throws
			ServletException, IOException {

		ServletOutputStream out;
		WebApplicationContext applicationContext = WebApplicationContextUtils
				.getWebApplicationContext(this.getServletContext());
		MediaFileDao mediaFileDao = (MediaFileDao) applicationContext
				.getBean("MediaFileDao");

		MediaFile mediaFile = null;

		if(req.getParameter("fid") != null) {
			int mediaFileId = Integer.parseInt(req.getParameter("fid"));
			mediaFile = mediaFileDao.getMediaFile(mediaFileId);
		} else if(req.getParameter("cmId") != null &&
				req.getParameter("preview") != null) {
			int cmId = Integer.parseInt(req.getParameter("cmId"));
			mediaFile = mediaFileDao.getPreviewOfContentModule(cmId);
		} else if(req.getParameter("cmtId") != null &&
				req.getParameter("preview") != null) {
			int cmtId = Integer.parseInt(req.getParameter("cmtId"));
			mediaFile = mediaFileDao.getPreviewOfContentModuleType(cmtId);
		} else if(req.getParameter("cmTemplateId") != null &&
				req.getParameter("preview") != null) {
			int cmTemplateId = Integer.parseInt(req.getParameter("cmTemplateId"));
			mediaFile = mediaFileDao.getPreviewOfContentModuleTemplate(cmTemplateId);
		}

		String mimeType;
		byte[] imageData;

		if(mediaFile != null) {
			mimeType = mediaFile.getMimeType();
			imageData = mediaFile.getContent();
		} else {
			mimeType = "text/html";
			imageData = "image not found".getBytes();
		}

		try {
			response.setContentType(mimeType);
			out = response.getOutputStream();
			out.write(imageData);
			out.flush();
			out.close();
		} catch(Exception e) {
			AgnUtils.logger().error("Error while writing image data to response", e);
		}
	}
}
