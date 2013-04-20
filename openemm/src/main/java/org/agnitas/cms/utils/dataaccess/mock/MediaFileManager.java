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

package org.agnitas.cms.utils.dataaccess.mock;

import org.agnitas.cms.utils.dataaccess.mock.beans.MediaFile;

import java.util.*;

/**
 * This class used only for Java2WSDL generation.
 * Interface must be synchronized with
 * org.agnitas.cms.utils.dataaccess.MediaFileManager
 *
 * @author Igor Nesterenko
 */
public interface MediaFileManager {

	MediaFile createMediaFile(MediaFile mediaFile);

	MediaFile getMediaFile(int id);

	void removeMediaFile(int id);

	void removeMediaFilesForCMTemplateId(int cmTemplateId);

	void removeContentModuleImage(int contentModuleId, String mediaName);

	List<MediaFile> getMediaFilesForContentModule(int contentModuleId);

	void removeMediaFilesForContentModuleId(int contentModuleId);

	MediaFile getPreviewOfContentModule(int contentModuleId);

	MediaFile getPreviewOfContentModuleType(int cmtId);

	MediaFile getPreviewOfContentModuleTemplate(int cmTemplateId);

	void removePreviewOfContentModule(int contentModuleId);

	void removePreviewOfContentModuleType(int contentModuleTypeId);

	void removePreviewOfContentModuleTemplate(int cmTemplateId);

    void updateMediaFile(int id, byte[] content);

    void updateMediaFile(MediaFile mediaFile);

    List<MediaFile> getMediaFilesForContentModuleTemplate(int cmTemplateId);

    MediaFile getMediaFileForContentModelAndMediaName(int cmTemplateId,String mediaName);
}