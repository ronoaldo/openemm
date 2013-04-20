package org.agnitas.emm.springws.endpoint.dyncontent;

import javax.annotation.Resource;

import org.agnitas.emm.core.dyncontent.service.ContentModel;
import org.agnitas.emm.core.dyncontent.service.DynamicTagContentService;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.agnitas.emm.springws.jaxb.UpdateContentBlockRequest;
import org.agnitas.emm.springws.jaxb.UpdateContentBlockResponse;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class UpdateContentBlockEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private DynamicTagContentService dynamicTagContentService;
	@Resource
	private ObjectFactory objectFactory; 

	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		UpdateContentBlockRequest request = (UpdateContentBlockRequest) arg0;
		UpdateContentBlockResponse response = objectFactory.createUpdateContentBlockResponse();
		
		ContentModel model = new ContentModel();
		model.setCompanyId(Utils.getUserCompany());
		model.setContentId(request.getContentID());
		model.setTargetId(request.getTargetID());
		model.setOrder(request.getOrder());
		model.setContent(request.getContent());
		
		dynamicTagContentService.updateContent(model);
		return response;
	}

}
