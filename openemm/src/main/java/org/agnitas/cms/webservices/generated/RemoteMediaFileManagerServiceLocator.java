/**
 * RemoteMediaFileManagerServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.agnitas.cms.webservices.generated;

public class RemoteMediaFileManagerServiceLocator extends org.apache.axis.client.Service implements org.agnitas.cms.webservices.generated.RemoteMediaFileManagerService {

    public RemoteMediaFileManagerServiceLocator() {
    }


    public RemoteMediaFileManagerServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public RemoteMediaFileManagerServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for RemoteMediaFileManager
    private java.lang.String RemoteMediaFileManager_address = "http://localhost:8080/services/org/agnitas/cms/utils/dataaccess/mock/RemoteMediaFileManager";

    public java.lang.String getRemoteMediaFileManagerAddress() {
        return RemoteMediaFileManager_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String RemoteMediaFileManagerWSDDServiceName = "RemoteMediaFileManager";

    public java.lang.String getRemoteMediaFileManagerWSDDServiceName() {
        return RemoteMediaFileManagerWSDDServiceName;
    }

    public void setRemoteMediaFileManagerWSDDServiceName(java.lang.String name) {
        RemoteMediaFileManagerWSDDServiceName = name;
    }

    public org.agnitas.cms.webservices.generated.RemoteMediaFileManager_PortType getRemoteMediaFileManager() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(RemoteMediaFileManager_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getRemoteMediaFileManager(endpoint);
    }

    public org.agnitas.cms.webservices.generated.RemoteMediaFileManager_PortType getRemoteMediaFileManager(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.agnitas.cms.webservices.generated.RemoteMediaFileManagerSoapBindingStub _stub = new org.agnitas.cms.webservices.generated.RemoteMediaFileManagerSoapBindingStub(portAddress, this);
            _stub.setPortName(getRemoteMediaFileManagerWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setRemoteMediaFileManagerEndpointAddress(java.lang.String address) {
        RemoteMediaFileManager_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.agnitas.cms.webservices.generated.RemoteMediaFileManager_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                org.agnitas.cms.webservices.generated.RemoteMediaFileManagerSoapBindingStub _stub = new org.agnitas.cms.webservices.generated.RemoteMediaFileManagerSoapBindingStub(new java.net.URL(RemoteMediaFileManager_address), this);
                _stub.setPortName(getRemoteMediaFileManagerWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("RemoteMediaFileManager".equals(inputPortName)) {
            return getRemoteMediaFileManager();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://mock.dataaccess.utils.cms.agnitas.org", "RemoteMediaFileManagerService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://mock.dataaccess.utils.cms.agnitas.org", "RemoteMediaFileManager"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("RemoteMediaFileManager".equals(portName)) {
            setRemoteMediaFileManagerEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
