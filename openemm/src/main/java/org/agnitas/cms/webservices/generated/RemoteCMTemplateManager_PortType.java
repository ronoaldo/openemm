/**
 * RemoteCMTemplateManager_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.agnitas.cms.webservices.generated;

public interface RemoteCMTemplateManager_PortType extends java.rmi.Remote {
	public org.agnitas.cms.webservices.generated.CMTemplate createCMTemplate(
			org.agnitas.cms.webservices.generated.CMTemplate template) throws
			java.rmi.RemoteException;

	public org.agnitas.cms.webservices.generated.CMTemplate getCMTemplate(int id) throws
			java.rmi.RemoteException;

	public java.lang.Object[] getCMTemplates(int companyId) throws
			java.rmi.RemoteException;

	public java.lang.Object[] getCMTemplatesSortByName(int companyId,
													   java.lang.String sortDirection) throws
			java.rmi.RemoteException;

	public void deleteCMTemplate(int id) throws java.rmi.RemoteException;

	public boolean updateCMTemplate(int id, java.lang.String name,
									java.lang.String description) throws
			java.rmi.RemoteException;

	public boolean updateContent(int id, byte[] content) throws java.rmi.RemoteException;

	public void addMailingBindings(int cmTemplateId, java.lang.Object[] mailingIds) throws
			java.rmi.RemoteException;

	public org.agnitas.cms.webservices.generated.CMTemplate getCMTemplateForMailing(
			int mailingId) throws java.rmi.RemoteException;

	public void removeMailingBindings(java.lang.Object[] mailingIds) throws
			java.rmi.RemoteException;

	public java.lang.Object[] getMailingBindingWrapper(int cmTemplate) throws
			java.rmi.RemoteException;

	public java.lang.Object[] getMailingBindingArrayWrapper(
			java.lang.Object[] mailingIds) throws java.rmi.RemoteException;

	public java.lang.String getTextVersion(int adminId) throws java.rmi.RemoteException;

	public void removeTextVersion(int adminId) throws java.rmi.RemoteException;

	public void saveTextVersion(int adminId, java.lang.String text) throws
			java.rmi.RemoteException;

	public java.lang.Object[] getMailingWithCmsContent(java.lang.Object[] mailingIds,
													   int companyId) throws
			java.rmi.RemoteException;
}
