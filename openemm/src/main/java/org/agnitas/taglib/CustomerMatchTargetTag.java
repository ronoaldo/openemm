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

package org.agnitas.taglib;

import javax.servlet.jsp.JspException;

import org.agnitas.dao.TargetDao;
import org.agnitas.target.Target;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Connect: Connect to a database Table
 *
 * <Connect table="..." />
 */

public class CustomerMatchTargetTag extends BodyBase {
    private static final long serialVersionUID = 5503535991822272855L;
	protected int customerID;
    protected int targetID;
    
    //***************************************
    //* Implementations for Tag
    //***************************************
    
     /**
     * Setter for property customerID.
     * 
     * @param custID New value of property customerID.
     */
    public void setCustomerID(int custID) {
        this.customerID=custID;
    }
    
     /**
     * Setter for property targetID.
     * 
     * @param targID New value of property targetID.
     */
    public void setTargetID(int targID) {
        this.targetID=targID;
    }
    
    /**
     * checks if customer belongs to target group
     */
    @Override
    public int	doStartTag() throws JspException	{
        int returnValue=SKIP_BODY;
        ApplicationContext aContext=WebApplicationContextUtils.getWebApplicationContext(this.pageContext.getServletContext());
        TargetDao tDao=(TargetDao)aContext.getBean("TargetDao");
        
        if(this.targetID==0) {
            return EVAL_BODY_BUFFERED;
        }
        
        Target aTarget=tDao.getTarget(this.targetID, this.getCompanyID());
        
        if(aTarget!=null) {
            if(aTarget.isCustomerInGroup(this.customerID, aContext)) {
                returnValue=EVAL_BODY_BUFFERED;
            }
        } else {
            returnValue=EVAL_BODY_BUFFERED;
        }
        
        return returnValue;
    }
    
    @Override
    public int doAfterBody() throws JspException {
        return SKIP_BODY;
    }
}
