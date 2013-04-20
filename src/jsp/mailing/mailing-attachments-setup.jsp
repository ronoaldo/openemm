<%@ page language="java" import="org.agnitas.web.forms.MailingAttachmentsForm" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>
<agn:Permission token="mailing.attachments.show"/>

<%
    int tmpMailingID=0;
    String tmpShortname=new String("");
    MailingAttachmentsForm aForm=(MailingAttachmentsForm)request.getAttribute("mailingAttachmentsForm");
    if(request.getAttribute("mailingAttachmentsForm")!=null) {
        tmpMailingID=aForm.getMailingID();
        tmpShortname=aForm.getShortname();
    }
    request.setAttribute("tmpMailingID", tmpMailingID);
    request.setAttribute("isWorldMailingSend", aForm.isWorldMailingSend());
%>

<logic:equal name="mailingAttachmentsForm" property="isTemplate" value="true">
    <% // template navigation:
        request.setAttribute("sidemenu_active", new String("Mailings"));
        request.setAttribute("sidemenu_sub_active", new String("none"));
        request.setAttribute("agnNavigationKey", new String("templateView"));
        request.setAttribute("agnHighlightKey", new String("mailing.Attachments"));
        request.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
        request.setAttribute("agnTitleKey", new String("Template"));
        request.setAttribute("agnSubtitleKey", new String("Template"));
        request.setAttribute("agnSubtitleValue", tmpShortname);
        request.setAttribute("agnHelpKey","attachments");
    %>
</logic:equal>

<logic:equal name="mailingAttachmentsForm" property="isTemplate" value="false">
    <%
        // mailing navigation:
        request.setAttribute("sidemenu_active", new String("Mailings"));
        request.setAttribute("sidemenu_sub_active", new String("none"));
        request.setAttribute("agnNavigationKey", new String("mailingView"));
        request.setAttribute("agnHighlightKey", new String("mailing.Attachments"));
        request.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
        request.setAttribute("agnTitleKey", new String("Mailing"));
        request.setAttribute("agnSubtitleKey", new String("Mailing"));
        request.setAttribute("agnSubtitleValue", tmpShortname);
        request.setAttribute("agnHelpKey","attachments");
    %>
</logic:equal>