<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<agn:CheckLogon/>
<agn:Permission token="campaign.delete"/>

<c:set var="agnSubtitleKey" value="campaign.Campaign" scope="request" />
<c:set var="agnSubtitleValue" value="${campaignForm.shortname}" scope="request" />
<c:set var="agnNavigationKey" value="campaignDelete" scope="request" />
<c:set var="agnHighlightKey" value="campaign.Campaign" scope="request" />
<c:set var="sidemenu_sub_active" value="Campaigns" scope="request" />
<c:set var="sidemenu_active" value="Mailings" scope="request" />
<c:set var="agnTitleKey" value="Campaigns" scope="request" />
<c:set var="agnNavHrefAppend" value="&campaignID=${campaignForm.campaignID}" scope="request" />
<c:set var="agnHelpKey" value="archiveView" scope="request" />