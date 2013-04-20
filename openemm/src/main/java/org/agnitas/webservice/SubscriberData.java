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

/**
 * SubscriberData.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package org.agnitas.webservice;

public class SubscriberData  implements java.io.Serializable {
    private StringArrayType paramNames;
    private StringArrayType paramValues;
    private int customerID;

    public SubscriberData() {
    }

    public StringArrayType getParamNames() {
        return paramNames;
    }

    public void setParamNames(StringArrayType paramNames) {
        this.paramNames = paramNames;
    }

    public StringArrayType getParamValues() {
        return paramValues;
    }

    public void setParamValues(StringArrayType paramValues) {
        this.paramValues = paramValues;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SubscriberData)) return false;
        SubscriberData other = (SubscriberData) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.paramNames==null && other.getParamNames()==null) || 
             (this.paramNames!=null &&
              this.paramNames.equals(other.getParamNames()))) &&
            ((this.paramValues==null && other.getParamValues()==null) || 
             (this.paramValues!=null &&
              this.paramValues.equals(other.getParamValues()))) &&
            this.customerID == other.getCustomerID();
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getParamNames() != null) {
            _hashCode += getParamNames().hashCode();
        }
        if (getParamValues() != null) {
            _hashCode += getParamValues().hashCode();
        }
        _hashCode += getCustomerID();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SubscriberData.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:agnitas-webservice", "SubscriberData"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paramNames");
        elemField.setXmlName(new javax.xml.namespace.QName("", "paramNames"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:agnitas-webservice", "StringArrayType"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paramValues");
        elemField.setXmlName(new javax.xml.namespace.QName("", "paramValues"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:agnitas-webservice", "StringArrayType"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customerID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "customerID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
