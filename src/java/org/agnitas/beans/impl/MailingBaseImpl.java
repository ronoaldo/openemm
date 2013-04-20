package org.agnitas.beans.impl;

import java.util.Date;

import org.agnitas.beans.MailingBase;
import org.agnitas.beans.Mailinglist;

public class MailingBaseImpl implements MailingBase {

	protected int mailinglistID;
	protected int id;
	protected int companyID;
	protected int campaignID;
	private Mailinglist mailinglist;
	private Date sendDate;
	private boolean hasActions;
	
	/**
	 * Holds value of property description.
	 */
	protected String description;
	/**
	 * Holds value of property shortname.
	 */
	protected String shortname;

	private boolean useDynamicTemplate;
	
	public MailingBaseImpl() {
		super();
	}

	public void setCompanyID(int tmpid) {
	    companyID=tmpid;
	}

	public void setCampaignID(int tmpid) {
	    campaignID=tmpid;
	}

	public void setId(int tmpid) {
	    id=tmpid;
	}

	public void setMailinglistID(int tmpid) {
	    mailinglistID=tmpid;
	}

	/** Getter for property description.
	 * @return Value of property description.
	 */
	public String getDescription() {
	    return description;
	}

	/** Setter for property description.
	 * @param description New value of property description.
	 */
	public void setDescription(String description) {
	    this.description = description;
	}

	/** Getter for property shortname.
	 * @return Value of property shortname.
	 */
	public String getShortname() {
	    return shortname;
	}

	/** Setter for property shortname.
	 * @param shortname New value of property shortname.
	 */
	public void setShortname(String shortname) {
	    this.shortname = shortname;
	}

    public void setHasActions(boolean hasActions) {
        this.hasActions = hasActions;
    }

    public boolean isHasActions() {
        return hasActions;
    }

    public int getId() {
	    return id;
	}

	public int getMailinglistID() {
	
	    return mailinglistID;
	}

	public int getCompanyID() {
	    return companyID;
	}

	public int getCampaignID() {
	    return campaignID;
	}


	public Mailinglist getMailinglist() {
		return mailinglist;
	}


	public void setMailinglist(Mailinglist mailinglist) {
		this.mailinglist = mailinglist;		
	}

	@Override
	public Date getSenddate() {
		return sendDate;
	}

	@Override
	public void setSenddate(Date sendDate) {
		this.sendDate = sendDate;		
	}

	@Override
	public boolean getUseDynamicTemplate() {
		return this.useDynamicTemplate;
	}

	@Override
	public void setUseDynamicTemplate(boolean useDynamicTemplate) {
		this.useDynamicTemplate = useDynamicTemplate;
	}

}