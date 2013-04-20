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

package org.agnitas.dao.impl;

import org.agnitas.dao.ImportLoggerDao;
import org.agnitas.util.AgnUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Vyacheslav Stepanov
 */
public class ImportLoggerDaoImpl extends AbstractImportDao implements ImportLoggerDao {
    public void log(final int companyId, final int adminId, final int datasource_id, final int importedLines, final String statistics, final String profile) {
        String sql = null;
        if (AgnUtils.isOracleDB()) {
            sql = "INSERT INTO import_log_tbl " +
                    "(log_id, company_id, admin_id, datasource_id, imported_lines, statistics, profile) " +
                    "VALUES(import_log_tbl_seq.nextval,?, ?, ?, ?, ?, ?)";
        }

        if (AgnUtils.isMySQLDB()) {
            sql = "INSERT INTO import_log_tbl " +
                    "(company_id, admin_id, datasource_id, imported_lines, statistics, profile) " +
                    "VALUES(?, ?, ?, ?, ?, ?)";
        }
        final JdbcTemplate template = createJdbcTemplate();
        final String updateSql = sql;
        template.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                final PreparedStatement statement = connection.prepareStatement(updateSql);
                statement.setInt(1, companyId);
                statement.setInt(2, adminId);
                statement.setInt(3, datasource_id);
                statement.setInt(4, importedLines);
                statement.setString(5, statistics);
                statement.setString(6, profile);
                return statement;
            }
        });
    }
}

