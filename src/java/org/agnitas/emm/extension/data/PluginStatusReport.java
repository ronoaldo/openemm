package org.agnitas.emm.extension.data;

import java.util.Collection;

public interface PluginStatusReport {
	public Collection<? extends PluginStatus> getItems();
}
