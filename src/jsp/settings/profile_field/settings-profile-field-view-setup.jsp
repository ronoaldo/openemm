<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<agn:Permission token="profileField.show"/>

<c:if test="${empty hasErrors}">	
	<c:set var="TMP_FIELDNAME" value="${profileFieldForm.fieldname}" scope="request" />
</c:if>

<c:set var="sidemenu_active" value="Administration" scope="request" />
<c:set var="sidemenu_sub_active" value="settings.Profile_DB" scope="request" />
<c:set var="agnTitleKey" value="Profile_Database" scope="request" />
<c:set var="agnSubtitleKey" value="Profile_Database" scope="request" />
<c:choose>
	<c:when test="${not empty TMP_FIELDNAME}">
        <c:set var="agnNavigationKey" value="profiledbEdit" scope="request" />
		<c:set var="agnHighlightKey" value="settings.EditProfileDB_Field" scope="request" />
	</c:when>
	<c:otherwise>
        <c:set var="agnNavigationKey" value="profiledb" scope="request" />
		<c:set var="agnHighlightKey" value="settings.NewProfileDB_Field" scope="request" />
	</c:otherwise>
</c:choose>

<c:set var="ACTION_CONFIRM_DELETE" value="<%= ProfileFieldAction.ACTION_CONFIRM_DELETE %>" scope="request" />
<c:set var="ACTION_LIST" value="<%= ProfileFieldAction.ACTION_LIST %>" scope="request" />
<c:set var="agnHelpKey" value="newProfileField" scope="request" />

