<%@ page language="java"
         import="org.agnitas.util.SafeString, java.util.Hashtable, java.util.Locale"
         contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.beans.Recipient" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    String shortname = (String) request.getAttribute("shortname");
    String timekey = (String) request.getAttribute("timekey");
    // put csv file from the form in the hash table:
    String file = "";
%>

<div class="content_element_container">

<script type="text/javascript">
    var tableID = 'customertbl';
</script>

<html:form action="/mailing_stat">
    <html:hidden property="action"/>

    <table border="0" cellspacing="0" cellpadding="0">

        <tr>
            <td><B>Mailing: </B></td>
            <td>&nbsp;&nbsp;<B><%= shortname %>
            </B>&nbsp;&nbsp;</td>
            <td>
                <div align="right"><html:link page='<%= new String(\"/file_download?key=\" + timekey) %>'><img
                        src="${emmLayoutBase.imagesURL}/icon_save.gif"
                        border="0"></html:link></div>
            </td>
        </tr>

    </table>

    </div>

    <br>

    <table border="0" cellspacing="0" cellpadding="0" class="list_table" id="customertbl">
    <tr>
        <th><bean:message key="recipient.Salutation"/>&nbsp;</th>
        <th><bean:message key="recipient.Firstname"/></th>
        <th><bean:message key="recipient.Lastname"/></th>
        <th><bean:message key="mailing.E-Mail"/></th>
    </tr>

    <% file += SafeString.getLocaleString("recipient.Salutation", (Locale) session.getAttribute("emm.locale")) + ";" + SafeString.getLocaleString("recipient.Firstname", (Locale) session.getAttribute("emm.locale")) + ";" + SafeString.getLocaleString("recipient.Lastname", (Locale) session.getAttribute("emm.locale")) + ";" + SafeString.getLocaleString("mailing.E-Mail", (Locale) session.getAttribute("emm.locale")); %>
    <% Recipient recipient = null; %>

    <c:forEach var="recipient" items="${recipientList}">
        <% recipient = (Recipient)pageContext.getAttribute("recipient"); %>
        <tr>
            <span class="ie7hack">
                <td><% if (recipient.getGender() == 0) { %>
                        <% file += "\n \"" + SafeString.getLocaleString("settings.MisterShort", (Locale) session.getAttribute("emm.locale")); %>
                        <bean:message key="settings.MisterShort"/>
                    <% } else if (recipient.getGender() == 1) { %>
                        <% file += "\n \"" + SafeString.getLocaleString("settings.MissesShort", (Locale) session.getAttribute("emm.locale")); %>
                        <bean:message key="settings.MissesShort"/>
                    <% } else { %>
                        <% file += "\n \"" + SafeString.getLocaleString("recipient.Unknown", (Locale) session.getAttribute("emm.locale")); %>
                        <bean:message key="recipient.Unknown"/>
                    <% }%>
                </td>
            </span>
            <td>
                <span class="ie7hack">${recipient.firstname}</span>
            </td>
            <td>
                <span class="ie7hack">${recipient.lastname}</span>
            </td>
            <td>
                <span class="ie7hack">${recipient.email}</span>
            </td>
        </tr>
        <% file += "\";\"" + recipient.getFirstname(); %>
        <% file += "\";\"" + recipient.getLastname(); %>
        <% file += "\";\"" + recipient.getEmail() + "\""; %>
    </c:forEach>

</html:form>
<%((Hashtable) pageContext.getSession().getAttribute("map")).put(timekey, file); %>
</table>

<script type="text/javascript">
    table = document.getElementById('customertbl');

    $$('#customertbl tbody tr').each(function(item) {
        item.observe('mouseover', function() {
            item.addClassName('list_highlight');
        });
        item.observe('mouseout', function() {
            item.removeClassName('list_highlight');
        });
    });
</script>