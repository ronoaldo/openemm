<%--checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*, java.util.*,java.lang.Integer" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    int tmpMailingID = 0;
    MailingContentForm aForm = null;
    if (session.getAttribute("mailingContentForm") != null) {
        aForm = (MailingContentForm) session.getAttribute("mailingContentForm");
        tmpMailingID = aForm.getMailingID();
    }
%>


<html:form action="/mailingsend">
    <input type="hidden" name="mailingID" value="<%= tmpMailingID %>"/>
    <input type="hidden" name="action" value="<%= MailingSendAction.ACTION_PREVIEW_SELECT %>">
    <div id="filterbox_container_for_name">
        <div class="filterbox_form_container">
            <div id="filterbox_top"></div>
            <div class="filterbox_form_container" id="searchbox_content">
                <div id="filterbox_small_label" class="filterbox_small_label_tab">
                        ${mailingContentForm.shortname}&nbsp;&nbsp;<%
                    if (aForm != null && aForm
                            .getDescription() != null && !aForm.getDescription().isEmpty()) {%>
                            |&nbsp;&nbsp;${mailingContentForm
                        .description} <%}%>
                </div>
            </div>
            <div id="filterbox_bottom"></div>
        </div>
    </div>
    <div class="grey_box_container">
        <div class="grey_box_top"></div>
        <div class="grey_box_content">

            <div class="float_left">
                <div><label class="mailing_content_preview_box_label"><bean:message
                        key="recipient.Recipient"/>:&nbsp;</label>
                </div>
                <div>
                   	<html:select property="previewCustomerID" size="1" styleClass="mailing_content_preview_box_select">
                   		<c:forEach var="recipient" items="${previewRecipients}">
                    	    <html:option value="${recipient.key}">${recipient.value}</html:option>
                	    </c:forEach>
                   	</html:select>
                </div>
            </div>
            <div class="float_left mailing_content_preview_box">
                <div><label class="mailing_content_preview_box_label"><bean:message key="action.Format"/>:&nbsp;</label>
                </div>
                <div><html:select property="previewFormat" size="1" styleClass="mailing_content_preview_box_select">

                    <html:option value="0"><bean:message key="mailing.Text"/></html:option>
                    <logic:greaterThan name="mailingContentForm" property="mailFormat" value="0">
                        <html:option value="1"><bean:message key="mailing.HTML"/></html:option>
                    </logic:greaterThan>
                </html:select>
                </div>
            </div>
            <div class="float_left mailing_content_preview_box">
                <div><label class="mailing_content_preview_box_label"><bean:message key="default.Size"/>:&nbsp;</label>
                </div>
                <html:select property="previewSize" size="1" styleClass="mailing_content_preview_box_select">
                    <html:option value="4">640x480</html:option>
                    <html:option value="1">800x600</html:option>
                    <html:option value="2">1024x768</html:option>
                    <html:option value="3">1280x1024</html:option>
                </html:select>
            </div>
            <div class="float_right">
                <div><label class="mailing_content_preview_box_label">&nbsp;</label></div>
                <div class="action_button" style="margin-right:20px;">
                    <a href="#"
                       onclick="document.mailingSendForm.submit(); return false;"><span><bean:message
                            key="mailing.Preview"/></span></a>
                </div>
            </div>

        </div>
        <div class="grey_box_bottom"></div>
    </div>

</html:form>
<br>
<table border="0" cellspacing="0" cellpadding="0" class="list_table" id="contents">

    <tr>
        <th class="mailing_content_module"><bean:message key="mailing.Text_Module"/>&nbsp;</th>
        <th class="mailing_content_target_group"><bean:message key="target.Target"/>&nbsp;</th>
        <th class="mailing_content_content"><bean:message key="mailing.Content"/>&nbsp;</th>
    </tr>

    <%
        String rowStyle = "even";
        boolean bgColor = true;
        boolean newTag = false;
    %>

    <% DynamicTag dynTag = null;
        DynamicTagContent tagContent = null;
    %>

    <% int prev_group = -1; %>
    <logic:iterate id="dyntag" name="mailingContentForm" property="tags">
        <% Map.Entry ent = (Map.Entry) pageContext.getAttribute("dyntag");
            dynTag = (DynamicTag) ent.getValue();
            newTag = true;
            if (dynTag.getGroup() != prev_group) {
                if (bgColor) {
                    bgColor = false;
                    rowStyle = "odd";
                } else {
                    bgColor = true;
                    rowStyle = "even";
                }
            }
        %>

        <logic:iterate id="dyncontent" name="dyntag" property="value.dynContent">
            <% Map.Entry ent2 = (Map.Entry) pageContext.getAttribute("dyncontent");
                tagContent = (DynamicTagContent) ent2.getValue(); %>
            <tr class="<%=rowStyle%>">
                <% if (newTag) { %>
                <td valign="top">
                    <span class="ie7hack">
                    <a name="<%= dynTag.getId() %>">
                        &nbsp;<html:link
                            page='<%= new String(\"/mailingcontent.do?action=\" + MailingContentAction.ACTION_VIEW_TEXTBLOCK + \"&dynNameID=\" + dynTag.getId() + \"&mailingID=\" + tmpMailingID) %>'><b><%= dynTag.getDynName() %>
                    </b></html:link></a>
                        &nbsp;&nbsp;
                    </span>
                </td>
                <% } else { %>
                <td valign="top">
                    &nbsp;&nbsp;
                </td>
                <% } %>
                <td valign="top">
                    <span class="ie7hack">
                    <html:link
                        page='<%= new String(\"/mailingcontent.do?action=\" + MailingContentAction.ACTION_VIEW_TEXTBLOCK + \"&dynNameID=\" + dynTag.getId() + \"&mailingID=\" + tmpMailingID + \"#\" + tagContent.getId()) %>'>
                    <% if (tagContent.getTargetID() == 0) { %>
                    <bean:message key="statistic.All_Subscribers"/>
                    <% } else { %>
                    <logic:iterate id="trgt" name="targetGroups" scope="request">
                        <logic:equal name="trgt" property="id"
                                     value="<%= Integer.toString(tagContent.getTargetID()) %>">
                            <c:choose>
                                <c:when test="${trgt.deleted == 0}">
                                    ${trgt.targetName}
                                </c:when>
                                <c:otherwise>
                                    <span class="warning">${trgt.targetName} (<bean:message
                                            key="target.Deleted"/>)</span>
                                </c:otherwise>
                            </c:choose>
                        </logic:equal>
                    </logic:iterate>
                    <% } %>
                </html:link>&nbsp;&nbsp;&nbsp;&nbsp;
                </span>
                </td>
                <td valign="top">
                    <span class="ie7hack">
                        <%= SafeString.getHTMLSafeString(tagContent.getDynContent(), 35) %>&nbsp;
                    </span>
                </td>
            </tr>
            <% newTag = false; %>
        </logic:iterate>


        <tr class="<%=rowStyle%>">
            <% if (newTag) { %>
            <td valign="top"><a name="<%= dynTag.getId() %>">
                &nbsp;<html:link
                    page='<%= new String(\"/mailingcontent.do?action=\" + MailingContentAction.ACTION_VIEW_TEXTBLOCK + \"&dynNameID=\" + dynTag.getId() + \"&mailingID=\" + tmpMailingID) %>'><b><%= dynTag.getDynName() %>
            </b></html:link>
                &nbsp;&nbsp;
            </td>
            <% } else { %>
            <td valign="top">
                &nbsp;&nbsp;
            </td>
            <% } %>
            <td valign="top"><html:link
                    page='<%= new String(\"/mailingcontent.do?action=\" + MailingContentAction.ACTION_VIEW_TEXTBLOCK  + \"&dynNameID=\" + dynTag.getId() + \"&mailingID=\" + tmpMailingID + \"#0\" )  %>'><bean:message
                    key="mailing.New_Content"/></html:link>&nbsp;&nbsp;&nbsp;&nbsp;</td>
            <td valign="top">&nbsp;</td>
        </tr>
        <% newTag = false; %>

    </logic:iterate>

</table>

<script type="text/javascript">
    //    table = document.getElementById('contents');
    //    rewriteTableHeader(table);
    //    writeWidthFromHiddenFields(table);

    $$('#contents tbody tr').each(function(item) {
        item.observe('mouseover', function() {
            item.addClassName('list_highlight');
        });
        item.observe('mouseout', function() {
            item.removeClassName('list_highlight');
        });
    });
</script>