package org.agnitas.emm.extension.dao;

import org.agnitas.emm.extension.data.PluginData;
import org.agnitas.emm.extension.exceptions.UnknownPluginException;

/**
 * DAO interface to access plugin data.
 * 
 * @author md
 */
public interface PluginDao {
	
	/**
	 * Get plugin data from DB.
	 * 
	 * @param pluginId ID of the plugin
	 *
	 * @return data to given plugin ID
	 * 
	 * @throws UnknownPluginException if there is no data to the given ID
	 */
	public PluginData getPluginData( String pluginId) throws UnknownPluginException;
	
	/**
	 * Saves the plugin data to DB.
	 * 
	 * @param pluginData plugin data to save
	 */
	public void savePluginData( PluginData pluginData);

	/**
	 * Removes the plugin data from DB.
	 * 
	 * @param pluginId ID of plugin for removing data
	 */
	public void removePluginData(String pluginId);
}
