<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<agn:Permission token="forms.view"/>

<c:choose>
	<c:when test="${userFormEditForm.formID != 0}">
		<c:set var="sidemenu_sub_active" value="Forms" scope="request" />
     	<c:set var="agnNavigationKey" value="formEdit" scope="request" />
     	<c:set var="agnHighlightKey" value="settings.Edit_Form" scope="request" />
	</c:when>
	<c:otherwise>
     	<c:set var="sidemenu_sub_active" value="Forms" scope="request" />
     	<c:set var="agnNavigationKey" value="formView" scope="request" />
     	<c:set var="agnHighlightKey" value="settings.New_Form" scope="request" />
	</c:otherwise>	
</c:choose>
<c:set var="agnHelpKey" value="formView" scope="request" />
<c:set var="sidemenu_active" value="SiteActions" scope="request" />
<c:set var="agnTitleKey" value="Form" scope="request" />
<c:set var="agnSubtitleKey" value="Form" scope="request" />
<c:set var="agnSubtitleValue" value="${userFormEditForm.formName}" scope="request" />
