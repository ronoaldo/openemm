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

package org.agnitas.service.csv;

import org.agnitas.util.AgnUtils;
import org.apache.commons.beanutils.PropertyUtils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.text.MessageFormat;
import java.util.Map;

/**
 * General-purpose utilities for Bean Builder.
 *
 * @author Viktor Gema
 */
public abstract class Toolkit {

    /**
     * Returns a property descriptor for a named property of a bean.
     *
     * @param bean         The bean to retrieve the property descriptor for
     * @param propertyName The name of the property to find.
     * @return A <code>PropertyDescriptor</code> for the named property or <code>null</code> if no matching property could
     *         be found
     * @throws IntrospectionException
     */
    public static PropertyDescriptor getPropertyDescriptor(Object bean, String propertyName) throws IntrospectionException {
        if ((bean == null) || (propertyName == null)) {
            return null;
        }

        PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            if (propertyName.equals(propertyDescriptor.getName())) {
                return propertyDescriptor;
            }
        }
        //if property description doesn't found return customFields HashMap
        try {
            return PropertyUtils.getPropertyDescriptor(bean, "customFields");
        } catch (Exception e) {
            AgnUtils.logger().error("Retriving of bean descriptor failed", e);
        }

        return null;
    }

    public static String getValueFromBean(Object bean, String fieldName) {
        try {
            PropertyDescriptor propertyDescriptor = getPropertyDescriptor(bean, fieldName);
            // This should never be null as it would be caused by a node representing an unknown property being popped whereas it
            // should never have been pushed in the first place!
            if (propertyDescriptor.getName().equals("customFields")) {
                final Map<String, String> customCollumnMappig = (Map<String, String>) propertyDescriptor.getReadMethod().invoke(bean);
                return customCollumnMappig.get(fieldName);
            } else {
                return (String) propertyDescriptor.getReadMethod().invoke(bean);
            }
        } catch (Exception e) {
            AgnUtils.logger().warn(MessageFormat.format("Failed to get bean ({0}) property ({1}) value", bean, fieldName), e);
            return null;
        }
    }

    public static void setValueFromBean(Object bean, String fieldName, String value) {
        try {
            PropertyDescriptor propertyDescriptor = getPropertyDescriptor(bean, fieldName);
            // This should never be null as it would be caused by a node representing an unknown property being popped whereas it
            // should never have been pushed in the first place!
            if (propertyDescriptor.getName().equals("customFields")) {
                final Map<String, String> customCollumnMappig = (Map<String, String>) propertyDescriptor.getReadMethod().invoke(bean);
                customCollumnMappig.put(fieldName, value);
            } else {
                propertyDescriptor.getWriteMethod().invoke(bean, value);
            }
        } catch (Exception e) {
            AgnUtils.logger().warn(MessageFormat.format("Failed to set bean ({0}) property {1} with value {2}", bean, fieldName, value), e);
		}
	}
}
