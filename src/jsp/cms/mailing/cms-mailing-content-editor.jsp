<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.cms.web.CmsMailingContentAction" %>
<%@ page import="org.agnitas.web.MailingSendAction" %>
<%@ page import="org.agnitas.cms.web.forms.CmsMailingContentForm" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript" src="<%= request.getContextPath() %>/js/cms/frameresize.js"></script>

<% CmsMailingContentForm aForm = (CmsMailingContentForm) session.getAttribute("mailingContentForm");
    int mailingId = aForm.getMailingID();
%>

<div class="button_container cms_editor_container">
    <div class="action_button">
        <html:link page='<%= "/mailingcontent.do?action=" + CmsMailingContentAction.ACTION_SHOW_TEXT_VERSION + "&mailingID="+mailingId%>'>
            <span><bean:message key="mailing.Text_Version"/></span>
        </html:link>
    </div>
    <div class="action_button"><bean:message key="mailing.Content"/>:</div>
</div>

<html:form action="/mailingsend" styleClass="cms_editor_container">
    <input type="hidden" name="mailingID" value="<%= mailingId %>"/>
    <input type="hidden" name="action" value="<%= MailingSendAction.ACTION_PREVIEW_SELECT %>">

    <div class="blue_box_container">
        <div class="blue_box_top"></div>
        <div class="blue_box_content">
            <h2 class="blue_box_header"><bean:message key="mailing.Preview"/></h2>
                <label><bean:message key="recipient.Recipient"/>:</label>
                <html:select property="previewCustomerID">
                    <c:forEach var="recipient" items="${mailingContentForm.testRecipients}">
                        <html:option value="${recipient.key}">
                            ${recipient.value}
                        </html:option>
                    </c:forEach>
                </html:select>&nbsp;&nbsp;

                <label><bean:message key="action.Format"/>:</label>
                <html:select property="previewFormat" size="1">
                    <html:option value="0"><bean:message key="mailing.Text"/></html:option>
                    <logic:greaterThan name="mailingContentForm" property="mailFormat" value="0">
                        <html:option value="1"><bean:message key="mailing.HTML"/></html:option>
                    </logic:greaterThan>
                </html:select>&nbsp;&nbsp;

                <label><bean:message key="default.Size"/>:</label>
                <html:select property="previewSize" size="1">
                    <html:option value="4">640x480</html:option>
                    <html:option value="1">800x600</html:option>
                    <html:option value="2">1024x768</html:option>
                    <html:option value="3">1280x1024</html:option>
                </html:select>&nbsp;&nbsp;
            <div class="button_grey_box_container">
                <div class="action_button no_margin_right no_margin_bottom">
                    <a href="#" onclick="document.mailingSendForm.submit();"><span><bean:message
                            key="mailing.Preview"/></span></a>
                </div>
            </div>
        </div>
        <div class="blue_box_bottom cm_preview_panel"></div>
    </div>

</html:form>

<div class="export_wizard_content ">
    <iframe id="editorFrame" scrolling="no" marginwidth="0" marginheight="0"
            frameborder="0" vspace="0" hspace="0" width="889px" height="50px"
            style="background-color : #FFFFFF;"
            src="<html:rewrite page='<%= "/mailingcontent.do?action=" +
            CmsMailingContentAction.ACTION_CMS_EDITOR  +
            "&mailingID=" + mailingId%>'/>">
        "Your Browser does not support IFRAMEs, please
        update!
    </iframe>
</div>

<logic:equal value="false" name="mailingContentForm" property="worldMailingSend">
    <div class="button_container cmtemplate_assign_button">
        <input type="hidden" id="save" name="save" value=""/>
        <div class="action_button">
            <a href="#" onclick="document.getElementById('editorFrame').contentWindow.submitTheForm();"><span><bean:message key="button.Save"/></span></a>
        </div>
        <div class="action_button"><bean:message key="mailing.Content"/>:</div>
    </div>
</logic:equal>

<%-- Link to store CM-edit URL that will be appended with session id --%>
<div style="visibility:hidden; width:1px; height:1px;">
    <a href="<html:rewrite page="/cms_contentmodule.do?action=2&mailingId=${mailingContentForm.mailingID}&contentModuleId="/>"
       id="edit-CM-link">link-text</a>
</div>

<div style="visibility:hidden; width:1px; height:1px;">
    <a href="<html:rewrite page="/cms_contentmodule.do?action=9&mailingId=${mailingContentForm.mailingID}&contentModuleId="/>"
       id="new-CM-link">link-text</a>
</div>