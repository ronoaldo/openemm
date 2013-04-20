<%-- checked --%>
<%@ page import="org.agnitas.web.forms.MailingBaseForm" %>
<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<html:form action="/mailingbase">
    <html:hidden property="mailingID"/>
    <html:hidden property="action"/>
    <html:hidden property="isTemplate"/>

    <div class="new_mailing_start_description">
        <% if ((Integer) request.getAttribute("isTemplate") == 0) { %>
        <bean:message key="Mailing"/>:&nbsp;<%= request.getAttribute("tmpShortname") %> <br>
        <bean:message key="mailing.MailingDeleteQuestion"/>
        <% } else { %>
        <bean:message key="Template"/>:&nbsp;<%= request.getAttribute("tmpShortname") %> <br>
        <bean:message key="mailing.Delete_Template_Question"/>
        <% } %>
    </div>
    <div class="remove_element_button_container">
        <div class="greybox_small_top"></div>
        <div class="greybox_small_content">
            <div class="new_mailing_step1_left_column">
                <input type="hidden" id="delete" name="delete" value=""/>

                <div class="big_button"><a href="#"
                                           onclick="document.getElementById('delete').value='delete'; document.mailingBaseForm.submit(); return false;"><span><bean:message
                        key="button.Delete"/></span></a></div>
            </div>
            <div class="new_mailing_step1_right_column">
                <div class="big_button"><a
                        href="<html:rewrite page='<%= new String(\"/mailingbase.do?action=\" + ((MailingBaseForm)session.getAttribute("mailingBaseForm")).getPreviousAction() + \"&mailingID=\" + request.getAttribute("tmpMailingID")) %>'/>"><span><bean:message
                        key="button.Cancel"/></span></a></div>
            </div>
        </div>
        <div class="greybox_small_bottom"></div>
    </div>


</html:form>