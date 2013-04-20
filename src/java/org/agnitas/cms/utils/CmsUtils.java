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

import javax.servlet.http.*;
import java.io.*;
import java.nio.charset.*;
import java.util.*;
import org.agnitas.cms.utils.dataaccess.*;
import org.agnitas.cms.webservices.generated.*;
import org.agnitas.util.*;
import org.springframework.context.*;
import org.springframework.web.context.*;

/**
 * @author Vyacheslav Stepanov
 */
public class CmsUtils {

	private static CMTemplateManager cmTemplateManager = null;
	private static MediaFileManager mediaFileManager = null;
	private static ContentModuleTypeManager cmtManager = null;
	private static ContentModuleManager contentModuleManager = null;
	public static final String UNKNOWN_MIME_TYPE = "application/octet-stream";

	public static CMTemplateManager getCMTemplateManager(
			ApplicationContext applicationContext) {
		if(cmTemplateManager == null) {
			cmTemplateManager = (CMTemplateManager) applicationContext
					.getBean("CMTemplateManager");
		}
		return cmTemplateManager;
	}

	public static MediaFileManager getMediaFileManager(
			ApplicationContext applicationContext) {
		if(mediaFileManager == null) {
			mediaFileManager = (MediaFileManager) applicationContext
					.getBean("MediaFileManager");
		}
		return mediaFileManager;
	}

	public static ContentModuleTypeManager getContentModuleTypeManager(
			ApplicationContext applicationContext) {
		if(cmtManager == null) {
			cmtManager = (ContentModuleTypeManager) applicationContext
					.getBean("ContentModuleTypeManager");
		}
		return cmtManager;
	}

	public static ContentModuleManager getContentModuleManager(
			ApplicationContext applicationContext) {
		if(contentModuleManager == null) {
			contentModuleManager = (ContentModuleManager) applicationContext
					.getBean("ContentModuleManager");
		}
		return contentModuleManager;
	}

    public static String appendImageURLsWithSystemUrl(String html) {
        String systemUrl = AgnUtils.getDefaultValue("system.url");
        String resultHtml = html.replaceAll("/cms_image", systemUrl + "/cms_image");
        return resultHtml;
    }

    public static String removeSystemUrlFromImageUrls(String html) {
        String systemUrl = AgnUtils.getDefaultValue("system.url");
        String resultHtml = html.replaceAll(systemUrl + "/cms_image", "/cms_image");
        return resultHtml;
    }

	public static String generateMediaFileUrl(int imageId) {
		return "/cms_image?fid=" + imageId;
	}

	public static String getDefaultPlaceholderName() {
		return "default placeholder";
	}

	public static String getDefaultCMTemplate() {
		return "[agnDYN name=\"" + getDefaultPlaceholderName() + "\"/]";
	}

	public static String fixEncoding(String sourceStr) {
		try {
			return new String(sourceStr.getBytes(Charset.forName("iso-8859-1").name()),
					Charset.forName("UTF-8").name());
		} catch(UnsupportedEncodingException e) {
			AgnUtils.logger().warn("Wrong charset name", e);
		}
		return "";
	}

	public static void generateClassicTemplate(final int mailingId,
											   final HttpServletRequest request,
											   final WebApplicationContext aContext) {
		final ClassicTemplateGenerator classicTemplateGenerator =
				(ClassicTemplateGenerator) aContext.getBean("ClassicTemplateGenerator");
		classicTemplateGenerator.generate(mailingId, request, true, true);
	}

	public static boolean isCmsMailing(int mailingId, ApplicationContext context) {
		final CMTemplate cmTemplate = getCMTemplateManager(context)
				.getCMTemplateForMailing(mailingId);
		final Collection<Integer> assignedCMs = getAssignedContentModuleIds(mailingId,
				context);
		return !(assignedCMs.size() == 0 && cmTemplate == null);
	}

	public static List<Integer> getAssignedContentModuleIds(Integer mailingId,
															ApplicationContext context) {
		final List<ContentModule> moduleList = getContentModuleManager(context)
				.getContentModulesForMailing(mailingId);
		List<Integer> cmIdList = new ArrayList<Integer>();
		for(ContentModule contentModule : moduleList) {
			cmIdList.add(contentModule.getId());
		}
		return cmIdList;
	}

	public static boolean isOracleDB() {
		org.hibernate.dialect.Dialect dialect = org.hibernate.dialect.DialectFactory
				.buildDialect(CmsUtils.getDefaultValue("cmsdb.dialect"));

		if(dialect instanceof org.hibernate.dialect.Oracle9Dialect ||
				dialect instanceof org.hibernate.dialect.OracleDialect) {
			return true;
		}
		return false;
	}

	public static boolean isMySQLDB() {
		org.hibernate.dialect.Dialect dialect = org.hibernate.dialect.DialectFactory
				.buildDialect(CmsUtils.getDefaultValue("cmsdb.dialect"));

		if(dialect instanceof org.hibernate.dialect.MySQLDialect) {
			return true;
		}
		return false;
	}

	public static String getDefaultValue(String key) {
		ResourceBundle defaults = null;
		String result = null;

		try {
			defaults = ResourceBundle.getBundle("cms");
		} catch(Exception e) {
			AgnUtils.logger().error("getDefaultValue: " + e.getMessage());
			return null;
		}

		try {
			result = defaults.getString(key);
		} catch(Exception e) {
			AgnUtils.logger().error("getDefaultValue: " + e.getMessage());
			result = null;
		}
		return result;
	}

	public static void cloneMailingCmsData(int sourceMailingId, int newMailingId, ApplicationContext context) {
		ContentModuleManager cmManager = getContentModuleManager(context);
		CMTemplateManager cmTemplateManager = getCMTemplateManager(context);

		// get CM bindings of current mailing
		List<Integer> cmIds = cmManager.getAssignedCMsForMailing(sourceMailingId);
		List<ContentModuleLocation> cmLocations = cmManager.getCMLocationsForMailingId(sourceMailingId);

		// copy CM bindings to new mailing
		cmManager.addMailingBindingToContentModules(cmIds, newMailingId);
		for(ContentModuleLocation location : cmLocations) {
			location.setMailingId(newMailingId);
		}
		cmManager.addCMLocations(cmLocations);

		// copy CM Template assignment
		CMTemplate template = cmTemplateManager.getCMTemplateForMailing(sourceMailingId);
		if (template != null) {
			cmTemplateManager.addMailingBindings(template.getId(), Collections.singletonList(newMailingId));
		}
	}

	public static boolean mailingHasCmsData(int mailingId, ApplicationContext context) {
		CMTemplate template = getCMTemplateManager(context).getCMTemplateForMailing(mailingId);
		if (template == null) {
			List<Integer> assignedCms = getContentModuleManager(context).getAssignedCMsForMailing(mailingId);
			if(assignedCms.isEmpty()) {
				return false;
			}
		}
		return true;

	}
}
