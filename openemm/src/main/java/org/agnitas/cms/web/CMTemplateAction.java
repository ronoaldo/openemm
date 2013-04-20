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

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import org.agnitas.beans.MailingBase;
import org.agnitas.cms.dao.CmsMailingDao;
import org.agnitas.cms.dao.impl.MediaFileDaoImpl;
import org.agnitas.cms.utils.ClassicTemplateGenerator;
import org.agnitas.cms.utils.CmsUtils;
import org.agnitas.cms.utils.dataaccess.CMTemplateManager;
import org.agnitas.cms.utils.dataaccess.MediaFileManager;
import org.agnitas.cms.utils.preview.PreviewImageGenerator;
import org.agnitas.cms.web.forms.CMTemplateForm;
import org.agnitas.cms.webservices.generated.CMTemplate;
import org.agnitas.cms.webservices.generated.MediaFile;
import org.agnitas.dao.MailingDao;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.StrutsActionBase;
import org.agnitas.web.forms.StrutsFormBase;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import org.displaytag.pagination.PaginatedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Action for managing CM templates
 *
 * @author Vyacheslav Stepanov
 */

public class CMTemplateAction extends StrutsActionBase {

	public static final int ACTION_PURE_PREVIEW = ACTION_LAST + 1;
	public static final int ACTION_UPLOAD = ACTION_LAST + 2;
	public static final int ACTION_STORE_UPLOADED = ACTION_LAST + 3;
	public static final int ACTION_ASSIGN_LIST = ACTION_LAST + 4;
	public static final int ACTION_STORE_ASSIGNMENT = ACTION_LAST + 5;
    public static final int ACTION_EDIT_TEMPLATE = ACTION_LAST + 6;
    public static final int ACTION_DELETE_IMAGE_TEMPLATE = ACTION_LAST + 7;
    public static final int ACTION_SAVE_TEMPLATE = ACTION_LAST + 8;

	// @todo will be moved to some other place
	public static final String MEDIA_FOLDER = "template-media";
    public static final String THUMBS_DB = "thumbs.db";

	public static final int LIST_PREVIEW_WIDTH = 500;
	public static final int LIST_PREVIEW_HEIGHT = 400;
	public static final int PREVIEW_MAX_WIDTH = 150;
	public static final int PREVIEW_MAX_HEIGHT = 150;


	public ActionForward execute(ActionMapping mapping, ActionForm form,
								 HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {

		CMTemplateForm aForm;

		ActionMessages errors = new ActionMessages();
		ActionMessages messages = new ActionMessages();
		ActionForward destination = null;

		if(!AgnUtils.isUserLoggedIn(req)) {
			return mapping.findForward("logon");
		}

		if(form != null) {
			aForm = (CMTemplateForm) form;
		} else {
			aForm = new CMTemplateForm();
		}

		AgnUtils.logger().info("Action: " + aForm.getAction());

		// if preview size is changed - return to view page
		if(AgnUtils.parameterNotEmpty(req, "changePreviewSize")) {
			aForm.setAction(CMTemplateAction.ACTION_VIEW);
		}

		// if assign button is pressed - store mailings assignment
		if(AgnUtils.parameterNotEmpty(req, "assign")) {
			aForm.setAction(CMTemplateAction.ACTION_STORE_ASSIGNMENT);
		}

		try {
			switch(aForm.getAction()) {
				case CMTemplateAction.ACTION_LIST:
                    initializeColumnWidthsListIfNeeded(aForm);
					destination = mapping.findForward("list");
					aForm.reset(mapping, req);
					setAvailableCharsets(aForm, req);
					aForm.setAction(CMTemplateAction.ACTION_LIST);
					break;

				case CMTemplateAction.ACTION_ASSIGN_LIST:
                    initializeColumnWidthsListIfNeeded(aForm);
					destination = mapping.findForward("assign_list");
					aForm.reset(mapping, req);
					aForm.setAction(CMTemplateAction.ACTION_ASSIGN_LIST);
					break;

				case CMTemplateAction.ACTION_STORE_ASSIGNMENT:
                    initializeColumnWidthsListIfNeeded(aForm);
					storeMailingAssignment(req, aForm);
					destination = mapping.findForward("assign_list");
					aForm.reset(mapping, req);
					aForm.setAction(CMTemplateAction.ACTION_ASSIGN_LIST);

					messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
					break;

				case CMTemplateAction.ACTION_VIEW:
					loadCMTemplate(req, aForm);
					aForm.setAction(CMTemplateAction.ACTION_SAVE);
					destination = mapping.findForward("view");
					break;

				case CMTemplateAction.ACTION_UPLOAD:
					aForm.setAction(CMTemplateAction.ACTION_STORE_UPLOADED);
					setAvailableCharsets(aForm, req);
					destination = mapping.findForward("upload");
					break;

				case CMTemplateAction.ACTION_STORE_UPLOADED:
					errors = storeUploadedTemplate(aForm, req);
					// if template is uploaded and stored successfuly - go to
					// template edit page, otherwise - stay on upload page to display
					// errors and allow user to repeat his try to upload template
					if(errors.isEmpty()) {
                        AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": create CM template " + aForm.getName());
						loadCMTemplate(req, aForm);
						aForm.setAction(CMTemplateAction.ACTION_SAVE);
						destination = mapping.findForward("view");

						messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
					} else {
						aForm.setAction(CMTemplateAction.ACTION_STORE_UPLOADED);
						destination = mapping.findForward("list");
					}
					break;

				case CMTemplateAction.ACTION_SAVE:
					boolean saveOk = saveCMTemplate(aForm);
					// if save is successful - stay on view page
					// if not - got to list page
					if(saveOk) {
						aForm.setAction(CMTemplateAction.ACTION_SAVE);
						destination = mapping.findForward("view");

						messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
					} else {
						destination = mapping.findForward("list");
						aForm.setAction(CMTemplateAction.ACTION_LIST);
					}
					break;

				case CMTemplateAction.ACTION_PURE_PREVIEW:
					destination = mapping.findForward("pure_preview");
					aForm.reset(mapping, req);
					aForm.setPreview(getCmTemplatePreview(aForm.getCmTemplateId()));
					aForm.setAction(CMTemplateAction.ACTION_PURE_PREVIEW);
					break;

				case CMTemplateAction.ACTION_CONFIRM_DELETE:
					loadCMTemplate(req, aForm);
					aForm.setAction(CMTemplateAction.ACTION_DELETE);
					destination = mapping.findForward("delete");
					break;

				case CMTemplateAction.ACTION_DELETE:
					if(AgnUtils.parameterNotEmpty(req, "kill")) {
						deleteCMTemplate(aForm.getCmTemplateId(), req);
					}
					aForm.setAction(CMTemplateAction.ACTION_LIST);
					destination = mapping.findForward("list");

					messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
					break;

                case CMTemplateAction.ACTION_EDIT_TEMPLATE:
                    loadCMTemplate(req, aForm);
                    aForm.setLMediaFile(this.getMediaFile(aForm.getCmTemplateId()));
                    aForm.setContentTemplateNoConvertion(getContentTemplate(aForm.getCmTemplateId()));
                    aForm.setAction(CMTemplateAction.ACTION_EDIT_TEMPLATE);
                    destination = mapping.findForward("edit_template");
                    break;
                case CMTemplateAction.ACTION_DELETE_IMAGE_TEMPLATE:
                    this.deleteImage(req);
                    aForm.setLMediaFile(this.getMediaFile(aForm.getCmTemplateId()));
                    loadCMTemplate(req, aForm);
                    aForm.setAction(CMTemplateAction.ACTION_EDIT_TEMPLATE);
                    destination = mapping.findForward("edit_template");
                    break;
                case CMTemplateAction.ACTION_SAVE_TEMPLATE:
                    if (aForm.getErrorFieldMap().isEmpty()) {
                        this.saveEditTemplate(req, aForm);
                        aForm.setLMediaFile(this.getMediaFile(aForm.getCmTemplateId()));
                        aForm.setAction(CMTemplateAction.ACTION_EDIT_TEMPLATE);
                        destination = mapping.findForward("edit_template");
                        if (errors.isEmpty()) {
                            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
                            req.removeAttribute("save_ok");
                        }
                        break;
                    } else{
                        aForm.setAction(CMTemplateAction.ACTION_EDIT_TEMPLATE);
                        destination = mapping.findForward("edit_template");
                        for(String key : aForm.getErrorFieldMap().keySet() ){
                          errors.add(key,new ActionMessage(aForm.getErrorFieldMap().get(key)));
                        }
                        req.setAttribute("save_ok","false");
                    }
			}
		}
		catch(Exception e) {
			AgnUtils.logger()
					.error("Error while executing action with CM Template: " + e + "\n" +
							AgnUtils.getStackTrace(e));
			errors.add(ActionMessages.GLOBAL_MESSAGE,
					new ActionMessage("error.exception"));
		}

		// collect list of CM Templates for list-page
		if(destination != null && "list".equals(destination.getName())) {
			try {
				setNumberOfRows(req, (StrutsFormBase) form);
				req.setAttribute("cmTemplateList", getCMTemplateList(req));
			} catch(Exception e) {
				AgnUtils.logger()
						.error("cmTemplateList: " + e + "\n" + AgnUtils.getStackTrace(e));
				errors.add(ActionMessages.GLOBAL_MESSAGE,
						new ActionMessage("error.exception"));
			}
		}

		// collect list of Mailings for assign-page
		if(destination != null && "assign_list".equals(destination.getName())) {
			try {
				setNumberOfRows(req, (StrutsFormBase) form);
				req.setAttribute("mailingsList", getMailingsList(req, aForm));
			} catch(Exception e) {
				AgnUtils.logger().error("getMailingsList: " + e + "\n" +
						AgnUtils.getStackTrace(e));
				errors.add(ActionMessages.GLOBAL_MESSAGE,
						new ActionMessage("error.exception"));
			}
		}

		// Report any errors we have discovered back to the original form
		if(!errors.isEmpty()) {
			saveErrors(req, errors);
		}

		if(!messages.isEmpty()) {
			saveMessages(req, messages);
		}

		return destination;
	}

    protected void initializeColumnWidthsListIfNeeded(CMTemplateForm aForm){
        if (aForm.getColumnwidthsList() == null) {
            aForm.setColumnwidthsList(getInitializedColumnWidthList(3));
        }
    }

	private void setAvailableCharsets(CMTemplateForm aForm, HttpServletRequest request) {
		List<String> charsets = new ArrayList<String>();
		for(String charset : CMTemplateForm.CHARTERSET_LIST) {
			if(allowed("charset.use." + charset.replaceAll("-", "_"), request)) {
				charsets.add(charset);
			}
		}
		aForm.setAvailableCharsets(charsets);
	}

	private void storeMailingAssignment(HttpServletRequest req, CMTemplateForm aForm) {
		List<Integer> assignedMailings = new ArrayList<Integer>();
		Enumeration parameterNames = req.getParameterNames();
		while(parameterNames.hasMoreElements()) {
			String paramName = (String) parameterNames.nextElement();
			if(paramName.startsWith("assign_mailing_")) {
				String value = req.getParameter(paramName);
				if(value != null) {
					if(value.startsWith("mailing_")) {
						value = value.substring("mailing_".length());
						assignedMailings.add(Integer.parseInt(value));
					}
				}
			}
		}
		List<Integer> mailingsToAssign = new ArrayList<Integer>();
		List<Integer> mailingsToDeassign = new ArrayList<Integer>();
		Map<Integer, Integer> oldAssignment = aForm.getOldAssignment();
		for(Integer mailingId : oldAssignment.keySet()) {
			if(!assignedMailings.contains(mailingId) &&
					oldAssignment.get(mailingId) == aForm.getCmTemplateId()) {
				mailingsToDeassign.add(mailingId);
			}
		}
		for(Integer assignedMailingId : assignedMailings) {
			if(oldAssignment.get(assignedMailingId) == null) {
				mailingsToAssign.add(assignedMailingId);
			} else if(oldAssignment.get(assignedMailingId) != aForm.getCmTemplateId()) {
				mailingsToDeassign.add(assignedMailingId);
				mailingsToAssign.add(assignedMailingId);
			}
		}

		getTemplateManager().removeMailingBindings(mailingsToDeassign);
		getTemplateManager()
				.addMailingBindings(aForm.getCmTemplateId(), mailingsToAssign);
		final ClassicTemplateGenerator classicTemplateGenerator =
				(ClassicTemplateGenerator) getWebApplicationContext()
						.getBean("ClassicTemplateGenerator");
		for(Integer mailingId : mailingsToAssign) {
			classicTemplateGenerator.generate(mailingId, req, false);
		}
		for(Integer mailingId : mailingsToDeassign) {
			classicTemplateGenerator.generate(mailingId, req, false);
		}
	}

	private ActionErrors storeUploadedTemplate(CMTemplateForm aForm,
											   HttpServletRequest req) {
		ActionErrors errors = new ActionErrors();
		FormFile file = aForm.getTemplateFile();
		if(file != null) {
			if(!file.getFileName().toLowerCase().endsWith("zip")) {
				errors.add(ActionMessages.GLOBAL_MESSAGE,
						new ActionMessage("error.cmtemplate.filetype"));
			} else {
				try {
					byte[] fileData = file.getFileData();
					if(fileData.length > 0) {
						int templateId = readArchivedCMTemplate(aForm,
								file.getInputStream(), req);
						if(templateId == -1) {
							errors.add(ActionMessages.GLOBAL_MESSAGE,
									new ActionMessage("error.cmtemplate.notemplatefile"));
						} else {
							aForm.setCmTemplateId(templateId);
							final int maxWidth = PREVIEW_MAX_WIDTH;
							final int maxHeight = PREVIEW_MAX_HEIGHT;
							final HttpSession session = req.getSession();
							final PreviewImageGenerator previewImageGenerator =
									new PreviewImageGenerator(getWebApplicationContext(),
											session,
											maxWidth,
											maxHeight);
							previewImageGenerator.generatePreview(templateId, 0, 0);
						}
						return errors;
					}
				} catch(IOException e) {
					AgnUtils.logger()
							.error("Error while uploading CM Template: " + e + "\n" +
									AgnUtils.getStackTrace(e));
				}
			}
		}
		if(errors.isEmpty()) {
			errors.add(ActionMessages.GLOBAL_MESSAGE,
					new ActionMessage("error.cmtemplate.upload"));
		}
		return errors;
	}

	public int readArchivedCMTemplate(CMTemplateForm aForm, InputStream stream,
									  HttpServletRequest request) {
		ZipInputStream zipInputStream = new ZipInputStream(stream);
		ZipEntry entry;
		String templateBody = null;
		// binds image name in zip to image id in CCR (Central Content Repository)
		Map<String, Integer> imageBindMap = new HashMap<String, Integer>();
		int newTemplateId = createEmptyCMTemplate(request);
        if(newTemplateId == -1){
            return -1;
        }
		try {
			while((entry = zipInputStream.getNextEntry()) != null) {
				String entryName = entry.getName();
				// hack for ignoring MACOS archive system folders
				if(entryName.contains("__MACOSX")) {
					continue;
				}
				// skip if directory
				if(entryName.endsWith("/")) {
					continue;
				}
				// if file is in media-folder - store it in CCR
				if(entryName.startsWith(MEDIA_FOLDER)) {
                    // thumbs.db is ignored by EMM
                    if (!StringUtils.endsWithIgnoreCase(entryName, THUMBS_DB)) {
                        byte[] fileData = getEntryData(zipInputStream, entry);
                        int mediaFileId = storeMediaFile(fileData, entryName, newTemplateId,
                                request);
                        if (mediaFileId != -1) {
                            imageBindMap.put(entryName, mediaFileId);
                        }
                    }
				} else if(entryName.endsWith(".html") && templateBody == null) {
					// first html file that was found in root folder of
					// zip-archive is considered to be a template-file
					byte[] templateData = getEntryData(zipInputStream, entry);
					templateBody = new String(templateData,
							Charset.forName(aForm.getCharset()).name());
				}
			}
			zipInputStream.close();
		} catch(IOException e) {
			AgnUtils.logger().error("Error occured reading CM template from zip: ", e);
		}
		if(templateBody == null) {
			getTemplateManager().deleteCMTemplate(newTemplateId);
			getMediaManager().removeMediaFilesForCMTemplateId(newTemplateId);
			return -1;
		} else {
			templateBody = replacePictureLinks(templateBody, imageBindMap);
			try {
				getTemplateManager().updateContent(newTemplateId,
						templateBody.getBytes(Charset.forName("UTF-8").name()));
			} catch(UnsupportedEncodingException e) {
				AgnUtils.logger().warn("Wrong charset name", e);
			}
			return newTemplateId;
		}
	}

	private byte[] getEntryData(ZipInputStream zipInputStream, ZipEntry entry) throws
			IOException {
		byte[] fileData = new byte[(int) entry.getSize()];
		byte[] buf = new byte[2048];
		int bytesRead = 0;
		int dataIndex = 0;
		while(bytesRead != -1) {
			bytesRead = zipInputStream.read(buf);
			for(int i = 0; i < bytesRead; i++) {
				if(dataIndex < fileData.length && i < buf.length) {
					fileData[dataIndex] = buf[i];
					dataIndex++;
				}
			}
		}
		return fileData;
	}

	private String replacePictureLinks(String templateBody,
									   Map<String, Integer> imageBindMap) {
		for(String imageName : imageBindMap.keySet()) {
			Integer imageId = imageBindMap.get(imageName);
			String newImageUrl = CmsUtils.generateMediaFileUrl(imageId);
			templateBody = templateBody.replaceAll("./" + imageName, newImageUrl);
			templateBody = templateBody.replaceAll(imageName, newImageUrl);
		}
		return templateBody;
	}

	private int createEmptyCMTemplate(HttpServletRequest request) {
		Locale locale = (Locale) request.getSession()
				.getAttribute(org.apache.struts.Globals.LOCALE_KEY);
		ResourceBundle bundle = ResourceBundle.getBundle("cmsmessages", locale);
		CMTemplate template = new CMTemplate();
		template.setCompanyId(AgnUtils.getCompanyID(request));
		template.setName(bundle.getString("NewCMTemplateName"));
		template.setDescription(bundle.getString("NewCMDescription"));
		template.setContent(new byte[]{0});
		template = getTemplateManager().createCMTemplate(template);
		return template.getId();
	}

	private int storeMediaFile(byte[] fileData, String entryName,
							   int cmTemplateId, HttpServletRequest request) {
		// get mime-type for file
		String mimeType = CmsUtils.UNKNOWN_MIME_TYPE;
		Collection mimeTypes = MimeUtil.getMimeTypes(entryName);
		if(!mimeTypes.isEmpty()) {
			MimeType type = (MimeType) mimeTypes.iterator().next();
			mimeType = type.toString();
		}
		// store media file
		MediaFile mediaFile = new MediaFile();
		mediaFile.setCompanyId(AgnUtils.getCompanyID(request));
		mediaFile.setCmTemplateId(cmTemplateId);
		mediaFile.setName(entryName);
		mediaFile.setMimeType(mimeType);
		mediaFile.setContent(fileData);
		mediaFile = getMediaManager().createMediaFile(mediaFile);
		return mediaFile.getId();
	}

	private boolean saveCMTemplate(CMTemplateForm aForm) {
		return getTemplateManager().updateCMTemplate(aForm.getCmTemplateId(),
				aForm.getName(), aForm.getDescription());
	}

	private void loadCMTemplate(HttpServletRequest request, CMTemplateForm aForm) {
		CMTemplate template = getTemplateManager().getCMTemplate(aForm.getCmTemplateId());
		if(template != null) {
			aForm.setName(template.getName());
			aForm.setDescription(template.getDescription());
		}
        AgnUtils.userlogger().info(AgnUtils.getAdmin(request).getUsername() + ": do load CM template " + aForm.getName());
	}

    private void deleteImage(HttpServletRequest request){
        int idImage = Integer.parseInt(request.getParameter("cmTemplateMediaFileId"));
        this.getMediaManager().removeMediaFile(idImage);
    }

    private List<MediaFile> getMediaFile(int cmTemplateId) {
        return getMediaManager().getMediaFilesForContentModuleTemplate(cmTemplateId);
    }

    private void saveEditTemplate(HttpServletRequest request, CMTemplateForm aForm) throws Exception{
        for (MediaFile mediaFile : aForm.getLMediaFile()) {
            final String imageUploadOrExternalSelect = request.getParameter(String.format("imageUploadOrExternal.%s.select", mediaFile.getId()));
            final String imageUploadOrExternalUrl = request.getParameter(String.format("imageUploadOrExternal.%s.url", mediaFile.getId()));
            final String changeImageSelect = request.getParameter(String.format("changeImage.%s.select", mediaFile.getId()));
            if (!StringUtils.isEmpty(changeImageSelect) && changeImageSelect.equals("on")) {
                if (!StringUtils.isEmpty(imageUploadOrExternalSelect)) {
                    if (imageUploadOrExternalSelect.equals("upload")) {
                        saveUploadImage(aForm, mediaFile.getId());
                    } else if (imageUploadOrExternalSelect.equals("external")) {
                        if (!StringUtils.isEmpty(imageUploadOrExternalSelect)){
                           getMediaManager().updateMediaFile(new MediaFile(0, 0, getImageToUrl(imageUploadOrExternalUrl), 0, 0, mediaFile.getId(), 0, null, null));
                        }
                    }
                }
            }
        }
        MediaFile mediaFile = new MediaFile(aForm.getCmTemplateId(), AgnUtils.getCompanyID(request),
                null, 0, 0, -1, 0, "image/jpeg", null);
        final String imageUploadOrExternalNew = request.getParameter("imageUploadOrExternal.new.select");
        final String changeImageNewSelect = request.getParameter("changeImage.new.select");
        if (!StringUtils.isEmpty(changeImageNewSelect) &&  changeImageNewSelect.equals("on")) {
            mediaFile.setName("template-media/" + request.getParameter("name.new.text"));
            if (!StringUtils.isEmpty(imageUploadOrExternalNew)) {
                if (imageUploadOrExternalNew.equals("upload")) {
                    if (aForm.getLNewFile().containsKey(-1)) {
						String fileName = aForm.getLNewFile().get(-1).getFileName();
				     	mediaFile.setName("template-media/" + fileName);
						mediaFile.setContent(aForm.getLNewFile().get(-1).getFileData());
                        getMediaManager().createMediaFile(mediaFile);
                    }
                } else if (imageUploadOrExternalNew.equals("external")) {
                    final String image = request.getParameter("imageUploadOrExternal.new.url");
                    final String type = image.substring(image.lastIndexOf('.') + 1);
                    if (type.equals("gif")) {
                        mediaFile.setMimeType("image/gif");
                    } else if (type.equals("png")) {
                        mediaFile.setMimeType("image/png");
                    }
                    if (!StringUtils.isEmpty(image)){
                        mediaFile.setContent(getImageToUrl(image));
                    }
                    getMediaManager().createMediaFile(mediaFile);
                }
            }
        }
        replaceAgnitasTagImage(aForm, request);
    }

    private void replaceAgnitasTagImage(CMTemplateForm aForm,HttpServletRequest req){
        String content = aForm.getContentTemplate();
        String sPattern = "\\[agnIMAGE\\s*name=\"(.*)\"\\]";
        Pattern pattern = Pattern.compile(sPattern);
        Matcher matcher = pattern.matcher(content);
        MediaFile mediaFile = null;
        while (matcher.find()) {
            mediaFile = getMediaManager().getMediaFileForContentModelAndMediaName(aForm.getCmTemplateId(),matcher.group(1));
            if (mediaFile == null){continue;}
            String imgTag = CmsUtils.generateMediaFileUrl(mediaFile.getId());
            content = content.replaceFirst(sPattern,imgTag);
        }
        aForm.setContentTemplateNoConvertion(content);
        getTemplateManager().updateContent(aForm.getCmTemplateId(),content.getBytes(Charset.forName("UTF-8")));
        new PreviewImageGenerator(getWebApplicationContext(),req.getSession(),	PREVIEW_MAX_WIDTH, PREVIEW_MAX_HEIGHT).
                generatePreview(aForm.getCmTemplateId(), 0, 0);
    }

    private String getContentTemplate(int cmTemplateId){
        return new String(getTemplateManager().getCMTemplate(cmTemplateId).getContent(), Charset.forName("UTF-8"));
    }

    private void saveUploadImage(CMTemplateForm aForm,int id) throws IOException{
		if(aForm.getLNewFile().containsKey(id)) {
			Map<Integer, FormFile> mTemp = aForm.getLNewFile();
			FormFile ffTemp = mTemp.get(id);
			getMediaManager().updateMediaFile(new MediaFile(0, 0, ffTemp.getFileData(), 0, 0, id, 0, null, null));
		}
    }

    private byte[] getImageToUrl(String address) throws IOException {
        URL url = new URL(address);
        InputStream urlInputStream = url.openStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1];
        int n = 0;
        while ((n = urlInputStream.read(buffer, 0, buffer.length)) > 0) {
            outputStream.write(buffer);
        }
        return outputStream.toByteArray();
    }

    private MediaFileDaoImpl getMediaFileDao(){
        MediaFileDaoImpl mediaFileDao = new MediaFileDaoImpl();
        mediaFileDao.setApplicationContext(getWebApplicationContext());
        return mediaFileDao;
    }

	protected void deleteCMTemplate(int templateId, HttpServletRequest req) {
        getTemplateManager().deleteCMTemplate(templateId);
		getMediaManager().removeMediaFilesForCMTemplateId(templateId);
        AgnUtils.userlogger().info(AgnUtils.getAdmin(req).getUsername() + ": delete CM template " + templateId);
	}

	private String getCmTemplatePreview(int cmTemplateId) {
		CMTemplate template = getTemplateManager().getCMTemplate(cmTemplateId);
		if(template != null) {
			try {
                String templateContent = new String(template.getContent(), Charset.forName("UTF-8").name());
                templateContent = CmsUtils.appendImageURLsWithSystemUrl(templateContent);
                return templateContent;
			} catch(UnsupportedEncodingException e) {
				AgnUtils.logger().warn("Wrong charser name", e);
			}
		}
		return "";
	}

	/**
	 * Gets list of CM Templates for overview-page table
	 */
	public List<CMTemplate> getCMTemplateList(HttpServletRequest request) throws
			IllegalAccessException, InstantiationException {
		return getTemplateManager().getCMTemplates(AgnUtils.getCompanyID(request));
	}

	/**
	 * Gets list of mailings for assign-page
	 */
	public PaginatedList getMailingsList(HttpServletRequest req,
										 CMTemplateForm templateForm) throws
			IllegalAccessException, InstantiationException {
		PaginatedList mailingList = getPageMailings(req, templateForm,
				(MailingDao) getBean("MailingDao"));

		List<Integer> mailingIds = getMailingIds(mailingList);

		CmsMailingDao cmsMailingDao = (CmsMailingDao) getWebApplicationContext()
				.getBean("CmsMailingDao");

		List<Integer> mailingWithNoClassicTemplate =
				cmsMailingDao.getMailingsWithNoClassicTemplate(mailingIds,
						AgnUtils.getCompanyID(req));

		Map<Integer, Integer> mailBinding = getTemplateManager()
				.getMailingBinding(mailingIds);
		templateForm.setOldAssignment(mailBinding);

        List<Map> resultList = new ArrayList<Map>();

		for(Object object : mailingList.getList()) {
			Number mailingId;
			String shortname;
			String description;
			if (object instanceof MailingBase) {
			MailingBase mailingBean = (MailingBase) object;
				mailingId = mailingBean.getId();
				shortname = mailingBean.getShortname();
				description = mailingBean.getDescription();
			} else {
				Map mailingMap = (Map) object;
				mailingId = (Number)mailingMap.get("mailingid");
				shortname = String.valueOf(mailingMap.get("shortname"));
				description = AgnUtils.getStringFromNull(String.valueOf(mailingMap.get("description")));
			}
			Integer bindTemplate = mailBinding.get(mailingId.intValue());
			boolean assigned = bindTemplate != null &&
					bindTemplate == templateForm.getCmTemplateId();

            Map newBean = new HashMap();
            newBean.put("mailingid", mailingId);
			newBean.put("shortname", shortname);
			newBean.put("description", description);
			newBean.put("hasCMTemplate", bindTemplate != null);
			newBean.put("assigned", assigned);
			newBean.put("hasClassicTemplate",
					!mailingWithNoClassicTemplate.contains(mailingId.intValue()));
			resultList.add(newBean);
		}
		mailingList.getList().clear();
		mailingList.getList().addAll(resultList);
		return mailingList;
	}

	public static PaginatedList getPageMailings(HttpServletRequest req,
												StrutsFormBase aForm,
												MailingDao mailingDao) {
		String sort1 = req.getParameter("sort");
		if(sort1 == null) {
			sort1 = aForm.getSort();
		} else {
			aForm.setSort(sort1);
		}
		String sort = sort1;
		String direction = req.getParameter("dir");
		if(direction == null) {
			direction = aForm.getOrder();
		} else {
			aForm.setOrder(direction);
		}
		String pageStr = req.getParameter("page");
		if(pageStr == null || "".equals(pageStr.trim())) {
			if(aForm.getPage() == null || "".equals(aForm.getPage().trim())) {
				aForm.setPage("1");
			}
			pageStr = aForm.getPage();

		} else {
			aForm.setPage(pageStr);
		}
		if(aForm.isNumberOfRowsChanged()) {
			aForm.setPage("1");
			aForm.setNumberOfRowsChanged(false);
			pageStr = "1";
		}
		int page = Integer.parseInt(pageStr);
		int rownums = aForm.getNumberofRows();
		PaginatedList mailingList = mailingDao.getMailingList(AgnUtils.getCompanyID(req),
				"0", false, sort, direction, page, rownums);
		return mailingList;
	}

	public static List<Integer> getMailingIds(PaginatedList mailingList) {
		List<Integer> mailingIds = new ArrayList<Integer>();
		for(Object object : mailingList.getList()) {
			int mailingId = object instanceof  MailingBase ? ((MailingBase) object).getId() : ((Number)((Map) object).get("mailingid")).intValue();
			mailingIds.add(mailingId);//mailingBean.getId());
		}
		return mailingIds;
	}

	private CMTemplateManager getTemplateManager() {
		return CmsUtils.getCMTemplateManager(getWebApplicationContext());
	}

	private MediaFileManager getMediaManager() {
		return CmsUtils.getMediaFileManager(getWebApplicationContext());
	}

}