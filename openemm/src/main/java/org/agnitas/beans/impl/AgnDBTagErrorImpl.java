package org.agnitas.beans.impl;

import org.agnitas.beans.AgnDBTagError;

public class AgnDBTagErrorImpl implements AgnDBTagError {

	private String invalidTag;
	private String errorDescription;
	
	public AgnDBTagErrorImpl(String invalidTag, String errorDescription) {
		this.invalidTag = invalidTag;
		this.errorDescription = errorDescription;
	}

	public String getInvalidTag() {
		return invalidTag;
	}
	public void setInvalidTag(String invalidTag) {
		this.invalidTag = invalidTag;
	}
	public String getErrorDescription() {
		return errorDescription;
	}
	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}
	 
	
}
