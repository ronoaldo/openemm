package org.agnitas.service;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.Callable;
import org.agnitas.dao.RecipientDao;
import org.displaytag.pagination.PaginatedList;

/**
 * wrapper for a long sql query. It will be used for asynchronous tasks
 * 
 * @author ms
 */
public class RecipientBeanQueryWorker implements Callable<PaginatedList>, Serializable {
	private static final long serialVersionUID = 210620121911L;

	protected RecipientDao dao;
	protected String sqlStatementForCount;
	protected Object[] sqlParametersForCount;
	protected String sqlStatementForRows;
	protected Object[] sqlParametersForRows;
	protected String sort;
	protected String direction;
	protected int previousFullListSize;
	protected int page;
	protected int rownums;
	protected Set<String> columns;
	protected Exception error;

	public RecipientBeanQueryWorker(RecipientDao dao, Set<String> columns, String sqlStatementForCount, Object[] sqlParametersForCount, String sqlStatementForRows,
			Object[] sqlParametersForRows, String sort, String direction, int page, int rownums, int previousFullListSize) {
		this.dao = dao;
		this.sqlStatementForCount = sqlStatementForCount;
		this.sqlParametersForCount = sqlParametersForCount;
		this.sqlStatementForRows = sqlStatementForRows;
		this.sqlParametersForRows = sqlParametersForRows;
		this.sort = sort;
		this.direction = direction;
		this.page = page;
		this.rownums = rownums;
		this.previousFullListSize = previousFullListSize;
		this.columns = columns;
	}

	@Override
	public PaginatedList call() throws Exception {
		try {
			return dao.getRecipientList(columns, sqlStatementForCount, sqlParametersForCount, sqlStatementForRows, sqlParametersForRows, sort, direction, page, rownums, previousFullListSize);
		} catch (Exception e) {
			error = e;
			return null;
		}
	}

	public Exception getError() {
		return error;
	}
}
