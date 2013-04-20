/**
 * CmsTag.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.agnitas.cms.webservices.generated;

public class CmsTag implements java.io.Serializable {
	private java.lang.String name;

	private int type;

	private java.lang.String value;

	public CmsTag() {
	}

	public CmsTag(
			java.lang.String name,
			int type,
			java.lang.String value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}


	/**
	 * Gets the name value for this CmsTag.
	 *
	 * @return name
	 */
	public java.lang.String getName() {
		return name;
	}


	/**
	 * Sets the name value for this CmsTag.
	 *
	 * @param name
	 */
	public void setName(java.lang.String name) {
		this.name = name;
	}


	/**
	 * Gets the type value for this CmsTag.
	 *
	 * @return type
	 */
	public int getType() {
		return type;
	}


	/**
	 * Sets the type value for this CmsTag.
	 *
	 * @param type
	 */
	public void setType(int type) {
		this.type = type;
	}


	/**
	 * Gets the value value for this CmsTag.
	 *
	 * @return value
	 */
	public java.lang.String getValue() {
		return value;
	}


	/**
	 * Sets the value value for this CmsTag.
	 *
	 * @param value
	 */
	public void setValue(java.lang.String value) {
		this.value = value;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if(!(obj instanceof CmsTag)) return false;
		CmsTag other = (CmsTag) obj;
		if(obj == null) return false;
		if(this == obj) return true;
		if(__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true &&
				((this.name == null && other.getName() == null) ||
						(this.name != null &&
								this.name.equals(other.getName()))) &&
				this.type == other.getType() &&
				((this.value == null && other.getValue() == null) ||
						(this.value != null &&
								this.value.equals(other.getValue())));
		__equalsCalc = null;
		return _equals;
	}

	private boolean __hashCodeCalc = false;

	public synchronized int hashCode() {
		if(__hashCodeCalc) {
			return 0;
		}
		__hashCodeCalc = true;
		int _hashCode = 1;
		if(getName() != null) {
			_hashCode += getName().hashCode();
		}
		_hashCode += getType();
		if(getValue() != null) {
			_hashCode += getValue().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc =
			new org.apache.axis.description.TypeDesc(CmsTag.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://beans.mock.dataaccess.utils.cms.agnitas.org",
				"CmsTag"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("name");
		elemField.setXmlName(new javax.xml.namespace.QName("", "name"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("type");
		elemField.setXmlName(new javax.xml.namespace.QName("", "type"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("value");
		elemField.setXmlName(new javax.xml.namespace.QName("", "value"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
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
				new org.apache.axis.encoding.ser.BeanSerializer(
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
				new org.apache.axis.encoding.ser.BeanDeserializer(
						_javaType, _xmlType, typeDesc);
	}

}
