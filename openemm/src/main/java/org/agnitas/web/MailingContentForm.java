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

package org.agnitas.web;


import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.agnitas.beans.DynamicTag;
import org.agnitas.beans.DynamicTagContent;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.DynTagNameComparator;
import org.agnitas.web.forms.StrutsFormBase;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;


public class MailingContentForm extends StrutsFormBase {
    private static final long serialVersionUID = -5368233817734972584L;

    private static final transient Logger logger = Logger.getLogger(MailingContentForm.class);
	
	protected final Pattern dynamicContentParameterPatter = Pattern.compile( "^content\\(\\d+\\)\\.dynContent$");
    
    public static final int TEXTAREA_WIDTH = 85;

    private int mailingID;
    private String shortname;
    private String description;
    private int action;
    private boolean isTemplate;
    private boolean worldMailingSend;
    private boolean showHTMLEditor;
    protected boolean noImages;
    private int dynNameID;
    private int newTargetID;
    private String newContent;
    private Map<String, DynamicTag> tags;
    private Map<String, DynamicTagContent> content;
    private int contentID;
    private int previewFormat;
    private int previewSize;
    private int previewCustomerID;
    private int mailinglistID;
    private int mailFormat;
    private String dynName;
    
    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        
        this.mailingID=0;
        this.shortname="";
        this.contentID=0;
        this.newContent="";
        this.newTargetID=0;
        this.noImages=false;
    }
    
    /**
     * Validate the properties that have been set from this HTTP request,
     * and return an <code>ActionErrors</code> object that encapsulates any
     * validation errors that have been found.  If no errors are found, return
     * <code>null</code> or an <code>ActionErrors</code> object with no
     * recorded error messages.
     * 
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     * @return errors
     */
    @Override
    public ActionErrors formSpecificValidate(ActionMapping mapping,
    HttpServletRequest request) {
        
        ActionErrors errors = new ActionErrors();
                
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
     * @param mailingID New value of property mailingID.
     */
    public void setMailingID(int mailingID) {
        this.mailingID = mailingID;
        if (mailingID == 0) {
            logger.error("Mailing ID is set to 0 for MailingContentForm. That can be a reason of problem with saving of mailing with companyId = 0.");
        }
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
     * @param shortname New value of property shortname.
     */
    public void setShortname(String shortname) {
        this.shortname = shortname;
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
     * @param action New value of property action.
     */
    public void setAction(int action) {
        this.action = action;
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
     * @param isTemplate New value of property isTemplate.
     */
    public void setIsTemplate(boolean isTemplate) {
        this.isTemplate = isTemplate;
    }

    /**
     * Getter for property dynNameID.
     *
     * @return Value of property dynNameID.
     */
    public int getDynNameID() {

        return this.dynNameID;
    }

    /**
     * Setter for property dynNameID.
     *
     * @param dynNameID New value of property dynNameID.
     */
    public void setDynNameID(int dynNameID) {

        this.dynNameID = dynNameID;
    }

    /**
     * Getter for property newContent.
     *
     * @return Value of property newContent.
     */
    public String getNewContent() {

        return this.newContent;
    }

    /**
     * Setter for property newContent.
     *
     * @param newContent New value of property newContent.
     */
    public void setNewContent(String newContent) {

        this.newContent = newContent;
    }

    /**
     * Getter for property newTargetID.
     *
     * @return Value of property newTargetID.
     */
    public int getNewTargetID() {

        return this.newTargetID;
    }

    /**
     * Setter for property newTargetID.
     *
     * @param newTargetID New value of property newTargetID.
     */
    public void setNewTargetID(int newTargetID) {

        this.newTargetID = newTargetID;
    }
        
    /**
     * Getter for property tags.
     *
     * @return Value of property tags.
     */
    public Map<String, DynamicTag> getTags() {

        return this.tags;
    }

    /**
     * Setter for property tags.
     *
     * @param content New value of property tags.
     */
    public void setTags(Map<String, DynamicTag> tags) {
        this.setTags(tags, false);
    }

    /**
     * Setter for property content. Performs sorting if the corresponding parameter is true
     *
     * @param content New value of property content.
     * @param sortContent do we need to sort the content?
     */
    public void setTags(Map<String, DynamicTag> tags, boolean sortTags) {
        if (sortTags) {
            this.tags = AgnUtils.sortMap(tags, new DynTagNameComparator());
        }
        else {
            this.tags = tags;
        }
    }

    /**
     * Getter for property content.
     *
     * @return Value of property content.
     */
    public Map<String, DynamicTagContent> getContent() {

        return this.content;
    }

    /**
     * Setter for property content.
     *
     * @param content New value of property content.
     */
    public void setContent(Map<String, DynamicTagContent> content) {
        this.setContent(content, false);
    }

    /**
     * Setter for property content. Performs sorting if the corresponding parameter is true
     *
     * @param content New value of property content.
     * @param sortContent do we need to sort the content?
     */
    public void setContent(Map<String, DynamicTagContent> content, boolean sortContent) {
        if (sortContent) {
            this.content = AgnUtils.sortMap(content, new DynTagNameComparator());
        }
        else {
            this.content = content;
        }
    }

    /**
     * Getter for property contentID.
     *
     * @return Value of property contentID.
     */
    public int getContentID() {
        return this.contentID;
    }

    /**
     * Setter for property contentID.
     *
     * @param contentID New value of property contentID.
     */
    public void setContentID(int contentID) {
        this.contentID = contentID;
    }

    /**
     * Getter for property previewFormat.
     *
     * @return Value of property previewFormat.
     */
    public int getPreviewFormat() {
        return this.previewFormat;
    }

    /**
     * Setter for property previewFormat.
     *
     * @param previewFormat New value of property previewFormat.
     */
    public void setPreviewFormat(int previewFormat) {
        this.previewFormat = previewFormat;
    }

    /**
     * Getter for property previewSize.
     *
     * @return Value of property previewSize.
     */
    public int getPreviewSize() {
        return this.previewSize;
    }

    /**
     * Setter for property previewSize.
     *
     * @param previewSize New value of property previewSize.
     */
    public void setPreviewSize(int previewSize) {
        this.previewSize = previewSize;
    }

    /**
     * Getter for property previewCustomerID.
     *
     * @return Value of property previewCustomerID.
     */
    public int getPreviewCustomerID() {
        return this.previewCustomerID;
    }

    /**
     * Setter for property previewCustomerID.
     *
     * @param previewCustomerID New value of property previewCustomerID.
     */
    public void setPreviewCustomerID(int previewCustomerID) {
        this.previewCustomerID = previewCustomerID;
    }
    
    /**
     * Getter for property mailinglistID.
     *
     * @return Value of property mailinglistID.
     */
    public int getMailinglistID() {
        return this.mailinglistID;
    }

    /**
     * Setter for property mailinglistID.
     *
     * @param mailinglistID New value of property mailinglistID.
     */
    public void setMailinglistID(int mailinglistID) {
        this.mailinglistID = mailinglistID;
    }

    /**
     * Getter for property mailFormat.
     *
     * @return Value of property mailFormat.
     */
    public int getMailFormat() {
        return this.mailFormat;
    }

    /**
     * Setter for property mailFormat.
     *
     * @param mailFormat New value of property mailFormat.
     */
    public void setMailFormat(int mailFormat) {
        this.mailFormat = mailFormat;
    }

    /**
     * Getter for property dynName.
     *
     * @return Value of property dynName.
     */
    public String getDynName() {
        return this.dynName;
    }

    /**
     * Setter for property dynName.
     *
     * @param dynName New value of property dynName.
     */
    public void setDynName(String dynName) {
        this.dynName = dynName;
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

    public boolean isShowHTMLEditor() {
        return this.showHTMLEditor;
    }

    public void setShowHTMLEditor(boolean showHTMLEditor) {
        this.showHTMLEditor = showHTMLEditor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	@Override
	protected boolean isParameterExcludedForUnsafeHtmlTagCheck(	String parameterName, HttpServletRequest request) {
		if( parameterName.equals( "newContent"))
			return true;
		
		Matcher m = this.dynamicContentParameterPatter.matcher( parameterName);
		return m.matches();
	}

    public boolean isNoImages() {
        return noImages;
    }

    public void setNoImages(boolean noImages) {
        this.noImages = noImages;
    }
}
