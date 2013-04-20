package org.agnitas.emm.springws.endpoint.mailinglist;

import javax.annotation.Resource;

import org.agnitas.beans.Mailinglist;
import org.agnitas.emm.core.mailinglist.service.MailinglistModel;
import org.agnitas.emm.core.mailinglist.service.MailinglistService;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.GetMailinglistRequest;
//import org.agnitas.emm.springws.jaxb.GetMailinglistResponse;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class GetMailinglistEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
    private MailinglistService mailinglistService;
	@Resource
    private ObjectFactory objectFactory;

    @Override
    protected Object invokeInternal(Object arg0) throws Exception {
        GetMailinglistRequest request = (GetMailinglistRequest) arg0;

		MailinglistModel model = new MailinglistModel();
		model.setCompanyId(Utils.getUserCompany());
		model.setMailinglistId(request.getMailinglistID());

        Mailinglist mailinglist = mailinglistService.getMailinglist(model);
        
        return objectFactory.createGetMailinglistResponse(new ResponseBuilder(mailinglist, objectFactory).createResponse());
    }

}
