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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.agnitas.dao.TargetDao;
import org.agnitas.target.Target;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.SafeString;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

public class DomainStatImpl implements org.agnitas.stat.DomainStat {
    
    private static final long serialVersionUID = 4471064211444932400L;
	protected int listID;
    protected int companyID;
    protected int targetID;
    protected   int total;
    protected   int rest;
    protected   int lines;
    protected   int sum;
    protected   int max;
    protected   String csvfile = "";        // String for csv file download
    protected LinkedList domains;
    protected LinkedList subscribers;
    
    /** Holds value of property maxDomains. */
    protected int maxDomains = 20;
    
    /** CONSTRUCTOR */
    public DomainStatImpl() {
        
    }
    
    public boolean getStatFromDB(TargetDao targetDao, DataSource dataSource, HttpServletRequest request) {
        boolean returnCode=true;
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String targetSQL = "";
        
        lines       = 0;
        sum         = 0;
        domains     = new LinkedList();
        subscribers = new LinkedList();
        
        csvfile += SafeString.getLocaleString("statistic.domains", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\n";
        csvfile += "\n";
        
        // 1. get target group SQL:
        if(targetID!=0) {
            Target aTarget=targetDao.getTarget(this.targetID, this.companyID);
            if(aTarget.getId()!=0) {
                if(listID != 0) {
                    targetSQL = " AND (" + aTarget.getTargetSQL() + ")";
                } else {
                    targetSQL = " WHERE (" + aTarget.getTargetSQL() + ")";
                }
                csvfile += SafeString.getLocaleString("target.Target", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ":;" + aTarget.getTargetName() + "\n";
                AgnUtils.logger().info("getStatFromDB: target loaded " + targetID);
            } else {
                csvfile += SafeString.getLocaleString("target.Target", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ":;" + SafeString.getLocaleString("statistic.All_Subscribers", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\n";
                AgnUtils.logger().info("getStatFromDB: could not load target " + targetID);
            }
        }
        
        // 2. how many total subscribers ?
        String sqlCount = "SELECT COUNT(cust.customer_id) "
                + "FROM customer_" + companyID + "_tbl cust, customer_" + companyID + "_binding_tbl bind";
        
        if(listID != 0) {
            
            sqlCount += " WHERE bind.mailinglist_id = " + listID;
            sqlCount += " AND cust.customer_id = bind.customer_id ";
            sqlCount += " AND bind.user_status =1";
            sqlCount += targetSQL;
        } else {
            if(targetID==0) {                
                sqlCount += " WHERE cust.customer_id = bind.customer_id ";
                sqlCount += " AND bind.user_status =1";
                csvfile += SafeString.getLocaleString("Mailinglist", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ":;" + SafeString.getLocaleString("statistic.All_Mailinglists", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\n";
            } else {
                sqlCount += targetSQL;
                sqlCount += " AND cust.customer_id = bind.customer_id ";
                sqlCount += " AND bind.user_status =1";
                csvfile += SafeString.getLocaleString("Mailinglist", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ":;" + SafeString.getLocaleString("statistic.All_Mailinglists", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\n";
            }
        }
       
        try { 
            total= jdbcTemplate.queryForInt(sqlCount);
        } catch(Exception e) {
        	AgnUtils.sendExceptionMail("sql:" + sqlCount, e);
            AgnUtils.logger().error("getStatFromDB: "+e);
            AgnUtils.logger().error("SQL: "+sqlCount);
        }
        
        // 3. get the top domains:
        String sqlStmt;
        if(listID != 0) {
            sqlStmt = "SELECT COUNT(cust.customer_id) AS tmpcount, SUBSTR(email, INSTR(email, '@')) AS tmpsub from customer_" + companyID + "_tbl cust , customer_" + companyID + "_binding_tbl bind WHERE cust.customer_id = bind.customer_id AND bind.user_status =1 AND bind.MAILINGLIST_ID =" + listID + targetSQL + " group by tmpsub order by tmpcount desc LIMIT "+this.maxDomains;
        } else {
            if(targetID==0) {
                sqlStmt = "SELECT COUNT(cust.customer_id) AS tmpcount, SUBSTR(cust.email, INSTR(cust.email, '@')) AS tmpsub from customer_" + companyID + "_tbl cust , customer_" + companyID + "_binding_tbl bind WHERE cust.customer_id = bind.customer_id AND bind.user_status =1 group by tmpsub order by tmpcount desc LIMIT "+this.maxDomains;
            } else {
                sqlStmt = "SELECT COUNT(cust.customer_id) AS tmpcount, SUBSTR(cust.email, INSTR(cust.email, '@')) AS tmpsub from customer_" + companyID + "_tbl cust , customer_" + companyID + "_binding_tbl bind " + targetSQL + " AND cust.customer_id = bind.customer_id AND bind.user_status =1 group by tmpsub order by tmpcount desc LIMIT "+this.maxDomains;
            }
        }
        
        csvfile += "\n";
        csvfile += SafeString.getLocaleString("statistic.domain", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ":;" + SafeString.getLocaleString("Recipients", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\n";
        try { 
            jdbcTemplate.query(sqlStmt, new Object[] {}, new RowCallbackHandler() {
                public void processRow(ResultSet rs) throws SQLException {
                    lines++;
                    domains.add(rs.getString(2));
                    subscribers.add(new Integer(rs.getInt(1)));
                    sum += rs.getInt(1);
                    csvfile += rs.getString(2) + ";" + rs.getString(1) + "\n";
                }
            }
            );
        } catch(Exception e) {
        	AgnUtils.sendExceptionMail("sql:" + sqlStmt, e);
            AgnUtils.logger().error("getStatFromDB(query): "+e);
            AgnUtils.logger().error("SQL: "+sqlStmt);
        }

        rest = total - sum;
        
        csvfile += "\n";
        csvfile += SafeString.getLocaleString("statistic.Other", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ":;" + rest + "\n";
        csvfile += "\n";
        csvfile += SafeString.getLocaleString("statistic.Total", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ":;" + total + "\n";
        
        return returnCode;
    }
    
    // SETTER:
    
    public void setCompanyID(int id) {
        companyID=id;
    }
    
    public void setTargetID(int id) {
        targetID=id;
    }
    
    public void setListID(int id) {
        listID=id;
    }
    
    public void setTotal(int total) {
        this.total = total;
    }
    
    public void setRest(int rest) {
        this.rest = rest;
    }
    
    public void setLines(int lines) {
        this.lines = lines;
    }
    
    public void setDomains(java.util.LinkedList domains) {
        this.domains = domains;
    }
    
    public void setSubscribers(java.util.LinkedList subscribers) {
        this.subscribers = subscribers;
    }
    
    public void setCsvfile(String file) {
        this.csvfile = file;
    }
    
    public void setMaxDomains(int maxDomains) {
        this.maxDomains = maxDomains;
    }
    
    // GETTER:
    
    public int getListID() {
        return listID;
    }
    
    public int getTargetID() {
        return targetID;
    }
    
    public int getCompanyID() {
        return companyID;
    }
    
    public int getTotal() {
        return total;
    }
    
    public int getRest() {
        return rest;
    }
    
    public int getLines() {
        return lines;
    }
    
    public java.util.LinkedList getDomains() {
        return domains;
    }
    
    public java.util.LinkedList getSubscribers() {
        return subscribers;
    }
    
    public int getMaxDomains() {
        return this.maxDomains;
    }
    
    public String getCsvfile() {
        return this.csvfile;
    }
}
