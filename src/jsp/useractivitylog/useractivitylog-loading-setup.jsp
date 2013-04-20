<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>
<agn:Permission token="userlog.show|adminlog.show|masterlog.show"/>

<% request.setAttribute("sidemenu_active", "Administration"); %>
<% request.setAttribute("sidemenu_sub_active", "Userlogs"); %>
<% request.setAttribute("agnTitleKey", "Userlogs"); %>
<% request.setAttribute("agnSubtitleKey", "Userlogs"); %>
<% request.setAttribute("agnNavigationKey", "userlogs"); %>
<% request.setAttribute("agnHighlightKey", "default.Overview"); %>
<% request.setAttribute("agnHelpKey", new String("userlog")); %>

