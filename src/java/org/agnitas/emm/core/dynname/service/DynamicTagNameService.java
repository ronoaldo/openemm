package org.agnitas.emm.core.dynname.service;

import java.util.List;

import org.agnitas.beans.DynamicTag;

public interface DynamicTagNameService {

	List<DynamicTag> getNameList(NameModel nameModel);
	
}
