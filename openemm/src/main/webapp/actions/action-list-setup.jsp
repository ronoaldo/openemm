<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<agn:Permission token="actions.show"/>

<% request.setAttribute("sidemenu_active", new String("SiteActions")); %>
<% request.setAttribute("sidemenu_sub_active", new String("Actions")); %>
<% request.setAttribute("agnTitleKey", new String("Actions")); %>
<% request.setAttribute("agnSubtitleKey", new String("Actions")); %>
<% request.setAttribute("agnNavigationKey", new String("ActionsOverview")); %>
<% request.setAttribute("agnHighlightKey", new String("default.Overview")); %>
<% request.setAttribute("agnHelpKey", new String("actionList")); %>
