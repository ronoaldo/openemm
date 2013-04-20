package org.agnitas.emm.springws.endpoint.component;

import javax.annotation.Resource;

import org.agnitas.beans.MailingComponent;
import org.agnitas.emm.core.component.service.ComponentModel;
import org.agnitas.emm.core.component.service.ComponentService;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.AddAttachmentRequest;
import org.agnitas.emm.springws.jaxb.AddAttachmentResponse;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class AddAttachmentEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private ComponentService componentService;
	@Resource
	private ObjectFactory objectFactory;
	
	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		AddAttachmentRequest request = (AddAttachmentRequest) arg0;
		
		ComponentModel model = new ComponentModel();
		model.setCompanyId(Utils.getUserCompany());
		model.setMailingId(request.getMailingID());
		model.setMimeType(request.getMimeType());
		model.setComponentType(MailingComponent.TYPE_ATTACHMENT);
		model.setComponentName(request.getComponentName());
		model.setData(request.getData());

		AddAttachmentResponse response = objectFactory.createAddAttachmentResponse();
		response.setComponentID(componentService.addComponent(model));
		return response;
	}

}
