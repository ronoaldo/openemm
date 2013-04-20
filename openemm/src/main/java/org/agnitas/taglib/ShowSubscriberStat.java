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

package org.agnitas.taglib;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.TimeZone;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.sql.DataSource;

import org.agnitas.beans.BindingEntry;
import org.agnitas.dao.TargetDao;
import org.agnitas.target.Target;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.EmmCalendar;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ShowSubscriberStat extends BodyBase {
	
	private static final transient Logger logger = Logger.getLogger( ShowSubscriberStat.class);

    private static final long serialVersionUID = -8314097414954251274L;
	Hashtable allSubscribes;
    Hashtable allOptouts;
    Hashtable allBounces;

    int maxSubscribe=0;
    int maxOptout=0;
    int maxBounce=0;

    int numMonth=0;

    EmmCalendar aCal;
    SimpleDateFormat aFormatYYYYMMDD=new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat aFormatYYYYMM=new SimpleDateFormat("yyyyMM");

    protected int targetID=0;

    /**
     * Holds value of property mailinglistID.
     */
    private int mailinglistID;

    /**
     * Holds value of property month.
     */
    private String month;

    /**
     * Holds value of property mediaType.
     */
    private String mediaType;

    /**
     * Reads statistics to recipients
     */
    @Override
    public int doStartTag() throws  JspTagException, JspException {
        ApplicationContext aContext=WebApplicationContextUtils.getWebApplicationContext(this.pageContext.getServletContext());
        String thisMonth;
        Target aTarget=null;
        String dateFull="";
        String dateMonth="";

        super.doStartTag();

        aCal=new EmmCalendar(java.util.TimeZone.getDefault());
        TimeZone zone=TimeZone.getTimeZone(AgnUtils.getAdmin(pageContext).getAdminTimezone());
        double zoneOffset=aCal.getTimeZoneOffsetHours(zone);

        aCal.changeTimeWithZone(zone);
        try {
            aCal.setTime(aFormatYYYYMM.parse(this.month));
        } catch (Exception e) {
            aCal.set(Calendar.DAY_OF_MONTH, 1);  // set to first day in month!
        }

        if(zoneOffset!=0.0) {
            dateFull="date_add(bind." + AgnUtils.changeDateName() + " INTERVAL "+zoneOffset+" HOURS)";
        } else {
            dateFull="bind." + AgnUtils.changeDateName();
        }

        dateMonth=AgnUtils.sqlDateString(dateFull, "yyyymm");
        dateFull=AgnUtils.sqlDateString(dateFull, "yyyymmdd");

        thisMonth=aFormatYYYYMM.format(aCal.getTime());
        numMonth=aCal.get(Calendar.MONTH);

        this.allBounces=new Hashtable();
        this.allOptouts=new Hashtable();
        this.allSubscribes=new Hashtable();

        if(targetID!=0) {
            TargetDao targetDao=(TargetDao) aContext.getBean("TargetDao");

            aTarget=targetDao.getTarget(targetID, getCompanyID());
        }

        StringBuffer allQuery=new StringBuffer("select "+dateFull);

        allQuery.append(", bind.user_status, count(bind.customer_id) from customer_");

        allQuery.append(this.getCompanyID()+"_binding_tbl bind");
        if(aTarget != null) {
            allQuery.append(", customer_" + this.getCompanyID() + "_tbl cust");
        }

        allQuery.append(" WHERE bind.mediatype=" +this.mediaType);

        allQuery.append(" AND ");
        allQuery.append(dateMonth+"='"+thisMonth+"'");
        if(this.mailinglistID!=0) {
            allQuery.append(" AND bind.mailinglist_id="+this.mailinglistID);
        }
        if(aTarget != null) {
            allQuery.append(" AND ((" + aTarget.getTargetSQL() + ") AND cust.customer_id=bind.customer_id)");
        }
        allQuery.append(" GROUP BY "+dateFull+", bind.user_status");

        if(aTarget != null) {
            pageContext.setAttribute("target_name", aTarget.getTargetName());
        } else {
            pageContext.setAttribute("target_name", "");
        }

        DataSource ds=(DataSource) aContext.getBean("dataSource");
        Connection con=DataSourceUtils.getConnection(ds);
        try {
            Statement stmt=con.createStatement();
            ResultSet rset=null;
            int tmpUserStatus=0;
            int tmpValue=0;

            rset=stmt.executeQuery(allQuery.toString());
            while(rset.next()) {
                tmpUserStatus=rset.getInt(2);
                switch(tmpUserStatus) {
                    case BindingEntry.USER_STATUS_ACTIVE:
                        tmpValue=rset.getInt(3);
                        this.allSubscribes.put(rset.getString(1), new Integer(tmpValue));
                        if(this.maxSubscribe<tmpValue)
                            this.maxSubscribe=tmpValue;
                        break;

                    case BindingEntry.USER_STATUS_BOUNCED:
                        tmpValue=rset.getInt(3);
                        this.allBounces.put(rset.getString(1), new Integer(tmpValue));
                        if(this.maxBounce<tmpValue)
                            this.maxBounce=tmpValue;
                        break;

                    case BindingEntry.USER_STATUS_OPTOUT:
                    case BindingEntry.USER_STATUS_ADMINOUT:
                        tmpValue=rset.getInt(3);
                        this.allOptouts.put(rset.getString(1), new Integer(tmpValue));
                        if(this.maxOptout<tmpValue)
                            this.maxOptout=tmpValue;
                        break;
                }
            }
            rset.close();
            stmt.close();
        } catch (Exception e) {
        	logger.error( "doStartTag (sql: " + allQuery + ")", e);
            DataSourceUtils.releaseConnection(con, ds);
            AgnUtils.sendExceptionMail("sql: " + allQuery.toString(), e);
            return SKIP_BODY;
        }
        DataSourceUtils.releaseConnection(con, ds);

        pageContext.setAttribute("max_subscribes", new Integer(maxSubscribe));
        pageContext.setAttribute("max_bounces", new Integer(maxBounce));
        pageContext.setAttribute("max_optouts", new Integer(maxOptout));

        return doAfterBody();
    }

    /**
     * Sets attribute for the pagecontext.
     */
    @Override
    public int doAfterBody() throws JspTagException, JspException {

        if(numMonth!=aCal.get(Calendar.MONTH)) {
            return SKIP_BODY;
        }
        String dayKey=null;
        java.util.Date thisDay=null;
        Integer numBounce=new Integer(0);
        Integer numSubscribe=new Integer(0);
        Integer numOptout=new Integer(0);

        thisDay=aCal.getTime();
        dayKey=aFormatYYYYMMDD.format(thisDay);

        if(this.allSubscribes.containsKey(dayKey)) {
            numSubscribe=(Integer)this.allSubscribes.get(dayKey);
        }
        if(this.allBounces.containsKey(dayKey)) {
            numBounce=(Integer)this.allBounces.get(dayKey);
        }
        if(this.allOptouts.containsKey(dayKey)) {
            numOptout=(Integer)this.allOptouts.get(dayKey);
        }

        pageContext.setAttribute("today", thisDay);
        pageContext.setAttribute("subscribes", numSubscribe);
        pageContext.setAttribute("bounces", numBounce);
        pageContext.setAttribute("optouts", numOptout);

        aCal.add(Calendar.DATE, 1);
        return EVAL_BODY_BUFFERED;
    }

    /**
     * Getter for property targetID.
     *
     * @return Value of property targetID.
     */
    public int getTargetID() {
        return this.targetID;
    }

    /**
     * Setter for property targetID.
     *
     * @param targetID New value of property targetID.
     */
    public void setTargetID(int targetID) {
        this.targetID = targetID;
    }

    /**
     * Getter for property mailinglistID.
     *
     * @return Value of property mailinglistID.
     */
    public int getMailinglistID() {
        return this.mailinglistID;
    }

    /**
     * Setter for property mailinglistID.
     *
     * @param mailinglistID New value of property mailinglistID.
     */
    public void setMailinglistID(int mailinglistID) {
        this.mailinglistID = mailinglistID;
    }

    /**
     * Getter for property month.
     *
     * @return Value of property month.
     */
    public String getMonth() {
        return this.month;
    }

    /**
     * Setter for property month.
     *
     * @param month New value of property month.
     */
    public void setMonth(String month) {
        this.month = month;
    }

    /**
     * Getter for property mediaType.
     *
     * @return Value of property mediaType.
     */
    public String getMediaType() {
        return this.mediaType;
    }

    /**
     * Setter for property mediaType.
     *
     * @param mediaType New value of property mediaType.
     */
    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
}
