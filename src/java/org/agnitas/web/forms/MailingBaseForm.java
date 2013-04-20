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

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import org.agnitas.beans.Campaign;
import org.agnitas.beans.Mailing;
import org.agnitas.beans.MailingBase;
import org.agnitas.beans.Mediatype;
import org.agnitas.beans.MediatypeEmail;
import org.agnitas.target.Target;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.MailingBaseAction;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 *
 * @author  mhe, Nicole Serek
 */
public class MailingBaseForm extends StrutsFormBase {
    
    private static final long serialVersionUID = 8995916091799817822L;
    public static final int TEXTAREA_WIDTH = 75;

	/** 
     * Holds value of property mailinglistID. 
     */
    private int mailingID;
    
    /**
     * Holds value of property campaignID. 
     */
    private int campaignID;
    
    /**
     * Holds value of property shortname. 
     */
    protected String shortname = "";
    
    /**
     * Holds value of property description. 
     */
    private String description;
    
    /**
     * Holds value of property emailCharset. 
     */
    private String emailCharset;
    
    /**
     * Holds value of property action. 
     */
    protected int action;
    
    /**
     * Holds value of property emailLinefeed.
     */
    private int emailLinefeed;
    
    /**
     * Holds value of property mailingType. 
     */
    protected int mailingType;
    
    /**
     * Holds value of property targetID. 
     */
    protected int targetID;
    
    /**
     * Holds value of property mailinglistID. 
     */
    protected int mailinglistID;
    
    /**
     * Holds value of property templateID. 
     */
    private int templateID;
    
    /**
     * Holds value of property worldMailingSend.
     */
    protected boolean worldMailingSend;
     
    /**
     * Holds value of property showTemplate.
     */
    private boolean showTemplate;
  
    /**
     * Holds value of property targetGroups.
     */
    protected Collection<Integer> targetGroups;
    
    /**
     * Holds value of property isTemplate.
     */
    private boolean isTemplate=false;
    
    /**
     * Holds value of property oldMailingID.
     */
    private int oldMailingID;

	/**
     * Holds value of property oldMailFormat.
     */
	private int oldMailFormat;

    /**
     * Holds value of property copyFlag.
     */
    private boolean copyFlag;
    
    /**
     * Holds value of property needsTarget.
     */
    private boolean needsTarget;
    
    /**
     * Holds value of property targetMode.
     */
    private int targetMode = Mailing.TARGET_MODE_AND;
    
    /**
     * The id of mailing that was clones (is used
	 * for cloning CMS content of the mailing)
     */
    private int originalMailingId;
    
    /**
     * Holds value of property replyEmail.
     */
    protected String emailReplytoEmail;
    
    /**
     * Holds value of property replyFullname.
     */
    protected String emailReplytoFullname;
    
    protected Map<String, String> actions;
    
    protected ActionMessages messages;

    protected ActionMessages errors;

    /**
     * Code for previous action (used in "confirm delete" dialogs to jump back to last view on cancel)
     */
    protected int previousAction;
    
    
    protected boolean templateContainerVisible;
    protected boolean otherMediaContainerVisible;
    protected boolean generalContainerVisible;
    protected boolean targetgroupsContainerVisible;
    /**
	 * @return the templateContainerVisible
	 */
	public boolean isTemplateContainerVisible() {
		return templateContainerVisible;
	}

	/**
	 * @param templateContainerVisible the templateContainerVisible to set
	 */
	public void setTemplateContainerVisible(boolean templateContainerVisible) {
		this.templateContainerVisible = templateContainerVisible;
	}

	/**
	 * @return the otherMediaContainerVisible
	 */
	public boolean isOtherMediaContainerVisible() {
		return otherMediaContainerVisible;
	}

	/**
	 * @param otherMediaContainerVisible the otherMediaContainerVisible to set
	 */
	public void setOtherMediaContainerVisible(boolean otherMediaContainerVisible) {
		this.otherMediaContainerVisible = otherMediaContainerVisible;
	}

	/**
	 * @return the generalContainerVisible
	 */
	public boolean isGeneralContainerVisible() {
		return generalContainerVisible;
	}

	/**
	 * @param generalContainerVisible the generalContainerVisible to set
	 */
	public void setGeneralContainerVisible(boolean generalContainerVisible) {
		this.generalContainerVisible = generalContainerVisible;
	}

	/**
	 * @return the targetgroupsContainerVisible
	 */
	public boolean isTargetgroupsContainerVisible() {
		return targetgroupsContainerVisible;
	}

	/**
	 * @param targetgroupsContainerVisible the targetgroupsContainerVisible to set
	 */
	public void setTargetgroupsContainerVisible(boolean targetgroupsContainerVisible) {
		this.targetgroupsContainerVisible = targetgroupsContainerVisible;
	}

    /**
     * Holds list of MailingBase.
     */
    protected List<MailingBase> templateMailingBases;

    /**
     * Holds template shortname selected by templateID.
     */
    protected String templateShortname;

    /**
     * Holds list of mailing lists.
     */
    protected List mailingLists;

    /**
     * Holds list of campaigns.
     */
    protected List<Campaign> campaigns;

    /**
     * Holds list of targets.
     */
    protected List<Target> targets;

    /**
     * Holds list of targets selected by ids from targetGroups.
     */
    protected List<Target> targetGroupsList;

    
	/** 
     * Creates a new instance of TemplateForm 
     */
    public MailingBaseForm() {
    }

    /**
     * Initialization.
     */
    public void clearData(int companyID, int defaultMediaType) throws Exception {
        this.targetID = 0;
        this.mailinglistID = 0;
        this.templateID = 0;
        this.campaignID = 0;
        this.mailingType = Mailing.TYPE_NORMAL;
        
        this.shortname = "";
        this.description = "";
       
        mediatypes = new HashMap<Integer, Mediatype>();
        Mediatype mt = (Mediatype) getWebApplicationContext().getBean("MediatypeEmail");
        mt.setStatus(Mediatype.STATUS_ACTIVE);
        mediatypes.put(new Integer(0), mt); 
        this.emailReplytoEmail = "";
        this.emailReplytoFullname = "";
        this.emailCharset = "UTF-8";
        this.emailLinefeed = 72;
        this.emailOnepixel = MediatypeEmail.ONEPIXEL_TOP;
        
        this.worldMailingSend = false;
        this.targetGroups = null;
        this.showTemplate = false;
        this.copyFlag = false;
        this.archived = false;
        this.needsTarget = false;
        this.targetMode = Mailing.TARGET_MODE_AND;
        
        this.templateContainerVisible = false;
        this.otherMediaContainerVisible = false;
        this.generalContainerVisible = false;
        this.targetgroupsContainerVisible = false;

    }
    
    @Override
    public void reset(ActionMapping map, HttpServletRequest request) {
    	this.archived = false;
        this.templateContainerVisible = false;
        this.otherMediaContainerVisible = false;
        this.generalContainerVisible = false;
        this.targetgroupsContainerVisible = false;
        this.dynamicTemplate = false;

    	super.reset(map, request);
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
        Mailing aMailing = null;

        if(action == MailingBaseAction.ACTION_SAVE) {

        	if (this.shortname.length() > 99)
        		errors.add("global", new ActionMessage("error.shortname_too_long"));

        	if(this.mailinglistID == 0) {
        		errors.add("global", new ActionMessage("error.mailing.noMailinglist"));
        	}
            
            if(request.getParameter("addtarget") != null && request.getParameter("addtarget").equals("addtarget")) {
                //this.action=MailingBaseAction.ACTION_VIEW_WITHOUT_LOAD;
                if(this.targetID != 0) {
                    if(this.targetGroups == null) {
                        this.targetGroups = new HashSet<Integer>();
                    }
                    this.targetGroups.add(new Integer(this.targetID));
                }
            }
            
            Enumeration allNames = request.getParameterNames();
            String aName = null;
            int tmpTarget = 0;
            while(allNames.hasMoreElements()) {
                aName = (String)allNames.nextElement();
                if(aName.startsWith("removetarget") && StringUtils.isNotEmpty(request.getParameter(aName))) {
                    try {
                        tmpTarget = Integer.parseInt(aName.substring(12));
                    } catch (Exception e) {
                        AgnUtils.logger().error("validate: "+e.getMessage());
                    }
                }
            }
            if(tmpTarget != 0) {
                this.targetID = tmpTarget;
                this.action = MailingBaseAction.ACTION_REMOVE_TARGET;
            }
        }
        
        if(action == MailingBaseAction.ACTION_SAVE) {
            if((this.isIsTemplate() == false) && this.isNeedsTarget() && this.targetGroups == null) {
                errors.add("global", new ActionMessage("error.mailing.rulebased_without_target"));
            }

            if(this.shortname.length()<3) {
                errors.add("shortname", new ActionMessage("error.nameToShort"));
            }
            
            // NEW CODE (to be inserted):
            if(this.emailReplytoFullname != null && this.emailReplytoFullname.length() > 255) {
                errors.add("replyFullname", new ActionMessage("error.reply_fullname_too_long"));
            }
            if(getSenderFullname() != null && getSenderFullname().length() > 255) {
                errors.add("senderFullname", new ActionMessage("error.sender_fullname_too_long"));
            }
            if(this.emailReplytoFullname != null && this.emailReplytoFullname.trim().length() == 0) {
                this.emailReplytoFullname = getSenderFullname();
            }
            
            if(this.targetGroups == null && this.mailingType == Mailing.TYPE_DATEBASED) {
                errors.add("global", new ActionMessage("error.mailing.rulebased_without_target"));
            }
            
            // NEW CODE (to be inserted):
            if(getMediaEmail().getFromEmail().length() < 3)
                errors.add("shortname", new ActionMessage("error.invalid.email"));
            
            if(getEmailSubject().length() < 2) {
                errors.add("subject", new ActionMessage("error.mailing.subject.too_short"));
            }
            
            try {
                InternetAddress adr = new InternetAddress(getMediaEmail().getFromEmail());
                if(adr.getAddress().indexOf("@") == -1) {
                    errors.add("sender", new ActionMessage("error.mailing.sender_adress"));
                }              
            } catch (Exception e) {
                if(getMediaEmail().getFromEmail().indexOf("[agn") == -1) {
                    errors.add("sender", new ActionMessage("error.mailing.sender_adress"));
                }
            }
            
            try {
                aMailing = (Mailing) getWebApplicationContext().getBean("Mailing");
                aMailing.setCompanyID(this.getCompanyID(request));
                aMailing.findDynTagsInTemplates(this.getEmailSubject(), this.getWebApplicationContext());
                aMailing.findDynTagsInTemplates(this.getSenderFullname(), this.getWebApplicationContext());
            } catch (Exception e) {
                AgnUtils.logger().error("validate: "+e);
                errors.add("subject", new ActionMessage("error.template.dyntags"));
            }
            
            try {
                aMailing = (Mailing) getWebApplicationContext().getBean("Mailing");
                aMailing.setCompanyID(this.getCompanyID(request));
                aMailing.personalizeText(this.getEmailSubject(), 0, this.getWebApplicationContext());
                aMailing.personalizeText(this.getSenderFullname(), 0, this.getWebApplicationContext());
            } catch (Exception e) {
                errors.add("subject", new ActionMessage("error.personalization_tag"));
            }
            
//            if(getTextTemplate().length() != 0) {
//                // Just a syntax-check, no MailingID required
//                aMailing = (Mailing) getWebApplicationContext().getBean("Mailing");
//                aMailing.setCompanyID(this.getCompanyID(request));
//                
//                try {
//                    aMailing.personalizeText(new String(this.getTextTemplate()), 0, this.getWebApplicationContext());
//                } catch (Exception e) {
//                    errors.add("texttemplate", new ActionMessage("error.personalization_tag"));
//                }
//                
//                try {
//                    aMailing.findDynTagsInTemplates(new String(getTextTemplate()), this.getWebApplicationContext());
//                } catch (Exception e) {
//                    errors.add("texttemplate", new ActionMessage("error.template.dyntags"));
//                }
//                
//            }
//            
//            if(getHtmlTemplate().length() != 0) {
//                // Just a syntax-check, no MailingID required
//                aMailing = (Mailing) getWebApplicationContext().getBean("Mailing");
//                aMailing.setCompanyID(this.getCompanyID(request));
//                
//                try {
//                    aMailing.personalizeText(new String(this.getHtmlTemplate()), 0, this.getWebApplicationContext());
//                } catch (Exception e) {
//                    errors.add("texttemplate", new ActionMessage("error.personalization_tag"));
//                }
//                
//                try {
//                    aMailing.findDynTagsInTemplates(new String(getHtmlTemplate()), this.getWebApplicationContext());
//                } catch (Exception e) {
//                    AgnUtils.logger().error("validate: find "+e);
//                    errors.add("texttemplate", new ActionMessage("error.template.dyntags"));
//                }
//            }
        }
        return errors;
    }

    @Override
    protected ActionErrors checkForHtmlTags(HttpServletRequest request) {
        if(action != MailingBaseAction.ACTION_VIEW_WITHOUT_LOAD){
            return super.checkForHtmlTags(request);
        }
        return new ActionErrors();
    }
    
    /**
     * Getter for property templateID.
     *
     * @return Value of property templateID.
     */
    public int getMailingID() {
        return this.mailingID;
    }
    
    /**
     * Setter for property templateID.
     * 
     * @param mailingID New value of property mailingID.
     */
    public void setMailingID(int mailingID) {
        this.mailingID = mailingID;
    }
    
    /**
     * Getter for property campaignID.
     *
     * @return Value of property campaignID.
     */
    public int getCampaignID() {
        return this.campaignID;
    }
    
    /**
     * Setter for property campaignID.
     * 
     * @param campaignID New value of property campaignID.
     */
    public void setCampaignID(int campaignID) {
        this.campaignID = campaignID;
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
     * @param description New value of property description.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Getter for property charset.
     *
     * @return Value of property charset.
     */
    public String getEmailCharset() {
        return this.emailCharset;
    }
    
    /**
     * Setter for property charset.
     * 
     * @param emailCharset New value of property emailCharset.
     */
    public void setEmailCharset(String emailCharset) {
        this.emailCharset = emailCharset;
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
     * Getter for property subject.
     *
     * @return Value of property subject.
     */
    public String getEmailSubject() {
        return getMediaEmail().getSubject();
    }
    
    /** 
     * Setter for property subject.
     *
     * @param subject New value of property subject.
     */
    public void setEmailSubject(String subject) {
        getMediaEmail().setSubject(subject);
    }
    
    /** 
     * Getter for property emailLinefeed.
     *
     * @return Value of property emailLinefeed.
     */
    public int getEmailLinefeed() {
        return this.emailLinefeed;
    }
    
    /**
     * Setter for property emailLinefeed.
     *
     * @param emailLinefeed New value of property emailLinefeed.
     */
    public void setEmailLinefeed(int emailLinefeed) {
        this.emailLinefeed = emailLinefeed;
    }
    
    /**
     * Getter for property mailingType.
     *
     * @return Value of property mailingType.
     */
    public int getMailingType() {
        return this.mailingType;
    }
    
    /**
     * Setter for property mailingType.
     *
     * @param mailingType New value of property mailingType.
     */
    public void setMailingType(int mailingType) {
        this.mailingType = mailingType;
    }
    
    /** 
     * Getter for property targetID.
     *
     * @return Value of property targetID.
     */
    public int getTargetID() {
        return this.targetID;
    }
    
    /**
     * Setter for property targetID.
     *
     * @param targetID New value of property targetID.
     */
    public void setTargetID(int targetID) {
        this.targetID = targetID;
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
     * Getter for property templateID.
     *
     * @return Value of property templateID.
     */
    public int getTemplateID() {
        return this.templateID;
    }
    
    /**
     * Setter for property templateID.
     *
     * @param templateID New value of property templateID.
     */
    public void setTemplateID(int templateID) {
        this.templateID = templateID;
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
    
    
    /**
     * Getter for property htmlTemplate.
     * 
     * @return Value of property htmlTemplate.
     */
    public String getHtmlTemplate() {
        return getMediaEmail().getHtmlTemplate();
    }
    
    /**
     * Setter for property htmlTemplate.
     *
     * @param htmlTemplate New value of property htmlTemplate.
     */
    public void setHtmlTemplate(String htmlTemplate) {
        getMediaEmail().setHtmlTemplate(htmlTemplate);
    }
    
    /**
     * Getter for property textTemplate.
     *
     * @return Value of property textTemplate.
     */
    public String getTextTemplate() {
        return getMediaEmail().getTemplate();
    }
    
    /**
     * Setter for property textTemplate.
     *
     * @param textTemplate New value of property textTemplate.
     */
    public void setTextTemplate(String textTemplate) {
        getMediaEmail().setTemplate(textTemplate);
    }
    
    /**
     * Getter for property showTemplate.
     *
     * @return Value of property showTemplate.
     */
    public boolean isShowTemplate() {
        return this.showTemplate;
    }
    
    /**
     * Setter for property showTemplate.
     *
     * @param showTemplate New value of property showTemplate.
     */
    public void setShowTemplate(boolean showTemplate) {
        this.showTemplate = showTemplate;
    }
    
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
     * Getter for property oldMailingID.
     *
     * @return Value of property oldMailingID.
     */
    public int getOldMailingID() {
        return this.oldMailingID;
    }
    
    /**
     * Setter for property oldMailingID.
     *
     * @param oldMailingID New value of property oldMailingID.
     */
    public void setOldMailingID(int oldMailingID) {
        this.oldMailingID = oldMailingID;
    }
    
    /**
     * Getter for property copyFlag.
     *
     * @return Value of property copyFlag.
     */
    public boolean isCopyFlag() {
        return this.copyFlag;
    }
    
    /**
     * Setter for property copyFlag.
     *
     * @param copyFlag New value of property copyFlag.
     */
    public void setCopyFlag(boolean copyFlag) {
        this.copyFlag = copyFlag;
    }
    
    /**
     * Getter for property needsTarget.
     *
     * @return Value of property needsTarget.
     */
    public boolean isNeedsTarget() {
        return this.needsTarget;
    }
    
    /**
     * Setter for property needsTarget.
     *
     * @param needsTarget New value of property needsTarget.
     */
    public void setNeedsTarget(boolean needsTarget) {
        this.needsTarget = needsTarget;
    }
    
    /**
     * Getter for property targetMode.
     *
     * @return Value of property targetMode.
     */
    public int getTargetMode() {
        return this.targetMode;
    }
    
    /**
     * Setter for property targetMode.
     *
     * @param targetMode New value of property targetMode.
     */
    public void setTargetMode(int targetMode) {
        this.targetMode = targetMode;
    }
    
    /**
     * Getter for property senderFullname.
     *
     * @return Value of property senderFullname.
     */
    public String getSenderFullname() {
        return getMediaEmail().getFromFullname();
    }
    
    /**
     * Setter for property senderFullname.
     *
     * @param senderFullname New value of property senderFullname.
     */
    public void setSenderFullname(String senderFullname) {
        getMediaEmail().setFromFullname(senderFullname);
    }
    
    /**
     * Getter for property replyEmail.
     *
     * @return Value of property replyEmail.
     */
    public String getEmailReplytoEmail() {
        
        return this.emailReplytoEmail;
    }
    
    /**
     * Setter for property replyEmail.
     *
     * @param replyEmail New value of property replyEmail.
     */
    public void setEmailReplytoEmail(String replyEmail) {
        
        this.emailReplytoEmail = replyEmail;
    }
    
    /**
     * Getter for property replyFullname.
     *
     * @return Value of property replyFullname.
     */
    public String getEmailReplytoFullname() {
        
        return this.emailReplytoFullname;
    }
    
    /**
	 * Getter for property originalMailingId.
     *
     * @return Value of property originalMailingId.
	 */
	public int getOriginalMailingId() {
		return originalMailingId;
	}

	/**
     * Setter for property originalMailingId.
     *
     * @param originalMailingId New value of property originalMailingId.
     */
	public void setOriginalMailingId(int originalMailingId) {
		this.originalMailingId = originalMailingId;
	}

	/**
     * Setter for property replyFullname.
     *
     * @param replyFullname New value of property replyFullname.
     */
    public void setEmailReplytoFullname(String replyFullname) {
        
        this.emailReplytoFullname = replyFullname;
    }
    
    /**
     * Holds value of property emailOnepixel.
     */
    private String emailOnepixel;
    
    /**
     * Getter for property emailOnepixel.
     *
     * @return Value of property emailOnepixel.
     */
    public String getEmailOnepixel() {
        
        return this.emailOnepixel;
    }
    
    /**
     * Setter for property emailOnepixel.
     *
     * @param emailOnepixel New value of property emailOnepixel.
     */
    public void setEmailOnepixel(String emailOnepixel) {
        
        this.emailOnepixel = emailOnepixel;
    }  

    protected Map<Integer, Mediatype> mediatypes;
    /**
     * Getter for property mediatypes.
     *
     * @return Value of property mediatypes.
     */
    public Map<Integer, Mediatype> getMediatypes() {
        if(mediatypes == null) {
            mediatypes=new HashMap<Integer, Mediatype>();
        }
        return mediatypes;
    }
   
    public Mediatype getMedia(int id) {
        Mediatype mt=(Mediatype) getMediatypes().get(new Integer(id));

        if(mt == null) { 
            switch(id) {
                case 0: mt=(Mediatype) getWebApplicationContext().getBean("MediatypeEmail");
                        break;
                default: return null;
            }
            mediatypes.put(new Integer(id), mt);
        }
        return mt;
    }
 
    public MediatypeEmail getMediaEmail() {
        return (MediatypeEmail) getMedia(0);
    }
 
    /**
     * Setter for property mediatypes.
     *
     * @param mediatypes New value of property mediatypes.
     */
    public void setMediatypes(Map<Integer, Mediatype> mediatypes) {
        this.mediatypes=mediatypes;
    }  
    
    /**
     * Holds value of property archived.
     */
    private boolean archived = false;

    /**
     * Getter for property archived.
     * @return Value of property archived.
     */
    public boolean isArchived() {
        return this.archived;
    }

    /**
     * Setter for property archived.
     * @param archived New value of property archived.
     */
    public void setArchived(boolean archived) {
        this.archived = archived;
    }

	public Map<String, String> getActions() {
		return actions;
	}

	public void setActions(Map<String, String> actions) {
		this.actions = actions;
	}
	
	/**
     * Holds value of property mailingTypeNormal.
     */
    protected boolean mailingTypeNormal = true;
    
    /**
     * Holds value of property mailingTypeEvent.
     */
    protected boolean mailingTypeEvent;
    
    /**
     * Holds value of property mailingTypeDate.
     */
    protected boolean mailingTypeDate;
    
    /**
     * Holds value of property types.
     */
    protected String types = "";

	private boolean dynamicTemplate;

    /**
     * Getter for property mailingTypeDate.
     *
     * @return Value of property mailingTypeDate.
     */
	public boolean getMailingTypeDate() {
		return mailingTypeDate;
	}

	/**
     * Setter for property mailingTypeDate.
     *
     * @param mailingTypeDate New value of property mailingTypeDate.
     */
	public void setMailingTypeDate(boolean mailingTypeDate) {
		this.mailingTypeDate = mailingTypeDate;
	}

	/**
     * Getter for property mailingTypeEvent.
     *
     * @return Value of property mailingTypeEvent.
     */
	public boolean getMailingTypeEvent() {
		return mailingTypeEvent;
	}

	/**
     * Setter for property mailingTypeEvent.
     *
     * @param mailingTypeEvent New value of property mailingTypeEvent.
     */
	public void setMailingTypeEvent(boolean mailingTypeEvent) {
		this.mailingTypeEvent = mailingTypeEvent;
	}

	/**
     * Getter for property mailingTypeNormal.
     *
     * @return Value of property mailingTypeNormal.
     */
	public boolean getMailingTypeNormal() {
		return mailingTypeNormal;
	}

	/**
     * Setter for property mailingTypeNormal.
     *
     * @param mailingTypeNormal New value of property mailingTypeNormal.
     */
	public void setMailingTypeNormal(boolean mailingTypeNormal) {
		this.mailingTypeNormal = mailingTypeNormal;
	}
	
	public void setMessages(ActionMessages messages) {
		this.messages = messages;
	}
	
	public ActionMessages getMessages() {
		return this.messages;
	}

	/**
     * Getter for property mailingType.
     *
     * @return Value of property mailingType.
     */
	public String getTypes() {
		types = "";
		if(mailingTypeNormal) {
			types = "0";
		}
		if(mailingTypeEvent) {
			if(!types.equals("")) {
				types = types + ",";
			}
			types = types + "1";
		}
		if(mailingTypeDate) {
			if(!types.equals("")) {
				types = types + ",";
			}
			types = types + "2";
		}
		if(types.equals("")) {
			types = "100";
		}
		return types;
	}

	public int getPreviousAction() {
		return previousAction;
	}

	public void setPreviousAction(int previousAction) {
		this.previousAction = previousAction;
	}

	public int getOldMailFormat() {
		return oldMailFormat;
	}

	public void setOldMailFormat(int oldMailFormat) {
		this.oldMailFormat = oldMailFormat;
	}

	public void setErrors(ActionMessages errors) {
		this.errors = errors;
	}

	public ActionMessages getErrors() {
		return this.errors;
	}

    public List<MailingBase> getTemplateMailingBases() {
        return templateMailingBases;
    }

    public void setTemplateMailingBases(List<MailingBase> templateMailingBases) {
        this.templateMailingBases = templateMailingBases;
    }

    public String getTemplateShortname() {
        return templateShortname;
    }

    public void setTemplateShortname(String templateShortname) {
        this.templateShortname = templateShortname;
    }

    public List getMailingLists() {
        return mailingLists;
    }

    public void setMailingLists(List mailingLists) {
        this.mailingLists = mailingLists;
    }

    public List<Campaign> getCampaigns() {
        return campaigns;
    }

    public void setCampaigns(List<Campaign> campaigns) {
        this.campaigns = campaigns;
    }

    public List<Target> getTargets() {
        return targets;
    }

    public void setTargets(List<Target> targets) {
        this.targets = targets;
    }

    public List<Target> getTargetGroupsList() {
        return targetGroupsList;
    }

    public void setTargetGroupsList(List<Target> targetGroupsList) {
        this.targetGroupsList = targetGroupsList;
    }

	public void setUseDynamicTemplate(boolean dynamicTemplate) {
		this.dynamicTemplate = dynamicTemplate;
	}

	public boolean getUseDynamicTemplate() {
		return this.dynamicTemplate;
	}
	
	public void setDynamicTemplateString( String dynamicTemplateString) {
		if( dynamicTemplateString == null)
			this.dynamicTemplate = false;
		else
			this.dynamicTemplate = dynamicTemplateString.equals( "on") || dynamicTemplateString.equals( "on") || dynamicTemplateString.equals( "true");
	}

	public String getDynamicTemplateString() {
		if( dynamicTemplate)
			return "on";
		else
			return "";
	}
	
	@Override
	protected boolean isParameterExcludedForUnsafeHtmlTagCheck( String parameterName, HttpServletRequest request) {
		return parameterName.equals( "textTemplate") || parameterName.equals( "htmlTemplate");
	}
}
