package org.agnitas.emm.core.commons.uid;

@Deprecated
public interface UIDParser {

	public abstract UID parseUID(String uidString) throws DeprecatedUIDVersionException;

}