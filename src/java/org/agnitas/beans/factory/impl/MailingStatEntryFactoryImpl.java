package org.agnitas.beans.factory.impl;

import org.agnitas.beans.factory.MailingStatEntryFactory;
import org.agnitas.stat.MailingStatEntry;
import org.agnitas.stat.impl.MailingStatEntryImpl;


public class MailingStatEntryFactoryImpl implements MailingStatEntryFactory {
    @Override
    public MailingStatEntry newMailingStatEntry() {
        return new MailingStatEntryImpl();
    }
}
