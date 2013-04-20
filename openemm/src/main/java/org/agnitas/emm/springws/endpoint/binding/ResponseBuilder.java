package org.agnitas.emm.springws.endpoint.binding;

import org.agnitas.beans.BindingEntry;
import org.agnitas.emm.springws.jaxb.ObjectFactory;
import org.apache.log4j.Logger;

public class ResponseBuilder {
	
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(ResponseBuilder.class);
	
	private final ObjectFactory objectFactory;

	public ResponseBuilder(ObjectFactory objectFactory) {
		this.objectFactory = objectFactory;
	}
	
	public org.agnitas.emm.springws.jaxb.Binding createResponse(BindingEntry binding) {
		org.agnitas.emm.springws.jaxb.Binding response = objectFactory.createBinding();
		
		response.setCustomerID(binding.getCustomerID());
		response.setMailinglistID(binding.getMailinglistID());
		response.setMediatype(binding.getMediaType());
		response.setStatus(binding.getUserStatus());
		response.setUserType(binding.getUserType());
		response.setRemark(binding.getUserRemark());
		response.setExitMailingID(binding.getExitMailingID());
		response.setChangeDate(binding.getChangeDate());
		response.setCreationDate(binding.getCreationDate());
		
		return response;
	}
	
}
