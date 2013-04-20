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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.sql.DataSource;

import org.agnitas.beans.BindingEntry;
import org.agnitas.beans.MaildropEntry;
import org.agnitas.beans.Mailing;
import org.agnitas.beans.MailingBase;
import org.agnitas.beans.MailingComponent;
import org.agnitas.beans.Mailinglist;
import org.agnitas.beans.Mediatype;
import org.agnitas.beans.TrackableLink;
import org.agnitas.beans.impl.MailingBaseImpl;
import org.agnitas.beans.impl.MailingImpl;
import org.agnitas.beans.impl.MailinglistImpl;
import org.agnitas.beans.impl.PaginatedListImpl;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.TrackableLinkDao;
import org.agnitas.target.Target;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.SafeString;
import org.apache.log4j.Logger;
import org.displaytag.pagination.PaginatedList;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 *
 * @author mhe, Nicole Serek
 */
public class MailingDaoImpl extends BaseDaoImpl implements MailingDao {
	private static final transient Logger logger = Logger.getLogger(MailingDaoImpl.class);
	
	private void processMediatypes(Mailing mailing) {
        if(mailing == null) {
        	return;
        }
		Map<Integer, Mediatype> map = mailing.getMediatypes();
		Iterator<Integer> it = map.keySet().iterator();

		while (it.hasNext()) {
			Integer key = it.next();

			if (map.get(key) instanceof org.agnitas.beans.impl.MediatypeImpl) {
				Mediatype mt = null;
				Mediatype src = (Mediatype) map.get(key);

				switch (key.intValue()) {
				case 0:
					mt = (Mediatype) this.applicationContext.getBean("MediatypeEmail");
					break;
				case 1:
					mt = (Mediatype) this.applicationContext.getBean("MediatypeFax");
					break;
				case 2:
					mt = (Mediatype) this.applicationContext.getBean("MediatypePrint");
					break;
				case 3:
					mt = (Mediatype) this.applicationContext.getBean("MediatypeMMS");
					break;
				case 4:
					mt = (Mediatype) this.applicationContext.getBean("MediatypeSMS");
					break;
				default:
					mt = (Mediatype) this.applicationContext.getBean("Mediatype");
				}
				mt.setPriority(src.getPriority());
				mt.setStatus(src.getStatus());
				try {
					mt.setParam(src.getParam());
				} catch (Exception e) {
					logger.error( "Error processing media type", e);
				}
				map.put(key, mt);
			}
		}
	}

	@Override
	public Mailing getMailing(int mailingID, int companyID) {
        HibernateTemplate tmpl = new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        
        @SuppressWarnings("unchecked")
		Mailing mailing = (Mailing)AgnUtils.getFirstResult(tmpl.find("from Mailing where id = ? and companyID = ? and deleted <> 1", new Object [] {new Integer(mailingID), new Integer(companyID)} ));
        processMediatypes(mailing);
        return mailing;
    }
    
	@Override
    public int saveMailing(Mailing mailing) {
        int result = 0;

        HibernateTemplate tmpl = new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));



        Map<Integer, Mediatype> map = mailing.getMediatypes();
        Map<Integer, Mediatype> dst = new HashMap<Integer, Mediatype>();
        Iterator<Integer> i = map.keySet().iterator();

        while(i.hasNext()) {
            Integer idx = i.next();
            Mediatype mt = (Mediatype) map.get(idx);
            Mediatype tgt = (Mediatype) this.applicationContext.getBean("Mediatype");

            try {
                tgt.setPriority(mt.getPriority()); 
                tgt.setStatus(mt.getStatus()); 
                tgt.setParam(mt.getParam()); 
            } catch(Exception e) {
            	logger.error( "Error saving mailing", e);
            }
            dst.put(idx, tgt);
        }
        mailing.setMediatypes(dst);

        JdbcTemplate jdbc = AgnUtils.getJdbcTemplate(this.applicationContext);
        Map<String, MailingComponent> components = mailing.getComponents();
        Iterator<String> iter = components.keySet().iterator();
        while (iter.hasNext()) {
			MailingComponent entry = components.get(iter.next());
			
			// don't save the images and attachments twice , AGNEMM-141
			if( entry.getType() == MailingComponent.TYPE_IMAGE || entry.getType() == MailingComponent.TYPE_ATTACHMENT || entry.getType() == MailingComponent.TYPE_HOSTED_IMAGE ) {
					entry.setEmmBlock(null);
			}
			
			if (entry.getType() != 0) {
				if (entry.getLink() != null && !entry.getLink().equals("")) {
//					Map trackableLinks = new HashMap();
					TrackableLinkDao linkDao = (TrackableLinkDao) applicationContext.getBean("TrackableLinkDao");
					TrackableLink trkLink = null;
					trkLink = linkDao.getTrackableLink(entry.getLink(), entry.getCompanyID(), mailing.getId());
					if(trkLink == null) {
						trkLink = (TrackableLink) applicationContext.getBean("TrackableLink");
					}
					trkLink.setCompanyID(entry.getCompanyID());
					trkLink.setFullUrl(entry.getLink());
					trkLink.setMailingID(mailing.getId());
					trkLink.setUsage(TrackableLink.TRACKABLE_TEXT_HTML);
					trkLink.setActionID(0);
					linkDao.saveTrackableLink(trkLink);
					
					String sql = "select url_id from rdir_url_tbl where mailing_id = ? and company_id = ? and full_url = ?";
					int id = jdbc.queryForInt(sql,new Object[] { new Integer(mailing.getId()), new Integer(entry.getCompanyID()), entry.getLink() });
					entry.setUrlID(id);
				}
			}
        }

        if (mailing.getId() != 0) {
            if (logger.isInfoEnabled()) logger.info("Clearing mailing");

            try {
                @SuppressWarnings("unchecked")
                MailingBase tmpMailing = (MailingBase) AgnUtils.getFirstResult(tmpl.find("from Mailing where id = ? and companyID = ? and deleted <> 1", new Object[]{new Integer(mailing.getId()), new Integer(mailing.getCompanyID())}));
                if (tmpMailing == null) {
                    mailing.setId(0);
                }
            } catch (TransientDataAccessResourceException e) {
                tmpl.clear();
                throw e;
            }
        }
        tmpl.saveOrUpdate("Mailing", mailing);
        result=mailing.getId();
        tmpl.flush();
        return result;
    }
    
	@Override
    public boolean deleteMailing(int mailingID, int companyID) {
        Mailing tmpMailing = null;
        HibernateTemplate tmpl = new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        
        tmpMailing = this.getMailing(mailingID, companyID);
        if(tmpMailing == null) {
            return false;
        }
        
        tmpMailing.setDeleted(1);
        tmpl.flush();
        
        return true;
    }

	@Override
    @SuppressWarnings("unchecked")
	public List<Mailing> getMailingsForMLID(int companyID, int mailinglistID) {
        HibernateTemplate tmpl = new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        List<Mailing> resultList = tmpl.find("from Mailing where companyID = ? and mailinglistID = ? and deleted = 0", new Object [] {new Integer(companyID), new Integer(mailinglistID)} );
        for (Mailing mailing : resultList) {
			processMediatypes(mailing);
		}
        return resultList;
    }
    
	@Override
    public Map<String, String> loadAction(int mailingID, int companyID) {
        Map<String, String> actions = new HashMap<String, String>();
        JdbcTemplate jdbc = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
    	
    	String stmt = "select action_id, shortname, full_url from rdir_url_tbl where mailing_id = ? and company_id = ?";
    	try {
    		@SuppressWarnings("unchecked")
			List<Map<String, Object>> list = jdbc.queryForList(stmt, new Object[] {new Integer(mailingID), new Integer(companyID)});
    		for(int i = 0; i < list.size(); i++) {
                Map<String, Object> map = list.get(i);
                int action_id = ((Number) map.get("action_id")).intValue();
                if(action_id > 0) {
                	stmt = "select shortname from rdir_action_tbl where company_id = ? and action_id = ?";
                	String action_short = (String) jdbc.queryForObject(stmt, new Object[]{ companyID, action_id }, stmt.getClass());
                
                	String name = "";
                	if (map.get("shortname") != null) {
                		name = (String) map.get("shortname"); 
                	} else {
                		name = (String) map.get("full_url");
                	}
                	actions.put(action_short, name);
                }
    		}
    	} catch (Exception e) {
    		logger.error( "sql:" + stmt + ", " + mailingID + ", " + companyID, e);
    	}
        return actions;
    }

	@Override
    public boolean deleteContentFromMailing(MailingBase mailing, int contentID){
    	JdbcTemplate jdbcTemplate = AgnUtils.getJdbcTemplate(this.applicationContext);
    	String deleteContentSQL = "DELETE from dyn_content_tbl WHERE dyn_content_id = ? AND company_id = ?";

    	if(mailing == null) {
    		mailing = new MailingImpl();
    		mailing.setCompanyID(1);
    	}
    	Object[] params = new Object[]{new Integer(contentID), mailing.getCompanyID()};
    	int affectedRows = 0;
    	affectedRows = jdbcTemplate.update(deleteContentSQL, params);
    	return affectedRows > 0;
    }

	
	/**
	 * Build an SQL-expression from th egiven target_expression.
	 * The expression is a list of targetIDs connected with the operators:
	 * <ul>
	 * <li>( - block start
	 * <li>) - block end
	 * <li>&amp; - AND 
	 * <li>| - OR 
	 * <li>! - NOT 
	 * </ul>
	 * @param targetExpression The expression as string.
	 * @param jdbc Template for SQL queries.
	 * @return the resulting where clause.
	 */
	@Override
	public String getSQLExpression(String targetExpression) {
		if (targetExpression == null) {
			return null;
		}

		StringBuffer buf = new StringBuffer();
		int tlen = targetExpression.length();

		for (int n = 0; n < tlen; ++n) {
			char ch = targetExpression.charAt(n);

			if ((ch == '(') || (ch == ')')) {
				buf.append(ch);
			} else if ((ch == '&') || (ch == '|')) {
				if (ch == '&')
					buf.append(" AND");
				else
					buf.append(" OR");
				while (((n + 1) < tlen) && (targetExpression.charAt(n + 1) == ch))
					++n;
			} else if (ch == '!') {
				buf.append(" NOT");
			} else if (Character.isDigit(ch)) {
				String temp = "";
				int first = n;
				int tid = (-1);

				while (n < tlen && Character.isDigit(targetExpression.charAt(n))) {
					n++;
				}
				tid = Integer.parseInt(targetExpression.substring(first, n));
				n--;
				temp = select(logger, "select target_sql from dyn_target_tbl where target_id = ?", String.class, tid);
				if (temp != null && temp.trim().length() > 2)
					buf.append(" (" + temp + ")");
			}
		}
		if (buf.length() >= 3)
			return buf.toString();
		return null;
	}

	/**
	 * Finds the last newsletter that would have been sent to the given
	 * customer.
	 * @param customerID Id of the recipient for the newsletter.
	 * @param companyID the company to look in.
	 * @return The mailingID of the last newsletter that would have been
	 *              sent to this recipient.
	 */
	@Override
	public int	findLastNewsletter(int customerID, int companyID, int mailinglist) {
		JdbcTemplate jdbc = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		String	sql="select m.mailing_id, m.target_expression, a."+AgnUtils.changeDateName()+" from mailing_tbl m left join mailing_account_tbl a ON a.mailing_id=m.mailing_id where m.company_id=? and m.deleted<>1 and m.is_template=0 and a.status_field='W' and m.mailinglist_id=? order by a."+AgnUtils.changeDateName()+" desc, m.mailing_id desc";

		try {
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> list = jdbc.queryForList(sql, new Object[] {new Integer(companyID), new Integer(mailinglist)});

			for(int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
				int mailing_id = ((Number) map.get("mailing_id")).intValue();
				String targetExpression = (String) map.get("target_expression");

				if(targetExpression == null || targetExpression.trim().length() == 0) {
					return mailing_id;
				}
				sql="select count(*) from customer_" + companyID + "_tbl cust where " + getSQLExpression(targetExpression) + " and customer_id=?";
				
				if( logger.isInfoEnabled()) {
					logger.info( "SQL: " + sql);  // Was previously "
				}

				if(jdbc.queryForInt(sql, new Object[]{ customerID }) > 0) {
					return mailing_id;
				}
                	}
		} catch (Exception e) {
			logger.error( "findLastNewsletter: " + e.getMessage(), e);
		}
		return 0;
	}
	
	@Override
	public String[]	getTag(String name, int companyID) {
        JdbcTemplate jdbc = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		String sql = "select selectvalue, type from tag_tbl where tagname=? and (company_id=0 or company_id=?)";
		String[] result = null;

		try {
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> list = jdbc.queryForList(sql, new Object[] { name, new Integer(companyID) });

			if(list.size() > 0) {
				Map<String, Object> map = list.get(0);

				result = new String[]{ (String) map.get("selectvalue"), (String) map.get("type") };
			}
		} catch (Exception e) {
			logger.error( "processTag: " + e.getMessage(), e);
			AgnUtils.sendExceptionMail("sql:" + sql + ", "+ name + ", " + companyID, e);

			result = null;
		}
		return result;
	}

	@Override
	public String	getAutoURL(int mailingID)	{
        JdbcTemplate jdbc = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		String	sql="select auto_url from mailing_tbl where mailing_id=?";

		try	{
			return (String) jdbc.queryForObject(sql, new Object[]{new Integer(mailingID)}, sql.getClass());
		} catch(Exception e) {
			logger.error( "getAutoURL: " + e.getMessage(), e);
		}
		return null;
	}
	
	@Override
	public String getAutoURL(int mailingID, int companyID) {
		JdbcTemplate jdbc = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		String rdirdomain = null;
		String rdir_mailinglistquery = "select  ml.RDIR_DOMAIN  FROM MAILINGLIST_TBL ml JOIN MAILING_TBL m ON ( ml.MAILINGLIST_ID = m.MAILINGLIST_ID) WHERE  m.MAILING_ID=?"; 
		rdirdomain = (String) jdbc.queryForObject(rdir_mailinglistquery, new Object[]{new Integer(mailingID)}, String.class );
		if( rdirdomain != null ) {
			return rdirdomain;
		}
		String rdir_companyquery = "select RDIR_DOMAIN FROM COMPANY_TBL where company_id=?";
		rdirdomain = (String) jdbc.queryForObject(rdir_companyquery, new Object[]{new Integer(companyID)}, String.class );
			return rdirdomain;
	}

	@Override
    public List<Map<String, String>> getTags(int companyID) {
        JdbcTemplate jdbc = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		String sql = "SELECT tagname, selectvalue FROM tag_tbl WHERE company_id IN (0, ?) AND tagname NOT IN ('agnITAS', 'agnAUTOURL', 'agnLASTNAME', 'agnFIRSTNAME', 'agnMAILTYPE') ORDER BY tagname";
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();

		try {
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> list= jdbc.queryForList(sql, new Object[]{new Integer(companyID)});

			if(list.size() > 0) {
                for(int i=0; i<list.size(); i++){
                	Map<String, Object> map = list.get(i);
                    Map<String,String> mapstr = new HashMap<String,String>();
                    mapstr.put((String) map.get("tagname"), (String) map.get("selectvalue"));
                    result.add(mapstr);
                }
			}
		} catch (Exception e) {
			logger.error( "getTags: " + e.getMessage(), e);
			
			result = null;
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

	@Override
	public PaginatedList getMailingList(int companyID, String types, boolean isTemplate, String sort, String direction, int page, int rownums) {
		JdbcTemplate aTemplate = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		List<String> charColumns = Arrays.asList(new String[] { "shortname", "description", "mailinglist" });

		String mailingTypes = " AND mailing_type in (" + types + ") ";
		if (isTemplate) {
			mailingTypes = " ";
		}

		String orderby = null;
		String defaultorder = " send_null ASC, senddate DESC, mailing_id DESC  ";
		if (sort != null && !"".equals(sort.trim())) {
			orderby = getUpperSort(charColumns, sort);
			orderby = orderby + " " + direction;
		} else {
			orderby = defaultorder;
		}

		String sqlStatement = " SELECT *, case when senddate is null then 0 else 1 end as send_null " + " FROM (   SELECT a.mailing_id , a.shortname  , a.description ,   min(c."
				+ AgnUtils.changeDateName() + ") senddate, m.shortname mailinglist "
				+ " FROM  (mailing_tbl  a LEFT JOIN mailing_account_tbl c ON (a.mailing_id=c.mailing_id AND c.status_field='W')) "
				+ " LEFT JOIN mailinglist_tbl m ON (  a.mailinglist_id=m.mailinglist_id AND  a.company_id=m.company_id) "
				+ "  WHERE a.company_id = ? AND a.deleted<>1 AND a.is_template=?" + mailingTypes
				+ "  GROUP BY  a.mailing_id, a.shortname, a.description, m.shortname ) openemm ORDER BY " + orderby;

		int totalsize = aTemplate.queryForInt("select count(*) from ( " + sqlStatement + ") agn", new Object[] { companyID, (isTemplate ? 1 : 0) });
        page = AgnUtils.getValidPageNumber(totalsize, page, rownums);
        int offset = (page - 1) * rownums;

		sqlStatement = sqlStatement + " LIMIT " + offset + " , " + rownums;

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> tmpList = aTemplate.queryForList(sqlStatement, new Object[] { companyID, (isTemplate ? 1 : 0) });

		List<MailingBase> result = new ArrayList<MailingBase>();
		for (Map<String, Object> row : tmpList) {
			MailingBase newBean = new MailingBaseImpl();

			int mailingID = ((Number) row.get("MAILING_ID")).intValue();
			newBean.setId(mailingID);
			newBean.setShortname((String) row.get("SHORTNAME"));
			newBean.setDescription((String) row.get("DESCRIPTION"));

			Mailinglist mailinglist = new MailinglistImpl();
			mailinglist.setShortname((String) row.get("MAILINGLIST"));

			newBean.setMailinglist(mailinglist);
			newBean.setSenddate((Date) row.get("SENDDATE"));
			if (hasActions(mailingID, companyID))
				newBean.setHasActions(Boolean.TRUE);

			result.add(newBean);
		}

		PaginatedListImpl<MailingBase> paginatedList = new PaginatedListImpl<MailingBase>(result, totalsize, rownums, page, sort, direction);

		return paginatedList;
	}
	
	protected String getUpperSort(List<String> charColumns, String sort) {
		String upperSort = sort;
		if (charColumns.contains( sort )) {
	    	upperSort = "upper( trim( " +sort + ") )";
	     }
		return upperSort;
	}

	@Override
	public String getFormat(int type) {
		String format = "d.M.yyyy";
		JdbcTemplate jdbc = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		try {
            String sql = "SELECT format FROM date_tbl WHERE type = ?";
            format = (String) jdbc.queryForObject(sql, new Object[] {new Integer(type)}, String.class);
        } catch (Exception e) {
        	logger.error( "Query failed for data_tbl: " + e.getMessage(), e);
        }
		return format;
	}
	
	
	
	@Override
	public int getStatusidForWorldMailing(int mailingID, int companyID) {
		int returnValue = 0;
		JdbcTemplate jdbcTemplate = new JdbcTemplate( dataSource );
		String query = "SELECT status_id  FROM maildrop_status_tbl  WHERE company_id="+companyID+" and mailing_id="+mailingID+" and status_field='W' and genstatus=3 and senddate < now()";
		try {	
			returnValue = jdbcTemplate.queryForInt(query);
		} catch (Exception e) {
			returnValue = 0;
		}
		return returnValue;
		
	}
	
	@Override
	public int getGenstatusForWorldMailing(int mailingID) throws Exception {
		JdbcTemplate jdbcTemplate = new JdbcTemplate( dataSource );
		String query = "SELECT genstatus  FROM maildrop_status_tbl  WHERE mailing_id= ? and status_field='W' ";
		return jdbcTemplate.queryForInt(query, new Object[]{mailingID});
		
	}

	@Override
	public boolean hasPreviewRecipients(int mailingID, int companyID) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		String query = "SELECT DISTINCT 1 FROM mailing_tbl m, mailinglist_tbl ml, customer_" + companyID + "_binding_tbl c WHERE c.user_type in ('A', 'T') AND c.mailinglist_id = ml.mailinglist_id AND ml.company_id = " + companyID + " AND m.mailinglist_id = ml.mailinglist_id AND m.mailing_id = " + mailingID + " AND m.company_id = " + companyID + " AND c.user_status = 1";
		
		try {
			template.queryForInt(query);
			return true;
		} catch (Exception e) {
			logger.error( "hasPreviewRecipients: mailingID = " + mailingID + ", companyID = " + companyID, e);
			return false;
		}
	}

	@Override
    public Map<Integer, Integer> getAllMailingsOnTheSystem() {
        return null;
    }

	@Override
	public boolean isTransmissionRunning(int mailingID) {
		return true;
	}

	@Override
    public boolean hasActions(int mailingId, int companyID) {
        JdbcTemplate jdbc = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
    	String stmt = "select count(action_id) from rdir_url_tbl where mailing_id = ? and company_id = ? and action_id != 0";
    	try {
    		Integer count = jdbc.queryForInt(stmt, new Object[] {new Integer(mailingId), new Integer(companyID)});
            return count > 0;
    	} catch (Exception e) {
    		logger.error( "hasActions: " + e.getMessage(), e);
    		AgnUtils.sendExceptionMail("sql:" + stmt + ", " + mailingId + ", " + companyID, e);
    	}        
        return false;
    }

    @Override
    public boolean cleanupContentForDynName(int mailingID, String dynName, int companyID) {
        // do nothing because Hibernate will manage these beans
        return false;
    }

	@Override
    public String compareMailingsNameAndDesc(String mailingIDList, Hashtable<Integer, String> allNames, Hashtable<Integer, String> allDesc, int companyID) {

        String csv_file = "";
        // * Names & descriptions  *  //
        String sql = "SELECT shortname, description, mailing_id FROM mailing_tbl A WHERE company_id=" + companyID + " AND mailing_id IN (" + mailingIDList + ")";

        Connection dbCon = DataSourceUtils.getConnection(dataSource);

        try {
            Statement stmt = dbCon.createStatement();
            ResultSet rset = stmt.executeQuery(sql);

            while (rset.next()) {
                Integer id = new Integer(rset.getInt(3));

                allNames.put(id, SafeString.getHTMLSafeString(rset.getString(1)));
                allDesc.put(id, SafeString.getHTMLSafeString(rset.getString(2)));
                csv_file += "\r\n" + SafeString.getHTMLSafeString(rset.getString(1)) + " (" + SafeString.getHTMLSafeString(rset.getString(2)) + ")";
            }
            rset.close();
            stmt.close();
        } catch (Exception e) {
        	logger.error( "error loading mailing info (sql: " + sql + ")", e);
        }
        DataSourceUtils.releaseConnection(dbCon, dataSource);

        return csv_file;
    }

	@Override
    public int compareMailingsSendMailings(String mailingIDList, Hashtable<Integer, Integer> allSent, int biggestRecipients, int companyID, Target aTarget) {
        //  T O T A L   S E N T   M A I L S
        StringBuffer sqlBuf = null;
        Connection dbCon = null;
        sqlBuf = new StringBuffer("SELECT count(distinct mailtrack.customer_id), mailtrack.mailing_id FROM mailtrack_tbl mailtrack ");

        if (aTarget.getId() != 0) {
            sqlBuf.append(", customer_" + companyID + "_tbl cust");
        }
        sqlBuf.append(" WHERE mailtrack.company_id=" + companyID + "  and mailtrack.mailing_id IN (" + mailingIDList);
        sqlBuf.append(") ");
        if (aTarget.getId() != 0) {
            sqlBuf.append(" AND ((" + aTarget.getTargetSQL() + ") AND cust.customer_id=mailtrack.customer_id)");
        }
        sqlBuf.append(" GROUP BY mailtrack.mailing_id");
        dbCon = DataSourceUtils.getConnection(dataSource);
        try {
            Statement stmt = dbCon.createStatement();
            ResultSet rset = stmt.executeQuery(sqlBuf.toString());

            while (rset.next()) {
                Integer id = new Integer(rset.getInt(2));    // get MailingID
                if (allSent.containsKey(id)) {      // check if there is a value for this mailing
                    int aVal = ((Integer) allSent.get(id)).intValue();
                    if (rset.getInt(1) > aVal) {
                        allSent.put(id, new Integer(rset.getInt(1)));
                    }
                } else {
                    allSent.put(id, new Integer(rset.getInt(1)));
                }
                // used for bar length in JSP's graphical display
                if (rset.getInt(1) > biggestRecipients) {
                    biggestRecipients = rset.getInt(1);
                }
            }
            rset.close();
            stmt.close();
        } catch (Exception e) {
        	logger.error( "Error getting total mailing info (sql: " + sqlBuf.toString() + ")", e);
        }
        DataSourceUtils.releaseConnection(dbCon, dataSource);
        return biggestRecipients;
    }


	@Override
    public int compareMailingsOpened(String mailingIDList, int companyID, Hashtable<Integer, Integer> allOpen, int biggestOpened, Target aTarget) {
        Connection dbCon = null;
        StringBuffer sqlBuf = null;

        // O P E N E D   M A I L S
        sqlBuf = new StringBuffer("SELECT count(onepixel.customer_id), onepixel.mailing_id FROM onepixel_log_tbl onepixel");
        if (aTarget.getId() != 0) {
            sqlBuf.append(", customer_" + companyID + "_tbl cust");
        }
        sqlBuf.append(" WHERE company_id=" + companyID + " AND mailing_id IN (" + mailingIDList + ")");
        if (aTarget.getId() != 0) {
            sqlBuf.append(" AND ((" + aTarget.getTargetSQL() + ") AND onepixel.customer_id=cust.customer_id)");
        }
        sqlBuf.append(" GROUP BY mailing_id");
        dbCon = DataSourceUtils.getConnection(dataSource);
        try {
            Statement stmt = dbCon.createStatement();
            ResultSet rset = stmt.executeQuery(sqlBuf.toString());

            while (rset.next()) {
                Integer id = new Integer(rset.getInt(2));
                allOpen.put(id, new Integer(rset.getInt(1)));
                if (rset.getInt(1) > biggestOpened) {
                    biggestOpened = rset.getInt(1);
                }
            }
            rset.close();
            stmt.close();
        } catch (Exception e) {
        	logger.error( "Error getting opened mails (sql: " + sqlBuf.toString() + ")", e);
        }
        DataSourceUtils.releaseConnection(dbCon, dataSource);
        return biggestOpened;
    }

	@Override
    public int compareMailingsTotalClicks(String mailingIDList, Hashtable<Integer, Integer> allClicks, int biggestClicks, int companyID, Target aTarget) {
        // * T O T A L   C L I C K S *
        Connection dbCon = null;
        StringBuffer sqlBuf = null;
        sqlBuf = new StringBuffer("SELECT count(rdir.customer_id), rdir.url_id, rdir.mailing_id FROM rdir_log_tbl rdir");
        if (aTarget.getId() != 0) {
            sqlBuf.append(", customer_" + companyID + "_tbl cust");
        }

        sqlBuf.append(" WHERE company_id=" + companyID + " AND rdir.mailing_id IN (" + mailingIDList + ")");

        if (aTarget.getId() != 0) {
            sqlBuf.append(" AND ((" + aTarget.getTargetSQL() + ") AND cust.customer_id=rdir.customer_id)");
        }
        sqlBuf.append(" GROUP BY rdir.url_id, rdir.mailing_id");
        dbCon = DataSourceUtils.getConnection(dataSource);
        try {
            Statement stmt = dbCon.createStatement();
            ResultSet rset = stmt.executeQuery(sqlBuf.toString());

            while (rset.next()) {
                Integer id = new Integer(rset.getInt(3)); // get mailingID
                int aVal = 0;

                if (allClicks.containsKey(id)) {
                    aVal = ((Integer) allClicks.get(id)).intValue();
                    aVal += rset.getInt(1);
                } else {
                    aVal = rset.getInt(1);
                }
                allClicks.put(id, new Integer(aVal));
                if (aVal > biggestClicks) {
                    biggestClicks = aVal;
                }
            }
            rset.close();
            stmt.close();
        } catch (Exception e) {
        	logger.error( "Error getting total clicks (sql: " + sqlBuf.toString() + ")", e);
        }
        DataSourceUtils.releaseConnection(dbCon, dataSource);
        return biggestClicks;
    }

	@Override
    public Map<String, Integer> compareMailingsOptoutAndBounce(String mailingIDList, Hashtable<Integer, Integer> allOptout, Hashtable<Integer, Integer> allBounce, int biggestOptout, int biggestBounce, int companyID, Target aTarget) {
        Connection dbCon = null;
        StringBuffer sqlBuf = null;
        Map<String, Integer> optoutBounce = new HashMap<String, Integer>();

        // O P T O U T  &  B O U N C E
        sqlBuf = new StringBuffer("SELECT count(bind.customer_id), bind.user_status, bind.exit_mailing_id FROM customer_" + companyID + "_binding_tbl bind");
        if (aTarget.getId() != 0) {
            sqlBuf.append(", customer_" + companyID + "_tbl cust");
        }
        sqlBuf.append(" WHERE exit_mailing_id IN (" + mailingIDList + ")");
        if (aTarget.getId() != 0) {
            sqlBuf.append(" AND ((" + aTarget.getTargetSQL() + ") AND cust.customer_id=bind.customer_id)");
        }
        sqlBuf.append(" GROUP BY bind.user_status, bind.exit_mailing_id, bind.mailinglist_id");
        dbCon = DataSourceUtils.getConnection(dataSource);
        try {
            Statement stmt = dbCon.createStatement();
            ResultSet rset = stmt.executeQuery(sqlBuf.toString());


            while (rset.next()) {
                Integer id = new Integer(rset.getInt(3));
                switch (rset.getInt(2)) {
                    case BindingEntry.USER_STATUS_ADMINOUT:
                    case BindingEntry.USER_STATUS_OPTOUT:
                        allOptout.put(id, new Integer(rset.getInt(1)));
                        if (rset.getInt(1) > biggestOptout) {
                            biggestOptout = rset.getInt(1);
                        }
                        break;

                    case BindingEntry.USER_STATUS_BOUNCED:
                        int tmpVal = 0;
                        if (allBounce.containsKey(id)) {
                            tmpVal = ((Integer) allBounce.get(id)).intValue();
                        }
                        if (rset.getInt(1) > tmpVal) {
                            tmpVal = rset.getInt(1);
                        }
                        allBounce.put(id, new Integer(tmpVal));
                        if (rset.getInt(1) > biggestBounce) {
                            biggestBounce = tmpVal;
                        }
                        break;
                }
            }
            rset.close();
            stmt.close();
        } catch (Exception e) {
        	logger.error( "Error getting optout (sql: " + sqlBuf.toString() + ")", e);
        }
        DataSourceUtils.releaseConnection(dbCon, dataSource);
        //  * * * * * * * * * * * * * * * * * * * * * * * * * * * *
        //  * * E N D   G E T T I N G   D A T A   F R O M   D B * *
        //  * * * * * * * * * * * * * * * * * * * * * * * * * * * *
        optoutBounce.put("biggestBounce", biggestBounce);
        optoutBounce.put("biggestOptout", biggestOptout);
        return optoutBounce;
    }


    @Override
    public List<MailingBase> getMailingsForComparation(int companyID) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate( dataSource );

        String sqlStatement = "SELECT mailing_id, shortname, description FROM mailing_tbl A WHERE company_id=? AND deleted<>1 AND is_template=0 and A.mailing_id in (select mailing_id from maildrop_status_tbl where status_field in ('W', 'E', 'C') and company_id = ?) ORDER BY mailing_id DESC";                
        @SuppressWarnings("unchecked")
		List<Map<String, Object>> tmpList = jdbcTemplate.queryForList(sqlStatement, new Object[]{companyID, companyID});

        List<MailingBase> result = new ArrayList<MailingBase>();
        for (Map<String, Object> row : tmpList) {
            MailingBase newBean = new MailingBaseImpl();
            int mailingID = ((Number) row.get("MAILING_ID")).intValue();
            newBean.setId(mailingID);
            newBean.setShortname((String) row.get("SHORTNAME"));
            newBean.setDescription((String) row.get("DESCRIPTION"));

            result.add(newBean);
        }

        return result;
    }
    
    @Override
    public List<Mailing> getTemplates(int companyID) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate( dataSource );

        String sqlStatement = "SELECT mailing_id, shortname FROM mailing_tbl WHERE company_id=? AND is_template=1 AND deleted=0 ORDER BY shortname";
        @SuppressWarnings("unchecked")
		List<Map<String, Object>> tmpList = jdbcTemplate.queryForList(sqlStatement, new Object[]{companyID});

        List<Mailing> result = new ArrayList<Mailing>();
        for (Map<String, Object> row : tmpList) {
            Mailing newBean = new MailingImpl();
            int mailingID = ((Number) row.get("MAILING_ID")).intValue();
            newBean.setId(mailingID);
            newBean.setShortname((String) row.get("SHORTNAME"));

            result.add(newBean);
        }

        return result;
    }
    
	@Override
	public List<MailingBase> getTemplateMailingsByCompanyID(int companyID) {
		HibernateTemplate tmpl = new HibernateTemplate((SessionFactory) this.applicationContext.getBean("sessionFactory"));
		@SuppressWarnings("unchecked")
		List<MailingBase> result = tmpl.find("from Mailing where companyID = ? and deleted = 0 and is_template = 1 ORDER BY shortname", new Object[] { new Integer(companyID) });
		return result;
	}

	@Override
	public MailingBase getMailingForTemplateID(int templateID, int companyID) {
		HibernateTemplate tmpl = new HibernateTemplate((SessionFactory) this.applicationContext.getBean("sessionFactory"));
		@SuppressWarnings("unchecked")
		MailingBase result = (MailingBase) AgnUtils.getFirstResult(tmpl.find("from Mailing where id = ? and companyID = ? ORDER BY shortname", new Object[] { new Integer(templateID), new Integer(companyID) }));
		return result;
	}

	@Override
    public List<MailingBase> getMailingsByStatusE(int companyID) {
        HibernateTemplate tmpl = new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        @SuppressWarnings("unchecked")
		List<MailingBase> result = tmpl.find("from Mailing where companyID = ? and deleted <> 1 and maildropStatus.status = 'E' and mailingType = 1", new Object [] {new Integer(companyID)} );
        return result;
    }
    
    @Override
	public List<Integer> getTemplateReferencingMailingIds(Mailing mailTemplate) {
		if( !mailTemplate.isIsTemplate()) // No template? Do nothing!
			return null;
		
		String query = "SELECT m.mailing_id FROM mailing_tbl m WHERE m.dynamic_template=1 AND m.mailtemplate_id = ? AND m.company_id=? AND deleted=0 AND NOT EXISTS (SELECT 1 FROM maildrop_status_tbl mds WHERE mds.mailing_id = m.mailing_id AND mds.status_field IN ('w', 'W'))";
		SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(dataSource);
		List<Map<String, Object>> mailingIdList = jdbcTemplate.queryForList(query, mailTemplate.getId(), mailTemplate.getCompanyID());
		
		List<Integer> result = new Vector<Integer>();
		for( Map<String, Object> idMap : mailingIdList) {
			result.add( ((Number) idMap.get( "MAILING_ID")).intValue());
		}
		return result;
	}

	@Override
	public boolean checkMailingReferencesTemplate(int templateID, int companyID) {
		SimpleJdbcTemplate template = new SimpleJdbcTemplate( this.dataSource);
		
		int isTemplate = template.queryForInt( "SELECT is_template FROM mailing_tbl WHERE mailing_id=? AND company_id=?", templateID, companyID);
		
		return isTemplate == 1;
	}


	@Override
	public boolean exist(int mailingID, int companyID) {
		JdbcTemplate jdbc = new JdbcTemplate(dataSource);
		String sql = "select count(*) from mailing_tbl where company_id = ? and mailing_id = ? and deleted <> 1";
		return jdbc.queryForInt(sql, new Object[]{new Integer(companyID), mailingID }) > 0;

	}

    @Override
	public boolean exist(int mailingID, int companyID, boolean isTemplate) {
		JdbcTemplate jdbc = new JdbcTemplate(dataSource);
		String sql = "select count(*) from mailing_tbl where company_id = ? and mailing_id = ? and deleted <> 1 and is_template = ?";
		return jdbc.queryForInt(sql, new Object[]{new Integer(companyID), mailingID, isTemplate }) > 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Mailing> getMailings(int companyId, boolean isTemplate) {
        HibernateTemplate tmpl = new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        List<Mailing> resultList = tmpl.find("from Mailing where companyID = ? and deleted = 0 and is_template = ?", new Object [] {companyId, isTemplate} );
        for (Mailing mailing : resultList) {
			processMediatypes(mailing);
		}
        return resultList;
	}

    @Override
	public int getMailingOpenAction(int mailingID, int companyID) {
        try {
            JdbcTemplate jdbc = new JdbcTemplate(dataSource);
            String sql = "select openaction_id from mailing_tbl where company_id = ? and mailing_id = ?";
		    return jdbc.queryForInt(sql, new Object[]{companyID, mailingID });
        } catch (Exception e) {
        	logger.info( "Error while getting mailing open action ID (mailingID: " + mailingID + ", companyID: " + companyID + ")", e);
			return 0;
		}
	}

    @Override
	public int getMailingClickAction(int mailingID, int companyID) {
        try {
            JdbcTemplate jdbc = new JdbcTemplate(dataSource);
            String sql = "select clickaction_id from mailing_tbl where company_id = ? and mailing_id = ?";
            return jdbc.queryForInt(sql, new Object[]{companyID, mailingID });
        } catch (Exception e) {
        	logger.info( "Error while getting mailing click action ID (mailingID: " + mailingID + ", companyID: " + companyID + ")", e);

			return 0;
		}

	}

	@Override
    public boolean isWorldMailingSent(int mailingId, int companyId) {
        String sql="select maildrop.status_field, mail.mailing_type from mailing_tbl mail join maildrop_status_tbl maildrop on " +
                "(mail.mailing_id = maildrop.mailing_id) where mail.mailing_id = ? and mail.company_id = ? and maildrop.company_id = ?";
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        @SuppressWarnings("unchecked")
		List<Map<String, Object>> resultList = jdbc.queryForList(sql, new Object[] {mailingId, companyId, companyId});
        if (resultList == null || resultList.isEmpty()) {
            return false;
        }
        int mailingType = ((Number)resultList.get(0).get("mailing_type")).intValue();

        String status= String.valueOf(MaildropEntry.STATUS_WORLD);
        switch(mailingType) {
            case Mailing.TYPE_ACTIONBASED:
                status = String.valueOf(MaildropEntry.STATUS_ACTIONBASED);
                break;
            case Mailing.TYPE_DATEBASED:
                status = String.valueOf(MaildropEntry.STATUS_DATEBASED);
                break;
        }

        for (Map<String, Object> rowMap : resultList) {
            String dropStatus = (String)rowMap.get("status_field");
            if (status.equals(dropStatus)) {
                return true;
            }
        }

        return false;
    }

    /**
	 * returns the mailing-Parameter for the given mailing (only email, no sms or anything else).
	 * @param mailingID
	 * @return
	 */

	@Override
	public String getEmailParameter(int mailingID) {
		String params = null;
		JdbcTemplate jdbc = new JdbcTemplate(dataSource);
		String sql = "SELECT param FROM mailing_mt_tbl WHERE mailing_id=" + mailingID +	" AND mediatype=0";
		try {
			params = (String) jdbc.queryForObject(sql, String.class);
		} catch (Exception e) {
			logger.error( "getEmaiLParameter() failed for mailing " + mailingID, e);
		}
		return params;
	}
}
