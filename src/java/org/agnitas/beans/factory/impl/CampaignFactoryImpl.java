package org.agnitas.beans.factory.impl;

import org.agnitas.beans.Campaign;
import org.agnitas.beans.factory.CampaignFactory;
import org.agnitas.beans.impl.CampaignImpl;


public class CampaignFactoryImpl implements CampaignFactory {
    @Override
    public Campaign newCampaign() {
        return new CampaignImpl();
    }
}
