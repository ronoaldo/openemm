package org.agnitas.beans.factory.impl;

import org.agnitas.beans.Mailing;
import org.agnitas.beans.factory.MailingFactory;
import org.agnitas.beans.impl.MailingImpl;

public class MailingFactoryImpl implements MailingFactory {
    @Override
    public Mailing newMailing() {
        MailingImpl mailing = new MailingImpl();   
        return mailing;
    }
}
