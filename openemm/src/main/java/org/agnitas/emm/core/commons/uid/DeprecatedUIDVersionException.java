package org.agnitas.emm.core.commons.uid;

@Deprecated
public class DeprecatedUIDVersionException extends Exception {
	private final int uidVersion;
	private final String uidString;
	private final UID uid;
	
	public DeprecatedUIDVersionException( int uidVersion, String uidString, UID uid) {
		super( "deprecated UID version " + uidVersion + " for UID " + uidString);
		
		this.uidVersion = uidVersion;
		this.uidString = uidString;
		this.uid = uid;
	}

	public int getUIDVersion() {
		return this.uidVersion;
	}
	
	public String getUIDString() {
		return this.uidString;
	}
	
	public UID getUID() {
		return this.uid;
	}
}
