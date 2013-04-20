<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.beans.TrackableLink, org.agnitas.stat.MailingStatEntry, org.agnitas.stat.URLStatEntry, org.agnitas.target.Target, org.agnitas.util.AgnUtils" %>
<%@ page import="org.agnitas.util.EmmCalendar" %>
<%@ page import="org.agnitas.util.SafeString" %>
<%@ page import="org.agnitas.web.MailingStatAction" %>
<%@ page import="org.agnitas.web.MailingStatForm" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.LinkedList" %>
<%@ page import="java.util.ListIterator" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.TimeZone" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% int tmpMailingID = 0;
    int tmpTargetID = 0;
    // int tmpUniqueClicks=0;
    String tmpNetto = "no";
    String tmpShortname = new String("");
    MailingStatForm aForm = null;
    int maxblue = 0;
    int maxNRblue = 0;
    int maxSubscribers = 0;
    if (session.getAttribute("mailingStatForm") != null) {
        aForm = (MailingStatForm) session.getAttribute("mailingStatForm");
        tmpMailingID = aForm.getMailingID();
        tmpTargetID = aForm.getTargetID();
        tmpShortname = aForm.getMailingShortname();
        maxblue = aForm.getMaxblue();
        maxNRblue = aForm.getMaxNRblue();
        maxSubscribers = aForm.getMaxSubscribers();
    }

    java.text.DecimalFormat prcFormat = new java.text.DecimalFormat("##0.#");
    prcFormat.setDecimalSeparatorAlwaysShown(false);

    EmmCalendar my_calendar = new EmmCalendar(java.util.TimeZone.getDefault());
    my_calendar.changeTimeWithZone(TimeZone.getTimeZone(AgnUtils.getAdmin(request).getAdminTimezone()));
    java.util.Date my_time = my_calendar.getTime();
    String Datum = my_time.toString();
    String timekey = Long.toString(my_time.getTime());
    pageContext.setAttribute("time_key", timekey);

    // map for the csv download
    java.util.Hashtable my_map = null;
    if (pageContext.getSession().getAttribute("map") == null) {
        my_map = new java.util.Hashtable();
        pageContext.getSession().setAttribute("map", my_map);
        // System.out.println("map exists.");
    } else {
        my_map = (java.util.Hashtable) (pageContext.getSession().getAttribute("map"));
        // System.out.println("new map.");
    }
    // put csv file from the form in the hash table:
    String file = ((MailingStatForm) (session.getAttribute("mailingStatForm"))).getCsvfile();
%>

<html:form action="/mailing_stat">
<html:hidden property="mailingID"/>
<html:hidden property="action"/>

<div class="button_container">

    <div class="action_button float_left">
        <html:link page='<%= \"/ecs_stat.do?mailingId=\" + tmpMailingID + \"&init=true\" %>'>
            <span><bean:message key="ecs.Heatmap"/></span>
        </html:link>
    </div>

</div>

<% //prepare loop over targetIDs:
    Hashtable statValues = new Hashtable();
    statValues = ((MailingStatForm) session.getAttribute("mailingStatForm")).getStatValues();
    LinkedList targets = null;
    ListIterator targetIter = null;
    targets = ((MailingStatForm) session.getAttribute("mailingStatForm")).getTargetIDs();
%>

<div class="content_element_container">
    <div class="target_group_select_panel">
        <div class="compare_view_group_container targetgroups_select_container">
            <% /* * * * * * * * * * */ %>
            <% /* add target group  */ %>
            <% /* * * * * * * * * * */ %>
            <% if (targets.size() < 5) { %>
            <bean:message key="target.Target"/>:
            <html:select property="nextTargetID">
                <html:option value="0"><bean:message key="statistic.All_Subscribers"/></html:option>
                <c:forEach var="target" items="${targetList}">
                    <% if (!targets.contains(new Integer(((Target) pageContext.getAttribute("target")).getId()))) { %>
                    <html:option value='${target.id}'>
                        ${target.targetName}
                    </html:option>
                    <% } %>
                </c:forEach>
            </html:select>
            <% } %>
        </div>
        <% if (targets.size() < 5) { %>
        <div class="action_button add_button">
            <input type="hidden" name="add" value="">
            <a href="#"
               onclick="document.mailingStatForm.add.value='add';document.mailingStatForm.submit();return false;">
                <span><bean:message key="button.Add"/></span>
            </a>
        </div>
        <% } %>
    </div>
    <div align="right" >
        <html:link style="align:right" page='<%= new String(\"/file_download?key=\" + timekey) %>'><img
                src="${emmLayoutBase.imagesURL}/icon_save.gif" border="0">
        </html:link>
    </div>
</div>

<div class="content_element_container">

<table border="0" cellspacing="0" cellpadding="0">

<% /* * * * * * * */ %>
<% /* CLICK STATS */ %>
<% /* * * * * * * */ %>
<tr>
    <td><span class="head3"><bean:message key="statistic.KlickStats"/>:<br><br></span></td>
    <% for (int columns = 0; columns < targets.size(); columns++) { %>
    <td>&nbsp;</td>
    <% } %>
</tr>

<% /* * * * * * * * * * * */ %>
<% /* clicks table header */ %>
<% /* * * * * * * * * * * */ %>
<tr>
    <td><b><bean:message key="mailing.URL"/>&nbsp;</b></td>
    <%
        file += "\r\n\r\n\"" + SafeString.getLocaleString("mailing.URL", (Locale) request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\";\"" + SafeString.getLocaleString("default.description", (Locale) request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\"";
        for (int columns = 0; columns < targets.size(); columns++) { %>
    <td align="right">&nbsp;<b><bean:message key="statistic.ClicksBruttoNetto"/></b></td>

    <% //file += ";\"" + SafeString.getLocaleString("Clicks", (Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\"";
    } %>
</tr>

<% /* * * * * * * * */ %>
<% /* target groups */ %>
<% /* * * * * * * * */ %>
<tr>
    <td align="right">&nbsp;</td>
    <%
        targetIter = targets.listIterator();
        while (targetIter.hasNext()) {
            int aktTargetID = ((Integer) targetIter.next()).intValue();
            MailingStatEntry aktMailingStatEntry = (MailingStatEntry) statValues.get(new Integer(aktTargetID));
    %>
    <td align="right">&nbsp;<b><%=aktMailingStatEntry.getTargetName()%>
    </b>
        <%
            file += ";\"" + SafeString.getLocaleString("statistic.ClicksBruttoNetto", (Locale) request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + ", " + aktMailingStatEntry.getTargetName() + "\""; %>
        <% if (targets.size() > 1) { %>
        &nbsp;<html:link styleClass="blue_link"
                page='<%= new String(\"/mailing_stat.do?action=\" + Integer.toString(MailingStatAction.ACTION_MAILINGSTAT) + \"&delTargetID=\" + aktTargetID) %>'><img
                src="${emmLayoutBase.imagesURL}/table_delete_icon.png"
                alt="<bean:message key='button.Delete'/>" border="0"></html:link>&nbsp;
        <% } %>
    </td>

    <% } %>
</tr>

<tr>
    <td colspan="<%=(targets.size() + 1)%>">
        <hr>
    </td>
</tr>

<% // * * * * * * * * * * * * * * * * * * * * %>
<% // * *  R E L E V A N T  C L I C K S:  * * %>
<% // * * * * *  ( b e g i n )  * * * * * * * %>
<% // * * * * * * * * * * * * * * * * * * * * %>

<% boolean changeColor = true; %>

<% /* * * * * * * * * * * */ %>
<% /* loop over all URLs  */ %>
<% /* * * * * * * * * * * */ %>
<%
    int aktTargetID = 0;
    int aktUrlID = 0;
    Hashtable urlNames = new Hashtable();
    Hashtable urlShortnames = new Hashtable();
    urlNames = ((MailingStatForm) session.getAttribute("mailingStatForm")).getUrlNames();
    urlShortnames = ((MailingStatForm) session.getAttribute("mailingStatForm")).getUrlShortnames();
    java.text.NumberFormat nf = java.text.NumberFormat.getCurrencyInstance((Locale) request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY));
    LinkedList tmpClickedUrls = new LinkedList();
    tmpClickedUrls = aForm.getClickedUrls();
    HashSet map = new HashSet();
    int urlIndex = tmpClickedUrls.size();
    while (--urlIndex >= 0) {
        aktUrlID = ((URLStatEntry) (tmpClickedUrls.get(urlIndex))).getUrlID();
        map.add(new Integer(aktUrlID));
    }
    urlIndex = map.size();
    while (--urlIndex >= 0) {
        aktUrlID = ((URLStatEntry) (tmpClickedUrls.get(urlIndex))).getUrlID();
        TrackableLink trkLnk = (TrackableLink) urlNames.get(new Integer(aktUrlID));
        file += "\r\n\"" + trkLnk.getFullUrl() + "\";\"" + urlShortnames.get(new Integer(aktUrlID)) + "\"";

%>

<%
    // * * * * * * * * * * * * * *
    // * *  outer loop start:  * *
    // * * * * * * * * * * * * * * %>

<% /* * * * * * * * * * * * * * * * * * * * * * */ %>
<% /* clicks table (inner loop over targetIDs)  */ %>
<% /* * * * * * * * * * * * * * * * * * * * * * */ %>
<% if (changeColor) { %>
<tr class="stats_normal_color">
            <% } else { %>
<tr>
    <% }
        changeColor = !changeColor; %>
    <td valign="center"><a href='<%= trkLnk != null ? trkLnk.getFullUrl():"" %>' target="_blank">
        <img src="${emmLayoutBase.imagesURL}/extlink.gif" border="0"
             alt="<%= urlNames.get(new Integer(aktUrlID)) %>">
    </a>&nbsp;
        <html:link styleClass="blue_link"
                page='<%= new String(\"/mailing_stat.do?action=\" + MailingStatAction.ACTION_WEEKSTAT + \"&mailingID=\" + tmpMailingID + \"&urlID=\" + aktUrlID + \"&targetID=0\") %>'>
            <% if (((String) urlShortnames.get(new Integer(aktUrlID))).compareTo("") != 0) { %><%= urlShortnames.get(new Integer(aktUrlID)) %><% } else { %><%= urlNames.get(new Integer(aktUrlID)) %><% } %>
        </html:link>&nbsp;</td>
    <%
        targetIter = targets.listIterator();
        while (targetIter.hasNext()) {
            aktTargetID = ((Integer) targetIter.next()).intValue();
            MailingStatEntry aktMailingStatEntry = (MailingStatEntry) statValues.get(new Integer(aktTargetID));
            Hashtable aktClickStatValues = (Hashtable) aktMailingStatEntry.getClickStatValues();
            URLStatEntry aktURLStatEntry = (URLStatEntry) aktClickStatValues.get(new Integer(aktUrlID));
            int barNetto = 1;
            int barDiff = 0;
            int barFree = 149;
            double agnClkNetto = 0;
            double agnClkDiff = 0;

            if (aktURLStatEntry != null) {
                agnClkNetto = (double) aktURLStatEntry.getClicksNetto() / (double) maxblue;
                agnClkDiff = (double) (aktURLStatEntry.getClicks() - aktURLStatEntry.getClicksNetto()) / (double) maxblue;
                barNetto = (int) (agnClkNetto * 150.0) + 1;
                barDiff = (int) (agnClkDiff * 150.0);
                barFree = 150 - barNetto - barDiff;
            }
            if (barFree < 0) {
                barFree = 0;
            }
    %>

    <td align="right" width="165"><img
            src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="3" height="3"
            border="0">
        <table width="155" cellspacing="0" cellpadding="0" border="0">
            <tr>
                <td align="right" style="border:1px solid #444444;" bgcolor="#ffffff">
                    <img src="${emmLayoutBase.imagesURL}/one_pixel.gif"
                         width="<%=barFree%>" height="10" border="0"><% if (barDiff != 0) { %><img border="0"
                                                                                                   width="<%=barDiff%>"
                                                                                                   height="10"
                                                                                                   src="${emmLayoutBase.imagesURL}/one_pixel_s.gif"/><% } %><img
                        border="0" width="<%=barNetto%>" height="10"
                        src="${emmLayoutBase.imagesURL}/one_pixel_h.gif"/>
                </td>
                <td>
                    <img src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="3"
                         height="3" border="0">
                </td>
            </tr>
        </table>
        <html:link styleClass="blue_link"
                page='<%= new String(\"/mailing_stat.do?action=\" + MailingStatAction.ACTION_WEEKSTAT + \"&mailingID=\" + tmpMailingID + \"&urlID=\" + aktUrlID + \"&targetID=\" + aktTargetID) %>'>
            <% if (aktURLStatEntry != null) { %><%= aktURLStatEntry.getClicks() %> (<%= aktURLStatEntry.getClicksNetto() %>)<% } else { %>0 (0)<% } %>&nbsp;
            <% // Prozent-Anzeige: %>
            <% if (aktTargetID == 0 && aktMailingStatEntry.getTotalMails() != 0) {
                if (aktURLStatEntry != null) {
                    double prc = (double) aktURLStatEntry.getClicksNetto() / (double) aktMailingStatEntry.getTotalMails() * 100d; %>
            (<%=prcFormat.format(prc)%>&nbsp;%)&nbsp;
            <% } else { %>
            (0&nbsp;%)&nbsp;
            <% }
            } %>

        </html:link>
    </td>
    <% if (aktURLStatEntry != null) {
        file += ";\"" + aktURLStatEntry.getClicks() + " (" + aktURLStatEntry.getClicksNetto() + ")\"";
    } else {
        file += ";\"0 (0)\"";
    }%>


    <% /* * * * * * * * * * * * * * * * */ %>
    <% /* end inner loop over targetIDs */ %>
    <% /* * * * * * * * * * * * * * * * */ %>


    <% /* * * * * * * * * * * * * * * * */ %>
    <% /* end loop over clickStat URLs  */ %>
    <% /* * * * * * * * * * * * * * * * */ %>
    <% } %>
</tr>
<%
    }

    // * * * * * * * * * * * * *
    // * *  outer loop end:  * *
    // * * * * * * * * * * * * *
%>

<% // * * * * * * * * * * * * * * * * * * * * %>
<% // * * * * * *  ( e n d )  * * * * * * * * %>
<% // * *  R E L E V A N T  C L I C K S:  * * %>
<% // * * * * * * * * * * * * * * * * * * * * %>

<% /* * * * * * */ %>
<% /* empty row */ %>
<% /* * * * * * */ %>
<tr>
    <td colspan="<%=(targets.size() + 1)%>">&nbsp;</td>
    <% file += "\r\n"; %>
</tr>

<% /* * * * * * * * * * * * * */ %>
<% /* total clickSubscribers  */ %>
<% /* * * * * * * * * * * * * */ %>
<tr>
    <td><bean:message key="statistic.TotalClickSubscribers"/>:&nbsp;</td>
    <%
        file += "\"" + SafeString.getLocaleString("statistic.TotalClickSubscribers", (Locale) request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\";\"\"";
        targetIter = targets.listIterator();
        while (targetIter.hasNext()) {
            aktTargetID = ((Integer) targetIter.next()).intValue();
            MailingStatEntry aktMailingStatEntry = (MailingStatEntry) statValues.get(new Integer(aktTargetID));
            file += ";\"" + aktMailingStatEntry.getTotalClickSubscribers() + "\"";
            double agnTotal = 0;
            if (aktMailingStatEntry.getTotalClickSubscribers() > maxblue) {
                agnTotal = 1;
            } else {
                agnTotal = (double) aktMailingStatEntry.getTotalClickSubscribers() / (double) maxblue;
            }
            int barNetto = (int) (agnTotal * 150.0);
            int barFree = 150 - barNetto;
            if (barFree < 0) {
                barFree = 0;
            }
    %>
    <td align="right">
        <table width="155" cellspacing="0" cellpadding="0" border="0">
            <tr>
                <td align="right" style="border:1px solid #444444;" bgcolor="#ffffff">
                    <img src="${emmLayoutBase.imagesURL}/one_pixel.gif"
                         width="<%=barFree%>" height="10" border="0"><img border="0" width="<%=barNetto%>" height="10"
                                                                          src="${emmLayoutBase.imagesURL}/one_pixel_h.gif"/>
                </td>
                <td>
                    <img src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="3"
                         height="3" border="0">
                </td>
            </tr>
        </table>
        &nbsp;<%=aktMailingStatEntry.getTotalClickSubscribers()%>&nbsp;
        <% // Prozent-Anzeige: %>
        <% if (aktTargetID == 0 && aktMailingStatEntry.getTotalMails() != 0) {
            double prc = (double) aktMailingStatEntry.getTotalClickSubscribers() / (double) aktMailingStatEntry.getTotalMails() * 100d; %>
        (<%=prcFormat.format(prc)%>&nbsp;%)&nbsp;
        <% } %>
    </td>

    <% } %>
</tr>

<% /*  <HR> */ %>
<tr>
    <td colspan="<%=(targets.size() + 1)%>">
        <hr size="1" noshade>
    </td>
</tr>


<% /* * * * * * * * */ %>
<% /* total clicks  */ %>
<% /* * * * * * * * */ %>
<tr>
    <td><html:link styleClass="blue_link"
            page='<%= new String(\"/mailing_stat.do?action=\" + MailingStatAction.ACTION_WEEKSTAT + \"&mailingID=\" + tmpMailingID + \"&urlID=0\") %>'><b><bean:message
            key="statistic.TotalClicks"/>:&nbsp;</b></html:link></td>
    <%
        file += "\r\n\"" + SafeString.getLocaleString("statistic.TotalClicks", (Locale) request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + "\";\"\"";
        targetIter = targets.listIterator();
        while (targetIter.hasNext()) {
            aktTargetID = ((Integer) targetIter.next()).intValue();
            MailingStatEntry aktMailingStatEntry = (MailingStatEntry) statValues.get(new Integer(aktTargetID));
            double agnClkNetto = (double) aktMailingStatEntry.getTotalClicksNetto() / (double) maxblue;
            double agnClkDiff = ((double) aktMailingStatEntry.getTotalClicks() - (double) aktMailingStatEntry.getTotalClicksNetto()) / (double) maxblue;
            int barNetto = (int) (agnClkNetto * 150.0) + 1;
            int barDiff = (int) (agnClkDiff * 150.0);
            int barFree = 150 - barNetto - barDiff;
            if (barFree < 0) {
                barFree = 0;
            }
            file += ";\"" + aktMailingStatEntry.getTotalClicks() + " (" + aktMailingStatEntry.getTotalClicksNetto() + ")\"";
    %>
    <td align="right">
        <table width="155" cellspacing="0" cellpadding="0" border="0">
            <tr>
                <td align="right" style="border:1px solid #444444;">
                    <img src="${emmLayoutBase.imagesURL}/one_pixel.gif"
                         width="<%=barFree%>" height="10" border="0"><% if (barDiff > 0) { %><img border="0"
                                                                                                  width="<%=barDiff%>"
                                                                                                  height="10"
                                                                                                  src="${emmLayoutBase.imagesURL}/one_pixel_s.gif"/><% } %><img
                        border="0" width="<%=barNetto%>" height="10"
                        src="${emmLayoutBase.imagesURL}/one_pixel_h.gif"/>
                </td>
                <td>
                    <img src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="3"
                         height="3" border="0">
                </td>
            </tr>
        </table>

        &nbsp;<b><%=aktMailingStatEntry.getTotalClicks()%> (<%=aktMailingStatEntry.getTotalClicksNetto()%>)</b>&nbsp;
    </td>

    <% } %>
</tr>

<tr>
    <td colspan="<%=(targets.size() + 1)%>">
        <hr>
    </td>
</tr>

<tr>
    <td colspan="<%=(targets.size() + 1)%>">&nbsp;</td>
    <% file += "\r\n"; %>
</tr>


<% // * * * * * * * * * * * * * * * * * * * * * * * * %>
<% // * *  N O N - R E L E V A N T  C L I C K S:  * * %>
<% // * * * * * * *  ( b e g i n )  * * * * * * * * * %>
<% // * * * * * * * * * * * * * * * * * * * * * * * * %>
<%
    LinkedList tmpNotRelevantUrls = new LinkedList();
    tmpNotRelevantUrls = aForm.getNotRelevantUrls();
    if (tmpNotRelevantUrls.size() > 0) {
%>


<% /* headline */ %>
<tr>
    <td><span class="head3"><bean:message key="statistic.OtherLinks"/>:&nbsp;</span></td>
    <% for (int columns = 0; columns < targets.size(); columns++) { %>
    <td>&nbsp;</td>
    <% } %>
</tr>


<tr>
    <td colspan="<%=(targets.size() + 1)%>">&nbsp;</td>
</tr>

<% file += "\r\n\" " + SafeString.getLocaleString("statistic.OtherLinks", (Locale) request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + " \""; %>

<% changeColor = true; %>

<% /* * * * * * * * * * * */ %>
<% /* loop over all URLs  */ %>
<% /* * * * * * * * * * * */ %>
<%
    // int aktTargetID = 0;
    // int aktUrlID = 0;
    // Hashtable urlNames = new Hashtable();
    // Hashtable urlShortnames = new Hashtable();
    // urlNames = ((MailingStatForm)session.getAttribute("mailingStatForm")).getUrlNames();
    // urlShortnames = ((MailingStatForm)session.getAttribute("mailingStatForm")).getUrlShortnames();
    // java.text.NumberFormat nf = java.text.NumberFormat.getCurrencyInstance((Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY));

    urlIndex = 0;
    while (urlIndex < tmpNotRelevantUrls.size()) {
        aktUrlID = ((URLStatEntry) (tmpNotRelevantUrls.get(urlIndex))).getUrlID();

        file += "\r\n\"" + urlNames.get(new Integer(aktUrlID)) + "\";\"" + urlShortnames.get(new Integer(aktUrlID)) + "\"";
        urlIndex++;
%>

<%
    // * * * * * * * * * * * * *
    // * * outer loop start: * *
    // * * * * * * * * * * * * * %>

<% /* * * * * * * * * * * * * * * * * * * * * * */ %>
<% /* clicks table (inner loop over targetIDs)  */ %>
<% /* * * * * * * * * * * * * * * * * * * * * * */ %>
<% if (changeColor) { %>
<tr class="stats_normal_color">
            <% } else { %>
<tr>
    <% }
        changeColor = !changeColor; %>
    <td valign="center"><a href="<%= urlNames.get(new Integer(aktUrlID)) %>" target="_blank">
        <img src="${emmLayoutBase.imagesURL}/extlink.gif" border="0"
             alt="<%= urlNames.get(new Integer(aktUrlID)) %>">
    </a>&nbsp;
        <html:link styleClass="blue_link"
                page='<%= new String(\"/mailing_stat.do?action=\" + MailingStatAction.ACTION_WEEKSTAT + \"&mailingID=\" + tmpMailingID + \"&urlID=\" + aktUrlID + \"&targetID=0\") %>'>
            <% if (((String) urlShortnames.get(new Integer(aktUrlID))).compareTo("") != 0) { %><%= urlShortnames.get(new Integer(aktUrlID)) %><% } else { %><%= urlNames.get(new Integer(aktUrlID)) %><% } %>
        </html:link>&nbsp;</td>
    <%
        targetIter = targets.listIterator();
        while (targetIter.hasNext()) {
            aktTargetID = ((Integer) targetIter.next()).intValue();
            MailingStatEntry aktMailingStatEntry = (MailingStatEntry) statValues.get(new Integer(aktTargetID));
            Hashtable aktClickStatValues = (Hashtable) aktMailingStatEntry.getClickStatValues();
            URLStatEntry aktURLStatEntry = (URLStatEntry) aktClickStatValues.get(new Integer(aktUrlID));
            int barNetto = 1;
            int barDiff = 0;
            int barFree = 149;
            double agnClkNetto = 0;
            double agnClkDiff = 0;
            if (aktURLStatEntry != null) {
                agnClkNetto = (double) aktURLStatEntry.getClicksNetto() / (double) maxNRblue;
                agnClkDiff = (double) (aktURLStatEntry.getClicks() - aktURLStatEntry.getClicksNetto()) / (double) maxNRblue;
                barNetto = (int) (agnClkNetto * 150.0) + 1;
                barDiff = (int) (agnClkDiff * 150.0);
                barFree = 150 - barNetto - barDiff;
            }
            if (barFree < 0) {
                barFree = 0;
            }
    %>

    <td align="right" width="165"><img
            src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="3" height="3"
            border="0">
        <table width="155" cellspacing="0" cellpadding="0" border="0">
            <tr>
                <td align="right" style="border:1px solid #444444;" bgcolor="#ffffff">
                    <img src="${emmLayoutBase.imagesURL}/one_pixel.gif"
                         width="<%=barFree%>" height="10" border="0"><% if (barDiff != 0) { %><img border="0"
                                                                                                   width="<%=barDiff%>"
                                                                                                   height="10"
                                                                                                   src="${emmLayoutBase.imagesURL}/one_pixel_s.gif"/><% } %><img
                        border="0" width="<%=barNetto%>" height="10"
                        src="${emmLayoutBase.imagesURL}/one_pixel_h.gif"/>
                </td>
                <td>
                    <img src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="3"
                         height="3" border="0">
                </td>
            </tr>
        </table>
        <html:link styleClass="blue_link"
                page='<%= new String(\"/mailing_stat.do?action=\" + MailingStatAction.ACTION_WEEKSTAT + \"&mailingID=\" + tmpMailingID + \"&urlID=\" + aktUrlID + \"&targetID=\" + aktTargetID) %>'>
            <% if (aktURLStatEntry != null) { %><%= aktURLStatEntry.getClicks() %> (<%= aktURLStatEntry.getClicksNetto() %>)<% } else { %>0 (0)<% } %>&nbsp;
            <% // Prozent-Anzeige: %>
            <% if (aktTargetID == 0 && aktMailingStatEntry.getTotalMails() != 0) {
                if (aktURLStatEntry != null) {
                    double prc = (double) aktURLStatEntry.getClicksNetto() / (double) aktMailingStatEntry.getTotalMails() * 100d; %>
            (<%=prcFormat.format(prc)%>&nbsp;%)&nbsp;
            <% } else {%>
            (0&nbsp;%)&nbsp;
            <% }
            } %>

        </html:link>
    </td>
    <% if (aktURLStatEntry != null) {
        file += ";\"" + aktURLStatEntry.getClicks() + " (" + aktURLStatEntry.getClicksNetto() + ")\"";
    } else {
        file += ";\"0 (0)\"";
    }%>


    <% /* * * * * * * * * * * * * * * * */ %>
    <% /* end inner loop over targetIDs */ %>
    <% /* * * * * * * * * * * * * * * * */ %>
    <% } %>


    <% /* * * * * * * * * * * * * * * * */ %>
    <% /* end loop over clickStat URLs  */ %>
    <% /* * * * * * * * * * * * * * * * */ %>
</tr>
<%
    }
%>

<%
    // * * * * * * * * * * * *
    // * * outer loop end: * *
    // * * * * * * * * * * * *
%>

<% } %>

<% // * * * * * * * * * * * * * * * * * * * * * * * * %>
<% // * * * * * * * *  ( e n d )  * * * * * * * * * * %>
<% // * *  N O N - R E L E V A N T  C L I C K S:  * * %>
<% // * * * * * * * * * * * * * * * * * * * * * * * * %>


<% /* empty row */ %>
<tr>
    <td colspan="<%=(targets.size() + 1)%>">&nbsp;</td>
</tr>

<% file += "\r\n"; %>


<% /* * * * * * * * * */ %>
<% /* DELIVERY STATS  */ %>
<% /* * * * * * * * * */ %>
<tr>
    <td><span class="head3"><bean:message key="statistic.Delivery_Statistic"/>:<br><br></span></td>
    <%
        file += "\r\n\r\n\" " + SafeString.getLocaleString("statistic.Delivery_Statistic", (Locale) request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + " \"\r\n"; %>
    <% for (int columns = 0; columns < targets.size(); columns++) { %>
    <td>&nbsp;</td>
    <% } %>
</tr>

<%
    // delivery stats for each target group
%>


<% /* * * * * * * * */ %>
<% /* opened mails  */ %>
<% /* * * * * * * * */ %>
<tr class="stats_normal_color">
    <td>&nbsp;<html:link styleClass="blue_link"
            page='<%= new String(\"/mailing_stat.do?action=\" + MailingStatAction.ACTION_OPENEDSTAT + \"&mailingID=\" + tmpMailingID + \"&urlID=\" + aktUrlID) %>'><B><bean:message
            key="statistic.Opened_Mails"/>:</B></html:link>&nbsp;</td>
    <%
        file += "\r\n\" " + SafeString.getLocaleString("statistic.Opened_Mails", (Locale) request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + " \";\"\"";
        targetIter = targets.listIterator();
        while (targetIter.hasNext()) {
            aktTargetID = ((Integer) targetIter.next()).intValue();
            MailingStatEntry aktMailingStatEntry = (MailingStatEntry) statValues.get(new Integer(aktTargetID));
            file += ";\"" + aktMailingStatEntry.getOpened() + "\"";
            int barBlue = 1;
            int barFree = 149;
            if (maxSubscribers >= aktMailingStatEntry.getOpened()) {
                double agnBlue = (double) aktMailingStatEntry.getOpened() / (double) maxSubscribers;
                barBlue = (int) (agnBlue * 150.0) + 1;
                barFree = 150 - barBlue;
            }
            if (barFree < 0) {
                barFree = 0;
            }
    %>
    <td align="right"><img src="${emmLayoutBase.imagesURL}/one_pixel.gif"
                           width="10" height="3" border="0">
        <table width="155" cellspacing="0" cellpadding="0" border="0">
            <tr>
                <td align="right" style="border:1px solid #444444;" bgcolor="#ffffff">
                    <img src="${emmLayoutBase.imagesURL}/one_pixel.gif"
                         width="<%=barFree%>" height="10" border="0"><img border="0" width="<%=barBlue%>" height="10"
                                                                          src="${emmLayoutBase.imagesURL}/one_pixel_h.gif"/>
                </td>
                <td>
                    <img src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="3"
                         height="3" border="0">
                </td>
            </tr>
        </table>

        &nbsp;<b><%=aktMailingStatEntry.getOpened()%>
        </b>&nbsp;
        <% // Prozent-Anzeige: %>
        <% if (aktMailingStatEntry.getTotalMails() != 0) {
            double prc = (double) aktMailingStatEntry.getOpened() / (double) aktMailingStatEntry.getTotalMails() * 100d; %>
        (<%=prcFormat.format(prc)%>&nbsp;%)&nbsp;
        <% } %>
    </td>
    <% } %>
</tr>


<% /* * * * * */ %>
<% /* optouts */ %>
<% /* * * * * */ %>
<tr>
    <td>&nbsp;<B><bean:message key="statistic.Opt_Outs"/>:&nbsp;</B></td>
    <%
        file += "\r\n\" " + SafeString.getLocaleString("statistic.Opt_Outs", (Locale) request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + " \";\"\"";
        targetIter = targets.listIterator();
        while (targetIter.hasNext()) {
            aktTargetID = ((Integer) targetIter.next()).intValue();
            MailingStatEntry aktMailingStatEntry = (MailingStatEntry) statValues.get(new Integer(aktTargetID));
            file += ";\"" + aktMailingStatEntry.getOptouts() + "\"";
            int barBlue = 1;
            int barFree = 149;
            if (maxSubscribers >= aktMailingStatEntry.getOptouts()) {
                double agnBlue = (double) aktMailingStatEntry.getOptouts() / (double) maxSubscribers;
                barBlue = (int) (agnBlue * 150.0) + 1;
                barFree = 150 - barBlue;
            }
            if (barFree < 0) {
                barFree = 0;
            }
    %>
    <td align="right"><img src="${emmLayoutBase.imagesURL}/one_pixel.gif"
                           width="10" height="3" border="0">
        <table width="155" cellspacing="0" cellpadding="0" border="0">
            <tr>
                <td align="right" style="border:1px solid #444444;">
                    <img src="${emmLayoutBase.imagesURL}/one_pixel.gif"
                         width="<%=barFree%>" height="10" border="0"><img border="0" width="<%=barBlue%>" height="10"
                                                                          src="${emmLayoutBase.imagesURL}/one_pixel_h.gif"/>
                </td>
                <td>
                    <img src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="3"
                         height="3" border="0">
                </td>
            </tr>
        </table>

        &nbsp;<b><%=aktMailingStatEntry.getOptouts()%>
        </b>&nbsp;
        <% // Prozent-Anzeige: %>
        <% if (aktMailingStatEntry.getTotalMails() != 0) {
            double prc = (double) aktMailingStatEntry.getOptouts() / (double) aktMailingStatEntry.getTotalMails() * 100d; %>
        (<%=prcFormat.format(prc)%>&nbsp;%)&nbsp;
        <% } %>
    </td>
    <% } %>
</tr>


<% /* * * * * */ %>
<% /* bounces */ %>
<% /* * * * * */ %>
<tr class="stats_normal_color">
    <td>&nbsp;<html:link styleClass="blue_link"
            page='<%= new String(\"/mailing_stat.do?action=\" + MailingStatAction.ACTION_BOUNCESTAT + \"&mailingID=\" + tmpMailingID + \"&urlID=\" + aktUrlID) %>'><B><bean:message
            key="statistic.Bounces"/>:&nbsp;</B></html:link></td>
    <%
        file += "\r\n\" " + SafeString.getLocaleString("statistic.Bounces", (Locale) request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + " \";\"\"";
        targetIter = targets.listIterator();
        while (targetIter.hasNext()) {
            aktTargetID = ((Integer) targetIter.next()).intValue();
            MailingStatEntry aktMailingStatEntry = (MailingStatEntry) statValues.get(new Integer(aktTargetID));
            file += ";\"" + aktMailingStatEntry.getBounces() + "\"";
            int barBlue = 1;
            int barFree = 149;
            if (maxSubscribers >= aktMailingStatEntry.getBounces()) {
                double agnBlue = (double) aktMailingStatEntry.getBounces() / (double) maxSubscribers;
                barBlue = (int) (agnBlue * 150.0) + 1;
                barFree = 150 - barBlue;
            }
            if (barFree < 0) {
                barFree = 0;
            }
    %>
    <td align="right"><img src="${emmLayoutBase.imagesURL}/one_pixel.gif"
                           width="10" height="3" border="0">
        <table width="155" cellspacing="0" cellpadding="0" border="0">
            <tr>
                <td align="right" style="border:1px solid #444444;" bgcolor="#ffffff">
                    <img src="${emmLayoutBase.imagesURL}/one_pixel.gif"
                         width="<%=barFree%>" height="10" border="0"><img border="0" width="<%=barBlue%>" height="10"
                                                                          src="${emmLayoutBase.imagesURL}/one_pixel_h.gif"/>
                </td>
                <td>
                    <img src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="3"
                         height="3" border="0">
                </td>
            </tr>
        </table>

        &nbsp;<b><%=aktMailingStatEntry.getBounces()%>
        </b>&nbsp;
        <% // Prozent-Anzeige: %>
        <% if (aktMailingStatEntry.getTotalMails() != 0) {
            double prc = (double) aktMailingStatEntry.getBounces() / (double) aktMailingStatEntry.getTotalMails() * 100d; %>
        (<%=prcFormat.format(prc)%>&nbsp;%)&nbsp;
        <% } %>
    </td>
    <% } %>
</tr>


<tr>
    <td colspan="<%=(targets.size() + 1)%>">
        <hr>
    </td>
</tr>


<% /* * * * * * * */ %>
<% /* total mails */ %>
<% /* * * * * * * */ %>
<tr>
    <td>&nbsp;<B><bean:message key="Recipients"/>:&nbsp;</B></td>
    <%
        file += "\r\n\" " + SafeString.getLocaleString("Recipients", (Locale) request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)) + " \";\"\"";
        targetIter = targets.listIterator();
        while (targetIter.hasNext()) {
            aktTargetID = ((Integer) targetIter.next()).intValue();
            MailingStatEntry aktMailingStatEntry = (MailingStatEntry) statValues.get(new Integer(aktTargetID));
            file += ";\"" + aktMailingStatEntry.getTotalMails() + "\"";
            int barBlue = 1;
            int barFree = 149;
            if (maxSubscribers >= aktMailingStatEntry.getTotalMails()) {
                double agnBlue = (double) aktMailingStatEntry.getTotalMails() / (double) maxSubscribers;
                barBlue = (int) (agnBlue * 150.0) + 1;
                barFree = 150 - barBlue;
            }
            if (barFree < 0) {
                barFree = 0;
            }
    %>
    <td align="right">
        <table width="155" cellspacing="0" cellpadding="0" border="0">
            <tr>
                <td align="right" style="border:1px solid #444444;">
                    <img src="${emmLayoutBase.imagesURL}/one_pixel.gif"
                         width="<%=barFree%>" height="10" border="0"><img border="0" width="<%=barBlue%>" height="10"
                                                                          src="${emmLayoutBase.imagesURL}/one_pixel_h.gif"/>
                </td>
                <td>
                    <img src="${emmLayoutBase.imagesURL}/one_pixel.gif" width="3"
                         height="3" border="0">
                </td>
            </tr>
        </table>

        &nbsp;<b><%=aktMailingStatEntry.getTotalMails()%>
    </b>&nbsp;
    </td>
    <% } %>
</tr>

<% /* * * * * * * * * * * */ %>
<% /* clean admin clicks  */ %>
<agn:ShowByPermission token="stats.clean">
    <tr>
        <td colspan="<%=(targets.size() + 1)%>" align="right"><br><html:link styleClass="blue_link"
                page='<%= new String(\"/mailing_stat.do?action=\" + MailingStatAction.ACTION_CLEAN_QUESTION + \"&mailingID=\" + tmpMailingID) %>'><bean:message
                key="statistic.DeleteAdminClicks"/></html:link></td>
    </tr>
</agn:ShowByPermission>

</table>

</div>

<%
    my_map.put(timekey, file);
    /*
   System.out.println("#######################################");
   System.out.println(file);
   System.out.println("#######################################");
    */
    pageContext.getSession().setAttribute("map", my_map);

%>

</html:form>