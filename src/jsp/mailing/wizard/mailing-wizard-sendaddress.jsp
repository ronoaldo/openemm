<%--checked --%>
<%@ page language="java" import="org.agnitas.web.MailingWizardAction" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>




<div class="new_mailing_content">

    <ul class="new_mailing_step_display">
        <li class="step_display_first"><span>1</span></li>
        <li><span>2</span></li>
        <li><span>3</span></li>
        <li><span class="step_active">4</span></li>
        <li><span>5</span></li>
        <li><span>6</span></li>
        <li><span>7</span></li>
        <li><span>8</span></li>
        <li><span>9</span></li>
        <li><span>10</span></li>
        <li><span>11</span></li>
    </ul>

    <p>

    <div class="tooltiphelp" id="sendaddress"><bean:message key="mailing.SendAddressMsg"/>:</div>
    </p><br>
    <script type="text/javascript">
        var hb1 = new HelpBalloon({
            dataURL: 'help_${helplanguage}/mailingwizard/step_04/SendAddressMsg.xml'
        });
        $('sendaddress').appendChild(hb1.icon);
    </script>
    <html:form action="/mwSendaddress" focus="senderEmail">
        <html:hidden property="action"/>



        <div class="assistant_step7_form_item">
            <label for="senderEmail"><bean:message key="mailing.Sender_Adress"/>:&nbsp;</label>
            <html:text styleId="senderEmail" property="senderEmail" maxlength="99" size="34"/>
                <%--<a href="#" class="info_bubble" ></a>--%>
        </div>
        <div class="assistant_step7_form_item">
            <label for="senderFullname"><bean:message key="mailing.SenderFullname"/>:&nbsp;</label>
            <html:text styleId="senderFullname" property="senderFullname" maxlength="99" size="34"/>
                <%--<a href="#" class="info_bubble" ></a>--%>
        </div>
        <div class="assistant_step7_form_item">
            <label for="replyEmail"><bean:message key="mailing.ReplyEmail"/>:&nbsp;</label>
            <html:text styleId="replyEmail" property="replyEmail" maxlength="99" size="34"/>
                <%--<a href="#" class="info_bubble" ></a>--%>
        </div>
        <div class="assistant_step7_form_item">
            <label for="replyFullname"><bean:message key="mailing.ReplyFullName"/>:&nbsp;</label>
            <html:text styleId="replyFullname" property="replyFullname" maxlength="99" size="34"/>
                <%--<a href="#" class="info_bubble" ></a>--%>
        </div>


        <div class="assistant_step7_button_container">
            <div class="action_button"><a href="#"
                                              onclick="document.mailingWizardForm.action.value='${ACTION_SENDADDRESS}'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Proceed"/></span></a></div>
            <div class="action_button"><a href="#"
                                              onclick="document.mailingWizardForm.action.value='previous'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Back"/></span></a></div>
        </div>
    </html:form>
</div>