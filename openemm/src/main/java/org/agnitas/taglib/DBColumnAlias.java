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

import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import org.agnitas.util.AgnUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class DBColumnAlias extends BodyBase {
	
	private static final transient Logger logger = Logger.getLogger( DBColumnAlias.class);
    
    protected String column=null;
    
     /**
     * Setter for property column.
     * 
     * @param aCol New value of property column.
     */
    public void setColumn(String aCol) {
        if(aCol!=null) {
            column = aCol;
        } else {
            column = "";
        }
    }
    
    /**
     * lists shortnames
     */
    @Override
    public int doStartTag() throws JspTagException {
        ApplicationContext aContext=WebApplicationContextUtils.getWebApplicationContext(this.pageContext.getServletContext());
        JdbcTemplate jdbc=AgnUtils.getJdbcTemplate(aContext);
        String result = this.column;
        String sql="SELECT shortname FROM customer_field_tbl WHERE company_id="+this.getCompanyID()+" AND col_name=? AND (admin_id=? OR admin_id=0) ORDER BY admin_id DESC";
        
        try {
        	List l=jdbc.queryForList(sql, new Object[] {this.column, new Integer(AgnUtils.getAdmin(pageContext).getAdminID())});

            if(l !=null && l.size() > 0) {
                Map row=(Map)l.get(0);
                result=(String) row.get("shortname");
            }
        } catch (Exception e) {
        	logger.error( "doStartTag", e);
        	AgnUtils.sendExceptionMail("sql:" + sql + ", " + column + ", " + AgnUtils.getAdmin(pageContext).getAdminID(), e);
        }
        finally {
        	writeResult( result );
        }
        return SKIP_BODY;
    }
    
    private void writeResult( String result ) {
    	try {
    		JspWriter out=null;
    		out=pageContext.getOut();
    		out.print(result);
    	}
    	catch(Exception e) {
    		logger.error( "doStartTag", e);
    	}
    }
}
