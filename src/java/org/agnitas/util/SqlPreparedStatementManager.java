package org.agnitas.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class SqlPreparedStatementManager {
	private static final String SQL_WHERE_SIGN = "WHERE";
	private static final String SQL_OPERATOR_AND = "AND";
	private static final String SQL_OPERATOR_OR = "OR";
	
	private StringBuilder statement = new StringBuilder();
	private List<Object> statementParameters = new ArrayList<Object>();
	private boolean hasAppendedClauses = false;
	
	public SqlPreparedStatementManager(String baseStatement) {
		String baseStatementString = baseStatement.trim();
		if (baseStatementString.toLowerCase().endsWith(" " + SQL_WHERE_SIGN.toLowerCase())) {
			statement.append(baseStatementString.substring(0, baseStatementString.length() - SQL_WHERE_SIGN.length() - 1));
		} else {
			statement.append(baseStatementString);
		}
	}
	
	/**
	 * Append a where clause to the statement concatenated by " AND "
	 * 
	 * @param whereClause
	 * @param parameter
	 * @throws Exception 
	 */
	public void addWhereClause(String whereClause, Object... parameter) throws Exception {
		addWhereClause(false, whereClause, parameter);
	}
	
	/**
	 * Append a where clause to the statement.
	 * 
	 * @param concatenateByOr type of concatenation (false = AND / true = OR)
	 * @param whereClause
	 * @param parameter
	 * @throws Exception 
	 */
	public void addWhereClause(boolean concatenateByOr, String whereClause, Object... parameter) throws Exception {
		if (StringUtils.isBlank(whereClause)) {
			throw new Exception("Invalid empty where clause");
		}
		
		int numberOfQuestionMarks = StringUtils.countMatches(whereClause, "?");
		if ((parameter == null && numberOfQuestionMarks != 0) || (numberOfQuestionMarks != parameter.length)) {
			throw new Exception("Invalid number of parameters in where clause");
		}
		
		if (hasAppendedClauses) {
			statement.append(" ");
			statement.append(concatenateByOr ? SQL_OPERATOR_OR : SQL_OPERATOR_AND);
		} else {
			statement.append(" ");
			statement.append(SQL_WHERE_SIGN);
		}
		
		statement.append(" (");
		statement.append(whereClause.trim());
		statement.append(")");
		if (parameter != null) {
			for (Object item : parameter) {
				statementParameters.add(item);
			}
		}
			
		hasAppendedClauses = true;
	}
	
	public void finalizeStatement(String statementTail) {
		statement.append(" ");
		statement.append(statementTail.trim());
	}

	public boolean hasAppendedWhereClauses() {
		return hasAppendedClauses;
	}
	
	public String getPreparedSqlString() {
		return statement.toString();
	}
	
	public Object[] getPreparedSqlParameters() {
		return statementParameters.toArray();
	}
}
