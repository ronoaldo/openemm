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

import org.agnitas.cms.dao.ContentModuleDao;
import org.agnitas.cms.utils.TagUtils;
import org.agnitas.cms.webservices.generated.CmsTag;
import org.agnitas.cms.webservices.generated.ContentModule;
import org.agnitas.cms.webservices.generated.ContentModuleLocation;
import org.agnitas.cms.webservices.generated.ContentModuleCategory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Vyacheslav Stepanov
 */
public class ContentModuleDaoImpl extends CmsDaoImpl
		implements ContentModuleDao {

	public int createContentModule(ContentModule contentModule) {
		DataFieldMaxValueIncrementer contentModuleIncrement = createIncrement(
				"cm_tbl_seq");
		int id = contentModuleIncrement.nextIntValue();
		String sql = "INSERT INTO cm_content_module_tbl (id,company_id, shortname, " +
				"description, content, category_id) VALUES(?, ?, ?, ?, ?, ?)";
		SqlUpdate sqlUpdate = new SqlUpdate(getDataSource(), sql, new int[]{
				Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.VARCHAR,
				Types.LONGVARCHAR, Types.INTEGER});
		sqlUpdate.compile();
		sqlUpdate.update(new Object[]{id, contentModule.getCompanyId(),
				contentModule.getName(), contentModule.getDescription(),
				contentModule.getContent(), contentModule.getCategoryId()});
		return id;
	}

	public boolean updateContentModule(int id, String newName,
									   String newDescription, int categoryId) {
		String sql = "UPDATE cm_content_module_tbl set shortname=?, description=?, category_id=? where id=?";
		SqlUpdate sqlUpdate = new SqlUpdate(getDataSource(), sql,
				new int[]{Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.INTEGER});
		sqlUpdate.compile();
		int rowsUpdated = sqlUpdate.update(new Object[]{newName,
				newDescription, categoryId, id});
		return rowsUpdated > 0;
	}

	public ContentModule getContentModule(int id) {
		String sqlStatement = "SELECT * FROM cm_content_module_tbl WHERE id=" + id;
		try {
			ContentModule contentModule = (ContentModule) createJdbcTemplate().
					queryForObject(sqlStatement, new ContentModuleRowMapper());
			return contentModule;
		}
		catch(IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	public List<ContentModule> getContentModules(int companyId) {
		String sqlStatement =
				"SELECT * FROM cm_content_module_tbl WHERE company_id=" + companyId;
		return createJdbcTemplate()
				.query(sqlStatement, new ContentModuleRowMapper());
	}

	public void deleteContentModule(int id) {
		String sqlStatement = "DELETE FROM cm_content_module_tbl WHERE id=" + id;
		createJdbcTemplate().execute(sqlStatement);
	}

	public List<CmsTag> getContentModuleContents(int contentModuleId) {
		String sql = "SELECT * FROM cm_content_tbl " +
				"WHERE content_module_id=" + contentModuleId;
		List<Map> queryResult = createJdbcTemplate().queryForList(sql);
		List<CmsTag> result = new ArrayList<CmsTag>();
		for(Map row : queryResult) {

			final Object tagTypeObject = row.get("tag_type");
			int type = 0;
			if(tagTypeObject instanceof Long) {
				type = ((Long) tagTypeObject).intValue();
			}
			if(tagTypeObject instanceof BigDecimal) {
				type = ((BigDecimal) tagTypeObject).intValue();
			}

            Object content = row.get("content");
            String value = (content == null) ? "" : String.valueOf(content);
            CmsTag tag = TagUtils.createTag(String.valueOf(row.get("tag_name")), type,
					value);
			result.add(tag);
		}
		return result;
	}

	public void removeContentsForContentModule(int contentModuleId) {
		String sqlStatement = "DELETE FROM cm_content_tbl WHERE " +
				"content_module_id=" + contentModuleId;
		createJdbcTemplate().execute(sqlStatement);
	}

	public List<Integer> getAssignedCMsForMailing(int mailingId) {
		String sql =
				"SELECT content_module_id FROM cm_mailing_bind_tbl " +
						"WHERE mailing_id =" + mailingId + " ORDER BY id";
		List<Map> queryResult = createJdbcTemplate().queryForList(sql);
		List<Integer> result = new ArrayList<Integer>();
		for(Map row : queryResult) {
			final Object contentModuleIdObject = row.get("content_module_id");
			if(contentModuleIdObject instanceof Long) {
				result.add(((Long) contentModuleIdObject).intValue());
			}
			if(contentModuleIdObject instanceof BigDecimal) {
				result.add(((BigDecimal) contentModuleIdObject).intValue());
			}
		}
		return result;
	}

	public List<Integer> getMailingBinding(List<Integer> mailingIds,
										   int contentModuleId) {
		if(mailingIds.isEmpty()) {
			return null;
		}
		String sql = "SELECT mailing_id FROM cm_mailing_bind_tbl " +
				"WHERE mailing_id IN (";
		for(Integer mailingId : mailingIds) {
			sql = sql + mailingId + ",";
		}
		// remove last unnecessary "," and add ")" to end
		sql = sql.substring(0, sql.length() - 1) + ") AND content_module_id=" +
				contentModuleId;
		List<Map> queryResult = createJdbcTemplate().queryForList(sql);
		List<Integer> result = new ArrayList<Integer>();
		for(Map row : queryResult) {
			final Object mailingIdObject = row.get("mailing_id");
			if(mailingIdObject instanceof Long) {
				result.add(((Long) mailingIdObject).intValue());
			}

			if(mailingIdObject instanceof BigDecimal) {
				result.add(((BigDecimal) mailingIdObject).intValue());
			}
		}
		return result;
	}

	public List<Integer> getMailingsByContentModule(int contentModuleId) {

		String sql =
				"SELECT mailing_id FROM cm_mailing_bind_tbl " +
						"WHERE  content_module_id=" + contentModuleId;
		List<Map> queryResult = createJdbcTemplate().queryForList(sql);
		List<Integer> result = new ArrayList<Integer>();
		for(Map row : queryResult) {
			final Object idObject = row.get("mailing_id");
			if(idObject instanceof Long) {
				result.add(((Long) idObject).intValue());
			}
			if(idObject instanceof BigDecimal) {
				result.add(((BigDecimal) idObject).intValue());
			}

		}
		return result;
	}

	public void addMailingBindings(final int contentModuleId,
								   final List<Integer> mailingIds) {
		if(mailingIds.isEmpty()) {
			return;
		}
        // Mailing IDs start from 1. Mailing ID = 0 is invalid situation.
        mailingIds.remove(new Integer(0));

		final JdbcTemplate template = createJdbcTemplate();

		String sql = "INSERT INTO cm_mailing_bind_tbl " +
				"(id ,mailing_id, content_module_id) VALUES (?,?,?)";

		final DataFieldMaxValueIncrementer cmMailingIncrement =
				createIncrement("cm_mailing_bind_tbl_seq");
		final BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setInt(1, cmMailingIncrement.nextIntValue());
				ps.setInt(2, mailingIds.get(i));
				ps.setInt(3, contentModuleId);
			}

			public int getBatchSize() {
				return mailingIds.size();
			}
		};

		template.batchUpdate(sql, setter);
	}

	public void addMailingBindings(final List<Integer> contentModuleIds,
								   final int mailingId) {
        // Mailing IDs start from 1. Mailing ID = 0 is invalid situation.
		if(contentModuleIds.isEmpty() || mailingId == 0) {
			return;
		}

		final JdbcTemplate template = createJdbcTemplate();

		String sql = "INSERT INTO cm_mailing_bind_tbl " +
				"(id,mailing_id, content_module_id) VALUES (?, ?, ?)";

		final DataFieldMaxValueIncrementer cmMailingIncrement =
				createIncrement("cm_mailing_bind_tbl_seq");
		final BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setInt(1, cmMailingIncrement.nextIntValue());
				ps.setInt(2, mailingId);
				ps.setInt(3, contentModuleIds.get(i));
			}

			public int getBatchSize() {
				return contentModuleIds.size();
			}
		};

		template.batchUpdate(sql, setter);
	}

	public void removeMailingBindings(int contentModuleId,
									  List<Integer> mailingIds) {
		if(mailingIds.isEmpty()) {
			return;
		}
		String sql = "DELETE FROM cm_mailing_bind_tbl " +
				"WHERE mailing_id IN (";
		for(Integer mailingId : mailingIds) {
			sql = sql + mailingId + ",";
		}
		// remove last unnecessary "," and add ")" to end
		sql = sql.substring(0, sql.length() - 1) + ") AND content_module_id=" +
				contentModuleId;
		createJdbcTemplate().execute(sql);
	}

	public void removeMailingBindings(List<Integer> contentModuleIds,
									  int mailingId) {
		if(contentModuleIds.isEmpty()) {
			return;
		}
		String sql = "DELETE FROM cm_mailing_bind_tbl " +
				"WHERE content_module_id IN (";
		for(Integer cmId : contentModuleIds) {
			sql = sql + cmId + ",";
		}
		// remove last unnecessary "," and add ")" to end
		sql = sql.substring(0, sql.length() - 1) + ") AND mailing_id=" +
				mailingId;
		createJdbcTemplate().execute(sql);
	}

	public List<ContentModuleLocation> getCMLocationsForMailingId(
			int mailingId) {
		String sqlStatement =
				"SELECT * FROM cm_location_tbl WHERE mailing_id=" +
						mailingId;
		return createJdbcTemplate()
				.query(sqlStatement, new ContentModuleLocationRowMapper());
	}

	public List<ContentModule> getContentModulesForMailing(int mailingId) {
		String sqlStatement = "SELECT * FROM cm_content_module_tbl cm WHERE " +
				"id IN (SELECT content_module_id FROM cm_mailing_bind_tbl " +
				"WHERE mailing_id=" + mailingId +
				" AND content_module_id=cm.id)";
		return createJdbcTemplate()
				.query(sqlStatement, new ContentModuleRowMapper());
	}

	public void removeCMLocationsForMailing(int mailingId) {
		String sql =
				"DELETE from cm_location_tbl WHERE mailing_id=" +
						mailingId;
		createJdbcTemplate().execute(sql);
	}

	public void removeCMLocationsForMailingsByContentModule(int contentModuleId,
															List<Integer> mailingIds) {
		if(mailingIds.size() > 0) {
			String sql = "DELETE from cm_location_tbl WHERE content_module_id= " +
					contentModuleId;
			StringBuffer mailingSql = new StringBuffer(" AND mailing_id IN (");
			for(Integer mailingId : mailingIds) {
				mailingSql.append(mailingId).append(",");
			}
			mailingSql.deleteCharAt(mailingSql.length() - 1);
			mailingSql.append(")");
			createJdbcTemplate().execute(sql + mailingSql.toString());
		}
	}

	public void addCMLocations(final List<ContentModuleLocation> locations) {
		if(locations.isEmpty()) {
			return;
		}
		String sql = "INSERT INTO cm_location_tbl " +
				"(id,cm_template_id, content_module_id, mailing_id, " +
				"dyn_name, dyn_order, target_group_id) " +
				"VALUES (?,?,?,?,?,?,?)";

		final JdbcTemplate template = createJdbcTemplate();
		final DataFieldMaxValueIncrementer cmLocationIncrement = createIncrement(
				"cm_location_tbl_seq");
		final BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setInt(1, cmLocationIncrement.nextIntValue());
				ps.setInt(2, locations.get(i).getCmTemplateId());
				ps.setInt(3, locations.get(i).getContentModuleId());
				ps.setInt(4, locations.get(i).getMailingId());
				ps.setString(5, locations.get(i).getDynName());
				ps.setInt(6, locations.get(i).getOrder());
				ps.setInt(7, locations.get(i).getTargetGroupId());
			}

			public int getBatchSize() {
				return locations.size();
			}
		};

		template.batchUpdate(sql, setter);
	}

	public int createContentModuleCategory(ContentModuleCategory category) {
		DataFieldMaxValueIncrementer contentModuleIncrement = createIncrement(
				"cm_category_tbl_seq");
		int id = contentModuleIncrement.nextIntValue();
		String sql = "INSERT INTO cm_category_tbl (id, company_id, shortname, description) VALUES(?, ?, ?, ?)";
		SqlUpdate sqlUpdate = new SqlUpdate(getDataSource(), sql, new int[]{
				Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.VARCHAR});
		sqlUpdate.compile();
		sqlUpdate.update(new Object[]{id, category.getCompanyId(), category.getName(), category.getDescription() == null || category.getDescription().equals("") ? " " : category.getDescription()});
		return id;
	}

	public void deleteContentModuleCategory(int categoryId) {
		String sqlStatement = "DELETE FROM cm_category_tbl WHERE id=" + categoryId;
		createJdbcTemplate().execute(sqlStatement);
	}

	public ContentModuleCategory getContentModuleCategory(int id) {
		String sqlStatement = "SELECT * FROM cm_category_tbl WHERE id=" + id;
		try {
			ContentModuleCategory cmCategory = (ContentModuleCategory) createJdbcTemplate().
					queryForObject(sqlStatement, new ContentModuleCategoryRowMapper());
			return cmCategory;
		}
		catch(IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	public boolean updateContentModuleCategory(int id, String newName, String newDescription) {
		String sql = "UPDATE cm_category_tbl set shortname=?, description=? where id=?";
		SqlUpdate sqlUpdate = new SqlUpdate(getDataSource(), sql,
				new int[]{Types.VARCHAR, Types.VARCHAR, Types.INTEGER});
		sqlUpdate.compile();
		int rowsUpdated = sqlUpdate.update(new Object[]{newName,
				newDescription == null || newDescription.equals("") ? " " : newDescription, id});
		return rowsUpdated > 0;
	}

	public List<ContentModuleCategory> getAllCMCategories(int companyId) {
		String sqlStatement = "SELECT * FROM cm_category_tbl WHERE company_id=" + companyId;
		return createJdbcTemplate().query(sqlStatement, new ContentModuleCategoryRowMapper());
	}

	public List<ContentModule> getContentModulesForCategory(int companyId, int categoryId) {
		String sqlStatement = "SELECT * FROM cm_content_module_tbl WHERE company_id=" + companyId + " AND category_id=" + categoryId;
		return createJdbcTemplate().query(sqlStatement, new ContentModuleRowMapper());
	}

	public void saveContentModuleContent(int contentModuleId, CmsTag tag) {

		String sql = "UPDATE cm_content_tbl SET content=?" +
				" WHERE content_module_id=? AND tag_type= ? AND tag_name=?";
		final SqlUpdate sqlUpdate = new SqlUpdate(getDataSource(), sql,
				new int[]{Types.VARCHAR, Types.INTEGER, Types.INTEGER,
						Types.VARCHAR});
		sqlUpdate.compile();
		int rowsUpdated = sqlUpdate
				.update(new Object[]{tag.getValue(), contentModuleId,
						tag.getType(), tag.getName()});
		// if record doesn't exist - create a new one
		if(rowsUpdated == 0) {

			sql = "INSERT INTO cm_content_tbl " +
					"(id,content_module_id, tag_name, tag_type, content) VALUES (?,?,?,?,?)";
			final SqlUpdate sqlInsert = new SqlUpdate(getDataSource(), sql,
					new int[]{Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.INTEGER,
							Types.VARCHAR});
			sqlInsert.compile();
			final DataFieldMaxValueIncrementer cmContentIncrement = createIncrement(
					"cm_content_tbl_seq");
			final int id = cmContentIncrement.nextIntValue();
			sqlInsert.update(new Object[]{id, contentModuleId, tag.getName(),
					tag.getType(), tag.getValue()});
		}
	}

	public void saveContentModuleContentList(final int contentModuleId,
											 final List<CmsTag> tagList) {
		removeContentsForContentModule(contentModuleId);
		String sql = "INSERT INTO cm_content_tbl " +
				"(id,content_module_id, tag_name, tag_type, content) VALUES (?,?,?,?,?)";

		final JdbcTemplate template = createJdbcTemplate();
		final DataFieldMaxValueIncrementer cmContentIncrement = createIncrement(
				"cm_content_tbl_seq");
		final BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setInt(1, cmContentIncrement.nextIntValue());
				ps.setInt(2, contentModuleId);
				ps.setString(3, tagList.get(i).getName());
				ps.setInt(4, tagList.get(i).getType());
				ps.setString(5, tagList.get(i).getValue());
			}

			public int getBatchSize() {
				return tagList.size();
			}
		};

		template.batchUpdate(sql, setter);
	}
}