<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>

<agn:CheckLogon/>

<agn:Permission token="campaign.show"/> 

<c:choose>
	<c:when test="${campaignForm.campaignID != 0}">
		<c:set var="agnSubtitleKey" value="campaign.Campaign" scope="request" />
		<c:set var="agnSubtitleValue" value="${campaignForm.shortname}" scope="request" />
		<c:set var="agnNavigationKey" value="Campaign" scope="request" />
		<c:set var="agnHighlightKey" value="campaign.Edit" scope="request" />
		<c:set var="agnHelpKey" value="archiveView" scope="request" />
	</c:when>
	<c:otherwise>
 		<c:set var="agnSubtitleKey" value="campaign.NewCampaign" scope="request" />
		<c:set var="agnNavigationKey" value="CampaignNew" scope="request" />
		<c:set var="agnHighlightKey" value="campaign.NewCampaign" scope="request" />
        <c:set var="agnHelpKey" value="newArchive" scope="request" />
	</c:otherwise>
</c:choose>
<c:set var="sidemenu_sub_active" value="Campaigns" scope="request" />

<c:set var="sidemenu_active" value="Mailings" scope="request" />
<c:set var="agnTitleKey" value="Campaigns" scope="request" />
<c:set var="agnNavHrefAppend" value="&campaignID=${campaignForm.campaignID}" scope="request" />