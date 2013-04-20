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

import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.agnitas.actions.EmmAction;
import org.agnitas.beans.Company;
import static org.agnitas.beans.Company.STATUS_ACTIVE;
import org.agnitas.dao.CompanyDao;
import org.agnitas.dao.EmmActionDao;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.OnepixelDao;
import org.agnitas.emm.core.commons.uid.ExtensibleUID;
import org.agnitas.emm.core.commons.uid.ExtensibleUIDConstants;
import org.agnitas.emm.core.commons.uid.ExtensibleUIDService;
import org.agnitas.emm.core.commons.uid.parser.exception.UIDParseException;
import org.agnitas.util.AgnUtils;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * The servlet is used for registration of opening emails have been sent to customers and loading one pixel gif image
 * that is automatically adding into content of each OpenEMM mailing.
 * Each time the customer opens the email, the request is sent to the server, the data about the mailing and customer
 * are logged in database and the server returns http response that contains one pixel gif image.
 */
public class OnePixelCount extends HttpServlet {
    private static final transient Logger logger = Logger.getLogger( OnePixelCount.class);
    
	private static final long serialVersionUID = -3837933074485365451L;
	protected byte[] onePixelGif={71 ,73 ,70 ,56 ,57 ,97 ,1 ,0 ,1 ,0 ,-128 ,-1 ,0 ,-64 ,-64 ,-64 ,0 ,0 ,0 ,33 ,-7 ,4 ,1 ,0 ,0 ,0 ,0 ,44 ,0 ,0 ,0 ,0 ,1 ,0 ,1 ,0 ,0 ,2 ,2 ,68 ,1 ,0 ,59};
	private static Company	cachedCompany=null;

    /**
     * Gets company data from database and stores it in cachedCompany variable.
     * @param companyID
     * @return
     */
	protected Company	getCompany(int companyID) {
		if(cachedCompany == null) {
			ApplicationContext con=WebApplicationContextUtils.getWebApplicationContext(getServletContext());
			CompanyDao cDao=(CompanyDao)con.getBean("CompanyDao");

			cachedCompany=cDao.getCompany(companyID);
		}
		return cachedCompany;
	}

    /**
     * Servlet service-method, is invoked on calling the servlet.
     * Parses data from uid parameter (company id, mailing id and customer id),
     * stores the data from the request in database,
     * writes one pixel gif image into response.
     * Also executes mailing open action, if the mailing has one.
     * Returns nothing if the company is not in status "active" or if some execution error occurs.
     * @param req HTTP request; should contain "uid" parameter with values of company id, mailing id and customer id
     * @param res HTTP response, contains one pixel gif image
     * @throws IOException if an input/output error occurs
     * @throws ServletException if a servlet exception occurs
     */
	@Override
	public void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		ApplicationContext con=WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
		String param=null;
		OnepixelDao pixelDao=(OnepixelDao)con.getBean("OnepixelDao");
		
		ExtensibleUIDService uidService = (ExtensibleUIDService) con.getBean( ExtensibleUIDConstants.SERVICE_BEAN_NAME);

		// send gif to Browser.
		res.setContentType("image/gif");
		OutputStream out=res.getOutputStream();
		out.write(onePixelGif);
		out.close();

		param=req.getParameter("uid");
		if(param == null) {
		    	logger.error(  "service: no uid set");
			return;
		}

		try {
			// validate uid
			ExtensibleUID uid = null;
			Company company = null;
			
			try {
			    uid = uidService.parse( param);
			} catch( UIDParseException e) {
			    logger.warn("Error parsing UID: " + param + " (" + e.getMessage() + ")");
			    logger.debug( e);
			}
			
			if(uid == null || uid.getCompanyID()==0) {
				return;
			}
            
			company=getCompany((int)uid.getCompanyID());

			if(company == null ) {
				logger.error("Company with ID: "+ uid.getCompanyID()+ " not found ");
				return;
			}			

			/*
			// TODO: Create a separate Helper class to validate the old UIDs
			if(uid.validateUID(company.getSecret())==false) {
				logger.warn("uid invalid: " + param);
				return;
			}
			*/

			if(!STATUS_ACTIVE.equals(company.getStatus())){
				return;
			}

			persistLog(req, pixelDao, uid);
            executeMailingOpenAction(uid, con, req);
			
		} catch (Exception e) {
			logger.error(e);
			return;
		}
	}

    /**
     * Calls writePixelLogToDB method and logs to console in case of its successful execution.
     * @param req  HTTP request
     * @param pixelDao  OnePixelDao object
     * @param uid  ExtensibleUID object, contains parsed data from the "uid" request parameter
     */
	protected void persistLog(HttpServletRequest req, OnepixelDao pixelDao, ExtensibleUID uid) {
		if(writePixelLogToDB(req, pixelDao, uid)) {
			logger.info("Onepixel: cust: "+uid.getCustomerID()+" mi: "+uid.getMailingID()+" ci: "+uid.getCompanyID());
		}
	}

    /**
     * Stores opened email data (company id, mailing id, customer id, client IP address) in database.
     * @param req HTTP request, is used fot getting the IP address of the client that sent the request
     * @param pixelDao OnePixelDao object
     * @param uid ExtensibleUID object, contains parsed data from the "uid" request parameter
     * @return  true==success
     *          false==error
     */
	protected boolean writePixelLogToDB(HttpServletRequest req, OnepixelDao pixelDao, ExtensibleUID uid) {
    		return pixelDao.writePixel( uid.getCompanyID(), uid.getCustomerID(), uid.getMailingID(), req.getRemoteAddr());
    	}

    /**
     * Gets the id of action to be exequted on opening the mailing; if the action id > 0, executes the action with
     * parameters from the request.
     * @param uid ExtensibleUID object, contains parsed data from the "uid" request parameter
     * @param context application context
     * @param req  HTTP request
     */
    protected void executeMailingOpenAction(ExtensibleUID uid, ApplicationContext context, HttpServletRequest req){
        MailingDao mailingDao = (MailingDao) context.getBean("MailingDao");
        EmmActionDao actionDao = (EmmActionDao) context.getBean("EmmActionDao");
        int companyID = uid.getCompanyID();
        int mailingID = uid.getMailingID();
        int customerID = uid.getCustomerID();
        int openActionID = mailingDao.getMailingOpenAction(mailingID, companyID);
        if(openActionID != 0){
            EmmAction emmAction = actionDao.getEmmAction(openActionID, companyID);
            if(emmAction != null){
                // execute configured actions
                CaseInsensitiveMap params = new CaseInsensitiveMap();
                params.put("requestParameters", AgnUtils.getReqParameters(req));
                params.put("_request", req);
                params.put("customerID", customerID);
                params.put("mailingID", mailingID);
                emmAction.executeActions(context, params);
            }
        }
    }
}
