<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.target.*, org.agnitas.stat.*, java.util.*, org.agnitas.web.*, org.agnitas.beans.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<agn:Permission token="stats.rdir"/>

<% int tmpMailingID=0;
    MailingStatForm aForm = null;
    String tmpShortname = null;
    if(session.getAttribute("mailingStatForm") != null) {
        aForm=(MailingStatForm)session.getAttribute("mailingStatForm");
        tmpMailingID=aForm.getMailingID();
        tmpShortname=aForm.getMailingShortname();
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
<% request.setAttribute("agnHelpKey", new String("mailingStatistic")); %>