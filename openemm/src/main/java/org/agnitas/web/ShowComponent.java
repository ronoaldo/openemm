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

import org.agnitas.beans.MailingComponent;
import org.agnitas.dao.MailingComponentDao;
import org.agnitas.dao.RecipientDao;
import org.agnitas.preview.Page;
import org.agnitas.preview.Preview;
import org.agnitas.preview.PreviewFactory;
import org.agnitas.util.AgnUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class ShowComponent extends HttpServlet {

	/** Logger. */
	private static final transient Logger logger = Logger.getLogger( ShowComponent.class);
	
    private static final long serialVersionUID = 6640509099616089054L;

	/**
     * Gets mailing components
     * TYPE_IMAGE: if component not empty, write it into response
     * <br><br>
     * TYPE_HOSTED_IMAGE: if component not empty, write it into response
     * <br><br>
     * TYPE_THUMBMAIL_IMAGE: if component not empty, write it into response
     * <br><br>
     * TYPE_ATTACHMENT: create preview <br>
     *          write component into response
     * <br><br>
     * TYPE_PERSONALIZED_ATTACHMENT: create preview <br>
     *          write component into response
     * <br><br>
     */
    @Override
    public void service(HttpServletRequest req, HttpServletResponse res)
    throws IOException, ServletException {
    	
        ServletOutputStream out=null;
        long len=0;
        int compId=0;
        
		if (!AgnUtils.isUserLoggedIn(req)) {
            return;
        }
        
        try {
            compId=Integer.parseInt(req.getParameter("compID"));
        } catch (Exception e) {
        	logger.warn( "Error converting " + (req.getParameter("compID") != null ? "'" + req.getParameter("compID") + "'" : req.getParameter("compID")) + " to integer", e);
            return;
        }
        
        if(compId==0) {
            return;
        }
        
        
        int customerID = 0;
        
        String customerIDStr = req.getParameter("customerID");
        if( StringUtils.isNumeric(customerIDStr)) {
        	customerID = Integer.parseInt(customerIDStr);
        }
        
        MailingComponentDao mDao=(MailingComponentDao)WebApplicationContextUtils.getWebApplicationContext(this.getServletContext()).getBean("MailingComponentDao");
        
        MailingComponent comp=mDao.getMailingComponent(compId, AgnUtils.getCompanyID(req));
        
        if(comp!=null) {
            
            switch(comp.getType()) {
                case MailingComponent.TYPE_IMAGE:
                case MailingComponent.TYPE_HOSTED_IMAGE:
                    if (comp.getBinaryBlock() != null) {
                    res.setContentType(comp.getMimeType());
                    out=res.getOutputStream();
                    out.write(comp.getBinaryBlock());
                    out.flush();
                    out.close();
                    }
                    break;
                case MailingComponent.TYPE_THUMBMAIL_IMAGE:
                    if (comp.getBinaryBlock() != null) {
                    res.setContentType(comp.getMimeType());
                    out=res.getOutputStream();
                    out.write(comp.getBinaryBlock());
                    out.flush();
                    out.close();
                    }
                    break;
                case MailingComponent.TYPE_ATTACHMENT:
                case MailingComponent.TYPE_PERSONALIZED_ATTACHMENT:
                    res.setHeader("Content-Disposition", "attachment; filename=" + comp.getComponentName() + ";");
                    out=res.getOutputStream();
                    ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
                    Preview preview = ((PreviewFactory)applicationContext.getBean("PreviewFactory")).createPreview();        
                   
                    byte[] attachment = null;
                    int mailingID = comp.getMailingID(); 
                                        
                    if( comp.getType() == MailingComponent.TYPE_PERSONALIZED_ATTACHMENT) { 
                    	Page page = null;                        
                        if( customerID == 0 ){ // no customerID is available, take the 1st available test recipient
                              RecipientDao recipientDao = (RecipientDao) applicationContext.getBean("RecipientDao");
                              Map<Integer,String> recipientList = recipientDao.getAdminAndTestRecipientsDescription(comp.getCompanyID(), mailingID);
                              customerID = recipientList.keySet().iterator().next(); 
                        }
                        page = preview.makePreview(mailingID, customerID, false);
                        attachment = page.getAttachment(comp.getComponentName());
                        
                    } else {
                    	attachment = comp.getBinaryBlock();
                    }                                       
                    
                    len= attachment.length;
                    res.setContentLength((int)len);
                    out.write(attachment);
                    out.flush();
                    out.close();
                    break;
            }
        }
    }
}
