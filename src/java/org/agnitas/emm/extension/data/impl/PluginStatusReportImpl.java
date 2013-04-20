package org.agnitas.emm.extension.data.impl;

import java.util.Collection;

import org.agnitas.emm.extension.data.PluginStatus;
import org.agnitas.emm.extension.data.PluginStatusReport;

public class PluginStatusReportImpl implements PluginStatusReport {

	private final Collection<? extends PluginStatus> items;
	
	public PluginStatusReportImpl( Collection<? extends PluginStatus> items) {
		this.items = items;
	}
	
	@Override
	public Collection<? extends PluginStatus> getItems() {
		return this.items;
	}

}
