package org.agnitas.service;

import java.io.Serializable;
import java.util.concurrent.Callable;

import org.agnitas.beans.BlackListEntry;
import org.agnitas.beans.impl.PaginatedListImpl;
import org.agnitas.dao.BlacklistDao;

/**
 * wrapper for a long sql query. It will be used for asynchronous tasks 
 * @author ms
 *
 */
public class BlacklistQueryWorker implements Callable<PaginatedListImpl<BlackListEntry>>, Serializable {
	private static final long serialVersionUID = -3047853894976634885L;
	
	private BlacklistDao blacklistDao;
	private String sort;
	private String direction;
	private int page;
	private int rownums;
	private int companyID;
	
	public BlacklistQueryWorker(BlacklistDao dao, int companyID,
			String sort, String direction, int page, int rownums ) {
		this.blacklistDao = dao;
		this.sort = sort;
		this.direction = direction;
		this.page = page;
		this.rownums = rownums;
		this.companyID = companyID;
	}

	public PaginatedListImpl<BlackListEntry> call() throws Exception {
	   return blacklistDao.getBlacklistedRecipients(companyID, sort, direction, page, rownums); 
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public BlacklistDao getBlacklistDao() {
		return blacklistDao;
	}

	public String getSort() {
		return sort;
	}

	public String getDirection() {
		return direction;
	}

	public int getPage() {
		return page;
	}

	public int getRownums() {
		return rownums;
	}

	public int getCompanyID() {
		return companyID;
	}
}
