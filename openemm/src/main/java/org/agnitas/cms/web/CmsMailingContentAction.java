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

package org.agnitas.cms.web;

import org.agnitas.beans.DynamicTag;
import org.agnitas.beans.Mailing;
import org.agnitas.beans.impl.MailingImpl;
import org.agnitas.cms.beans.CmsTargetGroup;
import org.agnitas.cms.dao.CmsMailingDao;
import org.agnitas.cms.utils.CMLocationsUtils;
import org.agnitas.cms.utils.ClassicTemplateGenerator;
import org.agnitas.cms.utils.CmsUtils;
import org.agnitas.cms.utils.TagUtils;
import org.agnitas.cms.utils.dataaccess.CMTemplateManager;
import org.agnitas.cms.utils.dataaccess.ContentModuleManager;
import org.agnitas.cms.web.forms.CmsMailingContentForm;
import org.agnitas.cms.webservices.generated.CMTemplate;
import org.agnitas.cms.webservices.generated.ContentModule;
import org.agnitas.cms.webservices.generated.ContentModuleLocation;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.MailingContentAction;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * @author Vyacheslav Stepanov
 */
                       
public class CmsMailingContentAction extends MailingContentAction {

	public static final int ACTION_CMS_EDITOR = ACTION_LAST + 50;
	public static final int ACTION_SAVE_CMS_DATA = ACTION_LAST + 51;
	public static final int ACTION_ADD_CM = ACTION_LAST + 52;
	public static final int ACTION_REMOVE_CM = ACTION_LAST + 53;
	public static final int ACTION_SHOW_TEXT_VERSION = ACTION_LAST + 54;
	public static final int ACTION_SAVE_CMS_TEXT_VERSION = ACTION_LAST + 55;
	public static final int ACTION_CHANGE_CATEGORY = ACTION_LAST + 56;

	public static final int CM_LIST_SIZE = 5;

    private CmsMailingDao cmsMailingDao;
    private ClassicTemplateGenerator classicTemplateGenerator;

    public ActionForward execute(ActionMapping mapping,
								 ActionForm form,
								 HttpServletRequest request,
								 HttpServletResponse res)
			throws IOException, ServletException {

		ActionForward actualDestination = super.execute(mapping, form, request, res);

		CmsMailingContentForm aForm = null;
		ActionMessages errors = new ActionMessages();
		ActionForward destination = null;

		aForm = (CmsMailingContentForm) form;
		AgnUtils.logger().info("Action: " + aForm.getAction());

		if(!allowed("cms.mailing_content_management", request)) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
            saveErrors(request, errors);
			return actualDestination;
		}

		if(hasParameterStartsWith(request, "addCM")) {
			aForm.setAction(CmsMailingContentAction.ACTION_ADD_CM);
		}

		if(hasParameterStartsWith(request, "removeCM")) {
			aForm.setAction(CmsMailingContentAction.ACTION_REMOVE_CM);
		}

		if(AgnUtils.parameterNotEmpty(request, "show")) {
			aForm.setAction(CmsMailingContentAction.ACTION_CHANGE_CATEGORY);
		}

		try {
			final int adminId = AgnUtils.getAdmin(request).getAdminID();
			switch(aForm.getAction()) {
				// if mailing has CMS model - show CMS content edit page
				case CmsMailingContentAction.ACTION_VIEW_CONTENT: {
                    // Get test recipients for preview panel drop down list.
                    // Impossible to do separation of layers (in order not to get dao from applicationContext object)
                    // until MailingContentAction is refactored so that it will not access applicationContext object
                    Map<Integer, String> testRecipients = recipientDao.getAdminAndTestRecipientsDescription(
                            aForm.getCompanyID(request), aForm.getMailingID());
                    aForm.setTestRecipients(testRecipients);
					// try to find assigned template
					CMTemplate template = getTemplateManager(request)
							.getCMTemplateForMailing(aForm.getMailingID());
					// if no assigned template found - try to find assigned CMs
                    if(template == null) {
						List<Integer> assignedCms =
								getContentModuleManager(request)
										.getAssignedCMsForMailing(aForm.getMailingID());
						if(!assignedCms.isEmpty()) {
							destination = mapping.findForward("cms_content_editor");
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("cms.NoTemplateAssigned"));
						}
					} else {
						destination = mapping.findForward("cms_content_editor");
					}
					break;
				}
				case CmsMailingContentAction.ACTION_CMS_EDITOR: {
					aForm.resetCmsData();
					loadAllContentModules(aForm, request);
					errors.add(loadEditor(aForm, request));
					aForm.setAction(CmsMailingContentAction.ACTION_SAVE_CMS_DATA);
					aForm.setAllCategories(getContentModuleManager(request).getAllCMCategories(AgnUtils.getCompanyID(request)));
					destination = mapping.findForward("cms_editor_frame");
					break;
				}
				case CmsMailingContentAction.ACTION_SAVE_CMS_DATA: {
					if(!request.getParameterMap().isEmpty()) {
						saveCmAssignments(aForm, request);
						saveLocations(aForm, request, true);
						final int mailingId = aForm.getMailingID();
						classicTemplateGenerator.generate(mailingId, request, false);
					}

					String cmEditId = request.getParameter("cmToEdit");
					String phForNewCM = request.getParameter("phForNewCM");
					if(cmEditId != null && !cmEditId.isEmpty() && !"0".equals(cmEditId)) {
						destination = new ActionForward(mapping.findForward("edit_cm").getPath() + "&contentModuleId=" + 
								cmEditId + "&mailingId=" + aForm.getMailingID(), true);
					} else if(phForNewCM != null && !phForNewCM.isEmpty()) {
						destination = new ActionForward(mapping.findForward("new_cm").getPath() + "&mailingId=" +
								aForm.getMailingID() + "&phName=" + phForNewCM + "&createForMailing=true", true);
					} else {
						aForm.setAction(CmsMailingContentAction.ACTION_SAVE_CMS_DATA);
						destination = mapping.findForward("cms_editor_frame");
					}
					break;
				}
				case CmsMailingContentAction.ACTION_ADD_CM: {
					saveLocations(aForm, request, false);
					errors = addContentModule(aForm, request);
					aForm.setAction(CmsMailingContentAction.ACTION_SAVE_CMS_DATA);
					destination = mapping.findForward("cms_editor_frame");
					break;
				}
				case CmsMailingContentAction.ACTION_REMOVE_CM: {
					saveLocations(aForm, request, false);
					removeContentModule(aForm, request);
					aForm.setAction(CmsMailingContentAction.ACTION_SAVE_CMS_DATA);
					destination = mapping.findForward("cms_editor_frame");
					break;

				}
				case CmsMailingContentAction.ACTION_CHANGE_CATEGORY: {
					saveLocations(aForm, request, false);
					aForm.setAction(CmsMailingContentAction.ACTION_SAVE_CMS_DATA);
					destination = mapping.findForward("cms_editor_frame");
					break;
				}
				case CmsMailingContentAction.ACTION_SHOW_TEXT_VERSION: {
					//Do some thing
					final CMTemplateManager manager = CmsUtils
							.getCMTemplateManager(getApplicationContext(request));
					aForm.setTextVersion(manager.getTextVersion(adminId));
					aForm.setAction(CmsMailingContentAction.ACTION_SAVE_CMS_TEXT_VERSION);
                    aForm.setWorldMailingSend(isWorldMailingSend(aForm.getMailingID(),request));
					destination = mapping.findForward("text_editor");
					break;
				}
				case CmsMailingContentAction.ACTION_SAVE_CMS_TEXT_VERSION: {
					//save text content
					final int mailingId = aForm.getMailingID();
					final int companyId = aForm.getCompanyID(request);
					final CMTemplateManager manager = CmsUtils
							.getCMTemplateManager(getApplicationContext(request));
					manager.saveTextVersion(adminId, aForm.getTextVersion());
					classicTemplateGenerator.generate(mailingId, request);
					aForm.setAction(CmsMailingContentAction.ACTION_SAVE_CMS_DATA);
					destination = mapping.findForward("cms_content_editor");
					break;
				}
			}
		} catch(Exception e) {
			AgnUtils.logger().error("execute: " + e + "\n" + AgnUtils.getStackTrace(e));
			errors.add(ActionMessages.GLOBAL_MESSAGE,
					new ActionMessage("error.exception"));
		}

		if(destination != null && "cms_editor_frame".equals(destination.getName())) {
			aForm.setAvailableContentModules(getAllContentModulesList(aForm));
			request.setAttribute("availableCmCount",
					aForm.getAvailableContentModules().size());
		}

		// Report any errors we have discovered back to the original form
		if(!errors.isEmpty()) {
			saveErrors(request, errors);
		}

		if(destination == null) {
			return actualDestination;
		} else {
			return destination;
		}

	}

	private boolean hasParameterStartsWith(HttpServletRequest request, String startStr) {
		Enumeration parameterNames = request.getParameterNames();
		while(parameterNames.hasMoreElements()) {
			String parameterName = (String) parameterNames.nextElement();
			if(parameterName.startsWith(startStr)) {
				return true;
			}
		}
		return false;
	}

    private boolean isWorldMailingSend(int mailingID,HttpServletRequest request){
        Mailing aMailing=mailingDao.getMailing(mailingID, this.getCompanyID(request));
        return aMailing.isWorldMailingSend();
    }

	private String getParameterValueFromName(HttpServletRequest request,
											 String startStr) {
		Enumeration parameterNames = request.getParameterNames();
		while(parameterNames.hasMoreElements()) {
			String parameterName = (String) parameterNames.nextElement();
			if(parameterName.startsWith(startStr) && parameterName.endsWith(".x")) {
				int delimiterIndex = parameterName.indexOf("_");
				if(delimiterIndex == -1) {
					return null;
				}
				return parameterName
						.substring(delimiterIndex + 1, parameterName.length() - 2);
			}
		}
		return null;
	}

	private void saveCmAssignments(CmsMailingContentForm aForm,
								   HttpServletRequest request) {
		List<Integer> oldAssignedCMs = getContentModuleManager(request).
				getAssignedCMsForMailing(aForm.getMailingID());
		Set<Integer> newAssignedCMs = aForm.getContentModules().keySet();
		List<Integer> cmsToAssign = new ArrayList<Integer>();
		List<Integer> cmsToDeassign = new ArrayList<Integer>();
		for(Integer cmId : newAssignedCMs) {
			if(!oldAssignedCMs.contains(cmId)) {
				cmsToAssign.add(cmId);
			}
		}
		for(Integer cmId : oldAssignedCMs) {
			if(!newAssignedCMs.contains(cmId)) {
				cmsToDeassign.add(cmId);
			}
		}
		getContentModuleManager(request)
				.addMailingBindingToContentModules(cmsToAssign, aForm.getMailingID());
		getContentModuleManager(request).
				removeMailingBindingFromContentModules(cmsToDeassign,
						aForm.getMailingID());
	}

	private void removeContentModule(CmsMailingContentForm aForm,
									 HttpServletRequest request) {
		Integer cmId = Integer.valueOf(getParameterValueFromName(request, "removeCM"));
		aForm.getContentModules().remove(cmId);
		ContentModuleLocation location =
				CMLocationsUtils
						.getLocationForCMId(aForm.getContentModuleLocations(), cmId);
		aForm.getContentModuleLocations().remove(location);
		refreshLocations(aForm);
	}

	private ActionMessages addContentModule(CmsMailingContentForm aForm,
											HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		String[] placeholders = aForm.getPlaceholders();
		List<ContentModuleLocation> cmLocations = aForm.getContentModuleLocations();
		if(cmLocations.size() >= placeholders.length) {
			errors.add("global", new ActionMessage("error.cant.add.cm"));
			return errors;
		}
		int cmId = Integer.valueOf(getParameterValueFromName(request, "addCM"));
		String cmContent = TagUtils.generateContentModuleContent(cmId,
				false, getApplicationContext(request));
        cmContent = CmsUtils.appendImageURLsWithSystemUrl(cmContent);
		aForm.getContentModules().put(cmId, cmContent);
		refreshLocations(aForm);
		return errors;
	}

	private void refreshLocations(CmsMailingContentForm aForm) {
		List<ContentModuleLocation> validLocations =
				CMLocationsUtils.createValidLocations(
						aForm.getContentModuleLocations(),
						aForm.getContentModules().keySet(),
						aForm.getPlaceholders(), aForm.getCmTemplateId(),
						aForm.getMailingID());
		aForm.setContentModuleLocations(validLocations);
	}

	private List<ContentModule> getAllContentModulesList(CmsMailingContentForm aForm) {
		List<ContentModule> resultList = new ArrayList<ContentModule>();
		Set<Integer> assignedCmIds = aForm.getContentModules().keySet();
		for(ContentModule contentModule : aForm.getAllContentModules()) {
			if(!assignedCmIds.contains(contentModule.getId())) {
				if (aForm.getCategoryToShow() == 0 || 
						aForm.getCategoryToShow() > 0 && aForm.getCategoryToShow() == contentModule.getCategoryId()) {
					resultList.add(contentModule);
				}
			}
		}
		return resultList;
	}

	private void loadAllContentModules(CmsMailingContentForm aForm,
									   HttpServletRequest request) {
		aForm.setAllContentModules(getContentModuleManager(request).getContentModulesForMailing(aForm.getMailingID()));
	}

	private void saveLocations(CmsMailingContentForm aForm, HttpServletRequest request,
							   boolean storeToDB) {
		Map<Integer, String> modules = aForm.getContentModules();
		for(Integer cmId : modules.keySet()) {
			ContentModuleLocation location = CMLocationsUtils.getLocationForCMId(
					aForm.getContentModuleLocations(), cmId);
			if(location == null) {
				continue;
			}
			String phName = request.getParameter("cm." + cmId + ".ph_name");
			int phOrder = Integer
					.parseInt(request.getParameter("cm." + cmId + ".order_in_ph"));
			int targetId = Integer.parseInt(request.getParameter("cm_target." + cmId));
			location.setDynName(phName);
			location.setOrder(phOrder);
			location.setTargetGroupId(targetId);
		}
		if(storeToDB) {
			getContentModuleManager(request).removeCMLocationsForMailing(aForm.getMailingID());
			getContentModuleManager(request).addCMLocations(aForm.getContentModuleLocations());
		}
	}

	private ActionMessages loadEditor(CmsMailingContentForm aForm,
									  HttpServletRequest request) {
		// load template data
		CMTemplate template = getTemplateManager(request)
				.getCMTemplateForMailing(aForm.getMailingID());
		String templateContent = CmsUtils.getDefaultCMTemplate();
		String templateHead = "";
		int templateId = 0;
		if(template != null) {
			try {
				templateContent = new String(template.getContent(), "UTF-8");
                templateContent = CmsUtils.appendImageURLsWithSystemUrl(templateContent);
			} catch(UnsupportedEncodingException e) {
				AgnUtils.logger().warn("Wrong charset name", e);
			}
			templateHead = getTemplateHead(templateContent);
			templateContent = getTemplateBody(templateContent);
			templateId = template.getId();
		}
		aForm.setCmTemplateId(templateId);
		aForm.setTemplateHead(templateHead);
		// find dyn tags in template body
		ActionMessages errors = FindDynTags(aForm, request, templateContent);
		if(!errors.isEmpty()) {
			return errors;
		}
		// generate content modules content
		loadContentModules(aForm, request);
		// load and fix if necessary modules locations
		loadCMLocations(aForm, templateId, request);
		// load target groups
		loadTargetGroups(aForm, request);

		return new ActionMessages();
	}


	private void loadCMLocations(CmsMailingContentForm aForm, int templateId, HttpServletRequest req) {
		List<ContentModuleLocation> locations = getContentModuleManager(req).
				getCMLocationsForMailingId(aForm.getMailingID());
		List<ContentModuleLocation> validLocations =
				CMLocationsUtils.createValidLocations(locations,
						aForm.getContentModules().keySet(),
						aForm.getPlaceholders(), templateId, aForm.getMailingID());
		aForm.setContentModuleLocations(validLocations);
	}

	private void loadContentModules(CmsMailingContentForm aForm, HttpServletRequest req) {
		List<ContentModule> moduleList = getContentModuleManager(req).
				getContentModulesForMailing(aForm.getMailingID());
		Map<Integer, String> contentMap = new HashMap<Integer, String>();
		for(ContentModule contentModule : moduleList) {
			String cmContent = TagUtils
					.generateContentModuleContent(contentModule.getId(),
							false, getApplicationContext(req));
            cmContent = CmsUtils.appendImageURLsWithSystemUrl(cmContent);
			contentMap.put(contentModule.getId(), cmContent);
		}
		aForm.setContentModules(contentMap);
	}

	private ActionMessages FindDynTags(CmsMailingContentForm aForm,
									   HttpServletRequest request,
									   String templateContent) {
		ActionMessages errors = new ActionMessages();
		MailingImpl tmpMailing = new MailingImpl();
		tmpMailing.setCompanyID(AgnUtils.getCompanyID(request));
		tmpMailing.setDynTags(new HashMap());
		try {
			Vector dynTags = tmpMailing
					.findDynTagsInTemplates(templateContent, getApplicationContext(request));
			for(Object dynObject : dynTags) {
				String dynName = (String) dynObject;
				DynamicTag dynTag = (DynamicTag) tmpMailing.getDynTags().get(dynName);
				int tagStart = -1;
				int tagEnd = -1;
				if(dynTag.getStartTagEnd() > 0) {
					tagStart = dynTag.getStartTagStart();
					tagEnd = dynTag.getStartTagEnd();
				} else if(dynTag.getValueTagEnd() > 0) {
					tagStart = dynTag.getValueTagStart();
					tagEnd = dynTag.getValueTagEnd();
				}
				if(tagStart > -1 && tagEnd > -1) {
					String tagReplacer = generatePlaceholderHtml(dynName);
					int shift = tagReplacer.length() - (tagEnd - tagStart);
					adjustTagsPositions(tmpMailing.getDynTags(), shift);
					templateContent = templateContent.substring(0, tagStart) +
							tagReplacer + templateContent.substring(tagEnd);
				}
			}
			aForm.setTemplateBody(templateContent);
			if(!dynTags.isEmpty()) {
				aForm.setPlaceholders((String[]) dynTags.toArray(new String[0]));
			}
		} catch(Exception e) {
			AgnUtils.logger().error("Error during dyn tags parsing: " + e + "\n" +
					AgnUtils.getStackTrace(e));
			errors.add(ActionMessages.GLOBAL_MESSAGE,
					new ActionMessage("error.template.dyntags"));
		}
		return errors;
	}

	private void adjustTagsPositions(Map dynTags, int shift) {
		for(Object tag : dynTags.keySet()) {
			DynamicTag dynTag = (DynamicTag) dynTags.get(tag);
			if(dynTag.getStartTagEnd() > 0) {
				dynTag.setStartTagStart(dynTag.getStartTagStart() + shift);
				dynTag.setStartTagEnd(dynTag.getStartTagEnd() + shift);
			}
			if(dynTag.getValueTagEnd() > 0) {
				dynTag.setValueTagStart(dynTag.getValueTagStart() + shift);
				dynTag.setValueTagEnd(dynTag.getValueTagEnd() + shift);
			}
		}
	}

	private void loadTargetGroups(CmsMailingContentForm aForm,
								  HttpServletRequest request) {
		Map<Integer, CmsTargetGroup> targetGroups = cmsMailingDao.getTargetGroups(AgnUtils.getCompanyID(request));
		aForm.setTargetGroups(targetGroups);
	}

	private String getTemplateHead(String template) {
		return getTagContent(template, "head");
	}

	private String getTemplateBody(String template) {
		String body = getTagContent(template, "body");
		if(body.length() == 0) {
			return template;
		}
		return body;
	}

	private String getTagContent(String content, String tagName) {
		int headStart = content.indexOf("<" + tagName + ">");
		if(headStart == -1) {
			headStart = content.indexOf("<" + tagName.toUpperCase() + ">");
		}
		int headEnd = content.indexOf("</" + tagName + ">");
		if(headEnd == -1) {
			headEnd = content.indexOf("</" + tagName.toUpperCase() + ">");
		}
		if(headStart != -1 && headEnd != -1 && headEnd > headStart) {
			return content.substring(headStart + tagName.length() + 2, headEnd);
		} else {
			return "";
		}
	}

	private String generatePlaceholderHtml(String name) {
		return "<table id=\"table.placeholder." + name + "\"class=\"placeholder\">\n" +
				"    <tbody id=\"placeholder." + name + "\">\n" +
				"        <tr>\n" +
				"            <td id=\"name.placeholder." + name + "\" class=\"placeholder-headline\">" + name + "</td>\n" +
				"        </tr>\n" +
				"    </tbody>\n" +
				"</table>";
	}

	private CMTemplateManager getTemplateManager(HttpServletRequest req) {
		return CmsUtils.getCMTemplateManager(getApplicationContext(req));
	}

	private ContentModuleManager getContentModuleManager(HttpServletRequest req) {
		return CmsUtils.getContentModuleManager(getApplicationContext(req));
	}

    public void setCmsMailingDao(CmsMailingDao cmsMailingDao) {
        this.cmsMailingDao = cmsMailingDao;
    }

    public void setClassicTemplateGenerator(ClassicTemplateGenerator classicTemplateGenerator) {
        this.classicTemplateGenerator = classicTemplateGenerator;
    }
}