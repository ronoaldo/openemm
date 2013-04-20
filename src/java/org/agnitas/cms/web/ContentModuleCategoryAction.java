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

package org.agnitas.cms.web;

import org.agnitas.cms.utils.CmsUtils;
import org.agnitas.cms.utils.dataaccess.ContentModuleManager;
import org.agnitas.cms.webservices.generated.ContentModuleCategory;
import org.agnitas.cms.web.forms.ContentModuleCategoryForm;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.StrutsActionBase;
import org.agnitas.web.forms.StrutsFormBase;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Action for managing Content Module Categories
 *
 * @author Vyacheslav Stepanov
 */

public class ContentModuleCategoryAction extends StrutsActionBase {

	public static final int ACTION_NEW = ACTION_LAST + 1;

	public ActionForward execute(ActionMapping mapping, ActionForm form,
								 HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {

		ContentModuleCategoryForm aForm;
		ActionMessages errors = new ActionMessages();
		ActionMessages messages = new ActionMessages();

		ActionForward destination = null;

		if(!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}

		if(form != null) {
			aForm = (ContentModuleCategoryForm) form;
		} else {
			aForm = new ContentModuleCategoryForm();
		}

		AgnUtils.logger().info("Action: " + aForm.getAction());

		try {
			switch(aForm.getAction()) {
				case ContentModuleCategoryAction.ACTION_LIST:
					aForm.setName("");
					aForm.setDescription("");
					aForm.setCmcId(0);
					if ( aForm.getColumnwidthsList() == null) {
						aForm.setColumnwidthsList(getInitializedColumnWidthList(3));
					}
					destination = mapping.findForward("list");
					aForm.setAction(ContentModuleCategoryAction.ACTION_LIST);
					break;

				case ContentModuleCategoryAction.ACTION_VIEW:
					loadCMCategory(aForm);
					aForm.setAction(ContentModuleCategoryAction.ACTION_SAVE);
					destination = mapping.findForward("view");
					break;

				case ContentModuleCategoryAction.ACTION_NEW:
					resetFormData(aForm);
					aForm.setAction(ContentModuleCategoryAction.ACTION_SAVE);
					destination = mapping.findForward("new");
					break;

				case ContentModuleCategoryAction.ACTION_SAVE:
					saveCategory(aForm, req);
					aForm.setAction(ContentModuleCategoryAction.ACTION_SAVE);
					destination = mapping.findForward("view");
					messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
					break;

				case ContentModuleCategoryAction.ACTION_CONFIRM_DELETE:
                    loadCMCategory(aForm);
					aForm.setAction(ContentModuleCategoryAction.ACTION_DELETE);
					destination = mapping.findForward("delete");
					break;

				case ContentModuleCategoryAction.ACTION_DELETE:
					if(AgnUtils.parameterNotEmpty(req, "kill")) {
						getContentModuleManager().deleteContentModuleCategory(aForm.getCmcId());
                        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
					}
					aForm.setAction(ContentModuleCategoryAction.ACTION_LIST);
					aForm.setName("");
					aForm.setDescription("");
					aForm.setCmcId(0);
					destination = mapping.findForward("list");
					break;
			}
		}
		catch(Exception e) {
			AgnUtils.logger()
					.error("Error while executing action with CM Category: " + e + "\n" + AgnUtils.getStackTrace(e));
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
		}

		// collect list of CMCs for list-page
		if(destination != null && "list".equals(destination.getName())) {
			try {
				setNumberOfRows(req, (StrutsFormBase) form);
				req.setAttribute("cmCategoryList", getContentModuleCategoryList(req));
			} catch(Exception e) {
				AgnUtils.logger().error("getContentModuleList: " + e + "\n" + AgnUtils.getStackTrace(e));
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
			}
		}

		// Report any errors we have discovered back to the original form
		if(!errors.isEmpty()) {
			saveErrors(req, errors);
		}

		if(!messages.isEmpty()) {
			saveMessages(req, messages);
		}

		// we need some destination to show error messages
		if(destination == null && !errors.isEmpty()) {
			destination = mapping.findForward("list");
		}

		return destination;
	}

	private void resetFormData(ContentModuleCategoryForm aForm) {
		aForm.setName("");
		aForm.setDescription("");
	}

	private void saveCategory(ContentModuleCategoryForm aForm, HttpServletRequest req) {
		ContentModuleCategory category = new ContentModuleCategory(AgnUtils.getCompanyID(req), aForm.getDescription(),
				aForm.getCmcId(), aForm.getName());
		if (aForm.getCmcId() == 0) {
			int categoryId = getContentModuleManager().createContentModuleCategory(category);
			aForm.setCmcId(categoryId);
		}
		else {
			getContentModuleManager().updateContentModuleCategory(category);
		}
	}

	private void loadCMCategory(ContentModuleCategoryForm aForm) {
        ContentModuleCategory category = getContentModuleManager().getContentModuleCategory(aForm.getCmcId());
        if (category != null) {
            aForm.setName(category.getName());
            aForm.setDescription(category.getDescription());
        }
	}

	protected ContentModuleManager getContentModuleManager() {
		return CmsUtils.getContentModuleManager(getWebApplicationContext());
	}

	public List<ContentModuleCategory> getContentModuleCategoryList(HttpServletRequest request) throws
			IllegalAccessException, InstantiationException {
		return getContentModuleManager().getAllCMCategories(AgnUtils.getCompanyID(request));
	}

}