package org.agnitas.beans.impl;


import org.agnitas.beans.ImportProfile;
import org.agnitas.beans.ProfileRecipientFields;
import org.agnitas.service.csv.Toolkit;
import org.agnitas.service.impl.CSVColumnState;
import org.agnitas.util.ImportUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.sql.Timestamp;
import java.util.*;

public class ImportKeyColumnsKey {

    public static final String KEY_COLUMN_PREFIX = "key_column_";

    private Map<String, CSVColumnState> keyColumnsMap;
    private Map<String, Object> keyColumnsValues;
    private Map<String, String> rawValues;

    private ImportKeyColumnsKey() {
        keyColumnsMap = new HashMap<String, CSVColumnState>();
        keyColumnsValues = new HashMap<String, Object>();
        rawValues = new HashMap<String, String>();
    }

    public static ImportKeyColumnsKey createInstance(final ImportProfile profile, final ProfileRecipientFields recipientFields, final CSVColumnState[] columns) {
        ImportKeyColumnsKey key = new ImportKeyColumnsKey();
        List<String> keyColumns = profile.getKeyColumns();
        String keyColumn = profile.getKeyColumn();
        for (CSVColumnState column : columns) {
            String colName = column.getColName();
            if (keyColumns.isEmpty() && colName.equals(keyColumn) && column.getImportedColumn()) {
                key.getKeyColumnsMap().put(colName, column);
                String value = Toolkit.getValueFromBean(recipientFields, profile.getKeyColumn());
                value = value.toLowerCase();
                key.getRawValues().put(colName, value);
                key.getKeyColumnsValues().put(colName, formatValue(value, profile, column.getType()));
            } else if (keyColumns.contains(colName) && column.getImportedColumn()) {
                key.getKeyColumnsMap().put(colName, column);
                String value = Toolkit.getValueFromBean(recipientFields, colName);
                value = value.toLowerCase();
                key.getRawValues().put(colName, value);
                key.getKeyColumnsValues().put(colName, formatValue(value, profile, column.getType()));
            }
        }
        return key;
    }

    public static ImportKeyColumnsKey createInstance(Map row) {
        ImportKeyColumnsKey key = new ImportKeyColumnsKey();
        Iterator<String> iterator = row.keySet().iterator();
        while (iterator.hasNext()) {
            String dbColumn = iterator.next();
            if(dbColumn.equalsIgnoreCase("customer_id"))
                continue;
            String columnName = dbColumn.substring(KEY_COLUMN_PREFIX.length()).toLowerCase();
            Object dbValue = row.get(dbColumn) != null ? row.get(dbColumn): "";
            String value = String.valueOf(dbValue).toLowerCase();
            key.getRawValues().put(columnName, value);
            key.getKeyColumnsValues().put(columnName, value);
        }
        return key;
    }

    private static Object formatValue(String rawValue, ImportProfile profile, int type) {
        Object value = null;
        if (type == CSVColumnState.TYPE_CHAR) {
            value = rawValue;
        } else if (type == CSVColumnState.TYPE_NUMERIC) {
            value = Double.valueOf(rawValue);
        } else {
            Date date = ImportUtils.getDateAsString(rawValue, profile.getDateFormat());
            final Date sqlDate = new Timestamp(date.getTime());
            value = String.valueOf(sqlDate.toString());
        }
        return value;
    }

    public Map<String, CSVColumnState> getKeyColumnsMap() {
        return keyColumnsMap;
    }

    public Map<String, Object> getKeyColumnsValues() {
        return keyColumnsValues;
    }

    public Map<String, String> getRawValues() {
        return rawValues;
    }

    public void addParameters(Map<String, List<Object>> parameters) {
        Iterator<String> iterator = keyColumnsValues.keySet().iterator();
        while (iterator.hasNext()) {
            String columnName = iterator.next();
            Object columnValue = keyColumnsValues.get(columnName);
            List<Object> objectList = parameters.get(columnName);
            if(objectList == null){
                objectList = new ArrayList<Object>();
                parameters.put(columnName, objectList);
            }
            objectList.add(columnValue);
        }
    }

    public String getParametersString() {
        return " ?,";
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        for(String rawValue : rawValues.values()){
            hashCodeBuilder.append(rawValue);
        }
        return hashCodeBuilder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != getClass())
            return false;

        ImportKeyColumnsKey key = (ImportKeyColumnsKey) obj;
        return this.getRawValues().equals(key.getRawValues());
    }

}
