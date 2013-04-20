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

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.sql.DataSource;

import org.agnitas.util.SafeString;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 *
 * @author Martin Helff, Andreas Rehak
 */
public class BlacklistAction extends BodyBase {
    
    private static final long serialVersionUID = 8571036430687433983L;

	/**
     * Adds or removes a data set to blacklist.
     */
    @Override
    public int doStartTag() throws JspTagException {
        ApplicationContext aContext=WebApplicationContextUtils.getWebApplicationContext(this.pageContext.getServletContext());
        JdbcTemplate aTemplate=new JdbcTemplate((DataSource)aContext.getBean("dataSource")); 
        ServletRequest req=null;

        req=pageContext.getRequest();
        if(req.getParameter("newemail")!=null && req.getParameter("newemail").length()>0) {
                String sqlInsert="INSERT INTO cust_ban_tbl (company_id, email) VALUES (" + this.getCompanyID() + ", '" +
                SafeString.getSQLSafeString(req.getParameter("newemail").toLowerCase().trim()) + "')";
                
                aTemplate.update(sqlInsert);
        }
        
        if(req.getParameter("delete")!=null && req.getParameter("delete").length()>0) {
                String sqlDelete="DELETE FROM cust_ban_tbl WHERE company_id=" + this.getCompanyID() + " AND email='" +
                SafeString.getSQLSafeString(req.getParameter("delete").toLowerCase()) + "'";
                
                aTemplate.update(sqlDelete);
        }
        return SKIP_BODY;
    }
}
