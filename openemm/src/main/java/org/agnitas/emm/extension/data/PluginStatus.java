package org.agnitas.emm.extension.data;

import java.net.URL;

public interface PluginStatus {
	public URL getUrl();
	public String getVersion();
	public String getVendor();
	public String getPluginName();
	public String getPluginId();
	public String getDescription();
	public boolean isActivated();
}
