package org.agnitas.service;

import java.io.Serializable;
import java.util.concurrent.Callable;

import org.agnitas.dao.TitleDao;
import org.displaytag.pagination.PaginatedList;

/**
 * wrapper for a long sql query. It will be used for asynchronous tasks
 * @author viktor gema
 *
 */
public class SalutationListQueryWorker implements Callable, Serializable {


	/**
	 *
	 */
	private static final long serialVersionUID = -3047853895576634885L;


	private TitleDao titleDao;
	private String sort;
	private String direction;
	private int page;
	private int rownums;
	private int companyID;


    public SalutationListQueryWorker(TitleDao dao, int companyID,
			String sort, String direction, int page, int rownums ) {
		this.titleDao = dao;
		this.sort = sort;
		this.direction = direction;
		this.page = page;
		this.rownums = rownums;
		this.companyID = companyID;
	}

    public PaginatedList call() throws Exception {
	   return titleDao.getSalutationList(companyID, sort, direction, page, rownums); 
	}



}