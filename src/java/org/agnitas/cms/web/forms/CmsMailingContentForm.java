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

package org.agnitas.cms.web.forms;

import org.agnitas.cms.beans.CmsTargetGroup;
import org.agnitas.cms.webservices.generated.ContentModule;
import org.agnitas.cms.webservices.generated.ContentModuleLocation;
import org.agnitas.cms.webservices.generated.ContentModuleCategory;
import org.agnitas.web.MailingContentForm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
                               
/**
 * @author Vyacheslav Stepanov
 */
public class CmsMailingContentForm extends MailingContentForm {

	private Map<Integer, CmsTargetGroup> targetGroups;

	private String templateHead;

	private String templateBody;

	private Map<Integer, String> contentModules;

	private List<ContentModuleLocation> contentModuleLocations;

	private String[] placeholders;

	private List<ContentModule> allContentModules;

	private List<ContentModule> availableContentModules;

	private int cmTemplateId;

	private String textVersion;

    protected Map<Integer, String> testRecipients;

	private List<ContentModuleCategory> allCategories;

	private int categoryToShow;

	public String getTextVersion() {
		return textVersion;
	}

	public void setTextVersion(String textVersion) {
		this.textVersion = textVersion;
	}

	public Map<Integer, CmsTargetGroup> getTargetGroups() {
		return targetGroups;
	}

	public void setTargetGroups(Map<Integer, CmsTargetGroup> targetGroups) {
		this.targetGroups = targetGroups;
	}

	public String getTemplateHead() {
		return templateHead;
	}

	public void setTemplateHead(String templateHead) {
		this.templateHead = templateHead;
	}

	public String getTemplateBody() {
		return templateBody;
	}

	public void setTemplateBody(String templateBody) {
		this.templateBody = templateBody;
	}

	public Map<Integer, String> getContentModules() {
		return contentModules;
	}

	public void setContentModules(Map<Integer, String> contentModules) {
		this.contentModules = contentModules;
	}

	public List<ContentModuleLocation> getContentModuleLocations() {
		return contentModuleLocations;
	}

	public void setContentModuleLocations(
			List<ContentModuleLocation> contentModuleLocations) {
		this.contentModuleLocations = contentModuleLocations;
	}

	private boolean showDateSettings = false;

    public boolean isShowDateSettings() {
        return showDateSettings;
    }

    public void setShowDateSettings(boolean showDateSettings) {
        this.showDateSettings = showDateSettings;
    }

	public String[] getPlaceholders() {
		return placeholders;
	}

	public void setPlaceholders(String[] placeholders) {
		this.placeholders = placeholders;
	}

	public List<ContentModule> getAllContentModules() {
		return allContentModules;
	}

	public void setAllContentModules(List<ContentModule> allContentModules) {
		this.allContentModules = allContentModules;
	}

	public int getCmTemplateId() {
		return cmTemplateId;
	}

	public void setCmTemplateId(int cmTemplateId) {
		this.cmTemplateId = cmTemplateId;
	}

	public List<ContentModule> getAvailableContentModules() {
		return availableContentModules;
	}

	public void setAvailableContentModules(List<ContentModule> availableContentModules) {
		this.availableContentModules = availableContentModules;
	}

	public void resetCmsData() {
		templateBody = "";
		templateHead = "";
		placeholders = new String[0];
		targetGroups = new HashMap<Integer, CmsTargetGroup>();
		contentModules = new HashMap<Integer, String>();
		contentModuleLocations = new ArrayList<ContentModuleLocation>();
		cmTemplateId = 0;
		allContentModules = new ArrayList<ContentModule>();
		availableContentModules = new ArrayList<ContentModule>();
	}

    public Map<Integer, String> getTestRecipients() {
        return testRecipients;
    }

    public void setTestRecipients(Map<Integer, String> testRecipients) {
        this.testRecipients = testRecipients;
    }

	public List<ContentModuleCategory> getAllCategories() {
		return allCategories;
	}

	public void setAllCategories(List<ContentModuleCategory> allCategories) {
		this.allCategories = allCategories;
	}

	public int getCategoryToShow() {
		return categoryToShow;
	}

	public void setCategoryToShow(int categoryToShow) {
		this.categoryToShow = categoryToShow;
	}
}
