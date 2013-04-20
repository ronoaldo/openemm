package org.agnitas.emm.extension;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.agnitas.emm.extension.exceptions.PluginInstantiationException;
import org.apache.log4j.Logger;
import org.java.plugin.PluginClassLoader;
import org.java.plugin.PluginManager;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.PluginDescriptor;

/**
 * Utility class to encapsulate all logic for managing EMM extension instances to ensure
 * that there is only one instance per Extension instance.
 * 
 * JspExtension and EmmFeatureExtension are two different types of EMM extensions, so each
 * of these needs one ExtensionManager.
 * 
 * @author md
 *
 * @param <T> type of plugin
 */
public class ExtensionManager<T> {
	
	/** Logger. */
	private static final Logger logger = Logger.getLogger(ExtensionManager.class);
	
	/** JPFs PluginManager */
	private final PluginManager pluginManager;
	
	/** Instances of plugins for Extension instances */
	private final Map<Extension, T> extensionInstances;
	
	/**
	 * Create a new ExtensionManager.
	 * 
	 * @param pluginManager JPFs PluginManager 
	 */
	public ExtensionManager( PluginManager pluginManager) {
		this.extensionInstances = new HashMap<Extension, T>();
		this.pluginManager = pluginManager;
	}
	
	/**
	 * Returns an instance of the EMM feature for the given Extension. 
	 * If an EMM feature was previously created for the Extension, this instance is returned. Otherwise, a
	 * new instance is created.
	 * 
	 * @param extension JPF Extension
	 * 
	 * @return instance of EMM extension
	 * 
	 * @throws PluginInstantiationException on errors creating the instance
	 */
	public T getOrCreateExtensionInstance( Extension extension) throws PluginInstantiationException {
		T extensionInstance = this.extensionInstances.get( extension);
		
		if( extensionInstance == null) 
			extensionInstance = createAndRegisterExtensionInstance( extension);
		
		return extensionInstance;
	}
	
	/**
	 * Creates a new instance of the EMM extension for the given JPS extension and registeres this
	 * instance in the internal storage.
	 * 
	 * @param extension JPF Extension
	 * 
	 * @return new EMM extension instance
	 * 
	 * @throws PluginInstantiationException on errors createing the instance
	 */
	private T createAndRegisterExtensionInstance( Extension extension) throws PluginInstantiationException {
		PluginDescriptor pluginDescriptor = extension.getDeclaringPluginDescriptor();
		PluginClassLoader pluginClassLoader = this.pluginManager.getPluginClassLoader( pluginDescriptor);

		String className = extension.getParameter( "class").valueAsString();
		
		try {
			Class<?> extensionClass = pluginClassLoader.loadClass( className);
			
			@SuppressWarnings("unchecked")
			T extensionInstance = (T) extensionClass.newInstance();
	
			this.extensionInstances.put( extension, extensionInstance);
			
			return extensionInstance;
		} catch (ClassNotFoundException e) {
			logger.error( e);
			throw new PluginInstantiationException( e);
		} catch (InstantiationException e) {
			logger.error( e);
			throw new PluginInstantiationException( e);
		} catch (IllegalAccessException e) {
			logger.error( e);
			throw new PluginInstantiationException( e);
		} catch( ClassCastException e) {
			logger.error( e);
			throw new PluginInstantiationException( e);
		}
	}

	/**
	 * De-register given extensions.
	 * 
	 * @param extensions list of extensions to de-register
	 */
	public void deregisterExtensions(Collection<Extension> extensions) {
		for( Extension extension : extensions)
			this.extensionInstances.remove( extension);
	}

}
