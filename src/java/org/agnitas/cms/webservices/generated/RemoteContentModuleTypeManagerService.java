/**
 * RemoteContentModuleTypeManagerService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.agnitas.cms.webservices.generated;

public interface RemoteContentModuleTypeManagerService
		extends javax.xml.rpc.Service {
	public java.lang.String getRemoteContentModuleTypeManagerAddress();

	public org.agnitas.cms.webservices.generated.RemoteContentModuleTypeManager_PortType getRemoteContentModuleTypeManager() throws
			javax.xml.rpc.ServiceException;

	public org.agnitas.cms.webservices.generated.RemoteContentModuleTypeManager_PortType getRemoteContentModuleTypeManager(
			java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
