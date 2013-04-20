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
import java.io.Serializable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.agnitas.actions.ActionOperation;
import org.agnitas.beans.BindingEntry;
import org.agnitas.beans.Mailing;
import org.agnitas.beans.Recipient;
import org.agnitas.dao.MailingDao;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Martin Helff
 */
public class ActivateDoubleOptIn extends ActionOperation implements Serializable {
    
    static final long serialVersionUID = 7821487725578716506L;
    
    /** 
     * Creates new ActionOperationUpdateCustomer 
     */
    public ActivateDoubleOptIn() {
    }
    
    /**
     * Reads an Object and puts the read fields into allFields
     * throws IOException or ClassNotFoundException
     *
     * @param in inputstream from Object
     */
    private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException {
    }
    
    /**
     * Checks if customer id and mailing id are filled
     * Checks user status
     *
     * @return true==sucess
     * false=error
     * @param con
     * @param companyID
     * @param params HashMap containing all available informations
     */
    public boolean executeOperation(ApplicationContext con, int companyID, Map<String, Object> params) {
        int customerID=0;
        int mailingID=0;
        Integer tmpNum=null;
        Recipient aCust=(Recipient)con.getBean("Recipient");
        boolean returnValue=false;
        HttpServletRequest request=(HttpServletRequest)params.get("_request");
        Mailing aMailing=null;

        aCust.setCompanyID(companyID);
        if(params.get("customerID")!=null) {
            tmpNum=(Integer)params.get("customerID");
            customerID=tmpNum.intValue();
        }
        
        if(params.get("mailingID")!=null) {
            tmpNum=(Integer)params.get("mailingID");
            mailingID=tmpNum.intValue();
        }
        
        if(customerID!=0 && mailingID!=0) {
            aCust.setCustomerID(customerID);
            aCust.loadCustDBStructure();
            aCust.loadAllListBindings();
            
            MailingDao mailingDao=(MailingDao)con.getBean("MailingDao");
            aMailing=mailingDao.getMailing(mailingID, companyID);
            
            int mailinglistID=aMailing.getMailinglistID();
            Map<Integer, Map<Integer, BindingEntry>> aTbl = aCust.getListBindings();
            
            if (aTbl.containsKey(mailinglistID)) {
            	Map<Integer, BindingEntry> aTbl2 = aTbl.get(mailinglistID);
                if (aTbl2.containsKey(BindingEntry.MEDIATYPE_EMAIL)) {
                    BindingEntry aEntry = aTbl2.get(BindingEntry.MEDIATYPE_EMAIL);
                    switch(aEntry.getUserStatus()) {
                        case BindingEntry.USER_STATUS_WAITING_FOR_CONFIRM:
                            aEntry.setUserStatus(BindingEntry.USER_STATUS_ACTIVE);
                            aEntry.setUserRemark("Opt-In-IP: " + request.getRemoteAddr());
                            aEntry.updateStatusInDB(companyID);
                            returnValue=true;
                            break;
                            
                        case BindingEntry.USER_STATUS_ACTIVE:
                            returnValue=true;
                            break;
                            
                        default:
                            returnValue=false;
                    }
                }
            }
        }
        return returnValue;
    }
}
