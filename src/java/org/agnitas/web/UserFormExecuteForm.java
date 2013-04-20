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

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Form bean for the measure actions
 *
 * @author mhe
 * @version $Revision: 1.1 $
 */

public final class UserFormExecuteForm extends ActionForm {

    private static final long serialVersionUID = -7075986957480597367L;

	/** Holds value of property agnCI. */
    private int agnCI;
    
    /** Holds value of property agnFN. */
    private String agnFN;
    
    /**
     * Validate the properties that have been set from this HTTP request,
     * and return an <code>ActionErrors</code> object that encapsulates any
     * validation errors that have been found.  If no errors are found, return
     * <code>null</code> or an <code>ActionErrors</code> object with no
     * recorded error messages.
     * 
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     * @return messages for errors, that occured. 
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        return new ActionErrors();        
    }
    
    /**
     * Getter for property ci.
     * @return Value of property ci.
     */
    public int getAgnCI() {
        return this.agnCI;
    }
    
    /**
     * Setter for property ci.
     * 
     * @param agnCI 
     */
    public void setAgnCI(int agnCI) {
        this.agnCI = agnCI;
    }
    
    /**
     * Getter for property agnFN.
     * @return Value of property agnFN.
     */
    public String getAgnFN() {
        return this.agnFN;
    }
    
    /**
     * Setter for property agnFN.
     * @param agnFN New value of property agnFN.
     */
    public void setAgnFN(String agnFN) {
        this.agnFN = agnFN;
    }

    /** Holds value of property agnUseSession. */
    private int agnUseSession=0;

    /**
     * Getter for property agnUseSession.
     * @return Value of property agnUseSession.
     */
    public int getAgnUseSession() {

        return this.agnUseSession;
    }

    /**
     * Setter for property agnUseSession.
     * @param agnUseSession New value of property agnUseSession.
     */
    public void setAgnUseSession(int agnUseSession) {

        this.agnUseSession = agnUseSession;
    }
    
}
