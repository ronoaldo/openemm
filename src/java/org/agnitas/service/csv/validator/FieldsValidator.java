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
package org.agnitas.service.csv.validator;

import java.io.UnsupportedEncodingException;
import org.agnitas.beans.ColumnMapping;
import org.agnitas.beans.ImportProfile;
import org.agnitas.beans.Recipient;
import org.agnitas.service.NewImportWizardService;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.ImportUtils;
import org.agnitas.util.importvalues.DateFormat;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.util.ValidatorUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * @author Viktor Gema
 */
public class FieldsValidator {

    private static final transient Logger logger = Logger.getLogger(FieldsValidator.class);
    public static final String DB_CHARSET = "UTF-8";

    /**
     * Checks if the field is mandatory.
     *
     * @return boolean If the field isn't <code>null</code> and
     *         has a length greater than zero, <code>true</code> is returned.
     *         Otherwise <code>false</code>.
     */
    public static boolean validateMandatoryField(Object bean, Field field, Validator validator) {
        String value = getValueAsString(bean, field);
        final ImportProfile profile = (ImportProfile) validator.getParameterValue("org.agnitas.beans.ImportProfile");
        final ColumnMapping currentColumn = getColumnMappingForCurentField(field, profile);
        return !(currentColumn != null && currentColumn.getMandatory()) || !GenericValidator.isBlankOrNull(value);
    }

    private static String getValueAsString(Object bean, Field field) {
        String value = null;
        final String varValue = field.getVarValue("customField");
        if (Boolean.valueOf(varValue)) {
            try {
                Object property = PropertyUtils.getProperty(bean, "customFields");
                if (property instanceof Map) {
                    value = ((Map<String, String>) property).get(field.getProperty());
                }
            } catch (Exception e) {
                AgnUtils.logger().warn(MessageFormat.format("Failed to get bean ({0}) property ({1})as string", bean, varValue), e);
            }
        } else {
            value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        }
        return value;
    }

    private static ColumnMapping getColumnMappingForCurentField(Field field, ImportProfile profile) {
        ColumnMapping currentColumn = null;
        final List<ColumnMapping> columnMapping = profile.getColumnMapping();
        for (ColumnMapping column : columnMapping) {
            if (column.getDatabaseColumn().equals(field.getProperty())) {
                currentColumn = column;
                break;
            }
        }
        return currentColumn;
    }

    /**
     * Checks if the field is an e-mail address.
     *
     * @param value The value validation is being performed on.
     * @return boolean        If the field is an e-mail address
     *         <code>true</code> is returned.
     *         Otherwise <code>false</code>.
     */
    public static boolean validateEmail(Object bean, Field field, Validator validator) {
        String value = getValueAsString(bean, field);
        final ImportProfile profile = (ImportProfile) validator.getParameterValue("org.agnitas.beans.ImportProfile");
        final ColumnMapping currentColumn = getColumnMappingForCurentField(field, profile);
        if (currentColumn != null && currentColumn.getMandatory()) {
            return !GenericValidator.isBlankOrNull(value) && GenericValidator.isEmail(value);
        } else {
            return currentColumn == null || GenericValidator.isEmail(value);
        }

    }

    /**
     * Checks if the field is an e-mail address.
     *
     * @param value The value validation is being performed on.
     * @return boolean        If the field is an e-mail address
     *         <code>true</code> is returned.
     *         Otherwise <code>false</code>.
     */
    public static boolean validateGender(Object bean, Field field, Validator validator) {
        String value = getValueAsString(bean, field);
        final ImportProfile profile = (ImportProfile) validator.getParameterValue("org.agnitas.beans.ImportProfile");
        Integer maxGenderValue = (Integer) validator.getParameterValue("java.lang.Integer");
        if(maxGenderValue == null || maxGenderValue == 0){
            maxGenderValue = NewImportWizardService.MAX_GENDER_VALUE_BASIC;
        }
        final ColumnMapping currentColumn = getColumnMappingForCurentField(field, profile);
        if (currentColumn != null && currentColumn.getMandatory()) {
            return !GenericValidator.isBlankOrNull(value) && genderMappingValidation(value, profile, maxGenderValue);
        } else {
            return currentColumn == null || genderMappingValidation(value, profile, maxGenderValue);
        }
    }

    private static boolean genderMappingValidation(String value, ImportProfile profile, Integer maxGenderValue) {
        final Map<String, Integer> genderMap = profile.getGenderMapping();
        return GenericValidator.isInt(value) && Integer.valueOf(value) <= maxGenderValue && Integer.valueOf(value) >= 0 || genderMap.keySet().contains(value);
    }

    /**
     * Checks if the field can be successfully converted to a <code>int</code>.
     *
     * @param value The value validation is being performed on.
     * @return boolean If the field can be successfully converted
     *         to a <code>int</code> <code>true</code> is returned.
     *         Otherwise <code>false</code>.
     */
    public static boolean validateInt(Object bean, Field field, Validator validator) {
        String value = getValueAsString(bean, field);
        final ImportProfile profile = (ImportProfile) validator.getParameterValue("org.agnitas.beans.ImportProfile");
        final ColumnMapping currentColumn = getColumnMappingForCurentField(field, profile);
        if (currentColumn != null && currentColumn.getMandatory()) {
            return !GenericValidator.isBlankOrNull(value) && GenericValidator.isInt(value);
        } else {
            return currentColumn == null || GenericValidator.isInt(value);
        }
    }

    /**
	 * Checks the mailtype value
	 *
	 * @param mailtype the mailtype value
	 * @return true if mailtype is a number from 0 to 2 or mailtype is one
	 * of the strings: "txt", "text", "html" (the letter case is not important). False otherwise
	 */
	private static boolean isValidMailTypeValue(String mailtype) {
		if(GenericValidator.isInt(mailtype)) {
            int mailTypeInt = Integer.valueOf(mailtype);
			return mailTypeInt == Recipient.MAILTYPE_TEXT || mailTypeInt == Recipient.MAILTYPE_HTML ||
                   mailTypeInt == Recipient.MAILTYPE_HTML_OFFLINE || mailTypeInt == Recipient.MAILTYPE_MHTML;
		} else {
			String mailtypeLower = mailtype.toLowerCase();
			return (mailtypeLower.equals(ImportUtils.MAIL_TYPE_HTML) || 
					mailtypeLower.equals(ImportUtils.MAIL_TYPE_TEXT)
					|| mailtypeLower.equals(ImportUtils.MAIL_TYPE_TEXT_ALT));
		}
	}

    /**
     * Checks if the field can be successfully converted to a <code>int</code>.
     *
     * @param value The value validation is being performed on.
     * @return boolean If the field can be successfully converted
     *         to a <code>int</code> <code>true</code> is returned.
     *         Otherwise <code>false</code>.
     */
    public static boolean validateMailType(Object bean, Field field, Validator validator) {
        String value = getValueAsString(bean, field);
        final ImportProfile profile = (ImportProfile) validator.getParameterValue("org.agnitas.beans.ImportProfile");
        final ColumnMapping currentColumn = getColumnMappingForCurentField(field, profile);
        if (currentColumn != null && currentColumn.getMandatory()) {
            return !GenericValidator.isBlankOrNull(value) && isValidMailTypeValue(value);
        } else {
            return currentColumn == null || isValidMailTypeValue(value);
        }
    }

    public static boolean checkRange(Object bean, Field field, Validator validator) {
        final String value = getValueAsString(bean, field);
        final ImportProfile profile = (ImportProfile) validator.getParameterValue("org.agnitas.beans.ImportProfile");
        final ColumnMapping currentColumn = getColumnMappingForCurentField(field, profile);
        final Integer length = Integer.valueOf(field.getVarValue("maxLength"));
        if (currentColumn != null && currentColumn.getMandatory()) {
            return !GenericValidator.isBlankOrNull(value) && checkMaxStringLength(value, length);
        } else {
            return currentColumn == null || checkMaxStringLength(value, length);
        }
    }

    private static boolean checkMaxStringLength(String value, int length) {
        boolean dbEncodingLengthOk = true;
        if (value != null) {
            try {
                dbEncodingLengthOk = value.getBytes(DB_CHARSET).length <= length;
            } catch (UnsupportedEncodingException e) {
                logger.error("Error during import maxlength validation (encoding not supported): " + e + "\n" + AgnUtils.getStackTrace(e));
            }
        }
        return GenericValidator.maxLength(value, length) && dbEncodingLengthOk;
    }

    /**
     * Checks if the field can be successfully converted to a <code>double</code>.
     *
     * @param value The value validation is being performed on.
     * @return boolean If the field can be successfully converted
     *         to a <code>double</code> <code>true</code> is returned.
     *         Otherwise <code>false</code>.
     */
    public static boolean validateDouble(Object bean, Field field, Validator validator) {
        String value = getValueAsString(bean, field);
        final ImportProfile profile = (ImportProfile) validator.getParameterValue("org.agnitas.beans.ImportProfile");
        final ColumnMapping currentColumn = getColumnMappingForCurentField(field, profile);
        if (currentColumn != null && currentColumn.getMandatory()) {
            return !GenericValidator.isBlankOrNull(value) && GenericValidator.isDouble(value);
        } else {
            return currentColumn == null || GenericValidator.isDouble(value);
        }
    }

    /**
     * Checks if the field can be successfully converted to a <code>date</code>.
     *
     * @param value The value validation is being performed on.
     * @return boolean If the field can be successfully converted
     *         to a <code>date</code> <code>true</code> is returned.
     *         Otherwise <code>false</code>.
     */
    public static boolean validateDate(Object bean, Field field, Validator validator) {
        String value = getValueAsString(bean, field);
        final ImportProfile profile = (ImportProfile) validator.getParameterValue("org.agnitas.beans.ImportProfile");
        final ColumnMapping currentColumn = getColumnMappingForCurentField(field, profile);
        if (currentColumn != null && currentColumn.getMandatory()) {
            return !GenericValidator.isBlankOrNull(value) && GenericValidator.isDate(value, DateFormat.getValue(profile.getDateFormat()), true);
        } else {
            return currentColumn == null || GenericValidator.isDate(value, DateFormat.getValue(profile.getDateFormat()), true);
        }
    }
}
