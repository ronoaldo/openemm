<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<agn:CheckLogon/>

<agn:Permission token="profileField.show"/>

<% request.setAttribute("sidemenu_active", new String("Administration")); %>
<% request.setAttribute("sidemenu_sub_active", new String("settings.Profile_DB")); %>
<% request.setAttribute("agnTitleKey", new String("Profile_Database")); %>
<% request.setAttribute("agnSubtitleKey", new String("Profile_Database")); %>
<% request.setAttribute("agnNavigationKey", new String("profiledb")); %>
<% request.setAttribute("agnHighlightKey", new String("default.Overview")); %>
<% request.setAttribute("agnHelpKey", new String("profileFieldList")); %>

<c:set var="ACTION_LIST" value="<%= ProfileFieldAction.ACTION_LIST %>" scope="request" />
<c:set var="ACTION_VIEW" value="<%= ProfileFieldAction.ACTION_VIEW %>" scope="request" />
<c:set var="ACTION_CONFIRM_DELETE" value="<%= ProfileFieldAction.ACTION_CONFIRM_DELETE %>" scope="request" />

