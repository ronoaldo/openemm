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

import javax.xml.rpc.*;
import java.net.*;
import java.rmi.*;
import java.util.*;
import org.agnitas.cms.webservices.generated.*;
import org.agnitas.util.*;

/**
 * Provide remote functionality to MediaFileManager.
 * Invokes service from central content repository.
 *
 * @author Igor Nesterenko
 * @see org.agnitas.cms.webservices.MediaFileService
 */

public class RemoteMediaFileManager implements MediaFileManager {

	private RemoteMediaFileManager_PortType mediaFileManager;

	public void setPortUrl(String portUrlString) {
		final RemoteMediaFileManagerServiceLocator serviceLocator = new RemoteMediaFileManagerServiceLocator();
		try {
			mediaFileManager = serviceLocator
					.getRemoteMediaFileManager(new URL(portUrlString));
		} catch(ServiceException e) {
			AgnUtils.logger()
					.error("Error while acces to service by port=" + portUrlString + e +
							"\n" + AgnUtils.getStackTrace(e));
		} catch(MalformedURLException e) {
			AgnUtils.logger().error("Error while parsing port address portAddress=" +
					portUrlString + e + "\n" + AgnUtils.getStackTrace(e));
		}

	}

	public MediaFile createMediaFile(MediaFile mediaFile) {
		try {
			return mediaFileManager.createMediaFile(mediaFile);
		} catch(RemoteException e) {
			AgnUtils.logger()
					.error("Error while create " + MediaFile.class.getSimpleName() + " " +
							e + "\n" + AgnUtils.getStackTrace(e));
		}
		return null;
	}

	public MediaFile getMediaFile(int id) {
		try {
			return mediaFileManager.getMediaFile(id);
		} catch(RemoteException e) {
			AgnUtils.logger().error("Error while get " + MediaFile.class.getSimpleName() +
					" by id=" + id + e + "\n" + AgnUtils.getStackTrace(e));
		}
		return null;
	}

	public void removeMediaFile(int id) {
		try {
			mediaFileManager.removeMediaFile(id);
		} catch(RemoteException e) {
			AgnUtils.logger()
					.error("Error while remove mediaFile by id=" + id + e + "\n" +
							AgnUtils.getStackTrace(e));
		}
	}

	public void removeMediaFilesForCMTemplateId(int cmTemplateId) {
		try {
			mediaFileManager.removeMediaFilesForCMTemplateId(cmTemplateId);
		} catch(RemoteException e) {
			AgnUtils.logger()
					.error("Error while remove " + MediaFile.class.getSimpleName() +
							" from cmTemplateId=" + cmTemplateId + e + "\n" +
							AgnUtils.getStackTrace(e));
		}
	}

	public void removeContentModuleImage(int contentModuleId, String mediaName) {
		try {
			mediaFileManager.removeContentModuleImage(contentModuleId, mediaName);
		} catch(RemoteException e) {
			AgnUtils.logger()
					.error("Error while remove " + MediaFile.class.getSimpleName() +
							" from content module id=" + contentModuleId +
							" and media name=" + mediaName + e + "\n" +
							AgnUtils.getStackTrace(e));
		}
	}

	public List<MediaFile> getMediaFilesForContentModule(int contentModuleId) {
		final ArrayList<MediaFile> mediaFileList = new ArrayList<MediaFile>();
		try {
			final Object[] mediaFiles = mediaFileManager
					.getMediaFilesForContentModule(contentModuleId);
			for(Object mediaFile : mediaFiles) {
				mediaFileList.add(((MediaFile) mediaFile));
			}
		} catch(RemoteException e) {
			AgnUtils.logger()
					.error("Error while getting " + MediaFile.class.getSimpleName() +
							" of content module id=" + contentModuleId + e + "\n" +
							AgnUtils.getStackTrace(e));
		}
		return mediaFileList;
	}

	public void removeMediaFilesForContentModuleId(int contentModuleId) {
		try {
			mediaFileManager.removeMediaFilesForContentModuleId(contentModuleId);
		} catch(RemoteException e) {
			AgnUtils.logger()
					.error("Error while remove media file of content module id=" +
							contentModuleId + e + "\n" + AgnUtils.getStackTrace(e));
		}
	}

	public MediaFile getPreviewOfContentModule(int contentModuleId) {
		try {
			return mediaFileManager.getPreviewOfContentModule(contentModuleId);
		} catch(RemoteException e) {
			AgnUtils.logger().error("Error while getting preview of content module id=" +
					contentModuleId + e + "\n" + AgnUtils.getStackTrace(e));
		}
		return null;
	}

	public MediaFile getPreviewOfContentModuleType(int cmtId) {
		try {
			return mediaFileManager.getPreviewOfContentModuleType(cmtId);
		} catch(RemoteException e) {
			AgnUtils.logger()
					.error("Error while getting preview of content module type id=" +
							cmtId + e + "\n" + AgnUtils.getStackTrace(e));
		}
		return null;
	}

	public MediaFile getPreviewOfContentModuleTemplate(int cmTemplateId) {
		try {
			return mediaFileManager.getPreviewOfContentModuleTemplate(cmTemplateId);
		} catch(RemoteException e) {
			AgnUtils.logger()
					.error("Error while getting preview of content module template id=" +
							cmTemplateId + e + "\n" + AgnUtils.getStackTrace(e));
		}
		return null;
	}

	public void removePreviewOfContentModule(int contentModuleId) {
		try {
			mediaFileManager.removePreviewOfContentModule(contentModuleId);
		} catch(RemoteException e) {
			AgnUtils.logger().error("Error while remove preview of content module id=" +
					contentModuleId + e + "\n" + AgnUtils.getStackTrace(e));
		}
	}

	public void removePreviewOfContentModuleType(int contentModuleTypeId) {
		try {
			mediaFileManager.removePreviewOfContentModuleType(contentModuleTypeId);
		} catch(RemoteException e) {
			AgnUtils.logger()
					.error("Error while remove preview of content module type  id=" +
							contentModuleTypeId + e + "\n" + AgnUtils.getStackTrace(e));
		}
	}

	public void removePreviewOfContentModuleTemplate(int cmTemplateId) {
		try {
			mediaFileManager.removePreviewOfContentModuleTemplate(cmTemplateId);
		} catch(RemoteException e) {
			AgnUtils.logger()
					.error("Error while removing  preview of content module template id=" +
							cmTemplateId + e + "\n" + AgnUtils.getStackTrace(e));
		}
	}

    public void updateMediaFile(int id, byte[] content) {
        try {
            mediaFileManager.updateMediaFile(id, content);
        } catch (RemoteException e) {
            AgnUtils.logger()
                    .error("Error while update media file of content module template id=" +
                            id + e + "\n" + AgnUtils.getStackTrace(e));
        }

    }

    public void updateMediaFile(MediaFile mediaFile) {
        try {
            mediaFileManager.updateMediaFile(mediaFile);
        } catch (Exception e) {
            AgnUtils.logger()
                    .error("Error while update media file of content module template id=" +
                            mediaFile.getId() + e + "\n" + AgnUtils.getStackTrace(e));
        }
    }

    public List<MediaFile> getMediaFilesForContentModuleTemplate(int cmTemplateId) {
       final ArrayList<MediaFile> mediaFileList = new ArrayList<MediaFile>();
		try {
			final Object[] mediaFiles = mediaFileManager
					.getMediaFilesForContentModuleTemplate(cmTemplateId);
			for(Object mediaFile : mediaFiles) {
				mediaFileList.add(((MediaFile) mediaFile));
			}
        } catch (Exception e) {
            AgnUtils.logger()
                    .error("Error while get list media file of content module template cmTemplateId=" +
                            cmTemplateId + e + "\n" + AgnUtils.getStackTrace(e));
        }
        return mediaFileList;
    }

    public MediaFile getMediaFileForContentModelAndMediaName(int cmTemplateId, String mediaName) {
        try {
            return mediaFileManager.getMediaFileForContentModelAndMediaName(cmTemplateId, mediaName);
        } catch (Exception e) {
            AgnUtils.logger()
                    .error("Error while get media file of content module template cmTemplateId=" +
                            cmTemplateId+" and media_name="+mediaName+" " + e + "\n" + AgnUtils.getStackTrace(e));
        }
        return null;
    }
}
