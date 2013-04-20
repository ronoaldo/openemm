package org.agnitas.beans.factory.impl;

import org.agnitas.beans.Recipient;
import org.agnitas.beans.factory.RecipientFactory;
import org.agnitas.beans.impl.RecipientImpl;
import org.agnitas.dao.RecipientDao;
import org.agnitas.service.ColumnInfoService;


public class RecipientFactoryImpl implements RecipientFactory {

    protected RecipientDao recipientDao;
    protected ColumnInfoService columnInfoService;

    public void setRecipientDao(RecipientDao recipientDao) {
        this.recipientDao = recipientDao;
    }

    public void setColumnInfoService(ColumnInfoService columnInfoService) {
        this.columnInfoService = columnInfoService;
    }

    public Recipient newRecipient() {
        RecipientImpl recipient = new RecipientImpl();
        recipient.setRecipientDao(recipientDao);
        recipient.setColumnInfoService(columnInfoService);
        return recipient;
    }
}
