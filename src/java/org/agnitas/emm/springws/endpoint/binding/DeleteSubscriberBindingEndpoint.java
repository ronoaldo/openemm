package org.agnitas.emm.springws.endpoint.binding;

import javax.annotation.Resource;

import org.agnitas.emm.core.binding.service.BindingModel;
import org.agnitas.emm.core.binding.service.BindingService;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.DeleteSubscriberBindingRequest;
import org.agnitas.emm.springws.jaxb.DeleteSubscriberBindingResponse;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class DeleteSubscriberBindingEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private BindingService bindingService;
	@Resource
	private ObjectFactory objectFactory;
	
	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		DeleteSubscriberBindingRequest request = (DeleteSubscriberBindingRequest) arg0;
		DeleteSubscriberBindingResponse response = objectFactory.createDeleteSubscriberBindingResponse();
		
		BindingModel model = new BindingModel();
		model.setCustomerId(request.getCustomerID());
		model.setCompanyId(Utils.getUserCompany());
		model.setMailinglistId(request.getMailinglistID());
		model.setMediatype(request.getMediatype());

		bindingService.deleteBinding(model);
		return response;
	}

}
