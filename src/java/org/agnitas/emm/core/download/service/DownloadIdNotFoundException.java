package org.agnitas.emm.core.download.service;

/**
 * Exception indication an unknown download ID:
 * 
 * @author md
 */
public class DownloadIdNotFoundException extends Exception {

	/** Serial version UID. */
	private static final long serialVersionUID = 5159045007834924781L;
	
	/** Unknown download ID. */
	private final String id;
	
	/**
	 * Creates a new exception with given unknown download ID.
	 * 
	 * @param id unknown download ID
	 */
	public DownloadIdNotFoundException( String id) {
		super( "Unknown download ID: " + id);
		
		this.id = id;
	}
	
	/**
	 * Returns the download ID.
	 * 
	 * @return unknown download ID
	 */
	public String getDownloadId() {
		return this.id;
	}
}
