/**
 * RemoteCMTemplateManagerService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.agnitas.cms.webservices.generated;

public interface RemoteCMTemplateManagerService extends javax.xml.rpc.Service {
	public java.lang.String getRemoteCMTemplateManagerAddress();

	public org.agnitas.cms.webservices.generated.RemoteCMTemplateManager_PortType getRemoteCMTemplateManager() throws
			javax.xml.rpc.ServiceException;

	public org.agnitas.cms.webservices.generated.RemoteCMTemplateManager_PortType getRemoteCMTemplateManager(
			java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
