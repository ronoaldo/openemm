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

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.TagSupport;

import org.agnitas.util.AgnUtils;
import org.agnitas.util.SafeString;

/**
 * Connect: Connect to a database Table
 *
 * <Connect table="..." />
 */

// Use javax.servlet.jsp.tagext.BodyTagSupport instead
@Deprecated
public abstract class BodyBase extends TagSupport implements BodyTag {
    protected BodyContent bodyContent=null;
       
    //***************************************
    //* Implementations for Tag
    //***************************************
    @Override
    public void	doInitBody() {
        return;
    }
    
    @Override
    public int doStartTag() throws JspException	{
        return EVAL_BODY_BUFFERED;
    }
    
     /**
     * Setter for property bodyContent.
     * 
     * @param b New value of property bodyContent.
     */
    @Override
    public void	setBodyContent(BodyContent b) {
        bodyContent=b;
    }
    
    /**
     * writes the body content.
     */
    @Override
    public int doEndTag() throws JspException {
        try {
            if(bodyContent!=null) {
//                JspWriter w=bodyContent.getEnclosingWriter();

                /*
                if(w!=null) {
                    bodyContent.writeOut(w);
                } else
                */
                pageContext.getOut().print( bodyContent.getString());
            }
        } catch(IOException e) {
            throw new JspException(e.getMessage());
        }
        
        return EVAL_PAGE;
    }
    
    /**
     * Getter for property localeString.
     *
     * @return Value of localeString.
     */
    public String getLocaleString(String key) {
        return SafeString.getLocaleString(key, AgnUtils.getAdmin(pageContext).getLocale());
    }
    
    /**
     * Getter for property companyID.
     *
     * @return Value of companyID.
     */
    public int getCompanyID() {
        
        int companyID=0;
        
        try {
            companyID=AgnUtils.getAdmin(pageContext).getCompany().getId();
        } catch (Exception e) {
            AgnUtils.logger().error("BodyBase - getCompanyID: no companyID: "+e.getMessage());
            companyID=0;
        }
        return companyID;
    }
}

