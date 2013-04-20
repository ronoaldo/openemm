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

package org.agnitas.target;

import org.agnitas.target.impl.TargetNodeDate;
import org.agnitas.target.impl.TargetNodeNumeric;
import org.agnitas.target.impl.TargetNodeString;

public interface TargetNodeFactory {
	
	/**
	 * Creates a new target group node for numerical fields without any properties set.
	 * 
	 * @return new target node for numerical fields
	 */
	public TargetNodeNumeric newNumericNode();
	
	/**
	 * Creates a new target group node for numerical fields with properties set to given values.
	 * 
	 * @param chainOperator
	 * @param parenthesisOpened 
	 * @param primaryField
	 * @param primaryType
	 * @param primaryOperator
	 * @param primaryValue
	 * @param secondaryOperator
	 * @param secondaryValue
	 * @param parenthesisClosed
	 * 
	 * @return new target group node for numerical fields
	 */
	public TargetNodeNumeric newNumericNode(int chainOperator, 
											int parenthesisOpened, 
											String primaryField, 
											String primaryType,
											int primaryOperator,
											String primaryValue,
											int secondaryOperator,
											int secondaryValue,
											int parenthesisClosed);
	
	/**
	 * Creates a new target group node for text fields without any properties set.
	 * 
	 * @return new target node for text fields
	 */
	public TargetNodeString newStringNode();
	
	/**
	 * Creates a new target group node for text fields with properties set to given values.
	 * 
	 * @param chainOperator
	 * @param parenthesisOpened
	 * @param primaryField
	 * @param primaryType
	 * @param primaryOperator
	 * @param primaryValue
	 * @param parenthesisClosed
	 * 
	 * @return new target node for text fields
	 */
	public TargetNodeString newStringNode(	int chainOperator,
											int parenthesisOpened,
											String primaryField,
											String primaryType,
											int primaryOperator,
											String primaryValue,
											int parenthesisClosed);

	/**
	 * Creates a new target group node for date fields without any properties set.
	 * 
	 * @return new target node for date fields
	 */
	public TargetNodeDate newDateNode();
	
	/**
	 * Creates a new target group node for date fields with properties set to given values.
	 * 
	 * @param chainOperator
	 * @param parenthesisOpened
	 * @param primaryField
	 * @param primaryType
	 * @param primaryOperator
	 * @param dateFormat
	 * @param primaryValue
	 * @param parenthesisClosed
	 * 
	 * @return new target node for date fields
	 */
	public TargetNodeDate newDateNode(		int chainOperator,
											int parenthesisOpened,
											String primaryField,
											String primaryType,
											int primaryOperator,
											String dateFormat,
											String primaryValue,
											int parenthesisClosed);
}
