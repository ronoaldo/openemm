package org.agnitas.cms.beans;

public interface CmsTargetGroup {
	public int getTargetGroupID();
	public void setTargetGroupID( int targetGroupID);
	
	public String getShortname();
	public void setShortname( String shortname);
	
	public boolean isDeleted();
	public void setDeleted( boolean deleted);
}
