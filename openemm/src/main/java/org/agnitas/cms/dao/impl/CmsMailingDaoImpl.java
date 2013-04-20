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

package org.agnitas.cms.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.agnitas.cms.beans.CmsTargetGroup;
import org.agnitas.cms.beans.impl.CmsTargetGroupImpl;
import org.agnitas.cms.dao.CmsMailingDao;
import org.agnitas.cms.utils.dataaccess.CMTemplateManager;
import org.agnitas.util.AgnUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Vyacheslav Stepanov
 */
public class CmsMailingDaoImpl implements CmsMailingDao {

	public static String HTML_COMPONENT_NAME = "agnHtml";
	public static String DEFAULT_MAILING_TEMPLATE = "[agnDYN name=\"" +
			AgnUtils.DEFAULT_MAILING_HTML_DYNNAME + "\"/]";

	public void setMailingHtmlTemplateChanged(int companyId, int mailingId, boolean changed) {
		int chanedInt = changed ? 1 : 0;
		String query = "UPDATE mailing_tbl SET cms_has_classic_content=" + chanedInt + " WHERE mailing_id=" + mailingId +
				" AND company_id=" + companyId;
		createJdbcTemplate().execute(query);
	}

	public List<Integer> getMailingsWithNoClassicTemplate(
			List<Integer> mailingIds, int companyId) {
		JdbcTemplate jdbcTemplate = createJdbcTemplate();
		String mailingIdsSql = "(";
		for(Integer mailingId : mailingIds) {
			mailingIdsSql = mailingIdsSql + mailingId + ",";
		}
		// remove last unnecessary "," and add ")" to end
		mailingIdsSql =
				mailingIdsSql.substring(0, mailingIdsSql.length() - 1) + ")";

//		String sql = "SELECT mailing_id FROM mailing_tbl mail WHERE company_id=" + companyId +
//				" AND cms_has_classic_content=0 " +
//				" AND (SELECT COUNT(*) FROM dyn_content_tbl " +
//				" WHERE dyn_name_id IN " +
//				"(select dyn_name_id FROM dyn_name_tbl " +
//				" WHERE mailing_id=mail.mailing_id AND " +
//				"dyn_name='" + DEFAULT_MAILING_HTML_DYNNAME +
//				"')) IN (0) ";

		String sql = "SELECT mailing_id FROM mailing_tbl mail WHERE company_id=" + companyId +
				" AND cms_has_classic_content=0 " +
				" AND (SELECT COUNT(cont.dyn_content_id) FROM dyn_content_tbl cont, dyn_name_tbl dname " +
		        " WHERE cont.dyn_name_id = dname.dyn_name_id " +
		        " AND dname.mailing_id=mail.mailing_id " +
		        " AND dname.dyn_name='" + AgnUtils.DEFAULT_MAILING_HTML_DYNNAME +
		        " ') IN (0) ";

		if(!mailingIds.isEmpty()) {
			sql = sql + "and mailing_id in " + mailingIdsSql;
		}

		List<Map> queryResult = jdbcTemplate.queryForList(sql);
		List<Integer> result = new ArrayList<Integer>();
		for(Map row : queryResult) {
			Object idObject = row.get("mailing_id");
			if(idObject instanceof Long) {
				result.add(((Long) idObject).intValue());
			}
			if(idObject instanceof BigDecimal) {
				result.add(((BigDecimal) idObject).intValue());
			}
		}

		final CMTemplateManager manager = (CMTemplateManager) applicationContext.
				getBean("CMTemplateManager");
		final List<Integer> cmAssignResult = manager.getMailingWithCmsContent(
				mailingIds, companyId);

		if(cmAssignResult != null) {
		for(Integer mailingId : cmAssignResult) {
			if(!result.contains(mailingId)) {
				result.add(mailingId);
			}
		}
		}
		return result;
	}

	public Map<Integer, CmsTargetGroup> getTargetGroups(int companyId) {
		String sql = "SELECT target_id, target_shortname, deleted FROM dyn_target_tbl " +
				"WHERE company_id=" + companyId + " ORDER BY target_shortname";
		List<Map> queryResult = createJdbcTemplate().queryForList(sql);
		Map<Integer, CmsTargetGroup> result = new HashMap<Integer, CmsTargetGroup>();
		for(Map row : queryResult) {
			CmsTargetGroup targetGroup = new CmsTargetGroupImpl();
			
			Object idObject = row.get("target_id");
			if(idObject instanceof Long) {
				targetGroup.setTargetGroupID(((Long) idObject).intValue());
			} else 	if(idObject instanceof BigDecimal) {
				targetGroup.setTargetGroupID(((BigDecimal) idObject).intValue());
			}
			targetGroup.setShortname(String.valueOf(row.get("target_shortname")));
			targetGroup.setDeleted( ((Number)row.get("deleted")).intValue() != 0);
			
			result.put(targetGroup.getTargetGroupID(), targetGroup);
		}
		return result;
	}

	protected DataSource getDataSource() {
		return (DataSource) applicationContext.getBean("dataSource");
	}

	/**
	 * Holds value of property applicationContext.
	 */
	protected ApplicationContext applicationContext;

	/**
	 * Setter for property applicationContext.
	 *
	 * @param applicationContext New value of property applicationContext.
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	protected JdbcTemplate createJdbcTemplate() {
		return new JdbcTemplate(getDataSource());
	}

}
