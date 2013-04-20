package org.agnitas.beans.factory.impl;

import org.agnitas.beans.DynamicTag;
import org.agnitas.beans.factory.DynamicTagFactory;
import org.agnitas.beans.impl.DynamicTagImpl;

import java.util.LinkedHashMap;

public class DynamicTagFactoryImpl implements DynamicTagFactory {
    @Override
    public DynamicTag newDynamicTag() {
        DynamicTagImpl dynamicTag = new DynamicTagImpl();
        dynamicTag.setDynContent(new LinkedHashMap());
        return dynamicTag;
    }
}
