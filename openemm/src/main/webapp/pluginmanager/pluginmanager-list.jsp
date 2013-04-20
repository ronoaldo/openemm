<%@ page language="java" contentType="text/html; charset=utf-8" buffer="32kb" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script src="${emmLayoutBase.jsURL}/tablecolumnresize.js" type="text/javascript"></script>
<script type="text/javascript">
    var prevX = -1;
    var tableID = 'pluginStatus';
    var columnindex = 0;
    var dragging = false;

    document.onmousemove = drag;
    document.onmouseup = dragstop;
    window.onload = onPageLoad;

    function parametersChanged() {
        document.getElementsByName('pluginManagerForm')[0].numberOfRowsChanged.value = true;
    }
</script>

<html:form action="/pluginManager?action=list">
    <html:hidden property="numberOfRowsChanged"/>
    <div class="list_settings_container">
        <div class="filterbox_form_button">
            <a href="#" onclick="parametersChanged(); document.pluginManagerForm.submit(); return false;">
                <span><bean:message key="button.Show"/></span>
            </a>
        </div>
        <div class="list_settings_mainlabel"><bean:message key="settings.Admin.numberofrows"/>:</div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="20"/>
            <label for="list_settings_length_0">20</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="50"/>
            <label for="list_settings_length_1">50</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="100"/>
            <label for="list_settings_length_2">100</label></div>
        <logic:iterate collection="${pluginManagerForm.columnwidthsList}" indexId="i" id="width">
            <html:hidden property="columnwidthsList[${i}]"/>
        </logic:iterate>
    </div>

    <c:set var="editLink" value="/pluginManager.do?action=detail"/>

    <display:table class="list_table" id="pluginStatus" name="pluginStatusReport.items" requestURI="/pluginManager.do?action=list&__fromdisplaytag=true" excludedParams="*" pagesize="${pluginManagerForm.numberofRows}">
        <display:column titleKey="pluginmanager.plugin.id" class="plugin_id" headerClass="plugin_id_header" property="pluginId" sortable="true" paramId="plugin" paramProperty="pluginId" url="${editLink}"/>
        <display:column titleKey="pluginmanager.plugin.name" class="plugin_name" headerClass="plugin_name_header" sortable="true" sortProperty="pluginName">
            <span class="ie7hack">
                <html:link page="/pluginManager.do?action=detail&plugin=${pluginStatus.pluginId}">
                    ${pluginStatus.pluginName}
                </html:link>
            </span>
        </display:column>
        <display:column titleKey="pluginmanager.plugin.description" class="plugin_description" headerClass="plugin_description_header" sortable="true" sortProperty="description">
            <span class="ie7hack">
                <html:link page="/pluginManager.do?action=detail&plugin=${pluginStatus.pluginId}">
                    ${pluginStatus.description}
                </html:link>
            </span>
        </display:column>
        <display:column titleKey="pluginmanager.plugin.version" class="plugin_version" headerClass="plugin_version_header" property="version" sortable="true" paramId="plugin" paramProperty="pluginId" url="${editLink}"/>
        <display:column titleKey="pluginmanager.plugin.vendor" class="plugin_vendor" headerClass="plugin_vendor_header" property="vendor" sortable="true" paramId="plugin" paramProperty="pluginId" url="${editLink}"/>
        <display:column titleKey="pluginmanager.plugin.activated" class="plugin_activated" headerClass="plugin_activated_header" sortable="true" paramId="plugin" paramProperty="pluginId" url="${editLink}" sortProperty="activated">
            <c:if test="${pluginStatus.activated}">
                <bean:message key="pluginmanager.plugin.activated" />
            </c:if>
            <c:if test="${not pluginStatus.activated}">
                <bean:message key="pluginmanager.plugin.deactivated" />
            </c:if>
        </display:column>
        <display:column class="edit">
            <html:link styleClass="mailing_edit" titleKey="pluginmanager.details" page="${editLink}&plugin=${pluginStatus.pluginId}"></html:link>
        </display:column>
    </display:table>

    <script type="text/javascript">
        table = document.getElementById('pluginStatus');
        rewriteTableHeader(table);
        writeWidthFromHiddenFields(table);

        $$('#pluginStatus tbody tr').each(function(item) {
            item.observe('mouseover', function() {
                item.addClassName('list_highlight');
            });
            item.observe('mouseout', function() {
                item.removeClassName('list_highlight');
            });
        });
    </script>
</html:form>
