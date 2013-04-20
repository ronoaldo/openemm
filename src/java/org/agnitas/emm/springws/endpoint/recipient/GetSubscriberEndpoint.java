package org.agnitas.emm.springws.endpoint.recipient;

import java.util.Map;

import javax.annotation.Resource;

import org.agnitas.emm.core.recipient.service.RecipientService;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.GetSubscriberRequest;
import org.agnitas.emm.springws.jaxb.GetSubscriberResponse;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class GetSubscriberEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private RecipientService recipientService;
	@Resource
	private ObjectFactory objectFactory;
	
	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		GetSubscriberRequest request = (GetSubscriberRequest) arg0;
		GetSubscriberResponse response = objectFactory.createGetSubscriberResponse();
		Map<String, Object> parameters = recipientService.getSubscriber(Utils.getUserCompany(), request.getCustomerID());
		if (parameters != null && parameters.size() > 0) {
			response.setParameters(Utils.toJaxbMap(parameters, objectFactory));
			response.setCustomerID(request.getCustomerID());
		} else {
			response.setCustomerID(0);
		}
		return response;
	}

}
