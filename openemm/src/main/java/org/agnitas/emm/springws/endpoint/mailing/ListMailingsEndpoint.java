package org.agnitas.emm.springws.endpoint.mailing;

import javax.annotation.Resource;

import org.agnitas.beans.Mailing;
import org.agnitas.emm.core.mailing.service.MailingModel;
import org.agnitas.emm.core.mailing.service.MailingService;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.ListMailingsRequest;
import org.agnitas.emm.springws.jaxb.ListMailingsResponse;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class ListMailingsEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private MailingService mailingService;
	@Resource
	private ObjectFactory objectFactory; 

	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		@SuppressWarnings("unused")
		ListMailingsRequest request = (ListMailingsRequest) arg0;
		ListMailingsResponse response = objectFactory.createListMailingsResponse();

		MailingModel model = new MailingModel();
		model.setCompanyId(Utils.getUserCompany());
		model.setTemplate(false);
		
		for (Mailing mailing : mailingService.getMailings(model)) {
			response.getItem().add(new ResponseBuilder(objectFactory).createResponse(mailing));
		}
			
		return response;
	}

}
