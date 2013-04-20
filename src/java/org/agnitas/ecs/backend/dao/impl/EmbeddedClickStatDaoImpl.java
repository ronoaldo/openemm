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
 * the code written by AGNITAS AG are Copyright (c) 2009 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

package org.agnitas.ecs.backend.dao.impl;

import org.agnitas.ecs.EcsGlobals;
import org.agnitas.ecs.backend.beans.ClickStatColor;
import org.agnitas.ecs.backend.beans.ClickStatInfo;
import org.agnitas.ecs.backend.beans.impl.ClickStatInfoImpl;
import org.agnitas.ecs.backend.dao.EmbeddedClickStatDao;
import org.agnitas.util.AgnUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Collection;

/**
 * Implementation of {@link org.agnitas.ecs.backend.dao.EmbeddedClickStatDao}
 *
 * @author Vyacheslav Stepanov
 */
public class EmbeddedClickStatDaoImpl implements EmbeddedClickStatDao {

	protected DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Collection<ClickStatColor> getClickStatColors(int companyId) {
		if(dataSource == null) {
			return null;
		}
		JdbcTemplate jdbc = new JdbcTemplate(dataSource);
		String sqlStatement = "SELECT * FROM click_stat_colors_tbl WHERE company_id=" + companyId + " ORDER BY range_end";
        return jdbc.query(sqlStatement, new ClickStatColorsRowMapper());
	}

	public ClickStatInfo getClickStatInfo(int companyId, int mailingId, int mode) {
		if(dataSource == null) {
			return null;
		}
		JdbcTemplate jdbc = new JdbcTemplate(dataSource);


        final String rdirUrlTableName = "RDIR_URL_TBL";
        final String rdirUrlTableNameDatabaseSpecific = AgnUtils.isOracleDB() ? rdirUrlTableName : rdirUrlTableName.toLowerCase();
        final String rdirLogTableNameDatabaseSpecific = AgnUtils.isProjectEMM() ? "RDIRLOG_" + companyId + "_TBL" : "rdir_log_tbl";
        String sql = "SELECT urltbl.url_id url_id, " +
				"count(logtbl.customer_id) clicks_gros, " +
				"count(distinct logtbl.customer_id) clicks_net " +
                "FROM   " + rdirUrlTableNameDatabaseSpecific + "  urltbl join " + rdirLogTableNameDatabaseSpecific +
				" logtbl on (logtbl.mailing_id=" + mailingId +
				" and logtbl.company_id=" + companyId +
				" and urltbl.company_id=" + companyId +
				" and logtbl.url_id = urltbl.url_id ) " +
				"GROUP BY urltbl.full_url,urltbl.shortname ,urltbl.url_id " +
				"order by clicks_net desc";
		SqlRowSet rs = jdbc.queryForRowSet(sql);
		ClickStatInfo clickStatInfo = new ClickStatInfoImpl();
		int totalSent = getTotalMailsSent(companyId, mailingId);
		while(rs.next()) {
			int urlId = rs.getInt("url_id");
			int clicks = 0;
			if(mode == EcsGlobals.MODE_GROSS_CLICKS) {
				clicks = rs.getInt("clicks_gros");
			} else if(mode == EcsGlobals.MODE_NET_CLICKS) {
				clicks = rs.getInt("clicks_net");
			}
			double clicksPercent = 0;
			if(totalSent > 0) {
				clicksPercent = ((double) clicks / (double) totalSent) * 100;
			}
			clickStatInfo.addURLInfo(urlId, clicks, clicksPercent);
		}
		return clickStatInfo;
	}

	/**
	 * Method returns number of mails sent for the mailing
	 *
	 * @param companyId id of company
	 * @param mailingId id of mailing
	 * @return mails sent number for the mailing
	 */
	private int getTotalMailsSent(int companyId, int mailingId) {
		String sql = "";
		if(dataSource == null) {
			return 0;
		}
		JdbcTemplate jdbc = new JdbcTemplate(dataSource);
		if ( AgnUtils.isOracleDB() ) {
			// add + " and status_field not in ('A', 'T')" to exclude admin and test recipients
			sql = "SELECT SUM(no_of_mailings) mails FROM mailing_account_tbl WHERE mailing_id="+ mailingId;
		}
		else {
			sql = "select count(distinct mailtrack.customer_id) from mailtrack_tbl mailtrack" +
				" where mailtrack.company_id=" + companyId + " and mailtrack.mailing_id=" + mailingId;
		}
		SqlRowSet rowSet = jdbc.queryForRowSet(sql);
		if(rowSet.next()) {
			return rowSet.getInt(1);
		} else {
			return 0;
		}
	}

}
