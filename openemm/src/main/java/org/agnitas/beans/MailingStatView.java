package org.agnitas.beans;

public interface MailingStatView {

	public long getMailingid();
	public String getShortname();
	public String getDescription();
	public String getListname();

	public void setMailingid(long mailingid);
	public void setShortname(String shortname);
	public void setDescription(String description);
	public void setListname(String listname);
}
