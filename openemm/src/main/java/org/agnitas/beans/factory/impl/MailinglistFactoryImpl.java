package org.agnitas.beans.factory.impl;

import org.agnitas.beans.Mailinglist;
import org.agnitas.beans.factory.MailinglistFactory;
import org.agnitas.beans.impl.MailinglistImpl;


public class MailinglistFactoryImpl implements MailinglistFactory {
    public Mailinglist newMailinglist(){
        MailinglistImpl mailinglist = new MailinglistImpl();
        return mailinglist;
    }

}
