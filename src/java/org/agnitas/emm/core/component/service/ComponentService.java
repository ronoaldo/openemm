package org.agnitas.emm.core.component.service;

import java.util.List;

import org.agnitas.beans.MailingComponent;

public interface ComponentService {

	List<MailingComponent> getComponents(ComponentModel model);

	MailingComponent getComponent(ComponentModel model);

	int addComponent(ComponentModel model);
	
	void updateComponent(ComponentModel model);

	void deleteComponent(ComponentModel model);

}
