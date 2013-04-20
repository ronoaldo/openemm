package org.agnitas.emm.springws.endpoint.blacklist;

import javax.annotation.Resource;

import org.agnitas.emm.core.blacklist.service.BlacklistService;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.GetBlacklistItemsRequest;
import org.agnitas.emm.springws.jaxb.GetBlacklistItemsResponse;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class GetBlacklistItemsEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private BlacklistService blacklistService;
	@Resource
	private ObjectFactory objectFactory; 

	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		@SuppressWarnings("unused")
		GetBlacklistItemsRequest request = (GetBlacklistItemsRequest) arg0;
		GetBlacklistItemsResponse response = objectFactory.createGetBlacklistItemsResponse();
		response.getEmail().addAll(blacklistService.getEmailList(Utils.getUserCompany()));
		return response;
	}

}
