package org.agnitas.beans.factory.impl;

import org.agnitas.beans.factory.URLStatEntryFactory;
import org.agnitas.stat.URLStatEntry;
import org.agnitas.stat.impl.URLStatEntryImpl;

public class URLStatEntryFactoryImpl implements URLStatEntryFactory {
    @Override
    public URLStatEntry newURLStatEntry() {
        return new URLStatEntryImpl();
    }
}
