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

import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.agnitas.beans.Admin;
import org.agnitas.beans.AdminEntry;
import org.agnitas.beans.impl.AdminEntryImpl;
import org.agnitas.beans.impl.AdminImpl;
import org.agnitas.beans.impl.PaginatedListImpl;
import org.agnitas.dao.AdminDao;
import org.agnitas.dao.AdminGroupDao;
import org.agnitas.dao.CompanyDao;
import org.agnitas.util.AgnUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 *
 * @author mhe
 */
public class AdminDaoImpl extends BaseDaoImpl implements AdminDao {
	private static final transient Logger logger = Logger.getLogger(AdminDaoImpl.class);

    protected AdminGroupDao adminGroupDao;
	protected CompanyDao companyDao;

	// ----------------------------------------------------------------------------------------------------------------
	// Business Logic

	@Override
	public Admin getAdmin(int adminID, int companyID) {
		if(adminID==0) {
			return null;
		}
        Admin admin = null;
        String query = "select admin_id, username, company_id, fullname, admin_country, admin_lang, " +
                "admin_lang_variant, admin_timezone, layout_id, creation_date, pwd_change, admin_group_id, pwd_hash, preferred_list_size, default_import_profile_id " +
                "from admin_tbl where admin_id="+ adminID +" AND (company_id="+ companyID +" OR company_id IN (SELECT company_id FROM company_tbl comp WHERE creator_company_id="+ companyID +"))";
        try {
			admin = getSimpleJdbcTemplate().queryForObject(query, new Admin_RowMapper());
		} catch (DataAccessException e) {
			// No User found
			return null;
		}

        return admin;
	}

	@Override
	public Admin getAdminByLogin(String name, String password) {
		byte[] pwdHash=null;

		try {
			pwdHash=MessageDigest.getInstance("MD5").digest(password.getBytes());
		} catch (Exception e) {
			logger.error( "fatal: " + e.getMessage(), e);

			return null;
		}

        Admin admin = null;
        String query = "select admin_id, username, company_id, fullname, admin_country, admin_lang, " +
                "admin_lang_variant, admin_timezone, layout_id, creation_date, pwd_change, admin_group_id, pwd_hash, preferred_list_size, default_import_profile_id " +
                "from admin_tbl where username = ? and pwd_hash = ?";
         try {
			admin = getSimpleJdbcTemplate().queryForObject(query, new Admin_RowMapper(), new Object[] {name, pwdHash});
		} catch (DataAccessException e) {
			// No User found
			return null;
		}
		return admin;
	}

	@Override
	public void	save(Admin admin) {
        SimpleJdbcTemplate template = getSimpleJdbcTemplate();
        String sql = "";
        boolean newAdmin = admin.getAdminID() == 0;
        if(newAdmin){
            int newID = 0;
            if(AgnUtils.isMySQLDB()){
                newID = template.queryForInt("select ifnull(max(admin_id),0) + 1 from admin_tbl");
                sql = "insert into admin_tbl values(" + newID + ",?,?,?,now(),?,?,?,?,?,?,?,?,?,?,?)";
            } else {
                newID = template.queryForInt("select admin_tbl_seq.nextval from dual");
                sql = "insert into admin_tbl values(" + newID + ",?,?,?,sysdate,?,?,?,?,?,?,?,?,?,?,?)";
            }
            admin.setAdminID(newID);

        } else {
            sql = "update admin_tbl set username = ?, company_id = ?, fullname = ?, admin_country = ?, admin_lang = ?, " +
                    "admin_lang_variant = ?, admin_timezone = ?, layout_id = ?, creation_date = ?, pwd_change = ?, admin_group_id = ?, pwd_hash = ?, " +
                    "preferred_list_size = ?, default_import_profile_id = ? where admin_id = " + admin.getAdminID();
        }
        template.update(sql, new Object[] {admin.getUsername(), admin.getCompanyID(), admin.getFullname(), admin.getAdminCountry(), admin.getAdminLang(),
                    admin.getAdminLangVariant(), admin.getAdminTimezone(), admin.getLayoutID(), admin.getCreationDate(), admin.getLastPasswordChange(),
                    admin.getGroup().getGroupID(), admin.getPasswordHash(), admin.getPreferredListSize(), admin.getDefaultImportProfileID()});
        if(!newAdmin){
            saveAdminRights(admin.getAdminID(), admin.getAdminPermissions());
        }
	}

	@Override
	public boolean delete(Admin admin) {
		  SimpleJdbcTemplate tmpl =  getSimpleJdbcTemplate();
          String query1 = "delete from admin_tbl where admin_id = " + admin.getAdminID();
          String query2 = "delete from admin_permission_tbl where admin_id = " + admin.getAdminID();
          tmpl.update(query1);
          tmpl.update(query2);
	      return true;
	}

	@Override
    public List<AdminEntry> getAllAdminsByCompanyId(int companyID) {
        SimpleJdbcTemplate tmpl =  getSimpleJdbcTemplate();
        String query = "SELECT adm.admin_id, adm.username, adm.fullname, comp.shortname FROM admin_tbl adm, company_tbl comp where adm.company_id = comp.company_id and adm.company_id = " + companyID + " ORDER BY lower(adm.username)";
        List<Map<String, Object>> adminElements = tmpl.queryForList(query);
		List<AdminEntry> list = toAdminList(adminElements);
        return list;

    }

	@Override
    public List<AdminEntry> getAllAdmins() {
        SimpleJdbcTemplate tmpl =  getSimpleJdbcTemplate();
        String query = "SELECT adm.admin_id, adm.username, adm.fullname, comp.shortname FROM admin_tbl adm, company_tbl comp where adm.company_id = comp.company_id ORDER BY lower(adm.username)";
        List<Map<String, Object>> adminElements = tmpl.queryForList(query);
		List<AdminEntry> list = toAdminList(adminElements);
        return list;
    }

	@Override
	public boolean adminExists(int companyId, String username) {
		String sql = "select admin_id from admin_tbl where company_id=? and username=?";
		List<Map<String, Object>> list = getSimpleJdbcTemplate().queryForList(sql, companyId, username);
		return list != null && list.size() > 0;
	}

    @Override
    public void saveAdminRights(int adminID, Set<String> userRights) {
        SimpleJdbcTemplate template = getSimpleJdbcTemplate();
        template.update("delete from admin_permission_tbl where admin_id = " + adminID);
        List<Object[]> parameterList = new ArrayList<Object[]>();
                for (String permission : userRights) {
					parameterList.add(new Object[] { adminID, permission });
                }
        template.batchUpdate("INSERT INTO admin_permission_tbl (admin_id, security_token) VALUES (?, ?)", parameterList);
    }

    @Override
    public PaginatedListImpl<AdminEntry> getAdminListByCompanyId(int companyID, String sort, String direction, int page, int rownums) {

        if (StringUtils.isBlank(sort)) {
            sort = "adm.username";
        }

        if (StringUtils.isEmpty(direction)) {
            direction = "asc";
        }

        String totalRowsQuery = "select count(adm.admin_id) from admin_tbl adm, company_tbl comp  WHERE (adm.company_id=? OR adm.company_id IN (SELECT company_id FROM company_tbl WHERE creator_company_id=?)) AND status<>'deleted' AND comp.company_ID=adm.company_id";

        int totalRows = -1;
        try {
            totalRows = getSimpleJdbcTemplate().queryForInt(totalRowsQuery, companyID, companyID);
        } catch (Exception e) {
            totalRows = 0; // query for int has a bug , it doesn't return '0' in case of nothing is found...
        }
        // the page numeration begins with 1
        if (page < 1) {
        	page = 1;
        }
        page = AgnUtils.getValidPageNumber(totalRows, page, rownums);

        int offset = (page - 1) * rownums;
        String adminListQuery = "SELECT adm.admin_id, adm.username, adm.fullname, comp.shortname, adm.company_id FROM admin_tbl adm, company_tbl comp WHERE (adm.company_id=? OR adm.company_id IN (SELECT company_id FROM company_tbl WHERE creator_company_id=?)) AND status<>'deleted' AND comp.company_ID=adm.company_id ORDER BY " + sort + " "+direction+" LIMIT ? , ? ";

        List<Map<String, Object>> adminElements = getSimpleJdbcTemplate().queryForList(adminListQuery, companyID, companyID, offset, rownums);
        return new PaginatedListImpl<AdminEntry>(toAdminList(adminElements), totalRows, rownums, page, sort, direction);
    }

    protected List<AdminEntry> toAdminList(List<Map<String, Object>> adminElements) {
        List<AdminEntry> mailloopEntryList = new ArrayList<AdminEntry>();
        for (Map<String, Object> row : adminElements) {
            Integer id = (Integer) row.get("admin_id");
            String username = (String) row.get("username");
            String fullname = (String) row.get("fullname");
            String shortname = (String) row.get("shortname");
            AdminEntry entry = new AdminEntryImpl(id, username, fullname, shortname);
            mailloopEntryList.add(entry);
        }

        return mailloopEntryList;

    }

    private class Admin_RowMapper implements ParameterizedRowMapper<Admin> {
		public Admin mapRow(ResultSet resultSet, int row) throws SQLException {
			Admin admin = new AdminImpl();

			admin.setAdminID(resultSet.getInt("admin_id"));
			admin.setUsername(resultSet.getString("username"));
			admin.setFullname(resultSet.getString("fullname"));
			admin.setCompanyID(resultSet.getInt("company_id"));
			admin.setPasswordHash((byte[]) resultSet.getObject("pwd_hash"));
			admin.setCreationDate(resultSet.getTimestamp("creation_date"));
			admin.setLastPasswordChange(resultSet.getTimestamp("pwd_change"));
			admin.setAdminCountry(resultSet.getString("admin_country"));
			admin.setAdminLang(resultSet.getString("admin_lang"));
			admin.setAdminLangVariant(resultSet.getString("admin_lang_variant"));
			admin.setAdminTimezone(resultSet.getString("admin_timezone"));
			admin.setLayoutID(resultSet.getInt("layout_id"));
			admin.setDefaultImportProfileID(resultSet.getInt("default_import_profile_id"));
			admin.setPreferredListSize(resultSet.getInt("preferred_list_size"));

			// Read additional data

			admin.setCompany(companyDao.getCompany(admin.getCompanyID()));

			admin.setGroup(adminGroupDao.getAdminGroup(resultSet.getInt("admin_group_id")));

			List<Map<String, Object>> result = getSimpleJdbcTemplate().queryForList("SELECT security_token FROM admin_permission_tbl WHERE admin_id = ?", admin.getAdminID());

			Set<String> adminPermissions = new HashSet<String>();
			for (Map<String, Object> resultRow : result) {
				adminPermissions.add((String) resultRow.get("security_token"));
			}
			admin.setAdminPermissions(adminPermissions);
			return admin;
		}
	}

    public void setAdminGroupDao(AdminGroupDao adminGroupDao) {
        this.adminGroupDao = adminGroupDao;
    }

    public void setCompanyDao(CompanyDao companyDao) {
        this.companyDao = companyDao;
    }
}