<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<agn:Permission token="stats.ip"/>

<% request.setAttribute("sidemenu_active", new String("Statistics")); %>
<% request.setAttribute("sidemenu_sub_active", new String("statistic.IPStats")); %>


<% request.setAttribute("agnTitleKey", new String("statistic.IPStats")); %>
<% request.setAttribute("agnSubtitleKey", new String("Statistics")); %>

<% request.setAttribute("agnNavigationKey", new String("IPStats")); %>
<% request.setAttribute("agnHighlightKey", new String("statistic.IPStats")); %>
<% request.setAttribute("agnRefresh", new String("2")); %>
<% request.setAttribute("agnHelpKey", new String("feedbackAnalysis")); %>
