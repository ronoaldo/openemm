<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.web.TargetAction" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<agn:Permission token="targets.show"/>
<% request.setAttribute("sidemenu_active", new String("Targetgroups")); %>
<% request.setAttribute("sidemenu_sub_active", new String("default.Overview")); %>
<% request.setAttribute("agnTitleKey", new String("target.Targets")); %>
<% request.setAttribute("agnSubtitleKey", new String("target.Targets")); %>
<% request.setAttribute("agnNavigationKey", new String("targets")); %>
<% request.setAttribute("agnHighlightKey", new String("default.Overview")); %>
<% request.setAttribute("ACTION_LIST", TargetAction.ACTION_LIST ); %>
<% request.setAttribute("ACTION_VIEW", TargetAction.ACTION_VIEW ); %>
<% request.setAttribute("ACTION_CONFIRM_DELETE", TargetAction.ACTION_CONFIRM_DELETE); %>
<% request.setAttribute("agnHelpKey", new String("targetGroupView")); %>
