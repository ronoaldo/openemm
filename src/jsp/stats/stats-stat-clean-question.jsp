<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.web.MailingStatAction " %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>


<%
    Integer tmpMailingID = (Integer) request.getAttribute("tmpMailingID");
%>


<html:form action="mailing_stat">

    <html:hidden property="mailingID"/>
    <html:hidden property="action"/>
    <html:hidden property="targetID"/>
    <html:hidden property="netto"/>


    <div class="new_mailing_start_description"><bean:message
            key="statistic.DeleteAdminClicks"/><br><bean:message
            key="statistic.AreYouSure"/></div>
    <div class="remove_element_button_container">
        <div class="greybox_small_top"></div>
        <div class="greybox_small_content">
            <div class="new_mailing_step1_left_column">
                <div class="big_button">
                    <html:link
                            page='<%= new String( \"/mailing_stat.do?action=\" + MailingStatAction.ACTION_CLEAN + \"&mailingID=\"+tmpMailingID) %>'>
                        <span><bean:message key="button.OK"/></span></html:link>
                </div>
            </div>
            <div class="new_mailing_step1_right_column">
                <div class="big_button">
                    <html:link
                            page='<%= new String( \"/mailing_stat.do?action=\" + MailingStatAction.ACTION_MAILINGSTAT + \"&mailingID=\" + tmpMailingID ) %>'>
                        <span><bean:message key="button.Cancel"/></span></html:link>
                </div>
            </div>
        </div>
        <div class="greybox_small_bottom"></div>
    </div>

</html:form>