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

package org.agnitas.util;

import org.agnitas.beans.DatasourceDescription;
import org.agnitas.beans.ImportProfile;
import org.agnitas.dao.ImportRecipientsDao;
import org.agnitas.service.impl.DataType;
import org.agnitas.util.importvalues.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.mail.ByteArrayDataSource;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.validator.ValidatorResult;
import org.apache.commons.validator.ValidatorResults;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.HibernateTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author andrew May 12, 2009 2:38:15 PM
 */
public class ImportUtils {
    public static final List<String> HIDDEN_COLUMNS = Collections.unmodifiableList(Arrays.asList("customer_id", "change_date", "timestamp", "creation_date", "datasource_id"));

	public static final String MAIL_TYPE_HTML = "html";
	public static final String MAIL_TYPE_TEXT = "text";
	public static final String MAIL_TYPE_TEXT_ALT = "txt";

	public static final String MAIL_TYPE_DB_COLUMN = "mailtype";

    public static final String MAIL_TYPE_UNDEFINED = "0";
	public static final String MAIL_TYPE_DEFINED = "1";

    /**
     * Retrieves new Datasource-ID for newly imported Subscribers
     *
     * @param aContext
     * @return new Datasource-ID or 0
     */
    public static DatasourceDescription getNewDatasourceDescription(int companyID, String description, ApplicationContext aContext) {
        HibernateTemplate tmpl = new HibernateTemplate((SessionFactory) aContext.getBean("sessionFactory"));
        DatasourceDescription dsDescription = (DatasourceDescription) aContext.getBean("DatasourceDescription");

        dsDescription.setId(0);
        dsDescription.setCompanyID(companyID);
        dsDescription.setSourcegroupID(2);
        dsDescription.setCreationDate(new java.util.Date());
        dsDescription.setDescription(description);
        tmpl.save("DatasourceDescription", dsDescription);
        return dsDescription;
    }

    public static void createTemporaryTable(int adminID, int datasource_id, ImportProfile importProfile, String sessionId, ImportRecipientsDao importRecipientsDao) {
        importRecipientsDao.createTemporaryTable(adminID, datasource_id, importProfile.getKeyColumn(), importProfile.getKeyColumns(), importProfile.getCompanyId(), sessionId);
    }

    public static String describe(ImportProfile importProfile) {
        StringBuffer description = new StringBuffer();
        try {
            final Map beanProperties = BeanUtils.describe(importProfile);
            final Set set = beanProperties.keySet();
            for (Object keyObject : set) {
                final String key = (String) keyObject;
                String value = (String) beanProperties.get(key);
                if ("checkForDuplicates".equalsIgnoreCase(key)) {
                    value = CheckForDuplicates.getPublicValue(Integer.parseInt(value));
                }

                if ("charset".equalsIgnoreCase(key)) {
                    value = Charset.getValue(Integer.parseInt(value));
                }

                if ("nullValuesAction".equalsIgnoreCase(key)) {
                    value = NullValuesAction.getPublicValue(Integer.parseInt(value));
                }

                if ("importMode".equalsIgnoreCase(key)) {
                    value = ImportMode.getPublicValue(Integer.parseInt(value));
                }

                if ("textRecognitionChar".equalsIgnoreCase(key)) {
                    value = TextRecognitionChar.getValue(Integer.parseInt(value));
                }

                if ("dateFormat".equalsIgnoreCase(key)) {
                    value = DateFormat.getValue(Integer.parseInt(value));
                }

                if ("separator".equalsIgnoreCase(key)) {
                    value = Separator.getValue(Integer.parseInt(value));
                }

                if ("adminId".equalsIgnoreCase(key) || "companyId".equalsIgnoreCase(key) || "class".equalsIgnoreCase(key)) {
                    continue;
                }
                description.append(key).append(" = \"").append(value).append("\"\n");
            }
        } catch (Exception e) {
            AgnUtils.logger().warn("Can't describe importProfile", e);
        }

        return description.toString();
    }

    public static String describeMap(Map reportMap) {
        StringBuffer description = new StringBuffer();
        final Set set = reportMap.keySet();
        for (Object keyObject : set) {
            final String key = (String) keyObject;
            String value = (String) reportMap.get(key);
            description.append(key).append(" = \"").append(value).append("\"\n");
        }
        return description.toString();
    }

    public static Boolean checkIsCurrentFieldValid(ValidatorResults results, String propertyName) {
        // Get the result of validating the property.
        final ValidatorResult result = results.getValidatorResult(propertyName);
// Get all the actions run against the property, and iterate over their names.
        if (result == null) {
            return true;
        }
        Map actionMap = result.getActionMap();
        for (Object action : actionMap.keySet()) {
            String actName = (String) action;

            if (!result.isValid(actName)) {
                return false;
            }

        }
        return true;
    }


    public static Boolean checkIsCurrentFieldValid(ValidatorResults results, String propertyName, String actionName) {
        // Get the result of validating the property.
        final ValidatorResult result = results.getValidatorResult(propertyName);
// Get all the actions run against the property, and iterate over their names.
        if (result == null) {
            return true;
        }
        Map actionMap = result.getActionMap();
        for (Object action : actionMap.keySet()) {
            String actName = (String) action;

            if (!result.isValid(actName) && actName.equals(actionName)) {
                return false;
            }

        }
        return true;
    }

    public static Date getDateAsString(String value, int format) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormat.getValue(format));
        Date date = null;
        try {
            date = dateFormat.parse(value);
        } catch (ParseException e) {
            AgnUtils.logger().error("Can't parse data", e);
        }
        return date;
    }

    public static Object deserialiseBean(byte[] byeRecipient) {
        final ByteArrayInputStream f_in = new ByteArrayInputStream(byeRecipient);
        ObjectInputStream obj_in;
        Object object = null;
        try {
            obj_in = new ObjectInputStream(f_in);
            object = obj_in.readObject();
            IOUtils.closeQuietly(obj_in);
        } catch (Exception e) {
            AgnUtils.logger().error("Can't restore object from bytes", e);
        }
        return object;
    }

    public static byte[] getObjectAsBytes(Object recipient) {
        byte[] bytes = null;
        try {
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(arrayOutputStream);
            outputStream.writeObject(recipient);
            bytes = arrayOutputStream.toByteArray();
            IOUtils.closeQuietly(arrayOutputStream);
        } catch (IOException e) {
            AgnUtils.logger().error("Can't serialize object", e);
        }
        return bytes;
    }

    public static String dbtype2string(int type) {
        switch (type) {
            case java.sql.Types.BIGINT:
            case java.sql.Types.INTEGER:
            case java.sql.Types.SMALLINT:
                return DataType.INTEGER;

            case java.sql.Types.DECIMAL:
            case java.sql.Types.DOUBLE:
            case java.sql.Types.FLOAT:
            case java.sql.Types.NUMERIC:
            case java.sql.Types.REAL:
                return DataType.DOUBLE;

            case java.sql.Types.CHAR:
                return DataType.CHAR;

            case java.sql.Types.VARCHAR:
            case java.sql.Types.LONGVARCHAR:
            case java.sql.Types.CLOB:
                return DataType.VARCHAR;

            case java.sql.Types.DATE:
            case java.sql.Types.TIMESTAMP:
            case java.sql.Types.TIME:
                return DataType.DATE;
        }
        return "UNKNOWN(" + type + ")";
    }

    /**
     * Some pages have enctype="multipart/form-data" because they need to
     * upload file. With such enctype the Charset is corrupted. This method
     * fixes strings and make them UTF-8
     *
     * @param sourceStr source corrupted string
     * @return fixed string
     */
    public static String fixEncoding(String sourceStr) {
        try {
            return new String(sourceStr.getBytes("iso-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            AgnUtils.logger().error("Error during encoding converting", e);
            return sourceStr;
        }
    }

    /**
     * Send email with attachments
     */
    public static boolean sendEmailWithAttachments(String from, String to, String subject, String message, EmailAttachment[] attachments) {
    	return sendEmailWithAttachments(from, to, null, subject, message, attachments);
    }

    public static boolean sendEmailWithAttachments(String from, String fromName, String to, String subject, String message, EmailAttachment[] attachments) {
        boolean result = true;
        try {
            // Create the email message
            MultiPartEmail email = new MultiPartEmail();
            email.setCharset("UTF-8");
            email.setHostName(AgnUtils.getDefaultValue("system.mail.host"));
            email.addTo(to);

            if( fromName == null || fromName.equals(""))
            	email.setFrom(from);
            else
            	email.setFrom( from, fromName);

            email.setSubject(subject);
            email.setMsg(message);

            //bounces and reply forwarded to support@agnitas.de
            String replyName = AgnUtils.getDefaultValue( "import.report.replyTo.name");
            if( replyName == null || replyName.equals( ""))
            	email.addReplyTo( AgnUtils.getDefaultValue( "import.report.replyTo.address"));
            else
            	email.addReplyTo( AgnUtils.getDefaultValue( "import.report.replyTo.address"), replyName);

            email.setBounceAddress( AgnUtils.getDefaultValue( "import.report.bounce"));

            // Create and attach attachments
            for (EmailAttachment attachment : attachments) {
                ByteArrayDataSource dataSource = new ByteArrayDataSource(attachment.getData(), attachment.getType());
                email.attach(dataSource, attachment.getName(), attachment.getDescription());
            }

            // send the email
            email.send();
        } catch (Exception e) {
            AgnUtils.logger().error("sendEmailAttachment: " + e.getMessage());
            result = false;
        }
        return result;
    }

    /**
     * Checks if request parameters has parameter starting with this string
     *
     * @param request    request
     * @param paramStart beginning part of parameter name to check
     * @return true if such parameter exists
     */
    public static boolean hasParameterStartsWith(HttpServletRequest request, String paramStart) {
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = (String) parameterNames.nextElement();
            if (paramName.startsWith(paramStart)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasNoEmptyParameterStartsWith(HttpServletRequest request, String paramStart) {
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = (String) parameterNames.nextElement();
            if (paramName.startsWith(paramStart)) {
                boolean notEmpty = AgnUtils.parameterNotEmpty(request, paramName);
                if(notEmpty){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets value that is embedded into parameter name
     *
     * @param request        request
     * @param parameterStart parameter start string
     * @param parameterEnd   parameter end string
     * @return string from parameter name between parameterStart and parameterEnd
     */
    public static String getValueFromParameter(HttpServletRequest request, String parameterStart, String parameterEnd) {
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = (String) parameterNames.nextElement();
            if (paramName.startsWith(parameterStart) && paramName.endsWith(parameterEnd)) {
                String rest = paramName.substring(parameterStart.length());
                String value = rest.substring(0, rest.length() - parameterEnd.length());
                return value;
            }
        }
        return null;
    }

    public static String getNotEmptyValueFromParameter(HttpServletRequest request, String parameterStart) {
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = (String) parameterNames.nextElement();
            if (paramName.startsWith(parameterStart)) {
                if (AgnUtils.parameterNotEmpty(request, paramName)) {
                    String value = request.getParameter(paramName);
                    return value;
                }
            }
        }
        return null;
    }

    public static int getBooleanAsInt(boolean extendedEmailCheck) {
        return (extendedEmailCheck ? 1 : 0);
    }

    public static void removeHiddenColumns(Map dbColumns) {
        for (String hiddenColumn : HIDDEN_COLUMNS) {
            dbColumns.remove(hiddenColumn);
		}
	}
}
