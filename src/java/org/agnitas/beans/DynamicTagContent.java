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

/**
 *
 * @author mhe
 */
public interface DynamicTagContent {
/**
     * Getter for property dynContent.
     *
     * @return Value of property dynContent.
     */
    String getDynContent();

    /**
     * Getter for property id.
     *
     * @return Value of property id.
     */
    int getId();

    /**
     * Getter for property dynName.
     *
     * @return Value of property dynName.
     */
    String getDynName();

    /**
     * Getter for property dynNameID.
     *
     * @return Value of property dynNameID.
     */
    int getDynNameID();

    /**
     * Getter for property dynOrder.
     *
     * @return Value of property dynOrder.
     */
    int getDynOrder();

    /**
     * Getter for property targetID.
     *
     * @return Value of property targetID.
     */
    int getTargetID();

    /**
     * Setter for property companyID.
     * 
     * @param id New value of property companyID.
     */
    void setCompanyID(int id);

    /**
     * Setter for property dynContent.
     * 
     * @param content New value of property dynContent.
     */
    void setDynContent(String content);

    /**
     * Setter for property id.
     * 
     * @param id New value of property id.
     */
    void setId(int id);

    /**
     * Setter for property dynName.
     * 
     * @param name New value of property dynName.
     */
    void setDynName(String name);

    /**
     * Setter for property dynNameID.
     * 
     * @param id New value of property dynNameID.
     */
    void setDynNameID(int id);

    /**
     * Setter for property dynOrder.
     * 
     * @param id New value of property dynOrder.
     */
    void setDynOrder(int id);

    /**
     * Setter for property mailingID.
     * 
     * @param id New value of property mailingID.
     */
    void setMailingID(int id);

    /**
     * Setter for property targetID.
     * 
     * @param id New value of property targetID.
     */
    void setTargetID(int id);

    /**
     * Getter for property mailingID.
     *
     * @return Value of property mailingID.
     */
    public int getMailingID();

    /**
     * Getter for property companyID.
     *
     * @return Value of property companyID.
     */
    public int getCompanyID();
    
}
