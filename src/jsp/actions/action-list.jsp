<%--checked--%>
<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.web.EmmActionAction, org.agnitas.web.forms.EmmActionForm" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>

<% pageContext.setAttribute("ACTION_LIST",EmmActionAction.ACTION_LIST ); %>
<% pageContext.setAttribute("ACTION_VIEW",EmmActionAction.ACTION_VIEW ); %>
<% pageContext.setAttribute("ACTION_CONFIRM_DELETE",EmmActionAction.ACTION_CONFIRM_DELETE ); %>

<script src="${emmLayoutBase.jsURL}/tablecolumnresize.js" type="text/javascript"></script>
<script type="text/javascript">
	var prevX = -1;
    var tableID = 'emmaction';
    var columnindex = 0;
    var dragging = false;

    document.onmousemove = drag;
    document.onmouseup = dragstop;
    window.onload = onPageLoad;
</script>

<%
	EmmActionForm aForm = null;
	if(session.getAttribute("emmActionForm")!=null) {
		aForm = (EmmActionForm) session.getAttribute("emmActionForm");
	}
%>

        	<html:form action="/action">
        <div class="list_settings_container">
        <div class="filterbox_form_button"><a href="#"
                                              onclick="document.emmActionForm.submit(); return false;"><span><bean:message
                key="button.Show"/></span></a></div>
        <div class="list_settings_mainlabel"><bean:message key="settings.Admin.numberofrows"/>:</div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="20"/><label
                for="list_settings_length_0">20</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="50"/><label
                for="list_settings_length_1">50</label></div>
        <div class="list_settings_item"><html:radio property="numberofRows" value="100"/><label
                for="list_settings_length_2">100</label></div>
        <logic:iterate collection="${emmActionForm.columnwidthsList}" indexId="i" id="width">
            <html:hidden property="columnwidthsList[${i}]"/>
        </logic:iterate>
    </div>
						<display:table class="list_table" id="emmaction" name="emmactionList" pagesize="${emmActionForm.numberofRows}" requestURI="/action.do?action=${ACTION_LIST}&__fromdisplaytag=true" excludedParams="*" >
							<display:column headerClass="action_head_name header" class="name" titleKey="action.Action" sortable="true" sortProperty="shortname">
                                <span class="ie7hack">
                                    <html:link page="/action.do?action=${ACTION_VIEW}&actionID=${emmaction.id}">
                                        ${emmaction.shortname}
                                    </html:link>
                                </span>
                            </display:column>
						    <display:column headerClass="action_head_desc header" class="description"  titleKey="default.description" sortable="true" sortProperty="description">
                                <span class="ie7hack">
                                    <html:link page="/action.do?action=${ACTION_VIEW}&actionID=${emmaction.id}">
                                        ${emmaction.description}
                                    </html:link>
                                </span>
                            </display:column>
							<display:column headerClass="action_head_used header" class="name" titleKey="action.used">
							<logic:greaterThan name="emmaction" property="used" value="0">
									<html:link title="${emmaction.formNames}" page="#">
                                        <bean:message key="default.Yes"/>
                                    </html:link>
							</logic:greaterThan>
							<logic:lessThan name="emmaction" property="used" value="1">
										<bean:message key="default.No"/>
							</logic:lessThan>
							
								
							</display:column>
							<display:column class="edit">
							<agn:ShowByPermission token="actions.change">
								 <html:link styleClass="mailing_edit" titleKey="action.Edit_Action"
                       				page="/action.do?action=${ACTION_VIEW}&actionID=${emmaction.id}"> </html:link>
							</agn:ShowByPermission>
							<agn:ShowByPermission token="actions.delete">
            					<html:link styleClass="mailing_delete" titleKey="action.ActionsDelete"
                       				page="/action.do?action=${ACTION_CONFIRM_DELETE}&actionID=${emmaction.id}&fromListPage=true"> </html:link>
                       		</agn:ShowByPermission>
							</display:column>
						</display:table>
                       <script type="text/javascript">
        table = document.getElementById('emmaction');
        rewriteTableHeader(table);
        writeWidthFromHiddenFields(table);

        $$('#emmaction tbody tr').each(function(item) {
            item.observe('mouseover', function() {
                item.addClassName('list_highlight');
            });
            item.observe('mouseout', function() {
                item.removeClassName('list_highlight');
            });
        });
    </script>
</html:form>
