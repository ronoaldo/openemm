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

package org.agnitas.beans.impl;

import org.agnitas.beans.Campaign;
import org.agnitas.beans.CampaignStats;

public class CampaignImpl implements Campaign {

	// hold the Stats of this Campaign.
	private CampaignStats campaignStatsImpl = null;
	
	// getter for campaignStats, creates the CampaignStat-Bean with
	// lazy creation.

    @Override
    public CampaignStats getCampaignStats() {
		if (campaignStatsImpl == null) {
			campaignStatsImpl = new CampaignStatsImpl();
		}
		return this.campaignStatsImpl;
    }


    // setter for CampaignStats.
	public void setCampaignStats(CampaignStatsImpl in_CampaignStats) {
		this.campaignStatsImpl = in_CampaignStats;
	}
	

	
//	public static class StatsImpl implements Campaign.Stats {
//        int clicks=0;
//        int opened=0;
//        int optouts=0;
//        int bounces=0;
//        int subscribers=0;
//        Hashtable mailingData = new Hashtable();
//        int maxBounces=0;
//        int maxClicks=0;
//        int maxOpened=0;
//        int maxOptouts=0;
//        int maxSubscribers=0;
//        double maxClickRate=0.0;
//        double maxOpenRate=0.0;
//
//        public int getBounces() {
//            return bounces;
//        }
//
//        public int getClicks() {
//            return clicks;
//        }
//
//        public double getMaxClickRate() {
//			return maxClickRate;
//		}
//
//		public double getMaxOpenRate() {
//			return maxOpenRate;
//		}
//
//		public void setBounces(int bounces) {
//			this.bounces = bounces;
//		}
//
//		public void setClicks(int clicks) {
//			this.clicks = clicks;
//		}
//
//		public void setMailingData(Hashtable mailingData) {
//			this.mailingData = mailingData;
//		}
//
//		public void setMaxBounces(int maxBounces) {
//			this.maxBounces = maxBounces;
//		}
//
//		public void setMaxClicks(int maxClicks) {
//			this.maxClicks = maxClicks;
//		}
//
//		public void setMaxOpened(int maxOpened) {
//			this.maxOpened = maxOpened;
//		}
//
//		public void setMaxOptouts(int maxOptouts) {
//			this.maxOptouts = maxOptouts;
//		}
//
//		public void setMaxSubscribers(int maxSubscribers) {
//			this.maxSubscribers = maxSubscribers;
//		}
//
//		public void setOpened(int opened) {
//			this.opened = opened;
//		}
//
//		public void setOptouts(int optouts) {
//			this.optouts = optouts;
//		}
//
//		public void setSubscribers(int subscribers) {
//			this.subscribers = subscribers;
//		}
//
//		public int getOpened() {
//            return opened;
//        }
//
//        public int getOptouts() {
//            return optouts;
//        }
//
//        public int getSubscribers() {
//            return subscribers;
//        }
//        public Hashtable getMailingData() {
//            return mailingData;
//        }
//
//        public int getMaxBounces() {
//            return maxBounces;
//        }
//
//        public int getMaxClicks() {
//            return maxClicks;
//        }
//
//        public int getMaxOpened() {
//            return maxOpened;
//        }
//
//        public int getMaxOptouts() {
//            return maxOptouts;
//        }
//
//        public int getMaxSubscribers() {
//            return maxSubscribers;
//        }
//
//        public void setMaxClickRate(double maxClickRate) {
//            this.maxClickRate=maxClickRate;
//        }
//
//        public void setMaxOpenRate(double maxOpenRate) {
//            this.maxOpenRate=maxOpenRate;
//        }
//    };
    
    /** Holds value of property id. */
    private int id;
    
    /** Holds value of property companyID. */
    private int companyID;
    
    /** Holds value of property netto. */    
    private boolean netto;

    /** Holds value of property shortname. */
    private String shortname;
    
    /** Holds value of property description. */
    private String description;
    
    /** Holds value of property csvfile. */
    private String csvfile="";

    public String getCsvfile() {
        return csvfile;
    }
    
    public void setCsvfile(String csvfile) {
		this.csvfile = csvfile;
	}

	// CONSTRUCTORS:
    public CampaignImpl() {
        id = 0;
        companyID = 0;
    }
    
    // automatically generated
    // get & set methods:

    public int getId() {
        return id;
    }
    
    public int getCompanyID() {
        return companyID;
    }
   
    public String getShortname() {
        return this.shortname;
    }
    
    public String getDescription() {
        return description;
    }

    /** Getter for property netto.
     * @return Value of property netto.
     *
     */
    public boolean isNetto() {
        return this.netto;
    }
    
    /** Setter for property netto.
     * @param netto New value of property netto.
     *
     */
    public void setNetto(boolean netto) {
        this.netto = netto;
    }    
    
    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public void setShortname(String shortname) {
        this.shortname = shortname;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
}
