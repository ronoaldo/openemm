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
package org.agnitas.backend;

import org.agnitas.emm.core.commons.uid.impl.UIDImpl;

/** Create redirect URLs
 */
public class URLMaker {
    /** Reference to configuration */
    private Data data = null;
    private UIDImpl uid = null;
    private boolean proURLstatic = false;
    private boolean unsubURLstatic = false;

    /** Checks an URL if it should be used static
     * @return if the url is static
     */
    private boolean checkForStaticURL (String url) {
        return (! url.endsWith ("?")) && (! url.endsWith ("&"));
    }

    public void setupData (Object nData) {
        data = (Data) nData;
    }

    /** Setup UID creator
     */
    public void setupUIDMaker () throws Exception{
        uid = new UIDImpl (data.company_id, data.mailing_id, data.password);
    }
    public void setURLID (long url) {
        uid.setURLID (url);
    }
    public void setPrefix (String nPrefix) {
        uid.setPrefix (nPrefix);
    }
    public void setCustomerID (long customerID) {
        uid.setCustomerID (customerID);
    }
    public String makeUID () throws Exception {
        return uid.makeUID ();
    }

    /** Constructor
     * @param data reference to configuration
     */
    public URLMaker (Data data) throws Exception {
        setupData (data);
        setupUIDMaker ();
        proURLstatic = checkForStaticURL (data.profileURL);
        unsubURLstatic = checkForStaticURL (data.unsubscribeURL);
    }

    /** Create a URL created out of base and given paramter
     * @param base the base url
     * @param url (optional) url id
     * @return the URL
     */
    public String makeURL (String base, long url) throws Exception {
        setURLID (url);
        return base + "uid=" + makeUID ();
    }

    /** Create a profile URL
     * @return the URL
     */
    public String profileURL () throws Exception {
        if (proURLstatic)
            return data.profileURL;
        return makeURL (data.profileURL, 0);
    }

    /** Create an unsubscribe URL
     * @return the URL
     */
    public String unsubscribeURL () throws Exception {
        if (unsubURLstatic)
            return data.unsubscribeURL;
        return makeURL (data.unsubscribeURL, 0);
    }

    /** Create an auto URL
     * @return the URL
     */
    public String autoURL (long url) throws Exception {
        return makeURL (data.autoURL, url);
    }

    /** Create an onepixellog URL
     * @return the URL
     */
    public String onepixelURL () throws Exception {
        return makeURL (data.onePixelURL, 0);
    }
}
