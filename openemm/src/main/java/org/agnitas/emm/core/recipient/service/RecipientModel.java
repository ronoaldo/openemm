package org.agnitas.emm.core.recipient.service;

import org.apache.commons.collections.map.CaseInsensitiveMap;

public class RecipientModel {

	private int companyId;
	private boolean doubleCheck;
	private String keyColumn;
	private boolean overwrite;
	private CaseInsensitiveMap parameters;
	
	public int getCompanyId() {
		return companyId;
	}
	
	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}
	
	public boolean isDoubleCheck() {
		return doubleCheck;
	}
	
	public void setDoubleCheck(boolean doubleCheck) {
		this.doubleCheck = doubleCheck;
	}
	
	public String getKeyColumn() {
		return keyColumn;
	}
	
	public void setKeyColumn(String keyColumn) {
		this.keyColumn = keyColumn;
	}
	
	public boolean isOverwrite() {
		return overwrite;
	}
	
	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}
	
	public CaseInsensitiveMap getParameters() {
		return parameters;
	}
	
	public void setParameters(CaseInsensitiveMap parameters) {
		this.parameters = parameters;
	}

	public String getEmail() {
		return (String) parameters.get("email");
	}

	public void setEmail(String email) {
		parameters.put("email", email);
	}

	public Integer getMailtype() {
		return parameters.get("mailtype") == null ? null : Integer.valueOf((String) parameters.get("mailtype"));
	}

//	public void setMailtype(Number mailtype) {
//		parameters.put("mailtype", mailtype);
//	}

	public Integer getGender() {
		return parameters.get("gender") == null ? null : Integer.valueOf((String) parameters.get("gender"));
	}

//	public void setGender(Number gender) {
//		parameters.put("gender", gender);
//	}
	
}
