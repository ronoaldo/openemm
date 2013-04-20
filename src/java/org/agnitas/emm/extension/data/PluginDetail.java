package org.agnitas.emm.extension.data;

import java.util.Collection;

public interface PluginDetail extends PluginStatus {
	public Collection<ExtensionStatus> getExtensionStatusList();
	public Collection<String> getDependingPluginIds();
	public boolean isSystemPlugin();
}
