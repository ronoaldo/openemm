<%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, java.util.*"
         contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.web.MailinglistAction" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="ACTION_CONFIRM_DELETE" value="<%=MailinglistAction.ACTION_MAILINGLIST_RECIPIENTS_CONFIRM_DELETE%>" />

<html:form action="/mailinglist">
    <html:hidden property="mailinglistID"/>
    <html:hidden property="action"/>
    <html:hidden property="targetID"/>
    <html:hidden property="previousAction" value="${ACTION_CONFIRM_DELETE}"/>
    <html:hidden property="activeOnly"/>
    <html:hidden property="notAdminsAndTest"/>

    <agn:ShowByPermission token="mailinglist.recipients.delete">
        <div class="grey_box_container">
            <div class="grey_box_top"></div>
            <div class="grey_box_content">
                <bean:message key="mailinglist.delete.recipients.question"/>
            </div>
            <div class="grey_box_bottom"></div>
        </div>

        <div class="button_container">
            <div class="action_button">
                <a href="<html:rewrite page="/mailinglist.do?action=2&mailinglistID=${mailinglistForm.mailinglistID}"/>">
                    <span><bean:message key="button.Cancel"/></span>
                </a>
            </div>
            <div class="action_button">
                <a href="#" onclick="document.mailinglistForm.submit(); return false;">
                    <span><bean:message key="default.Yes"/></span>
                </a>
            </div>
        </div>
    </agn:ShowByPermission>
</html:form>