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

package org.agnitas.target.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.agnitas.target.TargetNode;
import org.agnitas.target.TargetOperator;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.SafeString;

/**
 *
 * @author  mhe
 */
public class TargetNodeString extends TargetNode implements Serializable {
    
    //    public static char columnType='C';
    
    private static final long serialVersionUID = -5363353927700548241L;
    
    /** Holds value of property openBracketBefore. */
    protected boolean openBracketBefore;
    
    /** Holds value of property closeBracketAfter. */
    protected boolean closeBracketAfter;
    
    /** Holds value of property chainOperator. */
    protected int chainOperator;
    
    /** Holds value of property primaryOperator. */
    protected int primaryOperator;
    
    /** Holds value of property primaryField. */
    protected String primaryField;
    
    /** Holds value of property primaryFieldType. */
    protected String primaryFieldType;
    
    /** Holds value of property primaryValue. */
    protected String primaryValue;
    
    /** Creates a new instance of TargetNodeString */
    public TargetNodeString() {
    	this.initializeOperatorLists();
    }
    
    public static TargetOperator[] getValidOperators() {
    	return new TargetOperator[] {
            	OPERATOR_EQ, 
            	OPERATOR_NEQ, 
            	OPERATOR_GT, 
            	OPERATOR_LT, 
            	OPERATOR_LIKE, 
            	OPERATOR_NLIKE, 
            	null, 
            	OPERATOR_IS, 
            	OPERATOR_LT_EQ, 
            	OPERATOR_GT_EQ
            	};
    }
    
    @Override
	protected void initializeOperatorLists() {
        TYPE_OPERATORS = TargetNodeString.getValidOperators();
	}

	public String generateSQL() {
        StringBuffer tmpSQL=new StringBuffer("");
        
        switch(this.chainOperator) {
            case TargetNode.CHAIN_OPERATOR_AND:
                tmpSQL.append(" AND ");
                break;
            case TargetNode.CHAIN_OPERATOR_OR:
                tmpSQL.append(" OR ");
                break;
            default:
                tmpSQL.append(" ");
        }
        
        if(this.openBracketBefore) {
            tmpSQL.append("(");
        }

		StringBuffer mainSQL = new StringBuffer();

		if(this.primaryOperator!=TargetNode.OPERATOR_IS.getOperatorCode()) {
            mainSQL.append("lower(cust.");
        } else {
            mainSQL.append("cust.");
        }
        mainSQL.append(this.primaryField);
        if(this.primaryOperator!=TargetNode.OPERATOR_IS.getOperatorCode()) {
            mainSQL.append(") ");
        } else {
            mainSQL.append(" ");
        }
        mainSQL.append(this.TYPE_OPERATORS[this.primaryOperator-1].getOperatorSymbol());
        if(this.primaryOperator!=TargetNode.OPERATOR_IS.getOperatorCode()) {
            mainSQL.append(" lower('");
        } else {
            mainSQL.append(" ");
        }
        mainSQL.append(SafeString.getSQLSafeString(this.primaryValue));
        if(this.primaryOperator!=TargetNode.OPERATOR_IS.getOperatorCode()) {
            mainSQL.append("')");
        } else {
            mainSQL.append(" ");
        }

		if(AgnUtils.isMySQLDB() && this.primaryOperator == TargetNode.OPERATOR_IS.getOperatorCode() &&
				("null".equals(primaryValue) || "not null".equals(primaryValue))) {
			String compareString = "null".equals(primaryValue) ? "=''" : "<>''";
			String mainStr = mainSQL.toString();
			mainSQL = new StringBuffer();
			mainSQL.append("(");
			mainSQL.append(mainStr).append(" OR cust.").append(this.primaryField).append(compareString);
			mainSQL.append(")");
		}

		tmpSQL.append(mainSQL);

        if(this.closeBracketAfter) {
            tmpSQL.append(")");
        }

        return tmpSQL.toString();
    }
    
    public String generateBsh() {
        StringBuffer tmpBsh=new StringBuffer("");
        
        switch(this.chainOperator) {
            case TargetNode.CHAIN_OPERATOR_AND:
                tmpBsh.append(" && ");
                break;
            case TargetNode.CHAIN_OPERATOR_OR:
                tmpBsh.append(" || ");
                break;
            default:
                tmpBsh.append(" ");
        }
        
        if(this.openBracketBefore) {
            tmpBsh.append("(");
        }
        
        if ((this.primaryOperator == TargetNode.OPERATOR_LIKE.getOperatorCode()) || (this.primaryOperator == TargetNode.OPERATOR_NLIKE.getOperatorCode())) {
	        if(this.primaryOperator==TargetNode.OPERATOR_NLIKE.getOperatorCode()) {
	            tmpBsh.append("!");
	        }
	        tmpBsh.append("AgnUtils.match(AgnUtils.toLowerCase(\"");
	        tmpBsh.append(this.primaryValue);
	        tmpBsh.append("\"), AgnUtils.toLowerCase(");
	      	if( AgnUtils.isOracleDB() ) {
	        	tmpBsh.append(this.primaryField.toUpperCase());
	        } else {                
	        	tmpBsh.append(this.primaryField);
	        }
	        tmpBsh.append("))");
        } else if (this.primaryOperator == TargetNode.OPERATOR_IS.getOperatorCode()) {
        	if( AgnUtils.isOracleDB() ) {
            	tmpBsh.append(this.primaryField.toUpperCase());
            } else {                
            	tmpBsh.append(this.primaryField);
            }
            if(this.primaryValue.startsWith("null")) {
                tmpBsh.append("==");
            } else {
                tmpBsh.append("!=");
            }
            tmpBsh.append("null ");
        } else {
            tmpBsh.append("AgnUtils.compareString(AgnUtils.toLowerCase(");
            if( AgnUtils.isOracleDB() ) {
            	tmpBsh.append(this.primaryField.toUpperCase());
            } else {                
            	tmpBsh.append(this.primaryField);
            }
            tmpBsh.append("), ");
            tmpBsh.append("AgnUtils.toLowerCase(\"");
            tmpBsh.append(SafeString.getSQLSafeString(this.primaryValue));
            tmpBsh.append("\"), ");
            tmpBsh.append(Integer.toString(this.primaryOperator-1));
            tmpBsh.append(") ");
        }
        
        if(this.closeBracketAfter) {
            tmpBsh.append(")");
        }
        
        return tmpBsh.toString();
    }
    
    public void setPrimaryOperator(int primOp) {
        if(primOp==TargetNode.OPERATOR_MOD.getOperatorCode())
            primOp=TargetNode.OPERATOR_EQ.getOperatorCode();
        
        this.primaryOperator=primOp;
    }
    
    private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField allFields=null;
        allFields=in.readFields();
        this.chainOperator=allFields.get("chainOperator", TargetNode.CHAIN_OPERATOR_NONE);
        this.primaryField=(String)allFields.get("primaryField", "default");
        this.primaryFieldType=(String)allFields.get("primaryFieldType", "VARCHAR");
        this.primaryOperator=allFields.get("primaryOperator", TargetNode.OPERATOR_EQ.getOperatorCode());
        this.primaryValue=(String)allFields.get("primaryValue", " ");
        this.closeBracketAfter=allFields.get("closeBracketAfter", false);
        this.openBracketBefore=allFields.get("openBracketBefore", false);
        
    	this.initializeOperatorLists();
    }
    
    /** Getter for property openBracketBefore.
     * @return Value of property openBracketBefore.
     */
    public boolean isOpenBracketBefore() {
        return this.openBracketBefore;
    }
    
    /** Setter for property openBracketBefore.
     * @param openBracketBefore New value of property openBracketBefore.
     */
    public void setOpenBracketBefore(boolean openBracketBefore) {
        this.openBracketBefore=openBracketBefore;
    }
    
    /** Getter for property closeBracketAfter.
     * @return Value of property closeBracketAfter.
     */
    public boolean isCloseBracketAfter() {
        return this.closeBracketAfter;
    }
    
    /** Setter for property closeBracketAfter.
     * @param closeBracketAfter New value of property closeBracketAfter.
     */
    public void setCloseBracketAfter(boolean closeBracketAfter) {
        this.closeBracketAfter=closeBracketAfter;
    }
    
    /** Getter for property chainOperator.
     * @return Value of property chainOperator.
     */
    public int getChainOperator() {
        return this.chainOperator;
    }
    
    /** Setter for property chainOperator.
     * @param chainOperator New value of property chainOperator.
     */
    public void setChainOperator(int chainOperator) {
        this.chainOperator=chainOperator;
    }
    
    /** Getter for property primaryOperator.
     * @return Value of property primaryOperator.
     */
    public int getPrimaryOperator() {
        return this.primaryOperator;
    }
    
    /** Getter for property primaryField.
     * @return Value of property primaryField.
     */
    public String getPrimaryField() {
        return this.primaryField;
    }
    
    /** Setter for property primaryField.
     * @param primaryField New value of property primaryField.
     */
    public void setPrimaryField(String primaryField) {
        this.primaryField=primaryField;
    }
    
    /** Getter for property primaryFieldType.
     * @return Value of property primaryFieldType.
     */
    public String getPrimaryFieldType() {
        return this.primaryFieldType;
    }
    
    /** Setter for property primaryFieldType.
     * @param primaryFieldType New value of property primaryFieldType.
     */
    public void setPrimaryFieldType(String primaryFieldType) {
        this.primaryFieldType=primaryFieldType;
    }
    
    /** Getter for property primaryValue.
     * @return Value of property primaryValue.
     */
    public String getPrimaryValue() {
        return this.primaryValue;
    }
    
    /**
     * Setter for property primaryValue.
     * @param primValue 
     */
    public void setPrimaryValue(String primValue) {
        if(this.primaryOperator==TargetNode.OPERATOR_IS.getOperatorCode()) {
            if(!primValue.equals("null") && !primValue.equals("not null")) {
                this.primaryValue = "null";
            } else {
                this.primaryValue=primValue;
            }
        } else {
            this.primaryValue=primValue;
        }
    }
}
