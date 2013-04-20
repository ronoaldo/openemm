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

package org.agnitas.beans.impl;

import org.agnitas.beans.Mailing;
import org.agnitas.beans.Mediatype;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author mhe
 */
public class MediatypeImpl implements Mediatype {
    
    /**
     * Holds value of property param.
     */
    protected String param="";

    /**
     * Getter for property param.
     * @return Value of property param.
     */
    public String getParam() throws Exception {
        return param;
    }

    /**
     * Setter for property param.
     * @param param New value of property param.
     */
    public void setParam(String param) throws Exception {
        this.param = param;
    }
   
    protected int priority=5;

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    protected int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /** Holds value of property companyID. */
    protected int companyID;

    /** Getter for property companyID.
     * @return Value of property companyID.
     *
     */
    public int getCompanyID() {
        return this.companyID;
    }

    /** Setter for property companyID.
     * @param companyID New value of property companyID.
     *
     */
    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }

    /**
     * Holds value of property param.
     */
    protected String template;

    /**
     * Getter for property param.
     * @return Value of property param.
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Setter for property param.
     * @param param New value of property param.
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    public void syncTemplate(Mailing mailing, ApplicationContext con) {
    }
 
    public int hashCode() {
        return param.hashCode();
    }
    
    public boolean equals(Object that) {
    	if( that == null) // According to Object.equals(Object), equals(null) returns false
    		return false;
    	
        return ((Mediatype)that).hashCode()==this.hashCode();
    }
}
