<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.AgnUtils, org.agnitas.web.MailloopForm, java.util.Locale" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<agn:Permission token="mailing.show"/>

<% int tmpLoopID=0;
   String tmpShortname=new String("");

   if(request.getAttribute("mailloopForm")!=null) {
      tmpLoopID=((MailloopForm)request.getAttribute("mailloopForm")).getMailloopID();
      tmpShortname=((MailloopForm)request.getAttribute("mailloopForm")).getShortname();
   }
   request.setAttribute("tmpLoopID", tmpLoopID);
%>

<% request.setAttribute("sidemenu_active", new String("Administration")); %>


<% if(tmpLoopID!=0) { %>

<% request.setAttribute("agnNavigationKey", new String("MailloopsEdit")); %>
<% request.setAttribute("agnHighlightKey", new String("settings.EditMailloop")); %>
<% request.setAttribute("agnTitleKey", new String("settings.Mailloop")); %>
<% request.setAttribute("agnSubtitleKey", new String("settings.Mailloop")); %>
<% request.setAttribute("agnSubtitleValue", tmpShortname); %>

<% } else { %>
<% request.setAttribute("agnNavigationKey", new String("Mailloops")); %>
<% request.setAttribute("agnHighlightKey", new String("settings.NewMailloop")); %>
<% request.setAttribute("agnTitleKey", new String("settings.NewMailloop")); %>
<% request.setAttribute("agnSubtitleKey", new String("settings.NewMailloop")); %>

<% } %>

<% request.setAttribute("sidemenu_sub_active", new String("settings.Mailloops"));  %>
<% request.setAttribute("agnNavHrefAppend", new String("&mailloopID="+tmpLoopID)); %>
<% request.setAttribute("agnHelpKey", new String("bounceFilter")); %>