<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.target.Target, org.agnitas.util.AgnUtils, org.agnitas.web.MailingStatAction, org.agnitas.web.MailingStatForm, java.util.Hashtable" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    Hashtable tmpValues = (Hashtable) request.getAttribute("tmpValues");
    String timekey = (String) request.getAttribute("time_key");
    String aktURL = (String) request.getAttribute("aktURL");
    Integer tmpMailingID = (Integer) request.getAttribute("tmpMailingID");
    Integer tmpTargetID = (Integer) request.getAttribute("tmpTargetID");
    Integer tmpUrlID = (Integer) request.getAttribute("tmpUrlID");
    Integer tmpMaxblue = (Integer) request.getAttribute("tmpMaxblue");
    String statfile = (String) request.getAttribute("statfile");

%>

<%  String day = (String)(pageContext.getRequest().getParameter("startdate"));
    java.util.GregorianCalendar aCal=new java.util.GregorianCalendar();
    java.text.SimpleDateFormat bFormat=new java.text.SimpleDateFormat("yyyyMMdd");
    try {
        aCal.setTime(bFormat.parse(day));
    } catch(Exception e) {
        AgnUtils.logger().info("mailing_stat_day.jsp aCal.setTime Exception: "+e);
    }
    java.text.DateFormat aFormat=java.text.DateFormat.getDateInstance(java.text.DateFormat.FULL, (java.util.Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY));
    java.util.Date aDate=aCal.getTime();
%>
<html:form action="/mailing_stat.do">

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
                    <bean:message key="statistic.FeedbAnalys"/>:&nbsp;<%= aFormat.format(aDate) %>
                    <logic:notEqual name="mailingStatForm" property="urlID" value="0">
                        <br>
                        <bean:message key="statistic.ForURL"/>
                        <a styleClass="target_view_link" href="<%= aktURL %>" target="_blank">&quot;<%= aktURL %>&quot;</a>
                    </logic:notEqual>
                    <br><br>
                    <div class="float_left">
                        <bean:message key="target.Target"/>:
                        <html:hidden property="action"/>
                        <html:hidden property="mailingID"/>
                        <html:hidden property="urlID"/>
                        <html:hidden property="startdate"/>
                        <% String uds = (String)(pageContext.getRequest().getParameter("user_date")); %>
                        <% if( uds != null ) { %>
                        <input type="hidden" name="user_date" value="<%= uds %>">
                        <% } %>
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
                <td colspan="27">
                    <div class="dotted_line stat_line"></div>
                </td>
            </tr>
            <tr>
                <td valign=bottom>
                    <bean:message key="statistic.Clicks"/>:
                    <div class="dotted_line stat_line"></div>
                    <bean:message key="default.Time"/>:
                    </a>
                </td>

                <!-- * * * * * * *-->
                <!-- VALUES BEGIN -->
                <%  String [] hours = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
                    String aktHour = "";
                    int i = 0;
                    /*
                    System.out.println("jsp: "+((MailingStatForm)session.getAttribute("mailingStatForm")).getValues().size() + " entries:");
                    java.util.Enumeration ke = ((MailingStatForm)session.getAttribute("mailingStatForm")).getValues().keys();
                    while (ke.hasMoreElements()) {
                    System.out.println(" - " + ke.nextElement());
                    }
                     */
                    String aktDate = "";
                    java.text.SimpleDateFormat format01=new java.text.SimpleDateFormat("yyyyMMdd");
                    aCal.add(aCal.DATE, -1);
                    aktDate = format01.format(aCal.getTime());
                %>

                <td valign=bottom><div class="dotted_line stat_line"></div>
                    <html:link styleClass="mail_week_table_arrow"  page='<%= new String(\"/mailing_stat.do?action=\" + MailingStatAction.ACTION_DAYSTAT + \"&mailingID=\" + tmpMailingID + \"&urlID=\" + tmpUrlID + \"&startdate=\" + aktDate + \"&targetID=\" + tmpTargetID) %>'>
                        &nbsp;&nbsp;<img src="${emmLayoutBase.imagesURL}/arrow_back.gif" border="0">&nbsp;
                    </html:link>
                </td>


                <%
                    while(i<24) {
                            aktHour = hours[i];
                            //System.out.println("aktHour: " + aktHour);
                %>

                <td valign=bottom width="32px">
                    <center>
                        <img src="${emmLayoutBase.imagesURL}/one_pixel_h.gif" width="20" border="0" height="<% if(tmpValues.containsKey(aktHour)) { %><%= java.lang.StrictMath.floor(((Integer)(tmpValues.get(aktHour))).doubleValue()/(double)tmpMaxblue*200) %><% } else { %>1<% } %>"><br>
                        <% if(tmpValues.containsKey(aktHour)) { %><%= tmpValues.get(aktHour) %><% } else { %>0<% } %>
                        <div class="dotted_line stat_line"></div>
                        <%= i %>
                    </center>
                </td>

                <% statfile += "\r\n\"" + aktHour + "\";\"";
                    if(tmpValues.containsKey(aktHour))
                        statfile += tmpValues.get(aktHour);
                    else
                        statfile += "0";
                    statfile += "\"";
                    i++;
                    }
                %>
                <!-- VALUES END -->
                <!-- * * * * * *-->
                <%
                    aCal.add(aCal.DATE, 2);
                    aktDate = format01.format(aCal.getTime());
                %>

                <td valign=bottom><div class="dotted_line stat_line"></div>
                    <html:link styleClass="mail_week_table_arrow" page='<%= new String(\"/mailing_stat.do?action=\" + MailingStatAction.ACTION_DAYSTAT + \"&mailingID=\" + tmpMailingID + \"&urlID=\" + tmpUrlID + \"&startdate=\" + aktDate + \"&targetID=\" + tmpTargetID) %>'>
                        &nbsp;<img src="${emmLayoutBase.imagesURL}/arrow_next.gif" border="0">&nbsp;
                    </html:link>
                </td>
            </tr>
            <td colspan="27">
                <div class="dotted_line stat_line"></div>
            </td>
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

<%
    // put csv file from the form in the hash table:
    Hashtable myMap = ((Hashtable) pageContext.getSession().getAttribute("map"));
   myMap.put(timekey,  statfile);
   pageContext.getSession().setAttribute("map", myMap);
%>

