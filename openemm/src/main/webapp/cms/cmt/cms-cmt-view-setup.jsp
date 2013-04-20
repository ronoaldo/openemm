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
<c:choose>
	<c:when test="${contentModuleTypeForm.cmtId > 0}">
    	<c:set var="agnNavigationKey" value="ContentManagementSub" scope="request" />
	</c:when>
	<c:otherwise>	
    	<c:set var="agnNavigationKey" value="ContentManagementSub" scope="request" />
    </c:otherwise>
</c:choose>
<c:set var="agnHighlightKey" value="cms.ContentModuleTypes" scope="request" />
<c:set var="agnNavHrefAppend" value="&cmtId=${contentModuleTypeForm.cmtId}" scope="request" />

<c:set var="readOnly" value="${contentModuleTypeForm.readOnly}" scope="request" />

<c:set var="ACTION_COPY" value="<%= ContentModuleTypeAction.ACTION_COPY %>" scope="request" />
<c:set var="ACTION_CONFIRM_DELETE" value="<%= ContentModuleTypeAction.ACTION_CONFIRM_DELETE %>" scope="request" />
<c:set var="ACTION_PREVIEW" value="<%= ContentModuleTypeAction.ACTION_PURE_PREVIEW %>" scope="request" />
<c:set var="agnHelpKey" value="cmModuleTypeView" scope="request" />