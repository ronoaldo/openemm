package org.agnitas.emm.springws.endpoint.recipient;

import javax.annotation.Resource;

import org.agnitas.emm.core.recipient.service.RecipientService;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.agnitas.emm.springws.jaxb.UpdateSubscriberRequest;
import org.agnitas.emm.springws.jaxb.UpdateSubscriberResponse;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class UpdateSubscriberEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private RecipientService recipientService;
	@Resource
	private ObjectFactory objectFactory; 
	
	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		UpdateSubscriberRequest request = (UpdateSubscriberRequest) arg0;
		UpdateSubscriberResponse response = objectFactory.createUpdateSubscriberResponse();
		response.setValue(recipientService.updateSubscriber(Utils.getUserCompany(), request.getCustomerID(), Utils.toCaseInsensitiveMap(request.getParameters())));
		return response;
	}
	
}
