<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<agn:Permission token="mailing.show"/>

<% request.setAttribute("sidemenu_active", new String("Administration")); %>
<% request.setAttribute("sidemenu_sub_active", new String("settings.Mailloops"));  %>
<% request.setAttribute("agnNavigationKey", new String("Mailloops")); %>
<% request.setAttribute("agnHighlightKey", new String("default.Overview")); %>
<% request.setAttribute("agnTitleKey", new String("settings.Mailloops")); %>
<% request.setAttribute("agnSubtitleKey", new String("settings.Mailloops")); %>
<% request.setAttribute("agnHelpKey", new String("bounceFilter")); %>