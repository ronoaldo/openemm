package org.agnitas.emm.extension.data.impl;

import java.util.Collection;
import java.util.Vector;

import org.agnitas.emm.extension.data.ExtensionStatus;
import org.agnitas.emm.extension.data.PluginDetail;

public class PluginDetailImpl extends PluginStatusImpl implements PluginDetail {

	private Collection<ExtensionStatus> extensionStatusList = new Vector<ExtensionStatus>();
	private Collection<String> dependingPluginIds = new Vector<String>();
	private boolean systemPlugin;

	@Override
	public Collection<ExtensionStatus> getExtensionStatusList() {
		return this.extensionStatusList;
	}

	public void setExtensionStatusList( Collection<ExtensionStatus> extensionStatusList) {
		this.extensionStatusList = extensionStatusList;
	}

	@Override
	public Collection<String> getDependingPluginIds() {
		return this.dependingPluginIds;
	}
	
	public void setDependingPluginIds(Collection<String> dependingPluginIds) {
		this.dependingPluginIds = dependingPluginIds;
	}

	@Override
	public boolean isSystemPlugin() {
		return this.systemPlugin;
	}
	
	public void setSystemPlugin(boolean systemPlugin) {
		this.systemPlugin = systemPlugin;
	}
}
