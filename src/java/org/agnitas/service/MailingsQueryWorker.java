package org.agnitas.service;

import java.io.Serializable;
import java.util.concurrent.Callable;

import org.agnitas.dao.MailingDao;
import org.displaytag.pagination.PaginatedList;

public class MailingsQueryWorker implements Callable, Serializable{
	private MailingDao mDao;
	private int companyID ;
	private String types;
	private boolean isTemplate;
	private String sort;
	private String direction;
	private int page;
	private int rownums;
		
	public MailingsQueryWorker(MailingDao dao, int companyID, String types,
			boolean isTemplate, String sort, String direction, int page,
			int rownums) {
	  	this.mDao = dao;
		this.companyID = companyID;
		this.types = types;
		this.isTemplate = isTemplate;
		this.sort = sort;
		this.direction = direction;
		this.page = page;
		this.rownums = rownums;
	}

	public PaginatedList call() throws Exception {
		return mDao.getMailingList(companyID, types, isTemplate, sort, direction, page, rownums);
	}

}
