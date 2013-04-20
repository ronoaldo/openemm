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

import org.agnitas.cms.web.CMTemplateAction;
import org.agnitas.cms.webservices.generated.MediaFile;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.UrlValidator;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;
import org.apache.struts.upload.MultipartRequestHandler;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.util.*;


/**
 * @author Vyacheslav Stepanov
 */
public class CMTemplateForm extends CmsBaseForm {

	protected int cmTemplateId;

	private FormFile templateFile;

    private Map<Integer,FormFile> lNewFile;

    private List<MediaFile> lMediaFile;

    private String contentTemplate;

	public static final List<String> CHARTERSET_LIST = Arrays
			.asList("utf-8", "iso-8859-1", "iso-8859-15", "gb2312");

	private List<String> availableCharsets;

	private String charset = CHARTERSET_LIST.get(0);

	protected Map<Integer, Integer> oldAssignment;

    private UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https"});

    private Map<String, String> errorFieldMap;

    //public static final List<String> PREVIEW_MODE_LIST = Arrays
	//		.asList("in table column", "in popup window");

    public static final int  PREVIEW_MODE_COLUMN = 0;

    public static final int  PREVIEW_MODE_POPUP = 1;

    private int previewMode = 0;

    public Map<String, String> getErrorFieldMap() {
        return errorFieldMap;
    }

    public void setErrorFieldMap(Map<String, String> errorFieldMap) {
        this.errorFieldMap = errorFieldMap;
    }

    public int getCmTemplateId() {
		return cmTemplateId;
	}

	public void setCmTemplateId(int cmTemplateId) {
		this.cmTemplateId = cmTemplateId;
	}

	public void setAvailableCharsets(List<String> charsets) {
		availableCharsets = charsets;
	}

	public List<String> getAvailableCharsets() {
		return availableCharsets;
	}

	public FormFile getTemplateFile() {
		return templateFile;
	}

	public void setTemplateFile(FormFile templateFile) {
		this.templateFile = templateFile;
	}

	public Map<Integer, Integer> getOldAssignment() {
		return oldAssignment;
	}

	public void setOldAssignment(Map<Integer, Integer> oldAssignment) {
		this.oldAssignment = oldAssignment;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

    public List<MediaFile> getLMediaFile() {
        return lMediaFile;
    }

    public void setLMediaFile(List<MediaFile> lMediaFile) {
        this.lMediaFile = lMediaFile;
    }

    public Map<Integer, FormFile> getLNewFile() {
        return lNewFile;
    }

    public void setLNewFile(Map<Integer, FormFile> lNewFile) {
        this.lNewFile = lNewFile;
    }

    public String getContentTemplate() {
        return contentTemplate;
    }

    public void setContentTemplate(String contentTemplate) {
		setContentTemplateNoConvertion(contentTemplate);
    }

	public void setContentTemplateNoConvertion(String contentTemplate) {
        this.contentTemplate = contentTemplate;
    }

    public int getPreviewMode() {
        return previewMode;
    }

    public void setPreviewMode(int previewMode) {
        this.previewMode = previewMode;
    }

    /**
	 * Validate the properties that have been set from this HTTP request, and
	 * return an <code>ActionErrors</code> object that encapsulates any
	 * validation errors that have been found. If no errors are found, return
	 * <code>null</code> or an <code>ActionErrors</code> object with no
	 * recorded error messages.
	 *
	 * @param mapping The mapping used to select this instance
	 * @param request The servlet request we are processing
	 * @return errors
	 */
	public ActionErrors formSpecificValidate(ActionMapping mapping,
								 HttpServletRequest request) {
		ActionErrors actionErrors = new ActionErrors();

		try {
            this.handelExternaleUrl(this,request,actionErrors);
            this.handelUploadUrl(this, request);
			if(templateFile != null) {
				String utf8Name = URLEncoder.encode(templateFile.getFileName(), "utf-8");
				if(!templateFile.getFileName().equals(utf8Name)) {
					actionErrors.add("global",
							new ActionMessage("error.mailing.hosted_image_filename"));
				}
			}
			if(action == CMTemplateAction.ACTION_SAVE) {
				if(name.length() < 3) {
					actionErrors.add("shortname", new ActionMessage("error.nameToShort"));
				}
			}
		} catch(Exception e) {
			// do nothing
		}
		return actionErrors;
	}
    private void handelUploadUrl(CMTemplateForm aForm, HttpServletRequest request) {
        final Map<String, String> errorFieldMap = this.getErrorFieldMap();
        MultipartRequestHandler requestHandler = aForm.getMultipartRequestHandler();
        final Map<Integer, FormFile> lFormFile = new HashMap<Integer, FormFile>();
        for(MediaFile mediaFile : aForm.getLMediaFile()) {
			String uploadOrExternal = request.getParameter("imageUploadOrExternal." + mediaFile.getId() + ".select");
			boolean isUpload = (!StringUtils.isEmpty(uploadOrExternal) && uploadOrExternal.equals("upload"));
			String fileInputName = String.format("imageUploadOrExternal.%s.file", mediaFile.getId());
            if (requestHandler != null && isUpload) {
                FormFile file = (FormFile) requestHandler.getFileElements().get(fileInputName);
                if (file != null) {
                    if (!StringUtils.isEmpty(file.getFileName())) {
                        lFormFile.put(mediaFile.getId(), (FormFile) file);
                    } else {
                        errorFieldMap.put("url_img_upload_" + mediaFile.getId(), "error.import.no_file");
                    }
                }
            }
        }
        if(requestHandler != null) {
			String uploadOrExternal = request.getParameter("imageUploadOrExternal.new.select");
			boolean isUpload = (!StringUtils.isEmpty(uploadOrExternal) && uploadOrExternal.equals("upload"));
            FormFile file = (FormFile) requestHandler.getFileElements().get("imageUploadOrExternal.new.file");
            if(file != null && isUpload) {
                if(!StringUtils.isEmpty(file.getFileName())) {
                    lFormFile.put(-1, (FormFile) file);
                } else {
                    errorFieldMap.put("url_img_new_upload", "error.import.no_file");
                }
            }
        }

        if(!lFormFile.isEmpty()) {
            aForm.setLNewFile(lFormFile);
        }
        this.setErrorFieldMap(errorFieldMap);
    }

    private void handelExternaleUrl(CMTemplateForm aForm, HttpServletRequest request, ActionErrors actionErrors) {
        final Map<String,String> errorFieldMap = new Hashtable<String,String>();
        for(MediaFile mediaFile : aForm.getLMediaFile()) {
            String externaleImage =  request.getParameter(String.format("imageUploadOrExternal.%s.select", mediaFile.getId()));
            if(!StringUtils.isEmpty(externaleImage) && externaleImage.equals("external")){
            this.validateUrl(String.format("url_img_%s", mediaFile.getId()),
            	request.getParameter(String.format("imageUploadOrExternal.%s.url", mediaFile.getId())), errorFieldMap);
            }
        }
        String externaleNewImage =  request.getParameter("imageUploadOrExternal.new.select");
        if(!StringUtils.isEmpty(externaleNewImage) && externaleNewImage.equals("external")) {
            final String imageUrl = request.getParameter("imageUploadOrExternal.new.url");
            this.validateUrl("url_img_new", imageUrl, errorFieldMap);
            final String urlImageName = request.getParameter("name.new.text");
            if(StringUtils.isEmpty(urlImageName)){
                errorFieldMap.put("url_img_name","error.cmtemplate.edit.nameadd");
            }
        }
        this.setErrorFieldMap(errorFieldMap);
    }

    private void validateUrl(String parametr, String url, Map<String,String> errorFieldMap) {
        if(!urlValidator.isValid(url)) {
            errorFieldMap.put(parametr,"error.linkUrlWrong");
        }
    }

	@Override
	protected boolean isParameterExcludedForUnsafeHtmlTagCheck( String parameterName, HttpServletRequest request) {
		return parameterName.equals( "contentTemplate");
	}
}