<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>
<agn:Permission token="recipient.show"/>

<% request.setAttribute("sidemenu_active", new String("Recipients")); %>
<% request.setAttribute("sidemenu_sub_active", new String("default.Overview")); %>
<% request.setAttribute("agnTitleKey", new String("Recipients")); %>
<% request.setAttribute("agnSubtitleKey", new String("Recipients")); %>
<% request.setAttribute("agnNavigationKey", new String("subscriber_list")); %>
<% request.setAttribute("agnHighlightKey", new String("default.Overview")); %>

