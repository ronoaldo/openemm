<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.web.ImportProfileAction" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<% request.setAttribute("sidemenu_active", "Recipients"); %>
<% request.setAttribute("sidemenu_sub_active", "import.csv_upload"); %>
<% request.setAttribute("agnTitleKey", "import.ImportProfile"); %>
<% request.setAttribute("agnSubtitleKey", "import.ImportProfile"); %>
<% request.setAttribute("agnNavigationKey", "ImportProfileOverview"); %>
<% request.setAttribute("agnHighlightKey", "import.ProfileAdministration"); %>
<% request.setAttribute("agnHelpKey", new String("manageProfile")); %>
<% request.setAttribute("ACTION_VIEW", ImportProfileAction.ACTION_VIEW); %>
<% request.setAttribute("ACTION_CONFIRM_DELETE", ImportProfileAction.ACTION_CONFIRM_DELETE); %>