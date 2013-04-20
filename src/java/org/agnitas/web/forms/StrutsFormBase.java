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

package org.agnitas.web.forms;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.agnitas.beans.Admin;
import org.agnitas.util.AgnUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Implementation of <strong>Action</strong> that validates a user logon.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.1 $ $Date: 2006/08/03 08:47:47 $
 */

public class StrutsFormBase extends org.apache.struts.action.ActionForm {

    /**
	 * 
	 */
	private static final long serialVersionUID = -517998059502119608L;
	public static final int DEFAULT_NUMBER_OF_ROWS = 50;
    public static final int DEFAULT_REFRESH_MILLIS = 250;

    public static final int WIDTH_STATE_UNDEFINED = -1;
    public static final int WIDTH_STATE_NORMAL = 0;
    public static final int WIDTH_STATE_WIDE = 1;

    private static final Pattern htmlTagPattern;
    
    static {
    	htmlTagPattern = Pattern.compile( "^.*?</?(\\w+)[^>]*>(.*)$", Pattern.MULTILINE | Pattern.DOTALL);
    }
    
    /**
     * List of HTML tags. Alls tags in the list are assumed to be safe (no XSS).
     */
    public static final String[] GENERIC_SAFE_HTML_TAGS = { "u", "i", "b", "p", "div", "span", "img", "a", "font", "br", "map", "area" };
    
    /**
     *  holds the preferred number of rows a user wants to see in a list
     */
    private int numberofRows = -1; // -1 -> not initialized
    /**
     * flag which show's that the number of rows a user wants to see has been changed
     */
    private boolean numberOfRowsChanged = false; 
    
 // keep sort, order , page , columnwidth
    private String sort="";
    private String order="";
    private String page="1";
    protected List<String> columnwidthsList = new ArrayList<String>();
        
    private int refreshMillis = DEFAULT_REFRESH_MILLIS ;
    private boolean error = false;

    private int extendedWidthState = WIDTH_STATE_UNDEFINED;
    
	/**
     * Getter for property companyID.
     *
     * @return Value of property companyID.
     * @param req
     */
    public int getCompanyID(HttpServletRequest req) {

        int companyID=0;

        try {
			companyID = AgnUtils.getAdmin(req).getCompany().getId();
        } catch (Exception e) {
            AgnUtils.logger().error("getCompanyID: "+e.getMessage());
            companyID=0;
        }

        return companyID;
    }

    /**
     * Checks permission.
     */
    protected static boolean allowed(String id, HttpServletRequest req) {
        Admin aAdmin = AgnUtils.getAdmin(req);
		if (aAdmin == null) {
            return false; //Nothing allowed if there is no permission set in Session
        }
        else {
        	return aAdmin.permissionAllowed(id);
        }
    }

    /**
     * Resets parameters.
     */
    @Override
    public void reset(ActionMapping map, HttpServletRequest request) {
        String aCBox=null;
        String name=null;
        String value=null;

        Enumeration names=request.getParameterNames();
        while(names.hasMoreElements()) {
            name=(String)names.nextElement();
            if(name.startsWith("__STRUTS_CHECKBOX_") && name.length()>18) {
                aCBox=name.substring(18);
                try {
                    if((value=request.getParameter(name))!=null) {
                        BeanUtils.setProperty(this, aCBox, value);
                    }
                } catch (Exception e) {
                    AgnUtils.logger().error("reset: "+e.getMessage());
                }
            }
        }
    }

    /**
     * Getter for property webApplicationContext.
     *
     * @return Value of property webApplicationContext.
     */
    public ApplicationContext getWebApplicationContext() {
        return WebApplicationContextUtils.getWebApplicationContext(this.getServlet().getServletContext());
    }

	public int getNumberofRows() {
		return numberofRows;
	}

	public void setNumberofRows(int numberofRows) {
		this.numberofRows = numberofRows;
	}

	public boolean isNumberOfRowsChanged() {
		return numberOfRowsChanged;
	}

	public void setNumberOfRowsChanged(boolean numberOfRowsChanged) {
		this.numberOfRowsChanged = numberOfRowsChanged;
	}
	

    public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}
		
	public int getRefreshMillis() {
		return refreshMillis;
	}

	public void setRefreshMillis(int refreshMillis) {
		this.refreshMillis = refreshMillis;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public List<String> getColumnwidthsList() {
		return columnwidthsList;
	}

	public void setColumnwidthsList(List<String> columnwidthsList) {
		this.columnwidthsList = columnwidthsList;
	}

	/**
	 * Original validate() called by Struts.
	 * This method is made "final" to force calling method checkForUnsafeHtmlTags(). 
	 * If you want to implement your own validate() use formSpecificValidate()!
	 * 
	 * @see formSpecificVaidate(ActionMapping, HttpServletRequest)
	 */
	@Override
	public final ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		
		// First, check if we can find HTML tags in at least one request parameter.
		ActionErrors errors = checkForHtmlTags(request);
		
		// The do user defined (and form specific) validation
		errors.add( formSpecificValidate(mapping, request));

		errors.add(super.validate(mapping,request));
		
		return errors;
	}
	
	public ActionErrors formSpecificValidate(ActionMapping mapping, HttpServletRequest request) {
		return null;
	}

	protected ActionErrors checkForHtmlTags( HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		Set<String> tagNames = getUnsafeHtmlTagNames(request);

		for(String tagName : tagNames) 
			errors.add( ActionErrors.GLOBAL_MESSAGE, new ActionMessage("mailing.unsafe_html_tag", tagName));
		
		return errors;
	}
	
	/**
	 * Checks, if parameter is excluded from checking for unsafe HTML tags. If method returns false,
	 * method checkForHtmlTags() is called.
	 * 
	 * If method is not overwritten, false is returned for every parameter name.
	 * 
	 * @param parameterName parameter name
	 * @param request TODO
	 * @return true, if parameter is excluded from check for unsafe HTML tags
	 */
	protected boolean isParameterExcludedForUnsafeHtmlTagCheck( String parameterName, HttpServletRequest request) {
		return false;
	}
	
	protected Set<String> getUnsafeHtmlTagNames( HttpServletRequest request) {
		Set<String> tagNames = new HashSet<String>();
		Enumeration parameterNames = request.getParameterNames();
		
		while( parameterNames.hasMoreElements()) {
			String paramName = (String) parameterNames.nextElement();
			
			if( !isParameterExcludedForUnsafeHtmlTagCheck( paramName, request)) {
				tagNames.addAll( getAllUnsafeHtmlTagNames( paramName, request.getParameterValues(paramName)));
			}
		}

		return tagNames;
	}
	
	protected Set<String> getAllUnsafeHtmlTagNames( String paramName, String[] textArray) {
		Set<String> tagNames = new HashSet<String>();
		
		for(String text : textArray) 
			tagNames.addAll( getUnsafeHtmlTagNames( paramName, text));
		
		return tagNames;
	}
	
	protected Set<String> getUnsafeHtmlTagNames( String paramName, String text) {
		Set<String> tagNames = new HashSet<String>();
		String tagName;
		
		Matcher matcher = htmlTagPattern.matcher( text);
		while( matcher.matches()) {
			tagName = matcher.group( 1).toLowerCase();
			
			if( isUnsafeHtmlTagForParameter( tagName, paramName))
				tagNames.add( tagName);
			
			matcher = htmlTagPattern.matcher(matcher.group(2));
		}
		
		return tagNames;
	}
	
	protected boolean isUnsafeHtmlTagForParameter( String tagName, String paramName) {
		return !ArrayUtils.contains( GENERIC_SAFE_HTML_TAGS, tagName);
	}

    public int getExtendedWidthState() {
        return extendedWidthState;
    }

    public void setExtendedWidthState(int extendedWidthState) {
        this.extendedWidthState = extendedWidthState;
    }
}
