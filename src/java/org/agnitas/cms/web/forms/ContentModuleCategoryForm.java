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
 * the code written by AGNITAS AG are Copyright (c) 2010 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/

package org.agnitas.cms.web.forms;

import javax.servlet.http.*;
import org.agnitas.cms.web.*;
import org.apache.struts.action.*;

/**
 * @author Vyacheslav Stepanov
 */
public class ContentModuleCategoryForm extends CmsBaseForm {

	protected int cmcId;
	
	protected String name;

	protected String description;

	public int getCmcId() {
		return cmcId;
	}

	public void setCmcId(int cmcId) {
		this.cmcId = cmcId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public ActionErrors formSpecificValidate(ActionMapping actionMapping,
								 HttpServletRequest httpServletRequest) {
		ActionErrors actionErrors = new ActionErrors();

		if(action == ContentModuleTypeAction.ACTION_SAVE) {
			if(this.name.length() < 3) {
				actionErrors.add("shortname", new ActionMessage("error.nameToShort"));
			}
		}
		return actionErrors;
	}
}