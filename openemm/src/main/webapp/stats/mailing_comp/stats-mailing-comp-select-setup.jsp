<%@ page import="org.agnitas.web.MailingStatAction" %>
<%@ page language="java" contentType="text/html; charset=utf-8" buffer="32kb" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<agn:Permission token="stats.mailing"/>

<% request.setAttribute("sidemenu_active", new String("Statistics")); %>
<% request.setAttribute("sidemenu_sub_active", new String("statistic.comparison")); %>
<% request.setAttribute("agnTitleKey", new String("statistic.comparison")); %>
<% request.setAttribute("agnSubtitleKey", new String("Statistics")); %>
<% request.setAttribute("agnNavigationKey", new String("statsCompare")); %>
<% request.setAttribute("agnHighlightKey", new String("statistic.comparison")); %>
<% request.setAttribute("agnHelpKey", new String("compareMailings")); %>

<% request.setAttribute("ACTION_MAILINGSTAT", MailingStatAction.ACTION_MAILINGSTAT); %>