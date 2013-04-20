package org.agnitas.service;

import javax.servlet.http.HttpServletRequest;

import org.agnitas.dao.TargetDao;
import org.agnitas.target.TargetNode;
import org.agnitas.target.TargetNodeFactory;
import org.agnitas.target.TargetRepresentation;
import org.agnitas.target.TargetRepresentationFactory;
import org.agnitas.target.impl.TargetNodeDate;
import org.agnitas.target.impl.TargetNodeNumeric;
import org.agnitas.target.impl.TargetNodeString;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.SqlPreparedStatementManager;
import org.agnitas.web.RecipientForm;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Helper-class for building the sql-query in /recipient/list.jsp
 * 
 * @author ms
 */
public class RecipientQueryBuilder {
	private static final transient Logger logger = Logger.getLogger(RecipientQueryBuilder.class);

	private static final String PREFIX_BIND = "bind";
	private static final String PREFIX_CUST = "cust";

	private TargetDao targetDao;

	public void setTargetDao(TargetDao targetDao) {
		this.targetDao = targetDao;
	}

	private TargetRepresentation createTargetRepresentationFromForm(RecipientForm form, TargetRepresentationFactory targetRepresentationFactory, TargetNodeFactory targetNodeFactory) {
		TargetRepresentation target = targetRepresentationFactory.newTargetRepresentation();

		int lastIndex = form.getNumTargetNodes();

		for (int index = 0; index < lastIndex; index++) {
			String colAndType = form.getColumnAndType(index);
			String column = colAndType.substring(0, colAndType.indexOf('#'));
			String type = colAndType.substring(colAndType.indexOf('#') + 1);

			TargetNode node = null;

			if (type.equalsIgnoreCase("VARCHAR") || type.equalsIgnoreCase("VARCHAR2") || type.equalsIgnoreCase("CHAR")) {
				node = createStringNode(form, column, type, index, targetNodeFactory);
			} else if (type.equalsIgnoreCase("INTEGER") || type.equalsIgnoreCase("DOUBLE") || type.equalsIgnoreCase("NUMBER")) {
				node = createNumericNode(form, column, type, index, targetNodeFactory);
			} else if (type.equalsIgnoreCase("DATE")) {
				node = createDateNode(form, column, type, index, targetNodeFactory);
			}

			target.addNode(node);
		}

		return target;
	}

	/**
	 * construct a sql query from all the provided parameters
	 * 
	 * @param request
	 * 
	 * @return
	 * @throws Exception 
	 */
	public SqlPreparedStatementManager getSQLStatement(HttpServletRequest request, RecipientForm aForm, TargetRepresentationFactory targetRepresentationFactory, TargetNodeFactory targetNodeFactory, boolean optimized, boolean queryForCount) throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("Creating SQL statement for recipients");
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Oracle DB: " + AgnUtils.isOracleDB());
			logger.debug("optimized: " + optimized);
			logger.debug("result type: " + (queryForCount ? "count" : "list"));
		}

		String sort = request.getParameter("sort");
		if (sort == null) {
			sort = aForm.getSort();
			
			if (logger.isDebugEnabled()) {
				logger.debug("request parameter sort = null");
				logger.debug("using form parameter sort = " + sort);
			}
		}

		String direction = request.getParameter("dir");
		if (direction == null) {
			direction = aForm.getOrder();
			
			if (logger.isDebugEnabled()) {
				logger.debug("request parameter dir = null");
				logger.debug("using form parameter order = " + direction);
			}
		}

		if (request.getParameter("listID") != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("parameter listID = " + request.getParameter("listID"));
			}

			aForm.setListID(Integer.parseInt(request.getParameter("listID")));
		}
		int mailingListID = aForm.getListID();

		if (request.getParameter("targetID") != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("parameter targetID = " + request.getParameter("targetID"));
			}

			aForm.setTargetID(Integer.parseInt(request.getParameter("targetID")));
		}
		int targetID = aForm.getTargetID();

		if (request.getParameter("user_type") != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("parameter user_type = " + request.getParameter("user_type"));
			}

			aForm.setUser_type(request.getParameter("user_type"));
		}
		String user_type = aForm.getUser_type();

		if (request.getParameter("searchFirstName") != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("parameter searchFirstName = " + request.getParameter("searchFirstName"));
			}

			aForm.setSearchFirstName(request.getParameter("searchFirstName"));
		}
		String firstName = aForm.getSearchFirstName();

		if (request.getParameter("searchLastName") != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("parameter searchLastName = " + request.getParameter("searchLastName"));
			}

			aForm.setSearchLastName(request.getParameter("searchLastName"));
		}
		String lastName = aForm.getSearchLastName();

		if (request.getParameter("searchEmail") != null) {
			aForm.setSearchEmail(request.getParameter("searchEmail"));
		}
		String email = aForm.getSearchEmail();

		if (request.getParameter("user_status") != null) {
			aForm.setUser_status(Integer.parseInt(request.getParameter("user_status")));
		}
		int user_status = aForm.getUser_status();

		SqlPreparedStatementManager mainStatement;
		if (queryForCount) {
			mainStatement = new SqlPreparedStatementManager("SELECT COUNT(*) FROM customer_" + AgnUtils.getCompanyID(request) + "_tbl " + PREFIX_CUST);
		} else {
			mainStatement = new SqlPreparedStatementManager("SELECT * FROM customer_" + AgnUtils.getCompanyID(request) + "_tbl " + PREFIX_CUST);
		}

		if (targetID != 0) {
			mainStatement.addWhereClause(targetDao.getTarget(targetID, AgnUtils.getCompanyID(request)).getTargetSQL());
		}

		if (StringUtils.isNotEmpty(firstName)) {
			mainStatement.addWhereClause("lower(" + PREFIX_CUST + ".firstname) = ?", firstName.toLowerCase().trim());
		}

		if (StringUtils.isNotEmpty(lastName)) {
			mainStatement.addWhereClause("lower(" + PREFIX_CUST + ".lastname) = ?", lastName.toLowerCase().trim());
		}

		if (StringUtils.isNotEmpty(email)) {
			mainStatement.addWhereClause(PREFIX_CUST + ".email like ('%' || ? || '%')", email.toLowerCase().trim());
		}

		if (AgnUtils.isOracleDB() && optimized) {
			int maxRownum = 20;
			if (!StringUtils.isEmpty(aForm.getPage()) && StringUtils.isNumeric(aForm.getPage())) {
				maxRownum = ((Integer.parseInt(aForm.getPage()) - 1) * aForm.getNumberofRows()) + aForm.getNumberofRows() + 1;
			}
			mainStatement.addWhereClause("rownum < ?", maxRownum);
		}
		
		SqlPreparedStatementManager customerIdSubSelectStatement = new SqlPreparedStatementManager("SELECT customer_id FROM customer_" + AgnUtils.getCompanyID(request) + "_binding_tbl " + PREFIX_BIND);

		if (user_type != null && !"E".equalsIgnoreCase(user_type)) {
			customerIdSubSelectStatement.addWhereClause(PREFIX_BIND + ".user_type = ?", user_type);
		}

		if (user_status != 0) {
			customerIdSubSelectStatement.addWhereClause(PREFIX_BIND + ".user_status = ?", user_status);
		}

		if (mailingListID != 0) {
			customerIdSubSelectStatement.addWhereClause(PREFIX_BIND + ".mailinglist_id = ?", mailingListID);
		}
		
		TargetRepresentation targetRep = createTargetRepresentationFromForm(aForm, targetRepresentationFactory, targetNodeFactory);
		if (targetRep.generateSQL().length() > 0 && targetRep.checkBracketBalance()) {
			if (targetRep.generateSQL().contains("bind.")) {
				customerIdSubSelectStatement.addWhereClause(targetRep.generateSQL());
			} else {
				mainStatement.addWhereClause(targetRep.generateSQL());
			}
		}
		
		if (customerIdSubSelectStatement.hasAppendedWhereClauses()) {
			mainStatement.addWhereClause(PREFIX_CUST + ".customer_id in (" + customerIdSubSelectStatement.getPreparedSqlString() + ")", customerIdSubSelectStatement.getPreparedSqlParameters());
		}

		// we need the sorting of inner query only for Oracle
		if (AgnUtils.isOracleDB()) {
			if (StringUtils.isNotBlank(sort)) {
				mainStatement.finalizeStatement("ORDER BY " + sort.trim() + " " + direction);
			}
		}

		return mainStatement;
	}

	private TargetNodeString createStringNode(RecipientForm form, String column, String type, int index, TargetNodeFactory factory) {
		return factory.newStringNode(form.getChainOperator(index), form.getParenthesisOpened(index), column, type, form.getPrimaryOperator(index), form.getPrimaryValue(index),
				form.getParenthesisClosed(index));
	}

	private TargetNodeNumeric createNumericNode(RecipientForm form, String column, String type, int index, TargetNodeFactory factory) {
		int primaryOperator = form.getPrimaryOperator(index);
		int secondaryOperator = form.getSecondaryOperator(index);
		int secondaryValue = 0;

		if (primaryOperator == TargetNode.OPERATOR_MOD.getOperatorCode()) {
			try {
				secondaryOperator = Integer.parseInt(form.getSecondaryValue(index));
			} catch (Exception e) {
				secondaryOperator = TargetNode.OPERATOR_EQ.getOperatorCode();
			}
			try {
				secondaryValue = Integer.parseInt(form.getSecondaryValue(index));
			} catch (Exception e) {
				secondaryValue = 0;
			}
		}

		return factory.newNumericNode(form.getChainOperator(index), form.getParenthesisOpened(index), column, type, primaryOperator, form.getPrimaryValue(index),
				secondaryOperator, secondaryValue, form.getParenthesisClosed(index));
	}

	private TargetNodeDate createDateNode(RecipientForm form, String column, String type, int index, TargetNodeFactory factory) {
		return factory.newDateNode(form.getChainOperator(index), form.getParenthesisOpened(index), column, type, form.getPrimaryOperator(index), form.getDateFormat(index),
				form.getPrimaryValue(index), form.getParenthesisClosed(index));
	}
}