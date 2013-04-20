<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.cms.web.CMTemplateAction" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ include file="/WEB-INF/taglibs.jsp" %>

<agn:Permission token="cms.central_content_management"/>

<c:set var="sidemenu_active" value="Mailings" scope="request" />
<c:set var="sidemenu_sub_active" value="ContentManagement" scope="request" />
<c:set var="agnTitleKey" value="ContentManagement" scope="request" />
<c:set var="agnSubtitleKey" value="cms.CMTemplates" scope="request" />
<c:set var="agnSubtitleValue" value="${cmTemplateForm.name}" scope="request" />
<c:set var="agnNavigationKey" value="ContentManagementSub" scope="request" />
<c:set var="agnHighlightKey" value="cms.CMTemplates" scope="request" />
<c:set var="agnNavHrefAppend" value="&cmTemplateId=${cmTemplateForm.cmTemplateId}" scope="request" />
<c:set var="agnHelpKey" value="cmTemplateView" scope="request" />

<c:set var="ACTION_PURE_PREVIEW" value="<%= CMTemplateAction.ACTION_PURE_PREVIEW %>" scope="request" />
<c:set var="ACTION_CONFIRM_DELETE" value="<%= CMTemplateAction.ACTION_CONFIRM_DELETE %>" scope="request" />
<c:set var="ACTION_ASSIGN_LIST" value="<%= CMTemplateAction.ACTION_ASSIGN_LIST %>" scope="request" />
<c:set var="ACTION_EDIT_TEMPLATE" value="<%= CMTemplateAction.ACTION_EDIT_TEMPLATE%>" scope="request" />
<c:set var="ACTION_SAVE" value="<%= CMTemplateAction.ACTION_SAVE %>" scope="request" />

<c:set var="PREVIEW_URL" value="/cms_cmtemplate.do?action=${ACTION_PURE_PREVIEW}&cmTemplateId=${cmTemplateForm.cmTemplateId}" scope="request"/>
