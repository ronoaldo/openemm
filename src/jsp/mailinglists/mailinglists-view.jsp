<%@ page import="org.agnitas.web.MailinglistAction" %>
<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="ACTION_VIEW" value="<%= MailinglistAction.ACTION_VIEW %>" />

<html:form action="/mailinglist" focus="shortname">
    <html:hidden property="mailinglistID"/>
    <html:hidden property="action"/>
    <html:hidden property="targetID"/>
    <html:hidden property="previousAction" value="${ACTION_VIEW}"/>
    <input type="hidden" name="save.x" value="0">

    <%--fix to disable form submit by clicking Enter button--%>
    <input type="text" readonly="readonly" class="display-none"/>

    <div class="grey_box_container">
        <div class="grey_box_top"></div>
        <div class="grey_box_content">
            <div class="grey_box_left_column">
                <label for="mailing_name"><bean:message key="default.Name"/>:</label>
                <html:text styleId="mailing_name" property="shortname" maxlength="99" size="42"/>
            </div>
            <div class="grey_box_center_column">
                <label for="mailing_name"><bean:message key="default.description"/>:</label>
                <html:textarea styleId="mailing_description" property="description" rows="5" cols="32"/>
            </div>
            <div class="grey_box_right_column"></div>
        </div>
        <div class="grey_box_bottom"></div>
    </div>
        <div class="button_container">
        <agn:ShowByPermission token="mailinglist.change">
            <input type="hidden" name="save" value="" id="save">
            <div class="action_button">
                <a href="#"
                   onclick="document.getElementById('save').value='save'; document.mailinglistForm.submit();return false;">
                    <span><bean:message key="button.Save"/></span>
                </a>
            </div>
        </agn:ShowByPermission>
        <agn:ShowByPermission token="mailinglist.delete">
            <c:if test="${mailinglistForm.mailinglistID != 0}">
                <input type="hidden" id="delete" name="delete" value=""/>

                <div class="action_button">
                    <a href="#"
                       onclick="document.getElementById('delete').value='true'; document.mailinglistForm.submit(); return false;"><span><bean:message
                            key="button.Delete"/></span></a>
                </div>
            </c:if>
        </agn:ShowByPermission>
        <div class="action_button"><bean:message key="Mailinglist"/>:</div>
    </div>
    <agn:ShowByPermission token="mailinglist.recipients.delete">
        <c:if test="${mailinglistForm.mailinglistID != 0}">
            <div class="button_container">
                <div align=right>
                    <html:link styleClass="target_view_link"
                               page="/mailinglist.do?action=9&mailinglistID=${mailinglistForm.mailinglistID}">
                        <bean:message key="mailinglist.delete.recipients"/>
                    </html:link>
                </div>
            </div>
        </c:if>
    </agn:ShowByPermission>
</html:form>
