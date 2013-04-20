package org.agnitas.emm.extension.exceptions;

/**
 * Exception indicating errors during plugin instantiation.
 * 
 * @author md
 */
public class PluginInstantiationException extends Exception {

	public PluginInstantiationException() {
		// Nothing to do here
	}

	public PluginInstantiationException(String message) {
		super(message);
	}

	public PluginInstantiationException(Throwable cause) {
		super(cause);
	}

	public PluginInstantiationException(String message, Throwable cause) {
		super(message, cause);
	}

}
