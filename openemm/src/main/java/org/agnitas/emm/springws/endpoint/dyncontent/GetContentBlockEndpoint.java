package org.agnitas.emm.springws.endpoint.dyncontent;

import javax.annotation.Resource;

import org.agnitas.beans.DynamicTagContent;
import org.agnitas.emm.core.dyncontent.service.ContentModel;
import org.agnitas.emm.core.dyncontent.service.DynamicTagContentService;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.GetContentBlockRequest;
import org.agnitas.emm.springws.jaxb.GetContentBlockResponse;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class GetContentBlockEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private DynamicTagContentService dynamicTagContentService;
	@Resource
	private ObjectFactory objectFactory; 

	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		GetContentBlockRequest request = (GetContentBlockRequest) arg0;
		GetContentBlockResponse response = objectFactory.createGetContentBlockResponse();
		
		ContentModel model = new ContentModel();
		model.setCompanyId(Utils.getUserCompany());
		model.setContentId(request.getContentID());
		
		DynamicTagContent content = dynamicTagContentService.getContent(model);
		response.setContentID(content.getId());
		response.setName(content.getDynName());
		response.setTargetID(content.getTargetID());
		response.setOrder(content.getDynOrder());
		response.setContent(content.getDynContent());
		response.setMailingID(content.getMailingID());
		
		return response;
	}

}
