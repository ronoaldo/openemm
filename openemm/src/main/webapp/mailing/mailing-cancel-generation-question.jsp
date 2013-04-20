<%--checked --%>
<%@ page language="java" import="org.agnitas.web.MailingSendAction" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<agn:CheckLogon/>

<agn:Permission token="mailing.send.show"/>

<%
    int tmpMailingID = (Integer) request.getAttribute("tmpMailingID");
%>


<html:form action="/mailingsend">
    <html:hidden property="mailingID"/>
    <html:hidden property="action"/>

    <div class="new_mailing_start_description"><bean:message key="mailing.generation.cancel.question"/></div>
    <div class="remove_element_button_container">
        <div class="greybox_small_top"></div>
        <div class="greybox_small_content">
            <div class="new_mailing_step1_left_column">
                <input type="hidden" id="kill" name="kill" value=""/>

                <div class="big_button"><a href="#"
                                           onclick="document.getElementById('kill').value='true'; document.mailingSendForm.submit(); return false;"><span><bean:message
                        key="default.Yes"/></span></a></div>
            </div>
            <div class="new_mailing_step1_right_column">
                <div class="big_button"><a
                        href="<html:rewrite page="/mailingsend.do?action=${ACTION_VIEW_SEND}&mailingID=${tmpMailingID}"/>"><span><bean:message
                        key="default.No"/></span></a></div>
            </div>
        </div>
        <div class="greybox_small_bottom"></div>
    </div>

</html:form>

