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

package org.agnitas.cms.web.forms;

import org.agnitas.web.forms.*;


/**
 * @author Vyacheslav Stepanov
 */
public class CmsBaseForm extends StrutsFormBase {

	protected int action;

	protected boolean fromListPage;

	protected String name;

	protected String description;

	protected String preview;

	protected int previewSize = 4;

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public int getPreviewSize() {
		return previewSize;
	}

	public void setPreviewSize(int previewSize) {
		this.previewSize = previewSize;
	}

	public String getPreview() {
		return preview;
	}

	public void setPreview(String preview) {
		this.preview = preview;
	}

	public boolean getFromListPage() {
		return fromListPage;
	}

	public void setFromListPage(boolean fromListPage) {
		this.fromListPage = fromListPage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPreviewWidth() {
		switch(getPreviewSize()) {
			case 1:
				return 800;
			case 2:
				return 1024;
			case 3:
				return 1280;
			case 4:
				return 640;
			default:
				setPreviewSize(4);
				return 640;
		}
	}

	public int getPreviewHeight() {
		switch(getPreviewSize()) {
			case 1:
				return 600;
			case 2:
				return 768;
			case 3:
				return 1024;
			case 4:
				return 480;
			default:
				setPreviewSize(4);
				return 480;
		}
	}

	public String[] getPreviewSizes() {
		return new String[]{"640x480", "800x600", "1024x768", "1280x1024"};
	}

	public int[] getPreviewValues() {
		return new int[]{4, 1, 2, 3};
	}

}