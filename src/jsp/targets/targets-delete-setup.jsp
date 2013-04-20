<%@ page language="java" import="org.agnitas.web.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<agn:Permission token="targets.show"/>

<c:set var="sidemenu_active" value="Targetgroups" scope="request" />
<c:set var="sidemenu_sub_active" value="none" scope="request" />
<c:set var="agnTitleKey" value="target.Target" scope="request" />
<c:set var="agnSubtitleKey" value="target.Target" scope="request" />
<c:set var="agnSubtitleValue" value="${targetForm.shortname}" scope="request" />
<c:set var="agnNavigationKey" value="targetView" scope="request" />
<c:set var="agnHighlightKey" value="target.NewTarget" scope="request" />
<c:set var="agnNavHrefAppend" value="&targetID=${targetForm.targetID}" scope="request" />

<c:set var="ACTION_VIEW" value="<%= TargetAction.ACTION_VIEW %>" scope="request" />
<% request.setAttribute("agnHelpKey", new String("targetGroupView")); %>
