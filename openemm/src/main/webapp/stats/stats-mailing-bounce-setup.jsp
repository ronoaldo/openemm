<%@ page language="java"
         import="org.agnitas.beans.Admin, org.agnitas.web.MailingStatForm, java.util.GregorianCalendar, java.util.TimeZone"
         contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<% request.setAttribute("sidemenu_active", new String("Statistics")); %>
<% request.setAttribute("sidemenu_sub_active", new String("Mailing")); %>
<% request.setAttribute("agnTitleKey", new String("Mailing")); %>
<% request.setAttribute("agnSubtitleKey", new String("Statistics")); %>
<% request.setAttribute("agnNavigationKey", new String("mailingView")); %>
<% request.setAttribute("agnHighlightKey", new String("Statistics")); %>
<% request.setAttribute("agnHelpKey", new String("feedbackAnalysis")); %>


<%
    // key for the csv download
    TimeZone tz = TimeZone.getTimeZone(((Admin) session.getAttribute("emm.admin")).getAdminTimezone());
    GregorianCalendar aCal = new GregorianCalendar(tz);
    java.util.Date my_time = aCal.getTime();
    String timekey = Long.toString(my_time.getTime());
    request.setAttribute("timekey", timekey);

    MailingStatForm aForm = null;
    Integer tmpMailingID = 0;
    String shortname = "";
    if (session.getAttribute("mailingStatForm") != null) {
        aForm = (MailingStatForm) session.getAttribute("mailingStatForm");
        tmpMailingID = aForm.getMailingID();
        shortname = aForm.getMailingShortname();
    }

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
    request.setAttribute("tmpMailingID", tmpMailingID);
    request.setAttribute("shortname", shortname);
    pageContext.getSession().setAttribute("map", my_map);
%>
