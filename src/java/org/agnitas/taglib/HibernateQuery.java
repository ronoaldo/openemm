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
import java.util.List;
import java.util.ListIterator;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class HibernateQuery extends BodyTagSupport {
	
	private static final transient Logger logger = Logger.getLogger( HibernateQuery.class);
    
    private static final long serialVersionUID = -9049390953532060151L;
	// global variables:
    protected String query;
    protected int startOffset=0;
    protected int maxRows=-1;
    protected int encodeHtml=1;
    
     /**
     * Setter for property query.
     * 
     * @param sql New value of property query.
     */
    public void setQuery(String sql) {
        query = sql;
    }
     
     /**
     * Setter for property maxRows.
     * 
     * @param off New value of property maxRows.
     */
    public void setMaxRows(String off) {
        try {
            maxRows=Integer.parseInt(off);
        } catch (Exception e) {
            maxRows=-1;
        }
    }
    
    @Override
    public int doStartTag() throws JspTagException {
        ApplicationContext aContext=WebApplicationContextUtils.getWebApplicationContext(this.pageContext.getServletContext());
        HibernateTemplate aTemplate=new HibernateTemplate((SessionFactory)aContext.getBean("sessionFactory"));
        List rset=null;
        
        if(id==null) {
            id = "";
        }
        
        try {
            startOffset=Integer.parseInt(pageContext.getRequest().getParameter("startWith"));
        } catch (Exception e) {
            startOffset=0;
        }
        
        pageContext.setAttribute("__"+id+"_MaxRows", maxRows);
        
        try {
            rset=aTemplate.find(query);
            if(rset!=null && rset.size()>0) {
                ListIterator aIt=rset.listIterator(startOffset);
                pageContext.setAttribute("__"+id, rset);
                pageContext.setAttribute("__"+id+"_data", aIt);
                pageContext.setAttribute("__"+id+"_ShowTableRownum", rset.size());
                return EVAL_BODY_BUFFERED;
            } else {
                return SKIP_BODY;
            }
            
        }   catch ( Exception e) {
        	logger.error( "doStartTag", e);
            throw new JspTagException("Error: " + e);
        }
    }
    
    @Override
    public int doAfterBody() throws JspException {
    	BodyContent bodyContent = getBodyContent();
    	if( bodyContent != null) {
    		try {
    			bodyContent.getEnclosingWriter().write( bodyContent.getString());
    		} catch( IOException e) {
    			logger.error( "doAfterBody", e);
    		}
    		bodyContent.clearBody();
    	}
    	
    	
        Object aRecord = getNextRecord();
        
        if( aRecord != null) {
            pageContext.setAttribute(id, aRecord);
            return EVAL_BODY_BUFFERED;
        } else {
            return SKIP_BODY;
        }
    }

	@Override
	public void doInitBody() throws JspException {
		Object object = getNextRecord();
		
		pageContext.setAttribute(id, object);
	}
    
	protected Object getNextRecord() {
        ListIterator it=(ListIterator)pageContext.getAttribute("__"+id+"_data");
		Object result = null;
		
		if( it.hasNext() && (this.maxRows--) != 0) {
			result = it.next();
		}
		
		return result;
	}
	
	@Override
	public int doEndTag() {
		BodyContent content = getBodyContent();
		
		if( content != null)
			content.clearBody();
		
		return EVAL_PAGE;
	}
}
