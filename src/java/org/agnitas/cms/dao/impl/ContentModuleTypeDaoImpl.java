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

import java.sql.*;
import java.util.*;
import org.agnitas.cms.dao.*;
import org.agnitas.cms.webservices.generated.*;
import org.springframework.dao.*;
import org.springframework.jdbc.object.*;
import org.springframework.jdbc.support.incrementer.*;

/**
 * @author Vyacheslav Stepanov
 */
public class ContentModuleTypeDaoImpl extends CmsDaoImpl implements ContentModuleTypeDao {

	public int createContentModuleType(ContentModuleType moduleType) {
		DataFieldMaxValueIncrementer contentModuleIncrement = createIncrement(
				"cm_type_tbl_seq");
		int id = contentModuleIncrement.nextIntValue();
		String sql = "INSERT INTO cm_type_tbl " +
				"(id, company_id, shortname, description, content, read_only, is_public) " +
				"VALUES(?,?, ?, ?, ?, ?, ?)";
		SqlUpdate sqlUpdate = new SqlUpdate(getDataSource(), sql,
				new int[]{Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.VARCHAR,
						Types.LONGVARCHAR, Types.INTEGER, Types.INTEGER});
		sqlUpdate.compile();
		sqlUpdate.update(new Object[]{id, moduleType.getCompanyId(),
				moduleType.getName(),
				moduleType.getDescription(),
				moduleType.getContent(),
				moduleType.isReadOnly() ? 1 : 0,
				moduleType.isIsPublic() ? 1 : 0});
		return id;
	}

	public boolean updateContentModuleType(ContentModuleType moduleType) {
		String sql =
				"UPDATE cm_type_tbl set shortname=?, content=?, " +
						"description=? where id=?";
		SqlUpdate sqlUpdate = new SqlUpdate(getDataSource(), sql,
				new int[]{Types.VARCHAR, Types.LONGVARCHAR, Types.VARCHAR,
						Types.INTEGER});
		sqlUpdate.compile();
		int rowsUpdated = sqlUpdate.update(new Object[]{moduleType.getName(),
				moduleType.getContent(), moduleType.getDescription(),
				moduleType.getId()});
		return rowsUpdated > 0;
	}

	public ContentModuleType getContentModuleType(int id) {
		String sqlStatement =
				"SELECT * FROM cm_type_tbl WHERE id=" + id;
		try {
			ContentModuleType moduleType = (ContentModuleType) createJdbcTemplate()
					.queryForObject(sqlStatement,
							new ContentModuleTypeRowMapper());
			return moduleType;
		}
		catch(IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	public List<ContentModuleType> getContentModuleTypes(int companyId,
														 boolean includePublic) {
		String sqlStatement =
				"SELECT * FROM cm_type_tbl WHERE company_id=" +
						companyId;
		if(includePublic) {
			sqlStatement = sqlStatement + " OR is_public=1";
		}
		return createJdbcTemplate()
				.query(sqlStatement, new ContentModuleTypeRowMapper());
	}

	public void deleteContentModuleType(int id) {
		String sqlStatement =
				"DELETE FROM cm_type_tbl WHERE id=" + id +
						" AND read_only=0";
		createJdbcTemplate().execute(sqlStatement);
	}

}