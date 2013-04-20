<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.web.SalutationForm" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<agn:Permission token="settings.show"/>

<%
    int tmpSalutationID = 0;
    String tmpShortname = new String("");

    if(request.getAttribute("salutationForm") != null) {
        tmpSalutationID = ((SalutationForm) request.getAttribute("salutationForm")).getSalutationID();
        tmpShortname = ((SalutationForm) request.getAttribute("salutationForm")).getShortname();
    }

    request.setAttribute("tmpSalutationID", tmpSalutationID);

    request.setAttribute("sidemenu_active", new String("Recipients"));
    request.setAttribute("sidemenu_sub_active", new String("settings.FormsOfAddress"));
    if(tmpSalutationID != 0) {
        request.setAttribute("agnNavigationKey", new String("SalutationsEdit"));
        request.setAttribute("agnHighlightKey", new String("settings.EditFormOfAddress"));
    } else {
        request.setAttribute("agnNavigationKey", new String("Salutations"));
        request.setAttribute("agnHighlightKey", new String("settings.NewFormOfAddress"));
    }
    request.setAttribute("agnSubtitleKey", new String("settings.FormOfAddress"));
    request.setAttribute("agnTitleKey", new String("settings.FormsOfAddress"));
    request.setAttribute("agnSubtitleValue", tmpShortname);
    request.setAttribute("agnNavHrefAppend", new String("&salutationID=" + tmpSalutationID));
%>
<% request.setAttribute("agnHelpKey", new String("salutationForms")); %>

