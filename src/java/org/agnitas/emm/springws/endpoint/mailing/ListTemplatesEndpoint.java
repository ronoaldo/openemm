package org.agnitas.emm.springws.endpoint.mailing;

import javax.annotation.Resource;

import org.agnitas.beans.Mailing;
import org.agnitas.emm.core.mailing.service.MailingModel;
import org.agnitas.emm.core.mailing.service.MailingService;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.ListTemplatesRequest;
import org.agnitas.emm.springws.jaxb.ListTemplatesResponse;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class ListTemplatesEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private MailingService mailingService;
	@Resource
	private ObjectFactory objectFactory; 

	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		@SuppressWarnings("unused")
		ListTemplatesRequest request = (ListTemplatesRequest) arg0;
		ListTemplatesResponse response = objectFactory.createListTemplatesResponse();

		MailingModel model = new MailingModel();
		model.setCompanyId(Utils.getUserCompany());
		model.setTemplate(true);
		
		for (Mailing mailing : mailingService.getMailings(model)) {
			response.getItem().add(new ResponseBuilder(objectFactory).createTemplateResponse(mailing));
		}
			
		return response;
	}

}
