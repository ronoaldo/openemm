package org.agnitas.emm.extension.exceptions;

/**
 * Exception to signal an unknown plugin name.
 * 
 * @author md
 *
 */
public class UnknownPluginException extends Exception {
	
	/** Name of unknown plugin. */
	private final String pluginName;
	
	/**
	 * Create a new UnknownPluginException.
	 * 
	 * @param pluginName name of unknown plugin
	 */
	public UnknownPluginException( String pluginName) {
		super( "unknown plugin: " + pluginName);
		
		this.pluginName = pluginName;
	}
	
	/**
	 * Returns the plugin name.
	 * 
	 * @return name of unknown plugin
	 */
	public String getPluginName() {
		return this.pluginName;
	}
}
