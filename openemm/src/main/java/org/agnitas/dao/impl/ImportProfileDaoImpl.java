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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.agnitas.beans.ColumnMapping;
import org.agnitas.beans.ImportProfile;
import org.agnitas.beans.impl.ColumnMappingImpl;
import org.agnitas.beans.impl.ImportProfileImpl;
import org.agnitas.dao.ImportProfileDao;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.ImportUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

/**
 * DAO handler for ImportProfile-Objects
 * This class is compatible with oracle and mysql datasources and databases  
 * 
 * @author Andreas Soderer
 * @date 03.05.2012
 */
public class ImportProfileDaoImpl extends BaseDaoImpl implements ImportProfileDao {
	private static final transient Logger logger = Logger.getLogger(ImportProfileDaoImpl.class);

	private static final String TABLE = "import_profile_tbl";
	
	private static final String FIELD_ID = "id";
	private static final String FIELD_SHORTNAME = "shortname";
	private static final String FIELD_COMPANY_ID = "company_id";
	private static final String FIELD_ADMIN_ID = "admin_id";
	private static final String FIELD_COLUMN_SEPARATOR = "column_separator";
	private static final String FIELD_TEXT_DELIMITER = "text_delimiter";
	private static final String FIELD_FILE_CHARSET = "file_charset";
	private static final String FIELD_DATE_FORMAT = "date_format";
	private static final String FIELD_IMPORT_MODE = "import_mode";
	private static final String FIELD_KEY_COLUMN = "key_column";
	private static final String FIELD_CHECK_FOR_DUPLICATES = "check_for_duplicates";
	private static final String FIELD_NULL_VALUES_ACTION = "null_values_action";
	private static final String FIELD_EXT_EMAIL_CHECK = "ext_email_check";
	private static final String FIELD_REPORT_EMAIL = "report_email";
	private static final String FIELD_MAIL_TYPE = "mail_type";
	private static final String FIELD_UPDATE_ALL_DUPLICATES = "update_all_duplicates";

	private static final String SELECT_NEXT_PROFILEID = "SELECT import_profile_tbl_seq.nextval FROM DUAL";
	private static final String SELECT_BY_ID = "SELECT * FROM " + TABLE + " WHERE " + FIELD_ID + " = ?";
	private static final String SELECT_BY_SHORTNAME = "SELECT * FROM " + TABLE + " WHERE UPPER(" + FIELD_SHORTNAME + ") = UPPER(?)";
	private static final String SELECT_BY_COMPANYID = "SELECT * FROM " + TABLE + " WHERE " + FIELD_COMPANY_ID + " = ?";
	private static final String DELETE = "DELETE FROM " + TABLE + " WHERE " + FIELD_ID + " = ?";
	private static final String UPDATE = "UPDATE " + TABLE + " SET " + FIELD_COMPANY_ID + " = ?, " + FIELD_ADMIN_ID + " = ?, " + FIELD_SHORTNAME + " = ?, " + FIELD_COLUMN_SEPARATOR + " = ?, " + FIELD_TEXT_DELIMITER + " = ?, " + FIELD_FILE_CHARSET + " = ?, " + FIELD_DATE_FORMAT + " = ?, " + FIELD_IMPORT_MODE + " = ?, " + FIELD_NULL_VALUES_ACTION + " = ?, " + FIELD_KEY_COLUMN + " = ?, " + FIELD_EXT_EMAIL_CHECK + " = ?, " + FIELD_REPORT_EMAIL + " = ?, " + FIELD_CHECK_FOR_DUPLICATES + " = ?, " + FIELD_MAIL_TYPE + " = ?, " + FIELD_UPDATE_ALL_DUPLICATES + " = ? WHERE " + FIELD_ID + " = ?";
	private static final String INSERT_ORACLE = "INSERT INTO " + TABLE + " (" + FIELD_ID + ", " + FIELD_COMPANY_ID + ", " + FIELD_ADMIN_ID + ", " + FIELD_SHORTNAME + ", " + FIELD_COLUMN_SEPARATOR + ", " + FIELD_TEXT_DELIMITER + ", " + FIELD_FILE_CHARSET + ", " + FIELD_DATE_FORMAT + ", " + FIELD_IMPORT_MODE + ", " + FIELD_NULL_VALUES_ACTION + ", " + FIELD_KEY_COLUMN + ", " + FIELD_EXT_EMAIL_CHECK + ", " + FIELD_REPORT_EMAIL + ", " + FIELD_CHECK_FOR_DUPLICATES + ", " + FIELD_MAIL_TYPE + ", " + FIELD_UPDATE_ALL_DUPLICATES + ") VALUES(" + AgnUtils.repeatString("?", 16, ", ") + ")";
	private static final String INSERT_MYSQL = "INSERT INTO " + TABLE + " (" + FIELD_COMPANY_ID + ", " + FIELD_ADMIN_ID + ", " + FIELD_SHORTNAME + ", " + FIELD_COLUMN_SEPARATOR + ", " + FIELD_TEXT_DELIMITER + ", " + FIELD_FILE_CHARSET + ", " + FIELD_DATE_FORMAT + ", " + FIELD_IMPORT_MODE + ", " + FIELD_NULL_VALUES_ACTION + ", " + FIELD_KEY_COLUMN + ", " + FIELD_EXT_EMAIL_CHECK + ", " + FIELD_REPORT_EMAIL + ", " + FIELD_CHECK_FOR_DUPLICATES + ", " + FIELD_MAIL_TYPE + ", " + FIELD_UPDATE_ALL_DUPLICATES + ") VALUES(" + AgnUtils.repeatString("?", 15, ", ") + ")";
	
	// COLUMN_MAPPING Table
	private static final String COLUMN_MAPPING_TABLE = "import_column_mapping_tbl";
	private static final String COLUMN_MAPPING_ID = "id";
	private static final String COLUMN_MAPPING_PROFILE_ID = "profile_id";
	private static final String COLUMN_MAPPING_MANDATORY = "mandatory";
	private static final String COLUMN_MAPPING_DB_COLUMN = "db_column";
	private static final String COLUMN_MAPPING_FILE_COLUMN = "file_column";
	private static final String COLUMN_MAPPING_DEFAULT_VALUE = "default_value";
	private static final String SELECT_COLUMN_MAPPINGS = "SELECT * FROM " + COLUMN_MAPPING_TABLE + " WHERE " + COLUMN_MAPPING_PROFILE_ID + " = ?";
	private static final String DELETE_COLUMN_MAPPINGS = "DELETE FROM " + COLUMN_MAPPING_TABLE + " WHERE " + COLUMN_MAPPING_PROFILE_ID + " = ?";
	private static final String INSERT_COLUMN_MAPPINGS_ORACLE = "INSERT INTO " + COLUMN_MAPPING_TABLE + " (" + COLUMN_MAPPING_ID + ", " + COLUMN_MAPPING_PROFILE_ID + ", " + COLUMN_MAPPING_FILE_COLUMN + ", " + COLUMN_MAPPING_DB_COLUMN + ", " + COLUMN_MAPPING_MANDATORY + ", " + COLUMN_MAPPING_DEFAULT_VALUE + ") VALUES (import_column_mapping_tbl_seq.nextval, ?, ?, ?, ?, ?)";
	private static final String INSERT_COLUMN_MAPPINGS_MYSQL = "INSERT INTO " + COLUMN_MAPPING_TABLE + " (" + COLUMN_MAPPING_PROFILE_ID + ", " + COLUMN_MAPPING_FILE_COLUMN + ", " + COLUMN_MAPPING_DB_COLUMN + ", " + COLUMN_MAPPING_MANDATORY + ", " + COLUMN_MAPPING_DEFAULT_VALUE + ") VALUES (?, ?, ?, ?, ?)";

	// GENDER_MAPPING Table
	private static final String GENDER_MAPPING_TABLE = "import_gender_mapping_tbl";
	private static final String GENDER_MAPPING_ID = "id";
	private static final String GENDER_MAPPING_PROFILE_ID = "profile_id";
	private static final String GENDER_MAPPING_STRING_GENDER = "string_gender";
	private static final String GENDER_MAPPING_INT_GENDER = "int_gender";
	private static final String SELECT_GENDER_MAPPINGS = "SELECT * FROM " + GENDER_MAPPING_TABLE + " WHERE " + GENDER_MAPPING_PROFILE_ID + " = ? ORDER BY " + GENDER_MAPPING_ID;
	private static final String DELETE_GENDER_MAPPINGS = "DELETE FROM " + GENDER_MAPPING_TABLE + " WHERE " + GENDER_MAPPING_PROFILE_ID + " = ?";
	private static final String INSERT_GENDER_MAPPINGS_ORACLE = "INSERT INTO " + GENDER_MAPPING_TABLE + " (" + GENDER_MAPPING_ID + ", " + GENDER_MAPPING_PROFILE_ID + ", " + GENDER_MAPPING_INT_GENDER + ", " + GENDER_MAPPING_STRING_GENDER + ") VALUES (import_gender_mapping_tbl_seq.nextval, ?, ?, ?)";
	private static final String INSERT_GENDER_MAPPINGS_MYSQL = "INSERT INTO " + GENDER_MAPPING_TABLE + " (" + GENDER_MAPPING_PROFILE_ID + ", " + GENDER_MAPPING_INT_GENDER + ", " + GENDER_MAPPING_STRING_GENDER + ") VALUES (?, ?, ?)";
	
	public int insertImportProfile(ImportProfile importProfile) {
		int profileId;
		if (AgnUtils.isOracleDB()) {
			logSqlStatement(logger, SELECT_NEXT_PROFILEID);
			profileId = getSimpleJdbcTemplate().queryForInt(SELECT_NEXT_PROFILEID);
	        
			logSqlStatement(logger, INSERT_ORACLE);
			getSimpleJdbcTemplate().update(
					INSERT_ORACLE,
					profileId,
					importProfile.getCompanyId(),
					importProfile.getAdminId(),
					importProfile.getName(),
					importProfile.getSeparator(),
					importProfile.getTextRecognitionChar(),
					importProfile.getCharset(),
					importProfile.getDateFormat(),
					importProfile.getImportMode(),
					importProfile.getNullValuesAction(),
					importProfile.getKeyColumn(),
					ImportUtils.getBooleanAsInt(importProfile.getExtendedEmailCheck()),
					importProfile.getMailForReport(),
					importProfile.getCheckForDuplicates(),
					importProfile.getDefaultMailType(),
					ImportUtils.getBooleanAsInt(importProfile.getUpdateAllDuplicates()));
		} else {
			logSqlStatement(logger, INSERT_MYSQL);
			SqlUpdate sqlUpdate = new SqlUpdate(getDataSource(), INSERT_MYSQL, new int[] { 
					Types.INTEGER, 
					Types.INTEGER, 
					Types.VARCHAR, 
					Types.INTEGER, 
					Types.INTEGER,
					Types.INTEGER, 
					Types.INTEGER, 
					Types.INTEGER, 
					Types.INTEGER, 
					Types.VARCHAR, 
					Types.BOOLEAN, 
					Types.VARCHAR, 
					Types.INTEGER, 
					Types.INTEGER, 
					Types.BOOLEAN });
            sqlUpdate.setReturnGeneratedKeys(true);
			sqlUpdate.setGeneratedKeysColumnNames(new String[] { FIELD_ID });
            sqlUpdate.compile();
            GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
            Object[] values = new Object[] {
            		importProfile.getCompanyId(),
					importProfile.getAdminId(),
					importProfile.getName(),
					importProfile.getSeparator(),
					importProfile.getTextRecognitionChar(),
					importProfile.getCharset(),
					importProfile.getDateFormat(),
					importProfile.getImportMode(),
					importProfile.getNullValuesAction(),
					importProfile.getKeyColumn(),
					ImportUtils.getBooleanAsInt(importProfile.getExtendedEmailCheck()),
					importProfile.getMailForReport(),
					importProfile.getCheckForDuplicates(),
					importProfile.getDefaultMailType(),
					ImportUtils.getBooleanAsInt(importProfile.getUpdateAllDuplicates()) };
            sqlUpdate.update(values, generatedKeyHolder);
            profileId = generatedKeyHolder.getKey().intValue();
		}
		
		importProfile.setId(profileId);
		insertColumnMappings(importProfile.getColumnMapping(), importProfile.getId());
		insertGenderMappings(importProfile.getGenderMapping(), importProfile.getId());
		
		return importProfile.getId();
	}
	
	public void updateImportProfile(ImportProfile importProfile) {
		logSqlStatement(logger, UPDATE);
		getSimpleJdbcTemplate().update(
				UPDATE, 
				importProfile.getCompanyId(),
				importProfile.getAdminId(),
				importProfile.getName(),
				importProfile.getSeparator(),
				importProfile.getTextRecognitionChar(),
				importProfile.getCharset(),
				importProfile.getDateFormat(),
				importProfile.getImportMode(),
				importProfile.getNullValuesAction(),
				importProfile.getKeyColumn(),
				ImportUtils.getBooleanAsInt(importProfile.getExtendedEmailCheck()),
				importProfile.getMailForReport(),
				importProfile.getCheckForDuplicates(),
				importProfile.getDefaultMailType(),
				ImportUtils.getBooleanAsInt(importProfile.getUpdateAllDuplicates()),
				importProfile.getId());
		
    	logSqlStatement(logger, DELETE_COLUMN_MAPPINGS, importProfile.getId());
		getSimpleJdbcTemplate().update(DELETE_COLUMN_MAPPINGS, importProfile.getId());
		insertColumnMappings(importProfile.getColumnMapping(), importProfile.getId());
		
    	logSqlStatement(logger, DELETE_GENDER_MAPPINGS, importProfile.getId());
		getSimpleJdbcTemplate().update(DELETE_GENDER_MAPPINGS, importProfile.getId());
		insertGenderMappings(importProfile.getGenderMapping(), importProfile.getId());
	}
	
    public ImportProfile getImportProfileById(int id) {
		try {
	    	logSqlStatement(logger, SELECT_BY_ID, id);
			return getSimpleJdbcTemplate().queryForObject(SELECT_BY_ID, new ImportProfileRowMapper(), id);
		} catch (DataAccessException e) {
			// No ImportProfile found
			return null;
		}
    }
    
    public ImportProfile getImportProfileByShortname(String shortname) {
		try {
	    	logSqlStatement(logger, SELECT_BY_SHORTNAME, shortname);
			return getSimpleJdbcTemplate().queryForObject(SELECT_BY_SHORTNAME, new ImportProfileRowMapper(), shortname);
		} catch (DataAccessException e) {
			// No ImportProfile found
			return null;
		}
    }
    
    public List<ImportProfile> getImportProfilesByCompanyId(int companyId) {
    	logSqlStatement(logger, SELECT_BY_COMPANYID, companyId);
		return getSimpleJdbcTemplate().query(SELECT_BY_COMPANYID, new ImportProfileRowMapper(), companyId);
    }
    
    public void deleteImportProfile(ImportProfile profile) {
    	deleteImportProfileById(profile.getId());
    }
    
    public void deleteImportProfileById(int profileId) {
    	logSqlStatement(logger, DELETE, profileId);
		getSimpleJdbcTemplate().update(DELETE, profileId);
    	logSqlStatement(logger, DELETE_COLUMN_MAPPINGS, profileId);
		getSimpleJdbcTemplate().update(DELETE_COLUMN_MAPPINGS, profileId);
    	logSqlStatement(logger, DELETE_GENDER_MAPPINGS, profileId);
		getSimpleJdbcTemplate().update(DELETE_GENDER_MAPPINGS, profileId);
    }
    
    private void insertColumnMappings(List<ColumnMapping> mappings, int importProfileId) {
    	if (mappings != null && !mappings.isEmpty()){
        	String insertStatementString = null;
	        if (AgnUtils.isOracleDB()) {
	        	insertStatementString = INSERT_COLUMN_MAPPINGS_ORACLE;
	        } else {
	        	insertStatementString = INSERT_COLUMN_MAPPINGS_MYSQL;
	        }
	        
			List<Object[]> parameterList = new ArrayList<Object[]>();
            for (ColumnMapping mapping : mappings) {
				parameterList.add(new Object[] {
						mapping.getProfileId(),
		                mapping.getFileColumn(),
		                mapping.getDatabaseColumn(),
		                mapping.getMandatory(),
		                mapping.getDefaultValue() });           
            }
            logSqlStatement(logger, insertStatementString);
            getSimpleJdbcTemplate().batchUpdate(insertStatementString, parameterList);
        }
    }
    
    private void insertGenderMappings(Map<String, Integer> mappings, int importProfileId) {
    	if (mappings != null && !mappings.isEmpty()){
        	String insertStatementString = null;
	        if (AgnUtils.isOracleDB()) {
	        	insertStatementString = INSERT_GENDER_MAPPINGS_ORACLE;
	        } else {
	        	insertStatementString = INSERT_GENDER_MAPPINGS_MYSQL;
	        }
	        
			List<Object[]> parameterList = new ArrayList<Object[]>();
            for (Entry<String, Integer> entry : mappings.entrySet()) {
				parameterList.add(new Object[] { importProfileId, entry.getValue(), entry.getKey() });           
            }
            logSqlStatement(logger, insertStatementString);
            getSimpleJdbcTemplate().batchUpdate(insertStatementString, parameterList);
        }
    }
    
    private class ImportProfileRowMapper implements ParameterizedRowMapper<ImportProfile> {
        @Override
        public ImportProfile mapRow(ResultSet resultSet, int row) throws SQLException {
            ImportProfile profile = new ImportProfileImpl();
            profile.setId(resultSet.getInt(FIELD_ID));
            profile.setName(resultSet.getString(FIELD_SHORTNAME));
            profile.setCompanyId(resultSet.getInt(FIELD_COMPANY_ID));
            profile.setAdminId(resultSet.getInt(FIELD_ADMIN_ID));
            profile.setSeparator(resultSet.getInt(FIELD_COLUMN_SEPARATOR));
            profile.setTextRecognitionChar(resultSet.getInt(FIELD_TEXT_DELIMITER));
            profile.setCharset(resultSet.getInt(FIELD_FILE_CHARSET));
            profile.setDateFormat(resultSet.getInt(FIELD_DATE_FORMAT));
            profile.setImportMode(resultSet.getInt(FIELD_IMPORT_MODE));
            profile.setKeyColumn(resultSet.getString(FIELD_KEY_COLUMN));
            profile.setCheckForDuplicates(resultSet.getInt(FIELD_CHECK_FOR_DUPLICATES));
            profile.setNullValuesAction(resultSet.getInt(FIELD_NULL_VALUES_ACTION));
            profile.setExtendedEmailCheck(resultSet.getBoolean(FIELD_EXT_EMAIL_CHECK));
            profile.setMailForReport(resultSet.getString(FIELD_REPORT_EMAIL));
            profile.setDefaultMailType(resultSet.getInt(FIELD_MAIL_TYPE));
            profile.setUpdateAllDuplicates(resultSet.getBoolean(FIELD_UPDATE_ALL_DUPLICATES));
            
            // Read additional data
            
            // Read ColumnMappings
            logSqlStatement(logger, SELECT_COLUMN_MAPPINGS, profile.getId());
            profile.setColumnMapping(getSimpleJdbcTemplate().query(SELECT_COLUMN_MAPPINGS, new ColumnMappingRowMapper(), profile.getId()));
            
            // Read GenderMappings
            logSqlStatement(logger, SELECT_GENDER_MAPPINGS, profile.getId());
            List<Map<String, Object>> queryResult = getSimpleJdbcTemplate().queryForList(SELECT_GENDER_MAPPINGS, profile.getId());
            Map<String, Integer> genderMappings = new HashMap<String, Integer>();
            for (Map<String, Object> resultSetRow : queryResult) {
            	genderMappings.put((String) resultSetRow.get(GENDER_MAPPING_STRING_GENDER), ((Number)resultSetRow.get(GENDER_MAPPING_INT_GENDER)).intValue());
            }
            profile.setGenderMapping(genderMappings);
            
            return profile;
    	}
    }
    
    private class ColumnMappingRowMapper implements ParameterizedRowMapper<ColumnMapping> {
    	@Override
        public ColumnMapping mapRow(ResultSet resultSet, int row) throws SQLException {
            ColumnMapping mapping = new ColumnMappingImpl();
            mapping.setId(resultSet.getInt(COLUMN_MAPPING_ID));
            mapping.setProfileId(resultSet.getInt(COLUMN_MAPPING_PROFILE_ID));
            mapping.setMandatory(resultSet.getBoolean(COLUMN_MAPPING_MANDATORY));
            mapping.setDatabaseColumn(resultSet.getString(COLUMN_MAPPING_DB_COLUMN));
            mapping.setFileColumn(resultSet.getString(COLUMN_MAPPING_FILE_COLUMN));
            String defaultValue = resultSet.getString(COLUMN_MAPPING_DEFAULT_VALUE);
            if (StringUtils.isNotEmpty(defaultValue)) {
                mapping.setDefaultValue(defaultValue);
            }
            return mapping;
    	}
    }
}
