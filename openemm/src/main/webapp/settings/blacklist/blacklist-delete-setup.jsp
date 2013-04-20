<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<agn:Permission token="recipient.show"/>

<% request.setAttribute("sidemenu_active", new String("Recipients")); %>
<% request.setAttribute("sidemenu_sub_active", new String("recipient.Blacklist")); %>
<% request.setAttribute("agnTitleKey", new String("recipient.Blacklist")); %>
<% request.setAttribute("agnSubtitleKey", new String("recipient.Blacklist")); %>
<% request.setAttribute("agnNavigationKey", new String("blacklist")); %>
<% request.setAttribute("agnHighlightKey", new String("recipient.Blacklist")); %>
<% request.setAttribute("agnHelpKey", new String("blacklist")); %>