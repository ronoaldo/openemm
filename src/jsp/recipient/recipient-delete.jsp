<%@ page import="org.agnitas.web.RecipientAction" %>
<%@ page import="org.agnitas.web.RecipientForm" %>
<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    RecipientForm aForm = (RecipientForm) session.getAttribute("recipientForm");
    int cancelAction = RecipientAction.ACTION_VIEW;
    if(aForm.getFromListPage()) {
        cancelAction = RecipientAction.ACTION_LIST;
    }
%>
<html:form action="/recipient">
    <html:hidden property="recipientID"/>
    <html:hidden property="action" value="${ACTION_DELETE}"/>
    <html:hidden property="user_type"/>
    <html:hidden property="user_status"/>
    <html:hidden property="listID"/>

    <c:set var="cancelAction" value="<%= cancelAction %>" scope="page"/>

    <div class="new_mailing_start_description recipient_delete_name_container"><bean:message
            key="Recipient"/>:&nbsp;${recipientForm.firstname} ${recipientForm.lastname}<br><bean:message
            key="recipient.confirm_delete"/></div>
    <div class="remove_element_button_container">
        <div class="greybox_small_top"></div>
        <div class="greybox_small_content">
            <div class="new_mailing_step1_left_column">
                <input type="hidden" id="kill" name="kill" value=""/>

                <div class="big_button"><a href="#"
                                           onclick="document.getElementById('kill').value='true'; document.recipientForm.submit(); return false;"><span><bean:message
                        key="button.Delete"/></span></a></div>
            </div>
            <div class="new_mailing_step1_right_column">
                <div class="big_button"><a
                        href="<html:rewrite page="/recipient.do?action=${cancelAction}&recipientID=${recipientForm.recipientID}&user_type=${recipientForm.user_type}&user_status=${recipientForm.user_status}&listID=${recipientForm.listID}"/>"><span><bean:message
                        key="button.Cancel"/></span></a></div>
            </div>
        </div>
        <div class="greybox_small_bottom"></div>
    </div>


</html:form>
