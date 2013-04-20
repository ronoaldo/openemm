package org.agnitas.emm.springws.exceptionresolver.blacklist;

import org.agnitas.emm.core.blacklist.service.BlacklistAlreadyExistException;
import org.agnitas.emm.springws.exceptionresolver.CommonExceptionResolver;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;

public class BlacklistExceptionResolver extends CommonExceptionResolver {

	@Override
	protected SoapFaultDefinition getFaultDefinition(Object endpoint,
			Exception ex) {
		if (ex instanceof BlacklistAlreadyExistException) {
			SoapFaultDefinition definition = getDefaultDefinition(ex);
			definition.setFaultStringOrReason("Address already blacklisted");
			return definition;
		}
		return super.getFaultDefinition(endpoint, ex);
	}
}
