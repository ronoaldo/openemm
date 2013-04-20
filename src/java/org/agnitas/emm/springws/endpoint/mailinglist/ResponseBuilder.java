package org.agnitas.emm.springws.endpoint.mailinglist;

import org.agnitas.beans.Mailinglist;
import org.agnitas.emm.springws.jaxb.ObjectFactory;

public class ResponseBuilder {

	private final Mailinglist mailinglist;
	private final ObjectFactory objectFactory;

	public ResponseBuilder(Mailinglist mailinglist, ObjectFactory objectFactory) {
		this.mailinglist = mailinglist;
		this.objectFactory = objectFactory;
	}
	
	public org.agnitas.emm.springws.jaxb.Mailinglist createResponse() {
        org.agnitas.emm.springws.jaxb.Mailinglist mailinglistResponse = objectFactory.createMailinglist();
        mailinglistResponse.setId(mailinglist.getId());
        mailinglistResponse.setShortname(mailinglist.getShortname());
        mailinglistResponse.setDescription(mailinglist.getDescription());
        
        return mailinglistResponse;
	}
}
