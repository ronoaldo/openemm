package org.agnitas.emm.springws.endpoint.binding;

import javax.annotation.Resource;

import org.agnitas.emm.core.binding.service.BindingModel;
import org.agnitas.emm.core.binding.service.BindingService;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.GetSubscriberBindingRequest;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class GetSubscriberBindingEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private BindingService bindingService;
	@Resource
	private ObjectFactory objectFactory;
	
	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		GetSubscriberBindingRequest request = (GetSubscriberBindingRequest) arg0;
		
		BindingModel model = new BindingModel();
		model.setCustomerId(request.getCustomerID());
		model.setCompanyId(Utils.getUserCompany());
		model.setMailinglistId(request.getMailinglistID());
		model.setMediatype(request.getMediatype());

		return objectFactory.createGetSubscriberBindingResponse(new ResponseBuilder(objectFactory).createResponse(bindingService.getBinding(model)));
	}

}
