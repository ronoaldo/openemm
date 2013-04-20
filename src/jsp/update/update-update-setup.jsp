<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>
<% request.setAttribute("sidemenu_active", new String("Administration")); %>
<% request.setAttribute("sidemenu_sub_active", new String("none")); %>
<% request.setAttribute("agnTitleKey", new String("default.A_EMM")); %>
<% request.setAttribute("agnSubtitleKey", new String("Settings")); %>
<% request.setAttribute("agnNavigationKey", new String("update")); %>
<% request.setAttribute("agnHighlightKey", new String("settings.Update")); %>
<%
String	host = request.getHeader ("host");
if (host != null) {
	int	n = host.indexOf (':');

	if (n != -1) {
		host = host.substring (0, n);
	}
} else
	host = "localhost";
request.setAttribute("agnRefresh", new String ("2; URL=http://" + host + ":8044/"));
%>
<% request.setAttribute("agnHelpKey", new String("update")); %>
