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
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.agnitas.beans.BindingEntry;
import org.agnitas.beans.CustomerImportStatus;
import org.agnitas.beans.ProfileField;
import org.agnitas.beans.Recipient;
import org.agnitas.beans.impl.PaginatedListImpl;
import org.agnitas.beans.impl.RecipientImpl;
import org.agnitas.dao.RecipientDao;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.CaseInsensitiveMap;
import org.agnitas.util.CsvColInfo;
import org.agnitas.util.SafeString;
import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.displaytag.pagination.PaginatedList;
import org.hibernate.dialect.Dialect;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 *
 * @author Nicole Serek, Andreas Rehak
 */
public class RecipientDaoImpl implements RecipientDao {
	private static final transient Logger logger = Logger.getLogger(RecipientDaoImpl.class);

	private static Integer maxRecipient = null;

	private int getMaxRecipient() {
		if (maxRecipient == null) {
			synchronized (this) {
				if (maxRecipient == null) {
					maxRecipient = new Integer(AgnUtils.getDefaultIntValue("recipient.maxRows"));
				}
			}
		}
		if (maxRecipient == null) {
			return 0;
		}
		return maxRecipient.intValue();
	}

	@Override
	public boolean mayAdd(int companyID, int count) {
		if(getMaxRecipient() != 0) {
			JdbcTemplate jdbc = new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
			String sql = "select count(customer_id) from customer_" + companyID + "_tbl";
			int current = jdbc.queryForInt(sql);
			int max = getMaxRecipient();

			if(max == 0 || current+count <= max) {
				return true;
			}
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean	isNearLimit(int companyID, int count) {
		if(getMaxRecipient() != 0) {
			JdbcTemplate jdbc = new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
			String sql = "select count(customer_id) from customer_" + companyID + "_tbl";
			int current=jdbc.queryForInt(sql);
			int max=(int) (getMaxRecipient()*0.9);

			if(max == 0 || current+count <= max) {
				return false;
			}
			return true;
		} else {
			return true;
		}
	}

    /**
     * Inserts new customer record in Database with a fresh customer-id
     *
     * @return true on success
     */
	@Override
    public int insertNewCust(Recipient cust) {
        StringBuffer Columns = new StringBuffer("(");
        StringBuffer Values = new StringBuffer(" VALUES (");
        String aColumn = null;
        String aParameter = null;
        String ColType = null;
        int intValue = 0;
        int day, month, year;
        int hour=0;
        int minute=0;
        int second=0;
        StringBuffer insertCust = new StringBuffer("INSERT INTO customer_" + cust.getCompanyID() + "_tbl ");
        boolean appendIt = false;
        boolean hasDefault = false;
        String appendColumn = null;
        String appendValue = null;
        NumberFormat aFormat1 = null;
        NumberFormat aFormat2 = null;

        if(cust.getCustDBStructure() == null) {
        	cust.loadCustDBStructure();
        }
        // logic from former method getNewCustomerID
        String sqlStatement = null;
        int customerID = 0;
        int companyID = cust.getCompanyID();
        if(companyID == 0) {
        	return customerID;
        }
        if(mayAdd(companyID, 1) == false) {
        	return customerID;
        }
        try {
        	// set customerID for Oracle
        	if(AgnUtils.isOracleDB()) {
                JdbcTemplate tmpl = new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
                sqlStatement = "select customer_" + companyID + "_tbl_seq.nextval FROM dual";
                customerID = tmpl.queryForInt(sqlStatement);
                cust.setCustomerID(customerID);
            }
        } catch (Exception e) {
        	logger.error( "Error inserting new customer", e);
            customerID = 0;

            return customerID;
        }

        if( logger.isDebugEnabled()) {
        	logger.debug("new customerID: "+ customerID);
        }

        // Oracle: put customerID in SQL statement at first
        // (MySQL: no customerID available, yet)
      	if(AgnUtils.isOracleDB()) {
      		Columns.append("customer_id");
      		Values.append(Integer.toString(cust.getCustomerID()));
      	}
        Iterator<String> i = cust.getCustDBStructure().keySet().iterator();
        while(i.hasNext()) {
            aColumn = i.next();
            ColType = cust.getCustDBStructure().get(aColumn);
            appendIt = false;
			hasDefault = false;
			if(!aColumn.equalsIgnoreCase("customer_id")) {
				if(aColumn.equalsIgnoreCase("creation_date") || aColumn.equalsIgnoreCase("timestamp") || aColumn.equalsIgnoreCase("change_date")) {
					appendValue = "current_timestamp";
					appendColumn = aColumn;
					appendIt = true;
				} else if(ColType.equalsIgnoreCase("DATE")) {
					if(cust.getCustParameters(aColumn + "_DAY_DATE") != null && cust.getCustParameters(aColumn + "_MONTH_DATE") != null && cust.getCustParameters(aColumn + "_YEAR_DATE") != null) {
						aFormat1 = new DecimalFormat("00");
						aFormat2 = new DecimalFormat("0000");
						try {
							if(!cust.getCustParameters(aColumn + "_DAY_DATE").trim().equals("")) {
								day = Integer.parseInt(cust.getCustParameters(aColumn+"_DAY_DATE"));
								month = Integer.parseInt(cust.getCustParameters(aColumn+"_MONTH_DATE"));
								year = Integer.parseInt(cust.getCustParameters(aColumn+"_YEAR_DATE"));
								if((cust.getCustParameters(aColumn + "_HOUR_DATE") != null) && !cust.getCustParameters(aColumn + "_HOUR_DATE").trim().equals("")) {
									hour = Integer.parseInt(cust.getCustParameters(aColumn+"_HOUR_DATE"));
								}
								if((cust.getCustParameters(aColumn + "_MINUTE_DATE") != null) && !cust.getCustParameters(aColumn + "_MINUTE_DATE").trim().equals("")) {
									minute = Integer.parseInt(cust.getCustParameters(aColumn+"_MINUTE_DATE"));
								}
								if((cust.getCustParameters(aColumn + "_SECOND_DATE") != null) && !cust.getCustParameters(aColumn + "_SECOND_DATE").trim().equals("")) {
									second = Integer.parseInt(cust.getCustParameters(aColumn+"_SECOND_DATE"));
								}

								if ( AgnUtils.isOracleDB() ) {
									appendValue = "to_date('"+ aFormat1.format(day) +"."+aFormat1.format(month)+"."+aFormat2.format(year)+" "+ aFormat1.format(hour)+":"+aFormat1.format(minute)+":"+aFormat1.format(second)+"', 'DD.MM.YYYY HH24:MI:SS')";
								} else {
									appendValue = "STR_TO_DATE('"+ aFormat1.format(day) +"-"+aFormat1.format(month)+"-"+aFormat2.format(year)+" "+ aFormat1.format(hour)+":"+aFormat1.format(minute)+":"+aFormat1.format(second)+"', '%d-%m-%Y %H:%i:%s')";
								}
								appendColumn = aColumn;
								appendIt = true;
							} else {
								ProfileField tmp = cust.getCustDBProfileStructure().get(aColumn);
								if (tmp != null) {
									String defaultValue = tmp.getDefaultValue();
									if (!StringUtils.isBlank(defaultValue)) {
										appendValue = createDateDefaultValueExpression( defaultValue);

										hasDefault = true;
									}
								}
								if (!hasDefault) {
									appendValue = "null";
								}
								appendColumn = aColumn;
								appendIt = true;
							}
						} catch (Exception e1) {
							logger.error("insertNewCust: (" + aColumn + ") " + e1.getMessage(), e1);
						}
					} else {
						ProfileField tmp = cust.getCustDBProfileStructure().get(aColumn);

						if (tmp != null) {
							String defaultValue = tmp.getDefaultValue();

							if (!StringUtils.isBlank(defaultValue)) {
								appendValue = createDateDefaultValueExpression( defaultValue);

								hasDefault = true;
							}
						}
						if (hasDefault) {
							appendColumn = aColumn;
							appendIt=true;
						}
					}
				}
				if(ColType.equalsIgnoreCase("INTEGER") || ColType.equalsIgnoreCase("DOUBLE")) {
					aParameter = cust.getCustParameters(aColumn);
					if(!StringUtils.isEmpty(aParameter)) {
						try {
							intValue = Integer.parseInt(aParameter);
						} catch (Exception e1) {
							intValue = 0;
						}
						appendValue = Integer.toString(intValue);
						appendColumn = aColumn;
						appendIt = true;
					} else {
						ProfileField tmp = cust.getCustDBProfileStructure().get( aColumn );

						if (tmp != null) {
							String defaultValue = tmp.getDefaultValue();

							if (!StringUtils.isBlank(defaultValue)) {
								appendValue = defaultValue;
								hasDefault = true;
							}
						}
						if (hasDefault) {
							appendColumn = aColumn;
							appendIt = true;
						}
					}
				}
				if(ColType.equalsIgnoreCase("VARCHAR") || ColType.equalsIgnoreCase("CHAR")) {
					aParameter = cust.getCustParameters(aColumn);
					if(!StringUtils.isEmpty(aParameter)) {
						appendValue = "'" + SafeString.getSQLSafeString(aParameter) + "'";
						appendColumn = aColumn;
						appendIt = true;
					} else {
						ProfileField tmp = cust.getCustDBProfileStructure().get(aColumn);
						if (tmp != null) {
							String defaultValue = tmp.getDefaultValue();
							if (!StringUtils.isBlank(defaultValue) ) {
								appendValue = "'" + defaultValue + "'";
								hasDefault = true;
							}
						}
						if (hasDefault) {
							appendColumn = aColumn;
							appendIt = true;
						}
					}
				}

				if(appendIt) {
					// if Columns contains more than "(", i.e. customerID was set
					if(!Columns.toString().equals("(")) {
						Columns.append(", ");
						Values.append(", ");
					}
					Columns.append(appendColumn.toLowerCase());
					Values.append(appendValue);
				}
			}
        }

        Columns.append(")");
        Values.append(")");

        insertCust.append(Columns.toString());
        insertCust.append(Values.toString());

        if(AgnUtils.isOracleDB()) {
        	try {
        		JdbcTemplate tmpl = new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
        		tmpl.execute(insertCust.toString());

        		if( logger.isDebugEnabled()) {
        			logger.debug("insertCust: "+insertCust.toString());
        		}
        	} catch (Exception e3) {
        		logger.error( "insertNewCustomer in Oracle", e3);
        		cust.setCustomerID(0);
        		return 0;
        	}
        } else {
        	try {
        		SqlUpdate sqlUpdate = new SqlUpdate((DataSource)this.applicationContext.getBean("dataSource"), insertCust.toString());
        		sqlUpdate.setReturnGeneratedKeys(true);
        		sqlUpdate.compile();
        		GeneratedKeyHolder key = new GeneratedKeyHolder();
        		sqlUpdate.update(null, key);
        		customerID = key.getKey().intValue();
        		cust.setCustomerID(customerID);
        	} catch (Exception e3) {
        		logger.error("insertNewCust in MySQL", e3);
        		cust.setCustomerID(0);
        		return 0;
        	}
        }

        return cust.getCustomerID();
    }

    /**
     * Updates Customer in DB. customerID must be set to a valid id, customer-data is taken from this.customerData
     *
     * @return true on success
     */
	@Override
    public boolean updateInDB(Recipient cust) {
        String currentTimestamp=AgnUtils.getSQLCurrentTimestampName();
        String aColumn;
        String colType = null;
        boolean appendIt = false;
        StringBuffer updateCust = new StringBuffer("UPDATE customer_" + cust.getCompanyID() + "_tbl SET " + AgnUtils.changeDateName() + "=" + currentTimestamp);
        NumberFormat aFormat1 = null;
        NumberFormat aFormat2 = null;
        int day, month, year;
        int hour=0;
        int minute=0;
        int second=0;
        String aParameter = null;
        int intValue;
        String appendValue = null;
        boolean result = true;

        if(cust.getCustDBStructure() == null) {
            cust.loadCustDBStructure();
        }

        if(cust.getCustomerID() == 0) {
        	if( logger.isInfoEnabled()) {
        		logger.info("updateInDB: creating new customer");
        	}

            if(this.insertNewCust(cust) == 0) {
                result = false;
            }
        } else {
            if(cust.isChangeFlag()) { // only if something has changed
                Iterator<String> i = cust.getCustDBStructure().keySet().iterator();
                while(i.hasNext()) {
                    aColumn = i.next();
                    colType = (String) cust.getCustDBStructure().get(aColumn);
                    appendIt = false;

                    if(aColumn.equalsIgnoreCase("customer_id") || aColumn.equalsIgnoreCase("change_date") || aColumn.equalsIgnoreCase("timestamp") || aColumn.equalsIgnoreCase("creation_date") || aColumn.equalsIgnoreCase("datasource_id")) {
                    	continue;
                    }

                    if(colType.equalsIgnoreCase("DATE")) {
                        if((cust.getCustParameters().get(aColumn + "_DAY_DATE") != null) && (cust.getCustParameters().get(aColumn + "_MONTH_DATE") != null) && (cust.getCustParameters().get(aColumn + "_YEAR_DATE") != null)) {
                            aFormat1 = new DecimalFormat("00");
                            aFormat2 = new DecimalFormat("0000");
                            try {
                                if(!((String) cust.getCustParameters().get(aColumn + "_DAY_DATE")).trim().equals("")) {
                                	day = Integer.parseInt((String) cust.getCustParameters().get(aColumn + "_DAY_DATE"));
                                    month = Integer.parseInt((String) cust.getCustParameters().get(aColumn + "_MONTH_DATE"));
                                    year = Integer.parseInt((String) cust.getCustParameters().get(aColumn + "_YEAR_DATE"));
                                    if((cust.getCustParameters().get(aColumn + "_HOUR_DATE") != null) && !cust.getCustParameters(aColumn + "_HOUR_DATE").trim().equals("")) {
                                    	hour = Integer.parseInt((String) cust.getCustParameters().get(aColumn + "_HOUR_DATE"));
                                    }
                                    if((cust.getCustParameters().get(aColumn + "_MINUTE_DATE") != null) && !cust.getCustParameters(aColumn + "_MINUTE_DATE").trim().equals("")) {
                                    	minute = Integer.parseInt((String) cust.getCustParameters().get(aColumn + "_MINUTE_DATE"));
                                    }
                                    if((cust.getCustParameters().get(aColumn + "_SECOND_DATE") != null) && !cust.getCustParameters(aColumn + "_SECOND_DATE").trim().equals("")) {
                                    	second = Integer.parseInt((String) cust.getCustParameters().get(aColumn + "_SECOND_DATE"));
                                    }
    								if (AgnUtils.isOracleDB()) {
                                        appendValue = aColumn.toLowerCase() + "=to_date('"+ aFormat1.format(day) +"."+aFormat1.format(month)+"."+aFormat2.format(year)+" "+ aFormat1.format(hour)+":"+aFormat1.format(minute)+":"+aFormat1.format(second)+"', 'DD.MM.YYYY HH24:MI:SS')";
                                    } else {
                                    	appendValue = aColumn.toLowerCase() + "=STR_TO_DATE('"+ aFormat1.format(day) +"-"+aFormat1.format(month)+"-"+aFormat2.format(year)+" "+ aFormat1.format(hour)+":"+aFormat1.format(minute)+":"+aFormat1.format(second)+"', '%d-%m-%Y %H:%i:%s')";
                                    }
                                    appendIt = true;
                                } else {
                            		appendValue = aColumn.toLowerCase() + "=null";
                                    appendIt = true;
                                }
                            } catch (Exception e1) {
                                logger.error("updateInDB: Could not parse Date "+aColumn + " because of "+e1.getMessage(), e1);
                            }
                        } else {
                            logger.error("updateInDB: Parameter missing!");
                        }
                    } else if(colType.equalsIgnoreCase("INTEGER")) {
                        aParameter = (String) cust.getCustParameters(aColumn);
                        if(!StringUtils.isEmpty(aParameter)){
                            try {
                                intValue = Integer.parseInt(aParameter);
                            } catch (Exception e1) {
                                intValue = 0;
                            }
                            appendValue = aColumn.toLowerCase() + "=" + intValue;
                            appendIt = true;
                        } else {
                    		appendValue = aColumn.toLowerCase() + "=null";
                            appendIt = true;
                        }
                    } else if(colType.equalsIgnoreCase("DOUBLE")) {
                        double dValue;
                        aParameter = (String) cust.getCustParameters(aColumn);
                        if(!StringUtils.isEmpty(aParameter)){
                            try {
                                dValue = Double.parseDouble(aParameter);
                            } catch(Exception e1) {
                                dValue = 0;
                            }
                            appendValue = aColumn.toLowerCase() + "=" + dValue;
                            appendIt = true;
                        } else {
                    		appendValue = aColumn.toLowerCase() + "=null";
                            appendIt = true;
                        }
                    } else /* if(colType.equalsIgnoreCase("VARCHAR") || colType.equalsIgnoreCase("CHAR"))*/ {
                        aParameter = (String) cust.getCustParameters(aColumn);
                        if(!StringUtils.isEmpty(aParameter)) {
                            appendValue = aColumn.toLowerCase() + "='" + SafeString.getSQLSafeString(aParameter) + "'";
                            appendIt = true;
                        } else {
                    		appendValue = aColumn.toLowerCase() + "=null";
                            appendIt = true;
                        }
                    }
                    if(appendIt) {
                        updateCust.append(", ");
                        updateCust.append(appendValue);
                    }
                }

                updateCust.append(" WHERE customer_id=" + cust.getCustomerID());
                try {
                    JdbcTemplate tmpl = new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));

                    if( logger.isInfoEnabled()) {
                    	logger.info("updateInDB: " + updateCust.toString());
                    }

                    tmpl.execute(updateCust.toString());

                    if(cust.getCustParameters("DATASOURCE_ID") != null) {
                    	String sql = "select datasource_id from customer_" + cust.getCompanyID() + "_tbl where customer_id = ?";
                    	@SuppressWarnings("unchecked")
						List<Map<String, Object>> list = tmpl.queryForList(sql, new Object[] {new Integer(cust.getCustomerID())});
                    	Iterator<Map<String, Object>> id = list.iterator();
                    	if(!id.hasNext()) {
                    		aParameter = (String) cust.getCustParameters("DATASOURCE_ID");
                    		if(!StringUtils.isEmpty(aParameter)){
                    			try {
                    				intValue = Integer.parseInt(aParameter);
                    				sql = "update customer_" + cust.getCompanyID() + "_tbl set datasource_id = " + intValue + " where customer_id = " + cust.getCustomerID();
                    				tmpl.execute(sql);

                    			} catch (Exception e1) {
                    				logger.error( "Error updating customer", e1);
                    			}
                    		}
                    	}
                    }
                } catch(Exception e3) {
                    // Util.SQLExceptionHelper(e3,dbConn);
                    logger.error("updateInDB: " + e3.getMessage(), e3);
                    result = false;
                }
            } else {
            	if( logger.isInfoEnabled()) {
            		logger.info("updateInDB: nothing changed");
            	}
            }
        }
        return result;
    }

    /**
     * Find Subscriber by providing a column-name and a value. Only exact machtes possible.
     *
     * @return customerID or 0 if no matching record found
     * @param col Column-Name
     * @param value Value to search for in col
     */
	@Override
    public int findByKeyColumn(Recipient cust, String col, String value) {
        int val = 0;
        String aType = null;
        String getCust = null;

        try {
            if(cust.getCustDBStructure() == null) {
                cust.loadCustDBStructure();
            }

            if ("email".equalsIgnoreCase(col)) {
                value = AgnUtils.normalizeEmail(value);
            }

            aType = (String) cust.getCustDBStructure().get(col);

            if(aType != null) {
                if(aType.equalsIgnoreCase("DECIMAL") || aType.equalsIgnoreCase("INTEGER") || aType.equalsIgnoreCase("DOUBLE")) {
                	try {
                        val = Integer.parseInt(value);
                    } catch (Exception e) {
                        val = 0;
                    }
                    getCust = "SELECT customer_id FROM customer_" + cust.getCompanyID() + "_tbl cust WHERE cust." + SafeString.getSQLSafeString(col, 30) + "=" + val;
                }

                if(aType.equalsIgnoreCase("VARCHAR") || aType.equalsIgnoreCase("CHAR")) {
                	getCust = "SELECT customer_id FROM customer_" + cust.getCompanyID() + "_tbl cust WHERE cust." + SafeString.getSQLSafeString(col, 30) + "='" + SafeString.getSQLSafeString(value) + "'";
                }

                if( logger.isInfoEnabled()) {
                	logger.info("RecipientDaoImpl:findByKeyColumn: "+getCust);
                }

                JdbcTemplate tmpl = new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
                // cannot use queryForInt, because of possible existing doublettes
                @SuppressWarnings("unchecked")
				List<Map<String,Integer>> custList = tmpl.queryForList(getCust);
                if(custList.size() > 0) {
                	Map<String, Object> map = new CaseInsensitiveMap<Object>(custList.get(0));
                	cust.setCustomerID(((Number) map.get("customer_id")).intValue());
                } else {
                	cust.setCustomerID(0);
                }
            }
        } catch (Exception e) {
        	logger.error( "findByKeyColumn (sql: " + getCust + ")", e);
            cust.setCustomerID(0);
        }
        return cust.getCustomerID();
    }

	@Override
    public int findByColumn(int companyID, String col, String value) {
    	Recipient cust = (Recipient) applicationContext.getBean("Recipient");
    	cust.setCompanyID(companyID);
        int custID = 0;
        int val = 0;
        String aType = null;
        String getCust = null;

        if(cust.getCustDBStructure() == null) {
            cust.loadCustDBStructure();
        }
        if(col.toLowerCase().equals("email")) {
            value=value.toLowerCase();
        }

        aType = (String) cust.getCustDBStructure().get(col.toLowerCase());

        if(aType != null) {
            if(aType.equalsIgnoreCase("VARCHAR") || aType.equalsIgnoreCase("CHAR")) {
                getCust = "select customer_id from customer_" + companyID + "_tbl cust where lower(cust." + SafeString.getSQLSafeString(col, 30) + ")=lower('" + SafeString.getSQLSafeString(value) + "')";
            } else {
                try {
                    val = Integer.parseInt(value);
                } catch (Exception e) {
                    val = 0;
                }
                getCust = "select customer_id from customer_" + companyID + "_tbl cust where cust."+SafeString.getSQLSafeString(col, 30)+"="+val;
            }
            try {
                JdbcTemplate tmpl = new JdbcTemplate((DataSource) this.applicationContext.getBean("dataSource"));
                //custID = tmpl.queryForInt(getCust);
                @SuppressWarnings("unchecked")
				List<Map<String, Object>> results = tmpl.queryForList(getCust);
                if (results.size() > 0) {
    				Map<String, Object> map = results.get(0);
    				custID = ((Number) map.get("customer_id")).intValue();
                }
            } catch (Exception e) {
                custID = 0;
            }
        }
        return custID;
    }

    /**
     * Find Subscriber by providing a username and password. Only exact machtes possible.
     *
     * @return customerID or 0 if no matching record found
     * @param userCol Column-Name for Username
     * @param userValue Value for Username
     * @param passCol Column-Name for Password
     * @param passValue Value for Password
     */
	@Override
    public int findByUserPassword(int companyID, String userCol, String userValue, String passCol, String passValue) {
        String getCust = null;
        int customerID = 0;

        if(userCol.toLowerCase().equals("email")) {
            userValue = userValue.toLowerCase();
        }

        getCust = "SELECT customer_id FROM customer_" + companyID + "_tbl cust WHERE cust."+SafeString.getSQLSafeString(userCol, 30)+"='"+SafeString.getSQLSafeString(userValue)+"' AND cust."+SafeString.getSQLSafeString(passCol, 30)+"='"+SafeString.getSQLSafeString(passValue)+"'";

        try {
            JdbcTemplate tmpl = new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
            customerID = tmpl.queryForInt(getCust);
        } catch (Exception e) {
        	logger.error( "findByUserPassword", e);
            customerID = 0;
        }
        return customerID;
    }

    /**
     * Load complete Subscriber-Data from DB. customerID must be set first for this method.
     *
     * @return Map with Key/Value-Pairs of customer data
     */
	@Override
    public CaseInsensitiveMap<Object> getCustomerDataFromDb(int companyID, int customerID) {
        String aName = null;
        String aValue = null;
        int a;
        java.sql.Timestamp aTime = null;
        Recipient cust = (Recipient) applicationContext.getBean("Recipient");

        if(cust.getCustParameters() == null) {
            cust.setCustParameters(new CaseInsensitiveMap<Object>());
        }

        String getCust = "SELECT * FROM customer_" + companyID + "_tbl WHERE customer_id=" + customerID;

        if(cust.getCustDBStructure() == null) {
            cust.loadCustDBStructure();
        }

        DataSource ds = (DataSource)this.applicationContext.getBean("dataSource");
        Connection con = DataSourceUtils.getConnection(ds);

        try {
            Statement stmt = con.createStatement();
            ResultSet rset = stmt.executeQuery(getCust);

            if( logger.isInfoEnabled()) {
            	logger.info("getCustomerDataFromDb: "+getCust);
            }

            if(rset.next()) {
                ResultSetMetaData aMeta = rset.getMetaData();

                for(a = 1; a <= aMeta.getColumnCount(); a++) {
                    aValue = null;
                    aName = aMeta.getColumnName(a).toLowerCase();
                    switch(aMeta.getColumnType(a)) {
                        case java.sql.Types.TIMESTAMP:
                        case java.sql.Types.TIME:
                        case java.sql.Types.DATE:
                        	try {
                        		aTime = rset.getTimestamp(a);
                        	} catch(Exception e) {
                        		aTime = null;
                        	}
                            if(aTime == null) {
                                cust.getCustParameters().put(aName + "_DAY_DATE", "");
                                cust.getCustParameters().put(aName + "_MONTH_DATE", "");
                                cust.getCustParameters().put(aName + "_YEAR_DATE", "");
                                cust.getCustParameters().put(aName + "_HOUR_DATE", "");
                                cust.getCustParameters().put(aName + "_MINUTE_DATE", "");
                                cust.getCustParameters().put(aName + "_SECOND_DATE", "");
                                cust.getCustParameters().put(aName, "");
                            } else {
                                GregorianCalendar aCal = new GregorianCalendar();
                                aCal.setTime(aTime);
                                cust.getCustParameters().put(aName + "_DAY_DATE", Integer.toString(aCal.get(GregorianCalendar.DAY_OF_MONTH)));
                                cust.getCustParameters().put(aName + "_MONTH_DATE", Integer.toString(aCal.get(GregorianCalendar.MONTH) + 1));
                                cust.getCustParameters().put(aName + "_YEAR_DATE", Integer.toString(aCal.get(GregorianCalendar.YEAR)));
                                cust.getCustParameters().put(aName + "_HOUR_DATE", Integer.toString(aCal.get(GregorianCalendar.HOUR_OF_DAY)));
                                cust.getCustParameters().put(aName + "_MINUTE_DATE", Integer.toString(aCal.get(GregorianCalendar.MINUTE)));
                                cust.getCustParameters().put(aName + "_SECOND_DATE", Integer.toString(aCal.get(GregorianCalendar.SECOND)));
                                SimpleDateFormat bdfmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                cust.getCustParameters().put(aName, bdfmt.format(aCal.getTime()));
                            }
                            break;

                        default:
                            aValue = rset.getString(a);
                            if(aValue == null) {
                                aValue = "";
                            }
                            cust.getCustParameters().put(aName, aValue);
                            break;
                    }
                }
            }
            rset.close();
            stmt.close();

        } catch (Exception e) {
            logger.error("getCustomerDataFromDb: " + getCust, e);
        	AgnUtils.sendExceptionMail("sql:" + getCust, e);
        }
        DataSourceUtils.releaseConnection(con, ds);
        cust.setChangeFlag(false);
        Map<String, Object> result = cust.getCustParameters();
        if (result instanceof CaseInsensitiveMap) {
        	return (CaseInsensitiveMap<Object>) result;
        } else {
        	return new CaseInsensitiveMap<Object>(result);
        }
    }

    /**
     * Delete complete Subscriber-Data from DB. customerID must be set first for this method.
     */
	@Override
    public void deleteCustomerDataFromDb(int companyID, int customerID) {
        String sql = null;
        Object[] params = new Object[] { new Integer(customerID) };

        try {
            JdbcTemplate tmpl = new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));

            sql = "DELETE FROM customer_" + companyID + "_binding_tbl WHERE customer_id=?";
            tmpl.update(sql, params);

            sql = "DELETE FROM customer_" + companyID + "_tbl WHERE customer_id=?";
            tmpl.update(sql, params);
        } catch (Exception e) {
            logger.error("deleteCustomerDataFromDb: " + sql, e);
        	AgnUtils.sendExceptionMail("sql:" + sql, e);
        }
    }

    /**
     * Loads complete Mailinglist-Binding-Information for given customer-id from Database
     *
     * @return Map with key/value-pairs as combinations of mailinglist-id and BindingEntry-Objects
     */
	@Override
    public Map<Integer, Map<Integer, BindingEntry>> loadAllListBindings(int companyID, int customerID) {
    	Recipient cust = (Recipient) applicationContext.getBean("Recipient");
        cust.setListBindings(new Hashtable<Integer, Map<Integer, BindingEntry>> ()); // MailingList_ID as keys
        Map<Integer, BindingEntry> mTable = new Hashtable<Integer, BindingEntry>(); // Media_ID as key, contains rest of data (user type, status etc.)
        String sqlGetLists = null;
        BindingEntry aEntry = null;
        int tmpMLID = 0;

        try {
            sqlGetLists = "SELECT mailinglist_id, user_type, user_status, user_remark, "+AgnUtils.changeDateName()+", mediatype FROM customer_" + companyID + "_binding_tbl WHERE customer_id=" +
                    customerID + " ORDER BY mailinglist_id, mediatype";
            JdbcTemplate tmpl = new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
            @SuppressWarnings("unchecked")
			List<Map<String, Object>> list = tmpl.queryForList(sqlGetLists);
            Iterator<Map<String, Object>> i = list.iterator();

            while(i.hasNext()) {
                Map<String, Object> map = i.next();
                int listID = ((Number) map.get("mailinglist_id")).intValue();
                Integer mediaType = new Integer(((Number) map.get("mediatype")).intValue());

                aEntry = (BindingEntry) applicationContext.getBean("BindingEntry");
                aEntry.setCustomerID(customerID);
                aEntry.setMailinglistID(listID);
                aEntry.setUserType((String) map.get("user_type"));
                aEntry.setUserStatus(((Number) map.get("user_status")).intValue());
                aEntry.setUserRemark((String) map.get("user_remark"));
                aEntry.setChangeDate((java.sql.Timestamp) map.get(AgnUtils.changeDateName()));
                aEntry.setMediaType(mediaType.intValue());

                if(tmpMLID != listID) {
                    if(tmpMLID != 0) {
                        cust.getListBindings().put(tmpMLID, mTable);
                        mTable = new Hashtable<Integer, BindingEntry>();
                        mTable.put(mediaType, aEntry);
                        tmpMLID = listID;
                    } else {
                        mTable.put(mediaType, aEntry);
                        tmpMLID = listID;
                    }
                } else {
                    mTable.put(mediaType, aEntry);
                }
            }
            cust.getListBindings().put(tmpMLID, mTable);
        } catch (Exception e) {
            logger.error("loadAllListBindings: " + sqlGetLists, e);
        	AgnUtils.sendExceptionMail("sql:" + sqlGetLists, e);
            return null;
        }
        return cust.getListBindings();
    }

    /**
     * Checks if E-Mail-Adress given in customerData-HashMap is registered in blacklist(s)
     *
     * @return true if E-Mail-Adress is blacklisted
     */
	@Override
    public boolean blacklistCheck(String email, int companyID) {
        boolean returnValue = false;
        String sqlSelect = null;

        try {
            JdbcTemplate tmpl = new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
            sqlSelect = "SELECT email FROM cust_ban_tbl WHERE '" + SafeString.getSQLSafeString(email) + "' LIKE email";
            @SuppressWarnings("unchecked")
			List<Map<String, Object>> list = tmpl.queryForList(sqlSelect);
            if(list.size() > 0) {
                returnValue=true;
            }
            if (AgnUtils.isProjectEMM()) {
            	sqlSelect = "SELECT email FROM cust"+ companyID + "_ban_tbl WHERE '" + SafeString.getSQLSafeString(email) + "' LIKE email";
            	@SuppressWarnings("unchecked")
				List<Map<String, Object>> list2 = tmpl.queryForList(sqlSelect);
            	if(list2.size() > 0) {
            		returnValue = true;
            	}
            }
        } catch (Exception e) {
        	logger.error( "blacklistCheck: " + sqlSelect, e);
        	AgnUtils.sendExceptionMail("sql:" + sqlSelect, e);
            returnValue = true;
        }
        return returnValue;
    }

    /*
     * Extract an int parameter from CustParameters
     *
     * @return the int value or the default value in case of an exception
     * @param column Column-Name
     * @param defaultValue Value to be returned in case of exception
     *
     * TODO: Method not used. Remove it, when nobody misses it (Support team?)
    private int extractInt(String column, int defaultValue, Recipient cust) {
		try {
		    return Integer.parseInt(cust.getCustParameters(column));
		} catch (Exception e1) {
		    return defaultValue;
		}
	}
    */

	@Override
	public String getField(String selectVal, int recipientID, int companyID)	{
		JdbcTemplate jdbc = new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
		String sql = "SELECT " + selectVal + " value FROM customer_" + companyID + "_tbl cust WHERE cust.customer_id=?";

		try {
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> list = jdbc.queryForList(sql, new Object[]{ new Integer(recipientID)});

			if(list.size() > 0) {
				Map<String, Object> map = list.get(0);
				Object temp = map.get("value");
				if(temp != null) {
					return temp.toString();
				}
			}
        } catch (Exception e) {
           	logger.error("processTag: " + sql, e);
        	AgnUtils.sendExceptionMail("sql:" + sql, e);
           	return null;
        }
		return "";
	}

	@Override
	public Map<Integer, Map<Integer, BindingEntry>> getAllMailingLists(int customerID, int companyID) {
		Map<Integer, Map<Integer, BindingEntry>> result = new HashMap<Integer, Map<Integer, BindingEntry>>();
		String sql = "SELECT mailinglist_id, user_type, user_status, user_remark, " + AgnUtils.changeDateName()+", mediatype FROM customer_" + companyID + "_binding_tbl WHERE customer_id=? ORDER BY mailinglist_id, mediatype";
		JdbcTemplate jdbc = new JdbcTemplate((DataSource) this.applicationContext.getBean("dataSource"));

		if( logger.isInfoEnabled()) {
			logger.info("getAllMailingLists: " + sql);
		}

		try	{
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> list = jdbc.queryForList(sql, new Object[]{new Integer(customerID)});
			Iterator<Map<String, Object>> i = list.iterator();
			BindingEntry entry = null;

			while(i.hasNext()) {
				Map<String, Object> map = i.next();
				int listID = ((Number) map.get("mailinglist_id")).intValue();
				int mediaType = ((Number) map.get("mediatype")).intValue();
				Map<Integer, BindingEntry> sub = result.get(new Integer(listID));

				if(sub == null) {
					sub = new HashMap<Integer, BindingEntry>();
				}
				entry = (BindingEntry) applicationContext.getBean("BindingEntry");
				entry.setCustomerID(customerID);
                entry.setMailinglistID(listID);
				entry.setUserType((String) map.get("user_type"));
				entry.setUserStatus(((Number) map.get("user_status")).intValue());
				entry.setUserRemark((String) map.get("user_remark"));
				entry.setChangeDate((java.sql.Timestamp) map.get(AgnUtils.changeDateName()));
				entry.setMediaType(mediaType);
				sub.put(new Integer(mediaType), entry);
				result.put(new Integer(listID), sub);
			}
		} catch(Exception e) {
			logger.error("getAllMailingLists (customer ID: " + customerID + "sql: " + sql + ")", e);
			AgnUtils.sendExceptionMail("sql:" + sql + ", " + customerID, e);
		}
		return result;
	}

	@Override
	public boolean createImportTables(int companyID, int datasourceID, CustomerImportStatus status) {
		JdbcTemplate jdbc = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		String prefix = "cust_" + companyID + "_tmp";
		String tabName = prefix+datasourceID+"_tbl";
		String keyIdx = prefix+datasourceID+"$KEYCOL$IDX";
		String custIdx = prefix+datasourceID+"$CUSTID$IDX";
		String sql = null;

		try {
			sql = "create temporary table " + tabName + " as (select * from customer_" + companyID + "_tbl where 1=0)";
			jdbc.execute(sql);

			sql = "alter table " + tabName + " modify change_date timestamp null default null";
			jdbc.execute(sql);

			sql = "alter table " + tabName + " modify creation_date timestamp null default current_timestamp";
			jdbc.execute(sql);

			sql = "create index " + keyIdx + " on " + tabName + " (" + SafeString.getSQLSafeString(status.getKeycolumn()) + ")";
			jdbc.execute(sql);

			sql = "create index " + custIdx +" on " + tabName + " (customer_id)";
			jdbc.execute(sql);
		}   catch (Exception e) {
			logger.error( "createTemporaryTables: " + sql, e);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean deleteImportTables(int companyID, int datasourceID) {
		JdbcTemplate jdbc = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
		String tabName = "cust_" + companyID + "_tmp" + datasourceID + "_tbl";

		if(AgnUtils.isOracleDB()) {
			try {
				jdbc.execute("drop table "+tabName);
			} catch (Exception e) {
				logger.error( "deleteTemporarytables (table: " + tabName + ")", e);
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/*
	 * Retrieves new Datasource-ID for newly imported Subscribers
	 *
	 * @return new Datasource-ID or 0
	 *
	 * TODO: Method not used, remove when nobody misses it (Support team?)
	private DatasourceDescription getNewDatasourceDescription(int companyID, String description) {
		HibernateTemplate tmpl = new HibernateTemplate((SessionFactory)applicationContext.getBean("sessionFactory"));
		DatasourceDescription dsDescription=(DatasourceDescription) applicationContext.getBean("DatasourceDescription");

		dsDescription.setId(0);
		dsDescription.setCompanyID(companyID);
		dsDescription.setSourcegroupID(2);
		dsDescription.setCreationDate(new java.util.Date());
		dsDescription.setDescription(description);
		tmpl.save("DatasourceDescription", dsDescription);
		return dsDescription;
	}
	 */


	@Override
	public int sumOfRecipients(int companyID, String target) {
        int recipients = 0;

        String sql = "select count(customer_id) from customer_" + companyID + "_tbl cust where " + target;
        try {
        	JdbcTemplate tmpl = new JdbcTemplate((DataSource) this.applicationContext.getBean("dataSource"));
            recipients = tmpl.queryForInt(sql);
        } catch (Exception e) {
            recipients = 0;
        }
        return recipients;
    }

	@Override
	public boolean deleteRecipients(int companyID, String target) {
		boolean returnValue = false;
		JdbcTemplate tmpl = new JdbcTemplate((DataSource) this.applicationContext.getBean("dataSource"));
		String sql;

		sql= "DELETE FROM customer_" + companyID + "_binding_tbl WHERE customer_id in (select customer_id from customer_" + companyID + "_tbl cust where " + target + ")";
        try {
        	tmpl.execute(sql);
        } catch (Exception e) {
        	logger.error("error deleting recipient bindings", e);
        	returnValue = false;
        }

        sql = "delete ";
        if(AgnUtils.isMySQLDB()) {
        	sql = sql + "cust ";
        }
        sql = sql + "from customer_" + companyID + "_tbl cust where " + target;
        try {
        	tmpl.execute(sql);
        	returnValue = true;
        } catch (Exception e) {
        	logger.error("error deleting recipients", e);
        	returnValue = false;
        }
        return returnValue;
    }

	@Override
	public PaginatedList getRecipientList(Set<String> columns, String sqlStatementForCount, String sqlStatementForRows, String sort, String direction,
			int page, int rownums, int previousFullListSize) throws IllegalAccessException, InstantiationException {
		return getRecipientList(columns, sqlStatementForCount, null, sqlStatementForRows, null, sort, direction, page, rownums, previousFullListSize);
	}

	@Override
	public PaginatedListImpl<DynaBean> getRecipientList(Set<String> columns, String sqlStatementForCount, Object[] parametersForsCount, String sqlStatementForRows, Object[] parametersForsRows, String sort, String direction,
			int page, int rownums, int previousFullListSize) throws IllegalAccessException, InstantiationException {
		JdbcTemplate aTemplate = new JdbcTemplate((DataSource) this.applicationContext.getBean("dataSource"));
		int totalRows = aTemplate.queryForInt(sqlStatementForCount, parametersForsCount);
		if (previousFullListSize == 0 || previousFullListSize != totalRows) {
			page = 1;
		}
        page = AgnUtils.getValidPageNumber(totalRows, page, rownums);

		String sortClause = "";
		if (!StringUtils.isBlank(sort)) {
			sortClause = " ORDER BY " + "lower(" + sort + ")" ;
			if (!StringUtils.isEmpty(direction)) {
				sortClause = sortClause + " " + direction;
			}
		}

		int offset = (page - 1) * rownums;

		if (AgnUtils.isOracleDB()) {
			sqlStatementForRows = "SELECT * from ( select " + StringUtils.join(columns, ", ") + ", rownum r from ( " + sqlStatementForRows + " )  where 1 = 1 " + sortClause
					+ ") where r between " + (offset + 1) + " and " + (offset + rownums);
		} else {
			sqlStatementForRows = sqlStatementForRows + sortClause + " LIMIT  " + offset + " , " + rownums;
		}

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> tmpList = aTemplate.queryForList(sqlStatementForRows, parametersForsRows);
		List<DynaBean> result = new ArrayList<DynaBean>();
		if (tmpList != null && !tmpList.isEmpty()) {
			DynaProperty[] properties = new DynaProperty[columns.size()];
			int i = 0;
			for (String c : columns) {
				properties[i++] = new DynaProperty(c.toLowerCase(), String.class);
			}
			BasicDynaClass dynaClass = new BasicDynaClass("recipient", null, properties);

			for (Map<String, Object> row : tmpList) {
				DynaBean bean = dynaClass.newInstance();
				for (String c : columns) {
					bean.set(c.toLowerCase(), row.get(c.toUpperCase()) != null ? row.get(c.toUpperCase()).toString() : "");
				}
				result.add(bean);
			}
		}

		PaginatedListImpl<DynaBean> paginatedList = new PaginatedListImpl<DynaBean>(result, totalRows, rownums, page, sort, direction);
		return paginatedList;
	}

	/*
	 * TODO: Method is not used. Remove, when nobody misses it (Support team?)
	private String getUpperSort(List<String> charColumns, String sort) {
		String upperSort = sort;
		if (charColumns.contains( sort )) {
	    	upperSort =   "upper( " +sort + " )";
	     }
		return upperSort;
	}
	 */



	/**
	 * Holds value of property applicationContext.
	 */

    public void deleteAllNoBindings(int companyID, String toBeDeletedTable) {
        JdbcTemplate tmpl = new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
        String delete =
                "delete from customer_"+companyID+"_tbl " +
                    "where customer_id not in (" +
                        "select customer_id from customer_" + companyID + "_binding_tbl" +
                    ") " +
                    "and customer_id in (select * from "+toBeDeletedTable+")";

        tmpl.update(delete);
        tmpl.execute("drop table " + toBeDeletedTable);
    }

    public String createTmpTableByMailinglistID(int companyID, int mailinglistID) {
        String tableName = "tmp_" +String.valueOf(System.currentTimeMillis())+ "_delete_tbl";

        JdbcTemplate tmpl = new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
        String sql =
                "create table " + tableName + " as (" +
                        "select customer_id from customer_"+companyID+"_tbl where customer_id in (" +
                            "select customer_id from customer_"+companyID+"_binding_tbl where mailinglist_id = " + mailinglistID +
                        ")" +
                ")";
        tmpl.execute(String.format(sql, mailinglistID));
        return tableName;
    }

    public void deleteRecipientsBindings(int mailinglistID, int companyID, boolean activeOnly, boolean notAdminsAndTests) {
        JdbcTemplate jdbc = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));

        String delete = "delete from customer_"+companyID+"_binding_tbl";
        String where  = "where mailinglist_id = ?";
        StringBuffer sql = new StringBuffer(delete).append(" ").append(where);

        if(activeOnly){
            sql.append(" ").append(String.format("and user_status = %d", BindingEntry.USER_STATUS_ACTIVE));
        }
        if(notAdminsAndTests){
            sql.append(" ").append(String.format("and user_type <> '%s' and user_type <> '%s' and user_type <> '%s'",
                    BindingEntry.USER_TYPE_ADMIN,
                    BindingEntry.USER_TYPE_TESTUSER,
                    BindingEntry.USER_TYPE_TESTVIP));
        }

        jdbc.update(sql.toString(), new Object[]{new Integer(mailinglistID)});
    }

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
    public CaseInsensitiveMap<CsvColInfo> readDBColumns(int companyID) {
        String sqlGetTblStruct = "SELECT * FROM customer_" + companyID + "_tbl WHERE 1=0";
        CsvColInfo aCol = null;
        int colType;
        CaseInsensitiveMap<CsvColInfo> dbAllColumns = new CaseInsensitiveMap<CsvColInfo>();
        DataSource ds = (DataSource) this.applicationContext.getBean("dataSource");
        Connection con = DataSourceUtils.getConnection(ds);
        try {
            Statement stmt = con.createStatement();
            ResultSet rset = stmt.executeQuery(sqlGetTblStruct);
            ResultSetMetaData meta = rset.getMetaData();

            for (int i = 1; i <= meta.getColumnCount(); i++) {
                if (!meta.getColumnName(i).equals("change_date")
                        && !meta.getColumnName(i).equals("creation_date")
                        && !meta.getColumnName(i).equals("datasource_id")) {
//						if (meta.getColumnName(i).equals("customer_id")) {
//							if (status == null) {
//								initStatus(getWebApplicationContext());
//							}
//							if (!( mode == ImportWizardServiceImpleImpl.MODE_ONLY_UPDATE && status.getKeycolumn().equals("customer_id"))) {
//								continue;
//							}
//						}

                    aCol = new CsvColInfo();
                    aCol.setName(meta.getColumnName(i));
                    aCol.setLength(meta.getColumnDisplaySize(i));
                    aCol.setType(CsvColInfo.TYPE_UNKNOWN);
                    aCol.setActive(false);
                    aCol.setNullable(meta.isNullable(i) != 0);

                    colType = meta.getColumnType(i);
                    aCol.setType(dbTypeToCsvType(colType));
                    dbAllColumns.put(meta.getColumnName(i), aCol);
                }
            }
            rset.close();
            stmt.close();
        } catch (Exception e) {
            logger.error("readDBColumns (companyID: " + companyID + ")", e);
        }
        DataSourceUtils.releaseConnection(con, ds);
        return dbAllColumns;
    }

	private static int dbTypeToCsvType(int type) {
		switch (type) {
		case java.sql.Types.BIGINT:
		case java.sql.Types.INTEGER:
		case java.sql.Types.SMALLINT:
		case java.sql.Types.DECIMAL:
		case java.sql.Types.DOUBLE:
		case java.sql.Types.FLOAT:
		case java.sql.Types.NUMERIC:
		case java.sql.Types.REAL:
			return CsvColInfo.TYPE_NUMERIC;

		case java.sql.Types.CHAR:
		case java.sql.Types.VARCHAR:
		case java.sql.Types.LONGVARCHAR:
		case java.sql.Types.CLOB:
			return CsvColInfo.TYPE_CHAR;

		case java.sql.Types.DATE:
		case java.sql.Types.TIMESTAMP:
		case java.sql.Types.TIME:
			return CsvColInfo.TYPE_DATE;

		default:
			return CsvColInfo.TYPE_UNKNOWN;
		}
	}

	@Override
	public Set<String> loadBlackList(int companyID) throws Exception {
		JdbcTemplate jdbcTemplate = new JdbcTemplate( (DataSource) applicationContext.getBean("dataSource"));
		SqlRowSet rset = null;
		Object[] params = new Object[] { new Integer(companyID) };
	    Set<String> blacklist = new HashSet<String>();
	    try {
	       rset = jdbcTemplate.queryForRowSet("SELECT email FROM cust_ban_tbl WHERE company_id=? OR company_id=0", params);
	     	while (rset.next()) {
	     		blacklist.add(rset.getString(1).toLowerCase());
	     	}
	    } catch (Exception e) {
	       logger.error("loadBlacklist (company ID: " + companyID + ")", e);
	       throw e;
	    }

	    return blacklist;
	}

	@Override
    public Map<Integer, String> getAdminAndTestRecipientsDescription(int companyId, int mailingId) {
        String sql = "SELECT bind.customer_id, cust.email, cust.firstname, cust.lastname FROM mailing_tbl mail, " +
                "customer_" + companyId + "_tbl cust, customer_" + companyId + "_binding_tbl bind WHERE " +
                "bind.user_type in ('A', 'T') AND bind.user_status=1 AND bind.mailinglist_id=" +
                "mail.mailinglist_id AND bind.customer_id=cust.customer_id and mail.mailing_id=" + mailingId +
                " ORDER BY bind.user_type, bind.customer_id";
        JdbcTemplate jdbcTemplate = new JdbcTemplate((DataSource) applicationContext.getBean("dataSource"));
        @SuppressWarnings("unchecked")
		List<Map<String, Object>> tmpList = jdbcTemplate.queryForList(sql);
        HashMap<Integer, String> result = new HashMap<Integer, String>();
        for (Map<String, Object> map : tmpList) {
            int id = ((Number) map.get("customer_id")).intValue();
            String email = (String) map.get("email");
            String firstName = (String) map.get("firstname");
            String lastName = (String) map.get("lastname");

            if( firstName == null)
            	firstName = "";

            if( lastName == null)
            	lastName = "";

            result.put(id, firstName + " " + lastName + " &lt;" + email + "&gt;");
        }
        return result;
    }

    @Override
    public List<Recipient> getBouncedMailingRecipients(int companyId, int mailingId) {
        String sqlStatement = "select cust.email as email, cust.firstname as firstname, cust.lastname as lastname, cust.gender as gender " +
                "from customer_" + companyId + "_binding_tbl bind, customer_" + companyId + "_tbl cust	where bind.customer_id=cust.customer_id and exit_mailing_id = ? and user_status = 2 " +
                "and mailinglist_id=(select mailinglist_id from mailing_tbl where mailing_id = ?)";

        JdbcTemplate template = new JdbcTemplate((DataSource) this.applicationContext.getBean("dataSource"));
        @SuppressWarnings("unchecked")
		List<Map<String, Object>> tmpList = template.queryForList(sqlStatement, new Object [] {mailingId, mailingId});
        List<Recipient> result = new ArrayList<Recipient>();
        for (Map<String, Object> row : tmpList) {
            Recipient newBean = new RecipientImpl();
            Map<String, Object> customerData = new HashMap<String, Object>();

            customerData.put("gender", row.get("GENDER"));
            customerData.put("firstname", row.get("FIRSTNAME"));
            customerData.put("lastname", row.get("LASTNAME"));
            customerData.put("email", row.get("EMAIL"));

            newBean.setCustParameters(customerData);

            result.add(newBean);
        }
        return result;
    }

    /**
     * Gets new customerID from Database-Sequence an stores it in member-variable "customerID"
     *
     * @return true on success
     */
	@Override
    public int getNewCustomerID(int companyID) {
        String sqlStatement = null;
        int customerID = 0;
        Dialect dialect = AgnUtils.getHibernateDialect();

        if(companyID == 0) {
        	return customerID;
        }
        if(mayAdd(companyID, 1) == false) {
        	return customerID;
        }
        try {
            if(dialect.supportsSequences()) {
                JdbcTemplate tmpl = new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
                sqlStatement = "select customer_" + companyID + "_tbl_seq.nextval FROM dual";
                customerID = tmpl.queryForInt(sqlStatement);
            } else {
                sqlStatement = "insert into customer_" + companyID + "_tbl_seq () values ()";
                SqlUpdate updt = new SqlUpdate((DataSource)this.applicationContext.getBean("dataSource"), sqlStatement);
                updt.setReturnGeneratedKeys(true);
                GeneratedKeyHolder key = new GeneratedKeyHolder();
                customerID = updt.update(null, key);
                customerID = key.getKey().intValue();
            }
        } catch (Exception e) {
            customerID = 0;
            System.err.println("Exception:" + e);
            System.err.println(AgnUtils.getStackTrace(e));
        }

        if( logger.isDebugEnabled()) {
        	logger.debug("new customerID: "+ customerID);
        }

        return customerID;
    }

	@Override
	public void deleteRecipients(int companyID, List<Integer> list) {
		if (list == null || list.size() < 1) {
			throw new RuntimeException("Invalid customerID list size");
		}
		StringBuilder sb = new StringBuilder("WHERE customer_id in (");
		for (Integer customerId : list) {
			sb.append(customerId);
			sb.append(",");
		}
		sb.setCharAt(sb.length() - 1, ')');
		String where = sb.toString();

		sb = new StringBuilder("DELETE FROM customer_");
		sb.append(companyID);
		sb.append("_binding_tbl ");
		sb.append(where);
		String bindingQuery = sb.toString();

		sb = new StringBuilder("DELETE FROM customer_");
		sb.append(companyID);
		sb.append("_tbl ");
		sb.append(where);
		String customerQuery = sb.toString();

		JdbcTemplate tmpl = new JdbcTemplate((DataSource) this.applicationContext.getBean("dataSource"));
		tmpl.batchUpdate(new String[] {bindingQuery, customerQuery});
	}

	@Override
	public boolean exist(int customerId, int companyId) {
		JdbcTemplate jdbc = new JdbcTemplate((DataSource)this.applicationContext.getBean("dataSource"));
		String sql = "select count(*) from customer_" + companyId + "_tbl where customer_id = ?";
		return jdbc.queryForInt(sql, new Object[]{ customerId }) > 0;

	}

	protected String createDateDefaultValueExpression( String defaultValue) {
		if( defaultValue.toLowerCase().equals( "now()")) {
			return AgnUtils.getSQLCurrentTimestampName();
		} else {
    		if ( AgnUtils.isOracleDB() ) {
    			return "to_date('" + defaultValue + "', 'DD.MM.YYYY HH24:MI:SS')";
    		} else {
    			return "STR_TO_DATE('" + defaultValue + "', '%d-%m-%Y')";
    		}
		}
	}
}
