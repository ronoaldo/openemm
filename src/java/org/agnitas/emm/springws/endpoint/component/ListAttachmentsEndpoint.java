package org.agnitas.emm.springws.endpoint.component;

import javax.annotation.Resource;

import org.agnitas.beans.MailingComponent;
import org.agnitas.emm.core.component.service.ComponentModel;
import org.agnitas.emm.core.component.service.ComponentService;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.ListAttachmentsRequest;
import org.agnitas.emm.springws.jaxb.ListAttachmentsResponse;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class ListAttachmentsEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private ComponentService componentService;
	@Resource
	private ObjectFactory objectFactory; 

	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		ListAttachmentsRequest request = (ListAttachmentsRequest) arg0;
		ListAttachmentsResponse response = objectFactory.createListAttachmentsResponse();
		
		ComponentModel model = new ComponentModel();
		model.setCompanyId(Utils.getUserCompany());
		model.setMailingId(request.getMailingID());
		model.setComponentType(MailingComponent.TYPE_ATTACHMENT);
		
		for (MailingComponent component : componentService.getComponents(model)) {
			response.getItem().add(new ResponseBuilder(objectFactory).createResponse(component, false));
		}
			
		return response;
	}

}
