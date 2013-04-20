package org.agnitas.emm.core.commons.uid.builder.impl.exception;

/**
 * Exception indicating error during conversion of UID to its string representation.
 * 
 * @author md
 */
public class UIDStringBuilderException extends Exception {

	/**
	 * Creates a new instance.
	 * 
	 * @param cause Throwable causing this exception
	 */
	public UIDStringBuilderException( Throwable cause) {
		super( cause);
	}

	/**
	 * Creates a new instance.
	 * 
	 * @param message Error message
	 */
	public UIDStringBuilderException( String message) {
		super( message);
	}
}
