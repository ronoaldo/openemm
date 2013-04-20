<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.service.NewImportWizardService" %>
<%@ page import="org.agnitas.web.NewImportWizardAction" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<% request.setAttribute("DOWNLOAD_ACTION", NewImportWizardAction.ACTION_DOWNLOAD_CSV_FILE); %>
<% request.setAttribute("RESULT", NewImportWizardService.RESULT_TYPE); %>
<% request.setAttribute("VALID", NewImportWizardService.RECIPIENT_TYPE_VALID); %>
<% request.setAttribute("INVALID", NewImportWizardService.RECIPIENT_TYPE_INVALID); %>
<% request.setAttribute("FIXED", NewImportWizardService.RECIPIENT_TYPE_FIXED_BY_HAND); %>
<% request.setAttribute("DUPLICATE", NewImportWizardService.RECIPIENT_TYPE_DUPLICATE_RECIPIENT); %>

<% request.setAttribute("sidemenu_active", "Recipients"); %>
<% request.setAttribute("sidemenu_sub_active", "import.csv_upload"); %>
<% request.setAttribute("agnTitleKey", "import.UploadSubscribers"); %>
<% request.setAttribute("agnSubtitleKey", "import.UploadSubscribers"); %>
<% request.setAttribute("agnNavigationKey", "ImportProfileOverview"); %>
<% request.setAttribute("agnHighlightKey", "import.Wizard"); %>
<% request.setAttribute("agnHelpKey", new String("importStep4")); %>

<agn:CheckLogon/>

<agn:Permission token="wizard.import"/>
