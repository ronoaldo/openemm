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


import org.agnitas.beans.TrackableLink;
import org.agnitas.dao.TrackableLinkDao;
import org.agnitas.util.AgnUtils;
import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 *
 * @author mhe
 */
public class TrackableLinkDaoImpl extends BaseHibernateDaoImpl implements TrackableLinkDao {
	private static final transient Logger logger = Logger.getLogger(TrackableLinkDaoImpl.class);

    @Override
    public TrackableLink getTrackableLink(int linkID, int companyID) {
        HibernateTemplate tmpl=new HibernateTemplate(sessionFactory);

        if(linkID==0 || companyID==0) {
            return null;
        }

        return (TrackableLink)AgnUtils.getFirstResult(tmpl.find("from TrackableLink where id = ? and companyID = ?", new Object [] {new Integer(linkID), new Integer(companyID)} ));
    }
    
    @Override
    public TrackableLink getTrackableLink(String url, int companyID, int mailingID) {
        HibernateTemplate tmpl=new HibernateTemplate(sessionFactory);

        if(url.equals("") || companyID==0) {
            return null;
        }

        return (TrackableLink)AgnUtils.getFirstResult(tmpl.find("from TrackableLink where fullUrl = ? and companyID = ? and mailingID = ?", new Object [] {url, new Integer(companyID), new Integer(mailingID)} ));
    }

    @Override
    public int saveTrackableLink(TrackableLink link) {
        int result=0;
        TrackableLink tmpLink=null;

        if(link==null || link.getCompanyID()==0) {
            return 0;
        }

        HibernateTemplate tmpl=new HibernateTemplate(sessionFactory);
        if(link.getId()!=0) {
            tmpLink=(TrackableLink)AgnUtils.getFirstResult(tmpl.find("from TrackableLink where id = ? and companyID = ?", new Object [] {new Integer(link.getId()), new Integer(link.getCompanyID())} ));
            if(tmpLink==null) {
                link.setId(0);
            }
        }

        tmpl.saveOrUpdate("TrackableLink", link);
        result=link.getId();
        tmpl.flush();

        return result;
    }

    @Override
    public boolean deleteTrackableLink(int linkID, int companyID) {
        TrackableLink tmp=null;
        boolean result=false;

        if((tmp=this.getTrackableLink(linkID, companyID))!=null) {
            HibernateTemplate tmpl=new HibernateTemplate(sessionFactory);
            try {
                tmpl.delete(tmp);
                result=true;
            } catch (Exception e) {
                result=false;
            }
        }

        return result;
    }

    @Override
    public boolean setDeeptracking(int deepTracking, int companyID, int mailingID) {
        boolean result=false;

        String sql="UPDATE RDIR_URL_TBL SET DEEP_TRACKING=? WHERE COMPANY_ID=? AND MAILING_ID=?";
        
        if( logger.isInfoEnabled()) {
        	logger.info( "setDeeptracking - sql: " + sql);
        }

        try {
        	getSimpleJdbcTemplate().update(sql, new Object[] {new Integer(deepTracking), new Integer(companyID), new Integer(mailingID) });
            result=true;
       } catch (Exception e) {
           result=false;
       }

       return result;
    }

    @Override
	public boolean logClickInDB(TrackableLink link, int customerID, String remoteAddr) {
		String sql="insert into rdir_log_tbl (customer_id, url_id, company_id, ip_adr, mailing_id) values (?, ?, ?, ?, ?)";

		try {
			getSimpleJdbcTemplate().update(sql, new Object[] { new Integer(customerID), new Integer(link.getId()), new Integer(link.getCompanyID()), remoteAddr, new Integer(link.getMailingID())});
		} catch (Exception e) {
			logger.error("logClickInDB (customer ID: " + customerID + ", remote addr: " + remoteAddr + ")", e);
			return false;
		}
		return true;
	}
}
