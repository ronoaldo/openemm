package org.agnitas.emm.extension.exceptions;

/**
 * Exception to signal errors during plugin execution.
 * 
 * @author md
 *
 */
public class PluginException extends Exception {

	public PluginException() {
		super();
	}

	public PluginException(String message) {
		super(message);
	}

	public PluginException(Throwable cause) {
		super(cause);
	}

	public PluginException(String message, Throwable cause) {
		super(message, cause);
	}

}
