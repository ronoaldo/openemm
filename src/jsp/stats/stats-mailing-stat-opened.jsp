<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.stat.MailingStatEntry, org.agnitas.web.MailingStatAction, org.agnitas.web.MailingStatForm, java.util.Hashtable" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%
    Hashtable values = (Hashtable) request.getAttribute("values");
    String timekey = (String) request.getAttribute("time_key");
    Integer tmpMailingID = (Integer) request.getAttribute("tmpMailingID");
    // put csv file from the form in the hash table:
    String file = ((MailingStatForm) (session.getAttribute("mailingStatForm"))).getCsvfile();
%>

<html:form action="/mailing_stat">
    <html:hidden property="mailingID"/>
    <html:hidden property="action"/>


    <div class="content_element_container">
        <table border="0" cellspacing="0" cellpadding="0" width="100%">
            <tr>
                <td><span class="head3"><bean:message key="statistic.Opened_Mails"/><br><br></span></td>
                <td align="right">
                    &nbsp;
                    <html:link styleClass="blue_link" page='<%= new String(\"/file_download?key=\" + timekey) %>'><img
                            src="${emmLayoutBase.imagesURL}/icon_save.gif" border="0"></html:link>
                </td>
            </tr>
        </table>
    </div>

    <div class="content_element_container">
        <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td colspan="2"><html:link
                        page='<%= new String(\"/mailing_stat.do?action=\" + MailingStatAction.ACTION_OPEN_TIME + \"&mailingID=\" + tmpMailingID) %>'><b><bean:message
                        key="statistic.OpenTime"/></b></html:link></td>
            </tr>
            <tr>
                <td colspan="2">&nbsp;</td>
            </tr>
            <tr>
                <td><b><bean:message key="statistic.domain"/>&nbsp;&nbsp;</b></td>
                <td><b><bean:message key="statistic.Opened_Mails"/></b></td>
            </tr>
            <tr>
                <td colspan="2">
                    <hr>
                </td>
            </tr>

            <%
                int i = 1;
                while (values.containsKey(new Integer(i))) {
                    MailingStatEntry aEntry = (MailingStatEntry) (values.get(new Integer(i)));
                    int aktOpened = aEntry.getOpened();
                    String aktDomain = aEntry.getTargetName();
                    i++;%>
            <tr>
                <td><%=aktDomain %>&nbsp;&nbsp;</td>
                <td align="right"><%=aktOpened %>&nbsp;&nbsp;</td>
            </tr>

            <% }
                MailingStatEntry aEntry = (MailingStatEntry) (values.get(new Integer(0)));
                int totalOp = aEntry.getOpened();
                int restOp = aEntry.getTotalClicks();
                if (restOp > 0) {
            %>
            <tr>
                <td colspan="2">&nbsp;</td>
            </tr>
            <tr>
                <td><bean:message key="statistic.Other"/>&nbsp;&nbsp;</td>
                <td align="right"><%=restOp %>&nbsp;&nbsp;</td>
            </tr>
            <% } %>
            <tr>
                <td colspan="2">
                    <hr>
                </td>
            </tr>
            <tr>
                <td><b><bean:message key="statistic.Total"/></b>&nbsp;&nbsp;</td>
                <td align="right"><b><%=totalOp %>&nbsp;&nbsp;</b></td>
            </tr>

            <%
                Hashtable myMap = ((Hashtable) pageContext.getSession().getAttribute("map"));
                myMap.put(timekey, file);
                pageContext.getSession().setAttribute("map", myMap);
            %>
            <tr>
                <td colspan="2">&nbsp;</td>
            </tr>
            <tr>
                <td><br></td>
            </tr>
            <tr>
                <td colspan="2"></td>
            </tr>
        </table>
    </div>

    <div class="button_container">

        <div class="action_button float_left">
            <html:link
                    page='<%= new String(\"/mailing_stat.do?action=\" + MailingStatAction.ACTION_MAILINGSTAT) %>'><span><bean:message
                    key="button.Back"/></span></html:link>
        </div>

    </div>


</html:form>