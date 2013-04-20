<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.web.RecipientAction" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>
<agn:Permission token="recipient.delete"/>

<c:set var="sidemenu_active" value="Recipients" scope="request" />
<c:set var="sidemenu_sub_active" value="default.Overview" scope="request" />
<c:set var="agnTitleKey" value="Recipients" scope="request" />
<c:set var="agnSubtitleKey" value="Recipients" scope="request" />
<c:set var="agnNavigationKey" value="subscriber_editor" scope="request" />
<c:set var="agnHighlightKey" value="recipient.RecipientEdit" scope="request" />

<c:set var="ACTION_LIST" value="<%= RecipientAction.ACTION_LIST %>" scope="request"/>
<c:set var="ACTION_DELETE" value="<%= RecipientAction.ACTION_DELETE %>" scope="request"/>

