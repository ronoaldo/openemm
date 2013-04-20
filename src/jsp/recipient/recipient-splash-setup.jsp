<%@ page language="java" import=" org.agnitas.web.*" contentType="text/html; charset=utf-8" buffer="256kb" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<agn:CheckLogon/>

<agn:Permission token="stats.rdir"/>

<% 
   int tmpMailingID=0;
   int tmpTargetID=0;
   String tmpShortname=new String("");
   MailingStatForm aForm=null;
   int maxblue = 0;
   int maxSubscribers = 0;

   if(session.getAttribute("mailingStatForm")!=null) {
      aForm=(MailingStatForm)session.getAttribute("mailingStatForm");
      tmpMailingID=aForm.getMailingID();
      tmpTargetID=aForm.getTargetID();
      tmpShortname=aForm.getMailingShortname();
      maxblue=aForm.getMaxblue();
      maxSubscribers=aForm.getMaxSubscribers();
   }
%>

<% request.setAttribute("sidemenu_active", new String("Mailings")); %>
<% request.setAttribute("sidemenu_sub_active", new String("none")); %>
<% request.setAttribute("agnTitleKey", new String("Mailing")); %>
<% request.setAttribute("agnSubtitleKey", new String("Mailing")); %>
<% request.setAttribute("agnSubtitleValue", tmpShortname); %>
<% request.setAttribute("agnNavigationKey", new String("mailingView")); %>
<% request.setAttribute("agnHighlightKey", new String("Statistics")); %>
<% request.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID)); %>

<% request.setAttribute("agnRefresh", new String("2")); %>
