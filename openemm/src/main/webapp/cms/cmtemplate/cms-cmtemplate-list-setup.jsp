<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.cms.web.CMTemplateAction" %>
<%@ page import="org.agnitas.cms.web.forms.CMTemplateForm" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<agn:Permission token="cms.central_content_management"/>

<% request.setAttribute("sidemenu_active", "Mailings"); %>
<% request.setAttribute("sidemenu_sub_active", "ContentManagement"); %>
<% request.setAttribute("agnTitleKey", "ContentManagement"); %>
<% request.setAttribute("agnSubtitleKey", "cms.CMTemplates"); %>
<% request.setAttribute("agnNavigationKey", "ContentManagementSub"); %>
<% request.setAttribute("agnHighlightKey", "cms.CMTemplates"); %>
<% request.setAttribute("agnHelpKey", new String("cmTemplateList")); %>

<% request.setAttribute("ACTION_LIST", CMTemplateAction.ACTION_LIST); %>
<% request.setAttribute("ACTION_VIEW", CMTemplateAction.ACTION_VIEW); %>
<% request.setAttribute("ACTION_CONFIRM_DELETE", CMTemplateAction.ACTION_CONFIRM_DELETE); %>
<% request.setAttribute("ACTION_PURE_PREVIEW", CMTemplateAction.ACTION_PURE_PREVIEW); %>
<% request.setAttribute("PREVIEW_WIDTH", CMTemplateAction.LIST_PREVIEW_WIDTH); %>
<% request.setAttribute("PREVIEW_HEIGHT", CMTemplateAction.LIST_PREVIEW_HEIGHT); %>
<% request.setAttribute("PREVIEW_MODE_COLUMN", CMTemplateForm.PREVIEW_MODE_COLUMN); %>
<% request.setAttribute("PREVIEW_MODE_POPUP", CMTemplateForm.PREVIEW_MODE_POPUP); %>

<script type="text/javascript" src="<%= request.getContextPath() %>/js/cms/toggleElem.js"></script>