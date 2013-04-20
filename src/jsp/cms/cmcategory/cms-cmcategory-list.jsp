<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ include file="/WEB-INF/taglibs.jsp" %>

<script src="${emmLayoutBase.jsURL}/tablecolumnresize.js" type="text/javascript"></script>
<script type="text/javascript">
    var prevX = -1;
    var tableID = 'cmCategory';
    var columnindex = 0;
    var dragging = false;

    document.onmousemove = drag;
    document.onmouseup = dragstop;


    function toggleContainer(container){
        $(container).toggleClassName('toggle_open');
        $(container).toggleClassName('toggle_closed');
        $(container).next().toggle();
    }

    Event.observe(window, 'load', function() {
        <agn:ShowByPermission token="settings.open">
                var closed = document.getElementsByClassName('toggle_closed');
                if(closed)
                for(var i=0;i<closed.length;i++){
                    closed[i].addClassName('toggle_open');
                    closed[i].next().show();
                    closed[i].removeClassName('toggle_closed');
                }
        </agn:ShowByPermission>
        <agn:HideByPermission token="settings.open">
            toggleContainer(document.getElementById("new_cm_category"));
        </agn:HideByPermission>
        onPageLoad();
    });

</script>
<style type="text/css">
   .no-background {background: none !important;}
</style>
<html:form action="/cms_cmcategory" >

    <input type="hidden" name="action" id="action">
    <div style="margin-left:28px;">
    <div id="advanced_search_top"></div>
    <div id="advanced_search_content">
    <div class="advanced_search_toggle cms_new_element toggle_open" id="new_cm_category" onclick="toggleContainer(this);"><a href="#"><bean:message key="cms.NewCMCategory"/></a></div>
    <div>
        <jsp:include page="cms-cmcategory-view-pure.jsp"/>
    </div>
    </div>
    <div id="advanced_search_bottom"></div>
    </div>

    <div class="list_settings_container">
        <div class="filterbox_form_button"><a href="#" onclick="document.getElementById('action').value='1'; document.contentModuleCategoryForm.submit(); return false;"><span><bean:message key="button.Show"/></span></a></div>
        <div class="list_settings_mainlabel"><bean:message key="settings.Admin.numberofrows"/>:</div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="20"/><label
                for="list_settings_length_0">20</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="50"/><label
                for="list_settings_length_1">50</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="100"/><label
                for="list_settings_length_2">100</label></div>
        <logic:iterate collection="${contentModuleCategoryForm.columnwidthsList}" indexId="i" id="width">
            <html:hidden property="columnwidthsList[${i}]"/>
        </logic:iterate>
    </div>

</html:form>

    <display:table class="list_table" id="cmCategory" name="cmCategoryList"
                   pagesize="${contentModuleCategoryForm.numberofRows}"
                   requestURI="/cms_cmcategory.do?action=${ACTION_LIST}" excludedParams="*" sort="list"
                   defaultsort="1">
        <display:column headerClass="campaign_head_name header" class="name" titleKey="default.Name" sortable="true" sortProperty="name">
            <span class="ie7hack">
                <html:link
                           page="/cms_cmcategory.do?action=${ACTION_VIEW}&cmcId=${cmCategory.id}">${cmCategory.name}
                </html:link>
            </span>
        </display:column>
        <display:column headerClass="campaign_head_desc header" class="description" titleKey="default.description" sortable="true" sortProperty="description">
            <span class="ie7hack">
                ${cmCategory.description}
            </span>
        </display:column>
        <display:column class="edit">
            <html:link styleClass="mailing_edit " titleKey="cms.CMCategory.Edit"
                page="/cms_cmcategory.do?action=${ACTION_VIEW}&cmcId=${cmCategory.id}"> </html:link>
            <html:link styleClass="mailing_delete" titleKey="cms.CMCategory.Delete"
                page="/cms_cmcategory.do?action=${ACTION_CONFIRM_DELETE}&cmcId=${cmCategory.id}&fromListPage=true"> </html:link>
        </display:column>
    </display:table>

   <script type="text/javascript">
        table = document.getElementById('cmCategory');
        rewriteTableHeader(table);
        writeWidthFromHiddenFields(table);

        $$('#cmCategory tbody tr').each(function(item) {
            item.observe('mouseover', function() {
                item.addClassName('list_highlight');
            });
            item.observe('mouseout', function() {
                item.removeClassName('list_highlight');
            });
        });
    </script>
