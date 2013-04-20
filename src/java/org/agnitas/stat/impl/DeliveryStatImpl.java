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

package org.agnitas.stat.impl;

import javax.sql.DataSource;

import org.agnitas.beans.Mailing;
import org.agnitas.dao.MailingDao;
import org.agnitas.stat.DeliveryStat;
import org.agnitas.util.AgnUtils;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class DeliveryStatImpl implements DeliveryStat {
    
    private static final long serialVersionUID = 1903937574581611723L;

	/**
     * Holds value of property totalMails.
     */
    protected int totalMails;
    
    /**
     * Holds value of property generatedMails.
     */
    protected int generatedMails;
    
    /**
     * Holds value of property sentMails.
     */
    protected int sentMails;
    
    /**
     * Holds value of property deliveryStatus.
     */
    protected int deliveryStatus;
    
    /**
     * Holds value of property generateStartTime.
     */
    protected java.util.Date generateStartTime;
    
    /**
     * Holds value of property generateEndTime.
     */
    protected java.util.Date generateEndTime;
    
    /**
     * Holds value of property sendStartTime.
     */
    protected java.util.Date sendStartTime;
    
    /**
     * Holds value of property sendEndTime.
     */
    protected java.util.Date sendEndTime;
    
    /**
     * Holds value of property scheduledSendTime.
     */
    protected java.util.Date scheduledSendTime;
    
    /**
     * Holds value of property mailingID.
     */
    protected int mailingID;
    
    /**
     * Holds value of property companyID.
     */
    protected int companyID;
    
    /**
     * Holds value of property scheduledGenerateTime.
     */
    protected java.util.Date scheduledGenerateTime;
    
    /**
     * Holds value of property cancelable.
     */
    protected boolean cancelable;
    
    /**
     * Holds value of property lastType.
     */
    protected String lastType;
    
    /**
     * Holds value of property lastTotal.
     */
    protected int lastTotal;
    
    /**
     * Holds value of property lastGenerated.
     */
    protected int lastGenerated;
    
    /**
     * Holds value of property lastDate.
     */
    protected java.util.Date lastDate;
    
    private MailingDao mailingDao;
    
    private DataSource dataSource;
    
    public DeliveryStatImpl() {
        companyID=0;
        mailingID=0;
        generatedMails=0;
        sentMails=0;
        totalMails=0;
        deliveryStatus=0;
        cancelable=false;
        lastType="NO";
        // lastDate="";
    }
    
    public boolean getDeliveryStatsFromDB(int mailingType) {
        SqlRowSet rset=null;
        String scheduledSQL=null;
        String detailSQL=null;
        String lastTypeSQL=null;
        String lastBackendSQL=null;
        int statusID=0;
        JdbcTemplate tmpl=new JdbcTemplate(dataSource);
        
        // * * * * * * * * * * * * * * * * * * * * * * *
        // *  last thing backend did for this mailing: *
        // * * * * * * * * * * * * * * * * * * * * * * *
        lastTypeSQL = "SELECT status_field, status_id, genstatus FROM maildrop_status_tbl " +
        "WHERE mailing_id=? ORDER BY if(status_field in ('T','A'), 0, 1) DESC, genchange desc";
        try {
            rset = tmpl.queryForRowSet(lastTypeSQL, new Object[]{ this.mailingID });
            if(rset.next() == true) {
                if(rset.getInt(3) > 0) {
                    lastType=rset.getString(1);
                } else {
                    setCancelable(true);
                    setDeliveryStatus(DeliveryStatImpl.STATUS_SCHEDULED);
                } 
                statusID=rset.getInt(2);
            } else {
                // nothing found:
                lastType = "NO";
                setCancelable(false);
                setDeliveryStatus(DeliveryStatImpl.STATUS_NOT_SENT);
            }
        } catch (Exception e) {
        	AgnUtils.sendExceptionMail("sql:" + lastTypeSQL, e);
            AgnUtils.logger().error("getDeliveryStatsFromDB(lastType): "+e);
            AgnUtils.logger().error("SQL: "+lastTypeSQL);
            return false;
        }
        
        // no entry in mailing_backend_log_tbl ==> don't proceed:
        if(lastType.compareTo("NO") != 0) {
        
            // * * * * * * * * * * * * * * * * * * * * * * * * * * *
            // * how many mails in last admin/test backend action? *
            // * * * * * * * * * * * * * * * * * * * * * * * * * * *
            lastBackendSQL = "SELECT current_mails, total_mails, change_date, creation_date FROM mailing_backend_log_tbl WHERE status_id=?";
            try {
                rset=tmpl.queryForRowSet(lastBackendSQL, new Object[]{ statusID });
                if(rset.next() == true) {
                    lastGenerated=rset.getInt(1);
                    lastTotal=rset.getInt(2);
                    lastDate=rset.getTimestamp(3);
                    this.generateStartTime=rset.getTimestamp(4);
                } else {
                    lastDate=new java.util.Date();
                }
            } catch (Exception e) {
            	AgnUtils.sendExceptionMail("sql:" + lastBackendSQL, e);
                AgnUtils.logger().error("getDeliveryStatsFromDB(lastBackend): "+e);
                AgnUtils.logger().error("SQL: "+lastBackendSQL);
                return false;
            }
        
        }

        String statusField="W";
        switch(mailingType) {
            case Mailing.TYPE_NORMAL:
                statusField="W";
                break;
                
            case Mailing.TYPE_DATEBASED:
                statusField="R";
                break;
                
            case Mailing.TYPE_ACTIONBASED:
                statusField="C";
        }
        
        // * * * * * * * * * * * * * * * * * *
        // *  check generation status first: *
        // * * * * * * * * * * * * * * * * * *
        scheduledSQL = "SELECT genstatus, gendate, senddate, status_id FROM maildrop_status_tbl WHERE company_id = ? AND mailing_id = ? AND status_field=?";
        try {
            rset=tmpl.queryForRowSet(scheduledSQL, new Object[]{ this.companyID, this.mailingID, statusField});
            if(rset.next() == true) {
                setScheduledGenerateTime(rset.getTimestamp(2));
                setScheduledSendTime(rset.getTimestamp(3));
                switch(rset.getInt(1)) {
                    case 0:
                        setDeliveryStatus(DeliveryStat.STATUS_SCHEDULED); // generating didnt start yet:
                        setCancelable(true);
                        break;
                    case 1:
                        setDeliveryStatus(DeliveryStat.STATUS_SCHEDULED); // generating can begin:
                        break;
                    case 2:
                        setDeliveryStatus(DeliveryStat.STATUS_GENERATING); // generating has begun:
                        break;
                    case 3:
                        setDeliveryStatus(DeliveryStat.STATUS_GENERATED);  // too late for cancel:
                        break;
                }
            } else {
                // mailing not scheduled for sending:
                setCancelable(false);
                setDeliveryStatus(DeliveryStatImpl.STATUS_NOT_SENT);
            }
        } catch (Exception e) {
        	AgnUtils.sendExceptionMail("sql:" + scheduledSQL, e);
            AgnUtils.logger().error("getDeliveryStatsFromDB(scheduled): "+e);
            AgnUtils.logger().error("SQL: "+scheduledSQL);
            return false;
        }
        
     if(lastType.equalsIgnoreCase("W")) {
            // * * * * * * * * * * * * * * * * * * * * * * * * *
            // * detailed stats for mailings beeing generated: *
            // * * * * * * * * * * * * * * * * * * * * * * * * *
            
            try {
                lastBackendSQL = "SELECT current_mails, total_mails, " + AgnUtils.changeDateName() + ", creation_date FROM mailing_backend_log_tbl WHERE status_id=?";
                rset=tmpl.queryForRowSet(lastBackendSQL, new Object[]{ statusID });
                if(rset.next() == true) {
                    setTotalMails(rset.getInt(2));
                    setGeneratedMails(rset.getInt(1));
                    setGenerateEndTime(rset.getTimestamp(3));
                    setGenerateStartTime(rset.getTimestamp(4));
                
                    detailSQL = "SELECT mstat.senddate FROM maildrop_status_tbl mstat WHERE mstat.status_id = ?";
                    rset=tmpl.queryForRowSet(detailSQL, new Object[]{ statusID });
                    if(rset.next() == true) {
                        setScheduledSendTime(rset.getTimestamp(1));
                    }
                    
                    detailSQL = "SELECT sum(acc.no_of_mailings) FROM mailing_account_tbl acc WHERE acc.maildrop_id = ?";
                    setSentMails((int)tmpl.queryForLong(detailSQL, new Object[]{ statusID }));
                    
                    detailSQL = "SELECT min(acc.change_date) FROM mailing_account_tbl acc WHERE acc.maildrop_id = ?";
                    rset=tmpl.queryForRowSet(detailSQL, new Object[]{ statusID });
                    if(rset.next() == true) {
                        setSendStartTime(rset.getTimestamp(1));
                    }
                    
                    detailSQL = "SELECT max(acc.change_date) FROM mailing_account_tbl acc WHERE acc.maildrop_id = ?";
                    rset=tmpl.queryForRowSet(detailSQL, new Object[]{ statusID });
                    if(rset.next() == true) {
                        setSendEndTime(rset.getTimestamp(1));
                    }
    
                    if(this.getGeneratedMails()==this.getTotalMails() && this.getSentMails()==this.getTotalMails()) {
                        //("Versendet!");
                        setDeliveryStatus(DeliveryStatImpl.STATUS_SENT);
                    } else if (this.getGeneratedMails()==this.getTotalMails() && this.getScheduledSendTime().after(new java.util.Date()) ) {
                        //("Fertig erzeugt, Versand ab " + rset.getString(8));
                        setDeliveryStatus(DeliveryStatImpl.STATUS_GENERATED);
                    } else if (this.getGeneratedMails()==this.getTotalMails()) {
                        //("Wird versendet !");
                        setDeliveryStatus(DeliveryStatImpl.STATUS_SENDING);
                    } else {
                        //("Wird erzeugt !");
                        setDeliveryStatus(DeliveryStatImpl.STATUS_GENERATING);
                    }

                }
                
            } catch (Exception e) {
            	AgnUtils.sendExceptionMail("sql:" + lastBackendSQL, e);
            	AgnUtils.sendExceptionMail("sql:" + detailSQL, e);
                AgnUtils.logger().error("getDeliveryStatsFromDB(detail): "+e);
                AgnUtils.logger().error("SQL: "+detailSQL);
                return false;
            }
            
            // cancel only mailings the generation has not yet begun at the moment:
            if(deliveryStatus==1) {
                setCancelable(true);
            }   else {
                setCancelable(false);
            }
        }
        return true;
    }
    
    public boolean cancelDelivery() {
         String sql = null;
         boolean proceed = false;
         JdbcTemplate tmpl = new JdbcTemplate(dataSource);
         
         sql = "select genstatus from maildrop_status_tbl where company_id = ? and mailing_id = ? and status_field = 'W' ";
         int genstatus = 1; 
         try {
         	genstatus = tmpl.queryForInt(sql, new Object[]{companyID, mailingID});
         } catch(IncorrectResultSizeDataAccessException e) { // work-around for JDBC-Template Bug
         	genstatus = 0;
         }
         
         proceed = ( genstatus == 0 ? true:false ); // only if the production of the mailing has not been started yet we can cancel it 

         // remove MAILDROP_STATUS_TBL entry:
         if(proceed) {
             boolean success = false;
         	sql = "DELETE FROM maildrop_status_tbl WHERE company_id = ? AND mailing_id = ? AND status_field='W'";
             try {
                 tmpl.update(sql, new Object[]{ companyID, mailingID });
                 success = true;
             } catch ( Exception e ) {
                 AgnUtils.logger().error("cancelDelivery: "+e);
                 AgnUtils.logger().error("SQL: "+sql);
                 AgnUtils.logger().error(AgnUtils.getStackTrace(e));
             }
             
             return success;
         }

         return false;
    }
    
    /**
     * Getter for property totalMails.
     * @return Value of property totalMails.
     */
    public int getTotalMails() {
        
        return this.totalMails;
    }
    
    /**
     * Setter for property totalMails.
     * @param totalMails New value of property totalMails.
     */
    public void setTotalMails(int totalMails) {
        
        this.totalMails = totalMails;
    }
    
    /**
     * Getter for property generatedMails.
     * @return Value of property generatedMails.
     */
    public int getGeneratedMails() {
        
        return this.generatedMails;
    }
    
    /**
     * Setter for property generatedMails.
     * @param generatedMails New value of property generatedMails.
     */
    public void setGeneratedMails(int generatedMails) {
        
        this.generatedMails = generatedMails;
    }
    
    /**
     * Getter for property sentMails.
     * @return Value of property sentMails.
     */
    public int getSentMails() {
        
        return this.sentMails;
    }
    
    /**
     * Setter for property sentMails.
     * @param sentMails New value of property sentMails.
     */
    public void setSentMails(int sentMails) {
        
        this.sentMails = sentMails;
    }
    
    /**
     * Getter for property deliveryStatus.
     * @return Value of property deliveryStatus.
     */
    public int getDeliveryStatus() {
        
        return this.deliveryStatus;
    }
    
    /**
     * Setter for property deliveryStatus.
     * @param deliveryStatus New value of property deliveryStatus.
     */
    public void setDeliveryStatus(int deliveryStatus) {
        
        this.deliveryStatus = deliveryStatus;
    }
    
    /**
     * Getter for property generateStartTime.
     * @return Value of property generateStartTime.
     */
    public java.util.Date getGenerateStartTime() {
        
        return this.generateStartTime;
    }
    
    /**
     * Setter for property generateStartTime.
     * @param generateStartTime New value of property generateStartTime.
     */
    public void setGenerateStartTime(java.util.Date generateStartTime) {
        
        this.generateStartTime = generateStartTime;
    }
    
    /**
     * Getter for property generateEndTime.
     * @return Value of property generateEndTime.
     */
    public java.util.Date getGenerateEndTime() {
        
        return this.generateEndTime;
    }
    
    /**
     * Setter for property generateEndTime.
     * @param generateEndTime New value of property generateEndTime.
     */
    public void setGenerateEndTime(java.util.Date generateEndTime) {
        
        this.generateEndTime = generateEndTime;
    }
    
    /**
     * Getter for property sendStartTime.
     * @return Value of property sendStartTime.
     */
    public java.util.Date getSendStartTime() {
        
        return this.sendStartTime;
    }
    
    /**
     * Setter for property sendStartTime.
     * @param sendStartTime New value of property sendStartTime.
     */
    public void setSendStartTime(java.util.Date sendStartTime) {
        
        this.sendStartTime = sendStartTime;
    }
    
    /**
     * Getter for property sendEndTime.
     * @return Value of property sendEndTime.
     */
    public java.util.Date getSendEndTime() {
        
        return this.sendEndTime;
    }
    
    /**
     * Setter for property sendEndTime.
     * @param sendEndTime New value of property sendEndTime.
     */
    public void setSendEndTime(java.util.Date sendEndTime) {
        
        this.sendEndTime = sendEndTime;
    }
    
    /**
     * Getter for property scheduledSendTime.
     * @return Value of property scheduledSendTime.
     */
    public java.util.Date getScheduledSendTime() {
        
        return this.scheduledSendTime;
    }
    
    /**
     * Setter for property scheduledSendTime.
     * @param scheduledSendTime New value of property scheduledSendTime.
     */
    public void setScheduledSendTime(java.util.Date scheduledSendTime) {
        
        this.scheduledSendTime = scheduledSendTime;
    }
    
    /**
     * Getter for property mailingID.
     * @return Value of property mailingID.
     */
    public int getMailingID() {
        
        return this.mailingID;
    }
    
    /**
     * Setter for property mailingID.
     * @param mailingID New value of property mailingID.
     */
    public void setMailingID(int mailingID) {
        
        this.mailingID = mailingID;
    }
    
    /**
     * Getter for property companyID.
     * @return Value of property companyID.
     */
    public int getCompanyID() {
        
        return this.companyID;
    }
    
    /**
     * Setter for property companyID.
     * @param companyID New value of property companyID.
     */
    public void setCompanyID(int companyID) {
        
        this.companyID = companyID;
    }
    
    /**
     * Getter for property scheduledGenerateTime.
     * @return Value of property scheduledGenerateTime.
     */
    public java.util.Date getScheduledGenerateTime() {
        
        return this.scheduledGenerateTime;
    }
    
    /**
     * Setter for property scheduledGenerateTime.
     * @param scheduledGenerateTime New value of property scheduledGenerateTime.
     */
    public void setScheduledGenerateTime(java.util.Date scheduledGenerateTime) {
        
        this.scheduledGenerateTime = scheduledGenerateTime;
    }
    
    /**
     * Getter for property cancelable.
     * @return Value of property cancelable.
     */
    public boolean isCancelable() {
        
        return this.cancelable;
    }
    
    /**
     * Setter for property cancelable.
     * @param cancelable New value of property cancelable.
     */
    public void setCancelable(boolean cancelable) {
        
        this.cancelable = cancelable;
    }
    
    /**
     * Getter for property lastType.
     * @return Value of property lastType.
     */
    public String getLastType() {
        
        return this.lastType;
    }
    
    /**
     * Setter for property lastType.
     * @param lastType New value of property lastType.
     */
    public void setLastType(String lastType) {
        
        this.lastType = lastType;
    }
    
    /**
     * Getter for property lastTotal.
     * @return Value of property lastTotal.
     */
    public int getLastTotal() {
        
        return this.lastTotal;
    }
    
    /**
     * Setter for property lastTotal.
     * @param lastTotal New value of property lastTotal.
     */
    public void setLastTotal(int lastTotal) {
        
        this.lastTotal = lastTotal;
    }
    
    /**
     * Getter for property lastGenerated.
     * @return Value of property lastGenerated.
     */
    public int getLastGenerated() {
        
        return this.lastGenerated;
    }
    
    /**
     * Setter for property lastGenerated.
     * @param lastGenerated New value of property lastGenerated.
     */
    public void setLastGenerated(int lastGenerated) {
        
        this.lastGenerated = lastGenerated;
    }
    
    /**
     * Getter for property lastDate.
     * @return Value of property lastDate.
     */
    public java.util.Date getLastDate() {
        
        return this.lastDate;
    }
    
    /**
     * Setter for property lastDate.
     * @param lastDate New value of property lastDate.
     */
    public void setLastDate(java.util.Date lastDate) {
        
        this.lastDate = lastDate;
    }
    
    public int getLastWorldMailingStatusId() {
    	return mailingDao.getStatusidForWorldMailing(mailingID, companyID);
    }

	public void setMailingDao(MailingDao mailingDao) {
		this.mailingDao = mailingDao;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
}
