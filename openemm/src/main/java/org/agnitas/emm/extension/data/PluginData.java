package org.agnitas.emm.extension.data;

/**
 * Stored informations on a plugin.
 * 
 * @author md
 */
public interface PluginData {
	
	/**
	 * Returns the plugin ID
	 * 
	 * @return plugin ID
	 */
	public String getPluginId();
	
	/**
	 * Set the plugin ID
	 * 
	 * @param pluginId plugin ID
	 */
	public void setPluginId( String pluginId);
	
	/**
	 * Returns, if the plugin is to be activated on startup of the extension system.
	 *
	 * <b>Note: This plugin can be activated on startup, when another plugin depends on
	 * this plugin, even if this methods returns false.</b> 
	 * 
	 * @return true, if plugin is to be activated
	 */
	public boolean isActivatedOnStartup();
	
	/**
	 * Set if plugin is to be activated on startup of the extension system.
	 * 
	 * @param activatedOnStartup true, if plugin is to be activated.
	 */
	public void setActivatedOnStartup( boolean activatedOnStartup);
}
