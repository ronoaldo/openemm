<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.cms.web.ContentModuleAction" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:Permission token="cms.central_content_management"/>

<c:set var="sidemenu_active" value="Mailings" scope="request" />
<c:set var="sidemenu_sub_active" value="ContentManagement" scope="request" />
<c:set var="agnTitleKey" value="ContentManagement" scope="request" />
<c:set var="agnSubtitleKey" value="cms.ContentModules" scope="request" />
<c:set var="agnSubtitleValue" value="${contentModuleForm.name}" scope="request" />
<c:set var="agnNavigationKey" value="ContentManagementSub" scope="request" />
<c:set var="agnHighlightKey" value="cms.ContentModules" scope="request" />
<c:set var="agnHelpKey" value="cmContentModuleView" scope="request" />

<c:choose>
	<c:when test="${contentModuleForm.fromListPage}">
		<c:set var="cancelAction" value="<%= ContentModuleAction.ACTION_LIST %>" scope="request" />
	</c:when>
	<c:otherwise>
		<c:set var="cancelAction" value="<%= ContentModuleAction.ACTION_VIEW %>" scope="request" />
	</c:otherwise>
</c:choose>