<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<html:form action="/update">
    <html:hidden property="action"/>

    <div class="new_mailing_start_description"><bean:message
            key="update.question"/></div>
    <div class="remove_element_button_container">
        <div class="greybox_small_top"></div>
        <div class="greybox_small_content">
            <div class="new_mailing_step1_left_column">
                <div class="big_button"><a
                        href="<html:rewrite page="/update.do?action=${ACTION_LIST}"/>"><span><bean:message
                        key="default.Yes"/></span></a></div>
            </div>
            <div class="new_mailing_step1_right_column">
                <div class="big_button"><a href="#" onclick="history.back();return false;"><span><bean:message
                        key="default.No"/></span></a></div>
            </div>
        </div>
        <div class="greybox_small_bottom"></div>
    </div>
</html:form>
