package org.agnitas.emm.springws.endpoint.mailing;

import javax.annotation.Resource;

import org.agnitas.beans.Mailing;
import org.agnitas.emm.core.mailing.service.MailingModel;
import org.agnitas.emm.core.mailing.service.MailingService;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.ListMailingsInMailinglistRequest;
import org.agnitas.emm.springws.jaxb.ListMailingsInMailinglistResponse;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class ListMailingsInMailinglistEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private MailingService mailingService;
	@Resource
	private ObjectFactory objectFactory; 

	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		ListMailingsInMailinglistRequest request = (ListMailingsInMailinglistRequest) arg0;
		ListMailingsInMailinglistResponse response = objectFactory.createListMailingsInMailinglistResponse();

		MailingModel model = new MailingModel();
		model.setCompanyId(Utils.getUserCompany());
		model.setMailinglistId(request.getMailinglistID());
		
		for (Mailing mailing : mailingService.getMailingsForMLID(model)) {
			response.getItem().add(new ResponseBuilder(objectFactory).createResponse(mailing));
		}
			
		return response;
	}

}
