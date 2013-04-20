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

import org.agnitas.beans.VersionObject;
import org.apache.commons.lang.StringUtils;

public class VersionObjectImpl implements VersionObject {

	private static final char TOKEN_SECURITY = 's';
	private static final char TOKEN_UPDATE = 'u';
	private final String serverVersion;
	private boolean latestVersion = true;
	private boolean securityExploit = false;
	private boolean update = false;

	/** creates new VersionObjectImpl **/
	public VersionObjectImpl(String currentVersion, String serverVersion) {
		if ( serverVersion != null ) {
			this.latestVersion = StringUtils.equalsIgnoreCase( currentVersion, serverVersion.trim() );
				
			if ( serverVersion.trim().endsWith( String.valueOf( TOKEN_SECURITY ) ) ) {
				this.serverVersion = serverVersion.substring( 0, serverVersion.lastIndexOf( TOKEN_SECURITY ) );
				this.latestVersion = StringUtils.equalsIgnoreCase( currentVersion, this.serverVersion );
				if ( !latestVersion ) {
					securityExploit = true;
					update = false;
				}
			} else if ( serverVersion.trim().endsWith( String.valueOf( TOKEN_UPDATE ) ) ) {
				this.serverVersion = serverVersion.substring( 0, serverVersion.lastIndexOf( TOKEN_UPDATE ) );
				this.latestVersion = StringUtils.equalsIgnoreCase( currentVersion, this.serverVersion );
				if ( !latestVersion ) {
					securityExploit = false;
					update = true;
				}
			} else {
				securityExploit = false;
				update = false;
				this.serverVersion = serverVersion;
			}
		} else {
			securityExploit = false;
			update = false;
			this.serverVersion = null;
		}
		
	}

	/* (non-Javadoc)
	 * @see org.agnitas.bean.VersionObject#isLatestVersion()
	 */
	public boolean isLatestVersion() {
		return latestVersion;
	}
	
	/* (non-Javadoc)
	 * @see org.agnitas.bean.VersionObject#isSecurityExploit()
	 */
	public boolean isSecurityExploit() {
		return securityExploit;
	}
	
	/* (non-Javadoc)
	 * @see org.agnitas.bean.VersionObject#isUpdate()
	 */
	public boolean isUpdate() {
		return update;
	}

	/* (non-Javadoc)
	 * @see org.agnitas.bean.VersionObject#getServerVersion()
	 */
	public String getServerVersion() {
		return serverVersion;
	}

}
