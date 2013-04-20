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
import java.util.*;
import org.agnitas.cms.dao.*;
import org.agnitas.cms.utils.*;
import org.agnitas.cms.webservices.generated.*;
import org.springframework.dao.*;
import org.springframework.jdbc.core.support.*;
import org.springframework.jdbc.object.*;

/**
 * @author Vyacheslav Stepanov
 */
public class MediaFileDaoImpl extends CmsDaoImpl implements MediaFileDao {

	public MediaFile createMediaFile(MediaFile mediaFile) {
		String sql = "INSERT INTO cm_media_file_tbl " +
				"(id, company_id, cm_template_id, content_module_id, cmtId, media_name, " +
				"media_type, mime_type, content) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
		SqlUpdate sqlUpdate = new SqlUpdate(getDataSource(), sql,
				new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER,
						Types.INTEGER, Types.VARCHAR,
						Types.INTEGER, Types.VARCHAR, Types.BLOB});
		sqlUpdate.compile();
		int id = createIncrement("cm_media_file_tbl_seq").nextIntValue();
		sqlUpdate.update(new Object[]{id, mediaFile.getCompanyId(),
				mediaFile.getCmTemplateId(), mediaFile.getContentModuleId(),
				mediaFile.getContentModuleTypeId(), mediaFile.getName(),
				mediaFile.getMediaType(), mediaFile.getMimeType(),
				new SqlLobValue(mediaFile.getContent())});
		mediaFile.setId(id);
		return mediaFile;
	}

    public void updateMediaFile(int id, byte[] content) {
		String sql = "UPDATE cm_media_file_tbl SET content=? WHERE id=?";
        SqlUpdate sqlUpdate = new SqlUpdate(getDataSource(), sql, new int[]{Types.BLOB, Types.INTEGER});
        sqlUpdate.compile();
        sqlUpdate.update(new Object[]{new SqlLobValue(content), id});
	}

    public void updateMediaFile(MediaFile mediaFile){
        this.updateMediaFile(mediaFile.getId(), mediaFile.getContent());
    }
    
	public MediaFile getMediaFile(int id) {
		String sqlStatement = "SELECT * FROM cm_media_file_tbl WHERE id=" + id;
		try {
			MediaFile mediaFile = (MediaFile) createJdbcTemplate().
					queryForObject(sqlStatement, new MediaFileRowMapper());
			return mediaFile;
		}
		catch(IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	public void removeMediaFile(int id) {
		String sqlStatement = "DELETE FROM cm_media_file_tbl WHERE id=" + id;
		createJdbcTemplate().execute(sqlStatement);
	}

	public MediaFile getPreviewOfContentModule(int contentModuleId) {
		String sqlStatement = "SELECT * FROM cm_media_file_tbl WHERE content_module_id=" +
				contentModuleId + " AND media_type=" + MediaFileUtils.PREVIEW_TYPE;
		try {
			MediaFile mediaFile = (MediaFile) createJdbcTemplate().
					queryForObject(sqlStatement, new MediaFileRowMapper());
			return mediaFile;
		}
		catch(IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	public MediaFile getPreviewOfContentModuleType(int cmtId) {

		String sqlStatement = "SELECT * FROM cm_media_file_tbl WHERE cmtId=" +
				cmtId + " AND media_type=" + MediaFileUtils.PREVIEW_TYPE;
		try {
			MediaFile mediaFile = (MediaFile) createJdbcTemplate().
					queryForObject(sqlStatement, new MediaFileRowMapper());
			return mediaFile;
		}
		catch(IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	public MediaFile getPreviewOfContentModuleTemplate(int cmTemplateId) {
		String sqlStatement = "SELECT * FROM cm_media_file_tbl WHERE cm_template_id=" +
				cmTemplateId + " AND media_type=" + MediaFileUtils.PREVIEW_TYPE;
		try {
			MediaFile mediaFile = (MediaFile) createJdbcTemplate().
					queryForObject(sqlStatement, new MediaFileRowMapper());
			return mediaFile;
		}
		catch(IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

    public List<MediaFile> getMediaFilesForContentModuleTemplate(int cmTemplateId) {
		String sqlStatement = "SELECT * FROM cm_media_file_tbl WHERE cm_template_id=" +
				cmTemplateId+" and media_name <> 'preview'";
		try {
			return (List<MediaFile>) createJdbcTemplate().
					query(sqlStatement, new MediaFileRowMapper());
		}
		catch(IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

    public MediaFile getMediaFileForContentModelAndMediaName(int cmTemplateId,String mediaName){
        String sqlStatement = String.format("SELECT * FROM cm_media_file_tbl WHERE cm_template_id='%s' AND media_name LIKE '%s%s%s'",cmTemplateId,'%',mediaName,'%');
        try {
            return (MediaFile) createJdbcTemplate().queryForObject(sqlStatement, new MediaFileRowMapper());
		}
		catch(IncorrectResultSizeDataAccessException e) {
			return null;
		}
    }
	public void removePreviewOfContentModule(int contentModuleId) {
		String sqlStatement = "DELETE FROM cm_media_file_tbl WHERE content_module_id="
				+ contentModuleId +
				" AND media_type=" + MediaFileUtils.PREVIEW_TYPE;
		createJdbcTemplate().execute(sqlStatement);
	}

	public void removePreviewOfContentModuleType(int contentModuleTypeId) {
		String sqlStatement = "DELETE FROM cm_media_file_tbl WHERE cmtId="
				+ contentModuleTypeId +
				" AND media_type=" + MediaFileUtils.PREVIEW_TYPE;
		createJdbcTemplate().execute(sqlStatement);
	}

	public void removePreviewOfContentModuleTemplate(int cmTemplateId) {
		String sqlStatement = "DELETE FROM cm_media_file_tbl WHERE cm_template_id="
				+ cmTemplateId +
				" AND media_type=" + MediaFileUtils.PREVIEW_TYPE;
		createJdbcTemplate().execute(sqlStatement);
	}

	public List<MediaFile> getMediaFilesForContentModule(int contentModuleId) {
		String sqlStatement = "SELECT * FROM cm_media_file_tbl WHERE " +
				"content_module_id=" + contentModuleId;
		try {
			return (List<MediaFile>) createJdbcTemplate().
					query(sqlStatement, new MediaFileRowMapper());
		}
		catch(IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	public void removeContentModuleImage(int contentModuleId, String mediaName) {
		String sqlStatement = "DELETE FROM cm_media_file_tbl WHERE content_module_id=" +
				contentModuleId + " AND media_name='" + mediaName + "'";
		createJdbcTemplate().execute(sqlStatement);
	}

	public void removeMediaFilesForCMTemplateId(int cmTemplateId) {
		String sqlStatement = "DELETE FROM cm_media_file_tbl WHERE cm_template_id="
				+ cmTemplateId;
		createJdbcTemplate().execute(sqlStatement);
	}

	public void removeMediaFilesForContentModuleId(int contentModuleId) {
		String sqlStatement = "DELETE FROM cm_media_file_tbl WHERE content_module_id="
				+ contentModuleId;
		createJdbcTemplate().execute(sqlStatement);
	}

}