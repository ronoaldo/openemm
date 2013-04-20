<%@ page language="java" import="org.agnitas.web.MailingSendForm" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<agn:Permission token="mailing.send.world"/>

<% MailingSendForm aForm=null;
    int tmpMailingID=0;
    String tmpShortname=new String("");
    if(request.getAttribute("mailingSendForm")!=null) {
        aForm=(MailingSendForm)request.getAttribute("mailingSendForm");
        tmpMailingID=aForm.getMailingID();
        tmpShortname=aForm.getShortname();
    }
%>

<% request.setAttribute("sidemenu_active", new String("Mailings")); %>
<% request.setAttribute("sidemenu_sub_active", new String("none")); %>
<% request.setAttribute("agnTitleKey", new String("Mailing")); %>
<% request.setAttribute("agnSubtitleKey", new String("Mailing")); %>
<% request.setAttribute("agnSubtitleValue", tmpShortname); %>
<% request.setAttribute("agnNavigationKey", new String("mailingView")); %>
<% request.setAttribute("agnHighlightKey", new String("mailing.Send_Mailing")); %>
<% request.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID)); %>
<% request.setAttribute("agnHelpKey", new String("mailingTestAndSend")); %>

