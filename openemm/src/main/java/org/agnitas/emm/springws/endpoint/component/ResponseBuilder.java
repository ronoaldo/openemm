package org.agnitas.emm.springws.endpoint.component;

import org.agnitas.beans.MailingComponent;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.apache.log4j.Logger;

public class ResponseBuilder {
	
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(ResponseBuilder.class);
	
	private final ObjectFactory objectFactory;

	public ResponseBuilder(ObjectFactory objectFactory) {
		this.objectFactory = objectFactory;
	}
	
	public org.agnitas.emm.springws.jaxb.Attachment createResponse(MailingComponent component, boolean copyData) {
		org.agnitas.emm.springws.jaxb.Attachment response = objectFactory.createAttachment();
		
		response.setComponentID(component.getId());
		response.setMimeType(component.getMimeType());
		response.setComponentType(component.getType());
		response.setComponentName(component.getComponentName());
		response.setTimestamp(component.getTimestamp());
		
		byte[] data = component.getBinaryBlock();
		response.setSize(data != null ? data.length : 0);
		if (copyData) {
			response.setData(data);
		}
		
		return response;
	}
}
