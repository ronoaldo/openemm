<%--
/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/
 --%>
<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.stat.*, org.agnitas.beans.*, java.util.*, java.text.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<agn:CheckLogon/>

<agn:Permission token="mailing.send.show"/>

<%
    java.util.Locale locale =  AgnUtils.getAdmin(request).getLocale();
    java.text.DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(locale);
    java.text.NumberFormat intFormat = new java.text.DecimalFormat("###,###,###,###,##0", decimalFormatSymbols);
    DeliveryStat aDelstat = null;
    int tmpMailingID=0;
    MailingSendForm aForm=(MailingSendForm)request.getAttribute("mailingSendForm");
    if(aForm!=null) {
        aDelstat = aForm.getDeliveryStat();
        tmpMailingID = aForm.getMailingID();
    }

    TimeZone aZone=TimeZone.getTimeZone(AgnUtils.getAdmin(request).getAdminTimezone());
    GregorianCalendar aDate=new GregorianCalendar(aZone);
    DateFormat showFormat=DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY));
	showFormat.setCalendar(aDate);

    DateFormat dateFormat=DateFormat.getDateInstance(DateFormat.MEDIUM, (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY));
	dateFormat.setCalendar(aDate);

    DateFormat timeFormat=DateFormat.getTimeInstance(DateFormat.SHORT, (Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY));
	timeFormat.setCalendar(aDate);

    DateFormat internalFormat=new SimpleDateFormat("yyyyMMdd");

%>

<html>
    <meta http-equiv="Page-Exit" content="RevealTrans(Duration=1,Transition=1)">


    <head>
        <link type="text/css" rel="stylesheet" href="${emmLayoutBase.cssURL}/style.css">
        <link type="text/css" rel="stylesheet" href="${emmLayoutBase.cssURL}/structure.css">
        <link type="text/css" rel="stylesheet" href="${emmLayoutBase.cssURL}/displaytag.css">
        <link type="text/css" rel="stylesheet" href="${emmLayoutBase.cssURL}/ie7.css">
    </head>

    <body onLoad="window.setTimeout('window.location.reload()',30000)" STYLE="background-image:none;background-color:transparent">

    <script type="text/javascript">
        window.onload = function() {
            var frame = parent.document.getElementById('statFrame');
            if(frame){
                frame.height = this.document.body.scrollHeight;
            }
        };
    </script>


    <div class="grey_box_container">
    <div class="grey_box_top statbox_panel_top">&nbsp;</div>
    <div class="grey_box_content" id="page_content">

        <span class="send_status"><bean:message key="mailing.DistribStatus"/>:</span><bean:message key='<%=new String(\"statistic.DeliveryStatus.\" + aDelstat.getDeliveryStatus())%>'/>

        <div class="send_status_div_container_ie_hack">
        <logic:notEqual name="mailingSendForm" property="deliveryStat.lastType" value="NO">
        <div class="send_status_table_container">
        <table class="send_status_table">
            <thead>
                <th class="send_status_table_header_first send_status_table_label send_status_table_title send_status_first_column">
                    <bean:message key="mailing.LastDelivery"/>
                </th>
                <th class="send_status_table_header_second send_status_second_column">
                    <bean:message key="mailing.send.delivery.status.${mailingSendForm.deliveryStat.lastType}"/>
                </th>
            </thead>
            <tbody>
                <tr class="odd">
                    <td  class="send_status_first_column">
                        <bean:message key="statistic.Date"/>
                    </td>
                    <td class="send_status_second_column"><%=dateFormat.format(aDelstat.getLastDate())%></td>
                </tr>
                <tr class="even">
                    <td  class="send_status_first_column">
                        <bean:message key="default.Time"/>
                    </td>
                    <td class="send_status_second_column"><%=timeFormat.format(aDelstat.getLastDate())%></td>
                </tr>
                <tr class="odd">
                    <td  class="send_status_first_column">
                        <bean:message key="Targets"/>
                    </td>
                    <td class="send_status_second_column">
                        <%-- yes, this should be in one line - in other case there will be a gap at the right-side in IE7 --%>
                        <div class="send_status_targets_container"><c:set var="targetListSize" value="${fn:length(mailingSendForm.targetGroupsNames) - 1}"/><c:if test="${targetListSize > -1}"><c:forEach var="targetName" items="${mailingSendForm.targetGroupsNames}" varStatus="vs">${targetName}<c:if test="${targetListSize > vs.index}"><br></c:if></c:forEach></c:if></div>
                    </td>
                </tr>
                <tr class="even">
                    <td  class="send_status_first_column">
                        <bean:message key="statistic.TotalMails"/>
                    </td>
                    <td class="send_status_second_column"><%=intFormat.format(aDelstat.getLastTotal())%></td>
                </tr>
            </tbody>
        </table>
        </div>
        </logic:notEqual>


        <logic:greaterThan name="mailingSendForm" property="deliveryStat.deliveryStatus" value="0">
        <div class="send_status_table_container">
        <table class="send_status_table">
            <thead>
                <th class="send_status_table_header_first send_status_table_label send_status_table_title send_status_first_column">
                    <bean:message key="mailing.Generation"/>
                </th>
                <th class="send_status_table_header_second send_status_second_column"></th>
            </thead>
            <tbody>
                <logic:greaterThan name="mailingSendForm" property="deliveryStat.deliveryStatus" value="1">
                    <% if(aDelstat.getGenerateStartTime()!=null) { %>
                    <tr class="odd">
                        <td  class="send_status_first_column">
                            <bean:message key="mailing.sendStatus.started"/>
                        </td>
                        <td class="send_status_second_column"><%=showFormat.format(aDelstat.getGenerateStartTime())%></td>
                    </tr>
                    <% } %>
                </logic:greaterThan>
                <logic:greaterThan name="mailingSendForm" property="deliveryStat.deliveryStatus" value="2">
                    <% if(aDelstat.getGenerateEndTime()!=null) { %>
                    <tr class="even">
                        <td  class="send_status_first_column">
                            <bean:message key="mailing.sendStatus.ended"/>
                        </td>
                        <td class="send_status_second_column"><%=timeFormat.format(aDelstat.getGenerateEndTime())%></td>
                    </tr>
                    <% } %>
                </logic:greaterThan>
                <logic:lessThan name="mailingSendForm" property="deliveryStat.deliveryStatus" value="2">
                    <tr class="odd">
                        <td  class="send_status_first_column">
                            <bean:message key="statistic.ScheduledGenerateTime"/>
                        </td>
                        <td class="send_status_second_column"><%=showFormat.format(aDelstat.getScheduledGenerateTime())%></td>
                    </tr>
                </logic:lessThan>
                <logic:greaterThan name="mailingSendForm" property="deliveryStat.deliveryStatus" value="1">
                    <tr class="even">
                        <td  class="send_status_first_column">
                            <bean:message key="mailing.GeneratedMails"/>
                        </td>
                        <td class="send_status_second_column"><%=intFormat.format(aDelstat.getGeneratedMails())%></td>
                    </tr>
                </logic:greaterThan>
            </tbody>
        </table>
        </div>
        </logic:greaterThan>

        <logic:greaterThan name="mailingSendForm" property="deliveryStat.deliveryStatus" value="0">
        <div class="send_status_table_container">
        <table class="send_status_table">
            <thead>
                <th class="send_status_table_header_first send_status_table_label send_status_table_title send_status_first_column">
                    <bean:message key="mailing.Delivery"/>
                </th>
                <th class="send_status_table_header_second send_status_second_column"></th>
            </thead>
            <tbody>
                <logic:greaterThan name="mailingSendForm" property="deliveryStat.deliveryStatus" value="3">
                    <% if(aDelstat.getSendStartTime()!=null) { %>
                    <tr class="odd">
                        <td  class="send_status_first_column">
                            <bean:message key="mailing.sendStatus.started"/>
                        </td>
                        <td class="send_status_second_column"><%=showFormat.format(aDelstat.getSendStartTime())%></td>
                    </tr>
                    <% } %>
                </logic:greaterThan>
                <logic:greaterThan name="mailingSendForm" property="deliveryStat.deliveryStatus" value="4">
                    <% if(aDelstat.getSendEndTime()!=null) { %>
                    <tr class="even">
                        <td  class="send_status_first_column">
                            <bean:message key="mailing.sendStatus.ended"/>
                        </td>
                        <td class="send_status_second_column"><%=showFormat.format(aDelstat.getSendEndTime())%></td>
                    </tr>
                    <% } %>
                </logic:greaterThan>
                <logic:lessThan name="mailingSendForm" property="deliveryStat.deliveryStatus" value="4">
                    <tr class="odd">
                        <td  class="send_status_first_column">
                            <bean:message key="statistic.ScheduledSendTime"/>
                        </td>
                        <td class="send_status_second_column"><%=showFormat.format(aDelstat.getScheduledSendTime())%></td>
                    </tr>
                </logic:lessThan>
                <logic:greaterThan name="mailingSendForm" property="deliveryStat.deliveryStatus" value="3">
                    <tr class="even">
                        <td  class="send_status_first_column">
                            <bean:message key="mailing.SentMails"/>
                        </td>
                        <td class="send_status_second_column"><%=intFormat.format(aDelstat.getSentMails())%></td>
                    </tr>
                </logic:greaterThan>
            </tbody>
        </table>
        </div>
        </logic:greaterThan>
    </div>


    <% if( (((MailingSendForm)request.getAttribute("mailingSendForm")).getDeliveryStat())!=null   ) { %>
        <logic:equal name="mailingSendForm" property="deliveryStat.cancelable" value="true">
            <div class="button_container cancel_generation_button_container">
                <div class="action_button">
                    <html:link page='<%= new String(\"/mailingsend.do?action=\" + MailingSendAction.ACTION_CANCEL_MAILING_REQUEST + \"&mailingID=\" + tmpMailingID) %>' target="_parent" >
                        <span style="display:inline-block;"><bean:message key="button.Cancel"/></span>
                    </html:link>
                </div>
                <div class="action_button"><bean:message key="mailing.CancelGeneration"/>:</div>
            </div>
        </logic:equal>
    <% } %>

    </div>
    <div class="grey_box_bottom"></div>
    </div>

    </body>
</html>
