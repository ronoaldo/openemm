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

package org.agnitas.cms.utils.dataaccess;


import java.util.*;
import org.agnitas.cms.webservices.generated.*;

/**
 * Provides core operations with media files
 *
 * @author Vyacheslav Stepanov
 */
public interface MediaFileManager {

	/**
	 * Method stores media-file in database
	 *
	 * @param mediaFile media-file to be created in DB
	 * @return stored media-file with set id
	 */
	MediaFile createMediaFile(MediaFile mediaFile);

	/**
	 * Gets media-file for id
	 *
	 * @param id id of media-file needed
	 * @return media-file for id
	 */
	MediaFile getMediaFile(int id);

	/**
	 * Removes media file
	 *
	 * @param id id of media file
	 */

	void removeMediaFile(int id);

	/**
	 * Removes all media files with given cm_template_id
	 *
	 * @param cmTemplateId id of CM template, media files with such cm_template_id
	 *                     will be removed
	 */
	void removeMediaFilesForCMTemplateId(int cmTemplateId);

	/**
	 * Removes media file that is content module placeholder's content
	 *
	 * @param contentModuleId id of content module
	 * @param mediaName	   name of media file
	 */
	void removeContentModuleImage(int contentModuleId, String mediaName);

	/**
	 * Gets media files for specific content module
	 *
	 * @param contentModuleId id of content module
	 * @return all media files for content module id
	 */
	List<MediaFile> getMediaFilesForContentModule(int contentModuleId);

	/**
	 * Removes all media files from specific content module
	 *
	 * @param contentModuleId id of content module
	 */
	void removeMediaFilesForContentModuleId(int contentModuleId);

	/**
	 * Gets preview for specific content module
	 *
	 * @param contentModuleId id of specific content module
	 * @return media file with reference to preview image
	 */
	MediaFile getPreviewOfContentModule(int contentModuleId);

	/**
	 * Gets preview for specific content module type
	 *
	 * @param cmtId id of specific content module type
	 * @return media file with reference to preview image
	 */
	MediaFile getPreviewOfContentModuleType(int cmtId);

	/**
	 * Gets preview for specific cms`s template
	 *
	 * @param cmTemplateId id of specific cms`s template
	 * @return media file with reference to preview image
	 */
	MediaFile getPreviewOfContentModuleTemplate(int cmTemplateId);

	/**
	 * Removes preview of specific content module
	 *
	 * @param contentModuleId id of specific content module
	 */
	void removePreviewOfContentModule(int contentModuleId);

	/**
	 * Removes preview of specific content module type
	 *
	 * @param contentModuleTypeId id of specific content module type
	 */
	void removePreviewOfContentModuleType(int contentModuleTypeId);

	/**
	 * Removes preview of specific cms`s tempate
	 *
	 * @param cmTemplateId id of specific cms`s template
	 */
	void removePreviewOfContentModuleTemplate(int cmTemplateId);

    /**
	 * Method update media-file in database
	 *
	 * @param  id of CM template, media files with such id
     *
     * @param  content of CM template, media files with such content will be updated
	 */
    void updateMediaFile(int id, byte[] content);

    /**
     * @param mediaFile media-file to be update in DB
     *
     */
    void updateMediaFile(MediaFile mediaFile);

     /**
     * Method list stores media-file in database
	 *
	 * @param cmTemplateId id of CM template, media files with such cm_template_id
	 * @return list stored media-file with set cmTemplateId
     */
    List<MediaFile> getMediaFilesForContentModuleTemplate(int cmTemplateId);

    /**
     * Method stores media-file in database
	 *
	 * @param cmTemplateId id of CM template, media files with such cm_template_id
     * @param mediaName media name of CM template, media files with such media_name
	 * @return stored media-file with set cmTemplateId and media_name
     */
    MediaFile getMediaFileForContentModelAndMediaName(int cmTemplateId,String mediaName);

}