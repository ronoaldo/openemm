<%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, java.util.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<agn:Permission token="mailinglist.show"/>

<c:set var="sidemenu_active" value="Mailinglists" scope="request" />
<c:set var="ACTION_CONFIRM_DELETE" value="<%= MailinglistAction.ACTION_CONFIRM_DELETE %>" scope="page" />

<c:choose>
	<c:when test="${mailinglistForm.mailinglistID != 0}">
	    <c:set var="sidemenu_sub_active" value="none" scope="request" />
	    <c:set var="agnNavigationKey" value="MailinglistEdit" scope="request" />
	    <c:set var="agnHighlightKey" value="settings.EditMailinglist" scope="request" />
       	<c:set var="agnTitleKey" value="Mailinglist" scope="request" />
     	<c:set var="agnSubtitleKey" value="Mailinglist" scope="request" />
     	<c:set var="agnSubtitleValue" value="${mailinglistForm.shortname}" scope="request" />
	</c:when>
	<c:otherwise>
     	<c:set var="sidemenu_sub_active" value="settings.NewMailinglist" scope="request" />
    	<c:set var="agnNavigationKey" value="MailinglistNew" scope="request" />
     	<c:set var="agnHighlightKey" value="settings.NewMailinglist" scope="request" />
     	<c:set var="agnTitleKey" value="settings.NewMailinglist" scope="request" />
     	<c:set var="agnSubtitleKey" value="settings.NewMailinglist" scope="request" />
	</c:otherwise>
</c:choose>
<c:set var="agnHelpKey" value="newMailinglist" scope="request" />
<c:set var="agnNavHrefAppend" value="&mailinglistID=${mailinglistForm.mailinglistID}" scope="request" />

