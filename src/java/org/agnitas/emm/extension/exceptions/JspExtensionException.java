package org.agnitas.emm.extension.exceptions;

/**
 * Exception indicating errors during executing of JSP extension.
 * 
 * @author md
 */
public class JspExtensionException extends Exception {

	public JspExtensionException() {
		// Nothing to do here
	}

	public JspExtensionException(String message) {
		super(message);
	}

	public JspExtensionException(Throwable cause) {
		super(cause);
	}

	public JspExtensionException(String message, Throwable cause) {
		super(message, cause);
	}

}
