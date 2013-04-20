<%-- checked --%>
<%@ page language="java" contentType="text/html; charset=utf-8"
         import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.math.BigDecimal" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script src="${emmLayoutBase.jsURL}/tablecolumnresize.js" type="text/javascript"></script>
<script type="text/javascript">
    var prevX = -1;
    var tableID = 'fields';
    var columnindex = 0;
    var dragging = false;

    document.onmousemove = drag;
    document.onmouseup = dragstop;
    window.onload = onPageLoad;
</script>

<div class="table_messages_spacer"></div>


<display:table class="list_table" id="fields" name="columnInfo" pagesize="${profileFieldForm.numberofRows}"
               sort="list"
               requestURI="/profiledb.do?action=${ACTION_LIST}&__fromdisplaytag=true"
               excludedParams="*">
    <display:column headerClass="profile_fields_name" class="profile_fields_name" titleKey="settings.FieldName"
                    sortable="true"
                    sortProperty="shortname">
				   		<span class="ie7hack">
				   			<html:link
                                       page="/profiledb.do?action=${ACTION_VIEW}&fieldname=${fields.column}"><b>${fields.shortname}</b> </html:link>
				  	 	</span>
    </display:column>
    <display:column headerClass="profile_fields_dbname" class="profile_fields_dbname" titleKey="settings.FieldNameDB"
                    sortable="true"
                    sortProperty="column">
				   		<span class="ie7hack">
				   			<html:link
                                       page="/profiledb.do?action=${ACTION_VIEW}&fieldname=${fields.column}">${fields.column} </html:link>
				  	 	</span>
    </display:column>
    <display:column headerClass="profile_fields_type" class="profile_fields_type" titleKey="default.Type"
                    sortable="true"
                    sortProperty="dataType">
    	<bean:message key="settings.fieldType.${fields.dataType}"/>
    </display:column>
    <display:column headerClass="profile_fields_length" class="profile_fields_length" titleKey="settings.Length"
                    sortable="true"
                    sortProperty="dataTypeLength">
        <div class="profile_def_value_column" align="right">
                <%
                	ProfileField fieldValues = (ProfileField) pageContext.getAttribute("fields");
                    if (fieldValues.getDataTypeLength() > 0 ) { %>
                        ${fields.dataTypeLength}
                    <% } %>

        </div>
    </display:column>
    <display:column headerClass="profile_fields_defvalue" class="profile_fields_defvalue"
                    titleKey="settings.Default_Value" sortable="true"
                    sortProperty="defaultValue">
        <div align="right" class="profile_def_value_column">
                                <span class="ie7hack">
                                        ${fields.defaultValue}
                                </span>
        </div>
    </display:column>
    <display:column headerClass="profile_fields_null_allowed" class="profile_fields_defvalue"
                    titleKey="settings.NullAllowed" sortable="true"
                    sortProperty="default_value">
                                <span class="ie7hack">
                                <%
                                	ProfileField fieldValues = (ProfileField) pageContext.getAttribute("fields");
                                    if (fieldValues.getNullable()) { %>
                                    <bean:message key="default.Yes"/>
                                <% } else { %>
                                    <bean:message key="default.No"/>
                                <% } %>
                                </span>
    </display:column>
    <display:column class="edit">
        <html:link styleClass="mailing_edit" titleKey="settings.profile.ProfileEdit"
                   page="/profiledb.do?action=${ACTION_VIEW}&fieldname=${fields.column}"/>
        <html:link styleClass="mailing_delete" titleKey="settings.profile.ProfileDelete"
                   page="/profiledb.do?action=${ACTION_CONFIRM_DELETE}&fieldname=${fields.column}&fromListPage=true"/>
    </display:column>
</display:table>


<script type="text/javascript">
    table = document.getElementById('fields');
    rewriteTableHeader(table);
    writeWidthFromHiddenFields(table);

    $$('#fields tbody tr').each(function(item) {
        item.observe('mouseover', function() {
            item.addClassName('list_highlight');
        });
        item.observe('mouseout', function() {
            item.removeClassName('list_highlight');
        });
    });
</script>
