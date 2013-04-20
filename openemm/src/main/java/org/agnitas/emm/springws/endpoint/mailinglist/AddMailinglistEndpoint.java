package org.agnitas.emm.springws.endpoint.mailinglist;

import javax.annotation.Resource;

import org.agnitas.emm.core.mailinglist.service.MailinglistModel;
import org.agnitas.emm.core.mailinglist.service.MailinglistService;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.AddMailinglistRequest;
import org.agnitas.emm.springws.jaxb.AddMailinglistResponse;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class AddMailinglistEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private MailinglistService mailinglistService;
	@Resource
	private ObjectFactory objectFactory; 

	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		AddMailinglistRequest request = (AddMailinglistRequest) arg0;

		MailinglistModel model = new MailinglistModel();
		model.setCompanyId(Utils.getUserCompany());
		model.setShortname(request.getShortname());
		model.setDescription(request.getDescription());

		AddMailinglistResponse response = objectFactory.createAddMailinglistResponse();
		response.setMailinglistID(mailinglistService.addMailinglist(model));
		return response;
	}

}
