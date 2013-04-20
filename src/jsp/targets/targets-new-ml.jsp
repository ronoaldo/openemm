<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<html:form action="/mailinglist" method="post">
    <input type="hidden" name="action" value="2">
    <input type="hidden" name="targetID" value='<%= request.getAttribute("tmpTargetID") %>'>
    <input type="hidden" name="MTP0" value="on">

    <div class="new_mailing_start_description"><bean:message
            key="target.MailingListFromTargetQuestion"/></div>
    <div class="remove_element_button_container">
        <div class="greybox_small_top"></div>
        <div class="greybox_small_content">
            <div class="new_mailing_step1_left_column">
                <div class="big_button"><a href="#"
                                           onclick="document.mailinglistForm.submit(); return false;"><span><bean:message
                        key="button.OK"/></span></a></div>
            </div>
            <div class="new_mailing_step1_right_column">
                <div class="big_button"><a
                        href="<html:rewrite page="/target.do?action=${ACTION_VIEW}&targetID=${targetForm.targetID}"/>"><span><bean:message
                        key="button.Cancel"/></span></a></div>
            </div>
        </div>
        <div class="greybox_small_bottom"></div>
    </div>

</html:form>
