package org.agnitas.beans.factory.impl;

import org.agnitas.beans.DynamicTagContent;
import org.agnitas.beans.factory.DynamicTagContentFactory;
import org.agnitas.beans.impl.DynamicTagContentImpl;


public class DynamicTagContentFactoryImpl implements DynamicTagContentFactory {
    @Override
    public DynamicTagContent newDynamicTagContent() {
        return new DynamicTagContentImpl();
    }
}
