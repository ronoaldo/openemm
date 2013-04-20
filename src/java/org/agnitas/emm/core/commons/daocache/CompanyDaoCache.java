package org.agnitas.emm.core.commons.daocache;

import org.agnitas.beans.Company;
import org.agnitas.dao.CompanyDao;

/**
 * Implementation of DAOCache for wrapping a CompanyDAO.
 * 
 * @author md
 */
public class CompanyDaoCache extends AbstractDaoCache<Company> {

	// --------------------------------------------------- Dependency Injection
	/** Wrapped CompanyDAO. */
	private CompanyDao companyDao;

	/**
	 * Set the company DAO.
	 * 
	 * @param companyDao company DAO
	 */
	public void setCompanyDao( CompanyDao companyDao) {
		this.companyDao = companyDao;
	}

	// --------------------------------------------------- Business Logic
	@Override
	protected Company getItemFromDao( int id) {
		return companyDao.getCompany( id);
	}

}
