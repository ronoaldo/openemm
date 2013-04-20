package org.agnitas.emm.extension;

import org.agnitas.emm.extension.exceptions.ExtensionException;
import org.java.plugin.registry.Extension;
import org.springframework.context.ApplicationContext;

/**
 * Interface for new features. Features are used to add complete new functionality are invoked
 * by the ExtensionServlet. Feature are normally accessed by GET or POST requests.
 * 
 * @author md
 */
public interface EmmFeatureExtension {
	// TODO: ApplicationContext is used as parameter here, that not good. Quick hack for PoC only!!!

	/**
	 * Entry point for EMM features.
	 * 
	 * @param pluginContext PluginContext
	 * @param extension the Extension instance for the feature
	 * @param context Application context from Spring to access EMM's DAOs and services
	 * 
	 * @throws ExtensionException on errors
	 */
	public void invoke( PluginContext pluginContext, Extension extension, ApplicationContext context) throws ExtensionException;

	public void setup( PluginContext pluginContext, Extension extension, ApplicationContext context) throws ExtensionException;
}
