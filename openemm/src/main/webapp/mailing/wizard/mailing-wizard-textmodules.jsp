<%--checked --%>
<%@ page language="java" import="org.agnitas.web.MailingWizardAction" contentType="text/html; charset=utf-8" %>
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
        <li><span class="step_active">8</span></li>
        <li><span>9</span></li>
        <li><span>10</span></li>
        <li><span>11</span></li>
    </ul>

    <p><bean:message key="mailing.TextModules"/></p><br>
<html:form action="/mwTextmodules">

    <html:hidden property="action"/>
    <div class="tooltiphelp" id="textmodulesmsg"> <bean:message key="mailing.wizard.TextModulesMsg"/>:</div>
                <script type="text/javascript">
					var hb1 = new HelpBalloon({
						dataURL: 'help_${helplanguage}/mailingwizard/step_08/TextModulesMsg.xml'
						});
						$('textmodulesmsg').appendChild(hb1.icon);
				</script>

   <div class="assistant_step7_button_container">
             <div class="action_button"><a href="#"
                                              onclick="document.mailingWizardForm.action.value='${ACTION_FINISH}'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Finish"/></span></a></div>
            <div class="action_button"><a href="#"
                                              onclick="document.mailingWizardForm.action.value='skip'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Skip"/></span></a></div>
            <div class="action_button"><a href="#"
                                              onclick="document.mailingWizardForm.action.value='${ACTION_TEXTMODULE}'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Proceed"/></span></a></div>
            <div class="action_button"><a href="#"
                                              onclick="document.mailingWizardForm.action.value='${ACTION_TEXTMODULES_PREVIOUS}'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Back"/></span></a></div>
        </div>
</html:form>
               </div>

