<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<html:form action="/exportwizard">
    <html:hidden property="action"/>
    <html:hidden property="exportPredefID"/>

    <div class="grey_box_container">
        <div class="grey_box_top"></div>
        <div class="grey_box_content">
            <div class="grey_box_left_column">
                <label for="mailing_name"><bean:message key="default.Name"/>:</label>
                <html:text styleId="mailing_name" property="shortname" maxlength="99" size="42"/>
            </div>
            <div class="grey_box_center_column">
                <label for="mailing_name"><bean:message key="default.description"/>:</label>
                <html:textarea styleId="mailing_description" property="description" rows="5" cols="32"/>
            </div>
            <div class="grey_box_right_column"></div>
        </div>
        <div class="grey_box_bottom"></div>
    </div>

    <div class="button_container export_step2_buttons_panel">
        <div class="action_button"><a href="#" onclick="document.exportWizardForm.submit();"><span><bean:message key="button.Save"/></span></a></div>
        <div class="action_button"><bean:message key="export.ExportDefinition"/>:</div>
    </div>

</html:form>