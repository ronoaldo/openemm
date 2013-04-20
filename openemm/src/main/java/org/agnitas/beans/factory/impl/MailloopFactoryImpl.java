package org.agnitas.beans.factory.impl;

import org.agnitas.beans.Mailloop;
import org.agnitas.beans.factory.MailloopFactory;
import org.agnitas.beans.impl.MailloopImpl;


public class MailloopFactoryImpl implements MailloopFactory {
    @Override
    public Mailloop newMailloop() {
        return new MailloopImpl();
    }
}
