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

/*
 * WebServiceBase.java
 *
 * Created on 29. Juli 2003, 13:17
 */

package org.agnitas.webservice;

import org.agnitas.util.AgnUtils;
import org.agnitas.util.SafeString;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;
import org.springframework.remoting.jaxrpc.ServletEndpointSupport;

import javax.servlet.GenericServlet;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author  mhe
 */
public class WebServiceBase extends ServletEndpointSupport {
    
    protected DataSource agnDBPool=null;
    protected Log log=LogFactory.getLog("com.agnitas.webservice.LogHandler");
    
    protected Connection getConnection(MessageContext msct) {
        Connection dbConn=null;
        
        GenericServlet aServlet=(GenericServlet)msct.getProperty("transport.http.servlet");
        
        if(agnDBPool==null) {
            agnDBPool=AgnUtils.retrieveDataSource(aServlet.getServletContext());
        }
        
        try {
            dbConn=agnDBPool.getConnection();
        } catch(Exception e) {
            dbConn=null;
        }
        return dbConn;
    }
    
    protected void freeConnection(Connection dbConn) {
        if(dbConn!=null) {
            try {
                dbConn.close();
            } catch(Exception e) {
                // do nothing
                AgnUtils.logger().info("could not close connection");
            }
        }
    }
    
    protected boolean authenticateUser(MessageContext msct, String user, String pwd, int companyID) {
        boolean result=false;
        Connection dbConn=this.getConnection(msct);
        Statement agnStatement=null;
        ResultSet rset=null;
        
        try {
            agnStatement=dbConn.createStatement();
            rset=agnStatement.executeQuery("select a.ws_admin_id from ws_admin_tbl a where a.username='"+SafeString.getSQLSafeString(user)+"' and a.password='"+SafeString.getSQLSafeString(pwd)+"'");
            if(rset!=null && rset.next()) {
                result=true;
            } else {
                result=false;
                HttpServletRequest req=(HttpServletRequest)msct.getProperty("transport.http.servletRequest");
                log.info(req.getRemoteAddr()+" -0-l: login failed: "+user+" "+companyID);
            }
        } catch (Exception e) {
            AgnUtils.logger().info("soap authentication: "+e);
            result=false;
        }
        
        try {
            rset.close();
        } catch (Exception e) {
            AgnUtils.logger().info("soap authentication: "+e);
            result=false;
        }
        
        try {
            agnStatement.close();
        } catch (Exception e) {
            AgnUtils.logger().info("soap authentication: "+e);
            result=false;
        }
        
        this.freeConnection(dbConn);
        
        return result;
    }
}
