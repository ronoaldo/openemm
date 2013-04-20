<%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, java.util.*"
         contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.web.MailinglistAction" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="ACTION_RECIPIENTS_DELETE" value="<%= MailinglistAction.ACTION_MAILINGLIST_RECIPIENTS_DELETE %>" />

<html:form action="/mailinglist" focus="shortname">
    <html:hidden property="mailinglistID"/>
    <html:hidden property="action"/>
    <html:hidden property="targetID"/>
    <html:hidden property="previousAction" value="${ACTION_RECIPIENTS_DELETE}"/>

    <div class="grey_box_container">
        <div class="grey_box_top"></div>
        <div class="grey_box_content">
            <html:checkbox styleId="active-only" property="activeOnly"/>
            <label for="active-only">
                <bean:message key="mailinglist.delete.recipients.active"/>
            </label>

            <br/>

            <html:checkbox styleId="not-admins-and-tests" property="notAdminsAndTest"/>
            <label for="not-admins-and-tests">
                <bean:message key="mailinglist.delete.recipients.noadmin"/>
            </label>
        </div>
        <div class="grey_box_bottom"></div>
    </div>

    <agn:ShowByPermission token="mailinglist.recipients.delete">
        <div class="button_container">
            <div align=right>
                <div class="action_button">
                    <a href="#" onclick="document.mailinglistForm.submit(); return false;">
                    <span><bean:message key="button.Delete"/></span></a>
                </div>
            </div>
        </div>
    </agn:ShowByPermission>
</html:form>


