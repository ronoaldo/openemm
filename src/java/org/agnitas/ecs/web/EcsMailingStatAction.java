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
 * the code written by AGNITAS AG are Copyright (c) 2009 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

package org.agnitas.ecs.web;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.agnitas.beans.Mailing;
import org.agnitas.dao.MailingDao;
import org.agnitas.ecs.EcsGlobals;
import org.agnitas.ecs.backend.beans.ClickStatColor;
import org.agnitas.ecs.backend.dao.EmbeddedClickStatDao;
import org.agnitas.ecs.service.EcsRecipientsProvider;
import org.agnitas.ecs.web.forms.EcsMailingStatForm;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.StrutsActionBase;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import java.util.Collection;

/**
 * Action class that handles embedded click statistics page.
 *
 * @author Vyacheslav Stepanov
 */
public class EcsMailingStatAction extends StrutsActionBase {

	/**
	 * Service object that provides with
	 * test/admin recipients of mailing's mailinglist
	 */
	private EcsRecipientsProvider recipientsProvider;

	/**
	 * Setter for recipients provider
	 *
	 * @param recipientsProvider recipients provider
	 */
	public void setRecipientsProvider(EcsRecipientsProvider recipientsProvider) {
		this.recipientsProvider = recipientsProvider;
	}

	private EmbeddedClickStatDao ecsDao;
	
	public void setEmbeddedClickStatDao(EmbeddedClickStatDao ecsDao) {
		this.ecsDao = ecsDao;
	}
	
	private MailingDao mailingDao;
	
	public void setMailingDao(MailingDao mailingDao) {
		this.mailingDao = mailingDao;
	}
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse res) throws IOException, ServletException {
		ActionMessages errors = new ActionMessages();
		
		// check logon
		if(!AgnUtils.isUserLoggedIn(request)) {
			return mapping.findForward("logon");
		}
		
		// initialize form
		EcsMailingStatForm aForm;
		if(form != null) {
			aForm = (EcsMailingStatForm) form;
		} else {
			aForm = new EcsMailingStatForm();
		}

		
		// Initialization of the ECS page is now auto-detected		
		initEcsPage(request, aForm);

		// Checks for test recipients or admins
		if(!hasPreviewRecipients(aForm, request)) {
			errors.add("heatmap_errors", new ActionMessage("error.preview.no_recipient"));
		}

		if (!errors.isEmpty()) {
			aForm.setHeatmapErrors(	errors);
            this.saveErrors(request, errors);
		} else {
			aForm.setHeatmapErrors( null);
		}
		
		return mapping.findForward("quick_view");
	}

	/**
	 * Method inits data for ECS-page: sets list of test recipients,
	 * sets default view mode and default recipient, sets company id
	 *
	 * @param request request
	 * @param aForm   form
	 */
	private void initEcsPage(HttpServletRequest request, EcsMailingStatForm aForm) {
		if( aForm.getInitializedMailingId() != aForm.getMailingId() || request.getParameter("init") != null) {
			
			// get test recipients for drowp-down list
			int companyId = AgnUtils.getCompanyID(request);
			Map<Integer, String> recipients = recipientsProvider.getTestAndAdminRecipients(aForm.getMailingId(), companyId);
			aForm.setTestRecipients(recipients);
			// set color codes and ranges
			Collection<ClickStatColor> rangeColors = ecsDao.getClickStatColors(companyId);
			aForm.setRangeColors(rangeColors);
			// set company id
			aForm.setCompanyId(companyId);
			//set shortname of Mailing
			Mailing mailing = mailingDao.getMailing(aForm.getMailingId(), companyId); 
			aForm.setShortname(mailing.getShortname());
            aForm.setDescription(mailing.getDescription());
			// set statistics server URL
			aForm.setStatServerUrl(AgnUtils.getEMMProperty("system.url"));
			// set default recipient
			if(!recipients.isEmpty()) {
				aForm.setSelectedRecipient(recipients.keySet().iterator().next());
			} else {
				aForm.setSelectedRecipient(0);
			}
			// set default view mode and frame size
			aForm.setViewMode(EcsGlobals.MODE_GROSS_CLICKS);
			aForm.setFrameSize(1);
			
			aForm.setInitializedMailingId( aForm.getMailingId());
		}
	}

	protected boolean hasPreviewRecipients(EcsMailingStatForm form, HttpServletRequest request) {
		return this.mailingDao.hasPreviewRecipients(form.getMailingId(), getCompanyID(request));
	}
}
