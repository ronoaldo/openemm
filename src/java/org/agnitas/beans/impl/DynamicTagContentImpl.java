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

package org.agnitas.beans.impl;

import org.agnitas.beans.DynamicTagContent;

/**
 *
 * @author Martin Helff 
 */
public class DynamicTagContentImpl implements DynamicTagContent, java.io.Serializable {

    protected int mailingID;
    protected int companyID;
    protected int dynNameID;
    protected int id;
    protected String dynName;
    protected int dynOrder;
    protected int targetID;
    protected String dynContent;
    
    public static int WITHOUT_CLOB_CONTENT=0;
    public static int WITH_CLOB_CONTENT=1;
    
    /** Creates new DynamicTagContent */
    public DynamicTagContentImpl() {
    }
    
    public void setDynNameID(int id) {
        dynNameID=id;
    }

    public void setId(int id) {
        this.id=id;
    }

    public void setDynName(String name) {
        dynName=name;
    }

    public void setDynContent(String content) {
        dynContent=content;
    }
    
    public void setCompanyID(int id) {
        companyID=id;
    }

    public void setMailingID(int id) {
        mailingID=id;
    }

    public void setDynOrder(int id) {
        dynOrder=id;
    }

    public void setTargetID(int tid) {
        targetID=tid;
    }
    
    public int getDynOrder() {
        return dynOrder;
    }
    
    public int getDynNameID() {
        return dynNameID;
    }

    public int getId() {
        return this.id;
    }

    public String getDynName() {
        return dynName;
    }
    
    public String getDynContent() {
        if(dynContent == null) {
            dynContent = "";
        }
        return dynContent;
    }
    
    public int getTargetID() {
        return targetID;
    }
        
    public boolean equals(Object obj) {
    	if( obj == null) // According to Object.equals(Object), equals(null) returns false
    		return false;
    	
        return ((DynamicTagContent)obj).hashCode()==this.hashCode();
    }

    public int hashCode() {
        return this.getDynContent().hashCode();
    }

    /**
     * Getter for property mailingID.
     * @return Value of property mailingID.
     */
    public int getMailingID() {
        return this.mailingID;
    }

    /**
     * Getter for property companyID.
     * @return Value of property companyID.
     */
    public int getCompanyID() {
        return this.companyID;
    }
    
}

