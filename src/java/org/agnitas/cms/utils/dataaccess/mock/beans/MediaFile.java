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

package org.agnitas.cms.utils.dataaccess.mock.beans;

/**
 * This class used only for Java2WSDL generation.
 *
 * @author Igor Nesterenko
 */
public class MediaFile {

	protected int id;

	protected int cmTemplateId;

	protected int contentModuleId;

	protected int contentModuleTypeId;

	protected int companyId;

	protected String name;

	protected byte[] content;

	protected int mediaType;

	protected String mimeType;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCmTemplateId() {
		return cmTemplateId;
	}

	public void setCmTemplateId(int cmTemplateId) {
		this.cmTemplateId = cmTemplateId;
	}

	public int getContentModuleId() {
		return contentModuleId;
	}

	public void setContentModuleId(int contentModuleId) {
		this.contentModuleId = contentModuleId;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public int getMediaType() {
		return mediaType;
	}

	public void setMediaType(int mediaType) {
		this.mediaType = mediaType;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public int getContentModuleTypeId() {
		return contentModuleTypeId;
	}

	public void setContentModuleTypeId(int contentModuleTypeId) {
		this.contentModuleTypeId = contentModuleTypeId;
	}
}