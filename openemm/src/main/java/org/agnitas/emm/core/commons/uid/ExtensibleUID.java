package org.agnitas.emm.core.commons.uid;

/**
 * Common interface for the UID.
 * 
 * @author md
 */
public interface ExtensibleUID {
	
	/** Version of old UID. */
	public static final int VERSION_UID = 0;
	
	/** Version of improved XUID. */
	public static final int VERSION_XUID = 1;
	
	/**
	 * Returns the prefix.
	 * 
	 * @return prefix
	 */
    public String getPrefix();

    /**
     * Set prefix.
     * 
     * @param prefix prefix
     */
    public void setPrefix( String prefix);

    /**
     * Returns the company ID.
     * 
     * @return company ID
     */
    public int getCompanyID();

    /**
     * Set company ID.
     * 
     * @param companyID company ID
     */
    public void setCompanyID( int companyID);

    /**
     * Returns the customer ID.
     * 
     * @return customer ID
     */
    public int getCustomerID();

    /**
     * Set customer ID
     * @param customerID customer ID
     */
    public void setCustomerID( int customerID);

    /**
     * Returns the mailing ID.
     * 
     * @return mailing ID
     */
    public int getMailingID();

    /**
     * Set mailing ID.
     * 
     * @param mailingID mailing ID
     */
    public void setMailingID( int mailingID);

    /**
     * Returns the URL ID.
     * 
     * @return URL ID
     */
    public int getUrlID();

    /**
     * Set URL ID.
     * 
     * @param urlID URL ID
     */
    public void setUrlID( int urlID);
    
    /**
     * Returns the version of the parsed UID. This property is only set when 
     * a UID string is parsed. In any other case, this property return -1.
     * 
     * @return version of UID or -1
     */
    public int getUIDVersion();
}
