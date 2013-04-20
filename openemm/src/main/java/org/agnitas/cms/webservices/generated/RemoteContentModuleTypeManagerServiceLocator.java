/**
 * RemoteContentModuleTypeManagerServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.agnitas.cms.webservices.generated;

public class RemoteContentModuleTypeManagerServiceLocator
		extends org.apache.axis.client.Service implements
		org.agnitas.cms.webservices.generated.RemoteContentModuleTypeManagerService {

	public RemoteContentModuleTypeManagerServiceLocator() {
	}


	public RemoteContentModuleTypeManagerServiceLocator(
			org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public RemoteContentModuleTypeManagerServiceLocator(
			java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws
			javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for RemoteContentModuleTypeManager
	private java.lang.String RemoteContentModuleTypeManager_address = "http://localhost:8080/services/org/agnitas/cms/utils/dataaccess/mock/RemoteContentModuleTypeManager";

	public java.lang.String getRemoteContentModuleTypeManagerAddress() {
		return RemoteContentModuleTypeManager_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String RemoteContentModuleTypeManagerWSDDServiceName = "RemoteContentModuleTypeManager";

	public java.lang.String getRemoteContentModuleTypeManagerWSDDServiceName() {
		return RemoteContentModuleTypeManagerWSDDServiceName;
	}

	public void setRemoteContentModuleTypeManagerWSDDServiceName(
			java.lang.String name) {
		RemoteContentModuleTypeManagerWSDDServiceName = name;
	}

	public org.agnitas.cms.webservices.generated.RemoteContentModuleTypeManager_PortType getRemoteContentModuleTypeManager() throws
			javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(RemoteContentModuleTypeManager_address);
		}
		catch(java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getRemoteContentModuleTypeManager(endpoint);
	}

	public org.agnitas.cms.webservices.generated.RemoteContentModuleTypeManager_PortType getRemoteContentModuleTypeManager(
			java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
		try {
			org.agnitas.cms.webservices.generated.RemoteContentModuleTypeManagerSoapBindingStub _stub = new org.agnitas.cms.webservices.generated.RemoteContentModuleTypeManagerSoapBindingStub(
					portAddress, this);
			_stub.setPortName(
					getRemoteContentModuleTypeManagerWSDDServiceName());
			return _stub;
		}
		catch(org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setRemoteContentModuleTypeManagerEndpointAddress(
			java.lang.String address) {
		RemoteContentModuleTypeManager_address = address;
	}

	/**
	 * For the given interface, get the stub implementation.
	 * If this service has no port for the given interface,
	 * then ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws
			javax.xml.rpc.ServiceException {
		try {
			if(org.agnitas.cms.webservices.generated.RemoteContentModuleTypeManager_PortType.class
					.isAssignableFrom(serviceEndpointInterface)) {
				org.agnitas.cms.webservices.generated.RemoteContentModuleTypeManagerSoapBindingStub _stub = new org.agnitas.cms.webservices.generated.RemoteContentModuleTypeManagerSoapBindingStub(
						new java.net.URL(
								RemoteContentModuleTypeManager_address), this);
				_stub.setPortName(
						getRemoteContentModuleTypeManagerWSDDServiceName());
				return _stub;
			}
		}
		catch(java.lang.Throwable t) {
			throw new javax.xml.rpc.ServiceException(t);
		}
		throw new javax.xml.rpc.ServiceException(
				"There is no stub implementation for the interface:  " +
						(serviceEndpointInterface == null ? "null" :
								serviceEndpointInterface.getName()));
	}

	/**
	 * For the given interface, get the stub implementation.
	 * If this service has no port for the given interface,
	 * then ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(javax.xml.namespace.QName portName,
								   Class serviceEndpointInterface) throws
			javax.xml.rpc.ServiceException {
		if(portName == null) {
			return getPort(serviceEndpointInterface);
		}
		java.lang.String inputPortName = portName.getLocalPart();
		if("RemoteContentModuleTypeManager".equals(inputPortName)) {
			return getRemoteContentModuleTypeManager();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName(
				"http://mock.dataaccess.utils.cms.agnitas.org",
				"RemoteContentModuleTypeManagerService");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if(ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName(
					"http://mock.dataaccess.utils.cms.agnitas.org",
					"RemoteContentModuleTypeManager"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName,
								   java.lang.String address) throws
			javax.xml.rpc.ServiceException {

		if("RemoteContentModuleTypeManager".equals(portName)) {
			setRemoteContentModuleTypeManagerEndpointAddress(address);
		} else { // Unknown Port Name
			throw new javax.xml.rpc.ServiceException(
					" Cannot set Endpoint Address for Unknown Port" + portName);
		}
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(javax.xml.namespace.QName portName,
								   java.lang.String address) throws
			javax.xml.rpc.ServiceException {
		setEndpointAddress(portName.getLocalPart(), address);
	}

}
