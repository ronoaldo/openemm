<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.web.MailingStatAction, org.agnitas.web.MailingStatForm, java.util.Hashtable" %>
<%@ page import="org.agnitas.util.AgnUtils" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%
    Hashtable tmpValues = (Hashtable) request.getAttribute("tmpValues");
    Integer tmpMailingID = (Integer) request.getAttribute("tmpMailingID");
    Integer tmpMaxblue = (Integer) request.getAttribute("tmpMaxblue");
%>

<% String day = (String)(pageContext.getRequest().getParameter("startdate"));
    java.util.GregorianCalendar aCal=new java.util.GregorianCalendar();
    java.text.SimpleDateFormat bFormat=new java.text.SimpleDateFormat("yyyyMMdd");
    try {
        aCal.setTime(bFormat.parse(day));
    } catch(Exception e) {
        AgnUtils.logger().info("mailing_stat_day.jsp aCal.setTime Exception: " + e);
    }
    java.text.DateFormat aFormat=java.text.DateFormat.getDateInstance(java.text.DateFormat.FULL, (java.util.Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY));
    java.util.Date aDate=aCal.getTime();
%>

<html:form action="/mailing_stat.do">

<div class="mail_stat_week_container">

    <bean:message key="statistic.OpenTime"/>:&nbsp;<%= aFormat.format(aDate) %>
    <br>

    <div class="mail_week_table_container">
        <table border="0" cellspacing="0" cellpadding="0" class="mail_week_table">
            <tr>
                <td colspan="27">
                    <div class="dotted_line stat_line"></div>
                </td>
            </tr>
            <tr>
                <td valign=bottom >
                    <bean:message key="statistic.Opened_Mails"/>:&nbsp;
                    <div class="dotted_line stat_line"></div>
                    <bean:message key="default.Time"/>:&nbsp;
                </td>
                <!-- * * * * * * *-->
                <!-- VALUES BEGIN -->
                <%  String [] hours = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
                    int aktHour;
                    int i = 0;
                    String aktDate = "";
                    java.text.SimpleDateFormat format01=new java.text.SimpleDateFormat("yyyyMMdd");
                    aCal.add(aCal.DATE, -1);
                    aktDate = format01.format(aCal.getTime());
                %>
                <td valign=bottom><div class="dotted_line stat_line"></div>
                    <html:link styleClass="mail_week_table_arrow" page='<%= new String(\"/mailing_stat.do?action=\" + MailingStatAction.ACTION_OPEN_DAYSTAT + \"&mailingID=\" + tmpMailingID + \"&startdate=\" + aktDate) %>'>
                        &nbsp;&nbsp;<img src="${emmLayoutBase.imagesURL}/arrow_back.gif" border="0">&nbsp;
                    </html:link>
                </td>
                <%
                    for(i = 0; i < 24; i++) {
                        aktHour = Integer.parseInt(hours[i]);
                %>
                <td valign=bottom width="32px">
                    <center>
                        <img src="${emmLayoutBase.imagesURL}/one_pixel_h.gif" width="20" border="0" height="<% if(tmpValues.containsKey(aktHour)) { %><%= java.lang.StrictMath.floor(((Integer)(tmpValues.get(aktHour))).doubleValue()/(double)tmpMaxblue*200) %><% } else { %>1<% } %>"><br>
                        <% if(tmpValues.containsKey(aktHour)) { %><%= tmpValues.get(aktHour) %><% } else { %>0<% } %>
                        <div class="dotted_line stat_line"></div>
                        <%= i %>
                    </center>
                </td>
                <% } %>
                <!-- VALUES END -->
                <!-- * * * * * *-->
                <%
                    aCal.add(aCal.DATE, 2);
                    aktDate = format01.format(aCal.getTime());
                %>
                <td valign=bottom><div class="dotted_line stat_line"></div>
                    <html:link styleClass="mail_week_table_arrow" page='<%= new String(\"/mailing_stat.do?action=\" + MailingStatAction.ACTION_OPEN_DAYSTAT + \"&mailingID=\" + tmpMailingID + \"&startdate=\" + aktDate) %>'>
                        &nbsp;<img src="${emmLayoutBase.imagesURL}/arrow_next.gif" border="0">
                    </html:link>
                </td>

            </tr>
            <tr>
                <td colspan="27">
                    <div class="dotted_line stat_line"></div>
                </td>
            </tr>
        </table>

        <b><bean:message key="statistic.Total"/>:</b>&nbsp;<%= ((MailingStatForm)session.getAttribute("mailingStatForm")).getClicks()  %>&nbsp;<bean:message key="statistic.Opened_Mails"/>
    </div>
</div>

<div class="button_container">
    <div class="action_button">
        <html:link page='<%= new String(\"/mailing_stat.do?action=\" + MailingStatAction.ACTION_MAILINGSTAT + \"&mailingID=\" + tmpMailingID) %>'>
            <span><bean:message key="button.Back"/></span>
        </html:link>
    </div>
</div>

</html:form>