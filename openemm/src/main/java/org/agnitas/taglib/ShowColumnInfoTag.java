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

package org.agnitas.taglib;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.agnitas.beans.ProfileField;
import org.agnitas.service.ColumnInfoService;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.CaseInsensitiveMap;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Prepares a list of userdefined fields for use in web-templates.
 */
public class ShowColumnInfoTag extends BodyBase {
   
	private static final transient Logger logger = Logger.getLogger(ShowColumnInfoTag.class);
	
    private static final long serialVersionUID = -1235292192519826728L;
	// global variables:
    protected String id=null;
    protected int table=0;
    protected Set<String> sys_columns=new HashSet<String>();
    
    /**
     * Set the id of the table to show info for.
     *
     * @param table companyID to show the table for.
     */
    public void setTable(int table) {
        this.table=table;
    }
    
    /**
     * Set the id to use for session variables.
     *
     * @param id the id to use for global variables.
     */
    @Override
    public void setId(String id) {
        this.id=id;
    }
    
    
    /**
     * Set the hidden columns for this query.
     *
     * @param hide a comma sepereated list of columnnames which should not be
     *             shown.
     */
    public void setHide(String hide) {
        StringTokenizer tok=new StringTokenizer(hide, ",");
        
        while (tok.hasMoreTokens()) {
            sys_columns.add(tok.nextToken().trim());
        }
    }
    
//	private static String	dbtype2string(int type)	{
//		switch(type) {
//			case java.sql.Types.BIGINT:
//			case java.sql.Types.INTEGER:
//			case java.sql.Types.SMALLINT:
//				return new String("INTEGER");
//
//			case java.sql.Types.DECIMAL:
//			case java.sql.Types.DOUBLE:
//			case java.sql.Types.FLOAT:
//			case java.sql.Types.NUMERIC:
//			case java.sql.Types.REAL:
//				return new String("DOUBLE");
//                            
//			case java.sql.Types.CHAR:
//				return new String("CHAR");
//
//			case java.sql.Types.VARCHAR:
//			case java.sql.Types.LONGVARCHAR:
//			case java.sql.Types.CLOB:
//				return new String("VARCHAR");
//
//			case java.sql.Types.DATE:
//			case java.sql.Types.TIMESTAMP:
//			case java.sql.Types.TIME:
//				return new String("DATE");
//		}
//		return new String("UNKNOWN("+type+")");
//	}

    /**
     * Get information about database columns.
     * The information will be gathered from the DatabaseMetaData of the table
     * and the infoTable. The give infoTable should be the name of a table
     * which holds the following fields:
     * <dl>
     *    <dt>col_name
     *    <dd>Primary key to identify the column
     *    <dt>shortname
     *    <dd>Textfield for a short descriptive name of the column
     *    <dt>default_value
     *    <dd>the value which should be used for the column, when no other is given.
     * </dl>
     * The resulting list contains one row for each found column.
     * Each row is a Map consists of:
     * <dl>
     *    <dt>column
     *    <dd>the name of the column in Database
     *    <dt>type
     *    <dd>the typename as in java.sql.Types
     *        (eg. VARCHAR for a java.sql.Types.VARCHAR
     *    <dt>length
     *    <dd>the size of the column as in DatabaseMetaData.getColumns()
     *    <dt>nullable
     *    <dd>inidcates whather the column can contain NULL values (1) or not (0).
     *    <dt>shortname (optional)
     *    <dd>a descriptive name for the column
     *    <dt>default (optional)
     *    <dd>value that should be used, when no value is given.
     *    <dt>description (optional)
     *    <dd>descriptive text for the column
     * </ul>
     * 
     * @param context ApplicationContext, required to get a database connection.
     * @param customer id of the customer (required to access the correct table)
     * @param column column to query or "%" for all columns
     * @throws java.lang.Exception 
     * @return TreeMap containing column informations
     */
    public static List<ProfileField> getColumnInfos(ApplicationContext context, int customer) throws Exception {
        //DataSource ds=(DataSource)context.getBean("dataSource");
        ColumnInfoService columnInfoService = (ColumnInfoService) context.getBean("ColumnInfoService");
        
        return columnInfoService.getColumnInfos(customer);
//        Connection con=null;
//        LinkedHashMap<String,Hashtable<String,Object>> list=new LinkedHashMap<String, Hashtable<String,Object>>();
//        ResultSet rset=null;
//        
//        con=DataSourceUtils.getConnection(ds);
//        try {
//            if(AgnUtils.isOracleDB()) {
//                rset=con.getMetaData().getColumns(null, AgnUtils.getDefaultValue("jdbc.username").toUpperCase(), "CUSTOMER_"+customer+"_TBL", column.toUpperCase());
//            } else {
//                rset=con.getMetaData().getColumns(null, null, "customer_"+customer+"_tbl", column);
//            }
//            if(rset!=null) {
//                while(rset.next()) {
//                    String type=null;
//                    String col=rset.getString(4).toLowerCase();
//                    Hashtable m=new Hashtable();
//
//                    m.put("column", col);
//                    m.put("shortname", col);
//                    type=dbtype2string(rset.getInt(5));
//                    m.put("type", type);
//                    m.put("length", new Integer(rset.getInt(7)));
//                    if(rset.getInt(11) == DatabaseMetaData.columnNullable)
//                        m.put("nullable", new Integer(1));
//                    else
//                        m.put("nullable", new Integer(0));
//                    
//                    list.put((String)m.get("shortname"), m);
//                }
//            }
//            rset.close();
//        } catch ( Exception e) {
//            DataSourceUtils.releaseConnection(con, ds);
//            throw e;
//        }
//        DataSourceUtils.releaseConnection(con, ds);
//	if(customer <= 0) {
//        	return list;
//	}
//	
//	LinkedHashMap<String,Map<String,Object>> nlist=new LinkedHashMap<String, Map<String,Object>>();
//	try	{
//		ProfileFieldDao fieldDao=(ProfileFieldDao) context.getBean("ProfileFieldDao");
//		Iterator	i=list.keySet().iterator();
//		while(i.hasNext()) {
//			String	key=(String) i.next();
//			Map	m=(Map) list.get(key);
//			String	col=(String) m.get("column");
//            ProfileField field=fieldDao.getProfileField(customer, col);
//
//			if(field != null) {
//				m.put("shortname", field.getShortname());
//				m.put("default", field.getDefaultValue());
//				m.put("description", field.getDescription());
//				m.put("editable", new Integer(field.getModeEdit()));
//				m.put("insertable", new Integer(field.getModeInsert()));
//			}
//			nlist.put((String)m.get("column"), m);
//		}			
//        } catch(Exception e) {
//            throw e;
//        }
//        // sort the columnlist by the shortname
//        LinkedHashMap<String, Map<String, Object>> sortedList = sortColumnListByShortName(nlist);               
//        return sortedList;
    }

//	protected static LinkedHashMap<String, Map<String, Object>> sortColumnListByShortName(
//			LinkedHashMap<String, Map<String, Object>> nlist) {
//		LinkedHashMap<String,Map<String,Object>> sortedList = new LinkedHashMap<String, Map<String,Object>>();
//        Map.Entry<String,Map<String,Object>>[]  nlistEntries = nlist.entrySet().toArray(new Map.Entry[0]);
//        Arrays.sort(nlistEntries, new Comparator<Map.Entry>() {
//
//			public int compare(Entry entry1, Entry entry2) {
//				String shortname1 = ((String) ((Map)entry1.getValue()).get("shortname")).toLowerCase();
//				String shortname2 = ((String) ((Map)entry2.getValue()).get("shortname")).toLowerCase();
//				return  shortname1.compareTo(shortname2);
//			}
//			
//		});
//		
//		for (Entry<String, Map<String, Object>> entry : nlistEntries) {
//			sortedList.put(entry.getKey(),entry.getValue());
//		}
//		return sortedList;
//	}


    /**
     * Shows column information.
     */
    @Override
    public int doStartTag() throws JspTagException {
        List<ProfileField> list=null;
        
        ApplicationContext aContext=WebApplicationContextUtils.getWebApplicationContext(this.pageContext.getServletContext());
        
        if(id==null) {
            id=new String("");
        }
        
        if(table==0) {
			this.table = AgnUtils.getAdmin(pageContext).getCompany().getId();
        }
        
        try {
            list=getColumnInfos(aContext, table);
        } catch (Exception e) {
            throw new JspTagException(e);
        }
        
        if(list.size() <= 0) {
            return SKIP_BODY;
        }
		pageContext.setAttribute("__" + id + "_data", list.iterator());
		pageContext.setAttribute("__" + id + "_map", list);
		pageContext.setAttribute("__" + id + "_colmap", list);
        try {
            return doAfterBody();
        }   catch ( Exception e) {
        	logger.error( "doStartTag", e);
        	
            throw new JspTagException("Error: " + e);
        }
    }
    
    /**
     * Sets attributes for pagecontext.
     */
    public int doAfterBody() throws JspException {
    	@SuppressWarnings("unchecked")
		Iterator<ProfileField> i = (Iterator<ProfileField>) pageContext.getAttribute("__" + id + "_data");
        
        try {
            while(i.hasNext()) {
				ProfileField v = i.next();
                
                if(sys_columns.contains(v.getColumn().toLowerCase())) {
                    continue;
                }
                
				pageContext.setAttribute("_" + id + "_column_name", v.getColumn());
				pageContext.setAttribute("_" + id + "_data_type", v.getDataType());
				pageContext.setAttribute("_" + id + "_data_length", Integer.toString(v.getDataTypeLength()));
				pageContext.setAttribute("_" + id + "_shortname", v.getShortname());
				if (v.getDefaultValue() != null) {
					pageContext.setAttribute("_" + id + "_data_default", v.getDefaultValue());
				} else {
					pageContext.setAttribute("_" + id + "_data_default", "");
				}
				pageContext.setAttribute("_" + id + "_editable", v.getModeEdit());
				pageContext.setAttribute("_" + id + "_insertable", v.getModeInsert());
				pageContext.setAttribute("_" + id + "_nullable", v.getNullable() ? 1 : 0);
				return EVAL_BODY_BUFFERED;
            }
        } catch (Exception e) {
        	logger.error( "doAfterBody", e);
        }
        return SKIP_BODY;
    }

	@Override
	public int doEndTag() throws JspException {
		this.table = 0;

		return super.doEndTag();
	}
}
