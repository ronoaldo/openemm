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
 * EmmWebService_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package org.agnitas.webservice;

public class EmmWebService_ServiceLocator extends org.apache.axis.client.Service implements EmmWebService_Service {

    // Use to get a proxy class for EmmWebService
    private final java.lang.String EmmWebService_address = "http://localhost:8080/emm_webservice";

    public java.lang.String getEmmWebServiceAddress() {
        return EmmWebService_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String EmmWebServiceWSDDServiceName = "EmmWebService";

    public java.lang.String getEmmWebServiceWSDDServiceName() {
        return EmmWebServiceWSDDServiceName;
    }

    public void setEmmWebServiceWSDDServiceName(java.lang.String name) {
        EmmWebServiceWSDDServiceName = name;
    }

    public EmmWebService_Port getEmmWebService() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(EmmWebService_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getEmmWebService(endpoint);
    }

    public EmmWebService_Port getEmmWebService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            EmmWebServiceBindingStub _stub = new EmmWebServiceBindingStub(portAddress, this);
            _stub.setPortName(getEmmWebServiceWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (EmmWebService_Port.class.isAssignableFrom(serviceEndpointInterface)) {
                EmmWebServiceBindingStub _stub = new EmmWebServiceBindingStub(new java.net.URL(EmmWebService_address), this);
                _stub.setPortName(getEmmWebServiceWSDDServiceName());
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
        String inputPortName = portName.getLocalPart();
        if ("EmmWebService".equals(inputPortName)) {
            return getEmmWebService();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("urn:agnitas-webservice", "EmmWebService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("EmmWebService"));
        }
        return ports.iterator();
    }

}
