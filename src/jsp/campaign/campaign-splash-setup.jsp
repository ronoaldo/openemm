<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.web.CampaignAction, org.agnitas.web.forms.CampaignForm" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<agn:Permission token="campaign.show"/>

<%
    int tmpCampaignID = 0;
    String tmpShortname = new String("");

    if(session.getAttribute("campaignForm") != null) {
        CampaignForm campaignForm = (CampaignForm) session.getAttribute("campaignForm");
        tmpCampaignID = campaignForm.getCampaignID();
        tmpShortname = campaignForm.getShortname();
        campaignForm.setAction(CampaignAction.ACTION_STAT);
    }

    request.setAttribute("agnSubtitleKey", new String("campaign.Campaign"));
    request.setAttribute("agnSubtitleValue", tmpShortname);
    request.setAttribute("agnNavigationKey", new String("Campaign"));
    request.setAttribute("agnHighlightKey", new String("campaign.Campaign"));
    request.setAttribute("sidemenu_sub_active", new String("Campaigns"));
    request.setAttribute("sidemenu_active", new String("Mailings"));
    request.setAttribute("agnTitleKey", new String("Campaigns"));
    request.setAttribute("agnNavHrefAppend", new String("&campaignID=" + tmpCampaignID));
%>

