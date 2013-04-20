package org.agnitas.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.BatchUpdateException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.FloatValidator;
import org.apache.log4j.Logger;
import org.hibernate.dialect.Dialect;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.web.context.WebApplicationContext;

public class DbUtilities {
	private static final transient Logger logger = Logger.getLogger(DbUtilities.class);
	
    public static List<Map<String, Object>> executeQueryForList(String sqlQueryStatementString, WebApplicationContext aContext) {
    	if (logger.isDebugEnabled())
    		logger.debug("SQL Query: " + sqlQueryStatementString);
    	
    	SimpleJdbcTemplate jdbc = new SimpleJdbcTemplate((DataSource)aContext.getBean("dataSource"));
    	
    	long queryStartMillis = System.currentTimeMillis();
    	
		List<Map<String, Object>> queryResults = jdbc.queryForList(sqlQueryStatementString);
        
        if (logger.isInfoEnabled())
        	logger.info("time: " + (System.currentTimeMillis() - queryStartMillis));
        
        return queryResults;
    }
    
    public static int getResultRowValueAsInt(String valueName, Map<String, Object> resultRow) {
    	return ((Number)resultRow.get(valueName)).intValue();
    }
    
    public static Integer getResultRowValueAsInteger(String valueName, Map<String, Object> resultRow) {
    	return new Integer(((Number)resultRow.get(valueName)).intValue());
    }
    
    public static String getResultRowValueAsString(String valueName, Map<String, Object> resultRow) {
    	return (String)resultRow.get(valueName);
    }

    public static Timestamp getResultRowValueAsTimestamp(String valueName, Map<String, Object> resultRow) {
    	return (Timestamp)resultRow.get(valueName);
    }
    
    public static int readoutTableInFile(DataSource dataSource, String tableName, File outputFile, char separator) throws Exception {
    	return readoutInFile(dataSource, "SELECT * FROM " + tableName, outputFile, separator);
    }
    
    public static int readoutInFile(DataSource dataSource, String statementString, File outputFile, char separator) throws Exception {
		if (outputFile.exists()) throw new Exception("Outputfile already exists");
		
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		CsvWriter csvWriter = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(statementString);
			csvWriter = new CsvWriter(new FileOutputStream(outputFile));
			ResultSetMetaData metaData = resultSet.getMetaData();
			// write headers
			List<String> headers = new ArrayList<String>();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				headers.add(metaData.getColumnName(i));
			}
			csvWriter.writeValues(headers);
			
			// write values
			while (resultSet.next()) {
				List<String> values = new ArrayList<String>();
				for (int i = 1; i <= metaData.getColumnCount(); i++) {					
					values.add(resultSet.getString(i));
				}
				csvWriter.writeValues(values);
			}
			
			return csvWriter.getWrittenLines();
		} finally {	
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connection != null) {
				connection.close();
			}
			
			IOUtils.closeQuietly(csvWriter);
		}
	}
    
    public static String readout(Connection databaseConnection, String statementString, char separator) throws Exception {
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = databaseConnection.createStatement();
			resultSet = statement.executeQuery(statementString);
			StringBuilder tableDataString = new StringBuilder();
			ResultSetMetaData metaData = resultSet.getMetaData();
			// write headers
			List<String> headers = new ArrayList<String>();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				headers.add(metaData.getColumnName(i));
			}
			tableDataString.append(CsvWriter.getCsvLine(separator, '"', headers));
			tableDataString.append("\n");
			
			// write values
			while (resultSet.next()) {
				List<String> values = new ArrayList<String>();
				for (int i = 1; i <= metaData.getColumnCount(); i++) {					
					values.add(resultSet.getString(i));
				}
				tableDataString.append(CsvWriter.getCsvLine(separator, '"', values));
				tableDataString.append("\n");
			}
			
			return tableDataString.toString();
		} finally {	
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}
		}
	}
    
    public static String readout(DataSource dataSource, String statementString, char separator) throws Exception {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			
			return readout(connection, statementString, separator);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}
    
    public static String readoutTable(DataSource dataSource, String tableName, char separator) throws Exception {
    	return readout(dataSource, "SELECT * FROM " + tableName, separator);
    }
    
    public static String readoutTable(Connection connection, String tableName, char separator) throws Exception {
    	return readout(connection, "SELECT * FROM " + tableName, separator);
    }
	
	public static Map<Integer, Object[]> importDataInTable(DataSource dataSource, String tableName, String[] tableColumns, List<Object[]> dataSets, boolean commitOnFullSuccessOnly) throws Exception {
		if (StringUtils.isBlank(tableName)) {
			throw new Exception("Missing parameter tableName for dataimport");
		}
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		Statement statement = null;
		ResultSet resultSet = null;
		
		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			statement = connection.createStatement();
		
			checkTableAndColumnsExist(connection, tableName, tableColumns);
			
			// Insert data
			Map<Integer, Object[]> notInsertedData = new HashMap<Integer, Object[]>();
			String insertStatementString = "INSERT INTO " + tableName + " (" + StringUtils.join(tableColumns, ", ") + ") VALUES (" + AgnUtils.repeatString("?", tableColumns.length, ", ") + ")";
			preparedStatement = connection.prepareStatement(insertStatementString);
			boolean hasOpenData = false;
			List<Object[]> currentUncommitedLines = new ArrayList<Object[]>();
			int datasetIndex;
			for (datasetIndex = 0; datasetIndex < dataSets.size(); datasetIndex++) {
				Object[] dataSet = dataSets.get(datasetIndex);
				currentUncommitedLines.add(dataSet);
				hasOpenData = true;
				
				if (dataSet.length != tableColumns.length) {
					if (!commitOnFullSuccessOnly) {
						notInsertedData.put(datasetIndex, dataSet);
					} else {
						connection.rollback();
						throw new Exception("Error on insert of dataset at index " + datasetIndex + ": invalid number of dataitems");
					}
				} else {
					preparedStatement.clearParameters();
					for (int parameterIndex = 0; parameterIndex < dataSet.length; parameterIndex++) {
						preparedStatement.setObject(parameterIndex + 1, dataSet[parameterIndex]);
					}
					preparedStatement.addBatch();
					
					if ((datasetIndex + 1) % 100 == 0) {
						hasOpenData = false;
						try {
							preparedStatement.executeBatch();
							if (!commitOnFullSuccessOnly) {
								connection.commit();
							}
							currentUncommitedLines.clear();
						} catch (BatchUpdateException bue) {
							if (commitOnFullSuccessOnly) {
								connection.rollback();
								throw new Exception("Error on insert of dataset between index " + (datasetIndex - currentUncommitedLines.size()) + " and index " + datasetIndex + ": " + bue.getMessage());
							} else {
								connection.rollback();
								importDataInTable(datasetIndex - currentUncommitedLines.size(), connection, preparedStatement, tableColumns, currentUncommitedLines, notInsertedData);
							}
						} catch (Exception e) {
							connection.rollback();
							throw new Exception("Error on insert of dataset between index " + (datasetIndex - currentUncommitedLines.size()) + " and index " + datasetIndex + ": " + e.getMessage());
						}
					}
				}
			}
			
			if (hasOpenData) {
				hasOpenData = false;
				try {
					preparedStatement.executeBatch();
					if (!commitOnFullSuccessOnly) {
						connection.commit();
					}
					currentUncommitedLines.clear();
				} catch (BatchUpdateException bue) {
					if (commitOnFullSuccessOnly) {
						connection.rollback();
						throw new Exception("Error on insert of dataset between index " + (datasetIndex - currentUncommitedLines.size()) + " and index " + datasetIndex + ": " + bue.getMessage());
					} else {
						connection.rollback();
						importDataInTable(datasetIndex - currentUncommitedLines.size(), connection, preparedStatement, tableColumns, currentUncommitedLines, notInsertedData);
					}
				} catch (Exception e) {
					connection.rollback();
					throw new Exception("Error on insert of dataset between index " + (datasetIndex - currentUncommitedLines.size()) + " and index " + datasetIndex + ": " + e.getMessage());
				}
			}
			
			if (commitOnFullSuccessOnly) {
				connection.commit();
			}
			
			return notInsertedData;
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}
			
			if (preparedStatement != null) {
				preparedStatement.close();
			}

			if (connection != null) {
				connection.rollback();
				connection.close();
			}
		}
	}
	
	private static void importDataInTable(int offsetIndex, Connection connection, PreparedStatement preparedStatement, String[] columnMapping, List<Object[]> data, Map<Integer, Object[]> notInsertedData) throws Exception {
		int dataLineIndex = offsetIndex;
		for (Object[] dataLine : data) {
			dataLineIndex++;
			if (dataLine.length != columnMapping.length) {
				notInsertedData.put(dataLineIndex, dataLine);
			} else {
				int parameterIndex = 1;
				for (int csvValueIndex = 0; csvValueIndex < dataLine.length; csvValueIndex++) {
					if (columnMapping[csvValueIndex] != null) {
						preparedStatement.setObject(parameterIndex++, dataLine[csvValueIndex]);
					}
				}
				
				try {
					preparedStatement.execute();
					connection.commit();
				} catch (Exception e) {
					notInsertedData.put(dataLineIndex, dataLine);
					connection.rollback();
				}
			}
		}
	}
	
	public static Map<Integer, List<String>> importCsvFileInTable(DataSource dataSource, String tableName, String[] columnMapping, File csvFile, String encoding, boolean containsHeadersInFirstRow, boolean commitOnFullSuccessOnly) throws Exception {
		return importCsvFileInTable(dataSource, tableName, columnMapping, new FileInputStream(csvFile), encoding, containsHeadersInFirstRow, false, commitOnFullSuccessOnly);
	}
	
	public static Map<Integer, List<String>> importCsvFileInTable(DataSource dataSource, String tableName, String[] columnMapping, File csvFile, String encoding, boolean containsHeadersInFirstRow, boolean fillMissingTrailingColumnsWithNull, boolean commitOnFullSuccessOnly) throws Exception {
		return importCsvFileInTable(dataSource, tableName, columnMapping, new FileInputStream(csvFile), encoding, containsHeadersInFirstRow, fillMissingTrailingColumnsWithNull, commitOnFullSuccessOnly);
	}
	
	public static Map<Integer, List<String>> importCsvFileInTable(DataSource dataSource, String tableName, String[] columnMapping, FileInputStream csvFileInputStream, String encoding, boolean containsHeadersInFirstRow, boolean commitOnFullSuccessOnly) throws Exception {
		return importCsvFileInTable(dataSource, tableName, columnMapping, csvFileInputStream, encoding, containsHeadersInFirstRow, false, commitOnFullSuccessOnly);
	}
	
	public static Map<Integer, List<String>> importCsvFileInTable(DataSource dataSource, String tableName, String[] columnMapping, FileInputStream csvFileInputStream, String encoding, boolean containsHeadersInFirstRow, boolean fillMissingTrailingColumnsWithNull, boolean commitOnFullSuccessOnly) throws Exception {
		if (StringUtils.isBlank(tableName)) {
			throw new Exception("Missing parameter tableName for dataimport");
		}
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		Statement statement = null;
		ResultSet resultSet = null;
		
		CsvReader csvReader = null;
		
		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			
			checkTableAndColumnsExist(connection, tableName, columnMapping);
			
			csvReader = new CsvReader(csvFileInputStream, encoding, ';');
			csvReader.setFillMissingTrailingColumnsWithNull(fillMissingTrailingColumnsWithNull);

			// First line may contain headers
			List<String> csvLine;
			if (containsHeadersInFirstRow) {
				csvLine = csvReader.readNextCsvLine();
				if (columnMapping == null) {
					columnMapping = csvLine.toArray(new String[0]);
				}
			}
		
			List<String> dbColumns = new ArrayList<String>();
			for (String column : columnMapping) {
				if (column != null) {
					dbColumns.add(column);
				}
			}

			Map<Integer, List<String>> notInsertedData = new HashMap<Integer, List<String>>();
			String insertStatementString = "INSERT INTO " + tableName + " (" + StringUtils.join(dbColumns, ", ") + ") VALUES (" + AgnUtils.repeatString("?", dbColumns.size(), ", ") + ")";
			preparedStatement = connection.prepareStatement(insertStatementString);

			// Read and insert data
			int csvLineIndex = 1; // index obeys headerline => real lineindex in csv-file
			boolean hasOpenData = false;
			List<List<String>> currentUncommitedLines = new ArrayList<List<String>>();
			while ((csvLine = csvReader.readNextCsvLine()) != null) {
				csvLineIndex++;
				currentUncommitedLines.add(csvLine);
				hasOpenData = true;
				
				if (csvLine.size() != columnMapping.length) {
					if (!commitOnFullSuccessOnly) {
						notInsertedData.put(csvLineIndex, csvLine);
					} else {
						connection.rollback();
						throw new Exception("Error on insert of dataset at line " + csvLineIndex + ": invalid number of dataitems");
					}
				} else {
					int parameterIndex = 1;
					for (int csvValueIndex = 0; csvValueIndex < csvLine.size(); csvValueIndex++) {
						if (columnMapping[csvValueIndex] != null) {
							preparedStatement.setString(parameterIndex++, csvLine.get(csvValueIndex));
						}
					}
					preparedStatement.addBatch();
					
					if (csvLineIndex % 100 == 0) {
						hasOpenData = false;
						try {
							preparedStatement.executeBatch();
							if (!commitOnFullSuccessOnly) {
								connection.commit();
							}
							currentUncommitedLines.clear();
						} catch (BatchUpdateException bue) {
							if (commitOnFullSuccessOnly) {
								connection.rollback();
								throw new Exception("Error on insert of dataset between line " + (csvLineIndex - currentUncommitedLines.size()) + " and line " + csvLineIndex + ": " + bue.getMessage());
							} else {
								connection.rollback();
								importCsvDataInTable(csvLineIndex - currentUncommitedLines.size(), connection, preparedStatement, columnMapping, currentUncommitedLines, notInsertedData);
							}
						} catch (Exception e) {
							if (!commitOnFullSuccessOnly) {
								notInsertedData.put(csvLineIndex, csvLine);
								connection.rollback();
							} else {
								connection.rollback();
								throw new Exception("Error on insert of dataset at line " + csvLineIndex + ": " + e.getMessage());
							}
						}
					}
				}
			}
			
			if (hasOpenData) {
				hasOpenData = false;
				try {
					preparedStatement.executeBatch();
					if (!commitOnFullSuccessOnly) {
						connection.commit();
					}
					currentUncommitedLines.clear();
				} catch (BatchUpdateException bue) {
					if (commitOnFullSuccessOnly) {
						connection.rollback();
						throw new Exception("Error on insert of dataset between line " + (csvLineIndex - currentUncommitedLines.size()) + " and line " + csvLineIndex + ": " + bue.getMessage());
					} else {
						connection.rollback();
						importCsvDataInTable(csvLineIndex - currentUncommitedLines.size(), connection, preparedStatement, columnMapping, currentUncommitedLines, notInsertedData);
					}
				} catch (Exception e) {
					connection.rollback();
					throw new Exception("Error on insert of dataset between line " + (csvLineIndex - currentUncommitedLines.size()) + " and line " + csvLineIndex + ": " + e.getMessage());
				}
			}
			
			if (commitOnFullSuccessOnly) {
				connection.commit();
			}
			
			return notInsertedData;
		} finally {
			if (csvReader != null) {
				csvReader.close();
			}
			
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}
			
			if (preparedStatement != null) {
				preparedStatement.close();
			}

			if (connection != null) {
				connection.rollback();
				connection.close();
			}
		}
	}
	
	private static void importCsvDataInTable(int offsetIndex, Connection connection, PreparedStatement preparedStatement, String[] columnMapping, List<List<String>> data, Map<Integer, List<String>> notInsertedData) throws Exception {
		int csvLineIndex = offsetIndex;
		for (List<String> csvLine : data) {
			csvLineIndex++;
			if (csvLine.size() != columnMapping.length) {
				notInsertedData.put(csvLineIndex, csvLine);
			} else {
				int parameterIndex = 1;
				for (int csvValueIndex = 0; csvValueIndex < csvLine.size(); csvValueIndex++) {
					if (columnMapping[csvValueIndex] != null) {
						preparedStatement.setString(parameterIndex++, csvLine.get(csvValueIndex));
					}
				}
				
				try {
					preparedStatement.execute();
					connection.commit();
				} catch (Exception e) {
					notInsertedData.put(csvLineIndex, csvLine);
					connection.rollback();
				}
			}
		}
	}
	
	public static boolean checkDbVendorIsOracle(DataSource dataSource) throws SQLException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			return checkDbVendorIsOracle(connection);
		} finally {
			closeQuietly(connection);
		}
	}
	
	public static boolean checkDbVendorIsOracle(Connection connection) {
		try {
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			if (databaseMetaData != null) {
				String productName = databaseMetaData.getDatabaseProductName();
				if ("oracle".equalsIgnoreCase(productName)) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (SQLException e) {
			return false;
		}
	}
	
	public static String getDbUrl(DataSource dataSource) throws SQLException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			return getDbUrl(connection);
		} finally {
			closeQuietly(connection);
		}
	}
	
	public static String getDbUrl(Connection connection) {
		try {
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			if (databaseMetaData != null) {
				return databaseMetaData.getURL();
			} else {
				return null;
			}
		} catch (SQLException e) {
			return null;
		}
	}

	public static void checkTableAndColumnsExist(DataSource dataSource, String tableName, String[] columns) throws Exception {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			checkTableAndColumnsExist(connection, tableName, columns);
		} finally {
			closeQuietly(connection);
		}
	}
	
	public static void checkTableAndColumnsExist(Connection connection, String tableName, String[] columns) throws Exception {
		Statement statement = null;
		ResultSet resultSet = null;
		
		try {
			statement = connection.createStatement();
			
			// Check if table exists
			try {
				resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE 1 = 0");
			} catch (Exception e) {
				throw new Exception("Table '" + tableName + "' does not exist");
			}
			
			// Check if all needed columns exist
			Set<String> dbTableColumns = new HashSet<String>();
			ResultSetMetaData metaData = resultSet.getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				dbTableColumns.add(metaData.getColumnName(i).toUpperCase());
			}
			for (String column : columns) {
				if (column != null && !dbTableColumns.contains(column.toUpperCase())) {
					throw new Exception("Column '" + column + "' does not exist in table '" + tableName + "'");
				}
			}
		} finally {
			closeQuietly(resultSet);
			resultSet = null;
			closeQuietly(statement);
			statement = null;
		}
	}
	
	public static void checkTableExists(DataSource dataSource, String tableName) throws Exception {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			checkTableExists(connection, tableName);
		} finally {
			closeQuietly(connection);
		}
	}
	
	public static void checkTableExists(Connection connection, String tableName) throws Exception {
		Statement statement = null;
		ResultSet resultSet = null;
		
		try {
			statement = connection.createStatement();
			
			// Check if table exists
			try {
				resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE 1 = 0");
			} catch (Exception e) {
				throw new Exception("Table '" + tableName + "' does not exist");
			}
		} finally {
			closeQuietly(resultSet);
			resultSet = null;
			closeQuietly(statement);
			statement = null;
		}
	}
	
	public static String callStoredProcedureWithDbmsOutput(Connection connection, String procedureName, Object... parameters) throws SQLException {
		CallableStatement callableStatement = null;
		try {
			callableStatement = connection.prepareCall("begin dbms_output.enable(:1); end;");
			callableStatement.setLong(1, 10000);
			callableStatement.executeUpdate();
			callableStatement.close();
			callableStatement = null;
			
			if (parameters != null) {
				callableStatement = connection.prepareCall("{call " + procedureName + "(" + AgnUtils.repeatString("?", parameters.length, ", ") + ")}");
				for (int i = 0; i < parameters.length; i++) {
					callableStatement.setObject(i + 1, parameters[i]);
				}
			} else {
				callableStatement = connection.prepareCall("{call " + procedureName + "()}");
			}
			callableStatement.execute();
			callableStatement.close();
			callableStatement = null;

			callableStatement = connection
				.prepareCall(
					"declare "
					+ "    l_line varchar2(255); "
					+ "    l_done number; "
					+ "    l_buffer long; "
					+ "begin "
					+ "  loop "
					+ "    exit when length(l_buffer)+255 > :maxbytes OR l_done = 1; "
					+ "    dbms_output.get_line( l_line, l_done ); "
					+ "    l_buffer := l_buffer || l_line || chr(10); "
					+ "  end loop; " + " :done := l_done; "
					+ " :buffer := l_buffer; "
					+ "end;");

			callableStatement.registerOutParameter(2, Types.INTEGER);
			callableStatement.registerOutParameter(3, Types.VARCHAR);
			StringBuffer dbmsOutput = new StringBuffer(1024);
			while (true) {
				callableStatement.setInt(1, 32000);
				callableStatement.executeUpdate();
				dbmsOutput.append(callableStatement.getString(3).trim());
				if (callableStatement.getInt(2) == 1) {
					break;
				}
			}
			callableStatement.close();
			callableStatement = null;
			
			callableStatement = connection.prepareCall("begin dbms_output.disable; end;");
			callableStatement.executeUpdate();
			callableStatement.close();
			callableStatement = null;
			
			return dbmsOutput.toString();
		} finally {
			closeQuietly(callableStatement);
		}
	}
	
	public static void closeQuietly(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
			}
		}
	}
	
	public static void closeQuietly(Statement statement) {
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
			}
		}
	}
	
	public static void closeQuietly(ResultSet resultSet) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
			}
		}
	}
	
	public static String getResultAsTextTable(DataSource datasource, String selectString) throws Exception {
		List<Map<String, Object>> results = new SimpleJdbcTemplate(datasource).queryForList(selectString);
		if (results != null && results.size() > 0) {
			TextTable textTable = new TextTable();
			for (String column : results.get(0).keySet()) {
				textTable.addColumn(column);
			}
			
			if (results != null && results.size() > 0) {
				for (Map<String, Object> row : results) {
					textTable.startNewLine();
					for (String column : row.keySet()) {
						if (row.get(column) != null) {
							textTable.addValueToCurrentLine(row.get(column).toString());
						} else {
							textTable.addValueToCurrentLine("<null>");
						}
					}
				}
			}
			
			return textTable.toString();
		} else {
			return null;
		}
	}
	
	public static List<String> getColumnNames(DataSource dataSource, String tableName) throws Exception {
		if (dataSource == null) {
			throw new Exception("Invalid empty dataSource for getColumnNames");
		} else if (StringUtils.isBlank(tableName)) {
			throw new Exception("Invalid empty tableName for getColumnNames");
		} else {
			Connection connection = null;
			Statement stmt = null;
			ResultSet rset = null;
			try {
				connection = dataSource.getConnection();
				stmt = connection.createStatement();
		    	String sql = "SELECT * FROM " + SafeString.getSQLSafeString(tableName) + " WHERE 1 = 0";
		        rset = stmt.executeQuery(sql);
		        List<String> columnNamesList = new ArrayList<String>();
		        for (int i = 1; i <= rset.getMetaData().getColumnCount(); i++) {
		        	columnNamesList.add(rset.getMetaData().getColumnName(i));
		        }
		        return columnNamesList;
			} finally {
		        DbUtilities.closeQuietly(rset);
		        DbUtilities.closeQuietly(stmt);
		        DbUtilities.closeQuietly(connection);
			}
		}
	}
	
	public static DbColumnType getColumnDataType(DataSource dataSource, String tableName, String columnName) throws Exception {
		if (dataSource == null) {
			throw new Exception("Invalid empty dataSource for getColumnDataType");
		} else if (StringUtils.isBlank(tableName)) {
			throw new Exception("Invalid empty tableName for getColumnDataType");
		} else if (StringUtils.isBlank(columnName)) {
			throw new Exception("Invalid empty columnName for getColumnDataType");
		} else {
			Connection connection = null;
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			try {
				connection = dataSource.getConnection();
				int characterLength;
				int numericPrecision;
				int numericScale;
				boolean isNullable;
				if (checkDbVendorIsOracle(dataSource)) {
					// Watchout: Oracle's timestamp datatype is "TIMESTAMP(6)", so remove the bracket value
					String sql = "SELECT NVL(substr(data_type, 1, instr(data_type, '(') - 1), data_type) as data_type, data_length, data_precision, data_scale, nullable FROM user_tab_columns WHERE lower(table_name) = lower(?) AND lower(column_name) = lower(?)";
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, tableName);
					preparedStatement.setString(2, columnName);
					resultSet = preparedStatement.executeQuery();
					
					if (resultSet.next()) {
						characterLength = resultSet.getInt("data_length");
						if (resultSet.wasNull()) {
							characterLength = -1;
						}
						numericPrecision = resultSet.getInt("data_precision");
						if (resultSet.wasNull()) {
							numericPrecision = -1;
						}
						numericScale = resultSet.getInt("data_scale");
						if (resultSet.wasNull()) {
							numericScale = -1;
						}
						isNullable = resultSet.getString("nullable").equalsIgnoreCase("y");
						
						return new DbColumnType(resultSet.getString("data_type"), characterLength, numericPrecision, numericScale, isNullable);
					} else {
						return null;
					}
	        	} else {
	        		String sql = "SELECT data_type, character_maximum_length, numeric_precision, numeric_scale, is_nullable FROM information_schema.columns WHERE table_schema = schema() AND lower(table_name) = lower(?) AND lower(column_name) = lower(?)";
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, tableName);
					preparedStatement.setString(2, columnName);
					resultSet = preparedStatement.executeQuery();

					if (resultSet.next()) {
						characterLength = resultSet.getInt("character_maximum_length");
						if (resultSet.wasNull()) {
							characterLength = -1;
						}
						numericPrecision = resultSet.getInt("numeric_precision");
						if (resultSet.wasNull()) {
							numericPrecision = -1;
						}
						numericScale = resultSet.getInt("numeric_scale");
						if (resultSet.wasNull()) {
							numericScale = -1;
						}
						isNullable = resultSet.getString("is_nullable").equalsIgnoreCase("yes");
						
						return new DbColumnType(resultSet.getString("data_type"), characterLength, numericPrecision, numericScale, isNullable);
					} else {
						return null;
					}
	        	}
			} catch (Exception e) {
				return null;
			} finally {
		        DbUtilities.closeQuietly(resultSet);
		        DbUtilities.closeQuietly(preparedStatement);
		        DbUtilities.closeQuietly(connection);
			}
		}
	}

	public static CaseInsensitiveMap<DbColumnType> getColumnDataTypes(DataSource dataSource, String tableName) throws Exception {
		if (dataSource == null) {
			throw new Exception("Invalid empty dataSource for getColumnDataTypes");
		} else if (StringUtils.isBlank(tableName)) {
			throw new Exception("Invalid empty tableName for getColumnDataTypes");
		} else {
			Connection connection = null;
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			try {
				CaseInsensitiveMap<DbColumnType> returnMap = new CaseInsensitiveMap<DbColumnType>();
				connection = dataSource.getConnection();
				if (checkDbVendorIsOracle(dataSource)) {
					// Watchout: Oracle's timestamp datatype is "TIMESTAMP(6)", so remove the bracket value
					String sql = "SELECT column_name, NVL(substr(data_type, 1, instr(data_type, '(') - 1), data_type) as data_type, data_length, data_precision, data_scale, nullable FROM user_tab_columns WHERE lower(table_name) = lower(?)";
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, tableName);
					resultSet = preparedStatement.executeQuery();
					while (resultSet.next()) {
						int characterLength = resultSet.getInt("data_length");
						if (resultSet.wasNull()) {
							characterLength = -1;
						}
						int numericPrecision = resultSet.getInt("data_precision");
						if (resultSet.wasNull()) {
							numericPrecision = -1;
						}
						int numericScale = resultSet.getInt("data_scale");
						if (resultSet.wasNull()) {
							numericScale = -1;
						}
						boolean isNullable = resultSet.getString("nullable").equalsIgnoreCase("y");
						
						returnMap.put(resultSet.getString("column_name"), new DbColumnType(resultSet.getString("data_type"), characterLength, numericPrecision, numericScale, isNullable));
					}
	        	} else {
	        		String sql = "SELECT column_name, data_type, character_maximum_length, numeric_precision, numeric_scale, is_nullable FROM information_schema.columns WHERE table_schema = schema() AND lower(table_name) = lower(?)";
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, tableName);
					resultSet = preparedStatement.executeQuery();
					while (resultSet.next()) {
						int characterLength = resultSet.getInt("character_maximum_length");
						if (resultSet.wasNull()) {
							characterLength = -1;
						}
						int numericPrecision = resultSet.getInt("numeric_precision");
						if (resultSet.wasNull()) {
							numericPrecision = -1;
						}
						int numericScale = resultSet.getInt("numeric_scale");
						if (resultSet.wasNull()) {
							numericScale = -1;
						}
						boolean isNullable = resultSet.getString("is_nullable").equalsIgnoreCase("yes");

						returnMap.put(resultSet.getString("column_name"), new DbColumnType(resultSet.getString("data_type"), characterLength, numericPrecision, numericScale, isNullable));
					}
	        	}
		        return returnMap;
			} catch (Exception e) {
				throw e;
			} finally {
		        DbUtilities.closeQuietly(resultSet);
		        DbUtilities.closeQuietly(preparedStatement);
		        DbUtilities.closeQuietly(connection);
			}
		}
	}
	
	public static int getColumnCount(DataSource dataSource, String tableName) throws Exception {
		if (dataSource == null) {
			throw new Exception("Invalid empty dataSource for getColumnCount");
		} else if (StringUtils.isBlank(tableName)) {
			throw new Exception("Invalid empty tableName for getColumnCount");
		} else {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
		        return getColumnCount(connection, tableName);
			} finally {
		        DbUtilities.closeQuietly(connection);
			}
		}
	}
	
	public static int getColumnCount(Connection connection, String tableName) throws Exception {
		if (StringUtils.isBlank(tableName)) {
			throw new Exception("Invalid empty tableName for getColumnCount");
		} else {
			Statement stmt = null;
			ResultSet rset = null;
			try {
				stmt = connection.createStatement();
		    	String sql = "SELECT * FROM " + SafeString.getSQLSafeString(tableName) + " WHERE 1 = 0";
		        rset = stmt.executeQuery(sql);
		        return rset.getMetaData().getColumnCount();
			} finally {
		        DbUtilities.closeQuietly(rset);
		        DbUtilities.closeQuietly(stmt);
			}
		}
	}
	
	public static int getTableEntriesCount(Connection connection, String tableName) throws Exception {
		if (StringUtils.isBlank(tableName)) {
			throw new Exception("Invalid empty tableName for getTableEntriesNumber");
		} else {
			Statement stmt = null;
			ResultSet rset = null;
			try {
				stmt = connection.createStatement();
		    	String sql = "SELECT COUNT(*) FROM " + SafeString.getSQLSafeString(tableName);
		        rset = stmt.executeQuery(sql);
		        if (rset.next()) {
		        	return rset.getInt(1);
		        } else {
		        	return 0;
		        }
			} finally {
		        DbUtilities.closeQuietly(rset);
		        DbUtilities.closeQuietly(stmt);
			}
		}
	}
	
	public static boolean containsColumnName(DataSource dataSource, String tableName, String columnName) throws Exception {
		if (dataSource == null) {
			throw new Exception("Invalid empty dataSource for containsColumnName");
		} else if (StringUtils.isBlank(tableName)) {
			throw new Exception("Invalid empty tableName for containsColumnName");
		} else if (StringUtils.isBlank(columnName)) {
			throw new Exception("Invalid empty columnName for containsColumnName");
		} else {
			Connection connection = null;
	    	Statement stmt = null;
	    	ResultSet rset = null;
	    	try {
				connection = dataSource.getConnection();
	    		stmt = connection.createStatement();
	        	String sql = "SELECT * FROM " + SafeString.getSQLSafeString(tableName) + " WHERE 1 = 0";
	            rset = stmt.executeQuery(sql);
	            for (int columnIndex = 1; columnIndex <= rset.getMetaData().getColumnCount(); columnIndex++) {
	            	if (rset.getMetaData().getColumnName(columnIndex).equalsIgnoreCase(columnName.trim())) {
	            		return true;
	            	}
	            }
	    		return false;
	    	} finally {
	            DbUtilities.closeQuietly(rset);
	            DbUtilities.closeQuietly(stmt);
	            DbUtilities.closeQuietly(connection);
	    	}
		}
	}
	
	public static String getColumnDefaultValue(DataSource dataSource, String tableName, String columnName) throws Exception {
		try {
			if (dataSource == null) {
				throw new Exception("Invalid empty dataSource for getDefaultValueOf");
			} else if (StringUtils.isBlank(tableName)) {
				throw new Exception("Invalid empty tableName for getDefaultValueOf");
			} else if (StringUtils.isBlank(columnName)) {
				throw new Exception("Invalid empty columnName for getDefaultValueOf");
			} else {
				if (checkDbVendorIsOracle(dataSource)) {
					String sql = "SELECT data_default FROM user_tab_columns WHERE table_name = ? AND column_name = ?";
					return new SimpleJdbcTemplate(dataSource).queryForObject(sql, String.class, tableName.toUpperCase(), columnName.toUpperCase());
				} else {
					String sql = "SELECT column_default FROM information_schema.columns WHERE table_schema = schema() AND table_name = ? AND column_name = ?";
			    	return new SimpleJdbcTemplate(dataSource).queryForObject(sql, String.class, tableName, columnName);
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public static String getDateDefaultValue(String fieldDefault) {
		if (fieldDefault.toLowerCase().equals("current_timestamp")
				|| fieldDefault.toLowerCase().startsWith("sysdate")) {
			return AgnUtils.getSQLCurrentTimestampName();
		} else {
			if (AgnUtils.isOracleDB()) {
				// TODO: A fixed date format is not a good solution, should
				// depend on language setting of the user
				/*
				 * Here raise a problem: The default value is not only used for
				 * the ALTER TABLE statement. It is also stored in
				 * customer_field_tbl.default_value as a string. A problem
				 * occurs, when two users with language settings with different
				 * date formats edit the profile field.
				 */
				return "to_date('" + fieldDefault + "', 'DD.MM.YYYY')";
			} else {
				return "'" + fieldDefault + "'";
			}
		}
	}

	public static boolean addColumnToDbTable(DataSource dataSource, String tablename, String fieldname, String fieldType, int length, String fieldDefault, boolean notNull) throws Exception {
		if (StringUtils.isBlank(fieldname)) {
			return false;
		} else if (!tablename.equalsIgnoreCase(SafeString.getSQLSafeString(tablename))) {
			logger.error("Cannot create db column: Invalid tablename " + tablename);
			return false;
		} else if (StringUtils.isBlank(fieldname)) {
			return false;
		}  else if (!fieldname.equalsIgnoreCase(SafeString.getSQLSafeString(fieldname))) {
			logger.error("Cannot create db column: Invalid fieldname " + fieldname);
			return false;
		} else if (StringUtils.isBlank(fieldType)) {
			return false;
		} else if (DbUtilities.containsColumnName(dataSource, tablename, fieldname)) {
			return false;
		} else {
			if (fieldType != null) {
				fieldType = fieldType.toUpperCase().trim();
				if (fieldType.startsWith("VARCHAR")) {
					fieldType = "VARCHAR";
				}
			}

			// ColumnType
			int jsqlType = java.sql.Types.class.getDeclaredField(fieldType).getInt(null);
			Class<?> cl = Class.forName(AgnUtils.getDefaultValue("jdbc.dialect"));
			Dialect dia = (Dialect) cl.getConstructor(new Class[0]).newInstance(new Object[0]);

			String dbType = dia.getTypeName(jsqlType);

			// Bugfix for Oracle: Oracle dialect returns long for varchar
			if (fieldType.equalsIgnoreCase("VARCHAR")) {
				dbType = "VARCHAR";
			}

			/*
			 * Bugfix for mysql: The jdbc-Driver for mysql maps VARCHAR to
			 * longtext. This might be ok in most cases, but longtext doesn't
			 * support length restrictions. So the correct tpye for mysql should
			 * be varchar.
			 */
			if (fieldType.equalsIgnoreCase("VARCHAR") && dbType.equalsIgnoreCase("longtext") && length > 0) {
				dbType = "VARCHAR";
			}

			String addColumnStatement = "ALTER TABLE " + tablename + " ADD (" + fieldname.toLowerCase() + " " + dbType;
			if (fieldType.equalsIgnoreCase("VARCHAR")) {
				if (length <= 0) {
					length = 100;
				}
				addColumnStatement += "(" + length + ")";
			}

			// Default Value
			if (StringUtils.isNotEmpty(fieldDefault)) {
				if (fieldType.equalsIgnoreCase("VARCHAR")) {
					addColumnStatement += " DEFAULT '" + fieldDefault + "'";
				} else if (fieldType.equalsIgnoreCase("DATE")) {
					addColumnStatement += " DEFAULT " + getDateDefaultValue(fieldDefault);
				} else {
					addColumnStatement += " DEFAULT " + fieldDefault;
				}
			}

			// Maybe null
			if (notNull) {
				addColumnStatement += " NOT NULL";
			}

			addColumnStatement += ")";

			try {
				new SimpleJdbcTemplate(dataSource).update(addColumnStatement);
				return true;
			} catch (Exception e) {
				logger.error("Cannot create db column: " + addColumnStatement, e);
				return false;
			}
		}
	}
	
	public static boolean alterColumnDefaultValueInDbTable(DataSource dataSource, String tablename, String fieldname, String fieldDefault, boolean notNull) throws Exception {
		return alterColumnTypeInDbTable(dataSource, tablename, fieldname, null, -1, -1, fieldDefault, notNull);
	}

	public static boolean alterColumnTypeInDbTable(DataSource dataSource, String tablename, String fieldname, String fieldType, int length, int precision, String fieldDefault, boolean notNull) throws Exception {
		if (StringUtils.isBlank(fieldname)) {
			return false;
		} else if (!tablename.equalsIgnoreCase(SafeString.getSQLSafeString(tablename))) {
			logger.error("Cannot create db column: Invalid tablename " + tablename);
			return false;
		} else if (StringUtils.isBlank(fieldname)) {
			return false;
		}  else if (!fieldname.equalsIgnoreCase(SafeString.getSQLSafeString(fieldname))) {
			logger.error("Cannot create db column: Invalid fieldname " + fieldname);
			return false;
		} else if (!DbUtilities.containsColumnName(dataSource, tablename, fieldname)) {
			return false;
		} else {
			boolean dbChangeIsNeeded = false;
			boolean isDefaultChangeOnly = true;
			
			// ColumnType
			DbColumnType dbType;
			if (StringUtils.isBlank(fieldType)) {
				dbType = DbUtilities.getColumnDataType(dataSource, tablename, fieldname);
			} else {
				String tempFieldType = fieldType.toUpperCase().trim();
				
				if (tempFieldType.startsWith("VARCHAR")) {
					// Bugfix for Oracle: Oracle dialect returns long for varchar
					// Bugfix for MySQL: The jdbc-Driver for mysql maps VARCHAR to longtext. This might be ok in most cases, but longtext doesn't support length restrictions. So the correct tpye for mysql should be varchar
					dbType = new DbColumnType("VARCHAR", Types.VARCHAR, length, 0, !notNull);
				} else {
					int jsqlType = java.sql.Types.class.getDeclaredField(tempFieldType).getInt(null);
					Class<?> cl = Class.forName(AgnUtils.getDefaultValue("jdbc.dialect"));
					Dialect dia = (Dialect) cl.getConstructor(new Class[0]).newInstance(new Object[0]);

					dbType = new DbColumnType(dia.getTypeName(jsqlType), jsqlType, length, precision, !notNull);
				}
			}

			String changeColumnStatementPart = fieldname.toLowerCase();

			// Datatype, length (only change when fieldType is set)
			if (StringUtils.isNotEmpty(fieldType)) {
				dbChangeIsNeeded = true;
				isDefaultChangeOnly = false;
				if (dbType.getTypeName().toUpperCase().startsWith("VARCHAR")) {
					// varchar datatype
					changeColumnStatementPart += " " + dbType.getTypeName() + "(" + dbType.getCharacterLength() + ")";
				} else if (dbType.getTypeName().toUpperCase().contains("DATE") || dbType.getTypeName().toUpperCase().contains("TIME")) {
					// date or time type
					changeColumnStatementPart += " " + dbType.getTypeName();
				} else {
					// Numeric datatype
					if (dbType.getNumericScale() > -1) {
						changeColumnStatementPart += " " + dbType.getTypeName() + "(" + dbType.getNumericPrecision() + ", " + dbType.getNumericScale() + ")";
					} else {
						changeColumnStatementPart += " " + dbType.getTypeName() + "(" + dbType.getNumericPrecision() + ")";
					}
				}
			}
			
			// Default value
			String currentDefaultValue = getColumnDefaultValue(dataSource, tablename, fieldname);
			if ((currentDefaultValue == null && fieldDefault != null) || currentDefaultValue != null && !currentDefaultValue.equals(fieldDefault)) {
				dbChangeIsNeeded = true;
				if (fieldDefault == null || "".equals(fieldDefault)) {
					// null value as default
					changeColumnStatementPart += " DEFAULT NULL";
				} else if (dbType.getTypeName().toUpperCase().startsWith("VARCHAR")) {
					// varchar datatype
					changeColumnStatementPart += " DEFAULT '" + SafeString.getSQLSafeString(fieldDefault) + "'";
				} else if (dbType.getTypeName().toUpperCase().contains("DATE") || dbType.getTypeName().toUpperCase().contains("TIME")) {
					// date or time type
					changeColumnStatementPart += " DEFAULT " + getDateDefaultValue(SafeString.getSQLSafeString(fieldDefault));
				} else {
					// Numeric datatype
					changeColumnStatementPart += " DEFAULT " + SafeString.getSQLSafeString(fieldDefault);
				}
			}

			// Maybe null
			if (dbType.isNullable() == notNull) {
				dbChangeIsNeeded = true;
				isDefaultChangeOnly = false;
				changeColumnStatementPart += " NOT NULL";
			}

			if (dbChangeIsNeeded) {
				String changeColumnStatement;
				if (DbUtilities.checkDbVendorIsOracle(dataSource)) {
					changeColumnStatement = "ALTER TABLE " + tablename + " MODIFY (" + changeColumnStatementPart + ")";
				} else {
					if (isDefaultChangeOnly) {
						changeColumnStatement = "ALTER TABLE " + tablename + " ALTER " + changeColumnStatementPart.replaceFirst("DEFAULT", "SET DEFAULT");
					} else {
						changeColumnStatement = "ALTER TABLE " + tablename + " MODIFY " + changeColumnStatementPart;
					}
				}
	
				try {
					new SimpleJdbcTemplate(dataSource).update(changeColumnStatement);
					return true;
				} catch (Exception e) {
					logger.error("Cannot change db column: " + changeColumnStatement, e);
					return false;
				}
			} else {
				// No change is needed, but everything is OK
				return true;
			}
		}
	}
	
	public static boolean checkAllowedDefaultValue(String dataType, String defaultValue) {
		if (defaultValue == null) {
			return false;
		} else if ("".equals(defaultValue)) {
			// default value null
			return true;
		} else if (dataType.toUpperCase().contains("DATE") || dataType.toUpperCase().contains("TIME")) {
			if (defaultValue.equalsIgnoreCase("SYSDATE") || defaultValue.equalsIgnoreCase("SYSDATE()") || defaultValue.equalsIgnoreCase("CURRENT_TIMESTAMP")) {
				return true;
			} else {
				try {
					DateUtilities.DD_MM_YYYY.parse(defaultValue);
					return true;
				} catch (ParseException e) {
					return false;
				}
			}
		} else if (dataType.equalsIgnoreCase("NUMBER") || dataType.equalsIgnoreCase("INTEGER") || dataType.equalsIgnoreCase("FLOAT") || dataType.equalsIgnoreCase("DOUBLE")) {
			return new FloatValidator().isValid(defaultValue);
		} else {
			return true;
		}
	}
}
