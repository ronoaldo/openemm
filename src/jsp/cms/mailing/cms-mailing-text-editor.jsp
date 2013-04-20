<%@ page import="org.agnitas.cms.web.CmsMailingContentAction" %>
<%@ page import="org.agnitas.cms.web.forms.CmsMailingContentForm" %>
<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<% CmsMailingContentForm aForm = (CmsMailingContentForm) session
        .getAttribute("mailingContentForm");
    int mailingId = aForm.getMailingID();
%>

<div class="button_container">
    <div class="action_button">
        <html:link
                page='<%= "/mailingcontent.do?action=" + CmsMailingContentAction.ACTION_VIEW_CONTENT +"&mailingID="+mailingId%>'>
            <span><bean:message key="mailing.HTML_Version"/></span>
        </html:link>
    </div>
    <div class="action_button"><bean:message key="mailing.Content"/>:</div>
</div>

<html:form action="/mailingcontent">
    <html:hidden property="action"/>
    <html:hidden property="mailingID"/>

    <div class="export_wizard_content ">
        <div class="cm_preview_panel"><bean:message key="mailing.Text_Version"/>:</div>
        <html:textarea property="textVersion" rows="13" cols="60"/>
    </div>

    <logic:equal value="false" name="mailingContentForm" property="worldMailingSend">
        <div class="button_container">
            <div class="action_button">
                <input type="hidden" id="save" name="save" value=""/>

                <a href="#"
                   onclick="document.getElementById('save').value='true'; document.mailingContentForm.submit(); return false;"><span><bean:message
                        key="button.Save"/></span></a>
            </div>
            <div class="action_button"><bean:message key="mailing.Content"/>:</div>
        </div>
    </logic:equal>

</html:form>