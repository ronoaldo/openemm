package org.agnitas.beans.impl;

import java.util.Date;

import org.agnitas.beans.BlackListEntry;

public class BlackListEntryImpl implements BlackListEntry {

	
	private String email;
	private Date date;
		
	
	public BlackListEntryImpl(String email, Date date) {
		this.email = email;
		this.date = date;
	}

	public Date getDate() {
		return date;
	}

	public String getEmail() {
		return email;
	}

}
