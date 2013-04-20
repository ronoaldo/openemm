<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.web.MailingBaseAction, org.agnitas.web.forms.MailingBaseForm" buffer="32kb" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<% request.setAttribute("sidemenu_sub_active", new String("default.Overview")); %>
<% request.setAttribute("agnHighlightKey", new String("default.Overview")); %>

<logic:equal name="mailingBaseForm" property="isTemplate" value="false">
<% request.setAttribute("sidemenu_active", new String("Mailings")); %>
<% request.setAttribute("agnNavigationKey", new String("MailingsOverview")); %>
<% request.setAttribute("agnTitleKey", new String("Mailings")); %>
<% request.setAttribute("agnSubtitleKey", new String("Mailings")); %>
<% request.setAttribute("agnHelpKey", new String("mailingList")); %>
<% request.setAttribute("ACTION_USED_ACTIONS", MailingBaseAction.ACTION_USED_ACTIONS ); %>
</logic:equal>

<logic:equal name="mailingBaseForm" property="isTemplate" value="true">
<% request.setAttribute("sidemenu_active", new String("Mailings")); %>
<% request.setAttribute("sidemenu_sub_active", new String("Templates")); %>
<% request.setAttribute("agnNavigationKey", new String("TemplatesOverview")); %>
<% request.setAttribute("agnTitleKey", new String("Templates")); %>
<% request.setAttribute("agnSubtitleKey", new String("Templates")); %>
<% request.setAttribute("agnHelpKey", new String("templateList")); %>
</logic:equal>

<% request.setAttribute("ACTION_VIEW", MailingBaseAction.ACTION_VIEW ); %>
<% request.setAttribute("ACTION_CONFIRM_DELETE", MailingBaseAction.ACTION_CONFIRM_DELETE ); %>
<% request.setAttribute("ACTION_LIST", MailingBaseAction.ACTION_LIST); %>

<%
   int isTemplate=0;
   if(((MailingBaseForm)session.getAttribute("mailingBaseForm")).isIsTemplate()) {
       isTemplate=1;
   }
%>

<%--<% if(isTemplate==0) { %>--%>
<%--<agn:Permission token="mailing.show"/>--%>
<%--<% } else { %>--%>
<%--<agn:Permission token="template.show"/>--%>
<%--<% } %>--%>
