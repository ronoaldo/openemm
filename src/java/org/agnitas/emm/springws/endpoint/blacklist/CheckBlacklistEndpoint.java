package org.agnitas.emm.springws.endpoint.blacklist;

import javax.annotation.Resource;

import org.agnitas.emm.core.blacklist.service.BlacklistModel;
import org.agnitas.emm.core.blacklist.service.BlacklistService;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.CheckBlacklistRequest;
import org.agnitas.emm.springws.jaxb.CheckBlacklistResponse;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class CheckBlacklistEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private BlacklistService blacklistService;
	@Resource
	private ObjectFactory objectFactory; 

	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		CheckBlacklistRequest request = (CheckBlacklistRequest) arg0;
		CheckBlacklistResponse response = objectFactory.createCheckBlacklistResponse();
		BlacklistModel model = new BlacklistModel();
		model.setCompanyId(Utils.getUserCompany());
		model.setEmail(request.getEmail());
		response.setValue(blacklistService.checkBlacklist(model));
		return response;
	}

}
