<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<agn:Permission token="settings.show"/>

<%
request.setAttribute("sidemenu_active", new String("Recipients"));
request.setAttribute("sidemenu_sub_active", new String("settings.FormsOfAddress"));
request.setAttribute("agnNavigationKey", new String("Salutations"));
request.setAttribute("agnHighlightKey", new String("default.Overview"));
request.setAttribute("agnSubtitleKey", new String("settings.FormsOfAddress"));
request.setAttribute("agnTitleKey", new String("settings.FormsOfAddress"));
request.setAttribute("companyID",  AgnUtils.getCompanyID(request));
request.setAttribute("ACTION_VIEW", SalutationAction.ACTION_VIEW);
request.setAttribute("ACTION_CONFIRM_DELETE" ,SalutationAction.ACTION_CONFIRM_DELETE);

request.setAttribute("ACTION_LIST", SalutationAction.ACTION_LIST);    
%>
<% request.setAttribute("agnHelpKey", new String("salutationForms")); %>