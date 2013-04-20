package org.agnitas.emm.springws.exceptionresolver.mailinglist;

import org.agnitas.emm.core.mailinglist.service.MailinglistNotEmptyException;
import org.agnitas.emm.springws.exceptionresolver.CommonExceptionResolver;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;

public class MailinglistExceptionResolver  extends CommonExceptionResolver {

	@Override
	protected SoapFaultDefinition getFaultDefinition(Object endpoint,
			Exception ex) {
		if (ex instanceof MailinglistNotEmptyException) {
			SoapFaultDefinition definition = getDefaultDefinition(ex);
			definition.setFaultStringOrReason("Mailinglist not empty");
			return definition;
		}
		return super.getFaultDefinition(endpoint, ex);
	}

}
