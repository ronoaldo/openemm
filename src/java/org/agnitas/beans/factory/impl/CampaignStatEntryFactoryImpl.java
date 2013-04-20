package org.agnitas.beans.factory.impl;

import org.agnitas.beans.factory.CampaignStatEntryFactory;
import org.agnitas.stat.CampaignStatEntry;
import org.agnitas.stat.impl.CampaignStatEntryImpl;


public class CampaignStatEntryFactoryImpl implements CampaignStatEntryFactory {
    @Override
    public CampaignStatEntry newCampaignStatEntry() {
        return new CampaignStatEntryImpl();
    }
}
