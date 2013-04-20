<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.agnitas.web.MailingSendAction"%>
<%@ page import="org.agnitas.ecs.web.forms.EcsMailingStatForm" %>

<% request.setAttribute("__TEMPLATE__", MailingSendAction.TEMPLATE); %>
<% request.setAttribute("__FROM__", MailingSendAction.FROM); %>
<% request.setAttribute("__SUBJECT__", MailingSendAction.SUBJECT); %>


<% int tmpMailingID=0;
   String tmpShortname=new String("");
    EcsMailingStatForm aForm = (EcsMailingStatForm) session.getAttribute("ecsForm");

   if(aForm != null) {
	  tmpMailingID = aForm.getMailingId();
      tmpShortname = aForm.getShortname();
   }
%>

<% request.setAttribute("sidemenu_active", "Mailings"); %>
<% request.setAttribute("sidemenu_sub_active", "none"); %>
<% request.setAttribute("agnTitleKey", "Mailing"); %>
<% request.setAttribute("agnSubtitleKey", "Mailing"); %>
<% request.setAttribute("agnSubtitleValue", tmpShortname); %>
<% request.setAttribute("agnNavigationKey", "mailingView"); %>
<% request.setAttribute("agnHighlightKey", "Statistics"); %>
<% request.setAttribute("agnNavHrefAppend", "&mailingID=" + tmpMailingID); %>
<% request.setAttribute("agnHelpKey", new String("heatmap")); %>