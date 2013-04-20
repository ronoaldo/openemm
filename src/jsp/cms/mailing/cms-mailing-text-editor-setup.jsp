<%@ page import="org.agnitas.cms.web.forms.CmsMailingContentForm" %>
<%@ page language="java" contentType="text/html; charset=utf-8" %>

<%
    CmsMailingContentForm aForm = (CmsMailingContentForm) session
            .getAttribute("mailingContentForm");
    request.setAttribute("sidemenu_active", "Mailings");
    request.setAttribute("sidemenu_sub_active", "none");
    request.setAttribute("agnNavigationKey", "mailingView");
    request.setAttribute("agnHighlightKey", "mailing.Content");
    request.setAttribute("agnNavHrefAppend", "&mailingID=" + aForm.getMailingID());
    request.setAttribute("agnSubtitleValue", aForm.getShortname());
    request.setAttribute("agnTitleKey", "Mailing");
    request.setAttribute("agnSubtitleKey", "Mailing");
%>
