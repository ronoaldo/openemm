<%--checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script type="text/javascript" src="${emmLayoutBase.jsURL}/option_title.js"></script>

<div class="new_mailing_content">

    <ul class="new_mailing_step_display">
        <li class="step_display_first"><span>1</span></li>
        <li><span class="step_active">2</span></li>
        <li><span>3</span></li>
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

    <div class="tooltiphelp" id="choosetemplatemsg"><bean:message key="mailing.wizard.ChooseTemplateMsg"/></div>
    </p><br>
    <script type="text/javascript">
        var hb1 = new HelpBalloon({
            dataURL: 'help_${helplanguage}/mailingwizard/step_02/ChooseTemplateMsg.xml'
        });
        $('choosetemplatemsg').appendChild(hb1.icon);
    </script>
    <html:form styleId="mwiz" action="/mwTemplate" focus="shortname">
        <html:hidden property="action"/>

        <div class="assistant_step7_form_item">
            <html:select property="mailing.mailTemplateID">
                <html:option value="0"><bean:message key="mailing.No_Template"/></html:option>
                <c:forEach var="template" items="${templates}">
                    <html:option value="${template.id}">
                        ${template.shortname}
                    </html:option>
                </c:forEach>
            </html:select>&nbsp;
        </div>


        <div class="assistant_step7_button_container">
            <div class="action_button"><a href="#"
                                              onclick="document.mailingWizardForm.action.value='${ACTION_TEMPLATE}'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Proceed"/></span></a></div>
            <div class="action_button"><a href="#"
                                              onclick="document.mailingWizardForm.action.value='previous'; document.mailingWizardForm.submit(); return false;"><span><bean:message
                    key="button.Back"/></span></a></div>
        </div>

    </html:form>
</div>

<script language="javascript" type="text/javascript">
    addTitleToOptions();
</script>
