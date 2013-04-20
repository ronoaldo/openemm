package org.agnitas.mailing.web;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.beans.Admin;
import org.agnitas.dao.MailingDao;
import org.agnitas.util.AgnUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.web.struts.DispatchActionSupport;

public class MailingSendAjaxAction extends DispatchActionSupport {

	private MailingDao mailingDao;
	
	public ActionForward transmissionRunning(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception {

		Admin admin = AgnUtils.getAdmin(request); 
		if(  admin == null ) {
			return null;
		}
		
		if( !admin.permissionAllowed("mailing.send.admin\\|mailing.send.test")) {
			return null;
		}
				
		String message = "TRUE";
		String mailingIDStr = request.getParameter("mailingID");
		if( StringUtils.isNotEmpty(mailingIDStr) && StringUtils.isNumeric(mailingIDStr)) {
			boolean transmissionRunning = mailingDao.isTransmissionRunning(Integer.parseInt(mailingIDStr));
			message = transmissionRunning ? "TRUE": "FALSE";
		}
		
		response.setContentType("text/plain"); 
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.write(message);
		pw.close();
		
		return null;
	}

	public void setMailingDao(MailingDao mailingDao) {
		this.mailingDao = mailingDao;
	}
	
}
