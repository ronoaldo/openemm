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

package org.agnitas.target;

import org.springframework.context.ApplicationContext;

import bsh.Interpreter;

import java.sql.Timestamp;

/**
 *
 * @author mhe
 */
public interface Target {

    /**
     * Getter for property companyID.
     * 
     * @return Value of property companyID.
     */
    int getCompanyID();

    /**
     * Getter for property description.
     * 
     * @return Value of property description.
     */
    String getTargetDescription();

    /**
     * Getter for property id.
     * 
     * @return Value of property id.
     */
    int getId();

    /**
     * Getter for property targetName.
     * 
     * @return Value of property targetName.
     */
    String getTargetName();

    /**
     * Getter for property targetSQL.
     * 
     * @return Value of property targetSQL.
     */
    String getTargetSQL();

    /**
     * Getter for property targetStructure.
     * 
     * @return Value of property targetStructure.
     */
    TargetRepresentation getTargetStructure();

    /**
     * Getter for property customerInGroup.
     * 
     * @return Value of property customerInGroup.
     */
    boolean isCustomerInGroup(Interpreter aBsh);

     /**
     * Getter for property customerInGroup.
     * 
     * @return Value of property customerInGroup.
     */
    boolean isCustomerInGroup(int customerID, ApplicationContext con);

    /**
     * Setter for property companyID.
     * 
     * @param id New value of property companyID.
     */
    void setCompanyID(int id);

    /**
     * Setter for property targetDescription.
     * 
     * @param sql New value of property targetDescription.
     */
    void setTargetDescription(String sql);

    /**
     * Setter for property id.
     * 
     * @param id New value of property id.
     */
    void setId(int id);

    /**
     * Setter for property targetName.
     * 
     * @param name New value of property targetName.
     */
    void setTargetName(String name);

    /**
     * Setter for property targetSQL.
     * 
     * @param sql New value of property targetSQL.
     */
    void setTargetSQL(String sql);

    /**
     * Setter for property targetStructure.
     * 
     * @param targetStructure New value of property targetStructure.
     */
    void setTargetStructure(TargetRepresentation targetStructure);

    // TODO: Make "deleted" boolean, if Hibernate is not longer used
    void setDeleted(int deleted);
    
    // TODO: Make return type boolean, if Hibernate is not longer used
    int getDeleted();

	Timestamp getCreationDate();

	void setCreationDate(Timestamp creationDate);

	Timestamp getChangeDate();

	void setChangeDate(Timestamp changeDate);
}
