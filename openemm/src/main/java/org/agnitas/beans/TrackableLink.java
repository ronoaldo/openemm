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

import java.io.Serializable;
import java.util.Map;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author mhe
 */
public interface TrackableLink extends Serializable {
    
    public static final int TRACKABLE_NONE = 0;
    public static final int TRACKABLE_ONLY_HTML = 2;
    public static final int TRACKABLE_ONLY_TEXT = 1;
    public static final int TRACKABLE_TEXT_HTML = 3;

    public static final int DEEPTRACKING_NONE = 0;
    public static final int DEEPTRACKING_ONLY_COOKIE = 1;
    public static final int DEEPTRACKING_ONLY_URL = 2;
    public static final int DEEPTRACKING_BOTH = 3;

    /**
     * Getter for property actionID.
     * 
     * @return Value of property actionID.
     */
    int getActionID();

    /**
     * Getter for property companyID.
     * 
     * @return Value of property companyID.
     */
    int getCompanyID();

    /**
     * Getter for property fullUrl.
     * 
     * @return Value of property fullUrl.
     */
    String getFullUrl();

    /**
     * Getter for property deepTrackingUrl.
     * 
     * @return Value of property deepTrackingUrl.
     */
    String getDeepTrackingUrl();

    /**
     * Getter for property mailingID.
     * 
     * @return Value of property mailingID.
     */
    int getMailingID();

    /**
     * Getter for property shortname.
     * 
     * @return Value of property shortname.
     */
    String getShortname();

    /**
     * Getter for property urlID.
     * 
     * @return Value of property urlID.
     */
    int getId();

    /**
     * Getter for property usage.
     * 
     * @return Value of property usage.
     */
    int getUsage();

    /**
     * Performes the action behind the clicked link.
     */
    boolean performLinkAction(Map<String, Object> params, int customerID, ApplicationContext con);

    /**
     * Personalizes the person who clicked on the link.
     */
    String personalizeLink(int customerID, String orgUID, ApplicationContext con);

    boolean addDeepTrackingParameters(ApplicationContext con);
     /**
     * Setter for property actionID.
     * 
     * @param id New value of property actionID.
     */
    void setActionID(int id);

     /**
     * Setter for property companysID.
     * 
     * @param id New value of property companyID.
     */
    void setCompanyID(int id);

     /**
     * Setter for property fullUrl.
     * 
     * @param url New value of property fullUrl.
     */
    void setFullUrl(String url);

     /**
     * Setter for property mailingID.
     * 
     * @param id New value of property mailingID.
     */
    void setMailingID(int id);

    /**
     * Setter for property shortname.
     * 
     * @param shortname New value of property shortname.
     */
    void setShortname(String shortname);

    void setId(int id);

    /**
     * Setter for property usage.
     * 
     * @param usage New value of property usage.
     */
    void setUsage(int usage);

    /**
     * Getter for property relevance.
     *
     * @return Value of property relevance.
     */
    public String getDeepTrackingUID();

    /**
     * Getter for property relevance.
     *
     * @return Value of property relevance.
     */
    public String getDeepTrackingSession();

    /**
     * Getter for property relevance.
     *
     * @return Value of property relevance.
     */
    public int getDeepTracking();

    /**
     * Setter for property relevance.
     *
     * @param relevance New value of property relevance.
     */
    public void setDeepTracking(int deepTracking);
    
    /**
     * Getter for property relevance.
     *
     * @return Value of property relevance.
     */
    public int getRelevance();

    /**
     * Setter for property relevance.
     *
     * @param relevance New value of property relevance.
     */
    public void setRelevance(int relevance);

    public String encodeTagStringLinkTracking(ApplicationContext con, int custID);
    public String encodeTagStringDeepTracking(ApplicationContext con);

	public void setAdminLink(boolean adminLink);
	public boolean isAdminLink();

}
