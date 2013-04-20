package org.agnitas.mailing.beans.impl;

import org.agnitas.beans.MaildropEntry;
import org.agnitas.beans.impl.MaildropEntryImpl;
import org.agnitas.mailing.beans.MaildropEntryFactory;

public class MailDropEntryFactoryImpl implements MaildropEntryFactory {

    @Override
	public MaildropEntry createMaildropEntry() {
		return new MaildropEntryImpl();
	}

}
