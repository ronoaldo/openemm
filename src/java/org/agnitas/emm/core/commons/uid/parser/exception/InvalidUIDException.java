package org.agnitas.emm.core.commons.uid.parser.exception;

/**
 * Exception indicating an invalid UID.
 * 
 * @author md
 */
public class InvalidUIDException extends Exception {
	/** The invalid UID string. */
	private final String uidString;

	/**
	 * Creates a new instance.
	 * 
	 * @param uidString invalid UID string
	 */
	public InvalidUIDException(String uidString) {
		super( "Invalid UID: " + uidString);

		this.uidString = uidString;
	}
	
	/**
	 * Creates a new instance with additional description of error.
	 * 
	 * @param description description, why UID string is invalid
	 * @param uidString invalid UID string
	 */
	public InvalidUIDException( String description, String uidString) {
		super( "Invalid UID: " + uidString + " (" + description + ")");
		
		this.uidString = uidString;
	}

	/**
	 * Creates a new instance.
	 * 
	 * @param uidString invalid UID string
	 * @param cause Throwable causing this exception.
	 */
	public InvalidUIDException(String uidString, Throwable cause) {
		super( "Invalid UID: " + uidString, cause);

		this.uidString = uidString;
	}

	/**
	 * Returns the invalid UID string.
	 * 
	 * @return invalid UID string
	 */
	public String getUIDString() {
		return this.uidString;
	}
}
