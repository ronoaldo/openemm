package org.agnitas.emm.company.service.impl;

import org.agnitas.beans.Company;
import org.agnitas.dao.CompanyDao;
import org.agnitas.emm.company.service.CompanyService;

public class CompanyServiceImpl implements CompanyService {
	
	private CompanyDao companyDao;
	
	@Override
	public Company getCompany(int companyID) {
		return companyDao.getCompany(companyID);
	}

	public void setCompanyDao(CompanyDao companyDao) {
		this.companyDao = companyDao;
	}
}
