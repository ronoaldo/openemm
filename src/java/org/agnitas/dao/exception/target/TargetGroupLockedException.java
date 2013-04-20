package org.agnitas.dao.exception.target;

public class TargetGroupLockedException extends TargetGroupPersistenceException {
	
	private final int targetID;
	
	public TargetGroupLockedException( int targetID) {
		super( "Target group is locked: " + targetID);
		
		this.targetID = targetID;
	}
	
	public int getTargetID() {
		return this.targetID;
	}
}
