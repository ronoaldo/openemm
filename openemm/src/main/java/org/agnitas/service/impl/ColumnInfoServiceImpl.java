package org.agnitas.service.impl;

import java.util.List;

import org.agnitas.beans.ProfileField;
import org.agnitas.dao.ProfileFieldDao;
import org.agnitas.service.ColumnInfoService;
import org.agnitas.util.CaseInsensitiveMap;

public class ColumnInfoServiceImpl implements ColumnInfoService {
	private ProfileFieldDao profileFieldDao;

    @Override
    public ProfileField getColumnInfo(int companyID, String column) throws Exception {
        return profileFieldDao.getProfileField(companyID, column);
    }

    @Override
    public List<ProfileField> getColumnInfos(int companyID) throws Exception {
    	return profileFieldDao.getProfileFields(companyID);
	}

    @Override
    public List<ProfileField> getColumnInfos(int companyID, int adminID) throws Exception {
    	return profileFieldDao.getProfileFields(companyID, adminID);
	}
    
    @Override
    public CaseInsensitiveMap<ProfileField> getColumnInfoMap(int companyID) throws Exception {
    	return profileFieldDao.getProfileFieldsMap(companyID);
	}
    
    @Override
    public CaseInsensitiveMap<ProfileField> getColumnInfoMap(int companyID, int adminID) throws Exception {
    	return profileFieldDao.getProfileFieldsMap(companyID, adminID);
	}

	public void setProfileFieldDao(ProfileFieldDao profileFieldDao) {
		this.profileFieldDao = profileFieldDao;
	}
}
