package org.agnitas.emm.core.commons.uid.parser.exception;

import org.agnitas.emm.core.commons.uid.ExtensibleUID;

public class DeprecatedUIDVersionException extends Exception {
	private final String uidString;
	private final ExtensibleUID uid;
	
	public DeprecatedUIDVersionException( String uidString, ExtensibleUID uid) {
		super( "deprecated UID version " + uid.getUIDVersion() + " for UID " + uidString);
		
		this.uidString = uidString;
		this.uid = uid;
	}

	public int getUIDVersion() {
		return this.uid.getUIDVersion();
	}
	
	public String getUIDString() {
		return this.uidString;
	}
	
	public ExtensibleUID getUID() {
		return this.uid;
	}
}
