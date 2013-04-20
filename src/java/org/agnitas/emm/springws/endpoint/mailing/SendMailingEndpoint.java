package org.agnitas.emm.springws.endpoint.mailing;

import javax.annotation.Resource;

import org.agnitas.emm.core.mailing.service.MailingModel;
import org.agnitas.emm.core.mailing.service.MailingService;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.AddMailingRequest;
import org.agnitas.emm.springws.jaxb.AddMailingResponse;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.agnitas.emm.springws.jaxb.AddMailingRequest.TargetIDList;
import org.agnitas.emm.springws.jaxb.SendMailingRequest;
import org.agnitas.emm.springws.jaxb.SendMailingResponse;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class SendMailingEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private MailingService mailingService;
	@Resource
	private ObjectFactory objectFactory; 

	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		SendMailingRequest request = (SendMailingRequest) arg0;

		MailingModel model = new MailingModel();
		model.setCompanyId(Utils.getUserCompany());
		model.setMailingId(request.getMailingID());
		model.setMaildropStatus(request.getRecipientsType());
		model.setSendDate(request.getSendDate());
		model.setBlocksize(request.getBlocksize());
		model.setStepping(request.getStepping());
		
		mailingService.sendMailing(model);
		
		return objectFactory.createSendMailingResponse();
	}

}
