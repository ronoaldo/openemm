package org.agnitas.emm.extension.data.impl;

import java.net.URL;

import org.agnitas.emm.extension.data.PluginStatus;

public class PluginStatusImpl implements PluginStatus {

	private URL url;
	private String version;
	private String vendor;
	private String pluginName;
	private String pluginId;
	private String description;
	private boolean activated;
	
	@Override
	public URL getUrl() {
		return this.url;
	}
	
	public void setUrl( URL url) {
		this.url = url;
	}
	
	@Override
	public String getVersion() {
		return this.version;
	}
	
	public void setVersion( String version) {
		this.version = version;
	}
	
	@Override
	public String getVendor() {
		return this.vendor;
	}
	
	public void setVendor( String vendor) {
		this.vendor = vendor;
	}

	@Override
	public String getPluginName() {
		return this.pluginName;
	}
	
	public void setPluginName( String pluginName) {
		this.pluginName = pluginName;			
	}

	@Override
	public String getPluginId() {
		return this.pluginId;
	}
	
	public void setPluginId( String pluginId) {
		this.pluginId = pluginId;
	}
	
	@Override
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription( String description) {
		this.description = description;
	}

	@Override
	public boolean isActivated() {
		return this.activated;
	}
	
	public void setActivated( boolean activated) {
		this.activated = activated;
	}
}
