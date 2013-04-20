<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.web.MailinglistAction" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<% pageContext.setAttribute("ACTION_LIST" , MailinglistAction.ACTION_LIST); %>
<% pageContext.setAttribute("ACTION_VIEW" , MailinglistAction.ACTION_VIEW); %>
<% pageContext.setAttribute("ACTION_CONFIRM_DELETE" , MailinglistAction.ACTION_CONFIRM_DELETE); %>

<script src="${emmLayoutBase.jsURL}/tablecolumnresize.js" type="text/javascript"></script>
<script type="text/javascript">
	var prevX = -1;
    var tableID = 'mailinglist';
    var columnindex = 0;
    var dragging = false;

    document.onmousemove = drag;
    document.onmouseup = dragstop;
    window.onload = onPageLoad;
</script>
<script type="text/javascript">
    <!--
    function parametersChanged() {
        document.getElementsByName('mailinglistForm')[0].numberOfRowsChanged.value = true;
    }
    //-->
</script>

<html:form action="/mailinglist.do">
    <html:hidden property="numberOfRowsChanged"/>
    <div class="list_settings_container">
        <div class="filterbox_form_button"><a href="#"
                                              onclick="parametersChanged(); document.mailinglistForm.submit(); return false;"><span><bean:message
                key="button.Show"/></span></a></div>
        <div class="list_settings_mainlabel"><bean:message key="settings.Admin.numberofrows"/>:</div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="20"/><label
                for="list_settings_length_0">20</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="50"/><label
                for="list_settings_length_1">50</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="100"/><label
                for="list_settings_length_2">100</label></div>
        <logic:iterate collection="${mailinglistForm.columnwidthsList}" indexId="i" id="width">
            <html:hidden property="columnwidthsList[${i}]"/>
        </logic:iterate>
    </div>

    <display:table class="list_table" id="mailinglist" name="mailinglistList"
                   pagesize="${mailinglistForm.numberofRows}" sort="external"
                   requestURI="/mailinglist.do?action=${ACTION_LIST}&__fromdisplaytag=true"
                   excludedParams="*">
        <display:column headerClass="mailinglist_head_id header" class="id" property="id"
                        titleKey="MailinglistID"/>
        <display:column headerClass="mailinglist_head_name header" class="name" titleKey="Mailinglist" sortable="true"
                        sortProperty="shortname">
            <span class="ie7hack">
                <html:link
                        page="/mailinglist.do?action=${ACTION_VIEW}&mailinglistID=${mailinglist.id}">
                    ${mailinglist.shortname}
                </html:link>
            </span>
        </display:column>
        <display:column headerClass="mailinglist_head_desc header" class="description" titleKey="default.description"
                        sortable="true" sortProperty="description">
            <span class="ie7hack">
                <html:link
                        page="/mailinglist.do?action=${ACTION_VIEW}&mailinglistID=${mailinglist.id}">
                    ${mailinglist.description}
                </html:link>
            </span>
        </display:column>
        <display:column class="edit">
            <agn:ShowByPermission token="mailinglist.change">
                <html:link
                        page="/mailinglist.do?action=${ACTION_VIEW}&mailinglistID=${mailinglist.id}"
                        styleClass="mailing_edit" titleKey="mailinglist.edit">
                </html:link>
            </agn:ShowByPermission>
            <agn:ShowByPermission token="mailinglist.delete">
                <html:link
                        page="/mailinglist.do?action=${ACTION_CONFIRM_DELETE}&mailinglistID=${mailinglist.id}&previousAction=${ACTION_LIST}"
                        styleClass="mailing_delete" titleKey="mailinglist.delete">
                </html:link>
            </agn:ShowByPermission>
        </display:column>
    </display:table>
    <script type="text/javascript">
        table = document.getElementById('mailinglist');
        rewriteTableHeader(table);
        writeWidthFromHiddenFields(table);

        $$('#mailinglist tbody tr').each(function(item) {
            item.observe('mouseover', function() {
                item.addClassName('list_highlight');
            });
            item.observe('mouseout', function() {
                item.removeClassName('list_highlight');
            });
        });
    </script>

</html:form>

