<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" errorPage="/error.jsp"%>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<script type="text/javascript">
    <!--
    function parametersChanged() {
        document.getElementsByName('adminForm')[0].numberOfRowsChanged.value = true;
    }
    //-->
</script>
<script src="${emmLayoutBase.jsURL}/tablecolumnresize.js" type="text/javascript"></script>
<script type="text/javascript">
    var prevX = -1;
    var tableID = 'admin';
    var columnindex = 0;
    var dragging = false;
    document.onmousemove = drag;
    document.onmouseup = dragstop;
    window.onload = onPageLoad;
</script>
<html:form action="/admin.do?action=${ACTION_LIST}">
    <html:hidden property="numberOfRowsChanged"/>
    <div class="list_settings_container">
        <div class="filterbox_form_button"><a href="#"
                                              onclick="document.adminForm.submit(); return false;"><span><bean:message
                key="button.OK"/></span></a></div>
        <div class="list_settings_mainlabel"><bean:message key="settings.Admin.numberofrows"/>:</div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="20"/><label
                for="list_settings_length_0">20</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="50"/><label
                for="list_settings_length_1">50</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="100"/><label
                for="list_settings_length_2">100</label></div>
        <logic:iterate collection="${adminForm.columnwidthsList}" indexId="i" id="width">
            <html:hidden property="columnwidthsList[${i}]"/>
        </logic:iterate>
    </div>


 <display:table class="list_table header" pagesize="${adminForm.numberofRows}" id="admin"
                   name="adminEntries"
                   requestURI="/admin.do?action=${ACTION_LIST}&__fromdisplaytag=true&numberofRows=${adminForm.numberofRows}" excludedParams="*"
                   size="${adminEntries.fullListSize}" partialList="true">
            <display:column titleKey="settings.User_Name" headerClass="admin_head_name header" sortable="false">
                <span class="ie7hack">
        		    <html:link page="/admin.do?action=${ACTION_VIEW}&adminID=${admin.id}">${admin.username}</html:link>
                </span>
            </display:column>
          
            <display:column headerClass="admin_head_desc header" class="description"
                        titleKey="default.description" sortable="false">
            <span class="ie7hack">
			    <html:link page="/admin.do?action=${ACTION_VIEW}&adminID=${admin.id}">${admin.fullname} </html:link>
            </span>
            </display:column>

            <display:column title="&nbsp;" class="edit" headerClass="admin_head_edit">
            <html:link styleClass="mailing_edit" titleKey="settings.admin.edit"
                       page="/admin.do?action=${ACTION_VIEW}&adminID=${admin.id}"> </html:link>
            <agn:ShowByPermission token="forms.delete">
                <html:link styleClass="mailing_delete" titleKey="settings.admin.delete"
                           page="/admin.do?action=${ACTION_CONFIRM_DELETE}&adminID=${admin.id}"> </html:link>
            </agn:ShowByPermission>
        </display:column>
 </display:table>
</html:form>
<script type="text/javascript">
    table = document.getElementById('admin');
    rewriteTableHeader(table);
    writeWidthFromHiddenFields(table);

    $$('#admin tbody tr').each(function(item) {
        item.observe('mouseover', function() {
            item.addClassName('list_highlight');
        });
        item.observe('mouseout', function() {
            item.removeClassName('list_highlight');
        });
    });
</script>
