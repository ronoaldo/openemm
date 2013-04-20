package org.agnitas.emm.core.binding.service;

import java.util.List;

import org.agnitas.beans.BindingEntry;

public interface BindingService {

	BindingEntry getBinding(BindingModel model);

	void setBinding(BindingModel model);
	
	void deleteBinding(BindingModel model);
	
	List<BindingEntry> getBindings(BindingModel model);
}
