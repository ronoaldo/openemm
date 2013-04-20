/**
 * ContentModuleType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.agnitas.cms.webservices.generated;

public class ContentModuleType implements java.io.Serializable {
	private int companyId;

	private java.lang.String content;

	private java.lang.String description;

	private int id;

	private boolean isPublic;

	private java.lang.String name;

	private boolean readOnly;

	public ContentModuleType() {
	}

	public ContentModuleType(
			int companyId,
			java.lang.String content,
			java.lang.String description,
			int id,
			boolean isPublic,
			java.lang.String name,
			boolean readOnly) {
		this.companyId = companyId;
		this.content = content;
		this.description = description;
		this.id = id;
		this.isPublic = isPublic;
		this.name = name;
		this.readOnly = readOnly;
	}


	/**
	 * Gets the companyId value for this ContentModuleType.
	 *
	 * @return companyId
	 */
	public int getCompanyId() {
		return companyId;
	}


	/**
	 * Sets the companyId value for this ContentModuleType.
	 *
	 * @param companyId
	 */
	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}


	/**
	 * Gets the content value for this ContentModuleType.
	 *
	 * @return content
	 */
	public java.lang.String getContent() {
		return content;
	}


	/**
	 * Sets the content value for this ContentModuleType.
	 *
	 * @param content
	 */
	public void setContent(java.lang.String content) {
		this.content = content;
	}


	/**
	 * Gets the description value for this ContentModuleType.
	 *
	 * @return description
	 */
	public java.lang.String getDescription() {
		return description;
	}


	/**
	 * Sets the description value for this ContentModuleType.
	 *
	 * @param description
	 */
	public void setDescription(java.lang.String description) {
		this.description = description;
	}


	/**
	 * Gets the id value for this ContentModuleType.
	 *
	 * @return id
	 */
	public int getId() {
		return id;
	}


	/**
	 * Sets the id value for this ContentModuleType.
	 *
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}


	/**
	 * Gets the isPublic value for this ContentModuleType.
	 *
	 * @return isPublic
	 */
	public boolean isIsPublic() {
		return isPublic;
	}


	/**
	 * Sets the isPublic value for this ContentModuleType.
	 *
	 * @param isPublic
	 */
	public void setIsPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}


	/**
	 * Gets the name value for this ContentModuleType.
	 *
	 * @return name
	 */
	public java.lang.String getName() {
		return name;
	}


	/**
	 * Sets the name value for this ContentModuleType.
	 *
	 * @param name
	 */
	public void setName(java.lang.String name) {
		this.name = name;
	}


	/**
	 * Gets the readOnly value for this ContentModuleType.
	 *
	 * @return readOnly
	 */
	public boolean isReadOnly() {
		return readOnly;
	}


	/**
	 * Sets the readOnly value for this ContentModuleType.
	 *
	 * @param readOnly
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if(!(obj instanceof ContentModuleType)) return false;
		ContentModuleType other = (ContentModuleType) obj;
		if(obj == null) return false;
		if(this == obj) return true;
		if(__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true &&
				this.companyId == other.getCompanyId() &&
				((this.content == null && other.getContent() == null) ||
						(this.content != null &&
								this.content.equals(other.getContent()))) &&
				((this.description == null && other.getDescription() == null) ||
						(this.description != null &&
								this.description
										.equals(other.getDescription()))) &&
				this.id == other.getId() &&
				this.isPublic == other.isIsPublic() &&
				((this.name == null && other.getName() == null) ||
						(this.name != null &&
								this.name.equals(other.getName()))) &&
				this.readOnly == other.isReadOnly();
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
		_hashCode += getCompanyId();
		if(getContent() != null) {
			_hashCode += getContent().hashCode();
		}
		if(getDescription() != null) {
			_hashCode += getDescription().hashCode();
		}
		_hashCode += getId();
		_hashCode += (isIsPublic() ? Boolean.TRUE : Boolean.FALSE).hashCode();
		if(getName() != null) {
			_hashCode += getName().hashCode();
		}
		_hashCode += (isReadOnly() ? Boolean.TRUE : Boolean.FALSE).hashCode();
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc =
			new org.apache.axis.description.TypeDesc(ContentModuleType.class,
					true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName(
				"http://mock.dataaccess.utils.cms.agnitas.org",
				"ContentModuleType"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("companyId");
		elemField.setXmlName(new javax.xml.namespace.QName("", "companyId"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("content");
		elemField.setXmlName(new javax.xml.namespace.QName("", "content"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("description");
		elemField.setXmlName(new javax.xml.namespace.QName("", "description"));
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
		elemField.setFieldName("isPublic");
		elemField.setXmlName(new javax.xml.namespace.QName("", "isPublic"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "boolean"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("name");
		elemField.setXmlName(new javax.xml.namespace.QName("", "name"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("readOnly");
		elemField.setXmlName(new javax.xml.namespace.QName("", "readOnly"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "boolean"));
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
