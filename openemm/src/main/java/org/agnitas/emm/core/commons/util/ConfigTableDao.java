package org.agnitas.emm.core.commons.util;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.agnitas.dao.impl.BaseDaoImpl;
import org.agnitas.util.DbUtilities;
import org.apache.log4j.Logger;

/**
 * This class is intended to simplify access to the config_tbl.
 * 
 * @author aso
 */
public class ConfigTableDao extends BaseDaoImpl {
	@SuppressWarnings("unused")
	private static final transient Logger logger = Logger.getLogger(ConfigTableDao.class);
	
	private static final String SELECT_ALL_SIMPLIFIED_ORACLE = "SELECT TRIM(LEADING '.' FROM class || '.' || name) AS key_for_value, value AS value FROM config_tbl";
	private static final String SELECT_ALL_SIMPLIFIED_MYSQL = "SELECT TRIM(LEADING '.' FROM CONCAT(class, '.', name)) AS key_for_value, value AS value FROM config_tbl";
	
	public Map<String, String> getAllEntries() throws SQLException {
		boolean isOracleDb = DbUtilities.checkDbVendorIsOracle(getDataSource());
		List<Map<String, Object>> results = getSimpleJdbcTemplate().queryForList(isOracleDb ? SELECT_ALL_SIMPLIFIED_ORACLE : SELECT_ALL_SIMPLIFIED_MYSQL);
		Map<String, String> returnMap = new HashMap<String, String>();
		for (Map<String, Object> resultRow : results) {
			returnMap.put((String) resultRow.get("key_for_value"), (String) resultRow.get("value"));
		}
		
		if (isOracleDb) {
			returnMap.put(ConfigService.Value.DB_Vendor.toString(), "Oracle");
		} else {
			returnMap.put(ConfigService.Value.DB_Vendor.toString(), "MySQL");
		}
		
		return returnMap;
	}
}
