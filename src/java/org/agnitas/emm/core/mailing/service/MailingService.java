package org.agnitas.emm.core.mailing.service;

import java.util.List;

import org.agnitas.beans.Mailing;


public interface MailingService {

	int addMailing(MailingModel model);
	
	int addMailingFromTemplate(MailingModel model);

	Mailing getMailing(MailingModel model);

	void updateMailing(MailingModel model);

	void deleteMailing(MailingModel model);
	
	List<Mailing> getMailings(MailingModel model);
	
	List<Mailing> getMailingsForMLID(MailingModel model);

	void sendMailing(MailingModel model);

}
