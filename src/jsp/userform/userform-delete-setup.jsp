<%@ page language="java" import="org.agnitas.web.UserFormEditAction" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<agn:Permission token="forms.delete"/>

<c:set var="sidemenu_active" value="SiteActions" scope="request" />
<c:set var="sidemenu_sub_active" value="Forms" scope="request" />
<c:set var="agnTitleKey" value="Form" scope="request" />
<c:set var="agnSubtitleKey" value="Form" scope="request" />
<c:set var="agnSubtitleValue" value="${userFormEditForm.formName}" scope="request" />
<c:set var="agnNavigationKey" value="formView" scope="request" />
<c:set var="agnHighlightKey" value="settings.New_Form" scope="request" />
<c:set var="agnHelpKey" value="formView" scope="request" />

<c:set var="ACTION_VIEW" value="<%= UserFormEditAction.ACTION_VIEW %>" scope="request" />

