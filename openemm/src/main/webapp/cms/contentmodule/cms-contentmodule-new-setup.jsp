<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<agn:CheckLogon/>

<agn:Permission token="cms.central_content_management"/>

<% request.setAttribute("sidemenu_active", "Mailings"); %>
<% request.setAttribute("sidemenu_sub_active", "ContentManagement"); %>
<% request.setAttribute("agnTitleKey", "ContentManagement"); %>
<% request.setAttribute("agnSubtitleKey", "cms.ContentModules"); %>
<% request.setAttribute("agnNavigationKey", "ContentModuleOverview"); %>
<% request.setAttribute("agnHighlightKey", "cms.NewContentModule"); %>
<% request.setAttribute("agnHelpKey", new String("cmContentModuleView")); %>