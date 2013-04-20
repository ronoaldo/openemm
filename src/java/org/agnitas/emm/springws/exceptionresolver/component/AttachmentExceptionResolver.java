package org.agnitas.emm.springws.exceptionresolver.component;

import org.agnitas.emm.core.component.service.ComponentNotExistException;
import org.agnitas.emm.springws.exceptionresolver.CommonExceptionResolver;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;

public class AttachmentExceptionResolver extends CommonExceptionResolver {

	@Override
	protected SoapFaultDefinition getFaultDefinition(Object endpoint,
			Exception ex) {
		 if (ex instanceof ComponentNotExistException) {
			 SoapFaultDefinition definition = getDefaultDefinition(ex);
	         definition.setFaultStringOrReason("Attachment not exist");
	         return definition;
	     }
		return super.getFaultDefinition(endpoint, ex);
	}

}
