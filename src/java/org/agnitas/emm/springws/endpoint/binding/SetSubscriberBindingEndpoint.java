package org.agnitas.emm.springws.endpoint.binding;

import javax.annotation.Resource;

import org.agnitas.emm.core.binding.service.BindingModel;
import org.agnitas.emm.core.binding.service.BindingService;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.agnitas.emm.springws.jaxb.SetSubscriberBindingRequest;
import org.agnitas.emm.springws.jaxb.SetSubscriberBindingResponse;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class SetSubscriberBindingEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private BindingService bindingService;
	@Resource
	private ObjectFactory objectFactory;
	
	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		SetSubscriberBindingRequest request = (SetSubscriberBindingRequest) arg0;
		SetSubscriberBindingResponse response = objectFactory.createSetSubscriberBindingResponse();
		
		BindingModel model = new BindingModel();
		model.setCustomerId(request.getCustomerID());
		model.setCompanyId(Utils.getUserCompany());
		model.setMailinglistId(request.getMailinglistID());
		model.setMediatype(request.getMediatype());
		model.setStatus(request.getStatus());
		model.setUserType(request.getUserType());
		model.setRemark(request.getRemark());
		model.setExitMailingId(request.getExitMailingID());
		
		bindingService.setBinding(model);
		return response;
	}

}
