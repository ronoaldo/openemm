package org.agnitas.service;

import java.util.concurrent.Callable;

import org.agnitas.beans.AdminEntry;
import org.agnitas.beans.impl.PaginatedListImpl;
import org.agnitas.dao.AdminDao;

/**
 * wrapper for a long sql query. It will be used for asynchronous tasks
 * @author viktor gema
 */
public class AdminListQueryWorker implements Callable<PaginatedListImpl<AdminEntry>> {
	private AdminDao adminDao;
	private String sort;
	private String direction;
	private int page;
	private int rownums;
	private int companyID;


    public AdminListQueryWorker(AdminDao dao, int companyID, String sort, String direction, int page, int rownums ) {
		this.adminDao = dao;
		this.sort = sort;
		this.direction = direction;
		this.page = page;
		this.rownums = rownums;
		this.companyID = companyID;
	}

    public PaginatedListImpl<AdminEntry> call() throws Exception {
	   return adminDao.getAdminListByCompanyId(companyID, sort, direction, page, rownums); 
	}
}