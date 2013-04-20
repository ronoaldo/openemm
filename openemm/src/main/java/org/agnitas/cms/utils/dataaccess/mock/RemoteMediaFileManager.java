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


import org.agnitas.cms.utils.dataaccess.mock.beans.*;

import java.util.*;

/**
 * @author Igor Nesterenko
 */
public class RemoteMediaFileManager implements MediaFileManager {

	public MediaFile createMediaFile(MediaFile mediaFile) {
		return null;
	}

	public MediaFile getMediaFile(int id) {
		return null;
	}

	public void removeMediaFile(int id) {
	}

	public void removeMediaFilesForCMTemplateId(int cmTemplateId) {
	}

	public void removeContentModuleImage(int contentModuleId, String mediaName) {
	}

	public List<MediaFile> getMediaFilesForContentModule(int contentModuleId) {
		return null;
	}

	public void removeMediaFilesForContentModuleId(int contentModuleId) {
	}

	public MediaFile getPreviewOfContentModule(int contentModuleId) {
		return null;
	}

	public void removePreviewOfContentModule(int contentModuleId) {
	}

	public MediaFile getPreviewOfContentModuleType(int cmtId) {
		return null;
	}

	public MediaFile getPreviewOfContentModuleTemplate(int cmTemplateId) {
		return null;
	}

	public void removePreviewOfContentModuleType(int contentModuleTypeId) {
	}

	public void removePreviewOfContentModuleTemplate(int cmTemplateId) {
	}

    public void updateMediaFile(int id, byte[] content){

    }

    public void updateMediaFile(MediaFile mediaFile){

    }

    public List<MediaFile> getMediaFilesForContentModuleTemplate(int cmTemplateId) {
        return null;
    }

    public MediaFile getMediaFileForContentModelAndMediaName(int cmTemplateId, String mediaName) {
        return null;  
    }
}