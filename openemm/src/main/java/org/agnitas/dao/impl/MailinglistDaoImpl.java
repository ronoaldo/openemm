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

import javax.sql.DataSource;

import org.agnitas.beans.Mailinglist;
import org.agnitas.dao.MailinglistDao;
import org.agnitas.dao.TargetDao;
import org.agnitas.target.Target;
import org.agnitas.util.AgnUtils;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 *
 * @author mhe
 */
public class MailinglistDaoImpl implements MailinglistDao  {
	
	/** The logger. */
	private static final transient Logger logger = Logger.getLogger(MailinglistDaoImpl.class);
    
    /** Creates a new instance of MailingDaoImpl */
    public MailinglistDaoImpl() {
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public Mailinglist getMailinglist(int listID, int companyID) {
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        Mailinglist list=null;
        
        if(listID==0 || companyID==0) {
            return null;
        }
        
        list=(Mailinglist)AgnUtils.getFirstResult(tmpl.find("from Mailinglist where id = ? and companyID = ?", new Object [] {new Integer(listID), new Integer(companyID)} ));
        
        return list;
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public int saveMailinglist(Mailinglist list) {
        int result=0;
        Mailinglist tmpList=null;
        
        if(list==null || list.getCompanyID()==0) {
            return 0;
        }
        
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        if(list.getId()!=0) {
            tmpList=(Mailinglist)AgnUtils.getFirstResult(tmpl.find("from Mailinglist where id = ? and companyID = ?", new Object [] {new Integer(list.getId()), new Integer(list.getCompanyID())} ));
            if(tmpList==null) {
                list.setId(0);
            }
        }
        
        tmpl.saveOrUpdate("Mailinglist", list);
        tmpl.flush();        
        result=list.getId();
        
        return result;
    }
    
    @Override
    public boolean deleteMailinglist(int listID, int companyID) {
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        int deleted = tmpl.bulkUpdate("delete Mailinglist where id = ? and companyID = ?", new Object [] {new Integer(listID), new Integer(companyID)} );
        
        return deleted > 0;
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public List<Mailinglist> getMailinglists(int companyID) {
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        
        return tmpl.find("from Mailinglist where companyID = ? ORDER BY lower(shortname) ASC", new Object [] {new Integer(companyID)} );
    }
    
    /**
     * deletes the bindings for this mailinglist
     * (invocated before the mailinglist is deleted to avoid
     * orphaned mailinglist bindings)
     * @return return code
     */
    @Override
    public boolean deleteBindings(int id, int companyID) {
        
        JdbcTemplate myJdbcTempl=new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
        String sqlStmt = "delete from customer_" + companyID + "_binding_tbl where mailinglist_id= " + id;

        try {
            myJdbcTempl.execute(sqlStmt);
        } catch(Exception e) {
        	logger.error( "Error deleting bindings: " + sqlStmt, e);
        	AgnUtils.sendExceptionMail("sql:" + sqlStmt, e);
        }
        return true;
    }
    
    @Override
    public int getNumberOfActiveSubscribers(boolean admin, boolean test, boolean world, int targetID, int companyID, int id) {
        int numOfSubscribers=0;
        String sqlSelection=null;
        Target aTarget=null;
        TargetDao tDao=(TargetDao)this.applicationContext.getBean("TargetDao");
        
        
        // no target-group if pure admin/test-mailing
        if(!world) {
            targetID=0;
        }
        
        if(targetID==0) {
            sqlSelection = " ";
        } else {
            aTarget=tDao.getTarget(targetID, companyID);
            if(aTarget!=null) {
                sqlSelection = " AND ("  + aTarget.getTargetSQL() + ") ";
            } else {
                sqlSelection = " ";
            }
        }
        
        if(admin && !test && !world) {
            sqlSelection=sqlSelection+" AND (bind.user_type='A')";
        }
        
        if(admin && test && !world) {
            sqlSelection=sqlSelection+" AND (bind.user_type='A' OR bind.user_type='T')";
        }
        
        String sqlStatement="SELECT count(*) FROM customer_" + companyID + "_tbl cust, customer_" +
                companyID + "_binding_tbl bind WHERE bind.mailinglist_id=" + id +
                " AND cust.customer_id=bind.customer_id" + sqlSelection + " AND bind.user_status=1";
        
        try {
            JdbcTemplate tmpl=new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));

            numOfSubscribers=(int)tmpl.queryForLong(sqlStatement);
        } catch (Exception e) {
        	logger.error( "sql: " + sqlStatement, e);
            numOfSubscribers=0;
            AgnUtils.sendExceptionMail("sql:" + sqlStatement, e);
        }
        
        return numOfSubscribers;
    }

    @Override
    public boolean mailinglistExists(String mailinglistName, int companyID) {
        JdbcTemplate jdbc = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
        String sql = "select mailinglist_id from mailinglist_tbl where company_id=? and shortname=?";
        @SuppressWarnings("unchecked")
		List<Map<String, Object>> list = jdbc.queryForList(sql, new Object[]{new Integer(companyID), mailinglistName});
        return list != null && list.size() > 0;
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

	@Override
	public boolean exist(int mailinglistID, int companyID) {
		JdbcTemplate jdbc = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		String sql = "select count(*) from mailinglist_tbl where company_id = ? and mailinglist_id = ?";
		return jdbc.queryForInt(sql, new Object[]{new Integer(companyID), mailinglistID }) > 0;
	}
}
