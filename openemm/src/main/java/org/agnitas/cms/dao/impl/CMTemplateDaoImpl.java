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

import org.agnitas.cms.dao.CMTemplateDao;
import org.agnitas.cms.webservices.generated.CMTemplate;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Vyacheslav Stepanov
 */
public class CMTemplateDaoImpl extends CmsDaoImpl implements CMTemplateDao {

	public CMTemplate createCMTemplate(CMTemplate template) {
		String sql = "INSERT INTO cm_template_tbl " +
				"(id,company_id, shortname, description, content) VALUES(?, ?, ?, ?, ?)";
		SqlUpdate sqlUpdate = new SqlUpdate(getDataSource(), sql,
				new int[]{Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.VARCHAR,
						Types.BLOB});
		sqlUpdate.compile();
		final DataFieldMaxValueIncrementer increment = createIncrement(
				"cm_template_tbl_seq");
		int id = increment.nextIntValue();
		byte[] content = template.getContent();
		if(content.length == 0) {
			content = new byte[]{0};
		}
		final SqlLobValue blobContent = new SqlLobValue(content);
		sqlUpdate.update(new Object[]{
				id,
				template.getCompanyId(),
				template.getName(),
				template.getDescription(),
				blobContent});
		template.setId(id);
		return template;
	}

	public CMTemplate getCMTemplate(int id) {
		String sqlStatement = "SELECT * FROM cm_template_tbl WHERE id=" + id;
		try {
			CMTemplate template = (CMTemplate) createJdbcTemplate()
					.queryForObject(sqlStatement, new CMTemplateRowMapper());
			return template;
		}
		catch(IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	public List<CMTemplate> getCMTemplates(int companyId) {
		return getCMTemplates(companyId, false, null);
	}

	public List<CMTemplate> getCMTemplatesSortByName(int companyId,
													 String sortDirection) {
		return getCMTemplates(companyId, true, sortDirection);
	}

	public void deleteCMTemplate(int id) {
		String sqlStatement = "DELETE FROM cm_template_tbl WHERE id=" + id;
		createJdbcTemplate().execute(sqlStatement);
		sqlStatement =
				"DELETE FROM cm_template_mailing_bind_tbl WHERE cm_template_id=" +
						id;
		createJdbcTemplate().execute(sqlStatement);
	}

	public boolean updateCMTemplate(int id, String name, String description) {
		String sqlStatement = "UPDATE cm_template_tbl" +
				" SET shortname='" + name + "', description='" + description +
				"'" +
				" WHERE id=" + id;
		int rowsUpdated = createJdbcTemplate().update(sqlStatement);
		return rowsUpdated > 0;
	}

	public boolean updateContent(int id, byte[] content) {
		String sql = "UPDATE cm_template_tbl set content=? where id=?";
		SqlUpdate sqlUpdate = new SqlUpdate(getDataSource(), sql,
				new int[]{Types.BLOB, Types.INTEGER});
		sqlUpdate.compile();
		final SqlLobValue blobValue = new SqlLobValue(content);
		int rowsUpdated = sqlUpdate.update(new Object[]{blobValue, id});
		return rowsUpdated > 0;
	}

	public Map<Integer, Integer> getMailingBinding(List<Integer> mailingIds) {
		if(mailingIds.isEmpty()) {
			return null;
		}
		String sql = "SELECT * FROM cm_template_mailing_bind_tbl WHERE " +
				"mailing_id IN (";
		for(Integer mailingId : mailingIds) {
			sql = sql + mailingId + ",";
		}
		// remove last unnecessary "," and add ")" to end
		sql = sql.substring(0, sql.length() - 1) + ")";
		List<Map> queryResult = createJdbcTemplate().queryForList(sql);
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		for(Map row : queryResult) {
			final Object mailingIdObject = row.get("mailing_id");
			final Object cmTemplateIdObject = row.get("cm_template_id");
			if(mailingIdObject instanceof Long && cmTemplateIdObject instanceof Long) {
				result.put(((Long) mailingIdObject).intValue(),
						((Long) cmTemplateIdObject).intValue());
			}
			if(mailingIdObject instanceof BigDecimal &&
					cmTemplateIdObject instanceof BigDecimal) {
				result.put(((BigDecimal) mailingIdObject).intValue(),
						((BigDecimal) cmTemplateIdObject).intValue());
			}
		}
		return result;
	}

	public Map<Integer, Integer> getMailingBinding(int cmTemplateId) {
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		String sqlStatement = "SELECT * FROM cm_template_mailing_bind_tbl " +
				"WHERE cm_template_id=" + cmTemplateId;
		List<Map> queryResult = createJdbcTemplate().queryForList(sqlStatement);
		for(Map row : queryResult) {
			final Object mailingIdObject = row.get("mailing_id");
			final Object cmTemplateIdObject = row.get("cm_template_id");
			if(mailingIdObject instanceof Long && cmTemplateIdObject instanceof Long) {
				result.put(((Long) mailingIdObject).intValue(),
						((Long) cmTemplateIdObject).intValue());
			}
			if(mailingIdObject instanceof BigDecimal &&
					cmTemplateIdObject instanceof BigDecimal) {
				result.put(((BigDecimal) mailingIdObject).intValue(),
						((BigDecimal) cmTemplateIdObject).intValue());
			}
		}
		return result;
	}

	public CMTemplate getCMTemplateForMailing(int mailingId) {
		String sqlStatement = "SELECT * FROM cm_template_tbl template WHERE " +
				"id IN (SELECT cm_template_id FROM cm_template_mailing_bind_tbl " +
				"WHERE mailing_id=" + mailingId +
				" AND cm_template_id=template.id)";
		List<CMTemplate> templates = createJdbcTemplate()
				.query(sqlStatement, new CMTemplateRowMapper());
		if(templates.isEmpty()) {
			return null;
		} else {
			return templates.get(0);
		}
	}

	public String getTextVersion(int adminId) {
		String sql = "SELECT text FROM cm_text_version_tbl WHERE admin_id=" + adminId;
		final JdbcTemplate template = createJdbcTemplate();
		List textList = template.queryForList(sql);
		if(textList.size() > 0) {
            Object textVersion = ((Map) textList.get(0)).get("TEXT");
            return textVersion == null ? "" : (String) textVersion;
		}
		sql = "SELECT text FROM cm_text_version_tbl WHERE admin_id=0";
		textList = template.queryForList(sql);
		if(textList.size() > 0) {
			Object textVersion = ((Map) textList.get(0)).get("TEXT");
            return textVersion == null ? "" : (String) textVersion;
		}
		return "";
	}


	public void saveTextVersion(int adminId, String text) {

		String sql =
				"SELECT COUNT(id) FROM cm_text_version_tbl WHERE admin_id=" + adminId;
		final int rowsCount = createJdbcTemplate().queryForInt(sql);
		final SqlLobValue blobValue = new SqlLobValue(text);
		if(rowsCount == 0) {
			sql = "INSERT INTO cm_text_version_tbl (id,admin_id,text) VALUES(?, ?,?)";
			final SqlUpdate sqlUpdate = new SqlUpdate(getDataSource(), sql,
					new int[]{Types.INTEGER, Types.INTEGER, Types.CLOB});
			sqlUpdate.compile();
			int id = createIncrement("cm_text_version_tbl_seq").nextIntValue();
			sqlUpdate.update(new Object[]{id, adminId, blobValue});
		} else {
			sql = "UPDATE cm_text_version_tbl SET text=? WHERE admin_id=?";
			SqlUpdate sqlUpdate = new SqlUpdate(getDataSource(), sql,
					new int[]{Types.CLOB, Types.INTEGER});
			sqlUpdate.compile();
			sqlUpdate.update(new Object[]{blobValue, adminId});
		}
	}

	public void removeTextVersion(int adminId) {
		createJdbcTemplate()
				.execute("DELETE FROM cm_text_version_tbl WHERE admin_id=" + adminId);
	}

	public void addMailingBindings(final int cmTemplateId,
								   final List<Integer> mailingIds) {
		if(mailingIds.isEmpty()) {
			return;
		}
		String sql = "INSERT INTO cm_template_mailing_bind_tbl " +
				"(id, mailing_id, cm_template_id) VALUES (?, ?, ?)";

		final JdbcTemplate template = createJdbcTemplate();
		final DataFieldMaxValueIncrementer increment = createIncrement(
				"cm_template_mail_bind_tbl_seq");
		template.batchUpdate(sql, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws
					SQLException {
				ps.setInt(1, increment.nextIntValue());
				ps.setInt(2, mailingIds.get(i));
				ps.setInt(3, cmTemplateId);
			}

			public int getBatchSize() {
				return mailingIds.size();
			}
		});

	}

	public void removeMailingBindings(List<Integer> mailingIds) {
		if(mailingIds.isEmpty()) {
			return;
		}
		String sql = "DELETE FROM cm_template_mailing_bind_tbl " +
				"WHERE mailing_id IN (";
		for(Integer mailingId : mailingIds) {
			sql = sql + mailingId + ",";
		}
		// remove last unnecessary "," and add ")" to end
		sql = sql.substring(0, sql.length() - 1) + ")";
		createJdbcTemplate().execute(sql);
	}

	private List<CMTemplate> getCMTemplates(int companyId, boolean sortByName,
											String sortDirection) {
		String sqlStatement =
				"SELECT * FROM cm_template_tbl WHERE company_id=" + companyId;
		if(sortByName) {
			sqlStatement =
					sqlStatement + " ORDER BY shortname " + sortDirection;
		}
		return createJdbcTemplate()
				.query(sqlStatement, new CMTemplateRowMapper());
	}

	public List<Integer> getMailingWithCmsContent(List<Integer> mailingIds,
												  int company_id) {
        Set<Integer> result = new HashSet<Integer>();
        if (!mailingIds.isEmpty()) {
            String mailingIdsSql = "(";
            for (Integer mailingId : mailingIds) {
                mailingIdsSql = mailingIdsSql + mailingId + ",";
            }
            // remove last unnecessary "," and add ")" to end
            mailingIdsSql = mailingIdsSql.substring(0, mailingIdsSql.length() - 1) + ")";

            String sql = "SELECT mailing_id FROM  cm_mailing_bind_tbl " +
                    "INNER JOIN cm_content_module_tbl module ON " +
                    "(module.id = content_module_id) WHERE mailing_id IN " +
                    mailingIdsSql + " AND company_id=" + company_id;

            List<Map> queryResult = createJdbcTemplate().queryForList(sql);
            for (Map row : queryResult) {
                final Object mailingIdObject = row.get("mailing_id");
                if (mailingIdObject instanceof Long) {
                    result.add(((Long) mailingIdObject).intValue());
                }
                if (mailingIdObject instanceof BigDecimal) {
                    result.add(((BigDecimal) mailingIdObject).intValue());
                }
            }

            sql = "SELECT mailing_id FROM cm_template_mailing_bind_tbl " +
                    "INNER JOIN cm_template_tbl template ON " +
                    "(template.id=cm_template_id) WHERE mailing_id IN " +
                    mailingIdsSql + " AND company_id=" + company_id;

            queryResult = createJdbcTemplate().queryForList(sql);
            for (Map row : queryResult) {
                final Object mailingIdObject = row.get("mailing_id");
                if (mailingIdObject instanceof Long) {
                    result.add(((Long) mailingIdObject).intValue());
                }
                if (mailingIdObject instanceof BigDecimal) {
                    result.add(((BigDecimal) mailingIdObject).intValue());
                }
            }
        }

        return new ArrayList(result);
	}
}
