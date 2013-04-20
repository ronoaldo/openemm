<%--checked --%>
<%@ page language="java" import="org.agnitas.beans.Mailing, org.agnitas.web.MailingWizardAction" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>




<div class="new_mailing_content">

    <ul class="new_mailing_step_display">
        <li class="step_display_first"><span>1</span></li>
        <li><span>2</span></li>
        <li><span class="step_active">3</span></li>
        <li><span>4</span></li>
        <li><span>5</span></li>
        <li><span>6</span></li>
        <li><span>7</span></li>
        <li><span>8</span></li>
        <li><span>9</span></li>
        <li><span>10</span></li>
        <li><span>11</span></li>
    </ul>
    <p>

    <div class="tooltiphelp" id="mailingtypedescbefore"><bean:message key="mailing.MailingTypeDescBefore"/>:</div>
    </p><br>
    <script type="text/javascript">
        var hb1 = new HelpBalloon({
            dataURL: 'help_${helplanguage}/mailingwizard/step_03/MailingTypeDescBefore.xml'
        });
        $('mailingtypedescbefore').appendChild(hb1.icon);
    </script>
    <html:form action="/mwType">

        <html:hidden property="action"/>

        <div class="assistant_step7_form_item">
            &nbsp;&nbsp;<html:radio property="mailing.mailingType" style="vertical-align:text-top;"
                                    value="<%= Integer.toString(Mailing.TYPE_NORMAL) %>"/>&nbsp;<bean:message
                key="mailing.Normal_Mailing"/>
            <BR>
            &nbsp;&nbsp;<html:radio property="mailing.mailingType" style="vertical-align:text-top;"
                                    value="<%= Integer.toString(Mailing.TYPE_ACTIONBASED) %>"/>&nbsp;<bean:message
                key="mailing.Event_Mailing"/>
            <BR>
            &nbsp;&nbsp;<html:radio property="mailing.mailingType" style="vertical-align:text-top;"
                                    value="<%= Integer.toString(Mailing.TYPE_DATEBASED) %>"/>&nbsp;<bean:message
                key="mailing.Rulebased_Mailing"/>

            <BR>
            <BR>
            &nbsp;&nbsp;<bean:message key="mailing.MailingTypeDescAfter"/>
        </div>


        <div class="assistant_step7_button_container">
            <div class="action_button"><a href="#"
                                              onclick="document.mailingWizardForm.action.value='${ACTION_TYPE}'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Proceed"/></span></a></div>
            <div class="action_button"><a href="#"
                                              onclick="document.mailingWizardForm.action.value='${ACTION_TYPE_PREVIOUS}'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Back"/></span></a></div>
        </div>

    </html:form>
</div>

