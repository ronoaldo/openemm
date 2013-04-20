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
import javax.servlet.jsp.tagext.TagSupport;

import org.agnitas.util.AgnUtils;

public final class CheckLogonTag extends TagSupport {
     
     
    private static final long serialVersionUID = -4706642742651352150L;
    private String page = "/login.jsp";
       
    public String getPage() {
        return (this.page);   
    }
    
    public void setPage(String page) {
        this.page = page;  
    }
    
    @Override
    public int doStartTag() throws JspException { 
        return (SKIP_BODY);  
    }
    
    @Override
    public int doEndTag() throws JspException {
        // Is there a valid user logged on?        
        // Forward control based on the results
        if (AgnUtils.getAdmin(pageContext) != null)
            return (EVAL_PAGE);
        else {
            try {
                pageContext.forward(page);
            } catch (Exception e) {
                throw new JspException(e.toString());
            }
            return (SKIP_PAGE);
        }  
    }
    
    @Override
    public void release() {  
        super.release();
        this.page = "/logon.jsp";
    }
}
