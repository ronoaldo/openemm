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
<%@ tag pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/taglibs.jsp" %>

<%@ attribute name="hasFile" %>
<%@ attribute name="currentFileName" %>
<%@ attribute name="uploadButton" %>

<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="blue_box_container">
    <div class="blue_box_top"></div>
    <div class="blue_box_content">
        <b><bean:message key="import.csv.file"/></b>

        <c:if test="${hasFile == 'true'}">
            <div>
                <label><bean:message key="import.current.csv.file"/></label>
                :&nbsp;<strong>${currentFileName}</strong>
            </div>
            <div class="action_button no_margin_right no_margin_bottom">
                <input type="hidden" id="remove_file" name="remove_file" value=""/>
                <a href="#"
                   onclick="document.getElementById('remove_file').value='remove_file'; document.${currentFormName}.submit(); return false;"><span><bean:message
                        key="button.Remove"/></span></a>
            </div>
        </c:if>
        <c:if test="${hasFile != 'true'}">
            <div>
                <label><bean:message key="import.profile.chooseCsv"/>:</label>
                <html:file property="csvFile"/>
            </div>
            <c:if test="${uploadButton == 'true'}">
                <div class="button_grey_box_container">
                    <input type="hidden" id="upload_file" name="upload_file" value=""/>

                    <div class="action_button no_margin_right no_margin_bottom">
                        <a href="#"
                           onclick="document.getElementById('upload_file').value='upload_file'; document.${currentFormName}.submit(); return false;"><span><bean:message
                                key="button.Upload"/></span></a>
                    </div>
                </div>
            </c:if>
        </c:if>

    </div>
    <div class="blue_box_bottom"></div>
</div>