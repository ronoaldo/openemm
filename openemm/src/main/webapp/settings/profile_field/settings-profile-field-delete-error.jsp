<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.web.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<html:form action="/profiledb">
    <html:hidden property="companyID"/>
    <html:hidden property="action"/>
    <html:hidden property="fieldname"/>
    <html:hidden property="targetsDependent"/>

    <div class="new_mailing_start_description">
        <bean:message
                key="settings.FieldName"/>:&nbsp;<%= ((ProfileFieldForm) request.getAttribute("profileFieldForm")).getFieldname() %>
        <br><br>
        <b><bean:message
                key="settings.ProfileFieldErrorMsg"/><br><%=((ProfileFieldForm) request.getAttribute("profileFieldForm")).getTargetsDependent()%>
        </b><br>
    </div>

    <br>

    <div class="button_container">
        <div class="action_button">
            <html:link
                    page='<%= new String(\"/profiledb.do?action=\" + ProfileFieldAction.ACTION_LIST) %>'><span><bean:message
                    key="button.Back"/></span></html:link>
        </div>
    </div>

</html:form>

