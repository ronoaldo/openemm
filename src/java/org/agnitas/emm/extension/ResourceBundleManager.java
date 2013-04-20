package org.agnitas.emm.extension;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

public class ResourceBundleManager {
	private static final transient Logger logger = Logger.getLogger( ResourceBundleManager.class);
	
	private final ExtensionSystem extensionSystem;
	private final Map<String, Map<String, ResourceBundle>> pluginBundleCache;
	
	public ResourceBundleManager( ExtensionSystem extensionSystem) {
		this.extensionSystem = extensionSystem;
		this.pluginBundleCache = new HashMap<String, Map<String, ResourceBundle>>();
	}
	
	public ResourceBundle getResourceBundle( String plugin, String bundleName) {
		String bundleFileName = bundleName + ".properties";
		
		Map<String, ResourceBundle> bundleMap = this.pluginBundleCache.get( plugin);
		
		if( bundleMap == null) {
			bundleMap = new HashMap<String, ResourceBundle>();
			
			this.pluginBundleCache.put( plugin, bundleMap);
		}
		
		ResourceBundle bundle = bundleMap.get( bundleFileName);
		
		if( bundle == null) {
			InputStream stream = this.extensionSystem.getPluginResource( plugin, bundleFileName);
			
			if( stream != null) {
    			try {
        			try {
        				bundle = new PropertyResourceBundle( stream);
        				
       					bundleMap.put( bundleFileName, bundle);
        			} finally {
        				stream.close();
        			}
    			} catch( IOException e) {
    				logger.error( "Error reading resource bundle '" + bundleFileName + "' for plugin '" + plugin + "'", e);
    			}
			} else {
				logger.warn( "i18n bundle '" + bundleFileName + "' not found for plugin '" + plugin + "'");
			}
		}

		return bundle;
	}
}
