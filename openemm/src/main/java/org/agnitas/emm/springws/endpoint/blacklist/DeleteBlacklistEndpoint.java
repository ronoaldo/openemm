package org.agnitas.emm.springws.endpoint.blacklist;

import javax.annotation.Resource;

import org.agnitas.emm.core.blacklist.service.BlacklistModel;
import org.agnitas.emm.core.blacklist.service.BlacklistService;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.DeleteBlacklistRequest;
import org.agnitas.emm.springws.jaxb.DeleteBlacklistResponse;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class DeleteBlacklistEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private BlacklistService blacklistService;
	@Resource
	private ObjectFactory objectFactory; 

	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		DeleteBlacklistRequest request = (DeleteBlacklistRequest) arg0;
		DeleteBlacklistResponse response = objectFactory.createDeleteBlacklistResponse();
		
		BlacklistModel model = new BlacklistModel();
		model.setCompanyId(Utils.getUserCompany());
		model.setEmail(request.getEmail());
		response.setValue(blacklistService.deleteBlacklist(model));
		return response;
	}

}
