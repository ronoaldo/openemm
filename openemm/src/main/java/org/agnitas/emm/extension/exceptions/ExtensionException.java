package org.agnitas.emm.extension.exceptions;

/**
 * Exception to signal errors during plugin execution.
 * 
 * @author md
 *
 */
public class ExtensionException extends Exception {

	public ExtensionException() {
		super();
	}

	public ExtensionException(String message) {
		super(message);
	}

	public ExtensionException(Throwable cause) {
		super(cause);
	}

	public ExtensionException(String message, Throwable cause) {
		super(message, cause);
	}

}
