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


import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.agnitas.beans.Mailloop;
import org.agnitas.beans.MailloopEntry;
import org.agnitas.beans.impl.MailloopEntryImpl;
import org.agnitas.beans.impl.MailloopPaginatedList;
import org.agnitas.dao.MailloopDao;
import org.agnitas.util.AgnUtils;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.displaytag.pagination.PaginatedList;
import org.apache.commons.lang.StringUtils;

import javax.sql.DataSource;

/**
 *
 * @author mhe
 */
public class MailloopDaoImpl implements MailloopDao {
    
    /** Creates a new instance of MailingDaoImpl */
    public MailloopDaoImpl() {
    }
    
    @Override
    public Mailloop getMailloop(int mailloopID, int companyID) {
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        
        if(mailloopID==0 || companyID==0) {
            return null;
        }
        
        return (Mailloop)AgnUtils.getFirstResult(tmpl.find("from Mailloop where id = ? and companyID = ?", new Object [] {new Integer(mailloopID), new Integer(companyID)} ));
    }
    
    @Override
    public List getMailloops(int companyID) {
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));

        return tmpl.find("from Mailloop where companyID = ?", new Object[] {new Integer(companyID) });
    }

    @Override
    public PaginatedList getMailloopList(int companyID, String sort, String direction, int page, int rownums) {

        if (StringUtils.isEmpty(sort.trim())) {
            sort = "rid";
        }
        String sortForQuery = "upper( " + sort + " )";

        if (StringUtils.isEmpty(direction)) {
            direction = "asc";
        }


        String totalRowsQuery = "select count(rid) from mailloop_tbl WHERE company_id = ?";

        JdbcTemplate templateForTotalRows = new JdbcTemplate(getDataSource());

        int totalRows = -1;
        try {
            totalRows = templateForTotalRows.queryForInt(totalRowsQuery, new Object[]{companyID});
        } catch (Exception e) {
            totalRows = 0; // query for int has a bug , it doesn't return '0' in case of nothing is found...
        }
         if (page < 1) {
        	page = 1;
        }

        int offset = (page - 1) * rownums;
        String mailloopListQuery = "select shortname, description, rid from mailloop_tbl WHERE company_id = ? order by " + sortForQuery + " " + direction + "  LIMIT ? , ? ";

        JdbcTemplate templateForMailloop = new JdbcTemplate(getDataSource());
        List<Map> mailloopElements = templateForMailloop.queryForList(mailloopListQuery, new Object[]{companyID, offset, rownums});
        return new MailloopPaginatedList(toMailloopList(mailloopElements), totalRows, page, rownums, sort, direction);
    }

    protected List<MailloopEntry> toMailloopList(List<Map> mailloopElements) {
        List<MailloopEntry> mailloopEntryList = new ArrayList<MailloopEntry>();
        for (Map row : mailloopElements) {
            Long id = (Long) row.get("rid");
            String description = (String) row.get("description");
            String shortname = (String) row.get("shortname");
            MailloopEntry entry = new MailloopEntryImpl(id, description, shortname, "");
            mailloopEntryList.add(entry);
        }

        return mailloopEntryList;

    }

    protected DataSource getDataSource() {
        return (DataSource) applicationContext.getBean("dataSource");
    }
    
    @Override
    public int saveMailloop(Mailloop loop) {
        int result=0;
        Mailloop tmpLoop=null;
        
        if(loop==null || loop.getCompanyID()==0) {
            return 0;
        }
        
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        if(loop.getId()!=0) {
            tmpLoop=(Mailloop)AgnUtils.getFirstResult(tmpl.find("from Mailloop where id = ? and companyID = ?", new Object [] {new Integer(loop.getId()), new Integer(loop.getCompanyID())} ));
            if(tmpLoop==null) {
                loop.setId(0);
            }
        }
        
        tmpl.saveOrUpdate("Mailloop", loop);
        result=loop.getId();
        tmpl.flush();
        
        return result;
    }
    
    @Override
    public boolean deleteMailloop(int loopID, int companyID) {
        Mailloop tmp=null;
        boolean result=false;
        
        if((tmp=this.getMailloop(loopID, companyID))!=null) {
            HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
            try {
                tmpl.delete(tmp);
                tmpl.flush();
                result=true;
            } catch (Exception e) {
                result=false;
            }
        }
        
        return result;
    }
    
    /**
     * Holds value of property applicationContext.
     */
    protected ApplicationContext applicationContext;
    
    /**
     * Setter for property applicationContext.
     * @param applicationContext New value of property applicationContext.
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

}
