package org.agnitas.emm.core.commons.uid.impl;

import org.agnitas.beans.Company;
import org.agnitas.dao.CompanyDao;
import org.agnitas.emm.core.commons.uid.DeprecatedUIDVersionException;
import org.agnitas.emm.core.commons.uid.UID;
import org.agnitas.emm.core.commons.uid.UIDParser;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.TimeoutLRUMap;

@Deprecated
public class UIDParserImpl implements UIDParser {

	/**
	 * Version of UID handled by this UID parser.
	 */
	private final int HANDLED_UID_VERSION = 0;
	
	private TimeoutLRUMap companyCache;
	private CompanyDao companyDao;
	
	public void setCompanyCache( TimeoutLRUMap companyCache) {
		this.companyCache = companyCache;
	}
	
	public void setCompanyDao( CompanyDao companyDao) {
		this.companyDao = (CompanyDao) companyDao;
	}
	
	protected Company getCompany( int companyID) {
		if( companyCache == null)
			return companyDao.getCompany(companyID);
		else {
			Company company = (Company)companyCache.get(companyID);
	        
			if(company == null) {
	            company = companyDao.getCompany(companyID);
	            if(company != null) {
	                companyCache.put(companyID, company);
	            }
	        }
			
	    	return company;
		}
	}
	
	@Override
	public UID parseUID( String uidString) throws DeprecatedUIDVersionException {
		UID uid = null;
		
		if ((uidString) != null && (uidString.length() > 0)) {
			try {
				uid = new UIDImpl(); // Do not move that application context. Implementation of UID is directly associated with implementation of UIDParser!!!
				
				uid.parseUID(uidString);

				if( uid.getCompanyID() == 0)
					return null;
				
				Company company = getCompany((int)uid.getCompanyID());
               	if(company == null)
               		return null;
               	
               	// Check if UID version is supported
               	if( !isVersionSupported( company.getMinimumSupportedUIDVersion(), HANDLED_UID_VERSION))
               		throw new DeprecatedUIDVersionException(HANDLED_UID_VERSION, uidString, uid);
				
               	// Check, if UID is valid
               	/*
                if(uid.validateUID(company.getSecret()) == false) {
                    AgnUtils.logger().warn("uid invalid: " + uidString);
                    return null;
                }
                */
			} catch( DeprecatedUIDVersionException e) {  // catch required, because there is a block catching all Exception instances
				throw e; // Don't handle exception, throw new next caller.
			} catch (Exception e) {
				uid = null;
				AgnUtils.logger().warn("Exception: "+e);
				AgnUtils.logger().debug(AgnUtils.getStackTrace(e));
			}
		}
		
		return uid;
	}
	
	protected boolean isVersionSupported( Number minimumSupportedVersion, int handledVersion) {
		if( minimumSupportedVersion == null)
			return true;
		
		return minimumSupportedVersion.intValue() <= handledVersion;
	}
}
