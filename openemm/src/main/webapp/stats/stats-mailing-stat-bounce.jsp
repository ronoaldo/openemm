<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.stat.MailingStatEntry, org.agnitas.web.MailingStatAction, java.util.Hashtable" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%
    Integer tmpMailingID = (Integer) request.getAttribute("tmpMailingID");
    String timekey = (String) request.getAttribute("time_key");
    Hashtable values = (Hashtable) request.getAttribute("values");
%>
<html:form action="/mailing_stat">
    <html:hidden property="mailingID"/>
    <html:hidden property="action"/>

    <div class="content_element_container">

        <table border="0" cellspacing="0" cellpadding="0" width="100%">

            <tr>
                <td><span class="head3"><bean:message key="statistic.Bounces"/><br><br></span></td>
                <td align="right">
                    &nbsp;

                    <!--  <html:link page='<%= new String(\"/file_download?key=\" + timekey) %>'><img src="${emmLayoutBase.imagesURL}/icon_save.gif" border="0"></html:link> -->

                </td>
            </tr>

        </table>


        <table border="0" cellspacing="0" cellpadding="0">


            <tr>
                <td><b><bean:message key="statistic.bounces.softbounce"/>s&nbsp;&nbsp;</b></td>
                <td><b><bean:message key="statistic.Amount"/></b></td>

            </tr>

            <tr>
                <td colspan="2">
                    <hr>
                </td>
            </tr>


            <%if (values.get(new Integer(420)) != null) { %>
            <tr>
                <td><bean:message key="statistic.bounces.detail.420"/>&nbsp;&nbsp;</td>
                <td align="right"><%= ((MailingStatEntry) (values.get(new Integer(420)))).getBounces() %>
                    &nbsp;&nbsp;</td>
            </tr>
            <% } %>

            <%if (values.get(new Integer(430)) != null) { %>
            <tr>
                <td><bean:message key="statistic.bounces.detail.430"/>&nbsp;&nbsp;</td>
                <td align="right"><%= ((MailingStatEntry) (values.get(new Integer(430)))).getBounces() %>
                    &nbsp;&nbsp;</td>
            </tr>
            <% } %>

            <%if (values.get(new Integer(500)) != null) { %>
            <tr>
                <td><bean:message key="statistic.bounces.detail.500"/>&nbsp;&nbsp;</td>
                <td align="right"><%= ((MailingStatEntry) (values.get(new Integer(500)))).getBounces() %>
                    &nbsp;&nbsp;</td>
            </tr>
            <% } %>

            <%if (values.get(new Integer(400)) != null) { %>
            <tr>
                <td><bean:message key="statistic.bounces.detail.400"/>&nbsp;&nbsp;</td>
                <td align="right"><%= ((MailingStatEntry) (values.get(new Integer(400)))).getBounces() %>
                    &nbsp;&nbsp;</td>
            </tr>
            <% } %>

            <tr>
                <td colspan="2"><br><br></td>
            </tr>


            <tr>
                <td><b><bean:message key="statistic.bounces.hardbounce"/>s&nbsp;&nbsp;</b></td>
                <td><b><bean:message key="statistic.Amount"/></b></td>

            </tr>

            <tr>
                <td colspan="2">
                    <hr>
                </td>
            </tr>

            <%if (values.get(new Integer(511)) != null) { %>
            <tr>
                <td><bean:message key="statistic.bounces.detail.511"/>&nbsp;&nbsp;</td>
                <td align="right"><%= ((MailingStatEntry) (values.get(new Integer(511)))).getBounces() %>
                    &nbsp;&nbsp;</td>
            </tr>
            <% } %>

            <%if (values.get(new Integer(512)) != null) { %>
            <tr>
                <td><bean:message key="statistic.bounces.detail.512"/>&nbsp;&nbsp;</td>
                <td align="right"><%= ((MailingStatEntry) (values.get(new Integer(512)))).getBounces() %>
                    &nbsp;&nbsp;</td>
            </tr>
            <% } %>

            <%if (values.get(new Integer(510)) != null) { %>
            <tr>
                <td><bean:message key="statistic.bounces.detail.510"/>&nbsp;&nbsp;</td>
                <td align="right"><%= ((MailingStatEntry) (values.get(new Integer(510)))).getBounces() %>
                    &nbsp;&nbsp;</td>
            </tr>
            <% } %>

            <%
                MailingStatEntry aEntry = (MailingStatEntry) (values.get(new Integer(0)));
                int totalOp = aEntry.getBounces();
                int restOp = aEntry.getTotalClickSubscribers(); %>
            <tr>
                <td colspan="2">
                    <hr>
                </td>
            </tr>

            <tr>
                <td><b><bean:message key="statistic.Total"/></b>&nbsp;&nbsp;</td>
                <td align="right"><b><%=totalOp %>&nbsp;&nbsp;</b></td>
            </tr>
            <% if (restOp > 0) {
            %>
            <tr>
                <td colspan="2">&nbsp;</td>
            </tr>
            <tr>
                <td><bean:message key="statistic.bounces.deaktivated"/>*&nbsp;&nbsp;</td>
                <td align="right"><%=restOp %>&nbsp;&nbsp;</td>
            </tr>
            <% } %>

            <tr>
                <td colspan="2">&nbsp;</td>
            </tr>
            <tr>
                <td colspan="2">
                    <div class="action_button add_button">
                        <html:link
                                page='<%= new String(\"/mailing_stat.do?action=\" + MailingStatAction.ACTION_MAILINGSTAT) %>'>
                            <span><bean:message key="button.Back"/></span>
                        </html:link>
                    </div>
                </td>
                <td><html:link styleClass="blue_link"
                               page='<%= new String(\"/mailing_stat.do?action=\" + MailingStatAction.ACTION_BOUNCE + \"&mailingID=\" + tmpMailingID) %>'><B><bean:message
                        key="statistic.BounceDownload"/></html:link></td>
            </tr>

        </table>
        <br><br>
        *<bean:message key="statistic.bounces.disclaimer"/>

    </div>

</html:form>

