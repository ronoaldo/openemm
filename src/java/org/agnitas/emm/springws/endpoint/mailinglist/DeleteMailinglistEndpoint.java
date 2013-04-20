package org.agnitas.emm.springws.endpoint.mailinglist;

import javax.annotation.Resource;

import org.agnitas.emm.core.mailinglist.service.MailinglistModel;
import org.agnitas.emm.core.mailinglist.service.MailinglistService;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.DeleteMailinglistRequest;
import org.agnitas.emm.springws.jaxb.DeleteMailinglistResponse;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class DeleteMailinglistEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private MailinglistService mailinglistService;
	@Resource
	private ObjectFactory objectFactory; 

	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		DeleteMailinglistRequest request = (DeleteMailinglistRequest) arg0;
		DeleteMailinglistResponse response = objectFactory.createDeleteMailinglistResponse();
		
		MailinglistModel model = new MailinglistModel();
		model.setCompanyId(Utils.getUserCompany());
		model.setMailinglistId(request.getMailinglistID());

		response.setValue(mailinglistService.deleteMailinglist(model));
		return response;
	}

}
