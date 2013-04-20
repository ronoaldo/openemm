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

import java.util.*;
import org.agnitas.cms.web.*;
import org.agnitas.cms.webservices.generated.*;
import org.apache.commons.lang.*;
import org.springframework.context.*;

/**
 * @author Vyacheslav Stepanov
 */
public class TagUtils {

	public static final String[] TAG_NAMES = {"agnTEXT", "agnLABEL", "agnIMAGE",
			"agnLINK"};

	public static final int TAG_UNKNOWN = -1;
	public static final int TAG_TEXT = 0;
	public static final int TAG_LABEL = 1;
	public static final int TAG_IMAGE = 2;
	public static final int TAG_LINK = 3;

	public static String generateContentModuleContent(int contentModuleId,
													  boolean migrateImageTags,
													  ApplicationContext aContext) {
		ContentModule contentModule = CmsUtils.getContentModuleManager(aContext).
				getContentModule(contentModuleId);
		List<CmsTag> placeholders = CmsUtils.getContentModuleManager(aContext).
				getContentModuleContents(contentModuleId);
		String content = contentModule.getContent();
		List<String> tags = getValidTags(content);
		for(String tag : tags) {
			String replacement = getValueForTag(TagUtils.createTag(tag), placeholders);
			replacement = replacement == null ? "" : replacement;
			if(getTagType(tag) == TAG_IMAGE && migrateImageTags) {
				String tagName = getTagName(tag);
				replacement = "[" + TAG_NAMES[TAG_IMAGE] + " name=\"" + tagName +
						"_" + contentModule.getId() + "\"]";
			}
			content = StringUtils.replace(content, tag, replacement);
		}
		return content;
	}

	public static List<CmsTag> getCmsTags(String content) {
		List<CmsTag> cmsTags = new ArrayList<CmsTag>();
		List<String> validTags = getValidTags(content);
		for(String validTag : validTags) {
			CmsTag tag = TagUtils.createTag(validTag);
			boolean alreadyAdded = false;
			for(CmsTag addedTag : cmsTags) {
				if(tagsAreEqual(addedTag, tag)) {
					alreadyAdded = true;
					break;
				}
			}
			if(!alreadyAdded) {
				cmsTags.add(tag);
			}
		}
		return cmsTags;
	}

	public static List<String> getValidTags(String content) {
		List<String> tags = new ArrayList<String>();
		int tagStart = content.indexOf("[agn");
		while(tagStart != -1) {
			int nextTagStart = content.indexOf("[agn", tagStart + 1);
			int tagEnd = content.indexOf("]", tagStart + 1);
			if((nextTagStart < tagEnd && nextTagStart != -1) || tagEnd == -1) {
				content = content.substring(0, tagStart) + "TAG_WAS_HERE" +
						content.substring(tagStart + 4);
			} else {
				String tag = content.substring(tagStart, tagEnd + 1);
				if(isTagValid(tag)) {
					tags.add(tag);
				}
				content = content.substring(0, tagStart) + "TAG_WAS_HERE" +
						content.substring(tagEnd + 1);
			}
			tagStart = content.indexOf("[agn");
		}
		return tags;
	}

	public static String getValueForTag(CmsTag tag, List<CmsTag> valueTagList) {
		for(CmsTag valueTag : valueTagList) {
			if(tagsAreEqual(valueTag, tag)) {
				return valueTag.getValue();
			}
		}
		return "";
	}

	public static String getTagName(String tag) {
		int nameIndex = tag.indexOf("name");
		if(nameIndex != -1) {
			int quoteBegin = tag.indexOf("\"", nameIndex);
			if(quoteBegin != -1) {
				int quoteEnd = tag.indexOf("\"", quoteBegin + 1);
				if(quoteEnd != -1 && quoteEnd - quoteBegin > 1) {
					return tag.substring(quoteBegin + 1, quoteEnd);
				}
			}
		}
		return "";
	}

	public static int getTagType(String tag) {
		for(int i = 0; i < TAG_NAMES.length; i++) {
			if(tag.startsWith("[" + TAG_NAMES[i])) {
				return i;
			}
		}
		return TAG_UNKNOWN;
	}

	public static boolean isTagValid(String tag) {
		return getTagType(tag) != TAG_UNKNOWN;
	}

	public static CmsTag findTag(List<CmsTag> tags, String name, int type) {
		for(CmsTag tag : tags) {
			if(name.equals(tag.getName()) && type == tag.getType()) {
				return tag;
			}
		}
		return null;
	}

	public static MediaFile getMediaFileForTag(List<MediaFile> mediaFiles, CmsTag tag,
											   int contentModuleId) {
		for(MediaFile mediaFile : mediaFiles) {
			if(tag.getName().equals(mediaFile.getName()) &&
					mediaFile.getContentModuleId() == contentModuleId) {
				return mediaFile;
			}
		}
		return null;
	}

	public static List<CmsTag> filterTagsByType(List<CmsTag> tags, int tagType) {
		List<CmsTag> resultList = new ArrayList<CmsTag>();
		for(CmsTag tag : tags) {
			if(tag.getType() == tagType) {
				resultList.add(tag);
			}
		}
		return resultList;
	}

	public static CmsTag createTag(String tag) {
		final int tagType = getTagType(tag);
		final String tagName = getTagName(tag);
		CmsTag cmsTag;
		if(tagType == TAG_IMAGE) {
			cmsTag = new CmsImageTag();
		} else {
			cmsTag = new CmsTag();
		}
		cmsTag.setName(tagName);
		cmsTag.setType(tagType);
		return cmsTag;
	}

	public static CmsTag createTag(String name, int type, String value) {
		CmsTag cmsTag = new CmsTag();
		cmsTag.setName(name);
		cmsTag.setType(type);
		cmsTag.setValue(value);
		return cmsTag;
	}

	public static boolean tagsAreEqual(CmsTag tagOne, CmsTag tagTwo) {
		if(tagOne == null || tagTwo == null) {
			return false;
		}
		boolean namesEqual;
		if(tagTwo.getName() != null && tagOne.getName() != null) {
			namesEqual = tagOne.getName().equals(tagTwo.getName());
		} else {
			namesEqual = tagTwo.getName() == null && tagOne.getName() == null;
		}
		return namesEqual && tagOne.getType() == tagTwo.getType();
	}

	public static CmsImageTag createImageTag(String tagName, int tagType, String newValue,
											 String imagePath) {
		CmsImageTag cmsTag = new CmsImageTag();
		cmsTag.setName(tagName);
		cmsTag.setType(tagType);
		cmsTag.setValue(newValue);
		cmsTag.setNewValue(imagePath);
		return cmsTag;
	}

	public static int getType(String name) {
		int firstPoint = name.indexOf(".");
		int secondPoint = name.indexOf(".", firstPoint + 1);
		return Integer.parseInt(name.substring(firstPoint + 1, secondPoint));
	}

	public static String getName(String name) {
		int firstPoint = name.indexOf(".");
		int secondPoint = name.indexOf(".", firstPoint + 1);
		return name.substring(secondPoint + 1);
	}

    public static boolean containsTagsWithEmptyNames(String content){
        List<CmsTag> tags = getCmsTags(content);
        for (CmsTag tag: tags){
            String name = tag.getName();
            if(name == null || "".equals(name.trim()))
                return true;
        }
        return false;
    }    

}
