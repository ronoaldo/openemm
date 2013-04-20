<%--checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.web.forms.MailingBaseForm" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%
    MailingBaseForm aForm = (MailingBaseForm) session.getAttribute("mailingBaseForm");
    aForm.setIsTemplate(false);
%>
<html:form action="/mwStart" styleId="wizardForm">

    <div class="new_mailing_start_description"><bean:message key="mailing.NewMailingMethod"/>:</div>
    <div class="new_mailing_button_container">
        <div class="greybox_small_top"></div>
        <div class="greybox_small_content">
            <div class="new_mailing_step1_left_column">
                <div class="big_button"><a href="#" onclick="submitUseWizard(0)"><span><bean:message key="mailing.wizard.Normal"/></span></a></div>
                <html:link styleClass="assistant_icon" page="/mailingbase.do?action=${ACTION_NEW}&mailingID=0&isTemplate=false">
                    <img src="${emmLayoutBase.imagesURL}/icon_profis.png" alt=""/>
                </html:link>
                <html:link page="/mailingbase.do?action=${ACTION_NEW}&mailingID=0&isTemplate=false" styleClass="simple_link">
                    <bean:message key="mailing.wizard.NormalDescription"/>
                </html:link>
            </div>
            <div class="new_mailing_step1_right_column">
                <div class="big_button"><a href="#" onclick="submitUseWizard(1)"><span><bean:message key="mailing.Wizard"/></span></a></div>
                <html:link styleClass="assistant_icon" page="/mwStart.do?action=${ACTION_START}">
                    <img src="${emmLayoutBase.imagesURL}/icon_assistent.png" alt=""/>
                </html:link>
                <html:link page="/mwStart.do?action=${ACTION_START}" styleClass="simple_link">
                    <bean:message key="mailing.wizard.WizardDescription"/>
                </html:link>
            </div>
        </div>
        <div class="greybox_small_bottom"></div>
    </div>

    <script language="JavaScript" type="text/javascript">
    	function submitUseWizard(useWizard) {
    		if(useWizard) {
    			action = "${ACTION_START}";
    			actionForward = "start";	
    		} else {
    			action = "withoutWizard";
    			actionForward = "withoutWizard";
    		}
    		
    		document.getElementById('hidden_action').value = action;
    		document.getElementById('hidden_action_forward').value = actionForward;
    		
    		document.getElementById('wizardForm').submit();
    	}
    </script>
    
    <input type="hidden" id="hidden_action" name="action" value="" />
    <input type="hidden" id="hidden_action_forward" name="action_forward" value="" />
    <input type="hidden" name="mailingID" value="0" />
    <input type="hidden" name="isTemplate" value="false" />

</html:form>
