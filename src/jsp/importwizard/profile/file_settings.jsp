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
         import="org.agnitas.web.ImportProfileAction"
         contentType="text/html; charset=utf-8" %>
<%@ page import="org.agnitas.web.forms.ImportProfileForm" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/taglibs.jsp" %>


<div class="file_settings_left_column">
    <div class="file_settings_item">
        <label for="file_separator"><bean:message key="import.Separator"/>:</label>
        <html:select styleId="file_separator" property="profile.separator" size="1">
            <c:forEach var="separatorVar"
                       items="${importProfileForm.separators}">
                <html:option value="${separatorVar.intValue}">
                    <bean:message key="import.${separatorVar.publicValue}"/>
                </html:option>
            </c:forEach>
        </html:select>
        <div class="info_bubble_container">
            <div id="separator" class="info_bubble">
                &nbsp;
            </div>
        </div>
    </div>

    <div class="file_settings_item">
        <label for="file_charset"><bean:message key="mailing.Charset"/>:</label>
        <html:select styleId="file_charset" property="profile.charset" size="1">
            <c:forEach var="charset"
                       items="${importProfileForm.charsets}">
                <html:option value="${charset.intValue}">
                    <bean:message key="mailing.${charset.publicValue}"/>
                </html:option>
            </c:forEach>
        </html:select>
        <div class="info_bubble_container">
            <div id="charset" class="info_bubble">
                &nbsp;
            </div>
        </div>
    </div>
</div>

<div class="file_settings_right_column">
    <div class="file_settings_item">
        <label for="file_recognition"><bean:message key="import.Delimiter"/>:</label>
        <html:select styleId="file_recognition" property="profile.textRecognitionChar" size="1">
            <c:forEach var="delimiter"
                       items="${importProfileForm.delimiters}">
                <html:option value="${delimiter.intValue}">
                    <bean:message key="export.${delimiter.publicValue}"/>
                </html:option>
            </c:forEach>
        </html:select>
        <div class="info_bubble_container">
            <div id="delimiter" class="info_bubble">
                &nbsp;
            </div>
        </div>
    </div>
    <div class="file_settings_item">
        <label for="file_dateformat"><bean:message key="import.dateFormat"/>:</label>
        <html:select styleId="file_dateformat" property="profile.dateFormat" size="1">
            <c:forEach var="dateFormat"
                       items="${importProfileForm.dateFormats}">
                <html:option value="${dateFormat.intValue}">
                    <bean:message key="${dateFormat.publicValue}"/>
                </html:option>
            </c:forEach>
        </html:select>
        <div class="info_bubble_container">
            <div id="dateformat" class="info_bubble">
                &nbsp;
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    // separator help balloon
    var helpBalloonSeparator = new HelpBalloon({
        dataURL: 'help_${helplanguage}/importwizard/step_1/Separator.xml'
    });
    $('separator').insertBefore(helpBalloonSeparator.icon, $('separator').childNodes[0]);

    // delimiter help balloon
    var helpBalloonDelimiter = new HelpBalloon({
        dataURL: 'help_${helplanguage}/importwizard/step_1/Delimiter.xml'
    });
    $('delimiter').insertBefore(helpBalloonDelimiter.icon, $('delimiter').childNodes[0]);

    // charset help balloon
    var helpBalloonCharset = new HelpBalloon({
        dataURL: 'help_${helplanguage}/importwizard/step_1/Charset.xml'
    });
    $('charset').insertBefore(helpBalloonCharset.icon, $('charset').childNodes[0]);

    // date format help balloon
    var helpBalloonDateFormat = new HelpBalloon({
        dataURL: 'help_${helplanguage}/importwizard/step_1/DateFormat.xml'
    });
    $('dateformat').insertBefore(helpBalloonDateFormat.icon, $('dateformat').childNodes[0]);
</script>