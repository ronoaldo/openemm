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
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

/**
 * Bean containing all propertys for an Admin (User of openEMM)
 *
 * @author Martin Helff
 */
public interface Admin extends Serializable {

    /**
     * Getter for property adminCountry.
     * 
     * @return Value of property id of this Admin.
     */
    String getAdminCountry();

    /**
     * Getter for property adminID.
     * 
     * @return Value of property id of this Admin.
     */
    int getAdminID();

    /**
     * Getter for property companyID.
     * 
     * @return Value of property companyID for this Admin.
     */
    int getCompanyID();

    /**
     * Getter for property language.
     * 
     * @return Value of property language for this Admin.
     */
    String getAdminLang();

    /**
     * Getter for property language variant.
     *
     * @return Value of property language variant for this Admin.
     */
    String getAdminLangVariant();

    /**
     * Getter for property adminPermissions.
     * Admin permissions are the union of permission set for the admin and those
     * set for the group.
     *
     * @return Value of property adminPermissions.
     */
    Set<String> getAdminPermissions();

    /**
     * Getter for property adminTimezone
     *
     * @return Timezone as String
     */
    String getAdminTimezone();

    /**
     * Getter for Company-Object
     *
     * @return Company
     */
    Company getCompany();

    /**
     * Getter for property fullname
     *
     * @return fullname
     */
    String getFullname();

    /**
     * Getter for property group.
     *
     * @return Value of property group.
     */
    org.agnitas.beans.AdminGroup getGroup();

    /**
     * Getter for property layoutID.
     * The layout defines the look of the interface.
     *
     * @return layoutID.
     */
    int getLayoutID();

    /**
     * Getter for property layoutBaseID.
     * The layout defines the look of the interface.
     * new version / redesign.
     *
     * @return layoutBaseID.
     */
    int getLayoutBaseID();

    /**
     * Getter for property shortname.
     * The shortname is a short text describing the admin.
     *
     * @return shortname.
     */
    String getShortname();

    /**
     * Getter for property username.
     *
     * @return username.
     */
    String getUsername();

    /**
     * Getter for property creationDate.
     *
     * @return creationDate.
     */
    Timestamp getCreationDate();

    /**
     * Getter the date when the password was last changed.
     *
     * @return the date of last password change.
     */
    Date getLastPasswordChange();
    
    public String getPassword();

    /**
     * Getter for property passwordHash.
     *
     * @return Value of property passwordHash.
     */
    public byte[] getPasswordHash();

    /**
     * Getter for property mailtracking.
     * 
     * @return Value of property mailtracking for this Admin.
     */
    int getMailtracking();

    /**
     * Setter for property country.
     *
     * @param adminCountry new value for country.
     */
    void setAdminCountry(String adminCountry);

    /**
     * Setter for property adminID.
     *
     * @param adminID the new value for the adminID.
     */
    void setAdminID(int adminID);

    /**
     * Setter for property companyID.
     *
     * @param companyID the new value for the companyID.
     */
    void setCompanyID(int companyID);

    /**
     * Setter for the language.
     *
     * @param adminLang the new value for the language.
     */
    void setAdminLang(String adminLang);

    /**
     * Setter for the language variant.
     *
     * @param adminLangVariant the new value for the language variant.
     */
    void setAdminLangVariant(String adminLangVariant);

    /**
     * Setter for property adminPermissions.
     *
     * @param adminPermissions New value of property adminPermissions.
     */
    void setAdminPermissions(Set<String> adminPermissions);

    /**
     * Setter for the timezone.
     *
     * @param adminTimezone the new value for the timezone.
     */
    void setAdminTimezone(String adminTimezone);

    /**
     * Setter for the companyID.
     *
     * @param id the new value for the companyID.
     */
    void setCompany(Company id);

    /**
     * Setter for the fullname.
     * 
     * @param fullname the new value for the fullname.
     */
    void setFullname(String fullname);

    /**
     * Setter for property groupID.
     *
     * @param groupID New value of property groupID.
     */
    void setGroup(AdminGroup groupID);

    /**
     * Setter for the layout.
     *
     * @param layoutID the id of the new layout.
     */
    void setLayoutID(int layoutID);

    /**
     * Setter for the layoutBase.
     *
     * @param layoutBaseID the id of the new layout.
     */
    void setLayoutBaseID(int layoutBaseID);

    /**
     * Setter for the password.
     * 
     * @param password the new value for the password.
     */
    void setPassword(String password);

    /**
     * Setter for the shortname.
     *
     * @param name the new value for the shortname.
     */
    void setShortname(String name);

    /**
     * Setter for the username.
     * 
     * @param username the new value for the username.
     */
    void setUsername(String username);

    /**
     * Setter for the creationDate.
     *
     * @param creationDate the new value for the creationDate.
     */
    void setCreationDate(java.sql.Timestamp creationDate);

    /**
     * Set the date, when the password was last changed.
     *
     * @param lastPasswordChange the new value for the lastPasswordChange.
     */
    void setLastPasswordChange(java.util.Date lastPasswordChange);

    /**
     * Setter for property passwordHash.
     *
     * @param passwordHash New value of property passwordHash.
     */
    public void setPasswordHash(byte[] passwordHash);

    /**
     * Check for a permission.
     *
     * @param token the token of the permission to check.
     * @return true if Admin has the given permission, false otherwise.
     */
    public boolean permissionAllowed(String token);

    /**
     * Get the locale for this Admin.
     *
     * @return the locale.
     */
    public java.util.Locale getLocale();

    /**
     * Setter for property mailtracking.
     *
     * @param companyID the new value for the mailtracking.
     */
    void setMailtracking(int mailtracking);
    
    /**
     * Getter for the preferred list size of a table
     */

    public int getPreferredListSize();
    
    /**
     * Setter for the preferred list size of a table
     */
    
    public void setPreferredListSize(int preferredlistsize);
    
    public int getDefaultImportProfileID();

    public void setDefaultImportProfileID(int defaultImportProfileID);
}
