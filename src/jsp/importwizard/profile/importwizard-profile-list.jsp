<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax" %>

<body onload="hideSaveButton()">

<script src="${emmLayoutBase.jsURL}/tablecolumnresize.js" type="text/javascript"></script>
<script type="text/javascript">
    var prevX = -1;
    var tableID = 'profile';
    var columnindex = 0;
    var dragging = false;

    document.onmousemove = drag;
    document.onmouseup = dragstop;
    window.onload = onPageLoad;
</script>

<html:form action="/importprofile">
    <div class="list_settings_container">
        <div class="filterbox_form_button"><a href="#"
                                              onclick="document.importProfileForm.submit(); return false;"><span><bean:message
                key="button.Show"/></span></a></div>
        <div class="list_settings_mainlabel"><bean:message key="settings.Admin.numberofrows"/>:</div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="20"/><label
                for="list_settings_length_0">20</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="50"/><label
                for="list_settings_length_1">50</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="100"/><label
                for="list_settings_length_2">100</label></div>
    </div>

    <%--<ajax:displayTag id="importProfileTable" ajaxFlag="displayAjax">--%>
        <display:table class="list_table" id="profile"
                       name="profileList"
                       pagesize="${importProfileForm.numberofRows}"
                       requestURI="/importprofile.do?action=${ACTION_LIST}"
                       excludedParams="*" defaultsort="1">
            <display:column headerClass="importprofile_head_name" class="name" sortProperty="name"
                            titleKey="import.ImportProfile" sortable="true">
                <span class="ie7hack">
                    <html:link
                               page="/importprofile.do?action=${ACTION_VIEW}&profileId=${profile.id}">${profile.name}
                    </html:link>
                </span>
            </display:column>
            <display:column headerClass="importprofile_head_default"
                            class="description"
                            titleKey="import.profile.default">
                <html:radio name="importProfileForm"
                            property="defaultProfileId"
                            value="${profile.id}"/>
            </display:column>
            <display:column class="edit">
                <html:link styleClass="mailing_edit" titleKey="recipient.importprofile.edit"
                           page="/importprofile.do?action=${ACTION_VIEW}&profileId=${profile.id}">
                </html:link>
                <html:link styleClass="mailing_delete" titleKey="recipient.importprofile.delete"
                           page="/importprofile.do?action=${ACTION_CONFIRM_DELETE}&profileId=${profile.id}&fromListPage=true">
                </html:link>
            </display:column>
        </display:table>
        <script type="text/javascript">
            table = document.getElementById('profile');
            rewriteTableHeader(table);
            writeWidthFromHiddenFields(table);

            $$('#profile tbody tr').each(function(item) {
                item.observe('mouseover', function() {
                    item.addClassName('list_highlight');
                });
                item.observe('mouseout', function() {
                    item.removeClassName('list_highlight');
                });
            });
        </script>

    <%--</ajax:displayTag>--%>

    <br>

    <div id="importwizard_button_container" class="button_container importwizard_button_container">
        <input type="hidden" id="setDefault" name="setDefault" value=""/>

        <div class="action_button">
            <a href="#"
               onclick="document.importProfileForm.setDefault.value='setDefault'; document.importProfileForm.submit();return false;">
                <span><bean:message key="button.Save"/></span>
            </a>
        </div>
        <div class="action_button"><bean:message key="recipient.importprofile.defaultprofile"/>:</div>
    </div>

    <script type="text/javascript">
        function hideSaveButton() {
            table = document.getElementById('profile');
            if (table == null) {
                document.getElementById('importwizard_button_container').style.display = 'none';
            }
        }
    </script>

</html:form>
