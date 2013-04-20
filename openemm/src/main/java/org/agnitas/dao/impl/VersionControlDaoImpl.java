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
package org.agnitas.dao.impl;

import org.agnitas.beans.VersionObject;
import org.agnitas.beans.impl.VersionObjectImpl;
import org.agnitas.dao.VersionControlDao;
import org.agnitas.util.AgnUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

public class VersionControlDaoImpl implements VersionControlDao {
	
	private static final transient Logger logger = Logger.getLogger(VersionControlDaoImpl.class);
	
	private static final int CONNECTION_TIMEOUT = 5000;
	private static final String URL = AgnUtils.getDefaultValue( "system.updateserver" ) + "/version/" + AgnUtils.getCurrentVersion() + "/current_version.html";
	private static final String VERSION_KEY = "currentVersion";
	private static final long MAX_AGE = 60 * 60 * 1000L; // one hour cache time
	private static VersionObject versionObject = null;
	private long lastRefresh = 0L;

	/* (non-Javadoc)
	 * @see org.agnitas.dao.VersionControlDao#getServerVersion()
	 */
    @Override
	public VersionObject getServerVersion(String currentVersion, String referrer ) {
		checkRefresh( currentVersion, referrer );
		return versionObject;
	}

	private void checkRefresh(String currentVersion, String referrer) {
		if ( versionObject == null || System.currentTimeMillis() - lastRefresh > MAX_AGE ) {
			String serverVersion = fetchServerVersion(currentVersion, referrer);
			versionObject = new VersionObjectImpl( currentVersion, serverVersion );
			lastRefresh  = System.currentTimeMillis();
		}
	}

	private String fetchServerVersion(String currentVersion, String referrer) {
		HttpClient client = new HttpClient();
		client.setConnectionTimeout(CONNECTION_TIMEOUT);
		HttpMethod method = new GetMethod(URL);
		method.setRequestHeader( "referer", referrer );
		NameValuePair[] queryParams = new NameValuePair[1];
		queryParams[0] = new NameValuePair( VERSION_KEY, currentVersion );
		method.setQueryString( queryParams );
        method.setFollowRedirects(true);
        String responseBody = null;
        
        try{
            client.executeMethod(method);
            responseBody = method.getResponseBodyAsString();
        } catch (Exception he) {
        	logger.error( "HTTP error connecting to '" + URL + "'", he);
        }

        //clean up the connection resources
        method.releaseConnection();
		return responseBody;
	}
}
