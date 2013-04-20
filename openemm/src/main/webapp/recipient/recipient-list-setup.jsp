<%@ page language="java" contentType="text/html; charset=utf-8"
	import="org.agnitas.web.RecipientAction"
	buffer="32kb"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon />
<agn:Permission token="recipient.show" />

<c:set var="sidemenu_active" value="Recipients" scope="request"/>
<c:set var="sidemenu_sub_active" value="default.Overview" scope="request"/>
<c:set var="agnTitleKey" value="Recipients" scope="request"/>
<c:set var="agnSubtitleKey" value="Recipients" scope="request"/>
<c:set var="agnNavigationKey" value="subscriber_list" scope="request"/>
<c:set var="agnHighlightKey" value="default.Overview" scope="request"/>
<c:set var="ACTION_LIST" value="<%= RecipientAction.ACTION_LIST %>" scope="request"/>
<c:set var="ACTION_CONFIRM_DELETE" value="<%= RecipientAction.ACTION_CONFIRM_DELETE %>" scope="request"/>
<c:set var="ACTION_VIEW" value="<%= RecipientAction.ACTION_VIEW %>" scope="request"/>
<c:set var="DUMMY_RECIPIENT_FIELD" value="<%= RecipientAction.DUMMY_RECIPIENT_FIELD%>" scope="request" />
<c:set var="agnHelpKey" value="recipientList" scope="request" />

