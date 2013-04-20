<%@ page import="org.agnitas.web.forms.NewImportWizardForm" %>
<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<% request.setAttribute("sidemenu_active", "Recipients"); %>
<% request.setAttribute("sidemenu_sub_active", "import.csv_upload"); %>
<% request.setAttribute("agnTitleKey", "import.UploadSubscribers"); %>
<% request.setAttribute("agnSubtitleKey", "import.UploadSubscribers"); %>
<% request.setAttribute("agnNavigationKey", "ImportProfileOverview"); %>
<% request.setAttribute("agnHighlightKey", "import.Wizard"); %>
<% request.setAttribute("agnHelpKey", new String("importStep2")); %>
<%
    NewImportWizardForm newImportWizardForm = (NewImportWizardForm) session.getAttribute("newImportWizardForm");
    request.setAttribute("size", newImportWizardForm.getAllMailingLists().size());
%>
<agn:CheckLogon/>