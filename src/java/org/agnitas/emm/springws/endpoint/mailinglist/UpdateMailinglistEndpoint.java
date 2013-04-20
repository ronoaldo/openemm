package org.agnitas.emm.springws.endpoint.mailinglist;

import javax.annotation.Resource;

import org.agnitas.emm.core.mailinglist.service.MailinglistService;
import org.agnitas.emm.core.mailinglist.service.MailinglistModel;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.agnitas.emm.springws.jaxb.UpdateMailinglistRequest;
import org.agnitas.emm.springws.jaxb.UpdateMailinglistResponse;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class UpdateMailinglistEndpoint extends AbstractMarshallingPayloadEndpoint {
	
	@Resource
    private MailinglistService mailinglistService;
    @Resource
    private ObjectFactory objectFactory;

    @Override
    protected Object invokeInternal(Object arg0) throws Exception {
        UpdateMailinglistRequest request = (UpdateMailinglistRequest) arg0;
        UpdateMailinglistResponse response = objectFactory.createUpdateMailinglistResponse();

        MailinglistModel model = new MailinglistModel();
        model.setCompanyId(Utils.getUserCompany());
        model.setMailinglistId(request.getMailingListId());
        model.setShortname(request.getShortname());
        model.setDescription(request.getDescription());

        mailinglistService.updateMailinglist(model);
        return response;
    }

}
