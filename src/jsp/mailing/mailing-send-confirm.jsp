<%-- checked --%>
<%@ page language="java" import="org.agnitas.web.MailingSendAction" contentType="text/html; charset=utf-8"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

    <html:form action="/mailingsend">
        <html:hidden property="mailingID"/>
        <html:hidden property="action"/>

        <html:hidden property="sendDate"/>
        <html:hidden property="sendHour"/>
        <html:hidden property="sendMinute"/>


        <html:hidden property="stepping"/>
        <html:hidden property="blocksize"/>

        <div class="new_mailing_start_description">
            <bean:message key="mailing.send.confirm"/>
        </div>


    <div class="remove_element_button_container">
        <div class="greybox_small_top"></div>
        <div class="greybox_small_content">
            <div class="new_mailing_step1_left_column">
                <input type="hidden" id="send" name="send" value=""/>

                <div class="big_button"><a href="#"
                                           onclick="document.getElementById('send').value='send'; document.mailingSendForm.submit(); return false;"><span><bean:message
                        key="button.Send"/></span></a></div>
            </div>
            <div class="new_mailing_step1_right_column">
                <div class="big_button">
                <html:link
                page='<%= new String(\"/mailingsend.do?action=\" + MailingSendAction.ACTION_VIEW_SEND + \"&mailingID=\" + request.getAttribute("tmpMailingID")) %>'><span><bean:message
                        key="button.Cancel"/></span></html:link></div>
            </div>
        </div>
        <div class="greybox_small_bottom"></div>
    </div>

    </html:form>
