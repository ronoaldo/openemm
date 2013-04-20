/**
 * ContentModuleLocation.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.agnitas.cms.webservices.generated;

public class ContentModuleLocation implements java.io.Serializable {
	private int cmTemplateId;

	private int contentModuleId;

	private java.lang.String dynName;

	private int id;

	private int mailingId;

	private int order;

	private int targetGroupId;

	public ContentModuleLocation() {
	}

	public ContentModuleLocation(
			int cmTemplateId,
			int contentModuleId,
			java.lang.String dynName,
			int id,
			int mailingId,
			int order,
			int targetGroupId) {
		this.cmTemplateId = cmTemplateId;
		this.contentModuleId = contentModuleId;
		this.dynName = dynName;
		this.id = id;
		this.mailingId = mailingId;
		this.order = order;
		this.targetGroupId = targetGroupId;
	}


	/**
	 * Gets the cmTemplateId value for this ContentModuleLocation.
	 *
	 * @return cmTemplateId
	 */
	public int getCmTemplateId() {
		return cmTemplateId;
	}


	/**
	 * Sets the cmTemplateId value for this ContentModuleLocation.
	 *
	 * @param cmTemplateId
	 */
	public void setCmTemplateId(int cmTemplateId) {
		this.cmTemplateId = cmTemplateId;
	}


	/**
	 * Gets the contentModuleId value for this ContentModuleLocation.
	 *
	 * @return contentModuleId
	 */
	public int getContentModuleId() {
		return contentModuleId;
	}


	/**
	 * Sets the contentModuleId value for this ContentModuleLocation.
	 *
	 * @param contentModuleId
	 */
	public void setContentModuleId(int contentModuleId) {
		this.contentModuleId = contentModuleId;
	}


	/**
	 * Gets the dynName value for this ContentModuleLocation.
	 *
	 * @return dynName
	 */
	public java.lang.String getDynName() {
		return dynName;
	}


	/**
	 * Sets the dynName value for this ContentModuleLocation.
	 *
	 * @param dynName
	 */
	public void setDynName(java.lang.String dynName) {
		this.dynName = dynName;
	}


	/**
	 * Gets the id value for this ContentModuleLocation.
	 *
	 * @return id
	 */
	public int getId() {
		return id;
	}


	/**
	 * Sets the id value for this ContentModuleLocation.
	 *
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}


	/**
	 * Gets the mailingId value for this ContentModuleLocation.
	 *
	 * @return mailingId
	 */
	public int getMailingId() {
		return mailingId;
	}


	/**
	 * Sets the mailingId value for this ContentModuleLocation.
	 *
	 * @param mailingId
	 */
	public void setMailingId(int mailingId) {
		this.mailingId = mailingId;
	}


	/**
	 * Gets the order value for this ContentModuleLocation.
	 *
	 * @return order
	 */
	public int getOrder() {
		return order;
	}


	/**
	 * Sets the order value for this ContentModuleLocation.
	 *
	 * @param order
	 */
	public void setOrder(int order) {
		this.order = order;
	}


	/**
	 * Gets the targetGroupId value for this ContentModuleLocation.
	 *
	 * @return targetGroupId
	 */
	public int getTargetGroupId() {
		return targetGroupId;
	}


	/**
	 * Sets the targetGroupId value for this ContentModuleLocation.
	 *
	 * @param targetGroupId
	 */
	public void setTargetGroupId(int targetGroupId) {
		this.targetGroupId = targetGroupId;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if(!(obj instanceof ContentModuleLocation)) return false;
		ContentModuleLocation other = (ContentModuleLocation) obj;
		if(obj == null) return false;
		if(this == obj) return true;
		if(__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true &&
				this.cmTemplateId == other.getCmTemplateId() &&
				this.contentModuleId == other.getContentModuleId() &&
				((this.dynName == null && other.getDynName() == null) ||
						(this.dynName != null &&
								this.dynName.equals(other.getDynName()))) &&
				this.id == other.getId() &&
				this.mailingId == other.getMailingId() &&
				this.order == other.getOrder() &&
				this.targetGroupId == other.getTargetGroupId();
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
		_hashCode += getCmTemplateId();
		_hashCode += getContentModuleId();
		if(getDynName() != null) {
			_hashCode += getDynName().hashCode();
		}
		_hashCode += getId();
		_hashCode += getMailingId();
		_hashCode += getOrder();
		_hashCode += getTargetGroupId();
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc =
			new org.apache.axis.description.TypeDesc(
					ContentModuleLocation.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://beans.mock.dataaccess.utils.cms.agnitas.org",
				"ContentModuleLocation"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("cmTemplateId");
		elemField.setXmlName(new javax.xml.namespace.QName("", "cmTemplateId"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("contentModuleId");
		elemField.setXmlName(
				new javax.xml.namespace.QName("", "contentModuleId"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("dynName");
		elemField.setXmlName(new javax.xml.namespace.QName("", "dynName"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("id");
		elemField.setXmlName(new javax.xml.namespace.QName("", "id"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("mailingId");
		elemField.setXmlName(new javax.xml.namespace.QName("", "mailingId"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("order");
		elemField.setXmlName(new javax.xml.namespace.QName("", "order"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("targetGroupId");
		elemField
				.setXmlName(new javax.xml.namespace.QName("", "targetGroupId"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(false);
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
