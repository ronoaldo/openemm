<%@ page language="java" import="org.agnitas.web.AdminAction" contentType="text/html; charset=utf-8" buffer="32kb" errorPage="/error.jsp"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<c:set var="agnSubtitleKey" value="settings.Admins" scope="request" />             <!-- ueber rechte Seite -->
<c:set var="sidemenu_active" value="Administration" scope="request" />          <!-- links Button -->
<c:set var="sidemenu_sub_active" value="settings.Admins" scope="request" />        <!-- links unter Button -->
<c:set var="agnTitleKey" value="settings.Admins" scope="request" />                <!-- Titelleiste -->

<c:choose>
	<c:when test="${adminForm.adminID != 0}">
     	<c:set var="agnNavigationKey" value="admins" scope="request" />
    	<c:set var="agnHighlightKey" value="default.Overview" scope="request" />
	</c:when>
	<c:otherwise>
     	<c:set var="agnNavigationKey" value="admins" scope="request" />
     	<c:set var="agnHighlightKey" value="settings.New_Admin" scope="request" />
   </c:otherwise>
</c:choose>

<c:set var="agnSubtitleValue" value="${adminForm.username}" scope="request" />
<c:set var="agnNavHrefAppend" value="&adminID=${adminForm.adminID}" scope="request" />

<c:set var="ACTION_VIEW" value="<%= AdminAction.ACTION_VIEW %>" scope="request" />
<c:set var="agnHelpKey" value="newUser" scope="request" />