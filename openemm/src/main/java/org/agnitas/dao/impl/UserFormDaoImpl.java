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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.agnitas.beans.UserForm;
import org.agnitas.beans.impl.UserFormImpl;
import org.agnitas.dao.UserFormDao;
import org.agnitas.util.AgnUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * 
 * @author mhe
 */
public class UserFormDaoImpl extends BaseHibernateDaoImpl implements UserFormDao {
	private static final transient Logger logger = Logger.getLogger(UserFormDaoImpl.class);
	
	@SuppressWarnings("unchecked")
	@Override
	public UserForm getUserForm(int formID, int companyID) {
		if (formID == 0 || companyID == 0) {
			return null;
		} else {
			return (UserForm) AgnUtils.getFirstResult(new HibernateTemplate(sessionFactory).find("from UserForm where id = ? and companyID = ?", new Object[] { new Integer(formID), new Integer(companyID) }));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public UserForm getUserFormByName(String name, int companyID) {
		if (name == null || companyID == 0) {
			return null;
		} else {
			return (UserForm) AgnUtils.getFirstResult(new HibernateTemplate(sessionFactory).find("from UserForm where formName = ? and companyID = ?", new Object[] { name, new Integer(companyID) }));
		}
	}

	@Override
	public List<UserForm> getUserForms(int companyID) {
		return select(logger, "SELECT form_id, company_id, formname, description FROM userform_tbl WHERE company_id = ? ORDER BY formname", new UserForm_Light_RowMapper(), companyID);
	}

	@Override
	public int storeUserForm(UserForm form) {
		if (form == null || form.getCompanyID() == 0) {
			return 0;
		} else {
			HibernateTemplate tmpl = new HibernateTemplate(sessionFactory);
			tmpl.saveOrUpdate("UserForm", form);
			tmpl.flush();
			return form.getId();
		}
	}

	@Override
	public boolean deleteUserForm(int formID, int companyID) {
		UserForm existingUserForm = getUserForm(formID, companyID);
		if (existingUserForm != null) {
			try {
				HibernateTemplate tmpl = new HibernateTemplate(sessionFactory);
				tmpl.delete(existingUserForm);
				tmpl.flush();
				return true;
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}
	}
	
	private class UserForm_Light_RowMapper implements ParameterizedRowMapper<UserForm> {
		@Override
		public UserForm mapRow(ResultSet resultSet, int row) throws SQLException {
			UserForm readUserForm = new UserFormImpl();
			readUserForm.setId(resultSet.getInt("form_id"));
			readUserForm.setCompanyID(resultSet.getInt("company_id"));
			readUserForm.setFormName(resultSet.getString("formname"));
			readUserForm.setDescription(resultSet.getString("description"));
			return readUserForm;
		}
	}
}
