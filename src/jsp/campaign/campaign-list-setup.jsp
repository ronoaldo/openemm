<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<agn:Permission token="campaign.show"/> 

<% request.setAttribute("sidemenu_active", new String("Mailings")); %>
<% request.setAttribute("sidemenu_sub_active", new String("Campaigns")); %>

<% request.setAttribute("agnNavigationKey", new String("CampaignsOverview")); %>
<% request.setAttribute("agnHighlightKey", new String("default.Overview")); %>

<% request.setAttribute("agnTitleKey", new String("Campaigns")); %>
<% request.setAttribute("agnSubtitleKey", new String("Campaigns")); %>
<% request.setAttribute("agnHelpKey", new String("archiveView")); %>
