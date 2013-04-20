<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.SafeString, org.agnitas.web.forms.EmmActionForm" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<agn:Permission token="actions.change"/>

<%
	 String tmpShortname=new String("");
     int tmpActionID = 0;
	 if(session.getAttribute("emmActionForm")!=null) {
	     tmpShortname=((EmmActionForm)session.getAttribute("emmActionForm")).getShortname();
         tmpActionID = ((EmmActionForm)session.getAttribute("emmActionForm")).getActionID();
	 }
	 %>

<% if (tmpActionID != 0) {
    request.setAttribute("agnNavigationKey", new String("ActionEdit"));
    request.setAttribute("agnHighlightKey", new String("action.Edit_Action"));
} else {
    request.setAttribute("agnNavigationKey", new String("Action"));
    request.setAttribute("agnHighlightKey", new String("action.New_Action"));
}
%>
<% request.setAttribute("sidemenu_active", new String("SiteActions")); %>
<% request.setAttribute("sidemenu_sub_active", new String("Actions")); %>
<% request.setAttribute("agnTitleKey", new String("action.New_Action")); %>
<% request.setAttribute("agnSubtitleKey", new String("action.New_Action")); %>
<% request.setAttribute("agnSubtitleValue", SafeString.getHTMLSafeString(tmpShortname)); %>
<% request.setAttribute("agnNavHrefAppend", new String("")); %>
<% request.setAttribute("agnHelpKey", new String("newAction")); %>