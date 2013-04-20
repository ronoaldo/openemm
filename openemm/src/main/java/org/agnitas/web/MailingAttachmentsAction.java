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
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.beans.Mailing;
import org.agnitas.beans.MailingComponent;
import org.agnitas.beans.impl.MailingComponentImpl;
import org.agnitas.dao.MailingComponentDao;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.TargetDao;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.forms.MailingAttachmentsForm;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import org.apache.commons.lang.StringUtils;
import org.springframework.dao.TransientDataAccessResourceException;


/**
 * Implementation of <strong>Action</strong> that validates a user logon.
 *
 * @author Martin Helff
 */

public final class MailingAttachmentsAction extends StrutsActionBase {

    private static final transient Logger logger = Logger.getLogger(MailingAttachmentsAction.class);

    // --------------------------------------------------------- Public Methods
	ActionMessages errors;
	private MailingDao mailingDao;
	private TargetDao targetDao;
	private MailingComponentDao componentDao;

	/**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     * <br>
     * ACTION_LIST: loads mailing data into form, <br>
     *     loads attachments list into request, <br>
     *     loads target groups list into request, <br>
     *     forwards to list page
     * <br><br>
     * ACTION_SAVE: if attachment file is not empty and its size is lower than max allowed size - adds it to
     *     the mailing, and saves mailing to database; <br>
     *     if request has parameter like "delete" + componentID - deletes component with that ID; <br>
     *     saves changed target groups of components (gets that from request parameters "target" + componentID); <br>
     *     loads mailing data into form; <br>
     *     loads attachments list into request; <br>
     *     loads target groups list into request; <br>
     *     if there wasn't file selected while adding new components - shows error messages; <br>
     *     forwards to list page
     * <br><br>
     * @param form the optional ActionForm bean for this request (if any)
     * @param req HTTP request
     * @param res HTTP response
     * @param mapping The ActionMapping used to select this instance
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     * @return destination
     */
    public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest req,
            HttpServletResponse res)
            throws IOException, ServletException {
        
        // Validate the request parameters specified by the user
        MailingAttachmentsForm aForm=null;
        errors = new ActionMessages();
      	ActionMessages messages = new ActionMessages();
      	ActionForward destination=null;
        
        if(!AgnUtils.isUserLoggedIn(req)) {
            return mapping.findForward("logon");
        }
        
        aForm=(MailingAttachmentsForm)form;

        if(!allowed("mailing.attachments.show", req)) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
            saveErrors(req, errors);
            return null;
        }
        
        try {
            switch(aForm.getAction()) {
                case MailingAttachmentsAction.ACTION_LIST:
                    loadMailing(aForm, req);
					loadAttachments(aForm, req);
					loadTargetGroups(req);
                    aForm.setAction(MailingAttachmentsAction.ACTION_SAVE);
                    destination=mapping.findForward("list");
                    break;
                    
                case MailingAttachmentsAction.ACTION_SAVE:
                   	destination=mapping.findForward("list");
                    loadMailing(aForm, req);
                    try {
                        saveAttachment(aForm, req);
                    }
                    catch (TransientDataAccessResourceException e) {
                        AgnUtils.logger().error("execute: " + e + "\n" + AgnUtils.getStackTrace(e));
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.hibernate.attachmentTooLarge"));
                    }
                    loadAttachments(aForm, req);
                    loadTargetGroups(req);
                    aForm.setAction(MailingAttachmentsAction.ACTION_SAVE);

                    Enumeration parameterNames = req.getParameterNames();
                    boolean aComponentWasJustAdded = false;
                    while (parameterNames.hasMoreElements()) {
                        Object parameter = parameterNames.nextElement();
                        if (parameter instanceof String) {
                            String parameterString = (String) parameter;
                            if (parameterString.startsWith("add") && AgnUtils.parameterNotEmpty(req,parameterString)) {
                                aComponentWasJustAdded = true;
                                break;
                            }
                        }
                    }

                    if(errors.isEmpty()){
                        if (aComponentWasJustAdded && (aForm.getNewAttachment() == null || aForm.getNewAttachment().getFileName() == null || "".equals(aForm.getNewAttachment().getFileName()))) {
                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("mailing.errors.no_attachment_file"));
                        } else {
                            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            logger.error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
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

	private void loadAttachments(MailingAttachmentsForm aForm, HttpServletRequest request) {
		Vector<MailingComponent> attachments = componentDao.getMailingComponents(aForm.getMailingID(), getCompanyID(request), MailingComponent.TYPE_ATTACHMENT);
		request.setAttribute("attachments", attachments);
	}

	private void loadTargetGroups(HttpServletRequest request) {
		List targets = targetDao.getTargets(getCompanyID(request));
		request.setAttribute("targets", targets);
	}

    /**
     * Loads mailing
     */
    protected void loadMailing(MailingAttachmentsForm aForm, HttpServletRequest req) throws Exception {
        Mailing aMailing=mailingDao.getMailing(aForm.getMailingID(), this.getCompanyID(req));
        
        aForm.setShortname(aMailing.getShortname());
        aForm.setDescription(aMailing.getDescription());
        aForm.setIsTemplate(aMailing.isIsTemplate());
        aForm.setWorldMailingSend(aMailing.isWorldMailingSend());
        
        logger.info("loadMailing: mailing loaded");
    }
    
    /**
     * Saves attachement
     */
    protected void saveAttachment(MailingAttachmentsForm aForm, HttpServletRequest req) {
        MailingComponent aComp;
        String aParam;
        Vector deleteEm=new Vector();
        
        Mailing aMailing=mailingDao.getMailing(aForm.getMailingID(), this.getCompanyID(req));
        
        FormFile newAttachment=aForm.getNewAttachment();
        try {
        	double size = newAttachment.getFileSize();
        	int attachmentMaxSize = Integer.parseInt(AgnUtils.getDefaultValue("attachment.maxSize"));

            if(size != 0  && size < attachmentMaxSize) {
                aComp= new MailingComponentImpl();
                aComp.setCompanyID(this.getCompanyID(req));
                aComp.setMailingID(aForm.getMailingID());
                aComp.setType(MailingComponent.TYPE_ATTACHMENT);
                aComp.setComponentName(aForm.getNewAttachmentName());
                aComp.setBinaryBlock(newAttachment.getFileData());
                aComp.setEmmBlock(aComp.makeEMMBlock());
                aComp.setMimeType(newAttachment.getContentType());
                aComp.setTargetID(aForm.getAttachmentTargetID());
                aMailing.addComponent(aComp);
            } else if(size >= attachmentMaxSize) {
            	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.attachment", AgnUtils.getDefaultValue("attachment.maxSize")));
            }
        } catch(Exception e) {
            logger.error("saveAttachment: "+e);
        }
        
        Iterator it=aMailing.getComponents().values().iterator();
        while (it.hasNext()) {
            aComp=(MailingComponent)it.next();
            switch(aComp.getType()) {
                case MailingComponent.TYPE_ATTACHMENT:
                    aParam=req.getParameter("delete"+aComp.getId());
                    if(!StringUtils.isEmpty(aParam)) {
                        deleteEm.add(aComp);
                    }
                    aParam=req.getParameter("target"+aComp.getId());
                    if(aParam!=null) {
                        aComp.setTargetID(Integer.parseInt(aParam));
                    }
                    break;
            }
        }
        
        Enumeration en=deleteEm.elements();
        while(en.hasMoreElements()) {
            aMailing.getComponents().remove(((MailingComponent)en.nextElement()).getComponentName());
        }
        
        mailingDao.saveMailing(aMailing);
    }

	public void setMailingDao(MailingDao mailingDao) {
		this.mailingDao = mailingDao;
	}

	public MailingDao getMailingDao() {
		return mailingDao;
	}

	public void setTargetDao(TargetDao targetDao) {
		this.targetDao = targetDao;
	}

	public TargetDao getTargetDao() {
		return targetDao;
	}

	public void setComponentDao(MailingComponentDao componentDao) {
		this.componentDao = componentDao;
	}

	public MailingComponentDao getComponentDao() {
		return componentDao;
	}
}
