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
import org.agnitas.beans.Recipient;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Martin Helff
 */
public class GetCustomer extends ActionOperation implements Serializable {
    
    static final long serialVersionUID = -7318143901798712109L;
    
    /**
     * Holds value of property loadAlways.
     */
    protected boolean loadAlways;
    
    /** 
     * Creates new ActionOperationUpdateCustomer
     */
    public GetCustomer() {
    }
    
    /**
     * Reads an Object and puts the read fields into allFields
     * throws IOException or ClassNotFoundException
     *
     * @param in inputstream from Object
     */
    private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField allFields=null;
        
        allFields=in.readFields();
        this.loadAlways=allFields.get("loadAlways", false);
    }
    
    /**
     * Checks if customerID is filled
     * Fills customer data and customer bindings into params
     *
     * @return true==sucess
     * false=error
     * @param con
     * @param companyID
     * @param params HashMap containing all available informations
     */
    public boolean executeOperation(ApplicationContext con, int companyID, Map<String, Object> params) {
        int customerID=0;
        Integer tmpNum=null;
        Recipient aCust=(Recipient)con.getBean("Recipient");
        boolean returnValue=false;
        
        aCust.setCompanyID(companyID);
        if(params.get("customerID")!=null) {
            tmpNum=(Integer)params.get("customerID");
            customerID=tmpNum.intValue();
        }
        
        if(customerID!=0) {
            aCust.setCustomerID(customerID);
            aCust.loadCustDBStructure();
            aCust.getCustomerDataFromDb();
            aCust.loadAllListBindings();
            if(this.loadAlways || aCust.isActiveSubscriber()) {
                if(!aCust.getCustParameters().isEmpty()) {
                    params.put("customerData", aCust.getCustParameters());
                    params.put("customerBindings", aCust.getListBindings());
                    returnValue=true;
                }
            }
        }
        
        return returnValue;
    }
    
    /**
     * Getter for property loadAlways.
     *
     * @return Value of property loadAlways.
     */
    public boolean isLoadAlways() {
        return this.loadAlways;
    }
    
    /**
     * Setter for property loadAlways.
     *
     * @param loadAlways New value of property loadAlways.
     */
    public void setLoadAlways(boolean loadAlways) {
        this.loadAlways = loadAlways;
    }
}
