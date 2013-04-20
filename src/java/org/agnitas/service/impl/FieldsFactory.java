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

package org.agnitas.service.impl;

import org.agnitas.beans.ColumnMapping;
import org.agnitas.beans.ImportProfile;
import org.agnitas.dao.ImportRecipientsDao;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.Form;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * @author Andrey Polyakov
 */
public class FieldsFactory implements Serializable {
    public static final String CUSTOM_FIELD = "customField";
    public static final Map<String, String> mTypeColums = new LinkedHashMap <String, String>();

    public CSVColumnState[] createColumnHeader(String[] row, ImportProfile profile) {
        final CSVColumnState[] states = new CSVColumnState[row.length];
        for (int i = 0; i < row.length; i++) {
            String headerName = row[i];
            final String columnNameByCvsFileName = getDBColumnNameByCvsFileName(headerName, profile);
            if (columnNameByCvsFileName != null) {
                states[i] = new CSVColumnState();
                states[i].setColName(columnNameByCvsFileName);
                states[i].setImportedColumn(true);
            } else {
                states[i] = new CSVColumnState();
                states[i].setColName(headerName);
                states[i].setImportedColumn(false);
            }
        }
        return states;
    }

    public String getDBColumnNameByCvsFileName(String headerName, ImportProfile profile) {
        final List<ColumnMapping> columnMappingList = profile.getColumnMapping();
        for (ColumnMapping columnMapping : columnMappingList) {
            if (columnMapping.getFileColumn().equals(headerName) && !columnMapping.getDatabaseColumn().equals(ColumnMapping.DO_NOT_IMPORT)) {
                return columnMapping.getDatabaseColumn();
            }
        }
        return null;
    }

    public void createRulesForCustomFields(CSVColumnState[] columns, Form form, ImportRecipientsDao importRecipientsDao, ImportProfile importProfile) {
        for (CSVColumnState column : columns) {
            if (!column.getImportedColumn()) {
                continue;
            }

            final String colName = column.getColName();
            Map<String, Object> columnInfo =
                    importRecipientsDao.getColumnInfoByColumnName(
                            importProfile.getCompanyId(),
                            colName).get(colName);
            final String typeOfCustomColumn = (String) columnInfo.get(ImportRecipientsDao.TYPE);

            final Field field = new Field();
            field.setProperty(colName);
            mTypeColums.put(colName,typeOfCustomColumn);
            if ("email".equals(colName)) {
                column.setType(CSVColumnState.TYPE_CHAR);
                field.setDepends("mandatory,checkRange,email");
                field.addVar("maxLength", "100", null);
            } else if ("gender".equals(colName)) {
                column.setType(CSVColumnState.TYPE_NUMERIC);
                field.setDepends("mandatory,gender");
            } else if ("mailtype".equals(colName)) {
                column.setType(CSVColumnState.TYPE_NUMERIC);
                field.setDepends("mandatory,mailType");
            } else if ("firstname".equals(colName)) {
                column.setType(CSVColumnState.TYPE_CHAR);
                field.setDepends("mandatory,checkRange");
                field.addVar("maxLength", "100", null);
            } else if ("lastname".equals(colName)) {
                column.setType(CSVColumnState.TYPE_CHAR);
                field.setDepends("mandatory,checkRange");
                field.addVar("maxLength", "100", null);
            } else if ("title".equals(colName)) {
                column.setType(CSVColumnState.TYPE_CHAR);
                field.setDepends("mandatory,checkRange");
                field.addVar("maxLength", "100", null);
            } else if (typeOfCustomColumn.equals(DataType.INTEGER)) {
                field.setDepends("mandatory,int");
                markFieldAsCustom(field);
                column.setType(CSVColumnState.TYPE_NUMERIC);
            } else if (typeOfCustomColumn.equals(DataType.DOUBLE)) {
                field.setDepends("mandatory,validateDouble");
                markFieldAsCustom(field);
                column.setType(CSVColumnState.TYPE_NUMERIC);
            } else if (typeOfCustomColumn.equals(DataType.CHAR)) {
                field.setDepends("mandatory,checkRange");
                field.addVar("maxLength", "1", null);
                markFieldAsCustom(field);
                column.setType(CSVColumnState.TYPE_CHAR);
            } else if (typeOfCustomColumn.equals(DataType.VARCHAR)) {
                field.setDepends("mandatory,checkRange");
                final Integer maxLength = (Integer) columnInfo.get("length");
                field.addVar("maxLength", String.valueOf(maxLength), null);
                markFieldAsCustom(field);
                column.setType(CSVColumnState.TYPE_CHAR);
            } else if (typeOfCustomColumn.equals(DataType.DATE)) {
                field.setDepends("mandatory,date");
                markFieldAsCustom(field);
                column.setType(CSVColumnState.TYPE_DATE);
            }

            form.addField(field);
        }
    }

    private void markFieldAsCustom(Field field) {
        field.addVar(CUSTOM_FIELD, Boolean.TRUE.toString(), null);
    }
}
