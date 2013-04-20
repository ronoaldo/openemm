package org.agnitas.emm.core.commons.uid.impl;

import org.agnitas.emm.core.commons.uid.ExtensibleUID;

/**
 * Bean containing all informations used by ExtensibleUIDStringBuilder or
 * ExtensibleUIDParser.
 * 
 * @author md
 */
public class ExtensibleUIDImpl implements ExtensibleUID {

	/** Prefix for UID. */
    private String prefix;
    
    /** Company ID. */
    private int companyID;
    
    /** Customer ID. */
    private int customerID;
    
    /** Mailing ID. */
    private int mailingID;
    
    /** URL ID. */
    private int urlID;
    
    /** Version of UID. */
    private int version;

    /**
     * Create and initialize new UID instance.
     */
    public ExtensibleUIDImpl() {
    	version = -1;
    }
    
    @Override
    public String getPrefix() {
    	return this.prefix;
    }

    @Override
    public void setPrefix( String prefix) {
    	this.prefix = prefix;
    }

    @Override
    public int getCompanyID() {
    	return this.companyID;
    }

    @Override
    public void setCompanyID( int companyID) {
    	this.companyID = companyID;
    }

    @Override
    public int getCustomerID() {
    	return this.customerID;
    }

    @Override
    public void setCustomerID( int customerID) {
    	this.customerID = customerID;
    }

    @Override
    public int getMailingID() {
    	return this.mailingID;
    }

    @Override
    public void setMailingID( int mailingID) {
    	this.mailingID = mailingID;
    }

    @Override
    public int getUrlID() {
    	return this.urlID;
    }

    @Override
    public void setUrlID( int urlID) {
    	this.urlID = urlID;
    }

    /**
     * Set the version of the UID. This method is only called by the UID parser.
     * 
     * @param version version of UID
     */
    public void setUIDVersion( int version) {
    	this.version = version;
    }
    
    @Override
    public int getUIDVersion() {
    	return this.version;
    }
}
