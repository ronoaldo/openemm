<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>
<agn:Permission token="recipient.show"/>

<% request.setAttribute("sidemenu_active","Recipients"); %>
<% request.setAttribute("sidemenu_sub_active", "recipient.Blacklist"); %>
<% request.setAttribute("agnTitleKey","recipient.Blacklist"); %>
<% request.setAttribute("agnSubtitleKey","recipient.Blacklist"); %>
<% request.setAttribute("agnNavigationKey","blacklist"); %>
<% request.setAttribute("agnHighlightKey","recipient.Blacklist"); %>
<% request.setAttribute("agnHelpKey", new String("blacklist")); %>
