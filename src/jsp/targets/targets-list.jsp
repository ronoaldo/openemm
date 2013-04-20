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
    var tableID = 'targetGroup';
    var columnindex = 0;
    var dragging = false;

   document.onmousemove = drag;
   document.onmouseup = dragstop;
   window.onload = onPageLoad;
</script>
<html:form action="/target">
<div class="list_settings_container">
        <div class="filterbox_form_button"><a href="#" onclick="document.targetForm.submit(); return false;"><span><bean:message key="button.Show"/></span></a></div>
        <div class="list_settings_mainlabel"><bean:message key="settings.Admin.numberofrows"/>:</div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="20"/><label
                for="list_settings_length_0">20</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="50"/><label
                for="list_settings_length_1">50</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="100"/><label
                for="list_settings_length_2">100</label></div>
        <logic:iterate collection="${targetForm.columnwidthsList}" indexId="i" id="width">
            <html:hidden property="columnwidthsList[${i}]"/>
        </logic:iterate>
    </div>
 			<display:table class="list_table" id="targetGroup" name="targetlist" pagesize="${targetForm.numberofRows}" sort="list" requestURI="/target.do?action=${targetForm.action}" excludedParams="*">
               	<display:column class="email" headerClass="targets_head_name"  titleKey="target.Target" sortable="true" sortProperty="targetName">
                    <span class="ie7hack">
                        <html:link page="/target.do?action=${ACTION_VIEW}&targetID=${targetGroup.id}">
                            ${targetGroup.targetName}
                        </html:link>
                    </span>
                </display:column>
                <display:column class="email" headerClass="targets_head_desc"  titleKey="default.description" sortable="true" sortProperty="targetDescription">
                    <span class="ie7hack">
                        <html:link page="/target.do?action=${ACTION_VIEW}&targetID=${targetGroup.id}">
                            ${targetGroup.targetDescription}
                        </html:link>
                    </span>
                </display:column>
                <display:column headerClass="senddate" class="senddate" titleKey="target.CreationDate" format="{0,date,yyyy-MM-dd}" property="creationDate" sortable="true"/>
                <display:column headerClass="senddate" class="senddate" titleKey="target.ChangeDate" format="{0,date,yyyy-MM-dd}" property="changeDate" sortable="true"/>
                <display:column class="edit" >
                	<html:link styleClass="mailing_edit" titleKey="target.Edit"
                       page="/target.do?action=${ACTION_VIEW}&&targetID=${targetGroup.id}"> </html:link>
                    <agn:ShowByPermission token="targets.delete">
                    	<html:link styleClass="mailing_delete" titleKey="target.Delete"
                       		page="/target.do?action=${ACTION_CONFIRM_DELETE}&&targetID=${targetGroup.id}&previousAction=${ACTION_LIST}"> </html:link>
                    </agn:ShowByPermission>
				</display:column>
    		</display:table>


    <script type="text/javascript">
        table = document.getElementById('targetGroup');
        rewriteTableHeader(table);
        writeWidthFromHiddenFields(table);

        $$('#targetGroup tbody tr').each(function(item) {
            item.observe('mouseover', function() {
                item.addClassName('list_highlight');
            });
            item.observe('mouseout', function() {
                item.removeClassName('list_highlight');
            });
        });
    </script>
</html:form>

