/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

package org.agnitas.beans;

import java.util.Hashtable;

public interface CampaignStats {
	public int getBounces();
    public int getClicks();
    public int getOpened();
    public int getOptouts();
    public int getSubscribers();
	public Hashtable getMailingData();
    public int getMaxBounces();
    public int getMaxClicks();
    public int getMaxOpened();
    public int getMaxOptouts();
    public int getMaxSubscribers();

    public void setMaxBounces(int maxBounces);
	public void setMaxClicks(int maxClicks);
	public void setMaxOpened(int maxOpened);
	public void setMaxOptouts(int maxOptouts);
	public void setMaxSubscribers(int maxSubscribers);
    public void setSubscribers(int subscribers);
    public void setBounces(int bounces);
    public void setOptouts(int optouts);
    public void setOpened(int opened);
    public void setClicks(int clicks);

    public void setMaxClickRate(double maxClickRate);
    public void setMaxOpenRate(double maxOpenRate);
}
