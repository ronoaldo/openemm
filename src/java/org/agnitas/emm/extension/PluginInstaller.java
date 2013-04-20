package org.agnitas.emm.extension;

import java.io.IOException;

import org.agnitas.emm.extension.exceptions.DatabaseScriptException;
import org.agnitas.emm.extension.exceptions.MissingPluginManifestException;

/**
 * Interface for component to install plugins.
 * 
 * @author md
 *
 */
public interface PluginInstaller {
	/**
	 * Installs plugin from given ZIP file
	 * 
	 * @param filename name of ZIP file
	 * 
	 * @return ID of plugin
	 * 
	 * @throws IOException on errors install plugin
	 * @throws MissingPluginManifestException on errors with plugin manifest
	 * @throws DatabaseScriptException on errors executing database script
	 */
	public String installPlugin( String filename) throws IOException, MissingPluginManifestException, DatabaseScriptException;

	/**
	 * Uninstalls plugin files.
	 * 
	 * @param pluginId ID of plugin to be removed
	 */
	public void uninstallPlugin(String pluginId);
}
