<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.web.TrackableLinkForm" %>
<%@ page import="org.agnitas.web.TrackableLinkAction" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="mailing.content.show"/>

<% int tmpMailingID=0;
    String tmpShortname=new String("");
    TrackableLinkForm aForm=null;
    if(request.getAttribute("trackableLinkForm")!=null) {
        aForm=(TrackableLinkForm)request.getAttribute("trackableLinkForm");
        tmpMailingID=aForm.getMailingID();
        tmpShortname=aForm.getShortname();
    }
    request.setAttribute("tmpMailingID", tmpMailingID);
%>


<logic:equal name="trackableLinkForm" property="isTemplate" value="true">
    <% // template navigation:
        request.setAttribute("sidemenu_active", new String("Mailings"));
        request.setAttribute("sidemenu_sub_active", new String("none"));
        request.setAttribute("agnNavigationKey", new String("templateView"));
        request.setAttribute("agnHighlightKey", new String("mailing.Trackable_Links"));
        request.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
        request.setAttribute("agnTitleKey", new String("Template"));
        request.setAttribute("agnSubtitleKey", new String("Template"));
        request.setAttribute("agnSubtitleValue", tmpShortname); %>
    </logic:equal>

<logic:equal name="trackableLinkForm" property="isTemplate" value="false">
    <% // mailing navigation:
        request.setAttribute("sidemenu_active", new String("Mailings"));
        request.setAttribute("sidemenu_sub_active", new String("none"));
        request.setAttribute("agnNavigationKey", new String("mailingView"));
        request.setAttribute("agnHighlightKey", new String("mailing.Trackable_Links"));
        request.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
        request.setAttribute("agnTitleKey", new String("Mailing"));
        request.setAttribute("agnSubtitleKey", new String("Mailing"));
        request.setAttribute("agnSubtitleValue", tmpShortname);
    %>
</logic:equal>
<% request.setAttribute("agnHelpKey", new String("trackableLinks")); %>
<%
      request.setAttribute("ACTION_GLOBAL_USAGE", TrackableLinkAction.ACTION_GLOBAL_USAGE);
      request.setAttribute("ACTION_SET_STANDARD_ACTION", TrackableLinkAction.ACTION_SET_STANDARD_ACTION);
      request.setAttribute("ACTION_VIEW", TrackableLinkAction.ACTION_VIEW);
%>