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

package org.agnitas.stat;

import java.io.Serializable;
import java.util.Date;


/**
 *
 * @author mhe
 */
public interface DeliveryStat extends Serializable {
    int STATUS_NOT_SENT = 0;
    int STATUS_SCHEDULED = 1;
    int STATUS_GENERATING = 2;
    int STATUS_GENERATED = 3;
    int STATUS_SENDING = 4;
    int STATUS_SENT = 5;

    boolean cancelDelivery();

    /**
     * Getter for property companyID.
     * 
     * @return Value of property companyID.
     */
    int getCompanyID();

    /**
     * Getter for property deliveryStats from database.
     * 
     * @return Value of property deliveryStats from database.
     */
    boolean getDeliveryStatsFromDB(int mailingType);

    /**
     * Getter for property deliveryStatus.
     * 
     * @return Value of property deliveryStatus.
     */
    int getDeliveryStatus();

    /**
     * Getter for property generateEndTime.
     * 
     * @return Value of property generateEndTime.
     */
    Date getGenerateEndTime();

    /**
     * Getter for property generateStartTime.
     * 
     * @return Value of property generateStartTime.
     */
    Date getGenerateStartTime();

    /**
     * Getter for property generatedMails.
     * 
     * @return Value of property generatedMails.
     */
    int getGeneratedMails();

    /**
     * Getter for property lastDate.
     * 
     * @return Value of property lastDate.
     */
    Date getLastDate();

    /**
     * Getter for property lastGenerated.
     * 
     * @return Value of property lastGenerated.
     */
    int getLastGenerated();

    /**
     * Getter for property lastTotal.
     * 
     * @return Value of property lastTotal.
     */
    int getLastTotal();

    /**
     * Getter for property lastType.
     * 
     * @return Value of property lastType.
     */
    String getLastType();

    /**
     * Getter for property mailingID.
     * 
     * @return Value of property mailingID.
     */
    int getMailingID();

    /**
     * Getter for property scheduledGenerateTime.
     * 
     * @return Value of property scheduledGenerateTime.
     */
    Date getScheduledGenerateTime();

    /**
     * Getter for property scheduledSendTime.
     * 
     * @return Value of property scheduledSendTime.
     */
    Date getScheduledSendTime();

    /**
     * Getter for property sendEndTime.
     * 
     * @return Value of property sendEndTime.
     */
    Date getSendEndTime();

    /**
     * Getter for property sendStartTime.
     * 
     * @return Value of property sendStartTime.
     */
    Date getSendStartTime();

    /**
     * Getter for property sentMails.
     * 
     * @return Value of property sentMails.
     */
    int getSentMails();

    /**
     * Getter for property totalMails.
     * 
     * @return Value of property totalMails.
     */
    int getTotalMails();

    /**
     * Getter for property cancelable.
     * 
     * @return Value of property cancelable.
     */
    boolean isCancelable();

    /**
     * Setter for property cancelable.
     * 
     * @param cancelable New value of property cancelable.
     */
    void setCancelable(boolean cancelable);

    /**
     * Setter for property companyID.
     * 
     * @param companyID New value of property companyID.
     */
    void setCompanyID(int companyID);

    /**
     * Setter for property deliveryStatus.
     * 
     * @param deliveryStatus New value of property deliveryStatus.
     */
    void setDeliveryStatus(int deliveryStatus);

    /**
     * Setter for property generateEndTime.
     * 
     * @param generateEndTime New value of property generateEndTime.
     */
    void setGenerateEndTime(Date generateEndTime);

    /**
     * Setter for property generateStartTime.
     * 
     * @param generateStartTime New value of property generateStartTime.
     */
    void setGenerateStartTime(Date generateStartTime);

    /**
     * Setter for property generatedMails.
     * 
     * @param generatedMails New value of property generatedMails.
     */
    void setGeneratedMails(int generatedMails);

    /**
     * Setter for property lastDate.
     * 
     * @param lastDate New value of property lastDate.
     */
    void setLastDate(Date lastDate);

    /**
     * Setter for property lastGenerated.
     * 
     * @param lastGenerated New value of property lastGenerated.
     */
    void setLastGenerated(int lastGenerated);

    /**
     * Setter for property lastTotal.
     * 
     * @param lastTotal New value of property lastTotal.
     */
    void setLastTotal(int lastTotal);

    /**
     * Setter for property lastType.
     * 
     * @param lastType New value of property lastType.
     */
    void setLastType(String lastType);

    /**
     * Setter for property mailingID.
     * 
     * @param mailingID New value of property mailingID.
     */
    void setMailingID(int mailingID);

    /**
     * Setter for property scheduledGenerateTime.
     * 
     * @param scheduledGenerateTime New value of property scheduledGenerateTime.
     */
    void setScheduledGenerateTime(Date scheduledGenerateTime);

    /**
     * Setter for property scheduledSendTime.
     * 
     * @param scheduledSendTime New value of property scheduledSendTime.
     */
    void setScheduledSendTime(Date scheduledSendTime);

    /**
     * Setter for property sendEndTime.
     * 
     * @param sendEndTime New value of property sendEndTime.
     */
    void setSendEndTime(Date sendEndTime);

    /**
     * Setter for property sendStartTime.
     * 
     * @param sendStartTime New value of property sendStartTime.
     */
    void setSendStartTime(Date sendStartTime);

    /**
     * Setter for property sentMails.
     * 
     * @param sentMails New value of property sentMails.
     */
    void setSentMails(int sentMails);

    /**
     * Setter for property totalMails.
     * 
     * @param totalMails New value of property totalMails.
     */
    void setTotalMails(int totalMails);
    
    /**
     * get the status id for the last 'world mailing'
     * @return the status id, or 0 if this mailing hasn't been sent yet
     */
    public int getLastWorldMailingStatusId();
       
}
