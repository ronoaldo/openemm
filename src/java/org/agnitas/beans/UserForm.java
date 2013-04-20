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

package org.agnitas.beans;

import java.util.Map;
import org.agnitas.actions.EmmAction;
import org.springframework.context.ApplicationContext;

/**
 * 
 * @author mhe
 */
public interface UserForm {
	public String TEMP_REDIRECT_PARAM = "REDIRECT_TEMP";

	public boolean evaluateEndAction(ApplicationContext con, Map<String, Object> params);

	public String evaluateForm(ApplicationContext con, Map<String, Object> params);

	public boolean evaluateStartAction(ApplicationContext con, Map<String, Object> params);

	/**
	 * Getter for property companyID.
	 * 
	 * @return Value of property companyID.
	 */
	public int getCompanyID();

	/**
	 * Getter for property description.
	 * 
	 * @return Value of property description.
	 */
	public String getDescription();

	/**
	 * Getter for property endAction.
	 * 
	 * @return Value of property endAction.
	 */
	public EmmAction getEndAction();

	/**
	 * Getter for property endActionID.
	 * 
	 * @return Value of property endActionID.
	 */
	public int getEndActionID();

	/**
	 * Getter for property errorTemplate.
	 * 
	 * @return Value of property errorTemplate.
	 */
	public String getErrorTemplate();

	/**
	 * Getter for property formID.
	 * 
	 * @return Value of property formID.
	 */
	public int getId();

	/**
	 * Getter for property formName.
	 * 
	 * @return Value of property formName.
	 */
	public String getFormName();

	/**
	 * Getter for property startAction.
	 * 
	 * @return Value of property startAction.
	 */
	public EmmAction getStartAction();

	/**
	 * Getter for property startActionID.
	 * 
	 * @return Value of property startActionID.
	 */
	public int getStartActionID();

	/**
	 * Getter for property sucessTemplate.
	 * 
	 * @return Value of property sucessTemplate.
	 */
	public String getSuccessTemplate();

	/**
	 * Setter for property companyID.
	 * 
	 * @param companyID
	 *            New value of property companyID.
	 */
	public void setCompanyID(int companyID);

	/**
	 * Setter for property description.
	 * 
	 * @param description
	 *            New value of property description.
	 */
	public void setDescription(String description);

	/**
	 * Setter for property endAction.
	 * 
	 * @param endAction
	 *            New value of property endAction.
	 */
	public void setEndAction(EmmAction endAction);

	/**
	 * Setter for property endActionID.
	 * 
	 * @param endActionID
	 *            New value of property endActionID.
	 */
	public void setEndActionID(int endActionID);

	/**
	 * Setter for property errorTemplate.
	 * 
	 * @param errorTemplate
	 *            New value of property errorTemplate.
	 */
	public void setErrorTemplate(String errorTemplate);

	/**
	 * Setter for property formID.
	 * 
	 * @param formID
	 *            New value of property formID.
	 */
	public void setId(int formID);

	/**
	 * Setter for property formName.
	 * 
	 * @param formName
	 *            New value of property formName.
	 */
	public void setFormName(String formName);

	/**
	 * Setter for property startAction.
	 * 
	 * @param startAction
	 *            New value of property startAction.
	 */
	public void setStartAction(EmmAction startAction);

	/**
	 * Setter for property startActionID.
	 * 
	 * @param startActionID
	 *            New value of property startActionID.
	 */
	public void setStartActionID(int startActionID);

	/**
	 * Setter for property sucessTemplate.
	 * 
	 * @param successTemplate
	 */
	public void setSuccessTemplate(String successTemplate);

	public String getSuccessUrl();

	public void setSuccessUrl(String successUrl);

	public String getErrorUrl();

	public void setErrorUrl(String errorUrl);

	public boolean isSuccessUseUrl();

	public void setSuccessUseUrl(boolean successUseUrl);

	public boolean isErrorUseUrl();

	public void setErrorUseUrl(boolean errorUseUrl);
}
