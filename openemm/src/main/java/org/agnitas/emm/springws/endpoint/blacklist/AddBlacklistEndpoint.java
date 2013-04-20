package org.agnitas.emm.springws.endpoint.blacklist;

import javax.annotation.Resource;

import org.agnitas.emm.core.blacklist.service.BlacklistModel;
import org.agnitas.emm.core.blacklist.service.BlacklistService;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.AddBlacklistRequest;
import org.agnitas.emm.springws.jaxb.AddBlacklistResponse;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class AddBlacklistEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private BlacklistService blacklistService;
	@Resource
	private ObjectFactory objectFactory; 

	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		AddBlacklistRequest request = (AddBlacklistRequest) arg0;
		AddBlacklistResponse response = objectFactory.createAddBlacklistResponse();
		
		BlacklistModel model = new BlacklistModel();
		model.setCompanyId(Utils.getUserCompany());
		model.setEmail(request.getEmail());
		response.setValue(blacklistService.insertBlacklist(model));
		return response;
	}

}
