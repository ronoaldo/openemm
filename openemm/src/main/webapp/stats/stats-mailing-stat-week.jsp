<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.target.Target, org.agnitas.util.AgnUtils, org.agnitas.util.EmmCalendar, org.agnitas.web.MailingStatAction, org.agnitas.web.MailingStatForm" %>
<%@ page import="java.util.Hashtable" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    // date formats:
    java.text.SimpleDateFormat format01 = new java.text.SimpleDateFormat("yyyyMMdd");
    java.text.SimpleDateFormat format02 = new java.text.SimpleDateFormat("dd.MM.yy");

    String timekey = (String) request.getAttribute("time_key");

    Integer tmpMailingID = (Integer) request.getAttribute("tmpMailingID");
    Integer tmpTargetID = (Integer) request.getAttribute("tmpTargetID");
    Integer tmpUrlID = (Integer) request.getAttribute("tmpUrlID");
    Integer tmpMaxblue = (Integer) request.getAttribute("tmpMaxblue");
    String tmpStartdate = (String) request.getAttribute("tmpStartdate");
    String aktURL = (String) request.getAttribute("aktURL");
    String statfile = (String) request.getAttribute("statfile");
    Hashtable tmpValues = (Hashtable) request.getAttribute("tmpValues");
    EmmCalendar my_calendar = (EmmCalendar) request.getAttribute("my_calendar");
%>
<html:form action="/mailing_stat">
    <html:hidden property="action"/>
    <html:hidden property="mailingID"/>
    <html:hidden property="urlID"/>
    <html:hidden property="startdate"/>

    <div class="grey_box_container">
        <div class="grey_box_top"></div>
        <div class="grey_box_content">
            <div class="grey_box_left_column" style="float: none;">

                <div align="right" class="float_right box_button">
                    <html:link page='<%= new String(\"/file_download?key=\" + timekey) %>'>
                        <img src="${emmLayoutBase.imagesURL}/icon_save.gif" border="0">
                    </html:link>
                </div>


                <div class="grey_box_form_item stat_recipient_form_item">
                    <bean:message key="statistic.FeedbAnalys"/>
                    <logic:notEqual name="mailingStatForm" property="urlID" value="0">
                        <br>
                        <bean:message key="statistic.ForURL"/>
                        <a styleClass="target_view_link" href="<%= aktURL %>" target="_blank">&quot;<%= aktURL %>
                            &quot;</a>
                    </logic:notEqual>
                    <br><br><bean:message key="statistic.KlickForDay"/>
                    <br><br>

                    <div class="float_left">
                        <bean:message key="target.Target"/>:
                        <html:select property="targetID" size="1">
                            <html:option value="0"><bean:message key="statistic.All_Subscribers"/></html:option>
                            <c:forEach var="trgt" items="${targets}">
                                <html:option value="${trgt.id}">
                                    ${trgt.targetName}
                                </html:option>
                            </c:forEach>
                        </html:select>
                    </div>
                </div>

            </div>

            <div class="button_grey_box_container">
                <div class="action_button no_margin_right no_margin_bottom">
                    <html:link page="#" onclick="document.mailingStatForm.submit(); return false;">
                        <span><bean:message key="button.OK"/></span>
                    </html:link>
                </div>
            </div>

        </div>
        <div class="grey_box_bottom"></div>
    </div>

<div class="mail_stat_week_container">
    <div class="mail_week_table_container">
                <table border="0" cellspacing="0" cellpadding="0" class="mail_week_table">
                    <tr>
                        <td colspan="10">
                            <div class="dotted_line stat_line"></div>
                        </td>
                    </tr>
                    <tr>
                        <td width="80" valign=bottom>
                            <bean:message key="statistic.Clicks"/>:
                            <div class="dotted_line stat_line"></div>
                            <bean:message key="statistic.Date"/>:
                            </a>
                        </td>

                        <!-- * * * * * * *-->
                        <!-- VALUES BEGIN -->
                        <%  EmmCalendar aCal = new EmmCalendar(java.util.TimeZone.getDefault());
                            double zoneOffset=0.0;
                            zoneOffset=aCal.getTimeZoneOffsetHours(java.util.TimeZone.getDefault(), AgnUtils.getTimeZone(request));
                            //System.out.println("Timezone-Offset: "+aCal.getTimeZoneOffsetHours(java.util.TimeZone.getDefault(), (java.util.TimeZone)request.getSession().getAttribute("timezone")));
                            java.util.TimeZone my_zone = AgnUtils.getTimeZone(request);
                            String aktDate = "";
                            int i=0;
                            aCal.set( new Integer(((MailingStatForm)session.getAttribute("mailingStatForm")).getStartdate().substring(0,4)).intValue() ,
                                    (new Integer(((MailingStatForm)session.getAttribute("mailingStatForm")).getStartdate().substring(4,6)).intValue() - 1) ,
                                    new Integer(((MailingStatForm)session.getAttribute("mailingStatForm")).getStartdate().substring(6,8)).intValue() );
                            aCal.setTimeZone(java.util.TimeZone.getDefault());
                            //System.out.println("Time1: "+aCal.getTime());
                            aCal.changeTimeWithZone(my_zone);
                            //System.out.println("Time2: "+aCal.getTime());
                            //System.out.println("jsp: "+((MailingStatForm)session.getAttribute("mailingStatForm")).getValues().size() + " entries:");
                            java.util.Enumeration ke = ((MailingStatForm)session.getAttribute("mailingStatForm")).getValues().keys();
                            /*
                            while (ke.hasMoreElements()) {
                            System.out.println(" - " + ke.nextElement());
                            }
                             */
                        %>
                        <% //System.out.println("tmpStartdate: " + tmpStartdate);
                            //System.out.println("Firstdate: " + ((MailingStatForm)session.getAttribute("mailingStatForm")).getFirstdate());
                            if(  tmpStartdate.compareTo(  ((MailingStatForm)session.getAttribute("mailingStatForm")).getFirstdate() ) >= 0     ) {
                                    aCal.add(aCal.DATE, -7);
                                    aktDate = format01.format(aCal.getTime()); %>
                        <td valign=bottom><div class="dotted_line stat_line"></div>
                            <html:link styleClass="mail_week_table_arrow" page='<%= new String(\"/mailing_stat.do?action=\" + MailingStatAction.ACTION_WEEKSTAT + \"&mailingID=\" + tmpMailingID + \"&urlID=\" + tmpUrlID + \"&startdate=\" + aktDate + \"&targetID=\" + tmpTargetID) %>'>
                                <img src="${emmLayoutBase.imagesURL}/arrow_back.gif" border="0">&nbsp;
                            </html:link>
                        </td>
                        <%     aCal.add(aCal.DATE, 7);
                                } %>

                        <%
                            while(i<7) {

                                    aktDate = format01.format(aCal.getTime());
                                    //System.out.println("aktDate: " + aktDate);
                        %>

                        <td width="80" valign=bottom>
                            <center>
                            <html:link styleClass="blue_link" page='<%= new String(\"/mailing_stat.do?action=\" + MailingStatAction.ACTION_DAYSTAT + \"&mailingID=\" + tmpMailingID + \"&urlID=\" + tmpUrlID + \"&startdate=\" + aktDate + \"&targetID=\" + tmpTargetID) %>'>
                                <img src="${emmLayoutBase.imagesURL}/one_pixel_h.gif" width="20" border="0" height="<% if( tmpValues.containsKey(aktDate) ) {%><%=java.lang.StrictMath.floor(((Integer)(tmpValues.get(aktDate))).doubleValue()/(double)tmpMaxblue*200)%><% } else { %>0<% } %>">
                            </html:link>
                            <br>
                            <html:link styleClass="blue_link" page='<%= new String(\"/mailing_stat.do?action=\" + MailingStatAction.ACTION_DAYSTAT + \"&mailingID=\" + tmpMailingID + \"&urlID=\" + tmpUrlID + \"&startdate=\" + aktDate + \"&targetID=\" + tmpTargetID) %>'>
                                <% if( tmpValues.containsKey(aktDate) ) {%><%= tmpValues.get(aktDate) %><% } else { %>0<% } %>
                            </html:link>
                            <div class="dotted_line stat_line"></div>
                            <html:link styleClass="blue_link" page='<%= new String(\"/mailing_stat.do?action=\" + MailingStatAction.ACTION_DAYSTAT + \"&mailingID=\" + tmpMailingID + \"&urlID=\" + tmpUrlID + \"&startdate=\" + aktDate + \"&targetID=\" + tmpTargetID) %>'>
                                <%= format02.format(aCal.getTime()) %>
                            </html:link></center>
                        </td>


                        <% statfile += "\r\n\"" + format02.format(aCal.getTime()) + "\";\"";
                            if(tmpValues.containsKey(aktDate))
                                statfile += tmpValues.get(aktDate);
                            else
                                statfile += "0";
                            statfile += "\"";
                            i++;
                            aCal.add(aCal.DATE, 1);
                                } %>
                        <!-- VALUES END -->
                        <!-- * * * * * *-->

                        <% // aCal.add(aCal.DATE, 1);

                            //my_calendar.roll(my_calendar.DATE, 1);
                            my_calendar.add(my_calendar.MINUTE, 3);
                            //System.out.println("my_calendar.getTime(): " + my_calendar.getTime());
                            //System.out.println("aCal.getTime(): " + aCal.getTime());
                            if(my_calendar.getTime().after(aCal.getTime())) {
                                aktDate = format01.format(aCal.getTime());

                                // put csv file from the form in the hash table:
                                Hashtable myMap = ((Hashtable) pageContext.getSession().getAttribute("map"));
                                myMap.put(timekey,  statfile);
                                pageContext.getSession().setAttribute("map", myMap);

                        %>
                        <td valign=bottom>
                            <div class="dotted_line stat_line"></div>
                            <html:link styleClass="mail_week_table_arrow" page='<%= new String(\"/mailing_stat.do?action=\" + MailingStatAction.ACTION_WEEKSTAT + \"&mailingID=\" + tmpMailingID + \"&urlID=\" + tmpUrlID + \"&startdate=\" + aktDate + \"&targetID=\" + tmpTargetID) %>'>
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

                <B><bean:message key="statistic.Total"/>:</B>&nbsp;<%= ((MailingStatForm)session.getAttribute("mailingStatForm")).getClicks()  %>&nbsp;<bean:message key="statistic.Clicks"/>
        </div>
    </div>
    <div class="button_container">
        <div class="action_button">
        <html:link page='<%= new String(\"/mailing_stat.do?action=\" + MailingStatAction.ACTION_MAILINGSTAT + \"&mailingID=\" + tmpMailingID + \"&targetID=\" + tmpTargetID) %>'>
            <span><bean:message key="button.Back"/></span>
        </html:link>
        </div>
    </div>
</html:form>

