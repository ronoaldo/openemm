package org.agnitas.emm.extension.sqlparser.validator;

import java.util.List;

import org.agnitas.emm.extension.exceptions.DatabaseScriptException;

/**
 * Interface for implementations to validate SQL scripts.
 * 
 * @author md
 */
public interface DatabaseScriptValidator {
	
	/**
	 * Validate the list of SQL statements.
	 * 
	 * @param statements list of SQL statements
	 * @param namePrefix name prefix used for validation of identifiers (table names, ...)
	 * 
	 * @throws DatabaseScriptException on errors validating the statements (invalid names, ...)
	 */
	public void validate( List<String> statements, String namePrefix) throws DatabaseScriptException;
}
