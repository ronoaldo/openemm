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

package org.agnitas.stat.impl;

import org.agnitas.stat.URLStatEntry;
import org.agnitas.util.AgnUtils;

public class URLStatEntryImpl implements URLStatEntry {
    
    private static final long serialVersionUID = 533208075371264606L;
	protected int urlID;
    protected String url;
    protected String shortname;
    protected int clicks;
    protected int clicksNetto;
    
    public URLStatEntryImpl() {
        this.urlID = 0;
    }
    
    public int getUrlID() {
        return this.urlID;
    }
    public String getShortname() {
        return this.shortname;
    }
    public String getUrl() {
        return this.url;
    }
    public int getClicks() {
        return this.clicks;
    }
    
    public void setUrlID(int urlID) {
        this.urlID = urlID;
    }
    public void setShortname(String shortname) {
        this.shortname = shortname;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public void setClicks(int clicks) {
        this.clicks = clicks;
    }
    
    public int getClicksNetto() {
        return this.clicksNetto;
    }
    
    public void setClicksNetto(int clicksNetto) {
        this.clicksNetto = clicksNetto;
    }
    
    public int compareTo(Object obj) {
        try {
            if(this.clicksNetto<((URLStatEntry)obj).getClicksNetto()) {
                return -1;
            }
            if(this.clicksNetto==((URLStatEntry)obj).getClicksNetto()) {
                return 0;
            }
            if(this.clicksNetto>((URLStatEntry)obj).getClicksNetto()) {
                return 1;
            }
        } catch (Exception e) {
            AgnUtils.logger().error(e.getMessage());
        }
        return -1;
    }
    
    public boolean equals(Object obj) {
    	if(!(obj instanceof URLStatEntry))
    		return false;
    	
    	URLStatEntry u = (URLStatEntry) obj;
    	
    	return this.urlID == u.getUrlID();
    }
}