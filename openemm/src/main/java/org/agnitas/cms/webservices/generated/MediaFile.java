/**
 * MediaFile.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.agnitas.cms.webservices.generated;

public class MediaFile  implements java.io.Serializable {
    private int cmTemplateId;

    private int companyId;

    private byte[] content;

    private int contentModuleId;

    private int contentModuleTypeId;

    private int id;

    private int mediaType;

    private java.lang.String mimeType;

    private java.lang.String name;

    public MediaFile() {
    }

    public MediaFile(
           int cmTemplateId,
           int companyId,
           byte[] content,
           int contentModuleId,
           int contentModuleTypeId,
           int id,
           int mediaType,
           java.lang.String mimeType,
           java.lang.String name) {
           this.cmTemplateId = cmTemplateId;
           this.companyId = companyId;
           this.content = content;
           this.contentModuleId = contentModuleId;
           this.contentModuleTypeId = contentModuleTypeId;
           this.id = id;
           this.mediaType = mediaType;
           this.mimeType = mimeType;
           this.name = name;
    }


    /**
     * Gets the cmTemplateId value for this MediaFile.
     * 
     * @return cmTemplateId
     */
    public int getCmTemplateId() {
        return cmTemplateId;
    }


    /**
     * Sets the cmTemplateId value for this MediaFile.
     * 
     * @param cmTemplateId
     */
    public void setCmTemplateId(int cmTemplateId) {
        this.cmTemplateId = cmTemplateId;
    }


    /**
     * Gets the companyId value for this MediaFile.
     * 
     * @return companyId
     */
    public int getCompanyId() {
        return companyId;
    }


    /**
     * Sets the companyId value for this MediaFile.
     * 
     * @param companyId
     */
    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }


    /**
     * Gets the content value for this MediaFile.
     * 
     * @return content
     */
    public byte[] getContent() {
        return content;
    }


    /**
     * Sets the content value for this MediaFile.
     * 
     * @param content
     */
    public void setContent(byte[] content) {
        this.content = content;
    }


    /**
     * Gets the contentModuleId value for this MediaFile.
     * 
     * @return contentModuleId
     */
    public int getContentModuleId() {
        return contentModuleId;
    }


    /**
     * Sets the contentModuleId value for this MediaFile.
     * 
     * @param contentModuleId
     */
    public void setContentModuleId(int contentModuleId) {
        this.contentModuleId = contentModuleId;
    }


    /**
     * Gets the contentModuleTypeId value for this MediaFile.
     * 
     * @return contentModuleTypeId
     */
    public int getContentModuleTypeId() {
        return contentModuleTypeId;
    }


    /**
     * Sets the contentModuleTypeId value for this MediaFile.
     * 
     * @param contentModuleTypeId
     */
    public void setContentModuleTypeId(int contentModuleTypeId) {
        this.contentModuleTypeId = contentModuleTypeId;
    }


    /**
     * Gets the id value for this MediaFile.
     * 
     * @return id
     */
    public int getId() {
        return id;
    }


    /**
     * Sets the id value for this MediaFile.
     * 
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }


    /**
     * Gets the mediaType value for this MediaFile.
     * 
     * @return mediaType
     */
    public int getMediaType() {
        return mediaType;
    }


    /**
     * Sets the mediaType value for this MediaFile.
     * 
     * @param mediaType
     */
    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }


    /**
     * Gets the mimeType value for this MediaFile.
     * 
     * @return mimeType
     */
    public java.lang.String getMimeType() {
        return mimeType;
    }


    /**
     * Sets the mimeType value for this MediaFile.
     * 
     * @param mimeType
     */
    public void setMimeType(java.lang.String mimeType) {
        this.mimeType = mimeType;
    }


    /**
     * Gets the name value for this MediaFile.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this MediaFile.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MediaFile)) return false;
        MediaFile other = (MediaFile) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.cmTemplateId == other.getCmTemplateId() &&
            this.companyId == other.getCompanyId() &&
            ((this.content==null && other.getContent()==null) || 
             (this.content!=null &&
              java.util.Arrays.equals(this.content, other.getContent()))) &&
            this.contentModuleId == other.getContentModuleId() &&
            this.contentModuleTypeId == other.getContentModuleTypeId() &&
            this.id == other.getId() &&
            this.mediaType == other.getMediaType() &&
            ((this.mimeType==null && other.getMimeType()==null) || 
             (this.mimeType!=null &&
              this.mimeType.equals(other.getMimeType()))) &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName())));
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
        _hashCode += getCmTemplateId();
        _hashCode += getCompanyId();
        if (getContent() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getContent());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getContent(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += getContentModuleId();
        _hashCode += getContentModuleTypeId();
        _hashCode += getId();
        _hashCode += getMediaType();
        if (getMimeType() != null) {
            _hashCode += getMimeType().hashCode();
        }
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MediaFile.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://beans.mock.dataaccess.utils.cms.agnitas.org", "MediaFile"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cmTemplateId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cmTemplateId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("companyId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "companyId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("content");
        elemField.setXmlName(new javax.xml.namespace.QName("", "content"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "base64Binary"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("contentModuleId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "contentModuleId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("contentModuleTypeId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "contentModuleTypeId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mediaType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "mediaType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mimeType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "mimeType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
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
