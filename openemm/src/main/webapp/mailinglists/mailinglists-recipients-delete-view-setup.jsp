<%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, java.util.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>

<agn:Permission token="mailinglist.recipients.delete"/>

<c:set var="sidemenu_active" value="Mailinglists" scope="request"/>
<c:set var="sidemenu_sub_active" value="default.Overview" scope="request"/>
<c:set var="agnTitleKey" value="Mailinglist" scope="request"/>
<c:set var="agnSubtitleKey" value="mailinglist.delete.recipients" scope="request"/>
<c:set var="agnSubtitleValue" value="${mailinglistForm.shortname}" scope="request"/>
<c:set var="agnNavigationKey" value="mailinglists-recipients-delete" scope="request"/>
<c:set var="agnHighlightKey" value="mailinglist.delete.recipients" scope="request"/>
<c:set var="agnNavHrefAppend" value="&mailinglistID=${mailinglistForm.mailinglistID}" scope="request"/>