package org.agnitas.beans;

import java.util.Date;

public interface MailingBase {

	/**
	 * Getter for property companyID.
	 *
	 * @return Value of property companyID.
	 */
	public abstract int getCompanyID();

	/**
	 * Getter for property campaignID.
	 *
	 * @return Value of property campaignID.
	 */
	public abstract int getCampaignID();

	/**
	 * Getter for property description.
	 *
	 * @return Value of property description.
	 */
	public abstract String getDescription();

	/**
	 * Getter for property id.
	 *
	 * @return Value of property id.
	 */
	public abstract int getId();

	/**
	 * Getter for property mailinglistID.
	 *
	 * @return Value of property mailinglistID.
	 */
	public abstract int getMailinglistID();
	
	public abstract Mailinglist getMailinglist();
	
	public abstract Date getSenddate();
	
	/**
	 * Getter for property shortname.
	 *
	 * @return Value of property shortname.
	 */
	public abstract String getShortname();

	/**
	 * Setter for property companyID.
	 *
	 * @param id New value of property companyID.
	 */
	public abstract void setCompanyID(int id);

	/**
	 * Setter for property campaignID.
	 *
	 * @param id New value of property campaignID.
	 */
	public abstract void setCampaignID(int id);

	/**
	 * Setter for property description.
	 *
	 * @param description New value of property description.
	 */
	public abstract void setDescription(String description);

	/**
	 * Setter for property id.
	 *
	 * @param id New value of proerty id.
	 */
	public abstract void setId(int id);

	/**
	 * Setter for property mailinglistID.
	 *
	 * @param id New value of proertymailinglistID.
	 */
	public abstract void setMailinglistID(int id);
	
	public abstract void setMailinglist(Mailinglist mailinglist);

	
	public abstract void setSenddate(Date sendDate);
	
	/**
	 * Setter for property shortname.
	 *
	 * @param shortname New value of property shortname.
	 */
	public abstract void setShortname(String shortname);


	public abstract void setHasActions(boolean hasActions);

    public abstract boolean isHasActions();

	public boolean getUseDynamicTemplate();
	public void setUseDynamicTemplate( boolean useDynamicTemplate);
}