<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.SafeString, org.agnitas.web.forms.EmmActionForm" %>

<%
int tmpActionID=0;
String tmpShortname=new String("");

if(session.getAttribute("emmActionForm")!=null) {
    tmpActionID=((EmmActionForm)session.getAttribute("emmActionForm")).getActionID();
    tmpShortname=((EmmActionForm)session.getAttribute("emmActionForm")).getShortname();
    request.setAttribute("tmpShortname", tmpShortname);
    request.setAttribute("tmpActionID", tmpActionID);
}
%>
<% request.setAttribute("sidemenu_active", new String("SiteActions")); %>
<% request.setAttribute("sidemenu_sub_active", new String("Actions")); %>
<% request.setAttribute("agnTitleKey", new String("action.Action")); %>
<% request.setAttribute("agnSubtitleKey", new String("action.Action")); %>
<% request.setAttribute("agnSubtitleValue", SafeString.getHTMLSafeString(tmpShortname)); %>
<% request.setAttribute("agnNavigationKey", new String("Action")); %>
<% request.setAttribute("agnHighlightKey", new String("action.New_Action")); %>
<% request.setAttribute("agnNavHrefAppend", new String("?actionID="+tmpActionID)); %>
<% request.setAttribute("agnHelpKey", new String("actionList")); %>