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

package org.agnitas.util;

import java.util.Calendar;
import java.util.TimeZone;

public class EmmCalendar extends java.util.GregorianCalendar {
    
    private static final long serialVersionUID = 7579558923002723690L;

	public EmmCalendar(TimeZone zone) {
        this.setTimeZone(zone);
    }
    
    /**
     * changes the actual Time by changing the TimeZone
     */
    public void changeTimeWithZone(TimeZone target_zone){
        
        int old_offset = this.get(Calendar.ZONE_OFFSET);
        int old_daylight_offset = this.get(Calendar.DST_OFFSET);
        this.add(Calendar.MILLISECOND, (-1 * (old_offset + old_daylight_offset)) );
        
        this.setTimeZone(target_zone);
        
        int new_offset = this.get(Calendar.ZONE_OFFSET);
        int new_daylight_offset = this.get(Calendar.DST_OFFSET);
        this.add(Calendar.MILLISECOND, (new_offset + new_daylight_offset));
    }
    
    /**
     * returns TimeZoneOffset in milliseconds
     * Calendar has to be set before to originating TimeZone
     */
    public int getTimeZoneOffset(TimeZone target_zone) {
        // this.setTimeZone(TimeZone.getDefault()); // set to default for offset to gmt
        
        int old_offset = this.get(Calendar.ZONE_OFFSET) + this.get(Calendar.DST_OFFSET);
        
        this.setTimeZone(target_zone);
        
        int new_offset = this.get(Calendar.ZONE_OFFSET) + this.get(Calendar.DST_OFFSET);
        
        return (-1*old_offset)+new_offset;
    }
    
    /**
     * returns TimeZoneOffset in hour-format (+1 or -2.5)
     */
    public double getTimeZoneOffsetHours(TimeZone target_zone) {
        int millis=getTimeZoneOffset(target_zone);
        
        return millis/1000.0/3600;
    }
    
    /**
     * returns TimeZoneOffset in hour-format (+1 or -2.5)
     */
    public double getTimeZoneOffsetHours(TimeZone org_zone, TimeZone target_zone) {
        this.setTimeZone(org_zone);
        int millis=getTimeZoneOffset(target_zone);
        
        return millis/1000.0/3600;
    }
}

