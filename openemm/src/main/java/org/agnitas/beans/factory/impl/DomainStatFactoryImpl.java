package org.agnitas.beans.factory.impl;

import org.agnitas.beans.factory.DomainStatFactory;
import org.agnitas.stat.DomainStat;
import org.agnitas.stat.impl.DomainStatImpl;


public class DomainStatFactoryImpl implements DomainStatFactory {
    @Override
    public DomainStat newDomainStat() {
        return new DomainStatImpl();
    }
}
