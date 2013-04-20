package org.agnitas.emm.extension.util;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.agnitas.emm.extension.AnnotatedDispatchingEmmFeatureExtension;
import org.agnitas.emm.extension.ExtensionSystem;
import org.agnitas.emm.extension.PluginContext;

/**
 * Collection of utility methods for the extension feature.
 * 
 * @author md
 */
public class ExtensionUtils {
	
	/**
	 * Returns the instance of the extension system for the given servlet.
	 * 
	 * @param context context of the web application
	 * 
	 * @return ExtensionSystem for the web application
	 */
	public static ExtensionSystem getExtensionSystem( ServletContext context) {
		return (ExtensionSystem) context.getAttribute( ExtensionConstants.EXTENSION_SYSTEM_APPLICATION_SCOPE_ATTRIBUTE);
	}
	
	/**
	 * Returns the instance of the extension system for the given servlet.
	 * 
	 * @param session current HTTP session
	 * 
	 * @return ExtensionSystem for the web application
	 */
	public static ExtensionSystem getExtensionSystem( HttpSession session) {
		return getExtensionSystem( session.getServletContext());
	}
	
	/**
	 * Returns the instance of the extension system for the given servlet.
	 * 
	 * @param request current HTTP request
	 * 
	 * @return ExtensionSystem for the web application
	 */
	public static ExtensionSystem getExtensionSystem( HttpServletRequest request) {
		return getExtensionSystem( request.getSession());
	}
	
	/**
	 * Returns the value of the &quot;method&quot; request parameter. This parameter is used by the AnnotatedDispatchingEmmFeatureExtension class
	 * to decide which method is to be invoked.
	 * 
	 * @param pluginContext context of the plugin
	 * 
	 * @return value of the request parameter or null
	 * 
	 * @see AnnotatedDispatchingEmmFeatureExtension
	 */
	public static String getDispatchParameterValue( PluginContext pluginContext) {
		return getRequestParameterValue( "method", null, pluginContext.getServletRequest());
	}

	/**
	 * Returns the value of the &quot;method&quot; request parameter. This parameter is used by the AnnotatedDispatchingEmmFeatureExtension class
	 * to decide which method is to be invoked.
	 * 
	 * @param pluginContext context of the plugin
	 * @param defaultValue default value when request parameter is not present
	 * 
	 * @return value of the request parameter or the specified default value
	 * 
	 * @see AnnotatedDispatchingEmmFeatureExtension
	 */
	public static String getDispatchParameterValue( PluginContext pluginContext, String defaultValue) {
		return getRequestParameterValue( "method", defaultValue, pluginContext.getServletRequest());
	}
	
	/**
	 * Returns the value of the specified request parameter
	 * 
	 * @param paramName	name of the request parameter
	 * @param defaultValue default value 
	 * @param request ServletRequest
	 * 
	 * @return value of the request parameter or the default value when the parameter is not present
	 */
	public static String getRequestParameterValue( String paramName, String defaultValue, HttpServletRequest request) {
		String value = request.getParameter( paramName);
		
		if( value == null)
			return defaultValue;
		
		return value;
	}
}
