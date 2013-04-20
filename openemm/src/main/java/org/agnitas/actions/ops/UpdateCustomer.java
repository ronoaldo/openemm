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

package org.agnitas.actions.ops;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.agnitas.actions.ActionOperation;
import org.agnitas.util.AgnUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author Martin Helff
 */
public class UpdateCustomer extends ActionOperation implements Serializable {
    
    static final long serialVersionUID = -5598100419105432642L;
    
    protected static final int TYPE_INCREMENT_BY=1;
    protected static final int TYPE_DECREMENT_BY=2;
    protected static final int TYPE_SET_VALUE=3;
    
    // protected String updateStatement;
    
    /** 
     * Holds value of property columnName. 
     */
    protected String columnName;
    
    /**
     * Holds value of property updateType. 
     */
    protected int updateType;
    
    /**
     * Holds value of property updateValue. 
     */
    protected String updateValue;
    
    /**
     * Holds value of property columnType.
     */
    protected String columnType;
    
    /**
     * Creates new ActionOperationUpdateCustomer 
     */
    public UpdateCustomer() {
        columnName = "gender";
        updateType=TYPE_INCREMENT_BY;
        updateValue = "0";
    }
    
    /**
     * Declaration of decrement operator, increment operator
     * and equal operator depending on the column type
     * ...
     */
    public Object[] buildUpdateStatement(int companyID, String uValue) {
    	Object value = null;
    	
        String decOp=null;
        String incOp=null;
        String eqOp=null;
    	
        StringBuffer updateStatement = new StringBuffer("UPDATE customer_");
        updateStatement.append(companyID);
        updateStatement.append("_tbl SET change_date=");
        updateStatement.append(AgnUtils.getHibernateDialect().getCurrentTimestampSQLFunctionName());
        updateStatement.append(", ");
        
        if (this.columnType.equalsIgnoreCase("INTEGER")) {
            decOp = this.columnName + " - ?";
            incOp = this.columnName + " + ?";
            eqOp = "?";
        } else if (this.columnType.equalsIgnoreCase("DOUBLE")) {	
            decOp = this.columnName + " - ?";
            incOp = this.columnName + " + ?";
            eqOp = "?";
        } else  if (this.columnType.equalsIgnoreCase("CHAR") || this.columnType.equalsIgnoreCase("VARCHAR")) {
            decOp = this.columnName + " - ?";
            incOp = "concat(" + this.columnName + ", ?)";
            eqOp = "?";
        } else if (this.columnType.equalsIgnoreCase("DATE")) {
            decOp = this.columnName + " - ?";
            incOp = this.columnName + " + ?";
            eqOp = "?";
        }
        
        updateStatement.append(this.columnName);
        switch(this.updateType) {
            case TYPE_INCREMENT_BY:
                updateStatement.append("=");
                updateStatement.append(incOp);
                break;
                
            case TYPE_DECREMENT_BY:
                updateStatement.append("=");
                updateStatement.append(decOp);
                break;
                
            case TYPE_SET_VALUE:
                updateStatement.append("=");
                
                // Assignment for type "date" is handled in a special way in the next "if"-block
                if(!this.columnType.equalsIgnoreCase("DATE"))
                	updateStatement.append(eqOp);
                break;
        }
        
        if (this.columnType.equalsIgnoreCase("INTEGER")) {
        	try {
        		value = Integer.parseInt(this.updateValue);
        	} catch (Throwable t) {
        		value = 0.0;
        	}
        } else if (this.columnType.equalsIgnoreCase("DOUBLE")) {
        	try {
        		value = Double.parseDouble(this.updateValue);
        	} catch (Throwable t) {
        		value = 0.0;
        	}
        } else if (this.columnType.equalsIgnoreCase("CHAR") || this.columnType.equalsIgnoreCase("VARCHAR")) {
        	value = this.updateValue;
        } else if (this.columnType.equalsIgnoreCase("DATE")) {
            if (this.updateType==TYPE_INCREMENT_BY || this.updateType==TYPE_DECREMENT_BY) {
            	try {
            		value = Double.parseDouble(this.updateValue);
            	} catch (Throwable t) {
            		value = 0.0;
            	}
            } else {
                if (uValue.startsWith("sysdate")) {
                	value = null;
                    updateStatement.append(AgnUtils.getHibernateDialect().getCurrentTimestampSQLFunctionName());
                } else {
                	value = this.updateValue;
                    updateStatement.append("?");
                }
            }
        }
        
        updateStatement.append(" WHERE customer_id = ?");
        
        return new Object[]{ updateStatement.toString(), value};
    }
    
    /** Getter for property columnName.
     *
     * @return Value of property columnName.
     */
    public String getColumnName() {
        return columnName;
    }
    
    /** Setter for property columnName.
     *
     * @param columnName New value of property columnName.
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
    
    /** Getter for property updateType.
     *
     * @return Value of property updateType.
     */
    public int getUpdateType() {
        return updateType;
    }
    
    /** Setter for property updateType.
     *
     * @param updateType New value of property updateType.
     */
    public void setUpdateType(int updateType) {
        if(updateType<1 || updateType>3) {
            updateType=1;
        }
         this.updateType = updateType;

    }
    
    /** Getter for property updateValue.
     *
     * @return Value of property updateValue.
     */
    public String getUpdateValue() {
        return updateValue;
    }
    
    /** Setter for property updateValue.
     *
     * @param updateValue New value of property updateValue.
     */
    public void setUpdateValue(String updateValue) {
        this.updateValue = updateValue;
    }
    
    /**
     * Reads an Object and puts the read fields into allFields
     * Gets columnName, updateType, updateValue and columnType from allFields
     * throws IOException or ClassNotFoundException
     *
     * @param in inputstream from Object
     */
    private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField allFields=null;
        
        allFields=in.readFields();
        this.columnName=(String)allFields.get("columnName", "default");
        this.updateType=allFields.get("updateType", 1);
        this.updateValue=(String)allFields.get("updateValue", "0");
        this.columnType=(String)allFields.get("columnType", "");
    }
    
    /**
     * Checks if customer id is filled
     * Builds an UPDATE-statement for a customer
     * Tries to execute this SQL-statement
     *
     * @return true==sucess
     * false=error
     * @param con 
     * @param companyID 
     * @param params HashMap containing all available informations
     */
    public boolean executeOperation(ApplicationContext con, int companyID, Map params) {
        int customerID = 0;
        boolean exitValue=true;
        JdbcTemplate tmpl=AgnUtils.getJdbcTemplate(con);
        
        if(params.get("customerID")==null) {
            return false;
        }
        
        customerID = (Integer)params.get("customerID");
        
        Object[] sqlData = buildUpdateStatement(companyID, generateUpdateValue(params));
        
        try {
        	if (sqlData[1] == null)
        		tmpl.update(sqlData[0].toString(), new Object[]{ customerID });
        	else
        		tmpl.update(sqlData[0].toString(), new Object[] { sqlData[1], customerID });
        } catch (Exception e) {
        	AgnUtils.sendExceptionMail("SQL: " + sqlData[0], e);
            AgnUtils.logger().error("executeOperation: " + e);
            AgnUtils.logger().error("SQL: " + sqlData[0]);
            AgnUtils.logger().error("  param: " + sqlData[1]);
            exitValue=false;
        }
        
        return exitValue;
    }
    
    /**
     * Generates the values for the update
     *
     * @param params HashMap containing all available informations
     */
    protected String generateUpdateValue(Map params) {
        Matcher aMatcher=null;
        Pattern aRegExp=Pattern.compile("##[^#]+##");
        StringBuffer aBuf=new StringBuffer(this.updateValue);
        String tmpString=null;
        String tmpString2=null;
        
        try {
            // aRegExp=new RE("##[^#]+##");
            aMatcher=aRegExp.matcher(aBuf);
            while(aMatcher.find()) {
                tmpString=aBuf.toString().substring(aMatcher.start()+2, aMatcher.end()-2);
                tmpString2 = "";
                if(params.get(tmpString)!=null) {
                    tmpString2=params.get(tmpString).toString();
                }
                aBuf.replace(aMatcher.start(), aMatcher.end(), tmpString2);
                aMatcher=aRegExp.matcher(aBuf);
            }
        } catch (Exception e) {
            AgnUtils.logger().error("generateUpdateValue: "+e);
        }
        return aBuf.toString();
    }
    
    /**
     * Getter for property columnType.
     *
     * @return Value of property columnType.
     */
    public String getColumnType() {
        return this.columnType;
    }
    
    /**
     * Setter for property columnType.
     *
     * @param columnType New value of property columnType.
     */
    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }
    
    /**
     * Getter for property nameType.
     *
     * @return Value of property nameType.
     */
    public String getNameType() {
        return this.columnName + "#" + this.columnType;
    }
    
    /**
     * Setter for property nameType.
     *
     * @param nameType New value of property nameType.
     */
    public void setNameType(String nameType) {
        this.columnType = nameType.substring(nameType.indexOf('#')+1);
        this.columnName = nameType.substring(0, nameType.indexOf('#'));
    }
}
