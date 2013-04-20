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
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

package org.agnitas.web.forms;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.agnitas.web.MailingComponentsAction;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;

public class MailingComponentsForm extends StrutsFormBase {

	private static final long serialVersionUID = 2593985118672616983L;

	/**
	 * Holds value of property mailingID.
	 */
	private int mailingID;

	/**
	 * Holds value of property shortname.
	 */
	private String shortname;

	/**
	 * Holds value of property description.
	 */
	private String description;

	/**
	 * Holds value of property action.
	 */
	private int action;

	/**
	 * Holds value of property NewFile.
	 */
	private FormFile NewFile;

	/**
	 * Holds value of property isTemplate.
	 */
	private boolean isTemplate;

	/**
	 * Holds value of property link.
	 */
	private String link;

	/**
     * Holds value of property worldMailingSend.
     */
    private boolean worldMailingSend;

    /**
	 * Reset all properties to their default values.
	 * 
	 * @param mapping
	 *            The mapping used to select this instance
	 * @param request
	 *            The servlet request we are processing
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {

		this.mailingID = 0;
		this.shortname = ""; // text.getMessage(aLoc, "default.shortname");
		this.link = "";
	}

	/**
	 * Validate the properties that have been set from this HTTP request, and
	 * return an <code>ActionErrors</code> object that encapsulates any
	 * validation errors that have been found. If no errors are found, return
	 * <code>null</code> or an <code>ActionErrors</code> object with no
	 * recorded error messages.
	 * 
	 * @param mapping
	 *            The mapping used to select this instance
	 * @param request
	 *            The servlet request we are processing
	 * @return errors
	 */
	public ActionErrors formSpecificValidate(ActionMapping mapping,
			HttpServletRequest request) {

		ActionErrors errors = new ActionErrors();

		try {
			if (this.NewFile != null && action == MailingComponentsAction.ACTION_SAVE_COMPONENTS) {
				if (!this.NewFile.getFileName().equals(
						URLEncoder.encode(this.NewFile.getFileName(), "utf-8"))) {
					errors.add("global", new ActionMessage(
							"error.mailing.hosted_image_filename"));
				}
			}
		} catch (Exception e) {
			// do nothing
		}

		return errors;
	}

	/**
	 * Getter for property mailingID.
	 * 
	 * @return Value of property mailingID.
	 */
	public int getMailingID() {
		return this.mailingID;
	}

	/**
	 * Setter for property mailingID.
	 * 
	 * @param mailingID
	 *            New value of property mailingID.
	 */
	public void setMailingID(int mailingID) {
		this.mailingID = mailingID;
	}

	/**
	 * Getter for property shortname.
	 * 
	 * @return Value of property shortname.
	 */
	public String getShortname() {
		return this.shortname;
	}

	/**
	 * Setter for property shortname.
	 * 
	 * @param shortname
	 *            New value of property shortname.
	 */
	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	/**
	 * Getter for property description.
	 * 
	 * @return Value of property description.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Setter for property description.
	 * 
	 * @param description
	 *            New value of property description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Getter for property action.
	 * 
	 * @return Value of property action.
	 */
	public int getAction() {
		return this.action;
	}

	/**
	 * Setter for property action.
	 * 
	 * @param action
	 *            New value of property action.
	 */
	public void setAction(int action) {
		this.action = action;
	}

	/**
	 * Getter for property NewFile.
	 * 
	 * @return Value of property NewFile.
	 */
	public FormFile getNewFile() {
		return this.NewFile;
	}

	/**
	 * Setter for property NewFile.
	 * 
	 * @param newImage
	 *            New value of property newImage.
	 */
	public void setNewFile(FormFile newImage) {
		this.NewFile = newImage;
	}

	/**
	 * Getter for property isTemplate.
	 * 
	 * @return Value of property isTemplate.
	 */
	public boolean isIsTemplate() {
		return this.isTemplate;
	}

	/**
	 * Setter for property isTemplate.
	 * 
	 * @param isTemplate
	 *            New value of property isTemplate.
	 */
	public void setIsTemplate(boolean isTemplate) {
		this.isTemplate = isTemplate;
	}

	/**
	 * Getter for property link.
	 * 
	 * @return Value of property link.
	 */
	public String getLink() {
		return link;
	}

	/**
	 * Setter for property link.
	 * 
	 * @param link
	 *            New value of property link.
	 */
	public void setLink(String link) {
		this.link = link;
	}

    /**
     * Getter for property worldMailingSend.
     *
     * @return Value of property worldMailingSend.
     */
    public boolean isWorldMailingSend() {
        return this.worldMailingSend;
    }

    /**
     * Setter for property worldMailingSend.
     *
     * @param worldMailingSend New value of property worldMailingSend.
     */
    public void setWorldMailingSend(boolean worldMailingSend) {
        this.worldMailingSend = worldMailingSend;
    }
}
