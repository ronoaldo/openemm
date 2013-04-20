package org.agnitas.cms.beans.impl;

import org.agnitas.cms.beans.CmsTargetGroup;

public class CmsTargetGroupImpl implements CmsTargetGroup {

	private int targetGroupID;
	private String shortname;
	private boolean deleted;
	
	@Override
	public int getTargetGroupID() {
		return this.targetGroupID;
	}

	@Override
	public String getShortname() {
		return this.shortname;
	}

	@Override
	public boolean isDeleted() {
		return this.deleted;
	}

	@Override
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public void setTargetGroupID(int targetGroupID) {
		this.targetGroupID = targetGroupID;
	}

	@Override
	public void setShortname(String shortname) {
		this.shortname = shortname;
	}
	
	public String toString() {
		return "XXXX-FEHLER-XXXXX"; // Nur zum Testen!
	}
}
