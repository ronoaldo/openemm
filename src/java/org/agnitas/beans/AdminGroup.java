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
import java.util.Set;

/**
 * Bean representing an AdminGroup (mainly for User-Permissions)
 * The concept of permission is that every user with the permission to manage
 * userrights has the permission to manage the rights he got for all other
 * users.
 * This means that only a very limited number of user should be allowed to
 * manage userrights.
 *
 * @author Martin Helff
 */
public interface AdminGroup extends Serializable {
    /**
     * Getter for property companyID.
     * 
     * @return Value of property companyID
     */
    int getCompanyID();

    /**
     * Getter for property description.
     * 
     * @return The description of the AdminGroup.
     */
    String getDescription();

    /**
     * Getter for property groupID.
     * 
     * @return The id of the AdminGroup.
     */
    int getGroupID();

    /**
     * Getter for property groupPermissions.
     * Return a Set containing one entry for each permission, this group has. 
     * 
     * @return A set of Permissions.
     */
    Set<String> getGroupPermissions();

    /**
     * Get the property Shortname. 
     * The shortname is a short descriptive name for the group.
     * 
     * @return Value of property shortname
     */
    String getShortname();

    /**
     * Checks if the given token is in the list of tokens (user-rights) for
     * this group.
     * 
     * @param token security-token
     * @return true if the requested operation is allowed, false otherwise.
     */
    boolean permissionAllowed(String token);

    /**
     * Setter for property companyID. 
     * 
     * @param id companyID
     */
    void setCompanyID(int id);

    /**
     * Setter for property description.
     * 
     * @param description New value of property description.
     */
    void setDescription(String description);

    /**
     * Setter for property groupID.
     * 
     * @param groupID New value of property groupID.
     */
    void setGroupID(int groupID);

    /**
     * Setter for property groupPermissions.
     * 
     * @param groupPermissions New value of property groupPermissions.
     */
    void setGroupPermissions(Set<String> groupPermissions);

    /**
     * Setter for property shortname.
     * 
     * @param name shortname
     */
    void setShortname(String name);
}
