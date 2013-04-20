<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%
    String tmpShortname = (String) request.getAttribute("agnSubtitleValue");
%>

<html:form action="/salutation">
    <html:hidden property="salutationID"/>
    <html:hidden property="action"/>

    <div class="new_mailing_start_description"><bean:message
            key="recipient.Salutation"/>:&nbsp;<%= tmpShortname %><br><bean:message key="settings.DeleteSalutationQuestion"/>
    </div>
    <div class="remove_element_button_container">
        <div class="greybox_small_top"></div>
        <div class="greybox_small_content">
            <div class="new_mailing_step1_left_column">
                <input type="hidden" id="kill" name="kill" value=""/>

                <div class="big_button"><a href="#"
                                           onclick="document.getElementById('kill').value='kill'; document.salutationForm.submit(); return false;"><span><bean:message
                        key="button.Delete"/></span></a></div>
            </div>
            <div class="new_mailing_step1_right_column">
                <div class="big_button"><a
                        href="<html:rewrite page="/salutation.do?action=${ACTION_LIST}&salutationID=${salutationForm.salutationID}"/>"><span><bean:message
                        key="button.Cancel"/></span></a></div>
            </div>
        </div>
        <div class="greybox_small_bottom"></div>
    </div>
</html:form>
