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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.agnitas.actions.EmmAction;
import org.agnitas.actions.impl.EmmActionImpl;
import org.agnitas.dao.EmmActionDao;
import org.agnitas.util.AgnUtils;
import org.apache.log4j.Logger;
import org.displaytag.tags.TableTagParameters;
import org.displaytag.util.ParamEncoder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 *
 * @author mhe
 */
public class EmmActionDaoImpl extends BaseHibernateDaoImpl implements EmmActionDao {
	private static final transient Logger logger = Logger.getLogger(EmmActionDaoImpl.class);
    
    /** Creates a new instance of MailingDaoImpl */
    public EmmActionDaoImpl() {
    }
    
    public EmmAction getEmmAction(int actionID, int companyID) {
        EmmAction ret=null;
        
        if(actionID==0 || companyID==0) {
            return null;
        }
       
        try { 
            ret=(EmmAction)AgnUtils.getFirstResult(new HibernateTemplate(sessionFactory).find("from EmmAction where id = ? and companyID = ?", new Object [] {new Integer(actionID), new Integer(companyID)} ));
        } catch(org.springframework.orm.hibernate3.HibernateSystemException he) {
            org.hibernate.type.SerializationException se=(org.hibernate.type.SerializationException) he.getCause();
            if(se.getCause() != null && se.getCause() instanceof ClassNotFoundException) {
                ClassNotFoundException e=(ClassNotFoundException) se.getCause();

                logger.error("Cause: "+e.getCause());
                logger.error("Message: "+e.getMessage());
            } else if(se.getCause() != null) {
                logger.error("Cause: "+se.getCause());
                logger.error("CauseClass: "+se.getCause().getClass());
            } else {
                logger.error("Null Cause");
            }
        }
        return ret;
    }
    
    public int saveEmmAction(EmmAction action) {
        int result=0;
        
        if(action==null || action.getCompanyID()==0) {
            return 0;
        }
        
        new HibernateTemplate(sessionFactory).saveOrUpdate("EmmAction", action);
        result=action.getId();
        
        return result;
    }
    
    public boolean deleteEmmAction(int actionID, int companyID) {
        EmmAction tmp=null;
        boolean result=false;
        
        if((tmp=this.getEmmAction(actionID, companyID))!=null) {
            HibernateTemplate tmpl = new HibernateTemplate(sessionFactory);
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
    
    public List getEmmActions(int companyID) {
        return new HibernateTemplate(sessionFactory).find("from EmmAction where companyID = ? order by shortname", new Object [] {new Integer(companyID)} );
    }

    @Override
    public List getEmmNotFormActions(int companyID) {
        return new HibernateTemplate(sessionFactory).find("from EmmAction where companyID = ? and type != ? order by shortname", new Object [] {new Integer(companyID), new Integer(EmmAction.TYPE_FORM)} );
    }

    @Override
    public List getEmmNotLinkActions(int companyID) {
        return new HibernateTemplate(sessionFactory).find("from EmmAction where companyID = ? and type != ? order by shortname", new Object [] {new Integer(companyID), new Integer(EmmAction.TYPE_LINK)} );
    }

    public Map loadUsed(int companyID) {
    	Map used = new HashMap();
    	JdbcTemplate jdbc=new JdbcTemplate(dataSource);
    	
    	String stmt = "select action_id from rdir_action_tbl where company_id = ?";
    	try {
    		List list = jdbc.queryForList(stmt, new Object[] {new Integer(companyID)});
    		for(int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                int action_id = ((Number) map.get("action_id")).intValue();
                stmt = "select count(*) from userform_tbl where company_id = ? and (startaction_id = ? or endaction_id = ?)";
                int count = jdbc.queryForInt(stmt, new Object [] {new Integer(companyID), new Integer(action_id), new Integer(action_id)});
                used.put(action_id, count);
    		}
    	} catch (Exception e) {
    		AgnUtils.sendExceptionMail("sql:" + stmt, e);
    		logger.error(e.getMessage());
    		logger.error(AgnUtils.getStackTrace(e));
    	}
    	return used;
    }

    public String getUserFormNames(int actionId, int companyId){
          String result = "";
          String sql = "select formname from userform_tbl where (startaction_id="+ actionId + " or endaction_id=" + actionId + ") and company_id="+ companyId +" ORDER BY formname";
          List<Map> tmpList = new JdbcTemplate(dataSource).queryForList(sql);
          for(Map row : tmpList) {
             if(!result.equals("")){
                 result += "; ";
             }
             result += row.get("formname");
          }
          return result;
    }

    public List<EmmAction> getActionList(HttpServletRequest request) {
	      List<Integer>  charColumns = Arrays.asList(new Integer[]{0, 1, 2});
		  String[] columns = new String[] {"r.shortname", "r.shortname","r.description", "" };

		  int sortcolumnindex = 0;
		  if(request.getParameter(new ParamEncoder("emmaction").encodeParameterName(TableTagParameters.PARAMETER_SORT)) != null ) {
			  sortcolumnindex = Integer.parseInt(request.getParameter(new ParamEncoder("emmaction").encodeParameterName(TableTagParameters.PARAMETER_SORT)));
		  }

		  String sort =  columns[sortcolumnindex];
		  if(charColumns.contains(sortcolumnindex)) {
		  	 sort =   "upper( " +sort + " )";
		  }

		  int order = 1;
		  if(request.getParameter(new ParamEncoder("emmaction").encodeParameterName(TableTagParameters.PARAMETER_ORDER)) != null ) {
		   	 order = new Integer(request.getParameter(new ParamEncoder("emmaction").encodeParameterName(TableTagParameters.PARAMETER_ORDER)));
		  }

	      String sqlStatement = "SELECT r.action_id, r.shortname, r.description, count(u.form_id) used " +
	      		" FROM rdir_action_tbl r LEFT JOIN userform_tbl u ON (u.startaction_id = r.action_id or u.endaction_id = r.action_id) " +
	      		" WHERE r.company_id= " + AgnUtils.getCompanyID(request) +
	      		" GROUP BY  r.action_id, r.shortname, r.description " +
	      		" ORDER BY "+ sort 	+ " " + (order == 1?"ASC":"DESC");

	      List<Map> tmpList = new JdbcTemplate(dataSource).queryForList(sqlStatement);

	      List<EmmAction> result = new ArrayList<EmmAction>();
	      for(Map row:tmpList) {

	    	  EmmAction newBean = new EmmActionImpl();
	    	  newBean.setId(((Number)row.get("ACTION_ID")).intValue());
	    	  newBean.setShortname((String) row.get("SHORTNAME"));
	    	  newBean.setDescription( (String) row.get("DESCRIPTION"));
	    	  newBean.setUsed(((Number) row.get("USED")).intValue());
              if(newBean.getUsed() > 0){
                  newBean.setFormNames(getUserFormNames(newBean.getId(),AgnUtils.getCompanyID(request)));
              } else {
                  newBean.setFormNames("");
              }
	    	  result.add(newBean);
	      }
	      return result;
    }
}
