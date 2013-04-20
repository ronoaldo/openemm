package org.agnitas.emm.extension.sqlparser.validator.impl;

import java.util.List;

import org.agnitas.emm.extension.exceptions.DatabaseScriptException;
import org.agnitas.emm.extension.sqlparser.validator.DatabaseScriptValidator;
import org.apache.log4j.Logger;

/**
 * Implementation of a simple SQL script validator.
 * 
 * The validator used classes implementing the StatementValidation interface.
 * 
 * @author md
 *
 *@see DatabaseScriptValidator
 */
public class SimpleDatabaseScriptValidator implements DatabaseScriptValidator {

	/** Logger. */
	private static final transient Logger logger = Logger.getLogger( SimpleDatabaseScriptValidator.class);
	
	/** List of all validation steps. To extend the range of validation, simply add new instances here. */
	private StatementValidation[] validationSteps = new StatementValidation[] {
			new CreateTableValidation(),
			new DropTableValidation(),
			new AlterTableAddColumnValidation(),
			new AlterTableDropColumnValidation()
	};
	
	@Override
	public void validate(List<String> statements, String namePrefix) throws DatabaseScriptException {
		
		if( logger.isInfoEnabled())
			logger.info( "Validating statements. Name prefix is '" + namePrefix + "'");
		
		for( String statement : statements)
			validate( statement, namePrefix);
	}

	/** 
	 * Validate a single SQL statement.
	 * 
	 * @param statement SQL statement to validate
	 * @param namePrefix name prefix used for validation of identifiers (table names, ...)
	 * 
	 * @throws DatabaseScriptException on errors validating the statement (e. g. invalid names)
	 */
	private void validate( String statement, String namePrefix) throws DatabaseScriptException {
		if( logger.isInfoEnabled())
			logger.info( "Validating statement: " + statement);

		for( StatementValidation step : validationSteps) {
			if( logger.isInfoEnabled())
				logger.info( "Validating statement (validation class: " + step.getClass().getCanonicalName() + ")");
			
			if( step.validate( statement, namePrefix))
				break;	// Terminate loop if statement was validated
		}
	}
}
