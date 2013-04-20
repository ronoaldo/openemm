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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.sql.DataSource;

import org.agnitas.util.AgnUtils;
import org.agnitas.util.SafeString;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ShowTableTag extends BodyTagSupport {
	
	private static final Logger logger = Logger.getLogger( ShowTableTag.class);

	private static final long serialVersionUID = 9178865921553034730L;
	// global variables:
	protected String sqlStatement;
	protected int startOffset = 0;
	protected int maxRows = 10000;
	protected boolean grabAll = false;
	protected int encodeHtml = 1;

	/**
	 * Setter for property startOffset.
	 * 
	 * @param offset
	 *            New value of property startOffset.
	 */
	public void setStartOffset(String offset) {
		try {
			startOffset = Integer.parseInt(offset);
		} catch (Exception e) {
			startOffset = 0;
		}
	}

	/**
	 * Setter for property sqlStatement.
	 * 
	 * @param sql
	 *            New value of property sqlStatement.
	 */
	public void setSqlStatement(String sql) {
		sqlStatement = sql;
	}

	/**
	 * Setter for property maxRows.
	 * 
	 * @param off
	 *            New value of property maxRows.
	 */
	public void setMaxRows(String off) {
		try {
			maxRows = Integer.parseInt(off);
		} catch (Exception e) {
			maxRows = 0;
		}
	}

	/**
	 * Setter for property encodeHtml.
	 * 
	 * @param off
	 *            New value of property encodeHtml.
	 */
	public void setEncodeHtml(String off) {
		try {
			encodeHtml = Integer.parseInt(off);
		} catch (Exception e) {
			encodeHtml = 1;
		}
	}

	/**
	 * Sets attribute for the pagecontext.
	 */
	@Override
	public int doStartTag() throws JspTagException {
		ApplicationContext aContext = WebApplicationContextUtils.getWebApplicationContext(this.pageContext.getServletContext());
		JdbcTemplate aTemplate = new JdbcTemplate((DataSource) aContext.getBean("dataSource"));
		List rset = null;

		if (id == null) {
			id = "";
		}

		pageContext.setAttribute("__" + id + "_MaxRows", maxRows);

		try {
			grabAll = false;
			if (maxRows == 0) {
				rset = aTemplate.queryForList(sqlStatement);
				grabAll = true;
			} else {
				rset = aTemplate.queryForList(sqlStatement + " LIMIT " + startOffset + "," + maxRows);
			}
			if (rset != null && rset.size() > 0) {
				int rowc = getRowCount( aTemplate);

				ListIterator aIt = rset.listIterator();
				pageContext.setAttribute("__" + id + "_data", aIt);
				pageContext.setAttribute("__" + id + "_ShowTableRownum", rowc);
				return EVAL_BODY_BUFFERED;
			}
		} catch (Exception e) {
			logger.error( "doStartTag (sql: " + sqlStatement + ")", e);
			AgnUtils.sendExceptionMail("sql: " + sqlStatement, e);
			throw new JspTagException("Error: " + e);
		}
		return SKIP_BODY;
	}

	private int getRowCount( JdbcTemplate template) {
		int result = 0;
		try {
			result = template.queryForInt("select count(*) from ( " + sqlStatement + " ) as tmp_tbl");
		} catch (Exception ex) {
			logger.error( "getRowCount (sql: " + sqlStatement + ")", ex);
			AgnUtils.sendExceptionMail("sql: " + sqlStatement, ex);
		}
		return result;
	}

	/**
	 * Sets attribute for the pagecontext.
	 */
	@Override
	public int doAfterBody() throws JspException {
    	BodyContent bodyContent = getBodyContent();
    	if( bodyContent != null) {
    		try {
    			bodyContent.getEnclosingWriter().write( bodyContent.getString());
    		} catch( IOException e) {
    			logger.error( "Error writing body content", e);
    		}
    		bodyContent.clearBody();
   		}

		Map result = getNextRecord();
			
		if( result != null) {
			setResultInPageContext( result);

			return EVAL_BODY_BUFFERED;
		} else {
			return SKIP_BODY;
		}
	}
	
	private Map getNextRecord() {
		ListIterator it = (ListIterator) pageContext.getAttribute("__" + id + "_data");
		Map result = null;
		
		if( it.hasNext() && (grabAll || (this.maxRows--) != 0)) {
			result = (Map) it.next();
		}
		
		return result;
	}

	@Override
	public void doInitBody() throws JspException {
		setResultInPageContext( getNextRecord());
	}
	
	private void setResultInPageContext( Map map) {
		Iterator colIt = map.keySet().iterator();
		String colName;
		Object colData;
		String colDataStr;
		
		while (colIt.hasNext()) {
			colName = (String) colIt.next();
			colData = map.get(colName);
			if (colData != null) {
				colDataStr = colData.toString();
			} else {
				colDataStr = "";
			}
			if (encodeHtml != 0 && String.class.isInstance(colData)) {
				pageContext.setAttribute("_" + id + "_" + colName.toLowerCase(), SafeString.getHTMLSafeString(colDataStr));
			} else {
				pageContext.setAttribute("_" + id + "_" + colName.toLowerCase(), colDataStr);
			}

		}
	}
	
	@Override
	public int doEndTag() {
	
		BodyContent bodyContent = getBodyContent();
		
		if( bodyContent != null)
			bodyContent.clearBody();
		
		return EVAL_PAGE;
	}
}
