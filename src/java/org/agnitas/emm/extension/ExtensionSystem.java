package org.agnitas.emm.extension;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.agnitas.emm.extension.data.PluginDetail;
import org.agnitas.emm.extension.data.PluginStatusReport;
import org.agnitas.emm.extension.exceptions.DatabaseScriptException;
import org.agnitas.emm.extension.exceptions.ExtensionException;
import org.agnitas.emm.extension.exceptions.MissingPluginManifestException;
import org.agnitas.emm.extension.exceptions.PluginInstantiationException;
import org.agnitas.emm.extension.exceptions.RemovingSystemPluginNotAllowedException;
import org.agnitas.emm.extension.exceptions.UnknownPluginException;
import org.agnitas.emm.extension.util.I18NResourceBundle;
import org.java.plugin.JpfException;
import org.java.plugin.PluginLifecycleException;
import org.java.plugin.registry.Extension;
import org.springframework.context.ApplicationContext;

public interface ExtensionSystem {

	/**
	 * Invoke extension registered on a JSP extension point.
	 * 
	 * @param pluginName name of the plugin, where the extension point is defined in.
	 * @param extensionPointName name of the extension point
	 * @param pageContext PageContext from JSP tag
	 */
	public abstract void invokeJspExtension(String pluginName,
			String extensionPointName, PageContext pageContext);

	/**
	 * Invoke EmmFeatureExtension.
	 * 
	 * @param pluginId ID of the plugin
	 * @param context ApplicationContext of Spring for accessing EMM's DAOs and services.
	 * @param request servlet request
	 * @param response servlet response
	 * 
	 * @throws PluginInstantiationException on errors creating plugin instance
	 * @throws ExtensionException on errors during executing of the feature plugin
	 * @throws UnknownPluginException when specified plugin is unknown
	 */
	// TODO: Uses ApplicationContext in parameter list. PoC only!
	public abstract void invokeFeatureExtension(String pluginId,
			ApplicationContext context, HttpServletRequest request,
			HttpServletResponse response) throws PluginInstantiationException,
			ExtensionException, UnknownPluginException;

	/**
	 * Invoke setup of EmmFeatureExtension.
	 * 
	 * @param pluginId ID of the plugin
	 * @param context ApplicationContext of Spring for accessing EMM's DAOs and services.
	 * @param request servlet request
	 * @param response servlet response
	 * 
	 * @throws PluginInstantiationException on errors creating plugin instance
	 * @throws ExtensionException on errors during executing of the feature plugin
	 * @throws UnknownPluginException when specified plugin is unknown
	 */
	// TODO: Uses ApplicationContext in parameter list. PoC only!
	public abstract void invokeFeatureSetupExtension(String pluginId,
			ApplicationContext context, HttpServletRequest request,
			HttpServletResponse response) throws PluginInstantiationException,
			ExtensionException, UnknownPluginException;

	public abstract Collection<Extension> getActiveExtensions(String plugin, String extensionPoint);

	public abstract InputStream getPluginResource(String plugin, String resource);

	public abstract I18NResourceBundle getPluginI18NResourceBundle(String plugin);

	public abstract ResourceBundle getPluginResourceBundle(String plugin, String bundleName);

	public abstract Extension getExtension(String plugin, String extension);

	public abstract PluginStatusReport getPluginStatusReport();

	public abstract PluginDetail getPluginDetails(String pluginID) throws UnknownPluginException;

	public abstract void activatePluginForStartup(String pluginId) throws PluginLifecycleException;

	public abstract void deactivatePluginForStartup(String pluginId);

	public abstract void installPlugin(String pluginFilename) throws MissingPluginManifestException, IOException, PluginLifecycleException, JpfException, DatabaseScriptException;

	public abstract void uninstallPlugin(String pluginID) throws RemovingSystemPluginNotAllowedException;

	public abstract boolean isSystemPlugin( String pluginID);
}