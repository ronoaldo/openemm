package org.agnitas.emm.core.commons.uid.parser.impl;

import org.agnitas.beans.Company;
import org.agnitas.emm.core.commons.daocache.CompanyDaoCache;
import org.agnitas.emm.core.commons.uid.ExtensibleUID;
import org.agnitas.emm.core.commons.uid.builder.ExtensibleUIDStringBuilder;
import org.agnitas.emm.core.commons.uid.builder.impl.exception.RequiredInformationMissingException;
import org.agnitas.emm.core.commons.uid.builder.impl.exception.UIDStringBuilderException;
import org.agnitas.emm.core.commons.uid.impl.ExtensibleUIDImpl;
import org.agnitas.emm.core.commons.uid.parser.ExtensibleUIDParser;
import org.agnitas.emm.core.commons.uid.parser.exception.DeprecatedUIDVersionException;
import org.agnitas.emm.core.commons.uid.parser.exception.InvalidUIDException;
import org.agnitas.emm.core.commons.uid.parser.exception.UIDParseException;

/**
 * Implementation of the ExtensibleUIDParser interface for OpenEMMs UIDs.
 * 
 * @author md
 */
public class UID1ExtensibleUIDParserImpl implements ExtensibleUIDParser {

	// --------------------------------------------- Dependency Injection
	/** 
	 * ExtensibleUIDStringBuilder used to validate the signature.
	 * Ensure, that the implementation of the ExtensibleUIDStringBuilder and this
	 * implementation of the parser fit together, otherwise validating a valid
	 * signature will fail! 
	 */
	private ExtensibleUIDStringBuilder stringBuilder;

	/** Cache for Company objects. */
	private CompanyDaoCache companyDaoCache;
	
	/**
	 * Set the ExtensibleUIDStringBuilder.
	 * 
	 * @param stringBuilder ExtensibleUIDStringBuilder
	 */
	public void setStringBuilder( ExtensibleUIDStringBuilder stringBuilder) {
		this.stringBuilder = stringBuilder;
	}

	/**
	 * Set CompanyDaoCache.
	 * 
	 * @param cache CompanyDaoCache
	 */
	public void setCompanyDaoCache( CompanyDaoCache cache) {
		this.companyDaoCache = cache;
	}
	
	// --------------------------------------------- Business Code
	@Override
	public ExtensibleUID parse( String uidString) throws UIDParseException, InvalidUIDException, DeprecatedUIDVersionException {
		if( uidString == null)
			return null;
		
		String[] parts = splitUIDString( uidString);

		// Check number of fields
		if( parts.length != 5 && parts.length != 6)
			throw new InvalidUIDException( uidString);

		ExtensibleUIDImpl uid = new ExtensibleUIDImpl();
		int start = parts.length - 5;

		try {
			// Extract values of splitted UID
			uid.setPrefix( start == 0 ? null : parts[0]);
			uid.setCompanyID( decodeInteger( parts[start]));
			uid.setMailingID( decodeInteger( parts[start + 1]));
			uid.setCustomerID( decodeInteger( parts[start + 2]));
			uid.setUrlID( decodeInteger( parts[start + 3]));
			
			uid.setUIDVersion( 0);

			// Compute expected signature using the extracted values from above
		} catch( NumberFormatException e) {
			throw new InvalidUIDException( uidString, e);
		} catch( RuntimeException e) {
			throw new UIDParseException( "Error parsing UID", uidString, e);
		}
			
		try {
    		String signature = parts[start + 4];
    		String expectedSignature = getExpectedSignature( uid);
    		
    		// Compute extracted signature and expected signature
    		if( !signature.equals( expectedSignature))
    			throw new InvalidUIDException( uidString);
		} catch( UIDStringBuilderException e) {
			throw new UIDParseException( "Error validating UID", uidString, e);
		} catch( RequiredInformationMissingException e) {
			throw new UIDParseException( "Error validating UID", uidString, e);
		}
		
		checkUIDVersion( uidString, uid);
		
		return uid;
	}
	
	/**
	 * Determinte the expected signature for the given ExtensibleUID.
	 * 
	 * @param uid UID
	 * 
	 * @return expected signature
	 * 
	 * @throws UIDStringBuilderException on errors computing the expected signature
	 */
	private String getExpectedSignature( ExtensibleUID uid) throws UIDStringBuilderException, RequiredInformationMissingException {
		String uidString = this.stringBuilder.buildUIDString( uid);
		
		String[] parts = splitUIDString( uidString);
		
		return parts[parts.length - 1];
	}

	/**
	 * Splits the UID string.
	 * 
	 * @param str UID string
	 * 
	 * @return String array containing the elements of the UID string
	 */
	private String[] splitUIDString( String str) {
		return str.split( "\\.");
	}
	
	/**
	 * Base36-decodes an int value.
	 * 
	 * @param str String containing Base36-encoded int value
	 * 
	 * @return decoded int value
	 */
	private int decodeInteger( String str) {
		return Integer.parseInt( str, 36);
	}
	
	protected int getHandledVersion() {
		return 0;
	}
	
	private void checkUIDVersion( String uidString, ExtensibleUID uid) throws DeprecatedUIDVersionException {
		Company company = this.companyDaoCache.getItem( uid.getCompanyID());
		
		if( !isVersionSupported( company.getMinimumSupportedUIDVersion(), getHandledVersion()))
			throw new DeprecatedUIDVersionException( uidString, uid);
	}
	
	private boolean isVersionSupported( Number minimumSupportedVersion, int handledVersion) {
		if( minimumSupportedVersion == null)
			return true;
		
		return minimumSupportedVersion.intValue() <= handledVersion;
	}

}
