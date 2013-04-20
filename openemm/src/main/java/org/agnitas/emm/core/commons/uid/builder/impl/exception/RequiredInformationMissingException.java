package org.agnitas.emm.core.commons.uid.builder.impl.exception;

public class RequiredInformationMissingException extends Exception {
	private final String information;
	
	public RequiredInformationMissingException( String information) {
		super( "Required informations are missing: " + information);
		
		this.information = information;
	}
	
    public String getInformation() {
    	return this.information;
    }
}
