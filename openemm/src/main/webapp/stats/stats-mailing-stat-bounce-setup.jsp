<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.util.AgnUtils, org.agnitas.util.EmmCalendar, org.agnitas.web.MailingStatForm" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<agn:Permission token="stats.rdir"/>

<% Integer tmpMailingID = 0;
    // int tmpUniqueClicks=0;
    String tmpShortname = new String("");
    MailingStatForm aForm = null;
    if (session.getAttribute("mailingStatForm") != null) {
        aForm = (MailingStatForm) session.getAttribute("mailingStatForm");
        tmpMailingID = aForm.getMailingID();
        tmpShortname = aForm.getMailingShortname();
    }
    request.setAttribute("tmpMailingID", tmpMailingID);
    request.setAttribute("values", aForm.getValues());

%>

<% request.setAttribute("sidemenu_active", new String("Mailings")); %>
<% request.setAttribute("sidemenu_sub_active", new String("none")); %>
<% request.setAttribute("agnTitleKey", new String("Mailing")); %>
<% request.setAttribute("agnSubtitleKey", new String("Mailing")); %>
<% request.setAttribute("agnSubtitleValue", tmpShortname); %>
<% request.setAttribute("agnNavigationKey", new String("mailingView")); %>
<% request.setAttribute("agnHighlightKey", new String("Statistics")); %>
<% request.setAttribute("agnNavHrefAppend", new String("&mailingID=" + tmpMailingID)); %>
<% request.setAttribute("agnHelpKey", new String("feedbackAnalysis")); %>


<%
    EmmCalendar my_calendar = new EmmCalendar(java.util.TimeZone.getDefault());
    my_calendar.changeTimeWithZone(AgnUtils.getTimeZone(request));
    java.util.Date my_time = my_calendar.getTime();
    String Datum = my_time.toString();
    String timekey = Long.toString(my_time.getTime());
    request.setAttribute("time_key", timekey);

%>
