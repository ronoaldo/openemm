package org.agnitas.emm.extension.exceptions;

public class DatabaseScriptException extends Exception {

	public DatabaseScriptException() {
		super();
	}

	public DatabaseScriptException(String message, Throwable cause) {
		super(message, cause);
	}

	public DatabaseScriptException(String message) {
		super(message);
	}

	public DatabaseScriptException(Throwable cause) {
		super(cause);
	}

}
