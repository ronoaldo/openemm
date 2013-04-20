package org.agnitas.emm.springws.endpoint.mailinglist;

import javax.annotation.Resource;

import org.agnitas.beans.Mailinglist;
import org.agnitas.emm.core.mailinglist.service.MailinglistModel;
import org.agnitas.emm.core.mailinglist.service.MailinglistService;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.ListMailinglistsRequest;
import org.agnitas.emm.springws.jaxb.ListMailinglistsResponse;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class ListMailinglistsEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
    private MailinglistService mailinglistService;
	@Resource
    private ObjectFactory objectFactory;

    @Override
    protected Object invokeInternal(Object arg0) throws Exception {
        @SuppressWarnings("unused")
		ListMailinglistsRequest request = (ListMailinglistsRequest) arg0;
        ListMailinglistsResponse response = objectFactory.createListMailinglistsResponse();
        
		MailinglistModel model = new MailinglistModel();
		model.setCompanyId(Utils.getUserCompany());

        for (Mailinglist mailinglist : mailinglistService.getMailinglists(model)) {
			response.getItem().add(new ResponseBuilder(mailinglist, objectFactory).createResponse());
		}
        return response;
    }

}
