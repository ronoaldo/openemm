<%-- checked --%>
<%@ page language="java" import="org.agnitas.web.MailloopAction" contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.web.MailloopForm" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%
    MailloopForm aForm = (MailloopForm) request.getAttribute("mailloopForm");
    int cancelAction = MailloopAction.ACTION_VIEW;
    if(aForm.getFromListPage()){
        cancelAction = MailloopAction.ACTION_LIST;
    }
%>

<html:form action="/mailloop">
    <html:hidden property="mailloopID"/>
    <html:hidden property="action"/>

    <div class="new_mailing_start_description"><bean:message
            key="settings.Mailloop"/>:&nbsp;<%= request.getAttribute("tmpShortname") %> <br>
        <bean:message key="settings.mailloop.delete"/>
    </div>
    <div class="remove_element_button_container">
        <div class="greybox_small_top"></div>
        <div class="greybox_small_content">
            <div class="new_mailing_step1_left_column">
                <input type="hidden" id="inactive" name="inactive" value=""/>
                <div class="big_button"><a href="#"
                                           onclick="document.getElementById('inactive').value='inactive';document.mailloopForm.submit(); return false;"><span><bean:message
                        key="button.Delete"/></span></a></div>
            </div>
            <div class="new_mailing_step1_right_column">
                <div class="big_button"><a
                        href="<html:rewrite page='<%= new String("/mailloop.do?action=" + cancelAction + "&mailloopID="+request.getAttribute("tmpLoopID")) %>'/>"><span><bean:message
                        key="button.Cancel"/></span></a></div>
            </div>
        </div>
        <div class="greybox_small_bottom"></div>
    </div>


</html:form>