package org.agnitas.emm.springws.endpoint.recipient;

import javax.annotation.Resource;

import org.agnitas.emm.core.recipient.service.RecipientModel;
import org.agnitas.emm.core.recipient.service.RecipientService;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.AddSubscriberRequest;
import org.agnitas.emm.springws.jaxb.AddSubscriberResponse;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class AddSubscriberEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private RecipientService recipientService;
	@Resource
	private ObjectFactory objectFactory; 
	
	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		AddSubscriberRequest request = (AddSubscriberRequest) arg0;
		AddSubscriberResponse response = objectFactory.createAddSubscriberResponse();
		
		RecipientModel model = new RecipientModel();
		model.setCompanyId(Utils.getUserCompany());
		model.setDoubleCheck(request.isDoubleCheck());
		model.setKeyColumn(request.getKeyColumn());
		model.setOverwrite(request.isOverwrite());
		model.setParameters(Utils.toCaseInsensitiveMap(request.getParameters()));
		
		response.setCustomerID(recipientService.addSubscriber(model));
		return response;
	}
	
}
