package org.agnitas.beans.impl;

import org.agnitas.beans.MailingStatView;

public class MailingStatViewImpl implements MailingStatView {

	private String description;
	private String listname;
	private long mailingid;
	private String shortname;
	
	
	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getListname() {
		return listname;
	}

	@Override
	public long getMailingid() {
		return mailingid;
	}

	@Override
	public String getShortname() {
		return shortname;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public void setListname(String listname) {
		this.listname = listname;
	}

	@Override
	public void setMailingid(long mailingid) {
		this.mailingid = mailingid;
	}

	@Override
	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

}
