package org.agnitas.dao.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.agnitas.util.AgnUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;

public abstract class BaseDaoImpl {
	// ----------------------------------------------------------------------------------------------------------------
	// Dependency Injection

	protected DataSource dataSource;
	private SimpleJdbcTemplate simpleJdbcTemplate = null;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}

	protected SimpleJdbcTemplate getSimpleJdbcTemplate() {
		if (simpleJdbcTemplate == null)
			simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
		
		return simpleJdbcTemplate;
	}

	protected void logSqlStatement(Logger logger, String statement, Object... parameter) {
        if (logger.isDebugEnabled()) {
        	if (parameter != null && parameter.length > 0) {
        		logger.debug("SQL:" + statement + " Parameter: " + StringUtils.join(parameter, ", "));
        	} else {
        		logger.debug("SQL:" + statement);
        	}
        }
	}
	
	protected void logSqlError(Exception e, Logger logger, String statement, Object... parameter) {
        AgnUtils.sendExceptionMail("SQL: " + statement + " Parameter: " + StringUtils.join(parameter, ", "), e);
        logger.error("Error:" + e, e);
	}
	
	protected void closeSilently(Statement... statements) {
		for (Statement statement : statements) {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Logs the statement and parameter in debug-level, executes select and logs error.
	 * 
	 * @param logger
	 * @param statement
	 * @param parameter
	 * @return
	 * @throws Exception 
	 */
	protected List<Map<String, Object>> select(Logger logger, String statement, Object... parameter) {
		try {
    		logSqlStatement(logger, statement, parameter);
    		return getSimpleJdbcTemplate().queryForList(statement, parameter);
    	} catch (DataAccessException e) {
    		logSqlError(e, logger, statement, parameter);
    		throw e;
    	} catch (RuntimeException e) {
    		logSqlError(e, logger, statement, parameter);
    		throw e;
    	}
	}
	
	/**
	 * Logs the statement and parameter in debug-level, executes select and logs error.
	 * 
	 * @param logger
	 * @param statement
	 * @param parameter
	 * @return
	 * @throws Exception 
	 */
	protected <T> List<T> select(Logger logger, String statement, ParameterizedRowMapper<T> rowMapper, Object... parameter) {
		try {
    		logSqlStatement(logger, statement, parameter);
    		return getSimpleJdbcTemplate().query(statement, rowMapper, parameter);
    	} catch (DataAccessException e) {
    		logSqlError(e, logger, statement, parameter);
    		throw e;
    	} catch (RuntimeException e) {
    		logSqlError(e, logger, statement, parameter);
    		throw e;
    	}
	}
	
	/**
	 * Logs the statement and parameter in debug-level, executes select and logs error.
	 * 
	 * @param logger
	 * @param statement
	 * @param parameter
	 * @return
	 * @throws Exception 
	 */
	protected <T> T select(Logger logger, String statement, Class<T> requiredType, Object... parameter) {
		try {
    		logSqlStatement(logger, statement, parameter);
    		return getSimpleJdbcTemplate().queryForObject(statement, requiredType, parameter);
    	} catch (DataAccessException e) {
    		logSqlError(e, logger, statement, parameter);
    		throw e;
    	} catch (RuntimeException e) {
    		logSqlError(e, logger, statement, parameter);
    		throw e;
    	}
	}
	
	/**
	 * Logs the statement and parameter in debug-level, executes select and logs error.
	 * If the searched entry does not exist an DataAccessException is thrown. 
	 * 
	 * @param logger
	 * @param statement
	 * @param parameter
	 * @return
	 * @throws Exception 
	 */
	protected <T> T selectObject(Logger logger, String statement, ParameterizedRowMapper<T> rowMapper, Object... parameter) {
		try {
    		logSqlStatement(logger, statement, parameter);
    		return getSimpleJdbcTemplate().queryForObject(statement, rowMapper, parameter);
    	} catch (DataAccessException e) {
    		logSqlError(e, logger, statement, parameter);
    		throw e;
    	} catch (RuntimeException e) {
    		logSqlError(e, logger, statement, parameter);
    		throw e;
    	}
	}
	
	/**
	 * Logs the statement and parameter in debug-level, executes select and logs error.
	 * 
	 * @param logger
	 * @param statement
	 * @param parameter
	 * @return
	 * @throws Exception 
	 */
	protected int selectInt(Logger logger, String statement, Object... parameter) {
		try {
    		logSqlStatement(logger, statement, parameter);
    		return getSimpleJdbcTemplate().queryForInt(statement, parameter);
    	} catch (DataAccessException e) {
    		logSqlError(e, logger, statement, parameter);
    		throw e;
    	} catch (RuntimeException e) {
    		logSqlError(e, logger, statement, parameter);
    		throw e;
    	}
	}
	
	/**
	 * Logs the statement and parameter in debug-level, executes select and logs error.
	 * The given default value is returned, if the statement return an EmptyResultDataAccessException,
	 * which indicates that the selected value is missing and no rows are returned by DB.
	 * All other Exceptions are not touched and will be thrown in the usual way.
	 * 
	 * @param logger
	 * @param statement
	 * @param parameter
	 * @return
	 * @throws Exception 
	 */
	protected int selectIntWithDefaultValue(Logger logger, String statement, int defaultValue, Object... parameter) {
		try {
			logSqlStatement(logger, statement, parameter);
			return getSimpleJdbcTemplate().queryForInt(statement, parameter);
    	} catch (EmptyResultDataAccessException e) {
    		if (logger.isDebugEnabled()) {
    			logger.debug("Empty result, using default value: " + defaultValue);
    		}
			return defaultValue;
		} catch (DataAccessException e) {
			logSqlError(e, logger, statement, parameter);
    		throw e;
    	} catch (RuntimeException e) {
    		logSqlError(e, logger, statement, parameter);
    		throw e;
    	}
	}
	
	/**
	 * Logs the statement and parameter in debug-level, executes update and logs error.
	 * 
	 * @param logger
	 * @param statement
	 * @param parameter
	 * @return
	 * @throws Exception 
	 */
	protected int update(Logger logger, String statement, Object... parameter) {
		try {
    		logSqlStatement(logger, statement, parameter);
    		int touchedLines = getSimpleJdbcTemplate().update(statement, parameter);
    		if (logger.isDebugEnabled()) {
    			logger.debug("lines changed by update: " + touchedLines);
    		}
    		return touchedLines;
    	} catch (DataAccessException e) {
    		logSqlError(e, logger, statement, parameter);
    		throw e;
    	} catch (RuntimeException e) {
    		logSqlError(e, logger, statement, parameter);
    		throw e;
    	}
	}
	
	/**
	 * Method to update the data of an blob.
	 * This method should be DB-Vendor independent.
	 * The update statement must contain at least one parameter for the blob data and this must be the first parameter within the statement.
	 * 
	 * Example: updateBlob(logger, "UPDATE tableName SET blobField = ? WHERE idField1 = ? AND idField2 = ?", blobDataArray, id1Object, id2Object);
	 * 
	 * @param statementString
	 * @param blobData
	 * @param parameter
	 * @throws Exception 
	 */
	public void updateBlob(Logger logger, String statement, final byte[] blobData, final Object... parameter) throws Exception {
		final InputStream dataStream = new ByteArrayInputStream(blobData);
		try {
    		logSqlStatement(logger, statement, "blobDataLength:" + blobData.length, parameter);
			new JdbcTemplate(dataSource).execute(statement, new AbstractLobCreatingPreparedStatementCallback(new DefaultLobHandler()) {
				protected void setValues(PreparedStatement preparedStatement, LobCreator lobCreator) throws SQLException {
					lobCreator.setBlobAsBinaryStream(preparedStatement, 1, dataStream, (int) blobData.length);
					int parameterIndex = 2;
					for (Object parameterObject : parameter) {
						preparedStatement.setObject(parameterIndex++, parameterObject);
					}
				}
			});
		} catch(Exception e) {
    		logSqlError(e, logger, statement, "blobDataLength:" + blobData.length, parameter);
    		throw e;
    	} finally {
			try {
				dataStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
