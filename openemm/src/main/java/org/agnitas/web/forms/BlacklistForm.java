package org.agnitas.web.forms;

import java.util.List;

import org.agnitas.beans.Mailinglist;
import org.apache.struts.action.ActionMessages;

public class BlacklistForm extends StrutsFormBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = -437130377990091064L;
	private ActionMessages messages;
	private ActionMessages errors;
	
	private String newemail;
	
	/** List of mailinglists with blacklisted status for recipient. */
	private List<Mailinglist> blacklistedMailinglists;
	
	/** Array containing the status of the checkboxes for the mailinglists with blacklisted status for the recipient. */
	private int[] checkedBlacklistedMailingslists;

	public String getNewemail() {
		return newemail;
	}

	public void setNewemail(String newemail) {
		this.newemail = newemail;
	}
	
	public void setMessages(ActionMessages messages) {
		this.messages = messages;
	}
	
	public ActionMessages getMessages() {
		return this.messages;
	}

	public void setErrors(ActionMessages errors) {
		this.errors = errors;
	}

	public ActionMessages getErrors() {
		return errors;
	}

	public void setBlacklistedMailinglists( List<Mailinglist> mailinglists) {
		this.blacklistedMailinglists = mailinglists;
		this.checkedBlacklistedMailingslists = new int[mailinglists.size()];
	}
	
	public List<Mailinglist> getBlacklistedMailinglists() {
		return this.blacklistedMailinglists;
	}
	
	public int getCheckedBlacklistedMailinglists( int index) {
		return this.checkedBlacklistedMailingslists[index];
	}
	
	public void setCheckedBlacklistedMailinglists( int index, int value) {
		this.checkedBlacklistedMailingslists[index] = value;
	}
}
