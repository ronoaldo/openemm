<%--checked --%>
<%@ page language="java"
         import="org.agnitas.beans.MailingComponent, org.agnitas.dao.TargetDao, org.agnitas.target.Target, org.agnitas.util.AgnUtils, org.springframework.context.ApplicationContext, org.springframework.web.context.support.WebApplicationContextUtils"
         contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.web.forms.MailingAttachmentsForm" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    MailingAttachmentsForm aForm = null;
    if (request.getAttribute("mailingAttachmentsForm") != null) {
        aForm = (MailingAttachmentsForm) request.getAttribute("mailingAttachmentsForm");
    }
%>

<html:form action="/mailingattachments" enctype="multipart/form-data">
    <html:hidden property="mailingID"/>
    <html:hidden property="action"/>
    <div id="filterbox_container_for_name">
        <div class="filterbox_form_container">
            <div id="filterbox_top"></div>
            <div id="searchbox_content" class="filterbox_form_container">
                <div id="filterbox_small_label" class="filterbox_small_label_tab">
                ${mailingAttachmentsForm.shortname}&nbsp;&nbsp;<%if (aForm != null &&
                aForm.getDescription() != null && !aForm.getDescription().isEmpty()) {%>
                    |&nbsp;&nbsp;${mailingAttachmentsForm.description}<% } %>
                </div>
            </div>
            <div id="filterbox_bottom"></div>
        </div>
    </div>

    <div class="grey_box_container">
        <div class="grey_box_top"></div>
        <div class="grey_box_content">
            <div class="assistant_step7_form_item">
                <label><bean:message key="mailing.New_Attachment"/>:</label>
            </div>
            <div class="assistant_step7_form_item">
                <label><bean:message key="mailing.Attachment"/>:&nbsp;</label>
                <html:file property="newAttachment" styleId="newAttachment" onchange="getFilename()"/>
            </div>
            <div class="assistant_step7_form_item">
                <label><bean:message key="mailing.attachment.name"/>:&nbsp;</label>
                <html:text property="newAttachmentName" styleId="newAttachmentName"/>
            </div>
            <div class="assistant_step7_form_item">
                <label><bean:message key="target.Target"/>:&nbsp;</label>
                <html:select property="attachmentTargetID" size="1" styleId="attachmentTargetID">
                    <html:option value="0"><bean:message key="statistic.All_Subscribers"/></html:option>
                        <logic:iterate id="target" name="targets" scope="request">
                            <html:option value="${target.id}">
                                ${target.targetName}
                            </html:option>
                        </logic:iterate>
                </html:select>
            </div>
            <logic:equal name="mailingAttachmentsForm" property="worldMailingSend" value="false">
                <div class="action_button no_margin_right no_margin_bottom">
                    <input type="hidden" name="add" value=""/>
                    <a href="#"
                       onclick="document.mailingAttachmentsForm.add.value='add'; document.mailingAttachmentsForm.submit(); clearAttachment(); return false;"><span><bean:message
                            key="button.Add"/></span></a>
                </div>
           </logic:equal>

        </div>
        <div class="grey_box_bottom"></div>
    </div>

    <% MailingComponent comp; %>

    <c:set var="index" value="1" scope="request"/>
    <logic:iterate id="attachment" name="attachments" scope="request">
        <% comp = (MailingComponent) pageContext.getAttribute("attachment"); %>
        <div class="grey_box_container">
            <div class="grey_box_top"></div>
            <div class="grey_box_content">
                <div class="assistant_step7_form_item">
                    <bean:message key="mailing.Attachment"/>:&nbsp;
                    <html:link styleClass="usual_link" page="/sc?compID=${attachment.id}">${attachment.componentName}&nbsp;&nbsp;
                        <img src="${emmLayoutBase.imagesURL}/download.gif" border="0" alt="<bean:message key='button.Download'/>">
                    </html:link> <br><br>
                    <input type="hidden" name="compid${index}" value="${attachment.id}">

                    <label for="target${attachment.id}">
                        <bean:message key="target.Target"/>:&nbsp;
                    </label>
                    <html:select property="target${attachment.id}" size="1"
                                 value="${attachment.targetID}" styleId="target${attachment.id}">
                        <html:option value="0"><bean:message key="statistic.All_Subscribers"/></html:option>
                        <logic:iterate id="target" name="targets" scope="request">
                            <html:option value="${target.id}">
                                ${target.targetName}
                            </html:option>
                        </logic:iterate>
                    </html:select>
                    &nbsp;<br>

                    &nbsp;<br>
                    <bean:message key="mailing.Mime_Type"/>:&nbsp;${attachment.mimeType}&nbsp;<br>
                    <bean:message key="mailing.Original_Size"/>:&nbsp;<%= comp.getBinaryBlock().length / 1024 %>
                    &nbsp;<bean:message key="default.KByte"/><br>
                    <bean:message key="default.Size_Mail"/>:&nbsp;<%= comp.getBinaryBlock().length / 1024 *4/3 %>
                    &nbsp;<bean:message key="default.KByte"/><br><br>
                </div>
                <logic:equal name="mailingAttachmentsForm" property="worldMailingSend" value="false">
                    <div class="action_button no_margin_right no_margin_bottom">
                            <input type="hidden" name="delete${attachment.id}" value=""/>
                            <a href="#"
                               onclick="document.mailingAttachmentsForm.delete${attachment.id}.value='delete'; document.mailingAttachmentsForm.submit(); return false;"><span>
                                <bean:message key="button.Delete"/></span></a>
                    </div>
                </logic:equal>
            </div>
            <div class="grey_box_bottom"></div>
        </div>
        <c:set var="index" value="${index + 1}" scope="request"/>
    </logic:iterate>


    <logic:equal name="mailingAttachmentsForm" property="worldMailingSend" value="false">
        <div class="button_container">
            <input type="hidden" name="save" value=""/>
            <div class="action_button">
                <a href="#"
                   onclick="document.mailingAttachmentsForm.save.value='save'; document.mailingAttachmentsForm.submit(); clearAttachment(); return false;"><span><bean:message
                        key="button.Save"/></span></a>
            </div>
            <div class="action_button"><bean:message key="mailing.Attachments"/>:</div>
        </div>
    </logic:equal>


</html:form>
<script language="JavaScript">
    Event.observe(window, 'load', function() {
        document.getElementById("newAttachmentName").value = "";
        document.getElementById("attachmentTargetID").selectedIndex = 0;
    });

    function getFilename() {
        document.getElementById("newAttachmentName").value = document.getElementById("newAttachment").value.match(/[^\\\/]+$/);
    }

    function clearAttachment() {
        document.getElementById("newAttachmentName").value = "";
        document.getElementById("attachmentTargetID").selectedIndex = 0;
    }

</script>