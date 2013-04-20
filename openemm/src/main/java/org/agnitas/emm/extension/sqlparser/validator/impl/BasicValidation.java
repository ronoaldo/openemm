package org.agnitas.emm.extension.sqlparser.validator.impl;

import org.agnitas.emm.extension.exceptions.DatabaseScriptException;
import org.apache.log4j.Logger;

/**
 * Abstract implementation of the StatementValidation interface providing some utility methods validating table and column names.
 * 
 * @author md
 * 
 * @see StatementValidation
 */
abstract class BasicValidation implements StatementValidation {
	
	/** Logger. */
	private static final transient Logger logger = Logger.getLogger( BasicValidation.class);
	
	/** Enumeration with possible validation results. */
	protected static enum NameValidationResult {
		/** Identifier is valid. */
		OK,
		/** Identifier has the wrong prefix. */
		WRONG_PREFIX,
		/** Identifier has the wrong suffix. */
		WRONG_SUFFIX
	}
	
	/**
	 * Validates a column name.
	 * 
	 * @param name column name to check
	 * @param prefix required prefix
	 * 
	 * @throws DatabaseScriptException when the validation of the column name failed
	 */
	protected void validateColumnName( String name, String prefix) throws DatabaseScriptException {
		switch( validateName( name, prefix, null)) {
		case WRONG_PREFIX:
			logger.info( "Invalid prefix for table name " + name + " (expected: " + prefix + ")");
			throw new DatabaseScriptException( "Column name " + name + " does not start with " + prefix);
		
		case WRONG_SUFFIX:
			logger.info( "Suffix error in column name? That should never happen");
			throw new DatabaseScriptException( "Suffix error in column name? That should never happen");
			
		default:
			// No validation error here, do nothing
		}
		
	}

	/**
	 * Validates a table name. Tables names are expected to end with &quot;_tbl&quot;
	 * 
	 * @param name table name to check
	 * @param prefix required prefix
	 * 
	 * @throws DatabaseScriptException when the validation of the table name failed
	 */
	protected void validateTableName( String name, String prefix) throws DatabaseScriptException {
		// This check matches "plugin_id_tbl" (we do that check here, because this check is valid for tables names only)
		if( name.equals( prefix + "tbl"))
			return;
		
		// This check matches a pattern like "plugin_id_..._tbl"
		switch( validateName( name, prefix, "_tbl")) {
		case WRONG_PREFIX:
			logger.info( "Invalid prefix for table name " + name + " (expected: " + prefix + ")");
			throw new DatabaseScriptException( "Table name " + name + " does not start with " + prefix);
		
		case WRONG_SUFFIX:
			logger.info( "Invalid suffix for table name " + name + " (expected: _tbl)");
			throw new DatabaseScriptException( "Table name " + name + " does not end with _tbl");
			
		default:
			// No validation error here, do nothing
		}
	}
	
	/**
	 * Generic method for validation of names. The prefix and suffix of the name
	 * is only checked, when the corresponding parameter is not null.
	 * 
	 * @param name name to be validated
	 * @param prefix required prefix or null
	 * @param suffix required suffix or null
	 * 
	 * @return result of the validation
	 */
	private NameValidationResult validateName( String name, String prefix, String suffix) {
		String s = name;
		
		if( prefix != null && !s.startsWith( prefix))
			return NameValidationResult.WRONG_PREFIX;
		
		if( suffix != null) {
			if( prefix != null)
				s = s.substring( prefix.length());

			if( !s.endsWith( suffix))
				return NameValidationResult.WRONG_SUFFIX;
		}
		
		return NameValidationResult.OK;
	}
	
	/**
	 * Checks, if the given table name has given prefix (and has the correct suffix).
	 * 
	 * @param name table name
	 * @param prefix name prefix
	 * 
	 * @return true, if table name has given prefix. Returns false if prefix or suffix is wrong.
	 */
	protected boolean tableNameHasPluginPrefix( String name, String prefix) {
		return name.equals( prefix + "tbl") || (validateName( name, prefix, "_tbl") != NameValidationResult.WRONG_PREFIX);
	}
}
