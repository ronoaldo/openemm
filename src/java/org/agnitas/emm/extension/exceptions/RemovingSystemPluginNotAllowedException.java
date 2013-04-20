package org.agnitas.emm.extension.exceptions;

public class RemovingSystemPluginNotAllowedException extends Exception {
	private final String id;
	
	public RemovingSystemPluginNotAllowedException( String pluginId) {
		super( "Removing of system plugin not allowed: " + pluginId);
		
		this.id = pluginId;
	}
	
	public String getPluginId() {
		return this.id;
	}
}
