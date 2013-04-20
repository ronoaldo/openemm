package org.agnitas.emm.springws.endpoint.dyncontent;

import java.util.List;

import javax.annotation.Resource;

import org.agnitas.beans.DynamicTagContent;
import org.agnitas.emm.core.dyncontent.service.ContentModel;
import org.agnitas.emm.core.dyncontent.service.DynamicTagContentService;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.ListContentBlocksRequest;
import org.agnitas.emm.springws.jaxb.ListContentBlocksResponse;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.agnitas.emm.springws.jaxb.ListContentBlocksResponse.ContentBlock;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class ListContentBlocksEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private DynamicTagContentService dynamicTagContentService;
	@Resource
	private ObjectFactory objectFactory; 

	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		ListContentBlocksRequest request = (ListContentBlocksRequest) arg0;
		ListContentBlocksResponse response = objectFactory.createListContentBlocksResponse();
		
		ContentModel model = new ContentModel();
		model.setCompanyId(Utils.getUserCompany());
		model.setMailingId(request.getMailingID());
		
		List<DynamicTagContent> list = dynamicTagContentService.getContentList(model);
		List<ContentBlock> responseList = response.getContentBlock();
		for (DynamicTagContent content : list) {
			ContentBlock responseContentBlock = objectFactory.createListContentBlocksResponseContentBlock();
			responseContentBlock.setContentID(content.getId());
			responseContentBlock.setName(content.getDynName());
			responseContentBlock.setTargetID(content.getTargetID());
			responseContentBlock.setOrder(content.getDynOrder());
			responseList.add(responseContentBlock);
		}
		
		return response;
	}

}
