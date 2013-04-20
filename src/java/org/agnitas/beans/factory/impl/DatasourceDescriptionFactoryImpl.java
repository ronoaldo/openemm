package org.agnitas.beans.factory.impl;

import org.agnitas.beans.DatasourceDescription;
import org.agnitas.beans.factory.DatasourceDescriptionFactory;
import org.agnitas.beans.impl.DatasourceDescriptionImpl;


public class DatasourceDescriptionFactoryImpl implements DatasourceDescriptionFactory {
    @Override
    public DatasourceDescription newDatasourceDescription() {
        return new DatasourceDescriptionImpl();
    }
}
