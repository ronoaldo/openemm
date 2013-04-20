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
import java.util.Date;

public interface ExportPredef extends Serializable {
    /**
     * Setter for property id.
     * 
     * @param id New value of property id.
     */
    public void setId(int id);

    /**
     * Setter for property companyID.
     * 
     * @param company New value of property companyID.
     */
    public void setCompanyID(int company);

    /**
     * Setter for property charset.
     * 
     * @param charset New value of property charset.
     */
    public void setCharset(String charset);

    /**
     * Setter for property columns.
     * 
     * @param columns New value of property columns.
     */
    public void setColumns(String columns);

    /**
     * Setter for property shortname.
     * 
     * @param shortname New value of property shortname.
     */
    public void setShortname(String shortname);

    /**
     * Setter for property description.
     * 
     * @param description New value of property description.
     */
    public void setDescription(String description);

    /**
     * Setter for property mailinglists.
     * 
     * @param mailinglists New value of property mailinglists.
     */
    public void setMailinglists(String mailinglists);

    /**
     * Setter for property mailinglistID.
     * 
     * @param mailinglistID New value of property mailinglistID.
     */
    public void setMailinglistID(int mailinglistID);

    /**
     * Setter for property delimiter.
     * 
     * @param delimiter New value of property delimiter.
     */
    public void setDelimiter(String delimiter);

    /**
     * Setter for property separator.
     * 
     * @param separator New value of property separator.
     */
    public void setSeparator(String separator);

    /**
     * Setter for property targetID.
     * 
     * @param targetID New value of property targetID.
     */
    public void setTargetID(int targetID);

    /**
     * Setter for property userType.
     * 
     * @param userType New value of property userType.
     */
    public void setUserType(String userType);

    /**
     * Setter for property userStatus.
     * 
     * @param userStatus New value of property userStatus.
     */
    public void setUserStatus(int userStatus);

    /**
     * Setter for property deleted.
     * 
     * @param deleted New value of property deleted.
     */
    public void setDeleted(int deleted);

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
     * Getter for property charset.
     *
     * @return Value of property charset.
     */
    public String getCharset();
    
    /**
     * Getter for property columns.
     *
     * @return Value of property columns.
     */
    public String getColumns();
    
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
     * Getter for property mailinglists.
     *
     * @return Value of property mailinglists.
     */
    public String getMailinglists();
    
    /**
     * Getter for property mailinglistID.
     *
     * @return Value of property mailinglistID.
     */
    public int getMailinglistID();

    /**
     * Getter for property delimiter.
     *
     * @return Value of property delimiter.
     */
    public String getDelimiter();

    /**
     * Getter for property separator.
     *
     * @return Value of property separator.
     */
    public String getSeparator();

    /**
     * Getter for property targetID.
     *
     * @return Value of property targetID.
     */
    public int getTargetID();

    /**
     * Getter for property userType.
     *
     * @return Value of property userType.
     */
    public String getUserType();

    /**
     * Getter for property userStatus.
     *
     * @return Value of property userStatus.
     */
    public int getUserStatus();

    /**
     * Getter for property deleted.
     *
     * @return Value of property deleted.
     */
    public int getDeleted();

    public Date getTimestampStart();

    public void setTimestampStart(Date timestampStart);

    public Date getTimestampEnd();

    public void setTimestampEnd(Date timestampEnd);

    public Date getCreationDateStart();

    public void setCreationDateStart(Date creationDateStart);

    public Date getCreationDateEnd();

    public void setCreationDateEnd(Date creationDateEnd);

    public Date getMailinglistBindStart();

    public void setMailinglistBindStart(Date mailinglistBindStart);

    public Date getMailinglistBindEnd();

    public void setMailinglistBindEnd(Date mailinglistBindEnd);
}
