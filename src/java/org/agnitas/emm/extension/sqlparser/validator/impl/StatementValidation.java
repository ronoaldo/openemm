package org.agnitas.emm.extension.sqlparser.validator.impl;

import org.agnitas.emm.extension.exceptions.DatabaseScriptException;

/**
 * Interface used to implement validations for different SQL statements.
 * 
 * @author md
 */
interface StatementValidation {
	
	/**
	 * Attempts to validate the given statement. The name prefix can be used to check, if identifier (table names, column names, and so on) 
	 * matches a required format.
	 * 
	 * Three different results are possible:
	 * <ol>
	 * 	<li>The validation step is not responsible for the given statement. In this case, the methods returns <i>false</i></li>
	 *  <li>The validation step is responsible for the given statement and the statement is valid. The method returns <i>true</i></li>
	 *  <li>The validation step is responsible for the given statement but the validation fails. The method throws a DatabaseScriptException.</li>
	 * </ol>	
	 *  
	 * @param statement SQL statement to validate
	 * @param namePrefix name prefix used to check identifiers
	 * 
	 * @return false, when the validation step is not responsible for the given statement or true, when it is responsible for the given statement
	 * and the validation was successful. 
	 * 
	 * @throws DatabaseScriptException when the validation step is responsible for the given statement but the validation of the statement failed.
	 */
	public boolean validate( String statement, String namePrefix) throws DatabaseScriptException;
}
