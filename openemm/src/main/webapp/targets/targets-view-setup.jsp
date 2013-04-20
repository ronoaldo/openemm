<%@ page language="java" import="org.agnitas.web.TargetAction" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<agn:Permission token="targets.show"/>

<c:set var="sidemenu_active" value="Targetgroups" scope="request" />
<c:choose>
	<c:when test="${targetForm.targetID != 0}">
		<c:set var="sidemenu_sub_active" value="default.Overview" scope="request" />
                <c:set var="agnNavigationKey" value="TargetEdit" scope="request" />
                <c:set var="agnHighlightKey" value="target.Edit" scope="request" />
    </c:when>
   	<c:otherwise>
     <c:set var="sidemenu_sub_active" value="target.NewTarget" scope="request" />
     <c:set var="agnNavigationKey" value="targetView" scope="request" />
     <c:set var="agnHighlightKey" value="target.NewTarget" scope="request" />
    </c:otherwise>
</c:choose>
<c:set var="agnHelpKey" value="targetGroupView" scope="request" />
<c:set var="agnTitleKey" value="target.Target" scope="request" />
<c:set var="agnSubtitleKey" value="target.Target" scope="request" />
<c:set var="agnSubtitleValue" value="${targetForm.shortname}" scope="request" />
<c:set var="agnNavHrefAppend" value="&targetID=${targetForm.targetID}" scope="request" />
<c:set var="ACTION_VIEW" value="<%= TargetAction.ACTION_VIEW %>" scope="request" />
<c:set var="ACTION_CREATE_ML" value="<%= TargetAction.ACTION_CREATE_ML %>" scope="request" />
<c:set var="ACTION_DELETE_RECIPIENTS_CONFIRM" value="<%= TargetAction.ACTION_DELETE_RECIPIENTS_CONFIRM %>" scope="request" />
<c:set var="ACTION_SAVE" value="<%= TargetAction.ACTION_SAVE %>" scope="request" />
<c:set var="ACTION_CLONE" value="<%= TargetAction.ACTION_CLONE %>" scope="request" />
<c:set var="ACTION_CONFIRM_DELETE" value="<%= TargetAction.ACTION_CONFIRM_DELETE %>" scope="request" />
