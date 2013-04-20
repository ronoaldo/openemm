<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<script src="${emmLayoutBase.jsURL}/tablecolumnresize.js" type="text/javascript"></script>
<script type="text/javascript">
	var prevX = -1;
    var tableID = 'userform';
    var columnindex = 0;
    var dragging = false;

    document.onmousemove = drag;
    document.onmouseup = dragstop;
    window.onload = onPageLoad;
</script>
<html:form action="/userform">
           
<div class="list_settings_container">
        <div class="filterbox_form_button"><a href="#"
                                              onclick="document.userFormEditForm.submit(); return false;"><span><bean:message
                key="button.Show"/></span></a></div>
        <div class="list_settings_mainlabel"><bean:message key="settings.Admin.numberofrows"/>:</div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="20"/><label
                for="list_settings_length_0">20</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="50"/><label
                for="list_settings_length_1">50</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="100"/><label
                for="list_settings_length_2">100</label></div>
        <logic:iterate collection="${userFormEditForm.columnwidthsList}" indexId="i" id="width">
            <html:hidden property="columnwidthsList[${i}]"/>
        </logic:iterate>
    </div>
        	</html:form>

              			<display:table class="list_table"  id="userform" name="userformlist" pagesize="${userFormEditForm.numberofRows}" requestURI="/userform.do?action=${ACTION_LIST}&__fromdisplaytag=true&numberofRows=${userFormEditForm.numberofRows}" excludedParams="*">
              				<display:column headerClass="forms_head_name header" class="name" titleKey="Form" sortable="true" sortProperty="formName">
                                <span class="ie7hack">
                                    <html:link page="/userform.do?action=${ACTION_VIEW}&formID=${userform.id}">
                                        ${userform.formName}
                                    </html:link>
                                </span>
                            </display:column>
              				<display:column headerClass="forms_head_desc header" class="description" titleKey="default.description" sortable="true" sortProperty="description">
                                <span class="ie7hack">
                                    <html:link page="/userform.do?action=${ACTION_VIEW}&formID=${userform.id}">
                                        ${userform.description}
                                    </html:link>
                                </span>
                            </display:column>
              				<display:column class="edit">
            					  <agn:ShowByPermission token="forms.delete">
                        			<html:link styleClass="mailing_edit" titleKey="settings.form.edit"
                       page="/userform.do?action=${ACTION_VIEW}&formID=${userform.id}"> </html:link>
                            	</agn:ShowByPermission>
              					    <html:link styleClass="mailing_delete" titleKey="settings.form.delete"
                           page="/userform.do?action=${ACTION_CONFIRM_DELETE}&formID=${userform.id}&fromListPage=true"> </html:link>
              				</display:column>
              			</display:table>
                        <script type="text/javascript">
        table = document.getElementById('userform');
        rewriteTableHeader(table);
        writeWidthFromHiddenFields(table);

        $$('#userform tbody tr').each(function(item) {
            item.observe('mouseover', function() {
                item.addClassName('list_highlight');
            });
            item.observe('mouseout', function() {
                item.removeClassName('list_highlight');
            });
        });
    </script>


