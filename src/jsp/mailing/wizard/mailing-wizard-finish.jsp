<%--checked --%>
<%@ page language="java" import="org.agnitas.web.MailingBaseAction, org.agnitas.web.MailingSendAction" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%
    Integer tmpMailingID = (Integer) request.getAttribute("tmpMailingID");
%>


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
        <li><span>9</span></li>
        <li><span>10</span></li>
        <li><span class="step_active">11</span></li>
    </ul>
</div>

    <div class="grey_box_container">
        <div class="grey_box_top"></div>
        <div class="grey_box_content">
            <div class="send_page_panel_text"><bean:message key="mailing.SendPreviewMessage"></bean:message></div>
            <div class="button_grey_box_container">
                <div class="action_button no_margin_right no_margin_bottom">
                    <html:link
                            page='<%= new String(\"/mailingsend.do?action=\" + MailingSendAction.ACTION_PREVIEW_SELECT + \"&mailingID=\" + tmpMailingID) %>'>
                        <span><bean:message key="mailing.Preview"/></span>
                    </html:link>
                </div>
            </div>
        </div>
        <div class="grey_box_bottom"></div>
    </div>

    <div class="grey_box_container">
        <div class="grey_box_top"></div>
        <div class="grey_box_content">
            <bean:message key="mailing.MailingWizardReadyMsg"/>!
            <bean:message key="mailing.TestAdminDeliveryMsg"></bean:message>
        </div>
        <div class="grey_box_bottom"></div>
    </div>

<html:form action="/mwFinish" focus="shortname">
	<html:hidden property="action"/>

    <div class="grey_box_container">
        <div class="grey_box_top"></div>
        <div class="grey_box_content">
            <agn:ShowByPermission token="mailing.send.admin">
                <div class="send_page_button_text"><bean:message key="adminMail"/>:</div>
                <div class="action_button send_page_button" id="adminButton" style="float:left;">
                    <html:link
                            page='<%= new String(\"/mailingsend.do?action=\" + MailingSendAction.ACTION_SEND_ADMIN + \"&mailingID=\" + tmpMailingID) %>'>
                        <span><bean:message key="button.Send"/></span>
                    </html:link>
                </div>
            </agn:ShowByPermission>

            <agn:ShowByPermission token="mailing.send.test">
                <div class="send_page_button_text"><bean:message key="testMail"/>:</div>
                <div class="action_button send_page_button" id="testButton" style="float:left;">
                    <html:link
                            page='<%= new String(\"/mailingsend.do?action=\" + MailingSendAction.ACTION_SEND_TEST + \"&mailingID=\" + tmpMailingID) %>'>
                        <span><bean:message key="button.Send"/></span>
                    </html:link>
                </div>
            </agn:ShowByPermission>
        </div>
        <div class="grey_box_bottom"></div>
    </div>

    <div class="grey_box_container">
        <div class="grey_box_top"></div>
        <div class="grey_box_content">

            <div class="send_page_panel_text"><b><bean:message key="mailing.wizard.ClickFinishMsg"/>.</b></div>


            <div class="button_grey_box_container">
                <div class="action_button no_margin_right no_margin_bottom"><html:link
                        page='<%=new String(\"/mailingbase.do?action=\" + MailingBaseAction.ACTION_VIEW) + \"&mailingID=\" + tmpMailingID%>'><span><bean:message
                        key="button.Finish"/></span></html:link>
                </div>
            </div>

        </div>
        <div class="grey_box_bottom"></div>
    </div>


</html:form>
