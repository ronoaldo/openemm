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

import org.agnitas.dao.TargetDao;
import org.agnitas.dao.exception.target.TargetGroupPersistenceException;
import org.agnitas.target.Target;
import org.agnitas.target.TargetFactory;
import org.agnitas.util.AgnUtils;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateTemplate;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.*;

/**
 * 
 * @author mhe
 */
public class TargetDaoImpl implements TargetDao {
	
	private static final transient Logger logger = Logger.getLogger( TargetDaoImpl.class);
	
	// ---------------------------------------------------------------------------------- DI code

	protected DataSource dataSource;
	protected SessionFactory sessionFactory;
	protected TargetFactory targetFactory;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public void setTargetFactory(TargetFactory targetFactory) {
		this.targetFactory = targetFactory;
	}
	
	// ---------------------------------------------------------------------------------- business logic

	/** Creates a new instance of MailingDaoImpl */
	public TargetDaoImpl() {
	}

	@Override
	public Target getTarget(int targetID, int companyID) {
		Target ret = null;
		try {
			HibernateTemplate tmpl = new HibernateTemplate(this.sessionFactory);

			if (targetID == 0 || companyID == 0) {
				return null;
			}

			ret = (Target) AgnUtils.getFirstResult(tmpl.find(
					"from Target where id = ? and companyID = ?", new Object[] {
							targetID, companyID }));
		} catch (Exception e) {
			logger.error("Target load error (company ID:" + companyID + ", target ID: " + targetID + ")", e);
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * Getter for target by target name and company id.
	 * 
	 * @return target.
	 */
	@Override
	public Target getTargetByName(String targetName, int companyID) {
		HibernateTemplate tmpl = new HibernateTemplate(this.sessionFactory);

		targetName = targetName.trim();

		if (targetName.length() == 0 || companyID == 0) {
			return null;
		}

		return (Target) AgnUtils
				.getFirstResult(tmpl
						.find(
								"from Target where targetName = ? and (companyID = ? or companyID=0)",
								new Object[] { targetName,
										companyID }));
	}

	@Override
	public int saveTarget(Target target) throws TargetGroupPersistenceException {
		int result = 0;
		Target tmpTarget = null;

		if (target == null || target.getCompanyID() == 0) {
			return 0;
		}

		Calendar cal = Calendar.getInstance();
		Timestamp curTime = new Timestamp(cal.getTime().getTime());

		HibernateTemplate tmpl = new HibernateTemplate(this.sessionFactory);

		if (target.getId() != 0) {
			tmpTarget = (Target) AgnUtils.getFirstResult(tmpl.find(
					"from Target where id = ? and companyID = ?", new Object[] {
							target.getId(),
							target.getCompanyID() }));
			if (tmpTarget == null) {
				target.setCreationDate(curTime);
			}
		}
		else {
			target.setCreationDate(curTime);
		}

		target.setChangeDate(curTime);
		tmpl.saveOrUpdate("Target", target);
		result = target.getId();

		return result;
	}

	@Override
	public boolean deleteTarget(int targetID, int companyID) throws TargetGroupPersistenceException {
		Target tmp = null;
		boolean result = false;

		if ((tmp = this.getTarget(targetID, companyID)) != null) {
			tmp = getTarget(targetID, companyID);
			tmp.setDeleted(1);  	// Mark object as "deleted"

			HibernateTemplate tmpl = new HibernateTemplate(this.sessionFactory);

			try {
				tmpl.flush();
				result = true;
			} catch (Exception e) {
				result = false;
			}
		}
		return result;
	}

	@Override
	public List<Target> getTargets(int companyID) {
		return getTargets(companyID, false);
	}

	@Override
	public List<Target> getTargets(int companyID, boolean includeDeleted) {
		HibernateTemplate tmpl = new HibernateTemplate(this.sessionFactory);
		List<Target> targetList;
		
		if(includeDeleted)
			targetList = tmpl.find("from Target where companyID = ? order by targetName", new Object[] { companyID });
		else
			targetList = tmpl.find("from Target where companyID = ? and deleted = 0 order by targetName", new Object[] { companyID });
		
		 Collections.sort(targetList, new Comparator<Target> () {
			public int compare(Target target1, Target target2) {
				return target1.getTargetName().compareToIgnoreCase(target2.getTargetName()) ;
			}
			
		});
		return targetList;
	}

    @Override
    public List<Integer> getDeletedTargets(int companyID) {
		List<Integer> resultList = new ArrayList<Integer>();
		JdbcTemplate jdbc = new JdbcTemplate(this.dataSource);
		String sql = "select target_id from dyn_target_tbl where company_id = ? and deleted = 1";
		List list = jdbc.queryForList(sql);
		for(Object listElement : list) {
			Map map = (Map) listElement;
			int targetId = ((Number) map.get("target_id")).intValue();
			resultList.add(new Integer(targetId));
		}
        return resultList;
    }

    @Override
    public List<String> getTargetNamesByIds(int companyID, Set<Integer> targetIds) {
		List<String> resultList = new ArrayList<String>();
		if (targetIds.size() <= 0) {
			return resultList;
		}
		JdbcTemplate jdbc = new JdbcTemplate(this.dataSource);
		String targetsStr = "";
		for(Integer targetId : targetIds) {
			targetsStr = targetsStr + targetId + ",";
		}
		targetsStr = targetsStr.substring(0, targetsStr.length() - 1);
		String sql = "select target_shortname from dyn_target_tbl where company_id=" + companyID + " and target_id in (" + targetsStr + ")";
		List list = jdbc.queryForList(sql);
		for(Object listElement : list) {
			Map map = (Map) listElement;
			String targetName = (String) map.get("target_shortname");
			resultList.add(targetName);
		}
		return resultList;
	}

    @Override
	public Map getAllowedTargets(int companyID) {
		JdbcTemplate jdbc = new JdbcTemplate(this.dataSource);
		Map targets = new HashMap();
		String sql = "select target_id, target_shortname, target_description, target_sql from dyn_target_tbl where company_id="
				+ companyID + " order by target_id";

		try {
			List list = jdbc.queryForList(sql);
			Iterator i = list.iterator();

			while (i.hasNext()) {
				Map map = (Map) i.next();
				int id = ((Number) map.get("target_id")).intValue();
				String shortname = (String) map.get("target_shortname");
				String description = (String) map.get("target_description");
				String targetsql = (String) map.get("target_sql");
				Target target = this.targetFactory.newTarget();

				target.setCompanyID(companyID);
				target.setId(id);
				if (shortname != null) {
					target.setTargetName(shortname);
				}
				if (description != null) {
					target.setTargetDescription(description);
				}
				if (targetsql != null) {
					target.setTargetSQL(targetsql);
				}
				targets.put(new Integer(id), target);
			}
		} catch (Exception e) {
			logger.error( "getAllowedTargets (sql: " + sql + ")", e);
			AgnUtils.sendExceptionMail("sql:" + sql, e);
			return null;
		}
		return targets;
	}

    @Override
    public List<Target> getTargetGroup(int companyID, Collection<Integer> targetIds) {
       List<Target> resultList = new ArrayList<Target>();
		if (targetIds == null || targetIds.size() <= 0) {
			return resultList;
		}
		String targetsStr = "";
		for(Integer targetId : targetIds) {
			targetsStr = targetsStr + targetId + ",";
		}
		targetsStr = targetsStr.substring(0, targetsStr.length() - 1);
        HibernateTemplate tmpl = new HibernateTemplate(this.sessionFactory);
		resultList = tmpl.find("from Target where companyID = ? and id in ("+ targetsStr + ") order by targetName", new Object[] { companyID });
        return resultList;
    }

    @Override
    public List<Target> getUnchoosenTargets(int companyID, Collection<Integer> targetIds) {
       List<Target> resultList = new ArrayList<Target>();
		if (targetIds == null || targetIds.size() <= 0) {
			return getTargets(companyID,false);
		}
		String targetsStr = "";
		for(Integer targetId : targetIds) {
			targetsStr = targetsStr + targetId + ",";
		}
		targetsStr = targetsStr.substring(0, targetsStr.length() - 1);
        HibernateTemplate tmpl = new HibernateTemplate(this.sessionFactory);
        resultList = tmpl.find("from Target where companyID = ? and deleted=0 and id not in ("+ targetsStr + ") order by targetName", new Object[] { companyID });
        return resultList;
    }

}