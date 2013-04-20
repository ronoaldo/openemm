<%-- to be checked md? --%>
<%@ page language="java"
         import="org.agnitas.util.AgnUtils, org.agnitas.util.SafeString, org.agnitas.web.RecipientAction, org.agnitas.web.RecipientStatAction"
         contentType="text/html; charset=utf-8" buffer="32kb" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.GregorianCalendar" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    int mailinglistID = 0;
    int targetID = 0;
    int mediaType = 0;

    try {
        mailinglistID = Integer.parseInt(request.getParameter("mailingListID"));
    } catch (Exception e) {
        mailinglistID = 0;
    }

    try {
        targetID = Integer.parseInt(request.getParameter("targetID"));
    } catch (Exception e) {
        targetID = 0;
    }

    try {
        mediaType = Integer.parseInt(request.getParameter("mediaType"));
    } catch (Exception e) {
        mediaType = 0;
    }


    DateFormat aFormat3 = new SimpleDateFormat("yyyyMM");
    DateFormat aFormat4 = new SimpleDateFormat("yyyyMMdd");
    GregorianCalendar aCal = new GregorianCalendar();
    try {
        aCal.setTime(aFormat3.parse(request.getParameter("month")));
    } catch (Exception e) {
        aCal.set(Calendar.DAY_OF_MONTH, 1);  // set to first day in month!
    }

// key for the csv download
    java.util.Date my_time = aCal.getTime();
    String Datum = my_time.toString();
    String timekey = Long.toString(my_time.getTime());
    pageContext.setAttribute("time_key", timekey);     // Long.toString((aCal.getTime()).getTime())

// map for the csv download
    Hashtable<String, String> my_map = null;
    if (pageContext.getSession().getAttribute("map") == null) {
        my_map = new Hashtable<String, String>();
        pageContext.getSession().setAttribute("map", my_map);
        // System.out.println("map exists.");
    } else {
        my_map = (Hashtable<String, String>) pageContext.getSession().getAttribute("map");
        // System.out.println("new map.");
    }
%>

<html:form action="/recipient_stats" method="post">
<html:hidden property="action"/>
<html:hidden property="month"/>

    <div class="grey_box_container">
        <div class="grey_box_top"></div>
        <div class="grey_box_content">
    <div class="grey_box_left_column" style="float: none;">

        <div align="right" class="float_right box_button"><html:link
                page='<%= new String(\"/file_download?key=\" + timekey) %>'><img
                src="${emmLayoutBase.imagesURL}/icon_save.gif"
                border="0"></html:link></div>

        <div class="grey_box_form_item stat_recipient_form_item">
            <label><bean:message key="Mailinglist"/>:</label>
            <html:select property="mailingListID" size="1">
                <html:option value="0"><bean:message key="statistic.All_Mailinglists"/></html:option>
                <c:forEach var="mailinglist" items="${mailinglists}">
                    <html:option value="${mailinglist.id}">
                        ${mailinglist.shortname}
                    </html:option>
                </c:forEach>
            </html:select>
        </div>

        <div class="grey_box_form_item stat_recipient_form_item">
            <label><bean:message key="target.Target"/>:</label>
            <html:select property="targetID" size="1">
                <html:option value="0"><bean:message key="statistic.All_Subscribers"/></html:option>
                <c:forEach var="target" items="${targets}">
                    <html:option value="${target.id}">
                        ${target.targetName}
                    </html:option>
                </c:forEach>
            </html:select>
        </div>
    </div>

    <div class="button_grey_box_container">
        <div class="action_button no_margin_right no_margin_bottom">
            <a href="#" onclick="document.recipientStatForm.submit();">
                <span><bean:message key="button.OK"/></span>
            </a>
        </div>
    </div>

    <html:hidden property="mediaType" value="0"/>

        </div>
        <div class="grey_box_bottom"></div>
    </div>

<div class="upload_start_container">

<table border="0" cellspacing="0" cellpadding="0">
<tr>
    <td colspan="3">
        <hr />
        <span class="head3"><bean:message key="recipient.RecipientStatus"/>:</span>
    </td>
</tr>
<tr>
    <td><img src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="10"
             height="5" border="0"></td>
    <td><img src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="10"
             height="5" border="0"></td>
    <td><img src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="10"
             height="5" border="0"></td>
</tr>

<tr>
    <td><B><bean:message key="statistic.Unsubscribes"/>:&nbsp;&nbsp;</B></td>
    <td>
        <table border="0" cellspacing="0" cellpadding="0">
            <tr height="20">
                <td class="stats_highlight_color"
                    width="<bean:write name="recipientStatForm" property="blueOptout" scope="request"/>">&nbsp;</td>
            </tr>
        </table>
    </td>
    <td>
        <div align=right><b>&nbsp;<bean:write name="recipientStatForm" property="numOptout" scope="request"/><b>
        </div>
    </td>
</tr>

<tr>
    <td><img src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="10"
             height="5" border="0"></td>
    <td><img src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="10"
             height="5" border="0"></td>
    <td><img src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="10"
             height="5" border="0"></td>
</tr>

<tr>
    <td align="left"><b><bean:message key="statistic.Bounces"/>:&nbsp;&nbsp;</b></td>
    <td>
        <table border="0" cellspacing="0" cellpadding="0">
            <tr height="20">
                <td class="stats_highlight_color"
                    width="<bean:write name="recipientStatForm" property="blueBounce" scope="request"/>">&nbsp;</td>
            </tr>
        </table>
    </td>
    <td>
        <div align=right><b>&nbsp;<bean:write name="recipientStatForm" property="numBounce" scope="request"/><b>
        </div>
    </td>
</tr>

<tr>
    <td><img src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="10"
             height="5" border="0"></td>
    <td><img src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="10"
             height="5" border="0"></td>
    <td><img src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="10"
             height="5" border="0"></td>
</tr>

<tr>
    <td align="left"><b><bean:message key="recipient.Active"/>:&nbsp;&nbsp;</b></td>
    <td>
        <table border="0" cellspacing="0" cellpadding="0">
            <tr height="20">
                <td class="stats_highlight_color"
                    width="<bean:write name="recipientStatForm" property="blueActive" scope="request"/>">&nbsp;</td>
            </tr>
        </table>
    </td>
    <td>
        <div align=right><b>&nbsp;<bean:write name="recipientStatForm" property="numActive" scope="request"/><b>
        </div>
    </td>
</tr>

<tr>
    <td><img src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="10"
             height="5" border="0"></td>
    <td><img src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="10"
             height="5" border="0"></td>
    <td><img src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="10"
             height="5" border="0"></td>
</tr>

<tr>
    <td align="left"><b><bean:message key="recipient.unbound"/>:&nbsp;&nbsp;</b></td>
    <td>
        <table border="0" cellspacing="0" cellpadding="0">
            <tr height="20">
                <td class="stats_highlight_color"
                    width="<bean:write name="recipientStatForm" property="blueUnbound" scope="request"/>">
                    &nbsp;</td>
            </tr>
        </table>
    </td>
    <td>
        <div align=right><b>&nbsp;<bean:write name="recipientStatForm" property="numUnbound" scope="request"/><b>
        </div>
    </td>
</tr>

<tr>
    <td colspan=3>
        <hr />
    </td>
</tr>

<tr>
    <td align="left"><b><bean:message key="statistic.Total"/>:&nbsp;&nbsp;</b></td>
    <td>
        <table border="0" cellspacing="0" cellpadding="0">
            <tr height="20">
                <td class="stats_highlight_color"
                    width="200"></td>
            </tr>
        </table>
    </td>
    <td>
        <div align="right"><b>&nbsp;<bean:write name="recipientStatForm" property="numRecipients"
                                                scope="request"/></b></div>
    </td>
</tr>

<tr>
    <td colspan=3>
        <hr />
    </td>
</tr>


<% if (mediaType == 0) { %>


<tr>
    <td colspan="3">
        <BR>
        <span class="head3"><bean:message key="recipient.RecipientMailtype"/>:</span>
    </td>
</tr>

</tr>
<td><img src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="10" height="5"
         border="0"></td>
<td><img src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="10" height="5"
         border="0"></td>
<td><img src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="10" height="5"
         border="0"></td>
</tr>

<tr>
    <td align="left"><b><bean:message key="mailing.Text"/>:&nbsp;&nbsp;</b></td>
    <td>
        <table border="0" cellspacing="0" cellpadding="0">
            <tr height="20">
                <td class="stats_highlight_color"
                    width="<bean:write name="recipientStatForm" property="blueText" scope="request"/>">&nbsp;</td>
            </tr>
        </table>
    </td>
    <td>
        <div align=right><b><bean:write name="recipientStatForm" property="numText" scope="request"/>&nbsp;<b></div>
    </td>
</tr>

</tr>
<td><img src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="10" height="5"
         border="0"></td>
<td><img src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="10" height="5"
         border="0"></td>
<td><img src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="10" height="5"
         border="0"></td>
</tr>

<tr>
    <td align="left"><b><bean:message key="mailing.HTML"/>:&nbsp;&nbsp;</b></td>
    <td>
        <table border="0" cellspacing="0" cellpadding="0">
            <tr height="20">
                <td class="stats_highlight_color"
                    width="<bean:write name="recipientStatForm" property="blueHTML" scope="request"/>">&nbsp;</td>
            </tr>
        </table>
    </td>
    <td>
        <div align=right><b><bean:write name="recipientStatForm" property="numHTML" scope="request"/>&nbsp;<b></div>
    </td>
</tr>


</tr>
<td><img src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="10" height="5"
         border="0"></td>
<td><img src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="10" height="5"
         border="0"></td>
<td><img src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="10" height="5"
         border="0"></td>
</tr>

<tr>
    <td align="left"><b><bean:message key="recipient.OfflineHTML"/>:&nbsp;&nbsp;</b></td>
    <td>
        <table border="0" cellspacing="0" cellpadding="0">
            <tr height="20">
                <td class="stats_highlight_color"
                    width="<bean:write name="recipientStatForm" property="blueOffline" scope="request"/>">
                    &nbsp;</td>
            </tr>
        </table>
    </td>
    <td>
        <div align=right><b><bean:write name="recipientStatForm" property="numOffline" scope="request"/>&nbsp;</b>
        </div>
    </td>
</tr>

<tr>
    <td colspan=3>
        <hr />
    </td>
</tr>

<% } %>
<tr>
    <td colspan="3"><i><bean:message key="statistic.multiple"/></i></td>
</tr>

</table>

<!-- here month detail: -->
<%
    DateFormat aFormat2 = new SimpleDateFormat("MMMM yyyy", (Locale) session.getAttribute(org.apache.struts.Globals.LOCALE_KEY));
    DateFormat aFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, (Locale) session.getAttribute(org.apache.struts.Globals.LOCALE_KEY));
    DateFormat aFormatWeekday = new SimpleDateFormat("EE", (Locale) session.getAttribute(org.apache.struts.Globals.LOCALE_KEY));
    // GregorianCalendar aCal=(GregorianCalendar)pageContext.getAttribute("thisMonth");
    boolean changeColor = true;
%>
<br>
<span class="head2"><bean:message
        key="statistic.Detail_Analysis"/>: <%= aFormat2.format(aCal.getTime()) %></span><br><br>
<table border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td><span class="head3"><bean:message key="statistic.Day"/>&nbsp;</span></td>
        <td colspan="2"><span class="head3">&nbsp;&nbsp;&nbsp;<bean:message key="recipient.Opt_Ins"/>&nbsp;</span></td>
        <td colspan="2"><span class="head3">&nbsp;&nbsp;&nbsp;<bean:message key="statistic.Opt_Outs"/>&nbsp;</span></td>
        <td colspan="2"><span class="head3">&nbsp;&nbsp;&nbsp;<bean:message key="statistic.Bounces"/>&nbsp;</span></td>
    </tr>
    <%
        String file = (String) (session.getAttribute("csvdata"));
        file += "\n";
        String detStr = SafeString.getLocaleString("statistic.Detail_Analysis", (Locale) session.getAttribute(org.apache.struts.Globals.LOCALE_KEY));
        file += SafeString.getLocaleString("statistic.Detail_Analysis", (Locale) session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ": ; " + aFormat2.format(aCal.getTime()) + "\n";
        file += "\n";
        file += SafeString.getLocaleString("statistic.Day", (Locale) session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ": ;";
        file += SafeString.getLocaleString("recipient.Opt_Ins", (Locale) session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ";";
        file += SafeString.getLocaleString("statistic.Opt_Outs", (Locale) session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ";";
        file += SafeString.getLocaleString("statistic.Bounces", (Locale) session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\r\n";
    %>

    <tr>
        <td colspan="7">
            <hr />
        </td>
    </tr>

    <% int totalSubscribes = 0;
        int totalOptouts = 0;
        int totalBounces = 0;
    %>

    <agn:ShowSubscriberStat mailinglistID="<%= mailinglistID %>" targetID="<%= targetID %>"
                            month="<%= request.getParameter(\"month\") %>"
                            mediaType="<%= request.getParameter(\"mediaType\") %>">
        <% if (changeColor) { %>
        <tr class="stats_normal_color">
                    <% } else { %>
        <tr>
            <% } %>
            <td colspan="7"><img src="${emmLayoutBase.imagesURL}/one_pixel.gif"
                                 width="1" height="1" border="0"></td>
        </tr>

        <% if (changeColor) { %>
        <tr class="stats_normal_color">
                    <% } else { %>
        <tr>
            <% } %>
            <td><b>
                <nobr><%= aFormatWeekday.format(pageContext.getAttribute("today")) %>
                    ,&nbsp;<%= aFormat.format(pageContext.getAttribute("today")) %>&nbsp;&nbsp;</nobr>
            </b></td>
            <td align="right">&nbsp;<agn:ShowByPermission token="recipient.show"><a
                    href="<html:rewrite page='<%= new String(\"/recipient.do?action=\" + RecipientAction.ACTION_LIST + \"&user_status=1&trgt_clear=1&trgt_add.x=1&trgt_bracketopen0=0&trgt_bracketclose0=0&trgt_chainop0=0&trgt_column0=bind.\"+AgnUtils.changeDateName()+\"%23DATE&trgt_operator0=1&trgt_value0=\" + aFormat4.format(pageContext.getAttribute(\"today\")) + \"&listID=\" + mailinglistID) %>'/>">
                </agn:ShowByPermission>
                <%= pageContext.getAttribute("subscribes") %>
                <agn:ShowByPermission token="recipient.show"></a></agn:ShowByPermission>&nbsp;</td>
            <td>
                <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td background="${emmLayoutBase.imagesURL}/one_pixel_h.gif"
                            width="1"></td>
                        <td class="stats_highlight_color"
                            width="<%= (int)((double)((Integer)pageContext.getAttribute("subscribes")).intValue()/(double)((Integer) pageContext.getAttribute("max_subscribes")).intValue()*100.0) %>"></td>
                        <td width='<%= (int)(100.0-((double)((Integer)pageContext.getAttribute("subscribes")).intValue()/(double)((Integer)pageContext.getAttribute("max_subscribes")).intValue()*100.0)) %>'>
                            &nbsp;</td>
                    </tr>
                </table>
            </td>
            <td align="right">&nbsp;&nbsp;&nbsp;<agn:ShowByPermission token="recipient.show"><a
                    href="<html:rewrite page='<%= new String("/recipient.do?action=" + RecipientAction.ACTION_LIST + "&user_status=4&trgt_clear=1&trgt_add.x=1&trgt_bracketopen0=0&trgt_bracketclose0=0&trgt_chainop0=0&trgt_column0=bind."+AgnUtils.changeDateName()+"%23DATE&trgt_operator0=1&trgt_value0=" + aFormat4.format(pageContext.getAttribute(\"today\")) + "&listID=" + mailinglistID) %>'/>">
                </agn:ShowByPermission>
                <%= pageContext.getAttribute("optouts") %>
                <agn:ShowByPermission token="recipient.show"></a></agn:ShowByPermission>&nbsp;</td>
            <td>
                <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td background="${emmLayoutBase.imagesURL}/one_pixel_h.gif"
                            width="1"></td>
                        <td class="stats_highlight_color"
                            width="<%= (int)((double)((Integer)pageContext.getAttribute("optouts")).intValue()/(double)((Integer)pageContext.getAttribute("max_optouts")).intValue()*100.0) %>"></td>
                        <td width='<%= (int)(100.0-((double)((Integer)pageContext.getAttribute("optouts")).intValue()/(double)((Integer)pageContext.getAttribute("max_optouts")).intValue()*100.0)) %>'>
                            &nbsp;</td>
                    </tr>
                </table>
            </td>
            <td align="right">&nbsp;&nbsp;&nbsp;<agn:ShowByPermission token="recipient.show"><a
                    href="<html:rewrite page='<%= new String("/recipient.do?action=" + RecipientAction.ACTION_LIST + "&user_status=2&trgt_clear=1&trgt_add.x=1&trgt_bracketopen0=0&trgt_bracketclose0=0&trgt_chainop0=0&trgt_column0=bind."+AgnUtils.changeDateName()+"%23DATE&trgt_operator0=1&trgt_value0=" + aFormat4.format(pageContext.getAttribute(\"today\")) + "&listID=" + mailinglistID) %>'/>">
                </agn:ShowByPermission>
                <%= pageContext.getAttribute("bounces") %>
                <agn:ShowByPermission token="recipient.show"></a></agn:ShowByPermission>&nbsp;</td>
            <td>
                <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td background="${emmLayoutBase.imagesURL}/one_pixel_h.gif"
                            width="1"></td>
                        <td class="stats_highlight_color"
                            width="<%= (int)((double)((Integer)pageContext.getAttribute("bounces")).intValue()/(double)((Integer)pageContext.getAttribute("max_bounces")).intValue()*100.0) %>"></td>
                        <td width='<%= (int)(100.0-((double)((Integer)pageContext.getAttribute("bounces")).intValue()/(double)((Integer)pageContext.getAttribute("max_bounces")).intValue()*100.0)) %>'>
                            &nbsp;</td>
                    </tr>
                </table>
            </td>
        </tr>
        <% if (changeColor) { %>
        <tr class="stats_normal_color">
                    <% } else { %>
        <tr>
            <% }
                changeColor = !changeColor; %>
            <td colspan="7"><img src="${emmLayoutBase.imagesURL}/one_pixel.gif"
                                 width="1" height="1" border="0"></td>
        </tr>
        <% file += aFormatWeekday.format(pageContext.getAttribute("today")) + ", " + aFormat.format(pageContext.getAttribute("today")) + ";";
            file += pageContext.getAttribute("subscribes") + ";";
            file += pageContext.getAttribute("optouts") + ";";
            file += pageContext.getAttribute("bounces") + "\r\n";


            totalSubscribes += ((Integer) pageContext.getAttribute("subscribes")).intValue();
            totalOptouts += ((Integer) pageContext.getAttribute("optouts")).intValue();
            totalBounces += ((Integer) pageContext.getAttribute("bounces")).intValue();
        %>

    </agn:ShowSubscriberStat>


    <tr>
        <td colspan="7">
            <hr />
        </td>
    </tr>


    <% //total Overwiew:

        file += SafeString.getLocaleString("statistic.Total", (Locale) session.getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ";;";
        file += totalSubscribes + ";";
        file += totalOptouts + ";";
        file += totalBounces + "\r\n";

        // insert csv file in hashtable:
        my_map.put(timekey, file);
        pageContext.getSession().setAttribute("map", my_map);

    %>
    <tr>
        <td><b><bean:message key="statistic.Total"/>:&nbsp;&nbsp;</b></td>
        <td align="right">&nbsp;<b><%= totalSubscribes %>
        </b>&nbsp;</td>
        <td>&nbsp;</td>
        <td align="right">&nbsp;<b><%= totalOptouts %>
        </b>&nbsp;</td>
        <td>&nbsp;</td>
        <td align="right">&nbsp;<b><%= totalBounces %>
        </b>&nbsp;</td>
        <td>&nbsp;</td>

    </tr>


</table>

<hr />


<% aCal.add(Calendar.MONTH, -1); %>
<table border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td align="left"><html:link styleClass="recipient_month_navigation"
                page='<%= new String(\"/recipient_stats.do?action=\" + RecipientStatAction.ACTION_DISPLAY + \"&month=\" + aFormat3.format(aCal.getTime()) + \"&mailingListID=\" + mailinglistID + \"&targetID=\" + targetID + \"&mediaType=\" + mediaType) %>'><img
                src="${emmLayoutBase.imagesURL}/arrow_back.gif"
                border="0">&nbsp;<%= aFormat2.format(aCal.getTime()) %>
        </html:link></td>
        <% aCal.add(Calendar.MONTH, 2); %>
        <td align="right"><html:link styleClass="recipient_month_navigation_right"
                page='<%= new String(\"/recipient_stats.do?action=\" + RecipientStatAction.ACTION_DISPLAY + \"&month=\" + aFormat3.format(aCal.getTime()) + \"&mailingListID=\" + mailinglistID + \"&targetID=\" + targetID + \"&mediaType=\" + mediaType) %>'><%= aFormat2.format(aCal.getTime()) %>&nbsp;
            <img src="${emmLayoutBase.imagesURL}/arrow_next.gif" border="0"></html:link></td>
    </tr>
</table>

</div>


</html:form>