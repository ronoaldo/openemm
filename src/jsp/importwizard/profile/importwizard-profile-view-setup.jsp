<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.web.ImportProfileAction" %>
<%@ page import="org.agnitas.web.forms.ImportProfileForm" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<agn:Permission token="mailinglist.show"/>

<%
    ImportProfileForm aForm = (ImportProfileForm) session.getAttribute("importProfileForm");
%>

<% request.setAttribute("sidemenu_active", "Recipients"); %>
<% request.setAttribute("sidemenu_sub_active", "import.csv_upload"); %>
<% if (aForm.getProfileId() == 0) { %>
<% request.setAttribute("agnTitleKey", "import.NewImportProfile"); %>
<% request.setAttribute("agnSubtitleKey", "import.NewImportProfile"); %>
<% request.setAttribute("agnNavigationKey", "ImportProfileNew"); %>
<% request.setAttribute("agnHighlightKey", "import.NewImportProfile"); %>
<% request.setAttribute("agnHelpKey", new String("newImportProfile")); %>
<% } else { %>
<% request.setAttribute("agnTitleKey", "import.ImportProfile"); %>
<% request.setAttribute("agnSubtitleKey", "import.ImportProfile"); %>
<% request.setAttribute("agnNavigationKey", "ImportProfile"); %>
<% request.setAttribute("agnHighlightKey", "import.EditImportProfile"); %>
<% request.setAttribute("agnNavHrefAppend", "&profileId=" + aForm.getProfileId()); %>
<% request.setAttribute("agnSubtitleValue", aForm.getProfile().getName()); %>
<% request.setAttribute("agnHelpKey", new String("manageProfile")); %>
<% } %>
<% request.setAttribute("ACTION_CONFIRM_DELETE", ImportProfileAction.ACTION_CONFIRM_DELETE); %>