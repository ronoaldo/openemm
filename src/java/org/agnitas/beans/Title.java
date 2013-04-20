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

import java.util.Map;

public interface Title {
    
    /**
     * Constants
     */
    public static final int GENDER_MALE = 0;

    public static final int GENDER_FEMALE = 1;

    public static final int GENDER_UNKNOWN = 2;

    public static final int GENDER_MISS = 3;

    public static final int GENDER_PRACTICE = 4;

    public static final int GENDER_COMPANY = 5;
 
    /**
     * Setter for property companyID.
     * 
     * @param company New value of property companyID.
     */
    public void setCompanyID(int company);
    
    /**
     * Setter for property id.
     * 
     * @param title New value of property id.
     */
    public void setId(int title);
    
    /**
     * Setter for property shortname.
     * 
     * @param desc New value of property shortname.
     */
    public void setShortname(String desc);

    /**
     * Setter for property description.
     * 
     * @param desc New value of property description.
     */
    public void setDescription(String desc);
    
    /**
     * Setter for property titleGender.
     * 
     * @param titleGender New value of property titleGender.
     */
    public void setTitleGender(Map titleGender);

   /**
     * Getter for property companyID.
     * 
     * @return Value of property companyID.
     */
    public int getCompanyID();
    
    /**
     * Getter for property id.
     * 
     * @return Value of property id.
     */
    public int getId();
    
    /**
     * Getter for property shortname.
     * 
     * @return Value of property shortname.
     */
    public String getShortname();

    /**
     * Getter for property description.
     * 
     * @return Value of property description.
     */
    public String getDescription();

    /**
     * Getter for property titleGender.
     * 
     * @return Value of property titleGender.
     */
    public Map getTitleGender();
    
}
