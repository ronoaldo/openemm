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

package org.agnitas.actions.ops;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Map;

import org.agnitas.actions.ActionOperation;
import org.agnitas.beans.Mailing;
import org.agnitas.dao.MailingDao;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Martin Helff
 */
public class SendMailing extends ActionOperation implements Serializable {
	private static final transient Logger logger = Logger.getLogger(SendMailing.class);
    
    static final long serialVersionUID = 712043294800920235L;
    
    /** 
     * Holds value of property mailingID. 
     */
    protected int mailingID;
    
    /**
     * Holds value of property delayMinutes. 
     */
    protected int delayMinutes;
    
    /**
     * Creates new ActionOperationSendMailing 
     */
    public SendMailing() {
    }
    
    /** Getter for property mailingID.
     *
     * @return Value of property mailingID.
     */
    public int getMailingID() {
        return mailingID;
    }
    
    /** Setter for property mailingID.
     *
     * @param mailingID New value of property mailingID.
     */
    public void setMailingID(int mailingID) {
        this.mailingID = mailingID;
    }
     
    /**
     * Reads an Object and puts the read fields into allFields
     * Gets mailing id from allFields
     * Tries to get minutes of delay from allFields
     * throws IOException or ClassNotFoundException
     *
     * @param in inputstream from Object
     */
    private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField allFields=null;
        
        allFields=in.readFields();
        mailingID=allFields.get("mailingID", 0);
        try {
            delayMinutes=allFields.get("delayMinutes", 0);
        } catch (Exception e) {
        	logger.error("readObject: "+e);
        }
    }
    
    /** Getter for property delayMinutes.
     *
     * @return Value of property delayMinutes.
     */
    public int getDelayMinutes() {
        return this.delayMinutes;
    }
    
    /** Setter for property delayMinutes.
     *
     * @param delayMinutes New value of property delayMinutes.
     */
    public void setDelayMinutes(int delayMinutes) {
        this.delayMinutes = delayMinutes;
    }
    
    /**
     * Checks if customer id, mailing id and user status are filled
     * Sends mailing
     * Logges status (sent or failed)
     *
     * @return true==sucess
     * false=error
     * @param con
     * @param companyID
     * @param params HashMap containing all available informations
     */
    @Override
    public boolean executeOperation(ApplicationContext con, int companyID, Map<String, Object> params) {
        int customerID=0;
        Integer tmpNum=null;
        Mailing aMailing=null;
        MailingDao mDao=(MailingDao)con.getBean("MailingDao");
        boolean exitValue=true;
        String userStatus=null;

        if(params.get("customerID")==null) {
            return false;
        }
        tmpNum=(Integer)params.get("customerID");
        customerID=tmpNum.intValue();
        
        if(params.get("mailingID")!=null) {
            tmpNum=(Integer)params.get("mailingID");
        }
        
        if(params.get("__agn_USER_STATUS")!=null) {
            userStatus=(String)params.get("__agn_USER_STATUS");
        }
        
        aMailing=mDao.getMailing(this.mailingID, companyID);
        if(aMailing!=null) {
            if(aMailing.sendEventMailing(customerID, delayMinutes, userStatus, null, con)) {
            	if (logger.isInfoEnabled()) logger.info("executeOperation: Mailing "+mailingID+" to "+customerID+" sent");
                exitValue=true;
            } else {
            	logger.error("executeOperation: Mailing "+mailingID+" to "+customerID+" failed");
                exitValue=false;
            }
        }
        return exitValue;
    }
    
}
