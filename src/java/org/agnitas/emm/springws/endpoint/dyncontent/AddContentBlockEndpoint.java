package org.agnitas.emm.springws.endpoint.dyncontent;

import javax.annotation.Resource;

import org.agnitas.emm.core.dyncontent.service.ContentModel;
import org.agnitas.emm.core.dyncontent.service.DynamicTagContentService;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.AddContentBlockRequest;
import org.agnitas.emm.springws.jaxb.AddContentBlockResponse;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class AddContentBlockEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private DynamicTagContentService dynamicTagContentService;
	@Resource
	private ObjectFactory objectFactory; 

	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		AddContentBlockRequest request = (AddContentBlockRequest) arg0;
		AddContentBlockResponse response = objectFactory.createAddContentBlockResponse();
		
		ContentModel model = new ContentModel();
		model.setCompanyId(Utils.getUserCompany());
		model.setMailingId(request.getMailingID());
		model.setBlockName(request.getBlockName());
		model.setTargetId(request.getTargetID());
		model.setOrder(request.getOrder());
		model.setContent(request.getContent());
		
		response.setContentID(dynamicTagContentService.addContent(model));
		return response;
	}

}
