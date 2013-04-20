package org.agnitas.service;

import java.util.List;

import org.agnitas.beans.ProfileField;
import org.agnitas.util.CaseInsensitiveMap;

public interface ColumnInfoService {
	public ProfileField getColumnInfo(int companyID, String column) throws Exception;
	
	public List<ProfileField> getColumnInfos(int companyID) throws Exception;
	
	public List<ProfileField> getColumnInfos(int companyID, int adminID) throws Exception;

	public CaseInsensitiveMap<ProfileField> getColumnInfoMap(int companyID) throws Exception;
	
	public CaseInsensitiveMap<ProfileField> getColumnInfoMap(int companyID, int adminID) throws Exception;
}
