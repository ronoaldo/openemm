package org.agnitas.emm.core.mailinglist.service;

import java.util.List;

import org.agnitas.beans.Mailinglist;
import org.agnitas.emm.core.mailinglist.service.MailinglistModel;

public interface MailinglistService {

	int addMailinglist(MailinglistModel model);

    void updateMailinglist(MailinglistModel model);

	Mailinglist getMailinglist(MailinglistModel model);

	boolean deleteMailinglist(MailinglistModel model);

	List<Mailinglist> getMailinglists(MailinglistModel model);
	
}
