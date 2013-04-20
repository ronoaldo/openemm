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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.agnitas.beans.Admin;
import org.agnitas.dao.RecipientDao;
import org.agnitas.dao.TargetDao;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.forms.StrutsFormBase;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.web.struts.ActionSupport;


/**
 * Base action class which is used as a base class for other OpenEMM actions<br><br>
 * Contains predefined set of action values which can be used by subclasses:<br>
 * ACTION_LIST - usually used for overview pages for showing the list of elements in table<br>
 * ACTION_VIEW - used for loading data of some entity and showing it on edit-page<br>
 * ACTION_SAVE - used for saving entity after editing on edit-page<br>
 * ACTION_NEW - used for creation of new entity<br>
 * ACTION_CONFIRM_DELETE - used for forwarding to page with deletion confirmation<br>
 * ACTION_DELETE - used for removing entity from database<br>
 * ACTION_LAST - just indicates the last number-value used for default actions (the actions of subclasses should start<br>
 *     from ACTION_LAST + 1)<br><br>
 * Also contains util methods which can be useful for using in subclasses:<br>
 * - getBean: gets the bean via applicationContext<br>
 * - several methods for working with Hibernate: creating Hibernate template, creating Hibernate session etc.
 * (getHibernateTemplate, getHibernateSession, closeHibernateSession)<br>
 * - getCompanyID: gets company ID of current user (this method is deprecated - use getCompanyID from AgnUtils)<br>
 * - getDefaultMediaType: gets default media type<br>
 * - allowed: checks user permission<br>
 * - getMessage: takes localized message<br>
 * - setNumberOfRows: initializes the number of rows to be shown in tables<br>
 * - getInitializedColumnWidthList: resets the width of columns<br>
 * - getHelpLanguage: gets the language to be used for online help<br>
 * - getSort: gets the sort for tables<br>
 * - putTargetGroupsInRequest: puts target groups from DB to request<br>
 * - putPreviewRecipientsInRequest: Puts admin- and test-recipients to request<br>
 * - resolveFormWidth: decides what width-state should have the page<br>
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.1 $ $Date: 2006/08/03 08:47:47 $
 */

public class StrutsActionBase extends ActionSupport {

    public static final int ACTION_LIST = 1;

    public static final int ACTION_VIEW = 2;

    public static final int ACTION_SAVE = 3;

    public static final int ACTION_NEW = 4;

    public static final int ACTION_DELETE = 5;

    public static final int ACTION_CONFIRM_DELETE = 6;

    public static final int ACTION_LAST = 6;

    protected DataSource agnDBPool=null;
    protected SessionFactory sf=null;

    /**
     * Gets the bean from application context
     *
     * @param name the name of bean
     * @return the bean object
     */
    public Object getBean(String name) {
        return getWebApplicationContext().getBean(name);
    }

    /**
     * Creates hibernate template using the session factory which is taken from application context.
     *
     * @return hibernate template
     */
    protected HibernateTemplate getHibernateTemplate() {
        SessionFactory factory=null;

        factory=(SessionFactory) getBean("sessionFactory");

        return getHibernateTemplate(factory);
    }

    /**
     * Creates hibernate template using the session factory
     *
     * @param factory the session factory object
     * @return new hibernate template
     */
    protected HibernateTemplate getHibernateTemplate(SessionFactory factory) {
        return new HibernateTemplate(factory);
    }

    /**
     * Creates Hibernate session using session factory.
     *
     * @param req servlet request object
     * @return new hibernate session
     */
    protected Session getHibernateSession(HttpServletRequest req) {
        Session aSession=null;

        if(sf==null) {
            sf=AgnUtils.retrieveSessionFactory(this.getServlet().getServletContext());
        }
        aSession=sf.openSession();
        aSession.enableFilter("companyFilter").setParameter("companyFilterID", new Integer(this.getCompanyID(req)));
        return aSession;
    }

    /**
     * Closes the hibernateSession.
     *
     * @param aSession the session object
     */
    protected void closeHibernateSession(Session aSession) {
        Connection dbConn=null;

        dbConn=aSession.close();
        try {
            dbConn.close();
        } catch(SQLException e) {
            AgnUtils.logger().error(e);
        }
    }

    /**
     * Gets company ID of current user
     *
     * @param req servlet request object
     * @return Value of property companyID.
     *
     * @see org.agnitas.util.AgnUtils.getCompanyID(HttpServletRequest)
     */
    @Deprecated
    public int getCompanyID(HttpServletRequest req) {

        int companyID=0;

        try {
			companyID = AgnUtils.getAdmin(req).getCompany().getId();
        } catch (Exception e) {
            AgnUtils.logger().error("no companyID found for the admin in session");
            companyID=0;
        }

        return companyID;
    }

    /**
     * Gets default mediatype from session. If it is not found in session - returns 0 by default
     *
     * @param req servlet request object
     * @return default mediatype
     */
    public int getDefaultMediaType(HttpServletRequest req) {

        int mtype=0;

        try {
            mtype=((Integer)req.getSession().getAttribute("agnitas.defaultMediaType")).intValue();
        } catch (Exception e) {
            AgnUtils.logger().error("no default mediatype");
            mtype=0;
        }

        return mtype;
    }

    /**
     * Checks if the user has the permission given.
     *
     * @param id permission token
     * @param req HTTP request object
     * @return true if user has permission, false if not
     */
    public static boolean allowed(String id, HttpServletRequest req) { // TODO: This method is used in other classes, too (like StrutsFormBase). Move to utility class
		Admin aAdmin = AgnUtils.getAdmin(req);

		if (aAdmin == null) {
            return false; //Nothing allowed if there is no permission set in Session
        }

        return aAdmin.permissionAllowed(id);
    }

    /**
     * Gets the message from messages file according to current user locale.
     *
     * @param key message key
     * @param req servlet request object
     * @return the message in needed locale
     */
    public String getMessage(String key, HttpServletRequest req) {
        return this.getMessageSourceAccessor().getMessage(key, (Locale)req.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY));
    }

    public StrutsActionBase() {
        super();
        //Protocol.registerProtocol("https", new Protocol("https", new EasySSLProtocolSocketFactory(), 443));
    }
    
    /**
     * Sets the number of rows to be shown in tables to the form. If the value is not set yet - takes the value from
     * user settings, if it is empty - takes default value (<code>StrutsFormBase.DEFAULT_NUMBER_OF_ROWS</code>)
     *
     * @param req servlet request object
     * @param aForm StrutsFormBase object
     */
	public void setNumberOfRows(HttpServletRequest req, StrutsFormBase aForm) {
		if( aForm.getNumberofRows() == -1 ) {
			int numberofrows = AgnUtils.getAdmin(req).getPreferredListSize();
			if( numberofrows == 0 ) {
				aForm.setNumberofRows(StrutsFormBase.DEFAULT_NUMBER_OF_ROWS);
			}else {
				aForm.setNumberofRows(numberofrows);
			}
		}
	}
	
    /**
     * Initialize the list which keeps the current width of the columns, with a default value of '-1'
     * A JavaScript in the corresponding jsp will set the style.width of the column.
     *
     * @param size number of columns
     * @return the list of column width
     */
    protected List<String> getInitializedColumnWidthList(int size) {
		List<String> columnWidthList = new ArrayList<String>();
		for ( int i=0; i< size ; i++ ) {
			columnWidthList.add("-1");
		}
		return columnWidthList;
	}
    
    /**
     * Gets the language which will be used for the online help. Method gets the list of available languages for help
     * and checks if admin language is contained in that list. If yes - returns that language, if not - returns "en"
     * (english language)
     *
     * @param req servlet request object
     * @return help language String value
     */
    protected String getHelpLanguage(HttpServletRequest req) {
		String helplanguage = "en";
        String availableHelpLanguages = (String) getBean("onlinehelp.languages");
        
        if( availableHelpLanguages != null ) {
        	Admin admin = AgnUtils.getAdmin(req);
        	StringTokenizer tokenizer = new StringTokenizer(availableHelpLanguages,",");
        	while (tokenizer.hasMoreTokens() ) {
        		String token = tokenizer.nextToken();
        		if( token.trim().equalsIgnoreCase( admin.getAdminLang()) ) {
        			helplanguage = token.toLowerCase();
        			break;
        		}        		
        	}
        }
		return helplanguage;
	}

    /**
     * Checks if sort property is contained in request, if yes - puts it also to form, if not - gets it from form;
     * returns the obtained sort property.
     *
     * @param request servlet request object
     * @param aForm StrutsFormBase object
     * @return String value of sort
     */
    protected String getSort(HttpServletRequest request, StrutsFormBase aForm) {
        String sort = request.getParameter("sort");
        if (sort == null) {
            sort = aForm.getSort();
        } else {
            aForm.setSort(sort);
        }
        return sort;
	}

    /**
     * Gets target groups of current company and puts it to request
     *
     * @param req servlet request object
     */
    protected void putTargetGroupsInRequest(HttpServletRequest req) {
		TargetDao tDao=(TargetDao) getBean("TargetDao");
		req.setAttribute("targetGroups", tDao.getTargets(this.getCompanyID(req), true));
	}
    
    /**
     * Gets the list of admin and test recipients (user_type 'A' and 'T') from database and puts it to request.
     *
     * @param request servlet request object
     * @param mailingID mailing id
     * @param companyID company id
     * @return the list of admin and test recipients
     */
    protected Map<Integer, String> putPreviewRecipientsInRequest(HttpServletRequest request, int mailingID, int companyID) {
        return putPreviewRecipientsInRequest(request, mailingID, companyID, null);
    }

    /**
     * Gets the list of admin and test recipients (user_type 'A' and 'T') from database and puts it to request.
     *
     * @param request servlet request object
     * @param mailingID mailing id
     * @param companyID company id
     * @param recipientDao RecipientDao object
     * @return the list of admin and test recipients
     */
    protected Map<Integer, String> putPreviewRecipientsInRequest(HttpServletRequest request, int mailingID, int companyID, RecipientDao recipientDao) {
        if (recipientDao == null) {
            recipientDao = (RecipientDao) getWebApplicationContext().getBean("RecipientDao");
        }
        Map<Integer, String> recipientList = recipientDao.getAdminAndTestRecipientsDescription(companyID, mailingID);
        request.setAttribute("previewRecipients", recipientList);

        return recipientList;
    }

    /**
     * If the page width is undefined - sets it to wide if user has appropriate permission, if not - sets it to normal.
     *
     * @param request servlet request object
     * @param aForm StrutsFormBase object
     */
    protected void resolveFormWidth(HttpServletRequest request, StrutsFormBase aForm){
        if (aForm.getExtendedWidthState() == StrutsFormBase.WIDTH_STATE_UNDEFINED) {
            if (AgnUtils.allowed("layout.supersize", request)) {
                aForm.setExtendedWidthState(StrutsFormBase.WIDTH_STATE_WIDE);
            } else {
                aForm.setExtendedWidthState(StrutsFormBase.WIDTH_STATE_NORMAL);
            }
        }
    }
    

}
