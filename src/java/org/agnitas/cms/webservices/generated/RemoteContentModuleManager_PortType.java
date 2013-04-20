/**
 * RemoteContentModuleManager_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.agnitas.cms.webservices.generated;

public interface RemoteContentModuleManager_PortType extends java.rmi.Remote {
    public org.agnitas.cms.webservices.generated.ContentModule getContentModule(int id) throws java.rmi.RemoteException;
    public java.lang.Object[] getContentModules(int companyId) throws java.rmi.RemoteException;
    public void deleteContentModule(int id) throws java.rmi.RemoteException;
    public int createContentModule(org.agnitas.cms.webservices.generated.ContentModule contentModule) throws java.rmi.RemoteException;
    public boolean updateContentModule(int id, java.lang.String newName, java.lang.String newDescription, int newCategoryId) throws java.rmi.RemoteException;
    public java.lang.Object[] getContentModuleContents(int contentModuleId) throws java.rmi.RemoteException;
    public void saveContentModuleContent(int contentModuleId, org.agnitas.cms.webservices.generated.CmsTag tag) throws java.rmi.RemoteException;
    public void removeContentsForContentModule(int contentModuleId) throws java.rmi.RemoteException;
    public java.lang.Object[] getMailingBinding(java.lang.Object[] mailingIds, int contentModuleId) throws java.rmi.RemoteException;
    public java.lang.Object[] getMailingsByContentModule(int contentModuleId) throws java.rmi.RemoteException;
    public void addMailingBindings(int contentModuleId, java.lang.Object[] mailingIds) throws java.rmi.RemoteException;
    public void removeMailingBindings(int contentModuleId, java.lang.Object[] mailingIds) throws java.rmi.RemoteException;
    public void addMailingBindingToContentModules(java.lang.Object[] contentModuleIds, int mailingId) throws java.rmi.RemoteException;
    public void removeMailingBindingFromContentModules(java.lang.Object[] contentModuleIds, int mailingId) throws java.rmi.RemoteException;
    public java.lang.Object[] getAssignedCMsForMailing(int mailingId) throws java.rmi.RemoteException;
    public java.lang.Object[] getCMLocationsForMailingId(int mailingId) throws java.rmi.RemoteException;
    public java.lang.Object[] getContentModulesForMailing(int mailingId) throws java.rmi.RemoteException;
    public void removeCMLocationsForMailing(int mailingId) throws java.rmi.RemoteException;
    public void addCMLocations(java.lang.Object[] locations) throws java.rmi.RemoteException;
    public void saveContentModuleContentList(int contentModuleId, java.lang.Object[] tagList) throws java.rmi.RemoteException;
    public void updateCMLocation(org.agnitas.cms.webservices.generated.ContentModuleLocation location) throws java.rmi.RemoteException;
    public void removeCMLocationForMailingsByContentModule(int contentModuleId, java.lang.Object[] mailingsToDeassign) throws java.rmi.RemoteException;
    public int createContentModuleCategory(org.agnitas.cms.webservices.generated.ContentModuleCategory category) throws java.rmi.RemoteException;
    public void updateContentModuleCategory(org.agnitas.cms.webservices.generated.ContentModuleCategory category) throws java.rmi.RemoteException;
    public org.agnitas.cms.webservices.generated.ContentModuleCategory getContentModuleCategory(int id) throws java.rmi.RemoteException;
    public void deleteContentModuleCategory(int categoryId) throws java.rmi.RemoteException;
    public java.lang.Object[] getAllCMCategories(int companyId) throws java.rmi.RemoteException;
    public java.lang.Object[] getContentModulesForCategory(int companyId, int categoryId) throws java.rmi.RemoteException;
}
