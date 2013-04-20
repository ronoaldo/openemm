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

package org.agnitas.cms.utils;

import org.agnitas.beans.DynamicTag;
import org.agnitas.beans.DynamicTagContent;
import org.agnitas.beans.Mailing;
import org.agnitas.beans.MailingComponent;
import org.agnitas.beans.factory.DynamicTagContentFactory;
import org.agnitas.beans.factory.DynamicTagFactory;
import org.agnitas.beans.factory.MailingComponentFactory;
import org.agnitas.cms.dao.impl.CmsMailingDaoImpl;
import org.agnitas.cms.utils.dataaccess.CMTemplateManager;
import org.agnitas.cms.utils.dataaccess.ContentModuleManager;
import org.agnitas.cms.utils.dataaccess.MediaFileManager;
import org.agnitas.cms.webservices.generated.CMTemplate;
import org.agnitas.cms.webservices.generated.ContentModule;
import org.agnitas.cms.webservices.generated.ContentModuleLocation;
import org.agnitas.cms.webservices.generated.MediaFile;
import org.agnitas.dao.MailingDao;
import org.agnitas.util.AgnUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import java.util.Enumeration;

/**
 * This class bind cms`s template and content modules with
 * emm`s mailings dyn tag content.
 *
 * @author Viktor Gema
 * @author Igor Nesterenko
 */
public class ClassicTemplateGenerator implements ApplicationContextAware {
	private ContentModuleManager contenModuleManager;
	private CMTemplateManager cmTemplateManager;
	private MediaFileManager mediaFileManager;
	private ApplicationContext applicationContext;
	private String imageUrlPattern;
    private MailingDao mailingDao;
    private DynamicTagFactory dynamicTagFactory;
    private DynamicTagContentFactory dynamicTagContentFactory;
    private MailingComponentFactory mailingComponentFactory;

    public void setMailingDao(MailingDao mailingDao) {
        this.mailingDao = mailingDao;
    }

    public void setDynamicTagFactory(DynamicTagFactory dynamicTagFactory) {
        this.dynamicTagFactory = dynamicTagFactory;
    }

    public void setDynamicTagContentFactory(DynamicTagContentFactory dynamicTagContentFactory) {
        this.dynamicTagContentFactory = dynamicTagContentFactory;
    }

    public void setMailingComponentFactory(MailingComponentFactory mailingComponentFactory) {
        this.mailingComponentFactory = mailingComponentFactory;
    }

    public void generate(int mailingId, HttpServletRequest request) {
        // by default checkMailingType=true, copyImages=false
		generate(mailingId, request, true, false);
	}

    public void generate(int mailingId, HttpServletRequest request, boolean checkMailingType) {
        generate(mailingId, request, checkMailingType, false);
    }

	/**
	 * Method perform only mailings contained cms`s elements, clears previous
	 * mailing`s contetn and write new from cms`s template
	 * and content modules, if cmTemplate dosen`t exist it adds
	 * default tag`s name.
	 *
	 * @param mailingId mailing`s id to attach classic template content
	 * @param checkMailingType do we need to check that it is CMS-mailing?
	 */
	public void generate(int mailingId, HttpServletRequest request, boolean checkMailingType, boolean copyImages) {
        // Mailing IDs start from 1. Mailing ID = 0 is invalid situation.
        // generating a preview for mailing with id 0 will cause creating a new mailing with companyId o
        if (mailingId == 0) {
            return;
        }

        final int adminId = AgnUtils.getAdmin(request).getAdminID();
        final int companyId = AgnUtils.getCompanyID(request);
		final Mailing mailing = mailingDao.getMailing(mailingId, companyId);
		if(mailing != null) {
			if(!checkMailingType || isCmsMailing(mailingId)) {
				cleanMailingContent(mailing);
				if(isCmsMailing(mailingId)) {
					if(copyImages) {
						removeMailingImageComponents(mailing);
					}
					bindWithCmTemplate(mailing, copyImages, adminId);
					bindWithContentModules(mailing, copyImages);
				}
				try {
					mailing.buildDependencies(true, applicationContext);
				} catch(Exception e) {
					AgnUtils.logger().warn("Can`t build mailing dependencies", e);
				}
				mailingDao.saveMailing(mailing);
			}
		}
	}

	private void cleanMailingContent(Mailing mailing) {
        List<String> removedDynTags = mailing.cleanupDynTags(new Vector());
        if (isCmsMailing(mailing.getId())) {
            for (String dynName : removedDynTags)
                mailingDao.cleanupContentForDynName(mailing.getId(), dynName, mailing.getCompanyID());
        }

		//mailing.cleanupTrackableLinks(new Vector());
		MailingComponent htmlTemplate = mailing.getHtmlTemplate();
		if(htmlTemplate != null) {
			htmlTemplate.setEmmBlock(CmsMailingDaoImpl.DEFAULT_MAILING_TEMPLATE);
		}
		MailingComponent textTemplate = mailing.getTextTemplate();
		if(textTemplate != null) {
			textTemplate.setEmmBlock("");
		}
	}

	/**
	 * Used by spring`s dependency injection
	 *
	 * @param contenModuleManager
	 */
	public void setContenModuleManager(ContentModuleManager contenModuleManager) {
		this.contenModuleManager = contenModuleManager;
	}

	/**
	 * Used by spring`s dependency injection
	 *
	 * @param imageUrlPattern this image url pattern determine
	 *                        which url will be replaced to agn tag
	 * @see ClassicTemplateGenerator#generate(int, HttpServletRequest)
	 */
	public void setImageUrlPattern(String imageUrlPattern) {
		this.imageUrlPattern = imageUrlPattern;
	}

	/**
	 * Used by spring`s dependency injection
	 *
	 * @param cmTemplateManager
	 */
	public void setCmTemplateManager(CMTemplateManager cmTemplateManager) {
		this.cmTemplateManager = cmTemplateManager;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws
			BeansException {
		this.applicationContext = applicationContext;
	}

	private void bindWithContentModules(Mailing mailing, boolean copyImages) {
		final List<ContentModuleLocation> contentModuleLocationList =
				getValidCMLocation(mailing);
		int mailingId = mailing.getId();
		int companyID = mailing.getCompanyID();
		if(contentModuleLocationList != null) {
			for(ContentModuleLocation location : contentModuleLocationList) {
				DynamicTag dynamicTag;
				if(isNewDynTagName(mailing, location.getDynName())) {
					dynamicTag = dynamicTagFactory.newDynamicTag();
					dynamicTag.setDynName(location.getDynName());
					dynamicTag.setCompanyID(companyID);
					dynamicTag.setMailing(mailing);
					dynamicTag.setMailingID(mailingId);
				} else {
					dynamicTag = (DynamicTag) mailing.getDynTags()
							.get(location.getDynName());
				}

				final DynamicTagContent content = createDynTagContent(location,
						dynamicTag, mailing, copyImages);
				dynamicTag.addContent(content);

				mailing.addDynamicTag(dynamicTag);
			}
		}
	}

	private void bindWithCmTemplate(Mailing mailing, boolean copyImages, int adminId) {
		int mailingId = mailing.getId();
		final CMTemplate cmTemplate = cmTemplateManager
				.getCMTemplateForMailing(mailingId);
		String cmTemplateContent = "";
		if(cmTemplate == null) {
			cmTemplateContent = CmsUtils.getDefaultCMTemplate();
		} else {
			final byte[] cmTemplateContentByte = cmTemplate.getContent();
			try {
				cmTemplateContent = new String(cmTemplateContentByte, "UTF-8");
			} catch(UnsupportedEncodingException e) {
				AgnUtils.logger().warn("Wrong charset name", e);
			}
		}

		if(copyImages) {
			cmTemplateContent = replaceImageLinks(cmTemplateContent, mailing);
		}
        else {
            cmTemplateContent = CmsUtils.appendImageURLsWithSystemUrl(cmTemplateContent);
        }

		final MailingComponent classicHtmlTemplateComponent = mailing.getHtmlTemplate();
		if(classicHtmlTemplateComponent != null) {
			classicHtmlTemplateComponent.setEmmBlock(cmTemplateContent);
		}

		if(adminId > 0) {
			String textVersionContent = cmTemplateManager.getTextVersion(adminId);
			final MailingComponent classicTextTemplateComponet = mailing
					.getTextTemplate();
			if(classicTextTemplateComponet != null) {
				classicTextTemplateComponet.setEmmBlock(textVersionContent);
			}
		}
	}

	private List<Integer> getAssignedContentModuleIds(Integer mailingId) {
		return CmsUtils.getAssignedContentModuleIds(mailingId, applicationContext);
	}

	private List<ContentModuleLocation> getValidCMLocation(Mailing mailing) {

		final int mailingId = mailing.getId();
		final List<ContentModuleLocation> existModuleLocations = contenModuleManager
				.getCMLocationsForMailingId(mailingId);
		final List<Integer> assignCmIds = getAssignedContentModuleIds(mailingId);
		String[] dynNames = new String[0];
		try {
			final MailingComponent htmlTemplate = mailing.getHtmlTemplate();
			if(htmlTemplate != null) {
				final Vector dynNamesVector = mailing
						.findDynTagsInTemplates(htmlTemplate.getEmmBlock(),
								applicationContext);
				dynNames = new String[dynNamesVector.size()];
				for(int vectorIndex = 0; vectorIndex < dynNamesVector.size();
					vectorIndex++) {
					Object dynNameObject = dynNamesVector.elementAt(vectorIndex);
					dynNames[vectorIndex] = ((String) dynNameObject);
				}
			}
		} catch(Exception exception) {
			AgnUtils.logger().error("Can`t find dyn names in template", exception);
		}
		final CMTemplate template = cmTemplateManager.getCMTemplateForMailing(mailingId);
		int templateId = (template != null) ? template.getId() : 0;
		return CMLocationsUtils.
				createValidLocations(existModuleLocations, assignCmIds, dynNames,
						templateId, mailingId);
	}

	private boolean isCmsMailing(int mailingId) {
		return CmsUtils.isCmsMailing(mailingId, applicationContext);
	}

	private DynamicTagContent createDynTagContent(
			ContentModuleLocation contentModuleLocation, DynamicTag dynamicTag,
			Mailing mailing, boolean copyImages) {
		final DynamicTagContent content = dynamicTagContentFactory.newDynamicTagContent();
		final ContentModule contentModule = contenModuleManager
				.getContentModule(contentModuleLocation.getContentModuleId());
		String cmContent = TagUtils
				.generateContentModuleContent(contentModule.getId(), false,
						applicationContext);
		content.setCompanyID(dynamicTag.getCompanyID());
		if(copyImages) {
			cmContent = replaceImageLinks(cmContent, mailing, contentModule.getId());
		}
        else {
            cmContent = CmsUtils.appendImageURLsWithSystemUrl(cmContent);
        }
		content.setDynContent(cmContent);
		content.setDynName(dynamicTag.getDynName());
		content.setDynOrder(contentModuleLocation.getOrder());
		content.setMailingID(dynamicTag.getMailingID());
		content.setTargetID(contentModuleLocation.getTargetGroupId());
		return content;
	}

	public void setMediaFileManager(MediaFileManager mediaFileManager) {
		this.mediaFileManager = mediaFileManager;
	}

	private MailingComponent createMailingComponent(MediaFile mediaFile,
													Mailing mailing) {
		final MailingComponent component = mailingComponentFactory.newMailingComponent();
		component.setMailingID(mailing.getId());
		component.setMimeType(mediaFile.getMimeType());
		component.setComponentName(mediaFile.getName());
		component.setCompanyID(mailing.getCompanyID());
		component.setBinaryBlock(mediaFile.getContent());
		component.setType(MailingComponent.TYPE_HOSTED_IMAGE);
		component.setEmmBlock(component.makeEMMBlock());
		return component;
	}

	String replaceImageLinks(String content, Mailing mailing, int id) {
		final ArrayList<Integer> mediaFileIds = readMediaFileIds(content);

		final ArrayList<MediaFile> mediaFiles = new ArrayList<MediaFile>();
		for(Integer mediaFileId : mediaFileIds) {
			final MediaFile mediaFile = mediaFileManager.getMediaFile(mediaFileId);
			String imgTagName = mediaFile.getName();
			if(id != 0) {
				imgTagName += "_" + id;
			}
			mediaFile.setName(imgTagName);
			mediaFiles.add(mediaFile);
			content = content.replaceAll("/cms_image\\?fid=" + mediaFileId,
					"[agnIMAGE name=\"" + imgTagName + "\"]");
		}

		addImages(mailing, mediaFiles);

		return content;
	}

	private static boolean isNewDynTagName(Mailing mailing, String locationDynName) {
		for(Object dynTagObject : mailing.getDynTags().values()) {
			final DynamicTag dynamicTag = (DynamicTag) dynTagObject;
			if(dynamicTag.getDynName().equals(locationDynName)) {
				return false;
			}
		}
		return true;
	}

	private String parseForNumber(String content, int startDigit) {
		final StringBuffer stringBuffer = new StringBuffer();
		for(int charIndex = startDigit; charIndex < content.length(); charIndex++) {
			final char nextChar = content.charAt(charIndex);
			if(Character.isDigit(nextChar)) {
				stringBuffer.append(nextChar);
			} else {
				break;
			}
		}
		return stringBuffer.toString();
	}

	private void addImages(Mailing mailing, ArrayList<MediaFile> mediaFiles) {
		for(MediaFile mediaFile : mediaFiles) {
			MailingComponent mailingComponent = createMailingComponent(mediaFile,
					mailing);
			mailing.addComponent(mailingComponent);
		}
	}

	public void removeMailingImageComponents(Mailing mailing) {
        Vector remove = new Vector();
        MailingComponent tmp;
        Iterator it = mailing.getComponents().values().iterator();
        while(it.hasNext()) {
            tmp = (MailingComponent) it.next();
            if(tmp.getType() == MailingComponent.TYPE_IMAGE || tmp.getType() == MailingComponent.TYPE_HOSTED_IMAGE) {
                remove.add(tmp.getComponentName());
            }
        }
        Enumeration e = remove.elements();
        while(e.hasMoreElements()) {
            mailing.getComponents().remove(e.nextElement());
        }
    }

	private ArrayList<Integer> readMediaFileIds(String content) {
		final ArrayList<Integer> mediaFileIds = new ArrayList<Integer>();
		int startIndex = content.indexOf(imageUrlPattern);
		while(startIndex != -1) {
			final int startDigit = startIndex + imageUrlPattern.length();
			final String stringNumber = parseForNumber(content, startDigit);
			try {
				mediaFileIds.add(Integer.parseInt(stringNumber));
			} catch(Exception exception) {
				AgnUtils.logger().warn("Error in parsing cmTemplate content",
						exception);
			}
			startIndex = content.indexOf(imageUrlPattern, startDigit);
		}
		return mediaFileIds;
	}

	String replaceImageLinks(String cmTemplateContent, Mailing mailing) {
		return replaceImageLinks(cmTemplateContent, mailing, 0);
	}
}