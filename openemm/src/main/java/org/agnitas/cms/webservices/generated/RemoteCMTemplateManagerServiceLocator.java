/**
 * RemoteCMTemplateManagerServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.agnitas.cms.webservices.generated;

public class RemoteCMTemplateManagerServiceLocator extends org.apache.axis.client.Service
		implements org.agnitas.cms.webservices.generated.RemoteCMTemplateManagerService {

	public RemoteCMTemplateManagerServiceLocator() {
	}


	public RemoteCMTemplateManagerServiceLocator(
			org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public RemoteCMTemplateManagerServiceLocator(java.lang.String wsdlLoc,
												 javax.xml.namespace.QName sName) throws
			javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for RemoteCMTemplateManager
	private java.lang.String RemoteCMTemplateManager_address = "http://localhost:8080/services/org/agnitas/cms/utils/dataaccess/mock/RemoteCMTemplateManager";

	public java.lang.String getRemoteCMTemplateManagerAddress() {
		return RemoteCMTemplateManager_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String RemoteCMTemplateManagerWSDDServiceName = "RemoteCMTemplateManager";

	public java.lang.String getRemoteCMTemplateManagerWSDDServiceName() {
		return RemoteCMTemplateManagerWSDDServiceName;
	}

	public void setRemoteCMTemplateManagerWSDDServiceName(java.lang.String name) {
		RemoteCMTemplateManagerWSDDServiceName = name;
	}

	public org.agnitas.cms.webservices.generated.RemoteCMTemplateManager_PortType getRemoteCMTemplateManager() throws
			javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(RemoteCMTemplateManager_address);
		}
		catch(java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getRemoteCMTemplateManager(endpoint);
	}

	public org.agnitas.cms.webservices.generated.RemoteCMTemplateManager_PortType getRemoteCMTemplateManager(
			java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
		try {
			org.agnitas.cms.webservices.generated.RemoteCMTemplateManagerSoapBindingStub _stub = new org.agnitas.cms.webservices.generated.RemoteCMTemplateManagerSoapBindingStub(
					portAddress, this);
			_stub.setPortName(getRemoteCMTemplateManagerWSDDServiceName());
			return _stub;
		}
		catch(org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setRemoteCMTemplateManagerEndpointAddress(java.lang.String address) {
		RemoteCMTemplateManager_address = address;
	}

	/**
	 * For the given interface, get the stub implementation.
	 * If this service has no port for the given interface,
	 * then ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws
			javax.xml.rpc.ServiceException {
		try {
			if(org.agnitas.cms.webservices.generated.RemoteCMTemplateManager_PortType.class
					.isAssignableFrom(serviceEndpointInterface)) {
				org.agnitas.cms.webservices.generated.RemoteCMTemplateManagerSoapBindingStub _stub = new org.agnitas.cms.webservices.generated.RemoteCMTemplateManagerSoapBindingStub(
						new java.net.URL(RemoteCMTemplateManager_address), this);
				_stub.setPortName(getRemoteCMTemplateManagerWSDDServiceName());
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
		if("RemoteCMTemplateManager".equals(inputPortName)) {
			return getRemoteCMTemplateManager();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName(
				"http://mock.dataaccess.utils.cms.agnitas.org",
				"RemoteCMTemplateManagerService");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if(ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName(
					"http://mock.dataaccess.utils.cms.agnitas.org",
					"RemoteCMTemplateManager"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName,
								   java.lang.String address) throws
			javax.xml.rpc.ServiceException {

		if("RemoteCMTemplateManager".equals(portName)) {
			setRemoteCMTemplateManagerEndpointAddress(address);
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
