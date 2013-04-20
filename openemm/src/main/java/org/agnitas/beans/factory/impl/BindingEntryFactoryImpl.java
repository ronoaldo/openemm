package org.agnitas.beans.factory.impl;

import org.agnitas.beans.factory.BindingEntryFactory;
import org.agnitas.beans.impl.BindingEntryImpl;
import org.agnitas.beans.BindingEntry;
import org.agnitas.dao.BindingEntryDao;


public class BindingEntryFactoryImpl implements BindingEntryFactory {

    private BindingEntryDao bindingEntryDao;

    public BindingEntryDao getBindingEntryDao() {
        return bindingEntryDao;
    }

    public void setBindingEntryDao(BindingEntryDao bindingEntryDao) {
        this.bindingEntryDao = bindingEntryDao;
    }

    @Override
    public BindingEntry newBindingEntry() {
        BindingEntryImpl bindingEntry = new BindingEntryImpl();
        bindingEntry.setBindingEntryDao(bindingEntryDao);
        return bindingEntry;
    }
}
