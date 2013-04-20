<%@ page language="java" import="org.agnitas.beans.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>
<% request.setAttribute("sidemenu_active", new String("none")); %>
<% request.setAttribute("sidemenu_sub_active", new String("none")); %>
<% request.setAttribute("agnTitleKey", new String("default.A_EMM")); %>
<% request.setAttribute("agnSubtitleKey", new String("Welcome")); %>
<% request.setAttribute("agnSubtitleValue", ((Admin)session.getAttribute("emm.admin")).getFullname()); %>
<% request.setAttribute("agnNavigationKey", new String("none")); %>
<% request.setAttribute("agnHighlightKey", new String("none")); %>

