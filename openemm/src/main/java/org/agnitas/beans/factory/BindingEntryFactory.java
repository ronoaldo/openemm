package org.agnitas.beans.factory;

import org.agnitas.beans.BindingEntry;
import org.agnitas.dao.BindingEntryDao;


public interface BindingEntryFactory {
    public BindingEntry newBindingEntry();
    public BindingEntryDao getBindingEntryDao();
    public void setBindingEntryDao(BindingEntryDao bindingEntryDao);
}
