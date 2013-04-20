<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.beans.Admin, org.agnitas.web.forms.IPStatForm,java.util.TimeZone" buffer="32kb" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<agn:Permission token="stats.ip"/>

<% request.setAttribute("sidemenu_active", new String("Statistics")); %>
<% request.setAttribute("sidemenu_sub_active", new String("statistic.IPStats")); %>
<% request.setAttribute("agnTitleKey", new String("statistic.IPStats")); %>
<% request.setAttribute("agnSubtitleKey", new String("Statistics")); %>
<% request.setAttribute("agnNavigationKey", new String("IPStats")); %>
<% request.setAttribute("agnHighlightKey", new String("statistic.IPStats")); %>
<% request.setAttribute("agnHelpKey", new String("ipStatistics")); %>


<% // key for the csv download
    java.util.TimeZone tz = TimeZone.getTimeZone(((Admin) session.getAttribute("emm.admin")).getAdminTimezone());
    java.util.GregorianCalendar aCal = new java.util.GregorianCalendar(tz);
    java.util.Date my_time = aCal.getTime();
    String Datum = my_time.toString();
    String timekey = Long.toString(my_time.getTime());
    java.util.Hashtable my_map = null;
    if (pageContext.getSession().getAttribute("map") == null) {
        my_map = new java.util.Hashtable();
        pageContext.getSession().setAttribute("map", my_map);
    } else {
        my_map = (java.util.Hashtable) (pageContext.getSession().getAttribute("map"));
    }
    String file = ((IPStatForm) (session.getAttribute("ipStatForm"))).getCsvfile();
    my_map.put(timekey, file);
    pageContext.getSession().setAttribute("map", my_map);
%>
