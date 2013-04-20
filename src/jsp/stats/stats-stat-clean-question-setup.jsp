<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.web.MailingStatForm " %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<% Integer tmpMailingID = 0;
    //int tmpTargetID=0;
    //int tmpUniqueClicks=0;
    String tmpShortname = new String("");
    MailingStatForm aForm = null;
    if (session.getAttribute("mailingStatForm") != null) {
        aForm = (MailingStatForm) session.getAttribute("mailingStatForm");
        tmpMailingID = aForm.getMailingID();
        //tmpTargetID=aForm.getTempTargetID();
        tmpShortname = aForm.getMailingShortname();
    }
    request.setAttribute("tmpMailingID", tmpMailingID);
%>

<% request.setAttribute("sidemenu_active", new String("Mailings")); %>
<% request.setAttribute("sidemenu_sub_active", new String("none")); %>
<% request.setAttribute("agnTitleKey", new String("Mailing")); %>
<% request.setAttribute("agnSubtitleKey", new String("Mailing")); %>
<% request.setAttribute("agnSubtitleValue", tmpShortname); %>
<% request.setAttribute("agnNavigationKey", new String("mailingView")); %>
<% request.setAttribute("agnHighlightKey", new String("Statistics")); %>
<% request.setAttribute("agnNavHrefAppend", new String("&mailingID=" + tmpMailingID)); %>
