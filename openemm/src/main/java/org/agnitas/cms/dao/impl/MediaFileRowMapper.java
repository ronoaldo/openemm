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

package org.agnitas.cms.dao.impl;

import java.sql.*;
import org.agnitas.cms.webservices.generated.*;
import org.springframework.jdbc.core.*;

/**
 * Class is for MediaFileDaoImpl. It maps Media-file table row to MediaFile bean.
 *
 * @author Vyacheslav Stepanov
 */
public class MediaFileRowMapper implements RowMapper {

	public Object mapRow(ResultSet resultSet, int line) throws SQLException {
		MediaFile mediaFile = new MediaFile();
		mediaFile.setId((int) resultSet.getLong("id"));
		mediaFile.setCompanyId((int) resultSet.getLong("company_id"));
		mediaFile.setCmTemplateId((int) resultSet.getLong("cm_template_id"));
		mediaFile.setContentModuleId((int) resultSet.getLong("content_module_id"));
		mediaFile.setContentModuleTypeId((int) resultSet.getLong("cmtId"));
		mediaFile.setName(resultSet.getString("media_name"));
		mediaFile.setContent(resultSet.getBytes("content"));
		mediaFile.setMediaType((int) resultSet.getLong("media_type"));
		mediaFile.setMimeType(resultSet.getString("mime_type"));
		return mediaFile;
	}
}