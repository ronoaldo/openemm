<%-- checked --%>
<%@ page language="java" import="org.agnitas.web.TargetAction" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>


<div class="new_mailing_start_description">
    <bean:message
            key="target.delete.recipients.question.first"/>&nbsp;<%= request.getAttribute("tmpNumOfRecipients") %>&nbsp;<bean:message
        key="target.delete.recipients.question.last"/>
</div>
<div class="remove_element_button_container">
    <div class="greybox_small_top"></div>
    <div class="greybox_small_content">
        <div class="new_mailing_step1_left_column">
            <input type="hidden" id="kill" name="kill" value=""/>

            <div class="big_button">
                <a href="<html:rewrite page="/target.do?action=${ACTION_DELETE_RECIPIENTS}${agnNavHrefAppend}"/>"><span><bean:message
                        key="button.Delete"/></span></a>

            </div>
        </div>
        <div class="new_mailing_step1_right_column">
            <div class="big_button"><a
                    href="<html:rewrite page="/target.do?action=${ACTION_VIEW}${agnNavHrefAppend}"/>"><span><bean:message
                    key="button.Cancel"/></span></a></div>
        </div>
    </div>
    <div class="greybox_small_bottom"></div>
</div>
