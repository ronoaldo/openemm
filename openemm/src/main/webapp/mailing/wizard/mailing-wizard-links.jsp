<%--checked --%>
<%@ page language="java" import="org.agnitas.web.MailingWizardAction" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>


<div class="new_mailing_content">

    <ul class="new_mailing_step_display">
        <li class="step_display_first"><span>1</span></li>
        <li><span>2</span></li>
        <li><span>3</span></li>
        <li><span>4</span></li>
        <li><span>5</span></li>
        <li><span>6</span></li>
        <li><span>7</span></li>
        <li><span>8</span></li>
        <li><span class="step_active">9</span></li>
        <li><span>10</span></li>
        <li><span>11</span></li>
    </ul>

    <p><bean:message key="mailing.Trackable_Links"/>:</p><br>
<html:form action="/mwLinks" focus="shortname" enctype="application/x-www-form-urlencoded">
    <html:hidden property="action"/>


    <bean:message key="mailing.wizard.MeasureLinksDescriptionMsg"/>


      <div class="assistant_step7_button_container">
             <div class="action_button"><a href="#"
                                              onclick="document.mailingWizardForm.action.value='${ACTION_FINISH}'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Finish"/></span></a></div>
            <div class="action_button"><a href="#"
                                              onclick="document.mailingWizardForm.action.value='${ACTION_TO_ATTACHMENT}'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Skip"/></span></a></div>
            <div class="action_button"><a href="#"
                                              onclick="document.mailingWizardForm.action.value='${ACTION_MEASURELINK}'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Proceed"/></span></a></div>
            <div class="action_button"><a href="#"
                                              onclick="document.mailingWizardForm.action.value='previous'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Back"/></span></a></div>
        </div>

</html:form>
</div>
