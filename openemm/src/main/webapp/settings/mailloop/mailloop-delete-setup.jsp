<%@ page language="java" import="org.agnitas.web.MailloopForm" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<agn:Permission token="mailing.show"/>

<% int tmpLoopID=0;
   String tmpShortname=new String("");
   MailloopForm aForm=null;
   if(request.getAttribute("mailloopForm")!=null) {
      aForm=(MailloopForm)request.getAttribute("mailloopForm");
      tmpLoopID=aForm.getMailloopID();
      tmpShortname=aForm.getShortname();
   }
   request.setAttribute("tmpShortname", tmpShortname);
   request.setAttribute("tmpLoopID", tmpLoopID);
%>

<% request.setAttribute("sidemenu_active", new String("Settings")); %>
<% request.setAttribute("sidemenu_sub_active", new String("settings.Mailloops"));  %>
<% request.setAttribute("agnNavigationKey", new String("Mailloops")); %>
<% request.setAttribute("agnHighlightKey", new String("settings.NewMailloop")); %>
<% request.setAttribute("agnTitleKey", new String("settings.Mailloop")); %>
<% request.setAttribute("agnSubtitleKey", new String("settings.Mailloop")); %>
<% request.setAttribute("agnSubtitleValue", tmpShortname); %>
<% request.setAttribute("agnNavHrefAppend", new String("&mailloopID="+tmpLoopID)); %>
<% request.setAttribute("agnHelpKey", new String("bounceFilter")); %>