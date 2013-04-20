package org.agnitas.emm.springws.endpoint.binding;

import javax.annotation.Resource;

import org.agnitas.beans.BindingEntry;
import org.agnitas.emm.core.binding.service.BindingModel;
import org.agnitas.emm.core.binding.service.BindingService;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.ListSubscriberBindingRequest;
import org.agnitas.emm.springws.jaxb.ListSubscriberBindingResponse;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class ListSubscriberBindingEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private BindingService bindingService;
	@Resource
	private ObjectFactory objectFactory;
	
	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		ListSubscriberBindingRequest request = (ListSubscriberBindingRequest) arg0;
		ListSubscriberBindingResponse response = objectFactory.createListSubscriberBindingResponse();
		
		BindingModel model = new BindingModel();
		model.setCustomerId(request.getCustomerID());
		model.setCompanyId(Utils.getUserCompany());

		for (BindingEntry binding : bindingService.getBindings(model)) {
			response.getItem().add(new ResponseBuilder(objectFactory).createResponse(binding));
		}
			
		return response;
	}

}
