<%--checked --%>
<%@ page import="org.agnitas.web.CampaignAction" %>
<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<script src="${emmLayoutBase.jsURL}/tablecolumnresize.js" type="text/javascript"></script>
<script type="text/javascript">
    var prevX = -1;
    var tableID = 'campaign';
    var columnindex = 0;
    var dragging = false;

    document.onmousemove = drag;
    document.onmouseup = dragstop;
    window.onload = onPageLoad;
</script>

<%
	pageContext.setAttribute("ACTION_LIST", CampaignAction.ACTION_LIST);
	pageContext.setAttribute("ACTION_VIEW", CampaignAction.ACTION_VIEW);
	pageContext.setAttribute("ACTION_CONFIRM_DELETE", CampaignAction.ACTION_CONFIRM_DELETE);
%>

<html:form action="/campaign">
    <div class="list_settings_container">
        <div class="filterbox_form_button"><a href="#"
                                              onclick="document.campaignForm.submit(); return false;"><span><bean:message
                key="button.Show"/></span></a></div>
        <div class="list_settings_mainlabel"><bean:message key="settings.Admin.numberofrows"/>:</div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="20"/><label
                for="list_settings_length_0">20</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="50"/><label
                for="list_settings_length_1">50</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="100"/><label
                for="list_settings_length_2">100</label></div>
        <logic:iterate collection="${campaignForm.columnwidthsList}" indexId="i" id="width">
            <html:hidden property="columnwidthsList[${i}]"/>
        </logic:iterate>
    </div>

    <display:table class="list_table" id="campaign" name="campaignlist" pagesize="${campaignForm.numberofRows}"
                   requestURI="/campaign.do?action=${ACTION_LIST}&__fromdisplaytag=true" excludedParams="*"
                   sort="list">
        <display:column headerClass="campaign_head_name header" class="name" titleKey="campaign.Campaign" sortable="true">
            <span class="ie7hack">
			    <html:link page="/campaign.do?action=${ACTION_VIEW}&campaignID=${campaign.id}">${campaign.shortname} </html:link>
            </span>
        </display:column>
        <display:column headerClass="campaign_head_desc header" class="description" titleKey="default.description" sortable="true">
            <span class="ie7hack">
			    <html:link page="/campaign.do?action=${ACTION_VIEW}&campaignID=${campaign.id}">${campaign.description} </html:link>
            </span>
        </display:column>
        <display:column class="edit">
            <agn:ShowByPermission token="campaign.delete">
                <html:link styleClass="mailing_edit" titleKey="campaign.Edit"
                           page="/campaign.do?action=${ACTION_VIEW}&campaignID=${campaign.id}"> </html:link>
            </agn:ShowByPermission>
            <agn:ShowByPermission token="campaign.change">
                <html:link styleClass="mailing_delete" titleKey="campaign.Delete"
                           page="/campaign.do?action=${ACTION_CONFIRM_DELETE}&previousAction=${ACTION_LIST}&campaignID=${campaign.id}"> </html:link>
            </agn:ShowByPermission>
        </display:column>
    </display:table>
    <script type="text/javascript">
        table = document.getElementById('campaign');
        rewriteTableHeader(table);
        writeWidthFromHiddenFields(table);

       $$('#campaign tbody tr').each(function(item) {
            item.observe('mouseover', function() {
                item.addClassName('list_highlight');
            });
            item.observe('mouseout', function() {
                item.removeClassName('list_highlight');
            });
        });
    </script>

</html:form>
