<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.web.ExportWizardAction, org.agnitas.web.forms.ExportWizardForm" %>
<%@ page import="org.agnitas.util.AgnUtils" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%
    int tmpExportPredefID = 0;
    Object form = session.getAttribute("exportWizardForm");
    if (form != null) {
        tmpExportPredefID = ((ExportWizardForm) form).getExportPredefID();
         String tmpShortname = ((ExportWizardForm) form).getShortname();
        request.setAttribute("tmpShortname", tmpShortname);
    }
%>

<html:form action="/exportwizard">
    <html:hidden property="action"/>
    <html:hidden property="exportPredefID"/>

    <div class="new_mailing_start_description"><bean:message
            key="export.ExportDefinition"/>:&nbsp;${tmpShortname} <br>
        <bean:message key="export.WizardDeleteQuestion"/>
    </div>
    <div class="remove_element_button_container">
        <div class="greybox_small_top"></div>
        <div class="greybox_small_content">
            <div class="new_mailing_step1_left_column">
                <div class="big_button"><a href="#"
                                           onclick="document.exportWizardForm.submit(); return false;"><span><bean:message
                        key="button.Delete"/></span></a></div>
            </div>
            <div class="new_mailing_step1_right_column">
                <div class="big_button"><a
                        href="<html:rewrite page='<%= new String("/exportwizard.do?action=" + ExportWizardAction.ACTION_LIST + "&exportPredefID=" + tmpExportPredefID) %>'/>"><span><bean:message
                        key="button.Cancel"/></span></a></div>
            </div>
        </div>
        <div class="greybox_small_bottom"></div>
    </div>

</html:form>
