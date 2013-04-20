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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.beans.Admin;
import org.agnitas.util.AgnUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.web.struts.DispatchActionSupport;


/**
 * Implementation of <strong>Action</strong> that validates a user logon.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.1 $ $Date: 2006/08/03 08:47:47 $
 */

public class StrutsDispatchActionBase extends DispatchActionSupport {


    /**
     * Get the bean via it name
     *
     * @return Value of Object bean
     * @param name
     */
    protected Object getBean(String name) { 
        return getWebApplicationContext().getBean(name);
    }

    /**
     * Get the hibernateTemplate.
     *
     * @return Value of property hibernateTemplate.
     */
    protected HibernateTemplate getHibernateTemplate() {
        SessionFactory factory=null;
        
        factory=(SessionFactory)this.getWebApplicationContext().getBean("sessionFactory");
        
        return new HibernateTemplate(factory);
    }
    
    /**
     * Getter for property companyID.
     * 
     * @return Value of property companyID.
     * @param req 
     */
    public int getCompanyID(HttpServletRequest req) {
        
        return AgnUtils.getCompanyID(req);
    }
    
    /**
     * checks permission.
     */
    protected boolean allowed(String id, HttpServletRequest req) {
		Admin aAdmin = AgnUtils.getAdmin(req);
        
		if (aAdmin == null) {
			return false; // Nothing allowed if there is no permission set in Session
		}
        
        return aAdmin.permissionAllowed(id);
    }


    /**
     * Get the ActionForward object
     *
     * @return Value of property ActionForward
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @param name
     */
	protected ActionForward	dispatchMethod(
				ActionMapping mapping, ActionForm form,
				HttpServletRequest request,
				HttpServletResponse response,
				String name) throws java.lang.Exception {
		if(request.getParameter("action_forward") != null) {
			return super.dispatchMethod(mapping, form, request, response, request.getParameter("action_forward"));
		}
		return super.dispatchMethod(mapping, form, request, response, name);
	}

    
    /**
     * Constructor
     */
    public StrutsDispatchActionBase() {
        super();
    }  
}
