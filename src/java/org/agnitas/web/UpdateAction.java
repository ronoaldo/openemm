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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.util.AgnUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * Implementation of <strong>Action</strong> that handles Account Admins
 *
 * @author Nicole Serek
 */

public final class UpdateAction extends StrutsActionBase {

    // ---------------------------------------- Public Methods

    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     *
     * ACTION_LIST: launches Automatic update of OpenEMM, <br>
     *          forwards to success or error page <br>
     * <br><br>
     * ACTION_VIEW: forwards to jsp with question to confirm update
     * <br><br>
     * ACTION_NEW: forwards to administration list page
     * <br><br>
     * Any other ACTION_* would cause a forward to "list"
     * <br><br>
     * @param mapping The ActionMapping used to select this instance
     * @param form
     * @param req
     * @param res
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     * @return destination
     */
    public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest req,
            HttpServletResponse res)
            throws IOException, ServletException {

        UpdateForm aForm = null;
        ActionMessages errors = new ActionMessages();
        ActionForward destination=null;

        if(!AgnUtils.isUserLoggedIn(req)) {
            return mapping.findForward("logon");
        }
        
        if(form!=null) {
            aForm=(UpdateForm)form;
        } else {
            aForm=new UpdateForm();
        }

        try {
            switch(aForm.getAction()) {
                case UpdateAction.ACTION_LIST:
                    if(allowed("update.show", req)) {
                    	String cmd = "/home/openemm/bin/upgrade.sh start";
                    	int rc;
                    	Runtime rtime = Runtime.getRuntime ();
                        Process proc = rtime.exec (cmd);
                        rc = proc.waitFor ();
                        if (rc == 0) {
                        	destination=mapping.findForward("success");
                        }
                    } else {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
                        destination=mapping.findForward("error");
                    }
                    break;
                    
                case UpdateAction.ACTION_VIEW:
                	destination=mapping.findForward("question");
                	break;
                	
                case UpdateAction.ACTION_NEW:
                	aForm.setAction(UpdateAction.ACTION_VIEW);
                	destination=mapping.findForward("list");
                	break;

                default:
                    aForm.setAction(UpdateAction.ACTION_VIEW);
                    destination=mapping.findForward("list");
            }
        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
            throw new ServletException(e);
        }

        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
        }
        return destination;
    }
}
