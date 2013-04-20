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
import org.agnitas.cms.dao.*;
import org.agnitas.cms.dao.impl.*;
import org.agnitas.cms.webservices.generated.*;

/**
 * Temporary implementation of MediaFileManager. Will be replaced with
 * implementation that uses SOAP for accessing Central content repository
 *
 * @author Vyacheslav Stepanov
 */
public class LocalMediaFileManager implements MediaFileManager {

	MediaFileDao mediaFileDao;


	public LocalMediaFileManager(MediaFileDaoImpl mediaFileDao) {
		this.mediaFileDao = mediaFileDao;
	}

	public MediaFile createMediaFile(MediaFile mediaFile) {
		return mediaFileDao.createMediaFile(mediaFile);
	}

	public MediaFile getMediaFile(int id) {
		return mediaFileDao.getMediaFile(id);
	}

	public void removeMediaFilesForCMTemplateId(int cmTemplateId) {
		mediaFileDao.removeMediaFilesForCMTemplateId(cmTemplateId);
	}

	public void removeContentModuleImage(int contentModuleId, String mediaName) {
		mediaFileDao.removeContentModuleImage(contentModuleId, mediaName);
	}

	public List<MediaFile> getMediaFilesForContentModule(int contentModuleId) {
		return mediaFileDao.getMediaFilesForContentModule(contentModuleId);
	}

	public void removeMediaFilesForContentModuleId(int contentModuleId) {
		mediaFileDao.removeMediaFilesForContentModuleId(contentModuleId);
	}

	public MediaFile getPreviewOfContentModule(int contentModuleId) {
		return mediaFileDao.getPreviewOfContentModule(contentModuleId);
	}

	public MediaFile getPreviewOfContentModuleType(int cmtId) {
		return mediaFileDao.getPreviewOfContentModuleType(cmtId);
	}

	public MediaFile getPreviewOfContentModuleTemplate(int cmTemplateId) {
		return mediaFileDao.getPreviewOfContentModuleTemplate(cmTemplateId);
	}

	public void removePreviewOfContentModule(int contentModuleId) {
		mediaFileDao.removePreviewOfContentModule(contentModuleId);
	}

	public void removePreviewOfContentModuleType(int contentModuleTypeId) {
		mediaFileDao.removePreviewOfContentModuleType(contentModuleTypeId);
	}

	public void removePreviewOfContentModuleTemplate(int cmTemplateId) {
		mediaFileDao.removePreviewOfContentModuleTemplate(cmTemplateId);
	}

	public void removeMediaFile(int id) {
		mediaFileDao.removeMediaFile(id);
	}

    public void updateMediaFile(int id, byte[] content) {
        mediaFileDao.updateMediaFile(id,content);
    }

    public void updateMediaFile(MediaFile mediaFile) {
        mediaFileDao.updateMediaFile(mediaFile);
    }

    public List<MediaFile> getMediaFilesForContentModuleTemplate(int cmTemplateId) {
        return mediaFileDao.getMediaFilesForContentModuleTemplate(cmTemplateId);
    }

    public MediaFile getMediaFileForContentModelAndMediaName(int cmTemplateId, String mediaName) {
        return mediaFileDao.getMediaFileForContentModelAndMediaName(cmTemplateId,mediaName);
    }
}
