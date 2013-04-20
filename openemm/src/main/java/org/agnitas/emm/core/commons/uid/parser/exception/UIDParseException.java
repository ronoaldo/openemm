package org.agnitas.emm.core.commons.uid.parser.exception;

/**
 * Exception indicating an error during parsing.
 * This exception does not indicate errors on the UID string itself.
 * 
 * @author md
 *
 */
public class UIDParseException extends Exception {
	
	/** UID string parsed, when the exception was thrown. */
	private final String uidString;

	/**
	 * Creates a new instance.
	 * 
	 * @param message error message
	 * @param uidString UID string parsed, when the exception was thrown.
	 */
	public UIDParseException(String message, String uidString) {
		super( message + " (UID: " + uidString + ")");

		this.uidString = uidString;
	}

	/**
	 * Creates a new instance.
	 * 
	 * @param message error message
	 * @param uidString UID string parsed, when the exception was thrown.
	 * @param cause Throwable causing this exception
	 */
	public UIDParseException(String message, String uidString, Throwable cause) {
		super( message, cause);

		this.uidString = uidString;
	}

	/**
	 * Returns the UID string, that was parsed, when this exception was thrown.
	 * 
	 * @return UID string
	 */
	public String getUIDString() {
		return this.uidString;
	}
}
