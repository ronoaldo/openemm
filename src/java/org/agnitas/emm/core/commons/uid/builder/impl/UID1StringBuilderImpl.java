package org.agnitas.emm.core.commons.uid.builder.impl;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.agnitas.beans.Company;
import org.agnitas.emm.core.commons.daocache.CompanyDaoCache;
import org.agnitas.emm.core.commons.uid.ExtensibleUID;
import org.agnitas.emm.core.commons.uid.builder.ExtensibleUIDStringBuilder;
import org.agnitas.emm.core.commons.uid.builder.impl.exception.UIDStringBuilderException;
import org.apache.log4j.Logger;

/**
 * Implementation of ExtensibleUIDStringBuilder for OpenEMMs UIDs.
 * 
 * @author md
 */
public class UID1StringBuilderImpl implements ExtensibleUIDStringBuilder {

	/** Logger. */
	private static final transient Logger logger = Logger.getLogger( ExtensibleUIDStringBuilder.class);

	/** Character used to separate the parts of the UID. */
	public static final char SEPARATOR = '.';

	// -------------------------------------------------------------------------------- Dependecy Injection
	/** Cache for Company objects. */
	private CompanyDaoCache companyDaoCache;

	/**
	 * Set cache for Company objects.
	 * 
	 * @param companyDaoCache cache for Company objects
	 */
	public void setCompanyDaoCache( CompanyDaoCache companyDaoCache) {
		this.companyDaoCache = companyDaoCache;
	}

	// -------------------------------------------------------------------------------- Business Logic
	@Override
    public String buildUIDString( ExtensibleUID extensibleUID) throws UIDStringBuilderException {
    	try {
    	    String password = getPassword( extensibleUID);
        	String base = createUIDBase( extensibleUID);
        	String signature = createUIDSignature( base + '.' + (password == null ? "" : password));
        
        	return base + '.' + signature;
    	} catch( Exception e) {
    		logger.error( "buildUIDString", e);
    		
    		throw new UIDStringBuilderException( e);
    	}
    }
	
	@Override
	public int getNewestHandledUIDVersion() {
		return ExtensibleUID.VERSION_UID;
	}

	/**
	 * Returns the password, that is used to encrypt the UID.
	 * The password is determined by the Company referenced by the UID.
	 * 
	 * @param extensibleUID UID
	 * 
	 * @return password for encryption
	 */
	private String getPassword( ExtensibleUID extensibleUID) {
		Company company = this.companyDaoCache.getItem( extensibleUID.getCompanyID());

		if( company == null) {
			logger.warn( "Found no company information for ID " + extensibleUID.getCompanyID());
			
			return null;
		} else {
			return company.getSecret();
		}
	}

	/**
	 * Creates the basic UID string. This string includes company ID,
	 * customer ID, mailing ID and URL ID but not the signature of the UID.
	 * 
	 * @param extensibleUID UID
	 * 
	 * @return basic UID string
	 */
	private String createUIDBase( ExtensibleUID extensibleUID) {
		StringBuffer buffer = new StringBuffer();

		if( extensibleUID.getPrefix() != null) {
			buffer.append( extensibleUID.getPrefix());
			buffer.append( SEPARATOR);
		}

		buffer.append( encodeInteger( extensibleUID.getCompanyID()));
		buffer.append( SEPARATOR);
		buffer.append( encodeInteger( extensibleUID.getMailingID()));
		buffer.append( SEPARATOR);
		buffer.append( encodeInteger( extensibleUID.getCustomerID()));
		buffer.append( SEPARATOR);
		buffer.append( encodeInteger( extensibleUID.getUrlID()));

		return buffer.toString();
	}
	
	/**
	 * Computes the signature for the given string.
	 * 
	 * @param s string to compute signature
	 * 
	 * @return signature
	 * 
	 * @throws NoSuchAlgorithmException when SHA-1 is not supported by the used Java runtime environment
	 * @throws UnsupportedEncodingException when character set US-ASCII is not supported by the used Java runtime environment
	 */
	private String createUIDSignature( String s) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest hash = MessageDigest.getInstance( "sha-1");
		byte[] result;
		StringBuffer sig;
		long ch;

		hash.reset();
		hash.update( s.getBytes( "US-ASCII"));

		result = hash.digest();
		sig = new StringBuffer();
		for( int n = 0; n < result.length; n += 2) {
			ch = (((new Byte( result[n])).longValue() & 0xff) >> 2) % 36;
			sig.append( encodeLong( ch));
		}

		return sig.toString();
	}

	/**
	 * Base36-encoding of long value.
	 * 
	 * @param value long value
	 * 
	 * @return Base36-encoded value
	 */
	private String encodeLong( long value) {
		return Long.toString( value, 36);
	}

	/**
	 * Base36-encoding of int value.
	 * 
	 * @param value int value
	 * 
	 * @return Base36-encoded int value
	 */
	private String encodeInteger( int value) {
		return Integer.toString( value, 36);
	}
}
