package org.agnitas.exceptions;

public class FormNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3880039723225788494L;

	public FormNotFoundException() {
		super();
	}

	public FormNotFoundException(String message) {
		super(message);
	}

	public FormNotFoundException(Throwable cause) {
		super(cause);
	}

	public FormNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
