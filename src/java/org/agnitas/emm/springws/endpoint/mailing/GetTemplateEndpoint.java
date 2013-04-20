package org.agnitas.emm.springws.endpoint.mailing;

import javax.annotation.Resource;

import org.agnitas.beans.Mailing;
import org.agnitas.emm.core.mailing.service.MailingModel;
import org.agnitas.emm.core.mailing.service.MailingService;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.GetTemplateRequest;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class GetTemplateEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private MailingService mailingService;
	@Resource
	private ObjectFactory objectFactory; 

	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		GetTemplateRequest request = (GetTemplateRequest) arg0;

		MailingModel model = new MailingModel();
		model.setCompanyId(Utils.getUserCompany());
		model.setMailingId(request.getTemplateID());
		model.setTemplate(true);

		Mailing mailing = mailingService.getMailing(model);
		return objectFactory.createGetTemplateResponse(new ResponseBuilder(objectFactory).createTemplateResponse(mailing));
	}

}
