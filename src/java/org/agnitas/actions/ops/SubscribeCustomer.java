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
import java.util.HashMap;
import java.util.Map;

import org.agnitas.actions.ActionOperation;
import org.agnitas.beans.Company;
import org.agnitas.beans.Recipient;
import org.agnitas.dao.CompanyDao;
import org.agnitas.emm.core.commons.uid.ExtensibleUID;
import org.agnitas.emm.core.commons.uid.ExtensibleUIDConstants;
import org.agnitas.emm.core.commons.uid.ExtensibleUIDService;
import org.agnitas.util.AgnUtils;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Martin Helff
 */
public class SubscribeCustomer extends ActionOperation implements Serializable {
    
    static final long serialVersionUID = 3086814575002603882L;
    
    /**
     * Holds value of property doubleCheck.
     */
    protected boolean doubleCheck=true;
    
    /**
     * Holds value of property keyColumn.
     */
    protected String keyColumn="email";
    
    /**
     * Holds value of property doubleOptIn.
     */
    protected boolean doubleOptIn=false;
    
    /** 
     * Creates new ActionOperationUpdateCustomer 
     */
    public SubscribeCustomer() {
    }
    
    /**
     * Reads an Object and puts the read fields into allFields
     * Gets keyColumn, doubleCheck and doubleoptIn from allFields
     * throws IOException or ClassNotFoundException
     *
     * @param in inputstream from Object
     */
    private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField allFields=null;
        
        allFields=in.readFields();
        this.keyColumn=(String)allFields.get("keyColumn", "email");
        this.doubleCheck=allFields.get("doubleCheck", true);
        this.doubleOptIn=allFields.get("doubleOptIn", false);
    }
    
    /**
     * Checks if subscription of customer is blocked
     * Checks customer
     * Checks blacklist
     * Creats user information
     * 
     * @return true==sucess
     * false=error
     * @param con 
     * @param companyID 
     * @param params HashMap containing all available informations
     */
    public boolean executeOperation(ApplicationContext con, int companyID, Map params) {
        Integer tmpNum=null;
        Recipient aCust=(Recipient)con.getBean("Recipient");
        String keyVal=null;
        boolean isNewCust=false;
        boolean identifiedByUid=false;
        
        if(params.get("subscribeCustomer")!=null && params.get("subscribeCustomer").equals("no")) {
            return true; // do nothing, manually blocked
        }
        
        aCust.setCompanyID(companyID);
        aCust.loadCustDBStructure();

        if(params.get("customerID")!=null) {
            tmpNum=(Integer)params.get("customerID");
            aCust.setCustomerID(tmpNum.intValue());
            identifiedByUid=true;
        }

        if(aCust.getCustomerID()==0) {
            if(this.doubleCheck) {
                Map req=new CaseInsensitiveMap((HashMap)params.get("requestParameters"));
                keyVal=(String)(req).get(this.keyColumn);
                aCust.findByKeyColumn(this.keyColumn, keyVal);
            }
        }
        
        if(aCust.getCustomerID()!=0) {
            aCust.getCustomerDataFromDb();
        } else {
            isNewCust=true;
        }
        
        
        
        /* copy the request parameters into the customer */
		if(!aCust.importRequestParameters((HashMap)params.get("requestParameters"), null)) {
			return false;
		}
		
        /* is the email valid and not blacklisted? */
		if(!aCust.emailValid() || aCust.blacklistCheck()) {
			return false;	// abort, EMAIL is not allowed
		}
        
        if(!aCust.updateInDB()) {  // return error on failure
            return false;
        }
        
        aCust.loadAllListBindings();
        aCust.updateBindingsFromRequest(params, this.doubleOptIn, identifiedByUid);
        
        if(this.doubleOptIn) {
            params.put("__agn_USER_STATUS", "5"); // next Event-Mailing goes to a user with status 5
        }
        
        params.put("customerID", new Integer(aCust.getCustomerID()));

        if(isNewCust && aCust.getCustomerID()!=0) {
            // generate new agnUID
            try {
        	ExtensibleUIDService uidService = (ExtensibleUIDService) con.getBean( ExtensibleUIDConstants.SERVICE_BEAN_NAME);
        	ExtensibleUID uid = uidService.newUID();
                uid.setCompanyID(companyID);
                uid.setCustomerID(aCust.getCustomerID());
                CompanyDao dao=(CompanyDao)con.getBean("CompanyDao");
                Company company=dao.getCompany(companyID);
                uid.setUrlID(0);
                uid.setMailingID(0);
                params.put("agnUID", uidService.buildUIDString( uid));
            } catch (Exception e) {
                AgnUtils.logger().error("executeOperation: "+e);
            }
        }
        
        return true;
    }
    
    /**
     * Getter for property doubleCheck.
     *
     * @return Value of property doubleCheck.
     */
    public boolean isDoubleCheck() {
        return this.doubleCheck;
    }
    
    /**
     * Setter for property doubleCheck.
     *
     * @param doubleCheck New value of property doubleCheck.
     */
    public void setDoubleCheck(boolean doubleCheck) {
        this.doubleCheck = doubleCheck;
    }
    
    /**
     * Getter for property keyColumn.
     *
     * @return Value of property keyColumn.
     */
    public String getKeyColumn() {
        return this.keyColumn;
    }
    
    /**
     * Setter for property keyColumn.
     *
     * @param keyColumn New value of property keyColumn.
     */
    public void setKeyColumn(String keyColumn) {
        this.keyColumn = keyColumn;
    }
    
    /**
     * Getter for property doubleOptIn.
     *
     * @return Value of property doubleOptIn.
     */
    public boolean isDoubleOptIn() {
        return this.doubleOptIn;
    }
    
    /**
     * Setter for property doubleOptIn.
     *
     * @param doubleOptIn New value of property doubleOptIn.
     */
    public void setDoubleOptIn(boolean doubleOptIn) {
        this.doubleOptIn = doubleOptIn;
    }
    
}
