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
 * the code written by AGNITAS AG are Copyright (c) 2009 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/
 --%>
<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.cms.web.CmsMailingContentAction, org.agnitas.cms.web.forms.CmsMailingContentForm" %>
<%@ page import="org.agnitas.util.AgnUtils" %>
<%@ page import="org.agnitas.web.MailingSendAction" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<agn:CheckLogon/>

<agn:Permission token="cms.mailing_content_management"/>

<script type="text/javascript" src="<%= request.getContextPath() %>/js/cms/frameresize.js"></script>

<% CmsMailingContentForm aForm = (CmsMailingContentForm) session.getAttribute("mailingContentForm");
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

<%@include file="/header.jsp" %>
<%@include file="/messages.jsp" %>

<html:link
        page='<%= "/mailingcontent.do?action=" + CmsMailingContentAction.ACTION_SHOW_TEXT_VERSION + "&mailingID="+mailingId%>'>
    <bean:message key="mailing.Text_Version"/>&nbsp;>>
</html:link>
<br><br>

<html:form action="/mailingsend">
    <input type="hidden" name="mailingID" value="<%= mailingId %>"/>
    <input type="hidden" name="action" value="<%= MailingSendAction.ACTION_PREVIEW_SELECT %>">
    <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td valign="middle">
                <bean:message key="recipient.Recipient"/>:&nbsp;
            </td>
            <td valign="middle">
                <html:select property="previewCustomerID">
                    <c:forEach var="recipient" items="${mailingContentForm.testRecipients}">
                        <html:option value="${recipient.key}">
                            ${recipient.value}
                        </html:option>
                    </c:forEach>
                </html:select>
                &nbsp;&nbsp;
            </td>
            <td valign="middle">
                <bean:message key="action.Format"/>:&nbsp;
            </td>
            <td valign="middle">
                <html:select property="previewFormat" size="1">
                    <html:option value="0"><bean:message key="mailing.Text"/></html:option>
                    <logic:greaterThan name="mailingContentForm" property="mailFormat" value="0">
                        <html:option value="1"><bean:message key="mailing.HTML"/></html:option>
                    </logic:greaterThan>
                </html:select>
                &nbsp;&nbsp;
            </td>
            <td valign="middle">
                <bean:message key="default.Size"/>:&nbsp;
            </td>
            <td valign="middle">
                <html:select property="previewSize" size="1">
                    <html:option value="4">640x480</html:option>
                    <html:option value="1">800x600</html:option>
                    <html:option value="2">1024x768</html:option>
                    <html:option value="3">1280x1024</html:option>
                </html:select>
                &nbsp;&nbsp;
            </td>
            <td valign="middle">
                <html:image src="button?msg=mailing.Preview" border="0"/>
            </td>
        </tr>
    </table>
</html:form>

<hr size="1">

<iframe id="editorFrame" scrolling="no" marginwidth="0" marginheight="0"
        frameborder="0" vspace="0" hspace="0" width="100%"
        style="display: none; background-color : #FFFFFF;"
        src="<html:rewrite page='<%= "/mailingcontent.do?action=" +
        CmsMailingContentAction.ACTION_CMS_EDITOR  +
        "&mailingID=" + mailingId%>'/>">
    "Your Browser does not support IFRAMEs, please
    update!
</iframe>

<%-- Link to store CM-edit URL that will be appended with session id --%>
<div style="visibility:hidden; width:1px; height:1px;">
    <a href="<html:rewrite page="/cms_contentmodule.do?action=2&mailingId=${mailingContentForm.mailingID}&contentModuleId="/>" id="edit-CM-link">link-text</a>
</div>

<div style="visibility:hidden; width:1px; height:1px;">
    <a href="<html:rewrite page="/cms_contentmodule.do?action=9&mailingId=${mailingContentForm.mailingID}&contentModuleId="/>" id="new-CM-link">link-text</a>
</div>

<%@include file="/footer.jsp" %>
