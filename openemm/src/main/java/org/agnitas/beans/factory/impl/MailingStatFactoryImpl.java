package org.agnitas.beans.factory.impl;

import org.agnitas.beans.factory.MailingStatEntryFactory;
import org.agnitas.beans.factory.MailingStatFactory;
import org.agnitas.beans.factory.URLStatEntryFactory;
import org.agnitas.dao.MailingDao;
import org.agnitas.dao.TargetDao;
import org.agnitas.stat.MailingStat;
import org.agnitas.stat.impl.MailingStatImpl;

import javax.sql.DataSource;


public class MailingStatFactoryImpl implements MailingStatFactory {

    private TargetDao targetDao;
    private MailingDao mailingDao;
    private MailingStatEntryFactory mailingStatEntryFactory;
    private URLStatEntryFactory urlStatEntryFactory;
    private DataSource dataSource;

    @Override
    public MailingStat newMailingStat() {
        MailingStatImpl mailingStat = new MailingStatImpl();
        mailingStat.setTargetDao(targetDao);
        mailingStat.setMailingDao(mailingDao);
        mailingStat.setMailingStatEntryFactory(mailingStatEntryFactory);
        mailingStat.setUrlStatEntryFactory(urlStatEntryFactory);
        mailingStat.setDataSource(dataSource);
        return mailingStat;
    }

    public void setTargetDao(TargetDao targetDao) {
        this.targetDao = targetDao;
    }

    public void setMailingDao(MailingDao mailingDao) {
        this.mailingDao = mailingDao;
    }

    public void setMailingStatEntryFactory(MailingStatEntryFactory mailingStatEntryFactory) {
        this.mailingStatEntryFactory = mailingStatEntryFactory;
    }

    public void setUrlStatEntryFactory(URLStatEntryFactory urlStatEntryFactory) {
        this.urlStatEntryFactory = urlStatEntryFactory;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
