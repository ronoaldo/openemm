<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8"
        %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax" %>

<script type="text/javascript">
    <!--
    function parametersChanged() {
        document.getElementsByName('salutationForm')[0].numberOfRowsChanged.value = true;
    }
    //-->
</script>
<script src="${emmLayoutBase.jsURL}/tablecolumnresize.js" type="text/javascript"></script>
<script type="text/javascript">
    var prevX = -1;
    var tableID = 'salutation';
    var columnindex = 0;
    var dragging = false;
    document.onmousemove = drag;
    document.onmouseup = dragstop;
    window.onload = onPageLoad;
</script>

<html:form action="/salutation">

    <div class="list_settings_container">
        <div class="filterbox_form_button"><a href="#"
                                              onclick="document.salutationForm.submit(); return false;"><span><bean:message
                key="button.OK"/></span></a></div>
        <div class="list_settings_mainlabel"><bean:message key="settings.Admin.numberofrows"/>:</div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="20"/><label
                for="list_settings_length_0">20</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="50"/><label
                for="list_settings_length_1">50</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="100"/><label
                for="list_settings_length_2">100</label></div>
        <logic:iterate collection="${salutationForm.columnwidthsList}" indexId="i" id="width">
            <html:hidden property="columnwidthsList[${i}]"/>
        </logic:iterate>
    </div>


    <display:table class="list_table" pagesize="${salutationForm.numberofRows}" id="salutation"
                   name="salutationEntries" sort="external"
                   requestURI="/salutation.do?action=${ACTION_LIST}&__fromdisplaytag=true&numberofRows=${salutationForm.numberofRows}"
                   excludedParams="*"
                   size="${salutationEntries.fullListSize}" partialList="true">
        <display:column property="titleId" titleKey="MailinglistID"
                        sortable="false" headerClass="mailinglist_head_id header"/>
        <display:column class="description" headerClass="salutation_name_head" sortName="description"
                        titleKey="settings.FormOfAddress" sortable="true" sortProperty="description">
            <span class="ie7hack">
                    ${salutation.description}
            </span>
        </display:column>
        <display:column class="edit">
            <html:link styleClass="mailing_edit" titleKey="salutation.SalutationEdit"
                       page="/salutation.do?action=${ACTION_VIEW}&salutationID=${salutation.titleId}"> </html:link>

            <html:link styleClass="mailing_delete" titleKey="salutation.SalutationDelete"
                       page="/salutation.do?action=${ACTION_CONFIRM_DELETE}&salutationID=${salutation.titleId}"> </html:link>


        </display:column>

    </display:table>

    <script type="text/javascript">
        table = document.getElementById('salutation');
        rewriteTableHeader(table);
        writeWidthFromHiddenFields(table);

        $$('#salutation tbody tr').each(function(item) {
            item.observe('mouseover', function() {
                item.addClassName('list_highlight');
            });
            item.observe('mouseout', function() {
                item.removeClassName('list_highlight');
            });
        });
    </script>
</html:form>

