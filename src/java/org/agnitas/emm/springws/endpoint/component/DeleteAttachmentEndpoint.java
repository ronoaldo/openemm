package org.agnitas.emm.springws.endpoint.component;

import javax.annotation.Resource;

import org.agnitas.beans.MailingComponent;
import org.agnitas.emm.core.component.service.ComponentModel;
import org.agnitas.emm.core.component.service.ComponentService;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.AddAttachmentRequest;
import org.agnitas.emm.springws.jaxb.AddAttachmentResponse;
import org.agnitas.emm.springws.jaxb.DeleteAttachmentRequest;
import org.agnitas.emm.springws.jaxb.DeleteAttachmentResponse;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class DeleteAttachmentEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private ComponentService componentService;
	@Resource
	private ObjectFactory objectFactory;
	
	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		DeleteAttachmentRequest request = (DeleteAttachmentRequest) arg0;
		
		ComponentModel model = new ComponentModel();
		model.setCompanyId(Utils.getUserCompany());
		model.setComponentId(request.getComponentID());
		model.setComponentType(MailingComponent.TYPE_ATTACHMENT);

		DeleteAttachmentResponse response = objectFactory.createDeleteAttachmentResponse();
		componentService.deleteComponent(model);
		return response;
	}

}
