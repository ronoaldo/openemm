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

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.sql.DataSource;

import org.agnitas.beans.BindingEntry;
import org.agnitas.beans.Mailing;
import org.agnitas.beans.TrackableLink;
import org.agnitas.beans.factory.MailingStatEntryFactory;
import org.agnitas.beans.factory.URLStatEntryFactory;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.TargetDao;
import org.agnitas.stat.MailingStat;
import org.agnitas.stat.MailingStatEntry;
import org.agnitas.stat.URLStatEntry;
import org.agnitas.target.Target;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.EmmCalendar;
import org.agnitas.util.SafeString;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;


/**
 * Generates Response-Statistics for a mailing
 * @author Eduard Scherer, Martin Helff
 */
public class MailingStatImpl implements MailingStat {

    private static final long serialVersionUID = -1499991571543624942L;
	protected int companyID;
    protected int mailingID;
    protected int targetID;
    protected int sum;
    protected int max;
    protected int clickSubscribers;
    protected int clicks;
    protected int openedMails;
    protected int optOuts;
    protected int bounces;
    protected int totalSubscribers;
    protected int urlID;
    protected int maxblue;
    protected int maxSubscribers;

    protected String mailingShortname;
    protected String aktURL;
    protected String startdate;
    protected String csvfile = "";
    protected String targetName;
    protected String firstdate;

    protected boolean sent;
    protected boolean clicked;
    protected boolean netto;

    protected Hashtable values;
    protected Hashtable statValues;
    protected Hashtable urls;
    protected Hashtable urlShortnames;

    protected LinkedList targetIDs;
    protected LinkedList clickedUrls;
    protected LinkedList notRelevantUrls;

    /**
     * Holds value of property maxNRblue.
     */
    protected int maxNRblue;

    private TargetDao targetDao;
    private MailingDao mailingDao;
    private MailingStatEntryFactory mailingStatEntryFactory;
    private URLStatEntryFactory urlStatEntryFactory;
    private DataSource dataSource;

    /** CONSTRUCTOR */
    public MailingStatImpl() {
        statValues = new Hashtable();
        clickedUrls=new LinkedList();
        notRelevantUrls=new LinkedList();
    }

    /**
     *
     * @param aLocale
     * @return true if Statistics could be loaded
     */
    public boolean getMailingStatFromDB(Locale aLocale) {
        //local vars:
        JdbcTemplate jdbc=this.getJdbcTemplate();
        SqlRowSet rset=null;
        Target aTarget = null;
        urls=new Hashtable();
        urlShortnames=new Hashtable();
        values = new Hashtable();
        MailingStatEntry aktStatData = null;
        Mailing aMailing=null;
        TrackableLink trkLink=null;
        clicks = 0;

        // values to be written in the form:
        maxblue = 0;
        maxNRblue = 0;
        // values for each targetID:
        int tmpMaxblue=0;
        int tmpMaxNRblue = 0;

        // * * * * * * * *
        //  LOAD MAILING
        // * * * * * * * *
        aMailing=mailingDao.getMailing(mailingID, companyID);
        if(aMailing==null) {
            return false;
        }

        this.setMailingShortname(aMailing.getShortname());


        // * * * * * * * *
        //  LOAD URL NAMES
        // * * * * * * * *
        String url_short="";
        Iterator it=aMailing.getTrackableLinks().values().iterator();
        while(it.hasNext()) {
            trkLink=(TrackableLink)it.next();
            if(trkLink.getShortname()!=null && trkLink.getShortname().trim().length()>0) {
                url_short=trkLink.getShortname();
            } else {
                url_short=trkLink.getFullUrl();
            }
            urls.put(new Integer(trkLink.getId()), trkLink);
            urlShortnames.put(new Integer(trkLink.getId()), url_short);
        }

        // * * * * * * * * * *
        // * write csv file: *
        // * * * * * * * * * *
        csvfile += "\"Mailing:\";\"";
        csvfile += SafeString.getHTMLSafeString(getMailingShortname());
        csvfile += "\"\r\n\r\n\"" + SafeString.getLocaleString("statistic.KlickStats", aLocale) + ":\"";

        //  * * * * * * * * * * * * * * * * * *
        //  * * *   loop over targetIDs:  * * *
        //  * * * * * * * * * * * * * * * * * *
        int aktTargetID;
        URLStatEntry aEntry = null;
        ListIterator targetIter = targetIDs.listIterator();

        while(targetIter.hasNext()) {
            aktTargetID = ((Integer)(targetIter.next())).intValue();

            // only load stats for new targetIDs:
            if( statValues.containsKey(new Integer(aktTargetID)) == false ) {

                int aTotalClicks = 0;
                int aTotalClicksNetto = 0;

                AgnUtils.logger().info("## No entry for targetID " + aktTargetID + " found.");
                // write every value in this Hashtable Entry:
                aktStatData = mailingStatEntryFactory.newMailingStatEntry();

                // * * * * * * * * * * * *
                // *  LOAD TARGET GROUP  *
                // * * * * * * * * * * * *
                if(aktTargetID!=0) {
                    aTarget=targetDao.getTarget(aktTargetID, companyID);
                    targetName = aTarget.getTargetName();
                } else {
                    targetName = SafeString.getLocaleString("statistic.All_Subscribers", aLocale);
                }

                aktStatData.setTargetName(targetName);

                tmpMaxblue = 0;
                tmpMaxNRblue = 0;

                // * * * * * * * * *
                //  LOAD URL_CLICKS:
                // * * * * * * * * *
                String allURLQuery = "select count(rdir.customer_id), count(distinct rdir.customer_id) as distotal, rdir.url_id from rdir_log_tbl rdir";
                if(aktTargetID!=0)
                    allURLQuery += ", customer_" + companyID + "_tbl cust";
                allURLQuery += " where rdir.mailing_id=" + mailingID + " AND rdir.company_id=" + companyID;
                if(aktTargetID!=0)
                    allURLQuery += " and ((" + aTarget.getTargetSQL() + ") and cust.customer_id=rdir.customer_id)";
                allURLQuery += " group by rdir.url_id order by distotal desc";

                try {
                    rset=jdbc.queryForRowSet(allURLQuery);
                    //this will become the clickStatValues - Hashtable in the current MailingStatEntry:
                    Hashtable aktClickStatValues = new Hashtable();
                    while(rset.next()) {
                        aEntry = urlStatEntryFactory.newURLStatEntry();
                        aEntry.setClicks(rset.getInt(1));
                        aEntry.setClicksNetto(rset.getInt(2));
                        aEntry.setUrlID(rset.getInt(3));
                        trkLink=(TrackableLink)urls.get(new Integer(rset.getInt(3)));
                        if (trkLink == null) {
							AgnUtils.logger().error("no url found for id:"+rset.getInt(3));
							continue;
						}
                        if(trkLink != null) {
                            aEntry.setUrl(trkLink.getFullUrl());
                        }
                        aEntry.setShortname((String)(urlShortnames.get(new Integer(rset.getInt(3)))));

                        if(trkLink.getRelevance() == 0) {
                            aTotalClicks += rset.getInt(1);
                            aTotalClicksNetto += rset.getInt(2);
                            if(rset.getInt(1)>tmpMaxblue) {
                                tmpMaxblue=rset.getInt(1);
                            }
                        }

                        if(trkLink.getRelevance() == 1) {
                            if(rset.getInt(1)>tmpMaxNRblue) {
                                tmpMaxNRblue=rset.getInt(1);
                            }
                        }

                        aktClickStatValues.put(new Integer(rset.getInt(3)), aEntry);

                        if(aTotalClicks>tmpMaxblue) {
                            tmpMaxblue=aTotalClicks;
                        }
                    }
                    // put clickStatValues into MailingStatEntry
                    aktStatData.setClickStatValues(aktClickStatValues);
                    aktStatData.setTotalClicks(aTotalClicks);
                    aktStatData.setTotalClicksNetto(aTotalClicksNetto);

                    // yes, we have to remember two values,
                    // for the case the next time this
                    // targetID is in the cache
                    aktStatData.setMaxblue(tmpMaxblue);
                    aktStatData.setMaxNRblue(tmpMaxNRblue);

                    if(tmpMaxblue > maxblue) {
                        maxblue = tmpMaxblue;
                    }

                    if(tmpMaxNRblue > maxNRblue) {
                        maxNRblue = tmpMaxNRblue;
                    }
                } catch (Exception e) {
                	AgnUtils.sendExceptionMail("sql:" + allURLQuery, e);
                    AgnUtils.logger().error("getMailingStatFromDB: "+e);
                    AgnUtils.logger().error("SQL: "+allURLQuery);
                    AgnUtils.logger().error(AgnUtils.getStackTrace(e));
                }


                // * * * * * * * * *
                // CLICK_SUBSCRIBERS
                // * * * * * * * * *
                String allClickSubscribersQuery = "select count(distinct rdir.customer_id) from rdir_log_tbl rdir";
                allClickSubscribersQuery += ", rdir_url_tbl url";
                if(aktTargetID!=0)
                    allClickSubscribersQuery += ", customer_" + companyID + "_tbl cust";
                allClickSubscribersQuery += " where rdir.company_id=" + companyID +" AND rdir.mailing_id=" + mailingID;
                allClickSubscribersQuery += " and rdir.url_id=url.url_id AND url.relevance=0";
                if(aktTargetID!=0)
                    allClickSubscribersQuery += " and ((" + aTarget.getTargetSQL() + ") and cust.customer_id=rdir.customer_id)";

                try {
                    rset=jdbc.queryForRowSet(allClickSubscribersQuery);
                    if(rset.next())
                        aktStatData.setTotalClickSubscribers(rset.getInt(1));

                } catch (Exception e) {
                	AgnUtils.sendExceptionMail("sql:" + allClickSubscribersQuery, e);
                    AgnUtils.logger().error("getMailingStatFromDB: "+e);
                    AgnUtils.logger().error("SQL: "+allClickSubscribersQuery);
                    AgnUtils.logger().error(AgnUtils.getStackTrace(e));
                }

                // * * * * * * * * * * * * *
                // O P E N E D   M A I L S *
                // * * * * * * * * * * * * *
                String OnePixelQueryByCust = "select count(*) from onepixel_log_tbl onepix";
                if(aktTargetID!=0)
                    OnePixelQueryByCust += ", customer_" + companyID + "_tbl cust";

                OnePixelQueryByCust += " where onepix.mailing_id=" + mailingID + " and onepix.company_id=" + companyID;
                if(aktTargetID!=0)
                    OnePixelQueryByCust += " and ((" + aTarget.getTargetSQL() + ") AND cust.customer_id=onepix.customer_id)";

                try {
                    rset=jdbc.queryForRowSet(OnePixelQueryByCust);

                    if(rset.next())
                        aktStatData.setOpened(rset.getInt(1));

                } catch (Exception e) {
                	AgnUtils.sendExceptionMail("sql:" + OnePixelQueryByCust, e);
                    AgnUtils.logger().error("getMailingStatFromDB: "+e);
                    AgnUtils.logger().error("SQL: "+OnePixelQueryByCust);
                    AgnUtils.logger().error(AgnUtils.getStackTrace(e));
                }

                // * * * * * * * * * * * * * *
                // O P T O U T  &  B O U N C E
                // * * * * * * * * * * * * * *
                optOuts = 0;
                bounces = 0;

                String BounceOptoutQuery = "select count(bind.customer_id), bind.user_status from customer_" + companyID + "_binding_tbl bind";
                if(aktTargetID!=0)
                    BounceOptoutQuery += ", customer_" + companyID + "_tbl cust";
                BounceOptoutQuery += " where bind.exit_mailing_id=" + mailingID;
                if(aktTargetID!=0)
                    BounceOptoutQuery += " and ((" + aTarget.getTargetSQL() + ") and cust.customer_id=bind.customer_id)";
                BounceOptoutQuery += " GROUP BY bind.user_status, bind.mailinglist_id";

                try {
                    rset=jdbc.queryForRowSet(BounceOptoutQuery);
                    while(rset.next()) {
                        switch(rset.getInt(2)) {
                            case BindingEntry.USER_STATUS_ADMINOUT:
                            case BindingEntry.USER_STATUS_OPTOUT:
                                optOuts += rset.getInt(1);
                                break;
                            case BindingEntry.USER_STATUS_BOUNCED:
                                if(rset.getInt(1) > bounces)
                                    bounces = rset.getInt(1);
                                break;
                        }
                    }
                    aktStatData.setOptouts(optOuts);
                    aktStatData.setBounces(bounces);
                } catch (Exception e) {
                	AgnUtils.sendExceptionMail("sql:" + BounceOptoutQuery, e);
                    AgnUtils.logger().error("getMailingStatFromDB: "+e);
                    AgnUtils.logger().error("SQL: "+BounceOptoutQuery);
                    AgnUtils.logger().error(AgnUtils.getStackTrace(e));
                }

                // * * * * * * * * * * * * * * * *
                // T O T A L   S E N T   M A I L S
                // * * * * * * * * * * * * * * * *
                String SentMailsQuery=null;

                String mailtrackQuery = "select count(distinct mailtrack.customer_id) from mailtrack_tbl mailtrack";
                if(aktTargetID!=0)
                    mailtrackQuery += ", customer_" + companyID + "_tbl cust";
                mailtrackQuery += " where mailtrack.company_id=" + companyID;
                mailtrackQuery += " and mailtrack.mailing_id=" + mailingID;
                if(aktTargetID!=0)
                    mailtrackQuery += " and ((" + aTarget.getTargetSQL() + ") and cust.customer_id=mailtrack.customer_id)";
                SentMailsQuery=mailtrackQuery.toString();

                try {
                    rset=jdbc.queryForRowSet(SentMailsQuery);
                    if(rset.next())
                        aktStatData.setTotalMails(rset.getInt(1));
                    else
                        aktStatData.setTotalMails(0);
                } catch (Exception e) {
                	AgnUtils.sendExceptionMail("sql:" + SentMailsQuery, e);
                    AgnUtils.logger().error("getMailingStatFromDB: "+e);
                    AgnUtils.logger().error("SQL: "+SentMailsQuery);
                    AgnUtils.logger().error(AgnUtils.getStackTrace(e));
                }


                if(aktStatData.getTotalMails() > maxSubscribers) {
                    maxSubscribers = aktStatData.getTotalMails();
                }

                // put MailingStatEntry into statValues Hashtable:
                statValues.put(new Integer(aktTargetID), aktStatData);
                // end loop "check if aktTargetID allready in statValues"
            } else {
                AgnUtils.logger().info("targetTD " + aktTargetID + " found in statValues.");
                aktStatData = (MailingStatEntry)(statValues.get(new Integer(aktTargetID)));


                // keep maxblue and maxSubscribers in mind:
                if(aktStatData.getMaxblue() > maxblue) {
                    maxblue = aktStatData.getMaxblue();
                }

                if(aktStatData.getMaxNRblue() > maxNRblue) {
                    maxNRblue = aktStatData.getMaxNRblue();
                }

                if(aktStatData.getTotalMails() > maxSubscribers) {
                    maxSubscribers = aktStatData.getTotalMails();
                }
            }

            // * * * * * * * * * * * * *
            //  DETERMINE CLICKED URLS AND SORT THEM:
            // * * * * * * * * * * * * *
            URLStatEntry urlStat=null;
            
            Enumeration statEntEnum = statValues.elements();
            
            while(statEntEnum.hasMoreElements()) {
            	MailingStatEntry statEnt=(MailingStatEntry)statEntEnum.nextElement();

            	try {
            		it=statEnt.getClickStatValues().values().iterator();
            		while(it.hasNext()) {
            			urlStat=(URLStatEntry)it.next();
            			trkLink=(TrackableLink)urls.get(urlStat.getUrlID()); 
	                    if(trkLink.getRelevance()==0 && !clickedUrls.contains(urlStat)) {
	                        clickedUrls.add(urlStat);
	                    }
	                    if (trkLink.getRelevance()==2 && !notRelevantUrls.contains(urlStat)) {
	                        notRelevantUrls.add(urlStat);
	                    }
	                }
	             } catch(Exception e) {
	                 AgnUtils.logger().error("Exception: "+e);
	             }
            }
            
            Collections.sort(clickedUrls);
            Collections.sort(notRelevantUrls);
        }

        if(maxblue==0) {
            maxblue=1;
        }

        if(maxNRblue==0) {
            maxNRblue=1;
        }

        if(maxSubscribers==0) {
            maxSubscribers=1;
        }

        return true;
    }

    public boolean getWeekStatFromDB(javax.servlet.http.HttpServletRequest request) {

        JdbcTemplate jdbc=this.getJdbcTemplate();
        SqlRowSet rset=null;
        EmmCalendar aCal=null;
        Target aTarget = null;
        SimpleDateFormat formatter=null;
        values = new Hashtable();
        Mailing aMailing=null;
        TrackableLink trkLink=null;
        Date startDate=null, endDate=null;

        // LOAD MAILING SHORTNAME
        aMailing=mailingDao.getMailing(mailingID, companyID);
        if(aMailing==null) {
            return false;
        }
        setMailingShortname(aMailing.getShortname());

        if(urlID!=0) {
            trkLink=aMailing.getTrackableLinkById(urlID);
            setAktURL(trkLink.getFullUrl());
        }

        // LOAD TARGET GROUP
        if(targetID!=0) {
            aTarget=targetDao.getTarget(targetID, companyID);
            if(aTarget==null) {
                targetID=0;
            }
        }

        if(aTarget!=null) {
            targetName = aTarget.getTargetName();
        } else {
            targetName = SafeString.getLocaleString("statistic.All_Subscribers", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY));
        }

        TimeZone userZone = AgnUtils.getTimeZone(request);

        formatter=new SimpleDateFormat("yyyyMMdd");
        try {
            startDate=formatter.parse(this.startdate);
        } catch (Exception e) {
            startDate=null;
        }

        // set startdate (first day in JSP display)
        if(startDate!=null) { // startdate provided
            // shift from userZone to default zone
            aCal=new EmmCalendar(userZone);
            aCal.setTime(startDate);
            aCal.changeTimeWithZone(TimeZone.getDefault());
        } else { // no startdate provided
            // load start date from db
            String GetDateSQL="select min(change_date) from mailing_account_tbl where mailing_id=" + mailingID;
            try {
                rset=jdbc.queryForRowSet(GetDateSQL);
                if(rset.next()) {
                    aCal=new EmmCalendar(TimeZone.getDefault());
                    aCal.setTime(rset.getTimestamp(1));
                }
            } catch ( Exception e) {
            	AgnUtils.sendExceptionMail("sql:" + GetDateSQL, e);
                AgnUtils.logger().error("getWeekStatFromDB: "+e);
                AgnUtils.logger().error("SQL: "+GetDateSQL);
                aCal=new EmmCalendar(TimeZone.getDefault());
                aCal.setTime(new Date());
                aCal.set(EmmCalendar.HOUR_OF_DAY, 0);
                aCal.set(EmmCalendar.MINUTE, 0);
                aCal.set(EmmCalendar.SECOND, 0);
            }
        }

        startDate=aCal.getTime();
        // add 7 days for end-date
        aCal.add(EmmCalendar.DAY_OF_YEAR, 7);
        aCal.add(EmmCalendar.SECOND, -1);
        endDate=aCal.getTime();

        // set firstdate (first date for this mailing, used for skipping to next/last week in JSP)
        // shift timezone from default to user
        aCal=new EmmCalendar(TimeZone.getDefault());
        aCal.setTime(startDate);
        aCal.changeTimeWithZone(userZone);
        firstdate = formatter.format(aCal.getTime());

        aCal=new EmmCalendar(TimeZone.getDefault());
        aCal.setTime(startDate);
        aCal.changeTimeWithZone(userZone);
        this.startdate = formatter.format(aCal.getTime());

        // *  BUILD PROCEDURE: *
        String sqlQuery = "select rdir.change_date";
        sqlQuery += ", count(";
        if(isNetto())
            sqlQuery += "distinct ";
        sqlQuery += "rdir.customer_id) from rdir_log_tbl rdir";
        if(targetID!=0)
            sqlQuery += ", customer_" + companyID + "_tbl cust";
        sqlQuery += " where rdir.company_id=" + companyID + " and rdir.mailing_id=" + mailingID;
        if(urlID!=0)
            sqlQuery += " and rdir.url_id=" + urlID;
        if(targetID!=0)
            sqlQuery += " and((" + aTarget.getTargetSQL() + ") and cust.customer_id=rdir.customer_id)";
        sqlQuery += " and (rdir.change_date";
        sqlQuery += " >= ? and rdir.change_date <= ?) group by " + AgnUtils.sqlDateString("rdir.change_date", "yyyymmdd");

        // CALL PROCEDURE:
        // don't bother about zero clicks on a particular day: checking is performed in JSP
        int max = 0;
        try {
            values=new Hashtable(); // date (rset.getString(1) --> clicks (rset.getInt(2) )
            rset=jdbc.queryForRowSet(sqlQuery, new Object[] {startDate, endDate});
            while(rset.next()) {
                values.put(formatter.format(rset.getTimestamp(1)), new Integer(rset.getInt(2)));
                clicks+=rset.getInt(2);
                if(rset.getInt(2)>max)
                    max = rset.getInt(2);
            }
            if(max !=0) {
                maxblue = max;
            } else {
                maxblue = 1;
            }
        } catch (Exception e) {
        	AgnUtils.sendExceptionMail("sql:" + sqlQuery + ", " + startdate + ", " + endDate, e);
            AgnUtils.logger().error("getWeekStatFromDB: "+e);
            AgnUtils.logger().error("SQL: "+sqlQuery);
        }


        // csv file "header":
        csvfile += "\"Mailing:\";\"";
        csvfile += SafeString.getHTMLSafeString(getMailingShortname()) + "\"\r\n";
        if(urlID != 0)
            csvfile += "\"" + SafeString.getLocaleString("statistic.ForURL", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ":\";\"" + getAktURL() + "\"";
        csvfile += "\r\n\r\n\"" + SafeString.getLocaleString("target.Target", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ":\";\"";
        if(targetID!=0)
            csvfile += aTarget.getTargetName();
        else
            csvfile += SafeString.getLocaleString("statistic.All_Subscribers", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY));
        csvfile += "\"\r\n\"" + SafeString.getLocaleString("statistic.Unique_Clicks", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ":\";";
        if(isNetto())
            csvfile += "\"" + SafeString.getLocaleString("default.Yes", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\"";
        else
            csvfile += "\"" + SafeString.getLocaleString("default.No", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\"";
        csvfile += "\r\n\r\n\"" + SafeString.getLocaleString("statistic.Date", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\";\"" + SafeString.getLocaleString("statistic.Clicks", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\"";

        return true;
    }

    public boolean getDayStatFromDB(javax.servlet.http.HttpServletRequest request) {
        JdbcTemplate jdbc=this.getJdbcTemplate();
        SqlRowSet rset=null;
        EmmCalendar aCal=null;
        Target aTarget = null;
        SimpleDateFormat formatter=null;
        SimpleDateFormat hourformat=new SimpleDateFormat("HH");
        values = new Hashtable();
        Mailing aMailing=null;
        TrackableLink trkLink=null;

        // LOAD MAILING SHORTNAME
        aMailing=mailingDao.getMailing(mailingID, companyID);
        if(aMailing==null) {
            return false;
        }
        setMailingShortname(aMailing.getShortname());

        // LOAD TARGET GROUP
        if(targetID!=0) {
            aTarget=targetDao.getTarget(targetID, companyID);
            if(aTarget==null) {
                targetID=0;
            }
        }

        if(targetID!=0) {
            targetName=aTarget.getTargetName();
        } else {
            targetName = SafeString.getLocaleString("statistic.All_Subscribers", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY));
        }

        // LOAD URL NAME
        if(urlID!=0) {
            trkLink=aMailing.getTrackableLinkById(urlID);
            setAktURL(trkLink.getFullUrl());
        }

        EmmCalendar my_calendar=null;
        TimeZone userZone = AgnUtils.getTimeZone(request);

        // set up calendar:
        my_calendar = new EmmCalendar(java.util.TimeZone.getDefault());
        my_calendar.changeTimeWithZone(userZone);

        // set time zone offset:
        aCal=new EmmCalendar(TimeZone.getDefault());

        Date startDate=null;
        formatter=new SimpleDateFormat("yyyyMMdd");
        try {
            startDate=formatter.parse(this.startdate);
        } catch (Exception e) {
            startDate=null;
        }

        if (startDate==null) { // startdate provided
            return false;
        }

        aCal=new EmmCalendar(userZone);
        aCal.setTime(startDate);
        aCal.changeTimeWithZone(TimeZone.getDefault());
        startDate=aCal.getTime();

        // *  BUILD PROCEDURE: *
        String sqlQuery = "select rdir.change_date";
        sqlQuery += ", count(";
        if(isNetto())
            sqlQuery += "distinct ";
        sqlQuery += "rdir.customer_id) from rdir_log_tbl rdir";
        if(targetID!=0)
            sqlQuery += ", customer_" + companyID + "_tbl cust";
        sqlQuery += " where rdir.company_id=" + companyID + " and rdir.mailing_id=" + mailingID;
        if(urlID!=0)
            sqlQuery += " and rdir.url_id=" + urlID;
        if(targetID!=0)
            sqlQuery += " and((" + aTarget.getTargetSQL() + ") AND cust.customer_id=rdir.customer_id)";
        sqlQuery += " and " + AgnUtils.sqlDateString("rdir.change_date", "yyyymmdd");
        sqlQuery += " = '" + formatter.format(startDate) + "' group by " + AgnUtils.sqlDateString("rdir.change_date", "%H");

        // CALL PROCEDURE:
        // don't bother about zero clicks on a particular day: checking is performed in JSP
        int max = 0;
        try {
            values=new Hashtable(); // date (rset.getString(1) --> clicks (rset.getInt(2) )
            rset=jdbc.queryForRowSet(sqlQuery);
            while(rset.next()) {
                values.put(hourformat.format(rset.getTimestamp(1)), new Integer(rset.getInt(2)));
                clicks+=rset.getInt(2);
                if(rset.getInt(2)>max)
                    max = rset.getInt(2);
            }
            if(max !=0 )
                maxblue = max;
            else
                maxblue = 1;
        } catch (Exception e) {
        	AgnUtils.sendExceptionMail("sql:" + sqlQuery, e);
            AgnUtils.logger().error("getDayStatFromDB: "+e);
            AgnUtils.logger().error("SQL: "+sqlQuery);
        }


        // csv file "header":
        csvfile += "\"Mailing:\";\"";
        csvfile += SafeString.getHTMLSafeString(getMailingShortname()) + "\"\r\n";
        if(urlID != 0)
            csvfile += "\"" + SafeString.getLocaleString("statistic.ForURL", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ":\";\"" + getAktURL() + "\"";
        csvfile += "\r\n\r\n\"" + SafeString.getLocaleString("target.Target", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ":\";\"";
        if(targetID!=0)
            csvfile += aTarget.getTargetName();
        else
            csvfile += SafeString.getLocaleString("statistic.All_Subscribers", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY));
        csvfile += "\"\r\n\"" + SafeString.getLocaleString("statistic.Unique_Clicks", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ":\";";
        if(isNetto())
            csvfile += "\"" + SafeString.getLocaleString("default.Yes", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\"";
        else
            csvfile += "\"" + SafeString.getLocaleString("default.No", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\"";
        csvfile += "\r\n\r\n\"" + SafeString.getLocaleString("default.Time", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\";\"" + SafeString.getLocaleString("statistic.Clicks", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\"";

        return true;
    }

    public boolean getOpenedStatFromDB(javax.servlet.http.HttpServletRequest request) {
        JdbcTemplate jdbc=this.getJdbcTemplate();
        SqlRowSet rset=null;
        values = new Hashtable();
        MailingStatEntry aEntry = null;
        int totalOpened = 0;
        int tmpOpened = 0;
        int diffOpened = 0;
        Mailing aMailing=null;

        // LOAD MAILING SHORTNAME
        aMailing=mailingDao.getMailing(mailingID, companyID);
        if(aMailing==null) {
            return false;
        }
        setMailingShortname(aMailing.getShortname());

        // csv file:
        csvfile += "\"" + SafeString.getLocaleString("statistic.Opened_Mails", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ":\";\r\n";
        csvfile += "\"Mailing:\";\"";
        csvfile += SafeString.getHTMLSafeString(getMailingShortname()) + "\"\r\n\r\n";

        csvfile += "\"" + SafeString.getLocaleString("statistic.domain", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ":\";\"" + SafeString.getLocaleString("statistic.Opened_Mails", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\"\r\n";
        csvfile += "\r\n";

        // LOAD TOTAL OPENED MAILS
        String OnePixelQueryByCust = "select count(*) from onepixel_log_tbl onepix";
        OnePixelQueryByCust += " where onepix.mailing_id=" + mailingID + " and onepix.company_id="+companyID;
        try {
            rset=jdbc.queryForRowSet(OnePixelQueryByCust);
            if(rset.next())
                totalOpened=rset.getInt(1);

        } catch (Exception e) {
        	AgnUtils.sendExceptionMail("sql:" + OnePixelQueryByCust, e);
            AgnUtils.logger().error("getOpenedStatFromDB: "+e);
            AgnUtils.logger().error("SQL: "+OnePixelQueryByCust);
        }

        // *  BUILD PROCEDURE: *
        String openedQuery = AgnUtils.getHibernateDialect().getLimitString("select count(cust.customer_id) as cust_count, substr(cust.email, (instr(cust.email, '@')+1) ) as domain from onepixel_log_tbl onepix, customer_" + companyID + "_tbl cust where onepix.mailing_id=" + mailingID + " and cust.customer_id=onepix.customer_id group by domain order by cust_count desc", 0, 21);

        int i = 1;
        try {
            rset=jdbc.queryForRowSet(openedQuery, new Object[]{new Integer(21)});
            while(rset.next()) {
                // prepare entries:
                aEntry = mailingStatEntryFactory.newMailingStatEntry();
                aEntry.setOpened(rset.getInt(1));
                aEntry.setTargetName(rset.getString(2));
                values.put(new Integer(i), aEntry);
                tmpOpened += rset.getInt(1);
                csvfile += "\"" + rset.getString(2) + ":\";\"" + rset.getString(1) + "\"\r\n";
                i++;
            }

        } catch (Exception e) {
        	AgnUtils.sendExceptionMail("sql:" + openedQuery + ", 21", e);
            AgnUtils.logger().error("getOpenedStatFromDB: "+e);
            AgnUtils.logger().error("SQL: "+openedQuery);
        }


        diffOpened = totalOpened - tmpOpened;
        aEntry = mailingStatEntryFactory.newMailingStatEntry();
        aEntry.setOpened(totalOpened);
        aEntry.setTotalClicks(diffOpened);
        values.put(new Integer(0), aEntry);

        csvfile += "\"" + SafeString.getLocaleString("statistic.Other", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ":\";\"" + diffOpened + "\"\r\n\r\n";
        csvfile += "\"" + SafeString.getLocaleString("statistic.Total", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ":\";\"" + totalOpened + "\"\r\n";

        return true;
    }

    public boolean getBounceStatFromDB(javax.servlet.http.HttpServletRequest request) {
        JdbcTemplate jdbc=this.getJdbcTemplate();
        SqlRowSet rset=null;
        values = new Hashtable();
        MailingStatEntry aEntry = null;
        int tmpOpened = 0;
        int softBounces=0;
        int hardBounces=0;
        int bounces=0;
        Target aTarget=null;

        if(targetID!=0) {
            aTarget=targetDao.getTarget(targetID, companyID);
            if(aTarget==null) {
                targetID=0;
            }
        }
        // LOAD MAILING SHORTNAME
        String BounceOptoutQuery = "select count(bind.customer_id), bind.mailinglist_id from customer_" + companyID + "_binding_tbl bind";
        if(this.targetID!=0)
            BounceOptoutQuery += ", customer_" + companyID + "_tbl cust";
        BounceOptoutQuery += " where bind.exit_mailing_id=" + mailingID+ " and bind.user_status=2";
        if(this.targetID!=0)
            BounceOptoutQuery += " and ((" + aTarget.getTargetSQL() + ") and cust.customer_id=bind.customer_id)";
        BounceOptoutQuery += " group by bind.mailinglist_id";

        try {
            rset=jdbc.queryForRowSet(BounceOptoutQuery);
            while(rset.next()) {
                if(rset.getInt(1)>bounces) {
                    bounces = rset.getInt(1);
                }
            }
        } catch (Exception e) {
        	AgnUtils.sendExceptionMail("sql:" + BounceOptoutQuery, e);
            AgnUtils.logger().error("getBounceStatFromDB: "+e);
            AgnUtils.logger().error("SQL: "+BounceOptoutQuery);
        }

        // *  BUILD PROCEDURE: *
        String bounceQuery = "select count(distinct bounce.customer_id), bounce.detail from bounce_tbl bounce where bounce.company_id="+this.companyID+" and bounce.mailing_id="+this.mailingID+" group by bounce.detail";

        try {
            rset=jdbc.queryForRowSet(bounceQuery);
            while(rset.next()) {
                // prepare entries:
                aEntry = mailingStatEntryFactory.newMailingStatEntry();
                aEntry.setBounces(rset.getInt(1));
                if(rset.getInt(2)<=500) {
                    softBounces+=rset.getInt(1);
                } else {
                    hardBounces+=rset.getInt(1);
                }
                // aEntry.setTargetName(rset.getString(2));
                values.put(new Integer(rset.getInt(2)), aEntry);
                tmpOpened += rset.getInt(1);
                // csvfile += "\"" + rset.getString(2) + ":\";\"" + rset.getString(1) + "\"\r\n";
                // i++;
            }

        } catch (Exception e) {
        	AgnUtils.sendExceptionMail("sql:" + bounceQuery, e);
            AgnUtils.logger().error("getBounceStatFromDB: "+e);
            AgnUtils.logger().error("SQL: "+bounceQuery);
        }

        aEntry = mailingStatEntryFactory.newMailingStatEntry();
        aEntry.setBounces(softBounces);
        values.put(new Integer(4), aEntry);

        aEntry = mailingStatEntryFactory.newMailingStatEntry();
        aEntry.setBounces(hardBounces);
        values.put(new Integer(5), aEntry);

        aEntry = mailingStatEntryFactory.newMailingStatEntry();
        aEntry.setBounces(softBounces+hardBounces);
        aEntry.setTotalClickSubscribers(bounces);
        values.put(new Integer(0), aEntry);

        return true;
    }

    public boolean cleanAdminClicks() {
        JdbcTemplate jdbc=this.getJdbcTemplate();
        boolean returnValue=true;

        if(this.mailingID==0) {  // never delete mailing 0
            return false;
        }

        String sqlClicks = "delete from rdir_log_tbl where company_id="
				+ companyID
				+ " and mailing_id="
				+ mailingID
				+ " and customer_id in (select customer_id from customer_"
				+ companyID
				+ "_binding_tbl where (user_type = 'A' or user_type = 'T') and mailinglist_id=(select mailinglist_id from mailing_tbl where mailing_id = "
				+ mailingID + "))";
        
        String sqlOpen = "delete from onepixel_log_tbl where company_id="
			+ companyID
			+ " and mailing_id="
			+ mailingID
			+ " and customer_id in (select customer_id from customer_"
			+ companyID
			+ "_binding_tbl where (user_type = 'A' or user_type = 'T') and mailinglist_id=(select mailinglist_id from mailing_tbl where mailing_id = "
			+ mailingID + "))";
		
		String sqlBounce = "delete from bounce_tbl where company_id="
			+ companyID
			+ " and mailing_id="
			+ mailingID
			+ " and customer_id in (select customer_id from customer_"
			+ companyID
			+ "_binding_tbl where (user_type = 'A' or user_type = 'T') and mailinglist_id=(select mailinglist_id from mailing_tbl where mailing_id = "
			+ mailingID + "))";
		
		String sqlOptout = "update customer_"
			+ companyID
			+ "_binding_tbl set exit_mailing_id = 0 where exit_mailing_id="
			+ mailingID
			+ " and (user_type = 'A' or user_type = 'T') and mailinglist_id=(select mailinglist_id from mailing_tbl where mailing_id = "
			+ mailingID + ")";
        
        try {
            jdbc.execute(sqlClicks);
            jdbc.execute(sqlOpen);
            jdbc.execute(sqlBounce);
            jdbc.execute(sqlOptout);
        } catch ( Exception e ) {
            AgnUtils.logger().error("cleanAdminClicks: "+e);
            returnValue=false;
        }

        return returnValue;
    }

    // ***SETTER***:

    public void setCompanyID(int id) {
        companyID=id;
    }

    public void setCsvfile(String file) {
        this.csvfile = file;
    }

    public void setClickSubscribers(int clickSubscribers) {
        this.clickSubscribers = clickSubscribers;
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
    }

    public void setOpenedMails(int openedMails) {
        this.openedMails = openedMails;
    }

    public void setOptOuts(int optOuts) {
        this.optOuts = optOuts;
    }

    public void setBounces(int bounces) {
        this.bounces = bounces;
    }

    public void setTotalSubscribers(int totalSubscribers) {
        this.totalSubscribers = totalSubscribers;
    }

    public void setAktURL(String aktURL) {
        this.aktURL = aktURL;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public void setNetto(boolean netto) {
        this.netto = netto;
    }

    public void setMailingID(int mailingID) {
        this.mailingID = mailingID;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public void setSent(boolean sent) {
    }

    public void setClicked(boolean clicked) {
    }

    public void setTargetID(int targetID) {
        this.targetID = targetID;
    }

    public void setMailingShortname(String mailingShortname) {
        this.mailingShortname = mailingShortname;
    }

    public void setUrlID(int urlID) {
        this.urlID = urlID;
    }

    public void setMaxblue(int maxblue) {
        this.maxblue = maxblue;
    }

    // ***GETTER***:
    public int getTargetID() {
        return targetID;
    }

    public int getClickSubscribers(){
        return this.clickSubscribers;
    }

    public String getCsvfile() {
        return this.csvfile;
    }

    public int getClicks() {
        return this.clicks;
    }

    public int getOpenedMails() {
        return this.openedMails;
    }

    public int getOptOuts() {
        return this.optOuts;
    }

    public int getBounces() {
        return this.bounces;
    }

    public int getTotalSubscribers() {
        return this.totalSubscribers;
    }

    public String getAktURL() {
        return this.aktURL;
    }

    public String getStartdate() {
        return this.startdate;
    }

    public boolean isNetto() {
        return this.netto;
    }

    public Hashtable getValues() {
        return this.values;
    }

    public int getMailingID() {
        return this.mailingID;
    }

    public int getUrlID() {
        return this.urlID;
    }

    public String getMailingShortname() {
        return this.mailingShortname;
    }

    public String getTargetName() {
        return this.targetName;
    }

    public int getMaxblue() {
        return this.maxblue;
    }

    public boolean isSent() {
        return this.sent;
    }

    public boolean isClicked() {
        return this.clicked;
    }

    public String getFirstdate() {
        return this.firstdate;
    }

    public void setFirstdate(String firstdate) {
        this.firstdate = firstdate;
    }

    public Hashtable getStatValues() {
        return this.statValues;
    }

    public void setStatValues(Hashtable statValues) {
        this.statValues = statValues;
    }

    /**
     * Getter for property mailingIDs.
     * @return Value of property mailingIDs.
     */
    public LinkedList getTargetIDs() {
        return this.targetIDs;
    }

    /**
     * Setter for property mailingIDs.
     * @param targetIDs
     */
    public void setTargetIDs(LinkedList targetIDs) {
        this.targetIDs = targetIDs;
    }

    /**
     * Getter for property urlNames.
     * @return Value of property urlNames.
     */
    public Hashtable getUrls() {
        return this.urls;
    }

    /**
     * Setter for property urlNames.
     * @param urls
     */
    public void setUrls(Hashtable urls) {
        this.urls = urls;
    }

    /**
     * Getter for property urlShortnames.
     * @return Value of property urlShortnames.
     */
    public Hashtable getUrlShortnames() {
        return this.urlShortnames;
    }

    /**
     * Setter for property urlShortnames.
     * @param urlShortnames New value of property urlShortnames.
     */
    public void setUrlShortnames(Hashtable urlShortnames) {
        this.urlShortnames = urlShortnames;
    }

    /**
     * Getter for property maxSubscribers.
     * @return Value of property maxSubscribers.
     */
    public int getMaxSubscribers() {
        return this.maxSubscribers;
    }

    /**
     * Setter for property maxSubscribers.
     * @param maxSubscribers New value of property maxSubscribers.
     */
    public void setMaxSubscribers(int maxSubscribers) {
        this.maxSubscribers = maxSubscribers;
    }

    /**
     * Getter for property clickedUrls.
     * @return Value of property clickedUrls.
     */
    public LinkedList getClickedUrls() {
        return this.clickedUrls;
    }

    /**
     * Setter for property clickedUrls.
     * @param clickedUrls New value of property clickedUrls.
     */
    public void setClickedUrls(LinkedList clickedUrls) {
        this.clickedUrls = clickedUrls;
    }

    /**
     * Getter for property notRelevantUrls.
     * @return Value of property notRelevantUrls.
     */
    public LinkedList getNotRelevantUrls() {
        return this.notRelevantUrls;
    }

    /**
     * Setter for property notRelevantUrls.
     * @param notRelevantUrls New value of property notRelevantUrls.
     */
    public void setNotRelevantUrls(LinkedList notRelevantUrls) {
        this.notRelevantUrls = notRelevantUrls;
    }

    /**
     * Getter for property naxNRblue.
     * @return Value of property naxNRblue.
     */
    public int getMaxNRblue() {
        return this.maxNRblue;
    }

    /**
     * Setter for property naxNRblue.
     * @param maxNRblue
     */
    public void setMaxNRblue(int maxNRblue) {
        this.maxNRblue = maxNRblue;
    }

    protected JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }
    
public boolean getOpenTimeStatFromDB(javax.servlet.http.HttpServletRequest request) {
		JdbcTemplate jdbc = this.getJdbcTemplate();
		EmmCalendar aCal = null;
		SimpleDateFormat formatter = null;
		values = new Hashtable();
		Mailing aMailing = null;
		java.util.Date startDate = null, endDate = null;

		// LOAD MAILING SHORTNAME
		aMailing = mailingDao.getMailing(mailingID, companyID);
		if (aMailing == null) {
			return false;
		}
		setMailingShortname(aMailing.getShortname());
		TimeZone userZone = AgnUtils.getTimeZone(request);
		formatter = new SimpleDateFormat("yyyyMMdd");
		try {
			startDate = formatter.parse(this.startdate);
		} catch (Exception e) {
			startDate = null;
		}

		// set startdate (first day in JSP display)
		if (startDate != null) { // startdate provided
			// shift from userZone to default zone
			aCal = new EmmCalendar(userZone);
			aCal.setTime(startDate);
			aCal.changeTimeWithZone(TimeZone.getDefault());
		} else { // no startdate provided
			// load start date from db
			String sql = "select min(change_date) from mailing_account_tbl where mailing_id=" + mailingID;
			try {
				java.sql.Date date = (java.sql.Date) jdbc.queryForObject(sql, java.sql.Date.class);
				aCal = new EmmCalendar(TimeZone.getDefault());
				aCal.setTime(date);
			} catch (Exception e) {
				AgnUtils.logger().error("getOpenTimeStatFromDB: (startdate) " + e);
				AgnUtils.logger().error("SQL: " + sql);
				aCal = new EmmCalendar(TimeZone.getDefault());
				aCal.setTime(new java.util.Date());
				aCal.set(EmmCalendar.HOUR_OF_DAY, 0);
				aCal.set(EmmCalendar.MINUTE, 0);
				aCal.set(EmmCalendar.SECOND, 0);
			}
		}

		startDate = aCal.getTime();
		// add 7 days for end-date
		aCal.add(EmmCalendar.DAY_OF_YEAR, 7);
		aCal.add(EmmCalendar.SECOND, -1);
		endDate = aCal.getTime();

		// set firstdate (first date for this mailing, used for skipping to
		// next/last week in JSP)
		// shift timezone from default to user
		aCal = new EmmCalendar(TimeZone.getDefault());
		aCal.setTime(startDate);
		aCal.changeTimeWithZone(userZone);
		firstdate = formatter.format(aCal.getTime());

		aCal = new EmmCalendar(TimeZone.getDefault());
		aCal.setTime(startDate);
		aCal.changeTimeWithZone(userZone);
		this.startdate = formatter.format(aCal.getTime());

		// * BUILD PROCEDURE: *
		String sqlQuery = "select date_format(change_date, '%Y%m%d') as time, count(customer_id) as total from onepixel_log_tbl where mailing_id = ? and (change_date >= ? and change_date <= ?) group by time";
		// CALL PROCEDURE:
		// don't bother about zero clicks on a particular day: checking is
		// performed in JSP
		int max = 0;
		try {
			values = new Hashtable();
			List list = jdbc.queryForList(sqlQuery, new Object[] { new Integer(mailingID), startDate,
					endDate });
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Map map = (Map) it.next();
				values.put((String) map.get("time"), new Integer(((Number) map.get("total")).intValue()));
				clicks += ((Number) map.get("total")).intValue();
				if (((Number) map.get("total")).intValue() > max) {
					max = ((Number) map.get("total")).intValue();
				}
			}
			if (max != 0) {
				maxblue = max;
			} else {
				maxblue = 1;
			}
		} catch (Exception e) {
			AgnUtils.logger().error("getOpenTimeStatFromDB ( ): " + e);
			AgnUtils.logger().error("SQL: " + sqlQuery);
		}

		return true;
	}
	
	public boolean getOpenTimeDayStat(javax.servlet.http.HttpServletRequest request) {
		
		JdbcTemplate jdbc=this.getJdbcTemplate();
        SqlRowSet rset=null;
        EmmCalendar aCal=null;
        SimpleDateFormat formatter=null;
        SimpleDateFormat hourformat=new SimpleDateFormat("HH");
        values = new Hashtable();
        Mailing aMailing=null;

        // LOAD MAILING SHORTNAME
        aMailing=mailingDao.getMailing(mailingID, companyID);
        if(aMailing==null) {
            return false;
        }
        setMailingShortname(aMailing.getShortname());

        EmmCalendar my_calendar=null;
        TimeZone userZone = AgnUtils.getTimeZone(request);

        // set up calendar:
        my_calendar = new EmmCalendar(java.util.TimeZone.getDefault());
        my_calendar.changeTimeWithZone(userZone);

        // set time zone offset:
        aCal=new EmmCalendar(TimeZone.getDefault());

        Date startDate=null;
        formatter=new SimpleDateFormat("yyyyMMdd");
        try {
            startDate=formatter.parse(this.startdate);
        } catch (Exception e) {
            startDate=null;
        }

        if (startDate==null) { // startdate provided
            return false;
        }

        aCal=new EmmCalendar(userZone);
        aCal.setTime(startDate);
        aCal.changeTimeWithZone(TimeZone.getDefault());
        startDate=aCal.getTime();

        // *  BUILD PROCEDURE: *
        String sqlQuery = "select " + AgnUtils.sqlDateString("change_date", "%H") + "as time, count(customer_id) as total from onepixel_log_tbl where company_id=" + companyID + " and mailing_id=" + mailingID + " and " + AgnUtils.sqlDateString("change_date", "yyyymmdd") + " = '" + formatter.format(startDate) + "' group by " + AgnUtils.sqlDateString("change_date", "%h");
        																																															
        // CALL PROCEDURE:
        // don't bother about zero clicks on a particular day: checking is performed in JSP
        int max = 0;
        try {
            values=new Hashtable(); // date (rset.getString(1) --> clicks (rset.getInt(2) )
            rset=jdbc.queryForRowSet(sqlQuery);
            while(rset.next()) {
                values.put(new Integer(rset.getInt("time")), new Integer(rset.getInt("total")));
                clicks+=rset.getInt("total");
                if(rset.getInt("total")>max)
                    max = rset.getInt("total");
            }
            if(max !=0 )
                maxblue = max;
            else
                maxblue = 1;
        } catch (Exception e) {
        	AgnUtils.sendExceptionMail("sql:" + sqlQuery, e);
            AgnUtils.logger().error("getOpenTimeDayStat: "+e);
            AgnUtils.logger().error("SQL: "+sqlQuery);
        }
		return true;
	}

    public void setTargetDao(TargetDao targetDao) {
        this.targetDao = targetDao;
    }

    public void setMailingDao(MailingDao mailingDao) {
        this.mailingDao = mailingDao;
    }

    public void setMailingStatEntryFactory(MailingStatEntryFactory mailingStatEntryFactory) {
        this.mailingStatEntryFactory = mailingStatEntryFactory;
    }

    public void setUrlStatEntryFactory(URLStatEntryFactory urlStatEntryFactory) {
        this.urlStatEntryFactory = urlStatEntryFactory;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
