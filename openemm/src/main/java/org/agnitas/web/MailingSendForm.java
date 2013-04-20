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

import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.agnitas.beans.Mailing;
import org.agnitas.beans.MailingBase;
import org.agnitas.stat.DeliveryStat;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.forms.StrutsFormBase;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

public class MailingSendForm extends StrutsFormBase {
	private static final transient Logger logger = Logger.getLogger(MailingSendForm.class);

    private static final long serialVersionUID = -2753995761202472679L;

    /**
     * Holds value of property mailingID.
     */
    protected int mailingID;

    /**
     * Holds value of property shortname.
     */
    protected String shortname;

    /**
     * Holds value of property description.
     */
    protected String description;

    /**
     * Holds value of property emailFormat.
     */
    protected int emailFormat;

    /**
     * Holds value of property action.
     */
    protected int action;

    /**
     * Holds value of property previewCustomerID.
     */
    protected int previewCustomerID;

    /**
     * Holds value of property preview.
     */
    protected String preview;

    /**
     * Holds value of property previewFormat.
     */
    protected int previewFormat;

    /**
     * Holds value of property previewSize.
     */
    protected int previewSize = 1;

    /**
     * Holds value of property subjectPreview.
     */
    protected String subjectPreview;

    /**
     * Holds value of property senderPreview.
     */
    protected String senderPreview;

    /**
     * Holds value of property sendStatText.
     */
    protected int sendStatText;

    /**
     * Holds value of property sendStatHtml.
     */
    protected int sendStatHtml;

    /**
     * Holds value of property sendStatOffline.
     */
    protected int sendStatOffline;

    /**
     * Holds value of property isTemplate.
     */
    protected boolean isTemplate;

    /**
     * Holds value of property deliveryStat.
     */
    protected DeliveryStat deliveryStat;

    /**
     * Holds value of property hasPreviewRecipient
     */
    protected boolean hasPreviewRecipient;

    /**
     * Indicates, whether the mailing uses deleted target groups or not.
     */
    protected boolean hasDeletedTargetGroups;

	/**
	 * The names of target groups assigned to the mailing
	 */
	protected Collection<String> targetGroupsNames;

	/**
	 * The height for stats-box
	 */
	protected int frameHeight;

	
	/**
	 * Are there any mailing transmissions running ? ( Test- , Admin-, or Worldmailings which are currently sent ?)
	 */
	
	private boolean isTransmissionRunning;

    /**
	 * No images in preview
	 */
	protected boolean noImages;
		
    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        try {
            TimeZone aZone = TimeZone.getTimeZone(AgnUtils.getAdmin(request).getAdminTimezone());
            GregorianCalendar aDate = new GregorianCalendar(aZone);
            this.sendHour = aDate.get(GregorianCalendar.HOUR_OF_DAY);
            this.sendMinute = aDate.get(GregorianCalendar.MINUTE);
            this.previewFormat = 1;
            sendStat = new HashMap<Integer, Integer>();
        } catch (Exception e) {
            // do nothing
        }
    }

    protected HttpServletRequest request;

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
    public ActionErrors formSpecificValidate(ActionMapping mapping,
                                 HttpServletRequest req) {

        ActionErrors errors = new ActionErrors();
        if (action == MailingSendAction.ACTION_CONFIRM_SEND_WORLD) {
            TimeZone aZone = TimeZone.getTimeZone(AgnUtils.getAdmin(req).getAdminTimezone());
            GregorianCalendar currentDate = new GregorianCalendar(aZone);
            GregorianCalendar sendDate = new GregorianCalendar(aZone);
            sendDate.set(Integer.parseInt(this.sendDate.substring(0, 4)), Integer.parseInt(this.sendDate.substring(4, 6)) - 1, Integer.parseInt(this.sendDate.substring(6, 8)), sendHour, sendMinute);
            if (currentDate.getTime().getTime() > sendDate.getTime().getTime()) {
                errors.add("global", new ActionMessage("error.you_choose_a_time_before_the_current_time"));
            }
        }
        if(action == MailingSendAction.ACTION_PREVIEW_SELECT){
            MailingContentForm aForm = null;
            if(req != null){
                aForm = (MailingContentForm) req.getSession().getAttribute("mailingContentForm");
                if(aForm != null) aForm.setNoImages(this.isNoImages());
            }
        }
        if(action == MailingSendAction.ACTION_PREVIEW){
            MailingContentForm aForm = null;
            if(req != null){
                aForm = (MailingContentForm) req.getSession().getAttribute("mailingContentForm");
                if(aForm != null) this.setNoImages(aForm.isNoImages());
            }
        }
        request = req;
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
     * Getter for property preview.
     *
     * @return Value of property preview.
     */
    public String getPreview() {
        return preview;
    }

    /**
     * Setter for property textPreview.
     *
     * @param textPreview New value of property textPreview.
     */
    public void setPreview(String preview) {
        this.preview = preview;
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
        if (logger.isDebugEnabled()) logger.debug("Setting format to: " + this.previewFormat);
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
     * Getter for property subjectPreview.
     *
     * @return Value of property subjectPreview.
     */
    public String getSubjectPreview() {
        return this.subjectPreview;
    }

    /**
     * Setter for property subjectPreview.
     *
     * @param subjectPreview New value of property subjectPreview.
     */
    public void setSubjectPreview(String subjectPreview) {
        this.subjectPreview = subjectPreview;
    }

    /**
     * Getter for property senderPreview.
     *
     * @return Value of property senderPreview.
     */
    public String getSenderPreview() {
        return this.senderPreview;
    }

    /**
     * Setter for property senderPreview.
     *
     * @param senderPreview New value of property senderPreview.
     */
    public void setSenderPreview(String senderPreview) {
        this.senderPreview = senderPreview;
    }


    private Map<Integer, Integer> sendStat = null;

    public Map<Integer, Integer> getSendStats() {
        return sendStat;
    }

    /**
     */
    public int getSendStat(int id) {
        if (sendStat.containsKey(id)) {
            return sendStat.get(id);
        }
        return 0;
    }

    public void setSendStat(int id, int value) {
        sendStat.put(id, value);
    }

    /**
     */
    public int getSendTotal() {
        Iterator<Integer> i = sendStat.keySet().iterator();
        int total = 0;

        while (i.hasNext()) {
            total += sendStat.get(i.next());
        }
        return total;
    }

    /**
     * Getter for property sendStatText.
     *
     * @return Value of property sendStatText.
     */
    public int getSendStatText() {
        return this.sendStatText;
    }

    /**
     * Setter for property sendStatText.
     *
     * @param sendStatText New value of property sendStatText.
     */
    public void setSendStatText(int sendStatText) {
        this.sendStatText = sendStatText;
    }

    /**
     * Getter for property sendStatHtml.
     *
     * @return Value of property sendStatHtml.
     */
    public int getSendStatHtml() {
        return this.sendStatHtml;
    }

    /**
     * Setter for property sendStatHtml.
     *
     * @param sendStatHtml New value of property sendStatHtml.
     */
    public void setSendStatHtml(int sendStatHtml) {
        this.sendStatHtml = sendStatHtml;
    }

    /**
     * Getter for property sendStatOffline.
     *
     * @return Value of property sendStatOffline.
     */
    public int getSendStatOffline() {
        return this.sendStatOffline;
    }

    /**
     * Setter for property sendStatOffline.
     *
     * @param sendStatOffline New value of property sendStatOffline.
     */
    public void setSendStatOffline(int sendStatOffline) {
        this.sendStatOffline = sendStatOffline;
    }

    /**
     * Getter for property sendStatAll.
     *
     * @return Value of property sendStatAll.
     * @deprecated replaced by getSendStat(0)
     */
    public int getSendStatAll() {
        return sendStat.get(0);
    }

    /**
     * Setter for property sendStatAll.
     *
     * @param sendStatAll New value of property sendStatAll.
     * @deprecated replaced by setSendStat(0, value)
     */
    public void setSendStatAll(int sendStatAll) {
        sendStat.put(0, sendStatAll);
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
     * Getter for property deliveryStat.
     *
     * @return Value of property deliveryStat.
     */
    public DeliveryStat getDeliveryStat() {

        return this.deliveryStat;
    }

    /**
     * Setter for property deliveryStat.
     *
     * @param deliveryStat New value of property deliveryStat.
     */
    public void setDeliveryStat(DeliveryStat deliveryStat) {

        this.deliveryStat = deliveryStat;
    }

    /**
     * Holds value of property mailing.
     */
    private Mailing mailing;

    /**
     * Getter for property mailing.
     *
     * @return Value of property mailing.
     */
    public MailingBase getMailing() {
        return this.mailing;
    }

    /**
     * Setter for property mailing.
     *
     * @param mailing New value of property mailing.
     */
    public void setMailing(Mailing mailing) {
        this.mailing = mailing;
    }

    /**
     * Holds value of property worldMailingSend.
     */
    private boolean worldMailingSend;

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

    /**
     * Holds value of property mailingtype.
     */
    private int mailingtype;

    /**
     * Getter for property mailingtype.
     *
     * @return Value of property mailingtype.
     */
    public int getMailingtype() {
        return this.mailingtype;
    }

    /**
     * Setter for property mailingtype.
     *
     * @param mailingtype New value of property mailingtype.
     */
    public void setMailingtype(int mailingtype) {
        this.mailingtype = mailingtype;
    }

    /**
     * Holds value of property sendDate.
     */
    private String sendDate;

    /**
     * Getter for property sendDate.
     *
     * @return Value of property sendDate.
     */
    public String getSendDate() {
        return this.sendDate;
    }

    /**
     * Setter for property sendDate.
     *
     * @param sendDate New value of property sendDate.
     */
    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    /**
     * Holds value of property sendHour.
     */
    private int sendHour;

    /**
     * Getter for property sendHour.
     *
     * @return Value of property sendHour.
     */
    public int getSendHour() {
        return this.sendHour;
    }

    /**
     * Setter for property sendHour.
     *
     * @param sendHour New value of property sendHour.
     */
    public void setSendHour(int sendHour) {
        this.sendHour = sendHour;
    }

    /**
     * Holds value of property sendMinute.
     */
    private int sendMinute;

    /**
     * Getter for property sendMinute.
     *
     * @return Value of property sendMinute.
     */
    public int getSendMinute() {
        return this.sendMinute;
    }

    /**
     * Setter for property sendMinute.
     *
     * @param sendMinute New value of property sendMinute.
     */
    public void setSendMinute(int sendMinute) {
        this.sendMinute = sendMinute;
    }

	/**
     * Getter for property frameHeight.
     *
     * @return Value of property frameHeight.
     */
	public int getFrameHeight() {
		return frameHeight;
	}

	/**
     * Setter for property frameHeight.
     *
     * @param frameHeight New value of property frameHeight.
     */
	public void setFrameHeight(int frameHeight) {
		this.frameHeight = frameHeight;
	}

	/**
     * Getter for property targetGroups.
     *
     * @return Value of property targetGroupsNames.
     */
	public Collection<String> getTargetGroupsNames() {
		return targetGroupsNames;
	}

	/**
     * Setter for property targetGroupsNames.
     *
     * @param targetGroupsNames New value of property targetGroupsNames.
     */
	public void setTargetGroupsNames(Collection<String> targetGroupsNames) {
		this.targetGroupsNames = targetGroupsNames;
	}

	/**
     * Holds value of property targetGroups.
     */
    private Collection<Integer> targetGroups;

    /**
     * Getter for property targetGroups.
     *
     * @return Value of property targetGroups.
     */
    public Collection<Integer> getTargetGroups() {
        return this.targetGroups;
    }

    /**
     * Setter for property targetGroups.
     *
     * @param targetGroups New value of property targetGroups.
     */
    public void setTargetGroups(Collection<Integer> targetGroups) {
        this.targetGroups = targetGroups;
    }

    /**
     * Getter for property emailFormat.
     *
     * @return Value of property emailFormat.
     */
    public int getEmailFormat() {
        return this.emailFormat;
    }

    /**
     * Setter for property emailFormat.
     *
     * @param emailFormat New value of property emailFormat.
     */
    public void setEmailFormat(int emailFormat) {
        this.emailFormat = emailFormat;
    }

    /**
     * Holds value of property mailinglistID.
     */
    private int mailinglistID;

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

    private int stepping = 0;

    /**
     * Getter for property stepping.
     *
     * @return Value of property stepping.
     */
    public int getStepping() {
        return this.stepping;
    }
    public int getStep() {
    	// Backward compatibility
    	return getStepping();
    }

    /**
     * Setter for property stepping.
     *
     * @param stepping New value of property stepping.
     */
    public void setStepping(int stepping) {
        this.stepping = stepping;
    }
    public void setStep(int stepping) {
    	// Backward compatibility
    	setStepping(stepping);
    }
    /**
     * Getter for property blocksize.
     *
     * @return Value of property blocksize.
     */
    public int getBlocksize() {
        return this.blocksize;
    }

    private int blocksize = 1000;

    /**
     * Setter for property blocksize.
     *
     * @param blocksize New value of property blocksize.
     */
    public void setBlocksize(int blocksize) {
        this.blocksize = blocksize;
    }

    public boolean isLocked() {
    	// dirty workaround, mailing could be null!
    	if (mailing == null) {
    		return true;
    	}

        return (mailing.getLocked() != 0 ? true : false);
    }

    public void setLocked(boolean locked) {
        mailing.setLocked(locked ? 1 : 0);
    }

    public boolean isCanSendWorld() {
        if (getMailingtype() != Mailing.TYPE_NORMAL && getMailingtype() != Mailing.TYPE_FOLLOWUP) {
            return false;
        }
        if (isWorldMailingSend()) {
            return false;
        }
        return true;
    }

	public boolean isHasPreviewRecipient() {
		return hasPreviewRecipient;
	}

	public void setHasPreviewRecipient(boolean hasPreviewRecipient) {
		this.hasPreviewRecipient = hasPreviewRecipient;
	}

	public void setHasDeletedTargetGroups(boolean hasDeletedTargetGroups) {
		this.hasDeletedTargetGroups = hasDeletedTargetGroups;
	}

	public boolean getHasDeletedTargetGroups() {
		return hasDeletedTargetGroups;
	}


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


	public boolean isTransmissionRunning() {
		return isTransmissionRunning;
	}

	public void setTransmissionRunning(boolean isTransmissionRunning) {
		this.isTransmissionRunning = isTransmissionRunning;
	}

    public boolean isNoImages() {
        return noImages;
    }

    public void setNoImages(boolean noImages) {
        this.noImages = noImages;
    }
}
