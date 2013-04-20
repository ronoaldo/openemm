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

import javax.servlet.http.*;

import java.util.*;
import org.agnitas.cms.utils.*;
import org.agnitas.cms.web.*;
import org.agnitas.cms.webservices.generated.*;
import org.apache.commons.validator.*;
import org.apache.struts.action.*;
import org.apache.struts.upload.*;

/**
 * @author Vyacheslav Stepanov
 */
public class ContentModuleForm extends CmsBaseForm {

	protected int contentModuleId;

	protected int cmtId;

	protected int sourceCMId;

	protected String content;

	protected List<ContentModuleType> allCMT;

	protected List<CmsTag> tags;

	protected List<Integer> oldAssignment;

	protected boolean validState = true;

	protected FormFile fileUploadPrevious;
	private UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https"});

    private List<ContentModuleCategory> allCategories;

    private int category;

    private int categoryToShow;

	private int mailingId;

	private String phName;

	private boolean createForMailing;

	public void setUploadFile(FormFile uploadFile) {
		fileUploadPrevious = uploadFile;
	}

	public boolean isValidState() {
		return validState;
	}

	public void setValidState(boolean validState) {
		this.validState = validState;
	}

	public int getContentModuleId() {
		return contentModuleId;
	}

	public void setContentModuleId(int contentModuleId) {
		this.contentModuleId = contentModuleId;
	}

	public int getSourceCMId() {
		return sourceCMId;
	}

	public void setSourceCMId(int sourceCMId) {
		this.sourceCMId = sourceCMId;
	}

	public int getCmtId() {
		return cmtId;
	}

	public void setCmtId(int cmtId) {
		this.cmtId = cmtId;
	}

	public int getMailingId() {
		return mailingId;
	}

	public void setMailingId(int mailingId) {
		this.mailingId = mailingId;
	}

	public String getPhName() {
		return phName;
	}

	public void setPhName(String phName) {
		this.phName = phName;
	}

	public boolean isCreateForMailing() {
		return createForMailing;
	}

	public void setCreateForMailing(boolean createForMailing) {
		this.createForMailing = createForMailing;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<ContentModuleType> getAllCMT() {
		return allCMT;
	}

	public void setAllCMT(List<ContentModuleType> allCMT) {
		this.allCMT = allCMT;
	}

	public int getModuleTypeNumber() {
		if (allCMT != null) {
			return allCMT.size();
		}
		return 0;
	}

	public List<CmsTag> getTags() {
		return tags;
	}

	public void setTags(List<CmsTag> tags) {
		this.tags = tags;
	}

	public void clearData() {
		contentModuleId = 0;
		cmtId = 0;
		sourceCMId = 0;
		name = "";
		description = "";
		content = "";
	}

	public List<Integer> getOldAssignment() {
		return oldAssignment;
	}

	public void setOldAssignment(List<Integer> oldAssignment) {
		this.oldAssignment = oldAssignment;
	}

	@Override
	public void setName(String name) {
		super.setName(name);
	}

	public void setNameNoConvertion(String name) {
		super.setName(name);
	}

	@Override
	public void setDescription(String description) {
		super.setDescription(description);
	}

	public void setDescriptionNoConvertion(String description) {
		super.setDescription(description);
	}

	@Override
	public ActionErrors formSpecificValidate(ActionMapping actionMapping,
								 HttpServletRequest request) {
		ActionErrors actionErrors = new ActionErrors();

		if(action == ContentModuleAction.ACTION_SAVE) {
			updateTags(request, tags);
			if(this.name.length() < 3) {
				actionErrors.add("shortname", new ActionMessage("error.nameToShort"));
			}
			validateUrls(actionErrors);

			if(!actionErrors.isEmpty()) {
				setValidState(false);
			} else {
				setValidState(true);
				for(CmsTag tag : tags) {
					if(tag instanceof CmsImageTag) {
						final CmsImageTag cmsImageTag = (CmsImageTag) tag;
						final String newValue = cmsImageTag.getNewValue();
						if(newValue.length() != 0) {
							cmsImageTag.setValue(newValue);
						}
					}
				}
			}
		}
		return actionErrors;
	}

	private void validateUrls(ActionErrors actionErrors) {
		for(CmsTag tag : tags) {
			if(tag.getType() == TagUtils.TAG_LINK) {
				final String url = tag.getValue();
				if(!(urlValidator.isValid(url) || isEncharedLinck(tag) || isEmailLink(url))) {
					actionErrors.add("url_link_" + tag.getName(),
							new ActionMessage("error.linkUrlWrong"));
				}
			}
			if(tag instanceof CmsImageTag) {
				final CmsImageTag imageTag = (CmsImageTag) tag;
				if(!imageTag.isUpload()) {
					final String url = ((CmsImageTag) tag).getNewValue();
					if(!urlValidator.isValid(url)) {
						actionErrors.add("url_img_" + tag.getName(),
								new ActionMessage("error.externalImageUrlWrong"));
					}
				}
			}
		}
	}

	private boolean isEmailLink(String link) {
		int separatorIndex = link.indexOf(":");
		if(separatorIndex == -1) {
			return false;
		}
		String mailtoString = link.substring(0, separatorIndex);
		if(!mailtoString.toLowerCase().equals("mailto")) {
			return false;
		}
		int emailsEnd = link.indexOf("?");
		if(emailsEnd == -1) {
			emailsEnd = link.length();
		}
		if(emailsEnd - 1 <= separatorIndex) {
			return false;
		}
		String emails = link.substring(separatorIndex + 1, emailsEnd);
		String[] emailsArray = emails.split(",");
		for(String email : emailsArray) {
			if(!GenericValidator.isEmail(email.trim())) {
				return false;
			}
		}
		return true;
	}

	private boolean isEncharedLinck(CmsTag tag) {
		boolean isEncharedLink = false;
		final String value = tag.getValue();
		if(value.startsWith("#") && value.length() > 0) {
			final String substring = value
					.substring(value.indexOf("#") + 1, value.length());
			if(!substring.contains(" ") && !substring.contains("#")) {
				isEncharedLink = true;
			}
		}
		return isEncharedLink;
	}

	private void updateTags(HttpServletRequest request,
							List<CmsTag> tags) {
		Enumeration parameterNames = request.getParameterNames();
		Map<String, String> editors = new HashMap<String, String>();
		while(parameterNames.hasMoreElements()) {
			String name = (String) parameterNames.nextElement();
			if(name.startsWith("editor.")) {
				String id = name.substring(name.indexOf(".") + 1);
				editors.put(id, request.getParameter(name));
			}
		}
		parameterNames = request.getParameterNames();
		while(parameterNames.hasMoreElements()) {
			String name = (String) parameterNames.nextElement();
			if(name.startsWith("cm.")) {
				if(!"FCK".equals(editors.get(name))) {
					handleContentModuleTagEditor(this, request, name, tags);
				}
			} else if(name.startsWith("DataFCKeditor.") ) {
				String id = name.substring(name.indexOf(".") + 1);
				if("FCK".equals(editors.get(id))) {
					handleContentModuleTagEditor(this, request, name, tags);
				}
			}
		}
	}

	private void handleContentModuleTagEditor(ContentModuleForm aForm,
											  HttpServletRequest req, String name,
											  List<CmsTag> tagList) {
        int tagType;
        if (name.indexOf("DataFCKeditor.") != -1) {
            tagType = TagUtils.getType(name.substring(name.indexOf('.')+1));
        } else {
		    tagType = TagUtils.getType(name);
        }
		switch(tagType) {
			case TagUtils.TAG_LINK:
			case TagUtils.TAG_TEXT:
			case TagUtils.TAG_LABEL: {
				String tagName = name.indexOf("DataFCKeditor.") == -1 ?
                        TagUtils.getName(name) : TagUtils.getName(name.substring(name.indexOf('.')+1));
				String newValue = req.getParameter(name);
				CmsTag tag = TagUtils.createTag(tagName, tagType, newValue);
				replaceOldTag(tagList, tag);
				break;
			}
			case TagUtils.TAG_IMAGE: {
				String imageType = req.getParameter(name);
				String tagNameSelect = TagUtils.getName(name);
				String tagName = tagNameSelect
						.substring(0, tagNameSelect.lastIndexOf("."));
				CmsImageTag newTag = TagUtils.createImageTag(tagName, tagType, "", "");
				if(imageType.equals("external")) {
					handelExternalUrl(req, tagList, tagType, tagName, newTag);
				} else if(imageType.equals("upload")) {
					handelUploadUrl(aForm, tagList, tagType, tagName, newTag);
				}
				break;
			}
		}
	}

	private void handelUploadUrl(ContentModuleForm aForm, List<CmsTag> tagList,
								 int tagType, String tagName, CmsImageTag newTag) {
		String fileInputName = "cm." + tagType + "." + tagName + ".file";
		MultipartRequestHandler requestHandler = aForm.getMultipartRequestHandler();
		if(requestHandler != null) {
			Object file = requestHandler.getFileElements().get(fileInputName);
			if(file != null && file instanceof FormFile) {
				FormFile formFile = (FormFile) file;
				String pictureURL = formFile.getFileName();
				for(int tagIndex = 0; tagIndex < tagList.size(); tagIndex++) {
					CmsTag oldTag = tagList.get(tagIndex);
					if(oldTag.getName().equals(newTag.getName())) {
						final CmsImageTag previousImageTag = (CmsImageTag) tagList
								.get(tagIndex);
						if(!previousImageTag.isUpload()) {
							previousImageTag.setNewValue("");
							previousImageTag.setUpload(true);
						}

						if(pictureURL != null && pictureURL.length() != 0) {
							newTag.setFormFile(formFile);
							newTag.setValue(oldTag.getValue());
							newTag.setNewValue(pictureURL);
							tagList.add(tagIndex, newTag);
							tagList.remove(tagIndex + 1);
						}
					}
				}
			}
		}
	}

	private void handelExternalUrl(HttpServletRequest req, List<CmsTag> tagList,
								   int tagType, String tagName, CmsImageTag newTag) {
		newTag.setUpload(false);
		String newValue = req.getParameter("cm." + tagType + "." + tagName + ".url");
		for(int tagIndex = 0; tagIndex < tagList.size(); tagIndex++) {
			if(tagList.get(tagIndex) instanceof CmsImageTag) {
				CmsImageTag oldTag = ((CmsImageTag) tagList.get(tagIndex));
				if(oldTag.getName().equals(newTag.getName())) {
					newTag.setValue(oldTag.getValue());
					newTag.setFormFile(oldTag.getFormFile());
					newTag.setNewValue(newValue);
					tagList.add(tagIndex, newTag);
					tagList.remove(tagIndex + 1);
				}
			}
		}
	}

    public List<ContentModuleCategory> getAllCategories() {
        return allCategories;
    }

    public void setAllCategories(List<ContentModuleCategory> allCategories) {
        this.allCategories = allCategories;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getCategoryToShow() {
        return categoryToShow;
    }

    public void setCategoryToShow(int categoryToShow) {
        this.categoryToShow = categoryToShow;
    }

	private void replaceOldTag(List<CmsTag> tagList, CmsTag newTag) {
		for(int tagIndex = 0; tagIndex < tagList.size(); tagIndex++) {
			CmsTag oldTag = tagList.get(tagIndex);
			if(oldTag.getName().equals(newTag.getName())) {
				tagList.add(tagIndex, newTag);
				tagList.remove(tagIndex + 1);
			}
		}
	}

	@Override
	protected boolean isParameterExcludedForUnsafeHtmlTagCheck( String parameterName, HttpServletRequest request) {
		return !parameterName.equals( "name") && !parameterName.equals( "description");
	}

}