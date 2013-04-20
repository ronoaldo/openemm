package org.agnitas.emm.springws.endpoint.mailing;

import javax.annotation.Resource;

import org.agnitas.emm.core.mailing.service.MailingModel;
import org.agnitas.emm.core.mailing.service.MailingService;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.AddMailingFromTemplateRequest;
import org.agnitas.emm.springws.jaxb.AddMailingFromTemplateResponse;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class AddMailingFromTemplateEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private MailingService mailingService;
	@Resource
	private ObjectFactory objectFactory; 

	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		AddMailingFromTemplateRequest request = (AddMailingFromTemplateRequest) arg0;

		MailingModel model = new MailingModel();
		model.setCompanyId(Utils.getUserCompany());
		model.setTemplateId(request.getTemplateID());
		model.setShortname(request.getShortname());
		model.setDescription(request.getDescription());
		model.setAutoUpdate(request.isAutoUpdate());

		AddMailingFromTemplateResponse response = objectFactory.createAddMailingFromTemplateResponse();
		response.setMailingID(mailingService.addMailingFromTemplate(model));
		return response;
	}

}
