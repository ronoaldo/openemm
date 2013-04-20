<%@ page language="java" import="org.agnitas.web.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<agn:Permission token="recipient.delete"/>

<% int tmpTargetID = 0;
   String tmpShortname = new String("");
   if(session.getAttribute("targetForm")!=null) {
      tmpTargetID = ((TargetForm)session.getAttribute("targetForm")).getTargetID();
      tmpShortname = ((TargetForm)session.getAttribute("targetForm")).getShortname();
      request.setAttribute("tmpNumOfRecipients", ((TargetForm)session.getAttribute("targetForm")).getNumOfRecipients()); 
   }
%>

<% request.setAttribute("sidemenu_active", new String("Targetgroups")); %>
<% request.setAttribute("sidemenu_sub_active", new String("none")); %>
<% request.setAttribute("agnTitleKey", new String("target.Target")); %>
<% request.setAttribute("agnSubtitleKey", new String("target.Target")); %>
<% request.setAttribute("agnSubtitleValue", new String(tmpShortname)); %>
<% request.setAttribute("agnNavigationKey", new String("targetView")); %>
<% request.setAttribute("agnHighlightKey", new String("target.NewTarget")); %>
<% request.setAttribute("agnNavHrefAppend", new String("&targetID="+tmpTargetID)); %>
<% request.setAttribute("agnNavigationKey", new String("targetView")); %>
<% request.setAttribute("ACTION_VIEW", TargetAction.ACTION_VIEW ); %>
<% request.setAttribute("ACTION_DELETE_RECIPIENTS", TargetAction.ACTION_DELETE_RECIPIENTS  ); %>
<% request.setAttribute("agnHelpKey", new String("targetGroupView")); %>