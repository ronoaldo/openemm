<%@ page language="java" contentType="text/html; charset=utf-8" buffer="32kb" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<agn:Permission token="stats.domains"/>

<% request.setAttribute("sidemenu_active", new String("Statistics")); %>
<% request.setAttribute("sidemenu_sub_active", new String("statistic.domains")); %>
<% request.setAttribute("agnTitleKey", new String("statistic.domains")); %>
<% request.setAttribute("agnSubtitleKey", new String("Statistics")); %>
<% request.setAttribute("agnNavigationKey", new String("statsDomain")); %>
<% request.setAttribute("agnHighlightKey", new String("statistic.domains")); %>
<% request.setAttribute("agnHelpKey", new String("domainStatistic")); %>