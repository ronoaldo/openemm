<%@ page language="java"
         import="org.agnitas.util.AgnUtils,org.agnitas.beans.ColumnMapping,org.agnitas.web.forms.ImportProfileColumnsForm"
         contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/taglibs.jsp" %>

<html:form action="/importprofile_columns" enctype="multipart/form-data">
    <html:hidden property="profileId"/>
    <html:hidden property="action"/>

    <c:forEach var="columnsDefaults" items="${importProfileColumnsForm.dbColumnsDefaults}">
        <input type="hidden" id="default.${columnsDefaults.key}" value="${columnsDefaults.value}"/>
    </c:forEach>

    <controls:filePanel currentFileName="${importProfileColumnsForm.currentFileName}"
                        hasFile="${importProfileColumnsForm.hasFile}"
                        uploadButton="true"/>

    <div id="filterbox_container_for_name">

            <div class="filterbox_form_container">
                <div id="filterbox_top"></div>
                <div id="searchbox_content">
    <div class="manage_column_container">

        <c:if test="${importProfileColumnsForm.mappingNumber > 0}">
            <br>
            <bean:message key="export.CsvMappingMsg"/>
            <br>
            <br>

            <div class="manage_columns_header">
                <div class="manage_columns_csv">
                    <strong><bean:message key="import.CsvColumn"/></strong>
                </div>
                <div class="manage_columns_db">
                    <strong><bean:message key="import.DbColumn"/></strong>
                </div>
                <div class="manage_columns_mandatory">
                    <strong><bean:message key="import.profile.column.mandatory"/></strong>
                </div>
                <div class="manage_columns_default">
                    <strong><bean:message key="settings.Default_Value"/></strong>
                </div>
            </div>
            <c:set var="column_index" value="0"/>
            <c:forEach var="mapping"
                       items="${importProfileColumnsForm.profile.columnMapping}">
                <div class="manage_columns_entry">
                    <html:errors property="mapping_${mapping.fileColumn}"/>
                    <div class="manage_columns_csv">
                            ${mapping.fileColumn}
                    </div>
                    <div class="manage_columns_db">
                        <select size="1" name="dbColumn_${column_index}" id="id_dbColumn_${column_index}"
                                style="width:150px"
                                onchange="columnChanged('id_dbColumn_${column_index}', '${column_index}');">
                            <option value="<%= ColumnMapping.DO_NOT_IMPORT %>">
                                <bean:message key="import.NoMapping"/>
                            </option>
                            <c:forEach var="dbColumn"
                                       items="${importProfileColumnsForm.dbColumns}">
                                <c:if test="${dbColumn == mapping.databaseColumn}">
                                    <option value="${dbColumn}"
                                            selected="selected">
                                            ${dbColumn}
                                    </option>
                                </c:if>
                                <c:if test="${dbColumn != mapping.databaseColumn}">
                                    <option value="${dbColumn}">
                                            ${dbColumn}
                                    </option>
                                </c:if>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="manage_columns_mandatory">
                        <c:if test="${mapping.mandatory}">
                            <input name="mandatory_${column_index}"
                                   type="checkbox" checked="checked"/>
                        </c:if>
                        <c:if test="${!mapping.mandatory}">
                            <input name="mandatory_${column_index}"
                                   type="checkbox"/>
                        </c:if>
                    </div>
                    <div class="manage_columns_default">
                        <input type="text" name="default_value_${column_index}"
                               id="id_default_value_${column_index}"
                               value="${mapping.defaultValue}" style="width:150px">
                    </div>
                    <div class="manage_columns_delete">
                        <input type="hidden" name="removeMapping_${column_index}" value=""/>
                        <a class="mailing_delete" href="#" title="<bean:message key='button.Delete'/>"
                           onclick="document.importProfileColumnsForm.removeMapping_${column_index}.value='${column_index}'; document.importProfileColumnsForm.submit();return false;"></a>
                    </div>
                    <c:set var="column_index" value="${column_index + 1}"/>
                </div>
            </c:forEach>
        </c:if>

        <br>
        <br>
        <br>

        <div></div>

        <div class="grey_box_left_column">
            <div style="float: left;">
                <label for="column_name"><bean:message key="import.profile.new.column"/>:</label>
                <html:text styleId="column_name" property="newColumn" maxlength="99" style="width: 140px;"/>
            </div>

            <input type="hidden" name="add" value=""/>

            <div class="action_button add_button">
                <a href="#"
                   onclick="document.importProfileColumnsForm.add.value='add'; document.importProfileColumnsForm.submit();return false;">
                    <span><bean:message key="button.Add"/></span>
                </a>
            </div>
        </div>
    </div>

    <div class="filter_button_wrapper">
        <input type="hidden" name="save" value=""/>

        <div class="filterbox_form_button filterbox_form_button_right_corner">
            <a href="#"
               onclick="document.importProfileColumnsForm.save.value='save'; document.importProfileColumnsForm.submit();return false;">
                <span><bean:message key="button.Save"/></span>
            </a>
        </div>
        <div class="action_button"><bean:message key="import.ManageColumns"/>:</div>
    </div>
                </div>
                <div id="filterbox_bottom"></div>
            </div>
    </div>

</html:form>