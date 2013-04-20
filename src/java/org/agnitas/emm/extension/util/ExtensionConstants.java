package org.agnitas.emm.extension.util;

import org.agnitas.emm.extension.ExtensionSystem;

/**
 * Collection of constants used by the extension system.
 * 
 * @author md
 */
public class ExtensionConstants {
	/** Name of the application scope attribute containing the only instance of the ExtensionSystem. */
	public static final String EXTENSION_SYSTEM_APPLICATION_SCOPE_ATTRIBUTE = ExtensionSystem.class.getCanonicalName();

	/** Name of the request parameter used by the ExtensionService to determinte the plugin to invoke. */
	public static final String FEATURE_REQUEST_PARAMETER = "feature";

	public static final String PLUGIN_NAME_MANIFEST_ATTRIBUTE = "plugin-name";

	public static final String PLUGIN_DESCRIPTION_MANIFEST_ATTRIBUTE = "plugin-description";
}
