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

/**
 *
 * @author mhe
 */
public interface MaildropEntry {
    
    /**
     * Constants
     */
    public static final int GEN_SCHEDULED = 0;
    public static final int GEN_NOW = 1;
    public static final int GEN_WORKING = 2;
    public static final int GEN_FINISHED = 3;
    
    public static final char STATUS_ADMIN = 'A';
    public static final char STATUS_TEST = 'T';
    public static final char STATUS_WORLD = 'W';
    public static final char STATUS_ACTIONBASED = 'E';
    public static final char STATUS_DATEBASED = 'R';
    public static final char STATUS_PREDELIVERY = 'V';
    
    /**
     * Getter for property blocksize.
     *
     * @return Value of property blocksize.
     */
    int getBlocksize();
    
    /**
     * Getter for property companyID.
     *
     * @return Value of property companyID.
     */
    int getCompanyID();
    
    /**
     * Getter for property genChangeDate.
     *
     * @return Value of property genChangeDate.
     */
    Date getGenChangeDate();
    
    /**
     * Getter for property genDate.
     *
     * @return Value of property genDate.
     */
    Date getGenDate();
    
    /**
     * Getter for property genStatus.
     *
     * @return Value of property genStatus.
     */
    int getGenStatus();
    
    /**
     * Getter for property id.
     *
     * @return Value of property id.
     */
    int getId();
    
    /**
     * Getter for property mailingID.
     *
     * @return Value of property mailingID.
     */
    int getMailingID();
    
    /**
     * Getter for property senddate.
     *
     * @return Value of property senddate.
     */
    Date getSendDate();
    
    /**
     * Getter for property status.
     *
     * @return Value of property status.
     */
    char getStatus();
    
    /**
     * Getter for property stepping.
     *
     * @return Value of property stepping.
     */
    int getStepping();
    
    /**
     * Setter for property blocksize.
     *
     * @param blocksize New value of property blocksize.
     */
    void setBlocksize(int blocksize);
    
    /**
     * Setter for property companyID.
     *
     * @param companyID New value of property companyID.
     */
    void setCompanyID(int companyID);
    
    /**
     * Setter for property genChangeDate.
     *
     * @param genChangeDate New value of property genChangeDate.
     */
    void setGenChangeDate(Date genChangeDate);
    
    /**
     * Setter for property genDate.
     *
     * @param genDate New value of property genDate.
     */
    void setGenDate(Date genDate);
    
    /**
     * Setter for property genStatus.
     *
     * @param genStatus New value of property genStatus.
     */
    void setGenStatus(int genStatus);
    
    /**
     * Setter for property id.
     *
     * @param id New value of property id.
     */
    void setId(int id);
    
    /**
     * Setter for property mailingID.
     *
     * @param mailingID New value of property mailingID.
     */
    void setMailingID(int mailingID);
    
    /**
     * Setter for property senddate.
     * 
     * @param sendDate 
     */
    void setSendDate(Date sendDate);
    
    /**
     * Setter for property status.
     *
     * @param status New value of property status.
     */
    void setStatus(char status);
    
    /**
     * Setter for property stepping.
     *
     * @param stepping New value of property stepping.
     */
    void setStepping(int stepping);
    
    public int getMaxRecipients();

	public void setMaxRecipients(int maxRecipients);
}
