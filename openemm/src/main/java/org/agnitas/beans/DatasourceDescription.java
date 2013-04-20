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

import java.util.Date;

public interface DatasourceDescription {
    /**
     * Setter for property id.
     *
     * @param id New value of property id.
     */
    public void setId(int id);

    /**
     * Setter for property companyID.
     *
     * @param id New value of property companyID.
     */
    public void setCompanyID(int id);
    
    /**
     * Setter for property sourcegroupID.
     *
     * @param title New value of property sourcegroupID.
     */
    public void setSourcegroupID(int title);
    
    /**
     * Setter for property description.
     *
     * @param desc New value of property description.
     */
    public void setDescription(String desc);

    /**
     * Setter for property creationDate.
     *
     * @param creationDate New value of property creationDate.
     */
    public void setCreationDate(Date creationDate);

    /**
     * Getter for property id.
     * 
     * @return Value of property id.
     */
    public int getId();

    /**
     * Getter for property companyID.
     * 
     * @return Value of property companyID.
     */
    public int getCompanyID();
    
    /**
     * Getter for property sourcegroupID.
     * 
     * @return Value of property sourcegroupID.
     */
    public int getSourcegroupID();
    
    /**
     * Getter for property description.
     * 
     * @return Value of property description.
     */
    public String getDescription();

    /**
     * Getter for property creationDate.
     * 
     * @return Value of property creationDate.
     */
    public Date getCreationDate();
}
