<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.web.MailingBaseAction,org.agnitas.web.forms.MailingBaseForm"
         buffer="32kb" %>
<%@ page import="java.util.Map" %>
<%@ page import="org.agnitas.beans.Mailing" %>
<%@ page import="org.agnitas.beans.MailingBase" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    int isTemplate = 0;
    if (((MailingBaseForm) session.getAttribute("mailingBaseForm")).isIsTemplate()) {
        isTemplate = 1;
    }
    boolean showList = (Boolean) request.getAttribute("hasPermission");
%>

<logic:equal name="mailingBaseForm" property="isTemplate" value="false">
    <% pageContext.setAttribute("ACTION_USED_ACTIONS", MailingBaseAction.ACTION_USED_ACTIONS); %>
</logic:equal>

<% pageContext.setAttribute("ACTION_VIEW", MailingBaseAction.ACTION_VIEW); %>
<% pageContext.setAttribute("ACTION_CONFIRM_DELETE", MailingBaseAction.ACTION_CONFIRM_DELETE); %>
<% pageContext.setAttribute("ACTION_LIST", MailingBaseAction.ACTION_LIST); %>

<c:if test="<%= showList%>">
    <script type="text/javascript">
        <!--
        function parametersChanged() {
            document.getElementsByName('mailingBaseForm')[0].numberOfRowsChanged.value = true;
        }
        //-->
    </script>
    <script src="${emmLayoutBase.jsURL}/tablecolumnresize.js" type="text/javascript"></script>
    <script type="text/javascript">
        var prevX = -1;
        var tableID = 'mailing';
        var columnindex = 0;
        var dragging = false;

        document.onmousemove = drag;
        document.onmouseup = dragstop;

        window.onload = onPageLoad;

    </script>

    <html:form action="/mailingbase">
        <html:hidden property="numberOfRowsChanged"/>

        <% if (isTemplate == 0) { %>
        <div id="filterbox_container">
            <div id="filterbox_label"></div>
            <div class="filterbox_form_container">
                <div id="filterbox_top"></div>
                <div id="filterbox_content">
                    <div class="form_columns_wrapper">
                        <div class="filterbox_form_left">
                            <html:hidden property="__STRUTS_CHECKBOX_mailingTypeNormal" value="false"/>
                            <html:hidden property="__STRUTS_CHECKBOX_mailingTypeEvent" value="false"/>
                            <html:hidden property="__STRUTS_CHECKBOX_mailingTypeDate" value="false"/>
                            <agn:ShowByPermission token="mailing.followup">
                                <html:hidden property="__STRUTS_CHECKBOX_mailingTypeFollowup" value="false"/>
                            </agn:ShowByPermission>
                            <div class="filterbox_checkbox_item"><html:checkbox property="mailingTypeNormal"
                                                                                onchange="parametersChanged()"><label
                                    for="normal"><bean:message key="mailing.Mailing_normal_show"/></label></html:checkbox>
                            </div>
                            <div class="filterbox_checkbox_item"><html:checkbox property="mailingTypeEvent"
                                                                                onchange="parametersChanged()"><label
                                    for="aktionsbasiert"><bean:message
                                    key="mailing.Mailing_event_show"/></label></html:checkbox></div>
                        </div>

                        <div class="filterbox_form_right">
                            <div class="filterbox_checkbox_item"><html:checkbox property="mailingTypeDate"
                                                                                onchange="parametersChanged()"><label
                                    for="datumsgesteuert"><bean:message
                                    key="mailing.Mailing_date_show"/></label></html:checkbox></div>
                        </div>
                    </div>
                    <div class="filterbox_form_button filterbox_form_button_right_corner">
                        <a href="#" onclick="parametersChanged(); document.mailingBaseForm.submit(); return false;">
                            <span><bean:message key="button.Show"/></span></a>
                    </div>
                </div>
                <div id="filterbox_bottom"></div>
            </div>
        </div>
        <div class="list_settings_container">
            <div class="filterbox_form_button">
                <a href="#" onclick="parametersChanged(); document.mailingBaseForm.submit(); return false;"><span><bean:message
                        key="button.Show"/></span></a>
            </div>
            <div class="list_settings_mainlabel"><bean:message key="settings.Admin.numberofrows"/>:</div>
            <div class="list_settings_item"><html:radio property="numberofRows" value="20"/><label
                    for="list_settings_length_0">20</label></div>
            <div class="list_settings_item"><html:radio property="numberofRows" value="50"/><label
                    for="list_settings_length_1">50</label></div>
            <div class="list_settings_item"><html:radio property="numberofRows" value="100"/><label
                    for="list_settings_length_2">100</label></div>
            <logic:iterate collection="${mailingBaseForm.columnwidthsList}" indexId="i" id="width">
                <html:hidden property="columnwidthsList[${i}]"/>
            </logic:iterate>
        </div>
        <% } else { %>

        <div class="list_settings_container">
            <div class="filterbox_form_button"><a href="#"
                                                  onclick="parametersChanged(); document.mailingBaseForm.submit(); return false;"><span><bean:message
                    key="button.Show"/></span></a></div>
            <div class="list_settings_mainlabel"><bean:message key="settings.Admin.numberofrows"/>:</div>
            <div class="list_settings_item"><html:radio property="numberofRows" value="20"/><label
                    for="list_settings_length_0">20</label></div>
            <div class="list_settings_item"><html:radio property="numberofRows" value="50"/><label
                    for="list_settings_length_1">50</label></div>
            <div class="list_settings_item"><html:radio property="numberofRows" value="100"/><label
                    for="list_settings_length_2">100</label></div>
            <logic:iterate collection="${mailingBaseForm.columnwidthsList}" indexId="i" id="width">
                <html:hidden property="columnwidthsList[${i}]"/>
            </logic:iterate>
        </div>

        <% } %>

    </html:form>

    <display:table class="list_table" id="mailing" name="mailinglist" pagesize="${mailingBaseForm.numberofRows}"
                   requestURI="/mailingbase.do?action=${mailingBaseForm.action}&isTemplate=${mailingBaseForm.isTemplate}&__fromdisplaytag=true"
                   excludedParams="*" partialList="true" size="${mailinglist.fullListSize}" sort="external">
        <logic:equal name="mailingBaseForm" property="isTemplate" value="false">
            <display:column headerClass="head_action" class="action" titleKey="action.Action">
                <% if (((MailingBase) pageContext.getAttribute("mailing")).isHasActions()) { %>
                <span class="ie7hack">
                <html:link page="/mailingbase.do?action=${ACTION_USED_ACTIONS}&mailingID=${mailing.id}">
                    </span>
                    <img
                            border="0" title="<bean:message key="action.action_link" />"
                            src="${emmLayoutBase.imagesURL}/table_aktion_haken.png"></html:link>&nbsp;&nbsp;
                <% } else { %>
                &nbsp;&nbsp;
                <% } %>
            </display:column>
            <display:column headerClass="head_mailing header" class="mailing" titleKey="Mailing" sortable="true"
                            sortProperty="shortname">
				   		<span class="ie7hack">
				   			<html:link
                                       page="/mailingbase.do?action=${ACTION_VIEW}&mailingID=${mailing.id}">${mailing.shortname} </html:link>
				  	 	</span>
            </display:column>
            <display:column headerClass="head_description_wide header" class="description" titleKey="default.description"
                            sortable="true" sortProperty="description">
				   		<span class="ie7hack">
				   			<html:link
                                       page="/mailingbase.do?action=${ACTION_VIEW}&mailingID=${mailing.id}">${mailing.description} </html:link>
				  	 	</span>
            </display:column>
            <display:column headerClass="head_mailinglist header" class="mailinglist" titleKey="Mailinglist"
                            sortable="true" sortProperty="mailinglist">
                <span class="ie7hack">${mailing.mailinglist.shortname}</span>
            </display:column>
            <display:column headerClass="senddate" class="senddate" titleKey="mailing.senddate"
                            format="{0,date,yyyy-MM-dd}" property="senddate" sortable="true"/>
            <display:column class="edit">
            <html:link styleClass="mailing_edit" titleKey="mailing.MailingEdit"
                       page="/mailingbase.do?action=${ACTION_VIEW}&mailingID=${mailing.id}"> </html:link>
            <agn:ShowByPermission token="mailing.delete">
                <html:link
                        styleClass="mailing_delete" titleKey="mailing.MailingDelete"
                        page="/mailingbase.do?action=${ACTION_CONFIRM_DELETE}&previousAction=${ACTION_LIST}&mailingID=${mailing.id}"> </html:link>
            </agn:ShowByPermission>
        </display:column>
        </logic:equal>
        <logic:equal name="mailingBaseForm" property="isTemplate" value="true">
            <display:column headerClass="template_head_name header" class="mailing" titleKey="Template" sortable="true" sortProperty="shortname">
                <span class="ie7hack">
				   	<html:link page="/mailingbase.do?action=${ACTION_VIEW}&mailingID=${mailing.id}">${mailing.shortname} </html:link>
				</span>
            </display:column>
            <display:column headerClass="template_head_desc header" class="description" titleKey="default.description" sortable="true" sortProperty="description">
                <span class="ie7hack">
				   	<html:link page="/mailingbase.do?action=${ACTION_VIEW}&mailingID=${mailing.id}">${mailing.description} </html:link>
				</span>
            </display:column>
            <display:column headerClass="template_head_mailinglist header" class="mailinglist" titleKey="Mailinglist"
                            property="mailinglist.shortname" sortProperty="mailinglist" sortable="true"/>

        <display:column class="edit">
            <html:link styleClass="mailing_edit" titleKey="mailing.TemplateEdit"
                       page="/mailingbase.do?action=${ACTION_VIEW}&mailingID=${mailing.id}"> </html:link>
            <agn:ShowByPermission token="mailing.delete">
                <html:link
                        styleClass="mailing_delete" titleKey="mailing.TemplateDelete"
                        page="/mailingbase.do?action=${ACTION_CONFIRM_DELETE}&previousAction=${ACTION_LIST}&mailingID=${mailing.id}"> </html:link>
            </agn:ShowByPermission>
        </display:column>
        </logic:equal>
    </display:table>
    <script type="text/javascript">
        table = document.getElementById('mailing');
        rewriteTableHeader(table);
        writeWidthFromHiddenFields(table);

        $$('#mailing tbody tr').each(function(item) {
            item.observe('mouseover', function() {
                item.addClassName('list_highlight');
            });
            item.observe('mouseout', function() {
                item.removeClassName('list_highlight');
            });
        });
    </script>
</c:if>
