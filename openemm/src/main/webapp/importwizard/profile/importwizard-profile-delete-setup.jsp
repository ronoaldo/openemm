<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.web.forms.ImportProfileForm" %>

<% ImportProfileForm aForm = (ImportProfileForm) session.getAttribute("importProfileForm"); %>

<% request.setAttribute("sidemenu_active", "Recipients"); %>
<% request.setAttribute("sidemenu_sub_active", "import.csv_upload"); %>
<% if (aForm.getFromListPage()) { %>
<% request.setAttribute("agnTitleKey", "import.ImportProfile"); %>
<% request.setAttribute("agnSubtitleKey", "import.ImportProfile"); %>
<% request.setAttribute("agnNavigationKey", "ImportProfileOverview"); %>
<% request.setAttribute("agnHighlightKey", "import.ProfileAdministration"); %>
<% } else { %>
<% request.setAttribute("agnTitleKey", "import.ImportProfile"); %>
<% request.setAttribute("agnSubtitleKey", "import.ImportProfile"); %>
<% request.setAttribute("agnNavigationKey", "ImportProfile"); %>
<% request.setAttribute("agnHighlightKey", "import.ImportProfile"); %>
<%
    request.setAttribute("agnNavHrefAppend", "&profileId=" + aForm.getProfileId()); %>
<% } %>
<% request.setAttribute("agnHelpKey", new String("manageProfile")); %>