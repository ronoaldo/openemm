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

import javax.sql.DataSource;

import org.agnitas.dao.TargetDao;
import org.agnitas.stat.IPStat;
import org.agnitas.target.Target;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.SafeString;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;


public class IPStatImpl implements IPStat {

    private static final long serialVersionUID = 6040512926344656410L;
	/**
     * ID of the mailinglist for which the statistical data should be calculated.
     */
    protected int listID;
    /**
     * Company ID of the account
     */
    protected int companyID;
    /**
     * ID of the target group for which the statistical data should be calculated.
     */
    protected int targetID;
    /**
     * total number of subscribers found
     */
    protected int total;
    /**
     * number of subscribers with the IP adress not explicitely named in the list
     */
    protected int rest;
    /**
     * number of IP adresses in the list
     */
    protected int lines;
    protected int sum;
    protected int max;
    protected int biggest;
    protected int maxIPs = 20;
    /**
     * contents of the csv file for download
     */
    protected String csvfile = "";        // String for csv file download
    /**
     * contains the IP adresses diplayed
     */
    protected LinkedList ips;
    /**
     * contains the numbers of recipients displayed
     */
    protected LinkedList subscribers;

	protected DataSource dataSource;
	protected TargetDao targetDao;


	/** CONSTRUCTOR */
    public IPStatImpl() {

    }

    /**
     * retrieves the statistical data from the db.
     * @return return code
     */
    public boolean getStatFromDB(Locale locale) {
        boolean returnCode=true;

        String targetSQL = "";

        lines       = 0;
        sum         = 0;
        ips         = new LinkedList();
        subscribers = new LinkedList();

        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        csvfile += SafeString.getLocaleString("statistic.IPStats", locale) + "\n";
        csvfile += "\n";

        // 1. get target group SQL:
        if(targetID!=0) {
            Target aTarget=targetDao.getTarget(targetID, companyID);

            if(aTarget.getId()!=0) {
                if(listID != 0) {
                    targetSQL = " AND (" + aTarget.getTargetSQL() + ")";
                } else {
                    targetSQL = " WHERE (" + aTarget.getTargetSQL() + ")";
                }
                csvfile += SafeString.getLocaleString("target.Target", locale) + ":;" + aTarget.getTargetName() + "\n";
                AgnUtils.logger().info("getStatFromDB: target loaded " + targetID);
            } else {
                AgnUtils.logger().info("getStatFromDB: could not load target " + targetID);
            }
        }

        // 2. how many total subscribers ?
        String sqlCount;
        if(listID != 0) {
            sqlCount = "SELECT COUNT(cust.customer_id) FROM customer_" + companyID + "_tbl cust, customer_" + companyID +"_binding_tbl bind WHERE cust.customer_id = bind.customer_id AND bind.mailinglist_id = " + listID + targetSQL;

        } else {
            sqlCount = "SELECT COUNT(cust.customer_id) FROM customer_" + companyID + "_tbl cust " + targetSQL;
            csvfile += SafeString.getLocaleString("Mailinglist", locale) + ":;" + SafeString.getLocaleString("statistic.All_Mailinglists", locale) + "\n";
        }
        try {
            total= jdbc.queryForInt(sqlCount);
        } catch(Exception e) {
        	AgnUtils.sendExceptionMail("sql:" + sqlCount, e);
            AgnUtils.logger().error("getStatFromDB: "+e);
            AgnUtils.logger().error("SQL: "+sqlCount);
            total=0;
        }

        // 3. get the top IPs:
        String sqlStmt;
        if(listID != 0) {
            sqlStmt = "SELECT count(cust.customer_id) as tmpcount, substr(bind.user_remark, 12) as tmpsub from customer_" + companyID + "_tbl cust , customer_" + companyID + "_binding_tbl bind WHERE cust.customer_id = bind.customer_id AND bind.user_remark like 'Opt-In-IP:%' AND bind.mailinglist_id =" + listID + targetSQL + " GROUP BY tmpsub ORDER BY tmpcount desc LIMIT "+this.maxIPs;
        } else {
            if(targetID==0) {
                sqlStmt = "SELECT count(cust.customer_id) as tmpcount, substr(bind.user_remark, 12) as tmpsub from customer_" + companyID + "_tbl cust , customer_" + companyID + "_binding_tbl bind WHERE cust.customer_id = bind.customer_id AND bind.user_remark like 'Opt-In-IP:%' GROUP BY tmpsub ORDER BY tmpcount desc LIMIT "+this.maxIPs;
            } else {
                sqlStmt = "SELECT count(cust.customer_id) as tmpcount, substr(bind.user_remark, 12) as tmpsub from customer_" + companyID + "_tbl cust , customer_" + companyID + "_binding_tbl bind  " + targetSQL + " AND cust.customer_id = bind.customer_id AND bind.user_remark like 'Opt-In-IP:%' GROUP BY tmpsub ORDER BY tmpcount desc LIMIT "+this.maxIPs;
            }
        }

        csvfile += "\n";
        csvfile += SafeString.getLocaleString("statistic.IPAddress", locale) + ":;" + SafeString.getLocaleString("Recipients", locale) + "\n";
        try {
            jdbc.query(sqlStmt, new Object[] {}, new RowCallbackHandler() {
                public void processRow(ResultSet rs) throws SQLException {
                    if(lines==0) {
                        biggest=rs.getInt(1);
                    }
                    lines++;
                    ips.add(rs.getString(2));
                    subscribers.add(new Integer(rs.getInt(1)));
                    sum += rs.getInt(1);
                    csvfile += rs.getString(2) + ";" + rs.getString(1) + "\n";
                }
            }
            );
        } catch(Exception e) {
        	AgnUtils.sendExceptionMail("sql:" + sqlStmt, e);
            AgnUtils.logger().error("getStatFromDB: "+e);
            AgnUtils.logger().error("SQL: "+sqlCount);
        }
        rest = total - sum;


        csvfile += "\n";
        csvfile += SafeString.getLocaleString("statistic.Other", locale) + ":;" + rest + "\n";
        csvfile += "\n";
        csvfile += SafeString.getLocaleString("statistic.Total", locale) + ":;" + total + "\n";

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

    public void setIps(java.util.LinkedList ips) {
        this.ips = ips;
    }

    public void setSubscribers(java.util.LinkedList subscribers) {
        this.subscribers = subscribers;
    }

    public void setCsvfile(String file) {
        this.csvfile = file;
    }

    public void setMaxIPs(int maxIPs) {
        this.maxIPs = maxIPs;
    }

    public void setBiggest(int biggest) {
        this.biggest = biggest;
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

    public java.util.LinkedList getIps() {
        return ips;
    }

    public java.util.LinkedList getSubscribers() {
        return subscribers;
    }

    public int getMaxIPs() {
        return this.maxIPs;
    }

    public String getCsvfile() {
        return this.csvfile;
    }

    public int getBiggest() {
        return this.biggest;
    }

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setTargetDao(TargetDao targetDao) {
		this.targetDao = targetDao;
	}

	public TargetDao getTargetDao() {
		return targetDao;
	}
}
