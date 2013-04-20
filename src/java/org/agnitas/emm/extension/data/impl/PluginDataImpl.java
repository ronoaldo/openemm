package org.agnitas.emm.extension.data.impl;

import org.agnitas.emm.extension.data.PluginData;

public class PluginDataImpl implements PluginData {
	
	private String pluginId;
	private boolean activateOnStartup;

	@Override
	public String getPluginId() {
		return this.pluginId;
	}

	@Override
	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

	@Override
	public boolean isActivatedOnStartup() {
		return this.activateOnStartup;
	}

	@Override
	public void setActivatedOnStartup(boolean activatedOnStartup) {
		this.activateOnStartup = activatedOnStartup;
	}

}
