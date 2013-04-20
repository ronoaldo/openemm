package org.agnitas.emm.extension.exceptions;

/**
 * Exception indicating errors on invoking a plugin.
 * 
 * @author md
 */
public class PluginInvocationException extends Exception {

	public PluginInvocationException() {
		// Nothing to do here
	}

	public PluginInvocationException(String message) {
		super(message);
	}

	public PluginInvocationException(Throwable cause) {
		super(cause);
	}

	public PluginInvocationException(String message, Throwable cause) {
		super(message, cause);
	}

}
