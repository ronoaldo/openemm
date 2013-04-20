<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.cms.web.ContentModuleTypeAction" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:Permission token="cms.central_content_management"/>

<c:set var="sidemenu_active" value="Mailings" scope="request" />
<c:set var="sidemenu_sub_active" value="ContentManagement" scope="request" />
<c:set var="agnTitleKey" value="ContentManagement" scope="request" />
<c:set var="agnSubtitleKey" value="cms.ContentModuleTypes" scope="request" />
<c:set var="agnSubtitleValue" value="${contentModuleTypeForm.name}" scope="request" />
<c:set var="agnNavigationKey" value="ContentManagementSub" scope="request" />
<c:set var="agnHighlightKey" value="cms.ContentModuleTypes" scope="request" />
<c:set var="agnNavHrefAppend" value="&cmtId=${contentModuleTypeForm.cmtId}" scope="request" />

<c:set var="ACTION_PURE_PREVIEW" value="<%= ContentModuleTypeAction.ACTION_PURE_PREVIEW %>" scope="request" />

<c:set var="previewUrl" value="/cms_cmt.do?action=${ACTION_PURE_PREVIEW}&cmtId=${contentModuleTypeForm.cmtId}" scope="request" />
