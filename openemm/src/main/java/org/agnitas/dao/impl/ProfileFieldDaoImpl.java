/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

package org.agnitas.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.agnitas.beans.ProfileField;
import org.agnitas.beans.impl.ProfileFieldImpl;
import org.agnitas.dao.ProfileFieldDao;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.CaseInsensitiveMap;
import org.agnitas.util.DbColumnType;
import org.agnitas.util.DbUtilities;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * 
 * @author mhe
 */
public class ProfileFieldDaoImpl extends BaseHibernateDaoImpl implements ProfileFieldDao {
	private static final transient Logger logger = Logger.getLogger(ProfileFieldDaoImpl.class);

	@Override
	public ProfileField getProfileField(int companyID, String columnName) throws Exception {
		if (companyID == 0) {
			return null;
		} else {
			@SuppressWarnings("unchecked")
			ProfileField profileField = (ProfileField) AgnUtils.getFirstResult(new HibernateTemplate(sessionFactory).find("from ProfileField where companyID = ? and col_name = ?", new Object[] { companyID, columnName }));
			DbColumnType columnType = DbUtilities.getColumnDataType(dataSource, "customer_" + companyID + "_tbl", columnName);
			if (columnType == null) {
				return null;
			} else {
				if (profileField == null) {
					ProfileField dbOnlyField = new ProfileFieldImpl();
					dbOnlyField.setCompanyID(companyID);
					dbOnlyField.setColumn(columnName);
					dbOnlyField.setShortname(columnName);
					dbOnlyField.setDataType(columnType.getTypeName());
					dbOnlyField.setDataTypeLength(columnType.getCharacterLength());
					dbOnlyField.setNullable(columnType.isNullable());
					dbOnlyField.setDefaultValue(DbUtilities.getColumnDefaultValue(dataSource, "customer_" + companyID + "_tbl", columnName));
					return dbOnlyField;
				} else {
					profileField.setDataType(columnType.getTypeName());
					profileField.setDataTypeLength(columnType.getCharacterLength());
					profileField.setNullable(columnType.isNullable());
					return profileField;
				}
			}
		}
	}

	@Override
	public List<ProfileField> getProfileFields(int companyID) throws Exception {
		if (companyID == 0) {
			return null;
		} else {
			CaseInsensitiveMap<ProfileField> profileFieldMap = getProfileFieldsMap(companyID);
			List<ProfileField> returnList = new ArrayList<ProfileField>(profileFieldMap.values());

			// Sort by shortname
			sortProfileFieldListByShortName(returnList);

			return returnList;
		}
	}

	@Override
	public List<ProfileField> getProfileFields(int companyID, int adminID) throws Exception {
		if (companyID == 0) {
			return null;
		} else {
			List<ProfileField> profileFieldList = getProfileFields(companyID);
			List<ProfileField> returnList = new ArrayList<ProfileField>();

			for (ProfileField profileField : profileFieldList) {
				if (profileField.getAdminID() == 0 || profileField.getAdminID() == adminID) {
					returnList.add(profileField);
				}
			}

			return returnList;
		}
	}

	@Override
	public CaseInsensitiveMap<ProfileField> getProfileFieldsMap(int companyID) throws Exception {
		try {
			if (companyID == 0) {
				return null;
			} else {
				@SuppressWarnings("unchecked")
				List<ProfileField> profileFieldList = new HibernateTemplate(sessionFactory).find("from ProfileField where companyID = ?", new Object[] { companyID });
				CaseInsensitiveMap<DbColumnType> dbDataTypes = DbUtilities.getColumnDataTypes(dataSource, "customer_" + companyID + "_tbl");
				CaseInsensitiveMap<ProfileField> returnMap = new CaseInsensitiveMap<ProfileField>();

				for (String columnName : dbDataTypes.keySet()) {
					ProfileField profileFieldFound = null;
					for (ProfileField profileField : profileFieldList) {
						if (columnName.equalsIgnoreCase(profileField.getColumn())) {
							profileFieldFound = profileField;
							break;
						}
					}
					if (profileFieldFound == null) {
						profileFieldFound = new ProfileFieldImpl();
						profileFieldFound.setCompanyID(companyID);
						profileFieldFound.setColumn(columnName);
						profileFieldFound.setShortname(columnName);
						profileFieldFound.setDefaultValue(DbUtilities.getColumnDefaultValue(dataSource, "customer_" + companyID + "_tbl", columnName));
					}
					DbColumnType columnType = dbDataTypes.get(profileFieldFound.getColumn());
					profileFieldFound.setDataType(columnType.getTypeName());
					profileFieldFound.setDataTypeLength(columnType.getCharacterLength());
					profileFieldFound.setNullable(columnType.isNullable());

					returnMap.put(profileFieldFound.getColumn(), profileFieldFound);
				}

				return returnMap;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public CaseInsensitiveMap<ProfileField> getProfileFieldsMap(int companyID, int adminID) throws Exception {
		if (companyID == 0) {
			return null;
		} else {
			List<ProfileField> profileFieldList = getProfileFields(companyID);
			CaseInsensitiveMap<ProfileField> returnMap = new CaseInsensitiveMap<ProfileField>();

			for (ProfileField profileField : profileFieldList) {
				if (profileField.getAdminID() == 0 || profileField.getAdminID() == adminID) {
					returnMap.put(profileField.getColumn(), profileField);
				}
			}

			return returnMap;
		}
	}

	@Override
	public ProfileField getProfileFieldByShortname(int companyID, String shortName) throws Exception {
		if (companyID == 0) {
			return null;
		} else {
			@SuppressWarnings("unchecked")
			ProfileField profileField = (ProfileField) AgnUtils.getFirstResult(new HibernateTemplate(sessionFactory).find("from ProfileField where companyID = ? and shortname=?", new Object[] { companyID, shortName }));

			return profileField;
		}
	}

	@Override
	public boolean saveProfileField(ProfileField profileField) throws Exception {
		ProfileField previousProfileField = getProfileField(profileField.getCompanyID(), profileField.getColumn());
		if (previousProfileField == null) {
			// Check if new shortname already exists before a new column is added to dbtable
			if (getProfileFieldByShortname(profileField.getCompanyID(), profileField.getShortname()) != null) {
				throw new Exception("New shortname for customerprofilefield already exists");
			}
			
			// Change DB Structure (throws an Exception if change is not possible)
			boolean createdDbField = addColumnToDbTable(profileField.getCompanyID(), profileField.getColumn(), profileField.getDataType(), profileField.getDataTypeLength(), profileField.getDefaultValue(), !profileField.getNullable());
			if (!createdDbField) {
				throw new Exception("DB-field could not be created");
			}
		} else {
			// Check if new shortname already exists before a new column is added to dbtable
			if (!previousProfileField.getShortname().equals(profileField.getShortname())
					&& getProfileFieldByShortname(profileField.getCompanyID(), profileField.getShortname()) != null) {
				throw new Exception("New shortname for customerprofilefield already exists");
			}

			// Change DB Structure if needed (throws an Exception if change is not possible)
			boolean alteredDbField = alterColumnTypeInDbTable(profileField.getCompanyID(), profileField.getColumn(), profileField.getDataType(), profileField.getDataTypeLength(), profileField.getDefaultValue(), !profileField.getNullable());
			if (!alteredDbField) {
				throw new Exception("DB-field could not be changed");
			}
		}
		
		new HibernateTemplate(sessionFactory).saveOrUpdate("ProfileField", profileField);
		return true;
	}

	@Override
	public void removeProfileField(int companyID, String fieldname) throws Exception {
		update(logger, "ALTER TABLE customer_" + companyID + "_tbl DROP COLUMN " + fieldname);

		update(logger, "DELETE FROM customer_field_tbl WHERE company_id = ? AND LOWER(col_name) = LOWER(?)", companyID, fieldname);
	}

	@Override
	public boolean addColumnToDbTable(int companyID, String fieldname, String fieldType, int length, String fieldDefault, boolean notNull) throws Exception {
		if (companyID <= 0) {
    		return false;
    	} else if (StringUtils.isBlank(fieldname)) {
    		return false;
    	} else if (StringUtils.isBlank(fieldType)) {
    		return false;
    	} else if (DbUtilities.containsColumnName(dataSource, "customer_" + companyID + "_tbl", fieldname)) {
			return false;
		} else {
			return DbUtilities.addColumnToDbTable(dataSource, "customer_" + companyID + "_tbl", fieldname, fieldType, length, fieldDefault, notNull);
		}
	}
	
	@Override
	public boolean alterColumnTypeInDbTable(int companyID, String fieldname, String fieldType, int length, String fieldDefault, boolean notNull) throws Exception {
		if (companyID <= 0) {
    		return false;
    	} else if (StringUtils.isBlank(fieldname)) {
    		return false;
    	} else if (StringUtils.isBlank(fieldType)) {
    		return false;
    	} else if (!DbUtilities.containsColumnName(dataSource, "customer_" + companyID + "_tbl", fieldname)) {
			return false;
		} else {
			return DbUtilities.alterColumnDefaultValueInDbTable(dataSource, "customer_" + companyID + "_tbl", fieldname, fieldDefault, notNull);
		}
	}

	protected static void sortProfileFieldListByShortName(List<ProfileField> list) {
		Collections.sort(list, new Comparator<ProfileField>() {
			@Override
			public int compare(ProfileField profileField1, ProfileField profileField2) {
				String shortname1 = profileField1.getShortname();
				String shortname2 = profileField2.getShortname();
				if (shortname1 != null && shortname2 != null) {
					return shortname1.toLowerCase().compareTo(shortname2.toLowerCase());
				} else if (shortname1 != null) {
					return 1;
				} else {
					return -1;
				}
			}
		});
	}
}
