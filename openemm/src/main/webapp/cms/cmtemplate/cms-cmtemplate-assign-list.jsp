<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ include file="/WEB-INF/taglibs.jsp" %>
<script src="${emmLayoutBase.jsURL}/tablecolumnresize.js" type="text/javascript"></script>
<script type="text/javascript">
    var prevX = -1;
    var tableID = 'mailing';
    var columnindex = 0;
    var dragging = false;
    var minWidthLast = 95;

    document.onmousemove = drag;
    document.onmouseup = dragstop;
    window.onload = onPageLoad;
</script>
<script type="text/javascript">
    function parametersChanged() {
        document.getElementsByName('cmTemplateForm')[0].numberOfRowsChanged.value = true;
    }
</script>
    <html:form action="/cms_cmtemplate">
    <html:hidden property="action"/>
    <html:hidden property="numberOfRowsChanged"/>


    <div class="list_settings_container">
        <div class="filterbox_form_button"><a href="#" onclick="parametersChanged(); document.cmTemplateForm.submit(); return false;"><span><bean:message key="button.Show"/></span></a></div>
        <div class="list_settings_mainlabel"><bean:message key="settings.Admin.numberofrows"/>:</div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="20"/><label
                for="list_settings_length_0">20</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="50"/><label
                for="list_settings_length_1">50</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="100"/><label
                for="list_settings_length_2">100</label></div>
        <logic:iterate collection="${cmTemplateForm.columnwidthsList}" indexId="i" id="width">
            <html:hidden property="columnwidthsList[${i}]"/>
        </logic:iterate>
    </div>
    <div>
         	<display:table class="list_table" id="mailing" name="mailingsList" pagesize="${cmTemplateForm.numberofRows}"  requestURI="/cms_cmtemplate.do?action=${ACTION_ASSIGN_LIST}" excludedParams="*" sort="external">
         		<display:column headerClass="head_mailing" class="mailing" titleKey="Mailing" sortable="true">
                    <span class="ie7hack">
                        <html:link
                                   page="/mailingbase.do?action=${ACTION_VIEW_MAILING}&mailingID=${mailing.mailingid}">${mailing.shortname}
                        </html:link>
                    </span>
                </display:column>
				<display:column headerClass="head_cm_template_description" titleKey="default.description" sortable="true">
                    <span class="ie7hack">
                        <html:link
                                   page="/mailingbase.do?action=${ACTION_VIEW_MAILING}&mailingID=${mailing.mailingid}">${mailing.description}
                        </html:link>
                    </span>
                </display:column>
                <display:column headerClass="head_cm_template_assigned" class="cm_template_other_templates" titleKey="cms.AssignedToCMTemplate">
                    <logic:equal name="mailing" property="hasCMTemplate" value="true">
                        <bean:message key="yes" bundle="cmsbundle"/>
                    </logic:equal>
                    <logic:equal name="mailing" property="hasCMTemplate" value="false">
                        <bean:message key="no" bundle="cmsbundle"/>
                    </logic:equal>
                </display:column>
                <display:column headerClass="head_cm_template_assignment" class="cm_template_assigned" titleKey="cms.Assigned">
                    <input type="checkbox" name="assign_mailing_${mailing.mailingid}" value="mailing_${mailing.mailingid}"
                        <logic:equal name="mailing" property="assigned" value="true">
                            checked="1"
                        </logic:equal>
                        <logic:equal name="mailing" property="hasClassicTemplate" value="true">
                            disabled="true"
                        </logic:equal>
                    />
                </display:column>
         	</display:table>

    </div>
    <div class="button_container cmtemplate_assign_button">

        <input type="hidden" id="assign" name="assign" value=""/>
        <div class="action_button">
            <a href="#" onclick="document.getElementById('assign').value='assign'; document.cmTemplateForm.submit(); return false;"><span><bean:message key="button.Save"/></span></a>
        </div>

        <div class="action_button"><bean:message key="cms.Assignment"/>:</div>
    </div>

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
       <%-- </td></tr>
          <tr><td height="10"></td></tr>
        <tr><td>
        <html:image src="button?msg=button.Save" border="0" property="assign" value="assign"/>
        </td></tr>
      </table>--%>
    </html:form>
