<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" buffer="32kb" errorPage="/error.jsp" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<html:form action="admin">
    <html:hidden property="adminID"/>
    <html:hidden property="action"/>

    <div class="new_mailing_start_description"><bean:message
            key="Admin"/>:&nbsp;${adminForm.username}<br><bean:message key="settings.AdminDeleteQuestion"/>
    </div>
    <div class="remove_element_button_container">
        <div class="greybox_small_top"></div>
        <div class="greybox_small_content">
            <div class="new_mailing_step1_left_column">
                <input type="hidden" id="kill" name="kill" value=""/>

                <div class="big_button"><a href="#"
                                           onclick="document.getElementById('kill').value='kill'; document.adminForm.submit(); return false;"><span><bean:message
                        key="button.Delete"/></span></a></div>
            </div>
            <div class="new_mailing_step1_right_column">
                <div class="big_button"><a
                        href="<html:rewrite page="/admin.do?action=${adminForm.previousAction}&adminID=${adminForm.adminID}"/>"><span><bean:message
                        key="button.Cancel"/></span></a></div>
            </div>
        </div>
        <div class="greybox_small_bottom"></div>
    </div>

</html:form>

