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
 * @author Martin Helff
 */
public interface Company extends Serializable {
	
	public final static String STATUS_DELETED = "deleted"; 
	public final static String STATUS_ACTIVE = "active";
	public final static String STATUS_INACTIVE = "inactive";
	public final static String STATUS_TODELETE = "todelete";
	
    /**
     * Getter for property id.
     *
     * @return Value of property id.
     */
    int getId();

    /**
     * Getter for property mailtracking.
     *
     * @return Value of property mailtracking.
     */
    int getMailtracking();

    /**
     * Getter for property creatorID.
     *
     * @return Value of property creatorID.
     */
    int getCreatorID();

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

    /**
     * Getter for property secret.
     *
     * @return Value of property secret.
     */
    String getSecret();

    /**
     * Getter for property rdirDomain.
     *
     * @return Value of property rdirDomain.
     */
    String getRdirDomain();

    /**
     * Getter for property mailloopDomain.
     *
     * @return Value of property mailloopDomain.
     */
    public String getMailloopDomain();

    /**
     * Getter for property status.
     *
     * @return Value of property ststus.
     */
    String getStatus();

    /**
     * Getter for property use_utf.
     *
     * @return Value of property use_utf.
     */
    int getUseUTF();
    
    /**
     * Returns maximum number of failed logins before IP address is blocked for a period.
     * 
     * @return maximum allowed number of failed logins
     */
    int getMaxLoginFails();
    
    /**
     * Returns time (in minutes) an IP address is blocked after a series of failed login attempts.
     * 
     * @return block time in minutes
     */
    int getLoginBlockTime();

    /**
     * Setter for property id.
     *
     * @param id New value of property id.
     */
    void setId(int id);

    /**
     * Setter for property mailtracking.
     *
     * @param mailtracking New value of property mailtracking.
     */
    void setMailtracking(int mailtracking);

    /**
     * Setter for property creatorID.
     *
     * @param creatorID New value of property creatorID.
     */
    void setCreatorID(int creatorID);

    /**
     * Setter for property shortname.
     *
     * @param name New value of property shortname.
     */
    void setShortname(String name);

    /**
     * Setter for property description.
     *
     * @param description New value of property description.
     */
    void setDescription(String description);

    /**
     * Setter for property rdirDomain.
     *
     * @param rdirDomain New value of property rdirDomain.
     */
    void setRdirDomain(String rdirDomain);

    /**
     * Setter for property secret.
     *
     * @param secret New value of property secret.
     */
    void setSecret(String secret);

    /**
     * Setter for property mailloopDomain.
     *
     * @param mailloopDomain New value of property mailloopDomain.
     */
    public void setMailloopDomain(String mailloopDomain);

    /**
     * Setter for property status.
     *
     * @param status New value of property status.
     */
    void setStatus(String status);

    /**
     * Setter for property use_utf.
     *
     * @param use_utf New value of property use_utf.
     */
    void setUseUTF(int useUTF);
    
    /**
     * Set maximum allowed number of failed login attempts before blocking IP address temporarily.
     * 
     * @param maximum maximum number of failed logins
     */
    void setMaxLoginFails(int maximum);

    /**
     * Set time to block an IP address after a series of failed login attempts.
     * 
     * @param time time to block in minutes
     */
    void setLoginBlockTime(int time);
    
    void setMinimumSupportedUIDVersion( Number minimumSupportedUIDVersion);
    Number getMinimumSupportedUIDVersion();

    public int getMaxRecipients();

    public void setMaxRecipients(int maxRecipients);
}
