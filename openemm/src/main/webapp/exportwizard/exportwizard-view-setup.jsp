<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.web.ExportWizardAction" %>
<%@ page import="org.agnitas.web.forms.ExportWizardForm" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>
<agn:Permission token="wizard.export"/>

<% String tmpShortname=new String("");
if(session.getAttribute("exportWizardForm")!=null) {
    tmpShortname=((ExportWizardForm)session.getAttribute("exportWizardForm")).getShortname();
}
%>

<% request.setAttribute("sidemenu_active", new String("Recipients")); %>
<% request.setAttribute("sidemenu_sub_active", new String("export")); %>
<% request.setAttribute("agnTitleKey", new String("export")); %>
<% request.setAttribute("agnSubtitleKey", new String("export")); %>
<% request.setAttribute("agnSubtitleValue", tmpShortname); %>
<% request.setAttribute("agnNavigationKey", new String("subscriber_export")); %>
<% request.setAttribute("agnHighlightKey", new String("export.Wizard")); %>
<% request.setAttribute("agnHelpKey", new String("export")); %>

<c:set var="ACTION_QUERY" value="<%= ExportWizardAction.ACTION_QUERY %>" scope="request" />

