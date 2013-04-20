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

package org.agnitas.stat;

import java.io.Serializable;

/**
 *
 * @author mhe
 */
public interface URLStatEntry extends Serializable, Comparable {
    /**
     * Getter for property clicks.
     *
     * @return Value of property clicks.
     */
    int getClicks();

    /**
     * Getter for property clicksNetto.
     *
     * @return Value of property clicksNetto.
     */
    int getClicksNetto();

    /**
     * Getter for property shortname.
     *
     * @return Value of property shortname.
     */
    String getShortname();

    /**
     * Getter for property url.
     *
     * @return Value of property url.
     */
    String getUrl();

    /**
     * Getter for property urlID.
     *
     * @return Value of property urlID.
     */
    int getUrlID();

     /**
     * Setter for property clicks.
     *
     * @param clicks New value of property clicks.
     */
    void setClicks(int clicks);

    /**
     * Setter for property clicksNetto.
     *
     * @param clicksNetto New value of property clicksNetto.
     */
    void setClicksNetto(int clicksNetto);

    /**
     * Setter for property shortname.
     *
     * @param shortname New value of property shortname.
     */
    void setShortname(String shortname);

    /**
     * Setter for property url.
     *
     * @param url New value of property url.
     */
    void setUrl(String url);

    /**
     * Setter for property urlID.
     *
     * @param urlID New value of property urlID.
     */
    void setUrlID(int urlID);
    
}
