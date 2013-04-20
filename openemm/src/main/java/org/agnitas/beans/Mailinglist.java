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

package org.agnitas.beans;

import java.io.Serializable;

/**
 * 
 * @author Eduard Scherer
 */
public interface Mailinglist extends Serializable {

   /**
     * Setter for property companyID.
     *
     * @param id New value of propety companyID.
     */
    void setCompanyID(int id);
    
    /**
     * Setter for property id.
     *
     * @param id New value of propety id.
     */
    void setId(int id);
    
    /**
     * Setter for property shortname.
     *
     * @param shortname New value of propety shortname.
     */
    void setShortname(String shortname);
    
    /**
     * Setter for property description.
     *
     * @param description New value of propety description.
     */
    void setDescription(String description);
    
     /**
     * Getter for property companyID.
     *
     * @return Value of property companyID.
     */
    int getCompanyID();
    
     /**
     * Getter for property id.
     *
     * @return Value of property id.
     */
    int getId();
    
     /**
     * Getter for property shortname.
     *
     * @return Value of property shortname.
     */
    String getShortname();
    
     /**
     * Getter for property description.
     *
     * @return Value of property description.
     */
    String getDescription();
    
}