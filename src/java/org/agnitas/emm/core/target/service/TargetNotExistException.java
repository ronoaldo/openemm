package org.agnitas.emm.core.target.service;

public class TargetNotExistException extends RuntimeException {

	private static final long serialVersionUID = -859778424456594357L;

	private final Integer targetID;

	public TargetNotExistException(Integer targetID) {
		this.targetID = targetID;
	}

	public Integer getTargetID() {
		return targetID;
	}

}
