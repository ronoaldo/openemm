package org.agnitas.emm.springws.endpoint.recipient;

import javax.annotation.Resource;

import org.agnitas.emm.core.recipient.service.RecipientService;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.FindSubscriberRequest;
import org.agnitas.emm.springws.jaxb.FindSubscriberResponse;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class FindSubscriberEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private RecipientService recipientService;
	@Resource
	private ObjectFactory objectFactory;
	
	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		FindSubscriberRequest request = (FindSubscriberRequest) arg0;
		FindSubscriberResponse response = objectFactory.createFindSubscriberResponse();
		response.setValue(recipientService.findSubscriber(Utils.getUserCompany(), request.getKeyColumn(), request.getValue()));
		return response;
	}

}
