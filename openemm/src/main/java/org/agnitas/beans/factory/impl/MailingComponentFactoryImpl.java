package org.agnitas.beans.factory.impl;

import org.agnitas.beans.MailingComponent;
import org.agnitas.beans.factory.MailingComponentFactory;
import org.agnitas.beans.impl.MailingComponentImpl;


public class MailingComponentFactoryImpl implements MailingComponentFactory {
    @Override
    public MailingComponent newMailingComponent() {
        return new MailingComponentImpl();
    }
}
