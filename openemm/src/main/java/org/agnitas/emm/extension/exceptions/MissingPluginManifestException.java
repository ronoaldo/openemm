package org.agnitas.emm.extension.exceptions;

public class MissingPluginManifestException extends Exception {

	public MissingPluginManifestException( String message) {
		super( message);
	}
	
	public MissingPluginManifestException( String message, Throwable cause) {
		super( message, cause);
	}
}
