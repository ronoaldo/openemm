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

package org.agnitas.ecs.web.forms;

import org.agnitas.web.forms.StrutsFormBase;
import org.agnitas.ecs.backend.beans.ClickStatColor;
import org.apache.struts.action.ActionMessages;

import java.util.Map;
import java.util.Collection;

/**
 * Form class for Embedded click statistics page.
 *
 * @author Vyacheslav Stepanov
 */
public class EcsMailingStatForm extends StrutsFormBase {

	/**
	 * mailing id
	 */
	protected int mailingId;

	/**
	 * ID of mailing this form is initialized for.
	 */
	protected int initializedMailingId;
	
	/**
     * Holds value of property shortname. 
     */
    protected String shortname;

    private String description;

	/**
	 * test and admin recipients for drop-down box
	 */
	protected Map<Integer, String> testRecipients;

	/**
	 * selected ECS view mode
	 */
	protected int viewMode = 0;

	/**
	 * selected recipient for mailing content generation
	 */
	protected int selectedRecipient;

	/**
	 * color codes and ranges for heatmap
	 */
	protected Collection<ClickStatColor> rangeColors;

	/**
	 * companyId - is passed to ECS-servlet as a parameter
	 */
	protected int companyId;

	/**
	 * URL of statistics server
	 */
	protected String statServerUrl;

	/**
	 * size of mailing content frame
	 */
	protected int frameSize;

	/**
	 * width of mailing content frame
	 */
	protected int frameWidth;

	/**
	 * height of mailing content frame
	 */
	protected int frameHeight;
	
	protected ActionMessages heatmapErrors;

	public ActionMessages getHeatmapErrors() {
		return heatmapErrors;
	}

	public void setHeatmapErrors(ActionMessages errors) {
		this.heatmapErrors = errors;
	}

	public int getMailingId() {
		return mailingId;
	}

	public void setMailingId(int mailingId) {
		this.mailingId = mailingId;
	}

	public int getInitializedMailingId() {
		return this.initializedMailingId;
	}
	
	public void setInitializedMailingId( int mailingId) {
		this.initializedMailingId = mailingId;
	}

    public String getShortname() {
        return shortname;
    }
    
    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

	public int getFrameSize() {
		return frameSize;
	}

	public void setFrameSize(int frameSize) {
		this.frameSize = frameSize;
		switch(frameSize) {
			case 1:
				frameWidth = 800;
				frameHeight = 600;
				break;
			case 2:
				frameWidth = 1024;
				frameHeight = 768;
				break;
			case 3:
				frameWidth = 1280;
				frameHeight = 1024;
				break;
			case 4:
				frameWidth = 640;
				frameHeight = 480;
				break;
			case 5:
				frameWidth = 320;
				frameHeight = 480;
				break;
			default:
				setFrameSize(1);
				frameWidth = 800;
				frameHeight = 600;
				break;
		}
	}

	public int getFrameWidth() {
		return frameWidth;
	}

	public void setFrameWidth(int frameWidth) {
		this.frameWidth = frameWidth;
	}

	public int getFrameHeight() {
		return frameHeight;
	}

	public void setFrameHeight(int frameHeight) {
		this.frameHeight = frameHeight;
	}

	public int getSelectedRecipient() {
		return selectedRecipient;
	}

	public void setSelectedRecipient(int selectedRecipient) {
		this.selectedRecipient = selectedRecipient;
	}

	public Map<Integer, String> getTestRecipients() {
		return testRecipients;
	}

	public void setTestRecipients(Map<Integer, String> testRecipients) {
		this.testRecipients = testRecipients;
	}

	public int getViewMode() {
		return viewMode;
	}

	public void setViewMode(int viewMode) {
		this.viewMode = viewMode;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public Collection<ClickStatColor> getRangeColors() {
		return rangeColors;
	}
	
	public void setRangeColors(Collection<ClickStatColor> rangeColors) {
		this.rangeColors = rangeColors;
	}
	
	public String getStatServerUrl() {
		return statServerUrl;
	}

	public void setStatServerUrl(String statServerUrl) {
		this.statServerUrl = statServerUrl;
	}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
