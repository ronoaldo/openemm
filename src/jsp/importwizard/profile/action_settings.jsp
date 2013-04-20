<%--
  The contents of this file are subject to the Common Public Attribution
  License Version 1.0 (the "License"); you may not use this file except in
  compliance with the License. You may obtain a copy of the License at
  http://www.openemm.org/cpal1.html. The License is based on the Mozilla
  Public License Version 1.1 but Sections 14 and 15 have been added to cover
  use of software over a computer network and provide for limited attribution
  for the Original Developer. In addition, Exhibit A has been modified to be
  consistent with Exhibit B.
  Software distributed under the License is distributed on an "AS IS" basis,
  WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
  the specific language governing rights and limitations under the License.

  The Original Code is OpenEMM.
  The Original Developer is the Initial Developer.
  The Initial Developer of the Original Code is AGNITAS AG. All portions of
  the code written by AGNITAS AG are Copyright (c) 2009 AGNITAS AG. All Rights
  Reserved.

  Contributor(s): AGNITAS AG.
  --%>
<%@ page language="java"
         import="org.agnitas.beans.Recipient"
         contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.util.AgnUtils" %>
<%@ page import="org.agnitas.web.ImportProfileAction" %>
<%@ page import="org.agnitas.web.forms.ImportProfileForm" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/taglibs.jsp" %>

<% pageContext.setAttribute("MAILTYPE_TEXT", Recipient.MAILTYPE_TEXT); %>
<% pageContext.setAttribute("MAILTYPE_HTML", Recipient.MAILTYPE_HTML); %>
<% pageContext.setAttribute("MAILTYPE_HTML_OFFLINE", Recipient.MAILTYPE_HTML_OFFLINE); %>
<% pageContext.setAttribute("MAILTYPE_MHTML", Recipient.MAILTYPE_MHTML); %>
<script type="text/javascript">
    <!--
    function parametersChanged() {
        document.getElementsByName('importProfileForm')[0].numberOfRowsChanged.value = true;
    }
    //-->
</script>

<html:hidden property="numberOfRowsChanged"/>

<div class="import_process_settings">
    <div class="import_process_settings_item">
        <label for="import_mode_select"><bean:message key="settings.Mode"/>:</label>
        <html:select styleId="import_mode_select" property="profile.importMode" size="1">
            <c:forEach var="importMode"
                       items="${importProfileForm.importModes}">
                <html:option value="${importMode.intValue}">
                    <bean:message key="${importMode.publicValue}"/>
                </html:option>
            </c:forEach>
        </html:select>
        <div class="info_bubble_container">
            <div id="import_mode" class="info_bubble">
                &nbsp
            </div>
        </div>
    </div>

    <div class="import_process_settings_item">
        <label for="import_null_values"><bean:message key="import.null_value_handling"/>:</label>
        <html:select styleId="import_null_values" property="profile.nullValuesAction" size="1">
            <c:forEach var="nullValuesAction"
                       items="${importProfileForm.nullValuesActions}">
                <html:option value="${nullValuesAction.intValue}">
                    <bean:message key="${nullValuesAction.publicValue}"/>
                </html:option>
            </c:forEach>
        </html:select>
        <div class="info_bubble_container">
            <div id="null_value_handling" class="info_bubble">
                &nbsp
            </div>
        </div>
    </div>

    <div class="import_process_settings_item">
        <label for="import_key_column"><bean:message key="import.keycolumn"/>:</label>
        <html:select styleId="import_key_column" property="profile.keyColumn" size="1">
            <agn:ShowColumnInfo id="agnTbl"
                                table="<%= AgnUtils.getCompanyID(request) %>">
                <html:option
                        value='<%= new String((String)pageContext.getAttribute("_agnTbl_column_name")).toLowerCase() %>'>
                    <%= (String) pageContext.getAttribute("_agnTbl_shortname") %>
                </html:option>
            </agn:ShowColumnInfo>
        </html:select>
        <div class="info_bubble_container">
            <div id="key_column" class="info_bubble">
                &nbsp
            </div>
        </div>
    </div>

    <agn:ShowByPermission token="import.mode.doublechecking">
        <div class="import_process_settings_item">
            <label for="import_doublecheking"><bean:message key="import.doublechecking"/>:</label>
            <html:select styleId="import_doublecheking" property="profile.checkForDuplicates" size="1">
                <c:forEach var="value"
                           items="${importProfileForm.checkForDuplicatesValues}">
                    <html:option value="${value.intValue}">
                        <bean:message key="${value.publicValue}"/>
                    </html:option>
                </c:forEach>
            </html:select>
            <div class="info_bubble_container">
                <div id="check_for_duplicates" class="info_bubble">
                    &nbsp
                </div>
            </div>
        </div>
    </agn:ShowByPermission>

    <div class="import_process_settings_item">
        <label for="import_mailingtype"><bean:message key="recipient.mailingtype"/>:</label>
        <html:select styleId="import_mailingtype" property="profile.defaultMailType">
            <html:option value="${MAILTYPE_TEXT}">
                <bean:message key="recipient.mailingtype.text"/>
            </html:option>
            <html:option value="${MAILTYPE_HTML}">
                <bean:message key="recipient.mailingtype.html"/>
            </html:option>
            <html:option value="${MAILTYPE_HTML_OFFLINE}">
                <bean:message key="recipient.mailingtype.htmloffline"/>
            </html:option>
            <agn:ShowByPermission token="mailing.content.mhtml">
                <html:option value="${MAILTYPE_MHTML}">
                    <bean:message key="recipient.mailingtype.mhtml"/>
                </html:option>
            </agn:ShowByPermission>
        </html:select>
        <div class="info_bubble_container">
            <div id="mail_type" class="info_bubble">
                &nbsp
            </div>
        </div>
    </div>

    <div class="import_process_settings_item">
        <label for="import_email"><bean:message key="import.profile.report.email"/>:</label>
        <html:errors property="mailForReport"/>
        <html:text styleId="import_email" property="profile.mailForReport"/>
        <div class="info_bubble_container">
            <div id="report_email" class="info_bubble">
                &nbsp
            </div>
        </div>
    </div>

    <div>
        <html:hidden property="__STRUTS_CHECKBOX_profile.updateAllDuplicates" value="false"/>
        <html:checkbox styleId="import_duplicates" property="profile.updateAllDuplicates"
                       onchange="parametersChanged()"/>
        <label for="import_duplicates" id="import_duplicates_label"><bean:message
                key="import.profile.updateAllDuplicates"/>:</label>

        <div class="info_bubble_container">
            <div id="update_all_duplicates" class="info_bubble">
                &nbsp
            </div>
        </div>
    </div>

</div>

<script type="text/javascript">
    // mode help balloon
    var modeHelp = new HelpBalloon({
        dataURL: 'help_${helplanguage}/importwizard/step_2/Mode.xml'
    });
    $('import_mode').insertBefore(modeHelp.icon, $('import_mode').childNodes[0]);

    // null values handling help balloon
    var nullValuesHelp = new HelpBalloon({
        dataURL: 'help_${helplanguage}/importwizard/step_2/NullValueHandling.xml'
    });
    $('null_value_handling').insertBefore(nullValuesHelp.icon, $('null_value_handling').childNodes[0]);

    // key column balloon
    var keyColumnHelp = new HelpBalloon({
        dataURL: 'help_${helplanguage}/importwizard/step_2/KeyColumn.xml'
    });
    $('key_column').insertBefore(keyColumnHelp.icon, $('key_column').childNodes[0]);

    // check for duplicates help balloon
    var duplicatesHelp = new HelpBalloon({
        dataURL: 'help_${helplanguage}/importwizard/step_2/Doublechecking.xml'
    });
    $('check_for_duplicates').insertBefore(duplicatesHelp.icon, $('check_for_duplicates').childNodes[0]);

    // report email help balloon
    var reportEmailHelp = new HelpBalloon({
        dataURL: 'help_${helplanguage}/importwizard/step_2/ReportEmail.xml'
    });
    $('report_email').insertBefore(reportEmailHelp.icon, $('report_email').childNodes[0]);

    // extended email check help balloon
    //
    //    var extEmailCheckHelp = new HelpBalloon({
    //        dataURL: 'help_${helplanguage}/importwizard/step_2/ExtendCheck.xml'
    //    });
    //    $('ext_email_check').insertBefore(extEmailCheckHelp.icon, $('ext_email_check').childNodes[0]);
    //
    // update all duplicates help balloon
    var updateAllDuplicatesHelp = new HelpBalloon({
        dataURL: 'help_${helplanguage}/importwizard/step_2/UpdateAllDuplicates.xml'
    });
    $('update_all_duplicates').insertBefore(updateAllDuplicatesHelp.icon, $('update_all_duplicates').childNodes[0]);

    // mail type help balloon
    var mailTypeHelp = new HelpBalloon({
        dataURL: 'help_${helplanguage}/importwizard/step_2/MailType.xml'
    });
    $('mail_type').insertBefore(mailTypeHelp.icon, $('mail_type').childNodes[0]);
</script>

