<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.util.AgnUtils, org.agnitas.util.EmmCalendar, org.agnitas.web.MailingStatAction, org.agnitas.web.MailingStatForm, java.util.Hashtable" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%
    // date formats:
    java.text.SimpleDateFormat format01 = new java.text.SimpleDateFormat("yyyyMMdd");
    java.text.SimpleDateFormat format02 = new java.text.SimpleDateFormat("dd.MM.yy");

    String statfile = "";
    // for csv-download:
    EmmCalendar my_calendar = new EmmCalendar(java.util.TimeZone.getDefault());
    java.util.Date my_time = my_calendar.getTime();
    String Datum = my_time.toString();
    String timekey = Long.toString(my_time.getTime());

    String tmpStartdate = (String) request.getAttribute("tmpStartdate");
    Integer tmpMailingID = (Integer) request.getAttribute("tmpMailingID");
    Integer tmpMaxblue = (Integer) request.getAttribute("tmpMaxblue");
    Hashtable tmpValues = (Hashtable) request.getAttribute("tmpValues");
    Integer tmpTargetID = (Integer) request.getAttribute("tmpTargetID");

%>
<html:form action="/mailing_stat">
    <html:hidden property="action"/>
    <html:hidden property="mailingID"/>
    <html:hidden property="urlID"/>
    <html:hidden property="startdate"/>

<div class="mail_stat_week_container">
    <bean:message key="statistic.OpenTime"/>
    <br><br>
    <bean:message key="statistic.KlickForDay"/>

    <div class="mail_week_table_container">
                <table border="0" cellspacing="0" cellpadding="0" class="mail_week_table">
                    <tr>
                        <td colspan="10">
                            <div class="dotted_line stat_line"></div>
                        </td>
                    </tr>
                    <tr>
                        <td width="80" valign=bottom>
                            <bean:message key="statistic.Opened_Mails"/>:
                            <div class="dotted_line stat_line"></div>
                            <bean:message key="statistic.Date"/>:
                            </a>
                        </td>

                        <!-- * * * * * * *-->
                        <!-- VALUES BEGIN -->
                        <% EmmCalendar aCal = new EmmCalendar(java.util.TimeZone.getDefault());
                            double zoneOffset = 0.0;
                            zoneOffset = aCal.getTimeZoneOffsetHours(java.util.TimeZone.getDefault(), AgnUtils.getTimeZone(request));
                            java.util.TimeZone my_zone = AgnUtils.getTimeZone(request);
                            String aktDate = "";
                            int i = 0;
                            aCal.set(new Integer(((MailingStatForm) session.getAttribute("mailingStatForm")).getStartdate().substring(0, 4)).intValue(),
                                    (new Integer(((MailingStatForm) session.getAttribute("mailingStatForm")).getStartdate().substring(4, 6)).intValue() - 1),
                                    new Integer(((MailingStatForm) session.getAttribute("mailingStatForm")).getStartdate().substring(6, 8)).intValue());
                            aCal.setTimeZone(java.util.TimeZone.getDefault());
                            aCal.changeTimeWithZone(my_zone);
                            java.util.Enumeration ke = ((MailingStatForm) session.getAttribute("mailingStatForm")).getValues().keys();
                        %>

                        <% if (tmpStartdate.compareTo(((MailingStatForm) session.getAttribute("mailingStatForm")).getFirstdate()) >= 0) {
                            aCal.add(aCal.DATE, -7);
                            aktDate = format01.format(aCal.getTime()); %>
                        <td valign=bottom>
                            <div class="dotted_line stat_line"></div>
                            <html:link styleClass="mail_week_table_arrow"
                                    page='<%= new String("/mailing_stat.do?action=" + MailingStatAction.ACTION_OPEN_TIME + "&mailingID=" + tmpMailingID + "&startdate=" + aktDate) %>'>
                                <img src="${emmLayoutBase.imagesURL}/arrow_back.gif" border="0">&nbsp;
                            </html:link>
                        </td>
                        <% aCal.add(aCal.DATE, 7);
                        } %>
                        <%
                            while (i < 7) {
                                aktDate = format01.format(aCal.getTime());
                        %>

                        <td width="80" valign=bottom>
                            <center>
                                <html:link styleClass="blue_link" page='<%= new String("/mailing_stat.do?action=" + MailingStatAction.ACTION_OPEN_DAYSTAT + "&mailingID=" + tmpMailingID + "&startdate=" + aktDate) %>'>
                                    <img src="${emmLayoutBase.imagesURL}/one_pixel_h.gif"
                                         width="20" border="0" height="<% if(tmpValues.containsKey(aktDate)) {%><%=java.lang.StrictMath.floor(((Integer)(tmpValues.get(aktDate))).doubleValue()/(double)tmpMaxblue*200)%><% } else { %>0<% } %>">
                                </html:link>
                                <br>
                                <html:link styleClass="blue_link" page='<%= new String("/mailing_stat.do?action=" + MailingStatAction.ACTION_OPEN_DAYSTAT + "&mailingID=" + tmpMailingID + "&startdate=" + aktDate) %>'>
                                    <% if (tmpValues.containsKey(aktDate)) {%><%= tmpValues.get(aktDate) %><% } else { %>0<% } %>
                                </html:link>
                                <div class="dotted_line stat_line"></div>
                                <html:link styleClass="blue_link" page='<%= new String("/mailing_stat.do?action=" + MailingStatAction.ACTION_OPEN_DAYSTAT + "&mailingID=" + tmpMailingID + "&startdate=" + aktDate) %>'>
                                    <%= format02.format(aCal.getTime()) %>
                                </html:link>
                            </center>
                        </td>

                        <% statfile += "\r\n\"" + format02.format(aCal.getTime()) + "\";\"";
                            if (tmpValues.containsKey(aktDate)) {
                                statfile += tmpValues.get(aktDate);
                            } else {
                                statfile += "0";
                            }
                            statfile += "\"";
                            i++;
                            aCal.add(aCal.DATE, 1);
                        } %>
                        <!-- VALUES END -->
                        <!-- * * * * * *-->

                        <% // aCal.add(aCal.DATE, 1); 

                            //my_calendar.roll(my_calendar.DATE, 1);
                            my_calendar.add(my_calendar.MINUTE, 3);
                            if (my_calendar.getTime().after(aCal.getTime())) {
                                aktDate = format01.format(aCal.getTime());

                                // put csv file from the form in the hash table:
                                ((Hashtable) pageContext.getSession().getAttribute("map")).put(timekey, statfile);

                        %>
                        <td valign=bottom>
                            <div class="dotted_line stat_line"></div>
                            <html:link styleClass="mail_week_table_arrow" page='<%= new String("/mailing_stat.do?action=" + MailingStatAction.ACTION_OPEN_TIME + "&mailingID=" + tmpMailingID + "&startdate=" + aktDate) %>'>
                                <img src="${emmLayoutBase.imagesURL}/arrow_next.gif" border="0">&nbsp;
                            </html:link>
                        </td>
                        <% } %>
                    </tr>
                    <tr>
                        <td colspan="10">
                            <div class="dotted_line stat_line"></div>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        </table>

        <B><bean:message key="statistic.Total"/>:</B>&nbsp;<%= ((MailingStatForm) session.getAttribute("mailingStatForm")).getClicks()  %>&nbsp;<bean:message key="statistic.Opened_Mails"/>
        
    </div>
</div>

<div class="button_container">
    <div class="action_button">
        <html:link page='<%= new String("/mailing_stat.do?action=" + MailingStatAction.ACTION_MAILINGSTAT + "&mailingID=" + tmpMailingID + "&targetID=" + tmpTargetID) %>'>
            <span><bean:message key="button.Back"/></span>
        </html:link>
    </div>
</div>

</html:form>