<%--checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" import="java.util.Iterator" %>
<%@ page import="org.agnitas.web.forms.MailingBaseForm" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    MailingBaseForm aForm = (MailingBaseForm) session.getAttribute("mailingBaseForm");
%>

<html:form action="/mailingbase" styleClass="top_10">
    <html:hidden property="mailingID"/>
    <html:hidden property="action"/>

    <table border="0" cellspacing="0" cellpadding="0" class="list_table" id="actions">
        <thead>
        <tr>
            <th><span class="mailing_action_head_action"><bean:message key="action.Action"/></span></th>
            <th><span class="mailing_action_head_url"><bean:message key="mailing.URL"/>&nbsp;</span></th>
        </tr>
        </thead>
        <c:set var="index" value="0" scope="request"/>

        <% if (aForm.getActions().size() > 0) {
            Iterator it = aForm.getActions().keySet().iterator();
            while (it.hasNext()) {
                String shortname = (String) it.next();
        %>

        <c:set var="trStyle" value="even" scope="request"/>
        <c:if test="${(index mod 2) == 0}">
            <c:set var="trStyle" value="odd" scope="request"/>
        </c:if>
        <c:set var="index" value="${index + 1}" scope="request"/>

        <tr class="${trStyle}">
            <td>
                <span class="ie7hack">
                    <%= shortname %>
                </span>
            </td>
            <td>
                <span class="ie7hack">
                    <%=  aForm.getActions().get(shortname)%>
                </span>
            </td>
        </tr>
        <% } %>
        <% } else { %>
        <tr>
            <td colspan="2">
                <span class="ie7hack">
                    <bean:message key="mailing.noActionsLinked"/>
                </span>
            </td>
        </tr>
        <% } %>
    </table>

    <script type="text/javascript">
        $$('#actions tbody tr').each(function(item) {
            item.observe('mouseover', function() {
                item.addClassName('list_highlight');
            });
            item.observe('mouseout', function() {
                item.removeClassName('list_highlight');
            });
        });
    </script>

</html:form>

