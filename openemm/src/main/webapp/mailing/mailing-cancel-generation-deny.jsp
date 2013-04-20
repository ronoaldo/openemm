<%--checked --%>
<%@ page language="java" import="org.agnitas.web.MailingSendAction" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%
    int tmpMailingID = (Integer) request.getAttribute("tmpMailingID");
%>

<html:form action="/mailingsend">
    <html:hidden property="mailingID"/>
    <html:hidden property="action"/>

    <br>
    <b><bean:message key="mailing.generation.cancel.deny"/></b>
    <br>

    <p>
        <html:link page='<%= new String("/mailingsend.do?action=" + MailingSendAction.ACTION_VIEW_SEND + "&mailingID=" + tmpMailingID) %>'>
            <html:img src="button?msg=button.Back" border="0"/>
        </html:link>
    </p>

</html:form>
    

