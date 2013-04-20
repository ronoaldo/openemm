/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

package org.agnitas.dao.impl;

import org.agnitas.beans.Company;
import org.agnitas.dao.CompanyDao;
import org.agnitas.util.AgnUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.displaytag.pagination.PaginatedList;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 *
 * @author mhe
 */
public class CompanyDaoImpl extends BaseHibernateDaoImpl implements CompanyDao {
	private static final transient Logger logger = Logger.getLogger(CompanyDaoImpl.class);

    @Override
    public Company getCompany(int companyID) {
        try {
            HibernateTemplate tmpl=new HibernateTemplate(sessionFactory);

            if(companyID==0) {
                return null;
            }
            return (Company)AgnUtils.getFirstResult(tmpl.find("from Company where id = ?", new Object [] {new Integer(companyID)} ));
        } catch(Exception e) {
        	logger.error( "Error processing company " + companyID + ": " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void saveCompany(Company company) {
    	if (company.getId() != 1) {
    		throw new NotImplementedException();
    	}
    	HibernateTemplate tmpl=new HibernateTemplate(sessionFactory);
    	if (getCompany(1) == null) {
    		tmpl.save("Company", company);
    	}
    	else {
    		tmpl.merge("Company", company);
    	}
    }

    @Override
    public void deleteCompany(Company company) {
    	if (company.getId() != 1) {
    		throw new NotImplementedException();
    	}
    	HibernateTemplate tmpl=new HibernateTemplate(sessionFactory);
    	tmpl.delete(company);
        tmpl.flush();
    }

    @Override
    public PaginatedList getCompanyList(int companyID, String sort, String direction, int page, int rownums) {
        return null;
    }
}
