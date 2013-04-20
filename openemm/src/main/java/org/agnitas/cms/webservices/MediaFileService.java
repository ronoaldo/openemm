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

package org.agnitas.cms.webservices;

import java.rmi.*;
import java.util.*;
import org.agnitas.cms.dao.*;
import org.agnitas.cms.webservices.generated.*;
import org.springframework.remoting.jaxrpc.*;

/**
 * Gives service functionality to MediaFileManager
 * from central content repository. Delegate operations
 * to MediaFileDao.
 *
 * @author Igor Nesterenko
 * @see org.agnitas.cms.utils.dataaccess.MediaFileManager
 * @see org.agnitas.cms.utils.dataaccess.RemoteMediaFileManager
 * @see org.agnitas.cms.dao.MediaFileDao
 */
public class MediaFileService extends ServletEndpointSupport
		implements RemoteMediaFileManager_PortType {

	private MediaFileDao dao;

	private MediaFileDao getDao() {
		if(dao == null) {
			dao = ((MediaFileDao) getWebApplicationContext()
					.getBean("MediaFileDao"));
		}
		return dao;
	}

	public MediaFile createMediaFile(MediaFile mediaFile) throws
			RemoteException {
		return getDao().createMediaFile(mediaFile);
	}

	public MediaFile getMediaFile(int id) throws RemoteException {
		return getDao().getMediaFile(id);
	}

	public void removeMediaFile(int id) throws RemoteException {
		getDao().removeMediaFile(id);
	}

	public void removeMediaFilesForCMTemplateId(int cmTemplateId) throws
			RemoteException {
		getDao().removeMediaFilesForCMTemplateId(cmTemplateId);
	}

	public void removeContentModuleImage(int contentModuleId,
										 String mediaName) throws
			RemoteException {
		getDao().removeContentModuleImage(contentModuleId, mediaName);
	}

	public Object[] getMediaFilesForContentModule(int contentModuleId) throws
			RemoteException {
		final List<MediaFile> mediaFileList = getDao()
				.getMediaFilesForContentModule(contentModuleId);
		return mediaFileList.toArray();
	}

	public void removeMediaFilesForContentModuleId(int contentModuleId) throws
			RemoteException {
		getDao().removeMediaFilesForContentModuleId(contentModuleId);
	}

	public MediaFile getPreviewOfContentModule(int cmId) throws
			RemoteException {
		return getDao().getPreviewOfContentModule(cmId);
	}

	public MediaFile getPreviewOfContentModuleType(int cmtId) throws
			RemoteException {
		return getDao().getPreviewOfContentModuleType(cmtId);
	}

	public MediaFile getPreviewOfContentModuleTemplate(int cmTemplateId) throws
			RemoteException {
		return getDao().getPreviewOfContentModuleTemplate(cmTemplateId);
	}

	public void removePreviewOfContentModule(int contentModuleId) throws
			RemoteException {
		getDao().removePreviewOfContentModule(contentModuleId);
	}

	public void removePreviewOfContentModuleType(int contentModuleTypeId) throws
			RemoteException {
		getDao().removePreviewOfContentModuleType(contentModuleTypeId);
	}

	public void removePreviewOfContentModuleTemplate(int cmTemplateId) throws
			RemoteException {
		getDao().removePreviewOfContentModuleTemplate(cmTemplateId);
	}

    public void updateMediaFile(int id, byte[] content) throws RemoteException {
        getDao().updateMediaFile(id,content);
    }

    public void updateMediaFile(MediaFile mediaFile) throws RemoteException {
        getDao().updateMediaFile(mediaFile);
    }

    public Object[] getMediaFilesForContentModuleTemplate(int cmTemplateId) throws RemoteException {
        final List<MediaFile> mediaFileList = getDao().getMediaFilesForContentModuleTemplate(cmTemplateId);
        return mediaFileList.toArray();
    }

    public MediaFile getMediaFileForContentModelAndMediaName(int cmTemplateId, String mediaName) throws RemoteException {
        return getDao().getMediaFileForContentModelAndMediaName(cmTemplateId,mediaName);
    }
}
