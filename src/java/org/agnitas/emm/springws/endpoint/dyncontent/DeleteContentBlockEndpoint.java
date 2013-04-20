package org.agnitas.emm.springws.endpoint.dyncontent;

import javax.annotation.Resource;

import org.agnitas.emm.core.dyncontent.service.ContentModel;
import org.agnitas.emm.core.dyncontent.service.DynamicTagContentService;
import org.agnitas.emm.springws.endpoint.Utils;
import org.agnitas.emm.springws.jaxb.DeleteContentBlockRequest;
import org.agnitas.emm.springws.jaxb.DeleteContentBlockResponse;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

public class DeleteContentBlockEndpoint extends AbstractMarshallingPayloadEndpoint {

	@Resource
	private DynamicTagContentService dynamicTagContentService;
	@Resource
	private ObjectFactory objectFactory; 

	@Override
	protected Object invokeInternal(Object arg0) throws Exception {
		DeleteContentBlockRequest request = (DeleteContentBlockRequest) arg0;
		DeleteContentBlockResponse response = objectFactory.createDeleteContentBlockResponse();
		
		ContentModel model = new ContentModel();
		model.setCompanyId(Utils.getUserCompany());
		model.setContentId(request.getContentID());
		
		response.setValue(dynamicTagContentService.deleteContent(model));
		return response;
	}

}
