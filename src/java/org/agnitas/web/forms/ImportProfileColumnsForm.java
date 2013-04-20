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
 * the code written by AGNITAS AG are Copyright (c) 2009 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/

package org.agnitas.web.forms;

import org.agnitas.beans.ColumnMapping;
import org.agnitas.beans.ImportProfile;
import org.agnitas.util.ImportUtils;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.ImportProfileAction;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Vyacheslav Stepanov
 */
public class ImportProfileColumnsForm extends ImportBaseFileForm {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2073563662277194710L;

	protected int action;

    protected int profileId;

    protected ImportProfile profile;

    protected String[] dbColumns;

    protected String newColumn;

	protected Map<String, String> dbColumnsDefaults;

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public ImportProfile getProfile() {
        return profile;
    }

    public void setProfile(ImportProfile profile) {
        this.profile = profile;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String[] getDbColumns() {
        return dbColumns;
    }

    public void setDbColumns(String[] dbColumns) {
        this.dbColumns = dbColumns;
    }

    public String getNewColumn() {
        return newColumn;
    }

    public void setNewColumn(String newColumn) {
        this.newColumn = ImportUtils.fixEncoding(newColumn);
    }

    public void resetFormData() {
        profile = null;
        newColumn = "";
        dbColumns = new String[0];
		dbColumnsDefaults = new HashMap<String, String>();
    }

    @Override
    public ActionErrors formSpecificValidate(ActionMapping actionMapping, HttpServletRequest request) {
        ActionErrors errors = super.formSpecificValidate(actionMapping, request);
        if (errors == null) {
            errors = new ActionErrors();
        }
        storeMappings(request);
        if (action == ImportProfileAction.ACTION_SAVE &&
                !ImportUtils.hasNoEmptyParameterStartsWith(request, "removeMapping")) {
            if (AgnUtils.parameterNotEmpty(request, "add")) {
                if (StringUtils.isEmpty(newColumn)) {
                    errors.add("newColumn", new ActionMessage("error.import.column.empty"));
                } else if (columnExists(newColumn, profile.getColumnMapping())) {
                    errors.add("newColumn", new ActionMessage("error.import.column.duplicate"));
                }
            } else {
                Set<String> dbColumnSet = new HashSet<String>();
                List<ColumnMapping> mappings = profile.getColumnMapping();
                for (ColumnMapping mapping : mappings) {
                    if (!ColumnMapping.DO_NOT_IMPORT.equals(mapping.getDatabaseColumn())) {
                        if (dbColumnSet.contains(mapping.getDatabaseColumn())) {
                            errors.add("mapping_" + mapping.getFileColumn(),
                                    new ActionMessage("error.import.column.dbduplicate"));
                        }
                        dbColumnSet.add(mapping.getDatabaseColumn());
                    }
                }
            }
        }
        return errors;
    }

    private void storeMappings(HttpServletRequest request) {
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = (String) parameterNames.nextElement();
            if (name.startsWith("dbColumn_")) {
                int index = Integer.valueOf(name.substring(9));
                String dbColumn = request.getParameter(name);
                boolean mandatory = request.getParameter("mandatory_" + index) != null;
                String defaultValue = ImportUtils.fixEncoding(request.getParameter("default_value_" + index));
                ColumnMapping mapping = profile.getColumnMapping().get(index);
                mapping.setDatabaseColumn(dbColumn);
                mapping.setMandatory(mandatory);
                mapping.setDefaultValue(defaultValue);

                if (ImportUtils.HIDDEN_COLUMNS.contains(dbColumn)) {
                    mapping.setDatabaseColumn(ColumnMapping.DO_NOT_IMPORT);
                }
            }
        }
    }

    public ColumnMapping getMappingByFileColumn(String csvColumn, List<ColumnMapping> columnMappings) {
        for (ColumnMapping columnMapping : columnMappings) {
            if (csvColumn.equals(columnMapping.getFileColumn())) {
                return columnMapping;
            }
        }
        return null;
    }

    public boolean columnExists(String csvColumn, List<ColumnMapping> columnMappings) {
        for (ColumnMapping columnMapping : columnMappings) {
            if (csvColumn.equals(columnMapping.getFileColumn())) {
                return true;
            }
        }
        return false;
    }

    public int getMappingNumber() {
        return profile.getColumnMapping().size();
    }

	public Map<String, String> getDbColumnsDefaults() {
		return dbColumnsDefaults;
	}

	public void setDbColumnsDefaults(Map<String, String> dbColumnsDefaults) {
		this.dbColumnsDefaults = dbColumnsDefaults;
	}
}