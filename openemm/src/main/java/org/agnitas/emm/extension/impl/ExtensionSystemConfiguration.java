package org.agnitas.emm.extension.impl;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * Configuration of the extension system.
 * 
 * Utility class to hold configuration values and derived values.
 * 
 * @author md
 *
 */
public class ExtensionSystemConfiguration {
	
	private static final transient Logger logger = Logger.getLogger( ExtensionSystemConfiguration.class);
	
	private final String systemPluginBaseDirectory;
	private final String pluginBaseDirectory;
	private final String jspBaseDirectory;
	private final String databaseName;
	
	public ExtensionSystemConfiguration( String systemPluginBaseDirectory, String pluginBaseDirectory, String jspBaseDirectory, String databaseName) {
		this.systemPluginBaseDirectory = systemPluginBaseDirectory;
		this.pluginBaseDirectory = pluginBaseDirectory;
		this.jspBaseDirectory = jspBaseDirectory;
		this.databaseName = databaseName;
		
		if( logger.isDebugEnabled()) {
			logger.debug( "base directory for system plugins: " + this.systemPluginBaseDirectory);
			logger.debug( "base directory for additional plugins: " + this.pluginBaseDirectory);
			logger.debug( "base directory for JSPs (working copy): " + this.jspBaseDirectory);
			logger.debug( "Name of database: " + this.databaseName);
		}
	}
	
	public String getSystemPluginsBaseDirectory() {
		return this.systemPluginBaseDirectory;
	}
	
	public String getPluginsBaseDirectory() {
		return this.pluginBaseDirectory;
	}
	
	public String getSystemPluginDirectory( String pluginId) {
		return getPluginDirectory( this.systemPluginBaseDirectory, pluginId);
	}
	
	public String getPluginDirectory( String pluginId) {
		return getPluginDirectory( this.pluginBaseDirectory, pluginId);
	}
	
	public String getJspWorkingDirectory( String pluginId) {
		return this.jspBaseDirectory + File.separator + pluginId;
	}
	
	public String getJspBackupDirectory( String pluginId) {
		return getPluginDirectory( pluginId) + File.separator + "_jsp";
	}
	
	public String getDatabaseScriptsDirectory( String pluginId) {
		return getPluginDirectory( pluginId) + File.separator + "_db";
	}
	
	private String getPluginDirectory( String baseDirectory, String pluginId) {
		return baseDirectory + File.separator + pluginId;
	}
	
	public String getDatabaseInstallScript( String pluginId) {
		return getDatabaseScript( pluginId, "install");
	}
	
	public String getDatabaseDeinstallScript( String pluginId) {
		return getDatabaseScript( pluginId, "remove");
	}
	
	private String getDatabaseScript( String pluginId, String prefix) {
		String fileName = prefix + "-" + this.databaseName + ".sql";
		
		return getDatabaseScriptsDirectory( pluginId) + File.separator + fileName; 
	}

}
