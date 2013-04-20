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
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.beans.UserForm;
import org.agnitas.dao.UserFormDao;
import org.agnitas.emm.core.commons.uid.ExtensibleUID;
import org.agnitas.emm.core.commons.uid.ExtensibleUIDConstants;
import org.agnitas.emm.core.commons.uid.ExtensibleUIDService;
import org.agnitas.emm.core.commons.uid.parser.exception.UIDParseException;
import org.agnitas.exceptions.FormNotFoundException;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.CaseInsensitiveMap;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;


/**
 * Implementation of <strong>Action</strong> that processes a form.do request
 *
 * @author mhe
 * @version $Revision: 1.1 $ $Date: 2006/08/03 08:47:47 $
 */

public class UserFormExecuteAction extends StrutsActionBase {
    
    private static final transient Logger logger = Logger.getLogger( UserFormExecuteAction.class);
    
    // --------------------------------------------------------- Public Methods
    // TimeoutLRUMap companys=new TimeoutLRUMap(AgnUtils.getDefaultIntValue("onepixel.keys.maxCache"), AgnUtils.getDefaultIntValue("onepixel.keys.maxCacheTimeMillis"));
    
    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     * <br>
     * Loads the requested user form into response context; executes requested form
     * evaluates user form end action, sends the html response. <br>
     * If used Oracle database, loads character encoding into response. <br>
     * If requested user form not found, sends error message into response.
     * <br><br>
     * @param form  ActionForm object
     * @param req   request
     * @param res   response
     * @param mapping The ActionMapping used to select this instance
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     * @return null
     */
    @Override
    public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest req,
            HttpServletResponse res)
            throws IOException, ServletException {

        // Validate the request parameters specified by the user
        ActionMessages errors = new ActionMessages();
        UserFormExecuteForm aForm=(UserFormExecuteForm)form;
        ActionForward destination=null;
        CaseInsensitiveMap<Object> params=new CaseInsensitiveMap<Object>();
        
        try {
            res.setBufferSize(65535);

            if ( AgnUtils.isOracleDB() ) {
            	res.setCharacterEncoding(req.getCharacterEncoding());
            }
	/* Daimler Hack */
           // res.setCharacterEncoding("utf-8");
            this.processUID(req, params, aForm.getAgnUseSession());
            params.put("requestParameters", AgnUtils.getReqParameters(req));
            params.put("_request", req);
            if((req.getParameter("requestURL")!=null) && (req.getParameter("queryString") != null)){
                params.put("formURL",req.getRequestURL() + "?"	+ req.getQueryString());
            }
            String responseContent ="";
            try {
            	responseContent=executeForm(aForm, params, req, errors);
            } catch (FormNotFoundException formNotFoundEx) {
            	destination = handleFormNotFound(mapping, req, res, params);
            }
            if (params.get("_error") == null) {
                this.evaluateFormEndAction(aForm, params);
                responseContent = handleEndActionErrors(aForm, params, responseContent);
            }
            sendFormResult(res, params, responseContent);
        } catch (Exception e) {
            logger.error( "execute()", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }
        
        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
            return new ActionForward(mapping.getInput());
        }
        
        return destination;
    }

    /**
     * For user form that requires error handling adds the error messages (including velocity errors) to response
     * context.
     * @param aForm
     * @param params
     * @param responseContent html content to be sent in response (could be changed inside the method).
     * @return responseContent
     * @throws Exception 
     */

    protected String handleEndActionErrors(UserFormExecuteForm aForm, CaseInsensitiveMap<Object> params, String responseContent) throws Exception {
        UserFormDao daoUserForm = (UserFormDao) getBean("UserFormDao");
        UserForm aUserForm = daoUserForm.getUserFormByName(aForm.getAgnFN(), aForm.getAgnCI());
        if (aUserForm != null && aUserForm.isSuccessUseUrl()) {
            // no error handling, return original content
            return responseContent;
        }
        if (params.get("velocity_error") != null) {
            responseContent += "<br/><br/>";
            responseContent += params.get("velocity_error");
        }
        if (params.get("errors") != null) {
            responseContent += "<br/>";
            ActionErrors velocityErrors = ((ActionErrors) params.get("errors"));
            @SuppressWarnings("rawtypes")
			Iterator it = velocityErrors.get();
            while (it.hasNext()) {
                responseContent += "<br/>" + it.next();
            }
        }
        return responseContent;
    }

    /**
     * Sends responce with execution form result.
     * @param res  response
     * @param params
     * @param responseContent html content to be sent in response
     * @throws IOException
     */
    protected void sendFormResult(HttpServletResponse res, Map<String, Object> params, String responseContent) throws IOException {
        Boolean redirectParam = (Boolean) params.get(UserForm.TEMP_REDIRECT_PARAM);
        if (redirectParam != null && redirectParam) {
            res.sendRedirect(responseContent);
        } else {
            if (params.get("responseRedirect") != null) {
                res.sendRedirect((String) params.get("responseRedirect"));
            } else {
                String responseMimetype = "text/html";
                if (params.get("responseMimetype") != null) {
                    responseMimetype = (String) params.get("responseMimetype");
                }
                res.setContentType(responseMimetype);

                PrintWriter out = res.getWriter();
                out.print(responseContent);
                out.flush();
                // don't close output for error handling etc. to come
            }
        }
        res.flushBuffer();
    }

    /**
     * Sends response with error message in case of no requested user form found.
     * @param mapping ActionMapping
     * @param request  request
     * @param res  response
     * @param param user form parameters
     * @return null
     * @throws IOException
     */
    protected ActionForward handleFormNotFound(ActionMapping mapping, HttpServletRequest request, HttpServletResponse res, Map<String, Object> param) throws IOException {
    	sendFormResult(res, param, "form not found");
    	
    	return null;
    }

    /** Execute the requested form.
     * Reads the form defined by aForm.getAgnFN() and aForm.getAgnCI() from the
     * database and executes it.
     * @param aForm form info.
     * @param params a map containing the form values.
     * @param req the ServletRequest, used to get the ApplicationContext.
     * @param errors used to sotre error descriptions.
     * @return html content for sending in response
     * @throws Exception 
     */  
    protected String executeForm(UserFormExecuteForm aForm, Map<String, Object> params, HttpServletRequest req, ActionMessages errors) throws Exception {
        String result = "no parameters";
        UserFormDao dao=(UserFormDao) getBean("UserFormDao");
        UserForm aUserForm=dao.getUserFormByName(aForm.getAgnFN(), aForm.getAgnCI());
        
        if(aUserForm!=null) {
            result=aUserForm.evaluateForm(this.getWebApplicationContext(), params);
        } else {
            throw new FormNotFoundException();
        }
        
        return result;
    }
    
    /** Execute the end action of the requested form.
     * Reads the form defined by aForm.getAgnFN() and aForm.getAgnCI() from the
     * database and executes it's end action.
     * @param aForm form info.
     * @param params a map containing the form values.
     * @return true==success
     *         false==error
     * @throws Exception 
     */  
    protected boolean evaluateFormEndAction(UserFormExecuteForm aForm, Map<String, Object> params) throws Exception {
        
        UserFormDao dao=(UserFormDao) getBean("UserFormDao");
        UserForm aUserForm=dao.getUserFormByName(aForm.getAgnFN(), aForm.getAgnCI());
        
        if(aUserForm == null || aUserForm.getEndActionID()==0) {
            return false;
        }
        
        return aUserForm.evaluateEndAction(this.getWebApplicationContext(), params);
    }
   
    /* information from a given url. 
     * Parses an url and returns the retrieved values in a hash.
     * @param req ServletRequest, used to get the Session.
     * @param params HashMap to store the retrieved values in.
     * @param useSession also store the result in the session if this is not 0.
     */ 
    @SuppressWarnings("unchecked")
	public void processUID(HttpServletRequest req, Map<String, Object> params, int useSession) {
        ExtensibleUID uid=null;
        int compID = 0;
        String par=req.getParameter("agnUID");

        if(par!=null) {
            uid=this.decodeTagString(par);
        }
        
        if(req.getParameter("agnCI") != null) {
        	try {
        		compID = Integer.parseInt(req.getParameter("agnCI"));
        	} catch( NumberFormatException e) {
        		compID = 0;
        	}
        }
        
        if(uid!=null) {
        	if(compID == uid.getCompanyID()) {
        		params.put("customerID", uid.getCustomerID());
        		params.put("mailingID", uid.getMailingID());
        		params.put("urlID", uid.getUrlID());
        		params.put("agnUID", par);
        		params.put("companyID", compID);
        		params.put("locale", req.getLocale());

        		if(useSession!=0) {
        			CaseInsensitiveMap<Object> tmpPars=new CaseInsensitiveMap<Object>();
        			tmpPars.putAll(params);
        			req.getSession().setAttribute("agnFormParams", tmpPars);
        			params.put("sessionID", req.getSession().getId());
        		}
        	}
        } else {
            if(useSession!=0) {
                if(req.getSession().getAttribute("agnFormParams")!=null){
                    params.putAll((Map<String, Object>)req.getSession().getAttribute("agnFormParams"));
                }
            }
        }
    }
   
    /** Use a tag to get a UID.
     * Retrieves a UID according to a given tag.
     * @param tag a string defining the uid.
     * @return the resulting UID.
     */
    public ExtensibleUID decodeTagString(String tag) {
        ExtensibleUID uid=null;
        
        ExtensibleUIDService uidService = (ExtensibleUIDService) this.getWebApplicationContext().getBean( ExtensibleUIDConstants.SERVICE_BEAN_NAME);
                
        try {
            try {
        	uid = uidService.parse( tag);
            } catch( UIDParseException e) {
        	logger.warn("error paring UID: " + tag);
        	logger.debug( e);
            }
        } catch (Exception e) {
            logger.error( "decodeTagString()", e);
        }
        
        return uid;
    }

}
