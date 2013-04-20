<%@ page import="org.agnitas.cms.web.CmsMailingContentAction" %>
<%@ page import="org.agnitas.cms.web.forms.CmsMailingContentForm" %>
<%@ page import="org.agnitas.util.AgnUtils" %>
<%--
/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/
--%>
<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<% CmsMailingContentForm aForm = (CmsMailingContentForm) session
        .getAttribute("mailingContentForm");
    int mailingId = aForm.getMailingID();
    String mailingName = aForm.getShortname();
%>

<%
    pageContext.setAttribute("sidemenu_active", "Mailings");
    pageContext.setAttribute("sidemenu_sub_active", "none");
    pageContext.setAttribute("agnNavigationKey", "mailingView");
    pageContext.setAttribute("agnHighlightKey", "mailing.Content");
    pageContext.setAttribute("agnNavHrefAppend", "&mailingID=" + mailingId);
    pageContext.setAttribute("agnSubtitleValue", mailingName);
    pageContext.setAttribute("agnTitleKey", "Mailing");
    pageContext.setAttribute("agnSubtitleKey", "Mailing");
%>


<%@ include file="/header.jsp" %>

<html:link page='<%= "/mailingcontent.do?action=" + CmsMailingContentAction.ACTION_VIEW_CONTENT +"&mailingID="+mailingId%>'>
  <<&nbsp;<bean:message key="mailing.HTML_Version"/>&nbsp;
</html:link>
<br><br>

<html:form action="/mailingcontent">
    <html:hidden property="action"/>
    <html:hidden property="mailingID"/>
    <bean:message key="mailing.Text_Version"/>:<br>
    <html:textarea property="textVersion" rows="13" cols="60" />
    <br>
    <logic:equal value="false" name="mailingContentForm" property="worldMailingSend">
        <br>
    <html:image src="button?msg=button.Save" border="0" property="save" value="save"/>
    </logic:equal>
</html:form>

<%@include file="/footer.jsp" %>