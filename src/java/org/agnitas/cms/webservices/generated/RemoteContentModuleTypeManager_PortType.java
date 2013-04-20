/**
 * RemoteContentModuleTypeManager_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.agnitas.cms.webservices.generated;

public interface RemoteContentModuleTypeManager_PortType
		extends java.rmi.Remote {
	public org.agnitas.cms.webservices.generated.ContentModuleType getContentModuleType(
			int id) throws java.rmi.RemoteException;

	public java.lang.Object[] getContentModuleTypes(int companyId,
													boolean includePublic) throws
			java.rmi.RemoteException;

	public void deleteContentModuleType(int id) throws java.rmi.RemoteException;

	public int createContentModuleType(
			org.agnitas.cms.webservices.generated.ContentModuleType moduleType) throws
			java.rmi.RemoteException;

	public boolean updateContentModuleType(
			org.agnitas.cms.webservices.generated.ContentModuleType moduleType) throws
			java.rmi.RemoteException;
}
