<%--checked --%>
<%@ page language="java" import="org.agnitas.web.MailingWizardAction" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%--

<html:form action="/mwMailtype">
    <html:hidden property="action"/>
    
    <b><font color=#73A2D0><bean:message key="mailing.wizard.Step_5_of_11"/></font></b>
    
    <br>
    <img src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="400" height="10" border="0">
    <br>
    <div class="tooltiphelp" id="mailtypemessage"><b> <bean:message key="mailing.MailTypeMessage"/>:</b></div>
				<script type="text/javascript">
					var hb1 = new HelpBalloon({
						dataURL: 'help_${helplanguage}/mailingwizard/step_05/MailTypeMessage.xml' 		
						});
						$('mailtypemessage').appendChild(hb1.icon); 
				</script>  
    
    
    
    &nbsp;&nbsp;<html:radio property="emailFormat" style="vertical-align:text-bottom;" value="0"/>&nbsp;<bean:message key="mailing.Text"/>
    <BR>
    &nbsp;&nbsp;<html:radio property="emailFormat" style="vertical-align:text-bottom;" value="1"/>&nbsp;<bean:message key="mailing.Text_HTML"/>
    <BR>
    &nbsp;&nbsp;<html:radio property="emailFormat" style="vertical-align:text-bottom;" value="2"/>&nbsp;<bean:message key="mailing.Text_HTML_OfflineHTML"/>
    <BR>
    <BR> 



    <% // wizard navigation: %>
    <br>
    <table border="0" cellspacing="0" cellpadding="0" >
        <tr>
            <td>&nbsp;</td>
            <td align="right">
                &nbsp;
                <html:image src="button?msg=button.Back"  border="0" onclick="document.mailingWizardForm.action.value='previous'"/>
                &nbsp;
                <html:image src="button?msg=button.Proceed"  border="0" onclick='<%= \"document.mailingWizardForm.action.value='\" + MailingWizardAction.ACTION_MAILTYPE + \"'\" %>'/>
                &nbsp;
            </td>
        </tr>
    </table>         
</html:form>
--%>


<div class="new_mailing_content">

    <ul class="new_mailing_step_display">
        <li class="step_display_first"><span>1</span></li>
        <li><span>2</span></li>
        <li><span>3</span></li>
        <li><span>4</span></li>
        <li><span class="step_active">5</span></li>
        <li><span>6</span></li>
        <li><span>7</span></li>
        <li><span>8</span></li>
        <li><span>9</span></li>
        <li><span>10</span></li>
        <li><span>11</span></li>
    </ul>
    <p>

    <div class="tooltiphelp" id="mailtypemessage"><bean:message key="mailing.MailTypeMessage"/>:</div>
    </p><br>
    <script type="text/javascript">
        var hb1 = new HelpBalloon({
            dataURL: 'help_${helplanguage}/mailingwizard/step_05/MailTypeMessage.xml'
        });
        $('mailtypemessage').appendChild(hb1.icon);
    </script>
    <html:form action="/mwMailtype">
        <html:hidden property="action"/>


        <div class="assistant_step7_form_item">
            &nbsp;&nbsp;<html:radio property="emailFormat" style="vertical-align:text-top;"
                                    value="0"/>&nbsp;<bean:message key="mailing.Text"/>
            <BR>
            &nbsp;&nbsp;<html:radio property="emailFormat" style="vertical-align:text-top;"
                                    value="1"/>&nbsp;<bean:message key="mailing.Text_HTML"/>
            <BR>
            &nbsp;&nbsp;<html:radio property="emailFormat" style="vertical-align:text-top;"
                                    value="2"/>&nbsp;<bean:message key="mailing.Text_HTML_OfflineHTML"/>
        </div>


        <div class="assistant_step7_button_container">
            <div class="action_button"><a href="#"
                                              onclick="document.mailingWizardForm.action.value='${ACTION_MAILTYPE}'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Proceed"/></span></a></div>
            <div class="action_button"><a href="#"
                                              onclick="document.mailingWizardForm.action.value='previous'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Back"/></span></a></div>
        </div>
    </html:form>
</div>
