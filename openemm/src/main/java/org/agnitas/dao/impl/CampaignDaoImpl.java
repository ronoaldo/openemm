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

package org.agnitas.dao.impl;

import org.agnitas.beans.*;
import org.agnitas.beans.factory.CampaignStatEntryFactory;
import org.agnitas.beans.impl.CampaignImpl;
import org.agnitas.beans.impl.MailingBaseImpl;
import org.agnitas.beans.impl.MailinglistImpl;
import org.agnitas.dao.CampaignDao;
import org.agnitas.dao.TargetDao;
import org.agnitas.dao.impl.mapper.CampaignRowMapper;
import org.agnitas.stat.CampaignStatEntry;
import org.agnitas.target.Target;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.SafeString;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.*;

/**
 *
 * @author Martin Helff, Nicole Serek, Markus Unger
 */
public class CampaignDaoImpl implements CampaignDao {
	
	private static final transient Logger logger = Logger.getLogger( CampaignDaoImpl.class);

    protected DataSource dataSource;
    protected CampaignStatEntryFactory campaignStatEntryFactory;

    @Override
	public Campaign getCampaign(int campaignID, int companyID) {
        if(campaignID==0) {
            return null;
        }
        
        String query  = "select campaign_id, company_id, shortname, description from campaign_tbl where campaign_id = ? and company_id = ? ";
        JdbcTemplate template = getJdbcTemplate();
        return (Campaign) template.queryForObject(query, new Object[]{campaignID, companyID }, new CampaignRowMapper() );
    }
    
    @Override
    public CampaignStats getStats(boolean useMailtracking, Locale aLocale, LinkedList<Integer> mailingIDs, Campaign campaign, TargetDao targetDao, String mailingSelection, int targetID) {
    	CampaignImpl campaignImpl = new CampaignImpl();	// create Campaign Instance
    	CampaignStats stats = campaignImpl.getCampaignStats(); // get stats.
        
    	String uniqueStr = "";
        // boolean useMailtracking = req.getSession().getAttribute("use_mailtracking").equals("1");
        Target aTarget = null;
        StringBuffer mailIDs = null;
        boolean isFirst = true;
        String csv = "";

        // aLocale

        csv = "\"" + SafeString.getLocaleString("CampaignStats", aLocale) + "\"\r\n\r\n";

        // if we dont have the mailingIDs as parameter, we need to get them.
        if (mailingIDs == null) {        	
        	mailingIDs = getMailingIDs(campaign.getCompanyID(), campaign.getId());
        }
        
        if(mailingIDs != null) {
            Iterator<Integer> aIt = mailingIDs.iterator();
            Integer tmpInt = null;
            while(aIt.hasNext()) {
                tmpInt = aIt.next();
                if(mailIDs == null) {
                    mailIDs = new StringBuffer();
                }
                if(isFirst) {
                    mailIDs.append(tmpInt.intValue());
                    isFirst = false;
                } else {
                    mailIDs.append(", " + tmpInt.intValue());
                }
            }
        } 

        if(mailIDs != null) {
//            setMailingSelection(new String(mailIDs.toString()));
        	mailingSelection = mailIDs.toString();
        } else {        	
        	mailingSelection= null;
        }

        // * * * * * * * * * * * *
        // *  LOAD TARGET GROUP  *
        // * * * * * * * * * * * *

        //woher kommt die targetID - vorher nicht deklariert!!!
        if(targetID != 0) {
            aTarget = targetDao.getTarget(targetID, campaign.getCompanyID());
            csv += "\"" + SafeString.getLocaleString("target.Target", aLocale) + "\";\"" + aTarget.getTargetName() + "\"\r\n\r\n";

        } else {
            csv += "\"" + SafeString.getLocaleString("target.Target", aLocale) + "\";\"" + SafeString.getLocaleString("statistic.All_Subscribers", aLocale) + "\"\r\n\r\n";
        }

        // * * * * * * * * * *
        // *  SET NETTO SQL  *
        // * * * * * * * * * *
        if(campaign.isNetto()) {
            uniqueStr = "distinct ";
            csv += "\"" + SafeString.getLocaleString("statistic.Unique_Clicks", aLocale) + "\"\r\n\r\n";
        }

        stats = loadMailingNames(stats, campaign, mailingSelection);
        stats = loadClicks(stats, uniqueStr, mailingSelection);
        stats = loadOpenedMails(stats, campaign, aTarget, useMailtracking, mailingSelection);
        stats = loadOptout(stats, campaign, aTarget, useMailtracking, mailingSelection);

        // get mailing_id's from Hashtable
        Enumeration keys = stats.getMailingData().keys();
        int aktMailingID = 0;
        int aktBounces = 0;
        // loop over every mailing_id
        while(keys.hasMoreElements()) {
            aktBounces = 0;
            aktMailingID = ((Number) keys.nextElement()).intValue();

            stats = loadBounces(stats, campaign, aTarget, useMailtracking, aktMailingID, aktBounces);

            //  T O T A L  S E N T  M A I L S  *

            // * * * * * * * * * *
            // case mail_tracking:
            if(useMailtracking) {
                stats = loadTotalSentMailtracking(stats, campaign, aTarget, aktMailingID);
            } else {
                // * * * * * * * * * * * *
                // case no_mail_tracking:
                // look for world mailing:
            	stats = loadTotalSent(stats, campaign, aTarget, aktMailingID);
            }

        }

        // look for max values and set them to 1 if 0:
        if(stats.getMaxClicks() == 0) {
            stats.setMaxClicks(1);
        }
        if(stats.getMaxBounces() == 0) {
            stats.setMaxBounces(1);
        }
        if(stats.getMaxOpened() == 0) {
            stats.setMaxOpened(1);
        }
        if(stats.getMaxOptouts() == 0) {
            stats.setMaxOptouts(1);
        }
        if(stats.getMaxSubscribers() == 0) {
            stats.setMaxSubscribers(1);
        }
        
        campaign.setCsvfile(csv);

        return stats;
    }

    private CampaignStats loadMailingNames (CampaignStats stats, Campaign campaign, String mailingSelection) {
    	JdbcTemplate jdbc = getJdbcTemplate();
    	CampaignStatEntry aktEntry = null;
    	StringBuffer mailIDs = null;
    	String sql = "select mailing_id, shortname, description from mailing_tbl where company_id=" + campaign.getCompanyID() + " and campaign_id=" + campaign.getId() + " and deleted<>1 and is_template=0 order by mailing_id desc";
        if(mailingSelection != null) {
            sql="select mailing_id, shortname, description from mailing_tbl where company_id="+campaign.getCompanyID()+" AND MAILING_ID IN ("+mailingSelection+") order by mailing_id desc";
        }
        
        if( logger.isInfoEnabled()) {
        	logger.info( "MailingNameQuery: " + sql);
        }

        try {
            List list = jdbc.queryForList(sql);
            Iterator i = list.iterator();

            boolean isFirst = true;
            mailIDs = new StringBuffer();
            while(i.hasNext()) {
                Map map = (Map) i.next();
                Integer id = new Integer(((Number) map.get("mailing_id")).intValue());
                // create CampaignStatEntry...
                aktEntry = campaignStatEntryFactory.newCampaignStatEntry();
                aktEntry.setShortname((String) map.get("shortname"));
                if(map.get("description") != null) {
                    aktEntry.setName((String) map.get("description"));
                } else {
                    aktEntry.setName(" ");
                }
                //...and put it into the Hashtable
                stats.getMailingData().put(id, aktEntry);
                if(isFirst) {
                    mailIDs.append(id.intValue());
                    isFirst = false;
                } else {
                    mailIDs.append(", "+id.intValue());
                }
            }

        } catch (Exception e) {
        	logger.error( "MailingNameQuery error1: " + e.getMessage(), e);
        	AgnUtils.sendExceptionMail("sql:" + sql, e);
        }

        // TODO was passiert mit mailingSelection an dieser Stelle?
        if(mailingSelection == null) {
        	mailingSelection = mailIDs.toString();
        }
        return stats;
    }
    
    // load the clicks for one Campaign.    
    protected CampaignStats loadClicks (CampaignStats stats, String uniqueStr, String mailingSelection) {
     	   	
    	// create Array with needed SQL-Statements. Needed as Array for Overwriting method in Com
    	// because in Com we have more Parameters.
    	String[] sqlParts = {uniqueStr, mailingSelection};
    	
    	// get SQL-String
    	String sql = getLoadClickSQL(sqlParts);
    	
    	// call the helper method.
    	return loadClicksHelper(stats, sql);        
    }
    
    // helper method for loadClicks. Reason: we overwrite loadClicks in CSS.
    protected CampaignStats loadClicksHelper (CampaignStats stats, String sql) {
    	JdbcTemplate jdbc = getJdbcTemplate();
    	CampaignStatEntry aktEntry = null;
    	
    	if( logger.isInfoEnabled()) {
    		logger.info( "TotalClicksQuery: " + sql);
    	}
    	
        try {
            List list = jdbc.queryForList(sql);
            Iterator i = list.iterator();

            while(i.hasNext()) {
                Map map = (Map) i.next();
                Integer mailingID = new Integer(((Number) map.get("mailing_id")).intValue());
                int clicks = ((Number) map.get("amount")).intValue();

                // get CampaignStatEntry...
                aktEntry = (CampaignStatEntry) stats.getMailingData().get(mailingID);
                //...fill in total clicks...
                aktEntry.setClicks(clicks);
                //...put it back...
                stats.getMailingData().put(mailingID, aktEntry);
                //...and add value to global value:
                stats.setClicks(stats.getClicks() + clicks);
                // look for max. value
                if(clicks > stats.getMaxClicks()) {
                    stats.setMaxClicks(clicks);
                }
            }
        } catch (Exception e) {
        	logger.error( "TotalClicksQuery error1: " + e.getMessage(), e);        	
        	AgnUtils.sendExceptionMail("sql:" + sql, e);
        }        
    	return stats;
    }

    private CampaignStats loadOpenedMails (CampaignStats stats, Campaign campaign, Target aTarget, boolean useMailtracking, String mailingSelection) {
JdbcTemplate jdbc = getJdbcTemplate();
    	CampaignStatEntry aktEntry = null;
    	String sql = "select onepix.mailing_id as mailing_id, count(onepix.customer_id) as amount from onepixel_log_tbl onepix";
        if(useMailtracking && aTarget != null && aTarget.getId() != 0)
            sql += ", customer_" + campaign.getCompanyID() + "_tbl cust";

        sql += " where onepix.mailing_id in (" + mailingSelection + ")";
        if(useMailtracking && aTarget != null && aTarget.getId() != 0)
            sql += " and ((" + aTarget.getTargetSQL() + ") and cust.customer_id=onepix.customer_id)";
        sql += " group by onepix.mailing_id";
        
        if( logger.isInfoEnabled()) {
        	logger.info( "OnePixeLQueryByCust: " + sql);
        }

        try {
            List list = jdbc.queryForList(sql);
            Iterator i = list.iterator();

            while(i.hasNext()) {
                Map map = (Map) i.next();
                Integer mailingID = new Integer(((Number) map.get("mailing_id")).intValue());
                int opened = ((Number) map.get("amount")).intValue();

                // get CampaignStatEntry...
                aktEntry = (CampaignStatEntry) stats.getMailingData().get(mailingID);
                //...fill in opened mails...
                aktEntry.setOpened(opened);
                //...put it back...
                stats.getMailingData().put(mailingID, aktEntry);
                //...and add value to global value:
                stats.setOpened(stats.getOpened() + opened);
                // check for max. value:
                if(opened > stats.getMaxOpened()) {
                    stats.setMaxOpened(opened);
                }
            }
        } catch (Exception e) {
        	logger.error( "OnePixelQueryByCust error1: " + e.getMessage(), e);
        	AgnUtils.sendExceptionMail("sql:" + sql, e);
        }
        return stats;
	}

	private CampaignStats loadOptout (CampaignStats stats, Campaign campaign, Target aTarget, boolean useMailtracking, String mailingSelection) {
		JdbcTemplate jdbc = getJdbcTemplate();
    	CampaignStatEntry aktEntry = null;
		String sql = "select bind.exit_mailing_id as mailing_id, count(bind.customer_id) as amount from customer_" + campaign.getCompanyID() + "_binding_tbl bind";
        if(useMailtracking && aTarget != null && aTarget.getId() != 0)
            sql += ", customer_" + campaign.getCompanyID() + "_tbl cust";
        sql += " where bind.exit_mailing_id in ("+mailingSelection+")";
        if(useMailtracking && aTarget != null && aTarget.getId() != 0)
            sql += " and ((" + aTarget.getTargetSQL() + ") and cust.customer_id=bind.customer_id)";
        sql += " and bind.user_status in (" + BindingEntry.USER_STATUS_ADMINOUT + ", " + BindingEntry.USER_STATUS_OPTOUT + ") group by bind.exit_mailing_id";

        if( logger.isInfoEnabled()) {
        	logger.info( "OutoutQuery: " + sql);
        }

        try {
            List list=jdbc.queryForList(sql);
            Iterator i=list.iterator();

            while(i.hasNext()) {
                Map map=(Map) i.next();
                Integer mailingID=new Integer(((Number) map.get("mailing_id")).intValue());
                int optouts=((Number) map.get("amount")).intValue();

                // get CampaignStatEntry...
                aktEntry = (CampaignStatEntry) stats.getMailingData().get(mailingID);
                //...fill in optouts...
                aktEntry.setOptouts(optouts);
                //...put it back...
                stats.getMailingData().put(mailingID, aktEntry);
                //...and add value to global value:
                stats.setOptouts(stats.getOptouts() + optouts);
                // check for max. value:
                if(optouts > stats.getMaxOptouts()) {
                    stats.setMaxOptouts(optouts);
                }

            }
        } catch (Exception e) {
        	logger.error( "OutputQuery error1: " + e.getMessage(), e);
        	AgnUtils.sendExceptionMail("sql:" + sql, e);
        }
        return stats;
	}

	private CampaignStats loadBounces (CampaignStats stats, Campaign campaign, Target aTarget, boolean useMailtracking, int aktMailingID, int aktBounces) {
		JdbcTemplate jdbc = getJdbcTemplate();
    	CampaignStatEntry aktEntry = null;
		String sql = "select bind.mailinglist_id as mailinglist_id, count(bind.customer_id) as amount from customer_" + campaign.getCompanyID() + "_binding_tbl bind";
        if(useMailtracking && aTarget != null && aTarget.getId() != 0)
            sql += ", customer_" + campaign.getCompanyID() + "_tbl cust";
        sql += " where bind.exit_mailing_id=" + aktMailingID;
        if(useMailtracking && aTarget != null && aTarget.getId() != 0)
            sql += " and ((" + aTarget.getTargetSQL() + ") and cust.customer_id=bind.customer_id)";
        sql += " and bind.user_status = " + BindingEntry.USER_STATUS_BOUNCED + " group by bind.mailinglist_id";

        if( logger.isInfoEnabled()) {
        	logger.info( "BounceQuery: " + sql);
        }
        
        try {
            List list=jdbc.queryForList(sql);
            Iterator i=list.iterator();

            // get entry...
            aktEntry = (CampaignStatEntry) stats.getMailingData().get(new Integer(aktMailingID));
            while(i.hasNext()) {
                Map map=(Map) i.next();
                int bounces=((Number) map.get("amount")).intValue();

                if(bounces > aktBounces) {
                    aktBounces = bounces;
                }
            }
            //...set value...
            aktEntry.setBounces(aktBounces);
            //...put it back...
            stats.getMailingData().put(new Integer(aktMailingID), aktEntry);
            //...and add value to global value:
            stats.setBounces(stats.getBounces() + aktBounces);
            // check for max. value:
            if(aktBounces>stats.getMaxBounces()) {
                stats.setMaxBounces(aktBounces);
            }

        } catch (Exception e) {
        	logger.error( "BounceQuery error1: " + e.getMessage(), e);
        	AgnUtils.sendExceptionMail("sql:" + sql, e);
        }
        return stats;
	}
	
	private CampaignStats loadTotalSentMailtracking (CampaignStats stats, Campaign campaign, Target aTarget, int aktMailingID) {
		JdbcTemplate jdbc = getJdbcTemplate();
    	CampaignStatEntry aktEntry = null;
		String mailtrackTbl = AgnUtils.isOracleDB() ? "mailtrack_" + campaign.getCompanyID() + "_tbl" : "mailtrack_tbl";
		String sql = "select count(distinct mailtrack.customer_id) from " + mailtrackTbl  + " mailtrack";
        if(aTarget != null && aTarget.getId() != 0)
            sql += ", customer_" + campaign.getCompanyID() + "_tbl cust";
        sql += " where mailtrack.status_id in (select status_id from maildrop_status_tbl where mailing_id=" + aktMailingID;
        sql += " and company_id=" + campaign.getCompanyID() + ")";
        if(aTarget != null && aTarget.getId() != 0)
            sql += " and ((" + aTarget.getTargetSQL() + ") and cust.customer_id=mailtrack.customer_id)";
        
        if( logger.isInfoEnabled()) {
        	logger.info( "mailtrackQuery: " + sql);
        }
        
        try {
            long subscribers=jdbc.queryForLong(sql);
            // get CampaignStatEntry...
            aktEntry = (CampaignStatEntry)(stats.getMailingData().get(new Integer(aktMailingID)));
            //...fill in subscribers...
            aktEntry.setTotalMails((int) subscribers);
            //...write it back...
            stats.getMailingData().put(new Integer(aktMailingID), aktEntry);
            //... and add value to global value:
            stats.setSubscribers(stats.getSubscribers() + (int) subscribers);
            // check for max. value:
            if(subscribers > stats.getMaxSubscribers()) {
                stats.setMaxSubscribers((int) subscribers);
            }
        } catch (Exception e) {
        	logger.error( "mailtrackQuery error1: " + e.getMessage(), e);
        	AgnUtils.sendExceptionMail("sql:" + sql, e);
        }
		return stats;
	}
	
	private CampaignStats loadTotalSent (CampaignStats stats, Campaign campaign, Target aTarget, int aktMailingID) {
		JdbcTemplate jdbc = getJdbcTemplate();
    	CampaignStatEntry aktEntry = null;
    	long totalAdmMails = 0;
        long totalMails = 0;
		String sql = "select sum(no_of_mailings) from mailing_account_tbl where mailing_id=";
        sql += aktMailingID + " and company_id=" + campaign.getCompanyID() + " and status_field in ('W', 'C', 'R')";
        
        if( logger.isInfoEnabled()) {
        	logger.info( "SentMailsQuery: " + sql);
        }
        
        try {
            totalMails=jdbc.queryForLong(sql);

        } catch (Exception e) {
            totalMails=0;

            logger.error( "SentMailsQuery error1: " + e.getMessage(), e);
        }


        // look for admin or test mailing only:
        String sentAdmMailsQuery = "select max(tmp.mailing_count ) as amount from (select sum(no_of_mailings) as mailing_count from mailing_account_tbl mac where mac.mailing_id=";
        sentAdmMailsQuery += aktMailingID + " and mac.company_id=" + campaign.getCompanyID() + " and mac.status_field in ('A', 'T') group by mac.change_date) tmp";

        if( logger.isInfoEnabled()) {
        	logger.info( "SentAdmMailsQuery: " + sentAdmMailsQuery);
        }
        
        try {
            totalAdmMails=jdbc.queryForLong(sentAdmMailsQuery);
        } catch (Exception e) {
            totalAdmMails=0;
            logger.error( "SentAdmMailsQuery error1: " + e.getMessage(), e);
        }

        // take the bigger value for displaying:
        // get CampaignStatEntry...
        aktEntry = (CampaignStatEntry)(stats.getMailingData().get(new Integer(aktMailingID)));
        //...fill in subscribers...
        if(totalAdmMails>totalMails) {
            aktEntry.setTotalMails((int) totalAdmMails);
            // add value to global value:
            stats.setSubscribers(stats.getSubscribers() + (int) totalAdmMails);
            // check for max. value:
            if(totalAdmMails>stats.getMaxSubscribers()) {
                stats.setMaxSubscribers((int) totalAdmMails);
            }

        } else {
            aktEntry.setTotalMails((int) totalMails);
            // add value to global value:
            stats.setSubscribers(stats.getSubscribers() + (int) totalMails);
            // check for max. value:
            if(totalMails>stats.getMaxSubscribers()) {
                stats.setMaxSubscribers((int) totalMails);
            }

        }
        //...and write it back:
        stats.getMailingData().put(new Integer(aktMailingID), aktEntry);
		return stats;
	}
    
    // this helper method returns the mailing-IDs from a Campaign.
    
    private LinkedList<Integer> getMailingIDs(int compID, int campID) {
    	LinkedList<Integer> mailingIDs = new LinkedList<Integer>();
    	// get jdbc-Template
    	JdbcTemplate jdbc = getJdbcTemplate();
    	// StringBuffer mailIDs = null;
    	
    	String sql = "select mailing_id, shortname, description from mailing_tbl where company_id=" + compID + " and campaign_id=" + campID + " and deleted<>1 and is_template=0 order by mailing_id desc";
       
        if( logger.isInfoEnabled()) {
        	logger.info( "MailingNameQuery: " + sql);
        }
        
        try {
            List list = jdbc.queryForList(sql);
            Iterator i = list.iterator();
       
            // mailIDs = new StringBuffer();
            Map map = null;
            while(i.hasNext()) {
                map = (Map) i.next();
                Integer id = new Integer(((Number) map.get("mailing_id")).intValue());
                mailingIDs.add(id);             
            }
        } catch (Exception e) {
        	logger.error( "MailingNameQuery error1: " + e.getMessage(), e);
        	AgnUtils.sendExceptionMail("sql:" + sql, e);
        }
    	return mailingIDs;
    }
    
    /*
     * returns the SQL-Statement for loading the clicks.
     */
    protected String getLoadClickSQL(String[] sqlParts) {
    	String resultString = "";
    	// the following strings are defined for creating a SQL Statement
    	// between the first and the second one comes an additional Variable, thats the reason
    	// why we have three Strings here.
    	String SQL_Clicks1 = "select rdir.mailing_id as mailing_id, count(";
    	String SQL_Clicks2 = " rdir.customer_id) as amount from rdir_log_tbl rdir, rdir_url_tbl url where rdir.mailing_id in (";
    	String SQL_Clicks3 = ") and rdir.url_id=url.url_id and url.relevance=0 group by rdir.mailing_id";
    	resultString = SQL_Clicks1 + sqlParts[0] + SQL_Clicks2 + sqlParts[1] + SQL_Clicks3;    	
    	return resultString;
    }
    
    @Override
	public boolean delete(Campaign campaign) {
		JdbcTemplate template = getJdbcTemplate();
		String deleteQuery = "delete from campaign_tbl where campaign_id = ? and company_id = ?";
		int numberofRows  = template.update(deleteQuery, new Object[]{campaign.getId(), campaign.getCompanyID()});
		return numberofRows > 0;
	}

    @Override
    public List<MailingBase> getCampaignMailings(int campaignID, int companyID) {
        JdbcTemplate jdbc = getJdbcTemplate();

        String sql = "SELECT a.mailing_id, a.shortname, a.description, b.shortname AS listname, (SELECT " +
                "min(c." + AgnUtils.changeDateName() + ")" +
                " FROM mailing_account_tbl c WHERE a.mailing_id=c.mailing_id AND c.status_field=\'W\') AS senddate FROM mailing_tbl a, mailinglist_tbl b WHERE a.company_id=?"+
                " AND a.campaign_id=? AND a.deleted<>1 AND a.is_template=0 AND a.mailinglist_id=b.mailinglist_id ORDER BY senddate DESC, mailing_id DESC";
        
        List<Map> tmpList = jdbc.queryForList(sql, new Object[]{ companyID, campaignID });
        List<MailingBase> result = new ArrayList<MailingBase>();

        for (Map row : tmpList) {
            MailingBase newBean = new MailingBaseImpl();
            int mailingID = ((Number) row.get("mailing_id")).intValue();
            newBean.setId(mailingID);
            newBean.setShortname((String) row.get("shortname"));
            newBean.setDescription((String) row.get("description"));
            Mailinglist mailinglist = new MailinglistImpl();
            mailinglist.setShortname((String) row.get("listname"));
            newBean.setMailinglist(mailinglist);
            newBean.setSenddate( (Date) row.get("senddate"));

            result.add(newBean);
        }

        return result;
    }

    @Override
    public List<Campaign> getCampaignList(int companyID, String sort, int order) {
        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        String orderString = sort + " "+(order == 2 ? "DESC" : "ASC");
        String sqlStatement = "SELECT campaign_id, shortname, description FROM campaign_tbl WHERE company_id=? ORDER BY " + orderString;
        List<Map> tmpList = jdbcTemplate.queryForList(sqlStatement, new Object [] {companyID});

        List<Campaign> result = new ArrayList<Campaign>();
        for (Map row : tmpList) {

            Campaign campaign = new CampaignImpl();
            campaign.setId(((Number) row.get("CAMPAIGN_ID")).intValue());
            campaign.setShortname((String) row.get("SHORTNAME"));
            campaign.setDescription((String) row.get("DESCRIPTION"));
            result.add(campaign);

        }
        return result;
    }

    @Override
    public int save(Campaign campaign) {
		JdbcTemplate template = getJdbcTemplate();
		if( campaign.getId() == 0 ) {
			String query = "insert into campaign_tbl (company_id, shortname,  description ) values ( ?, ?, ? )  ";
			template.update(query, new Object[]{campaign.getCompanyID(), campaign.getShortname(), campaign.getDescription()});
			
			String newIDQuery = "select last_insert_id()";
			int newID = template.queryForInt(newIDQuery);
			return newID;
			
		}
		else {
			String query = " update campaign_tbl set company_id = ?, shortname = ? , description = ?  where campaign_id = ?";
			template.update(query,new Object[]{campaign.getCompanyID(), campaign.getShortname(), campaign.getDescription(), campaign.getId()}); 
			return campaign.getId();
		}
	}
    
	protected JdbcTemplate getJdbcTemplate() {
		return new JdbcTemplate(dataSource);
	}

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setCampaignStatEntryFactory(CampaignStatEntryFactory campaignStatEntryFactory) {
        this.campaignStatEntryFactory = campaignStatEntryFactory;
    }
}
