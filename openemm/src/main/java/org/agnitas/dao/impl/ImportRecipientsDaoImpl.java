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

import org.agnitas.beans.BindingEntry;
import org.agnitas.beans.ImportProfile;
import org.agnitas.beans.ProfileRecipientFields;
import org.agnitas.beans.impl.ImportKeyColumnsKey;
import org.agnitas.beans.impl.PaginatedListImpl;
import org.agnitas.dao.ImportRecipientsDao;
import org.agnitas.service.NewImportWizardService;
import org.agnitas.service.csv.Toolkit;
import org.agnitas.service.impl.CSVColumnState;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.ImportUtils;
import org.agnitas.util.ImportRecipientsToolongValueException;
import org.agnitas.util.importvalues.ImportMode;
import org.agnitas.util.importvalues.NullValuesAction;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.ValidatorResults;
import org.apache.log4j.Logger;
import org.displaytag.pagination.PaginatedList;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.sql.*;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Viktor Gema
 */
public class ImportRecipientsDaoImpl extends AbstractImportDao implements ImportRecipientsDao {

	private static final transient Logger logger = Logger.getLogger( ImportRecipientsDaoImpl.class);

    private SingleConnectionDataSource temporaryConnection;
    private static final SimpleDateFormat DB_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    public static final int MAX_WRITE_PROGRESS = 90;
    public static final int MAX_WRITE_PROGRESS_HALF = MAX_WRITE_PROGRESS / 2;

    @Override
    public LinkedHashMap<String, Map<String, Object>> getColumnInfoByColumnName(int companyId, String column) {
        DataSource ds = (DataSource) applicationContext.getBean("dataSource");
        LinkedHashMap<String, Map<String, Object>> list = new LinkedHashMap<String, Map<String, Object>>();
        ResultSet resultSet = null;

        Connection con = DataSourceUtils.getConnection(ds);
        try {
            if (AgnUtils.isOracleDB()) {
                resultSet = con.getMetaData().getColumns(null, AgnUtils.getDefaultValue("jdbc.username").toUpperCase(), "CUSTOMER_" + companyId + "_TBL", column.toUpperCase());
            } else {
                resultSet = con.getMetaData().getColumns(null, null, "customer_" + companyId + "_tbl", column);
            }
            if (resultSet != null) {
                while (resultSet.next()) {
                    String type;
                    String col = resultSet.getString(4).toLowerCase();
                    Map<String, Object> mapping = new HashMap<String, Object>();

                    mapping.put("column", col);
                    mapping.put("shortname", col);
                    type = ImportUtils.dbtype2string(resultSet.getInt(5));
                    mapping.put("type", type);
                    mapping.put("length", resultSet.getInt(7));
                    if (resultSet.getInt(11) == DatabaseMetaData.columnNullable) {
                        mapping.put("nullable", 1);
                    } else {
                        mapping.put("nullable", 0);
                    }

                    list.put((String) mapping.get("shortname"), mapping);
                }
            }
            resultSet.close();
        } catch (Exception e) {
        	logger.error( MessageFormat.format( "Failed to get column ({0}) info for admin ({1})", column, companyId), e);  // TODO: Check this: is "admin" in combination with companyId correct here???
        } finally {
            DataSourceUtils.releaseConnection(con, ds);
        }
        return list;

    }

    @Override
    public void createRecipients(final Map<ProfileRecipientFields, ValidatorResults> recipientBeansMap, final Integer adminID, final ImportProfile profile, final Integer type, int datasource_id, CSVColumnState[] columns) {
        if (recipientBeansMap.isEmpty()) {
            return;
        }
        final JdbcTemplate template = getJdbcTemplateForTemporaryTable();
        final String prefix = "cust_" + adminID + "_tmp_";
        final String tableName = prefix + datasource_id + "_tbl";
        final ProfileRecipientFields[] recipients = recipientBeansMap.keySet().toArray(new ProfileRecipientFields[recipientBeansMap.keySet().size()]);
        String keyColumn = profile.getKeyColumn();
        List<String> keyColumns = profile.getKeyColumns();
        String duplicateSql = "";
        String duplicateSqlParams = "";
        if (keyColumns.isEmpty()) {
            duplicateSql += " column_duplicate_check_0 ";
            duplicateSqlParams = "?";
        } else {
            for (int i = 0; i < keyColumns.size(); i++) {
                duplicateSql += "column_duplicate_check_" + i;
                duplicateSqlParams += "?";
                if (i != keyColumns.size() - 1) {
                    duplicateSql += ",";
                    duplicateSqlParams += ",";
                }
            }
        }
        final List<CSVColumnState> temporaryKeyColumns = new ArrayList<CSVColumnState>();
        for (CSVColumnState column : columns) {
            if (keyColumns.isEmpty()) {
                if (column.getColName().equals(keyColumn) && column.getImportedColumn()) {
                    temporaryKeyColumns.add(column);
                }
            } else {
                for (String columnName : keyColumns) {
                    if (column.getColName().equals(columnName) && column.getImportedColumn()) {
                        temporaryKeyColumns.add(column);
                        break;
                    }
                }
            }
        }
        final String query = "INSERT INTO " + tableName + " (recipient, validator_result, temporary_id, status_type, " + duplicateSql + ") VALUES (?,?,?,?," + duplicateSqlParams + ")";
        final BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {

            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setBytes(1, ImportUtils.getObjectAsBytes(recipients[i]));
                ps.setBytes(2, ImportUtils.getObjectAsBytes(recipientBeansMap.get(recipients[i])));
                ps.setString(3, recipients[i].getTemporaryId());
                ps.setInt(4, type);
                for (int j = 0; j < temporaryKeyColumns.size(); j++) {
                    setPreparedStatmentForCurrentColumn(ps, 5 + j, temporaryKeyColumns.get(j), recipients[i], profile, recipientBeansMap.get(recipients[i]));
                }

                if( logger.isInfoEnabled()) {
                	logger.info("Import ID: " + profile.getImportId() + " Adding recipient to temp-table: " + Toolkit.getValueFromBean(recipients[i], profile.getKeyColumn()));
                }
            }

            public int getBatchSize() {
                return recipientBeansMap.size();
            }
        };
        template.batchUpdate(query, setter);
    }

    @Override
    public HashMap<ProfileRecipientFields, ValidatorResults> getRecipientsByType(int adminID, Integer[] types, int datasource_id) {
        final JdbcTemplate aTemplate = getJdbcTemplateForTemporaryTable();
        final String prefix = "cust_" + adminID + "_tmp_";
        final String tableName = prefix + datasource_id + "_tbl";
        final String typesAsString = StringUtils.join(types, ",");
        String sqlStatement = "SELECT recipient, validator_result FROM " + tableName + " " +
                "WHERE status_type IN (" +
                typesAsString
                + ")";
        List<Map> resultList = aTemplate.queryForList(sqlStatement);
        HashMap<ProfileRecipientFields, ValidatorResults> recipients = new HashMap<ProfileRecipientFields, ValidatorResults>();
        for (Map row : resultList) {
            Object recipientBean = ImportUtils.deserialiseBean((byte[]) row.get("recipient"));
            final ProfileRecipientFields recipient = (ProfileRecipientFields) recipientBean;
            ValidatorResults validatorResults = null;
            if (row.get("validator_result") != null) {
                Object validatorResultsBean = ImportUtils.deserialiseBean((byte[]) row.get("validator_result"));
                validatorResults = (ValidatorResults) validatorResultsBean;
            }
            recipients.put(recipient, validatorResults);
        }

        return recipients;
    }

    @Override
    public Map<Integer, Integer> assiggnToMailingLists(List<Integer> mailingLists, int companyID, int datasourceId, int mode, int adminId, NewImportWizardService importWizardHelper) {
        Map<Integer, Integer> mailinglistStat = new HashMap<Integer, Integer>();
        if (mailingLists == null || mailingLists.isEmpty() || mode == ImportMode.TO_BLACKLIST.getIntValue()) {
            return mailinglistStat;
        }

        JdbcTemplate jdbc = createJdbcTemplate();
        String currentTimestamp = AgnUtils.getHibernateDialect().getCurrentTimestampSQLFunctionName();
        String sql = null;
        importWizardHelper.setCompletedPercent(0);
        double count = 0;
        double diffComplete = 0;
        int newRecipientsCount = jdbc.queryForInt("SELECT COUNT(*) FROM customer_" + companyID + "_tbl WHERE datasource_id = " + datasourceId);
        if (mode != ImportMode.ADD.getIntValue()) {
            count = newRecipientsCount != 0 ? (newRecipientsCount / NewImportWizardService.BLOCK_SIZE) * mailingLists.size() : 1;
            Integer[] types = {NewImportWizardService.RECIPIENT_TYPE_DUPLICATE_RECIPIENT};
            final int updatedRecipients = getRecipientsCountByType(types, adminId, datasourceId);
            if (updatedRecipients > 0) {
                diffComplete = MAX_WRITE_PROGRESS_HALF / (count != 0 ? count : 1);
            } else {
                diffComplete = MAX_WRITE_PROGRESS / (count != 0 ? count : 1);
            }
        } else {
            count = newRecipientsCount != 0 ? (newRecipientsCount / NewImportWizardService.BLOCK_SIZE) * mailingLists.size() : 1;
            diffComplete = MAX_WRITE_PROGRESS / (count != 0 ? count : 1);
        }
        double intNumber = 0;
        // assign new recipients to mailing lists
        for (Integer mailingList : mailingLists) {
            mailinglistStat.put(mailingList, 0);
            if (mode == ImportMode.ADD.getIntValue() || mode == ImportMode.ADD_AND_UPDATE.getIntValue()) {
                int position = 1;
                int recipientIterator = newRecipientsCount;
                int added = 0;
                while (recipientIterator > 0 || (position == 1 && newRecipientsCount > 0)) {
                    final ImportProfile importProfile = importWizardHelper.getImportProfile();
                    if (importProfile != null) {
                    	if( logger.isInfoEnabled()) {
                    		logger.info("Import ID: " + importProfile.getImportId() + " Assigning new recipients to mailinglist with ID " + mailingList + ", datasourceID: " + datasourceId);
                    	}
                    }else{
                    	if( logger.isInfoEnabled()) {
                    		logger.info("Import ID is undefined");
                    	}
                    }
                    if (AgnUtils.isMySQLDB()) {
                        sql = "INSERT INTO customer_" + companyID + "_binding_tbl (customer_id, user_type, user_status, user_remark, creation_date, exit_mailing_id, mailinglist_id) " +
                                "(SELECT customer_id, 'W', 1, 'CSV File Upload', " + currentTimestamp + ", 0," + mailingList + " FROM customer_" + companyID + "_tbl " +
                                "WHERE datasource_id = " + datasourceId + " LIMIT " + (position - 1) * NewImportWizardService.BLOCK_SIZE + "," + NewImportWizardService.BLOCK_SIZE + " )";
                    }
                    if (AgnUtils.isOracleDB()) {
                        sql = "INSERT INTO customer_" + companyID + "_binding_tbl (customer_id, user_type, user_status, user_remark, creation_date, exit_mailing_id, mailinglist_id) " +
                                "(SELECT customer_id, 'W', 1, 'CSV File Upload', " + currentTimestamp + ", 0," + mailingList + " FROM (SELECT customer_id, datasource_id, rownum r FROM customer_" + companyID + "_tbl WHERE datasource_id = " + datasourceId + " AND 1=1) " +
                                " WHERE r BETWEEN " + (((position - 1) * NewImportWizardService.BLOCK_SIZE) + 1) + " AND " + (((position - 1) * NewImportWizardService.BLOCK_SIZE) + NewImportWizardService.BLOCK_SIZE) + " )";
                    }
                    added = added + jdbc.update(sql);
                    mailinglistStat.put(mailingList, added);
                    intNumber = intNumber + diffComplete;
                    if (intNumber >= 1) {
                        importWizardHelper.setCompletedPercent((int) (importWizardHelper.getCompletedPercent() + Math.floor(intNumber)));
                        intNumber = intNumber - Math.floor(intNumber);
                    }
                    position++;
                    recipientIterator = recipientIterator - NewImportWizardService.BLOCK_SIZE;
                }
            }
        }

        if (mode != ImportMode.ADD.getIntValue()) {
            Integer[] types = {NewImportWizardService.RECIPIENT_TYPE_DUPLICATE_RECIPIENT};
            final int updatedRecipients = getRecipientsCountByType(types, adminId, datasourceId);

            count = count + updatedRecipients != 0 ? updatedRecipients / NewImportWizardService.BLOCK_SIZE : 1;
            if (newRecipientsCount > 0) {
                diffComplete = MAX_WRITE_PROGRESS_HALF / (count != 0 ? count : 1);
            } else {
                diffComplete = MAX_WRITE_PROGRESS / (count != 0 ? count : 1);
            }

        }
        // assign updated recipients to mailing lists
        if (mode != ImportMode.ADD.getIntValue()) {
            Integer[] types = {NewImportWizardService.RECIPIENT_TYPE_DUPLICATE_RECIPIENT};
            int page = 0;
            int rowNum = NewImportWizardService.BLOCK_SIZE;
            HashMap<ProfileRecipientFields, ValidatorResults> recipients = null;
            while (recipients == null || recipients.size() >= rowNum) {
                recipients = getRecipientsByTypePaginated(types, page, rowNum, adminId, datasourceId);
                List<Integer> updatedRecipients = new ArrayList<Integer>();
                for (ProfileRecipientFields recipient : recipients.keySet()) {
                    if (recipient.getUpdatedIds() != null && !recipient.getUpdatedIds().isEmpty()) {
                        updatedRecipients.addAll(recipient.getUpdatedIds());
                    }
                }
                updateMailinglists(mailingLists, companyID, datasourceId, mode, mailinglistStat, jdbc, currentTimestamp, updatedRecipients);
                page++;
                importWizardHelper.setCompletedPercent((int) (importWizardHelper.getCompletedPercent() + diffComplete));
            }
        }
        importWizardHelper.setCompletedPercent(MAX_WRITE_PROGRESS);
        return mailinglistStat;
    }

    @Override
    public void removeTemporaryTable(String tableName, String sessionId) {
        if (AgnUtils.isOracleDB()) {
            final JdbcTemplate template = createJdbcTemplate();
            try {
                String query = "select count(*) from user_tables where table_name = '" + tableName.toUpperCase() + "'";
                int totalRows = template.queryForInt(query);
                if (totalRows != 0) {
                    template.execute("DROP TABLE " + tableName);
                    template.execute("DELETE FROM IMPORT_TEMPORARY_TABLES WHERE SESSION_ID='" + sessionId + "'");
                }
            } catch (Exception e) {
            	logger.error( "deleteTemporaryTables: " +  e.getMessage() + " (table: " + tableName + ")", e);
            }
        }
    }

    @Override
    public List<String> getTemporaryTableNamesBySessionId(String sessionId) {
        List<String> result = new ArrayList<String>();
        final JdbcTemplate template = createJdbcTemplate();
        String query = "SELECT TEMPORARY_TABLE_NAME FROM IMPORT_TEMPORARY_TABLES WHERE SESSION_ID='" + sessionId + "'";
        List<Map> resultList = template.queryForList(query);
        for (Map row : resultList) {
            final String temporaryTableName = (String) row.get("TEMPORARY_TABLE_NAME");
            result.add(temporaryTableName);
        }
        return result;
    }

    private void updateMailinglists(List<Integer> mailingLists, int companyID, int datasourceId, int mode, Map<Integer, Integer> mailinglistStat, JdbcTemplate jdbc, String currentTimestamp, List<Integer> updatedRecipients) {
        String sql;
        for (Integer mailinglistId : mailingLists) {
            try {
                if (mode == ImportMode.ADD.getIntValue() || mode == ImportMode.ADD_AND_UPDATE.getIntValue() || mode == ImportMode.UPDATE.getIntValue()) {
                    int added = 0;
                    createRecipientBindTemporaryTable(companyID, datasourceId, updatedRecipients, jdbc);
                    sql = "DELETE FROM cust_" + companyID + "_exist1_tmp" + datasourceId + "_tbl WHERE customer_id IN (SELECT customer_id FROM customer_" + companyID + "_binding_tbl WHERE mailinglist_id=" + mailinglistId + ")";
                    jdbc.execute(sql);
                    sql = "INSERT INTO customer_" + companyID + "_binding_tbl (customer_id, user_type, user_status, user_remark, creation_date, exit_mailing_id, mailinglist_id) (SELECT customer_id, 'W', 1, 'CSV File Upload', " + currentTimestamp + ", 0," + mailinglistId + " FROM cust_" + companyID + "_exist1_tmp" + datasourceId + "_tbl)";
                    added += jdbc.update(sql);
                    mailinglistStat.put(mailinglistId, mailinglistStat.get(mailinglistId) + added);
                } else if (mode == ImportMode.MARK_OPT_OUT.getIntValue()) {
                    int changed = changeStatusInMailingList(companyID, updatedRecipients, jdbc, mailinglistId, BindingEntry.USER_STATUS_OPTOUT, "Mass Opt-Out by Admin", currentTimestamp);
                    mailinglistStat.put(mailinglistId, mailinglistStat.get(mailinglistId) + changed);
                } else if (mode == ImportMode.MARK_BOUNCED.getIntValue()) {
                    int changed = changeStatusInMailingList(companyID, updatedRecipients, jdbc, mailinglistId, BindingEntry.USER_STATUS_BOUNCED, "Mass Bounce by Admin", currentTimestamp);
                    mailinglistStat.put(mailinglistId, mailinglistStat.get(mailinglistId) + changed);
                }
            } catch (Exception e) {
            	logger.error( "writeContent: " + e.getMessage(), e);
            }
            finally {
                removeBindTemporaryTable(companyID, datasourceId, jdbc);
            }
        }
    }

    @Override
    public HashMap<ProfileRecipientFields, ValidatorResults> getRecipientsByTypePaginated(Integer[] types, int page, int rownums, Integer adminID, int datasourceId) {
        HashMap<ProfileRecipientFields, ValidatorResults> recipients = new HashMap<ProfileRecipientFields, ValidatorResults>();
        if (types == null || types.length == 0) {
            return recipients;
        }

        final JdbcTemplate aTemplate = getJdbcTemplateForTemporaryTable();
        final String prefix = "cust_" + adminID + "_tmp_";
        final String tableName = prefix + datasourceId + "_tbl";
        String typesStr = "(" + StringUtils.join(types, ",") + ")";
        int offset = (page) * rownums;
        String sqlStatement = "SELECT * FROM " + tableName + " WHERE status_type IN " + typesStr;
        if (AgnUtils.isMySQLDB()) {
            sqlStatement = sqlStatement + " LIMIT  " + offset + " , " + rownums;
        }
        if (AgnUtils.isOracleDB()) {
            sqlStatement = "SELECT * FROM ( SELECT recipient, validator_result, rownum r FROM ( " + sqlStatement + " )  WHERE 1=1 ) WHERE r BETWEEN " + (offset + 1) + " AND " + (offset + rownums);
        }
        List<Map> tmpList = aTemplate.queryForList(sqlStatement);


        for (Map row : tmpList) {
            Object recipientBean = ImportUtils.deserialiseBean((byte[]) row.get("recipient"));
            final ProfileRecipientFields recipient = (ProfileRecipientFields) recipientBean;
            ValidatorResults validatorResults = null;
            if (row.get("validator_result") != null) {
                Object validatorResultsBean = ImportUtils.deserialiseBean((byte[]) row.get("validator_result"));
                validatorResults = (ValidatorResults) validatorResultsBean;
            }
            recipients.put(recipient, validatorResults);
        }

        return recipients;
    }

    @Override
    public int getRecipientsCountByType(Integer[] types, Integer adminID, int datasourceId) {
        final JdbcTemplate aTemplate = getJdbcTemplateForTemporaryTable();
        final String prefix = "cust_" + adminID + "_tmp_";
        final String tableName = prefix + datasourceId + "_tbl";
        String typesStr = "(" + StringUtils.join(types, ",") + ")";
        int totalRows = aTemplate.queryForInt("SELECT count(temporary_id) FROM " + tableName + " WHERE status_type IN " + typesStr);
        return totalRows;
    }


    @Override
    public PaginatedList getInvalidRecipientList(CSVColumnState[] columns, String sort, String direction, int page, int rownums, int previousFullListSize, Integer adminID, int datasource_id) throws Exception {
        final JdbcTemplate aTemplate = getJdbcTemplateForTemporaryTable();
        final String prefix = "cust_" + adminID + "_tmp_";
        final String tableName = prefix + datasource_id + "_tbl";
        int totalRows = aTemplate.queryForInt("SELECT count(temporary_id) FROM " + tableName + " WHERE status_type=" + NewImportWizardService.RECIPIENT_TYPE_FIELD_INVALID);
        if (previousFullListSize == 0 || previousFullListSize != totalRows) {
            page = 1;
        }

        int offset = (page - 1) * rownums;
        String sqlStatement = "SELECT * FROM " + tableName + " where status_type=" + NewImportWizardService.RECIPIENT_TYPE_FIELD_INVALID;
        if (AgnUtils.isMySQLDB()) {
            sqlStatement = sqlStatement + " LIMIT  " + offset + " , " + rownums;
        }
        if (AgnUtils.isOracleDB()) {
            sqlStatement = "SELECT * from ( select recipient, validator_result, rownum r from ( " + sqlStatement + " )  where 1=1 ) where r between " + (offset + 1) + " and " + (offset + rownums);
        }
        List<Map> tmpList = aTemplate.queryForList(sqlStatement);


        List<Map> result = new ArrayList<Map>();
        for (Map row : tmpList) {
            Map newBean = new HashMap();
            final ProfileRecipientFields recipient = (ProfileRecipientFields) ImportUtils.deserialiseBean((byte[]) row.get("recipient"));
            final ValidatorResults validatorResult = (ValidatorResults) ImportUtils.deserialiseBean((byte[]) row.get("validator_result"));
            for (CSVColumnState column : columns) {
                if (column.getImportedColumn()) {
                    newBean.put(column.getColName(), Toolkit.getValueFromBean(recipient, column.getColName()));
                }
            }
            newBean.put(NewImportWizardService.VALIDATOR_RESULT_RESERVED, validatorResult);
            newBean.put(NewImportWizardService.ERROR_EDIT_RECIPIENT_EDIT_RESERVED, recipient);
            result.add(newBean);
        }

        PaginatedListImpl paginatedList = new PaginatedListImpl(result, totalRows, rownums, page, sort, direction);
        return paginatedList;
    }

    @Override
    public Set<String> loadBlackList(int companyID) throws Exception {
        final JdbcTemplate jdbcTemplate = createJdbcTemplate();
        SqlRowSet rset = null;
        Set<String> blacklist = new HashSet<String>();
        try {
        	if (AgnUtils.isOracleDB()) {
        		// ignore cust_ban_tbl so that global blacklisted addresses can be imported to local blacklist
        		rset = jdbcTemplate.queryForRowSet("SELECT email FROM cust" + companyID + "_ban_tbl");
        	} else {
        		rset = jdbcTemplate.queryForRowSet("SELECT email FROM cust_ban_tbl");
        	}
        	while (rset.next()) {
        		blacklist.add(rset.getString(1).toLowerCase());
        	}
        } catch (Exception e) {
        	logger.error( "loadBlacklist: " + e.getMessage(), e);

        	throw new Exception(e.getMessage());
        }
        return blacklist;
    }

    @Override
    public HashMap<ProfileRecipientFields, ValidatorResults> getDuplicateRecipientsFromNewDataOnly(Map<ProfileRecipientFields, ValidatorResults> listOfValidBeans, ImportProfile profile, CSVColumnState[] columns, Integer adminID, int datasource_id) {
        final HashMap<ProfileRecipientFields, ValidatorResults> result = new HashMap<ProfileRecipientFields, ValidatorResults>();
        if (listOfValidBeans.isEmpty()) {
            return result;
        }

        final String prefix = "cust_" + adminID + "_tmp_";
        final String tableName = prefix + datasource_id + "_tbl";
        final HashMap<ImportKeyColumnsKey, ProfileRecipientFields> columnKeyValueToTemporaryIdMap = new HashMap<ImportKeyColumnsKey, ProfileRecipientFields>();
        final JdbcTemplate template = getJdbcTemplateForTemporaryTable();
        List parameters = new ArrayList();
        Map<String, List<Object>> parametersMap = new HashMap<String, List<Object>>();
        String columnKeyBuffer = "(";
        for (ProfileRecipientFields profileRecipientFields : listOfValidBeans.keySet()) {
            ImportKeyColumnsKey keyValue = ImportKeyColumnsKey.createInstance(profile, profileRecipientFields, columns);
            if (columnKeyValueToTemporaryIdMap.containsKey(keyValue)) {
                result.put(profileRecipientFields, null);
                continue;
            }

            columnKeyBuffer += keyValue.getParametersString();
            keyValue.addParameters(parametersMap);

            columnKeyValueToTemporaryIdMap.put(keyValue, profileRecipientFields);
        }
        columnKeyBuffer = columnKeyBuffer.substring(0, columnKeyBuffer.length() - 1);
        columnKeyBuffer = columnKeyBuffer + ")";
        ImportKeyColumnsKey keyColumnsKey = columnKeyValueToTemporaryIdMap.keySet().iterator().next();
        Iterator<String> keyColumnIterator = keyColumnsKey.getKeyColumnsMap().keySet().iterator();
        StringBuffer sqlQuery = new StringBuffer("SELECT ");
        StringBuffer wherePart = new StringBuffer("");
        int index = 0;
        while (keyColumnIterator.hasNext()) {
            String keyColumnName = keyColumnIterator.next();
            CSVColumnState columnState = keyColumnsKey.getKeyColumnsMap().get(keyColumnName);
            String column = "i.column_duplicate_check_" + index;
            String columnAlias = ImportKeyColumnsKey.KEY_COLUMN_PREFIX + keyColumnName;
            sqlQuery.append(column + " AS " + columnAlias + ",");
            int type = columnState.getType();
            if (AgnUtils.isOracleDB() && (keyColumnName.equals("email") || type == CSVColumnState.TYPE_NUMERIC || type == CSVColumnState.TYPE_DATE)) {
                wherePart.append(column);
            } else {
                wherePart.append("LOWER(" + column + ")");
            }
            wherePart.append(" IN " + columnKeyBuffer + " AND ");
            // gather parameters
            List<Object> objectList = parametersMap.get(keyColumnName);
            if (objectList != null){
                parameters.addAll(objectList);
            }
            index++;
        }

        sqlQuery.delete(sqlQuery.length() - 1, sqlQuery.length());
        sqlQuery.append(" FROM " + tableName + " i  WHERE (");
        sqlQuery.append(wherePart);
        sqlQuery.append("(i.status_type=" +
                    NewImportWizardService.RECIPIENT_TYPE_VALID + " OR i.status_type=" + NewImportWizardService.RECIPIENT_TYPE_FIXED_BY_HAND + " OR i.status_type=" + NewImportWizardService.RECIPIENT_TYPE_DUPLICATE_RECIPIENT + "))");

        final List<Map> resultList = template.queryForList(sqlQuery.toString(), parameters.toArray());
        for (Map row : resultList) {
            ImportKeyColumnsKey columnsKey = ImportKeyColumnsKey.createInstance(row);
            ProfileRecipientFields recipientFields = columnKeyValueToTemporaryIdMap.get(columnsKey);
            if(recipientFields != null){
                result.put(recipientFields, null);
            }
        }
        return result;
    }

    private Date createDateValue(Date date) {
        return (AgnUtils.isOracleDB()) ? new Timestamp(date.getTime()) : new Timestamp(date.getTime()) ;
    }

    @Override
    public void updateRecipients(final Map<ProfileRecipientFields, ValidatorResults> recipientBeans, Integer adminID, final int type, final ImportProfile profile, int datasource_id, CSVColumnState[] columns) {
        if (recipientBeans.isEmpty()) {
            return;
        }
        final JdbcTemplate template = getJdbcTemplateForTemporaryTable();
        final String prefix = "cust_" + adminID + "_tmp_";
        final String tableName = prefix + datasource_id + "_tbl";
        final ProfileRecipientFields[] recipients = recipientBeans.keySet().toArray(new ProfileRecipientFields[recipientBeans.keySet().size()]);
        String keyColumn = profile.getKeyColumn();
        List<String> keyColumns = profile.getKeyColumns();
        String duplicateSql = "";
        if (keyColumns.isEmpty()) {
            duplicateSql += " column_duplicate_check_0=? ";
        } else {
            for (int i = 0; i < keyColumns.size(); i++) {
                duplicateSql += " column_duplicate_check_" + i+"=? ";
                if (i != keyColumns.size() - 1) {
                    duplicateSql += ", ";
                }
            }
        }
        final String query = "UPDATE  " + tableName + " SET recipient=?, validator_result=?, status_type=?, " + duplicateSql + " WHERE temporary_id=?";
        final List<CSVColumnState> temporaryKeyColumns = new ArrayList<CSVColumnState>();
        for (CSVColumnState column : columns) {
            if (keyColumns.isEmpty()) {
                if (column.getColName().equals(keyColumn) && column.getImportedColumn()) {
                    temporaryKeyColumns.add(column);
                }
            } else {
                for (String columnName : keyColumns){
                    if (column.getColName().equals(columnName) && column.getImportedColumn()) {
                        temporaryKeyColumns.add(column);
                        break;
                    }
                }
            }
        }
        final BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {

            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setBytes(1, ImportUtils.getObjectAsBytes(recipients[i]));
                ps.setBytes(2, ImportUtils.getObjectAsBytes(recipientBeans.get(recipients[i])));
                ps.setInt(3, type);
                for (int j = 0; j < temporaryKeyColumns.size(); j++) {
                    setPreparedStatmentForCurrentColumn(ps, 4 + j, temporaryKeyColumns.get(j), recipients[i], profile, recipientBeans.get(recipients[i]));
                }
                ps.setString(4 + temporaryKeyColumns.size(), recipients[i].getTemporaryId());

                if( logger.isInfoEnabled()) {
                	logger.info("Import ID: " + profile.getImportId() + " Updating recipient in temp-table: " + Toolkit.getValueFromBean(recipients[i], profile.getKeyColumn()));
                }
            }

            public int getBatchSize() {
                return recipientBeans.size();
            }
        };
        template.batchUpdate(query, setter);
    }

    @Override
    public void addNewRecipients(final Map<ProfileRecipientFields, ValidatorResults> validRecipients, Integer adminId, final ImportProfile importProfile, final CSVColumnState[] columns, final int datasourceID) throws Exception {
        if (validRecipients.isEmpty()) {
            return;
        }

		String currentTimestamp = AgnUtils.getHibernateDialect().getCurrentTimestampSQLFunctionName();

        final JdbcTemplate template = createJdbcTemplate();
        final ProfileRecipientFields[] recipientsBean = validRecipients.keySet().toArray(new ProfileRecipientFields[validRecipients.size()]);

        final int[] newcustomerIDs = getNextCustomerSequences(importProfile.getCompanyId(), recipientsBean.length);

        final String tableName = "customer_" + importProfile.getCompanyId() + "_tbl";

        String query = "INSERT INTO " + tableName + " (";

        if (AgnUtils.isOracleDB()) {
            query = query + "customer_id,";
        }
        boolean isGenderMapped = false;
        query = query + "mailtype, datasource_id, ";
        for (CSVColumnState column : columns) {
            if (column.getColName().equals("creation_date")){
                throw new Exception(" creation_date column is not allowed to be imported");
            }
            if (column.getImportedColumn() && !column.getColName().equals("mailtype")) {
                query = query + column.getColName() + ", ";
            }
            if (column.getImportedColumn() && column.getColName().equals("gender")) {
                isGenderMapped = true;
            }
        }
        if (!isGenderMapped) {
            if (AgnUtils.isOracleDB()) {
                query = query + "gender, ";
            }
        }
        query = query.substring(0, query.length() - 2);
		query = query + ", creation_date) VALUES (";

        if (AgnUtils.isOracleDB()) {
            query = query + "?, ";
        }

        for (CSVColumnState column : columns) {
            if (column.getImportedColumn() && !column.getColName().equals("mailtype")) {
                query = query + "?, ";
            }
        }
        if (!isGenderMapped) {
            if (AgnUtils.isOracleDB()) {
                query = query + "?, ";
            }
        }
        query = query + "?, ?, ";
        query = query.substring(0, query.length() - 2);
        query = query + ", " + currentTimestamp + ")";
        final Boolean finalIsGenderMapped = isGenderMapped;
        final BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ProfileRecipientFields profileRecipientFields = recipientsBean[i];
                Integer mailtype = Integer.valueOf(profileRecipientFields.getMailtype());

                int index = 0;
                if (AgnUtils.isOracleDB()) {
                    ps.setInt(1, newcustomerIDs[i]);
                    ps.setInt(2, mailtype);
                    ps.setInt(3, datasourceID);
                    index = 4;
                }
                if (AgnUtils.isMySQLDB()) {
                    ps.setInt(1, mailtype);
                    ps.setInt(2, datasourceID);
                    index = 3;
                }
                for (CSVColumnState column : columns) {
                    if (column.getImportedColumn() && !column.getColName().equals("mailtype")) {
                        setPreparedStatmentForCurrentColumn(ps, index, column, profileRecipientFields, importProfile, null);
                        index++;
                    }
                }
                if (!finalIsGenderMapped) {
                	if (AgnUtils.isOracleDB()) {
                		ps.setInt(index, 2);
                	}
                }

                if( logger.isInfoEnabled()) {
                	logger.info("Import ID: " + importProfile.getImportId() + " Adding recipient to recipient-table: " + Toolkit.getValueFromBean(profileRecipientFields, importProfile.getKeyColumn()));
                }
            }

            public int getBatchSize() {
                return validRecipients.size();
            }
        };

        template.batchUpdate(query, setter);
    }

    private void setPreparedStatmentForCurrentColumn(PreparedStatement ps, int index, CSVColumnState column, ProfileRecipientFields bean, ImportProfile importProfile, ValidatorResults validatorResults) throws SQLException {
         String value = Toolkit.getValueFromBean(bean, column.getColName());
        if (column.getType() == CSVColumnState.TYPE_NUMERIC && column.getColName().equals("gender")) {
            if (StringUtils.isEmpty(value) || value == null) {
                ps.setInt(index, 2);
            } else {
                if (GenericValidator.isInt(value) && Integer.valueOf(value) <= 5 && Integer.valueOf(value) >= 0) {
                    ps.setInt(index, Integer.valueOf(value));
                } else {
                    final Integer intValue = importProfile.getGenderMapping().get(value);
                    ps.setInt(index, intValue);
                }
            }

        } else if (column.getType() == CSVColumnState.TYPE_CHAR) {
            if (value == null) {
                ps.setNull(index, Types.VARCHAR);
            } else {
                String columnName = column.getColName();
                if (columnName.equals("email")) {
                    value = value.toLowerCase();
                    if (validatorResults != null && !ImportUtils.checkIsCurrentFieldValid(validatorResults, "email", "checkRange")) {
                        throw new ImportRecipientsToolongValueException(value);
                    }
                } else if (importProfile.getKeyColumns().contains(columnName) || (importProfile.getKeyColumns().isEmpty() && columnName.equals(importProfile.getKeyColumn()))) {
                    // range validation for keyColumn
                    if (validatorResults != null && !ImportUtils.checkIsCurrentFieldValid(validatorResults, columnName, "checkRange")) {
                        throw new ImportRecipientsToolongValueException(value);
                    }
                }
                if (AgnUtils.isOracleDB()){
                	ps.setString(index, value);
                } else if (AgnUtils.isMySQLDB()) {
					if (column.isNullable() && value.isEmpty()) {
					    ps.setNull(index, Types.VARCHAR);
					}
					else {
                    	ps.setString(index, value);
					}
                }

            }
        } else if (column.getType() == CSVColumnState.TYPE_NUMERIC) {
            if (StringUtils.isEmpty(value) || value == null) {
                ps.setNull(index, Types.NUMERIC);
            } else {
                ps.setDouble(index, Double.valueOf(value));
            }
        } else if (column.getType() == CSVColumnState.TYPE_DATE) {
            if (StringUtils.isEmpty(value) || value == null) {
                ps.setNull(index, Types.DATE);
            } else {
                Date date = ImportUtils.getDateAsString(value, importProfile.getDateFormat());

                ps.setTimestamp(index, new Timestamp(date.getTime()));
            }
        }
    }

    @Override
    public HashMap<ProfileRecipientFields, ValidatorResults> getDuplicateRecipientsFromExistData(Map<ProfileRecipientFields, ValidatorResults> listOfValidBeans, ImportProfile profile, CSVColumnState[] columns) {
        final HashMap<ProfileRecipientFields, ValidatorResults> result = new HashMap<ProfileRecipientFields, ValidatorResults>();
        if (listOfValidBeans.isEmpty()) {
            return result;
        }
        final HashMap<ImportKeyColumnsKey, ProfileRecipientFields> columnKeyValueToTemporaryIdMap = new HashMap<ImportKeyColumnsKey, ProfileRecipientFields>();
        final JdbcTemplate template = createJdbcTemplate();
        List parameters = new ArrayList();
        Map<String, List<Object>> parametersMap = new HashMap<String, List<Object>>();
        String columnKeyBuffer = "(";
        for (ProfileRecipientFields profileRecipientFields : listOfValidBeans.keySet()) {
            ImportKeyColumnsKey keyValue = ImportKeyColumnsKey.createInstance(profile, profileRecipientFields, columns);
            if (columnKeyValueToTemporaryIdMap.containsKey(keyValue)) {
                result.put(profileRecipientFields, null);
                continue;
            }

            columnKeyBuffer += keyValue.getParametersString();
            keyValue.addParameters(parametersMap);

            columnKeyValueToTemporaryIdMap.put(keyValue, profileRecipientFields);
        }
        columnKeyBuffer = columnKeyBuffer.substring(0, columnKeyBuffer.length() - 1);
        columnKeyBuffer = columnKeyBuffer + ")";

        ImportKeyColumnsKey keyColumnsKey = columnKeyValueToTemporaryIdMap.keySet().iterator().next();
        Iterator<String> keyColumnIterator = keyColumnsKey.getKeyColumnsMap().keySet().iterator();
        StringBuffer sqlQuery = new StringBuffer("SELECT customer_id, ");
        StringBuffer wherePart = new StringBuffer("");
        Map<String, Integer> columnTypes = new HashMap<String, Integer>();
        while (keyColumnIterator.hasNext()) {
            String keyColumnName = keyColumnIterator.next();
            CSVColumnState columnState = keyColumnsKey.getKeyColumnsMap().get(keyColumnName);
            String column = keyColumnName;
            String columnAlias = ImportKeyColumnsKey.KEY_COLUMN_PREFIX + keyColumnName;
            sqlQuery.append(column + " AS " + columnAlias + ",");
            int type = columnState.getType();
            if (AgnUtils.isOracleDB() && (keyColumnName.equals("email") || type == CSVColumnState.TYPE_NUMERIC || type == CSVColumnState.TYPE_DATE)) {
                wherePart.append(column);
            } else {
                wherePart.append("LOWER(" + column + ")");

            }
            wherePart.append(" IN " + columnKeyBuffer + " AND ");
            // gather parameters
            List<Object> objectList = parametersMap.get(keyColumnName);
            if (objectList != null){
                parameters.addAll(objectList);
            }
            columnTypes.put(columnAlias, type);
        }

        sqlQuery.delete(sqlQuery.length() - 1, sqlQuery.length());
        wherePart.delete(wherePart.length() - 4, wherePart.length());
        sqlQuery.append(" FROM customer_" + profile.getCompanyId() + "_tbl c WHERE (");
        sqlQuery.append(wherePart);
        sqlQuery.append(")");

        final List<Map> resultList = template.queryForList(sqlQuery.toString(), parameters.toArray());
        for (Map row : resultList) {
            ImportKeyColumnsKey columnsKey = ImportKeyColumnsKey.createInstance(row);
            ProfileRecipientFields recipientFields = columnKeyValueToTemporaryIdMap.get(columnsKey);
            if(recipientFields != null){
                result.put(recipientFields, null);
                if (profile.getUpdateAllDuplicates() || (recipientFields.getUpdatedIds() == null || recipientFields.getUpdatedIds().size() == 0)) {
                    recipientFields.addUpdatedIds(((Number) row.get("customer_id")).intValue());
                }
            }
        }
        return result;
    }

    @Override
    public void updateExistRecipients(final Collection<ProfileRecipientFields> recipientsForUpdate, final ImportProfile importProfile, final CSVColumnState[] columns, Integer adminId) {
        if (recipientsForUpdate.isEmpty()) {
            return;
        }

        final JdbcTemplate template = createJdbcTemplate();
        final ProfileRecipientFields[] recipientsBean = recipientsForUpdate.toArray(new ProfileRecipientFields[recipientsForUpdate.size()]);
        final String[] querys = new String[recipientsForUpdate.size()];
        for (int i = 0; i < querys.length; i++) {
            String query = "UPDATE  customer_" + importProfile.getCompanyId() + "_tbl SET ";
            if(recipientsBean[i].getMailtypeDefined().equals(ImportUtils.MAIL_TYPE_DEFINED))
                query = query + "mailtype=" + recipientsBean[i].getMailtype() + ", ";
            for (CSVColumnState column : columns) {
                if (column.getImportedColumn() && !column.getColName().equals("mailtype")) {
                     String value = Toolkit.getValueFromBean(recipientsBean[i], column.getColName());

                    // @todo: agn: value == null
                    if (StringUtils.isEmpty(value) && importProfile.getNullValuesAction() == NullValuesAction.OVERWRITE.getIntValue() && !column.getColName().equals("gender")) {
                        query = query + column.getColName() + "=NULL, ";
                    } else if (!StringUtils.isEmpty(value)) {
                        if (column.getColName().equals("gender")) {
                            if (StringUtils.isEmpty(value)) {
                                query = query + column.getColName() + "=2, ";
                            } else {
                            if (GenericValidator.isInt(value)) {
                                query = query + column.getColName() + "=" + value + ", ";
                            } else {
                                final Integer intValue = importProfile.getGenderMapping().get(value);
                                query = query + column.getColName() + "=" + intValue + ", ";
                            }
                            }
                        } else {
                            switch (column.getType()) {
                                case CSVColumnState.TYPE_CHAR:
                                    if (column.getColName().equals("email")) {
                                        value = value.toLowerCase();
                                    }
                                    if (AgnUtils.isOracleDB()){
                                        query = query + column.getColName() + "='" + value.replace("'","''") + "', ";
                                    } else if (AgnUtils.isMySQLDB()) {
                                        query = query + column.getColName() + "='" + value.replace("\\","\\\\").replace("'","\\'") + "', ";
                                    }
                                    break;
                                case CSVColumnState.TYPE_NUMERIC:
                                    query = query + column.getColName() + "=" + value + ", ";
                                    break;
                                case CSVColumnState.TYPE_DATE:
                                    if (StringUtils.isEmpty(value) || value == null) {
                                        query = query + column.getColName() + "=null, ";
                                    } else {
                                    final int format = importProfile.getDateFormat();
                                    Date date = ImportUtils.getDateAsString(value, format);
                                    if (AgnUtils.isMySQLDB()) {
                                            String temTimestamp = new Timestamp(date.getTime()).toString();
                                            query = query + column.getColName() + "='" + temTimestamp.substring(0, temTimestamp.indexOf(" ")) + "', ";
                                    }
                                    if (AgnUtils.isOracleDB()) {
                                        final String dateAsFormatedString = DB_DATE_FORMAT.format(date);
                                        query = query + column.getColName() + "=to_date('"
                                                + dateAsFormatedString + "', 'dd.MM.YYYY HH24:MI:SS'), ";
                                    }
                                    }
                                    break;
                            }
                        }
                    }
                }
            }

            query = query.substring(0, query.length() - 2);
            String value = Toolkit.getValueFromBean(recipientsBean[i], importProfile.getKeyColumn());
            value = value.toLowerCase();
            if (!importProfile.getUpdateAllDuplicates()) {
                query = query + " WHERE customer_id = " + recipientsBean[i].getUpdatedIds().get(0);
            } else {
                query = query + " WHERE customer_id IN(";
                final int countUpdatedRecipients = recipientsBean[i].getUpdatedIds().size();
                for (int index = 0; index < countUpdatedRecipients; index++) {
                    query = query + recipientsBean[i].getUpdatedIds().get(index) + ((index + 1) != countUpdatedRecipients ? "," : "");
                }
                query = query + ")";

            }

            if( logger.isInfoEnabled()) {
            	logger.info("Import ID: " + importProfile.getImportId() + " Updating recipient in recipient-table: " + Toolkit.getValueFromBean(recipientsBean[i], importProfile.getKeyColumn()));
            }

            querys[i] = query;
        }
        template.batchUpdate(querys);
    }


    @Override
    public void importInToBlackList(final Collection<ProfileRecipientFields> recipients, final int companyId) {
        if (recipients.isEmpty()) {
            return;
        }
        final JdbcTemplate template = createJdbcTemplate();
        final ProfileRecipientFields[] recipientsArray = recipients.toArray(new ProfileRecipientFields[recipients.size()]);
        String query;
        if (AgnUtils.isOracleDB()) {
        	query = "INSERT INTO cust" + companyId + "_ban_tbl (email) VALUES (?)";
        } else {
        query = "INSERT INTO cust_ban_tbl (company_id, email) VALUES (?,?)";
        }
        final BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
            	if (AgnUtils.isOracleDB()) {
            		ps.setString(1, recipientsArray[i].getEmail().toLowerCase());
            	} else {
            		ps.setInt(1, companyId);
            		ps.setString(2, recipientsArray[i].getEmail().toLowerCase());
            	}
            }

            public int getBatchSize() {
                return recipients.size();
            }
        };
        template.batchUpdate(query, setter);
    }

    @Override
    public void createTemporaryTable(int adminID, int datasource_id, String keyColumn, int companyId, String sessionId){
        createTemporaryTable(adminID, datasource_id, keyColumn, new ArrayList<String>(), companyId, sessionId);
    }

    @Override
    public void createTemporaryTable(int adminID, int datasource_id, String keyColumn, List<String> keyColumns, int companyId, String sessionId) {
        final DataSource dataSource = (DataSource) applicationContext.getBean("dataSource");
        try {
            if (temporaryConnection != null) {
                temporaryConnection.destroy();
                temporaryConnection = null;
            }
            SingleConnectionDataSource scds = null;
            scds = new SingleConnectionDataSource(dataSource.getConnection(), true);
            setTemporaryConnection(scds);
        } catch (SQLException e) {
            throw new DataAccessResourceFailureException("Unable to create single connection data source", e);
        }

        final JdbcTemplate template = getJdbcTemplateForTemporaryTable();
        final String prefix = "cust_" + adminID + "_tmp_";
        final String tableName = prefix + datasource_id + "_tbl";


        String indexSql = "";
        String duplicateSql = "";
        if (keyColumns.isEmpty()) {
            duplicateSql += keyColumn + " as column_duplicate_check_0 ";
            indexSql = "column_duplicate_check_0";
        } else {
            for (int i = 0; i < keyColumns.size(); i++) {
                duplicateSql += keyColumns.get(i) + " as column_duplicate_check_" + i;
                indexSql += "column_duplicate_check_" + i;
                if (i != keyColumns.size() - 1) {
                    duplicateSql += ", ";
                    indexSql += ", ";
                }
            }
        }
        duplicateSql += " from customer_" + companyId + "_tbl where 1=0)";

        if (AgnUtils.isMySQLDB()) {
            String query = "CREATE TEMPORARY TABLE IF NOT EXISTS " + tableName + " as (select ";
            query += duplicateSql;
            template.execute(query);
            query = "ALTER TABLE " + tableName + " ADD (recipient mediumblob NOT NULL, " +
                    "validator_result mediumblob NOT NULL, " +
                    "temporary_id varchar(128) NOT NULL, " +
                    "INDEX ("+indexSql+"), " +
                    "status_type int(3) NOT NULL)";
            template.execute(query);
            query = "alter table " + tableName + " collate utf8_unicode_ci";
            template.execute(query);
        } else if (AgnUtils.isOracleDB()) {
            // @todo: we need to decide when all those tables will be removed
            String query = "CREATE TABLE " + tableName + " as (select ";
            query += duplicateSql;
            template.execute(query);
            query = "ALTER TABLE " + tableName + " ADD (recipient blob NOT NULL, " +
                    "validator_result blob NOT NULL, " +
                    "temporary_id varchar2(128) NOT NULL, " +
                    "status_type number(3) NOT NULL)";
            template.execute(query);
            String indexquery = "create index " + tableName + "_cdc on " + tableName + " (" + indexSql + ") nologging";
            template.execute(indexquery);
            query = " INSERT INTO IMPORT_TEMPORARY_TABLES (SESSION_ID, TEMPORARY_TABLE_NAME) VALUES('" + sessionId + "', '" + tableName + "')";
            template.execute(query);
        }
    }

    private int changeStatusInMailingList(int companyID, List<Integer> updatedRecipients, JdbcTemplate jdbc,
                                          int mailinglistId, int newStatus, String remark, String currentTimestamp) {
        if (updatedRecipients.size() == 0) {
            return 0;
        }
        String recipientsStr = StringUtils.join(updatedRecipients, ',');
        String sql = "UPDATE customer_" + companyID + "_binding_tbl SET user_status=" + newStatus +
                ", exit_mailing_id=0, user_remark='" + remark + "', " + AgnUtils.changeDateName() + "=" + currentTimestamp +
                " WHERE mailinglist_id=" + mailinglistId + " AND customer_id IN (" + recipientsStr +
                ") AND user_status=" + BindingEntry.USER_STATUS_ACTIVE;
        return jdbc.update(sql);
    }

    private void createRecipientBindTemporaryTable(int companyID, int datasourceId, final List<Integer> updatedRecipients, JdbcTemplate jdbc) {
        String sql = removeBindTemporaryTable(companyID, datasourceId, jdbc);
        if (AgnUtils.isMySQLDB()) {
            sql = "CREATE TEMPORARY TABLE cust_" + companyID + "_exist1_tmp" + datasourceId + "_tbl (`customer_id` int(10) unsigned NOT NULL)";
        } else if (AgnUtils.isOracleDB()) {
            sql = "CREATE TABLE cust_" + companyID + "_exist1_tmp" + datasourceId + "_tbl (customer_id NUMBER(10) NOT NULL)";
        }
        jdbc.execute(sql);
        if (updatedRecipients.isEmpty()) {
            return;
        }
        sql = "INSERT INTO cust_" + companyID + "_exist1_tmp" + datasourceId + "_tbl (customer_id) VALUES (?)";

        final BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, updatedRecipients.get(i));
            }

            public int getBatchSize() {
                return updatedRecipients.size();
            }
        };
        jdbc.batchUpdate(sql, setter);
    }

    private String removeBindTemporaryTable(int companyID, int datasourceId, JdbcTemplate jdbc) {
        final String tablename = "cust_" + companyID + "_exist1_tmp" + datasourceId + "_tbl";
        if (AgnUtils.isOracleDB()) {
            String query = "select count(*) from user_tables where table_name = '" + tablename.toUpperCase() + "'";
            int totalRows = jdbc.queryForInt(query);

            if (totalRows != 0) {
                String sql = "DROP TABLE " + tablename;

                jdbc.execute(sql);
            }
        }

        if (AgnUtils.isMySQLDB()) {
            String sql = "DROP TABLE IF EXISTS " + tablename;
            try {
                jdbc.execute(sql);
            } catch (Exception e) {
            	if( logger.isInfoEnabled()) {
            		logger.info("Tried to remove table that doesn't exist", e);
            	}
            }
        }
        return "";
    }

    private int getNextCustomerSequence(int companyID, JdbcTemplate template) {
        String query = " SELECT customer_" + companyID + "_tbl_seq.nextval FROM DUAL ";
        return template.queryForInt(query);
    }

    private int[] getNextCustomerSequences(int companyID, int amount) {
        int[] customerids = new int[amount];
        if (AgnUtils.isOracleDB()) {
            JdbcTemplate template = createJdbcTemplate();
            for (int i = 0; i < amount; i++) {
                customerids[i] = getNextCustomerSequence(companyID, template);
            }
        }
        return customerids;
    }

    private JdbcTemplate getJdbcTemplateForTemporaryTable() {
        return new JdbcTemplate(temporaryConnection);
    }

    @Override
    public SingleConnectionDataSource getTemporaryConnection() {
        return temporaryConnection;
    }

    @Override
    public void setTemporaryConnection(SingleConnectionDataSource temporaryConnection) {
        this.temporaryConnection = temporaryConnection;
    }
}
