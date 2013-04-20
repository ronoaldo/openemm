package org.agnitas.emm.extension.sqlparser.validator.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.agnitas.emm.extension.exceptions.DatabaseScriptException;
import org.apache.log4j.Logger;

/**
 * Implementation of the StatementValidation interface providing a simple validation of the <code>DROP TABLE...</code> statement.
 * 
 * @author md
 *
 * @see StatementValidation
 */
class DropTableValidation extends BasicValidation {

	/** Logger. */
	private static final Logger logger = Logger.getLogger( DropTableValidation.class);
	
	/** Regular expression used for pattern recognition. */
	private final Pattern DROP_TABLE_PATTERN = Pattern.compile( "^\\s*drop\\s+table\\s+(?:if\\s+exists\\s+)?(.*?)\\s*$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.UNICODE_CASE);

	@Override
	public boolean validate(String statement, String namePrefix) throws DatabaseScriptException {
		Matcher matcher = DROP_TABLE_PATTERN.matcher( statement);
		
		if( !matcher.matches()) {
			if( logger.isInfoEnabled())
				logger.info( "Statement does not match regular expression");
			
			return false;
		}
		
		String tableName = matcher.group( 1);

		if( logger.isDebugEnabled()) {
			logger.debug( "table: " + tableName);
		}
		
		validateTableName( tableName, namePrefix);
		
		return true;
	}
}
