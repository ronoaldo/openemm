package org.agnitas.emm.springws.endpoint.dynname;

import java.util.List;

import javax.annotation.Resource;

import org.agnitas.beans.DynamicTag;
import org.agnitas.emm.core.dynname.service.DynamicTagNameService;
import org.agnitas.emm.core.dynname.service.NameModel;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.ListContentBlockNamesRequest;
import org.agnitas.emm.springws.jaxb.ListContentBlockNamesResponse;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.agnitas.emm.springws.jaxb.ListContentBlockNamesResponse.ContentBlockName;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class ListContentBlockNamesEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private DynamicTagNameService dynamicTagNameService;
	@Resource
	private ObjectFactory objectFactory; 

	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		ListContentBlockNamesRequest request = (ListContentBlockNamesRequest) arg0;
		ListContentBlockNamesResponse response = objectFactory.createListContentBlockNamesResponse();
		
		NameModel model = new NameModel();
		model.setCompanyId(Utils.getUserCompany());
		model.setMailingId(request.getMailingID());
		
		List<DynamicTag> list = dynamicTagNameService.getNameList(model);
		List<ContentBlockName> responseList = response.getContentBlockName();
		for (DynamicTag name : list) {
			ContentBlockName responseContentBlock = objectFactory.createListContentBlockNamesResponseContentBlockName();
			responseContentBlock.setNameID(name.getId());
			responseContentBlock.setName(name.getDynName());
			responseList.add(responseContentBlock);
		}
		
		return response;
	}

}
