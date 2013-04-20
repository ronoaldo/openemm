<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.web.AdminAction" errorPage="/error.jsp"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<%
	request.setAttribute("sidemenu_active", "none");
	request.setAttribute("sidemenu_sub_active", "none");
	request.setAttribute("agnTitleKey", "settings.Admins");
	request.setAttribute("agnSubtitleKey", "Welcome");
	request.setAttribute("agnSubtitleValue", session.getAttribute("fullName"));
	request.setAttribute("agnNavigationKey", "none");
	request.setAttribute("agnHighlightKey", "none");
%>