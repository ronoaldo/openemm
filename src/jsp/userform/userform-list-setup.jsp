<%@ page language="java" contentType="text/html; charset=utf-8" import=" org.agnitas.web.UserFormEditAction" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<agn:Permission token="forms.view"/>

<c:set var="sidemenu_active" value="SiteActions" scope="request" />
<c:set var="sidemenu_sub_active" value="Forms" scope="request" />
<c:set var="agnNavigationKey" value="FormsOverview" scope="request" />
<c:set var="agnHighlightKey" value="default.Overview" scope="request" />
<c:set var="agnTitleKey" value="Forms" scope="request" />
<c:set var="agnSubtitleKey" value="Forms" scope="request" />
<c:set var="agnHelpKey" value="formList" scope="request" />

<c:set var="ACTION_LIST" value="<%= UserFormEditAction.ACTION_LIST %>" scope="request" />
<c:set var="ACTION_CONFIRM_DELETE" value="<%= UserFormEditAction.ACTION_CONFIRM_DELETE %>" scope="request" />
<c:set var="ACTION_VIEW" value="<%= UserFormEditAction.ACTION_VIEW %>" scope="request" />
