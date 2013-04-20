<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ page import="org.agnitas.cms.web.ContentModuleCategoryAction" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:Permission token="cms.central_content_management"/>

<c:set var="sidemenu_active" value="Mailings" scope="request" />
<c:set var="sidemenu_sub_active" value="ContentManagement" scope="request" />
<c:set var="agnTitleKey" value="ContentManagement" scope="request" />
<c:set var="agnSubtitleKey" value="cms.CMCategories" scope="request" />
<c:set var="agnSubtitleValue" value="${contentModuleCategoryForm.name}" scope="request" />
<c:choose>
	<c:when test="${contentModuleCategoryForm.cmcId > 0}">
    	<c:set var="agnNavigationKey" value="ContentManagementSub" scope="request" />
	</c:when>
	<c:otherwise>	
    	<c:set var="agnNavigationKey" value="ContentManagementSub" scope="request" />
    </c:otherwise>
</c:choose>
<c:set var="agnHighlightKey" value="cms.CMCategories" scope="request" />
<c:set var="agnNavHrefAppend" value="&cmcId=${contentModuleCategoryForm.cmcId}" scope="request" />

<c:set var="ACTION_CONFIRM_DELETE" value="<%= ContentModuleCategoryAction.ACTION_CONFIRM_DELETE %>" scope="request" />
<c:set var="agnHelpKey" value="cmCategoryList" scope="request" />