package org.agnitas.emm.springws.endpoint.component;

import javax.annotation.Resource;

import org.agnitas.beans.MailingComponent;
import org.agnitas.emm.core.component.service.ComponentModel;
import org.agnitas.emm.core.component.service.ComponentService;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.GetAttachmentRequest;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class GetAttachmentEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private ComponentService componentService;
	@Resource
	private ObjectFactory objectFactory;
	
	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		GetAttachmentRequest request = (GetAttachmentRequest) arg0;
		
		ComponentModel model = new ComponentModel();
		model.setCompanyId(Utils.getUserCompany());
		model.setComponentId(request.getComponentID());
		model.setComponentType(MailingComponent.TYPE_ATTACHMENT);

		return objectFactory.createGetAttachmentResponse(new ResponseBuilder(objectFactory).createResponse(componentService.getComponent(model), true));
	}

}
